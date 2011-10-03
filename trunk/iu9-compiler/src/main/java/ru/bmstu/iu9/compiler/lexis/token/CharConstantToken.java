package ru.bmstu.iu9.compiler.lexis.token;

import ru.bmstu.iu9.compiler.Fragment;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
public final class CharConstantToken extends ConstantToken<Integer> {
    public CharConstantToken(Fragment coordinates, int value) {
        super(coordinates, value, Type.CONST_CHAR);
    }
    public CharConstantToken(Position starting, Position ending, int value) {
        super(new Fragment(starting, ending), value, Type.CONST_CHAR);
    }
}
