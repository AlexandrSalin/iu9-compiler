package ru.bmstu.iu9.compiler.parser;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author maggot
 */
class TreeNode {
    private TreeNode() { }
    
    public static TreeNode getCompositeNode(Type type) {
        TreeNode compositeNode = new TreeNode();
        compositeNode.children = new LinkedList<TreeNode>();
        compositeNode.type = type;
        
        return compositeNode;
    }
    public static TreeNode getConstantLeaf(Type type, Object value) {
        TreeNode constantNode = new TreeNode();
        constantNode.type = type;
        constantNode.value = value;
        
        return constantNode;
    }
    public static TreeNode getVariableLeaf(String name, SymbolTable symbolTable) {
        TreeNode variableNode = new TreeNode();
        variableNode.type = symbolTable.get(name);
        variableNode.name = name;
        variableNode.symbolTable = symbolTable;
        
        return variableNode;
    }
    
    public void addChild(TreeNode child) {
        if (children != null)
            children.add(child);
        else
            throw new UnsupportedOperationException();
    }
    public Type type() { return this.type; }
    public List<TreeNode> children() { return this.children; }
    public Object value() { return this.value; }
    public String name() { return this.name; }
    public SymbolTable symbolTable() { return this.symbolTable; }
    
    private List<TreeNode> children = null;
    private Type type = null;
    private Object value = null;
    private String name = null;
    private SymbolTable symbolTable = null;
}