package ru.bmstu.iu9.compiler.intermediate.representation;

/**
 *
 * @author maggot
 */
public class Instruction {

}
class Quadruple {
    public enum Operation { 
        MUL, UNARY_MINUS, MINUS, UNARY_PLUS, PLUS,
        PARAM, CALL, RETURN, GOTO, IF_GOTO,
        INDEX };
    
    protected Quadruple(Operation op, Address arg1, Address arg2, Address result) {
        this.operation = op;
        this.argument1 = arg1;
        this.argument2 = arg2;
        this.result = result;
    }
    public Operation operation() { return this.operation; }
    public Address argument1() { return this.argument1; }
    public Address argument2() { return this.argument2; }
    public Address result() { return this.result; }
    public String label() { return this.label; }
    
    public void setLabel() { 
        this.label = "LABEL" + counter; 
        ++counter;
    }
    
    protected Operation operation;
    protected Address argument1;
    protected Address argument2;
    protected Address result;
    protected String label;
    
    private static int counter = 0;
}



class Assignment extends Quadruple {
    public Assignment(Address result, Operation op, Address arg1, Address arg2) {
        super(op, arg1, arg2, result);
    }
}

class GoTo extends Quadruple {
    public GoTo(Integer offset) {
        super(Operation.GOTO, 
            new Constant(new PrimitiveType(PrimitiveType.Typename.INT), offset), 
            null, 
            null);
    }
}
class If extends Quadruple {
    public If(Address variable, Integer offset) {
        super(Operation.IF_GOTO,
            variable,
            new Constant(new PrimitiveType(PrimitiveType.Typename.INT), offset),
            null);
    }
}
class Call extends Quadruple {
    public Call(Address function, int argsNumber, Address result) {
        super(Operation.CALL,
            function,
            new Constant(new PrimitiveType(PrimitiveType.Typename.INT), argsNumber),
            result);
    }
        
    public Call(Address function, int argsNumber) {
        super(Operation.CALL,
            function,
            new Constant(new PrimitiveType(PrimitiveType.Typename.INT), argsNumber),
            null);
    }
}

class ArrayIndex extends Quadruple {
    public ArrayIndex(Address array, Address index, Address result) {
        super(Operation.INDEX,
            array,
            index,
            result);
    }
}
//class AddressAssignment extends Quadruple {
//
//}