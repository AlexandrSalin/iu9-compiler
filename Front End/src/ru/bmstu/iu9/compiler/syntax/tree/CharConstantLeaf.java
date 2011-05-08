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
public class CharConstantLeaf extends ConstantLeaf {
    public CharConstantLeaf(int value, Position position) {
        super(ConstantType.CHAR, position);
        this.value = value;
    }
    public CharConstantLeaf(int value, DebugInfo dInfo) {
        super(ConstantType.CHAR, dInfo);
        this.value = value;
    }
    
    @Override
    public String toString() {
        return "'" + Character.toChars(this.value) + "'";
    }
    
    public final int value;
}