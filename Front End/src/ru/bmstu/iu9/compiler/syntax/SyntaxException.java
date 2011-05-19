package ru.bmstu.iu9.compiler.syntax;

import ru.bmstu.iu9.compiler.Position;
import ru.bmstu.iu9.compiler.PositionedException;
import ru.bmstu.iu9.compiler.lexis.token.Token;

/**
 * Created by IntelliJ IDEA. User: maggot Date: 19.05.11 Time: 20:54 To change
 * this template use File | Settings | File Templates.
 *
 * @author anton.bobukh
 */
public class SyntaxException extends PositionedException {
    protected SyntaxException(Position position) {
        super(position);
    }

    protected SyntaxException(String message, Position position) {
        super(message, position);
    }
}

class InvalidTokenException extends SyntaxException {
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

class SkippingTokenException extends SyntaxException {
    public SkippingTokenException(Token token, Position position) {
        super("Skipping token " + token, position);
        this.token = token;
    }

    public final Token token;
}