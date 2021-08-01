package bullwinkle.util;

import bullwinkle.model.nodes.Node;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class for navigating a parse tree using XPath-like expressions.
 */
public enum NodePath {;

	private static final Pattern PATH = Pattern.compile("(.+)(\\[(\\d+)\\]){0,1}");

	/**
	 * Get the first subtree matching a path expression. This actually returns
	 * the first element of {@link #getPath(Node, String)}. What "first"
	 * means is actually the first subtree encountered by the lookup method.
	 * @param n The parse tree to look into
	 * @param path The path expression
	 * @return The first subtree matching the path expression
	 */
	public static Node getPathFirst(final Node n, final String path) {
		final List<Node> out = getPath(n, path);
		return out.isEmpty() ? null : out.get(0);
	}

	/**
	 * Get subtrees that match a given path
	 * @param n The parse tree to look into
	 * @param path The path expression
	 * @return A list of subtrees matching the path expression
	 */
	public static List<Node> getPath(Node n, String path) {
		String[] path_parts = path.split("\\.");
		List<String> path_list = new LinkedList<String>();
		Collections.addAll(path_list, path_parts);
		// Add a fake root to call recursive function
		Node new_root = new Node();
		new_root.addChild(n);
		return getPath(new_root, path_list);
	}

	protected static List<Node> getPath(Node n, List<String> path) {
		List<Node> out = new LinkedList<Node>();
		if (path.isEmpty())
		{
			// Return me
			out.add(n);
			return out;
		}
		if (path.size() == 1 && path.get(0).compareTo("*") == 0)
		{
			// Return all children
			out.addAll(n.getChildren());
			return out;
		}
		String path_el = path.get(0);
		Matcher mat = PATH.matcher(path_el);
		if (!mat.find())
		{
			return out; // Error parsing path
		}
		String el_name = mat.group(1);
		String el_card_s = mat.group(3);
		int el_card = 0;
		if (el_card_s != null)
		{
			el_card = Integer.parseInt(el_card_s);
		}
		List<Node> children = n.getChildren();
		int i = 0;
		for (Node child : children)
		{
			if (el_name.compareTo(child.getToken()) == 0)
			{
				if (el_card < 0 || el_card == i)
				{
					// We have the right element
					LinkedList<String> new_path = new LinkedList<String>(path);
					new_path.removeFirst();
					List<Node> new_nodes = getPath(child, new_path);
					out.addAll(new_nodes);
				}
				i++;
			}
		}
		return out;
	}
}
