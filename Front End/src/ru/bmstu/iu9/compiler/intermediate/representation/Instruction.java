/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.intermediate.representation;

/**
 *
 * @author maggot
 */
public class Instruction {

}
class Quadruple {
    public enum Operation { MUL, UNARY_MINUS, MINUS, UNARY_PLUS, PLUS };
    
    public Quadruple(Operation op, Address arg1, Address arg2, Address result) {
        this.operation = op;
        this.argument1 = arg1;
        this.argument2 = arg2;
        this.result = result;
    }
    public Operation operation() { return this.operation; }
    public Address argument1() { return this.argument1; }
    public Address argument2() { return this.argument2; }
    public Address result() { return this.result; }
    
    private Operation operation;
    private Address argument1;
    private Address argument2;
    private Address result;
}



class Command {
    
}
class BinaryAssignment extends Command {
    
}
class UnaryAssignment extends Command {
    
}
class Copy extends Command {
    
}
class GoTo extends Command {
    
}
class UnaryIf extends Command {
    
}
class BinaryIf extends Command {
    
}
class Call extends Command {
    
}
class ArrayIndex extends Command {
    
}
class AddressAssignment extends Command {
    
}