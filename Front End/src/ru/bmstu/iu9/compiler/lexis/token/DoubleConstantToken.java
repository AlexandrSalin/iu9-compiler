package ru.bmstu.iu9.compiler.lexis.token;

import ru.bmstu.iu9.compiler.Fragment;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
public final class DoubleConstantToken extends ConstantToken<Double> {
    public DoubleConstantToken(Fragment coordinates, double value) {
        super(coordinates, value, Type.CONST_DOUBLE);
    }
    public DoubleConstantToken(Position starting, Position ending, double value) {
        super(new Fragment(starting, ending), value, Type.CONST_DOUBLE);
    }
}
