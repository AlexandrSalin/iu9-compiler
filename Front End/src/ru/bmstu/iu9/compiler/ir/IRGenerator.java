/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.bmstu.iu9.compiler.ir;

import ru.bmstu.iu9.compiler.*;
import ru.bmstu.iu9.compiler.syntax.tree.*;

/**
 *
 * @author maggot
 */
public class IRGenerator {
    public IRGenerator(BlockNode<BaseNode> parseTree) {
        this.parseTree = parseTree;
    }
    
    public Code generate(BaseNode node) {
        switch(node.nodeType()) {
            case BINARY_OPERATION:
            {
                BinaryOperationNode b = (BinaryOperationNode)node;
                
                switch(b.operation()) {
                    case MEMBER_SELECT:
                    {
                        Code struct = generate(b.leftChild());
                        //Code field = generate(b.rightChild());
                        
                        TmpVariable tmp = new TmpVariable(
                                new PointerType(b.leftChild().realType(), true)
                            );
                        
                        Statement stmt = 
                            new RefStatement(
                                tmp,
                                
                            );
                        break;
                    }
                }
                
                break;
            }
            default:
            {
                break;
            }
        }
        return null;
    }
    
    private final BlockNode<BaseNode> parseTree;
}
