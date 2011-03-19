package ru.bmstu.iu9.compiler.parser;

import com.google.gson.InstanceCreator;
import java.lang.reflect.Type;

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
    public Position() {
        this.line = 1;
        this.position = 1;
        this.index = 0;
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
            
    private int line;
    private int position;
    private int index;
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
    
    private Position starting;
    private Position ending;
}

class FragmentInstanceCreator implements InstanceCreator<Fragment> {
  public Fragment createInstance(Type type) {
    return new Fragment(null, null);
  }
}