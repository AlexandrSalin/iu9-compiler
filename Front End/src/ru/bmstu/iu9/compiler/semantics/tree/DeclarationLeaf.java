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
final public class DeclarationLeaf extends Leaf {
    public DeclarationLeaf(String name, Type type) {
        super(Node.NodeType.DECLARATION, type);
        this.name = name;
    }
    
    public String name() { return this.name; }
    
    private String name;
}
