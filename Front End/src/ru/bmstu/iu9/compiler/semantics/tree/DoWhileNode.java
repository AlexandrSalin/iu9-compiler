/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.semantics.tree;

/**
 *
 * @author maggot
 */
public class DoWhileNode extends ConditionBlockNode {
    public DoWhileNode() {
        super(Node.NodeType.DO_WHILE);
    }
}

