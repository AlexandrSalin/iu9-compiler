package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
final public class IfNode extends ConditionalNode implements Statement {
    public IfNode(
            ExpressionNode condition, 
            BlockNode<Statement> block,
            ElseNode elseNode,
            Position position) {
        
        super(BaseNode.NodeType.IF, block, position);
        this.condition = condition;
        this.elseNode = elseNode;
    }
    public IfNode(
            ExpressionNode condition, 
            BlockNode<Statement> block,
            ElseNode elseNode,
            DebugInfo dInfo) {
        
        super(BaseNode.NodeType.IF, block, dInfo);
        this.condition = condition;
        this.elseNode = elseNode;
    }
    
    @Override
    public BaseNode getNode() {
        return this;
    }
    
    public final ExpressionNode condition;
    public final ElseNode elseNode;
}
