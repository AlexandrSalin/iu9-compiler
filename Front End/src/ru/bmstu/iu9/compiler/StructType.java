package ru.bmstu.iu9.compiler;

/**
 *
 * @author maggot
 */
public final class StructType extends Type {
    public StructType(String name) {
        super(Typename.STRUCT);
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
    
    private String name;
}
