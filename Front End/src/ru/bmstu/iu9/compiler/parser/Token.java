package ru.bmstu.iu9.compiler.parser;

import com.google.gson.InstanceCreator;

/**
 *
 * @author maggot
 */
class Token {
    public enum Type { 
        MEMBER_SELECT, INC, DEC, BITWISE_NOT, BOOL_NOT, PLUS, 
        MINUS, AMPERSAND, ASTERISK, DIV, MOD, BITWISE_SHIFT_LEFT,
        BITWISE_SHIFT_RIGHT, GREATER_OR_EQUAL, LESS_OR_EUQAL, GREATER, LESS, 
        EQUAL, NOT_EQUAL, BITWISE_XOR, BITWISE_OR, BOOL_AND, BOOL_OR, 
        ASSIGN, PLUS_ASSIGN, MINUS_ASSIGN, MUL_ASSIGN, DIV_ASSIGN, MOD_ASSIGN,
        COMMA, BITWISE_SHIFT_LEFT_ASSIGN, BITWISE_SHIFT_RIGHT_ASSIGN,
        BITWISE_AND_ASSIGN, BITWISE_OR_ASSIGN, BITWISE_XOR_ASSIGN,
        
        LEFT_BRACE, RIGHT_BRACE, LEFT_SQUARE_BRACKET,
        RIGHT_SQUARE_BRACKET, LEFT_BRACKET, RIGHT_BRACKET, COLON, SEMICOLON,
        
        INT, FLOAT, DOUBLE, CHAR, VOID, STRUCT, BOOL, CONTINUE, RETURN, BREAK, 
        ELSE, DEFAULT, CASE, SWITCH, IF, DO, WHILE, RUN, BARRIER, LOCK, TRUE,
        FALSE, FUNC, VAR, CONST, FOR,
        
        CONST_DOUBLE, CONST_INT, CONST_CHAR, IDENTIFIER };
    
    /**
     * Токен, представленный лексическим доменом и позицией лексемы в
     * тексте программы
     * @param tokenCoordinates координаты лексемы
     */
    public Token(Fragment coordinates, Type type) {
        this.coordinates = coordinates;
        this.value = null;
        this.type = type.ordinal();
    }
    public Token(Fragment coordinates, Type type, Object value) {
        this.coordinates = coordinates;
        this.value = value;
        this.type = type.ordinal();
    }
    /**
     * Токен, представленный лексическим доменом и позицией лексемы в
     * тексте программы
     * @param starting позиция первой кодовой точки лексема
     * @param ending позиция последней кодовой точки лексема
     */
    public Token(Position starting, Position ending, Type type) {
        this(new Fragment(starting, ending), type);
    }
    public Token(Position starting, Position ending, Type type, Object value) {
        this(new Fragment(starting, ending), type, value);
    }
    
    private Token() {
        this.type = -1;
        this.coordinates = null;
        this.value = null;
    }
    
    /**
     * Метод, предоставляющий доступ к координатам лексемы
     * @return Координаты лексемы
     */
    public Object value() { return value; }
    public Type tag() { return Type.values()[type]; }
    public Fragment coordinates() { return coordinates; }
    
    private int type;
    private Fragment coordinates;
    private Object value;
    
    public static class TokenInstanceCreator implements InstanceCreator<Token> {
        @Override
        public Token createInstance(java.lang.reflect.Type type) {
            return new Token();
        }
    }
}