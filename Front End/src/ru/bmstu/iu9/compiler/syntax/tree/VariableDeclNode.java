package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
public class VariableDeclNode extends DeclNode implements Statement { 
    public VariableDeclNode(String name, BaseTypeNode type, Position position) {
        super(NodeType.VAR_DECL, name, type, position);
        this.value = null;
    }
    public VariableDeclNode(String name, BaseTypeNode type, DebugInfo dInfo) {
        super(NodeType.VAR_DECL, name, type, dInfo);
        this.value = null;
    }
    public VariableDeclNode(
            String name, 
            BaseTypeNode type, 
            Position position,
            ExpressionNode value) {
        super(NodeType.VAR_DECL, name, type, position);
        this.value = value;
    }
    public VariableDeclNode(
            String name, 
            BaseTypeNode type, 
            DebugInfo dInfo,
            ExpressionNode value) {
        super(NodeType.VAR_DECL, name, type, dInfo);
        this.value = value;
    }
    
    @Override
    public String toString() {
        return this.realType + " " + this.name + 
                (value == null ? "" : value.toString());
    }
    
    public final ExpressionNode value;
}
