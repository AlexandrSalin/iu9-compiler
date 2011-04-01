package ru.bmstu.iu9.compiler;

/**
 *
 * @author maggot
 */
public final class PointerType extends PrimitiveType {
    public PointerType(Type type, boolean isConstant) {
        super(Typename.POINTER, isConstant);
        this.type = type;
    }
    public PointerType(boolean isConstant) {
        super(Typename.POINTER, isConstant);
    }
    
    public Type type() { return type; }
    public void setType(Type type) { this.type = type; } 
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) &&
                this.type.equals(((PointerType)obj).type);
    }
    @Override
    public String toString() {
        return super.toString() + " TO " + type.toString();
    }
    
    private Type type;
}
