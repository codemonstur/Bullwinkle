package bullwinkle;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import bullwinkle.error.*;
import bullwinkle.nodes.CaptureBlockNode;
import bullwinkle.nodes.Node;
import bullwinkle.tokens.*;
import bullwinkle.util.MutableString;

import static bullwinkle.util.Functions.indent;
import static bullwinkle.util.Functions.orThrow;
import static java.util.logging.Level.*;
import static java.util.stream.Collectors.joining;

/**
 * A parser reads a string according to a BNF grammar and produces a parse tree
 */
public final class BnfParser {

	private final List<BnfRule> rules;
	private final BnfRule startRule;

	private final Logger logger;
	private final int maxRecursionSteps;
	private final boolean partialParsing;

	/**
	 * Creates a new parser by copying the rules from another parser
	 */
	public BnfParser(final BnfParser parser) {
		this(parser.rules, parser.startRule, parser.logger, parser.maxRecursionSteps,
			parser.partialParsing);
	}

	public BnfParser(final List<BnfRule> rules, final BnfRule startRule, final Logger logger,
					 final int maxRecursionSteps, final boolean partialParsing) {
		this.rules = rules;
		this.startRule = startRule;
		this.logger = logger;
		this.maxRecursionSteps = maxRecursionSteps;
		this.partialParsing = partialParsing;
	}

	@Override
	public String toString() {
		return rules.stream().map(BnfRule::toString)
			.collect(joining(";\n"));
	}


	/**
	 * Gets the rule instance with given name
	 * @param ruleName The name of the rule
	 * @return The rule, or <tt>null</tt> if no rule exists with given name
	 */
	public BnfRule getRule(final String ruleName) {
		for (final var rule : rules) {
			if (ruleName.equals(rule.getLeftHandSide().getName()))
				return rule;
		}

		return null;
	}
	public BnfRule getRule(final Token token) {
		if (token == null) return null;

		final String name = token.toString();
		for (final var rule : rules) {
			if (name.equals(rule.getLeftHandSide().toString()))
				return rule;
		}

		return null;
	}

	public Node parse(final String input) {
		return orThrow(parse(startRule, new MutableString(input), 0), ParsingFailed::new);
	}

	private Node parse(final BnfRule rule, MutableString input, int level) {
		if (level > maxRecursionSteps)
			throw new MaximumRecursionReached(level);

		Node out_node = null;
		MutableString n_input = new MutableString(input);
		boolean wrong_symbol = true;
		boolean read_epsilon = false;
		logger.log(FINE, indent(level, "Considering input '" + input + "' with rule " + rule));
		for (TokenString alt : rule.getAlternatives()) {
			logger.log(FINE, indent(level, "Alternative " + alt));
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
						logger.log(SEVERE, indent(level, "FAILED parsing with case " + new_alt));
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
							throw new MissingRule(alt_tok);

						}
						child = parse(new_rule, n_input, level + 1);
						if (child == null)
						{
							// Parsing failed
							wrong_symbol = true;
							out_node = null;
							logger.log(SEVERE, indent(level, "FAILED parsing input " + input + " with rule " + rule));
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
					logger.log(FINE, indent(level, "No symbols left in input; will explore next alternative"));
					break;
				}
			}
		}
		int chars_consumed = input.length() - n_input.length();
		if (wrong_symbol) {
			// We did not consume anything, and the symbol was not epsilon: fail
			logger.log(SEVERE, indent(level, "FAILED: expected more symbols with rule " + rule));
			return null;
		}
		if (chars_consumed == 0 && !read_epsilon) {
			// We did not consume anything, and the symbol was not epsilon: fail
			logger.log(SEVERE, indent(level, "FAILED: did not consume anything of " + input + " with rule " + rule));
			return null;
		}
		input.truncateSubstring(chars_consumed);
		if (level == 0 && !input.isEmpty()) {
			// The top-level rule must parse the complete string
			logger.log(SEVERE, indent(level, "FAILED: The top-level rule must parse the complete string"));
			return null;
		}
		return out_node;
	}

	private static Node appendRegexChildren(final Node node, final RegexTerminalToken token, final MutableString string) {
		final var blocks = token.getCaptureBlocks(string.toString());
		for (final var block : blocks) {
			node.addChild(new CaptureBlockNode(block));
		}
		return node;
	}

}