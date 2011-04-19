package ru.bmstu.iu9.compiler.semantics;

import ru.bmstu.iu9.compiler.*;
import ru.bmstu.iu9.compiler.syntax.tree.*;

/**
 *
 * @author maggot
 */
class SemanticAnalyser {
    public SemanticAnalyser(Node parseTree) {
        this.parseTree = parseTree;
    }
    
    public void Analyse() {
        processNode(parseTree);
    }
    public static void main(String[] args) {
        NodeFactory tree = new NodeFactory(
                "C:\\Users\\maggot\\Documents\\NetBeansProjects\\ru.bmstu.iu9.compiler\\Front End\\src\\parse_tree.json" );
        
        SemanticAnalyser analyser = new SemanticAnalyser(tree.getTree());
        
        analyser.Analyse();
    }
    
    private void processNode(Node node) {
        switch (node.nodeType()) {
            case BINARY_OPERATION:
                BinaryOperationNode bNode = (BinaryOperationNode)node;
                
                processNode(bNode.leftChild());
                processNode(bNode.rightChild());
                
                checkTypes(bNode.leftChild().type(), bNode.rightChild().type(),
                        bNode.position());
                if (bNode.operation().is(BinaryOperationNode.Operation.Bitwise)) {
                    checkTypes(bNode.type(), PrimitiveType.Typename.INT, bNode.position());
                }
                
                if (bNode.operation().is(BinaryOperationNode.Operation.Comparison)) {
                    node.setType(new PrimitiveType(PrimitiveType.Typename.BOOL, true));
                } else {
                    node.setType(((BinaryOperationNode)node).leftChild().type());
                }
                break;
                
                
            case UNARY_OPERATION:
                UnaryOperationNode uNode = ((UnaryOperationNode)node);
                
                processNode(uNode.child());
                switch (uNode.operation()) {
                    case POST_INC:
                    case POST_DEC:
                    case PRE_INC:
                    case PRE_DEC:
                        checkTypes(uNode.child().type(), 
                                new PrimitiveType.Typename[] {
                                    PrimitiveType.Typename.INT, PrimitiveType.Typename.CHAR
                                }, uNode.position());
                        
                        node.setType(uNode.child().type());
                        break;
                    case MINUS:
                    case PLUS:
                        checkTypes(uNode.child().type(),
                                new PrimitiveType.Typename[] {
                                    PrimitiveType.Typename.INT, 
                                    PrimitiveType.Typename.DOUBLE,
                                    PrimitiveType.Typename.FLOAT
                                }, uNode.position());
                        
                        node.setType(uNode.child().type());
                        break;
                    case DEREF:
                        checkTypes(uNode.child().type(), Type.Typename.POINTER, 
                                uNode.position());
                        
                        node.setType(((PointerType)uNode.child().type()).type());
                        break;
                    case CAST:
                        break;
                    case REF:
                        node.setType(new PointerType(uNode.child().type(), true));
                        break;
                    case RETURN:
                        checkTypes(uNode.child().type(), returnType, uNode.position());
                        
                        node.setType(((UnaryOperationNode)node).child().type());
                        break;
                    case LOCK:
                        break;
                }
                break;
                
                
            case VARS_DECL:
                VariableDeclNode leaf = (VariableDeclNode)node;

                SymbolTable newScope = new SymbolTable();
                newScope.setOpenScope(currentScope);
                currentScope = newScope;

                for (VariableDeclNode.Variable var : leaf)                    
                    currentScope.add(new VariableSymbol(
                            var.name(), 
                            analyseType(var.type())
                        ));
                break;
            case FOR:
                ForNode forNode = (ForNode)node;
                
                processNode(forNode.initialization());
                processNode(forNode.condition());
                processNode(forNode.increase());
                processNode(forNode.block());
                
                checkTypes(forNode.condition().type(), PrimitiveType.Typename.BOOL,
                        ((ExpressionNode)forNode.condition()).position());
                break;
            case IF:
                IfNode ifNode = (IfNode)node;
                
                processNode(ifNode.expression());
                processNode(ifNode.block());
                processNode(ifNode.elseBlock());
                
                checkTypes(ifNode.expression().type(), PrimitiveType.Typename.BOOL, 
                        ((ExpressionNode)ifNode.expression()).position());
                break;
            case SWITCH:
                SwitchNode switchNode = ((SwitchNode)node);
                        
                processNode(switchNode.expression());
                processNode(switchNode.cases());
                processNode(switchNode.defaultNode());
                
                for (Node caseNode : ((SwitchNode)node).cases()) {
                    checkTypes(switchNode.expression().type(), caseNode.type(), 
                            ((ExpressionNode)switchNode.expression()).position());
                }
                break;
            case CASE:
                processNode(((CaseNode)node).expression());
                processNode(((CaseNode)node).block());
                break;
            case WHILE:
            case DO_WHILE:
                ConditionBlockNode cbNode = (ConditionBlockNode)node;
                
                processNode(cbNode.expression());
                processNode(cbNode.block());
                
                checkTypes(cbNode.expression().type(), PrimitiveType.Typename.BOOL,
                        ((ExpressionNode)cbNode.expression()).position());
                break;
            case BLOCK:
                for(Node child : (BlockNode)node) {
                    processNode(child);
                }
                break;
            case VARIABLE:
                VariableLeaf var = (VariableLeaf)node;
                Symbol symbol = currentScope.get(var.name());
                
                if (symbol != null) {
                    node.setType(symbol.type());
                } else {
                    Logger.logUndeclaredVarialbe(var.name(), var.position());
                }
                break;
            case CONSTANT:
                break;
            case FUNCTION_DECL:
                FunctionDeclNode function = (FunctionDeclNode)node;
                
                ambientScope = currentScope;
                currentScope = new SymbolTable();
                                
                this.returnType = ((FunctionType)function.type()).returnType();
                ambientScope.add(new FunctionSymbol(function.name(), 
                        function.type(), ambientScope, currentScope));
                for (FunctionType.Argument arg : ((FunctionType)function.type()).argumentsIterator()) {
                    currentScope.add(new VariableSymbol(arg.name(), arg.type()));
                }
                
                processNode(((FunctionDeclNode)node).block());
                // restore symbol table
                currentScope = ambientScope;
                break;
            case STRUCT_DECL:
                ambientScope = currentScope;
                currentScope = new SymbolTable();
                
                processNode(((StructDeclNode)node).declarations());
                
                long size = 0;
                for(Symbol s : currentScope) {
                    size += s.type().size();
                } 
                
                ambientScope.add(new StructSymbol(((StructDeclNode)node).name(),
                        new StructType(((StructDeclNode)node).name(), size),
                        ambientScope, currentScope));
                // restore symbol table
                currentScope = ambientScope;
                break;
            case INVALID:
                break;
            case CALL:
                CallNode call = (CallNode)node;
                
                processNode(call.function());
                processNode(call.arguments());
                
                if (checkTypes(call.function().type(), Type.Typename.FUNCTION, call.position())) {
                    FunctionType func = (FunctionType)((CallNode)node).function().type();
                    
                    if (func.arguments().length == ((CallNode)node).arguments().children().size()) {
                        for (int i = 0; i < func.arguments().length; ++i) {
                            checkTypes(
                                    func.arguments()[i].type(), 
                                    call.arguments().children().get(i).type(), 
                                    call.position());
                        }
                    }
                }
                
                node.setType(((FunctionType)((CallNode)node).function().type()).returnType());
            case NO_OPERAND_OPERATION:
                break;
        }
    }
    
