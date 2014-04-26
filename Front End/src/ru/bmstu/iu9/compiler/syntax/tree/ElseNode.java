package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
final public class ElseNode extends ConditionalNode {
    public ElseNode(BlockNode<Statement> block, Position position) {
        super(BaseNode.NodeType.ELSE, block, position);
    }
    public ElseNode(BlockNode<Statement> block, DebugInfo dInfo) {
        super(BaseNode.NodeType.ELSE, block, dInfo);
    }
}
