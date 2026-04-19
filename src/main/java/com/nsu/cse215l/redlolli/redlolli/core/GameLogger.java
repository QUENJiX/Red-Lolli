package com.nsu.cse215l.redlolli.redlolli.core;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * A utility for writing log messages and game events to a file.
 * This is incredibly useful for tracking down issues or crashes since 
 * console output is frequently lost when the app closes.
 */
public class GameLogger {
    private static final Logger LOGGER = Logger.getLogger("RedLolli");
    private static boolean configured = false;

    /**
     * Sets up the logger to automatically save into a "logs" directory.
     * Limits the file size so the logs don't eat up the user's hard drive.
     */
    public static void configure() {
        if (configured)
            return;
        try {
            File logDir = new File("logs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }

            // Keep up to 3 log files, rotating them at 5MB each
            FileHandler fileHandler = new FileHandler("logs/debug.log", 5 * 1024 * 1024, 3, true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.ALL);

            // Prevent logs from spamming the regular console output
            LOGGER.setUseParentHandlers(false);

            configured = true;
            LOGGER.info("GameLogger configured and initialized.");
        } catch (IOException e) {
            System.err.println("Could not setup logger: " + e.getMessage());
        }
    }

    /**
     * Fetches our ready-to-go logger! If it hasn't been configured yet, 
     * it will automatically set itself up.
     * 
     * @return The main logger instance for recording game events.
     */
    public static Logger getLogger() {
        if (!configured)
            configure();
        return LOGGER;
    }
}
