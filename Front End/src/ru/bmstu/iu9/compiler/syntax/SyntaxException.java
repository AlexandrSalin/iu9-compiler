package ru.bmstu.iu9.compiler.syntax;

import ru.bmstu.iu9.compiler.*;
import ru.bmstu.iu9.compiler.lexis.token.Token;


/**
 * Created by IntelliJ IDEA. User: maggot Date: 19.05.11 Time: 20:54 To change
 * this template use File | Settings | File Templates.
 *
 * @author anton.bobukh
 */
public class SyntaxException extends CompilerException {
    protected SyntaxException() {
        super();
    }

    protected SyntaxException(String message) {
        super(message);
    }
    
    protected SyntaxException(Throwable cause) {
        super(cause);
    }
}

class InvalidTokenException extends SyntaxException {
    public InvalidTokenException(Token found, Token.Type required) {
        super("Invalid token. Found " + found + ", required " + required);
        this.found = found;
        this.required = new Token.Type[] { required };
    }
    public InvalidTokenException(Token found, Token.Type[] required) {
        super("Invalid token. Found " + found + ", required " + required);
        this.found = found;
        this.required = required;
    }

    public final Token found;
    public final Token.Type[] required;
}

class NonAnalysisException extends SyntaxException {
    public NonAnalysisException() {
        super();
    }
    public NonAnalysisException(Throwable cause) {
        super(cause);
    }
}

class SkippingTokenException extends SyntaxException {
    public SkippingTokenException(Token token) {
        super("Skipping token " + token);
        this.token = token;
    }

    public final Token token;
}