/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.semantics.tree;

/**
 *
 * @author maggot
 */
final public class CallNode extends Node {
    public CallNode() {
        super(Node.NodeType.CALL);
    }
    public CallNode(Node function) {
        super(Node.NodeType.CALL);
        this.function = function;
    }
    public CallNode(Node function, BlockNode arguments) {
        super(Node.NodeType.CALL);
        this.function = function;
        this.arguments = arguments;
    }
    
    public Node function() { return this.function; }
    public void setFunction(Node function) { this.function = function; }
    public BlockNode arguments() { return this.arguments; }
    public void addArgument(Node argument) { this.arguments.addChild(argument); }
    
    private Node function;
    private BlockNode arguments;
}
