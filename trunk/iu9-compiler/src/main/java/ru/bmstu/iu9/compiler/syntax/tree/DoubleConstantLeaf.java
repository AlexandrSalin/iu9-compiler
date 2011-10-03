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
public class DoubleConstantLeaf extends ConstantLeaf {
    public DoubleConstantLeaf(double value, Position position) {
        super(ConstantType.DOUBLE, position);
        this.value = value;
    }
    public DoubleConstantLeaf(double value, DebugInfo dInfo) {
        super(ConstantType.DOUBLE, dInfo);
        this.value = value;
    }
    
    @Override
    public String toString() {
        return Double.toString(this.value);
    }
    
    public final double value;
}
