package ru.bmstu.iu9.compiler;

/**
 *
 * @author maggot
 */
public final class DoubleConstantToken extends ConstantToken {
    public DoubleConstantToken(Fragment coordinates, double value) {
        super(coordinates, Type.CONST_INT);
        this.value = value;
    }
    public DoubleConstantToken(Position starting, Position ending, double value) {
        super(new Fragment(starting, ending), Type.CONST_DOUBLE);
        this.value = value;
    }
    
    @Override
    public Double value() { return this.value; }
    
    private double value;
}
