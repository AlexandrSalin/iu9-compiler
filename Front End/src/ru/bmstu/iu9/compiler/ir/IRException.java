package ru.bmstu.iu9.compiler.ir;

import ru.bmstu.iu9.compiler.*;
import ru.bmstu.iu9.compiler.syntax.tree.*;

/**
 * Created by IntelliJ IDEA. User: maggot Date: 19.05.11 Time: 22:11 To change
 * this template use File | Settings | File Templates.
 *
 * @author anton.bobukh
 */

class IRException extends CompilerException {
    protected IRException() {
        super();
    }
    protected IRException(Throwable cause) {
        super(cause);
    }

    protected IRException(String message) {
        super(message);
    }
}

class InvalidLeftHandValueException extends IRException {
    public InvalidLeftHandValueException(
            BinaryOperationNode.Operation operation) {

        super(
            "Invalid left hand value. Result of " + operation + " can " + "" +
                "not be left hand value"
        );
        this.operation = operation.toString();
    }

    public InvalidLeftHandValueException(
            UnaryOperationNode.Operation operation) {

        super(
            "Invalid left hand value. Result of " + operation + " can " + "" +
                "not be left hand value"
        );
        this.operation = operation.toString();
    }

    public InvalidLeftHandValueException(
            BaseNode.NodeType nodeType,
            Position position) {

        super(
            "Invalid left hand value. Result of " + nodeType + " can " + "" +
                "not be left hand value"
        );
        this.operation = nodeType.toString();
    }

    public final String operation;
}

class UnexpectedOperationException extends IRException {
    public UnexpectedOperationException() {
        super();
    }
}

/**
 * @todo Test error report in case of initCause call
 */
class NonGenerationException extends IRException {
    public NonGenerationException(Throwable cause) {
        super(cause);
    }
    public NonGenerationException() {
        super();
    }
}