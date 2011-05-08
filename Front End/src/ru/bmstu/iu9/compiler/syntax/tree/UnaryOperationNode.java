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
public class UnaryOperationNode extends ExpressionNode {
    public enum Operation { 
        POST_INC, POST_DEC, MINUS, PLUS, REF, DEREF, PRE_DEC, PRE_INC, CAST
    };
    
    /*public Unary(Operation operation, Position position) {
        super(BaseNode.NodeType.UNARY_OPERATION, position);
        this.operation = operation.ordinal();
    }*/
    public UnaryOperationNode(
            Operation operation, 
            ExpressionNode node, 
            Position position) {
        
        super(BaseNode.NodeType.UNARY_OPERATION, position);
        this.operation = operation.ordinal();
        this.node = node;
    }
    public UnaryOperationNode(
            Operation operation, 
            ExpressionNode node, 
            DebugInfo dInfo) {
        
        super(BaseNode.NodeType.UNARY_OPERATION, dInfo);
        this.operation = operation.ordinal();
        this.node = node;
    }
    
    public Operation operation() { return Operation.values()[this.operation]; }
/*    public ExpressionNode node() { return this.node; }
    public void setChild(ExpressionNode node) { this.node = node; }*/
    
//    @SerializedName("node1")
    public final ExpressionNode node;
//    @SerializedName("operation")
    public final int operation;
}
