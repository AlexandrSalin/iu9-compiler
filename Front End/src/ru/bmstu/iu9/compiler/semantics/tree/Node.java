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
public class Node {
    protected enum NodeType { BINARY_OPERATION, UNARY_OPERATION, DECLARATION,
        FOR, IF, SWITCH, CASE, WHILE, DO_WHILE, BLOCK, EXPRESSION, VARIABLE,
        CONSTANT, FUNCTION, STRUCT, INVALID, CALL, NO_OPERAND_OPERATION
    };
    
    protected Node(Type type, NodeType nodeType) {
        this.type = type;
        this.nodeType = nodeType;
    }
    protected Node(NodeType nodeType) {
        this.nodeType = nodeType;
    }
    
    public Type type() { return this.type; }
    public void setType(Type type) { this.type = type; }
    
    protected Type type;
    private NodeType nodeType;
}