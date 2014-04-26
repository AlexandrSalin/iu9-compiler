package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
final public class ForNode extends IterationStructureNode {
    public ForNode(
            BlockNode<ExpressionNode> initialization, 
            ExpressionNode condition, 
            BlockNode<ExpressionNode> step, 
            BlockNode<Statement> block,
            Position position) {
        
        super(BaseNode.NodeType.FOR, condition, block, position);
        this.initialization = initialization;
        this.step = step;
    }
    public ForNode(
            BlockNode<ExpressionNode> initialization, 
            ExpressionNode condition, 
            BlockNode<ExpressionNode> step, 
            BlockNode<Statement> block,
            DebugInfo dInfo) {
        
        super(BaseNode.NodeType.FOR, condition, block, dInfo);
        this.initialization = initialization;
        this.step = step;
    }
    
    public BaseNode getNode() {
        return this;
    }
    
    public final BlockNode<ExpressionNode> initialization;
    public final BlockNode<ExpressionNode> step;
}
