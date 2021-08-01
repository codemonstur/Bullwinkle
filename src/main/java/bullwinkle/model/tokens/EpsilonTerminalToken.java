package bullwinkle.model.tokens;

/**
 * Terminal token matching the empty string
 */
public class EpsilonTerminalToken extends TerminalToken {

	public EpsilonTerminalToken()
	{
		super("ε");
	}

	@Override
	public boolean matches(Token tok)
	{
		return tok instanceof EpsilonTerminalToken;
	}

	@Override
	public int match(String s)
	{
		return 0;
	}

	@Override
	public String toString()
	{
		return getName();
	}
}
