/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ru.bmstu.iu9.compiler.syntax;

import ru.bmstu.iu9.compiler.Position;
import ru.bmstu.iu9.compiler.Type;
import ru.bmstu.iu9.compiler.lexis.token.Token;
import java.util.logging.Level;

/**
 *
 * @author maggot
 */
abstract class Logger {
    public static void logUndeclaredType(String type, Position position) {
        logger.log(Level.WARNING, 
                "Type {0} is used at {1} but not declared", 
                new Object[] {
                    type, position
                });
    }
    public static void logUndeclaredType(Type type, Position position) {
        logger.log(Level.WARNING, 
                "Type {0} is used at {1} but not declared", 
                new Object[] {
                    type, position
                });
    }
    public static void logUnknownError(Position position) {
        logger.log(
                java.util.logging.Level.WARNING, 
                "Unknown error at {0}", 
                position);
    }
    public static void logUnexpectedToken(Token.Type found, 
            Token.Type required, Position position) {
        logger.log(
                java.util.logging.Level.WARNING, 
                "Token mismatch at {0}: found {1}, required {2}", 
                new Object[]{
                    position, found, required
                });
    }
    public static void logUnexpectedToken(Token.Type found, 
            String tokensClass, Position position) {
        logger.log(
                java.util.logging.Level.WARNING, 
                "Token mismatch at {0}: found {1}, required {2}", 
                new Object[]{
                    position, found, tokensClass
                });
    }
    public static void logIncompatibleTypes(Type found, Type required, 
            Position position) {
        logger.log(
                java.util.logging.Level.WARNING, 
                "Type mismatch at {0}: found {1}, required {2}", 
                new Object[]{
                    position, found, required
                });
    }
    public static void logIncompatibleTypes(Type found, String requiredTypeClass, 
            Position position) {
        logger.log(
                java.util.logging.Level.WARNING, 
                "Type mismatch at {0}: found {1}, required {2}", 
                new Object[]{
                    position, found, requiredTypeClass
                });
    }
    public static void logIncompatibleTypes(Type found, Type.Typename required, Position position) {
        logger.log(
                java.util.logging.Level.WARNING, 
                "Type mismatch at {0}: found {1}, required {2}", 
                new Object[]{
                    position, found, required
                });
    }
    public static void log(String message, Position position) {
        logger.log(
                Level.WARNING, 
                "{0} at {1}", 
                new Object[] {
                    message, position
                });
    }
    
    private static final java.util.logging.Logger logger = 
            java.util.logging.Logger.getLogger("Parser");
}