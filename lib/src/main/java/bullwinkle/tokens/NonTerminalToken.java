package bullwinkle.tokens;

public class NonTerminalToken extends Token {

	public NonTerminalToken()
	{
		super();
	}

	public NonTerminalToken(final String name)
	{
		super(name);
	}

	@Override
	public boolean matches(final Token token)
	{
		return false;
	}

	@Override
	public int match(final String name)
	{
		return 0;
	}
}
