package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.InputStream;

/**
 * Stationary environmental hazards that kill the player on contact.
 * Requires specific mechanics like sneaking or distraction to bypass.
 */
public class GuardEntity extends Entity implements Collidable {

    public enum Type {
        BAT,
        COBRA
    }

    // ================= IMAGE ASSETS =================

    private static Image batImg;
    private static Image batDistractedImg;
    private static Image cobraImg;
    private static Image cobraDistractedImg;
    private static boolean imagesInitialized = false;

    private static Image loadSprite(String filename, int width, int height) {
        try {
            InputStream is = GuardEntity.class.getResourceAsStream("/assets/images/sprites/" + filename);
            if (is != null) {
                return new Image(is, width, height, true, false);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static void initImages() {
        if (imagesInitialized) return;
        batImg = loadSprite("guard_bat.png", 40, 40);
        batDistractedImg = loadSprite("guard_bat_distracted.png", 40, 40);
        cobraImg = loadSprite("guard_cobra.png", 40, 40);
        cobraDistractedImg = loadSprite("guard_cobra_distracted.png", 40, 40);
        imagesInitialized = true;
    }

    /** Call this to force images to reload (e.g. after changing asset paths). */
    public static void resetImages() { imagesInitialized = false; }

    // Visual render size (40x40 centered on the 28x28 hitbox)
    private static final double RENDER_SIZE = 40.0;

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
        Image img;
        if (type == Type.BAT) {
            img = distracted ? batDistractedImg : batImg;
        } else {
            img = distracted ? cobraDistractedImg : cobraImg;
        }
        // Draw sprite centered on hitbox (hitbox 28x28, sprite 40x40)
        double offset = (RENDER_SIZE - size) / 2;
        if (img != null) {
            gc.drawImage(img, x - offset, y - offset, RENDER_SIZE, RENDER_SIZE);
        } else {
            Color fallback = (type == Type.BAT)
                    ? (distracted ? Color.rgb(50, 120, 50) : Color.rgb(60, 60, 60))
                    : (distracted ? Color.rgb(120, 120, 50) : Color.rgb(80, 80, 30));
            gc.setFill(fallback);
            gc.fillOval(x - offset, y - offset, RENDER_SIZE, RENDER_SIZE);
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
