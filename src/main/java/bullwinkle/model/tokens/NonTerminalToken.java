package bullwinkle.model.tokens;

public class NonTerminalToken extends Token {

	/**
	 * The left-hand side symbol used to mark a non-terminal token
	 * in a grammar rule
	 */
	public static final String s_leftSymbol = "<";

	/**
	 * The right-hand side symbol used to mark a non-terminal token
	 * in a grammar rule
	 */
	public static final String s_rightSymbol = ">";

	public NonTerminalToken()
	{
		super();
	}

	public NonTerminalToken(String s)
	{
		super(s);
	}

	@Override
	public boolean matches(final Token tok)
	{
		return false;
	}

	@Override
	public int match(final String s)
	{
		return 0;
	}
}
