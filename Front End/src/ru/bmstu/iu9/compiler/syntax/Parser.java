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
                    registerTypeHierarchyAdapter(
                        BlockNode.class, 
                        new BlockNode.BlockNodeAdapter()).
                    /*registerTypeHierarchyAdapter(
                        BaseTypeNode.class, 
                        new BaseTypeNode.TypeNodeSerializer()).*/
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
    private BlockNode<Statement> parseTree = new BlockNode<Statement>();
    
    private void Program() {
        nextToken();
        while (current.type().is(
                new Token.Type[] { 
                    Token.Type.VAR, Token.Type.FUNC, Token.Type.STRUCT
                }
              )) {
            
            if(current.type().is(Token.Type.FUNC)) {
                parseTree.addChild(FunctionDecl());
            } else if (current.type().is(Token.Type.STRUCT)) {
                parseTree.addChild(StructDecl());
                Semicolon();
            } else if (current.type().is(Token.Type.VAR)){
                parseTree.addChild(VariableDecl());
                Semicolon();
            }
        }
    }
    private FunctionDeclNode FunctionDecl() {
        nextToken();
        Position pos = current.coordinates().starting();
        String name = Identifier();
        FunctionTypeNode signature = Signature();
        BlockNode<Statement> code = Code();
        
        return new FunctionDeclNode(name, signature, code, pos);
    }
    
    
    private BaseTypeNode Type() {
        boolean constancy = isConstant();
        
        BaseTypeNode type;
        if (current.type().is(Token.Type.PrimitiveType)) {
            type = PrimitiveType(constancy);
        } else if (current.type().is(
            new Token.Type[] {
                Token.Type.ASTERISK, 
                Token.Type.LEFT_SQUARE_BRACKET,
                Token.Type.STRUCT, 
                Token.Type.FUNC
            })
        ) {
            type = TypeLit(constancy);
        } else {
            LeftBracket();
            type = Type();
            RightBracket();
        }
        
        return type;
    }
    private boolean isConstant() {
        if (current.type().is(Token.Type.CONST)) {
            nextToken();
            return true;
        } else {
            return false;
        }
    }
    private BaseTypeNode TypeLit(boolean constancy) {
        switch (current.type()) {
            case ASTERISK:
                return PointerType(constancy);
            case LEFT_SQUARE_BRACKET:
                return ArrayType(constancy);
            case FUNC:
                return FunctionType(constancy);
            case STRUCT:
                return StructType(constancy);
            default:
                return BaseTypeNode.InvalidNode(current.coordinates().starting());
        }
    }
    private StructTypeNode StructType(boolean constancy) {
        Position pos = current.coordinates().starting();
        nextToken();
        return new StructTypeNode(Identifier(), constancy, pos);
    }
    private ArrayTypeNode ArrayType(boolean constancy) {
        IntegerConstantLeaf length = null;
        Position pos = current.coordinates().starting();
        LeftSquareBracket();
        if (checkTokens(
                current, 
                Token.Type.CONST_INT, 
                current.coordinates().starting()
            )
        ) {
            length = (IntegerConstantLeaf)Const();
        }
        RightSquareBracket();
        
        if (length == null)
            return new ArrayTypeNode(Type(), constancy, pos);
        else
            return new ArrayTypeNode(Type(), length, constancy, pos);
    }
    private PointerTypeNode FunctionType(boolean constancy) {
        Position pos = current.coordinates().starting();
        nextToken();
        return new PointerTypeNode(Signature(), constancy, pos);
    }
    private FunctionTypeNode Signature() {
        Position pos = current.coordinates().starting();
        BlockDeclNode<FunctionTypeNode.ArgumentNode> args = 
                new BlockDeclNode<FunctionTypeNode.ArgumentNode>(Parameters());
        BaseTypeNode result = Type();
        
        return new FunctionTypeNode(result, args, false, pos);
    }
    private List<FunctionTypeNode.ArgumentNode> Parameters() {
        LeftBracket();
        List<FunctionTypeNode.ArgumentNode> args = ParameterList();
        RightBracket();
        
        return args;
    }
    private List<FunctionTypeNode.ArgumentNode> ParameterList() {
        List<FunctionTypeNode.ArgumentNode> args = 
                new LinkedList<FunctionTypeNode.ArgumentNode>();
        
        args.addAll(ParameterDecl());
        while (current.type().is(Token.Type.COMMA)) {
            nextToken();
            args.addAll(ParameterDecl());
        }
        
        return args;
    }
    private List<FunctionTypeNode.ArgumentNode> ParameterDecl() {
        if (current.type().is(Token.Type.IDENTIFIER)) {
            return IdentifierList();
        } else if (current.type().is(Token.Type.Type)) {
            Position pos = current.coordinates().starting();
            return Arrays.asList(
                    new FunctionTypeNode.ArgumentNode("", Type(), pos));
        } else {
            return new LinkedList<FunctionTypeNode.ArgumentNode>();
        }
    }
    private List<FunctionTypeNode.ArgumentNode> IdentifierList() {
        List<FunctionTypeNode.ArgumentNode> vars = 
                new LinkedList<FunctionTypeNode.ArgumentNode>();
        
        Position pos = current.coordinates().starting();
        String name = Identifier();
        if (current.type().is(Token.Type.COMMA)) {
            nextToken();
            vars.addAll(IdentifierList());
            vars.add(
                0,
                new FunctionTypeNode.ArgumentNode(name, vars.get(0).type, pos)
            );
        } else {
            BaseTypeNode type = Type();
            vars.add(0, new FunctionTypeNode.ArgumentNode(name, type, pos));
        }
        
        return vars;
    }
    
    
    
    
    private List<VariableDeclNode> FieldsList() {
        List<VariableDeclNode> vars = new LinkedList<VariableDeclNode>();
        
        Position pos = current.coordinates().starting();
        String name = Identifier();
        if (current.type().is(Token.Type.COMMA)) {
            nextToken();
            vars.addAll(FieldsList());
            vars.add(0, new VariableDeclNode(name, vars.get(0).type, pos));
        } else {
            BaseTypeNode type = Type();
            vars.add(0, new VariableDeclNode(name, type, pos));
        }
        
        return vars;
    }
    private PointerTypeNode PointerType(boolean constancy) {
        Position pos = current.coordinates().starting();
        nextToken();
        return new PointerTypeNode(Type(), constancy, pos);
    }
    private PrimitiveTypeNode PrimitiveType(boolean constancy) {
        PrimitiveTypeNode type = null;
        Position pos = current.coordinates().starting();
        
        switch (current.type()) {
            case INT:
                type = new PrimitiveTypeNode(
                        PrimitiveTypeNode.Type.INT, 
                        constancy,
                        pos);
                break;
            case LONG:
                type = new PrimitiveTypeNode(
                        PrimitiveTypeNode.Type.LONG, 
                        constancy,
                        pos);
                break;
            case DOUBLE:
                type = new PrimitiveTypeNode(
                        PrimitiveTypeNode.Type.DOUBLE, 
                        constancy,
                        pos);
                break;
            case FLOAT:
                type = new PrimitiveTypeNode(
                        PrimitiveTypeNode.Type.FLOAT, 
                        constancy,
                        pos);
                break;
            case BOOL:
                type = new PrimitiveTypeNode(
                        PrimitiveTypeNode.Type.BOOL, 
                        constancy,
                        pos);
                break;
            case CHAR:
                type = new PrimitiveTypeNode(
                        PrimitiveTypeNode.Type.CHAR, 
                        constancy,
                        pos);
                break;
            case VOID:
                type = new PrimitiveTypeNode(
                        PrimitiveTypeNode.Type.VOID, 
                        constancy,
                        pos);
                break;
            default:
                Logger.logUnexpectedToken(
                        current.type(), 
                        Token.Type.PrimitiveType, 
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
    private BlockNode<Statement> Code() {
        LeftBrace();
        BlockNode<Statement> block = Block();
        RightBrace();
        
        return block;
    } 
    
    private BlockNode<Statement> Block() {
        BlockNode<Statement> block = new BlockNode<Statement>();
        
      
        while (current.type().is(
            new Token.Type[] {
                Token.Type.FirstOfControlStructure, 
                Token.Type.FirstOfExpression,
                Token.Type.VAR
            })
        ) {
            
            if (current.type().is(
                new Token.Type[] {
                    Token.Type.FirstOfControlStructure, 
                    Token.Type.VAR
                })
            ) {
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
                }
            } else if(current.type().is(Token.Type.FirstOfExpression)) {
                block.addChild(Expression());
                Semicolon();
            }
        }
        
        return block;
    }
    private ForNode For() {
        BlockNode init = null;
        ExpressionNode condition = null;
        BlockNode<ExpressionNode> step = new BlockNode<ExpressionNode>();
        
        Position pos = current.coordinates().starting();
        nextToken();
        LeftBracket();

        if (current.type().is(Token.Type.VAR)) {
            init = VariableDecl();
        } else if (current.type().is(Token.Type.FirstOfExpression)) {
            init = new BlockNode<ExpressionNode>();
            init.addChild(Expression());
            
            while (current.type().is(Token.Type.COMMA)) {
                nextToken();
                init.addChild(Expression());
            }
        }
        Semicolon();
        if (current.type().is(Token.Type.FirstOfExpression)) {
            condition = Expression();
        }
        Semicolon();
        if (current.type().is(Token.Type.FirstOfExpression)) {
            step.addChild(Expression());
            
            while (current.type().is(Token.Type.COMMA)) {
                nextToken();
                step.addChild(Expression());
            }
        }
        
        RightBracket();
        BlockNode<Statement> code = Code();
        
        return new ForNode(init, condition, step, code, pos);
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
        Position pos = current.coordinates().starting();
        nextToken();
        
        
        LeftBracket();
        ExpressionNode expr = Expression();
        RightBracket();
        
        BlockNode<Statement> block = Code();
        ElseNode elseNode = null;
        
        if (current.type().is(Token.Type.ELSE)) {
            Position elsepos = current.coordinates().starting();
            nextToken();
            
            if (current.type().is(Token.Type.IF)) {
                elseNode = new ElseNode(new BlockNode<Statement>(If()), elsepos);
            } else {
                BlockNode<Statement> code = Code();
                elseNode = new ElseNode(code, elsepos);
            }
        }
        return new IfNode(expr, block, elseNode, pos);
    }
    private WhileNode While() {
        Position pos = current.coordinates().starting();
        nextToken();
        
        LeftBracket();
        ExpressionNode expr = Expression();
        RightBracket();
        
        BlockNode<Statement> block = Code();
        
        return new WhileNode(expr, block, pos);
    }
    private DoWhileNode DoWhile() {
        Position pos = current.coordinates().starting();
        nextToken();
        
        BlockNode<Statement> block = Code();
        ExpressionNode cond = null;
        if (checkTokens(current, Token.Type.WHILE)) {
            nextToken();
            
            LeftBracket();
            cond = Expression();
            RightBracket();
        }
        
        return new DoWhileNode(cond, block, pos);
    }
    private RunNode NewThread() {
        Position pos = current.coordinates().starting();
        nextToken();
        
        return new RunNode(Expression(), pos);
    }
    private SwitchNode Switch() {
        Position pos = current.coordinates().starting();
        nextToken();
        
        LeftBracket();
        ExpressionNode expr = Expression();
        RightBracket();
        
        LeftBrace();
        BlockNode<CaseNode> cases = new BlockNode<CaseNode>(Case());
        
        while (current.type().is(Token.Type.CASE)) {
            cases.addChild(Case());
        }
        DefaultNode defaultNode = null;
        if (current.type().is(Token.Type.DEFAULT)) {
            Position dpos = current.coordinates().starting();
            defaultNode = new DefaultNode(Default(), dpos);
        }
        RightBrace();
        
        return new SwitchNode(expr, cases, defaultNode, pos);
    }
    private CaseNode Case() {
        Position pos = current.coordinates().starting();
        nextToken();
        
        ExpressionNode expr = Expression();
        Colon();
        BlockNode<Statement> block = Block();
        
        return new CaseNode(expr, block, pos);
    }
    private BlockNode<Statement> Default() {
        nextToken();
        
        Colon();
        return Block();
    }
    private void Colon() {
        if (checkTokens(current, Token.Type.COLON))
            nextToken();
    }
    private ReturnNode Return() {
        Position pos = current.coordinates().starting();
        nextToken();
        
        if (current.type().is(Token.Type.FirstOfExpression)) {
            return new ReturnNode(Expression(), pos);
        } else {            
            return new ReturnNode(pos);
        }
    }
    private BreakNode Break() {
        Position pos = current.coordinates().starting();
        nextToken();
        return new BreakNode(pos);
    }
    private ContinueNode Continue() {
        Position pos = current.coordinates().starting();
        nextToken();
        return new ContinueNode(pos);
    }
    private LockNode Lock() {
        Position pos = current.coordinates().starting();
        nextToken();
        return new LockNode(Code(), pos);
    }
    private BarrierNode Barrier() {
        Position pos = current.coordinates().starting();
        nextToken();
        return new BarrierNode(pos);
    }
    
    
    
    
    private ExpressionNode Expression() {
        ExpressionNode result = BoolExpression();
        
        while (current.type().is(Token.Type.Assignment)) {
            BinaryOperationNode.Operation operation = null;
            Position pos = current.coordinates().starting();
            switch (current.type()) {
                case ASSIGN:
                    operation = 
                        BinaryOperationNode.Operation.ASSIGN;
                    break;
                case PLUS_ASSIGN:
                    operation = 
                        BinaryOperationNode.Operation.PLUS_ASSIGN;
                    break;
                case MINUS_ASSIGN:
                    operation = 
                        BinaryOperationNode.Operation.MINUS_ASSIGN;
                    break;
                case MUL_ASSIGN:
                    operation = 
                        BinaryOperationNode.Operation.MUL_ASSIGN;
                    break;
                case DIV_ASSIGN:
                    operation = 
                        BinaryOperationNode.Operation.DIV_ASSIGN;
                    break;
                case MOD_ASSIGN:
                    operation = 
                        BinaryOperationNode.Operation.MOD_ASSIGN;
                    break;
                case BITWISE_OR_ASSIGN:
                    operation = 
                        BinaryOperationNode.Operation.BITWISE_OR_ASSIGN;
                    break;
                case BITWISE_SHIFT_LEFT_ASSIGN:
                    operation = 
                        BinaryOperationNode.Operation.BITWISE_SHIFT_LEFT_ASSIGN;
                    break;
                case BITWISE_SHIFT_RIGHT_ASSIGN:
                    operation = 
                        BinaryOperationNode.Operation.BITWISE_SHIFT_RIGHT_ASSIGN;
                    break;
                case BITWISE_XOR_ASSIGN:
                    operation =
                        BinaryOperationNode.Operation.BITWISE_XOR_ASSIGN;
                    break;
                case BITWISE_AND_ASSIGN:
                    operation = 
                        BinaryOperationNode.Operation.BITWISE_AND_ASSIGN;
                    break;
                default:
                    return BinaryOperationNode.InvalidNode(pos);
            }
            nextToken();
            
            ExpressionNode right = BoolExpression();
            
            result = new BinaryOperationNode(operation, result, right, pos);
        }
        
        return result;
    }
    
    private ExpressionNode BoolExpression() {
        ExpressionNode result = ABoolExpression();
        
        while (current.type().is(Token.Type.BOOL_OR)) {
            Position pos = current.coordinates().starting();
            nextToken();
            ExpressionNode right = ABoolExpression();
            result = 
                new BinaryOperationNode(
                    BinaryOperationNode.Operation.BOOL_OR, 
                    result, 
                    right, 
                    pos
                );
        }
        
        return result;
    }
    private ExpressionNode ABoolExpression() {
        ExpressionNode result = BBoolExpression();
        
        while (current.type().is(Token.Type.BOOL_AND)) {
            Position pos = current.coordinates().starting();
            nextToken();
            ExpressionNode right = BBoolExpression();
            result = 
                new BinaryOperationNode(
                    BinaryOperationNode.Operation.BOOL_AND, 
                    result, 
                    right, 
                    pos
                );
        }
        
        return result;
    }
    private ExpressionNode BBoolExpression() {
        ExpressionNode result = GExpression();
        
        while (current.type().is(Token.Type.BITWISE_OR)) {
            Position pos = current.coordinates().starting();
            nextToken();
            ExpressionNode right = GExpression();
            result = 
                new BinaryOperationNode(
                    BinaryOperationNode.Operation.BITWISE_OR, 
                    result, 
                    right, 
                    pos
                );
        }
        
        return result;
    }
    private ExpressionNode GExpression() {
        ExpressionNode result = HExpression();

        while (current.type().is(Token.Type.BITWISE_XOR)) {
            Position pos = current.coordinates().starting();
            nextToken();
            ExpressionNode right = HExpression();
            result = 
                new BinaryOperationNode(
                    BinaryOperationNode.Operation.BITWISE_XOR, 
                    result, 
                    right, 
                    pos
                );
        }
        
        return result;
    }
    private ExpressionNode HExpression() {
        ExpressionNode result = IExpression();
        
        while (current.type().is(Token.Type.AMPERSAND)) {
            Position pos = current.coordinates().starting();
            nextToken();
            ExpressionNode right = IExpression();
            result = 
                new BinaryOperationNode(
                    BinaryOperationNode.Operation.BITWISE_AND,
                    result, 
                    right,
                    pos
                );
        }
        
        return result;
    }
    private ExpressionNode IExpression() {
        ExpressionNode result = CBoolExpression();
        
        while (current.type().is(Token.Type.Equality)) {
            BinaryOperationNode.Operation operation = null;
            Position pos = current.coordinates().starting();
            switch (current.type()) {
                case EQUAL:
                    operation = BinaryOperationNode.Operation.EQUAL;
                    break;
                case NOT_EQUAL:
                    operation = BinaryOperationNode.Operation.NOT_EQUAL;
                    break;
                default:
                    return ExpressionNode.InvalidNode(pos);
            }
            nextToken();
            ExpressionNode right = CBoolExpression();
            
            result = new BinaryOperationNode(operation, result, right, pos);
        }
        
        return result;
    }
    private ExpressionNode CBoolExpression() {
        ExpressionNode result = DboolExpression();
        
        while (current.type().is(Token.Type.OrderRelation)) {
            BinaryOperationNode.Operation operation = null;
            Position pos = current.coordinates().starting();
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
                    return ExpressionNode.InvalidNode(pos);
            }
            nextToken();
            
            ExpressionNode right = DboolExpression();
            result = new BinaryOperationNode(operation, result, right, pos);
        }
        
        return result;
    }
    private ExpressionNode DboolExpression() {
        ExpressionNode result = AExpression();
        
        while (current.type().is(Token.Type.BitwiseShift)) {
            BinaryOperationNode.Operation operation = null;
            Position pos = current.coordinates().starting();
            switch (current.type()) {
                case BITWISE_SHIFT_LEFT:
                    operation = BinaryOperationNode.Operation.BITWISE_SHIFT_LEFT;
                    break;
                case BITWISE_SHIFT_RIGHT:
                    operation = BinaryOperationNode.Operation.BITWISE_SHIFT_RIGHT;
                    break;
                default:
                    return ExpressionNode.InvalidNode(pos);
            }
            nextToken();
            ExpressionNode right = AExpression();
            result = new BinaryOperationNode(operation, result, right, pos);
        }
        
        return result;
    }
    private ExpressionNode AExpression() {
        ExpressionNode result = BExpression();
        
        while (current.type().is(Token.Type.PlusMinus)) {
            BinaryOperationNode.Operation operation = null;
            Position pos = current.coordinates().starting();
            switch (current.type()) {
                case PLUS:
                    operation = BinaryOperationNode.Operation.PLUS;
                    break;
                case MINUS:
                    operation = BinaryOperationNode.Operation.MINUS;
                    break;
                default:
                    return ExpressionNode.InvalidNode(pos);
            }
            nextToken();
            ExpressionNode right = BExpression();
            result = new BinaryOperationNode(operation, result, right, pos);
        }
        
        return result;
    }
    private ExpressionNode BExpression() {
        ExpressionNode result = CExpression();
        
        while (current.type().is(Token.Type.MulDivMod)) {
            BinaryOperationNode.Operation operation;
            Position pos = current.coordinates().starting();
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
                    return ExpressionNode.InvalidNode(pos);
            }
            nextToken();
            ExpressionNode right = CExpression();
            result = new BinaryOperationNode(operation, result, right, pos);
        }
        
        return result;
    }
    private ExpressionNode CExpression() {
        if (current.type() == Token.Type.LEFT_BRACKET) {
            nextToken();
            if (current.type().is(Token.Type.VAR)) {
                BaseTypeNode type = Type();
                RightBracket();
                Position pos = current.coordinates().starting();
                
                return new CastNode(type, DExpression(), pos);
            } else {
                ExpressionNode node = Expression();
                RightBracket();
                return node;
            }
        } else {
            return DExpression();
        }
    }
    private ExpressionNode DExpression() {
        Position pos = current.coordinates().starting();
        if (current.type().is(Token.Type.PlusMinus)) {
            switch (current.type()) {
                case PLUS:
                    nextToken();
                    return EExpression();
                case MINUS:
                    nextToken();
                    return new UnaryOperationNode(
                        UnaryOperationNode.Operation.MINUS, 
                        EExpression(), 
                        pos);
                default:
                    nextToken();
                    return ExpressionNode.InvalidNode(pos);
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
                    return ExpressionNode.InvalidNode(pos);
            }
            nextToken();
            ExpressionNode node = EExpression();

            return new UnaryOperationNode(operation, node, pos);
        } else {
            return RefDeref();
        }
    }
    private ExpressionNode RefDeref() {
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
                    return ExpressionNode.InvalidNode(pos);
            }
            nextToken();
            return new UnaryOperationNode(operation, RefDeref(), pos);
        } else {
            return EExpression();
        }
    }
    private ExpressionNode EExpression() {
        ExpressionNode node = FExpression();
        
        if (current.type().is(Token.Type.IncDec)) {
            UnaryOperationNode.Operation operation;
            Position pos = current.coordinates().starting();
            switch (current.type()) {
                case INC:
                    operation = UnaryOperationNode.Operation.POST_INC;
                    break;
                case DEC:
                    operation = UnaryOperationNode.Operation.POST_DEC;
                    break;
                default:
                    return ExpressionNode.InvalidNode(pos);
            }
            nextToken();
            node = new UnaryOperationNode(operation, node, pos);
        }
        
        return node;
    }
    private ExpressionNode FExpression() {
        ExpressionNode node = JExpression();
        
        while (current.type().is(
            new Token.Type[] {
                Token.Type.MEMBER_SELECT, 
                Token.Type.LEFT_BRACKET,
                Token.Type.LEFT_SQUARE_BRACKET
            })
        ) {
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
                
                BlockNode<ExpressionNode> args = new BlockNode<ExpressionNode>();
                if (current.type().is(Token.Type.FirstOfExpression)) {           
                    args.addChild(Expression());
                    while (current.type() == Token.Type.COMMA) {
                        nextToken();
                        args.addChild(Expression());
                    }
                }
                RightBracket();

                node = new CallNode(node, args, pos);
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
    private ExpressionNode JExpression() {
        if (current.type().is(Token.Type.Constant)) {
            return Const();
        } else {
            Position pos = current.coordinates().starting();
            return new VariableLeaf(Identifier(), pos);
        }
    }    
    
    private ExpressionNode Const() {
        ExpressionNode node;
        Position pos = current.coordinates().starting();
        switch (current.type()) {
            case CONST_CHAR:
                node = new CharConstantLeaf((int)current.value(), pos);
                break;
            case CONST_DOUBLE:
                node = new DoubleConstantLeaf((double)current.value(), pos);
                break;
            case CONST_INT:
                node = new IntegerConstantLeaf((int)current.value(), pos);
                break;
            case TRUE:
                node = new BoolConstantLeaf(Boolean.TRUE, pos);
                break;
            case FALSE:
                node = new BoolConstantLeaf(Boolean.FALSE, pos);
                break;
            default:
                node = ExpressionNode.InvalidNode(pos);
        }
        
        nextToken();
        return node;
    }

    private StructDeclNode StructDecl() {
        Position pos = current.coordinates().starting();
        nextToken();
        
        String structName = Identifier();
        BlockDeclNode decls = new BlockDeclNode();
        
        LeftBrace();
        while (current.type().is(Token.Type.IDENTIFIER)) {
            decls.addChildren(FieldDecl());
        }
        RightBrace();
        
        return new StructDeclNode(
                structName, 
                new StructTypeNode(structName, false, pos),
                decls,
                pos);
    }
    private List<VariableDeclNode> FieldDecl() {
        List<VariableDeclNode> fields = FieldsList();
        Semicolon();
        
        return fields;
    }
   
    private BlockDeclNode VariableDecl() {        
        nextToken();
        
        if (current.type().is(Token.Type.LEFT_BRACKET)) {
            LeftBracket();
            BlockDeclNode decls = new BlockDeclNode();
            decls.addChildren(VariableSpec());
            Semicolon();
            
            while (current.type().is(Token.Type.IDENTIFIER)) {
                decls.addChildren(VariableSpec());
                Semicolon();
            }
            
            RightBracket();
            
            return decls;
        } else {
            return new BlockDeclNode(VariableSpec());
        }
    }
    
    private List<VariableDeclNode> VariableSpec() {
        List<VariableDeclNode> nodes = new LinkedList<VariableDeclNode>();
        
        Position declPos = current.coordinates().starting();
        String name = Identifier();
        BaseTypeNode type;
        
        if (current.type().is(Token.Type.ASSIGN)) {
            nextToken();
            ExpressionNode expr = Expression();
            
            if (current.type().is(Token.Type.COMMA)) {
                nextToken();
                nodes.addAll(VariableSpec());
                
                type = ((VariableDeclNode)nodes.get(nodes.size() - 1)).type;
            } else {
                type = Type();
            }
            nodes.add(0, new VariableDeclNode(name, type, declPos, expr));
        } else if (current.type().is(Token.Type.COMMA)) {
            nextToken();
            nodes.addAll(VariableSpec());
            type = ((VariableDeclNode)nodes.get(nodes.size() - 1)).type;
            
            nodes.add(0, new VariableDeclNode(name, type, declPos));
        } else {
            type = Type();
            return Arrays.asList(new VariableDeclNode(name, type, declPos));
        }
        
        return nodes;
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