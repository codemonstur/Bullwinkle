package bullwinkle.model.tokens;

/**
 * Terminal token represented by a string.
 */
public class StringTerminalToken extends TerminalToken {

	/**
	 * Creates a new empty string terminal token
	 */
	protected StringTerminalToken()
	{
		super();
	}

	/**
	 * Creates a new string terminal token
	 * @param label The string this token should match
	 */
	public StringTerminalToken(String label)
	{
		super(label);
	}

	@Override
	public boolean matches(final Token tok)
	{
		// Anything matches a string
		return tok != null;
	}

	@Override
	public int match(final String s)
	{   
		if (s == null)
		{
			return 0;
		}
		return s.indexOf(' ');
	}
}
