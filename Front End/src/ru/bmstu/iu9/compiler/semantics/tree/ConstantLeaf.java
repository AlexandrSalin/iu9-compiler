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
final public class ConstantLeaf extends Leaf {
    public ConstantLeaf(Object value) {
        super(Node.NodeType.CONSTANT);
        this.value = value;
    }
    public ConstantLeaf(Object value, Type type) {
        super(Node.NodeType.CONSTANT, type);
        this.value = value;
    }
    
    public Object value() { return this.value; }
    
    private final Object value;
}
