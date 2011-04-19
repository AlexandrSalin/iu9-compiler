/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.syntax.tree;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 *
 * @author maggot
 */
final public class ForNode extends Node {
    public ForNode() {
        super(Node.NodeType.FOR, null);
    }
    public ForNode(Node initialization, Node condition, Node increase, Node block) {
        this();
        this.initialization = initialization;
        this.condition = condition;
        this.increase = increase;
        this.block = block;
    }
    
    public Node initialization() { return this.initialization; }
    public void setInitialization(Node initialization) { 
        this.initialization = initialization; 
    }
    public void setInitialization(List<Node> initialization) { 
        this.initialization = new BlockNode();
        ((BlockNode)this.initialization).addChildren(initialization); 
    }
    public Node condition() { return this.condition; }
    public void setCondition(Node condition) { 
        this.condition = condition; 
    }
    public Node increase() { return this.increase; }
    public void setIncrease(Node increase) { this.increase = increase; }
    public Node block() { return this.block; }
    public void setBlock(Node block) { this.block = block; }
    
    @SerializedName("node1")
    private Node initialization;
    @SerializedName("node2")
    private Node condition;
    @SerializedName("node3")
    private Node increase;
    @SerializedName("node4")
    private Node block;
}
