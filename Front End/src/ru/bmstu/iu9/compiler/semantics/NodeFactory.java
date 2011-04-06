/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.semantics;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import java.io.BufferedReader;
import java.io.FileReader;
import ru.bmstu.iu9.compiler.*;
import ru.bmstu.iu9.compiler.Type;
import ru.bmstu.iu9.compiler.syntax.tree.*;

/**
 *
 * @author maggot
 */
public class NodeFactory {
    public NodeFactory(String filename) {
        BufferedReader reader = null;
        
        try {
            Gson gson = 
                    new GsonBuilder().
                        registerTypeAdapter(
                            GeneralizedNode.class, 
                            new GeneralizedNode.NodeInstanceCreator()).
                        registerTypeAdapter(
                            Type.class,
                            new GeneralizedType.TypeInstanceCreator()).
                        create();
            
            reader = new BufferedReader(
                        new FileReader(filename));
            
            node = gson.fromJson(reader, GeneralizedNode.class);
        } catch(java.io.IOException ex) {
//            ex.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch(java.io.IOException ex) {
//                ex.printStackTrace();
            }
        }
    }
    
    public Node getTree() {
        return processNode(node);
    }
    
    public static void main(String[] args) {
        NodeFactory factory = new NodeFactory(
                "C:\\Users\\maggot\\Documents\\NetBeansProjects\\ru.bmstu.iu9.compiler\\Front End\\src\\parse_tree.json");
        
        Node parseTree = factory.getTree();
        
        return;
    }
    
    private Node processNode(GeneralizedNode node) {
        Node node1, node2, node3, node4;
        Type type;
        
        switch (Node.NodeType.values()[node.nodeType]) {
            case BINARY_OPERATION:
                node1 = processNode(node.node1);
                node2 = processNode(node.node2);
                
                return new BinaryOperationNode(
                        BinaryOperationNode.Operation.values()[node.operation], 
                        node1, node2, node.position);
            case UNARY_OPERATION:
                node1 = processNode(node.node1);
                type = getType(node.type);
                
                return new UnaryOperationNode(
                        UnaryOperationNode.Operation.values()[node.operation],
                        type, node1, node.position);
            case DECLARATION:
                type = getType(node.type);
                
                return new DeclarationLeaf(node.name, type, node.position);
            case FOR:
                node1 = processNode(node.node1);
                node2 = processNode(node.node2);
                node3 = processNode(node.node3);
                node4 = processNode(node.node4);
                
                return new ForNode(node1, node2, node3, node4);
            case IF:
                node1 = processNode(node.node1);
                node2 = processNode(node.node2);
                node3 = processNode(node.node3);
                
                return new IfNode(node1, node2, node3);
            case SWITCH:
                node1 = processNode(node.node1);
                node2 = processNode(node.node2);
                node3 = processNode(node.node3);
                
                return new SwitchNode(node1, (BlockNode)node2, (BlockNode)node3);
            case CASE:
                node1 = processNode(node.node1);
                node2 = processNode(node.node2);
                
                return new CaseNode(node1, node2);
            case WHILE:
                node1 = processNode(node.node1);
                node2 = processNode(node.node2);
                
                return new WhileNode(node1, node2);
            case DO_WHILE:
                node1 = processNode(node.node1);
                node2 = processNode(node.node2);
                
                return new DoWhileNode(node1, node2);
            case BLOCK:
                Node[] block = new Node[node.nodes.length];
                for (int i = 0; i < node.nodes.length; ++i) {
                    block[i] = processNode(node.nodes[i]);
                }
                
                return new BlockNode(block);
            case VARIABLE:
                return new VariableLeaf(node.name, node.position);
            case CONSTANT:
                type = getType(node.type);
                
                return new ConstantLeaf(node.value, type, node.position);
            case FUNCTION:
                node1 = processNode(node.node1);
                node2 = processNode(node.node2);
                node3 = processNode(node.node3);
                
                return new FunctionNode(
                        (DeclarationLeaf)node1, (BlockNode)node2, (BlockNode)node3);
            case STRUCT:
                node1 = processNode(node.node1);
                node2 = processNode(node.node2);
                
                return new StructNode((DeclarationLeaf)node1, (BlockNode)node2);
            case INVALID:
                return new InvalidNode();
            case CALL:
                node1 = processNode(node.node1);
                node2 = processNode(node.node2);
                
                return new CallNode(node1, (BlockNode)node2, node.position);
            case NO_OPERAND_OPERATION:
                return new NoOperandOperationNode(
                        NoOperandOperationNode.Operation.values()[node.operation]);
            default:
                return new InvalidNode();
        }
    }
    
    private Type getType(GeneralizedType type) {
        if(type == null) 
            return null;
        switch (Type.Typename.values()[type.typename]) {
            case INT:
            case BOOL:
            case DOUBLE:
            case FLOAT:
            case CHAR:
            case VOID:
                return new PrimitiveType(Type.Typename.values()[type.typename], 
                        type.isConstant);
            case STRUCT:
                return new StructType(type.name);
            case FUNCTION:
                Type[] arguments = new Type[type.arguments.length];
                for (int i = 0; i < arguments.length; ++i) {
                    arguments[i] = getType(type.arguments[i]);
                }
                Type returnType = getType(type.type);
                
                return new FunctionType(returnType, arguments);
            case ARRAY:
                Type elementType = getType(type.type);
                
                return new ArrayType(elementType, type.length);
            case POINTER:
                Type pointerType = getType(type.type);
                
                return new PointerType(pointerType, type.isConstant);
            default:
                return null;
        }
    }

    private static class GeneralizedNode {
        private GeneralizedNode() { }
        
        private GeneralizedType type;
        private Integer operation;
        private GeneralizedNode node1;
        private GeneralizedNode node2;
        private GeneralizedNode node3;
        private GeneralizedNode node4;
        private GeneralizedNode[] nodes;
        private Object value;
        private String name;
        private Integer nodeType;
        private Position position;
        
        public static class NodeInstanceCreator implements InstanceCreator<GeneralizedNode> {
            @Override
            public GeneralizedNode createInstance(java.lang.reflect.Type type) {
                return new GeneralizedNode();
            }
        }
    }
    
    private static class GeneralizedType {
        private GeneralizedType() { }
        
        private Integer typename;
        private boolean isConstant;
        private GeneralizedType type;
        private Integer length;
        private GeneralizedType[] arguments;
        private String name;
        
        public static class TypeInstanceCreator implements InstanceCreator<GeneralizedType> {
            @Override
            public GeneralizedType createInstance(java.lang.reflect.Type type) {
                return new GeneralizedType();
            }
        }
    }
    
    private GeneralizedNode node;
}
