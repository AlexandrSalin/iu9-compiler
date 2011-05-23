package ru.bmstu.iu9.compiler.syntax;

import com.google.gson.*;
import java.io.*;
import ru.bmstu.iu9.compiler.*;
import java.util.*;
import ru.bmstu.iu9.compiler.lexis.token.*;
import ru.bmstu.iu9.compiler.syntax.tree.*;

/**
 * Класс Parser осуществляет синтаксический анализ текста программы на основании 
 * последовательности {@link ru.bmstu.iu9.compiler.lexis.token.Token Token}ов,
 * сгенерированных {@link ru.bmstu.iu9.compiler.lexis.Lexer Lexer}ом и 
 * построение дерева синтаксического анализа.
 * В последствии это дерево может быть сериализовано в файл в формате json.
 * <br/>
 * Пример использования класса Parser:
 * <pre>
 * Lexer lexer = new Lexer("program.src");
 * lexer.run();
 * lexer.toJson("tokens.json");
 * 
 * Parser parser = new Parser("tokens.src");
 * parser.process();
 * parser.toJson("parser_tree.json");
 * </pre>
 * 
 * @author anton.bobukh
 * @see ru.bmstu.iu9.compiler.lexis.Lexer
 * @see ru.bmstu.iu9.compiler.lexis.token.Token
 */
