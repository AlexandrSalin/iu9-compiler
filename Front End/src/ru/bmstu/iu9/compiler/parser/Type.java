package ru.bmstu.iu9.compiler.parser;

import java.util.List;
import java.util.LinkedList;

/**
 *
 * @author maggot
 */
final class Type {
    public enum Typename { INT, BOOL, FLOAT, DOUBLE, LONG, CHAR, 
        ARRAY, RECORD, FUNC };
    
    private Type() { }
    public static Type getPrimitiveType(Typename type) {
        Type primitiveType = new Type();
        
        switch(type) {
            case CHAR:
                primitiveType.width = 2;
                break;
            case INT:
                primitiveType.width = 4;
                break;
            case BOOL:
                primitiveType.width = 1;
                break;
            case FLOAT:
                primitiveType.width = 4;
                break;
            case DOUBLE:
                primitiveType.width = 8;
                break;
            case LONG:
                primitiveType.width = 8;
                break;
        }
        primitiveType.type = type;
        
        return primitiveType;
    }
    public static Type getArrayType(Type elementType, Integer length) {
        Type arrayType = new Type();
        
        arrayType.elementType = elementType;
        arrayType.length = length;
        arrayType.width = length * elementType.width();
        arrayType.type = Typename.ARRAY;
        
        return arrayType;
    }
    public static Type getRecordType(SymbolTable parentSymbolTable) {
        Type recordType = new Type();
        
        recordType.symbolsTypes = new SymbolTable();
        recordType.symbolsTypes.setOpenScope(parentSymbolTable);
        recordType.type = Typename.RECORD;
        
        return recordType;
    }
    public static Type getFuncType(Type returnValueType, List<Type> argumentsTypes) {
        Type funcType = new Type();
        
        funcType.returnValue = returnValueType;
        funcType.arguments = argumentsTypes;
        funcType.arguments = new LinkedList<Type>();
        funcType.type = Typename.FUNC;
        
        return funcType;
    }
    
    @Override
    public boolean equals(Object obj) {
        switch(type) {
            case INT:
            case BOOL:
            case FLOAT:
            case DOUBLE:
            case LONG:
            case CHAR:
                return this.getClass().equals(obj.getClass()) &&
                        this.type == ((Type)obj).type;
            case ARRAY:
                return this.getClass().equals(obj.getClass()) && 
                        this.type == ((Type)obj).type &&
                        ((Type)obj).length() == this.length &&
                        elementType.equals(((Type)obj).elementType());
            case RECORD:
                return this.getClass().equals(obj.getClass()) && 
                        this.type == ((Type)obj).type &&
                        this.symbolsTypes.equals(((Type)obj).symbolsTypes());
            case FUNC:
                return this.getClass().equals(obj.getClass()) && 
                        this.type == ((Type)obj).type &&
                        this.returnValue.equals(((Type)obj).returnValue);
            default:
                return false;                
        }
    }
    public Typename Typename() { return type; }
    public Integer width() { return this.width; }
    public Integer length() { return this.length; }
    public Type elementType() { return this.elementType; }
    public SymbolTable symbolsTypes() { return this.symbolsTypes; }
    public Type returnValueType() { return this.returnValue; }
    public List<Type> argumentsTypes() { return this.arguments; }
    public SymbolTable symbolTable() { return this.symbolTable; }
    
    // All types
    private Integer width = null;
    private Typename type = null;
    // Array Type
    private Integer length = null;
    private Type elementType = null;
    // Record type
    private SymbolTable symbolsTypes = null;
    // Func type
    private Type returnValue = null;
    private List<Type> arguments = null;
    private SymbolTable symbolTable = null;
}