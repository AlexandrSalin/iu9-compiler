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
abstract public class ConstantLeaf extends Leaf {
    public enum ConstantType { INT, CHAR, DOUBLE, BOOL };
    
    protected ConstantLeaf(ConstantType type, Position position) {
        super(BaseNode.NodeType.CONSTANT, position);
        this.constantType = type.ordinal();
    }
    protected ConstantLeaf(ConstantType type, DebugInfo dInfo) {
        super(BaseNode.NodeType.CONSTANT, dInfo);
        this.constantType = type.ordinal();
    }
    
    public ConstantType constantType() {
        return ConstantType.values()[this.constantType];
    }
    
    public final int constantType;
}
