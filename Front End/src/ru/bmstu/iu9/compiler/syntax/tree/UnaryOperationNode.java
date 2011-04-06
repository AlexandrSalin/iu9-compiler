/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.syntax.tree;

import com.google.gson.annotations.SerializedName;
import ru.bmstu.iu9.compiler.Position;
import ru.bmstu.iu9.compiler.Type;

/**
 *
 * @author maggot
 */
final public class UnaryOperationNode extends ExpressionNode {
    public enum Operation { POST_INC, POST_DEC, MINUS, PLUS, REF, DEREF, CAST,
        PRE_DEC, PRE_INC, RUN, LOCK, RETURN };
    
    public UnaryOperationNode(Operation operation, Position position) {
        super(Node.NodeType.UNARY_OPERATION, position);
        this.operation = operation.ordinal();
    }
    public UnaryOperationNode(Operation operation, Type type, Position position) {
        super(Node.NodeType.UNARY_OPERATION, type, position);
        this.operation = operation.ordinal();
    }
    public UnaryOperationNode(Operation operation, Node child, 
            Position position) {
        this(operation, position);
        this.child = child;
    }
    public UnaryOperationNode(Operation operation, Type type, Node child, 
            Position position) {
        this(operation, type, position);
        this.child = child;
    }
    
    public Operation operation() { return Operation.values()[this.operation]; }
    public Node child() { return this.child; }
    public void setChild(Node child) { this.child = child; }
    
    @SerializedName("node1")
    private Node child;
    @SerializedName("operation")
    private int operation;
}
