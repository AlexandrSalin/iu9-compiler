package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
final public class BreakNode extends JmpNode implements Statement {
    public BreakNode(Position position) {
        super(BaseNode.NodeType.BREAK, position);
    }
    public BreakNode(DebugInfo dInfo) {
        super(BaseNode.NodeType.BREAK, dInfo);
    }
    
    public BaseNode getNode() {
        return this;
    }
}
