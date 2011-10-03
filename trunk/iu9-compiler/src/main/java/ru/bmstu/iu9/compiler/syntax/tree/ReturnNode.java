package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
final public class ReturnNode extends JmpNode implements Statement {
    public ReturnNode(Position position) {
        super(BaseNode.NodeType.RETURN, position);
        this.returnExpr = null;
    }
    public ReturnNode(ExpressionNode expression, Position position) {
        super(BaseNode.NodeType.RETURN, position);
        this.returnExpr = expression;
    }
    public ReturnNode(DebugInfo dInfo) {
        super(BaseNode.NodeType.RETURN, dInfo);
        this.returnExpr = null;
    }
    public ReturnNode(ExpressionNode expression, DebugInfo dInfo) {
        super(BaseNode.NodeType.RETURN, dInfo);
        this.returnExpr = expression;
    }
    
    public BaseNode getNode() {
        return this;
    }
    
    public final ExpressionNode returnExpr;
}
