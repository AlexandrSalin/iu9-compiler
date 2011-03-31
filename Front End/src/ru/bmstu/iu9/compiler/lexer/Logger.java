/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.lexer;

import java.util.logging.Level;

/**
 *
 * @author maggot
 */
abstract class Logger {
    public static void logUnknownCharacter(Position position) {
        logger.log(
                java.util.logging.Level.WARNING, 
                "Unknown character at {0}", 
                position);
    }
    public static void log(String message, Position position) {
        logger.log(
                Level.WARNING, 
                "{0} at {1}", 
                new Object[] {
                    message, position
                });
    }
    
    private static final java.util.logging.Logger logger
             = java.util.logging.Logger.getLogger("Lexer");;
}
