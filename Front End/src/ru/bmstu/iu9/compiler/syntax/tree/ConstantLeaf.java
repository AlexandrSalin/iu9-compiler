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
final public class ConstantLeaf extends Leaf {
    public ConstantLeaf(Object value, Position position) {
        super(Node.NodeType.CONSTANT, position);
        this.value = value;
    }
    public ConstantLeaf(Object value, Type type, Position position) {
        super(Node.NodeType.CONSTANT, type, position);
        this.value = value;
    }
    
    public Object value() { return this.value; }
    
    @SerializedName("value")
    private final Object value;
}
