package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
final public class BoolConstantLeaf extends ConstantLeaf {
    public BoolConstantLeaf(boolean value, Position position) {
        super(ConstantType.BOOL, position);
        this.value = value;
    }
    public BoolConstantLeaf(boolean value, DebugInfo dInfo) {
        super(ConstantType.BOOL, dInfo);
        this.value = value;
    }
    
    @Override
    public String toString() {
        return value ? "true" : "false";
    }
    
    public final boolean value;
}
