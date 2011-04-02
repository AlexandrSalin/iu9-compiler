/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.semantics.tree;

/**
 *
 * @author maggot
 */
final public class CaseNode extends ConditionBlockNode {
    public CaseNode() {
        super(Node.NodeType.CASE);
    }
}
