/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.bmstu.iu9.compiler.lexis;

import org.junit.Ignore;
import ru.bmstu.iu9.compiler.lexis.token.Token;
import java.util.Iterator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author anton.bobukh
 */
public class ScannerTest {
    String testReserverSymbols = 
        ".      \n" +
        "'q'    \n" +
        "'qwe'  \n" +
        "'qwe   \n" +
        "~      \n" +
        "{      \n" +
        "}      \n" +
        "[      \n" +
        "]      \n" +
        "<      \n" +
        "<<     \n" +
        "<=     \n" +
        "<<=    \n" +
        ">      \n" +
        ">>     \n" +
        ">=     \n" +
        ">>=    \n" +
        "(      \n" +
        ")      \n" +
        ":      \n" +
        ",      \n" +
        ";      \n" +
        "+      \n" +
        "+=     \n" +
        "-      \n" +
        "-=     \n" +
        "/      \n" +
        "/=     \n" +
        "%      \n" +
        "%=     \n" +
        "!      \n" +
        "!=     \n" +
        "&      \n" +
        "&=     \n" +
        "&&     \n" +
        "*      \n" +
        "*=     \n" +
        "^      \n" +
        "^=     \n" +
        "|      \n" +
        "|=     \n" +
        "||     \n";
    
    String testComments = 
        "// test    \n" +
        "/* multi   \n" +
        "   line    \n" + 
        "   comment \n" +
        "*/         \n" +
            
        "/* invalid \n";
    
    String testNumericConstants = 
        "1111                                   \n" +
        "0                                      \n" +
        "999999999999999999999999999999999999   \n" + // overflow
        "123abc                                 \n" + // invalid symbols
        
        "00                                     \n" +
        "01234567                               \n" +
        "099999999999999999999999999999999999   \n" + // overflow
        "012345678                              \n" + // invalid symbols
        "0123abc                                \n" + // invalid symbols
        
        "0x0                                    \n" +
        "0X0                                    \n" +
        "0xABC                                  \n" +
        "0Xdef                                  \n" +
        "0x1234567890ABCDEF                     \n" + // overflow
        "0x1234567890ABCDEFGH                   \n" + // invalid symbols
        
        ".1                                     \n" +
        ".0e+9                                  \n" +
        ".9E-0123                               \n" +
        "1.0                                    \n" +
        "2.3e50                                 \n" +
        "99e99                                  \n" +
        "9e+99999                               \n" +  // overflow
        "1E-99999";                                    // overflow

    String testKeywords = 
        "int        \n" +
        "if         \n" +
        "float      \n" +
        "false      \n" +
        "func       \n" +
        "for        \n" +
        "double     \n" +
        "default    \n" +
        "do         \n" +
        "char       \n" +
        "continue   \n" +
        "case       \n" +
        "const      \n" +
        "void       \n" +
        "var        \n" +
        "struct     \n" +
        "switch     \n" +
        "bool       \n" +
        "break      \n" +
        "barrier    \n" +
        "return     \n" +
        "run        \n" +
        "else       \n" +
        "while      \n" +
        "lock       \n" +
        "long       \n" +
        "true";
    
    public ScannerTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of iterator method, of class Scanner.
     */
    @Ignore("")
    @Test
    public void testIterator() {
        System.out.println("iterator");
        Scanner instance = null;
        Iterator expResult = null;
        Iterator result = instance.iterator();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
    @Test//(expected=LexisException.class)
    public void testNextToken() throws LexisException {
        Scanner instance = new Scanner(testReserverSymbols);
        System.out.println("ReserverSymbols");
        for(Token t : instance) {
            continue;
        }
        // TODO review the generated test code and remove the default call to fail.
        //fail("The test case is a prototype.");
        
        instance = new Scanner(testNumericConstants);
        System.out.println("NumericConstants");
        Iterator<Token> iterator = instance.iterator();
        for(Token t : instance) {
            System.out.println(t);
            continue;
        }
        
        instance = new Scanner(testKeywords);
        System.out.println("Keywords");
        for(Token t : instance) {
            System.out.println(t);
            continue;
        }
        
        instance = new Scanner(testComments);
        System.out.println("Comments");
        iterator = instance.iterator();
        for(Token t : instance) {
            System.out.println(t);
            continue;
        }
    }

    /**
     * Test of errorRecovery method, of class Scanner.
     */
    @Ignore("")
    @Test
    public void testErrorRecovery() {
        System.out.println("errorRecovery");
        Scanner instance = null;
        instance.errorRecovery();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of skipWhitespacesAndComments method, of class Scanner.
     */
    @Ignore("")
    @Test
    public void testSkipWhitespacesAndComments() {
        System.out.println("skipWhitespacesAndComments");
        Scanner instance = null;
        boolean expResult = false;
        boolean result = instance.skipWhitespacesAndComments();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
