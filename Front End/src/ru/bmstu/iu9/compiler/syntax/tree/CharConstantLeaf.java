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
public class CharConstantLeaf extends ConstantLeaf<Integer> {
    public CharConstantLeaf(int value, Position position) {
        super(ConstantType.CHAR, value, position);
    }
    public CharConstantLeaf(int value, DebugInfo dInfo) {
        super(ConstantType.CHAR, value, dInfo);
    }
    
    @Override
    public String toString() {
        return "'" + Character.toChars(this.value) + "'";
    }
}