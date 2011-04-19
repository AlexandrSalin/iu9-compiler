/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.syntax.tree;

import com.google.gson.annotations.SerializedName;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
final public class NoOperandOperationNode extends ExpressionNode {
    public enum Operation { BREAK, CONTINUE, RETURN, BARRIER };
    
    public NoOperandOperationNode(Operation operation, Position position) {
        super(Node.NodeType.NO_OPERAND_OPERATION, position);
        this.operation = operation.ordinal();
    }
    
    public Operation operation() { return Operation.values()[this.operation]; }
    
    @SerializedName("operation")
    private int operation;
}
