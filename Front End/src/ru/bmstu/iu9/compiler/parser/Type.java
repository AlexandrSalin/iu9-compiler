package ru.bmstu.iu9.compiler.parser;

/**
 *
 * @author maggot
 */
abstract class Type {
    public enum Typename { INT, BOOL, FLOAT, DOUBLE, CHAR, VOID, 
        ARRAY, STRUCT, FUNCTION, POINTER, REFERENCE };
    
    protected Type(Typename typename) {
        
        this.typename = typename;
    }
    
    public Typename Typename() { return this.typename; }
    
    @Override
    public boolean equals(Object obj) {
        return this.getClass().equals(obj.getClass()) && 
                this.typename.equals(((Type)obj).typename);
    }
    @Override
    public String toString() {
        return typename.name();
    }
    
    protected final Typename typename;
}


class PrimitiveType extends Type {
    public PrimitiveType(Typename typename, boolean isConstant) {
        super(typename);
        this.isConstant = isConstant;
    }
    
    public boolean isConstant() { return this.isConstant; }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj)/* &&
                (this.isConstant == ((PrimitiveType)obj).isConstant)*/;
    }
    @Override
    public String toString() {
        return (isConstant) ? "CONST " : "" + super.toString();
    }
    
    protected final boolean isConstant;
}


final class PointerType extends PrimitiveType {
    public PointerType(Type type, boolean isConstant) {
        super(Typename.POINTER, isConstant);
        this.type = type;
    }
    public PointerType(boolean isConstant) {
        super(Typename.POINTER, isConstant);
    }
    
    public Type type() { return type; }
    public void setType(Type type) { this.type = type; } 
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) &&
                this.type.equals(((PointerType)obj).type);
    }
    @Override
    public String toString() {
        return super.toString() + " TO " + type.toString();
    }
    
    private Type type;
}


final class ArrayType extends Type {
    public ArrayType(Type elementType, int length) {
        super(Typename.ARRAY);
        this.elementType = elementType;
        this.length = length;
    }
    public ArrayType(Type elementType) {
        super(Typename.ARRAY);
        this.elementType = elementType;
        this.length = null;
    }
    
    public void setElementType(Type type) { this.elementType = type; }
    public void setLenght(int length) { this.length = length; }
    public Integer length() { return this.length; }
    public Type elementType() { return this.elementType; }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) &&
                ((ArrayType)obj).length() == this.length &&
                elementType.equals(((ArrayType)obj).elementType());
    }
    @Override
    public String toString() {
        return "(" + super.toString() + " OF " + elementType.toString() + ")[" + length.toString() + "]";
    }
    
    private Integer length;
    private Type elementType;
}

final class StructType extends Type {
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

final class FunctionType extends Type {
    public FunctionType(Type returnValueType, Type[] argumentsTypes) {
        super(Typename.FUNCTION);
        this.returnValue = returnValueType;
        this.arguments = argumentsTypes;
    }
    
    public void setReturnValueType(Type type) { this.returnValue = type; }
    public void setArgumentsTypes(Type[] types) { this.arguments = types; }
    public Type returnValueType() { return this.returnValue; }
    public Type[] argumentsTypes() { return this.arguments; }
    
    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) &&
                this.returnValue.equals(((FunctionType)obj).returnValue) && 
                this.arguments.length == ((FunctionType)obj).arguments.length) {
            for (int i = 0; i < this.arguments.length; ++i) {
                if (!this.arguments[i].equals(((FunctionType)obj).arguments[i]))
                    return false;
            }
        }
        return true;
    }
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(super.toString());
        result.append(returnValue.toString());
        result.append("(");
        
        for(int i = 0; i < arguments.length; ++i) {
            result.append(arguments[i].toString());
        }
        
        result.append(")");
        
        return result.toString();
    }
    
    private Type returnValue;
    private Type[] arguments;
}