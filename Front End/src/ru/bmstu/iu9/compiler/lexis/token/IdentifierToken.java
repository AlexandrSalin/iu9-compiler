package ru.bmstu.iu9.compiler.lexis.token;

import ru.bmstu.iu9.compiler.Fragment;
import ru.bmstu.iu9.compiler.Position;

/**
 *
 * @author maggot
 */
public final class IdentifierToken extends Token {
    public IdentifierToken(Fragment coordinates, String name) {
        super(coordinates, Type.IDENTIFIER);
        this.value = name;
    }
    public IdentifierToken(Position starting, Position ending, String name) {
        super(new Fragment(starting, ending), Type.IDENTIFIER);
        this.value = name;
    }
    
    @Override
    public String toString() {
        return value;
    }
    
    public final String value;
}