    private Type analyseType(Type type) {
        switch (type.typename()) {
            case PRIMITIVE_TYPE:
                switch (((PrimitiveType)type).primitive()) {
                    case POINTER:
                        PointerType pointer = (PointerType)type;
                        pointer.setType(analyseType(pointer.type()));
                        return pointer;
                    default:
                        return type;
                }
            case STRUCT:
                Symbol struct = currentScope.get(((StructType)type).name());
                if (struct != null && struct instanceof StructSymbol) {
                    return struct.type();
                } else {
                    Logger.logUndeclaredType(((StructType)type).name(), null);
                    return new InvalidType();
                }
            case FUNCTION:
                FunctionType function = (FunctionType)type;
                
                function.setReturnType(analyseType(function.returnType()));
                for (FunctionType.Argument arg : function.argumentsIterator()) {
                    arg.setType(analyseType(arg.type()));
                }
                return function;
            case ARRAY:
                ArrayType array = (ArrayType)type;
                array.setElementType(analyseType(array.elementType()));
                return array;
            default:
                return type;
        }
    }
    
    private boolean checkTypes(Type found, Type required, Position position) {
        boolean result;
        if (result = (found == null || !found.equals(required)))
            Logger.logIncompatibleTypes(found.toString(), required.toString(), position);
        return !result;
    }
    private boolean checkTypes(Type found, Type.Typename required, Position position) {
        boolean result;
        if (result = (found == null || !found.typename().is(required)))
            Logger.logIncompatibleTypes(found.toString(), required.name(), position);
        return !result;
    }
    private boolean checkTypes(Type found, PrimitiveType.Typename required, Position position) {
        boolean result;
        if (result = (found == null || !(found instanceof PrimitiveType) || 
                !(((PrimitiveType)found).primitive() == required)))
            Logger.logIncompatibleTypes(found.toString(), required.name(), position);
        return !result;
    }
    private boolean checkTypes(Type found, Type.Typename[] required, Position position) {
        boolean result = (found == null || !found.typename().is(required));
        if (result) {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < required.length; ++i) {
                str.append(required[i]);
                str.append(" or ");
            }
            str.delete(str.length() - 4, str.length());
            Logger.logIncompatibleTypes(found.toString(), str.toString(), position);
        }
        
        return !result;
    }
    private boolean checkTypes(Type found, PrimitiveType.Typename[] required, Position position) {
        boolean result = (found == null || !(found instanceof PrimitiveType) ||
                !((PrimitiveType)found).primitive().is(required));
        if (result) {
            StringBuilder str = new StringBuilder();
            for (int i = 0; i < required.length; ++i) {
                str.append(required[i]);
                str.append(" or ");
            }
            str.delete(str.length() - 4, str.length());
            Logger.logIncompatibleTypes(found.toString(), str.toString(), position);
        }
        
        return !result;
    }
    
    private Node parseTree;
    private Type returnType;
    private SymbolTable currentScope = new SymbolTable();
    private SymbolTable ambientScope;
}
