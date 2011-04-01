package ru.bmstu.iu9.compiler;

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
    public String value() { return this.value; }
    
    private String value;
}
