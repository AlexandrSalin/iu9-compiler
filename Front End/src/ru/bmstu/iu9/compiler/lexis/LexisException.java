package ru.bmstu.iu9.compiler.lexis;

import ru.bmstu.iu9.compiler.*;

/**
 * Created by IntelliJ IDEA. User: maggot Date: 19.05.11 Time: 20:49 To change
 * this template use File | Settings | File Templates.
 */
public class LexisException extends LoggedException {
    protected LexisException() {
        super("ru.bmstu.iu9.compiler.lexis");
    }

    protected LexisException(String message) {
        super(message, "ru.bmstu.iu9.compiler.lexis");
    }
}

class PositionedLexisException extends LexisException {
    protected PositionedLexisException(Position position) {
        super();
        this.position = position;
    }

    protected PositionedLexisException(String message, Position position) {
        super(message);
        this.position = position;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " at " + position;
    }

    public final Position position;
}

class InvalidCodePointException extends PositionedLexisException {
    public InvalidCodePointException(CodePoint codePoint) {
        super(
            "Invalid code point found '" + codePoint.asChar() + "'",
            codePoint.position()
        );
        this.codePoint = codePoint;
    }

    public final CodePoint codePoint;
}

class InvalidNumberFormatException extends PositionedLexisException {
    public InvalidNumberFormatException(String number, Position position) {
        super("Invalid number constant \"" + number + "\"", position);
        this.number = number;
    }

    public final String number;
}