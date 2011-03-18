package ru.bmstu.iu9.compiler.lexer;

/**
 *
 * @author maggot
 */
class Position implements Cloneable {
    /**
     * Позиция кодовой точки в тексте программы, представленная 
     * тройкой <line, pos, index>. Номер строки и позиция в строке 
     * нумеруются с 1, индекс нумеруется с 0
     * @param lineNumber номер строки программы
     * @param positionInLine позиция в строке программы
     * @param indexInText индекс в тексте программы
     */
    public Position(int line, int position, int index) {
        this.line = line;
        this.position = position;
        this.index = index;
    }
    
    /**
     * Метод, предоставляющий доступ к номеру строки
     * @return Номер строки
     */
    public int line() { return line; }
    /**
     * Метод, предоставляющий доступ к позиции в строке
     * @return Позиция в строке
     */
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
            
    private final int line;
    private final int position;
    private final int index;
}

class Fragment {
    /**
     * Координаты фрагмента текста программы
     * @param startingPosition позиция первого символа фрагмента
     * @param endingPosition позиция последнего символа фрагмента
     */
    public Fragment(Position starting, Position ending) {
        this.starting = starting;
        this.ending = ending;
    }
    
    public Position starting() { return starting; }
    public Position ending() { return ending; }
    
    private final Position starting;
    private final Position ending;
}