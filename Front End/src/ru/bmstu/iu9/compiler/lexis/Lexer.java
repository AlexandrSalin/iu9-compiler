package ru.bmstu.iu9.compiler.lexis;

import ru.bmstu.iu9.compiler.lexis.token.*;
import ru.bmstu.iu9.compiler.Fragment;
import com.google.gson.*;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.*;
import ru.bmstu.iu9.compiler.lexis.Program.CodePointIterator;

/**
 * Класс Lexer осуществляет лексический анализ текста программы и генерирует
 * последовательность {@link ru.bmstu.iu9.compiler.lexis.token.Token Token}ов,
 * соответствующих встетившимся лексемам.
 * Сгенерированные токены могут быть сериализованы в файл в формате json.
 * <br/>
 * Пример использования класса Lexer:
 * <pre>
 * Lexer lexer = new Lexer("program.src");
 * lexer.run();
 * lexer.toJson("tokens.json");
 * </pre>
 * 
 * @author anton.bobukh
 * @see ru.bmstu.iu9.compiler.lexis.Scanner
 */
public class Lexer {
    /**
     * Создает объект Lexer.
     * 
     * Создает объект Lexer, который будет осуществлять лексический анализ 
     * текста программы, размещенного в файле с указанным именем.
     * 
     * @param filename имя файла с текстом программы
     */
    public Lexer(String filename) {
        BufferedReader reader = null;
        
        try {
            reader = new BufferedReader(new FileReader(filename));
            String line;
            StringBuilder str = new StringBuilder();
            
            while((line = reader.readLine()) != null) {
                str.append(line);
                str.append('\n');
            }
            this.scanner = new Scanner(str.toString());
        } catch(java.io.IOException ex) {
//            ex.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch(Exception ex) {
//                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Осуществляет заполнение списка {@link ru.bmstu.iu9.compiler.lexis.token.Token Token}ов 
     * с использованием {@link ru.bmstu.iu9.compiler.lexis.Scanner Scanner}а.
     */
    public void run() {
        for (Token token : scanner) {
            tokens.add(token);
        }
    }
    
    /**
     * Сериализует массив {@link ru.bmstu.iu9.compiler.lexis.token.Token Token}ов
     * в файл в формате json.
     * 
     * Сериализует сгенерированный в процессе лексического анализа текста 
     * программы массив {@link ru.bmstu.iu9.compiler.lexis.token.Token Token}ов 
     * в файл с указанным именем в формате json.
     * 
     * @param filename имя файла, в который будет производиться сериализация
     */
    public void toJson(String filename) { 
        PrintWriter writer = null;
        
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            writer = new PrintWriter(filename);
            gson.toJson(tokens, writer);
        } catch(java.io.IOException ex) {
//            ex.printStackTrace();
        } finally {
            writer.flush();
            writer.close();
        }
    }
    
    public static void main(String[] args) {
        Lexer lex = new Lexer("C:\\Users\\maggot\\Documents\\IntelliJ IDEA " +
                              "Projects\\iu9-compiler\\Front End\\src\\input.src");
        lex.run();
        lex.toJson("C:\\Users\\maggot\\Documents\\IntelliJ IDEA " +
                   "Projects\\iu9-compiler\\Front End\\src\\output.json");
    }
    
    /**
     * {@link ru.bmstu.iu9.compiler.lexis.Scanner Scanner}, осущевствляющий 
     * итерирование по {@link ru.bmstu.iu9.compiler.lexis.token.Token Token}ам
     */
    private Scanner scanner;
    /**
     * Список уже полученных {@link ru.bmstu.iu9.compiler.lexis.token.Token Token}ов
     */
    private List<Token> tokens = new LinkedList<Token>();
}


/**
 * Это основной класс, используемый {@link ru.bmstu.iu9.compiler.lexis.Lexer Lerex}.
 * Scanner осуществляет итерацию по лексемам текста программы, при этом 
 * возвращая {@link ru.bmstu.iu9.compiler.lexis.token.Token Token}, 
 * соответствующий текущей лексеме.</br>
 * Пример использования класса Scanner:
 * <pre>
 * Scanner scanner = new Scanner(
 *     "func foo(i, j int) int {" +
 *     "   var a, b int;" + 
 *     "   a = i + j;" +
 *     "   b = i - j;" +
 *     "   return a ^ b;"
 *     "}"
 * );
 * List<Token> tokens = new LinkedList<Token>();
 * 
 * for(Token token : scanner) {
 *     tokens.add(token);
 * }
 * </pre>
 * 
 * @see ru.bmstu.iu9.compiler.lexis.Program
 * @see ru.bmstu.iu9.compiler.lexis.token.Token
 * @see ru.bmstu.iu9.compiler.lexis.Lexer
 * @author anton.bobukh
 */
class Scanner implements Iterable<Token> {
    /**
     * Создает объект Scanner.
     * 
     * Создает объект Scanner, который будет итерировать по лексемам указанного 
     * текста программы.
     * 
     * @param text текст программы, по лексемам которого будет производиться
     * итерирование
     */
    public Scanner(String text) {
        this.text = text;
        this.iterator = new Program(text).iterator();
    }
    
