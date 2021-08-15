package bullwinkle.tokens;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class RegexTerminalToken extends TerminalToken {

	private Pattern pattern;

	protected RegexTerminalToken()
	{
		super();
	}

	/**
	 * Creates a new terminal token
	 * @param name The regular expression that matches this token
	 */
	public RegexTerminalToken(final String name)
	{
		super(name);
	}

	@Override
	public void setName(final String name) {
		super.setName(name);
		pattern = Pattern.compile(name);
	}

	@Override
	public boolean matches(final Token token) {
		return pattern.matcher(token.getName()).matches();
	}

	@Override
	public int match(final String name) {
		final var matcher = pattern.matcher(name);
		return matcher.find() ? matcher.end() : -1;
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Returns the content of each capture block in the regex, matched against the input string
	 * @param input The input string
	 * @return A list of strings, each of which is the content of a capture block
	 */
	public List<String> getCaptureBlocks(final String input) {
		final var out = new LinkedList<String>();
		final var matcher = pattern.matcher(input);
		if (matcher.find()) {
			for (int i = 1; i <= matcher.groupCount(); i++) {
				out.add(matcher.group(i));
			}
		}
		return out;
	}
	
	@Override
	public int hashCode()
	{
		return pattern.toString().hashCode();
	}
	
	@Override
	public boolean equals(final Object o) {
		if (!(o instanceof RegexTerminalToken rt))
			return false;

		return pattern.toString().equals(rt.pattern.toString());
	}
}
