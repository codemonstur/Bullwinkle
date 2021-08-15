package app.output;

import bullwinkle.nodes.Node;
import bullwinkle.ParseNodeVisitor;

/**
 * Traverses a parse tree and converts it into another textual format.
 */
public interface OutputFormatVisitor extends ParseNodeVisitor {
	/**
	 * Gets the string corresponding to the converted parse tree created by
	 * this visitor.
	 * This method must be called <em>after</em> visiting the parse
	 * tree using {@link Node#postfixAccept(ParseNodeVisitor) postfixAccept()}
	 * or {@link Node#prefixAccept(ParseNodeVisitor) prefixAccept()}
	 * on the root of the parse tree.
	 * @return A string representing the converted parse tree
	 */
	String toOutputString();
}
