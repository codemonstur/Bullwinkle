package bullwinkle.parsing;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Logger;

import bullwinkle.error.InvalidGrammar;
import bullwinkle.error.InvalidRule;
import bullwinkle.error.ParseException;
import bullwinkle.model.*;
import bullwinkle.model.nodes.CaptureBlockNode;
import bullwinkle.model.nodes.Node;
import bullwinkle.model.tokens.*;
import bullwinkle.util.MutableString;

import static bullwinkle.Constants.TWO_SPACES;
import static java.util.logging.Level.INFO;

/**
 * A parser reads a string according to a BNF grammar and produces a parse tree
 */
public class BnfParser {

	private final LinkedList<BnfRule> rules = new LinkedList<>();
	private BnfRule startRule;

	private boolean debugMode = false;
	private Logger debugOut = Logger.getAnonymousLogger();

	/*
	 * Maximum number of recursion steps when parsing a string.
	 * Parsing will stop immediately when reaching this depth.
	 * This is an upper bound that prevents the parser from entering
	 * an infinite recursion.
	 */
	private int maxRecursionSteps = 50;

	private boolean partialParsing = false;

	/**
	 * Creates a new empty parser with no grammar
	 */
	public BnfParser() {}

	/**
	 * Creates a new parser by copying the rules from another parser
	 */
	public BnfParser(final BnfParser parser) {
		rules.addAll(parser.rules);
		startRule = parser.startRule;
	}

	/**
	 * Creates a new parser by reading its grammar from the contents of the input stream
	 */
	public BnfParser(final InputStream is) throws InvalidGrammar {
		Scanner s = new Scanner(is);
		setGrammar(s);
	}

	/**
	 * Instructs the parser to perform partial parsing. In partial parsing,
	 * a string can contain instances of non-terminal tokens. For example,
	 * given the rules
	 * <pre>
	 * &lt;S&gt; := &lt;A&gt; b
	 * &lt;A&gt; := foo | bar
	 * </pre>
	 * With partial parsing, the string <tt>&lt;A&gt; b</tt> will parse.
	 * In this case, note that the resulting parse tree can have non-terminal
	 * tokens as leaves.
	 * @param b Set to true to enable partial parsing
	 */
	public void setPartialParsing(boolean b)
	{
		partialParsing = b;
	}

	/**
	 * Whether the matching is sensitive to case. This is a program-wide
	 * value
	 * @param b True if parsing is case-sensitive, false otherwise
	 */
	public static void setCaseSensitive(final boolean b)
	{
		Token.setCaseSensitive(b);
	}

	/**
	 * Sets the maximum number of recursion steps that the parsing will use.
	 * This setting is there to avoid infinite loops in the parsing
	 * @param steps The maximum number of recursion steps. Must be positive.
	 */
	public void setMaxRecursionSteps(final int steps) {
		if (steps > 0) maxRecursionSteps = steps;
	}

	/**
	 * Sets the parser into "debug mode". This will print information messages
	 * about the status of the parsing on the standard error stream
	 * @param b Set to <code>true</code> to enable debug mode
	 */
	public void setDebugMode(final boolean b)
	{
		debugMode = b;
	}

	/**
	 * Sets the parser into "debug mode". This will print information messages
	 * about the status of the parsing in some print stream.
	 * @param b Set to <code>true</code> to enable debug mode
	 * @param out A logger to log the information
	 */
	public void setDebugMode(final boolean b, final Logger out) {
		debugMode = b;
		debugOut = out;
	}
	
	/**
	 * Retrieves the start rule of the grammar associated to this parser.
	 */
	public BnfRule getStartRule()
	{
		return startRule;
	}

	@Override
	public String toString() {
		final var out = new StringBuilder();
		for (final var rule : rules)
			out.append(rule).append(";\n");
		return out.toString();
	}

