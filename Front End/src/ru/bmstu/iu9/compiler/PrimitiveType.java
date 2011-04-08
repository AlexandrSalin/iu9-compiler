package ru.bmstu.iu9.compiler;

/**
 *
 * @author maggot
 */
public class PrimitiveType extends Type {
    public enum Typename { 
        INT(4), BOOL(1), FLOAT(4), DOUBLE(8), CHAR(1), VOID(0), LONG(8),
        POINTER(8);
        
        private Typename(long size) {
            this.size = size;
            this.value = 1 << this.ordinal();
        }
        
        public boolean is(Typename[] typenames) {
            for (int i = 0; i < typenames.length; ++i) {
                if (this.is(typenames[i]))
                    return true;
            }
            return false;
        }
        public boolean is(Typename typename) {
            return (this.value & typename.value) != 0;
        }
        
        public long size;
        private int value;
    };
    
    public PrimitiveType(Typename primitive, boolean constancy) {
        super(Type.Typename.PRIMITIVE_TYPE, constancy, primitive.size);
        this.primitive = primitive.ordinal();
    }
    public PrimitiveType(Typename primitive) {
        super(Type.Typename.PRIMITIVE_TYPE, primitive.size);
        this.primitive = primitive.ordinal();
    }
    
    public Typename primitive() { return Typename.values()[this.primitive]; }
    
    @Override
    public long getAlignedAddress(long rawAddress) {
        return rawAddress + (this.size - rawAddress % this.size);
    }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && 
               this.typename == ((PrimitiveType)obj).typename;
    }
    @Override
    public String toString() {
        return super.toString();
    }
    
    private final int primitive;
}
