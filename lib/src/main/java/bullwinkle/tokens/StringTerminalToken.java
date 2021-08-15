package bullwinkle.tokens;

public class StringTerminalToken extends TerminalToken {

	protected StringTerminalToken()
	{
		super();
	}

	public StringTerminalToken(final String name)
	{
		super(name);
	}

	@Override
	public boolean matches(final Token token) {
		// Anything matches a string
		return token != null;
	}

	@Override
	public int match(final String input) {
		return input == null ? 0 : input.indexOf(' ');
	}
}
