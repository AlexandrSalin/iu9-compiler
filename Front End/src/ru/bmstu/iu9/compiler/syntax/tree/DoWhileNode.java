package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
public class DoWhileNode extends IterationStructureNode {
    public DoWhileNode(
            ExpressionNode expression, 
            BlockNode<Statement> block,
            Position position) {
        
        super(BaseNode.NodeType.DO_WHILE, expression, block, position);
    }
    public DoWhileNode(
            ExpressionNode expression, 
            BlockNode<Statement> block,
            DebugInfo dInfo) {
        
        super(BaseNode.NodeType.DO_WHILE, expression, block, dInfo);
    }
}
