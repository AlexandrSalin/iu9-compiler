/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @param <T> 
 * @author maggot
 */
abstract public class ConstantLeaf<T> extends Leaf {
    public enum ConstantType { INT, CHAR, DOUBLE, BOOL };
    
    protected ConstantLeaf(ConstantType type, T value, Position position) {
        super(BaseNode.NodeType.CONSTANT, position);
        this.value = value;
        this.constantType = type.ordinal();
    }
    protected ConstantLeaf(ConstantType type, T value, DebugInfo dInfo) {
        super(BaseNode.NodeType.CONSTANT, dInfo);
        this.value = value;
        this.constantType = type.ordinal();
    }
    
    public ConstantType constantType() {
        return ConstantType.values()[this.constantType];
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
    
    public final int constantType;
    public final T value;
}
