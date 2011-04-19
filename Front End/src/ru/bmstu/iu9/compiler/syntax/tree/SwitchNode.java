/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.syntax.tree;

import com.google.gson.annotations.SerializedName;


/**
 *
 * @author maggot
 */
final public class SwitchNode extends ConditionBlockNode {
    public SwitchNode() {
        super(Node.NodeType.SWITCH, null, new BlockNode());
    }
    public SwitchNode(Node expression, BlockNode cases, BlockNode defaultNode) {
        super(Node.NodeType.SWITCH, expression, cases);
        this.defaultNode = defaultNode;
    }
    
    public BlockNode cases() { return (BlockNode)this.block; }
    public void addCase(CaseNode caseNode) { ((BlockNode)this.block).addChild(caseNode); }
    public BlockNode defaultNode() { return this.defaultNode; }
    public void setDefaultNode(BlockNode defaultNode) { this.defaultNode = defaultNode; }
    
    @SerializedName("node3")
    private BlockNode defaultNode;
}
