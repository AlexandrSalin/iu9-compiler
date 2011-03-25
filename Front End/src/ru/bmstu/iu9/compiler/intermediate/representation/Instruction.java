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
        INDEX, INDIRECT_ASSIGN, RUN, BARRIER,
        REF, DEREF};
    
    protected Quadruple(Operation op, Operand arg1, Operand arg2, Operand result) {
        this.operation = op;
        this.argument1 = arg1;
        this.argument2 = arg2;
        this.result = result;
    }
    public Operation operation() { return this.operation; }
    public Operand argument1() { return this.argument1; }
    public Operand argument2() { return this.argument2; }
    public Operand result() { return this.result; }
    public String label() { return this.label; }
    
    public void setLabel() { 
        this.label = "LABEL" + counter; 
        ++counter;
    }
    
    protected Operation operation;
    protected Operand argument1;
    protected Operand argument2;
    protected Operand result;
    protected String label;
    
    private static int counter = 0;
}



class Assignment extends Quadruple {
    public Assignment(Operand result, Operation op, Operand arg1, Operand arg2) {
        super(op, arg1, arg2, result);
    }
}

class GoTo extends Quadruple {
    public GoTo(Integer offset) {
        super(Operation.GOTO, 
                Operand.getConstantOperand(Type.getPrimitiveType(Type.Typename.INT), offset), 
                null, 
                null);
    }
}
class If extends Quadruple {
    public If(Operand variable, Integer offset) {
        super(Operation.IF_GOTO,
                variable,
                Operand.getConstantOperand(Type.getPrimitiveType(Type.Typename.INT), offset),
                null);
    }
}
class Call extends Quadruple {
    public Call(Operand function, int argsNumber, Operand result) {
        super(Operation.CALL,
                function,
                Operand.getConstantOperand(Type.getPrimitiveType(Type.Typename.INT), argsNumber),
                result);
    }
        
    public Call(Operand function, int argsNumber) {
        super(Operation.CALL,
                function,
                Operand.getConstantOperand(Type.getPrimitiveType(Type.Typename.INT), argsNumber),
                null);
    }
}
class Ref extends Quadruple {
    public Ref(Operand variable, Operand result) {
        super(Operation.REF, variable, null, result);
    }
}
class Deref extends Quadruple {
    public Deref(Operand variable, Operand result) {
        super(Operation.DEREF, variable, null, result);
    }
}
class ArrayIndex extends Quadruple {
    public ArrayIndex(Operand array, Operand index, Operand result) {
        super(Operation.INDEX, array, index, result);
    }
}
//class AddressAssignment extends Quadruple {
//
//}