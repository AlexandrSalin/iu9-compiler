package ru.bmstu.iu9.compiler.intermediate.representation;

final class Operand implements Cloneable {
    private Operand(Type type) {
        this.type = type;
    }
    private Operand(Type type, Integer number, Object value) {
        this.type = type;
        this.number = number;
        this.value = value;
    }
    
    public static Operand getVariableOperand(Type type) {
        Operand varOperand = new Operand(type);
        
        varOperand.number = newNumber();
        
        return varOperand;
    }
    public static Operand getConstantOperand(Type type, Object value) {
        Operand constOperand = new Operand(type);
        
        constOperand.value = value;
        
        return constOperand;
    }
    
    @Override
    public Object clone() {
        return new Operand(this.type, this.number, this.value);
    }
    
    public Type type() { return this.type; }
    public int number() { return this.number; }
    public Object value() { return this.value; }
    
    private Type type = null;
    private Integer number = null;
    private Object value = null;
    
    private static int newNumber() {
        return counter++;
    }
    private static int counter = 0;
}