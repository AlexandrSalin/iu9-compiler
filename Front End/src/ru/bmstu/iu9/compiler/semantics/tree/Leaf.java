/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.semantics.tree;

import ru.bmstu.iu9.compiler.Type;


/**
 *
 * @author maggot
 */
abstract public class Leaf extends Node {
    protected Leaf(Node.NodeType nodeType) {
        super(nodeType);
    }
    protected Leaf(Node.NodeType nodeType, Type type) {
        super(type, nodeType);
    }
}
