package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import com.nsu.cse215l.redlolli.redlolli.core.Hitbox2D;

/**
 * Stationary environmental hazards that kill the player on contact.
 * Requires specific mechanics like sneaking or 
 */
public class GuardEntity extends Entity implements Collidable {

    public enum Type {
        BAT,
        COBRA,
        CENTIPEDE
    }

    // ================= STATE =================

    private final Type type;

    // The escape room tile (row, col) this guard protects
    private final int escapeRow;
    private final int escapeCol;

    // Distraction state
    private boolean distracted = false;
    private double distractionTimer = 0;

    // Level 1: Bat distraction duration
    private static final int BAT_DISTRACTION_DURATION = 300; // 5 seconds

    // Level 2: Cobra distraction duration (TWIST: much shorter distraction window)
    private static final int COBRA_DISTRACTION_DURATION = 180; // 3 seconds

    // Level 3: Centipede distraction duration (TWIST: even shorter distraction window)
    private static final int CENTIPEDE_DISTRACTION_DURATION = 120; // 2 seconds

    private long lastUpdateTime = 0;
    private double timeDelta = 1.0;

    public GuardEntity(double x, double y, Type type, int escapeRow, int escapeCol) {
        super(x, y, 28.0);
        this.type = type;
        this.escapeRow = escapeRow;
        this.escapeCol = escapeCol;
    }

    @Override
    public void update() {
        long now = System.nanoTime();
        if (lastUpdateTime == 0) lastUpdateTime = now;
        double dtSeconds = (now - lastUpdateTime) / 1_000_000_000.0;
        lastUpdateTime = now;
        timeDelta = dtSeconds * 60.0;

        if (distracted) {
            distractionTimer -= timeDelta;
            if (distractionTimer <= 0) {
                distracted = false;
                distractionTimer = 0;
            }
        }
    }

    public void distract() {
        if (!distracted) {
            distracted = true;
            distractionTimer = type == Type.BAT ? BAT_DISTRACTION_DURATION : type == Type.COBRA ? COBRA_DISTRACTION_DURATION : CENTIPEDE_DISTRACTION_DURATION;
        }
    }

    // ================= SHARED =================

    public boolean isPlayerOnGuardedRoom(Hitbox2D playerHitbox) {
        double cx = (playerHitbox.getMinX() + playerHitbox.getMaxX()) / 2;
        double cy = (playerHitbox.getMinY() + playerHitbox.getMaxY()) / 2;

        double[][] points = {
                { cx, cy },
                { playerHitbox.getMinX() + 2, cy },
                { playerHitbox.getMaxX() - 2, cy },
                { cx, playerHitbox.getMinY() + 2 },
                { cx, playerHitbox.getMaxY() - 2 }
        };

        for (double[] p : points) {
            int c = (int) (p[0] / Maze.TILE_SIZE);
            int r = (int) ((p[1] - Maze.Y_OFFSET) / Maze.TILE_SIZE);
            if (r == escapeRow && c == escapeCol) {
                return true;
            }
        }
        return false;
    }

    public boolean isDistracted() {
        return distracted;
    }

    public double distanceToPlayerInTiles(double playerX, double playerY) {
        double dx = Math.abs(playerX - x) / Maze.TILE_SIZE;
        double dy = Math.abs(playerY - y) / Maze.TILE_SIZE;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public boolean isWithinDistractionRange(double playerX, double playerY) {
        return distanceToPlayerInTiles(playerX, playerY) <= 3.0; // Both share same range
    }

    @Override
    public Hitbox2D getHitbox() {
        return new Hitbox2D(x, y, size, size);
    }

    public Type getType() {
        return type;
    }
}
