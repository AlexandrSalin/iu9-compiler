/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.semantics.tree;

/**
 *
 * @author maggot
 */
final public class VariableLeaf extends Leaf {
    public VariableLeaf(String name) {
        super(Node.NodeType.VARIABLE);
        this.name = name;
    }
    
    public String name() { return this.name; }
    
    private final String name;
}
