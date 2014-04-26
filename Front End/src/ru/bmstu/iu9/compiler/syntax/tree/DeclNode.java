/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.syntax.tree;

import ru.bmstu.iu9.compiler.*;
import ru.bmstu.iu9.compiler.ir.type.BaseType;

/**
 *
 * @author maggot
 */
abstract public class DeclNode extends BaseNode implements Statement {
    protected DeclNode(
            NodeType nodeType, 
            String name, 
            BaseTypeNode type,
            Position position) {
        
        super(nodeType);
        this.name = name;
        this.type = type;
        this.dInfo = new DebugInfo(position);
    }
    protected DeclNode(
            NodeType nodeType, 
            String name, 
            BaseTypeNode type,
            DebugInfo dInfo) {
        
        super(nodeType);
        this.name = name;
        this.type = type;
        this.dInfo = dInfo;
    }
    
    public BaseType realType() { return this.realType; }
    public void setRealType(BaseType type) { this.realType = type; }
    
    protected BaseType realType;
    public final DebugInfo dInfo;    
    public final String name;
    public final BaseTypeNode type;
}
