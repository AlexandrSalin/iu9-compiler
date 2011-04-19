package ru.bmstu.iu9.compiler.intermediate.representation;

import ru.bmstu.iu9.compiler.*;

/**
 *
 * @author maggot
 */
abstract class Statement {
    public enum Operation { 
        MUL, UNARY_MINUS, MINUS, PLUS, PARAM, CALL, RETURN, GOTO, IF_GOTO,
        INDEX, INDIRECT_ASSIGN, ASSIGN, RUN, BARRIER, REF, DEREF, DIV, MOD,
        MEMBER_SELECT, 
    };
    
    protected Statement(Operation operation) {
        this.operation = operation;
    }
    
    protected final Operation operation;
}


final class Assignment extends Statement {
    public Assignment(Operand rhv, VariableOperand lhv) {
        super(Operation.ASSIGN);
        this.lhv = lhv;
        this.rhv = rhv;
    }
    
    public Operand rightHandValue() { return this.rhv; }
    public Operand leftHandValue() { return this.lhv; }
    
    private final Operand rhv;
    private final VariableOperand lhv;
}


final class IndirectAssignment extends Statement {
    public IndirectAssignment(Operand rhv, VariableOperand lhv) {
        super(Operation.INDIRECT_ASSIGN);
        this.lhv = lhv;
        this.rhv = rhv;
    }
    
    public Operand rightHandValue() { return this.rhv; }
    public Operand leftHandValue() { return this.lhv; }
    
    private final Operand rhv;
    private final VariableOperand lhv;
}


final class BinaryOperation extends Statement {
    public BinaryOperation(Operand leftOperand, Operand rightOperand, Operand lhv,
            Operation operation) {
        super(operation);
        this.lhv = lhv;
        this.leftOperand = leftOperand;
        this.rightOperand = rightOperand;
    }
    
    public Operand leftOperand() { return this.leftOperand; }
    public Operand rightOperand() { return this.rightOperand; }
    public Operand leftHandValue() { return this.lhv; }
    
    private final Operand leftOperand;
    private final Operand rightOperand;
    private final Operand lhv;
}


final class GoTo extends Statement {
    public GoTo(long index) {
        super(Operation.GOTO);
        this.index = new ConstantOperand(
                new PrimitiveType(PrimitiveType.Typename.INT, true), index);
    }
    
    public Operand index() { return this.index; }
    
    private final ConstantOperand index;
}


final class If extends Statement {
    public If(VariableOperand condition, int index) {
        super(Operation.IF_GOTO);
        this.condition = condition;
        this.index = new ConstantOperand(new PrimitiveType(PrimitiveType.Typename.INT, true), index);
    }
    
    public Operand condition() { return this.condition; }
    public Operand index() { return this.index; }
    
    private final VariableOperand condition;
    private final ConstantOperand index;
}


final class Call extends Statement {
    public Call(ConstantOperand function, int argsNumber, VariableOperand lhv) {
        super(Operation.CALL);
        this.function = function;
        this.argsNumber = new ConstantOperand(
                new PrimitiveType(PrimitiveType.Typename.INT, true), argsNumber);
        this.result = lhv;
    }
        
    public Call(ConstantOperand function, int argsNumber) {
        this(function, argsNumber, null);
    }
    
    public Operand condition() { return this.function; }
    public Operand argsNumber() { return this.argsNumber; }
    public Operand leftHandValue() { return this.result; }
    
    private final ConstantOperand function;
    private final ConstantOperand argsNumber;
    private final VariableOperand result;
}


final class Ref extends Statement {
    public Ref(VariableOperand rhv, VariableOperand lhv) {
        super(Operation.REF);
        this.rhv = rhv;
        this.lhv = lhv;
    }
    
    public Operand rightHandValue() { return this.rhv; }
    public Operand leftHandValue() { return this.lhv; }
    
    private final VariableOperand rhv;
    private final VariableOperand lhv;
}


class Deref extends Statement {
    public Deref(VariableOperand rhv, VariableOperand lhv) {
        super(Operation.REF);
        this.rhv = rhv;
        this.lhv = lhv;
    }
    
    public Operand rightHandValue() { return this.rhv; }
    public Operand leftHandValue() { return this.lhv; }
    
    private final VariableOperand rhv;
    private final VariableOperand lhv;
}


final class ArrayIndex extends Statement {
    public ArrayIndex(VariableOperand array, ConstantOperand index, 
            VariableOperand lhv) {
        super(Operation.INDEX);
        this.array = array;
        this.index = index;
        this.lhv = lhv;
    }
    
    public VariableOperand array() { return this.array; }
    public ConstantOperand index() { return this.index; }
    public VariableOperand leftHandValue() { return this.lhv; }
    
    private final VariableOperand array;
    private final ConstantOperand index;
    private final VariableOperand lhv;
}

final class MemberSelect extends Statement {
    public MemberSelect(VariableOperand struct, VariableOperand field) {
        super(Operation.MEMBER_SELECT);
        this.field = field;
        this.struct = struct;
    }
    
    public VariableOperand struct() { return this.struct; }
    public VariableOperand field() { return this.field; }
    
    private final VariableOperand struct;
    private final VariableOperand field;
}