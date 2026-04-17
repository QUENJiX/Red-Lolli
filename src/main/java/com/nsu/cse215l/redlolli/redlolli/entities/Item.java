package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import javafx.geometry.Rectangle2D;

/**
 * Interactive maze objects modeled as chests containing various loot types.
 * Manages visual state (open/closed) and glowing cues based on content.
 */
public class Item extends Entity implements Collidable {

    public enum ContentType {
        EMPTY,
        LOLLI,
        CLONE_DECOY
    }

    // ================= IMAGE ASSETS =================


            /** Call this to force images to reload (e.g. after changing asset paths). */
        // Visual render size (32x32 centered on the 16x16 hitbox)
    private static final double RENDER_SIZE = 32.0;

        // ================= LOGIC =================

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
}