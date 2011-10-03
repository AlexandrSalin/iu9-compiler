/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.bmstu.iu9.compiler.syntax.tree;

import java.util.List;

/**
 *
 * @author maggot
 */
final public class BlockDeclNode<T extends DeclNode> extends BlockNode<T> {
    public BlockDeclNode() {
        super(BaseNode.NodeType.BLOCK_DECL);
    }
    public BlockDeclNode(List<T> block) {
        super(BaseNode.NodeType.BLOCK_DECL, block);
    }
    
    @Override
    public String toString() {
        return super.toString();
    }
}
