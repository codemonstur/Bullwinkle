package bullwinkle.output;

import java.util.ArrayDeque;
import java.util.Deque;

import bullwinkle.Constants;
import bullwinkle.model.nodes.Node;

import static bullwinkle.Constants.*;

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

	// A stack of string, keeping the indents to apply to each line of text
	private final Deque<String> indents;
	// A string builder where the output is progressively built
	private final StringBuilder output;

	public IndentedPlainText() {
		indents = new ArrayDeque<>();
		output = new StringBuilder();
		indents.push(EMPTY);
	}

	@Override
	public void visit(final Node node) {
		final String label = node.getValue();
		final String currentIndent = indents.peek() + SPACE;
		if (label == null) {
			indents.push(currentIndent);
			output.append(indents.peek()).append(node.getToken()).append(NEW_LINE);
		} else {
			// Remove symbols surrounding the name of a rule
			output.append(currentIndent).append(label).append(NEW_LINE);
			indents.push(currentIndent);
		}
	}

	@Override
	public void pop()
	{
		indents.pop();
	}

	@Override
	public String toOutputString()
	{
		return output.toString();
	}

}
