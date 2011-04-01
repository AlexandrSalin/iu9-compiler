package ru.bmstu.iu9.compiler;

/**
 *
 * @author maggot
 */
public abstract class ConstantToken extends Token {
    protected ConstantToken(Fragment coordinates, Type type) {
        super(coordinates, type);
    }
}
