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
final public class PointerTypeNode extends PrimitiveTypeNode {
    public PointerTypeNode(
            BaseTypeNode type, 
            boolean constancy,
            Position position) {
        
        super(PrimitiveTypeNode.Type.POINTER, constancy, position);
        this.pointerType = type;
    }
    public PointerTypeNode(
            BaseTypeNode type, 
            boolean constancy,
            DebugInfo dInfo) {
        
        super(PrimitiveTypeNode.Type.POINTER, constancy, dInfo);
        this.pointerType = type;
    }
    
    @Override
    public String toString() {
        return super.toString() + " TO " + pointerType.toString();
    }
    
    public final BaseTypeNode pointerType;
}
