package ru.bmstu.iu9.compiler;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA. User: maggot Date: 19.05.11 Time: 20:40 To change
 * this template use File | Settings | File Templates.
 *
 * @author anton.bobukh
 */
public class LoggingException extends CompilerException {
    public LoggingException() {
        super();
        logger.log(Level.WARNING, "", this); // this.getMessage());
    }
    public LoggingException(String message) {
        super(message);
        logger.log(Level.WARNING, "", this); // this.getMessage());
    }

    private static Logger logger = Logger.getLogger("ru.bmstu.iu9.compiler");
}
