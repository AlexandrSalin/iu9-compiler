package ru.bmstu.iu9.compiler.lexis;

import ru.bmstu.iu9.compiler.Position;
import java.util.Iterator;

/**
 * Класс, представляющий кодовую точку в тексте программы.
 * Кодовая точка состоит из:
 * <ul>
 * <li>Символа</li>
 * <li>Позиции символа в тексте программы</li>
 * </ul>
 * @author anton.bobukh
 */
class CodePoint implements Cloneable {
    /**
     * Кодовая точка, используемая при итерировании по тексту программы
     * @param CPValue кодовая точка
     * @param CPPosition позиция кодовой точки в программе
     */
    public CodePoint(int value, Position position) {
        this.value = value;
        this.position = position;
    }
    
    /**
     * Метод, предоставляющий доступ к значению кодовой точки
     * @return Значение кодовой точки
     */
    public int value() { 
        return value;
    }
    /**
     * Метод, предоставляющий доступ к координатам кодовой точки
     * @return Координаты кодовой точки
     */
    public Position position() {
        return position; 
    }
    public void setValue(int newValue) {
        value = newValue;
    }
    public void setPosition(Position newPosition) { 
        position = newPosition; 
    }
    
    @Override
    public Object clone() {
        return new CodePoint(this.value, (Position)this.position.clone());
    } 
    
    @Override
    public String toString() {
        return "'" + Character.toChars(value)[0] + "' at " + position;
    }
    
    private int value;
    private Position position;
}

class Program implements Iterable<CodePoint> {
    /**
     * Текст программы с возможностью итерирования по кодовым точкам
     * @param program Текст программы
     * @param terminator Терминальный символ, расположенный в конце текста 
     * программы
     */
    public Program(String program, int terminator) {
        this.terminator = terminator;
        this.program = program + Character.toChars(this.terminator).toString();
    }
    /**
     * Текст программы с возможностью итерирования по кодовым точкам
     * @param program Текст программы
     */
    public Program(String program) {
        this.terminator = '$';
        this.program = program + '$';
    }
    
    public class CodePointIterator implements Iterator<CodePoint> {
        public CodePointIterator() {
            current = new CodePoint(program.codePointAt(0), 
                    new Position(1, 1, 0));
        }
        @Override
        public boolean hasNext() {
            return program.codePointAt(index) != terminator;
        }
        @Override
        public CodePoint next() {
            if(program.codePointAt(index) == '\n')
            {
                ++line;
                position = 0;
            }
            ++position;
            index = program.offsetByCodePoints(index, 1);
            
            current.setValue(program.codePointAt(index));
            current.setPosition(new Position(line, position, index));

            return current;
        }
        public CodePoint watchNext() {
            int l = line, p = position;
            if(program.codePointAt(index) == '\n')
            {
                ++l;
                p = 0;
            }
            ++p;
            
            int i = program.offsetByCodePoints(index, 1);
            
            return new CodePoint(program.codePointAt(i), new Position(l, p, i));
        }
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
        /**
         * Возвращает кодовую точку в текущей позиции
         * @return Текущая кодовая точка
         */
        public CodePoint current() {
            return current;
        }
        /**
         * Пропускает count кодовых точек в тексте программы
         * @param count Колечество кодовых точек, которое необходимо пропустить
         * @return Кодовая точка в полученной позиции
         */
        public CodePoint advance(int count) {
            while (count > 0 && hasNext()) {
                if(program.codePointAt(index) == '\n')
                {
                    line++;
                    position = 0;
                }
                position++;
                index = program.offsetByCodePoints(index, 1);
                count--;
            }
            current.setValue(program.codePointAt(index));
            current.setPosition(new Position(line, position, index));
            
            return current;
        }
        
        @Override
        public String toString() {
            return current.toString();
        }
    
        private int index = 0;
        private int line = 1;
        private int position = 1;
        private final CodePoint current;
    }
    
    @Override
    public CodePointIterator iterator() {
        return new CodePointIterator();
    }
    @Override
    public String toString() {
        return program;
    }
    public int length() { return program.length(); }
    
    private final String program;
    private final int terminator;
}