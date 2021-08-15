package bullwinkle;

import bullwinkle.error.InvalidRule;
import bullwinkle.tokens.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static bullwinkle.Constants.SPACE;
import static bullwinkle.util.Functions.unescapeString;

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
	public static BnfRule parseRule(final String input, final boolean useSticky) throws InvalidRule {
		return parseRule(-1, input, useSticky);
	}

	public static BnfRule parseRule(final int lineNumber, final String input, final boolean useSticky) throws InvalidRule {
		final BnfRule ret = new BnfRule();

		final String[] lr = input.split("\\s*:=\\s*");
		if (lr.length != 2)
			throw new InvalidRule(lineNumber, input, "Cannot find left- and right-hand side of BNF rule");

		ret.leftHandSide = new NonTerminalToken(lr[0].trim());

		if (lr[1].isBlank())
			throw new InvalidRule(lineNumber, input, "Right-hand side of BNF rule is empty");

		if (lr[1].startsWith("^")) {
			ret.alternatives.add(new TokenString(new RegexTerminalToken(unescapeString(lr[1]))));
			return ret;
		}

		if (useSticky) {
			final String[] parts = lr[1].split("\\s+\\|\\|\\s+");
			processAlternatives(ret, splitRawAlternatives(parts[0]), false);
			if (parts.length > 1)
				processAlternatives(ret, splitRawAlternatives(parts[1]), true);
		} else {
			final var alternatives = splitRawAlternatives(lr[1]);
			processAlternatives(ret, expandAlternativeOptionals(alternatives), false);
		}

		return ret;
	}

	private static String[] splitRawAlternatives(final String rightHandSide) {
		return rightHandSide.contains(" | ")
			 ? rightHandSide.split("\\s+\\|\\s+")
			 : new String[] { rightHandSide };
	}

	private static String[] expandAlternativeOptionals(final String[] alternatives) {
		final var list = new ArrayList<String>();
		for (final var alt : alternatives) {
			if (alt.contains(">?")) {
				alternativesForOptionals(list, 0, alt);
			} else list.add(alt);
		}
		return list.toArray(new String[0]);
	}

	private static void alternativesForOptionals(final List<String> alternatives, final int offset, final String rule) {
		final String cleanRule = rule.replace("  ", " ");
		final int endOfPositional = cleanRule.indexOf(">?", offset);
		if (endOfPositional == -1) {
			if (!rule.isBlank()) alternatives.add(cleanRule.trim());
			return;
		}
		final int startOfPositional = cleanRule.lastIndexOf('<', endOfPositional);

		final String remainder = cleanRule.substring(endOfPositional+2);
		final String withoutIdentifier = cleanRule.substring(0, startOfPositional);
		final String withIdentifier = withoutIdentifier + cleanRule.substring(startOfPositional, endOfPositional+1);

		alternativesForOptionals(alternatives, withoutIdentifier.length(), withoutIdentifier + remainder);
		alternativesForOptionals(alternatives, withIdentifier.length(), withIdentifier + remainder);
	}

	private static void processAlternatives(final BnfRule ret, final String[] alternatives, final boolean sticky)
			throws InvalidRule {
		for (final String alt : alternatives) {
			TokenString alternativeToAdd = new TokenString();

			alternativeToAdd.setTryLast(sticky);
			String[] words = alt.split(SPACE);
			if (words.length == 0) throw new InvalidRule("Alternative of BNF rule is empty");

			for (final String word : words) {
				String trimmedWord = word.trim();
				if (trimmedWord.contains("<") && !trimmedWord.startsWith("<")) {
					throw new InvalidRule("The expression '" + trimmedWord + "' contains tokens that are not separated by spaces");
				}
				if (trimmedWord.startsWith("<")) {
					// This is a non-terminal symbol
					Token to_add = new NonTerminalToken(trimmedWord);
					alternativeToAdd.add(to_add);
				}
				else if (trimmedWord.compareTo("\uCEB5") == 0 || trimmedWord.compareTo("\u03B5") == 0) {
					// There are two "lowercase epsilon" code points in Unicode; check for both
					alternativeToAdd.add(new EpsilonTerminalToken());
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
			ret.alternatives.add(alternativeToAdd);
		}
	}

//	private void setLeftHandSide(final NonTerminalToken t) {
//		leftHandSide = t;
//	}
//	private void addAlternative(final TokenString ts) {
//		alternatives.add(ts);
//	}

	/**
	 * Adds an alternative to the rule, and puts it in a specific position
	 * @param index The position to put the new alternative
	 * @param ts The alternative to add
	 */
	public void addAlternative(final int index, final TokenString ts) {
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
	public NonTerminalToken getLeftHandSide() {
		return leftHandSide;
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
