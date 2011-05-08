package ru.bmstu.iu9.compiler.syntax.tree;

import com.google.gson.annotations.SerializedName;
import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;


/**
 *
 * @author maggot
 */
final public class SwitchNode 
        extends ControlStructureNode implements Statement {
    public SwitchNode(
            ExpressionNode expression, 
            BlockNode<CaseNode> cases, 
            DefaultNode defaultNode,
            Position position) {
        
        super(BaseNode.NodeType.SWITCH, position);
        this.cases = cases;
        this.expression = expression;
        this.defaultNode = defaultNode;
    }
    public SwitchNode(
            ExpressionNode expression, 
            BlockNode<CaseNode> cases, 
            DefaultNode defaultNode,
            DebugInfo dInfo) {
        
        super(BaseNode.NodeType.SWITCH, dInfo);
        this.cases = cases;
        this.expression = expression;
        this.defaultNode = defaultNode;
    }
    
   
    public final ExpressionNode expression;
    public final BlockNode<CaseNode> cases;
    public final DefaultNode defaultNode;
}
