package bullwinkle.model.tokens;

/**
 * An abstract token of the grammar
 */
public abstract class Token {
	private String name;

	/**
	 * Whether the matching is sensitive to case. This is a program-wide value
	 */
	protected static boolean s_caseSensitive = true;
	
	public Token()
	{
		this("");
	}
	public Token(final String name) {
		setName(name);
	}

	/**
	 * Sets whether the matching for tokens is case sensitive. This is a
	 * system-wide setting.
	 * @param b {@code true} to make the matching case-sensitive,
	 * {@code false} otherwise
	 */
	public static void setCaseSensitive(boolean b)
	{
		s_caseSensitive = b;
	}

	public String getName()
	{
		return name;
	}
	void setName(final String name) {
		if (name != null) {
			this.name = name;
		}
	}

	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == null || !(o instanceof Token))
		{
			return false;
		}
		return ((Token) o).getName().compareTo(name) == 0;
	}

	public abstract boolean matches(final Token tok);

	public abstract int match(final String s);
}
