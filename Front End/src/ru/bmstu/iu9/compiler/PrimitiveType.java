package ru.bmstu.iu9.compiler;

/**
 *
 * @author maggot
 */
public class PrimitiveType extends Type {
    public PrimitiveType(Typename typename, boolean isConstant) {
        super(typename);
        this.isConstant = isConstant;
    }
    
    public boolean isConstant() { return this.isConstant; }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
    @Override
    public String toString() {
        return (isConstant) ? "CONST " : "" + super.toString();
    }
    
    protected final boolean isConstant;
}
