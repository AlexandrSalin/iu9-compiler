package ru.bmstu.iu9.compiler.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author maggot
 */
public class Parser {
    public Parser(String filename) {
        BufferedReader reader = null;
        
        try {
            Gson gson = 
                    new GsonBuilder().
                        registerTypeAdapter(
                            Fragment.class, 
                            new Fragment.FragmentInstanceCreator()).
                        registerTypeAdapter(
                            Token.class,
                            new Token.TokenInstanceCreator()).
                        create();
            
            reader = new BufferedReader(
                        new FileReader(filename));
            
            tokens = gson.fromJson(reader, Token[].class);
        } catch(java.io.IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch(java.io.IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public static void main(String[] args) {
        Parser parser = new Parser(
            "C:\\Users\\maggot\\Documents\\NetBeansProjects\\ru.bmstu.iu9.compiler\\Front End\\src\\output.json");
        parser.process();
    }
    
    public void process() {
        Program();
    }
    
    private void nextToken() {
        if (++position < tokens.length)
            current = tokens[position];
    }
    
    private Token[] tokens;
    private int position = -1;
    private Token current;
    private TreeNode parseTree = new CompositeNode(null);
    private SymbolTable globalScope = new SymbolTable();
    private SymbolTable currentScope;
    
    private void Program() {
        nextToken();
        Token.Type tag = current.tag();
        while (tag.is(new Token.Type[] { 
                    Token.Type.CONST, Token.Type.VAR, 
                    Token.Type.FUNC, Token.Type.STRUCT
                })) {
            currentScope = globalScope;
            
            if(tag.is(Token.Type.FUNC)) {
                SymbolTable ambientScope = currentScope;
                currentScope = new SymbolTable();
                
                nextToken();
                Type returnValueType = Type();
                String funcName = Identifier();                
                Type[] argsTypes = FuncArgList();
                FunctionSymbol function = new FunctionSymbol(
                        funcName, new FunctionType(returnValueType, argsTypes), 
                        ambientScope, currentScope);
                
                ambientScope.add(function);
                currentScope.setAssociatedSymbol(function);
                
                Code();
                currentScope = ambientScope;
            } else if (tag.is(Token.Type.STRUCT)) {
                StructDef();
                Semicolon();
            } else {
                VariableDef();
                Semicolon();
            }
            
            tag = current.tag();
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
        if (current.tag().is(Token.Type.ASTERISK)) {
            Pair result = Pointer();
            ((PointerType)result.type2()).setType(type);
            type = result.type1();
        }
        Pair innerType = TypeExpr1(new Pair(null, null));
        if (current.tag() == Token.Type.LEFT_SQUARE_BRACKET) {
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
        if (current.tag().is(Token.Type.ASTERISK)) {
            Pair pointers = Pointer();
            return TypeExpr1(pointers);
        } else {
            return TypeExpr1(new Pair(type, type));
        }
    }
    private Pair TypeExpr1(Pair typesPair) {
        if (current.tag().is(Token.Type.LEFT_BRACKET)) {
            Pair result = TypeExpr2(typesPair.type1());
            
            if (current.tag().is(Token.Type.LEFT_BRACKET)) {
                Type[] argsTypes = FuncArgsTypes();

                ((PointerType)result.type2()).setType(new FunctionType(typesPair.type1(), argsTypes));
                result.setType2(typesPair.type2());
            }

            return result;
        } else if (current.tag().is(Token.Type.LEFT_SQUARE_BRACKET)) {
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
        if (current.tag().is(Token.Type.CONST)) {
            pointer = new PointerType(pointer, true);
        } else {
            pointer = new PointerType(pointer, false);
        }
        
        Type innerPointer = pointer;
        while (current.tag().is(Token.Type.ASTERISK)) {
            nextToken();
            if (current.tag().is(Token.Type.CONST)) {
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
        if (current.tag().is(Token.Type.Modifier)) {                   
            Type argType = Type();
            args.add(argType);
            
            while (current.tag().is(Token.Type.COMMA)) {
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
        if (current.tag().is(Token.Type.CONST_INT)) {
            array = new ArrayType(elementType, (int)current.value());
        } else  if (current.tag().is(Token.Type.RIGHT_SQUARE_BRACKET)) {
            array = new ArrayType(elementType);
        } else {
            array = new ArrayType(elementType);
            Logger.logUnexpectedToken(current.tag(), Token.Type.CONST_INT, pos);
        }
        RightSquareBracket();
        
        while (current.tag().is(Token.Type.LEFT_SQUARE_BRACKET)) {
            LeftSquareBracket();
            pos = current.coordinates().starting();
            if (current.tag().is(Token.Type.CONST_INT)) {
                array = new ArrayType(array, (int)current.value());
                nextToken();
            } else {
                array = new ArrayType(elementType);
                Logger.logUnexpectedToken(current.tag(), Token.Type.CONST_INT, pos);
            }
            RightSquareBracket();
        }
        
        return array;
    }
    
    private Type PrimitiveType() {
        Type type = null;
        
        boolean isConst = false;
        if (current.tag().is(Token.Type.CONST)) {
            isConst = true;
            nextToken();
        } else if (current.tag().is(Token.Type.VAR)) {
            isConst = false;
            nextToken();
        } else {
            Logger.logUnexpectedToken(current.tag(), Token.Type.Modifier, 
                    current.coordinates().starting());
        }
        
        switch (current.tag()) {
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
                Logger.logUnexpectedToken(current.tag(), Token.Type.PrimitiveType, 
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
        if (current.tag().is(Token.Type.Modifier)) {
            args = new LinkedList<Type>();
                    
            Type argType = Type();
            String argName = Identifier();
            currentScope.add(new VariableSymbol(argName, argType));
            args.add(argType);
            
            while (current.tag().is(Token.Type.COMMA)) {
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
    private CompositeNode Code() {
        LeftBrace();
        CompositeNode block = Block();
        RightBrace();
        
        return block;
    } 
    
    private CompositeNode Block() {
        CompositeNode block = new CompositeNode(null);
        
      
        while (current.tag().is(new Token.Type[] {
            Token.Type.FirstOfControlStructure, Token.Type.FirstOfExpression,
            Token.Type.Modifier
        })) {
            
            if (current.tag().is(Token.Type.FirstOfControlStructure)) {
                switch (current.tag()) {
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
            } else if(current.tag().is(Token.Type.Modifier)) {
                block.addChild(VariableDef());
                Semicolon();
            } else if(current.tag().is(Token.Type.FirstOfExpression)) {
                block.addChild(Expression());
                Semicolon();
            }
        }
        
        return block;
    }
    private TreeNode For() {
        CompositeNode forNode = new CompositeNode(CompositeNode.Operation.FOR);
        
        nextToken();
        LeftBracket();

        if (current.tag().is(Token.Type.Modifier)) {
            forNode.addChild(VariableDef());
        } else if (current.tag().is(Token.Type.FirstOfExpression)) {
            forNode.addChild(Expression());
        }
        Semicolon();
        if (current.tag().is(Token.Type.FirstOfExpression)) {
            Position pos = current.coordinates().starting();
            TreeNode condition = Expression();
            checkTypes(condition.type(), Type.Typename.BOOL, pos);
            
            forNode.addChild(condition);
        }
        Semicolon();
        if (current.tag().is(Token.Type.FirstOfExpression)) {
            CompositeNode node = new CompositeNode(CompositeNode.Operation.SEQUENCING);
            node.addChild(Expression());
            while (current.tag().is(Token.Type.COMMA)) {
                nextToken();
                node.addChild(Expression());
            }
            forNode.addChild(node);
        }
        
        RightBracket();
        forNode.addChild(Code());
        
        return forNode;
    }
    private void Semicolon() {
        if (checkTokens(current, Token.Type.SEMICOLON))
            nextToken();
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    private boolean checkTokens(Token found, Token.Type required) {
        boolean result;
        if (result = !found.tag().is(required))
            Logger.logUnexpectedToken(found.tag(), required, current.coordinates().starting());
        return !result;
    }
    private boolean checkTokens(Token found, Token.Type required, Position pos) {
        boolean result;
        if (result = !found.tag().is(required))
            Logger.logUnexpectedToken(found.tag(), required, pos);
        return !result;
    }
    private boolean checkTypes(Type found, Type required, Position pos) {
        boolean result;
        if (result = !found.equals(required))
            Logger.logIncompatibleTypes(found, required, pos);
        return !result;
    }
    private boolean checkTypes(Type found, Type.Typename required, Position pos) {
        boolean result;
        if (result = found.Typename() != required)
            Logger.logIncompatibleTypes(found, required, pos);
        return !result;
    }
    private boolean checkTypes(Type found, Type.Typename[] required, Position pos) {
        boolean result = true;
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < required.length; ++i) {
            if (i > 0)
                str.append(" or ");
            result = result && (found.Typename() != required[i]);
            str.append(required[i]);
        }
        if (result)
            Logger.logIncompatibleTypes(found, str.toString(), pos);
        
        return !result;
    }
    
    
    
    
    
    
    
    
    
    
    
    
    private TreeNode If() {
        nextToken();
        CompositeNode ifNode = new CompositeNode(CompositeNode.Operation.IF);
        
        LeftBracket();
        
        Position pos = current.coordinates().starting();
        TreeNode condition = Expression();
        
        checkTypes(condition.type(), Type.Typename.BOOL, pos);
        
        ifNode.addChild(condition);
        
        RightBracket();
        
        ifNode.addChild(Code());
        
        if (current.tag().is(Token.Type.ELSE)) {
            nextToken();
            CompositeNode elseNode = new CompositeNode(CompositeNode.Operation.ELSE);
            
            if (current.tag().is(Token.Type.IF)) {
                elseNode.addChild(If());
            } else {
                elseNode.addChild(Code());
            }
            ifNode.addChild(elseNode);
        }
        return ifNode;
    }
    private TreeNode While() {
        nextToken();
        CompositeNode whileNode = new CompositeNode(CompositeNode.Operation.WHILE);
        
        LeftBracket();

        Position pos = current.coordinates().starting();
        TreeNode condition = Expression();
        checkTypes(condition.type(), Type.Typename.BOOL, pos);
        whileNode.addChild(condition);

        RightBracket();
        whileNode.addChild(Code());
        
        return whileNode;
    }
    private TreeNode DoWhile() {
        nextToken();
        CompositeNode dowhile = new CompositeNode(CompositeNode.Operation.DO_WHILE);
        
        dowhile.addChild(Code());
        if (checkTokens(current, Token.Type.WHILE)) {
            nextToken();
            LeftBracket();
            
            Position pos = current.coordinates().starting();
            TreeNode condition = Expression();
            checkTypes(condition.type(), Type.Typename.BOOL, pos);
            dowhile.addChild(condition);
            
            RightBracket();
        }
        
        return dowhile;
    }
    private TreeNode NewThread() {
        nextToken();
        
        Position pos = current.coordinates().starting();
        TreeNode expression = Expression();
        checkTypes(expression.type(), Type.Typename.FUNCTION, pos);
        
        CompositeNode run = new CompositeNode(CompositeNode.Operation.RUN);
        run.addChild(expression);
        
        return run;
    }
    private TreeNode Switch() {
        nextToken();
        CompositeNode node = new CompositeNode(CompositeNode.Operation.SWITCH);
        
        LeftBracket();
        TreeNode switchNode = Expression();
        RightBracket();
        
        LeftBrace();
        node.addChild(Case(switchNode));
        
        while (current.tag().is(Token.Type.CASE)) {
            node.addChild(Case(switchNode));
        }
        if (current.tag().is(Token.Type.DEFAULT)) {
            node.addChild(Default());
        }
        RightBrace();
        
        return node;
    }
    private TreeNode Case(TreeNode switchNode) {
        nextToken();
        
        Position pos = current.coordinates().starting();
        TreeNode caseNode = Expression();
        
        checkTypes(caseNode.type(), switchNode.type(), pos);
        
        Colon();
        TreeNode block = Block();
        
        CompositeNode node = new CompositeNode(CompositeNode.Operation.CASE);
        node.addChild(caseNode);
        node.addChild(block);
        
        return node;
    }
    private TreeNode Default() {
        nextToken();
        
        Colon();
        return Block();
    }
    private void Colon() {
        if (checkTokens(current, Token.Type.COLON))
            nextToken();
    }
    private TreeNode Return() {
        nextToken();
        CompositeNode returnNode;
        
        if (current.tag().is(Token.Type.FirstOfExpression)) {
            Position pos = current.coordinates().starting();
            TreeNode value = Expression();
            checkTypes(value.type(),
                    (((FunctionType)((FunctionSymbol)currentScope.associatedSymbol()).type()).returnValueType()),
                    pos);
            
            returnNode = 
                    new CompositeNode(value.type(), CompositeNode.Operation.RETURN);
            returnNode.addChild(value);
        } else {
            checkTypes(
                    (((FunctionType)((FunctionSymbol)currentScope.associatedSymbol()).type()).returnValueType()),
                    Type.Typename.VOID, 
                    current.coordinates().starting());
            
            returnNode = new CompositeNode(CompositeNode.Operation.RETURN);
        }
        
        return returnNode;
    }
    private TreeNode Break() {
        if (checkTokens(current, Token.Type.BREAK)) {
            nextToken();
            return new CompositeNode(CompositeNode.Operation.BREAK);
        } else {
            return new InvalidNode();
        }
    }
    private TreeNode Continue() {
        if (checkTokens(current, Token.Type.CONTINUE)) {
            nextToken();
            return new CompositeNode(CompositeNode.Operation.CONTINUE);
        } else {
            return new InvalidNode();
        }
    }
    private TreeNode Lock() {
        if (checkTokens(current, Token.Type.LOCK)) {
            nextToken();
            CompositeNode lock = new CompositeNode(CompositeNode.Operation.LOCK);
            lock.addChild(Code());

            return lock;
        } else {
            return new InvalidNode();
        }
    }
    private TreeNode Barrier() {
        if (checkTokens(current, Token.Type.BARRIER)) {
            nextToken();
            return new CompositeNode(CompositeNode.Operation.BARRIER);
        } else {
            return new InvalidNode();
        }
    }
    
    private TreeNode Expression() {
        Position pos = current.coordinates().starting();
        TreeNode result = BoolExpression();
        
        while (current.tag().is(Token.Type.Assignment)) {
            Token.Type type = current.tag();
            nextToken();
            TreeNode right = BoolExpression();
            
            checkTypes(result.type(), right.type(), pos);
            
            CompositeNode node = null;
            switch (type) {
                case ASSIGN:
                    node = new CompositeNode(result.type(), CompositeNode.Operation.ASSIGN);
                    break;
                case PLUS_ASSIGN:
                    node = new CompositeNode(result.type(), CompositeNode.Operation.PLUS_ASSIGN);
                    break;
                case MINUS_ASSIGN:
                    node = new CompositeNode(result.type(), CompositeNode.Operation.MINUS_ASSIGN);
                    break;
                case MUL_ASSIGN:
                    node = new CompositeNode(result.type(), CompositeNode.Operation.MUL_ASSIGN);
                    break;
                case DIV_ASSIGN:
                    node = new CompositeNode(result.type(), CompositeNode.Operation.DIV_ASSIGN);
                    break;
                case MOD_ASSIGN:
                    node = new CompositeNode(result.type(), CompositeNode.Operation.MOD_ASSIGN);
                    break;
                case BITWISE_OR_ASSIGN:
                    checkTypes(result.type(), Type.Typename.INT, pos);
                    node = new CompositeNode(result.type(), CompositeNode.Operation.BITWISE_OR_ASSIGN);
                    break;
                case BITWISE_SHIFT_LEFT_ASSIGN:
                    checkTypes(result.type(), Type.Typename.INT, pos);
                    node = new CompositeNode(result.type(), CompositeNode.Operation.BITWISE_SHIFT_LEFT_ASSIGN);
                    break;
                case BITWISE_SHIFT_RIGHT_ASSIGN:
                    checkTypes(result.type(), Type.Typename.INT, pos);
                    node = new CompositeNode(result.type(), CompositeNode.Operation.BITWISE_SHIFT_RIGHT_ASSIGN);
                    break;
                case BITWISE_XOR_ASSIGN:
                    checkTypes(result.type(), Type.Typename.INT, pos);
                    node = new CompositeNode(result.type(), CompositeNode.Operation.BITWISE_XOR_ASSIGN);
                    break;
                case BITWISE_AND_ASSIGN:
                    checkTypes(result.type(), Type.Typename.INT, pos);
                    node = new CompositeNode(result.type(), CompositeNode.Operation.BITWISE_AND_ASSIGN);
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
            node.addChild(result);
            node.addChild(right);
            result = node;
        }
        
        return result;
    }
    private TreeNode BoolExpression() {
        Position pos = current.coordinates().starting();
        TreeNode result = ABoolExpression();
        
        while (current.tag().is(Token.Type.BOOL_OR)) {
            checkTypes(result.type(), Type.Typename.BOOL, pos);
            
            nextToken();
            
            pos = current.coordinates().starting();
            TreeNode right = ABoolExpression();
            
            checkTypes(right.type(), Type.Typename.BOOL, pos);
            
            CompositeNode node = 
                new CompositeNode(result.type(), CompositeNode.Operation.BOOL_OR);

            node.addChild(result);
            node.addChild(right);
            result = node;
        }
        
        return result;
    }
    private TreeNode ABoolExpression() {
        Position pos = current.coordinates().starting();
        TreeNode result = BBoolExpression();
        
        while (current.tag().is(Token.Type.BOOL_AND)) {
            checkTypes(result.type(), Type.Typename.BOOL, pos);
            
            nextToken();
            pos = current.coordinates().starting();
            TreeNode right = BBoolExpression();
            
            checkTypes(right.type(), Type.Typename.BOOL, pos);
            
            CompositeNode node = 
                new CompositeNode(result.type(), CompositeNode.Operation.BOOL_AND);

            node.addChild(result);
            node.addChild(right);
            result = node;
        }
        
        return result;
    }
    private TreeNode BBoolExpression() {
        Position pos = current.coordinates().starting();
        TreeNode result = GExpression();
        
        while (current.tag().is(Token.Type.BITWISE_OR)) {
            checkTypes(result.type(), Type.Typename.BOOL, pos);
            
            nextToken();
            pos = current.coordinates().starting();
            TreeNode right = GExpression();
            
            checkTypes(right.type(), Type.Typename.BOOL, pos);
            
            CompositeNode node = 
                new CompositeNode(result.type(), CompositeNode.Operation.BITWISE_OR);

            node.addChild(result);
            node.addChild(right);
            result = node;
        }
        
        return result;
    }
    private TreeNode GExpression() {
        Position pos = current.coordinates().starting();
        TreeNode result = HExpression();

        while (current.tag().is(Token.Type.BITWISE_XOR)) {
            checkTypes(result.type(), Type.Typename.BOOL, pos);
            
            nextToken();
            pos = current.coordinates().starting();
            TreeNode right = HExpression();
            
            checkTypes(right.type(), Type.Typename.BOOL, pos);
            
            CompositeNode node = 
                new CompositeNode(result.type(), CompositeNode.Operation.BITWISE_XOR);

            node.addChild(result);
            node.addChild(right);
            result = node;
        }
        
        return result;
    }
    private TreeNode HExpression() {  
        Position pos = current.coordinates().starting();
        TreeNode result = IExpression();
        
        while (current.tag().is(Token.Type.AMPERSAND)) {
            checkTypes(result.type(), Type.Typename.INT, pos);
            nextToken();
            pos = current.coordinates().starting();
            TreeNode right = IExpression();
            
            checkTypes(right.type(), Type.Typename.INT, pos);
            
            CompositeNode node = 
                new CompositeNode(result.type(), CompositeNode.Operation.BITWISE_AND);

            node.addChild(result);
            node.addChild(right);
            result = node;
        }
        
        return result;
    }
    private TreeNode IExpression() {
        TreeNode result = CBoolExpression();
        
        while (current.tag().is(Token.Type.Equality)) {
            Position pos = current.coordinates().starting();
            Token.Type type = current.tag();
            nextToken();
            TreeNode right = CBoolExpression();
            
            checkTypes(result.type(), right.type(), pos);
            
            CompositeNode node = null;
            switch (type) {
                case EQUAL:
                    node = new CompositeNode(
                            new PrimitiveType(Type.Typename.BOOL, true), 
                            CompositeNode.Operation.EQUAL);
                    break;
                case NOT_EQUAL:
                    node = new CompositeNode(
                            new PrimitiveType(Type.Typename.BOOL, true), 
                            CompositeNode.Operation.NOT_EQUAL);
                    break;
                default:
                    Logger.logUnknownError(pos);
            }
            node.addChild(result);
            node.addChild(right);
            result = node;
        }
        
        return result;
    }
    private TreeNode CBoolExpression() {
        TreeNode result = DboolExpression();
        
        while (current.tag().is(Token.Type.OrderRelation)) {
            Position pos = current.coordinates().starting();
            Token.Type type = current.tag();
            nextToken();
            TreeNode right = DboolExpression();
            
            checkTypes(result.type(), right.type(), pos);
            
            CompositeNode node = null;
            switch (type) {
                case GREATER:
                    node = new CompositeNode(
                            new PrimitiveType(Type.Typename.BOOL, true), 
                            CompositeNode.Operation.GREATER);
                    break;
                case GREATER_OR_EQUAL:
                    node = new CompositeNode(
                            new PrimitiveType(Type.Typename.BOOL, true), 
                            CompositeNode.Operation.GREATER_OR_EQUAL);
                    break;
                case LESS:
                    node = new CompositeNode(
                            new PrimitiveType(Type.Typename.BOOL, true), 
                            CompositeNode.Operation.LESS);
                    break;
                case LESS_OR_EQUAL:
                    node = new CompositeNode(
                            new PrimitiveType(Type.Typename.BOOL, true), 
                            CompositeNode.Operation.LESS_OR_EUQAL);
                    break;
                default:
                    Logger.logUnknownError(pos);
            }
            node.addChild(result);
            node.addChild(right);
            node.setType(new PrimitiveType(Type.Typename.BOOL, true));
            result = node;
        }
        
        return result;
    }
    private TreeNode DboolExpression() {
        TreeNode result = AExpression();
        
        while (current.tag().is(Token.Type.BitwiseShift)) {
            Position pos = current.coordinates().starting();
            checkTypes(result.type(), Type.Typename.INT, pos);
            
            pos = current.coordinates().starting();
            Token.Type type = current.tag();
            nextToken();
            TreeNode right = AExpression();
            
            checkTypes(right.type(), Type.Typename.INT, pos);
            
            CompositeNode node = null;
            switch (type) {
                case BITWISE_SHIFT_LEFT:
                    node = new CompositeNode(result.type(), CompositeNode.Operation.BITWISE_SHIFT_LEFT);
                    break;
                case BITWISE_SHIFT_RIGHT:
                    node = new CompositeNode(result.type(), CompositeNode.Operation.BITWISE_SHIFT_RIGHT);
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
            node.addChild(result);
            node.addChild(right);
            result = node;
        }
        
        return result;
    }
    private TreeNode AExpression() {
        TreeNode result = BExpression();
        
        while (current.tag().is(Token.Type.PlusMinus)) {
            Position pos = current.coordinates().starting();
            Token.Type type = current.tag();
            nextToken();
            TreeNode right = BExpression();
            
            checkTypes(result.type(), right.type(), pos);
            
            CompositeNode node = null;
            switch (type) {
                case PLUS:
                    node = new CompositeNode(result.type(), CompositeNode.Operation.PLUS);
                    break;
                case MINUS:
                    node = new CompositeNode(result.type(), CompositeNode.Operation.MINUS);
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
            node.addChild(result);
            node.addChild(right);
            result = node;
        }
        
        return result;
    }
    private TreeNode BExpression() {
        TreeNode result = CExpression();
        
        while (current.tag().is(Token.Type.MulDivMod)) {
            Position pos = current.coordinates().starting();
            Token.Type type = current.tag();
            nextToken();
            TreeNode right = CExpression();
            
            checkTypes(result.type(), right.type(), pos);
            
            CompositeNode node = null;
            switch (type) {
                case DIV:
                    node = new CompositeNode(result.type(), CompositeNode.Operation.DIV);
                    break;
                case ASTERISK:
                    node = new CompositeNode(result.type(), CompositeNode.Operation.MUL);
                    break;
                case MOD:
                    node = new CompositeNode(result.type(), CompositeNode.Operation.MOD);
                    break;
                default:
                    throw new UnsupportedOperationException();
            }
            node.addChild(result);
            node.addChild(right);
            result = node;
        }
        
        return result;
    }
    private TreeNode CExpression() {
        if (current.tag() == Token.Type.LEFT_BRACKET) {
            nextToken();
            if (current.tag().is(Token.Type.Modifier)) {
                CompositeNode node = new CompositeNode(CompositeNode.Operation.CAST);
                Type type = Type();
                RightBracket();
                
                TreeNode expression = DExpression();
                
                node.setType(type);
                node.addChild(expression);
                
                return node;
            } else {
                TreeNode node = Expression();
                RightBracket();
                return node;
            }
        } else {
            return DExpression();
        }
    }
    private TreeNode DExpression() {
        if (current.tag().is(Token.Type.PlusMinus)) {
            return PlusMinus(EExpression());
        } else if (current.tag().is(Token.Type.IncDec)) {
            Token.Type type = current.tag();
            nextToken();
            TreeNode node = EExpression();
            
            checkTypes(node.type(), 
                    new Type.Typename[] { 
                        Type.Typename.CHAR, Type.Typename.INT
                    }, 
                    current.coordinates().starting());

            CompositeNode opNode = null;

            switch (type) {
                case INC:
                    opNode = new CompositeNode(node.type(), CompositeNode.Operation.PRE_INC);
                    break;
                case DEC:
                    opNode = new CompositeNode(node.type(), CompositeNode.Operation.PRE_DEC);
                    break;
                default:
                    Logger.logUnknownError(current.coordinates().starting());
            }
            opNode.addChild(node);

            return opNode;
        } else {
            TreeNode node = EExpression();
            while (current.tag() == Token.Type.AMPERSAND ||
                   current.tag() == Token.Type.ASTERISK) {
                node = AmpersandAsterisk(node);
            }
            return node;
        }
    }
    private TreeNode AmpersandAsterisk(TreeNode node) {
        CompositeNode compositeNode = null;
        switch (current.tag()) {
            case AMPERSAND:
                compositeNode = new CompositeNode(CompositeNode.Operation.REF);
                compositeNode.setType(new PointerType(node.type(), false));
                compositeNode.addChild(node);
                break;
            case ASTERISK:
                if (node.type().Typename() != Type.Typename.POINTER)
                    throw new UnsupportedOperationException();
                compositeNode = new CompositeNode(CompositeNode.Operation.DEREF);
                compositeNode.setType(((PointerType)node.type()).type());
                compositeNode.addChild(node);
                break;
            default:
                Logger.logUnknownError(current.coordinates().starting());
        }
        nextToken();
        
        return compositeNode;
    }
    private TreeNode PlusMinus(TreeNode node) {        
        CompositeNode compositeNode = null;
        switch (current.tag()) {
            case PLUS:
                break;
            case MINUS:
                compositeNode = new CompositeNode(CompositeNode.Operation.UNARY_MINUS);
                compositeNode.addChild(node);
            default:
                Logger.logUnknownError(current.coordinates().starting());
        }
        nextToken();
        
        return compositeNode;
    }
    private TreeNode EExpression() {
        TreeNode node = FExpression();
        
        if (current.tag().is(Token.Type.IncDec)) {
            node = PostIncDec(node);
        }
        
        return node;
    }
    private TreeNode PostIncDec(TreeNode node) {
        checkTypes(node.type(), 
                new Type.Typename[] { 
                    Type.Typename.CHAR, Type.Typename.INT
                }, 
                current.coordinates().starting());
        
        CompositeNode opNode = null;
        
        switch (current.tag()) {
            case INC:
                opNode = new CompositeNode(node.type(), CompositeNode.Operation.POST_INC);
                break;
            case DEC:
                opNode = new CompositeNode(node.type(), CompositeNode.Operation.POST_DEC);
                break;
            default:
                Logger.logUnknownError(current.coordinates().starting());
        }
        nextToken();
        opNode.addChild(node);
        
        return opNode;
    }
    private TreeNode FExpression() {
        TreeNode node = JExpression();
        
        while (current.tag() == Token.Type.MEMBER_SELECT ||
               current.tag() == Token.Type.LEFT_BRACKET ||
               current.tag() == Token.Type.LEFT_SQUARE_BRACKET) {
            if (current.tag() == Token.Type.MEMBER_SELECT) {
                node = StructMember(node);
            } else if (current.tag() == Token.Type.LEFT_BRACKET) {
                node = FuncArgs(node);
            } else {
                node = ArrayElement(node);
            }
        }
        
        return node;
    }    
    private TreeNode JExpression() {
        if (current.tag().is(Token.Type.Constant)) {
            return Const();
        } else {
            Position pos = current.coordinates().starting();
            String name = Identifier();
            Symbol var = currentScope.get(name);
            if (var == null)
                Logger.logUndeclaredType(name, pos);
            
            return new VariableLeaf(name, var.type());
        }
    }
    
    private CompositeNode StructMember(TreeNode struct) {
        nextToken();
        checkTypes(struct.type(), Type.Typename.STRUCT, current.coordinates().starting());

        CompositeNode composite = new CompositeNode(CompositeNode.Operation.MEMBER_SELECT);

        String fieldName = Identifier();
        
        Symbol structSymbol = currentScope.get(((StructType)struct.type()).name());
        if (structSymbol == null) {
            Logger.logUndeclaredType(((StructType)struct.type()).name(), current.coordinates().starting());
            return composite;
        } else if (checkTypes(structSymbol.type(), Type.Typename.STRUCT, current.coordinates().starting())) {
            Symbol var = ((StructSymbol)structSymbol).scope().get(fieldName);
            if (var == null)
                Logger.logUndeclaredType(fieldName, null);

            composite.addChild(struct);
            composite.addChild(new VariableLeaf(fieldName, var.type()));
            composite.setType(var.type());
            return composite;
        } else {
            return composite;
        }
    }
    private CompositeNode FuncArgs(TreeNode functionNode) {
        Position pos = current.coordinates().starting();
        checkTypes(functionNode.type(), Type.Typename.FUNCTION, pos);
        
        FunctionType function = (FunctionType)functionNode.type();
        CompositeNode functionCall = 
                new CompositeNode(
                    function.returnValueType(), 
                    CompositeNode.Operation.CALL);
        functionCall.addChild(functionNode);

        CompositeNode args = new CompositeNode(CompositeNode.Operation.FUNCTION_ARGUMENTS);
        LeftBracket();
        if (current.tag().is(Token.Type.FirstOfExpression)) {           
            args.addChild(Expression());
            while (current.tag() == Token.Type.COMMA) {
                nextToken();
                args.addChild(Expression());
            }
        }
        RightBracket();

        if (args.childrenNumber() != function.argumentsTypes().length)
            Logger.log("Invalid function arguments", pos);

        for (int i = 0; i < args.childrenNumber(); ++i) {
            checkTypes(args.children().get(i).type(), function.argumentsTypes()[i], pos);
        }

        functionCall.addChild(args);

        return functionCall;
    }
    private CompositeNode ArrayElement(TreeNode node) {
        Position pos = current.coordinates().starting();
        checkTypes(node.type(), Type.Typename.ARRAY, pos);
        
        ArrayType array = (ArrayType)node.type();
        CompositeNode arrayElementNode = 
            new CompositeNode(
                array.elementType(), 
                CompositeNode.Operation.ARRAY_ELEMENT);
        arrayElementNode.addChild(node);

        LeftSquareBracket();
        if (current.tag().is(Token.Type.FirstOfExpression)) {
            pos = current.coordinates().starting();
            TreeNode index = Expression();
            if (checkTypes(index.type(), Type.Typename.INT, pos)) {
                arrayElementNode.addChild(index);
            }
        }
        RightSquareBracket();

        return arrayElementNode;
    }
    
    
    private TreeNode Const() {
        ConstantLeaf leaf = null;
        switch (current.tag()) {
            case CONST_CHAR:
                leaf = new ConstantLeaf(new PrimitiveType(Type.Typename.CHAR, true), current.value());
                break;
            case CONST_DOUBLE:
                leaf = new ConstantLeaf(new PrimitiveType(Type.Typename.DOUBLE, true), current.value());
                break;
            case CONST_INT:
                leaf = new ConstantLeaf(new PrimitiveType(Type.Typename.INT, true), current.value());
                break;
            case TRUE:
                leaf = new ConstantLeaf(new PrimitiveType(Type.Typename.INT, true), new Integer(1));
                break;
            case FALSE:
                leaf = new ConstantLeaf(new PrimitiveType(Type.Typename.INT, true), new Integer(0));
                break;
            default:
                Logger.logUnknownError(current.coordinates().starting());
        }
        
        nextToken();
        return leaf;
    }
    
    
    private TreeNode ArrayInit() {
        CompositeNode value = new CompositeNode(CompositeNode.Operation.ARRAY_ELEMENT);
        LeftBrace();
        
        TreeNode element = ArrayElement();
        value.addChild(element);
        while (current.tag() == Token.Type.COMMA) {
            nextToken();
            Position pos = current.coordinates().starting();
            
            TreeNode nextElement = ArrayElement();
            value.addChild(nextElement);
            
            checkTypes(element.type(), nextElement.type(), pos);
        }
        if (current.tag() == Token.Type.COMMA) {
            nextToken();
        }
        
        RightBrace();
        
        return value;
    }
    private TreeNode ArrayElement() {
        if (current.tag() == Token.Type.LEFT_SQUARE_BRACKET) {
            return ArrayInit();
        } else {
            return Expression();
        }
    }
    
    
    private void StructDef() {
        nextToken();
        
        String typename = Identifier();
        StructType structType = new StructType(typename);
        
        StructSymbol struct = new StructSymbol(typename, structType, currentScope);
        currentScope.add(struct);
        currentScope.setAssociatedSymbol(struct);
        
        SymbolTable backup = currentScope;
        currentScope = struct.scope();
        
        LeftBrace();
        while (current.tag().is(Token.Type.Modifier)) {
            VariableDef();
            Semicolon();
        }

        RightBrace();
        
        currentScope = backup;
    }
    private TreeNode VariableDef() {
        CompositeNode var = new CompositeNode(CompositeNode.Operation.SEQUENCING);
        Type type = Type();
        
        var.addChild(Variable(type));
        while (current.tag() == Token.Type.COMMA) {
            nextToken();
            var.addChild(Variable(type));
        }
        
        return var;
    }
    private TreeNode Variable(Type type) {
        String name = Identifier();
        
        if (name != null) 
            currentScope.add(new VariableSymbol(name, type));
        
        if (current.tag() == Token.Type.ASSIGN) {
            nextToken();
             
            CompositeNode node = new CompositeNode(CompositeNode.Operation.ASSIGN);
            TreeNode var = new VariableLeaf(name, currentScope);
            node.addChild(var);
            
            Position pos = current.coordinates().starting();
            TreeNode value;
            if (current.tag().is(Token.Type.FirstOfExpression))
                value = Expression();
            else
                value = ArrayInit();
            
            checkTypes(type, value.type(), pos);
            
            node.addChild(value);
            node.setType(var.type());
            
            return node;
        } else if (name != null) {
            return new VariableLeaf(name, currentScope);
        } else {
            return new InvalidNode();
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
