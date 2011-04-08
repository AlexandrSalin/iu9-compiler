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
    };
    
    protected Statement(Operation operation) {
        this.operation = operation;
    }
    
    public Operand operand1() { return null; }
    public Operand operand2() { return null; }
    public Operand result() { return null; }
    public Operation operation() { return this.operation; }
    
    protected Operation operation;
}


final class Assignment extends Statement {
    public Assignment(Operand value, VariableOperand result) {
        super(Operation.ASSIGN);
        this.result = result;
        this.value = value;
    }
    
    @Override
    public Operand operand1() { return this.value; }
    @Override
    public Operand result() { return this.result; }
    
    private Operand value;
    private VariableOperand result;
}


final class IndirectAssignment extends Statement {
    public IndirectAssignment(Operand value, VariableOperand result) {
        super(Operation.INDIRECT_ASSIGN);
        this.result = result;
        this.value = value;
    }
    
    @Override
    public Operand operand1() { return this.value; }
    @Override
    public Operand result() { return this.result; }
    
    private Operand value;
    private VariableOperand result;
}


final class BinaryOperation extends Statement {
    public BinaryOperation(Operand operand1, Operand operand2, Operand result,
            Operation operation) {
        super(operation);
        this.result = result;
        this.operand1 = operand1;
        this.operand2 = operand2;
    }
    
    @Override
    public Operand operand1() { return this.operand1; }
    @Override
    public Operand operand2() { return this.operand2; }
    @Override
    public Operand result() { return this.result; }
    
    private Operand operand1;
    private Operand operand2;
    private Operand result;
}


final class GoTo extends Statement {
    public GoTo(int offset) {
        super(Operation.GOTO);
        this.offset = new ConstantOperand(new PrimitiveType(PrimitiveType.Typename.INT, true), offset);
    }
    
    @Override
    public Operand operand1() { return this.offset; }
    
    private ConstantOperand offset;
}


final class If extends Statement {
    public If(VariableOperand variable, int offset) {
        super(Operation.IF_GOTO);
        this.condition = variable;
        this.offset = new ConstantOperand(new PrimitiveType(PrimitiveType.Typename.INT, true), offset);
    }
    
    @Override
    public Operand operand1() { return this.condition; }
    @Override
    public Operand operand2() { return this.offset; }
    
    private VariableOperand condition;
    private ConstantOperand offset;
}


final class Call extends Statement {
    public Call(ConstantOperand function, int argsNumber, VariableOperand result) {
        super(Operation.CALL);
        this.function = function;
        this.argsNumber = 
                new ConstantOperand(new PrimitiveType(PrimitiveType.Typename.INT, true), argsNumber);
        this.result = result;
    }
        
    public Call(ConstantOperand function, int argsNumber) {
        this(function, argsNumber, null);
    }
    
    @Override
    public Operand operand1() { return this.function; }
    @Override
    public Operand operand2() { return this.argsNumber; }
    @Override
    public Operand result() { return this.result; }
    
    private ConstantOperand function;
    private ConstantOperand argsNumber;
    private VariableOperand result;
}


final class Ref extends Statement {
    public Ref(VariableOperand variable, VariableOperand result) {
        super(Operation.REF);
        this.variable = variable;
        this.result = result;
    }
    @Override
    public Operand operand1() { return this.variable; }
    @Override
    public Operand result() { return this.result; }
    
    private VariableOperand variable;
    private VariableOperand result;
}


class Deref extends Statement {
    public Deref(VariableOperand variable, VariableOperand result) {
        super(Operation.REF);
        this.variable = variable;
        this.result = result;
    }
    @Override
    public Operand operand1() { return this.variable; }
    @Override
    public Operand result() { return this.result; }
    
    private VariableOperand variable;
    private VariableOperand result;
}


final class ArrayIndex extends Statement {
    public ArrayIndex(VariableOperand array, ConstantOperand index, 
            VariableOperand result) {
        super(Operation.INDEX);
        this.array = array;
        this.index = index;
        this.result = result;
    }
    
    private VariableOperand array;
    private ConstantOperand index;
    private VariableOperand result;
}