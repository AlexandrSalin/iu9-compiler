/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.syntax.tree;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import ru.bmstu.iu9.compiler.*;

/**
 *
 * @author maggot
 */
final public class StructNode extends Node {
    public StructNode(DeclarationLeaf declaration) {
        super(Node.NodeType.STRUCT);
        this.declaration = declaration;
    }
    public StructNode(DeclarationLeaf declaration, BlockNode declarations) {
        super(Node.NodeType.STRUCT);
        this.declaration = declaration;
        this.declarations = declarations;
    }
    
    public DeclarationLeaf declaration() { return this.declaration; }
    public BlockNode declarations() { return this.declarations; }
    public void addDeclaration(Node declaration) { 
        this.declarations.addChild(declaration); 
    }
    public void addDeclarations(List<Node> declarations) {
        this.declarations.addChildren(declarations);
    }
    
    @SerializedName("node2")
    private BlockNode declarations = new BlockNode();
    @SerializedName("node1")
    private DeclarationLeaf declaration;
}
