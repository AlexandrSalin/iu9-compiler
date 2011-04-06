/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.syntax.tree;

import com.google.gson.annotations.SerializedName;

/**
 *
 * @author maggot
 */
final public class NoOperandOperationNode extends Node {
    public enum Operation { BREAK, CONTINUE, RETURN, BARRIER };
    
    public NoOperandOperationNode(Operation operation) {
        super(Node.NodeType.NO_OPERAND_OPERATION);
        this.operation = operation.ordinal();
    }
    
    public Operation operation() { return Operation.values()[this.operation]; }
    
    @SerializedName("operation")
    private int operation;
}
