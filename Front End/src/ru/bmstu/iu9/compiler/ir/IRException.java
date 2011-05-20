package ru.bmstu.iu9.compiler.ir;

import ru.bmstu.iu9.compiler.*;
import ru.bmstu.iu9.compiler.syntax.tree.*;

/**
 * Created by IntelliJ IDEA. User: maggot Date: 19.05.11 Time: 22:11 To change
 * this template use File | Settings | File Templates.
 *
 * @author anton.bobukh
 */

class IRException extends LoggedException {
    protected IRException() {
        super("ru.bmstu.iu9.compiler.ir");
    }

    protected IRException(String message) {
        super(message, "ru.bmstu.iu9.compiler.ir");
    }
}

class PositionedIRException extends IRException {
    public PositionedIRException(Position position) {
        super();
        this.position = position;
    }
    public PositionedIRException(String message, Position position) {
        super(message);
        this.position = position;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + " at " + position;
    }

    public final Position position;
}

class InvalidLeftHandValueException extends PositionedIRException {
    public InvalidLeftHandValueException(
            BinaryOperationNode.Operation operation,
            Position position) {

        super(
            "Invalid left hand value. Result of " + operation + " can " + "" +
                "not be left hand value",
            position
        );
        this.operation = operation.toString();
    }
    public InvalidLeftHandValueException(
            UnaryOperationNode.Operation operation,
            Position position) {

        super(
            "Invalid left hand value. Result of " + operation + " can " + "" +
                "not be left hand value",
            position
        );
        this.operation = operation.toString();
    }
    public InvalidLeftHandValueException(
            BaseNode.NodeType nodeType,
            Position position) {

        super(
            "Invalid left hand value. Result of " + nodeType + " can " + "" +
                "not be left hand value",
            position
        );
        this.operation = nodeType.toString();
    }

    public final String operation;
}

class UnexpectedOperationException extends PositionedIRException {
    public UnexpectedOperationException(Position position) {
        super(position);
    }
}

/**
 * @todo Test error report in case of initCause call
 */
class NonGenerationException extends IRException {
    public NonGenerationException() {
        super();
    }
}