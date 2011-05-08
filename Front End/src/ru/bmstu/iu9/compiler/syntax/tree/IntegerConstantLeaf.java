package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
final public class IntegerConstantLeaf extends ConstantLeaf {
    public IntegerConstantLeaf(int value, Position position) {
        super(ConstantType.INT, position);
        this.value = value;
    }
    public IntegerConstantLeaf(int value, DebugInfo dInfo) {
        super(ConstantType.INT, dInfo);
        this.value = value;
    }
    
    @Override
    public String toString() {
        return Integer.toString(this.value);
    }
    
    public final int value;
}
