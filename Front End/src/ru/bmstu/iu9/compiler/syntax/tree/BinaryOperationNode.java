package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;

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
        BITWISE_SHIFT_RIGHT_ASSIGN, BITWISE_SHIFT_LEFT_ASSIGN, 
        BITWISE_OR_ASSIGN, MOD_ASSIGN, DIV_ASSIGN, MUL_ASSIGN, MINUS_ASSIGN, 
        PLUS_ASSIGN, MEMBER_SELECT, ARRAY_ELEMENT, 
        
        Bitwise(new Operation[] {
            BITWISE_SHIFT_RIGHT, BITWISE_SHIFT_LEFT, BITWISE_AND, BITWISE_XOR,
            BITWISE_OR, BITWISE_XOR_ASSIGN, BITWISE_SHIFT_RIGHT_ASSIGN, 
            BITWISE_SHIFT_LEFT_ASSIGN, BITWISE_OR_ASSIGN
        }),
        Comparison(new Operation[] {
            GREATER, GREATER_OR_EQUAL, LESS, LESS_OR_EUQAL, NOT_EQUAL, EQUAL
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
    
    public BinaryOperationNode(Operation operation, Position position) {
        super(BaseNode.NodeType.BINARY_OPERATION, position);
        this.operation = operation.ordinal();
    }
    public BinaryOperationNode(
            Operation operation, 
            ExpressionNode left, 
            ExpressionNode right,
            Position position) {
        
        super(BaseNode.NodeType.BINARY_OPERATION, position);
        this.operation = operation.ordinal();
        this.left = left;
        this.right = right;
    }
    public BinaryOperationNode(
            Operation operation, 
            ExpressionNode left, 
            ExpressionNode right,
            DebugInfo dInfo) {
        
        super(BaseNode.NodeType.BINARY_OPERATION, dInfo);
        this.operation = operation.ordinal();
        this.left = left;
        this.right = right;
    }
    
    public Operation operation() { 
        return Operation.values()[this.operation]; 
    }
    public void setLeftChild(ExpressionNode child) { 
        this.left = child; 
    }
    public void setRightChild(ExpressionNode child) {
        this.right = child;
    }
    public ExpressionNode leftChild() {
        return this.left;
    }
    public ExpressionNode rightChild() {
        return this.right; 
    }
    
    private ExpressionNode left;
    private ExpressionNode right;
    public final int operation;
}
