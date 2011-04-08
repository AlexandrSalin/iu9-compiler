package ru.bmstu.iu9.compiler.syntax;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.Iterator;
import ru.bmstu.iu9.compiler.Fragment;
import ru.bmstu.iu9.compiler.lexis.token.CharConstantToken;
import ru.bmstu.iu9.compiler.lexis.token.DoubleConstantToken;
import ru.bmstu.iu9.compiler.lexis.token.IdentifierToken;
import ru.bmstu.iu9.compiler.lexis.token.IntegerConstantToken;
import ru.bmstu.iu9.compiler.lexis.token.SpecialToken;
import ru.bmstu.iu9.compiler.lexis.token.Token;

/**
 *
 * @author maggot
 */
public final class TokenFactory implements Iterable<Token> {
    public TokenFactory(String filename) {
        BufferedReader reader = null;
        
        try {
            Gson gson = 
                    new GsonBuilder().
                        registerTypeAdapter(
                            Fragment.class, 
                            new Fragment.FragmentInstanceCreator()).
                        registerTypeAdapter(
                            Token.class,
                            new GeneralizedToken.TokenInstanceCreator()).
                        create();
            
            reader = new BufferedReader(
                        new FileReader(filename));
            
            tokens = gson.fromJson(reader, GeneralizedToken[].class);
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
    
    @Override
    public Iterator<Token> iterator() {
        return new Iterator<Token>() {
                @Override
                public boolean hasNext() {
                    return counter < tokens.length;
                }
                @Override
                public Token next() {
                    GeneralizedToken token = tokens[counter++];
                    switch (Token.Type.values()[token.type]) {
                        case CONST_INT:
                            return new IntegerConstantToken(
                                    token.coordinates, token.value.intValue());
                        case CONST_DOUBLE:
                            return new DoubleConstantToken(
                                    token.coordinates, token.value.doubleValue());
                        case CONST_CHAR:
                            return new CharConstantToken(
                                    token.coordinates, token.value.intValue());
                        case IDENTIFIER:
                            return new IdentifierToken(
                                    token.coordinates, token.name);
                        default:
                            return new SpecialToken(
                                    token.coordinates, Token.Type.values()[token.type]);
                    }
                }
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
                
                int counter = 0;
            };
    }
    
    private static class GeneralizedToken {
        private GeneralizedToken() { }

        private int type;
        private Fragment coordinates;
        private BigDecimal value;
        private String name;

        public static class TokenInstanceCreator implements InstanceCreator<GeneralizedToken> {
            @Override
            public GeneralizedToken createInstance(java.lang.reflect.Type type) {
                return new GeneralizedToken();
            }
        }
    }
    
    private GeneralizedToken[] tokens;
}
