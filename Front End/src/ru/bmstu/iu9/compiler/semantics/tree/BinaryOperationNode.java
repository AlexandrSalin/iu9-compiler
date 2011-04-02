/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.semantics.tree;

/**
 *
 * @author maggot
 */
final public class BinaryOperationNode extends ExpressionNode {
    public enum Operation { 
        ASSIGN, MINUS, DIV, MUL, MOD, PLUS, 
        BITWISE_SHIFT_RIGHT, BITWISE_SHIFT_LEFT, GREATER, GREATER_OR_EQUAL, 
        LESS, LESS_OR_EUQAL, NOT_EQUAL, EQUAL, BITWISE_AND, BITWISE_XOR,
        BITWISE_OR, BOOL_AND, BOOL_OR, BITWISE_AND_ASSIGN, BITWISE_XOR_ASSIGN, 
        BITWISE_SHIFT_RIGHT_ASSIGN, BITWISE_SHIFT_LEFT_ASSIGN, BITWISE_OR_ASSIGN, 
        MOD_ASSIGN, DIV_ASSIGN, MUL_ASSIGN, MINUS_ASSIGN, PLUS_ASSIGN,
        MEMBER_SELECT, ARRAY_ELEMENT, 
        
        Bitwise(new Operation[] {
            BITWISE_SHIFT_RIGHT, BITWISE_SHIFT_LEFT, BITWISE_AND, BITWISE_XOR,
            BITWISE_OR, BITWISE_XOR_ASSIGN, BITWISE_SHIFT_RIGHT_ASSIGN, 
            BITWISE_SHIFT_LEFT_ASSIGN, BITWISE_OR_ASSIGN
        });
    
        private Operation() {
            this.value = 1 << this.ordinal();
        }
        private Operation(Operation[] operations) {
            for (int i = 0; i < operations.length; ++i)
                this.value = this.value | operations[i].value;
        }
        
        public boolean is(Operation operation) {
            return (this.value & operation.value) != 0;
        }
        
        private long value = 0;
    };
    
    public BinaryOperationNode(Operation operation) {
        super(Node.NodeType.BINARY_OPERATION);
        this.operation = operation;
    }
    public BinaryOperationNode(Operation operation, Node left, Node right) {
        super(Node.NodeType.BINARY_OPERATION);
        this.operation = operation;
        this.left = left;
        this.right = right;
    }
    
    public void setLeftChild(Node child) { this.left = child; }
    public void setRightChild(Node child) { this.right = child; }
    public Node leftChild() { return this.left; }
    public Node rightChild() { return this.right; }
    
    private Node left;
    private Node right;
    private Operation operation;
}
