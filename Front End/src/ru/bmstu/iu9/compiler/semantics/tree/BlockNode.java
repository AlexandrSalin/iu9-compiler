/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.semantics.tree;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author maggot
 */
final public class BlockNode extends Node implements Iterable<Node> {
    public BlockNode() {
        super(Node.NodeType.BLOCK);
    }
    
    public void addChild(Node child) { this.children.add(child); }
    public List<Node> children() { return this.children; }
    
    @Override
    public Iterator<Node> iterator() {
        return children.iterator();
    }
    
    List<Node> children = new LinkedList<Node>();
}
