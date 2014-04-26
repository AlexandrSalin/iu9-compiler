package ru.bmstu.iu9.compiler;

import java.util.logging.*;

/**
 * Created by IntelliJ IDEA. User: maggot Date: 19.05.11 Time: 20:31 To change
 * this template use File | Settings | File Templates.
 *
 * @author anton.bobukh
 */
public abstract class CompilerException extends Exception {
    protected CompilerException() {
        super();
        this.position = null;
    }
    protected CompilerException(String message) {
        super(message);
        this.position = null;
    }
    protected CompilerException(Throwable cause) {
        super(cause);
        this.position = null;
    }

    public CompilerException Log(String module) {
        Logger.getLogger(module).log(
            Level.WARNING,
            "",
            this
        );
        return this;
    }

    public CompilerException initPosition(Position position) {
        this.position = position;
        return this;
    }

    public Position getPosition() {
        return position;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + (position == null ? "" : " at " + this.position);
    }

    private Position position;
}