/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.semantics.tree;

import ru.bmstu.iu9.compiler.Type;

/**
 *
 * @author maggot
 */
final public class UnaryOperationNode extends ExpressionNode {
    public enum Operation { POST_INC, POST_DEC, MINUS, PLUS, REF, DEREF, CAST,
        PRE_DEC, PRE_INC, RUN, LOCK, RETURN };
    
    public UnaryOperationNode(Operation operation) {
        super(Node.NodeType.UNARY_OPERATION);
        this.operation = operation;
    }
    public UnaryOperationNode(Operation operation, Type type) {
        super(Node.NodeType.UNARY_OPERATION, type);
        this.operation = operation;
    }
    public UnaryOperationNode(Operation operation, Node child) {
        this(operation);
        this.child = child;
    }
    public UnaryOperationNode(Operation operation, Type type, Node child) {
        this(operation, type);
        this.child = child;
    }
    
    public Node child() { return this.child; }
    public void setChild(Node child) { this.child = child; }
    
    private Node child;
    private Operation operation;
}
