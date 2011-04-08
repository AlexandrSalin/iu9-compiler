/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.Type;

/**
 *
 * @author maggot
 */
abstract public class DeclNode extends Node {
    protected DeclNode(NodeType nodeType, Type type) {
        super(type, nodeType);
    }
    protected DeclNode(NodeType nodeType) {
        super(nodeType);
    }
}
