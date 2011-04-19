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
abstract public class Node {
    public enum NodeType { BINARY_OPERATION, UNARY_OPERATION, VARS_DECL,
        FOR, IF, SWITCH, CASE, WHILE, DO_WHILE, BLOCK, EXPRESSION, VARIABLE,
        CONSTANT, FUNCTION_DECL, STRUCT_DECL, INVALID, CALL, NO_OPERAND_OPERATION
    };
    
    protected Node(Type type, NodeType nodeType, Position position) {
        this.nodeType = nodeType.ordinal();
        this.position = position;
        this.type = type;
    }
    protected Node(NodeType nodeType, Position position) {
        this(null, nodeType, position);
    }
    
    public NodeType nodeType() { return NodeType.values()[this.nodeType]; }
    public Type type() { return this.type; }
    public void setType(Type type) { this.type = type; }
    public Position position() { return this.position; }
    
    @SerializedName("position")
    protected Position position;    
    @SerializedName("type")
    protected Type type;
    @SerializedName("nodeType")
    protected int nodeType;
}