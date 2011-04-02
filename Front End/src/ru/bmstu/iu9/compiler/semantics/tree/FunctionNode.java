/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.semantics.tree;

/**
 *
 * @author maggot
 */
final public class FunctionNode extends Node {
    public FunctionNode() {
        super(Node.NodeType.FUNCTION);
    }
    
    public BlockNode block() { return this.block; }
    public void setBlock(BlockNode block) { this.block = block; }
    public DeclarationLeaf declaration() { return this.declaration; }
    public void setDeclaration(DeclarationLeaf declaration) {
        this.declaration = declaration;
    }
    
    private BlockNode block;
    private DeclarationLeaf declaration;
}
