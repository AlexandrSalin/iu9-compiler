package ru.bmstu.iu9.compiler;

/**
 *
 * @author maggot
 */
public final class IntegerConstantToken extends ConstantToken {
    public IntegerConstantToken(Fragment coordinates, int value) {
        super(coordinates, Type.CONST_INT);
        this.value = value;
    }
    public IntegerConstantToken(Position starting, Position ending, int value) {
        super(new Fragment(starting, ending), Type.CONST_INT);
        this.value = value;
    }
    
    @Override
    public Integer value() { return this.value; }
    
    private int value;
}
