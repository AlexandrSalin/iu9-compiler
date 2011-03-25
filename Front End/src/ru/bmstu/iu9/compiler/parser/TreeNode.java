package ru.bmstu.iu9.compiler.parser;

import com.google.gson.InstanceCreator;
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
    
    private List<TreeNode> children = null;
    private Type type = null;
    private Object value = null;
    private String name = null;
    
    public static class TreeNodeInstanceCreator implements InstanceCreator<TreeNode> {
        @Override
        public TreeNode createInstance(java.lang.reflect.Type type) {
            return new TreeNode();
        }
    }
}