/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
public class PrimitiveTypeNode extends BaseTypeNode {
    public enum Type { 
        INT, BOOL, FLOAT, DOUBLE, CHAR, VOID, LONG, POINTER
    };
    
    public PrimitiveTypeNode(
            PrimitiveTypeNode.Type primitive, 
            boolean constancy,
            Position position) {
        super(BaseTypeNode.Type.PRIMITIVE_TYPE, constancy, position);
        this.primitive = primitive.ordinal();
    }
    public PrimitiveTypeNode(
            PrimitiveTypeNode.Type primitive, 
            boolean constancy,
            DebugInfo dInfo) {
        super(BaseTypeNode.Type.PRIMITIVE_TYPE, constancy, dInfo);
        this.primitive = primitive.ordinal();
    }
    
    public Type primitive() { return Type.values()[this.primitive]; }
    
    @Override
    public String toString() {
        return (constancy ? "CONST " : "") + this.primitive().toString();
    }
    
    private final int primitive;
}