	/**
	 * Adds a new case to an existing rule
	 * @param index The location in the list of cases where to put the new case. Use 0 to
	 *              put the new case at the beginning.
	 * @param ruleName The name of the rule
	 * @param caseString The case to add
	 */
	public void addCaseToRule(final int index, final String ruleName, final String caseString) {
		BnfRule rule = getRule(ruleName);
		if (rule == null) return;

		NonTerminalToken ntok = new NonTerminalToken(caseString);
		TokenString ts = new TokenString();
		ts.add(ntok);
		rule.addAlternative(index, ts);
	}

	/**
	 * Adds a new case to an existing rule
	 * @param ruleName The name of the rule
	 * @param caseString The case to add
	 */
	public void addCaseToRule(final String ruleName, final String caseString) {
		addCaseToRule(0, ruleName, caseString);
	}

	/**
	 * Gets the rule instance with given name
	 * @param ruleName The name of the rule
	 * @return The rule, or <tt>null</tt> if no rule exists with given name
	 */
	public BnfRule getRule(final String ruleName) {
		for (final var rule : rules) {
			String lhs = rule.getLeftHandSide().getName();
			if (ruleName.compareTo(lhs) == 0) return rule;
		}
		return null;
	}

	/**
	 * Sets the parser's grammar from a string
	 * @param grammar The string containing the grammar to be used
	 */
	public void setGrammar(final String grammar) throws InvalidGrammar {
		addRules(parseRules(grammar));
	}
	
	/**
	 * Sets the parser's grammar from a scanner
	 * @param scanner A scanner containing the grammar to be used
	 */
	public void setGrammar(final Scanner scanner) throws InvalidGrammar {
		addRules(parseRules(scanner));
	}

	/**
	 * Converts a string into a list of grammar rules
	 * @param grammar The string containing the grammar to be used
	 * @return A list of grammar rules
	 */
	public static List<BnfRule> parseRules(final String grammar) throws InvalidGrammar {
		if (grammar == null) throw new InvalidGrammar("Null argument given");

		return parseRules(new Scanner(grammar));
	}

	/**
	 * Converts a string source into a list of grammar rules
	 * @param scanner A scanner open on a string containing the grammar to be used
	 * @return A list of grammar rules
	 */
	public static List<BnfRule> parseRules(final Scanner scanner) throws InvalidGrammar {
		final var rules = new LinkedList<BnfRule>();

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();

			// Remove comments and empty lines
			final int offsetPound = line.indexOf('#');
			if (offsetPound != -1) line = line.substring(0, offsetPound);
			line = line.trim();
			if (line.isEmpty() || line.startsWith("#")) continue;

			try {
				rules.add(BnfRule.parseRule(line.trim()));
			} catch (InvalidRule e) {
				throw new InvalidGrammar(e);
			}
		}

