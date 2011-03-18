package ru.bmstu.iu9.compiler.intermediate.representation;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * @author maggot
 */
class Type {
    @Override
    public boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass());
    }
    public int width() { return this.width; }
    
    protected int width;
}

class PrimitiveType extends Type {
    public enum Typename { INT, BOOL, FLOAT, DOUBLE, LONG };
    
    public PrimitiveType(Typename type) {
        switch(type) {
            case INT:
                this.width = 4;
                break;
            case BOOL:
                this.width = 1;
                break;
            case FLOAT:
                this.width = 4;
                break;
            case DOUBLE:
                this.width = 8;
                break;
            case LONG:
                this.width = 8;
                break;
        }
        this.type = type;
    }
    
    private Typename type;
}

class ArrayType extends Type {
    public ArrayType(Type element, int length) {
        this.element = element;
        this.length = length;
        this.width = length * element.width();
    }
    
    public int length() { return this.length; }
    public Type element() { return element; }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && ((ArrayType)obj).length() == this.length &&
            element.equals(((ArrayType)obj).element());
    }
    
    private int length;
    private Type element;
}

class RecordType extends Type {

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && 
            this.variables.equals(((RecordType)obj).variables());
    }
    public Map<String, Type> variables() { return this.variables; }
    
    private Map<String, Type> variables = new HashMap<String, Type>();
}

class FunctionType extends Type {
    
    private Type returnValue;
    private List<Type> arguments = new LinkedList<Type>();
}