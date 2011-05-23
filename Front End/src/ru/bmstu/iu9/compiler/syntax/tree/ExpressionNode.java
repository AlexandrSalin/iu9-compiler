package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.Position;
import ru.bmstu.iu9.compiler.ir.type.BaseType;
import ru.bmstu.iu9.compiler.DebugInfo;

/**
 *
 * @author anton.bobukh
 */
public class ExpressionNode extends BaseNode implements Statement {
    public static ExpressionNode InvalidNode(Position position) {
        return new ExpressionNode(NodeType.INVALID, position);
    }
    
    protected ExpressionNode(BaseNode.NodeType nodeType, Position position) {
        super(nodeType);
        this.dInfo = new DebugInfo(position);
    }
    protected ExpressionNode(BaseNode.NodeType nodeType, DebugInfo dInfo) {
        super(nodeType);
        this.dInfo = dInfo;
    }
    
    public BaseNode getNode() {
        return this;
    }
    
    @Override
    public String toString() {
        return super.toString();
    }
    
    public BaseType realType() { return this.realType; }
    public void setRealType(BaseType type) { this.realType = type; }
    
    protected BaseType realType;
    public final DebugInfo dInfo;
}