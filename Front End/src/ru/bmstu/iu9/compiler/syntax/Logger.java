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
    
    private static final java.util.logging.Logger logger = 
            java.util.logging.Logger.getLogger("Parser");
}