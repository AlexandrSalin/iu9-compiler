package ru.bmstu.iu9.compiler;

/**
 *
 * @author maggot
 */
public final class ArrayType extends Type {
    public ArrayType(Type elementType, int length) {
        super(Typename.ARRAY, elementType.size * length);
        this.type = elementType;
        this.length = length;
    }
    public ArrayType(Type elementType) {
        super(Typename.ARRAY, 0);
        this.type = elementType;
        this.length = null;
    }
    
//    public void setElementType(Type type) { this.type = type; }
//    public void setLenght(int length) { this.length = length; }
    public Integer length() { return this.length; }
    public Type elementType() { return this.type; }
    public void setElementType(Type type) {
        this.type = type;
    }
    
    @Override
    public long getAlignedAddress(long rawAddress) {
        return rawAddress + (this.type.size - rawAddress % this.type.size);
    }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) &&
                ((ArrayType)obj).length() == this.length &&
                type.equals(((ArrayType)obj).elementType());
    }
    @Override
    public String toString() {
        return "(" + super.toString() + " OF " + type.toString() + ")[" + length.toString() + "]";
    }
    
    private Integer length;
    private Type type;
}