public class Parser {
    /**
     * Создает объект Parser.
     * 
     * Создает объект Parser, который будет производить синтаксический анализ
     * текста программы, представленного последовательностью 
     * {@link ru.bmstu.iu9.compiler.lexis.token.Token Token}ов.
     * 
     * @param tokens массив {@link ru.bmstu.iu9.compiler.lexis.token.Token Token}ов, 
     *               сгенерированный лексическим анализатором
     */
    public Parser(final Token[] tokens) {
        iterator = new TokenIterator(tokens);
    }
    /**
     * Создает объект Parser.
     * 
     * Создает объект Parser, который будет производить синтаксический анализ
     * текста программы, представленного последовательностью 
     * {@link ru.bmstu.iu9.compiler.lexis.token.Token Token}ов.
     * 
     * @param filename имя файла, в который был сериализован массив 
     *                 {@link ru.bmstu.iu9.compiler.lexis.token.Token Token}ов, 
     *                 сгенерированный лексическим анализатором
     */
    public Parser(String filename) {
        BufferedReader reader = null;
        
        try {
            Gson gson = 
                new GsonBuilder().
                    registerTypeHierarchyAdapter(
                        Token.class, 
                        new Token.TokenAdapter()
                    ).
                    create();
            
            reader = new BufferedReader(new FileReader(filename));
            Token[] tokens = gson.fromJson(reader, Token[].class);
            iterator = new TokenIterator(tokens);
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
    
    /**
     * Класс-итератор по массиву 
     * {@link ru.bmstu.iu9.compiler.lexis.token.Token Token}ов.
     * 
     * Класс-итератор по массиву 
     * {@link ru.bmstu.iu9.compiler.lexis.token.Token Token}ов. Расширяет 
     * интерфейс {@link java.util.Iterator<Token> Iterator<Token>}, добавляя
     * возможность доступа к текущему токену. Содержит в себе коллекцию, по
     * которой происходит итерирование, что позволяет не отводить отдельную 
     * переменную для коллекции.
     */
    private class TokenIterator implements Iterator<Token> {
        /**
         * Создает класс-итератор по массиву токенов.
         * 
         * @param tokens массив {@link ru.bmstu.iu9.compiler.lexis.token.Token Token}ов,
         *               по которому будет производиться итерирование
         */
        public TokenIterator(final Token[] tokens) {
            this.tokens = tokens;
        }
        
        public boolean hasNext() {
            return position < tokens.length;
        }

        public Token next() {
            current = tokens[position++];
            return current;
        }
        
        /**
         * Возвращает текущий {@link ru.bmstu.iu9.compiler.lexis.token.Token Token} 
         * без инкрементирования счетчика.
         * 
         * @return Текущий {@link ru.bmstu.iu9.compiler.lexis.token.Token Token}
         */
        public Token current() {
            return current;
        }

        public void remove() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
        private Token current;
        private int position = 0;
        private final Token[] tokens;
    }
    
    /**
     * Сериализует дерево синтаксического разбора в файл в формате json.
     * 
     * Сериализует сгенерированное в процессе синтаксического анализа дерево
     * синтаксического разбора в файл с указанным именем в формате json.
     * 
     * @param filename имя файла, в который будет производиться сериализация
     */
    public void toJson(String filename) {
        PrintWriter writer = null;
        
        try {
            Gson gson = new GsonBuilder().
                setPrettyPrinting().
                registerTypeHierarchyAdapter(
                    BlockNode.class, 
                    new BlockNode.BlockNodeAdapter()).
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
        
        Parser parser = new Parser("C:\\Users\\Bobukh\\Documents\\NetBeansProjects\\Front End\\src\\output.json");
        parser.process();
        parser.toJson("C:\\Users\\Bobukh\\Documents\\NetBeansProjects\\Front End\\src\\parse_tree.json");
    }
    
    /**
     * Осуществляет синтаксический анализ и построение дерева синтаксического
     * разбора.
     * 
     * @return Корневой узел дерева синтаксического разбора
     */
    public BlockNode process() {
        Program();
        return parseTree;
    }
    
    /**
     * Переходит к следующему {@link ru.bmstu.iu9.compiler.lexis.token.Token Token}у.
     */
    private void nextToken() {
        if (iterator.hasNext()) {
            iterator.next();
        }
    }
    
    /**
     * Итератор по токенам, полученным из лексера
     */
    private TokenIterator iterator;
    private BlockNode<Statement> parseTree = new BlockNode<Statement>();
    
    private void Program() {
        nextToken();
        while (iterator.current().type().is(
                new Token.Type[] { 
                    Token.Type.VAR, Token.Type.FUNC, Token.Type.STRUCT
            })) {

            try {
                if(iterator.current().type().is(Token.Type.FUNC)) {
                    parseTree.addChild(FunctionDecl());
                } else if (iterator.current().type().is(Token.Type.STRUCT)) {
                    parseTree.addChild(StructDecl());
                    Semicolon();
                } else if (iterator.current().type().is(Token.Type.VAR)){
                    parseTree.addChild(VariableDecl());
                    Semicolon();
                }
            } catch(SyntaxException ex) {
                continue;
            }
        }
    }
    private FunctionDeclNode FunctionDecl() throws SyntaxException {
        try {
            nextToken();
            Position pos = iterator.current().coordinates().starting();
            String name = Identifier();
            FunctionTypeNode signature = Signature();
            BlockNode<Statement> code = Code();

            return new FunctionDeclNode(name, signature, code, pos);
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }        
    }
    
    
    private BaseTypeNode Type() throws SyntaxException {
        boolean constancy = isConstant();
        
        BaseTypeNode type;
        if (iterator.current().type().is(Token.Type.PrimitiveType)) {
            type = PrimitiveType(constancy);
        } else if (iterator.current().type().is(
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
        if (iterator.current().type().is(Token.Type.CONST)) {
            nextToken();
            return true;
        } else {
            return false;
        }
    }
    private BaseTypeNode TypeLit(boolean constancy) throws SyntaxException {
        switch (iterator.current().type()) {
            case ASTERISK:
                return PointerType(constancy);
            case LEFT_SQUARE_BRACKET:
                return ArrayType(constancy);
            case FUNC:
                return FunctionType(constancy);
            case STRUCT:
                return StructType(constancy);
            default:
                return BaseTypeNode.InvalidNode(
                    iterator.current().coordinates().starting()
                );
        }
    }
    private StructTypeNode StructType(boolean constancy) 
            throws SyntaxException {
        
        Position pos = iterator.current().coordinates().starting();
        nextToken();
        return new StructTypeNode(Identifier(), constancy, pos);
    }
    private ArrayTypeNode ArrayType(boolean constancy) throws SyntaxException {
        try {
            IntegerConstantLeaf length = null;
            Position pos = iterator.current().coordinates().starting();
            LeftSquareBracket();
            if (checkTokens(iterator.current(), Token.Type.CONST_INT)) {
                length = (IntegerConstantLeaf)Const();
            }
            RightSquareBracket();

            if (length == null) {
                return new ArrayTypeNode(Type(), constancy, pos);
            } else {
                return new ArrayTypeNode(Type(), length, constancy, pos);
            }
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    private PointerTypeNode FunctionType(boolean constancy) 
            throws SyntaxException{

        Position pos = iterator.current().coordinates().starting();
        nextToken();
        return new PointerTypeNode(Signature(), constancy, pos);
    }
    private FunctionTypeNode Signature() throws SyntaxException {
        try {
            Position pos = iterator.current().coordinates().starting();
            BlockDeclNode<FunctionTypeNode.ArgumentNode> args = 
                    new BlockDeclNode<FunctionTypeNode.ArgumentNode>(Parameters());
            BaseTypeNode result = Type();

            return new FunctionTypeNode(result, args, false, pos);
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    private List<FunctionTypeNode.ArgumentNode> Parameters() 
            throws SyntaxException {
        
        try {
            LeftBracket();
            List<FunctionTypeNode.ArgumentNode> args = ParameterList();
            RightBracket();

            return args;
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    private List<FunctionTypeNode.ArgumentNode> ParameterList() 
            throws SyntaxException {
        
        try{
            List<FunctionTypeNode.ArgumentNode> args = 
                    new LinkedList<FunctionTypeNode.ArgumentNode>();

            args.addAll(ParameterDecl());
            while (iterator.current().type().is(Token.Type.COMMA)) {
                nextToken();
                args.addAll(ParameterDecl());
            }

            return args;
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    private List<FunctionTypeNode.ArgumentNode> ParameterDecl()
            throws SyntaxException {
        
        try {
            if (iterator.current().type().is(Token.Type.IDENTIFIER)) {
                return IdentifierList();
            } else if (iterator.current().type().is(Token.Type.Type)) {
                Position pos = iterator.current().coordinates().starting();
                return Arrays.asList(
                        new FunctionTypeNode.ArgumentNode("", Type(), pos));
            } else {
                return new LinkedList<FunctionTypeNode.ArgumentNode>();
            }
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    
    private List<FunctionTypeNode.ArgumentNode> IdentifierList()
            throws SyntaxException {
        try {
            List<FunctionTypeNode.ArgumentNode> vars = 
                    new LinkedList<FunctionTypeNode.ArgumentNode>();

            Position pos = iterator.current().coordinates().starting();
            String name = Identifier();
            if (iterator.current().type().is(Token.Type.COMMA)) {
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
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    
    
    
    
    private List<VariableDeclNode> FieldsList() throws SyntaxException {
        try {
            List<VariableDeclNode> vars = new LinkedList<VariableDeclNode>();

            Position pos = iterator.current().coordinates().starting();
            String name = Identifier();
            if (iterator.current().type().is(Token.Type.COMMA)) {
                nextToken();
                vars.addAll(FieldsList());
                vars.add(0, new VariableDeclNode(name, vars.get(0).type, pos));
            } else {
                BaseTypeNode type = Type();
                vars.add(0, new VariableDeclNode(name, type, pos));
            }

            return vars;
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    
    private PointerTypeNode PointerType(boolean constancy) 
            throws SyntaxException {
        
        try {
            Position pos = iterator.current().coordinates().starting();
            nextToken();
            return new PointerTypeNode(Type(), constancy, pos);
        } catch(SyntaxException ex) {
            throw ex;
        }
    }
    
    private PrimitiveTypeNode PrimitiveType(boolean constancy)
            throws SyntaxException {
        
        PrimitiveTypeNode type = null;
        Position pos = iterator.current().coordinates().starting();
        
        switch (iterator.current().type()) {
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
                throw (InvalidTokenException)new InvalidTokenException(
                        iterator.current(), 
                        Token.Type.PrimitiveType
                    )
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
        nextToken();
        return type;
    }
    
    
    
    private String Identifier() throws SyntaxException {
        if (checkTokens(iterator.current(), Token.Type.IDENTIFIER)) {
            String identifier = ((IdentifierToken)iterator.current()).value();
            nextToken();
            return identifier;
        } else {
            // @todo а надо ли?
            // было просто return "";
            throw (InvalidTokenException)new InvalidTokenException(
                    iterator.current(), 
                    Token.Type.IDENTIFIER
                )
                .initPosition(iterator.current().coordinates().starting())
                .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    
    private BlockNode<Statement> Code() throws SyntaxException {
        try {
            LeftBrace();
            BlockNode<Statement> block = Block();
            RightBrace();

            return block;
        } catch(SyntaxException ex) {
            throw ex;
        }
    } 
    
    private BlockNode<Statement> Block() {
        BlockNode<Statement> block = new BlockNode<Statement>();


        while (iterator.current().type().is(
            new Token.Type[] {
                Token.Type.FirstOfControlStructure, 
                Token.Type.FirstOfExpression,
                Token.Type.VAR
        })) {
            try {
                if (iterator.current().type().is(
                    new Token.Type[] {
                        Token.Type.FirstOfControlStructure, 
                        Token.Type.VAR
                    })
                ) {
                    switch (iterator.current().type()) {
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
                } else if(iterator.current().type().is(Token.Type.FirstOfExpression)) {
                    block.addChild(Expression());
                    Semicolon();
                }
            } catch(SyntaxException ex) {
                continue;
            }
        }

        return block;
    }
    private ForNode For() throws SyntaxException {
        try {
            BlockNode init = null;
            ExpressionNode condition = null;
            BlockNode<ExpressionNode> step = new BlockNode<ExpressionNode>();

            Position pos = iterator.current().coordinates().starting();
            nextToken();
            LeftBracket();

            if (iterator.current().type().is(Token.Type.VAR)) {
                init = VariableDecl();
            } else if (iterator.current().type().is(Token.Type.FirstOfExpression)) {
                init = new BlockNode<ExpressionNode>();
                init.addChild(Expression());

                while (iterator.current().type().is(Token.Type.COMMA)) {
                    nextToken();
                    init.addChild(Expression());
                }
            }
            Semicolon();
            if (iterator.current().type().is(Token.Type.FirstOfExpression)) {
                condition = Expression();
            }
            Semicolon();
            if (iterator.current().type().is(Token.Type.FirstOfExpression)) {
                step.addChild(Expression());

                while (iterator.current().type().is(Token.Type.COMMA)) {
                    nextToken();
                    step.addChild(Expression());
                }
            }

            RightBracket();
            BlockNode<Statement> code = Code();

            return new ForNode(init, condition, step, code, pos);
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    private void Semicolon() throws SyntaxException {
        if (checkTokens(iterator.current(), Token.Type.SEMICOLON)) {
            nextToken();
        } else {
            throw (InvalidTokenException)new InvalidTokenException(
                iterator.current(), 
                Token.Type.SEMICOLON
            )
            .initPosition(iterator.current().coordinates().starting())
            .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    
    
    
    
    /*
     } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
     */
    
    
    
    
    
    private boolean checkTokens(Token found, Token.Type required)
            throws SyntaxException {
        
        if (!found.type().is(required)) {
            throw (InvalidTokenException)new InvalidTokenException(
                found, 
                required
            )
            .initPosition(found.coordinates().starting())
            .Log("ru.bmstu.iu9.compiler.syntax");
        }
        return true;
    }  
    
    
    
    
    
    
    
    
    
    
    
    private IfNode If() throws SyntaxException {
        try {
            Position pos = iterator.current().coordinates().starting();
            nextToken();


            LeftBracket();
            ExpressionNode expr = Expression();
            RightBracket();

            BlockNode<Statement> block = Code();
            ElseNode elseNode = null;

            if (iterator.current().type().is(Token.Type.ELSE)) {
                Position elsepos = iterator.current().coordinates().starting();
                nextToken();

                if (iterator.current().type().is(Token.Type.IF)) {
                    elseNode =
                        new ElseNode(new BlockNode<Statement>(If()), elsepos);
                } else {
                    BlockNode<Statement> code = Code();
                    elseNode = new ElseNode(code, elsepos);
                }
            }
            return new IfNode(expr, block, elseNode, pos);
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    private WhileNode While() throws SyntaxException {
        try {
            Position pos = iterator.current().coordinates().starting();
            nextToken();

            LeftBracket();
            ExpressionNode expr = Expression();
            RightBracket();

            BlockNode<Statement> block = Code();

            return new WhileNode(expr, block, pos);
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    private DoWhileNode DoWhile() throws SyntaxException {
        try {
            Position pos = iterator.current().coordinates().starting();
            nextToken();

            BlockNode<Statement> block = Code();
            ExpressionNode cond = null;
            if (checkTokens(iterator.current(), Token.Type.WHILE)) {
                nextToken();

                LeftBracket();
                cond = Expression();
                RightBracket();
            }

            return new DoWhileNode(cond, block, pos);
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    private RunNode NewThread() throws SyntaxException {
        try {
            Position pos = iterator.current().coordinates().starting();
            nextToken();

            return new RunNode(Expression(), pos);
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    private SwitchNode Switch() throws SyntaxException {
        try {
            Position pos = iterator.current().coordinates().starting();
            nextToken();

            LeftBracket();
            ExpressionNode expr = Expression();
            RightBracket();

            LeftBrace();
            BlockNode<CaseNode> cases = new BlockNode<CaseNode>(Case());

            while (iterator.current().type().is(Token.Type.CASE)) {
                cases.addChild(Case());
            }
            DefaultNode defaultNode = null;
            if (iterator.current().type().is(Token.Type.DEFAULT)) {
                Position dpos = iterator.current().coordinates().starting();
                defaultNode = new DefaultNode(Default(), dpos);
            }
            RightBrace();

            return new SwitchNode(expr, cases, defaultNode, pos);
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    private CaseNode Case() throws SyntaxException {
        try {
            Position pos = iterator.current().coordinates().starting();
            nextToken();

            ExpressionNode expr = Expression();
            Colon();
            BlockNode<Statement> block = Block();

            return new CaseNode(expr, block, pos);
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    
    private BlockNode<Statement> Default() throws SyntaxException {
        try {
            nextToken();

            Colon();
            return Block();
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    
    private void Colon() throws SyntaxException {
        if (checkTokens(iterator.current(), Token.Type.COLON)) {
            nextToken();
        } else {
            throw (InvalidTokenException) new InvalidTokenException(
                iterator.current(),
                Token.Type.COLON
            )
            .initPosition(iterator.current().coordinates().starting())
            .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    
    private ReturnNode Return() throws SyntaxException {
        try {
            Position pos = iterator.current().coordinates().starting();
            nextToken();

            if (iterator.current().type().is(Token.Type.FirstOfExpression)) {
                return new ReturnNode(Expression(), pos);
            } else {            
                return new ReturnNode(pos);
            }
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    private BreakNode Break() {
        Position pos = iterator.current().coordinates().starting();
        nextToken();
        return new BreakNode(pos);
    }
    private ContinueNode Continue() {
        Position pos = iterator.current().coordinates().starting();
        nextToken();
        return new ContinueNode(pos);
    }
    private LockNode Lock() throws SyntaxException {
        try {
            Position pos = iterator.current().coordinates().starting();
            nextToken();
            return new LockNode(Code(), pos);
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }    
    }
    private BarrierNode Barrier() {
        Position pos = iterator.current().coordinates().starting();
        nextToken();
        return new BarrierNode(pos);
    }
    
    
    
    
    private ExpressionNode Expression() throws SyntaxException {
        try {
            ExpressionNode result = BoolExpression();

            while (iterator.current().type().is(Token.Type.Assignment)) {
                BinaryOperationNode.Operation operation = null;
                Position pos = iterator.current().coordinates().starting();
                switch (iterator.current().type()) {
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
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    
    private ExpressionNode BoolExpression() throws SyntaxException {
        try {
            ExpressionNode result = ABoolExpression();

            while (iterator.current().type().is(Token.Type.BOOL_OR)) {
                Position pos = iterator.current().coordinates().starting();
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
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    private ExpressionNode ABoolExpression() throws SyntaxException {
        try {
            ExpressionNode result = BBoolExpression();

            while (iterator.current().type().is(Token.Type.BOOL_AND)) {
                Position pos = iterator.current().coordinates().starting();
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
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    private ExpressionNode BBoolExpression() throws SyntaxException {
        try {
            ExpressionNode result = GExpression();

            while (iterator.current().type().is(Token.Type.BITWISE_OR)) {
                Position pos = iterator.current().coordinates().starting();
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
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    private ExpressionNode GExpression() throws SyntaxException {
        try {
            ExpressionNode result = HExpression();

            while (iterator.current().type().is(Token.Type.BITWISE_XOR)) {
                Position pos = iterator.current().coordinates().starting();
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
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    private ExpressionNode HExpression() throws SyntaxException {
        try {
            ExpressionNode result = IExpression();

            while (iterator.current().type().is(Token.Type.AMPERSAND)) {
                Position pos = iterator.current().coordinates().starting();
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
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    private ExpressionNode IExpression() throws SyntaxException {
        try {
            ExpressionNode result = CBoolExpression();

            while (iterator.current().type().is(Token.Type.Equality)) {
                BinaryOperationNode.Operation operation = null;
                Position pos = iterator.current().coordinates().starting();
                switch (iterator.current().type()) {
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
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    private ExpressionNode CBoolExpression() throws SyntaxException {
        try {
            ExpressionNode result = DboolExpression();

            while (iterator.current().type().is(Token.Type.OrderRelation)) {
                BinaryOperationNode.Operation operation = null;
                Position pos = iterator.current().coordinates().starting();
                switch (iterator.current().type()) {
                    case RIGHT_ANGLE_BRACKET:
                        operation = BinaryOperationNode.Operation.GREATER;
                        break;
                    case GREATER_OR_EQUAL:
                        operation = BinaryOperationNode.Operation.GREATER_OR_EQUAL;
                        break;
                    case LEFT_ANGLE_BRACKET:
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
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    
    private ExpressionNode DboolExpression() throws SyntaxException {
        try {
            ExpressionNode result = AExpression();

            while (iterator.current().type().is(Token.Type.BitwiseShift)) {
                BinaryOperationNode.Operation operation = null;
                Position pos = iterator.current().coordinates().starting();
                switch (iterator.current().type()) {
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
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    
    private ExpressionNode AExpression() throws SyntaxException {
        try {
            ExpressionNode result = BExpression();

            while (iterator.current().type().is(Token.Type.PlusMinus)) {
                BinaryOperationNode.Operation operation = null;
                Position pos = iterator.current().coordinates().starting();
                switch (iterator.current().type()) {
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
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    
    private ExpressionNode BExpression() throws SyntaxException {
        try {
            ExpressionNode result = CExpression();

            while (iterator.current().type().is(Token.Type.MulDivMod)) {
                BinaryOperationNode.Operation operation;
                Position pos = iterator.current().coordinates().starting();
                switch (iterator.current().type()) {
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
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    private ExpressionNode CExpression() throws SyntaxException {
        try {
            if (iterator.current().type() == Token.Type.LEFT_ANGLE_BRACKET) {
                nextToken();
                BaseTypeNode type = Type();
                RightAngleBracket();
                Position pos = iterator.current().coordinates().starting();

                return new CastNode(type, DExpression(), pos);
            } else {
                return DExpression();
            }
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    
    private ExpressionNode DExpression() throws SyntaxException {
        try {
            Position pos = iterator.current().coordinates().starting();
            if (iterator.current().type().is(Token.Type.PlusMinus)) {
                switch (iterator.current().type()) {
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
            } else if (iterator.current().type().is(Token.Type.BITWISE_NOT)) {
                nextToken();
                ExpressionNode node = EExpression();

                return new UnaryOperationNode(
                    UnaryOperationNode.Operation.BITWISE_NOT,
                    node,
                    pos
                );
            } else if (iterator.current().type().is(Token.Type.BOOL_NOT)) {
                nextToken();
                ExpressionNode node = EExpression();

                return new UnaryOperationNode(
                    UnaryOperationNode.Operation.NOT,
                    node,
                    pos
                );
            } else if (iterator.current().type().is(Token.Type.IncDec)) {
                UnaryOperationNode.Operation operation;
                switch (iterator.current().type()) {
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
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    private ExpressionNode RefDeref() throws SyntaxException {
        try {
            Position pos = iterator.current().coordinates().starting();

            if (iterator.current().type().is(new Token.Type[] {
                Token.Type.AMPERSAND, Token.Type.ASTERISK
            })) {
                UnaryOperationNode.Operation operation;
                switch (iterator.current().type()) {
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
                return new UnaryOperationNode(operation, DExpression(), pos);
            } else {
                return EExpression();
            }
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    private ExpressionNode EExpression() throws SyntaxException {
        try {
            ExpressionNode node = FExpression();

            if (iterator.current().type().is(Token.Type.IncDec)) {
                UnaryOperationNode.Operation operation;
                Position pos = iterator.current().coordinates().starting();
                switch (iterator.current().type()) {
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
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    
    private ExpressionNode FExpression() throws SyntaxException {
        try {
            ExpressionNode node = JExpression();

            while (iterator.current().type().is(
                new Token.Type[] {
                    Token.Type.MEMBER_SELECT, 
                    Token.Type.LEFT_BRACKET,
                    Token.Type.LEFT_SQUARE_BRACKET
                })
            ) {
                if (iterator.current().type().is(Token.Type.MEMBER_SELECT)) {
                    Position pos = iterator.current().coordinates().starting();
                    nextToken();
                    Position varPos = iterator.current().coordinates().starting();
                    String fieldName = Identifier();

                    node = new BinaryOperationNode(
                            BinaryOperationNode.Operation.MEMBER_SELECT,
                            node,
                            new VariableLeaf(fieldName, varPos), pos);
                } else if (iterator.current().type().is(Token.Type.LEFT_BRACKET)) {
                    Position pos = iterator.current().coordinates().starting();
                    LeftBracket();

                    BlockNode<ExpressionNode> args = new BlockNode<ExpressionNode>();
                    if (iterator.current().type().is(Token.Type.FirstOfExpression)) {           
                        args.addChild(Expression());
                        while (iterator.current().type() == Token.Type.COMMA) {
                            nextToken();
                            args.addChild(Expression());
                        }
                    }
                    RightBracket();

                    node = new CallNode(node, args, pos);
                } else {
                    LeftSquareBracket();
                    Position pos = iterator.current().coordinates().starting();
                    node = new BinaryOperationNode(
                            BinaryOperationNode.Operation.ARRAY_ELEMENT, 
                            node, Expression(), pos);
                    RightSquareBracket();
                }
            }

            return node;
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }    
    private ExpressionNode JExpression() throws SyntaxException {
        try {
            if (iterator.current().type().is(Token.Type.Constant)) {
                return Const();
            } else if (iterator.current().type().is(Token.Type.LEFT_BRACKET)) {
                nextToken();
                ExpressionNode node = Expression();
                RightBracket();
                return node;
            } else {
                Position pos = iterator.current().coordinates().starting();
                return new VariableLeaf(Identifier(), pos);
            }
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }    
    
    private ExpressionNode Const() {
        ExpressionNode node;
        Position pos = iterator.current().coordinates().starting();
        switch (iterator.current().type()) {
            case CONST_CHAR:
                node = 
                    new CharConstantLeaf(
                        ((CharConstantToken)iterator.current()).value(), 
                        pos
                    );
                break;
            case CONST_DOUBLE:
                node = 
                    new DoubleConstantLeaf(
                        ((DoubleConstantToken)iterator.current()).value(), 
                        pos
                    );
                break;
            case CONST_INT:
                node = 
                    new IntegerConstantLeaf(
                        ((IntegerConstantToken)iterator.current()).value(), 
                        pos
                    );
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

    private StructDeclNode StructDecl() throws SyntaxException {
        try {
            Position pos = iterator.current().coordinates().starting();
            nextToken();

            String structName = Identifier();
            BlockDeclNode decls = new BlockDeclNode();

            LeftBrace();
            while (iterator.current().type().is(Token.Type.IDENTIFIER)) {
                decls.addChildren(FieldDecl());
            }
            RightBrace();

            return new StructDeclNode(
                    structName, 
                    new StructTypeNode(structName, false, pos),
                    decls,
                    pos);
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    
    private List<VariableDeclNode> FieldDecl() throws SyntaxException {
        try {
            List<VariableDeclNode> fields = FieldsList();
            Semicolon();

            return fields;
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
   
    private BlockDeclNode VariableDecl() throws SyntaxException {
        try {
            nextToken();

            if (iterator.current().type().is(Token.Type.LEFT_BRACKET)) {
                LeftBracket();
                BlockDeclNode decls = new BlockDeclNode();
                decls.addChildren(VariableSpec());
                Semicolon();

                while (iterator.current().type().is(Token.Type.IDENTIFIER)) {
                    decls.addChildren(VariableSpec());
                    Semicolon();
                }

                RightBracket();

                return decls;
            } else {
                return new BlockDeclNode(VariableSpec());
            }
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    
    private List<VariableDeclNode> VariableSpec() throws SyntaxException {
        try {
            List<VariableDeclNode> nodes = new LinkedList<VariableDeclNode>();

            Position declPos = iterator.current().coordinates().starting();
            String name = Identifier();
            BaseTypeNode type;

            if (iterator.current().type().is(Token.Type.ASSIGN)) {
                nextToken();
                ExpressionNode expr = Expression();

                if (iterator.current().type().is(Token.Type.COMMA)) {
                    nextToken();
                    nodes.addAll(VariableSpec());

                    type = ((VariableDeclNode)nodes.get(nodes.size() - 1)).type;
                } else if (iterator.current().type().is(Token.Type.COLON)) {
                    nextToken();
                    type = Type();
                } else {
                    throw new InvalidTokenException(
                            iterator.current(),
                            new Token.Type[] {
                                Token.Type.COLON,
                                Token.Type.COMMA
                            }
                        )
                        .initPosition(iterator.current().coordinates().starting())
                        .Log("ru.bmstu.iu9.compiler.syntax");
                }
                nodes.add(0, new VariableDeclNode(name, type, declPos, expr));
            } else if (iterator.current().type().is(Token.Type.COMMA)) {
                nextToken();
                nodes.addAll(VariableSpec());
                type = ((VariableDeclNode)nodes.get(nodes.size() - 1)).type;

                nodes.add(0, new VariableDeclNode(name, type, declPos));
            } else if (iterator.current().type().is(Token.Type.COLON)){
                nextToken();
                type = Type();
                return Arrays.asList(new VariableDeclNode(name, type, declPos));
            }

            return nodes;
        } catch(SyntaxException ex) {
            throw ex;
        } catch(Exception ex) {
            throw (NonAnalysisException)new NonAnalysisException(ex)
                    .initPosition(iterator.current().coordinates().starting())
                    .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    
    
    
    private void LeftSquareBracket() throws SyntaxException {
        if (checkTokens(iterator.current(), Token.Type.LEFT_SQUARE_BRACKET)) {
            nextToken();
        } else {
            throw (InvalidTokenException)new InvalidTokenException(
                iterator.current(), 
                Token.Type.LEFT_ANGLE_BRACKET
            )
            .initPosition(iterator.current().coordinates().starting())
            .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    private void RightSquareBracket() throws SyntaxException {
        if (checkTokens(iterator.current(), Token.Type.RIGHT_SQUARE_BRACKET)) {
            nextToken();
        } else {
            throw (InvalidTokenException)new InvalidTokenException(
                iterator.current(), 
                Token.Type.RIGHT_SQUARE_BRACKET
            )
            .initPosition(iterator.current().coordinates().starting())
            .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    private void LeftBracket() throws SyntaxException {
        if (checkTokens(iterator.current(), Token.Type.LEFT_BRACKET)) {
            nextToken();
        } else {
            throw (InvalidTokenException)new InvalidTokenException(
                iterator.current(), 
                Token.Type.LEFT_BRACKET
            )
            .initPosition(iterator.current().coordinates().starting())
            .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    private void RightBracket() throws SyntaxException {
        if (checkTokens(iterator.current(), Token.Type.RIGHT_BRACKET)) {
            nextToken();
        } else {
            throw (InvalidTokenException)new InvalidTokenException(
                iterator.current(), 
                Token.Type.RIGHT_BRACKET
            )
            .initPosition(iterator.current().coordinates().starting())
            .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    private void LeftBrace() throws SyntaxException {
        if (checkTokens(iterator.current(), Token.Type.LEFT_BRACE)) {
            nextToken();
        } else {
            throw (InvalidTokenException)new InvalidTokenException(
                iterator.current(), 
                Token.Type.LEFT_BRACE
            )
            .initPosition(iterator.current().coordinates().starting())
            .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    private void RightBrace() throws SyntaxException {
        if (checkTokens(iterator.current(), Token.Type.RIGHT_BRACE)) {
            nextToken();
        } else {
            throw (InvalidTokenException)new InvalidTokenException(
                iterator.current(), 
                Token.Type.RIGHT_BRACE
            )
            .initPosition(iterator.current().coordinates().starting())
            .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
    private void RightAngleBracket() throws SyntaxException {
        if (checkTokens(iterator.current(), Token.Type.RIGHT_ANGLE_BRACKET)) {
            nextToken();
        } else {
            throw (InvalidTokenException)new InvalidTokenException(
                iterator.current(), 
                Token.Type.RIGHT_ANGLE_BRACKET
            )
            .initPosition(iterator.current().coordinates().starting())
            .Log("ru.bmstu.iu9.compiler.syntax");
        }
    }
}