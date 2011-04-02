/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.semantics.tree;

/**
 *
 * @author maggot
 */
final public class StructNode extends Node {
    public StructNode(String name) {
        super(Node.NodeType.STRUCT);
        this.name = name;
    }
    
    public BlockNode declarations() { return this.declarations; }
    public void addDeclaration(Node declaration) { 
        this.declarations.addChild(declaration); 
    }
    public String name() { return this.name; }
    public void setName(String name) { this.name = name; }
    
    private String name;
    private BlockNode declarations;
}
