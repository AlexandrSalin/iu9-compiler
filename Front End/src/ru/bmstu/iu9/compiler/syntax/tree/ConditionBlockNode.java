/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.syntax.tree;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author maggot
 */
abstract public class ConditionBlockNode extends Node {
    protected ConditionBlockNode(Node.NodeType type) {
        super(type);
    }
    protected ConditionBlockNode(Node.NodeType type, Node condition, Node block) {
        super(type);
        this.condition = condition;
        this.block = block;
    }
    
    public Node expression() { return this.condition; }
    public void setExpression(Node expression) { this.condition = expression; }
    public Node block() { return this.block; }
    public void setBlock(Node block) { this.block = block; }
    
    @SerializedName("node1")
    private Node condition;
    @SerializedName("node2")
    private Node block;
}