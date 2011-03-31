package ru.bmstu.iu9.compiler.intermediate.representation;

abstract class Operand implements Cloneable {
    protected Operand(Type type) {
        this.type = type;
    }
    
    public Type type() { return this.type; }
    
    protected Type type;
}

class Variable extends Operand {
    public Variable(String name, Type type) {
        super(type);
        this.name = name;
    }
    
    public String name() { return this.name; }
    
    @Override
    public Object clone() {
        return new Variable(this.name, this.type);
    }
    
    private String name;
}

class Constant extends Operand {
    public Constant(Type type, Object value) {
        super(type);
        this.value = value;
    }
    
    public Object value() { return this.value; }
    
    @Override
    public Object clone() {
        return new Constant(this.type, this.value);
    }
    
    private Object value;
}