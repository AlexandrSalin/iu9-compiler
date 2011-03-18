package ru.bmstu.iu9.compiler.intermediate.representation;

import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author maggot
 */
class SymbolTable {
    public void push(String name, Type type) {
        symbols.put(name, new Name(name, type));
    }
    public Address get(String name) {
        return symbols.get(name);
    }
    
    private Map<String, Address> symbols = new HashMap<String, Address>();
}


class Nonterminal {
    public Quadruple code() { return this.code; }
    public void setCode(Quadruple value) { code = value; }
    public Address address() { return this.address; }
    public void setAddress(Address value) { address = value; }
    
    private Quadruple code;
    private Address address;
}