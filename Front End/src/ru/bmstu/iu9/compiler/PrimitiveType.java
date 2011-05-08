package ru.bmstu.iu9.compiler;

/**
 *
 * @author maggot
 */
public class PrimitiveType extends BaseType {
    public enum Type { 
        INT(4), BOOL(1), FLOAT(4), DOUBLE(8), CHAR(1), VOID(0), LONG(8),
        POINTER(8);
        
        private Type(long size) {
            this.size = size;
            this.value = 1 << this.ordinal();
        }
        
        public boolean is(Type[] typenames) {
            for (int i = 0; i < typenames.length; ++i) {
                if (this.is(typenames[i]))
                    return true;
            }
            return false;
        }
        public boolean is(Type typename) {
            return (this.value & typename.value) != 0;
        }
        
        public long size;
        private int value;
    };
    
    public PrimitiveType(Type primitive, boolean constancy) {
        super(BaseType.Type.PRIMITIVE_TYPE, constancy, primitive.size);
        this.primitive = primitive.ordinal();
    }
    public PrimitiveType(Type primitive) {
        super(BaseType.Type.PRIMITIVE_TYPE, primitive.size);
        this.primitive = primitive.ordinal();
    }
    
    public Type primitive() { return Type.values()[this.primitive]; }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && 
               this.primitive == ((PrimitiveType)obj).primitive;
    }
    @Override
    public String toString() {
        return ((constancy) ? "CONST " : "") + this.type().toString();
    }
    
    private final int primitive;
}
