package ru.bmstu.iu9.compiler.lexis;

import ru.bmstu.iu9.compiler.lexis.token.*;
import com.google.gson.*;
import java.io.*;
import java.util.*;

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
        Lexer lex = new Lexer("C:\\Users\\Bobukh\\Documents\\NetBeansProjects\\Front End\\src\\input.src");
        lex.run();
        lex.toJson("C:\\Users\\Bobukh\\Documents\\NetBeansProjects\\Front End\\src\\output.json");
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