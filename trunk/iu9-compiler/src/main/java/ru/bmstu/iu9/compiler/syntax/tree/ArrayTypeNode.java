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
public class ArrayTypeNode extends BaseTypeNode {
    public ArrayTypeNode(
            BaseTypeNode elementType, 
            IntegerConstantLeaf length, 
            boolean constancy,
            Position position) {
        
        super(Type.ARRAY, constancy, position);
        this.element = elementType;
        this.length = length;
    }
    public ArrayTypeNode(
            BaseTypeNode elementType, 
            IntegerConstantLeaf length, 
            boolean constancy,
            DebugInfo dInfo) {
        
        super(Type.ARRAY, constancy, dInfo);
        this.element = elementType;
        this.length = length;
    }
    public ArrayTypeNode(
            BaseTypeNode elementType, 
            boolean constancy,
            Position position) {
        
        super(Type.ARRAY, constancy, position);
        this.element = elementType;
        this.length = null;
    }
    public ArrayTypeNode(
            BaseTypeNode elementType, 
            boolean constancy,
            DebugInfo dInfo) {
        
        super(Type.ARRAY, constancy, dInfo);
        this.element = elementType;
        this.length = null;
    }
    
    @Override
    public String toString() {
        return "(" + super.toString() + " OF " + element.toString() + 
                ")[" + length.toString() + "]";
    }
    
    public final IntegerConstantLeaf length;
    public final BaseTypeNode element;
    /*public ArrayTypeNode(
            BaseTypeNode<?> elementType, 
            Integer length,
            boolean constancy,
            Position position) {
        
        super(new DebugInfo(position), 
              new ArrayType(elementType.decoratedType, 
              length, 
              constancy));
    }
    public ArrayTypeNode(
            BaseTypeNode<?> elementType, 
            boolean constancy, 
            Position position) {
        
        this(elementType, null, constancy, position);
    }
    public ArrayTypeNode(ArrayType type, DebugInfo dInfo) {
        super(dInfo, type);
    }
   
    public Integer length() { 
        return this.decoratedType.length; 
    }
    public BaseType elementType() { 
        return this.decoratedType.elementType; 
    }*/
}
