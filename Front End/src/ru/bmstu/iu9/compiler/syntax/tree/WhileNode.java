package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
final public class WhileNode extends IterationStructureNode {
    public WhileNode(
            ExpressionNode expression, 
            BlockNode<Statement> block,
            Position position) {
        
        super(BaseNode.NodeType.WHILE, expression, block, position);
    }
    public WhileNode(
            ExpressionNode expression, 
            BlockNode<Statement> block,
            DebugInfo dInfo) {
        
        super(BaseNode.NodeType.WHILE, expression, block, dInfo);
    }
}
