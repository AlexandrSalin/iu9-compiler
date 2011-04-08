package ru.bmstu.iu9.compiler;

import com.google.gson.annotations.SerializedName;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author maggot
 */
public final class FunctionType extends Type {
    public static class Argument {
        public static Argument[] getArguments(Type type, String[] names) {
            List<Argument> args = new LinkedList<Argument>();
            for (int i = 0; i < names.length; ++i)
                args.add(new Argument(type, names[i]));
            
            return args.toArray(new Argument[0]);
        }
        public Argument(Type type, String name) {
            this.type = type;
            this.name = name;
        }
        private Argument() {
            this.name = null;
            this.type = null;
        }
        
        public String name() { return this.name; }
        public Type type() { return this.type; }
        
        @Override
        public String toString() {
            return type.toString() + " " + name;
        }

        public void setType(Type type) {
            this.type = type;
        }
        
        private final String name;
        private Type type;
    }
    
    public FunctionType(Type returnValueType, FunctionType.Argument[] arguments) {
        super(Typename.FUNCTION, 0);
        this.result = returnValueType;
        this.arguments = arguments;
    }
    
    public Type returnType() { return this.result; }
    public void setReturnType(Type type) {
        this.result = type;
    }
    public Argument[] arguments() { return this.arguments; }
//    public void setArgument(int index, Type type) {
//        this.arguments[index].type = type;
//    }
    
    @Override
    public long getAlignedAddress(long rawAddress) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) &&
                this.result.equals(((FunctionType)obj).result) && 
                this.arguments.length == ((FunctionType)obj).arguments.length) {
            for (int i = 0; i < this.arguments.length; ++i) {
                if (!(this.arguments[i].type.equals(((FunctionType)obj).arguments[i].type)))
                    return false;
            }
        }
        return true;
    }
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(super.toString());
        str.append(" ");
        str.append(this.result.toString());
        str.append("(");
        
        for(int i = 0; i < arguments.length; ++i) {
            str.append(arguments[i].toString());
            str.append(", ");
        }
        str.delete(str.length() - 2, str.length());
        str.append(")");
        
        return str.toString();
    }
    
    public Iterable<Argument> argumentsIterator() {
        return new Iterable<Argument>() {

            @Override
            public Iterator<Argument> iterator() {
                return new Iterator<Argument>() {

                    @Override
                    public boolean hasNext() {
                        return counter < arguments.length;
                    }

                    @Override
                    public Argument next() {
                        return arguments[counter++];
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("Not supported yet.");
                    }
                    
                    private int counter = 0;
                };
            }
            
        };
    }
    
    @SerializedName("type")
    private Type result;
    private Argument[] arguments;
}
