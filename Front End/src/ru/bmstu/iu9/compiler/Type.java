package ru.bmstu.iu9.compiler;
/**
 *
 * @author maggot
 */
public abstract class Type {
    public enum Typename { 
        ARRAY, STRUCT, FUNCTION, POINTER, PRIMITIVE_TYPE, INVALID;
        
        private Typename() {
            this.value = 1 << this.ordinal();
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
    
    protected Type(Typename typename, long size) {
        this(typename, false, size);
    }
    protected Type(Typename typename, boolean constancy, long size) {
        this.typename = typename.ordinal();
        this.constancy = constancy;
        this.size = size;
    }
    
    public Typename typename() { return Typename.values()[this.typename]; }
    public boolean isConstant() { return this.constancy; }
    public void setConstancy(boolean constancy) { this.constancy = constancy; }
    public long size() { return this.size; }
    
    @Override
    public boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass()) && 
                (this.typename == (((Type)obj).typename));
    }
    @Override
    public String toString() {
        return ((constancy) ? "CONST " : "") + 
                Typename.values()[this.typename].toString();
    }
    
    protected final int typename;
    protected boolean constancy;
    
    protected final long size;
    public abstract long getAlignedAddress(long rawAddress);
}
