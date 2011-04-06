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
final public class DeclarationLeaf extends Leaf {
    public DeclarationLeaf(String name, Type type, Position position) {
        super(Node.NodeType.DECLARATION, type, position);
        this.name = name;
    }
    
    public String name() { return this.name; }
    
    @SerializedName("name")
    private String name;
}
