package ru.bmstu.iu9.compiler.lexis;

import ru.bmstu.iu9.compiler.Position;
import java.util.Iterator;

/**
 * Класс, представляющий кодовую точку в тексте программы.
 * 
 * 
 * Кодовая точка состоит из:
 * <ul>
 * <li>Символа</li>
 * <li>Позиции символа в тексте программы</li>
 * </ul>
 * 
 * @author anton.bobukh
 */
class CodePoint implements Cloneable {
    /**
     * Создает объект CodePoint.
     * 
     * @param value кодовая точка
     * @param position позиция кодовой точки в программе
     */
    public CodePoint(int value, Position position) {
        this.value = value;
        this.position = position;
    }
    
    /**
     * Метод, предоставляющий доступ к значению кодовой точки.
     * 
     * @return Значение кодовой точки
     */
    public int value() { 
        return value;
    }
    public char asChar() {
        return Character.toChars(value)[0];
    }
    /**
     * Метод, предоставляющий доступ к координатам кодовой точки.
     * 
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
        return "'" + asChar() + "' at " + position;
    }
    
    private int value;
    private Position position;
}

/**
 * Класс Program предоставляет возможность итерирования по тексту программы.
 * При этом каждый символ представляется {@link ru.bmstu.iu9.compiler.lexis.CodePoint CodePoint}ом.
 * После перехода на новую строку счетчик строк автоматически инкрементируется,
 * а счетчик позиции в строке сбрасывает в 1.
 * <br/>
 * <pre>
 * Program text = new Program(
 *     "func foo(i, j int) int {" +
 *     "   var a, b int;" + 
 *     "   a = i + j;" +
 *     "   b = i - j;" +
 *     "   return a ^ b;"
 *     "}"
 * );
 * 
 * List<CodePoint> codePoints = new LinkedList<CodePoint>();
 * 
 * for(CodePoint codePoint : text) {
 *     codePoints.add(codePoint);
 * }
 * </pre>
 * 
 * @author anton.bobukh
 * @see ru.bmstu.iu9.compiler.lexis.CodePoint
 */
class Program implements Iterable<CodePoint> {
    /**
     * Создает объект Program. При этом в конец программы будет приписан
     * терминальный символ, который будет использоваться как флаг конца текста
     * программы при итерировании.
     * 
     * @param program Текст программы
     * @param terminator Терминальный символ
     */
    public Program(String program, int terminator) {
        this.terminator = terminator;
        this.text = program + Character.toChars(this.terminator).toString();
    }
    /**
     * Создает объект Program. При этом в конец программы будет приписан
     * терминальный символ по цмолчанию '$', который будет использоваться как 
     * флаг конца текста программы при итерировании.
     * 
     * @param program Текст программы
     */
    public Program(String program) {
        this.terminator = '$';
        this.text = program + '$';
    }
    
    /**
     * Класс CodePointIterator осуществляет итерирование по тексту программы.
     */
    public static class CodePointIterator implements Iterator<CodePoint> {
        /**
         * Создает объект CodePointIterator, при этом текущим становится первый 
         * символ текста программы в позиции <1, 1, 0>.
         */
        public CodePointIterator(Program program) {
            this.program = program;
            current = new CodePoint(program.text.codePointAt(0),
                    new Position(1, 1, 0));
        }

        /**
         * Проверяет, есть ли еще непросмотренные символы в тексте программы. 
         * 
         * Проверяет, есть ли еще непросмотренные символы в тексте программы.
         * Проверка осуществляется путем сравнения символа в текущей позиции 
         * с терминальным символом.
         * 
         * @return true, если еще не все символы были просмотрены, false 
         *         в противном случае
         */
        public boolean hasNext() {
            return program.text.codePointAt(index) != program.terminator;
        }

        /**
         * Переходит к следующему символу в тексте программы.
         * 
         * Переходит к следующему символу в тексте программы. При этом в случае 
         * перехода на новую строку счетчик строк инкрементируется, а счетчик
         * позиции в строке сбрасывает в 1.
         * 
         * @return {@link ru.bmstu.iu9.compiler.lexis.CodePoint CodePoint},
         *         соответствующий символу в новой позиции
         */
        public CodePoint next() {
            if(program.text.codePointAt(index) == '\n')
            {
                ++line;
                position = 0;
            }
            ++position;
            index = program.text.offsetByCodePoints(index, 1);
            
            current.setValue(program.text.codePointAt(index));
            current.setPosition(new Position(line, position, index));

            return current;
        }
        
        /**
         * Просматривает следующий сомвол в тексте программы. 
         * 
         * Просматривает следующий сомвол в тексте программы. При этом перехода
         * к нему не осуществляется.
         * 
         * @return {@link ru.bmstu.iu9.compiler.lexis.CodePoint CodePoint},
         *         соответствующий символу в следующей позиции
         */
        public CodePoint watchNext() {
            int l = line, p = position;
            if(program.text.codePointAt(index) == '\n')
            {
                ++l;
                p = 0;
            }
            ++p;
            
            int i = program.text.offsetByCodePoints(index, 1);
            
            return new CodePoint(program.text.codePointAt(i), new Position(l, p, i));
        }

        /**
         * Операция не поддерживается.
         */
        public void remove() {
            throw new UnsupportedOperationException();
        }
        
        /**
         * Возвращает {@link ru.bmstu.iu9.compiler.lexis.CodePoint CodePoint}
         * в текущей позиции.
         * 
         * @return {@link ru.bmstu.iu9.compiler.lexis.CodePoint CodePoint},
         *         соответствующий текущему символу
         */
        public CodePoint current() {
            return current;
        }
        
        /**
         * Пропускает count символов в тексте программы. 
         * 
         * Пропускает count символов в тексте программы. При каждом 
         * переходе на новую строку счетчик строк инкрементируется, а счетчик
         * позиции в строке сбрасывает в 1.
         * 
         * @param count Колечество символов, которое необходимо пропустить
         * @return {@link ru.bmstu.iu9.compiler.lexis.CodePoint CodePoint},
         *         соответствующий символу в новой позиции
         */
        public CodePoint advance(int count) {
            while (count > 0 && hasNext()) {
                if(program.text.codePointAt(index) == '\n')
                {
                    line++;
                    position = 0;
                }
                position++;
                index = program.text.offsetByCodePoints(index, 1);
                count--;
            }
            current.setValue(program.text.codePointAt(index));
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
        private Program program;
    }
    
    /**
     * Создает итератор по {@link ru.bmstu.iu9.compiler.lexis.CodePoint CodePoint}ам
     * текста программы.
     * 
     * @return Итератор по {@link ru.bmstu.iu9.compiler.lexis.CodePoint CodePoint}ам
     */
    public CodePointIterator iterator() {
        return new CodePointIterator(this);
    }
    @Override
    public String toString() {
        return text;
    }
    public int length() { return text.length(); }
    
    private final String text;
    private final int terminator;
}