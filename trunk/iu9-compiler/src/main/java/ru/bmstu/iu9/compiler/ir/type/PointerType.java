package ru.bmstu.iu9.compiler.ir.type;

/**
 *
 * @author maggot
 */
public final class PointerType extends PrimitiveType {
    public PointerType(BaseType type, boolean constancy) {
        super(PrimitiveType.Type.POINTER, constancy);
        this.pointerType = type;
    }
    public PointerType(BaseType type) {
        this(type, false);
    }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) &&
                this.pointerType.equals(((PointerType)obj).pointerType);
    }
    @Override
    public String toString() {
        return super.toString() + " TO " + pointerType.toString();
    }
    
    public final BaseType pointerType;
}
