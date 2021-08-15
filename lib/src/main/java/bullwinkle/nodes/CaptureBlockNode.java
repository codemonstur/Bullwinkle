package bullwinkle.nodes;

/**
 * A parse node that applies a regular expression with a capture block.
 */
public final class CaptureBlockNode extends Node {
	public CaptureBlockNode(final String token) {
		super(token);
	}
}
