/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.bmstu.iu9.compiler.lexis;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author bobukh
 */
public class LexerTest {
    
    public LexerTest() {
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
     * Test of run method, of class Lexer.
     */
    @Test
    public void testRun() {
        System.out.println("run");
        Lexer instance = null;
        instance.run();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toJson method, of class Lexer.
     */
    @Test
    public void testToJson() {
        System.out.println("toJson");
        String filename = "";
        Lexer instance = null;
        instance.toJson(filename);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of main method, of class Lexer.
     */
    @Test
    public void testMain() {
        System.out.println("main");
        String[] args = null;
        Lexer.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
}
