package com.nsu.cse215l.redlolli.redlolli;

import javafx.application.Application;
import com.nsu.cse215l.redlolli.redlolli.core.GameLogger;

/**
 * A simple launcher class to kickstart the game!
 * It just sets up the basic environment and hands control over to JavaFX.
 */
public class Launcher {

    /**
     * The very first method that runs when you start the game. 
     * It turns on our logging system and launches the main application window.
     * 
     * @param args Standard command-line arguments.
     */
    public static void main(String[] args) {
        // Turn on the logging system so we can see what's happening behind the scenes
        GameLogger.configure();

        // Hand over the reins to JavaFX to actually start the game loop and open the window
        Application.launch(HelloApplication.class, args);
    }
}
