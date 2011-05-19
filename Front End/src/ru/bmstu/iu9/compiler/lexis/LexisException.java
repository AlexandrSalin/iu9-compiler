package ru.bmstu.iu9.compiler.lexis;

import ru.bmstu.iu9.compiler.PositionedException;
import ru.bmstu.iu9.compiler.Position;

/**
 * Created by IntelliJ IDEA. User: maggot Date: 19.05.11 Time: 20:49 To change
 * this template use File | Settings | File Templates.
 */
public class LexisException extends PositionedException {
    protected LexisException(Position position) {
        super(position);
    }

    protected LexisException(String message, Position position) {
        super(message, position);
    }
}

class InvalidCodePointException extends LexisException {
    public InvalidCodePointException(CodePoint codePoint) {
        super(
            "Invalid code point found '" + codePoint.asChar() + "'",
            codePoint.position()
        );
        this.codePoint = codePoint;
    }

    public final CodePoint codePoint;
}

class InvalidNumberFormatException extends LexisException {
    public InvalidNumberFormatException(String number, Position position) {
        super("Invalid number constant \"" + number + "\"", position);
        this.number = number;
    }

    public final String number;
}