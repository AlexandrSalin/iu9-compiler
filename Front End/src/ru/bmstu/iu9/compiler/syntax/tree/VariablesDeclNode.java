package ru.bmstu.iu9.compiler.syntax.tree;

import com.google.gson.InstanceCreator;
import com.google.gson.annotations.Expose;
import java.util.Iterator;
import ru.bmstu.iu9.compiler.Type;

/**
 *
 * @author maggot
 */
final public class VariablesDeclNode extends DeclNode implements Iterable<VariablesDeclNode.Variable> {
    public static class Variable {
        public Variable(String name, VariablesDeclNode parent) {
            this.name = name;
            this.parent = parent;
        }
        private Variable() { 
            this(null, null);
        }
        
        public Type type() {
            return parent.type;
        }
        public String name() { return this.name; }
        
        private final String name;
        @Exclude
        private final VariablesDeclNode parent;
    }
    
    public VariablesDeclNode(Variable[] variables, Type type) {
        super(NodeType.VARS_DECL, type);
        this.vars = new Variable[variables.length];

        for (int i = 0; i < variables.length; ++i)
            vars[i] = new Variable(variables[i].name, this);
    }
    public VariablesDeclNode(String[] names, Type type) {
        super(NodeType.VARS_DECL, type);
        vars = new Variable[names.length];
        
        for (int i = 0; i < names.length; ++i) {
            vars[i] = new Variable(names[i], this);
        }
    }
    public VariablesDeclNode(String name, Type type) {
        super(NodeType.VARS_DECL, type);
        vars = new Variable[] { new Variable(name, this) };
    }
    
    @Override
    public Iterator<Variable> iterator() {
        return new Iterator<Variable>() {

            @Override
            public boolean hasNext() {
                return counter < vars.length;
            }

            @Override
            public Variable next() {
                return vars[counter++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
            
            private int counter = 0;
        };
    }
    
    private Variable[] vars;
}
