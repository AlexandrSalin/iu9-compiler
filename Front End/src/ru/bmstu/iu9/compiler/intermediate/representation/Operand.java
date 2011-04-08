package ru.bmstu.iu9.compiler.intermediate.representation;

import ru.bmstu.iu9.compiler.*;

abstract class Operand implements Cloneable {    
    public abstract Type type();
}

class VariableOperand extends Operand {
    public VariableOperand(String name, Type type, VariablesTable table) {
        this.table = table;
        this.number = this.table.add(new NamedVariable(name, type));
    }
    private VariableOperand(VariablesTable table, long number) {
        this.table = table;
        this.number = number;
    }

    public String name() { return ((NamedVariable)this.table.get(number)).name(); }
    @Override
    public Type type() { return this.table.get(number).type(); }

    @Override
    public Object clone() {
        return new VariableOperand(this.table, this.number);
    }

    private VariablesTable table;
    private long number;
}

class ConstantOperand extends Operand {
    public ConstantOperand(Type type, Object value) {
        this.type = type;
        this.value = value;
    }
    
    public Object value() { return this.value; }
    @Override
    public Type type() { return this.type; }
    
    @Override
    public Object clone() {
        return new ConstantOperand(this.type, this.value);
    }
    
    private Object value;
    protected Type type;
}