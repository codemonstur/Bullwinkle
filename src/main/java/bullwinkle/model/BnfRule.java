package bullwinkle.model;

import bullwinkle.error.InvalidRule;
import bullwinkle.model.tokens.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of a single rule of a BNF grammar
 */
public final class BnfRule {
	// A list of token strings that for all the possible cases of that rule
	private final List<TokenString> alternatives;

	// The left-hand side of the rule. Since we deal with BNF grammars, this
	// left-hand side must be a single non-terminal symbol.
	private NonTerminalToken leftHandSide;

	public BnfRule() {
		alternatives = new ArrayList<>();
	}

	/**
	 * Creates a BNF rule out of a string
	 * @param input The string that contains a BNF rule. This string must follow the syntactical restrictions described in the README
	 * @return A BNF rule if the parsing succeeded
	 * @throws InvalidRule Thrown if the parsing could not be done correctly
	 */
	public static BnfRule parseRule(String input) throws InvalidRule {
		BnfRule out = new BnfRule();
		String[] lr = input.split("\\s*:=\\s*");
		if (lr.length != 2) throw new InvalidRule("Cannot find left- and right-hand side of BNF rule");

		String lhs = lr[0].trim();
		out.setLeftHandSide(new NonTerminalToken(lhs));
		if (lr[1].startsWith("^")) {
			// This is a regex line
			String regex = unescapeString(lr[1]);
			// Remove semicolon
			TokenString alternativeToAdd = new TokenString();
			Token to_add = new RegexTerminalToken(regex);
			alternativeToAdd.add(to_add);
			out.addAlternative(alternativeToAdd);
		}
		else
		{
			// Anything but a regex line
			String[] parts = lr[1].split("\\s+\\|\\|\\s+");
			String[] normalAlternatives = parts[0].split("\\s+\\|\\s+");
			String[] stickyAlternatives = new String[0];
			if (parts.length > 1)
			{
				stickyAlternatives = parts[1].split("\\s+\\|\\s+");
			}
			if (normalAlternatives.length == 0 && stickyAlternatives.length == 0)
			{
				throw new InvalidRule("Right-hand side of BNF rule is empty");
			}
			processAlternatives(out, normalAlternatives, false);
			processAlternatives(out, stickyAlternatives, true);
		}
		return out;
	}
	
	private static void processAlternatives(BnfRule out, String[] alternatives, boolean sticky) throws InvalidRule {
		for (final String alt : alternatives) {
			TokenString alternativeToAdd = new TokenString();
			alternativeToAdd.setTryLast(sticky);
			String[] words = alt.split(" ");
			if (words.length == 0) throw new InvalidRule("Alternative of BNF rule is empty");

			for (final String word : words) {
				String trimmedWord = word.trim();
				if (trimmedWord.contains("<") && !trimmedWord.startsWith("<")) {
					throw new InvalidRule("The expression '" + trimmedWord + "' contains tokens that are not separated by spaces");
				}
				if (trimmedWord.startsWith("<"))
				{
					// This is a non-terminal symbol
					Token to_add = new NonTerminalToken(trimmedWord);
					alternativeToAdd.add(to_add);
				}
				else if (trimmedWord.compareTo("\uCEB5") == 0 || trimmedWord.compareTo("\u03B5") == 0)
				{
					// There are two "lowercase epsilon" code points in Unicode; check for both
					Token to_add = new EpsilonTerminalToken();
					alternativeToAdd.add(to_add);
				}
				else
				{
					if (trimmedWord.isEmpty())
					{
						throw new InvalidRule("Trying to create an empty terminal token");
					}
					// This is a literal token
					trimmedWord = unescapeString(trimmedWord);
					alternativeToAdd.add(new TerminalToken(trimmedWord));
				}
			}
			out.addAlternative(alternativeToAdd);
		}
	}

	private void setLeftHandSide(final NonTerminalToken t) {
		leftHandSide = t;
	}
	private void addAlternative(final TokenString ts)
	{
		alternatives.add(ts);
	}

	/**
	 * Adds an alternative to the rule, and puts it in a specific position
	 * @param index The position to put the new alternative
	 * @param ts The alternative to add
	 */
	public void addAlternative(final int index, final TokenString ts)
	{
		alternatives.add(index, ts);
	}

	/**
	 * Retrieves the list of all the alternatives that this rule defines
	 * @return A list of alternatives, each of which is a string of tokens
	 *   (either terminal or non-terminal)
	 */
	public List<TokenString> getAlternatives() {
		final var orderedList = new ArrayList<TokenString>();
		final var lastElements = new ArrayList<TokenString>();
		for (final TokenString ts : alternatives) {
			(ts.getTryLast() ? lastElements : orderedList).add(ts);
		}
		orderedList.addAll(lastElements);
		return orderedList;
	}
	
	/**
	 * Retrieves the left-hand side symbol of the rule
	 * @return The left-hand side symbol
	 */
	public NonTerminalToken getLeftHandSide()
	{
		return leftHandSide;
	}

	/**
	 * Interprets UTF-8 escaped characters and converts them back into
	 * a UTF-8 string. The solution used here (going through a
	 * <tt>Property</tt> object) can be found on
	 * <a href="http://stackoverflow.com/a/24046962">StackOverflow</a>.
	 * It has the advantage of not relying on (yet another) external
	 * library (as the accepted solution does) just for using a single
	 * method. 
	 * @param s The input string
	 * @return The converted (unescaped) string
	 */
	protected static String unescapeString(final String s) {
		// We want only the unicode characters to be resolved;
		// double all other backslashes
		String new_s = s.replaceAll("\\\\([^u])", "\\\\\\\\$1");
		Properties p = new Properties();
		try
		{
			p.load(new StringReader("key=" + new_s));
		}
		catch (IOException e)
		{
			Logger.getAnonymousLogger().log(Level.WARNING, "", e);
		}
		return p.getProperty("key");
	}

	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();
		out.append(leftHandSide).append(" := ");
		boolean first = true;
		for (TokenString alt : alternatives)
		{
			if (!first)
			{
				out.append(" | ");
			}
			first = false;
			out.append(alt);
		}
		return out.toString();
	}

	public Set<TerminalToken> getTerminalTokens() {
		Set<TerminalToken> out = new HashSet<>();
		for (TokenString ts : alternatives)
		{
			out.addAll(ts.getTerminalTokens());
		}
		return out;
	}

	/**
	 * Adds a collection of alternatives to the rule
	 * @param alternatives The alternatives to add
	 */
	public void addAlternatives(Collection<TokenString> alternatives)
	{
		this.alternatives.addAll(alternatives);
	}

	/**
	 * Adds a collection of alternatives to the rule at a specific position in the list
	 * @param position The position in the rule list where it is to be added
	 * @param alternatives The alternatives to add
	 */
	public void addAlternatives(final int position, final Collection<TokenString> alternatives) {
		for (final TokenString alt : alternatives) {
			this.alternatives.add(position, alt);
		}
	}

}
