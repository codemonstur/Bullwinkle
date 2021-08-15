package bullwinkle.tokens;

public class TerminalToken extends Token {

	protected TerminalToken()
	{
		super();
	}

	public TerminalToken(final String name) {
		super(name);
	}

	@Override
	public boolean matches(final Token token) {
		if (token == null) return false;
		return getName().compareToIgnoreCase(token.getName()) == 0;
	}

	@Override
	public int match(final String s) {
		final var name = getName();
		if (s.length() < name.length()) {
			return -1;
		}
		if (getName().compareTo(s.substring(0, getName().length())) == 0) {
			return getName().length();
		}
		return 0;
	}

	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof TerminalToken)) return false;
		return getName().compareTo(((TerminalToken) o).getName()) == 0;
	}
	
	@Override
	public int hashCode() {
		// FIXME this makes no sense, and is probably broken
		// We call this explicitly, as overriding equals without
		// overriding hashCode is considered bad practice
		return super.hashCode();
	}
	
}