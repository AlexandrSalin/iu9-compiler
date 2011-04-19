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
abstract public class DeclNode extends Node {
    protected DeclNode(NodeType nodeType, String name, Type type, Position position) {
        super(type, nodeType, position);
        this.name = name;
    }
    
    public String name() { return this.name; }
    
    @SerializedName("name")
    protected final String name; 
}
