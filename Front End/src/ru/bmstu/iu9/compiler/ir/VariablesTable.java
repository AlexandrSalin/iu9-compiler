package ru.bmstu.iu9.compiler.ir;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import ru.bmstu.iu9.compiler.*;


/**
 *
 * @author maggot
 */
class VariablesTable {
    public VariablesTable() {
        vars = new HashMap<Long, Variable>();
    }
    
    public Long get(String name) {
        for(Entry<Long, Variable> var : vars.entrySet()) {
            if (var.getValue() instanceof NamedVariable && 
                ((NamedVariable)var.getValue()).name.equals(name))
                return var.getKey();
        }
        return null;
    }
    public Variable get(long number) {
        return vars.get(number);
    }
    public long add(Variable variable) {
        long number = numerator.next();
        vars.put(number, variable);
        
        return number;
    }
    
    private Iterator<Long> numerator = 
            new Iterator<Long>() {
                @Override
                public boolean hasNext() {
                    return true;
                }

                @Override
                public Long next() {
                    return ++counter;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
                
                private long counter = 0;
            };
    private Map<Long, Variable> vars;
}

abstract class Variable {
    protected Variable(BaseType type) {
        this.type = type;
    }

    public final BaseType type;
}

class NamedVariable extends Variable {
    public NamedVariable(String name, BaseType type) {
        super(type);
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.type + " " + this.name;
    }

    public final String name;
}

class TmpVariable extends Variable {
    public TmpVariable(BaseType type) {
        super(type);
    }
    
    @Override
    public String toString() {
        return this.type + " tmp";
    }
}