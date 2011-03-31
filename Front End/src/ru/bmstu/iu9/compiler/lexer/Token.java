package ru.bmstu.iu9.compiler.lexer;

/**
 *
 * @author maggot
 */
abstract class Token {
    public enum Type { 
        MEMBER_SELECT, INC, DEC, BITWISE_NOT, BOOL_NOT, PLUS, 
        MINUS, AMPERSAND, ASTERISK, DIV, MOD, BITWISE_SHIFT_LEFT,
        BITWISE_SHIFT_RIGHT, GREATER_OR_EQUAL, LESS_OR_EQUAL, GREATER, LESS, 
        EQUAL, NOT_EQUAL, BITWISE_XOR, BITWISE_OR, BOOL_AND, BOOL_OR, 
        
        ASSIGN, PLUS_ASSIGN, MINUS_ASSIGN, MUL_ASSIGN, DIV_ASSIGN, MOD_ASSIGN,
        COMMA, BITWISE_SHIFT_LEFT_ASSIGN, BITWISE_SHIFT_RIGHT_ASSIGN,
        BITWISE_AND_ASSIGN, BITWISE_OR_ASSIGN, BITWISE_XOR_ASSIGN,
        
        LEFT_BRACE, RIGHT_BRACE, LEFT_SQUARE_BRACKET,
        RIGHT_SQUARE_BRACKET, LEFT_BRACKET, RIGHT_BRACKET, COLON, SEMICOLON,
        
        INT, FLOAT, DOUBLE, CHAR, VOID, STRUCT, BOOL, 
        CONTINUE, RETURN, BREAK, ELSE, DEFAULT, CASE, SWITCH, IF, DO, WHILE, 
        RUN, BARRIER, LOCK, TRUE, FALSE, FUNC, VAR, CONST, FOR,
        
        CONST_DOUBLE, CONST_INT, CONST_CHAR, IDENTIFIER;
    };
    
    protected Token(Fragment coordinates, Type type) {
        this.coordinates = coordinates;
        this.type = type.ordinal();
    }
    protected Token(Position starting, Position ending, Type type) {
        this.coordinates = new Fragment(starting, ending);
        this.type = type.ordinal();
    }
    
    private final int type;
    private final Fragment coordinates;
}


abstract class ConstantToken extends Token {
    protected ConstantToken(Fragment coordinates, Type type) {
        super(coordinates, type);
    }
}

final class IntegerConstantToken extends ConstantToken {
    public IntegerConstantToken(Fragment coordinates, int value) {
        super(coordinates, Type.CONST_INT);
        this.value = value;
    }
    public IntegerConstantToken(Position starting, Position ending, int value) {
        super(new Fragment(starting, ending), Type.CONST_INT);
        this.value = value;
    }
    
    public int value() { return this.value; }
    
    private int value;
}
final class DoubleConstantToken extends ConstantToken {
    public DoubleConstantToken(Fragment coordinates, double value) {
        super(coordinates, Type.CONST_INT);
        this.value = value;
    }
    public DoubleConstantToken(Position starting, Position ending, double value) {
        super(new Fragment(starting, ending), Type.CONST_DOUBLE);
        this.value = value;
    }
    
    public double value() { return this.value; }
    
    private double value;
}
final class CharConstantToken extends ConstantToken {
    public CharConstantToken(Fragment coordinates, int value) {
        super(coordinates, Type.CONST_INT);
        this.value = value;
    }
    public CharConstantToken(Position starting, Position ending, int value) {
        super(new Fragment(starting, ending), Type.CONST_CHAR);
        this.value = value;
    }
    
    public int value() { return this.value; }
    
    private int value;
}


final class IdentifierToken extends Token {
    public IdentifierToken(Fragment coordinates, String name) {
        super(coordinates, Type.IDENTIFIER);
        this.value = name;
    }
    public IdentifierToken(Position starting, Position ending, String name) {
        super(new Fragment(starting, ending), Type.IDENTIFIER);
        this.value = name;
    }
    
    public String name() { return this.value; }
    
    private String value;
}


final class SpecialToken extends Token {
    public SpecialToken(Fragment coordinates, Type type) {
        super(coordinates, type);
    }
    public SpecialToken(Position starting, Position ending, Type type) {
        super(new Fragment(starting, ending), type);
    }
}