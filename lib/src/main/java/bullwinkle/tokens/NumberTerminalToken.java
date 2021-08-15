package bullwinkle.tokens;

public class NumberTerminalToken extends TerminalToken {

	public NumberTerminalToken(final String name)
	{
		super(name);
	}

	@Override
	public boolean matches(final Token token) {
		if (token == null) return false;
		try {
			Float.parseFloat(token.getName());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
