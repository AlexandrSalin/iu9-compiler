package ru.bmstu.iu9.compiler;

import com.google.gson.InstanceCreator;

/**
 *
 * @author maggot
 */
public final class Position implements Cloneable {
    /**
     * Позиция кодовой точки в тексте программы, представленная 
     * тройкой <line, pos, index>. Номер строки и позиция в строке 
     * нумеруются с 1, индекс нумеруется с 0
     * @param lineNumber номер строки программы
     * @param positionInLine позиция в строке программы
     * @param indexInText индекс в тексте программы
     */
    private Position() { }
    public Position(int line, int position, int index) {
        this.line = line;
        this.position = position;
        this.index = index;
    }

    public int line() { return line; }
    public int position() { return position; }
    public int index() { return index; }
    
    @Override
    public String toString() {
        return String.format("line: %1$d, pos: %2$d", line, position);
    }
    @Override
    public Object clone() {
        return new Position(this.line, this.position, this.index);
    } 
            
    private int line;
    private int position;
    private int index;
    
    public static class PositionInstanceCreator implements InstanceCreator<Position> {
        @Override
        public Position createInstance(java.lang.reflect.Type type) {
            return new Position();
        }
    }
}
