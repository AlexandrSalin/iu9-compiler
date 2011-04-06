/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.syntax.tree;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author maggot
 */
final public class FunctionNode extends Node {
    public FunctionNode() {
        super(Node.NodeType.FUNCTION);
    }
    public FunctionNode(DeclarationLeaf declaration, BlockNode block) {
        super(Node.NodeType.FUNCTION);
        this.declaration = declaration;
        this.block = block;
    }
    public FunctionNode(DeclarationLeaf declaration, BlockNode arguments, BlockNode block) {
        this(declaration, block);
        this.arguments = arguments;
    }
    
    public BlockNode block() { return this.block; }
    public void setBlock(BlockNode block) { this.block = block; }
    public DeclarationLeaf declaration() { return this.declaration; }
    public void setDeclaration(DeclarationLeaf declaration) {
        this.declaration = declaration;
    }
    public BlockNode arguments() { return this.arguments; }
    public void addArgument(Node argument) { this.arguments.addChild(argument); }
    
    @SerializedName("node2")
    private BlockNode arguments = new BlockNode();
    @SerializedName("node3")
    private BlockNode block;
    @SerializedName("node1")
    private DeclarationLeaf declaration;
}
