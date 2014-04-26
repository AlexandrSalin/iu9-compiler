package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
final public class CaseNode extends ConditionalNode {
    public CaseNode(
            ExpressionNode expressiton,
            BlockNode<Statement> block,
            Position position) {
        
        super(BaseNode.NodeType.CASE, block, position);
        this.expression = expressiton;
    }
    public CaseNode(
            ExpressionNode expressiton,
            BlockNode<Statement> block,
            DebugInfo dInfo) {
        
        super(BaseNode.NodeType.CASE, block, dInfo);
        this.expression = expressiton;
    }
    
    
    public final ExpressionNode expression;
}
