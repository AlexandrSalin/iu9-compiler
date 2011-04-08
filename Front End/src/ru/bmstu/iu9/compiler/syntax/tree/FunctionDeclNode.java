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
final public class FunctionDeclNode extends DeclNode {
    public FunctionDeclNode(String name, Type type) {
        super(NodeType.FUNCTION_DECL, type);
        this.name = name;
    }
    public FunctionDeclNode(String name, Type type, BlockNode block) {
        this(name, type);
        this.block = block;
    }
    
    public BlockNode block() { return this.block; }
    public void setBlock(BlockNode block) { this.block = block; }
    public String name() { return this.name; }
    
    @SerializedName("name")
    private final String name;
    @SerializedName("node1")
    private BlockNode block;
}
