package ru.bmstu.iu9.compiler;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA. User: maggot Date: 19.05.11 Time: 20:40 To change
 * this template use File | Settings | File Templates.
 *
 * @author anton.bobukh
 */
public class LoggedException
        extends CompilerException {
    public LoggedException(String module) {
        super();
        this.logger = Logger.getLogger(module);
        logger.log(Level.WARNING, "", this); // this.getMessage());
    }
    public LoggedException(String message, String module) {
        super(message);
        this.logger = Logger.getLogger(module);
        logger.log(Level.WARNING, "", this); // this.getMessage());
    }

    private Logger logger;
}
