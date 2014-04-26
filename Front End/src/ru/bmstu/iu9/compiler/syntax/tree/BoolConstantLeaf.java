package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
final public class BoolConstantLeaf extends ConstantLeaf<Boolean> {
    public BoolConstantLeaf(boolean value, Position position) {
        super(ConstantType.BOOL, value, position);
    }
    public BoolConstantLeaf(boolean value, DebugInfo dInfo) {
        super(ConstantType.BOOL, value, dInfo);
    }
}
