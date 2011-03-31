package ru.bmstu.iu9.compiler.parser;

import com.google.gson.InstanceCreator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author maggot
 */
class Token {
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
        RUN, BARRIER, LOCK, TRUE, FALSE, FUNC, VAR, CONST_KEYWORD, FOR,
        
        CONST_DOUBLE, CONST_INT, CONST_CHAR, IDENTIFIER,
        
        
        PrimitiveType(INT.value | FLOAT.value | DOUBLE.value | CHAR.value | 
                VOID.value | STRUCT.value | BOOL.value),
//        Keyword(),
        Constant(CONST_DOUBLE.value | CONST_INT.value | CONST_CHAR.value |
                TRUE.value | FALSE.value),
        Assignment(ASSIGN.value | PLUS_ASSIGN.value | MINUS_ASSIGN.value | 
                MUL_ASSIGN.value | DIV_ASSIGN.value | MOD_ASSIGN.value |
                COMMA.value | BITWISE_SHIFT_LEFT_ASSIGN.value | 
                BITWISE_SHIFT_RIGHT_ASSIGN.value | BITWISE_AND_ASSIGN.value | 
                BITWISE_OR_ASSIGN.value | BITWISE_XOR_ASSIGN.value),
        Modifier(CONST_KEYWORD.value | VAR.value),
        FirstOfExpression(LEFT_BRACKET.value | PLUS.value | MINUS.value | 
                AMPERSAND.value | ASTERISK.value | INC.value | DEC.value | 
                Constant.value | IDENTIFIER.value),
        FirstOfControlStructure(IF.value | WHILE.value | DO.value | RUN.value |
                SWITCH.value | RETURN.value | CONTINUE.value | LOCK.value |
                BARRIER.value | BREAK.value),
        Equality(EQUAL.value | NOT_EQUAL.value),
        OrderRelation(GREATER.value | LESS.value | GREATER_OR_EQUAL.value |
                LESS_OR_EQUAL.value),
        IncDec(INC.value | DEC.value),
        BitwiseShift(BITWISE_SHIFT_LEFT.value | BITWISE_SHIFT_RIGHT.value),
        MulDivMod(ASTERISK.value | DIV.value | MOD.value),
        PlusMinus(PLUS.value | MINUS.value);
        
        private Type(long value) { this.value = value; }
        private Type() { init(); }
        private void init() {
            this.value = 1 << counter;
            counter++;
        }

        public boolean is(Type[] types) {
            for (int i = 0; i < types.length; ++i) {
                if ((this.value & types[i].value) != 0)
                    return true;
            }
            return false;
        }
        public boolean is(Type type) {
            return (this.value & type.value) != 0;
        }     
        
        public static void main(String[] args) {
        }
        
        private long value;
        private static byte counter = 0;
    };

    
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