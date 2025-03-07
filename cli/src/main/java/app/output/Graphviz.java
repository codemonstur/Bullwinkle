package app.output;

import bullwinkle.nodes.CaptureBlockNode;
import bullwinkle.nodes.Node;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Visitor that builds a DOT file from a parse tree. The file can be used
 * by <a href="http://graphviz.org/">Graphviz</a> to display a parse tree
 * graphically.
 */
public final class Graphviz implements OutputFormatVisitor {
	// A stack keeping the parent node IDs
	private final Deque<Integer> parents;

	// A counter keeping the last integer used for node IDs
	private int nodeCount = 0;

	// A string builder where the file contents are progressively created
	private final StringBuilder output;

	public Graphviz() {
		parents = new ArrayDeque<>();
		output = new StringBuilder();
	}

	@Override
	public void visit(final Node node) {
		final int cur_node = nodeCount++;

		if (!parents.isEmpty()) {
			output.append(parents.peek())
				.append(" -> ").append(cur_node).append(";\n");
		}

		String shape = node instanceof CaptureBlockNode ? "rectangle" : "oval";
		String color = "white";
		String fillcolor = "blue";
		String label = escape(node.getValue());
		if (label == null) {
			label = escape(node.getToken());
			shape = "rect";
			fillcolor = "white";
			color = "black";
		}

		output.append("  ").append(cur_node).append(" [fontcolor=\"").append(color)
			.append("\",style=\"filled\",fillcolor=\"").append(fillcolor).append("\",shape=\"")
			.append(shape).append("\",label=\"").append(label).append("\"];\n");

		parents.push(cur_node);
	}

	@Override
	public void pop() {
		parents.pop();
	}

	@Override
	public String toOutputString() {
		return "# File auto-generated by Bullwinkle\n\ndigraph G {\n" + output + "}";
	}

	// Escapes a few characters in a string to make it compatible with DOT files
	private static String escape(final String input) {
		if (input == null) return null;
		return input.replace("\"", "&quot;");
	}

}
