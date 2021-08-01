package bullwinkle.parsing;

import bullwinkle.error.VisitException;
import bullwinkle.model.nodes.Node;

/**
 * Implementation of the <em>Visitor</em> design pattern for a parse tree.
 */
public interface ParseNodeVisitor {
	/**
	 * Method called when entering a new node in the parse tree
	 * @param node The parse node
	 * @throws VisitException Thrown if something wrong happens
	 */
	void visit(Node node) throws VisitException;

	/**
	 * Method called when leaving a node in the parse tree
	 */
	void pop();

}
