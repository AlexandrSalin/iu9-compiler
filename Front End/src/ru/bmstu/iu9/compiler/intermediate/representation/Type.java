/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.intermediate.representation;

import java.util.List;
import java.util.LinkedList;

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
    public enum type { INT, BOOL, FLOAT, DOUBLE, LONG };
}

class ArrayType extends Type {
    public ArrayType() {

    }
    public int Length() { return this.length; }
    public Type Type() { return element; }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && ((ArrayType)obj).Length() == this.length &&
            element.equals(((ArrayType)obj).element);
    }
    
    private int length;
    private Type element;
}

class RecordType extends Type {
    
    private SymbolTable symbolTable;
}

class FunctionType extends Type {
    
    private Type returnValue;
    private List<Type> arguments = new LinkedList<Type>();
}