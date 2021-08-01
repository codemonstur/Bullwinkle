package bullwinkle.model.nodes;

import java.util.ArrayList;
import java.util.List;

import bullwinkle.error.VisitException;
import bullwinkle.parsing.ParseNodeVisitor;

/**
 * A node in the parse tree created by the parser.
 */
public class Node {
	private final ArrayList<Node> children = new ArrayList<>();

	// The grammar token represented by this parse node
	private String token;

	// The value (if any) represented by this parse node
	private String value;

	public Node() {}
	public Node(String token) {
		setToken(token);
	}

	/**
	 * Gets the children of this parse node. This method returns a
	 * <em>new</em> list instance, and not the internal list the parse
	 * node uses to store its children.
	 */
	public List<Node> getChildren() {
		ArrayList<Node> nodes = new ArrayList<>(children.size());
		nodes.addAll(children);
		return nodes;
	}

	/**
	 * Gets the value of this parse node
	 */
	public String getValue()
	{
		return value;
	}
	/**
	 * Sets the value for this parse node
	 */
	public void setValue(final String value)
	{
		this.value = value;
	}

	/**
	 * Gets the token name associated to this parse node
	 */
	public String getToken()
	{
		return token;
	}
	/**
	 * Sets the token name for this parse node
	 */
	public void setToken(final String token)
	{
		this.token = token;
	}

	/**
	 * Adds a child to this parse node
	 */
	public void addChild(final Node child)
	{
		children.add(child);
	}

	@Override
	public String toString()
	{
		return toString("");
	}

	/**
	 * Produces a string rendition of the contents of this parse node
	 * @param indent The indent to add to every line of the output
	 * @return The contents of this parse node as a string
	 */
	private String toString(String indent) {
		StringBuilder out = new StringBuilder();
		out.append(indent).append(token).append("\n");
		String n_indent = indent + " ";
		for (Node n : children) {
			out.append(n.toString(n_indent));
		}
		return out.toString();
	}

	/**
	 * Returns the size of the parse tree
	 * @return The number of nodes
	 */
	public int getSize() {
		int size = 1;
		for (Node node : children) {
			size += node.getSize();
		}
		return size;
	}

	/**
	 * Postfix traversal of the parse tree by a visitor
	 */
	public void postfixAccept(ParseNodeVisitor visitor) throws VisitException {
		for (Node n : children) {
			n.postfixAccept(visitor);
		}
		visitor.visit(this);
		visitor.pop();
	}

	/**
	 * Prefix traversal of the parse tree by a visitor
	 */
	public void prefixAccept(ParseNodeVisitor visitor) throws VisitException {
		visitor.visit(this);
		for (Node n : children) {
			n.prefixAccept(visitor);
		}
		visitor.pop();
	}

}
