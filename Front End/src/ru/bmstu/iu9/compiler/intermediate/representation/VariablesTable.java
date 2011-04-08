package ru.bmstu.iu9.compiler.intermediate.representation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import ru.bmstu.iu9.compiler.Type;


/**
 *
 * @author maggot
 */
class VariablesTable {
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
    private Map<Long, Variable> vars = new HashMap<Long, Variable>();
}

abstract class Variable {
    protected Variable(Type type) {
        this.type = type;
    }
    
    public Type type() { return this.type; }
    
    protected final Type type;
}

class NamedVariable extends Variable {
    public NamedVariable(String name, Type type) {
        super(type);
        this.name = name;
    }
    
    public String name() { return this.name; }
    
    private final String name;
}

class TmpVariable extends Variable {
    public TmpVariable(Type type) {
        super(type);
    }
}