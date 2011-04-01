package ru.bmstu.iu9.compiler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Iterator;

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
                    ++counter;
                    GeneralizedToken token = tokens[counter];
                    switch (Token.Type.values()[token.type]) {
                        case CONST_INT:
                            return new IntegerConstantToken(
                                    token.coordinates, (int)token.value);
                        case CONST_DOUBLE:
                            return new DoubleConstantToken(
                                    token.coordinates, (double)token.value);
                        case CONST_CHAR:
                            return new CharConstantToken(
                                    token.coordinates, (int)token.value);
                        case IDENTIFIER:
                            return new IdentifierToken(
                                    token.coordinates, (String)token.value);
                        default:
                            return new SpecialToken(
                                    token.coordinates, Token.Type.values()[token.type]);
                    }
                }
                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
                
                int counter = -1;
            };
    }
    
    private static class GeneralizedToken {

        private GeneralizedToken() { }

        private int type;
        private Fragment coordinates;
        private Object value;

        public static class TokenInstanceCreator implements InstanceCreator<GeneralizedToken> {
            @Override
            public GeneralizedToken createInstance(java.lang.reflect.Type type) {
                return new GeneralizedToken();
            }
        }
    }
    
    private GeneralizedToken[] tokens;
}
