package bullwinkle.output;

import java.util.ArrayDeque;
import java.util.Deque;

import bullwinkle.model.nodes.CaptureBlockNode;
import bullwinkle.model.nodes.Node;

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

	/**
	 * Creates a new GraphvizVisitor with default settings 
	 */
	public Graphviz() {
		parents = new ArrayDeque<>();
		output = new StringBuilder();
	}

	@Override
	public void visit(final Node node) {
		int cur_node = nodeCount++;
		if (!parents.isEmpty())
		{
			int parent = parents.peek();
			output.append(parent).append(" -> ").append(cur_node).append(";\n");
		}
		String shape = "oval";
		if (node instanceof CaptureBlockNode)
		{
			// Special treatment for regex capture blocks
			shape = "rectangle";
		}
		String color = "white";
		String fillcolor = "blue";
		String label = escape(node.getValue());
		if (label == null)
		{
			label = escape(node.getToken());
			shape = "rect";
			fillcolor = "white";
			color = "black";
		}
		output.append("  ").append(cur_node).append(" [fontcolor=\"").append(color).append("\",style=\"filled\",fillcolor=\"").append(fillcolor).append("\",shape=\"").append(shape).append("\",label=\"").append(label).append("\"];\n");
		parents.push(cur_node);
	}

	@Override
	public void pop()
	{
		parents.pop();
	}

	@Override
	public String toOutputString() {
		return "# File auto-generated by Bullwinkle\n\ndigraph G {\n" + output + "}";
	}

	// Escapes a few characters in a string to make it compatible with DOT files
	private static String escape(final String input) {
		if (input == null) return null;
		return input.replaceAll("\\\"", "&quot;");
	}

}
