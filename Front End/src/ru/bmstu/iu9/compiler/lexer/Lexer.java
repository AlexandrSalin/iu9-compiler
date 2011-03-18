/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.lexer;

import com.google.gson.*;
import java.util.LinkedList;
import java.util.List;
import java.io.PrintWriter;
import ru.bmstu.iu9.compiler.lexer.Program.CodePointIterator;

/**
 *
 * @author maggot
 */
public class Lexer {
    public Lexer(String program) {
        this.program = new Program(program);
        this.iterator = this.program.iterator();
    }
    
    public void run() {
        while(hasNext()) {
            
            
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
    
    private Token nextToken() {
        if (skipWhitespaces()) {
            return null;
        }
        
        CodePoint current = iterator.current();
        KeyWordToken.Type tokenType = null;
        switch (iterator.current().value()) {
            case '.':
                iterator.advance(1);
                if (Character.isDigit(iterator.current().value())) {
                    while (iterator.hasNext() && (
                        Character.isDigit(iterator.current().value()) ||
                        iterator.current().value() == 'E' ||
                        iterator.current().value() == 'e')) {
                        continue;
                    }
                    
                    return new DoubleToken(new Fragment(
                            current.position(), iterator.current().position()),
                            Double.parseDouble(program.toString().substring(
                                current.position().index(), 
                                iterator.current().position().index())));
                }
                tokenType = KeyWordToken.Type.MEMBER_SELECT;
                break;
            case '~':
                iterator.advance(1);
                tokenType = KeyWordToken.Type.PLUS;
                break;
            case '{':
                iterator.advance(1);
                tokenType = KeyWordToken.Type.LEFT_BRACE;
                break;
            case '}':
                iterator.advance(1);
                tokenType = KeyWordToken.Type.RIGHT_BRACE;
                break;
            case '[':
                iterator.advance(1);
                tokenType = KeyWordToken.Type.LEFT_SQUARE_BRACKET;
                break;
            case ']':
                iterator.advance(1);
                tokenType = KeyWordToken.Type.RIGHT_SQUARE_BRACKET;
                break;
            case '(':
                iterator.advance(1);
                tokenType = KeyWordToken.Type.LEFT_BRACKET;
                break;
            case ')':
                iterator.advance(1);
                tokenType = KeyWordToken.Type.RIGHT_BRACKET;
                break;
            case ':':
                iterator.advance(1);
                tokenType = KeyWordToken.Type.COLON;
                break;
            case ';':
                iterator.advance(1);
                tokenType = KeyWordToken.Type.SEMICOLON;
                break;
            case '+':
                iterator.advance(1); 
                if (iterator.current().value() == '+') {   
                    iterator.advance(1); 
                    tokenType = KeyWordToken.Type.INC;
                } else if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = KeyWordToken.Type.PLUS_ASSIGN;
                } else {
                    tokenType = KeyWordToken.Type.PLUS;
                }
                break;
            case '-':
                iterator.advance(1);
                if(iterator.current().value() == '-') {
                    iterator.advance(1);                    
                    tokenType = KeyWordToken.Type.DEC;
                } else if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = KeyWordToken.Type.PLUS_ASSIGN;
                } else {
                    tokenType = KeyWordToken.Type.MINUS;
                }
                break;
            case '!':
                iterator.advance(1);
                if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = KeyWordToken.Type.NOT_EQUAL;
                } else {
                    tokenType = KeyWordToken.Type.BOOL_NOT;
                }
                break;
            case '&':
                iterator.advance(1);
                if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = KeyWordToken.Type.BITWISE_AND_ASSIGN;
                } else {
                    tokenType = KeyWordToken.Type.AMPERSAND;
                }
                break;
            case '*':
                iterator.advance(1);
                if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = KeyWordToken.Type.MUL_ASSIGN;
                } else {
                    tokenType = KeyWordToken.Type.ASTERISK;
                }
                break;
            case '/':
                iterator.advance(1);
                if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = KeyWordToken.Type.DIV_ASSIGN;
                } else {
                    tokenType = KeyWordToken.Type.DIV;
                }
                break;
            case '%':
                iterator.advance(1);
                if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = KeyWordToken.Type.MOD_ASSIGN;
                } else {
                    tokenType = KeyWordToken.Type.MOD;
                }
                break;
            case '^':
                iterator.advance(1);
                if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = KeyWordToken.Type.BITWISE_XOR_ASSIGN;
                } else {
                    tokenType = KeyWordToken.Type.BITWISE_XOR;
                }
                break;
            case '|':
                iterator.advance(1);
                if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = KeyWordToken.Type.BITWISE_OR_ASSIGN;
                } else {
                    tokenType = KeyWordToken.Type.BITWISE_OR;
                }
                break;
            case '=':
                iterator.advance(1);
                if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = KeyWordToken.Type.EQUAL;
                } else {
                    tokenType = KeyWordToken.Type.ASSIGN;
                }
                break;
            case '>':
                iterator.advance(1); 
                if (iterator.current().value() == '>') {   
                    iterator.advance(1); 
                    if (iterator.current().value() == '=') {
                        iterator.advance(1); 
                        tokenType = KeyWordToken.Type.BITWISE_SHIFT_RIGHT_ASSIGN;
                    } else {
                        tokenType = KeyWordToken.Type.BITWISE_SHIFT_RIGHT;
                    }
                } else if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = KeyWordToken.Type.GREATER_OR_EQUAL;
                } else {
                    tokenType = KeyWordToken.Type.GREATER;
                }
                break;
            case '<':
                iterator.advance(1); 
                if (iterator.current().value() == '<') {   
                    iterator.advance(1); 
                    if (iterator.current().value() == '=') {
                        iterator.advance(1); 
                        tokenType = KeyWordToken.Type.BITWISE_SHIFT_RIGHT_ASSIGN;
                    } else {
                        tokenType = KeyWordToken.Type.BITWISE_SHIFT_RIGHT;
                    }
                } else if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = KeyWordToken.Type.GREATER_OR_EQUAL;
                } else {
                    tokenType = KeyWordToken.Type.GREATER;
                }
                break;
            default:
                if(Character.isDigit(iterator.current().value())) {
                    int value;
                            
                    if (iterator.current().value() == '0') {
                        iterator.advance(1);
                        if (iterator.current().value() == 'x' || 
                            iterator.current().value() == 'X') {
                            
                            iterator.advance(1);
                            current = iterator.current();
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
                    KeyWordToken.Type type = null; 
                    switch (current.value()) {
                        case 'i':
                            if (keyword.equals("int"))
                                type = KeyWordToken.Type.INT;
                            else if (keyword.equals("if"))
                                type = KeyWordToken.Type.IF;
                            break;
                        case 'f':
                            if (keyword.equals("float"))
                                type = KeyWordToken.Type.FLOAT;
                            else if (keyword.equals("false"))
                                type = KeyWordToken.Type.FALSE;
                            else if (keyword.equals("func"))
                                type = KeyWordToken.Type.FUNC;
                            break;
                        case 'd':
                            if (keyword.equals("double"))
                                type = KeyWordToken.Type.DOUBLE;
                            else if (keyword.equals("default"))
                                type = KeyWordToken.Type.DEFAULT;
                            else if (keyword.equals("do"))
                                type = KeyWordToken.Type.DO;
                            break;
                        case 'c':
                            if (keyword.equals("char"))
                                type = KeyWordToken.Type.CHAR;
                            else if (keyword.equals("continue"))
                                type = KeyWordToken.Type.CONTINUE;
                            else if (keyword.equals("case"))
                                type = KeyWordToken.Type.CASE;
                            else if (keyword.equals("const"))
                                type = KeyWordToken.Type.CONST;
                            break;
                        case 'v':
                            if (keyword.equals("void"))
                                type = KeyWordToken.Type.VOID;
                            else if (keyword.equals("var"))
                                type = KeyWordToken.Type.VAR;
                            break;
                        case 's':
                            if (keyword.equals("struct"))
                                type = KeyWordToken.Type.STRUCT;
                            else if (keyword.equals("switch"))
                                type = KeyWordToken.Type.SWITCH;
                            break;
                        case 'b':
                            if (keyword.equals("bool"))
                                type = KeyWordToken.Type.BOOL;
                            else if (keyword.equals("break"))
                                type = KeyWordToken.Type.BREAK;
                            else if (keyword.equals("berrier"))
                                type = KeyWordToken.Type.BARRIER;
                            break;
                        case 'r':
                            if (keyword.equals("return"))
                                type = KeyWordToken.Type.RETURN;
                            else if (keyword.equals("run"))
                                type = KeyWordToken.Type.RUN;
                            break;
                        case 'e':
                            if (keyword.equals("else"))
                                type = KeyWordToken.Type.ELSE;
                            break;
                        case 'w':
                            if (keyword.equals("while"))
                                type = KeyWordToken.Type.WHILE;
                            break;
                        case 'l':
                            if (keyword.equals("lock"))
                                type = KeyWordToken.Type.LOCK;
                            break;
                        case 't':
                            if (keyword.equals("true"))
                                type = KeyWordToken.Type.TRUE;
                            break;                            
                    }
                    
                    if (type != null)
                        return new KeyWordToken(fragment, type);
                    else
                        return new IdentifierToken(fragment, keyword);
                    
                } else {
                    return null;
                }
        }
        
        if (tokenType != null) {
            return new KeyWordToken(new Fragment(
                current.position(), iterator.current().position()),
                tokenType);
        } else {
            return null;
        }
    }
    public boolean hasNext() {
        return !skipWhitespaces();
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
    private List<Token> tokens = new LinkedList<Token>();
}
