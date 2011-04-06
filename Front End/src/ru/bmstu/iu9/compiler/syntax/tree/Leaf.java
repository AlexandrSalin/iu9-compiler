/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.Position;
import ru.bmstu.iu9.compiler.Type;


/**
 *
 * @author maggot
 */
abstract public class Leaf extends ExpressionNode {
    protected Leaf(Node.NodeType nodeType, Position position) {
        super(nodeType, position);
    }
    protected Leaf(Node.NodeType nodeType, Type type, Position position) {
        super(nodeType, type, position);
    }
}