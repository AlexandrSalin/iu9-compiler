/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.semantics;

import ru.bmstu.iu9.compiler.*;
import ru.bmstu.iu9.compiler.syntax.CompositeNode;
import ru.bmstu.iu9.compiler.syntax.Leaf;
import ru.bmstu.iu9.compiler.syntax.Logger;
import ru.bmstu.iu9.compiler.syntax.TreeNode;

/**
 *
 * @author maggot
 */
class SemanticAnalyser {
    public SemanticAnalyser(TreeNode parseTree) {
        this.parseTree = parseTree;
    }
    
    private Type processCompositeNode(CompositeNode node) {
        for(TreeNode child : node.children()) {
            if (child instanceof Leaf) {
                processLeaf((Leaf)child);
            } else if (child instanceof CompositeNode) {
                processCompositeNode((CompositeNode)child);
            } else { // InvalidNode
                return null;
            }
        }
        
        if (node.operation().is(CompositeNode.Operation.Bitwise)) {
            checkTypes(node.children().get(0).type(), Type.Typename.INT);
            checkTypes(node.children().get(1).type(), Type.Typename.INT);
            
            node.setType(new PrimitiveType(Type.Typename.INT, true));
        } 
        if (node.operation().is(CompositeNode.Operation.Assignment)) {
            TreeNode lhv = node.children().get(0);
            TreeNode rhv = node.children().get(1);
            
            if (checkTypes(lhv.type(), Type.Typename.PrimitiveType)) {
                if (((PrimitiveType)lhv.type()).isConstant()) {
                    Logger.log("LHV type is constant", null);
                }
            }
            checkTypes(lhv.type(), rhv.type());
        }
        
        return node.type();
    }
    
    private Type processLeaf(Leaf leaf) {
        return leaf.type();
    }
    
    private boolean checkTypes(Type found, Type required) {
        boolean result;
        if (result = !found.equals(required))
            Logger.logIncompatibleTypes(found, required, null);
        return !result;
    }
    private boolean checkTypes(Type found, Type.Typename required) {
        boolean result;
        if (result = !found.Typename().is(required))
            Logger.logIncompatibleTypes(found, required, null);
        return !result;
    }
    private boolean checkTypes(Type found, Type.Typename[] required) {
        boolean result = true;
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < required.length; ++i) {
            if (i > 0)
                str.append(" or ");
            result = result && (!found.Typename().is(required[i]));
            str.append(required[i]);
        }
        if (result)
            Logger.logIncompatibleTypes(found, str.toString(), null);
        
        return !result;
    }
    
    private TreeNode parseTree;
}
