package ru.bmstu.iu9.compiler;

/**
 *
 * @author maggot
 */
public final class StructType extends BaseType {
    public StructType(String name, boolean constancy, long size) {
        super(Type.STRUCT, constancy, size);
        this.name = name;
    }
    public StructType(String name, boolean constancy) {
        super(Type.STRUCT, constancy, 0);
        this.name = name;
    }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) &&
                this.name.equals(((StructType)obj).name);
    }
    @Override
    public String toString() {        
        return super.toString() + " " + name;
    }
    
    public final String name;
}
