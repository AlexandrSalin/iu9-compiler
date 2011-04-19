/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
final public class InvalidNode extends Node {
    public InvalidNode(Position position) {
        super(Node.NodeType.INVALID, position);
    }
}
