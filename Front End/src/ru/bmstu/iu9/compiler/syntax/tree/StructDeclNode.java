/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.syntax.tree;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import ru.bmstu.iu9.compiler.Position;
import ru.bmstu.iu9.compiler.StructType;

/**
 *
 * @author maggot
 */
final public class StructDeclNode extends DeclNode {
    public StructDeclNode(String name, StructType type, Position position) {
        super(NodeType.STRUCT_DECL, name, type, position);
    }
    public StructDeclNode(String name, StructType type, BlockNode declarations,
            Position position) {
        this(name, type, position);
        this.declarations = declarations;
    }
    
    public BlockNode declarations() { return this.declarations; }
    public void addDeclaration(VariableDeclNode declaration) { 
        this.declarations.addChild(declaration); 
    }
    public void addDeclarations(List<VariableDeclNode> declarations) { 
        this.declarations.addChildren(declarations); 
    }
       
    @SerializedName("node1")
    private BlockNode declarations = new BlockNode();
}
