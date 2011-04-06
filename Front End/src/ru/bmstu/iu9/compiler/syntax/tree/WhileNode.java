/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.syntax.tree;

/**
 *
 * @author maggot
 */
final public class WhileNode extends ConditionBlockNode {
    public WhileNode() {
        super(Node.NodeType.WHILE);
    }
    public WhileNode(Node condition, Node block) {
        super(Node.NodeType.WHILE, condition, block);
    }
}
