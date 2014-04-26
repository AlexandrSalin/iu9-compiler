package ru.bmstu.iu9.compiler.lexis.token;

import ru.bmstu.iu9.compiler.Fragment;

/**
 *
 * @param <T> 
 * @author maggot
 */
public class ConstantToken<T> extends Token {
    protected ConstantToken(Fragment coordinates, T value, Type type) {
        super(coordinates, type);
        this.value = value;
    }
    
    public final T value;
}
