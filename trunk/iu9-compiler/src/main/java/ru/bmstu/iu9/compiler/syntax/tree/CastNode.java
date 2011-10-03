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
final public class CastNode extends UnaryOperationNode {
    public CastNode(
            BaseTypeNode type, 
            ExpressionNode expression, 
            Position position) {
        
        super(UnaryOperationNode.Operation.CAST, expression, position);
        this.castingType = type;
    }
    public CastNode(
            BaseTypeNode type, 
            ExpressionNode expression, 
            DebugInfo dInfo) {
        
        super(UnaryOperationNode.Operation.CAST, expression, dInfo);
        this.castingType = type;
    }
    
    public final BaseTypeNode castingType;
}
