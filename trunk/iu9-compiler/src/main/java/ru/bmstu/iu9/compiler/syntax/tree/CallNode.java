package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
final public class CallNode extends ExpressionNode {
    public CallNode(
            ExpressionNode function, 
            BlockNode<ExpressionNode> arguments, 
            Position position) {
        
        super(BaseNode.NodeType.CALL, position);
        this.function = function;
        this.arguments = arguments;
    }
    public CallNode(
            ExpressionNode function, 
            BlockNode<ExpressionNode> arguments, 
            DebugInfo dInfo) {
        
        super(BaseNode.NodeType.CALL, dInfo);
        this.function = function;
        this.arguments = arguments;
    }
    
    public final ExpressionNode function;
    public final BlockNode<ExpressionNode> arguments;
}
