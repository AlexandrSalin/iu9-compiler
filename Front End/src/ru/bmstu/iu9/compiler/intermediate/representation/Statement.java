package ru.bmstu.iu9.compiler.intermediate.representation;

/**
 *
 * @author maggot
 */
abstract class Statement {
    public enum Operation { 
        MUL, UNARY_MINUS, MINUS, UNARY_PLUS, PLUS,
        PARAM, CALL, RETURN, GOTO, IF_GOTO,
        INDEX, INDIRECT_ASSIGN, ASSIGN, RUN, BARRIER,
        REF, DEREF};
    
    public Operand operand1() { return null; }
    public Operand operand2() { return null; }
    public Operand result() { return null; }
    public Operation operation() { return this.operation; }
    
    protected Operation operation;
}

final class Assign extends Statement {
    public Assign(Operand value, Variable result) {
        this.operation = Operation.ASSIGN;
        this.result = result;
        this.value = value;
    }
    
    @Override
    public Operand operand1() { return this.value; }
    @Override
    public Operand result() { return this.result; }
    
    private Operand value;
    private Variable result;
}
final class IndirectAssignment extends Statement {
    public IndirectAssignment(Operand value, Variable result) {
        this.operation = Operation.INDIRECT_ASSIGN;
        this.result = result;
        this.value = value;
    }
    
    @Override
    public Operand operand1() { return this.value; }
    @Override
    public Operand result() { return this.result; }
    
    private Operand value;
    private Variable result;
}
final class BinaryOperation extends Statement {
    public BinaryOperation(Operand operand1, Operand operand2, Operand result,
            Operation operation) {
        this.result = result;
        this.operand1 = operand1;
        this.operand2 = operand2;
        this.operation = operation;
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
        this.operation = Operation.GOTO;
        this.offset = new Constant(Type.getPrimitiveType(Type.Typename.INT), offset);
    }
    
    @Override
    public Operand operand1() { return this.offset; }
    
    private Constant offset;
}
final class If extends Statement {
    public If(Variable variable, int offset) {
        this.operation = Operation.IF_GOTO;
        this.condition = variable;
        this.offset = new Constant(Type.getPrimitiveType(Type.Typename.INT), offset);
    }
    
    @Override
    public Operand operand1() { return this.condition; }
    @Override
    public Operand operand2() { return this.offset; }
    
    private Variable condition;
    private Constant offset;
}
final class Call extends Statement {
    public Call(Constant function, int argsNumber, Variable result) {
        this.operation = Operation.CALL;
        this.function = function;
        this.argsNumber = 
                new Constant(Type.getPrimitiveType(Type.Typename.INT), argsNumber);
        this.result = result;
    }
        
    public Call(Constant function, int argsNumber) {
        this.operation = Operation.CALL;
        this.function = function;
        this.argsNumber = 
                new Constant(Type.getPrimitiveType(Type.Typename.INT), argsNumber);
        this.result = null;
    }
    
    @Override
    public Operand operand1() { return this.function; }
    @Override
    public Operand operand2() { return this.argsNumber; }
    @Override
    public Operand result() { return this.result; }
    
    private Constant function;
    private Constant argsNumber;
    private Variable result;
}
final class Ref extends Statement {
    public Ref(Variable variable, Variable result) {
        this.operation = Operation.REF;
        this.variable = variable;
        this.result = result;
    }
    @Override
    public Operand operand1() { return this.variable; }
    @Override
    public Operand result() { return this.result; }
    
    private Variable variable;
    private Variable result;
}
class Deref extends Statement {
    public Deref(Variable variable, Variable result) {
        this.operation = Operation.REF;
        this.variable = variable;
        this.result = result;
    }
    @Override
    public Operand operand1() { return this.variable; }
    @Override
    public Operand result() { return this.result; }
    
    private Variable variable;
    private Variable result;
}
final class ArrayIndex extends Statement {
    public ArrayIndex(Variable array, Constant index, Variable result) {
        this.operation = Operation.INDEX;
        this.array = array;
        this.index = index;
        this.result = result;
    }
    
    private Variable array;
    private Constant index;
    private Variable result;
}
//class AddressAssignment extends Quadruple {
//
//}