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
final public class VariableLeaf extends Leaf {
    public VariableLeaf(String name, Position position) {
        super(BaseNode.NodeType.VARIABLE, position);
        this.name = name;
    }
    public VariableLeaf(String name, DebugInfo dInfo) {
        super(BaseNode.NodeType.VARIABLE, dInfo);
        this.name = name;
    }
    
    @Override
    public String toString() {
        return this.realType + " " + name;
    }

    public final String name;
}
