package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
final public class LockNode extends ControlStructureNode implements Statement {
    public LockNode(BlockNode<Statement> block, Position position) {
        super(BaseNode.NodeType.LOCK, position);
        this.block = block;        
    }
    public LockNode(BlockNode<Statement> block, DebugInfo dInfo) {
        super(BaseNode.NodeType.LOCK, dInfo);
        this.block = block;        
    }
    
    public BaseNode getNode() {
        return this;
    }
    
    public final BlockNode<Statement> block;
}
