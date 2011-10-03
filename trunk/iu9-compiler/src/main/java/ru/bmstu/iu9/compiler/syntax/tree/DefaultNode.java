package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
final public class DefaultNode extends ConditionalNode {
    public DefaultNode(BlockNode<Statement> block, Position position) {
        super(BaseNode.NodeType.DEFAULT, block, position);
    }
    public DefaultNode(BlockNode<Statement> block, DebugInfo dInfo) {
        super(BaseNode.NodeType.DEFAULT, block, dInfo);
    }
}
