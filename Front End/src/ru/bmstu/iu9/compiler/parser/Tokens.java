package ru.bmstu.iu9.compiler.parser;

/**
 *
 * @author maggot
 */
abstract class Token {
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
    protected Token(Fragment coordinates) {
        this.coordinates = coordinates;
    }
    /**
     * Токен, представленный лексическим доменом и позицией лексемы в
     * тексте программы
     * @param starting позиция первой кодовой точки лексема
     * @param ending позиция последней кодовой точки лексема
     */
    protected Token(Position starting, Position ending) {
        this.coordinates = new Fragment(starting, ending);
    }
    
    /**
     * Метод, предоставляющий доступ к координатам лексемы
     * @return Координаты лексемы
     */
    public Object value() { return null; }
    public Type tag() { return type; }
    public Fragment coordinates() { return coordinates; }
    
    protected Type type;
    protected final Fragment coordinates;
}


class DoubleToken extends Token {
    public DoubleToken(Fragment coordinates, double value) {
        super(coordinates);
        this.value = value;
        this.type = Type.CONST_DOUBLE;
    }
    @Override
    public Double value() { return value; }

    private double value;
}

class IntegerToken extends Token {
    public IntegerToken(Fragment coordinates, int value) {
        super(coordinates);
        this.value = value;
        this.type = Type.CONST_INT;
    }
    @Override
    public Integer value() { return value; }
    
    private int value;
}

class CharToken extends Token {
    public CharToken(Fragment coordinates, char value) {
        super(coordinates);
        this.value = value;
        this.type = Type.CONST_CHAR;
    }
    @Override
    public Character value() { return value; }
    
    private char value;
}

class IdentifierToken extends Token {
    public IdentifierToken(Fragment coordinates, String lexeme) {
        super(coordinates);
        
        this.type = Type.IDENTIFIER;
        this.value = lexeme;
    }
    @Override
    public String value() { return value; }
    
    protected String value;
}

class KeyWordToken extends Token {
    public KeyWordToken(Fragment coordinates, Type tokenType) {
        super(coordinates);
        this.type = tokenType;
    }
    
    protected Type type;
}