package ru.bmstu.iu9.compiler.lexis.token;

import ru.bmstu.iu9.compiler.Fragment;

/**
 *
 * @author anton.bobukh
 */
public abstract class ConstantToken<T> extends Token {
    protected ConstantToken(Fragment coordinates, T value, Type type) {
        super(coordinates, type);
        this.value = value;
    }
    
    @Override
    public String toString() {
        return value.toString();
    }
    
    public final T value;
}
