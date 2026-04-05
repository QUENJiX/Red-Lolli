package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * The CardboardClone represents a stationary decoy entity placed by the player,
 * predominantly utilized in Level 3 to distract wandering threats (like the SerialKillerEntity).
 * 
 * It functions as a static wall/target for enemy AI. When deployed, certain enemies
 * will prioritize walking towards this clone instead of the player.
 * 
 * Development History:
 * - Phase 2, Week 2, Day 11: Decoy mechanics and visual rendering created.
 */
public class CardboardClone extends Entity implements Collidable {

    /**
     * Initializes the decoy at a specific point on the map.
     * 
     * @param x X-coordinate where it was dropped.
     * @param y Y-coordinate where it was dropped.
     */
    public CardboardClone(double x, double y) {
        super(x, y, 20.0);
    }

    // ==============================================================
    // GAME LOGIC
    // ==============================================================

    /** Empty implementation as decoys do not move or require per-frame physics/timers. */
    @Override
    public void update() {
        // Static decoy.
    }

    // ==============================================================
    // RENDERING
    // ==============================================================

    /** 
     * Draws the decoy sprite: a jagged, cardboard-colored crude cutout 
     * roughly resembling the shape and colors of the player.
     */
    @Override
    public void render(GraphicsContext gc) {
        // Draw corrugated cardboard body
        gc.setFill(Color.rgb(185, 160, 120));
        gc.fillRect(x + 3, y + 4, 14, 16);
        
        // Draw lighter cardboard head
        gc.setFill(Color.rgb(215, 200, 170));
        gc.fillOval(x + 4, y - 1, 12, 10);
        
        // Draw crude marker dots for eyes
        gc.setFill(Color.rgb(80, 20, 20));
        gc.fillOval(x + 7, y + 3, 2, 2);
        gc.fillOval(x + 11, y + 3, 2, 2);
    }

    // ==============================================================
    // COLLISION
    // ==============================================================

    /** @return Hitbox dimension required by Collidable interface. */
    @Override
    public Rectangle2D getHitbox() {
        return new Rectangle2D(x, y, size, size);
    }
}
