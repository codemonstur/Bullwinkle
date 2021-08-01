package bullwinkle.model.tokens;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * An ordered sequence of tokens
 */
public class TokenString extends LinkedList<Token> {

	/**
	 * Whether this case symbol should remain at the end of the
	 * alternatives for a rule
	 */
	private boolean tryLast = false;

	/**
	 * Creates a new empty token string
	 */
	public TokenString()
	{
		super();
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
	public final TokenString getCopy()
	{
		TokenString out = new TokenString();
		out.addAll(this);
		return out;
	}

	@Override
	public String toString()
	{
		StringBuilder out = new StringBuilder();
		boolean first = true;
		for (Token t: this)
		{
			if (!first)
				out.append(" ");
			first = false;
			out.append(t);
		}
		return out.toString();
	}

	/**
	 * Gets the set of all terminal tokens that appear in this string
	 */
	public Set<TerminalToken> getTerminalTokens()
	{
		Set<TerminalToken> out = new HashSet<TerminalToken>();
		for (Token t : this)
		{
			if (t instanceof TerminalToken)
				out.add((TerminalToken) t);
		}
		return out;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof TokenString))
		{
			return false;
		}
		TokenString rt = (TokenString) o;
		if (rt.size() != size())
		{
			return false;
		}
		for (int i = 0; i < size(); i++)
		{
			Token t1 = get(i);
			Token t2 = rt.get(i);
			if (!t1.equals(t2))
			{
				return false;
			}
		}
		return true;
	}
	
	@Override
	public int hashCode()
	{
		return size();
	}
}
