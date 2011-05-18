package ru.bmstu.iu9.compiler.semantics;

import java.util.Iterator;
import ru.bmstu.iu9.compiler.ir.type.BaseType;

import java.util.Map;
import java.util.LinkedHashMap;
/**
 *
 * @author maggot
 */
class SymbolTable implements Iterable<Symbol> {
    public SymbolTable() { 
        symbols = new LinkedHashMap<String, Symbol>();
    }
    
    public void setOpenScope(SymbolTable openScope) {
        this.openScope = openScope;
    }
    public Symbol add(Symbol symbol) {
        return symbols.put(symbol.name(), symbol);
    }
    public Symbol get(String name) {
        if (symbols.containsKey(name)) {
            return symbols.get(name);
        } else if (this.openScope != null) {
            return openScope.get(name);
        } else {
            return null;
        }
    }
    public boolean contains(String name) {
        return this.get(name) != null;
    }
    
    public Map<String, Symbol> symbols() { return this.symbols; }
    public SymbolTable openScope() { return this.openScope; }
    public Symbol associatedSymbol() { return this.associatedSymbol; }
    public void setAssociatedSymbol(Symbol associatedSymbol) { 
        this.associatedSymbol = associatedSymbol; 
    }

    public Iterator<Symbol> iterator() {
        return symbols.values().iterator();
    }

    private Map<String, Symbol> symbols;
    private SymbolTable openScope = null;
    private Symbol associatedSymbol = null;
}


abstract class Symbol {
    protected Symbol(String name, BaseType type) {
        this.type = type;
        this.name = name;
    }
    protected Symbol(String name) {
        this.name = name;
    }
    
    public String name() { return this.name; }
    public BaseType type() { return this.type; }
    public void setType(BaseType type) { this.type = type; }
    
    @Override
    public String toString() {
        return type + " " + name;
    }
    
    protected BaseType type;
    protected final String name;
}

abstract class SymbolWithScope extends Symbol {
    protected SymbolWithScope(
            String name, 
            BaseType type, 
//            SymbolTable ambientScope,
            SymbolTable scope) {
        
        super(name, type);
        this.scope = scope;
//        this.scope.setOpenScope(ambientScope);
    }
    protected SymbolWithScope(
            String name,
//            SymbolTable ambientScope,
            SymbolTable scope) {
        
        super(name);
        this.scope = scope;
 //       this.scope.setOpenScope(ambientScope);
    }
    
//    public SymbolTable scope() { return this.scope; }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
    
    public final SymbolTable scope;
}

final class VariableSymbol extends Symbol {
    public VariableSymbol(String name, BaseType type) {
        super(name, type);
    }
}

final class FunctionSymbol extends SymbolWithScope {
    public FunctionSymbol(String name, BaseType type/*, SymbolTable ambientScope*/) {
        super(name, type, /*ambientScope,*/ new SymbolTable());
    }
    public FunctionSymbol(
            String name, 
            BaseType type, 
//            SymbolTable ambientScope,
            SymbolTable scope) {
        
        super(name, type, /*ambientScope,*/ scope);
    }
    public FunctionSymbol(
            String name,
//            SymbolTable ambientScope,
            SymbolTable scope) {
        
        super(name, /*ambientScope,*/ scope);
    }
    
    @Override
    public String toString() {
        return type == null ? "" : type.toString();
    }
}

final class StructSymbol extends SymbolWithScope {
    public StructSymbol(String name, BaseType type/*, SymbolTable ambientScope*/) {
        super(name, type, /*ambientScope,*/ new SymbolTable());
    }
    public StructSymbol(
            String name, 
//            SymbolTable ambientScope,
            SymbolTable scope) {
        
        super(name, /*ambientScope,*/ scope);
    }
    public StructSymbol(
            String name, 
            BaseType type, 
//            SymbolTable ambientScope,
            SymbolTable scope) {
        
        super(name, type, /*ambientScope,*/ scope);
    }
    
    @Override
    public String toString() {
        return type == null ? "" : type.toString();
    }
}