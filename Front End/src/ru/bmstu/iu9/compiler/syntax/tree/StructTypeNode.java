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
final public class StructTypeNode extends BaseTypeNode {
    public StructTypeNode(
            String name, 
            boolean constancy,
            Position position) {
        
        super(BaseTypeNode.Type.STRUCT, constancy, new DebugInfo(position));
        this.name = name;
    }
    public StructTypeNode(
            String name, 
            boolean constancy,
            DebugInfo dInfo) {
        
        super(BaseTypeNode.Type.STRUCT, constancy, dInfo);
        this.name = name;
    }
    
    @Override
    public String toString() {
        return "SRTUCT " + name;
    }
        
    public final String name;
}
