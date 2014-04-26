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
public class DoubleConstantLeaf extends ConstantLeaf<Double> {
    public DoubleConstantLeaf(double value, Position position) {
        super(ConstantType.DOUBLE, value, position);
    }
    public DoubleConstantLeaf(double value, DebugInfo dInfo) {
        super(ConstantType.DOUBLE, value, dInfo);
    }
}
