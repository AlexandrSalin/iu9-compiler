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
final public class FunctionDeclNode extends DeclNode {
    public FunctionDeclNode(
            String name, 
            BaseTypeNode type, 
            BlockNode<Statement> block,
            Position position) {
        
        super(NodeType.FUNCTION_DECL, name, type, position);
        this.block = block;
    }
    public FunctionDeclNode(
            String name, 
            BaseTypeNode type, 
            BlockNode<Statement> block,
            DebugInfo dInfo) {
        
        super(NodeType.FUNCTION_DECL, name, type, dInfo);
        this.block = block;
    }
    
    public BaseNode getNode() {
        return this;
    }
    

    public final BlockNode<Statement> block;
}
