package ru.bmstu.iu9.compiler;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author maggot
 */
public final class FunctionType extends BaseType {
    public static class Argument {
        public Argument(String name, BaseType type) {
            this.name = name;
            this.type = type;
        }
        public Argument(Argument arg) {
            this(arg.name, arg.type);
        }
        private Argument() {
            this(null, null);
        }
        
        @Override
        public String toString() {
            return type + " " + name;
        }

        public final String name;
        public final BaseType type;
    }
    
    public FunctionType(BaseType returnValueType, List<Argument> arguments) {
        super(Type.FUNCTION, 0);
        this.returnValue = returnValueType;
        this.arguments.addAll(arguments);
    }
    public FunctionType(
            BaseType returnValueType,
            List<Argument> arguments,
            boolean constancy) {
        
        super(Type.FUNCTION, constancy, 0);
        this.returnValue = returnValueType;
        this.arguments.addAll(arguments);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) &&
                this.returnValue.equals(((FunctionType)obj).returnValue) && 
                this.arguments.size() == ((FunctionType)obj).arguments.size()) {
            for (int i = 0; i < this.arguments.size(); ++i) {
                if (!(this.arguments.get(i).type.equals(
                        ((FunctionType)obj).arguments.get(i).type)))
                    return false;
            }
        }
        return true;
    }
    @Override
    public String toString() {
        return super.toString() + " (" + arguments + ") " + returnValue;
    }
    
    public List<Argument> arguments() {
        return this.arguments;
    }
    
    public final BaseType returnValue;
    public final List<Argument> arguments = new LinkedList<Argument>();
}
