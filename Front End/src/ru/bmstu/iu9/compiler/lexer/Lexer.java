// JW9tj3vg5Uv4

package ru.bmstu.iu9.compiler.lexer;

import com.google.gson.*;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.*;
import java.util.Iterator;
import java.io.PrintWriter;
import ru.bmstu.iu9.compiler.lexer.Program.CodePointIterator;

/**
 *
 * @author maggot
 */
public class Lexer {
    public Lexer(String program) {
        this.scanner = new Scanner(program);
    }
    
    public void run() {
        for (Token token : scanner) {
            tokens.add(token);
        }
    }
    
    public void toJson(String filename) {
        PrintWriter writer = null;
        
        try {
            Gson gson = new Gson();
            writer = new PrintWriter(filename);
            gson.toJson(tokens, writer);
        } catch(java.io.IOException ex) {
            ex.printStackTrace();
        } finally {
            writer.flush();
            writer.close();
        }
    }
    
    public static void main(String[] args) {
        Lexer lex = new Lexer("  .1 123e4 123E-5 ");
        lex.run();
    }
    
    private Scanner scanner;
    private List<Token> tokens = new LinkedList<Token>();
}


class Scanner implements Iterable<Token> { 
    public Scanner(String program) {
        this.program = new Program(program);
        this.iterator = this.program.iterator();
    }
    
