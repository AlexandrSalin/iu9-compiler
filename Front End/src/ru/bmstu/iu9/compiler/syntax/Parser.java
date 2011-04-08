package ru.bmstu.iu9.compiler.syntax;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.PrintWriter;
import java.util.Arrays;
import ru.bmstu.iu9.compiler.lexis.token.Token;
import ru.bmstu.iu9.compiler.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import ru.bmstu.iu9.compiler.syntax.tree.*;

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
    
    public void toJson(String filename) {
        PrintWriter writer = null;
        
        try {
            Gson gson = new GsonBuilder().
                    setPrettyPrinting().
                    registerTypeAdapter(
                        BlockNode.class, 
                        new BlockNode.BlockNodeSerializer()).
                    setExclusionStrategies(new NodeExclusionStrategy()).
                    create();
            writer = new PrintWriter(filename);
            gson.toJson(parseTree, writer);
        } catch(java.io.IOException ex) {
//            ex.printStackTrace();
        } finally {
            writer.flush();
            writer.close();
        }
    }
    
    public static void main(String[] args) {
        Parser parser = new Parser(
            "C:\\Users\\maggot\\Documents\\NetBeansProjects\\ru.bmstu.iu9.compiler\\Front End\\src\\output.json");
        parser.process();
        parser.toJson(
            "C:\\Users\\maggot\\Documents\\NetBeansProjects\\ru.bmstu.iu9.compiler\\Front End\\src\\parse_tree.json");
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
    
    private void Program() {
        nextToken();
        while (current.type().is(new Token.Type[] { 
                    Token.Type.Modifier, Token.Type.FUNC, Token.Type.STRUCT
                })) {
            
            if(current.type().is(Token.Type.FUNC)) {
                parseTree.addChild(FunctionDecl());
            } else if (current.type().is(Token.Type.STRUCT)) {
                parseTree.addChild(StructDecl());
                Semicolon();
            } else if (current.type().is(Token.Type.VAR)){
                parseTree.addChild(VariableDecl());
                Semicolon();
            } else {
                parseTree.addChild(ConstantDecl());
                Semicolon();
            }
        }
    }
    private Node FunctionDecl() {
        nextToken();
        String name = Identifier();
        FunctionType signature = Signature();
        BlockNode code = Code();
        
        return new FunctionDeclNode(name, signature, code);
    }
    
    private Type Type() {
        Type type;
        if (current.type().is(Token.Type.PrimitiveType)) {
            type = PrimitiveType();
        } else if (current.type().is(new Token.Type[] {
            Token.Type.ASTERISK, Token.Type.LEFT_SQUARE_BRACKET,
            Token.Type.STRUCT, Token.Type.FUNC
        })) {
            type = TypeLit();
        } else {
            LeftBracket();
            type = Type();
            RightBracket();
        }
        
        return type;
    }
    private Type TypeLit() {
        switch (current.type()) {
            case ASTERISK:
                return PointerType();
            case LEFT_SQUARE_BRACKET:
                return ArrayType();
            case FUNC:
                return FunctionType();
            case STRUCT:
                return StructType();
            default:
                return new InvalidType();
        }
    }
    private StructType StructType() {
        nextToken();
        return new StructType(Identifier());
    }
    private ArrayType ArrayType() {
        Integer length = null;
        LeftSquareBracket();
        if (checkTokens(current, Token.Type.CONST_INT, current.coordinates().starting())) {
            length = (int)current.value();
        }
        RightSquareBracket();
        
        if (length == null)
            return new ArrayType(Type());
        else
            return new ArrayType(Type(), length);
    }
    private PointerType FunctionType() {
        nextToken();
        return new PointerType(Signature());
    }
    private FunctionType Signature() {
        FunctionType.Argument[] args = Parameters();
        Type result = Type();
        
        return new FunctionType(result, args);
    }
    private FunctionType.Argument[] Parameters() {
        LeftBracket();
        FunctionType.Argument args[] = ParameterList();
        RightBracket();
        
        return args;
    }
    private FunctionType.Argument[] ParameterList() {
        List<FunctionType.Argument> args = 
                new LinkedList<FunctionType.Argument>();
        
        args.addAll(Arrays.asList(ParameterDecl()));
        while (current.type().is(Token.Type.COMMA)) {
            nextToken();
            args.addAll(Arrays.asList(ParameterDecl()));
        }
        
        return args.toArray(new FunctionType.Argument[0]);
    }
    private FunctionType.Argument[] ParameterDecl() {
        String[] names = null;
        if (current.type().is(Token.Type.IDENTIFIER)) {
            names = IdentifierList();
        }
        Type type = Type();
        
        return FunctionType.Argument.getArguments(type, names);
    }
    private String[] IdentifierList() {
        List<String> idents = new LinkedList<String>();
        
        idents.add(Identifier());
        while (current.type().is(Token.Type.COMMA)) {
            nextToken();
            idents.add(Identifier());
        }
        
        return idents.toArray(new String[0]);
    }
    private PointerType PointerType() {
        nextToken();
        return new PointerType(Type(), false);
    }
    private PrimitiveType PrimitiveType() {
        PrimitiveType type = null;
        
        switch (current.type()) {
            case INT:
                type = new PrimitiveType(PrimitiveType.Typename.INT);
                break;
            case DOUBLE:
                type = new PrimitiveType(PrimitiveType.Typename.DOUBLE);
                break;
            case FLOAT:
                type = new PrimitiveType(PrimitiveType.Typename.FLOAT);
                break;
            case BOOL:
                type = new PrimitiveType(PrimitiveType.Typename.BOOL);
                break;
            case CHAR:
                type = new PrimitiveType(PrimitiveType.Typename.CHAR);
                break;
            case VOID:
                type = new PrimitiveType(PrimitiveType.Typename.VOID);
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
            return "";
        }
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
            
            if (current.type().is(new Token.Type[] {
                Token.Type.FirstOfControlStructure, Token.Type.Modifier
            })) {
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
                    case VAR:
                        block.addChild(VariableDecl());
                        Semicolon();
                        break;
                    case CONST:
                        block.addChild(ConstantDecl());
                        Semicolon();
                        break;
                }
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
            node.setInitialization(VariableDecl());
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
        Position pos = current.coordinates().starting();
        nextToken();
        
        return new UnaryOperationNode(
                UnaryOperationNode.Operation.RUN, Expression(), pos);
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
            Position pos = current.coordinates().starting();
            return new UnaryOperationNode(
                    UnaryOperationNode.Operation.RETURN, Expression(), pos);
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
            Position pos = current.coordinates().starting();
            return new UnaryOperationNode(
                    UnaryOperationNode.Operation.LOCK, Code(), pos);
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
            Position pos = current.coordinates().starting();
            nextToken();
            
            Node right = BoolExpression();
            
            result = new BinaryOperationNode(operation, result, right, pos);
        }
        
        return result;
    }
    
    private Node BoolExpression() {
        Node result = ABoolExpression();
        
        while (current.type().is(Token.Type.BOOL_OR)) {
            Position pos = current.coordinates().starting();
            nextToken();
            Node right = ABoolExpression();
            result = new BinaryOperationNode(
                        BinaryOperationNode.Operation.BOOL_OR, result, right, pos);
        }
        
        return result;
    }
    private Node ABoolExpression() {
        Node result = BBoolExpression();
        
        while (current.type().is(Token.Type.BOOL_AND)) {
            Position pos = current.coordinates().starting();
            nextToken();
            Node right = BBoolExpression();
            result = new BinaryOperationNode(
                        BinaryOperationNode.Operation.BOOL_AND, result, right, pos);
        }
        
        return result;
    }
    private Node BBoolExpression() {
        Node result = GExpression();
        
        while (current.type().is(Token.Type.BITWISE_OR)) {
            Position pos = current.coordinates().starting();
            nextToken();
            Node right = GExpression();
            result = new BinaryOperationNode(
                        BinaryOperationNode.Operation.BITWISE_OR, result, right, pos);
        }
        
        return result;
    }
    private Node GExpression() {
        Node result = HExpression();

        while (current.type().is(Token.Type.BITWISE_XOR)) {
            Position pos = current.coordinates().starting();
            nextToken();
            Node right = HExpression();
            result = new BinaryOperationNode(
                        BinaryOperationNode.Operation.BITWISE_XOR, result, right, pos);
        }
        
        return result;
    }
    private Node HExpression() {
        Node result = IExpression();
        
        while (current.type().is(Token.Type.AMPERSAND)) {
            Position pos = current.coordinates().starting();
            nextToken();
            Node right = IExpression();
            result = new BinaryOperationNode(
                        BinaryOperationNode.Operation.BITWISE_AND, result, right, pos);
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
            Position pos = current.coordinates().starting();
            nextToken();
            Node right = CBoolExpression();
            
            result = new BinaryOperationNode(operation, result, right, pos);
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
            Position pos = current.coordinates().starting();
            nextToken();
            
            Node right = DboolExpression();
            result = new BinaryOperationNode(operation, result, right, pos);
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
            Position pos = current.coordinates().starting();
            nextToken();
            Node right = AExpression();
            result = new BinaryOperationNode(operation, result, right, pos);
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
            Position pos = current.coordinates().starting();
            nextToken();
            Node right = BExpression();
            result = new BinaryOperationNode(operation, result, right, pos);
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
            Position pos = current.coordinates().starting();
            nextToken();
            Node right = CExpression();
            result = new BinaryOperationNode(operation, result, right, pos);
        }
        
        return result;
    }
    private Node CExpression() {
        if (current.type() == Token.Type.LEFT_BRACKET) {
            nextToken();
            if (current.type().is(Token.Type.Modifier)) {
                Type type = Type();
                RightBracket();
                Position pos = current.coordinates().starting();
                
                return new UnaryOperationNode(
                        UnaryOperationNode.Operation.CAST, type, DExpression(), pos);
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
                    Position pos = current.coordinates().starting();
                    nextToken();
                    return new UnaryOperationNode(
                            UnaryOperationNode.Operation.MINUS, EExpression(), pos);
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
            Position pos = current.coordinates().starting();
            nextToken();
            Node node = EExpression();

            return new UnaryOperationNode(operation, node, pos);
        } else {
            return RefDeref();
        }
    }
    private Node RefDeref() {
        Position pos = current.coordinates().starting();
        
        if (current.type().is(new Token.Type[] {
            Token.Type.AMPERSAND, Token.Type.ASTERISK
        })) {
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
            return new UnaryOperationNode(operation, RefDeref(), pos);
        } else {
            return EExpression();
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
            Position pos = current.coordinates().starting();
            nextToken();
            node = new UnaryOperationNode(operation, node, pos);
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
                Position pos = current.coordinates().starting();
                nextToken();
                Position varPos = current.coordinates().starting();
                String fieldName = Identifier();

                node = new BinaryOperationNode(
                        BinaryOperationNode.Operation.MEMBER_SELECT,
                        node,
                        new VariableLeaf(fieldName, varPos), pos);
            } else if (current.type().is(Token.Type.LEFT_BRACKET)) {
                Position pos = current.coordinates().starting();
                LeftBracket();
                CallNode call = new CallNode(pos);
                
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
                Position pos = current.coordinates().starting();
                node = new BinaryOperationNode(
                        BinaryOperationNode.Operation.ARRAY_ELEMENT, 
                        node, Expression(), pos);
                RightSquareBracket();
            }
        }
        
        return node;
    }    
    private Node JExpression() {
        if (current.type().is(Token.Type.Constant)) {
            return Const();
        } else {
            Position pos = current.coordinates().starting();
            return new VariableLeaf(Identifier(), pos);
        }
    }    
    
    private Node Const() {
        Node node;
        Position pos = current.coordinates().starting();
        switch (current.type()) {
            case CONST_CHAR:
                node = new ConstantLeaf(
                        current.value(), 
                        new PrimitiveType(PrimitiveType.Typename.CHAR, true), pos);
                break;
            case CONST_DOUBLE:
                node = new ConstantLeaf(
                        current.value(),
                        new PrimitiveType(PrimitiveType.Typename.DOUBLE, true), pos);
                break;
            case CONST_INT:
                node = new ConstantLeaf(
                        current.value(),
                        new PrimitiveType(PrimitiveType.Typename.INT, true), pos);
                break;
            case TRUE:
                node = new ConstantLeaf(
                        Boolean.TRUE,
                        new PrimitiveType(PrimitiveType.Typename.BOOL, true), pos);
                break;
            case FALSE:
                node = new ConstantLeaf(
                        Boolean.FALSE,
                        new PrimitiveType(PrimitiveType.Typename.BOOL, true), pos);
                break;
            default:
                node = new InvalidNode();
        }
        
        nextToken();
        return node;
    }

    private Node StructDecl() {
        Position pos = current.coordinates().starting();
        nextToken();
        
        String structName = Identifier();
        StructDeclNode struct = 
                new StructDeclNode(structName, new StructType(structName));
        
        LeftBrace();
        while (current.type().is(Token.Type.IDENTIFIER)) {
            struct.addDeclaration(FieldDecl());
        }
        RightBrace();
        
        return struct;
    }
    private Node FieldDecl() {
        Position pos = current.coordinates().starting();
        
        String[] fields = IdentifierList();
        Type type = Type();
        Semicolon();
        
        return new VariablesDeclNode(fields, type);
    }
    
    private boolean Modifier() {
        boolean constancy = true;
        switch (current.type()) {
            case VAR:
                nextToken();
                constancy = false;
                break;
            case CONST:
                nextToken();
                constancy = true;
                break;
            default:
                Logger.logUnexpectedToken(current.type(), Token.Type.Modifier, 
                        current.coordinates().starting());
        }
        return constancy;
    }
    
    private Node ConstantDecl() {
        nextToken();
        
        if (current.type().is(Token.Type.LEFT_BRACKET)) {
            LeftBracket();
            BlockNode decls = new BlockNode();
            decls.addChild(ConstantSpec());
            Semicolon();
            
            while (current.type().is(Token.Type.IDENTIFIER)) {
                decls.addChild(ConstantSpec());
                Semicolon();
            }
            
            RightBracket();
            
            return decls;
        } else {
            return ConstantSpec();
        }
    }
    private Node ConstantSpec() {
        Position pos = current.coordinates().starting();
        String name = Identifier();
        
        Type type = Type();

        Position posAssign = current.coordinates().starting();
        if (checkTokens(current, Token.Type.ASSIGN, posAssign))
            nextToken();

        BinaryOperationNode binOp = 
                new BinaryOperationNode(BinaryOperationNode.Operation.ASSIGN, posAssign);
        binOp.setLeftChild(new VariablesDeclNode(name, type));
        binOp.setRightChild(Expression());

        return binOp;
    }
    
    
    private Node VariableDecl() {        
        nextToken();
        
        if (current.type().is(Token.Type.LEFT_BRACKET)) {
            LeftBracket();
            BlockNode decls = new BlockNode();
            decls.addChild(VariableSpec());
            Semicolon();
            
            while (current.type().is(Token.Type.IDENTIFIER)) {
                decls.addChild(VariableSpec());
                Semicolon();
            }
            
            RightBracket();
            
            return decls;
        } else {
            return VariableSpec();
        }
    }
    private Node VariableSpec() {
        Position pos = current.coordinates().starting();
        String name = Identifier();
        
        if (current.type().is(Token.Type.COMMA)) {
            List<String> names = new LinkedList<String>();
            names.add(name);
            
            while (current.type().is(Token.Type.COMMA)) {
                nextToken();
                names.add(Identifier());
            }
            Type type = Type();
            return new VariablesDeclNode(names.toArray(new String[0]), type);
        } else {
            Type type = Type();
            if (current.type().is(Token.Type.ASSIGN)) {
                Position posAssign = current.coordinates().starting();
                nextToken();

                BinaryOperationNode binOp = 
                        new BinaryOperationNode(BinaryOperationNode.Operation.ASSIGN, posAssign);
                binOp.setLeftChild(new VariablesDeclNode(name, type));
                binOp.setRightChild(Expression());

                return binOp;
            } else {
                return new VariablesDeclNode(name, type);
            }
        }
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