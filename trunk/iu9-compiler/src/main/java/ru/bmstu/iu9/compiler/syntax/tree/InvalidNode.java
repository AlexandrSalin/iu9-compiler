/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
final public class InvalidNode extends BaseNode {
    public InvalidNode(Position position) {
        super(BaseNode.NodeType.INVALID);
        this.dInfo = new DebugInfo(position);
    }
    public InvalidNode(DebugInfo dInfo) {
        super(BaseNode.NodeType.INVALID);
        this.dInfo = dInfo;
    }
    
    public final DebugInfo dInfo;
}
