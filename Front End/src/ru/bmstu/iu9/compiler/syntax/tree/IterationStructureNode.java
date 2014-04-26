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
public abstract class IterationStructureNode 
        extends ControlStructureNode implements Statement {
    
    public IterationStructureNode(
            BaseNode.NodeType type, 
            ExpressionNode expression,
            BlockNode<Statement> block,
            Position position) {
        
        super(type, position);
        this.block = block;
        this.expression = expression;
    }
    public IterationStructureNode(
            BaseNode.NodeType type, 
            ExpressionNode expression,
            BlockNode<Statement> block,
            DebugInfo dInfo) {
        
        super(type, dInfo);
        this.block = block;
        this.expression = expression;
    }
    
    public final ExpressionNode expression;
    public final BlockNode<Statement> block;
}
