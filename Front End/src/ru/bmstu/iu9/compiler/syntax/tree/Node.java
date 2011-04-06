/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.syntax.tree;

import com.google.gson.annotations.SerializedName;
import ru.bmstu.iu9.compiler.Type;

/**
 *
 * @author maggot
 */
abstract public class Node {
    public enum NodeType { BINARY_OPERATION, UNARY_OPERATION, DECLARATION,
        FOR, IF, SWITCH, CASE, WHILE, DO_WHILE, BLOCK, EXPRESSION, VARIABLE,
        CONSTANT, FUNCTION, STRUCT, INVALID, CALL, NO_OPERAND_OPERATION
    };
    
    protected Node(Type type, NodeType nodeType) {
        this(nodeType);
        this.type = type;
    }
    protected Node(NodeType nodeType) {
        this.nodeType = nodeType.ordinal();
    }
    
    public NodeType nodeType() { return NodeType.values()[this.nodeType]; }
    public Type type() { return this.type; }
    public void setType(Type type) { this.type = type; }
    
    @SerializedName("type")
    protected Type type;
    @SerializedName("nodeType")
    protected int nodeType;
}