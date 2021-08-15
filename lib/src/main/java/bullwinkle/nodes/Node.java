package bullwinkle.nodes;

import bullwinkle.error.VisitException;
import bullwinkle.ParseNodeVisitor;

import java.util.ArrayList;
import java.util.List;

import static bullwinkle.Constants.SPACE;

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
	public Node(final String token) {
		setToken(token);
	}

	/**
	 * Gets the children of this parse node. This method returns a
	 * <em>new</em> list instance, and not the internal list the parse
	 * node uses to store its children.
	 */
	public List<Node> getChildren() {
		return new ArrayList<>(children);
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
	 * @return this
	 */
	public Node addChild(final Node child) {
		children.add(child);
		return this;
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
	private String toString(final String indent) {
		StringBuilder out = new StringBuilder();
		out.append(indent).append(token).append("\n");

		final String n_indent = indent + SPACE;
		for (final Node n : children) {
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
		for (final var node : children) {
			size += node.getSize();
		}
		return size;
	}

	public void postfixAccept(final ParseNodeVisitor visitor) throws VisitException {
		for (final var node : children) {
			node.postfixAccept(visitor);
		}
		visitor.visit(this);
		visitor.pop();
	}

	public void prefixAccept(final ParseNodeVisitor visitor) throws VisitException {
		visitor.visit(this);
		for (final var node : children) {
			node.prefixAccept(visitor);
		}
		visitor.pop();
	}

}