    @Override
    public Iterator<Token> iterator() {
        return new Iterator<Token>() {
                @Override
                public boolean hasNext() {
                    return !skipWhitespaces();
                }
                @Override
                public Token next() {
                    return nextToken();
                }
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
    }
    
    private void errorRecovery() {
        while (iterator.hasNext() && 
                (!Character.isWhitespace(iterator.current().value()) ||
                  iterator.current().value() != ';')) {
            iterator.next();
        }
    }
    
    private Token nextToken() {
        if (skipWhitespaces()) {
            return null;
        }
        
        CodePoint current = (CodePoint)iterator.current().clone();
        Token.Type tokenType = null;
                
        switch (iterator.current().value()) {
            case '.':
                Matcher matcher = 
                    Pattern.compile("\\.[0-9]+([eE][-+]?[0-9]+)?\\b").
                        matcher(program.toString()).
                            region(
                                current.position().index(), 
                                program.toString().length());

                if (matcher.lookingAt()) {
                    return new DoubleToken(
                        new Fragment(
                            iterator.current().position(), 
                            iterator.advance(matcher.group().length()).position()),
                        Double.parseDouble(matcher.group()));
                } else {
                    iterator.advance(1);
                    tokenType = Token.Type.MEMBER_SELECT;
                }
                break;
            case '~':
                iterator.advance(1);
                tokenType = Token.Type.PLUS;
                break;
            case '{':
                iterator.advance(1);
                tokenType = Token.Type.LEFT_BRACE;
                break;
            case '}':
                iterator.advance(1);
                tokenType = Token.Type.RIGHT_BRACE;
                break;
            case '[':
                iterator.advance(1);
                tokenType = Token.Type.LEFT_SQUARE_BRACKET;
                break;
            case ']':
                iterator.advance(1);
                tokenType = Token.Type.RIGHT_SQUARE_BRACKET;
                break;
            case '(':
                iterator.advance(1);
                tokenType = Token.Type.LEFT_BRACKET;
                break;
            case ')':
                iterator.advance(1);
                tokenType = Token.Type.RIGHT_BRACKET;
                break;
            case ':':
                iterator.advance(1);
                tokenType = Token.Type.COLON;
                break;
            case ',':
                iterator.advance(1);
                tokenType = Token.Type.COMMA;
                break;
            case ';':
                iterator.advance(1);
                tokenType = Token.Type.SEMICOLON;
                break;
            case '+':
                iterator.advance(1); 
                if (iterator.current().value() == '+') {   
                    iterator.advance(1); 
                    tokenType = Token.Type.INC;
                } else if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = Token.Type.PLUS_ASSIGN;
                } else {
                    tokenType = Token.Type.PLUS;
                }
                break;
            case '-':
                iterator.advance(1);
                if(iterator.current().value() == '-') {
                    iterator.advance(1);                    
                    tokenType = Token.Type.DEC;
                } else if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = Token.Type.PLUS_ASSIGN;
                } else {
                    tokenType = Token.Type.MINUS;
                }
                break;
            case '!':
                iterator.advance(1);
                if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = Token.Type.NOT_EQUAL;
                } else {
                    tokenType = Token.Type.BOOL_NOT;
                }
                break;
            case '&':
                iterator.advance(1);
                if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = Token.Type.BITWISE_AND_ASSIGN;
                } else {
                    tokenType = Token.Type.AMPERSAND;
                }
                break;
            case '*':
                iterator.advance(1);
                if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = Token.Type.MUL_ASSIGN;
                } else {
                    tokenType = Token.Type.ASTERISK;
                }
                break;
            case '/':
                iterator.advance(1);
                if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = Token.Type.DIV_ASSIGN;
                } else {
                    tokenType = Token.Type.DIV;
                }
                break;
            case '%':
                iterator.advance(1);
                if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = Token.Type.MOD_ASSIGN;
                } else {
                    tokenType = Token.Type.MOD;
                }
                break;
            case '^':
                iterator.advance(1);
                if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = Token.Type.BITWISE_XOR_ASSIGN;
                } else {
                    tokenType = Token.Type.BITWISE_XOR;
                }
                break;
            case '|':
                iterator.advance(1);
                if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = Token.Type.BITWISE_OR_ASSIGN;
                } else {
                    tokenType = Token.Type.BITWISE_OR;
                }
                break;
            case '=':
                iterator.advance(1);
                if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = Token.Type.EQUAL;
                } else {
                    tokenType = Token.Type.ASSIGN;
                }
                break;
            case '>':
                iterator.advance(1); 
                if (iterator.current().value() == '>') {   
                    iterator.advance(1); 
                    if (iterator.current().value() == '=') {
                        iterator.advance(1); 
                        tokenType = Token.Type.BITWISE_SHIFT_RIGHT_ASSIGN;
                    } else {
                        tokenType = Token.Type.BITWISE_SHIFT_RIGHT;
                    }
                } else if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = Token.Type.GREATER_OR_EQUAL;
                } else {
                    tokenType = Token.Type.GREATER;
                }
                break;
            case '<':
                iterator.advance(1); 
                if (iterator.current().value() == '<') {   
                    iterator.advance(1); 
                    if (iterator.current().value() == '=') {
                        iterator.advance(1); 
                        tokenType = Token.Type.BITWISE_SHIFT_RIGHT_ASSIGN;
                    } else {
                        tokenType = Token.Type.BITWISE_SHIFT_RIGHT;
                    }
                } else if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = Token.Type.GREATER_OR_EQUAL;
                } else {
                    tokenType = Token.Type.GREATER;
                }
                break;
            default:
                if(Character.isDigit(iterator.current().value())) {
                    Matcher matcherDouble = 
                        Pattern.compile("(([0-9]+\\.[0-9]*([eE][-+]?[0-9]+)?)|([0-9]+[eE][-+]?[0-9]+))\\b").
                            matcher(program.toString()).
                                region(
                                    current.position().index(), 
                                    program.toString().length());

                    if (matcherDouble.lookingAt()) {
                        return new DoubleToken(
                            new Fragment(
                                iterator.current().position(), 
                                iterator.advance(matcherDouble.group().length()).position()),
                            Double.parseDouble(matcherDouble.group()));
                    }
                    
                    int value;
                            
                    if (iterator.current().value() == '0') {
                        iterator.advance(1);
                        if (iterator.current().value() == 'x' || 
                            iterator.current().value() == 'X') {
                            
                            iterator.advance(1);
                            current = (CodePoint)iterator.current().clone();
                            while (iterator.hasNext() && 
                                Character.isDigit(iterator.next().value())) {
                                continue;
                            }
                            
                            value = Integer.parseInt(program.toString().substring(
                                current.position().index(), 
                                iterator.current().position().index()), 16);
                        } else {
                            while (iterator.hasNext() && 
                                Character.isDigit(iterator.next().value())) {
                                continue;
                            }
                            
                            value = Integer.parseInt(program.toString().substring(
                                current.position().index(), 
                                iterator.current().position().index()), 8);
                        }
                    } else {
                        while (iterator.hasNext() && 
                            Character.isDigit(iterator.next().value())) {
                            continue;
                        }
                        
                        value = Integer.parseInt(program.toString().substring(
                                current.position().index(), 
                                iterator.current().position().index()));
                    }
                    
                    return new IntegerToken(new Fragment(
                            current.position(), iterator.current().position()),
                            value);
                    
                } else if (Character.isLetter(iterator.current().value())) {
                    while (iterator.hasNext() &&
                        Character.isLetterOrDigit(iterator.next().value())) {
                        continue;
                    }
                    
                    String keyword = program.toString().substring(
                        current.position().index(), 
                        iterator.current().position().index());
                    
                    Fragment fragment = new Fragment(
                            current.position(), iterator.current().position());
                    Token.Type type = null; 
                    switch (current.value()) {
                        case 'i':
                            if (keyword.equals("int"))
                                type = Token.Type.INT;
                            else if (keyword.equals("if"))
                                type = Token.Type.IF;
                            break;
                        case 'f':
                            if (keyword.equals("float"))
                                type = Token.Type.FLOAT;
                            else if (keyword.equals("false"))
                                type = Token.Type.FALSE;
                            else if (keyword.equals("func"))
                                type = Token.Type.FUNC;
                            else if (keyword.equals("for"))
                                type = Token.Type.FOR;
                            break;
                        case 'd':
                            if (keyword.equals("double"))
                                type = Token.Type.DOUBLE;
                            else if (keyword.equals("default"))
                                type = Token.Type.DEFAULT;
                            else if (keyword.equals("do"))
                                type = Token.Type.DO;
                            break;
                        case 'c':
                            if (keyword.equals("char"))
                                type = Token.Type.CHAR;
                            else if (keyword.equals("continue"))
                                type = Token.Type.CONTINUE;
                            else if (keyword.equals("case"))
                                type = Token.Type.CASE;
                            else if (keyword.equals("const"))
                                type = Token.Type.CONST;
                            break;
                        case 'v':
                            if (keyword.equals("void"))
                                type = Token.Type.VOID;
                            else if (keyword.equals("var"))
                                type = Token.Type.VAR;
                            break;
                        case 's':
                            if (keyword.equals("struct"))
                                type = Token.Type.STRUCT;
                            else if (keyword.equals("switch"))
                                type = Token.Type.SWITCH;
                            break;
                        case 'b':
                            if (keyword.equals("bool"))
                                type = Token.Type.BOOL;
                            else if (keyword.equals("break"))
                                type = Token.Type.BREAK;
                            else if (keyword.equals("berrier"))
                                type = Token.Type.BARRIER;
                            break;
                        case 'r':
                            if (keyword.equals("return"))
                                type = Token.Type.RETURN;
                            else if (keyword.equals("run"))
                                type = Token.Type.RUN;
                            break;
                        case 'e':
                            if (keyword.equals("else"))
                                type = Token.Type.ELSE;
                            break;
                        case 'w':
                            if (keyword.equals("while"))
                                type = Token.Type.WHILE;
                            break;
                        case 'l':
                            if (keyword.equals("lock"))
                                type = Token.Type.LOCK;
                            break;
                        case 't':
                            if (keyword.equals("true"))
                                type = Token.Type.TRUE;
                            break;                            
                    }
                    
                    if (type != null)
                        return new KeyWordToken(fragment, type);
                    else
                        return new IdentifierToken(fragment, keyword);
                    
                } else {
                    errorRecovery();
                    return null;
                }
        }
        
        if (tokenType != null) {
            return new KeyWordToken(new Fragment(
                current.position(), iterator.current().position()),
                tokenType);
        } else {
            errorRecovery();
            return null;
        }
    }
    
    private boolean skipWhitespaces() {
        while (iterator.hasNext() && 
                Character.isWhitespace(iterator.current().value())) {
            iterator.next();
        }
        return !iterator.hasNext();
    }
    
    private CodePointIterator iterator;
    private Program program;
}
