package ru.bmstu.iu9.compiler.lexis.token;

import ru.bmstu.iu9.compiler.Fragment;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
public final class CharConstantToken extends ConstantToken {
    public CharConstantToken(Fragment coordinates, int value) {
        super(coordinates, Type.CONST_CHAR);
        this.value = value;
    }
    public CharConstantToken(Position starting, Position ending, int value) {
        super(new Fragment(starting, ending), Type.CONST_CHAR);
        this.value = value;
    }
    
    @Override
    public Integer value() { return this.value; }
    
    private int value;
}
