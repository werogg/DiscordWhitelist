package es.werogg.discordwhitelist.managers;

import java.util.logging.Logger;

public class LogManager {

    private static LogManager logManager;
    private Logger logger;

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void info(String message) {
        logger.info("Info - " + message);
    }

    public void warn(String message) {
        logger.warning("Warn - "+ message);
    }

    public void error(String message) {
        logger.warning("Error - " + message);
    }

    public static LogManager getInstance() {
        if (logManager == null) {
            logManager = new LogManager();
        }
        return logManager;
    }
}
