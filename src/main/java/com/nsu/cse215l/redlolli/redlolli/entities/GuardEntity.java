package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Stationary environmental hazards that kill the player on contact.
 * Requires specific mechanics like sneaking or distraction to bypass.
 */
public class GuardEntity extends Entity implements Collidable {

    public enum Type {
        BAT,
        COBRA
    }

    // ================= STATE =================

    private final Type type;

    // The escape room tile (row, col) this guard protects
    private final int escapeRow;
    private final int escapeCol;

    // Distraction state
    private boolean distracted = false;
    private int distractionTimer = 0;

    // Level 1: Bat distraction duration
    private static final int BAT_DISTRACTION_DURATION = 300; // 5 seconds

    // Level 2: Cobra distraction duration (TWIST: much shorter distraction window)
    private static final int COBRA_DISTRACTION_DURATION = 150; // 2.5 seconds

    public GuardEntity(double x, double y, Type type, int escapeRow, int escapeCol) {
        super(x, y, 28.0);
        this.type = type;
        this.escapeRow = escapeRow;
        this.escapeCol = escapeCol;
    }

    @Override
    public void update() {
        if (distracted) {
            distractionTimer--;
            if (distractionTimer <= 0) {
                distracted = false;
            }
        }
    }

    public void distract() {
        if (!distracted) {
            distracted = true;
            distractionTimer = type == Type.BAT ? BAT_DISTRACTION_DURATION : COBRA_DISTRACTION_DURATION;
        }
    }

    // ================= SHARED =================

    public boolean isPlayerOnGuardedRoom(Rectangle2D playerHitbox) {
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

    // ================= RENDERING =================

    @Override
    public void render(GraphicsContext gc) {
        if (distracted) {
            if (type == Type.BAT) {
                gc.setFill(Color.rgb(40, 120, 40, 0.22));
            } else {
                gc.setFill(Color.rgb(255, 255, 200, 0.4));
            }
            gc.fillOval(x - 5, y - 5, size + 10, size + 10);
        }

        if (type == Type.BAT) {
            gc.setFill(Color.rgb(45, 45, 55));
            gc.fillOval(x + 3, y + 5, 14, 10);
            gc.fillOval(x - 6, y + 7, 12, 6);
            gc.fillOval(x + 14, y + 7, 12, 6);
            gc.setFill(Color.rgb(220, 40, 40));
            gc.fillOval(x + 7, y + 8, 2, 2);
            gc.fillOval(x + 11, y + 8, 2, 2);
        } else {
            gc.setStroke(Color.rgb(50, 120, 40));
            gc.setLineWidth(3);
            gc.strokeLine(x + 3, y + 14, x + 17, y + 6);
            gc.strokeLine(x + 3, y + 14, x + 17, y + 14);
            gc.setFill(Color.rgb(40, 140, 35));
            gc.fillOval(x + 14, y + 6, 7, 10);
            gc.setFill(Color.rgb(255, 220, 80));
            gc.fillOval(x + 18, y + 9, 2, 2);
        }
    }

    @Override
    public Rectangle2D getHitbox() {
        return new Rectangle2D(x, y, size, size);
    }

    public Type getType() {
        return type;
    }
}
