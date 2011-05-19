package ru.bmstu.iu9.compiler;

/**
 * Created by IntelliJ IDEA. User: maggot Date: 19.05.11 Time: 20:31 To change
 * this template use File | Settings | File Templates.
 *
 * @author anton.bobukh
 */
public abstract class CompilerException extends Exception {
    protected CompilerException() {
        super();
    }
    public CompilerException(String message) {
        super(message);
    }
}
