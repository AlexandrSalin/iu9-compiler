/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.syntax.tree;

/**
 *
 * @author maggot
 */
public class DoWhileNode extends ConditionBlockNode {
    public DoWhileNode() {
        super(Node.NodeType.DO_WHILE);
    }
    public DoWhileNode(Node condition, Node block) {
        super(Node.NodeType.DO_WHILE, condition, block);
    }
}

