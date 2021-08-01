package bullwinkle.model.tokens;

/**
 * Terminal token in the grammar.
 */
public class TerminalToken extends Token {

	protected TerminalToken()
	{
		super();
	}

	/**
	 * Creates a new terminal token with a label
	 */
	public TerminalToken(final String label)
	{
		super(label);
	}

	@Override
	public boolean matches(final Token tok)
	{
		if (tok == null)
		{
			return false;
		}
		if (s_caseSensitive)
		{
			return getName().compareTo(tok.getName()) == 0;
		}
		return getName().compareToIgnoreCase(tok.getName()) == 0;
	}

	@Override
	public int match(final String s)
	{
		String name = getName();
		if (s.length() < name.length())
		{
			return -1;
		}
		if (getName().compareTo(s.substring(0, getName().length())) == 0)
		{
			return getName().length();
		}
		return 0;
	}

	@Override
	public boolean equals(Object o)
	{
		if (o == null)
		{
			return false;
		}
		if (!(o instanceof TerminalToken))
		{
			return false;
		}
		return getName().compareTo(((TerminalToken) o).getName()) == 0;
	}
	
	@Override
	public int hashCode()
	{
		// We call this explicitly, as overriding equals without
		// overriding hashCode is considered bad practice
		return super.hashCode();
	}
	
}