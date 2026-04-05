package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * The SerialKillerEntity plays the role of the persistent antagonist in Level 3.
 * 
 * Unlike Pale Luna (who operates on a timer system with cycles), this enemy 
 * is usually perpetually active and uses a simpler, continuous BFS pathfinding tracker.
 * It also features unique logic to interact with 'CardboardClone' decoys.
 * 
 * Development History:
 * - Phase 2, Week 2, Day 10: Aggressive tracking logic and kill-states implemented.
 * - Phase 3, Week 3, Day 19: Tweaked movement logic scaling to keep them relentlessly behind the player.
 */
public class SerialKillerEntity extends Entity implements Collidable {

    // ==============================================================
    // CONFIG & STATE FIELDS
    // ==============================================================

    /** Defines constant linear movement speed. */
    private static final double SPEED = 1.75;

    /** If true, the killer is awake and actively calculating tracking logic. */
    private boolean active;
    
    /** Flag denoting if the killer is currently locked into fighting a decoy item. */
    private boolean attackingDecoy;
    
    /** Countdown timer logic checking how long the killer remains distracted by a clone. */
    private int decoyAttackFrames;

    // ==============================================================
    // CONSTRUCTOR
    // ==============================================================

    public SerialKillerEntity(double x, double y) {
        super(x, y, 24.0);
    }

    // ==============================================================
    // GAME LOGIC
    // ==============================================================

    /** 
     * Core loop tick. Handled passively for internal timers (like attacking the decoy),
     * independent from the movement calculations.
     */
    @Override
    public void update() {
        if (decoyAttackFrames > 0) {
            decoyAttackFrames--;
            // Once the timer hits 0, the killer finishes destroying the decoy and resumes hunting.
            if (decoyAttackFrames == 0) {
                attackingDecoy = false;
            }
        }
    }

    /**
     * Re-evaluates pathfinding to a target (the player, or a clone).
     * 
     * @param targetX The absolute X destination to track towards.
     * @param targetY The absolute Y destination to track towards.
     * @param maze    The map providing the BFS `getNextMove` data representation.
     */
    public void updateChase(double targetX, double targetY, Maze maze) {
        // Halt movement completely if inactive or busy tearing up a cardboard clone
        if (!active || attackingDecoy) {
            return;
        }

        // 1. Translate pixel coordinates to Tile Grid indexes
        int currentC = (int) ((this.x + size / 2) / Maze.TILE_SIZE);
        int currentR = (int) ((this.y + size / 2 - Maze.Y_OFFSET) / Maze.TILE_SIZE);
        int targetC = (int) ((targetX + 10) / Maze.TILE_SIZE);
        int targetR = (int) ((targetY + 10 - Maze.Y_OFFSET) / Maze.TILE_SIZE);

        // 2. Query maze for BFS shortest path vector
        int[] nextTile = maze.getNextMove(currentR, currentC, targetR, targetC);
        if (nextTile == null) {
            return;
        }

        // 3. Move mathematically towards the center of that target tile
        double nextX = nextTile[1] * Maze.TILE_SIZE + (Maze.TILE_SIZE - size) / 2;
        double nextY = nextTile[0] * Maze.TILE_SIZE + Maze.Y_OFFSET + (Maze.TILE_SIZE - size) / 2;

        double dx = nextX - x;
        double dy = nextY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        
        // Normalize the vector and apply speed modifier
        if (dist > 0.0) {
            double move = Math.min(SPEED, dist);
            x += (dx / dist) * move;
            y += (dy / dist) * move;
        }
    }

    // ==============================================================
    // RENDERING
    // ==============================================================

    /** Draws the enemy character. Darker color palette than Pale Luna. */
    @Override
    public void render(GraphicsContext gc) {
        // Draw the main cloak/body (less opaque if inactive)
        gc.setFill(Color.rgb(15, 15, 15, active ? 0.95 : 0.35));
        gc.fillRect(x + 5, y + 2, 14, 20);
        
        // Draw the pale face/mask
        gc.setFill(Color.rgb(180, 150, 120, active ? 1.0 : 0.5));
        gc.fillOval(x + 6, y - 2, 12, 10);
        
        // Draw the red knife jutting out
        gc.setFill(Color.rgb(160, 20, 20));
        gc.fillRect(x + 18, y + 8, 5, 1.8);
        
        // Render a bloody red aura burst if actively violently attacking a clone
        if (attackingDecoy) {
            gc.setFill(Color.rgb(170, 20, 20, 0.4));
            gc.fillOval(x - 5, y - 5, size + 10, size + 10);
        }
    }

    // ==============================================================
    // GETTERS & SETTERS
    // ==============================================================

    /** @return Hitbox dimension required by Collidable interface. */
    @Override
    public Rectangle2D getHitbox() {
        return new Rectangle2D(x, y, size, size);
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /** Initiates the distraction sequence where the killer stops tracking the player. */
    public void startDecoyAttack() {
        this.attackingDecoy = true;
        this.decoyAttackFrames = 600; // 10 seconds of distraction (at 60fps)
    }

    public boolean isAttackingDecoy() {
        return attackingDecoy;
    }
}
