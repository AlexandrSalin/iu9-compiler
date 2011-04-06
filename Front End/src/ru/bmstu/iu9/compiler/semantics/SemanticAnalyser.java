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
                "C:\\Users\\maggot\\Documents\\NetBeansProjects\\ru.bmstu.iu9.compiler\\Front End\\src\\parse_tree.json");
        
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
                    checkTypes(bNode.type(), Type.Typename.INT, bNode.position());
                }
                
                if (bNode.operation().is(BinaryOperationNode.Operation.Comparison)) {
                    node.setType(new PrimitiveType(Type.Typename.BOOL, true));
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
                                new Type.Typename[] {
                                    Type.Typename.INT, Type.Typename.CHAR
                                }, uNode.position());
                        
                        node.setType(uNode.child().type());
                        break;
                    case MINUS:
                    case PLUS:
                        checkTypes(uNode.child().type(),
                                new Type.Typename[] {
                                    Type.Typename.INT, Type.Typename.DOUBLE,
                                    Type.Typename.FLOAT
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
                        checkTypes(uNode.child().type(), 
                                currentFunction.returnValueType(), uNode.position());
                        
                        node.setType(((UnaryOperationNode)node).child().type());
                        break;
                    case LOCK:
                        break;
                }
                break;
                
                
            case DECLARATION:
                DeclarationLeaf leaf = (DeclarationLeaf)node;
                if(leaf.type().Typename().is(
                        new Type.Typename[] {
                            Type.Typename.PrimitiveType, Type.Typename.POINTER,
                            Type.Typename.ARRAY
                        })) {
                    SymbolTable newScope = new SymbolTable();
                    newScope.setOpenScope(currentScope);
                    currentScope = newScope;
                    
                    currentScope.add(new VariableSymbol(leaf.name(), leaf.type()));
                } else if (leaf.type().Typename().is(Type.Typename.FUNCTION)) {
                    ambientScope = currentScope;
                    currentScope = new SymbolTable();
                    
                    ambientScope.add(
                        new FunctionSymbol(leaf.name(), leaf.type(), ambientScope, currentScope));
                } else if (leaf.type().Typename().is(Type.Typename.STRUCT)) {
                    ambientScope = currentScope;
                    currentScope = new SymbolTable();
                    
                    ambientScope.add(
                        new StructSymbol(leaf.name(), leaf.type(), ambientScope, currentScope));
                }
                break;
            case FOR:
                ForNode forNode = (ForNode)node;
                
                processNode(forNode.initialization());
                processNode(forNode.condition());
                processNode(forNode.increase());
                processNode(forNode.block());
                
                checkTypes(forNode.condition().type(), Type.Typename.BOOL,
                        ((ExpressionNode)forNode.condition()).position());
                break;
            case IF:
                IfNode ifNode = (IfNode)node;
                
                processNode(ifNode.expression());
                processNode(ifNode.block());
                processNode(ifNode.elseBlock());
                
                checkTypes(ifNode.expression().type(), Type.Typename.BOOL, 
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
                
                checkTypes(cbNode.expression().type(), Type.Typename.BOOL,
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
            case FUNCTION:
                // starts new symbol table
                processNode(((FunctionNode)node).declaration());
                processNode(((FunctionNode)node).arguments());
                
                this.currentFunction = (FunctionType)((FunctionNode)node).declaration().type();
                processNode(((FunctionNode)node).block());
                // restore symbol table
                currentScope = ambientScope;
                break;
            case STRUCT:
                // starts new symbol table
                processNode(((StructNode)node).declaration());
                processNode(((StructNode)node).declarations());
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
                    FunctionType function = (FunctionType)((CallNode)node).function().type();
                    
                    if (function.argumentsTypes().length == ((CallNode)node).arguments().children().size()) {
                        for (int i = 0; 
                             i < function.argumentsTypes().length;
                             ++i) {
                            checkTypes(function.argumentsTypes()[i], call.arguments().children().get(i).type(), call.position());
                        }
                    }
                }
                
                node.setType(((FunctionType)((CallNode)node).function().type()).returnValueType());
            case NO_OPERAND_OPERATION:
                break;
        }
    }
    
    private boolean checkTypes(Type found, Type required, Position position) {
        boolean result;
        if (result = !found.equals(required))
            Logger.logIncompatibleTypes(found, required, position);
        return !result;
    }
    private boolean checkTypes(Type found, Type.Typename required, Position position) {
        boolean result;
        if (result = !found.Typename().is(required))
            Logger.logIncompatibleTypes(found, required, position);
        return !result;
    }
    private boolean checkTypes(Type found, Type.Typename[] required, Position position) {
        boolean result = true;
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < required.length; ++i) {
            if (i > 0)
                str.append(" or ");
            result = result && (!found.Typename().is(required[i]));
            str.append(required[i]);
        }
        if (result)
            Logger.logIncompatibleTypes(found, str.toString(), position);
        
        return !result;
    }
    
    private Node parseTree;
    private FunctionType currentFunction;
    private SymbolTable currentScope = new SymbolTable();
    private SymbolTable ambientScope;
}
