package ru.bmstu.iu9.compiler;
/**
 *
 * @author maggot
 */
public abstract class Type {
    public enum Typename { 
        INT, BOOL, FLOAT, DOUBLE, CHAR, VOID, 
        ARRAY, STRUCT, FUNCTION, POINTER,
        PrimitiveType(new Typename[] {
            INT, VOID, DOUBLE, FLOAT, CHAR
        });
        
        private Typename() {
            this.value = 1 << this.ordinal();
        }
        private Typename(Typename[] typenames) {
            for (int i = 0; i < typenames.length; ++i)
                this.value = this.value | typenames[i].value;
        }
        
        public boolean is(Typename typename) {
            return (this.value & typename.value) != 0;
        }
        
        private int value = 0;
    };
    
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
