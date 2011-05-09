package ru.bmstu.iu9.compiler.ir;

import ru.bmstu.iu9.compiler.*;

abstract class Operand implements Cloneable {    
    public abstract BaseType type();
}


abstract class VariableOperand extends Operand {
    protected VariableOperand(VariablesTable table, long number) {
        this.table = table;
        this.number = number;
    }

    @Override
    public BaseType type() { 
        return this.table.get(number).type; 
    }

    protected final VariablesTable table;
    protected final long number;
}

class NamedVariableOperand extends VariableOperand {
    public NamedVariableOperand(
            String name, 
            BaseType type, 
            VariablesTable table) {
        
        super(table, table.add(new NamedVariable(name, type)));
    }
    public NamedVariableOperand(VariablesTable table, long number) {
        super(table, number);
    }

    public String name() { 
        return ((NamedVariable)this.table.get(number)).name;
    }

    @Override
    public Object clone() {
        return new NamedVariableOperand(this.table, this.number);
    }
    
    @Override
    public String toString() {
        return name();
    }
}

class TmpVariableOperand extends VariableOperand {
    public TmpVariableOperand(BaseType type, VariablesTable table) {
        super(table, table.add(new TmpVariable(type)));
    }
    private TmpVariableOperand(VariablesTable table, long number) {
        super(table, number);
    }

    @Override
    public Object clone() {
        return new TmpVariableOperand(this.table, this.number);
    }
    
    @Override
    public String toString() {
        return "tmp" + number;
    }
}



class ConstantOperand extends Operand {
    public ConstantOperand(BaseType type, Object value) {
        this.type = type;
        this.value = value;
    }
    
    public Object value() {
        return this.value;
    }
    @Override
    public BaseType type() {
        return this.type;
    }
    
    @Override
    public Object clone() {
        return new ConstantOperand(this.type, this.value);
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
    
    private Object value;
    protected BaseType type;
}