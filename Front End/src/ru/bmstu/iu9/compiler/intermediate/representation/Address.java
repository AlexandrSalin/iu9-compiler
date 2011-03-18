package ru.bmstu.iu9.compiler.intermediate.representation;


class Address implements Cloneable {
    protected Address(Type type, Object value) {
        this.type = type;
    }
    public Type type() { return this.type; }
    public Object value() { return this.value; }
    
    protected Type type;
    protected Object value;
}

class Name extends Address {
    public Name(String name, Type type) {
        super(type, null);
        this.name = name;
    }
    protected Name(String name, Type type, Object value) {
        super(type, value);
        this.name = name;
    }
    
    public void setValue(Object value) { this.value = value; }
    public String name() { return this.name; }
    
    @Override
    public Object clone() {
        return new Name(this.name, this.type, value);
    }
    
    private String name;
}

class Constant extends Address {
    public Constant(Type type, Object value) {
        super(type, value);
    }
    
    @Override
    public Object clone() {
        return new Constant(this.type, this.value);
    }
}

class Temp extends Address {
    public Temp(Type type) {
        super(type, null);
        name = "tmp" + counter;
        ++counter;
    }
    protected Temp(Type type, String name, Object value) {
        super(type, value);
        this.name = name;
    }
    public void setValue(Object value) { this.value = value; }
    public String name() { return this.name; }
    
    @Override
    public Object clone() {
        return new Temp(this.type, this.name, this.value);
    }
    
    private String name;
    private static int counter = 0;
}