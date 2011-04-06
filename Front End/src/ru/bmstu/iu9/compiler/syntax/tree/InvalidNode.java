/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.syntax.tree;

/**
 *
 * @author maggot
 */
final public class InvalidNode extends Node {
    public InvalidNode() {
        super(Node.NodeType.INVALID);
    }
}
