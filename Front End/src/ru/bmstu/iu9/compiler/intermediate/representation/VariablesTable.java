package ru.bmstu.iu9.compiler.intermediate.representation;

import ru.bmstu.iu9.compiler.Type;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author maggot
 */
class VariablesTable {
    public void add(String name, Type type) {
        Variable var = new Variable(name, type);
        symbols.put(var.number(), var);
    }
    public Operand get(int number) {
        return symbols.get(number);
    }
    
    private Map<Integer, Operand> symbols = new HashMap<Integer, Operand>();
}

/*
class Nonterminal {
    public Quadruple code() { return this.code; }
    public void setCode(Quadruple value) { code = value; }
    public Operand address() { return this.address; }
    public void setAddress(Operand value) { address = value; }
    
    protected Quadruple code;
    protected Operand address;
    protected Type type;
}

class ArrayNonterminal extends Nonterminal {
    public ArrayNonterminal() {
        
    }
    
    protected Operand array;
}
*/