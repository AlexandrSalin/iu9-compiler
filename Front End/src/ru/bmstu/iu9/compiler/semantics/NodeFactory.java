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
import java.util.LinkedList;
import java.util.List;
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
                        registerTypeAdapter(
                            FunctionType.Argument.class,
                            new GeneralizedArgument.ArgumentInstanceCreator()).
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
            case VARS_DECL:
                type = getType(node.type);
                
                return new VariableDeclNode(node.name, type, node.position);
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
            case FUNCTION_DECL:
                node1 = processNode(node.node1);
                
                return new FunctionDeclNode(node.name, getType(node.type), 
                        (BlockNode)node1, node.position);
            case STRUCT_DECL:
                node1 = processNode(node.node1);
                
                return new StructDeclNode(node.name, 
                        new StructType(node.name, false), (BlockNode)node1,
                        node.position);
            case INVALID:
                return new InvalidNode(node.position);
            case CALL:
                node1 = processNode(node.node1);
                node2 = processNode(node.node2);
                
                return new CallNode(node1, (BlockNode)node2, node.position);
            case NO_OPERAND_OPERATION:
                return new NoOperandOperationNode(
                        NoOperandOperationNode.Operation.values()[node.operation],
                        node.position);
            default:
                return new InvalidNode(node.position);
        }
    }
    
    private Type getType(GeneralizedType type) {
        switch (Type.Typename.values()[type.typename]) {
            case PRIMITIVE_TYPE:
                return new PrimitiveType(PrimitiveType.Typename.values()[type.primitive], 
                        type.constancy);
            case STRUCT:
                return new StructType(type.name, type.constancy, type.size);
            case FUNCTION:
                Type returnType = getType(type.type);
                List<FunctionType.Argument> args = 
                        new LinkedList<FunctionType.Argument>();
                
                for (int i = 0; i < type.arguments.length; ++i)
                    args.add(new FunctionType.Argument(type.arguments[i].name,
                            getType(type.arguments[i].type), type.arguments[i].position));
                
                return new FunctionType(returnType, args, type.constancy);
            case ARRAY:
                Type elementType = getType(type.type);
                
                return new ArrayType(elementType, type.length, type.constancy);
            case POINTER:
                Type pointerType = getType(type.type);
                
                return new PointerType(pointerType, type.constancy);
            default:
                return new InvalidType();
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
        
        private Integer primitive;
        private Integer typename;
        private boolean constancy;
        private GeneralizedType type;
        private Integer length;
        private GeneralizedArgument[] arguments;
        private String name;
        private long size;
        
        public static class TypeInstanceCreator implements InstanceCreator<GeneralizedType> {
            @Override
            public GeneralizedType createInstance(java.lang.reflect.Type type) {
                return new GeneralizedType();
            }
        }
    }
    private static class GeneralizedArgument {
        private GeneralizedArgument() { }
        
        private String name;
        private GeneralizedType type;
        private Position position; 
        
        public static class ArgumentInstanceCreator implements InstanceCreator<GeneralizedArgument> {
            @Override
            public GeneralizedArgument createInstance(java.lang.reflect.Type type) {
                return new GeneralizedArgument();
            }
        }
    }
    
    private GeneralizedNode node;
}
