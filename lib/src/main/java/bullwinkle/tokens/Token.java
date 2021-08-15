package bullwinkle.tokens;

public abstract class Token {
	protected String name;

	public Token()
	{
		this("");
	}
	public Token(final String name) {
		setName(name);
	}

	public String getName() {
		return name;
	}
	public void setName(final String name) {
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
	public boolean equals(final Object o) {
		if (!(o instanceof Token)) return false;
		return ((Token) o).getName().compareTo(name) == 0;
	}

	public abstract boolean matches(final Token tok);

	public abstract int match(final String s);
}
