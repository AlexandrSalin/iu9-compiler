package ru.bmstu.iu9.compiler.parser;

import ru.bmstu.iu9.compiler.Type;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author maggot
 */
abstract class TreeNode {  
    protected TreeNode(Type type) {
        this.type = type;
    }
    protected TreeNode() { }
    
    public Type type() { return this.type; }
    
    protected Type type;
}

final class InvalidNode extends TreeNode {
    public InvalidNode() { }
}

final class CompositeNode extends TreeNode {
    public enum Operation { ASSIGN, ARRAY_ELEMENT, FOR, MEMBER_SELECT, CALL,
        FUNCTION_ARGUMENTS, POST_INC, POST_DEC, LOCK, BARRIER, CONTINUE, RETURN, 
        RUN, DO_WHILE, WHILE, IF, ELSE, SEQUENCING, BREAK, MINUS, PRE_DEC, 
        PRE_INC, UNARY_MINUS, REF, DEREF, CAST, DIV, MUL, MOD, PLUS, 
        BITWISE_SHIFT_RIGHT, BITWISE_SHIFT_LEFT, GREATER, GREATER_OR_EQUAL, 
        LESS, LESS_OR_EUQAL, NOT_EQUAL, EQUAL, BITWISE_AND, BITWISE_XOR,
        BITWISE_OR, BOOL_AND, BOOL_OR, BITWISE_AND_ASSIGN, BITWISE_XOR_ASSIGN, 
        BITWISE_SHIFT_RIGHT_ASSIGN, BITWISE_SHIFT_LEFT_ASSIGN, BITWISE_OR_ASSIGN, 
        MOD_ASSIGN, DIV_ASSIGN, MUL_ASSIGN, MINUS_ASSIGN, PLUS_ASSIGN,
        SWITCH, CASE};
    public CompositeNode(Type type, Operation operation) {
        super(type);
        this.operation = operation;
    }
    public CompositeNode(Operation operation) {
        this.operation = operation;
    }
    
    public void addChild(TreeNode child) {
        if (child != null)
            children.add(child);
    }
    public int childrenNumber() { return this.children.size(); }
    public void setType(Type type) { this.type = type; }
    public List<TreeNode> children() { return this.children; }
    public Operation operation() { return this.operation; }
    
    private List<TreeNode> children = new LinkedList<TreeNode>();
    private Operation operation;
}

final class ConstantLeaf extends TreeNode {
    public ConstantLeaf(Type type, Object value) {
        super(type);
        this.value = value;
    }
    
    public Object value() { return this.value; }
    
    private final Object value;
}

final class VariableLeaf extends TreeNode {
    public VariableLeaf(String name, SymbolTable symbolTable) {
        super(symbolTable.get(name).type());
        this.name = name;
    }
    public VariableLeaf(String name, Type type) {
        super(type);
        this.name = name;
    }
    
    public String name() { return this.name; }
    
    private final String name;
}