package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;


/**
 *
 * @author maggot
 */
abstract public class Leaf extends ExpressionNode {
    protected Leaf(BaseNode.NodeType nodeType, Position position) {
        super(nodeType, position);
    }
    protected Leaf(BaseNode.NodeType nodeType, DebugInfo dInfo) {
        super(nodeType, dInfo);
    }
}
