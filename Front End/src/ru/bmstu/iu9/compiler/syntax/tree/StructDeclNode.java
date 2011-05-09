/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
final public class StructDeclNode extends DeclNode implements Statement {
    public StructDeclNode(
            String name, 
            StructTypeNode type, 
            BlockNode<VariableDeclNode> declarations,
            Position position) {
        
        super(NodeType.STRUCT_DECL, name, type, position);
        this.declarations = declarations;
    }
    public StructDeclNode(
            String name, 
            StructTypeNode type, 
            BlockNode<VariableDeclNode> declarations,
            DebugInfo dInfo) {
        
        super(NodeType.STRUCT_DECL, name, type, dInfo);
        this.declarations = declarations;
    }
    
    @Override
    public BaseNode getNode() {
        return this;
    }
    
      
    public final BlockNode<VariableDeclNode> declarations;
}
