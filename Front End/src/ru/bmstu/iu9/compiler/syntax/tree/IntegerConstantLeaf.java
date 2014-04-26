package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
final public class IntegerConstantLeaf extends ConstantLeaf<Integer> {
    public IntegerConstantLeaf(int value, Position position) {
        super(ConstantType.INT, value, position);
    }
    public IntegerConstantLeaf(int value, DebugInfo dInfo) {
        super(ConstantType.INT, value, dInfo);
    }
}
