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
final public class FunctionDeclNode extends DeclNode {
    public FunctionDeclNode(String name, Type type, Position position) {
        super(NodeType.FUNCTION_DECL, name, type, position);
    }
    public FunctionDeclNode(String name, Type type, BlockNode block,
            Position position) {
        this(name, type, position);
        this.block = block;
    }
    
    public BlockNode block() { return this.block; }
    public void setBlock(BlockNode block) { this.block = block; }

    @SerializedName("node1")
    private BlockNode block;
}
