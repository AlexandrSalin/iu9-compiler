package ru.bmstu.iu9.compiler.ir.type;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author anton.bobukh
 */
public final class StructType extends BaseType {
    public static class Field {
        private Field(String name, BaseType type, long offset) {
            this.name = name;
            this.type = type;
            this.offset = offset;
        }
        public Field(Field field) {
            this(field.name, field.type, field.offset);
        }

        @Override
        public String toString() {
            return type + " " + name;
        }

        public final String name;
        public final BaseType type;
        public final long offset;
    }

    public StructType(String name, boolean constancy) {
        super(Type.STRUCT, constancy, 0);
        this.name = name;
    }
    public StructType(String name, List<Field> fields, boolean constancy) {
        super(Type.STRUCT, constancy, 0);
        this.name = name;
        this.fields.addAll(fields);
    }

    public void addField(String name, BaseType type) {
        this.fields.add(new Field(name, type, currentOffset));
        this.size += type.size();
        this.currentOffset += type.size();
    }

    public long getFieldOffset(String name) {
        long offset = 0;
        for(Field field : fields) {
            if (field.name.equals(name)) {
                return offset;
            } else {
                offset += field.offset;
            }
        }

        return -1;
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
    private long currentOffset = 0;
}
