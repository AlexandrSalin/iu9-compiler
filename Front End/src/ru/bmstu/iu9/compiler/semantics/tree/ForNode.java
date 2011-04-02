/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.semantics.tree;

/**
 *
 * @author maggot
 */
final public class ForNode extends Node {
    public ForNode() {
        super(Node.NodeType.FOR);
    }
    
    public Node initialization() { return this.initialization; }
    public void setInitialization(Node initialization) { 
        this.initialization = initialization; 
    }
    public Node condition() { return this.condition; }
    public void setCondition(Node condition) { 
        this.condition = condition; 
    }
    public Node increase() { return this.increase; }
    public void setIncrease(Node increase) { this.increase = increase; }
    public Node block() { return this.block; }
    public void setBlock(Node block) { this.block = block; }
    
    private Node initialization;
    private Node condition;
    private Node increase;
    private Node block;
}
