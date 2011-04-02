/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.semantics.tree;

/**
 *
 * @author maggot
 */
abstract public class ConditionBlockNode extends Node {
    protected ConditionBlockNode(Node.NodeType type) {
        super(type);
    }
    
    public Node expression() { return this.expression; }
    public void setExpression(Node expression) { this.expression = expression; }
    public Node block() { return this.block; }
    public void setBlock(Node block) { this.block = block; }
    
    private Node expression;
    private Node block;
}