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
final public class IfNode extends ConditionBlockNode {
    public IfNode() {
        super(Node.NodeType.IF);
    }
    public IfNode(Node condition, Node block, Node elseBlock) {
        super(Node.NodeType.IF, condition, block);
        this.elseBlock = elseBlock;
    }

    public Node elseBlock() { return this.elseBlock; }
    public void setElseBlock(Node elseBlock) { this.elseBlock = elseBlock; }

    @SerializedName("node3")
    private Node elseBlock;
}
