package com.nsu.cse215l.redlolli.redlolli.core;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class GameLogger {
    private static final Logger LOGGER = Logger.getLogger("RedLolli");
    private static boolean configured = false;

    public static void configure() {
        if (configured) return;
        try {
            File logDir = new File("logs");
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            
            // 5MB max size per log file, 3 rotating logs
            FileHandler fileHandler = new FileHandler("logs/debug.log", 5 * 1024 * 1024, 3, true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
            LOGGER.setLevel(Level.ALL);
            
            // Optionally disable console output
            LOGGER.setUseParentHandlers(false);
            
            configured = true;
            LOGGER.info("GameLogger configured and initialized.");
        } catch (IOException e) {
            System.err.println("Could not setup logger: " + e.getMessage());
        }
    }

    public static Logger getLogger() {
        if (!configured) configure();
        return LOGGER;
    }
}
