package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
final public class BarrierNode extends ControlStructureNode implements Statement {
    public BarrierNode(Position position) {
        super(BaseNode.NodeType.BARRIER, position);
    }
    public BarrierNode(DebugInfo dInfo) {
        super(BaseNode.NodeType.BARRIER, dInfo);
    }

    @Override
    public BaseNode getNode() {
        return this;
    }
}
