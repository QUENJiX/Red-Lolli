package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * GuardEntity represents stationary environmental hazards/blockades in the game.
 * Serving as minor antagonists or puzzle roadblocks.
 * 
 * They generally sit in hallways and instantly kill the player upon touch,
 * requiring the player to use specific mechanics (like sneaking, invisibility, 
 * or throwing meat) to bypass them.
 * 
 * Development History:
 * - Phase 2, Week 2, Day 9: Initial patrol and vision logic established.
 * - Phase 3, Week 3, Day 19: Detection balancing to ensure fair player passing.
 */
public class GuardEntity extends Entity implements Collidable {

    /** Denotes the visual and mechanical ruleset for this specific Guard instance. */
    public enum Type {
        BAT,   // Used in Level 1; generally responds to player walking speed/sound
        COBRA  // Used in Level 2; acts as a tollgate requiring "meat" to pass
    }

    // ==============================================================
    // STATE FIELDS
    // ==============================================================

    /** The specific species of guard this instance is. */
    private final Type type;
    
    /** Flag denoting if the guard's attention is currently drawn away from the player. */
    private boolean distracted;
    
    /** Explicit counter for cobras to allow the player to walk over them safely X times. */
    private int cobraEntryPasses;

    // ==============================================================
    // CONSTRUCTOR
    // ==============================================================

    /**
     * Spawns a guard obstacle.
     * 
     * @param x    Absolute X coordinate.
     * @param y    Absolute Y coordinate.
     * @param type BAT or COBRA configuration.
     */
    public GuardEntity(double x, double y, Type type) {
        super(x, y, 20.0);
        this.type = type;
    }

    // ==============================================================
    // GAME LOGIC
    // ==============================================================

    @Override
    public void update() {
        // Guards are static sentinel entities; their logic is entirely responsive
        // driven by the main game loop checking distances and inputs.
    }

    // ==============================================================
    // RENDERING
    // ==============================================================

    /** Draws the guard differently based on its Type, and includes status indicators. */
    @Override
    public void render(GraphicsContext gc) {
        
        // --- 1. Distraction Indicator ---
        if (distracted) {
            // Draw a soft green aura to mathematically communicate 'SAFE TO PASS'
            gc.setFill(Color.rgb(40, 120, 40, 0.22));
            gc.fillOval(x - 5, y - 5, size + 10, size + 10);
        }

        // --- 2. Sprite Drawing ---
        if (type == Type.BAT) {
            // Bat Drawing: Body, Wings, and Red Eyes
            gc.setFill(Color.rgb(45, 45, 55)); // Body color
            gc.fillOval(x + 3, y + 5, 14, 10); // Core body
            gc.fillOval(x - 6, y + 7, 12, 6);  // Left wing
            gc.fillOval(x + 14, y + 7, 12, 6); // Right wing
            gc.setFill(Color.rgb(220, 40, 40)); // Eye color
            gc.fillOval(x + 7, y + 8, 2, 2);   // Left eye
            gc.fillOval(x + 11, y + 8, 2, 2);  // Right eye
        } else {
            // Cobra Drawing: Slithering green S-shape with yellow eye
            gc.setStroke(Color.rgb(50, 120, 40));
            gc.setLineWidth(3);
            gc.strokeLine(x + 3, y + 14, x + 17, y + 6);  // S-Curve top
            gc.strokeLine(x + 3, y + 14, x + 17, y + 14); // S-Curve tail
            gc.setFill(Color.rgb(40, 140, 35));
            gc.fillOval(x + 14, y + 6, 7, 10); // Hood / Head
            gc.setFill(Color.rgb(255, 220, 80));
            gc.fillOval(x + 18, y + 9, 2, 2);  // Single menacing yellow eye
        }
    }

    // ==============================================================
    // COLLISION & UTILITIES
    // ==============================================================

    /** @return Hitbox dimension required by Collidable interface. */
    @Override
    public Rectangle2D getHitbox() {
        return new Rectangle2D(x, y, size, size);
    }

    public Type getType() {
        return type;
    }

    /** 
     * Determines whether touching this guard will be instantly fatal right now.
     * Cobras are distracted via their 'pass limit', Bats via the boolean state.
     */
    public boolean isDistracted() {
        if (type == Type.COBRA) {
            return cobraEntryPasses > 0;
        }
        return distracted; // Normal bats
    }

    /** Triggers the distraction state. For bats, this occurs if player throws an item. */
    public void distract() {
        if (type == Type.BAT) {
            distracted = true;
        }
    }

    /** Adds a credit allowing the player to walk through a Cobra instance safely. */
    public void grantCobraEntryPass() {
        if (type == Type.COBRA) {
            cobraEntryPasses = 1;
        }
    }

    /** 
     * Used right before collision. If the player steps on a cobra and has a pass,
     * it consumes the pass and prevents game over.
     */
    public boolean consumeCobraEntryPass() {
        if (type == Type.COBRA && cobraEntryPasses > 0) {
            cobraEntryPasses--;
            return true;
        }
        return false;
    }
}
