package ru.bmstu.iu9.compiler;

/**
 *
 * @author maggot
 */
final public class InvalidType extends Type {
    public InvalidType() {
        super(Typename.INVALID, -1);
    }

    @Override
    public long getAlignedAddress(long rawAddress) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
