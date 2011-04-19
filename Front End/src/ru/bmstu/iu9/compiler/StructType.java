package ru.bmstu.iu9.compiler;

/**
 *
 * @author maggot
 */
public final class StructType extends Type {
    public StructType(String name, boolean constancy, long size) {
        super(Typename.STRUCT, constancy, size);
        this.name = name;
    }
    public StructType(String name, boolean constancy) {
        super(Typename.STRUCT, constancy, 0);
        this.name = name;
    }
    
    public String name() { return this.name; }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) &&
                this.name.equals(((StructType)obj).name());
    }
    @Override
    public String toString() {        
        return super.toString() + " " + name;
    }
    
    private final String name;
}
