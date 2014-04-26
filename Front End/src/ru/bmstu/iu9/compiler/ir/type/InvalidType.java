package ru.bmstu.iu9.compiler.ir.type;

/**
 *
 * @author anton.bobukh
 */
final public class InvalidType extends BaseType {
    public InvalidType() {
        super(Type.INVALID, -1);
    }
}
