/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.semantics.tree;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author maggot
 */
final public class SwitchNode extends Node {
    public SwitchNode() {
        super(Node.NodeType.SWITCH);
    }
    
    public Node expression() { return this.expression; }
    public void setExpression(Node expression) { this.expression = expression; }
    public List<CaseNode> cases() { return this.cases; }
    public void addCase(CaseNode caseNode) { this.cases.add(caseNode); }
    public BlockNode defaultNode() { return this.defaultNode; }
    public void setDefaultNode(BlockNode defaultNode) { this.defaultNode = defaultNode; }
    
    private Node expression;
    private List<CaseNode> cases = new LinkedList<CaseNode>();
    private BlockNode defaultNode;
}
