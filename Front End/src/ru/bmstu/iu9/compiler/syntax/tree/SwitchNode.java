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
final public class SwitchNode extends Node {
    public SwitchNode() {
        super(Node.NodeType.SWITCH);
    }
    public SwitchNode(Node expression, BlockNode cases, BlockNode defaultNode) {
        super(Node.NodeType.SWITCH);
        this.expression = expression;
        this.cases = cases;
        this.defaultNode = defaultNode;
    }
    
    public Node expression() { return this.expression; }
    public void setExpression(Node expression) { this.expression = expression; }
    public BlockNode cases() { return this.cases; }
    public void addCase(CaseNode caseNode) { this.cases.addChild(caseNode); }
    public BlockNode defaultNode() { return this.defaultNode; }
    public void setDefaultNode(BlockNode defaultNode) { this.defaultNode = defaultNode; }
    
    @SerializedName("node1")
    private Node expression;
    @SerializedName("node2")
    private BlockNode cases = new BlockNode();
    @SerializedName("node3")
    private BlockNode defaultNode;
}
