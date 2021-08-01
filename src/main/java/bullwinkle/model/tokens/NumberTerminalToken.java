package bullwinkle.model.tokens;

/**
 * Represents a non-terminal token in a grammar rule
 */
public class NumberTerminalToken extends TerminalToken {

	/**
	 * Creates a new non terminal token
	 * @param label The token's label
	 */
	public NumberTerminalToken(String label)
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
		String val = tok.getName();
		try
		{
			// Try to parse token into a number
			Float.parseFloat(val);
		}
		catch (NumberFormatException e)
		{
			return false;
		}
		return true;
	}
}
