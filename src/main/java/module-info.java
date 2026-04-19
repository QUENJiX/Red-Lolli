/**
 * Tells Java which libraries Escape Pale Luna needs to run, 
 * especially JavaFX for drawing our graphics and playing sound!
 */
module com.nsu.cse215l.redlolli.redlolli {
    requires transitive javafx.controls;
    requires javafx.media;
    requires transitive javafx.graphics;
    requires java.desktop;
    requires transitive java.logging;

    exports com.nsu.cse215l.redlolli.redlolli;
    exports com.nsu.cse215l.redlolli.redlolli.ui;
    exports com.nsu.cse215l.redlolli.redlolli.entities;
    exports com.nsu.cse215l.redlolli.redlolli.map;
    exports com.nsu.cse215l.redlolli.redlolli.core;
    exports com.nsu.cse215l.redlolli.redlolli.systems;
}