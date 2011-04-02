package ru.bmstu.iu9.compiler.lexis.token;

import java.util.BitSet;
import ru.bmstu.iu9.compiler.Fragment;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
public abstract class Token {
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

        CONST_DOUBLE, CONST_INT, CONST_CHAR, IDENTIFIER,


        PrimitiveType(new Type[] {
            INT, FLOAT, DOUBLE, CHAR, VOID, STRUCT, BOOL
        }),
        Constant(new Type[] {
            CONST_DOUBLE, CONST_INT, CONST_CHAR, TRUE, FALSE
        }),
        Assignment(new Type[] {
            ASSIGN, PLUS_ASSIGN, MINUS_ASSIGN, MUL_ASSIGN, DIV_ASSIGN, 
            MOD_ASSIGN, BITWISE_SHIFT_LEFT_ASSIGN, 
            BITWISE_SHIFT_RIGHT_ASSIGN, BITWISE_AND_ASSIGN, BITWISE_OR_ASSIGN, 
            BITWISE_XOR_ASSIGN
        }),
        Modifier(new Type[] { 
            CONST, VAR 
        }),
        FirstOfExpression(new Type[] {
            LEFT_BRACKET, PLUS, MINUS, AMPERSAND, ASTERISK, INC, DEC, Constant, 
            IDENTIFIER
        }),
        FirstOfControlStructure(new Type[] {
            IF, WHILE, DO, RUN, SWITCH, RETURN, CONTINUE, LOCK, BARRIER, BREAK, 
            FOR
        }),
        Equality(new Type[] { 
            EQUAL, NOT_EQUAL 
        }),
        OrderRelation(new Type[] { 
            GREATER, LESS, GREATER_OR_EQUAL, LESS_OR_EQUAL 
        }),
        IncDec(new Type[] { 
            INC, DEC 
        }),
        BitwiseShift(new Type[] { 
            BITWISE_SHIFT_LEFT, BITWISE_SHIFT_RIGHT 
        }),
        MulDivMod(new Type[] { 
            ASTERISK, DIV, MOD 
        }),
        PlusMinus(new Type[] { 
            PLUS, MINUS 
        });

        private Type(Type[] types) { 
            for(int i = 0; i < types.length; ++i) {
                this.value.or(types[i].value); 
            }
        }
        private Type() {
            this.value.set(this.ordinal());
        }

        public boolean is(Type[] types) {
            for (int i = 0; i < types.length; ++i) {
                if (this.value.intersects(types[i].value))
                    return true;
            }
            return false;
        }
        public boolean is(Type type) {
            return this.value.intersects(type.value);
        }     

        public static void main(String[] args) {
        }

        private BitSet value = new BitSet(80);
    };
    
    protected Token(Fragment coordinates, Type type) {
        this.coordinates = coordinates;
        this.type = type.ordinal();
    }
    protected Token(Position starting, Position ending, Type type) {
        this.coordinates = new Fragment(starting, ending);
        this.type = type.ordinal();
    }
    
    public Type type() { return Type.values()[this.type]; }
    public Fragment coordinates() { return this.coordinates; }
    public Object value() { return null; }
    
    private final int type;
    private final Fragment coordinates;
}
