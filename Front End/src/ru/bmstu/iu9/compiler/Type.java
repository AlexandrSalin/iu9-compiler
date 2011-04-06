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
            INT, VOID, DOUBLE, FLOAT, CHAR, BOOL
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
        public boolean is(Typename[] typenames) {
            for (int i = 0; i < typenames.length; ++i) {
                if ((this.value & typenames[i].value) != 0)
                    return true;
            }
            return false;
        }
        
        private int value = 0;
    };
    
    protected Type(Typename typename) {
        
        this.typename = typename.ordinal();
    }
    
    public Typename Typename() { return Typename.values()[this.typename]; }
    
    @Override
    public boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass()) && 
                (this.typename == (((Type)obj).typename));
    }
    @Override
    public String toString() {
        return Typename.values()[this.typename].toString();
    }
    
    protected final int typename;
}
