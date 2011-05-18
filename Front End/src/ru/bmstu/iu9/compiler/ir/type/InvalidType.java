package ru.bmstu.iu9.compiler.ir.type;

/**
 *
 * @author maggot
 */
final public class InvalidType extends BaseType {
    public InvalidType() {
        super(Type.INVALID, -1);
    }
}
