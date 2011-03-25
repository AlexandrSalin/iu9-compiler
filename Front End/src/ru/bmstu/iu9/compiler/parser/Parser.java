package ru.bmstu.iu9.compiler.parser;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.FileReader;

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
            "C:\\Users\\maggot\\Documents\\NetBeansProjects\\ru.bmstu.iu9.compiler\\Front End\\src\\output.src");
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
    
    private void Program() {
        nextToken();
        while (current.tag() == Token.Type.FUNC || 
               isModifier() || 
               current.tag() == Token.Type.STRUCT) {
            if(current.tag() == Token.Type.FUNC) {
                nextToken();
                Type();
                Identifier();
                FuncArgList();
                Code();
            } else if (isModifier()) {
                VariableDef();
                Semicolon();
            } else if (current.tag() == Token.Type.STRUCT) {
                StructDef();
                Semicolon();
            }
        }
    }
    private void Type() {
        if (isType()) {
            nextToken();
        } else if (current.tag() == Token.Type.STRUCT) {
            nextToken();
            Identifier();
        } else {
            // ERROR
        }
    }
    private void Identifier() {
        if (isIdentifier()) {
            nextToken();
        } else {
            // ERROR
        }
    }
    private void FuncArgList() {
        LeftBracket();
        if (isModifier()) {
            nextToken();
            Type();
            Identifier();
            while (current.tag() == Token.Type.COMMA) {
                nextToken();
                Modifier();
                Type();
                Identifier();
            }
        }
        RightBracket();
    }
    private void Code() {
        LeftBrace();
        Block();
        RightBrace();
    }
    private void LeftBracket() {
        if (current.tag() == Token.Type.LEFT_BRACKET) {
            nextToken();
        } else {
            // ERROR
        }
    }
    private void RightBracket() {
        if (current.tag() == Token.Type.RIGHT_BRACKET) {
            nextToken();
        } else {
            // ERROR
        }
    }
    private void LeftBrace() {
        if (current.tag() == Token.Type.LEFT_BRACE) {
            nextToken();
        } else {
            // ERROR
        }
    }
    private void RightBrace() {
        if (current.tag() == Token.Type.RIGHT_BRACE) {
            nextToken();
        } else {
            // ERROR
        }
    }
    private void Block() {
        while (isModifier() ||
               isFirstOfControlStructure() ||
               isFirstOfExpression()) {
            if (isModifier()) {
                VariableDef();
                Semicolon();
            }  else if (isFirstOfExpression()) {
                Expression();
                Semicolon();
            } else if (current.tag() == Token.Type.FOR) {
                For();
            } else if (current.tag() == Token.Type.IF) {
                If();
            } else if (current.tag() == Token.Type.WHILE) {
                While();
            } else if (current.tag() == Token.Type.DO) {
                DoWhile();
                Semicolon();
            } else if (current.tag() == Token.Type.RUN) {
                NewThread();
                Semicolon();
            } else if (current.tag() == Token.Type.SWITCH) {
                Switch();
            } else if (current.tag() == Token.Type.RETURN) {
                Return();
                Semicolon();
            } else if (current.tag() == Token.Type.CONTINUE) {
                Continue();
                Semicolon();
            } else if (current.tag() == Token.Type.LOCK) {
                Lock();
            } else if (current.tag() == Token.Type.BARRIER) {
                Barrier();
                Semicolon();
            } else if (current.tag() == Token.Type.BREAK) {
                Break();
                Semicolon();
            }
        }
    }
    private void For() {
        nextToken();
        LeftBracket();

        if (isModifier()) {
            VariableDef();
        } else if (isFirstOfExpression()) {
            Expression();
        }
        Semicolon();
        if (isFirstOfExpression()) {
            Expression();
        }
        Semicolon();
        if (isFirstOfExpression()) {
            Expression();
            while (current.tag() == Token.Type.COMMA) {
                nextToken();
                Expression();
            }
        }
        
        RightBracket();
        Code();
    }
    private void Semicolon() {
        if (current.tag() == Token.Type.SEMICOLON) {
            nextToken();
        } else {
            // ERROR
        }
    }
    private void If() {
        nextToken();
        
        LeftBracket();
        Expression();
        RightBracket();
        
        Code();
        
        if (current.tag() == Token.Type.ELSE) {
            nextToken();
            
            if (current.tag() == Token.Type.IF) {
                If();
            } else {
                Code();
            }
        }
    }
    private void While() {
        nextToken();
        
        LeftBracket();
        Expression();
        RightBracket();
        
        Code();
    }
    private void DoWhile() {
        nextToken();
        
        Code();
        if (current.tag() == Token.Type.WHILE) {
            nextToken();
            LeftBracket();
            Expression();
            RightBracket();
        }
    }
    private void NewThread() {
        nextToken();
        Expression();
    }
    private void Switch() {
        nextToken();
        
        LeftBracket();
        Expression();
        RightBracket();
        
        LeftBrace();
        Case();
        while (current.tag() == Token.Type.CASE) {
            Case();
        }
        if (current.tag() == Token.Type.DEFAULT) {
            Default();
        }
        RightBrace();
    }
    private void Case() {
        nextToken();
        
        Expression();
        Colon();
        Block();
    }
    private void Default() {
        nextToken();
        
        Colon();
        Block();
    }
    private void Colon() {
        if (current.tag() == Token.Type.COLON) {
            nextToken();
        } else {
            // ERROR
        }
    }
    private void Return() {
        nextToken();
        
        if (isFirstOfExpression()) {
            Expression();
        }
    }
    private void Continue() {
        nextToken();
    }
    private void Lock() {
        nextToken();
        Code();
    }
    private void Barrier() {
        nextToken();
    }    
    private void Expression() {
        BoolExpression();
        while (isAssign()) {
            nextToken();
            BoolExpression();
        }
    }
    private void BoolExpression() {
        ABoolExpression();
        while (current.tag() == Token.Type.BOOL_OR) {
            nextToken();
            ABoolExpression();
        }
    }
    private void ABoolExpression() {
        BBoolExpression();
        while (current.tag() == Token.Type.BOOL_AND) {
            nextToken();
            BBoolExpression();
        }
    }
    private void BBoolExpression() {
        GExpression();
        while (current.tag() == Token.Type.BITWISE_OR) {
            nextToken();
            GExpression();
        }
    }
    private void GExpression() {
        HExpression();
        while (current.tag() == Token.Type.BITWISE_XOR) {
            nextToken();
            HExpression();
        }
    }
    private void HExpression() {
        IExpression();
        while (current.tag() == Token.Type.AMPERSAND) {
            nextToken();
            IExpression();
        }
    }
    private void IExpression() {
        CBoolExpression();
        while (isEqual()) {
            nextToken();
            CBoolExpression();
        }
    }
    private void CBoolExpression() {
        DboolExpression();
        while (isOrderRelation()) {
            nextToken();
            DboolExpression();
        }
    }
    private void DboolExpression() {
        AExpression();
        while (isBitwiseShift()) {
            nextToken();
            AExpression();
        }
    }
    private void AExpression() {
        BExpression();
        while (isPlusMinus()) {
            nextToken();
            BExpression();
        }
    }
    private void BExpression() {
        CExpression();
        while (isMulDivMod()) {
            nextToken();
            CExpression();
        }
    }
    private void CExpression() {
        if (current.tag() == Token.Type.LEFT_BRACKET) {
            nextToken();
            if (isType()) {
                nextToken();
                RightBracket();
                DExpression();
            } else {
                Expression();
                RightBracket();
                return;
            }
        }
        DExpression();
    }
    private void DExpression() {
        if (isPlusMinus()) {
            nextToken();
        } else if (isIncDec()) {
            nextToken();
        } else {
            while (current.tag() == Token.Type.AMPERSAND ||
                   current.tag() == Token.Type.ASTERISK) {
                nextToken();
            }
        }
        
        EExpression();
    }
    private void EExpression() {
        FExpression();
        if (isIncDec()) {
            nextToken();
        }
    }
    private void FExpression() {
        JExpression();
        
        while (current.tag() == Token.Type.MEMBER_SELECT ||
               current.tag() == Token.Type.LEFT_BRACKET ||
               current.tag() == Token.Type.LEFT_SQUARE_BRACKET) {
            if (current.tag() == Token.Type.MEMBER_SELECT) {
                nextToken();
                Identifier();
            } else if (current.tag() == Token.Type.LEFT_BRACKET) {
                FuncArgs();
            } else {
                ArrayDim();
            }
        }
    }
    private void FuncArgs() {
        LeftBracket();
        
        if (isFirstOfExpression()) {
            Expression();
            while (current.tag() == Token.Type.COMMA) {
                nextToken();
                Expression();
            }
        }
        
        RightBracket();
    }
    private void ArrayDim() {
        LeftSquareBracket();
        if (isFirstOfExpression()) {
            Expression();
        }
        RightSquareBracket();
        
        while (current.tag() == Token.Type.LEFT_SQUARE_BRACKET) {
            LeftSquareBracket();
            if (isFirstOfExpression()) {
                Expression();
            }
            RightSquareBracket();
        }
    }
    private void LeftSquareBracket() {
        if (current.tag() == Token.Type.LEFT_SQUARE_BRACKET) {
            nextToken();
        } else {
            // ERROR
        }
    }
    private void RightSquareBracket() {
        if (current.tag() == Token.Type.RIGHT_SQUARE_BRACKET) {
            nextToken();
        } else {
            // ERROR
        }
    }
    private void JExpression() {
        if (isConst()) {
            Const();
        } else {
            Identifier();
        }
    }
    private void Const() {
        if (isConst()) {
            nextToken();
        } else {
            // ERROR
        }
    }
    private void Asterisk() {
        if (current.tag() == Token.Type.ASTERISK) {
            nextToken();
        } else {
            // ERROR
        }
    }
    private void Variable() {
        Identifier();
        if (current.tag() == Token.Type.ASSIGN) {
            nextToken();
            Expression();
        } else if (current.tag() == Token.Type.LEFT_SQUARE_BRACKET) {
            ArrayDim();
            if (current.tag() == Token.Type.ASSIGN) {
                nextToken();
                ArrayInit();
            }
        }
    }
    private void ArrayInit() {
        LeftBrace();
        
        ArrayElement();
        while (current.tag() == Token.Type.COMMA) {
            nextToken();
            ArrayElement();
        }
        if (current.tag() == Token.Type.COMMA) {
            nextToken();
        }
        
        RightBrace();
    }
    private void ArrayElement() {
        if (current.tag() == Token.Type.LEFT_SQUARE_BRACKET) {
            ArrayInit();
        } else {
            Expression();
        }
    }
    private void StructDef() {
        nextToken();
        
        Identifier();
        LeftBrace();
        while (isModifier()) {
            VariableDef();
            Semicolon();
        }
        RightBrace();
    }
    private void VariableDef() {
        nextToken();
            
        Type();
        if(isIdentifier()) {
            Variable();
            while (current.tag() == Token.Type.COMMA) {
                nextToken();
                Variable();
            }
        } else if (current.tag() == Token.Type.LEFT_BRACKET) {
            FuncPointer();
        } else {
            // ERROR
        }
    }
    private void FuncPointer() {
        LeftBracket();
        Asterisk();
        Identifier();
        if (current.tag() == Token.Type.LEFT_SQUARE_BRACKET) {
            ArrayDim();
        }
        RightBracket();
        FuncArgList();
    }
    private void Modifier() {
        if (current.tag() == Token.Type.VAR ||
            current.tag() == Token.Type.CONST) {
            nextToken();
        } else {
            // ERROR
        }
    }
   
    private boolean isIncDec() {
        return current.tag() == Token.Type.INC ||
               current.tag() == Token.Type.DEC;
    }
    private boolean isIdentifier() {
        return current.tag() == Token.Type.IDENTIFIER;
    }
    private boolean isModifier() {
        return current.tag() == Token.Type.CONST ||
               current.tag() == Token.Type.VAR;
    }
    private boolean isConst() {
        return current.tag() == Token.Type.CONST_CHAR ||
               current.tag() == Token.Type.CONST_DOUBLE ||
               current.tag() == Token.Type.CONST_INT ||
               current.tag() == Token.Type.TRUE ||
               current.tag() == Token.Type.FALSE;
    }
    private boolean isType() {
        return current.tag() == Token.Type.INT ||
               current.tag() == Token.Type.DOUBLE ||
               current.tag() == Token.Type.FLOAT ||
               current.tag() == Token.Type.BOOL ||
               current.tag() == Token.Type.CHAR ||
               current.tag() == Token.Type.VOID;
    }
    private boolean isAssign() {
        return current.tag() == Token.Type.ASSIGN ||
               current.tag() == Token.Type.PLUS_ASSIGN ||
               current.tag() == Token.Type.MINUS_ASSIGN ||
               current.tag() == Token.Type.MUL_ASSIGN ||
               current.tag() == Token.Type.DIV_ASSIGN ||
               current.tag() == Token.Type.MOD_ASSIGN ||
               current.tag() == Token.Type.BITWISE_OR_ASSIGN ||
               current.tag() == Token.Type.BITWISE_SHIFT_LEFT_ASSIGN ||
               current.tag() == Token.Type.BITWISE_SHIFT_RIGHT_ASSIGN ||
               current.tag() == Token.Type.BITWISE_XOR_ASSIGN ||
               current.tag() == Token.Type.BITWISE_AND_ASSIGN;
    }
    private boolean isFirstOfExpression() {
        return current.tag() == Token.Type.LEFT_BRACKET ||
               current.tag() == Token.Type.PLUS ||
               current.tag() == Token.Type.MINUS ||
               current.tag() == Token.Type.AMPERSAND ||
               current.tag() == Token.Type.ASTERISK ||
               current.tag() == Token.Type.INC ||
               current.tag() == Token.Type.DEC ||
               isConst() ||
               isIdentifier();
    }
    private boolean isBitwiseShift() {
        return current.tag() == Token.Type.BITWISE_SHIFT_LEFT ||
               current.tag() == Token.Type.BITWISE_SHIFT_RIGHT;
    }
    private boolean isEqual() {
        return current.tag() == Token.Type.EQUAL ||
               current.tag() == Token.Type.NOT_EQUAL;
    }
    private boolean isOrderRelation() {
        return current.tag() == Token.Type.GREATER ||
               current.tag() == Token.Type.GREATER_OR_EQUAL ||
               current.tag() == Token.Type.LESS ||
               current.tag() == Token.Type.LESS_OR_EUQAL;
    }
    private boolean isMulDivMod() {
        return current.tag() == Token.Type.ASTERISK ||
               current.tag() == Token.Type.DIV ||
               current.tag() == Token.Type.MOD;
    }
    private boolean isPlusMinus() {
        return current.tag() == Token.Type.PLUS ||
               current.tag() == Token.Type.MINUS;
    }

    private boolean isFirstOfControlStructure() {
        return current.tag() == Token.Type.FOR ||
                current.tag() == Token.Type.IF ||
                current.tag() == Token.Type.WHILE ||
                current.tag() == Token.Type.DO ||
                current.tag() == Token.Type.RUN ||
                current.tag() == Token.Type.SWITCH ||
                current.tag() == Token.Type.RETURN ||
                current.tag() == Token.Type.CONTINUE ||
                current.tag() == Token.Type.LOCK ||
                current.tag() == Token.Type.BARRIER ||
                current.tag() == Token.Type.BREAK;
    }

    private void Break() {
        if (current.tag() == Token.Type.BREAK) {
            nextToken();
        } else {
            // ERROR
        }
    }
}
