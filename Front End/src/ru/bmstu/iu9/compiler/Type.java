package ru.bmstu.iu9.compiler;
/**
 *
 * @author maggot
 */
public abstract class Type {
    public enum Typename { INT, BOOL, FLOAT, DOUBLE, CHAR, VOID, 
        ARRAY, STRUCT, FUNCTION, POINTER };
    
    protected Type(Typename typename) {
        
        this.typename = typename;
    }
    
    public Typename Typename() { return this.typename; }
    
    @Override
    public boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass()) && 
                this.typename.equals(((Type)obj).typename);
    }
    @Override
    public String toString() {
        return typename.name();
    }
    
    protected final Typename typename;
}
