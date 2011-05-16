package ru.bmstu.iu9.compiler.syntax.tree;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.List;
import ru.bmstu.iu9.compiler.*;

/**
 *
 * @author maggot
 */
public class BaseNode {
    public enum NodeType { 
        BINARY_OPERATION, UNARY_OPERATION, VAR_DECL, ELSE, DEFAULT, RUN,
        FOR, IF, SWITCH, CASE, WHILE, DO_WHILE, BLOCK, VARIABLE,
        CONSTANT, FUNCTION_DECL, STRUCT_DECL, INVALID, CALL, LOCK,
        TYPE, ARGUMENT, BREAK, RETURN, CONTINUE, BARRIER, BLOCK_DECL
    };
    
    protected BaseNode(NodeType nodeType) {
        this.nodeType = nodeType.ordinal();
    }
    
    public NodeType nodeType() {
        return NodeType.values()[this.nodeType]; 
    }
    
    protected int nodeType;
    

    public static class BaseNodeAdapter implements JsonDeserializer<BaseNode> {
        public BaseNode deserialize(
                JsonElement json, 
                Type typeOfNode,
                JsonDeserializationContext context) throws JsonParseException {
            
            JsonObject object = json.getAsJsonObject();
            BaseNode result = null;

            BaseNode.NodeType nodeType = 
                BaseNode.NodeType.values()[
                    (Integer)context.deserialize(object.get("nodeType"), Integer.class)
                ];
            
            JsonObject obj = null;
            JsonPrimitive primitive = null;
            DebugInfo dInfo = null;
            switch(nodeType) {
                case TYPE:
                {
                    obj = object.getAsJsonObject("dInfo");
                    assert obj == null;
                    dInfo = context.deserialize(obj, DebugInfo.class);
                    
                    primitive = object.getAsJsonPrimitive("type");
                    assert primitive == null;
                    BaseTypeNode.Type TYPE = 
                        BaseTypeNode.Type.values()[
                            (Integer)context.deserialize(primitive, Integer.class)
                        ];
                    
                    primitive = object.getAsJsonPrimitive("constancy");
                    assert primitive == null;
                    boolean constancy = 
                            context.deserialize(primitive, Boolean.class);
                    
                    switch(TYPE) {
                        case ARRAY:
                        {
                            obj = object.getAsJsonObject("element");
                            assert obj == null;
                            BaseTypeNode type = 
                                    context.deserialize(
                                        obj, 
                                        BaseTypeNode.class
                                    );
                            
                            obj = object.getAsJsonObject("length");
                            if(obj != null) {
                                IntegerConstantLeaf length = 
                                        context.deserialize(
                                            obj, 
                                            IntegerConstantLeaf.class
                                        );
                                result = 
                                    new ArrayTypeNode(
                                        type, 
                                        length, 
                                        constancy, 
                                        dInfo
                                    );
                            } else {
                                result = 
                                    new ArrayTypeNode(type, constancy, dInfo);
                            }
                            
                            break;
                        }
                        case STRUCT:
                        {
                            primitive = object.getAsJsonPrimitive("name");
                            assert primitive == null;
                            String name = 
                                context.deserialize(primitive, String.class);
                            
                            result = new StructTypeNode(name, constancy, dInfo);
                            break;
                        }
                        case FUNCTION:
                        {
                            obj = object.getAsJsonObject("arguments");
                            assert obj == null;
                            BlockDeclNode arguments = 
                                context.deserialize(obj, BlockDeclNode.class);
                            
                            obj = object.getAsJsonObject("returnValue");
                            assert obj == null;
                            BaseTypeNode returnValue = 
                                context.deserialize(obj, BaseTypeNode.class);
                            
                            result = 
                                new FunctionTypeNode(
                                    returnValue, 
                                    arguments, 
                                    constancy, 
                                    dInfo
                                );
                            
                            break;
                        }
                        case PRIMITIVE_TYPE:
                        {
                            primitive = object.getAsJsonPrimitive("primitive");
                            assert primitive == null;
                            PrimitiveTypeNode.Type t = 
                                PrimitiveTypeNode.Type.values()[
                                    (Integer)context.deserialize(primitive, Integer.class)
                                ];
                            switch(t) {
                                case POINTER:
                                {
                                    obj = object.getAsJsonObject("pointerType");
                                    assert obj == null;
                                    BaseTypeNode type = 
                                        context.deserialize(
                                            obj, 
                                            BaseTypeNode.class
                                        );
                                    
                                    result = 
                                        new PointerTypeNode(
                                            type, 
                                            constancy, 
                                            dInfo
                                        );
                                    break;
                                }
                                default:
                                {
                                    result = 
                                        new PrimitiveTypeNode(
                                            t, 
                                            constancy, 
                                            dInfo
                                        );
                                    break;
                                }
                            }
                            
                            break;
                        }
                        case INVALID:
                            result = BaseTypeNode.InvalidNode(dInfo);
                            break;
                    }
                    
                    break;
                }
                case BINARY_OPERATION:
                {
                    obj = object.getAsJsonObject("dInfo");
                    assert obj == null;
                    dInfo = context.deserialize(obj, DebugInfo.class);
                    
                    obj = object.getAsJsonObject("left");
                    assert obj == null;
                    ExpressionNode left = 
                        context.deserialize(obj, ExpressionNode.class);
                    
                    obj = object.getAsJsonObject("right");
                    assert obj == null;
                    ExpressionNode right = 
                        context.deserialize(obj, ExpressionNode.class);
                    
                    primitive = object.getAsJsonPrimitive("operation");
                    assert primitive == null;
                    BinaryOperationNode.Operation operation = 
                            BinaryOperationNode.Operation.values()[
                                (Integer)context.deserialize(primitive, Integer.class)
                            ];
                    
                    result = 
                        new BinaryOperationNode(operation, left, right, dInfo);
                    
                    break;
                }
                case UNARY_OPERATION:
                {
                    obj = object.getAsJsonObject("dInfo");
                    assert obj == null;
                    dInfo = context.deserialize(obj, DebugInfo.class);
                    
                    obj = object.getAsJsonObject("node");
                    assert obj == null;
                    ExpressionNode node = context.deserialize(obj, ExpressionNode.class);
                                        
                    primitive = object.getAsJsonPrimitive("operation");
                    assert primitive == null;
                    UnaryOperationNode.Operation uoperation = 
                            UnaryOperationNode.Operation.values()[(Integer)context.deserialize(primitive, Integer.class)];
                    
                    switch(uoperation) {
                        case CAST:
                            obj = object.getAsJsonObject("castingType");
                            BaseTypeNode t = context.deserialize(obj, BaseTypeNode.class);
                            result = new CastNode(t, node, dInfo);
                            break;
                        default:
                            result = new UnaryOperationNode(uoperation, node, dInfo);
                            break;
                    }
                    
                    break;
                }    
                case VAR_DECL:
                {
                    obj = object.getAsJsonObject("dInfo");
                    assert obj == null;
                    dInfo = context.deserialize(obj, DebugInfo.class);
                    
                    primitive = object.getAsJsonPrimitive("name");
                    assert primitive == null;
                    String name = context.deserialize(primitive, String.class);
                    
                    obj = object.getAsJsonObject("type");
                    assert obj == null;
                    BaseTypeNode typeNode = 
                        context.deserialize(obj, BaseTypeNode.class);
                    
                    obj = object.getAsJsonObject("value");
                    if(obj != null) {
                        ExpressionNode expr = 
                            context.deserialize(obj, ExpressionNode.class);
                        result = 
                            new VariableDeclNode(name, typeNode, dInfo, expr);
                    } else {
                        result = new VariableDeclNode(name, typeNode, dInfo);
                    }
                    
                    break;
                }    
                case ELSE:
                {
                    obj = object.getAsJsonObject("block");
                    assert obj == null;
                    BlockNode block = context.deserialize(obj, BlockNode.class);
                    
                    obj = object.getAsJsonObject("dInfo");
                    assert obj == null;
                    dInfo = context.deserialize(obj, DebugInfo.class);
                    
                    result = new ElseNode(block, dInfo);
                    
                    break;
                }   
                case DEFAULT:
                {
                    obj = object.getAsJsonObject("block");
                    assert obj == null;
                    BlockNode block = context.deserialize(obj, BlockNode.class);
                    
                    obj = object.getAsJsonObject("dInfo");
                    assert obj == null;
                    dInfo = context.deserialize(obj, DebugInfo.class);
                    
                    result = new DefaultNode(block, dInfo);
                    
                    break;
                }
                case RUN:
                {
                    obj = object.getAsJsonObject("expression");
                    assert obj == null;
                    ExpressionNode expression = 
                            context.deserialize(obj, ExpressionNode.class);
                    
                    obj = object.getAsJsonObject("dInfo");
                    assert obj == null;
                    dInfo = context.deserialize(obj, DebugInfo.class);
                    
                    result = new RunNode(expression, dInfo);
                    
                    break;
                }
                case FOR:
                {
                    obj = object.getAsJsonObject("dInfo");
                    assert obj == null;
                    dInfo = context.deserialize(obj, DebugInfo.class);
                    
                    obj = object.getAsJsonObject("expression");
                    assert obj == null;
                    ExpressionNode expression = 
                            context.deserialize(obj, ExpressionNode.class);
                    
                    obj = object.getAsJsonObject("block");
                    assert obj == null;
                    BlockNode block = context.deserialize(obj, BlockNode.class);
                    
                    obj = object.getAsJsonObject("initialization");
                    assert obj == null;
                    BlockNode initialization = context.deserialize(obj, BlockNode.class);
                    
                    obj = object.getAsJsonObject("step");
                    assert obj == null;
                    BlockNode step = context.deserialize(obj, BlockNode.class);
                    
                    result = new ForNode(initialization, expression, step, block, dInfo);
                    
                    break;
                }
                case IF:
                {
                    obj = object.getAsJsonObject("dInfo");
                    assert obj == null;
                    dInfo = context.deserialize(obj, DebugInfo.class);
                    
                    obj = object.getAsJsonObject("block");
                    assert obj == null;
                    BlockNode block = context.deserialize(obj, BlockNode.class);
                    
                    obj = object.getAsJsonObject("condition");
                    assert obj == null;
                    ExpressionNode condition = 
                            context.deserialize(obj, ExpressionNode.class);
                    
                    obj = object.getAsJsonObject("elseNode");
                    assert obj == null;
                    ElseNode elseNode = context.deserialize(obj, ElseNode.class);
                    
                    result = new IfNode(condition, block, elseNode, dInfo);
                    
                    break;
                }
                case SWITCH:
                {
                    obj = object.getAsJsonObject("dInfo");
                    assert obj == null;
                    dInfo = context.deserialize(obj, DebugInfo.class);
                    
                    obj = object.getAsJsonObject("cases");
                    assert obj == null;
                    BlockNode cases = context.deserialize(obj, BlockNode.class);
                    
                    obj = object.getAsJsonObject("expression");
                    assert obj == null;
                    ExpressionNode expression = 
                            context.deserialize(obj, ExpressionNode.class);
                    
                    obj = object.getAsJsonObject("defaultNode");
                    assert obj == null;
                    DefaultNode defaultNode = context.deserialize(obj, DefaultNode.class);
                    
                    result = new SwitchNode(expression, cases, defaultNode, dInfo);
                    
                    break;
                }
                case CASE:
                {
                    obj = object.getAsJsonObject("dInfo");
                    assert obj == null;
                    dInfo = context.deserialize(obj, DebugInfo.class);
                    
                    obj = object.getAsJsonObject("block");
                    assert obj == null;
                    BlockNode block = context.deserialize(obj, BlockNode.class);
                    
                    obj = object.getAsJsonObject("expression");
                    assert obj == null;
                    ExpressionNode expression = 
                            context.deserialize(obj, ExpressionNode.class);
                    
                    result = new CaseNode(expression, block, dInfo);
                    
                    break;
                }
                case WHILE:
                {
                    obj = object.getAsJsonObject("dInfo");
                    assert obj == null;
                    dInfo = context.deserialize(obj, DebugInfo.class);
                    
                    obj = object.getAsJsonObject("block");
                    assert obj == null;
                    BlockNode block = context.deserialize(obj, BlockNode.class);
                    
                    obj = object.getAsJsonObject("expression");
                    assert obj == null;
                    ExpressionNode expression = 
                            context.deserialize(obj, ExpressionNode.class);
                    
                    result = new WhileNode(expression, block, dInfo);
                    
                    break;
                }
                case DO_WHILE:
                {
                    obj = object.getAsJsonObject("dInfo");
                    assert obj == null;
                    dInfo = context.deserialize(obj, DebugInfo.class);
                    
                    obj = object.getAsJsonObject("block");
                    assert obj == null;
                    BlockNode block = context.deserialize(obj, BlockNode.class);
                    
                    obj = object.getAsJsonObject("expression");
                    assert obj == null;
                    ExpressionNode expression = 
                            context.deserialize(obj, ExpressionNode.class);
                    
                    result = new DoWhileNode(expression, block, dInfo);
                    
                    break;
                }
                case BLOCK:
                {
                    List<BaseNode> children = context.deserialize(
                        object.get("children"), 
                        new TypeToken<List<BaseNode>>() {}.getType());
            
                    result = new BlockNode<BaseNode>(children);
            
                    break;
                }
                case BLOCK_DECL:
                {
                    List<VariableDeclNode> children = context.deserialize(
                        object.get("children"), 
                        new TypeToken<List<VariableDeclNode>>() {}.getType());
            
                    result = new BlockDeclNode(children);
                    
                    break;
                }
                case VARIABLE:
                {
                    obj = object.getAsJsonObject("dInfo");
                    assert obj == null;
                    dInfo = context.deserialize(obj, DebugInfo.class);
                    
                    primitive = object.getAsJsonPrimitive("name");
                    assert primitive == null;
                    String name = context.deserialize(primitive, String.class);
                    
                    result = new VariableLeaf(name, dInfo);
                    
                    break;
                }
                case CONSTANT:
                {
                    obj = object.getAsJsonObject("dInfo");
                    assert obj == null;
                    dInfo = context.deserialize(obj, DebugInfo.class);
                    
                    primitive = object.getAsJsonPrimitive("constantType");
                    assert primitive == null;
                    ConstantLeaf.ConstantType type = 
                        ConstantLeaf.ConstantType.values()[
                            (Integer)context.deserialize(primitive, Integer.class)
                        ];
                    
                    primitive = object.getAsJsonPrimitive("value");
                    assert primitive == null;
                            
                    switch(type) {
                        case INT:
                        {
                            int value = 
                                context.deserialize(primitive, Integer.class);
                            result = new IntegerConstantLeaf(value, dInfo);
                            break;
                        }
                        case DOUBLE:
                        {
                            double value = 
                                context.deserialize(primitive, Double.class);
                            result = new DoubleConstantLeaf(value, dInfo);
                            break;
                        }
                        case CHAR:
                        {
                            int value = 
                                context.deserialize(primitive, Integer.class);
                            result = new CharConstantLeaf(value, dInfo);
                            break;
                        }
                        case BOOL:
                        {
                            boolean value = 
                                context.deserialize(primitive, Boolean.class);
                            result = new BoolConstantLeaf(value, dInfo);
                            break;
                        }
                    }
                    
                    break;
                }
                case FUNCTION_DECL:
                {
                    obj = object.getAsJsonObject("dInfo");
                    assert obj == null;
                    dInfo = context.deserialize(obj, DebugInfo.class);
                    
                    primitive = object.getAsJsonPrimitive("name");
                    assert primitive == null;
                    String name = context.deserialize(primitive, String.class);
                    
                    obj = object.getAsJsonObject("type");
                    assert obj == null;
                    BaseTypeNode typeNode = 
                        context.deserialize(obj, BaseTypeNode.class);
                    
                    obj = object.getAsJsonObject("block");
                    assert obj == null;
                    BlockNode block = context.deserialize(obj, BlockNode.class);
                    
                    result = new FunctionDeclNode(name, typeNode, block, dInfo);
                    
                    break;
                }    
                case STRUCT_DECL:
                {
                    obj = object.getAsJsonObject("dInfo");
                    assert obj == null;
                    dInfo = context.deserialize(obj, DebugInfo.class);
                    
                    primitive = object.getAsJsonPrimitive("name");
                    assert obj == null;
                    String name = context.deserialize(primitive, String.class);
                    
                    obj = object.getAsJsonObject("type");
                    assert obj == null;
                    StructTypeNode typeNode = 
                        context.deserialize(obj, StructTypeNode.class);
                    
                    obj = object.getAsJsonObject("declarations");
                    assert obj == null;
                    BlockNode declarations = 
                        context.deserialize(obj, BlockNode.class);
                    
                    result = 
                        new StructDeclNode(name, typeNode, declarations, dInfo);
                    
                    break;
                }  
                case INVALID:
                {
                    obj = object.getAsJsonObject("dInfo");
                    assert obj == null;
                    dInfo = context.deserialize(obj, DebugInfo.class);
                    
                    result = new InvalidNode(dInfo);
                    
                    break;
                }
                case CALL:
                {
                    obj = object.getAsJsonObject("dInfo");
                    assert obj == null;
                    dInfo = context.deserialize(obj, DebugInfo.class);
                    
                    obj = object.getAsJsonObject("arguments");
                    assert obj == null;
                    BlockNode arguments = 
                        context.deserialize(obj, BlockNode.class);
                    
                    obj = object.getAsJsonObject("function");
                    assert obj == null;
                    ExpressionNode function = 
                            context.deserialize(obj, ExpressionNode.class);
                    
                    result = new CallNode(function, arguments, dInfo);
                    
                    break;
                }
                case LOCK:
                {
                    obj = object.getAsJsonObject("dInfo");
                    assert obj == null;
                    dInfo = context.deserialize(obj, DebugInfo.class);
                    
                    obj = object.getAsJsonObject("block");
                    assert obj == null;
                    BlockNode block = context.deserialize(obj, BlockNode.class);
                    
                    result = new LockNode(block, dInfo);
                    
                    break;
                }
                case ARGUMENT:
                {
                    obj = object.getAsJsonObject("dInfo");
                    assert obj == null;
                    dInfo = context.deserialize(obj, DebugInfo.class);
                    
                    primitive = object.getAsJsonPrimitive("name");
                    assert obj == null;
                    String name = context.deserialize(primitive, String.class);
                    
                    obj = object.getAsJsonObject("type");
                    assert obj == null;
                    BaseTypeNode typeNode = 
                        context.deserialize(obj, BaseTypeNode.class);
                    
                    result = 
                        new FunctionTypeNode.ArgumentNode(name, typeNode, dInfo);
                    
                    break;
                }
                case BREAK:
                {
                    obj = object.getAsJsonObject("dInfo");
                    assert obj == null;
                    dInfo = context.deserialize(obj, DebugInfo.class);
                    
                    result = new BreakNode(dInfo);
                    
                    break;
                }
                case RETURN:
                {
                    obj = object.getAsJsonObject("dInfo");
                    assert obj == null;
                    dInfo = context.deserialize(obj, DebugInfo.class);
                    
                    obj = object.getAsJsonObject("returnExpr");
                    if(obj != null) {
                        ExpressionNode returnExpr = 
                                context.deserialize(obj, ExpressionNode.class);
                        result = new ReturnNode(returnExpr, dInfo);
                    } else {
                        result = new ReturnNode(dInfo);
                    }
                    
                    break;
                }
                case CONTINUE:
                {
                    obj = object.getAsJsonObject("dInfo");
                    assert obj == null;
                    dInfo = context.deserialize(obj, DebugInfo.class);
                    
                    result = new ContinueNode(dInfo);
                    
                    break;
                }
                case BARRIER:
                {
                    obj = object.getAsJsonObject("dInfo");
                    assert obj == null;
                    dInfo = context.deserialize(obj, DebugInfo.class);
                    
                    result = new BarrierNode(dInfo);
                    
                    break;
                }
            }
            
            return result;
        }
    }
}