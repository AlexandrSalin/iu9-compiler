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
abstract public class ConditionalNode extends ControlStructureNode {
    protected ConditionalNode(
            BaseNode.NodeType type, 
            BlockNode<Statement> block,
            Position position) {
        
        super(type, position);
        this.block = block;
    }
    protected ConditionalNode(
            BaseNode.NodeType type, 
            BlockNode<Statement> block,
            DebugInfo dInfo) {
        
        super(type, dInfo);
        this.block = block;
    }
    
    public final BlockNode<Statement> block;
}