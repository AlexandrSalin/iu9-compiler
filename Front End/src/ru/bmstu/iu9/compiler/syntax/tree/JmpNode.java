package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
public abstract class JmpNode extends ControlStructureNode implements Statement {
    public JmpNode(BaseNode.NodeType type, Position position) {
        super(type, position);
    }
    public JmpNode(BaseNode.NodeType type, DebugInfo dInfo) {
        super(type, dInfo);
    }
}
