package ru.bmstu.iu9.compiler.ir;

import ru.bmstu.iu9.compiler.*;

/**
 *
 * @author maggot
 */
abstract class Statement {
    public enum Operation { 
        PARAM, CALL, RETURN, GOTO, IF_GOTO, RUN, BARRIER, BINATY_OPERATION, 
        UNARY_OPERATION, ASSIGN, INDIRECT_ASSIGN, MEMBER_SELECT, INDEX
    };
    
    protected Statement(Operation operation) {
        this.baseOperation = operation;
    }
    
    protected final Operation baseOperation;
}


final class AssignmentStatement extends Statement {
    public AssignmentStatement(VariableOperand lhv, Operand rhv) {
        super(Operation.ASSIGN);
        this.lhv = lhv;
        this.rhv = rhv;
    }
    
    public Operand rightHandValue() { 
        return this.rhv; 
    }
    public Operand leftHandValue() { 
        return this.lhv; 
    }
    
    @Override
    public String toString() {
        return lhv + " = " + rhv;
    }
    
    private final Operand rhv;
    private final VariableOperand lhv;
}


final class IndirectAssignmentStatement extends Statement {
    public IndirectAssignmentStatement(Operand rhv, VariableOperand lhv) {
        super(Operation.INDIRECT_ASSIGN);
        this.lhv = lhv;
        this.rhv = rhv;
    }
    
    public Operand rightHandValue() { 
        return this.rhv; 
    }
    public Operand leftHandValue() { 
        return this.lhv;
    }
    
    @Override
    public String toString() {
        return "*" + lhv + " = " + rhv;
    }
    
    private final Operand rhv;
    private final VariableOperand lhv;
}


final class BinaryOperationStatement extends Statement {
    public enum Operation {
        MUL, MINUS, PLUS, INDEX, DIV, MOD, BITWISE_SHIFT_RIGHT, 
        BITWISE_SHIFT_LEFT, GREATER, GREATER_OR_EQUAL, LESS, LESS_OR_EUQAL, 
        NOT_EQUAL, EQUAL, BITWISE_AND, BITWISE_XOR, BITWISE_OR, BOOL_AND, 
        BOOL_OR
    };
    public BinaryOperationStatement(
            Operand leftOperand, 
            Operand rightOperand, 
            Operand lhv,
            Operation operation) {
        
        super(Statement.Operation.BINATY_OPERATION);
        this.lhv = lhv;
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
        this.operation = operation;
    }
    
    public final Operand leftOperand;
    public final Operand rightOperand;
    public final Operand lhv;
    public final Operation operation;
}


class Label {
    public Label() { }
    
    public void setIndex(long index) {
        this.index = index;
    }
    public long index() {
        return this.index;
    }
    
    private long index;
}

final class GoToStatement extends Statement {
    public GoToStatement(Label label) {
        super(Operation.GOTO);
        this.label = label;
    }
    
    public final Label label;
}


final class IfStatement extends Statement {
    public IfStatement(
            VariableOperand condition, 
            Label labelTrue,
            Label labelFalse) {
        
        super(Operation.IF_GOTO);
        this.condition = condition;
        this.labelTrue = labelTrue;
        this.labelFalse = labelFalse;
    }
    
    public final VariableOperand condition;
    public final Label labelTrue;
    public final Label labelFalse;
}


final class CallStatement extends Statement {
    public CallStatement(
            ConstantOperand function, 
            int argsNumber, 
            VariableOperand lhv) {
        
        super(Operation.CALL);
        this.function = function;
        this.argsNumber = 
            new ConstantOperand(
                new PrimitiveType(PrimitiveType.Type.INT, true), 
                argsNumber
            );
        this.result = lhv;
    }
        
    public CallStatement(ConstantOperand function, int argsNumber) {
        this(function, argsNumber, null);
    }
    
    public final ConstantOperand function;
    public final ConstantOperand argsNumber;
    public final VariableOperand result;
}

final class UnaryOperationStatement extends Statement {
    public enum Operation {
        POST_INC, POST_DEC, MINUS, PLUS, REF, DEREF, PRE_DEC, PRE_INC, CAST
    };
    public UnaryOperationStatement(
            VariableOperand lhv, 
            VariableOperand rhv,
            Operation operation) {
        
        super(Statement.Operation.UNARY_OPERATION);
        this.lhv = lhv;
        this.rhv = rhv;
        this.operation = operation;
    }
    
    public final VariableOperand rhv;
    public final VariableOperand lhv;
    public final Operation operation;
}

final class ArrayIndexStatement extends Statement {
    public ArrayIndexStatement(
            VariableOperand lhv,
            VariableOperand array, 
            ConstantOperand index
            ) {
        
        super(Operation.INDEX);
        this.array = array;
        this.index = index;
        this.lhv = lhv;
    }
    
    @Override
    public String toString() {
        return lhv + " = " + array + "[" + index + "]";
    }

    public final VariableOperand array;
    public final ConstantOperand index;
    public final VariableOperand lhv;
}

final class MemberSelectStatement extends Statement {
    public MemberSelectStatement(
            VariableOperand lhv,
            VariableOperand struct, 
            VariableOperand field) {
        
        super(Operation.MEMBER_SELECT);
        this.field = field;
        this.struct = struct;
        this.lhv = lhv;
    }
    
    @Override
    public String toString() {
        return lhv + " = " + struct + "." + field;
    }
    
    public final VariableOperand struct;
    public final VariableOperand field;
    public final VariableOperand lhv;
}