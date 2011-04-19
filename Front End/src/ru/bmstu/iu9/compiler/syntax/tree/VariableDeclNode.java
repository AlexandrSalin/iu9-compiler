package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.Position;
import ru.bmstu.iu9.compiler.Type;

/**
 *
 * @author maggot
 */
final public class VariableDeclNode extends DeclNode { 
    public VariableDeclNode(String name, Type type, Position position) {
        super(NodeType.VARS_DECL, name, type, position);
    }
}
