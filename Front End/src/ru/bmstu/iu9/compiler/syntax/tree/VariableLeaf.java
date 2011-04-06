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
final public class VariableLeaf extends Leaf {
    public VariableLeaf(String name, Position position) {
        super(Node.NodeType.VARIABLE, position);
        this.name = name;
    }
    
    public String name() { return this.name; }
    
    @SerializedName("name")
    private final String name;
}
