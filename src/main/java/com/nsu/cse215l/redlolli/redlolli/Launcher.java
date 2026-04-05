package com.nsu.cse215l.redlolli.redlolli;

import javafx.application.Application;

/**
 * Development History:
 * Phase 1, Week 1, Day 1 - Launcher class created to bypass strict JavaFX 11+
 * module-path requirements. By NOT extending Application directly here, 
 * the JVM allows us to boot the game via a standard classpath execution.
 * 
 * Application entry point wrapper.
 * Launches the JavaFX application directly into HelloApplication.
 */
public class Launcher {
    
    /**
     * Bootstraps the application via reflection proxy.
     */
    public static void main(String[] args) {
        Application.launch(HelloApplication.class, args);
    }
}