    /**
     * Создает анонимный класс-итератор по {@link ru.bmstu.iu9.compiler.lexis.token.Token Token}ам.
     * 
     * Создает анонимный класс-итератор по {@link ru.bmstu.iu9.compiler.lexis.token.Token Token}ам, 
     * методы которого определены следующим образом:
     * <dl>
     * <dt>hasNext</dt>
     * <dd>
     * Пропускает все пробельные символы и переносы строки, а также комментарии.
     * Возвращает true, если конец текста программы не достигнут, false в 
     * противном случае.
     * </dd>
     * <dt>next</dt>
     * <dd>
     * Возвращает следующий {@link ru.bmstu.iu9.compiler.lexis.token.Token Token}. 
     * При этом проверка достижения конца программы осуществляется вызовом 
     * метода hasNext.
     * </dd>
     * <dt>remove</dt>
     * <dd>
     * @throws UnsupportedOperationException
     * </dd>
     * </dl>
     * 
     * @return Итератор по {@link ru.bmstu.iu9.compiler.lexis.token.Token Token}ам
     */

    public Iterator<Token> iterator() {
        return new Iterator<Token>() {

                public boolean hasNext() {
                    return !skipWhitespacesAndComments();
                }

                public Token next() {
                    for(;;) {
                        try {
                            return nextToken();
                        } catch(LexisException ex) {
                            errorRecovery();
                        }
                    }
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
    }
    
    /**
     * Пропускает все символы, пока не дойдет до пробельного символа или
     * зарезервированного символа
     */
    private void errorRecovery() {
        while (iterator.hasNext() && 
                (!Character.isWhitespace(iterator.current().value()) ||
                 !reservedSymbols.contains(iterator.current().value())
              )) {
            iterator.next();
        }
    }
    
    /**
     * Выделяет следующую лексему в тексте программы.
     * 
     * В случае, когда ни одна возможная лексема на начинается с символа, 
     * стоящего на текущей рассмариваемой позиции в тексте программы, ошибка 
     * фиксируется в логах, и возвращается null. Так же null возвращается, когда
     * достигнут конец текста программы.
     * 
     * @return {@link ru.bmstu.iu9.compiler.lexis.token.Token Token}, 
     *         соответствующий следующей лексеме в тексте программы
     */
    private Token nextToken() throws LexisException {
        if (skipWhitespacesAndComments()) {
            return null;
        }
        
        CodePoint current = (CodePoint)iterator.current().clone();
        Token.Type tokenType = null;
                
        switch (iterator.current().value()) {
            case '.':
                Matcher matcher = 
                    Pattern.compile("\\.[0-9]+([eE][-+]?[0-9]+)?\\b").
                        matcher(text).
                            region(
                                current.position().index(), 
                                text.length());

                if (matcher.lookingAt()) {
                    return new DoubleConstantToken(
                        iterator.current().position(), 
                        iterator.advance(matcher.group().length()).position(),
                        Double.parseDouble(matcher.group()));
                } else {
                    iterator.advance(1);
                    tokenType = Token.Type.MEMBER_SELECT;
                }
                break;
            case '\'':
                iterator.advance(1);
                int cp = iterator.current().value();
                iterator.advance(1);
                if (iterator.current().value() == '\'') {
                    iterator.advance(1);
                    return new CharConstantToken(current.position(),
                        iterator.current().position(), cp);
                } else {
                    throw new InvalidCodePointException(iterator.current());
                }
            case '~':
                iterator.advance(1);
                tokenType = Token.Type.PLUS;
                break;
            case '{':
                iterator.advance(1);
                tokenType = Token.Type.LEFT_BRACE;
                break;
            case '}':
                iterator.advance(1);
                tokenType = Token.Type.RIGHT_BRACE;
                break;
            case '[':
                iterator.advance(1);
                tokenType = Token.Type.LEFT_SQUARE_BRACKET;
                break;
            case ']':
                iterator.advance(1);
                tokenType = Token.Type.RIGHT_SQUARE_BRACKET;
                break;
            case '(':
                iterator.advance(1);
                tokenType = Token.Type.LEFT_BRACKET;
                break;
            case ')':
                iterator.advance(1);
                tokenType = Token.Type.RIGHT_BRACKET;
                break;
            case ':':
                iterator.advance(1);
                tokenType = Token.Type.COLON;
                break;
            case ',':
                iterator.advance(1);
                tokenType = Token.Type.COMMA;
                break;
            case ';':
                iterator.advance(1);
                tokenType = Token.Type.SEMICOLON;
                break;
            case '+':
                iterator.advance(1); 
                if (iterator.current().value() == '+') {   
                    iterator.advance(1); 
                    tokenType = Token.Type.INC;
                } else if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = Token.Type.PLUS_ASSIGN;
                } else {
                    tokenType = Token.Type.PLUS;
                }
                break;
            case '-':
                iterator.advance(1);
                if(iterator.current().value() == '-') {
                    iterator.advance(1);                    
                    tokenType = Token.Type.DEC;
                } else if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = Token.Type.PLUS_ASSIGN;
                } else {
                    tokenType = Token.Type.MINUS;
                }
                break;
            case '!':
                iterator.advance(1);
                if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = Token.Type.NOT_EQUAL;
                } else {
                    tokenType = Token.Type.BOOL_NOT;
                }
                break;
            case '&':
                iterator.advance(1);
                if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = Token.Type.BITWISE_AND_ASSIGN;
                } else if(iterator.current().value() == '&') {
                    iterator.advance(1); 
                    tokenType = Token.Type.BOOL_AND;
                } else {
                    tokenType = Token.Type.AMPERSAND;
                }
                break;
            case '*':
                iterator.advance(1);
                if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = Token.Type.MUL_ASSIGN;
                } else {
                    tokenType = Token.Type.ASTERISK;
                }
                break;
            case '/':
                iterator.advance(1);
                if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = Token.Type.DIV_ASSIGN;
                } else {
                    tokenType = Token.Type.DIV;
                }
                break;
            case '%':
                iterator.advance(1);
                if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = Token.Type.MOD_ASSIGN;
                } else {
                    tokenType = Token.Type.MOD;
                }
                break;
            case '^':
                iterator.advance(1);
                if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = Token.Type.BITWISE_XOR_ASSIGN;
                } else {
                    tokenType = Token.Type.BITWISE_XOR;
                }
                break;
            case '|':
                iterator.advance(1);
                if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = Token.Type.BITWISE_OR_ASSIGN;
                } else if(iterator.current().value() == '|') {
                    iterator.advance(1); 
                    tokenType = Token.Type.BOOL_OR;
                } else {
                    tokenType = Token.Type.BITWISE_OR;
                }
                break;
            case '=':
                iterator.advance(1);
                if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = Token.Type.EQUAL;
                } else {
                    tokenType = Token.Type.ASSIGN;
                }
                break;
            case '>':
                iterator.advance(1); 
                if (iterator.current().value() == '>') {   
                    iterator.advance(1); 
                    if (iterator.current().value() == '=') {
                        iterator.advance(1); 
                        tokenType = Token.Type.BITWISE_SHIFT_RIGHT_ASSIGN;
                    } else {
                        tokenType = Token.Type.BITWISE_SHIFT_RIGHT;
                    }
                } else if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = Token.Type.GREATER_OR_EQUAL;
                } else {
                    tokenType = Token.Type.RIGHT_ANGLE_BRACKET;
                }
                break;
            case '<':
                iterator.advance(1); 
                if (iterator.current().value() == '<') {   
                    iterator.advance(1); 
                    if (iterator.current().value() == '=') {
                        iterator.advance(1); 
                        tokenType = Token.Type.BITWISE_SHIFT_RIGHT_ASSIGN;
                    } else {
                        tokenType = Token.Type.BITWISE_SHIFT_RIGHT;
                    }
                } else if (iterator.current().value() == '=') {
                    iterator.advance(1); 
                    tokenType = Token.Type.LESS_OR_EQUAL;
                } else {
                    tokenType = Token.Type.LEFT_ANGLE_BRACKET;
                }
                break;
            default:
                if(Character.isDigit(iterator.current().value())) {
                    Matcher matcherDouble = 
                        Pattern.compile("(([0-9]+\\.[0-9]*([eE][-+]?[0-9]+)?)|([0-9]+[eE][-+]?[0-9]+))\\b").
                            matcher(text).
                                region(
                                    current.position().index(), 
                                    text.length());

                    if (matcherDouble.lookingAt()) {
                        double value = 0.0;
                        try {
                            value = Double.parseDouble(matcherDouble.group());
                        } catch(NumberFormatException ex) {
                            throw new InvalidCodePointException(iterator.current());
                        }
                        return new DoubleConstantToken(
                            iterator.current().position(), 
                            iterator.advance(matcherDouble.group().length()).position(),
                            value);
                    }
                    
                    int value = 0;
                            
                    if (iterator.current().value() == '0') {
                        iterator.advance(1);
                        if (iterator.current().value() == 'x' || 
                            iterator.current().value() == 'X') {
                            
                            iterator.advance(1);
                            current = (CodePoint)iterator.current().clone();
                            while (iterator.hasNext() && 
                                (Character.isDigit(iterator.current().value()) ||
                                 iterator.current().value() == 'A' ||
                                 iterator.current().value() == 'B' ||
                                 iterator.current().value() == 'C' ||
                                 iterator.current().value() == 'D' ||
                                 iterator.current().value() == 'E' ||
                                 iterator.current().value() == 'F' ||
                                 iterator.current().value() == 'a' ||
                                 iterator.current().value() == 'b' ||
                                 iterator.current().value() == 'c' ||
                                 iterator.current().value() == 'd' ||
                                 iterator.current().value() == 'e' ||
                                 iterator.current().value() == 'f')) {
                                iterator.next();
                            }
                            
                            try {
                                value = Integer.parseInt(
                                    text.substring(
                                        current.position().index(),
                                        iterator.current().position().index()
                                    ),
                                    16
                                );
                            } catch(NumberFormatException ex) {
                                throw new InvalidNumberFormatException(
                                    text.substring(
                                        current.position().index(),
                                        iterator.current().position().index()
                                    ),
                                    iterator.current().position()
                                );
                            }
                        } else {
                            while (iterator.hasNext() && 
                                Character.isDigit(iterator.current().value())) {
                                iterator.next();
                                continue;
                            }
                            
                            try {
                                value = Integer.parseInt(
                                    text.substring(
                                        current.position().index(),
                                        iterator.current().position().index()
                                    ),
                                    8
                                );
                            } catch(NumberFormatException ex) {
                                throw new InvalidNumberFormatException(
                                    text.substring(
                                        current.position().index(),
                                        iterator.current().position().index()
                                    ),
                                    iterator.current().position()
                                );
                            }
                        }
                    } else {
                        while (iterator.hasNext() && 
                            Character.isDigit(iterator.next().value())) {
                            continue;
                        }
                        
                        try {
                            value = Integer.parseInt(
                                text.substring(
                                    current.position().index(), 
                                    iterator.current().position().index()
                            ));
                        } catch(NumberFormatException ex) {
                            throw new InvalidNumberFormatException(
                                text.substring(
                                        current.position().index(),
                                        iterator.current().position().index()
                                ),
                                iterator.current().position()
                            );
                        }
                    }
                    
                    return new IntegerConstantToken(new Fragment(
                            current.position(), iterator.current().position()), 
                            value);
                    
                } else if (Character.isLetter(iterator.current().value())) {
                    while (iterator.hasNext() &&
                        (Character.isLetterOrDigit(iterator.current().value()) ||
                         iterator.current().value() == '_')) {
                        iterator.next();
                    }
                    
                    String keyword = text.substring(
                        current.position().index(), 
                        iterator.current().position().index());
                    
                    Token.Type type = null; 
                    switch (current.value()) {
                        case 'i':
                            if (keyword.equals("int"))
                                type = Token.Type.INT;
                            else if (keyword.equals("if"))
                                type = Token.Type.IF;
                            break;
                        case 'f':
                            if (keyword.equals("float"))
                                type = Token.Type.FLOAT;
                            else if (keyword.equals("false"))
                                type = Token.Type.FALSE;
                            else if (keyword.equals("func"))
                                type = Token.Type.FUNC;
                            else if (keyword.equals("for"))
                                type = Token.Type.FOR;
                            break;
                        case 'd':
                            if (keyword.equals("double"))
                                type = Token.Type.DOUBLE;
                            else if (keyword.equals("default"))
                                type = Token.Type.DEFAULT;
                            else if (keyword.equals("do"))
                                type = Token.Type.DO;
                            break;
                        case 'c':
                            if (keyword.equals("char"))
                                type = Token.Type.CHAR;
                            else if (keyword.equals("continue"))
                                type = Token.Type.CONTINUE;
                            else if (keyword.equals("case"))
                                type = Token.Type.CASE;
                            else if (keyword.equals("const"))
                                type = Token.Type.CONST;
                            break;
                        case 'v':
                            if (keyword.equals("void"))
                                type = Token.Type.VOID;
                            else if (keyword.equals("var"))
                                type = Token.Type.VAR;
                            break;
                        case 's':
                            if (keyword.equals("struct"))
                                type = Token.Type.STRUCT;
                            else if (keyword.equals("switch"))
                                type = Token.Type.SWITCH;
                            break;
                        case 'b':
                            if (keyword.equals("bool"))
                                type = Token.Type.BOOL;
                            else if (keyword.equals("break"))
                                type = Token.Type.BREAK;
                            else if (keyword.equals("barrier"))
                                type = Token.Type.BARRIER;
                            break;
                        case 'r':
                            if (keyword.equals("return"))
                                type = Token.Type.RETURN;
                            else if (keyword.equals("run"))
                                type = Token.Type.RUN;
                            break;
                        case 'e':
                            if (keyword.equals("else"))
                                type = Token.Type.ELSE;
                            break;
                        case 'w':
                            if (keyword.equals("while"))
                                type = Token.Type.WHILE;
                            break;
                        case 'l':
                            if (keyword.equals("lock"))
                                type = Token.Type.LOCK;
                            else if (keyword.equals("long"))
                                type = Token.Type.LONG;
                            break;
                        case 't':
                            if (keyword.equals("true"))
                                type = Token.Type.TRUE;
                            break;
                        default:
                            return new IdentifierToken(
                                current.position(),
                                iterator.current().position(),
                                keyword
                            );
                    }
                    return new SpecialToken(
                        current.position(),
                        iterator.current().position(),
                        type
                    );

                    
                } else {
                    throw new InvalidCodePointException(iterator.current());
                }
        }
        
        if (tokenType != null) {
            return new SpecialToken(
                current.position(), iterator.current().position(),
                tokenType);
        } else {
            throw new InvalidCodePointException(iterator.current());
        }
    }
    
    /**
     * Пропускает все пробельные символы и переносы строк, а также комментарии в
     * тексте программы.
     * 
     * @return true, если в тексте программы еще остались символы, false в 
     *         противном случае
     */
    private boolean skipWhitespacesAndComments() {
        while (iterator.hasNext() && 
                Character.isWhitespace(iterator.current().value())) {
            iterator.next();
        }
        if(iterator.current().value() == '/' && iterator.hasNext()) {
            CodePoint c;
            if(iterator.hasNext()) {
                c = iterator.watchNext();
                if(c.value() == '/') {
                    iterator.advance(2);
                    while (iterator.hasNext() && 
                           iterator.current().value() != '\n') {

                        iterator.next();
                    }
                } else if(c.value() == '*') {
                    iterator.advance(2);
                    while (iterator.hasNext()) {
                        int ch = iterator.current().value();
                        iterator.next();
                        if(ch == '*' && iterator.current().value() == '/')
                            break;
                    }
                    iterator.advance(1);
                }
            }
        }
        return !iterator.hasNext();
    }

    private String text;
    private CodePointIterator iterator;
    private final List<Character> reservedSymbols = Arrays.asList(
        '.', ',', ':', ';', '-', '+', '/', '\'', '%', '*', '&', '>', '<', '=',
        '[', ']', '{', '}', '(', ')', '|', '^', '!', '~'
    );
}
