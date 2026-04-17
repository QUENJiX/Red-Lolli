package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import javafx.geometry.Rectangle2D;

/** A stationary decoy entity that distracts wandering threats. */
public class CardboardClone extends Entity implements Collidable {

    // ================= IMAGE ASSETS =================


            /** Call this to force images to reload (e.g. after changing asset paths). */
        // Visual render size (50x50 centered on the 20x20 hitbox)
    private static final double RENDER_SIZE = 50.0;

    // ================= LOGIC =================

    public CardboardClone(double x, double y) {
        super(x, y, 20.0);
    }

    @Override
    public void update() {
    }

        @Override
    public Rectangle2D getHitbox() {
        return new Rectangle2D(x, y, size, size);
    }
}