		return rules;
	}
	
	/**
	 * Returns the list of alternative rules
	 * @param rule_name The rule you need the alternatives
	 * @return a list of strings representing the alternatives
	 */
	public List<String> getAlternatives(String rule_name) {
		for (BnfRule rule : rules) {
			String lhs = rule.getLeftHandSide().getName();
			if (rule_name.compareTo(lhs) == 0) {
				List<String> alternatives = new ArrayList<String>();
				for(TokenString alt : rule.getAlternatives()) {
					alternatives.add(alt.toString());
				}
				return alternatives;
			}
	    }
	    return new ArrayList<>(0);
	}

	/**
	 * Adds a rule to the parser at a specific position.
	 * If a rule with the same left-hand side
	 * already exists in the parser, the case of the rule passed as an argument
	 * will be added to the cases of the existing rule.
	 * @param position The position where the alternatives to this rule will be added 
	 * @param rule The rule to add
	 */
	public void addRule(final int position, final BnfRule rule) {
		NonTerminalToken r_left = rule.getLeftHandSide();
		for (BnfRule in_rule : rules) {
			NonTerminalToken in_left = in_rule.getLeftHandSide();
			if (r_left.equals(in_left)) {
				in_rule.addAlternatives(position, rule.getAlternatives());
				break;
			}
		}
		// No rule with the same LHS was found
		rules.add(rule);
	}

	/**
	 * Adds a rule to the parser. If a rule with the same left-hand side
	 * already exists in the parser, the case of the rule passed as an argument
	 * will be added to the cases of the existing rule. 
	 * @param rule The rule to add
	 */
	public void addRule(final BnfRule rule) {
		NonTerminalToken r_left = rule.getLeftHandSide();
		for (BnfRule in_rule : rules) {
			NonTerminalToken in_left = in_rule.getLeftHandSide();
			if (r_left.equals(in_left)) {
				in_rule.addAlternatives(rule.getAlternatives());
				break;
			}
		}
		// No rule with the same LHS was found
		rules.add(rule);
	}

	/**
	 * Adds a collection of rules to the parser 
	 */
	public void addRules(final Collection<BnfRule> rules) {
		for (BnfRule rule : rules)
			addRule(rule);
	}

	/**
	 * Sets the start rule to be used for the parsing
	 * @param tokenName The name of the non-terminal to be used. It must
	 *   be defined in the grammar, otherwise a <code>NullPointerException</code>
	 *   will be thrown when attempting to parse a string.
	 */
	public void setStartRule(final String tokenName) {
		setStartRule(new NonTerminalToken(tokenName));
	}

	/**
	 * Sets the start rule to be used for the parsing
	 * @param token The non-terminal to be used. It must
	 *   be defined in the grammar, otherwise a <code>NullPointerException</code>
	 *   will be thrown when attempting to parse a string.
	 */
	public void setStartRule(final NonTerminalToken token)
	{
		startRule = getRule(token);
	}

	/**
	 * Parse a string
	 * @param input The string to parse
	 * @return The root of the resulting parsing tree
	 * @throws ParseException Thrown if the string does not follow the grammar
	 */
	public Node parse(final String input) throws ParseException {
		if (startRule == null) {
			if (rules.isEmpty()) throw new ParseException("No start rule could be found");

			// If no start rule was specified, take first rule of the list as default
			startRule = rules.peekFirst();
		}
		return parse(startRule, new MutableString(input), 0);
	}

	private Node parse(final BnfRule rule, MutableString input, int level) throws ParseException {
		if (level > maxRecursionSteps)
			throw new ParseException("Maximum number of recursion steps reached. If the input string is indeed valid, try increasing the limit.");

		Node out_node = null;
		MutableString n_input = new MutableString(input);
		boolean wrong_symbol = true;
		boolean read_epsilon = false;
		log("Considering input '" + input + "' with rule " + rule, level);
		for (TokenString alt : rule.getAlternatives())
		{
			log("Alternative " + alt, level);
			out_node = new Node();
			NonTerminalToken left_hand_side = rule.getLeftHandSide();
			out_node.setToken(left_hand_side.toString());
			out_node.setValue(left_hand_side.toString());
			TokenString new_alt = alt.getCopy();
			Iterator<Token> alt_it = new_alt.iterator();
			n_input = new MutableString(input);
			wrong_symbol = false;
			while (alt_it.hasNext() && !wrong_symbol)
			{
				n_input.trim();
				Token alt_tok = alt_it.next();
				if (alt_tok instanceof TerminalToken)
				{
					if (alt_tok instanceof EpsilonTerminalToken)
					{
						// Epsilon always works
						Node child = new Node();
						child.setToken("");
						out_node.addChild(child);       
						read_epsilon = true;
						break;
					}
					if (n_input.isEmpty())
					{
						// Rule expects a token, string has no more: NO MATCH
						wrong_symbol = true;
						break;
					}
					int match_prefix_size = alt_tok.match(n_input.toString());
					if (match_prefix_size > 0)
					{
						Node child = new Node();
						MutableString input_tok = n_input.truncateSubstring(0, match_prefix_size);
						if (alt_tok instanceof RegexTerminalToken)
						{
							// In the case of a regex, create children with each capture block
							child = appendRegexChildren(child, (RegexTerminalToken) alt_tok, input_tok);
						}
						child.setToken(input_tok.toString());
						out_node.addChild(child);
					}
					else
					{
						// Rule expects a token, token in string does not match: NO MATCH
						wrong_symbol = true;
						out_node = null;
						log("FAILED parsing with case " + new_alt, level);
						break;
					}
				}
				else
				{
					Node child = null;
					// Non-terminal token: recursively try to parse it
					String alt_tok_string = alt_tok.toString();
					if (partialParsing && n_input.startsWith(alt_tok_string))
					{
						n_input.truncateSubstring(0, alt_tok_string.length());
						child = new Node(alt_tok_string);
					}
					else
					{
						BnfRule new_rule = getRule(alt_tok);
						if (new_rule == null)
						{
							// No rule found for non-terminal symbol:
							// there is an error in the grammar
							throw new ParseException("Cannot find rule for token " + alt_tok);

						}
						child = parse(new_rule, n_input, level + 1);
						if (child == null)
						{
							// Parsing failed
							wrong_symbol = true;
							out_node = null;
							log("FAILED parsing input " + input + " with rule " + rule, level);
							break;
						}
					}
					out_node.addChild(child);
				}
			}
			if (!wrong_symbol)
			{
				if (!alt_it.hasNext())
				{
					// We succeeded in parsing the complete string: done
					if (level > 0 || (level == 0 && n_input.toString().trim().length() == 0))
					{
						break;
					}
				}
				else
				{
					// The rule expects more symbols, but there are none
					// left in the input; set wrong_symbol back to true to
					// force exploring the next alternative
					wrong_symbol = true;
					n_input = new MutableString(input);
					log("No symbols left in input; will explore next alternative", level);
					break;
				}
			}
		}
		int chars_consumed = input.length() - n_input.length();
		if (wrong_symbol)
		{
			// We did not consume anything, and the symbol was not epsilon: fail
			log("FAILED: expected more symbols with rule " + rule, level);
			return null;    	
		}
		if (chars_consumed == 0 && !read_epsilon)
		{
			// We did not consume anything, and the symbol was not epsilon: fail
			log("FAILED: did not consume anything of " + input + " with rule " + rule, level);
			return null;
		}
		input.truncateSubstring(chars_consumed);
		if (level == 0 && !input.isEmpty())
		{
			// The top-level rule must parse the complete string
			log("FAILED: The top-level rule must parse the complete string", level);
			return null;
		}
		return out_node;
	}

	private BnfRule getRule(final Token tok) {
		if (tok == null) return null;

		for (final BnfRule rule : rules) {
			NonTerminalToken lhs = rule.getLeftHandSide();
			if (lhs != null && lhs.toString().compareTo(tok.toString()) == 0)
				return rule;
		}
		return null;
	}

	public Set<TerminalToken> getTerminalTokens() {
		final Set<TerminalToken> out = new HashSet<>();
		for (final BnfRule rule : rules) {
			out.addAll(rule.getTerminalTokens());
		}
		return out;
	}

	private void log(final String message, final int level) {
		if (debugMode) {
			debugOut.log(INFO, "{0}", TWO_SPACES.repeat(Math.max(0, level)) + message);
		}
	}

	/**
	 * In the case where the parsing matches a regex terminal node, creates
	 * children to the parse node representing the contents of each capture
	 * block in the regex, if any.
	 * @param node The parse node
	 * @param tok The terminal token that matches the string
	 * @param s The string that was matched
	 * @return The input node, to which children may have been appended 
	 */
	protected static Node appendRegexChildren(Node node, RegexTerminalToken tok, MutableString s) {
		final List<String> blocks = tok.getCaptureBlocks(s.toString());
		for (final String block : blocks) {
			node.addChild(new CaptureBlockNode(block));
		}
		return node;
	}

}