package com.nsu.cse215l.redlolli.redlolli;

import javafx.application.Application;
import com.nsu.cse215l.redlolli.redlolli.core.GameLogger;

/** Application entry point that launches the JavaFX game. */
public class Launcher {

    public static void main(String[] args) {
        GameLogger.configure();
        Application.launch(HelloApplication.class, args);
    }
}
