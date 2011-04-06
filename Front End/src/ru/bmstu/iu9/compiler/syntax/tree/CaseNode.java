/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.syntax.tree;

/**
 *
 * @author maggot
 */
final public class CaseNode extends ConditionBlockNode {
    public CaseNode() {
        super(Node.NodeType.CASE);
    }
    public CaseNode(Node expression, Node block) {
        super(Node.NodeType.CASE, expression, block);
    }
}
