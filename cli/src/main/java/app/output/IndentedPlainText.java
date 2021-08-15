package app.output;

import bullwinkle.nodes.Node;

import static bullwinkle.Constants.NEW_LINE;
import static bullwinkle.Constants.SPACE;
import static bullwinkle.util.Functions.orDefault;

/**
 * Renders a parse tree into a sequence of indented lines of text. For
 * example, the following parse tree:
 * <pre>
 *   &lt;S&gt;
 *  /   \
 * foo  &lt;A&gt;
 *       |
 *      bar
 * </pre>
 * will be rendered as
 * <pre>
 * &lt;S&gt;
 *  foo
 *  &lt;A&gt;
 *   bar
 * </pre>
 * This can be an easy way to use Bullwinkle as an external program to
 * perform the parsing, and to pass a machine-readable parse tree to another
 * program.
 */
public final class IndentedPlainText implements OutputFormatVisitor {

	// A string builder where the output is progressively built
	private final StringBuilder output = new StringBuilder();
	private int depth = 0;

	@Override
	public void visit(final Node node) {
		output.append(SPACE.repeat(depth))
			.append(orDefault(node.getValue(), node.getToken()))
			.append(NEW_LINE);
		depth++;
	}

	@Override
	public void pop() {
		depth--;
	}

	@Override
	public String toOutputString()
	{
		return output.toString();
	}

}
