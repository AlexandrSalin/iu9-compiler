package ru.bmstu.iu9.compiler.lexis.token;

import ru.bmstu.iu9.compiler.Fragment;

/**
 *
 * @author maggot
 */
public abstract class ConstantToken extends Token {
    protected ConstantToken(Fragment coordinates, Type type) {
        super(coordinates, type);
    }
}
