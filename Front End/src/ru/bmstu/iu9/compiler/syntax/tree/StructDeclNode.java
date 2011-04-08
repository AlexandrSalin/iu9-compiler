/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.syntax.tree;

import com.google.gson.annotations.SerializedName;
import ru.bmstu.iu9.compiler.StructType;

/**
 *
 * @author maggot
 */
final public class StructDeclNode extends DeclNode {
    public StructDeclNode(String name, StructType type) {
        super(NodeType.STRUCT_DECL, type);
        this.name = name;
    }
    public StructDeclNode(String name, StructType type, BlockNode declarations) {
        this(name, type);
        this.declarations = declarations;
    }
    
    public BlockNode declarations() { return this.declarations; }
    public void addDeclaration(Node declaration) { 
        this.declarations.addChild(declaration); 
    }
    public String name() { return this.name; }
    
    @SerializedName("name")
    private final String name;    
    @SerializedName("node1")
    private BlockNode declarations = new BlockNode();
}
