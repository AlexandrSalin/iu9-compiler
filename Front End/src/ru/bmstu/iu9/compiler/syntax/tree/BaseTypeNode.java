/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.*;

/**
 *
 * @author maggot
 */
public class BaseTypeNode extends BaseNode {
    public enum Type { 
        ARRAY, STRUCT, FUNCTION, PRIMITIVE_TYPE, INVALID
    };
    
    public static BaseTypeNode InvalidNode(Position position) {
        return new BaseTypeNode(
                BaseTypeNode.Type.INVALID, 
                false,
                new DebugInfo(position));
    }
    public static BaseTypeNode InvalidNode(DebugInfo dInfo) {
        return new BaseTypeNode(
                BaseTypeNode.Type.INVALID,
                false,
                dInfo);
    }
    
    protected BaseTypeNode(
            BaseTypeNode.Type type, 
            boolean constancy,
            DebugInfo dInfo) {
        
        super(BaseNode.NodeType.TYPE);
        this.type = type.ordinal();
        this.constancy = constancy;
        this.dInfo = dInfo;
    }
    
    protected BaseTypeNode(
            BaseTypeNode.Type type, 
            boolean constancy,
            Position position) {
        
        super(BaseNode.NodeType.TYPE);
        this.type = type.ordinal();
        this.constancy = constancy;
        this.dInfo = new DebugInfo(position);
    }
    
    public Type type() {
        return Type.values()[this.type];
    }
    public BaseType realType() {
        return this.realType;
    }
    public void setRealType(BaseType realType) {
        this.realType = realType;
    }
    

    @Override
    public String toString() {
        return (constancy ? "CONST " : "") + 
                Type.values()[this.type].toString();
    }
    
    private final int type;
    public boolean constancy;
    public final DebugInfo dInfo;
    
    protected BaseType realType;
}
