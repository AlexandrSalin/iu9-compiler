package ru.bmstu.iu9.compiler;

/**
 *
 * @author maggot
 */
final public class InvalidType extends Type {
    public InvalidType() {
        super(Typename.INVALID, -1);
    }
}
