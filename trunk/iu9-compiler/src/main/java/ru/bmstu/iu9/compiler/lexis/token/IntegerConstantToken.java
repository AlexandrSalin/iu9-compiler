package ru.bmstu.iu9.compiler.lexis.token;

import ru.bmstu.iu9.compiler.Fragment;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
public final class IntegerConstantToken extends ConstantToken<Integer> {
    public IntegerConstantToken(Fragment coordinates, int value) {
        super(coordinates, value, Type.CONST_INT);
    }
    public IntegerConstantToken(Position starting, Position ending, int value) {
        super(new Fragment(starting, ending), value, Type.CONST_INT);
    }
}
