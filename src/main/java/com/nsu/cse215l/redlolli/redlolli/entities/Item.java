package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import javafx.geometry.Rectangle2D;

/**
 * Interactive maze objects modeled as chests containing various loot types.
 * Manages visual state (open/closed) and glowing cues based on content.
 * This entity has been fully DECOUPLED from JavaFX image rendering.
 */
public class Item extends Entity implements Collidable {

    public enum ContentType {
        EMPTY,
        LOLLI,
        CLONE_DECOY
    }

    private boolean isCollected = false;
    private final ContentType contentType;

    public Item(double x, double y, ContentType contentType) {
        super(x, y, 16.0);
        this.contentType = contentType;
    }

    @Override
    public void update() {
    }

    public void collect() {
        this.isCollected = true;
    }

    @Override
    public Rectangle2D getHitbox() {
        return new Rectangle2D(x, y, size, size);
    }

    public boolean isCollected() {
        return isCollected;
    }

    public boolean hasLolli() {
        return contentType == ContentType.LOLLI;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public double getSize() {
        return size;
    }
}