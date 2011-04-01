package ru.bmstu.iu9.compiler.intermediate.representation;

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
    public Type add(String name, Type type) {
        return symbols.put(name, type);
    }
    public Type get(String name) {
        if (symbols.containsKey(name)) {
            return symbols.get(name);
        } else {
            SymbolTable openScope = this.openScope;
            do {
                Type tmp = openScope.get(name);
                if (tmp != null)
                    return tmp;
                openScope = openScope.openScope();
            } while (openScope != null);
            return null;
        }
    }
    public Map<String, Type> Symbols() { return this.symbols; }
    public SymbolTable openScope() { return this.openScope; }
    
    @Override
    public boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass()) &&
                this.symbols.equals(((SymbolTable)obj).symbols) &&
                this.openScope.equals(((SymbolTable)obj).openScope);
    }
    
    private Map<String, Type> symbols = new HashMap<String, Type>();
    private SymbolTable openScope = null;
}
