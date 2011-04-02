package ru.bmstu.iu9.compiler.syntax;

import ru.bmstu.iu9.compiler.lexis.token.TokenFactory;
import ru.bmstu.iu9.compiler.lexis.token.Token;
import ru.bmstu.iu9.compiler.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;
import ru.bmstu.iu9.compiler.semantics.tree.*;

/**
 *
 * @author maggot
 */
public class Parser {
    public Parser(final Token[] tokens) {
        token = new Iterator<Token>() {
            @Override
            public boolean hasNext() {
                return counter < container.length;
            }

            @Override
            public Token next() {
                ++counter;
                return container[counter];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
            
            {
                container = tokens;
            }
            
            private int counter = -1;
            private Token[] container;
        };
    }
    public Parser(String filename) {
        tokener = new TokenFactory(filename);
        token = tokener.iterator();
    }
    
    public static void main(String[] args) {
        Parser parser = new Parser(
            "C:\\Users\\maggot\\Documents\\NetBeansProjects\\ru.bmstu.iu9.compiler\\Front End\\src\\output.json");
        
//        TreeVisualizer frame = new TreeVisualizer(parser.process());
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setSize(800, 600);
//        frame.setVisible(true);
    }
    
    public BlockNode process() {
        Program();
        return parseTree;
    }
    
    private void nextToken() {
        if (token.hasNext())
            current = token.next();
    }
    
    private TokenFactory tokener;
    private Iterator<Token> token;
    private Token current;
    private BlockNode parseTree = new BlockNode();
    private SymbolTable globalScope = new SymbolTable();
    private SymbolTable currentScope;
    
    private void Program() {
        nextToken();
        Token.Type tag = current.type();
        while (tag.is(new Token.Type[] { 
                    Token.Type.CONST, Token.Type.VAR, 
                    Token.Type.FUNC, Token.Type.STRUCT
                })) {
//            currentScope = globalScope;
            
            if(tag.is(Token.Type.FUNC)) {
                FunctionNode function = new FunctionNode();
                
//                SymbolTable ambientScope = currentScope;
//                currentScope = new SymbolTable();
                
                nextToken();
                Type returnValueType = Type();
                String name = Identifier();                
                Type[] argsTypes = FuncArgList();
//                FunctionSymbol function = new FunctionSymbol(
//                        funcName, new FunctionType(returnValueType, argsTypes), 
//                        ambientScope, currentScope);
                function.setDeclaration(
                        new DeclarationLeaf(
                            name, 
                            new FunctionType(returnValueType, argsTypes)
                        ));
                
//                func.addChild(new VariableLeaf(funcName, function.type()));
                
//                ambientScope.add(function);
//                currentScope.setAssociatedSymbol(function);
                function.setBlock(Code());
//                func.addChild(Code());
//                currentScope = ambientScope;
                
                parseTree.addChild(function);
            } else if (tag.is(Token.Type.STRUCT)) {
                StructDef();
                Semicolon();
            } else {
                parseTree.addChild(VariableDef());
                Semicolon();
            }
            
            tag = current.type();
        }
    }
    
    
    private class Pair {
        public Pair(Type type1, Type type2) {
            this.type1 = type1;
            this.type2 = type2;
        }
        
        public Type type1() { return this.type1; }
        public Type type2() { return this.type2; }
        public void setType2(Type type) { this.type2 = type; }
        
        private Type type1;
        private Type type2;
    }
    
    private Type Type() {
        Type type = PrimitiveType();
        type = AmbientType(type);
        
        return type;
    }
    private Type AmbientType(Type type) {
        if (current.type().is(Token.Type.ASTERISK)) {
            Pair result = Pointer();
            ((PointerType)result.type2()).setType(type);
            type = result.type1();
        }
        Pair innerType = TypeExpr1(new Pair(null, null));
        if (current.type() == Token.Type.LEFT_SQUARE_BRACKET) {
            type = ArrayDim(type);
        }
        
        if (innerType.type1() != null) {
            switch (innerType.type2().Typename()) {
                case FUNCTION:
                    ((FunctionType)innerType.type2()).setReturnValueType(type);
                    break;
                case POINTER:
                    ((PointerType)innerType.type2()).setType(type);
                    break;
                case ARRAY:
                    ((ArrayType)innerType.type2()).setElementType(type);
                    break;
            }
            return innerType.type1();
        } else {
            return type;
        }
    }
    private Pair TypeExpr0(Type type) {
        if (current.type().is(Token.Type.ASTERISK)) {
            Pair pointers = Pointer();
            return TypeExpr1(pointers);
        } else {
            return TypeExpr1(new Pair(type, type));
        }
    }
    private Pair TypeExpr1(Pair typesPair) {
        if (current.type().is(Token.Type.LEFT_BRACKET)) {
            Pair result = TypeExpr2(typesPair.type1());
            
            if (current.type().is(Token.Type.LEFT_BRACKET)) {
                Type[] argsTypes = FuncArgsTypes();

                ((PointerType)result.type2()).setType(new FunctionType(typesPair.type1(), argsTypes));
                result.setType2(typesPair.type2());
            }

            return result;
        } else if (current.type().is(Token.Type.LEFT_SQUARE_BRACKET)) {
            return new Pair(ArrayDim(typesPair.type1()), typesPair.type2());
        }
        
        return typesPair;
    }
    private Pair TypeExpr2(Type type) {
        LeftBracket();
        Pair result = TypeExpr0(type);
        RightBracket();
        
        return result;
    }
    
    private Pair Pointer() {
        Type pointer = null;
        
        nextToken();
        if (current.type().is(Token.Type.CONST)) {
            pointer = new PointerType(pointer, true);
        } else {
            pointer = new PointerType(pointer, false);
        }
        
        Type innerPointer = pointer;
        while (current.type().is(Token.Type.ASTERISK)) {
            nextToken();
            if (current.type().is(Token.Type.CONST)) {
                pointer = new PointerType(pointer, true);
            } else {
                pointer = new PointerType(pointer, false);
            }
        }
        return new Pair(pointer, innerPointer);
    }
    private Type[] FuncArgsTypes() {
        List<Type> args = new LinkedList<Type>();
        LeftBracket();
        if (current.type().is(Token.Type.Modifier)) {                   
            Type argType = Type();
            args.add(argType);
            
            while (current.type().is(Token.Type.COMMA)) {
                nextToken();
                argType = Type();
                args.add(argType);
            }
        }
        RightBracket();
        
        return args.toArray(new Type[0]);
    }
    private Type ArrayDim (Type elementType) {
        ArrayType array = null;
        
        LeftSquareBracket();
        Position pos = current.coordinates().starting();
        if (current.type().is(Token.Type.CONST_INT)) {
            array = new ArrayType(elementType, (int)current.value());
        } else  if (current.type().is(Token.Type.RIGHT_SQUARE_BRACKET)) {
            array = new ArrayType(elementType);
        } else {
            array = new ArrayType(elementType);
            Logger.logUnexpectedToken(current.type(), Token.Type.CONST_INT, pos);
        }
        RightSquareBracket();
        
        while (current.type().is(Token.Type.LEFT_SQUARE_BRACKET)) {
            LeftSquareBracket();
            pos = current.coordinates().starting();
            if (current.type().is(Token.Type.CONST_INT)) {
                array = new ArrayType(array, (int)current.value());
                nextToken();
            } else {
                array = new ArrayType(elementType);
                Logger.logUnexpectedToken(current.type(), Token.Type.CONST_INT, pos);
            }
            RightSquareBracket();
        }
        
        return array;
    }
    
    private Type PrimitiveType() {
        Type type = null;
        
        boolean isConst = false;
        if (current.type().is(Token.Type.CONST)) {
            isConst = true;
            nextToken();
        } else if (current.type().is(Token.Type.VAR)) {
            isConst = false;
            nextToken();
        } else {
            Logger.logUnexpectedToken(current.type(), Token.Type.Modifier, 
                    current.coordinates().starting());
        }
        
        switch (current.type()) {
            case INT:
                type = new PrimitiveType(Type.Typename.INT, isConst);
                break;
            case DOUBLE:
                type = new PrimitiveType(Type.Typename.DOUBLE, isConst);
                break;
            case FLOAT:
                type = new PrimitiveType(Type.Typename.FLOAT, isConst);
                break;
            case BOOL:
                type = new PrimitiveType(Type.Typename.BOOL, isConst);
                break;
            case CHAR:
                type = new PrimitiveType(Type.Typename.CHAR, isConst);
                break;
            case VOID:
                type = new PrimitiveType(Type.Typename.VOID, isConst);
                break;
            case STRUCT:
                Position pos = current.coordinates().starting();
                nextToken();
                String typename = Identifier();
                Symbol struct = currentScope.get(typename);
                if (struct == null) {
                    Logger.logUndeclaredType("STRUCT " + typename, pos);
                }
                type = struct.type();
                break;
            default:
                Logger.logUnexpectedToken(current.type(), Token.Type.PrimitiveType, 
                        current.coordinates().starting());
        }
        nextToken();
        return type;
    }
    
    
    
    private String Identifier() {
        if (checkTokens(current, Token.Type.IDENTIFIER)) {
            String identifier = (String)current.value();
            nextToken();
            return identifier;
        } else {
            return null;
        }
    }
    private Type[] FuncArgList() {
        List<Type> args = null;
        
        LeftBracket();
        if (current.type().is(Token.Type.Modifier)) {
            args = new LinkedList<Type>();
                    
            Type argType = Type();
            String argName = Identifier();
            currentScope.add(new VariableSymbol(argName, argType));
            args.add(argType);
            
            while (current.type().is(Token.Type.COMMA)) {
                nextToken();
                argType = Type();
                argName = Identifier();
                currentScope.add(new VariableSymbol(argName, argType));
                args.add(argType);
            }
        }
        RightBracket();
        
        return args.toArray(new Type[0]);
    }
    private BlockNode Code() {
        LeftBrace();
        BlockNode block = Block();
        RightBrace();
        
        return block;
    } 
    
    private BlockNode Block() {
        BlockNode block = new BlockNode();
        
      
        while (current.type().is(new Token.Type[] {
            Token.Type.FirstOfControlStructure, Token.Type.FirstOfExpression,
            Token.Type.Modifier
        })) {
            
            if (current.type().is(Token.Type.FirstOfControlStructure)) {
                switch (current.type()) {
                    case FOR:
                        block.addChild(For());
                        break;
                    case IF:
                        block.addChild(If());
                        break;
                    case WHILE:
                        block.addChild(While());
                        break;
                    case DO:
                        block.addChild(DoWhile());
                        Semicolon();
                        break;
                    case RUN:
                        block.addChild(NewThread());
                        Semicolon();
                        break;
                    case SWITCH:
                        block.addChild(Switch());
                        break;
                    case RETURN:
                        block.addChild(Return());
                        Semicolon();
                        break;
                    case CONTINUE:
                        block.addChild(Continue());
                        Semicolon();
                        break;
                    case LOCK:
                        block.addChild(Lock());
                        break;
                    case BARRIER:
                        block.addChild(Barrier());
                        Semicolon();
                        break;
                    case BREAK:
                        block.addChild(Break());
                        Semicolon();
                        break;
                }
            } else if(current.type().is(Token.Type.Modifier)) {
                block.addChild(VariableDef());
                Semicolon();
            } else if(current.type().is(Token.Type.FirstOfExpression)) {
                block.addChild(Expression());
                Semicolon();
            }
        }
        
        return block;
    }
    private ForNode For() {
        ForNode node = new ForNode();
        
        nextToken();
        LeftBracket();

        if (current.type().is(Token.Type.Modifier)) {
            node.setInitialization(VariableDef());
        } else if (current.type().is(Token.Type.FirstOfExpression)) {
            node.setInitialization(Expression());
        }
        Semicolon();
        if (current.type().is(Token.Type.FirstOfExpression)) {
            node.setCondition(Expression());
        }
        Semicolon();
        if (current.type().is(Token.Type.FirstOfExpression)) {
            Node increase = Expression();
            
            if (current.type().is(Token.Type.COMMA)) {
                BlockNode block = new BlockNode();
                block.addChild(increase);
                
                while (current.type().is(Token.Type.COMMA)) {
                    nextToken();
                    block.addChild(Expression());
                }
                increase = block;
            }
            
            node.setIncrease(increase);
        }
        
        RightBracket();
        node.setBlock(Code());
        
        return node;
    }
    private void Semicolon() {
        if (checkTokens(current, Token.Type.SEMICOLON))
            nextToken();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    private boolean checkTokens(Token found, Token.Type required) {
        boolean result;
        if (result = !found.type().is(required))
            Logger.logUnexpectedToken(found.type(), required, current.coordinates().starting());
        return !result;
    }
    private boolean checkTokens(Token found, Token.Type required, Position pos) {
        boolean result;
        if (result = !found.type().is(required))
            Logger.logUnexpectedToken(found.type(), required, pos);
        return !result;
    }   
    
    
    
    
    
    
    
    
    
    
    
    private IfNode If() {
        nextToken();
        IfNode node = new IfNode();
        
        LeftBracket();
        node.setExpression(Expression());
        RightBracket();
        
        node.setBlock(Code());
        
        if (current.type().is(Token.Type.ELSE)) {
            nextToken();
            
            if (current.type().is(Token.Type.IF)) {
                node.setElseBlock(If());
            } else {
                node.setElseBlock(Code());
            }
        }
        return node;
    }
    private WhileNode While() {
        nextToken();
        WhileNode node = new WhileNode();
        
        LeftBracket();
        node.setExpression(Expression());
        RightBracket();
        
        node.setBlock(Code());
        
        return node;
    }
    private DoWhileNode DoWhile() {
        nextToken();
        DoWhileNode node = new DoWhileNode();
        
        node.setBlock(Code());
        if (checkTokens(current, Token.Type.WHILE)) {
            nextToken();
            
            LeftBracket();
            node.setExpression(Expression());
            RightBracket();
        }
        
        return node;
    }
    private Node NewThread() {
        nextToken();
        
        return new UnaryOperationNode(
                UnaryOperationNode.Operation.RUN, Expression());
    }
    private SwitchNode Switch() {
        nextToken();
        SwitchNode node = new SwitchNode();
        
        LeftBracket();
        node.setExpression(Expression());
        RightBracket();
        
        LeftBrace();
        node.addCase(Case());
        
        while (current.type().is(Token.Type.CASE)) {
            node.addCase(Case());
        }
        if (current.type().is(Token.Type.DEFAULT)) {
            node.setDefaultNode(Default());
        }
        RightBrace();
        
        return node;
    }
    private CaseNode Case() {
        nextToken();
        CaseNode node = new CaseNode();
        
        node.setExpression(Expression());
        Colon();
        node.setBlock(Block());
        
        return node;
    }
    private BlockNode Default() {
        nextToken();
        
        Colon();
        return Block();
    }
    private void Colon() {
        if (checkTokens(current, Token.Type.COLON))
            nextToken();
    }
    private Node Return() {
        nextToken();
        
        if (current.type().is(Token.Type.FirstOfExpression)) {
            return new UnaryOperationNode(
                    UnaryOperationNode.Operation.RETURN, Expression());
        } else {            
            return new NoOperandOperationNode(
                    NoOperandOperationNode.Operation.RETURN);
        }
    }
    private Node Break() {
        if (checkTokens(current, Token.Type.BREAK)) {
            nextToken();
            return new NoOperandOperationNode(
                    NoOperandOperationNode.Operation.BREAK);
        } else {
            return new InvalidNode();
        }
    }
    private Node Continue() {
        if (checkTokens(current, Token.Type.CONTINUE)) {
            nextToken();
            return new NoOperandOperationNode(
                    NoOperandOperationNode.Operation.CONTINUE);
        } else {
            return new InvalidNode();
        }
    }
    private Node Lock() {
        if (checkTokens(current, Token.Type.LOCK)) {
            nextToken();
            return new UnaryOperationNode(
                    UnaryOperationNode.Operation.LOCK, Code());
        } else {
            return new InvalidNode();
        }
    }
    private Node Barrier() {
        if (checkTokens(current, Token.Type.BARRIER)) {
            nextToken();
            return new NoOperandOperationNode(
                    NoOperandOperationNode.Operation.BARRIER);
        } else {
            return new InvalidNode();
        }
    }
    
    private Node Expression() {
        Node result = BoolExpression();
        
        while (current.type().is(Token.Type.Assignment)) {
            BinaryOperationNode.Operation operation = null;
            switch (current.type()) {
                case ASSIGN:
                    operation = BinaryOperationNode.Operation.ASSIGN;
                    break;
                case PLUS_ASSIGN:
                    operation = BinaryOperationNode.Operation.PLUS_ASSIGN;
                    break;
                case MINUS_ASSIGN:
                    operation = BinaryOperationNode.Operation.MINUS_ASSIGN;
                    break;
                case MUL_ASSIGN:
                    operation = BinaryOperationNode.Operation.MUL_ASSIGN;
                    break;
                case DIV_ASSIGN:
                    operation = BinaryOperationNode.Operation.DIV_ASSIGN;
                    break;
                case MOD_ASSIGN:
                    operation = BinaryOperationNode.Operation.MOD_ASSIGN;
                    break;
                case BITWISE_OR_ASSIGN:
                    operation = BinaryOperationNode.Operation.BITWISE_OR_ASSIGN;
                    break;
                case BITWISE_SHIFT_LEFT_ASSIGN:
                    operation = BinaryOperationNode.Operation.BITWISE_SHIFT_LEFT_ASSIGN;
                    break;
                case BITWISE_SHIFT_RIGHT_ASSIGN:
                    operation = BinaryOperationNode.Operation.BITWISE_SHIFT_RIGHT_ASSIGN;
                    break;
                case BITWISE_XOR_ASSIGN:
                    operation = BinaryOperationNode.Operation.BITWISE_XOR_ASSIGN;
                    break;
                case BITWISE_AND_ASSIGN:
                    operation = BinaryOperationNode.Operation.BITWISE_AND_ASSIGN;
                    break;
                default:
                    return new InvalidNode();
            }
            nextToken();
            
            Node right = BoolExpression();
            
            result = new BinaryOperationNode(operation, result, right);
        }
        
        return result;
    }
    
    private Node BoolExpression() {
        Node result = ABoolExpression();
        
        while (current.type().is(Token.Type.BOOL_OR)) {            
            nextToken();
            Node right = ABoolExpression();
            result = new BinaryOperationNode(
                        BinaryOperationNode.Operation.BOOL_OR, result, right);
        }
        
        return result;
    }
    private Node ABoolExpression() {
        Node result = BBoolExpression();
        
        while (current.type().is(Token.Type.BOOL_AND)) {
            nextToken();
            Node right = BBoolExpression();
            result = new BinaryOperationNode(
                        BinaryOperationNode.Operation.BOOL_AND, result, right);
        }
        
        return result;
    }
    private Node BBoolExpression() {
        Node result = GExpression();
        
        while (current.type().is(Token.Type.BITWISE_OR)) {            
            nextToken();
            Node right = GExpression();
            result = new BinaryOperationNode(
                        BinaryOperationNode.Operation.BITWISE_OR, result, right);
        }
        
        return result;
    }
    private Node GExpression() {
        Node result = HExpression();

        while (current.type().is(Token.Type.BITWISE_XOR)) {
            nextToken();
            Node right = HExpression();
            result = new BinaryOperationNode(
                        BinaryOperationNode.Operation.BITWISE_XOR, result, right);
        }
        
        return result;
    }
    private Node HExpression() {
        Node result = IExpression();
        
        while (current.type().is(Token.Type.AMPERSAND)) {
            nextToken();
            Node right = IExpression();
            result = new BinaryOperationNode(
                        BinaryOperationNode.Operation.BITWISE_AND, result, right);
        }
        
        return result;
    }
    private Node IExpression() {
        Node result = CBoolExpression();
        
        while (current.type().is(Token.Type.Equality)) {
            BinaryOperationNode.Operation operation;
            switch (current.type()) {
                case EQUAL:
                    operation = BinaryOperationNode.Operation.EQUAL;
                    break;
                case NOT_EQUAL:
                    operation = BinaryOperationNode.Operation.NOT_EQUAL;
                    break;
                default:
                    return new InvalidNode();
            }
            nextToken();
            Node right = CBoolExpression();
            
            result = new BinaryOperationNode(operation, result, right);
        }
        
        return result;
    }
    private Node CBoolExpression() {
        Node result = DboolExpression();
        
        while (current.type().is(Token.Type.OrderRelation)) {
            BinaryOperationNode.Operation operation;
            switch (current.type()) {
                case GREATER:
                    operation = BinaryOperationNode.Operation.GREATER;
                    break;
                case GREATER_OR_EQUAL:
                    operation = BinaryOperationNode.Operation.GREATER_OR_EQUAL;
                    break;
                case LESS:
                    operation = BinaryOperationNode.Operation.LESS;
                    break;
                case LESS_OR_EQUAL:
                    operation = BinaryOperationNode.Operation.LESS_OR_EUQAL;
                    break;
                default:
                    return new InvalidNode();
            }
            nextToken();
            
            Node right = DboolExpression();
            result = new BinaryOperationNode(operation, result, right);
        }
        
        return result;
    }
    private Node DboolExpression() {
        Node result = AExpression();
        
        while (current.type().is(Token.Type.BitwiseShift)) {
            BinaryOperationNode.Operation operation;
            switch (current.type()) {
                case BITWISE_SHIFT_LEFT:
                    operation = BinaryOperationNode.Operation.BITWISE_SHIFT_LEFT;
                    break;
                case BITWISE_SHIFT_RIGHT:
                    operation = BinaryOperationNode.Operation.BITWISE_SHIFT_RIGHT;
                    break;
                default:
                    return new InvalidNode();
            }
            nextToken();
            Node right = AExpression();
            result = new BinaryOperationNode(operation, result, right);
        }
        
        return result;
    }
    private Node AExpression() {
        Node result = BExpression();
        
        while (current.type().is(Token.Type.PlusMinus)) {
            BinaryOperationNode.Operation operation;
            switch (current.type()) {
                case PLUS:
                    operation = BinaryOperationNode.Operation.PLUS;
                    break;
                case MINUS:
                    operation = BinaryOperationNode.Operation.MINUS;
                    break;
                default:
                    return new InvalidNode();
            }
            nextToken();
            Node right = BExpression();
            result = new BinaryOperationNode(operation, result, right);
        }
        
        return result;
    }
    private Node BExpression() {
        Node result = CExpression();
        
        while (current.type().is(Token.Type.MulDivMod)) {
            BinaryOperationNode.Operation operation;
            switch (current.type()) {
                case DIV:
                    operation = BinaryOperationNode.Operation.DIV;
                    break;
                case ASTERISK:
                    operation = BinaryOperationNode.Operation.MUL;
                    break;
                case MOD:
                    operation = BinaryOperationNode.Operation.MOD;
                    break;
                default:
                    return new InvalidNode();
            }
            nextToken();
            Node right = CExpression();
            result = new BinaryOperationNode(operation, result, right);
        }
        
        return result;
    }
    private Node CExpression() {
        if (current.type() == Token.Type.LEFT_BRACKET) {
            nextToken();
            if (current.type().is(Token.Type.Modifier)) {
                Type type = Type();
                RightBracket();
                
                return new UnaryOperationNode(
                        UnaryOperationNode.Operation.CAST, type, DExpression());
            } else {
                Node node = Expression();
                RightBracket();
                return node;
            }
        } else {
            return DExpression();
        }
    }
    private Node DExpression() {
        if (current.type().is(Token.Type.PlusMinus)) {
            switch (current.type()) {
                case PLUS:
                    nextToken();
                    return EExpression();
                case MINUS:
                    nextToken();
                    return new UnaryOperationNode(
                            UnaryOperationNode.Operation.MINUS, EExpression());
                default:
                    nextToken();
                    return new InvalidNode();
            }
        } else if (current.type().is(Token.Type.IncDec)) {
            UnaryOperationNode.Operation operation;
            switch (current.type()) {
                case INC:
                    operation = UnaryOperationNode.Operation.PRE_INC;
                    break;
                case DEC:
                    operation = UnaryOperationNode.Operation.PRE_DEC;
                    break;
                default:
                    return new InvalidNode();
            }
            nextToken();
            Node node = EExpression();

            return new UnaryOperationNode(operation, node);
        } else {
            Node node = EExpression();
            while (current.type() == Token.Type.AMPERSAND ||
                   current.type() == Token.Type.ASTERISK) {
                UnaryOperationNode.Operation operation;
                switch (current.type()) {
                    case AMPERSAND:
                        operation = UnaryOperationNode.Operation.REF;
                        break;
                    case ASTERISK:
                        operation = UnaryOperationNode.Operation.DEREF;
                        break;
                    default:
                        return new InvalidNode();
                }
                nextToken();
                
                node = new UnaryOperationNode(operation, node);
            }
            return node;
        }
    }
    private Node EExpression() {
        Node node = FExpression();
        
        if (current.type().is(Token.Type.IncDec)) {
            UnaryOperationNode.Operation operation;
            switch (current.type()) {
                case INC:
                    operation = UnaryOperationNode.Operation.POST_INC;
                    break;
                case DEC:
                    operation = UnaryOperationNode.Operation.POST_DEC;
                    break;
                default:
                    return new InvalidNode();
            }
            nextToken();
            node = new UnaryOperationNode(operation, node);
        }
        
        return node;
    }
    private Node FExpression() {
        Node node = JExpression();
        
        while (current.type().is(new Token.Type[] {
            Token.Type.MEMBER_SELECT, Token.Type.LEFT_BRACKET,
            Token.Type.LEFT_SQUARE_BRACKET
        })) {
            if (current.type().is(Token.Type.MEMBER_SELECT)) {
                nextToken();
                String fieldName = Identifier();

                node = new BinaryOperationNode(
                        BinaryOperationNode.Operation.MEMBER_SELECT,
                        node,
                        new VariableLeaf(fieldName));
            } else if (current.type().is(Token.Type.LEFT_BRACKET)) {
                LeftBracket();
                CallNode call = new CallNode();
                
                if (current.type().is(Token.Type.FirstOfExpression)) {           
                    call.addArgument(Expression());
                    while (current.type() == Token.Type.COMMA) {
                        nextToken();
                        call.addArgument(Expression());
                    }
                }
                RightBracket();

                node = call;
            } else {
                LeftSquareBracket();
                node = new BinaryOperationNode(
                        BinaryOperationNode.Operation.ARRAY_ELEMENT, 
                        node, Expression());
                RightSquareBracket();
            }
        }
        
        return node;
    }    
    private Node JExpression() {
        if (current.type().is(Token.Type.Constant)) {
            return Const();
        } else {
            return new VariableLeaf(Identifier());
        }
    }    
    
    private Node Const() {
        Node node;
        switch (current.type()) {
            case CONST_CHAR:
                node = new ConstantLeaf(
                        current.value(), 
                        new PrimitiveType(Type.Typename.CHAR, true));
                break;
            case CONST_DOUBLE:
                node = new ConstantLeaf(
                        current.value(),
                        new PrimitiveType(Type.Typename.DOUBLE, true));
                break;
            case CONST_INT:
                node = new ConstantLeaf(
                        current.value(),
                        new PrimitiveType(Type.Typename.INT, true));
                break;
            case TRUE:
                node = new ConstantLeaf(
                        Boolean.TRUE,
                        new PrimitiveType(Type.Typename.BOOL, true));
                break;
            case FALSE:
                node = new ConstantLeaf(
                        Boolean.FALSE,
                        new PrimitiveType(Type.Typename.BOOL, true));
                break;
            default:
                node = new InvalidNode();
        }
        
        nextToken();
        return node;
    }
    
    private Node StructDef() {
        nextToken();
        
        StructNode struct = new StructNode(Identifier());
        
        LeftBrace();
        while (current.type().is(Token.Type.Modifier)) {
            struct.addDeclaration(VariableDef());
            Semicolon();
        }

        RightBrace();
        
        return struct;
    }
    private Node VariableDef() {
        Type type = Type();
        
        Node node = Variable(type);
        if (current.type() == Token.Type.COMMA) {
            BlockNode vars = new BlockNode();
            vars.addChild(node);
            
            while (current.type() == Token.Type.COMMA) {
                nextToken();
                vars.addChild(Variable(type));
            }
            
            node = vars;
        }
        
        return node;
    }
    private Node Variable(Type type) {
        String name = Identifier();
        
        Node node;
        
        if (name != null) {
            node = new VariableLeaf(name);
        } else {
            node = new InvalidNode();
        }
        
        if (current.type() == Token.Type.ASSIGN) {
            nextToken();
             
            BinaryOperationNode binOp = 
                    new BinaryOperationNode(BinaryOperationNode.Operation.ASSIGN);
            binOp.setLeftChild(node);
            
            if (current.type().is(Token.Type.FirstOfExpression))
                binOp.setRightChild(Expression());
            
            node = binOp;
        }
        
        return node;
    }
    
    private void LeftSquareBracket() {
        if (checkTokens(current, Token.Type.LEFT_SQUARE_BRACKET)) {
            nextToken();
        }
    }
    private void RightSquareBracket() {
        if (checkTokens(current, Token.Type.RIGHT_SQUARE_BRACKET)) {
            nextToken();
        }
    }
    private void LeftBracket() {
        if (checkTokens(current, Token.Type.LEFT_BRACKET)) {
            nextToken();
        }
    }
    private void RightBracket() {
        if (checkTokens(current, Token.Type.RIGHT_BRACKET)) {
            nextToken();
        }
    }
    private void LeftBrace() {
        if (checkTokens(current, Token.Type.LEFT_BRACE)) {
            nextToken();
        }
    }
    private void RightBrace() {
        if (checkTokens(current, Token.Type.RIGHT_BRACE)) {
            nextToken();
        }
    }
}
