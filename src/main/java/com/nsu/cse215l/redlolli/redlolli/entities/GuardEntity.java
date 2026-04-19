package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import com.nsu.cse215l.redlolli.redlolli.core.Hitbox2D;

/**
 * A stationary enemy (like a bat, cobra, or centipede) that blocks passage
 * to a specific tile on the map (the escape room).
 * The player has to distract the guard to safely slip past them.
 * Their specific type dictates how quickly they recover from being distracted.
 */
public class GuardEntity extends Entity implements Collidable {

    /**
     * The different kinds of guards, which govern how long they stay distracted.
     */
    public enum Type {
        BAT,
        COBRA,
        CENTIPEDE
    }

    private final Type type;

    private final int escapeRow;
    private final int escapeCol;

    private boolean distracted = false;
    private double distractionTimer = 0;

    private static final int BAT_DISTRACTION_DURATION = 300;
    private static final int COBRA_DISTRACTION_DURATION = 180;
    private static final int CENTIPEDE_DISTRACTION_DURATION = 120;

    private long lastUpdateTime = 0;
    private double timeDelta = 1.0;

    /**
     * Creates a new guard protecting a specific tile on the map.
     *
     * @param x         The starting horizontal position of the guard.
     * @param y         The starting vertical position of the guard.
     * @param type      The kind of guard (BAT, COBRA, CENTIPEDE).
     * @param escapeRow The row on the map grid that this guard protects.
     * @param escapeCol The column on the map grid that this guard protects.
     */
    public GuardEntity(double x, double y, Type type, int escapeRow, int escapeCol) {
        super(x, y, 28.0);
        this.type = type;
        this.escapeRow = escapeRow;
        this.escapeCol = escapeCol;
    }

    /**
     * Updates the guard's state each frame. 
     * Mainly used to decrease their distraction timer over time until they recover.
     */
    @Override
    public void update() {
        long now = System.nanoTime();
        if (lastUpdateTime == 0)
            lastUpdateTime = now;

        // Figure out how much time has passed since the last frame
        double dtSeconds = (now - lastUpdateTime) / 1_000_000_000.0;
        lastUpdateTime = now;
        timeDelta = dtSeconds * 60.0;

        // If the guard is distracted, count down until they go back to normal
        if (distracted) {
            distractionTimer -= timeDelta;
            if (distractionTimer <= 0) {
                distracted = false;
                distractionTimer = 0;
            }
        }
    }

    /**
     * Causes the guard to get distracted and become harmless to the player for a short time.
     * The amount of time depends on the guard's type.
     */
    public void distract() {
        if (!distracted) {
            distracted = true;

            // Set different durations depending on how hard the guard is
            distractionTimer = type == Type.BAT ? BAT_DISTRACTION_DURATION
                    : type == Type.COBRA ? COBRA_DISTRACTION_DURATION : CENTIPEDE_DISTRACTION_DURATION;
        }
    }

    /**
     * Checks if the player stepped onto the specific tile this guard is defending.
     * We don't just check the center point, we check the edges of the player
     * to make sure the guard catches them as soon as they step over the line.
     *
     * @param playerHitbox The boundary of the player character.
     * @return True if the player is touching the protected area.
     */
    public boolean isPlayerOnGuardedRoom(Hitbox2D playerHitbox) {
        double cx = (playerHitbox.getMinX() + playerHitbox.getMaxX()) / 2;
        double cy = (playerHitbox.getMinY() + playerHitbox.getMaxY()) / 2;

        // Check the center and the four outer edges of the player's boundary
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

            // If any of those points touch the guarded row and column, you're caught!
            if (r == escapeRow && c == escapeCol) {
                return true;
            }
        }
        return false;
    }

    /**
     * Lets the game logic know if this guard is currently safe to slowly walk past.
     *
     * @return True if the guard is currently distracted.
     */
    public boolean isDistracted() {
        return distracted;
    }

    /**
     * Calculates the direct distance between the guard and the player in map tiles.
     *
     * @param playerX The player's current horizontal position.
     * @param playerY The player's current vertical position.
     * @return The distance in game tiles.
     */
    public double distanceToPlayerInTiles(double playerX, double playerY) {
        double dx = Math.abs(playerX - x) / Maze.TILE_SIZE;
        double dy = Math.abs(playerY - y) / Maze.TILE_SIZE;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Determines if the player is close enough to throw a distraction at the guard.
     *
     * @param playerX The player's X coordinate.
     * @param playerY The player's Y coordinate.
     * @return True if the player is within 3 tiles of the guard.
     */
    public boolean isWithinDistractionRange(double playerX, double playerY) {
        return distanceToPlayerInTiles(playerX, playerY) <= 3.0;
    }

    /**
     * Provides the physical boundary of the guard so the game knows if 
     * the player directly bumped into it.
     *
     * @return The Hitbox2D encompassing the guard.
     */
    @Override
    public Hitbox2D getHitbox() {
        return new Hitbox2D(x, y, size, size);
    }

    /**
     * Translates immutable instantiation categories interpreting localized visual
     * rendering decisions externally identical.
     *
     * @return Type Concrete archetype classification natively mapped.
     */
    public Type getType() {
        return type;
    }
}
