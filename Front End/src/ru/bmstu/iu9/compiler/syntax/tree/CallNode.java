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
final public class CallNode extends ExpressionNode {
    public CallNode(Position position) {
        super(Node.NodeType.CALL, position);
    }
    public CallNode(Node function, Position position) {
        this(position);
        this.function = function;
    }
    public CallNode(Node function, BlockNode arguments, Position position) {
        this(function, position);
        this.arguments = arguments;
    }
    
    public Node function() { return this.function; }
    public void setFunction(Node function) { this.function = function; }
    public BlockNode arguments() { return this.arguments; }
    public void addArgument(Node argument) { this.arguments.addChild(argument); }
    
    @SerializedName("node1")
    private Node function;
    @SerializedName("node2")
    private BlockNode arguments;
}
