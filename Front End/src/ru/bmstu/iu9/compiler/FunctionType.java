package ru.bmstu.iu9.compiler;

/**
 *
 * @author maggot
 */
public final class FunctionType extends Type {
    public FunctionType(Type returnValueType, Type[] argumentsTypes) {
        super(Typename.FUNCTION);
        this.type = returnValueType;
        this.arguments = argumentsTypes;
    }
    
    public void setReturnValueType(Type type) { this.type = type; }
    public void setArgumentsTypes(Type[] types) { this.arguments = types; }
    public Type returnValueType() { return this.type; }
    public Type[] argumentsTypes() { return this.arguments; }
    
    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) &&
                this.type.equals(((FunctionType)obj).type) && 
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
        result.append(type.toString());
        result.append("(");
        
        for(int i = 0; i < arguments.length; ++i) {
            result.append(arguments[i].toString());
        }
        
        result.append(")");
        
        return result.toString();
    }
    
    private Type type;
    private Type[] arguments;
}
