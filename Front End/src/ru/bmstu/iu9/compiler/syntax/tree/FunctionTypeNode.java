package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.DebugInfo;
import ru.bmstu.iu9.compiler.FunctionType;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
final public class FunctionTypeNode extends BaseTypeNode {
    public static class ArgumentNode extends DeclNode {
        public ArgumentNode(
                String name, 
                BaseTypeNode type, 
                Position position) {
            
            super(NodeType.ARGUMENT, name, type, position);
        }
        public ArgumentNode(
                String name, 
                BaseTypeNode type, 
                DebugInfo dInfo) {
            
            super(NodeType.ARGUMENT, name, type, dInfo);
        }
        
        public FunctionType.Argument toArgument() {
            return new FunctionType.Argument(this.name, this.realType);
        }
        
        @Override
        public String toString() {
            return this.type + " " + this.name;
        }
    }
    
    public FunctionTypeNode(
            BaseTypeNode returnValue, 
            BlockDeclNode<ArgumentNode> arguments,
            boolean constancy,
            Position position) {
        
        super(BaseTypeNode.Type.FUNCTION, constancy, position);
        this.returnValue = returnValue;
        this.arguments = arguments;
    }
    public FunctionTypeNode(
            BaseTypeNode returnValue, 
            BlockDeclNode<ArgumentNode> arguments,
            boolean constancy,
            DebugInfo dInfo) {
        
        super(BaseTypeNode.Type.FUNCTION, constancy, dInfo);
        this.returnValue = returnValue;
        this.arguments = arguments;
    }
    
    @Override
    public String toString() {
        return "FUNCTION (" + arguments + ") " + returnValue;
    }
    
    public final BlockDeclNode<ArgumentNode> arguments;
    public final BaseTypeNode returnValue;
}
