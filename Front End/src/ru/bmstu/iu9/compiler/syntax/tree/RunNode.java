package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
public class RunNode extends ControlStructureNode implements Statement {
    public RunNode(ExpressionNode expression, Position position) {
        super(BaseNode.NodeType.BARRIER, position);
        this.expression = expression;
    }
    public RunNode(ExpressionNode expression, DebugInfo dInfo) {
        super(BaseNode.NodeType.BARRIER, dInfo);
        this.expression = expression;
    }
    
    @Override
    public BaseNode getNode() {
        return this;
    }
    
    public final ExpressionNode expression;
}