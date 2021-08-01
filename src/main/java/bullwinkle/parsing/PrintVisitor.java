package bullwinkle.parsing;

import bullwinkle.model.nodes.Node;

public final class PrintVisitor implements ParseNodeVisitor {
    private final StringBuilder builder = new StringBuilder();

    public String getString()
    {
        return builder.toString();
    }

    @Override
    public void visit(Node node) {
        builder.append(node.getToken()).append(",");
    }

    @Override
    public void pop()
    {
        builder.append("pop,");
    }
}
