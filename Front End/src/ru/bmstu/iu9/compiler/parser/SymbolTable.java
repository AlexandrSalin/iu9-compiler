package ru.bmstu.iu9.compiler.parser;

import ru.bmstu.iu9.compiler.Type;
import java.util.Map;
import java.util.HashMap;
/**
 *
 * @author maggot
 */
class SymbolTable {
    public SymbolTable() { }
    
    public void setOpenScope(SymbolTable openScope) {
        this.openScope = openScope;
    }
    public Symbol add(Symbol symbol) {
        return symbols.put(symbol.name(), symbol);
    }
    public Symbol get(String name) {
        if (symbols.containsKey(name)) {
            return symbols.get(name);
        } else {
            SymbolTable openScope = this.openScope;
            do {
                Symbol tmp = openScope.get(name);
                if (tmp != null)
                    return tmp;
                openScope = openScope.openScope();
            } while (openScope != null);
            return null;
        }
    }
    public boolean contains(String name) {
        return this.get(name) != null;
    }
    
    public Map<String, Symbol> symbols() { return this.symbols; }
    public SymbolTable openScope() { return this.openScope; }
    public Symbol associatedSymbol() { return this.associatedSymbol; }
    public void setAssociatedSymbol(Symbol associatedSymbol) { this.associatedSymbol = associatedSymbol; }

    private Map<String, Symbol> symbols = new HashMap<String, Symbol>();
    private SymbolTable openScope = null;
    private Symbol associatedSymbol = null;
}


abstract class Symbol {
    protected Symbol(String name, Type type) {
        this.type = type;
        this.name = name;
    }
    
    public String name() { return this.name; }
    public Type type() { return this.type; }
    
    protected final Type type;
    protected final String name;
}

abstract class SymbolWithScope extends Symbol {
    protected SymbolWithScope(String name, Type type, SymbolTable ambientScope,
            SymbolTable scope) {
        super(name, type);
        this.scope = scope;
        this.scope.setOpenScope(ambientScope);
    }
    
    public SymbolTable scope() { return this.scope; }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
    
    protected final SymbolTable scope;
}

final class VariableSymbol extends Symbol {
    public VariableSymbol(String name, Type type) {
        super(name, type);
    }
}

final class FunctionSymbol extends SymbolWithScope {
    public FunctionSymbol(String name, Type type, SymbolTable ambientScope) {
        super(name, type, ambientScope, new SymbolTable());
    }
    public FunctionSymbol(String name, Type type, SymbolTable ambientScope,
            SymbolTable scope) {
        super(name, type, ambientScope, scope);
    }
}

final class StructSymbol extends SymbolWithScope {
    public StructSymbol(String name, Type type, SymbolTable ambientScope) {
        super(name, type, ambientScope, new SymbolTable());
    }
    public StructSymbol(String name, Type type, SymbolTable ambientScope,
            SymbolTable scope) {
        super(name, type, ambientScope, scope);
    }
}