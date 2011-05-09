package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
final public class ContinueNode extends JmpNode implements Statement {
    public ContinueNode(Position position) {
        super(BaseNode.NodeType.CONTINUE, position);
    }
    public ContinueNode(DebugInfo dInfo) {
        super(BaseNode.NodeType.CONTINUE, dInfo);
    }
    
    @Override
    public BaseNode getNode() {
        return this;
    }
}
