package ru.bmstu.iu9.compiler.lexis;

import ru.bmstu.iu9.compiler.*;

/**
 * Created by IntelliJ IDEA. User: maggot Date: 19.05.11 Time: 20:49 To change
 * this template use File | Settings | File Templates.
 */
public class LexisException extends CompilerException {
    protected LexisException() {
        super();
    }

    protected LexisException(String message) {
        super(message);
    }
}

class InvalidLexemeException extends  LexisException {
    public InvalidLexemeException(String lexeme) {
        super("Invalid lexeme \"" + lexeme + "\"");
        this.lexeme = lexeme;
    }
    
    public final String lexeme;
}

class InvalidCodePointException extends LexisException {
    public InvalidCodePointException(CodePoint codePoint) {
        super(
            "Invalid code point found '" + codePoint.asChar() + "'"
        );
        this.codePoint = codePoint;
    }

    public final CodePoint codePoint;
}

class InvalidNumberFormatException extends LexisException {
    public InvalidNumberFormatException(String number) {
        super("Invalid number constant \"" + number + "\"");
        this.number = number;
    }

    public final String number;
}