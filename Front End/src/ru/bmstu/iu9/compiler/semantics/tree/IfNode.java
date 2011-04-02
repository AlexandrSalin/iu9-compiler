/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.semantics.tree;

/**
 *
 * @author maggot
 */
final public class IfNode extends ConditionBlockNode {
    public IfNode() {
        super(Node.NodeType.IF);
    }

    public Node elseBlock() { return this.elseBlock; }
    public void setElseBlock(Node elseBlock) { this.elseBlock = elseBlock; }

    private Node elseBlock;
}
