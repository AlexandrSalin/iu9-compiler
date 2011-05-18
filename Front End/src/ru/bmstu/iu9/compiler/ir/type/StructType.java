package ru.bmstu.iu9.compiler.ir.type;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author maggot
 */
public final class StructType extends BaseType {
    public static class Field {
        public Field(String name, BaseType type) {
            this.name = name;
            this.type = type;
        }
        public Field(Field arg) {
            this(arg.name, arg.type);
        }

        @Override
        public String toString() {
            return type + " " + name;
        }

        public final String name;
        public final BaseType type;
    }

    /*
    public StructType(String name, boolean constancy, long size) {
        super(Type.STRUCT, constancy, size);
        this.name = name;
    }
    */
    public StructType(String name, boolean constancy) {
        super(Type.STRUCT, constancy, 0);
        this.name = name;
    }
    public StructType(String name, List<Field> fields, boolean constancy) {
        super(Type.STRUCT, constancy, 0);
        this.name = name;
        this.fields.addAll(fields);
    }
    
    /*public void setSize(long size) {
        this.size = size;
    }*/
    public void addField(Field field) {
        this.fields.add(field);
        this.size += field.type.size();
    }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) &&
                this.name.equals(((StructType)obj).name);
    }
    @Override
    public String toString() {        
        return "STRUCT " + name;
    }
    
    public final String name;
    public final List<Field> fields = new LinkedList<Field>();
}
