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
        public Argument(String name, Type type, Position position) {
            this.name = name;
            this.position = position;
            this.type = type;
        }
        public Argument(Argument arg) {
            this(arg.name, arg.type, arg.position);
        }
        private Argument() {
            this(null, null, null);
        }

        public Position position() { return this.position; }
        public String name() { return this.name; }
        public Type type() { return this.type; }

        private final String name;
        private final Type type;
        private final Position position;
    }
    
    public FunctionType(Type returnValueType, List<Argument> arguments) {
        super(Typename.FUNCTION, 0);
        this.result = returnValueType;
        this.arguments.addAll(arguments);
    }
    public FunctionType(Type returnValueType, List<Argument> arguments,
            boolean constancy) {
        super(Typename.FUNCTION, constancy, 0);
        this.result = returnValueType;
        this.arguments.addAll(arguments);
    }
    
    public Type returnType() { return this.result; }
    public void setReturnType(Type type) {
        this.result = type;
    }
    public Argument[] arguments() { return this.arguments.toArray(new Argument[0]); }
    
    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj) &&
                this.result.equals(((FunctionType)obj).result) && 
                this.arguments.size() == ((FunctionType)obj).arguments.size()) {
            for (int i = 0; i < this.arguments.size(); ++i) {
                if (!(this.arguments.get(i).type().equals(((FunctionType)obj).arguments.get(i).type())))
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
        
        for(int i = 0; i < arguments.size(); ++i) {
            str.append(arguments.get(i).toString());
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
                return arguments.listIterator();
            }
        };
    }
    
    @SerializedName("type")
    private Type result;
    private List<Argument> arguments = new LinkedList<Argument>();
}
