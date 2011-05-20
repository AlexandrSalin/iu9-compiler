package ru.bmstu.iu9.compiler.syntax;

import ru.bmstu.iu9.compiler.*;
import ru.bmstu.iu9.compiler.lexis.token.Token;

/**
 * Created by IntelliJ IDEA. User: maggot Date: 19.05.11 Time: 20:54 To change
 * this template use File | Settings | File Templates.
 *
 * @author anton.bobukh
 */
public class SyntaxException extends LoggedException {
    protected SyntaxException() {
        super("ru.bmtsu.iu9.compiler.syntax");
    }

    protected SyntaxException(String message) {
        super("ru.bmtsu.iu9.compiler.syntax", message);
    }
}

class PositionedSyntaxException extends SyntaxException {
    public PositionedSyntaxException(Position position) {
        super();
        this.position = position;
    }
    public PositionedSyntaxException(String message, Position position) {
        super(message);
        this.position = position;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " at " + position;
    }

    public final Position position;
}

class InvalidTokenException extends PositionedSyntaxException {
    public InvalidTokenException(
            Token found,
            Token required,
            Position position) {

        super(
            "Invalid token. Found " + found + ", required " + required,
            position
        );
        this.found = found;
        this.required = required;
    }

    public final Token found;
    public final Token required;
}

class SkippingTokenException extends PositionedSyntaxException {
    public SkippingTokenException(Token token, Position position) {
        super("Skipping token " + token, position);
        this.token = token;
    }

    public final Token token;
}