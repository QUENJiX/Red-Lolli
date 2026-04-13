/**
 * Development History:
 * Phase 1, Week 1, Day 1 - Module structure initialized.
 * Defines the core Java Modules required by Escape Pale Luna and exports
 * internal domains to the JavaFX graphics/reflection engines.
 */
module com.nsu.cse215l.redlolli.redlolli {
    requires transitive javafx.controls;
    requires javafx.media;
    requires transitive javafx.graphics;
    requires java.desktop;

    exports com.nsu.cse215l.redlolli.redlolli;
    exports com.nsu.cse215l.redlolli.redlolli.ui;
    exports com.nsu.cse215l.redlolli.redlolli.entities;
    exports com.nsu.cse215l.redlolli.redlolli.map;
    exports com.nsu.cse215l.redlolli.redlolli.core;
    exports com.nsu.cse215l.redlolli.redlolli.systems;
}