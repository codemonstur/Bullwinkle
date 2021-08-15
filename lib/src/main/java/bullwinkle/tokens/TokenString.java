package bullwinkle.tokens;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import static bullwinkle.Constants.SPACE;

/**
 * An ordered sequence of tokens
 */
public class TokenString extends LinkedList<Token> {

	/**
	 * Whether this case symbol should remain at the end of the
	 * alternatives for a rule
	 */
	private boolean tryLast = false;

	public TokenString() {}
	public TokenString(final Token token) {
		this.add(token);
	}

	/**
	 * Tells whether this element should be tried last when parsing
	 * @return True if it should be tried last
	 */
	public boolean getTryLast()
	{
		return tryLast;
	}

	/**
	 * Tells whether this element should be tried last when parsing
	 * @param b Set to true if it should be tried last
	 */
	public void setTryLast(boolean b)
	{
		tryLast = b;
	}

	/**
	 * Creates a copy of this token string
	 * @return The copy
	 */
	public final TokenString getCopy() {
		TokenString out = new TokenString();
		out.addAll(this);
		return out;
	}

	@Override
	public String toString() {
		StringBuilder out = new StringBuilder();

		boolean first = true;
		for (final var token: this) {
			if (!first)
				out.append(SPACE);
			first = false;
			out.append(token);
		}

		return out.toString();
	}

	/**
	 * Gets the set of all terminal tokens that appear in this string
	 */
	public Set<TerminalToken> getTerminalTokens() {
		final var out = new HashSet<TerminalToken>();
		for (final var token : this) {
			if (token instanceof TerminalToken)
				out.add((TerminalToken) token);
		}
		return out;
	}

	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof TokenString rt)) return false;
		if (rt.size() != size()) return false;

		for (int i = 0; i < size(); i++) {
			if (!get(i).equals(rt.get(i)))
				return false;
		}
		return true;
	}
	
	@Override
	public int hashCode()
	{
		return size();
	}
}
