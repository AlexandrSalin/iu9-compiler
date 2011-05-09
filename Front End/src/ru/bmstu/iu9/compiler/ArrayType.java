package ru.bmstu.iu9.compiler;

/**
 *
 * @author maggot
 */
public final class ArrayType extends BaseType {
    public ArrayType(BaseType element, int length, boolean constancy) {
        super(Type.ARRAY, constancy, element.size * length);
        this.element = element;
        this.length = length;
    }
    public ArrayType(BaseType element, boolean constancy) {
        super(Type.ARRAY, constancy, 0);
        this.element = element;
        this.length = null;
    }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) &&
                ((ArrayType)obj).length == this.length &&
                element.equals(((ArrayType)obj).element);
    }
    @Override
    public String toString() {
        return "ARRAY OF " + element.toString() + 
                "[" + length.toString() + "]";
    }
    
    public final Integer length;
    public final BaseType element;
}
