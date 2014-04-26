package ru.bmstu.iu9.compiler.semantics;

import ru.bmstu.iu9.compiler.*;
import ru.bmstu.iu9.compiler.ir.type.*;
import ru.bmstu.iu9.compiler.syntax.tree.*;

/**
 * Created by IntelliJ IDEA. User: maggot Date: 19.05.11 Time: 20:55 To change
 * this template use File | Settings | File Templates.
 *
 * @author anton.bobukh
 */
public class SymanticException extends CompilerException {
    protected SymanticException() {
        super();
    }

    protected SymanticException(String message) {
        super(message);
    }
}

class NonAnalysisErrorException extends SymanticException {
    public NonAnalysisErrorException() {
        super();
    }
}

class IncompatibleTypesInStatementException extends SymanticException {
    public IncompatibleTypesInStatementException(
            BaseType typeOne,
            BaseType typeTwo) {

        super("Incompatible types: " + typeOne + ", " + typeTwo);
        this.typeOne = typeOne;
        this.typeTwo = typeTwo;
    }

    public final BaseType typeOne;
    public final BaseType typeTwo;
}

class UnexpectedTypeException extends SymanticException {
    public UnexpectedTypeException(
            BaseType found,
            BaseType expected) {

        super(
            "Unexpected type. Found " + found + ", required " + expected
        );
        this.found = found;
        this.expected = expected.toString();
    }

    public UnexpectedTypeException(
            BaseType found,
            BaseType.Type expected) {

        super(
            "Unexpected type. Found " + found + ", required " + expected
        );
        this.found = found;
        this.expected = expected.toString();
    }

    public UnexpectedTypeException(
            BaseType found,
            PrimitiveType.Type expected) {

        super(
            "Unexpected type. Found " + found + ", required " + expected
        );
        this.found = found;
        this.expected = expected.toString();
    }

    public final BaseType found;
    public final String expected;
}

class OperationIncompatibleWithTypeException extends SymanticException {
    public OperationIncompatibleWithTypeException(
            BinaryOperationNode.Operation operation,
            BaseType type) {

        super(
            "Operation " + operation + " is incompatible with type " + type
        );

        this.operation = operation.toString();
        this.type = type;
    }

    public OperationIncompatibleWithTypeException(
            UnaryOperationNode.Operation operation,
            BaseType type) {

        super(
            "Operation " + operation + " is incompatible with type " + type
        );

        this.operation = operation.toString();
        this.type = type;
    }

    public final String operation;
    public final BaseType type;
}

class UseOfUndeclaredVariableException extends SymanticException {
    public UseOfUndeclaredVariableException(String name) {

        super("Use of undeclared variable \"" + name + "\"");
        this.name = name;
    }

    public final String name;
}

class UseOfUndeclaredTypeException extends SymanticException {
    public UseOfUndeclaredTypeException(String typename) {

        super("Use of undeclared type \"" + typename + "\"");
        this.typename = typename;
    }

    public final String typename;
}

class MissingReturnStatementException extends SymanticException {
    public MissingReturnStatementException() {
        super("Missing return statement");
    }
}