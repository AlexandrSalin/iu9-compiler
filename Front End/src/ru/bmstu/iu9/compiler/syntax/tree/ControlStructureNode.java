package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
public abstract class ControlStructureNode extends BaseNode {
    public ControlStructureNode(BaseNode.NodeType type, Position position) {
        super(type);
        this.dInfo = new DebugInfo(position);
    }
    public ControlStructureNode(BaseNode.NodeType type, DebugInfo dInfo) {
        super(type);
        this.dInfo = dInfo;
    }
    
    public final DebugInfo dInfo;
}
