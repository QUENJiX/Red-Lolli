package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.InputStream;

/**
 * Persistent antagonist in Level 3 using continuous BFS pathfinding.
 * Features unique logic to interact with CardboardClone decoys.
 */
public class SerialKillerEntity extends Entity implements Collidable {

    // ================= IMAGE ASSETS =================

    private static Image killerInactiveImg;
    private static Image killerChaseImg;
    private static Image killerAttackImg;
    private static boolean imagesInitialized = false;

    private static Image loadSprite(String filename, int width, int height) {
        try {
            InputStream is = SerialKillerEntity.class.getResourceAsStream("/assets/images/sprites/" + filename);
            if (is != null) {
                return new Image(is, width, height, true, false);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static void initImages() {
        if (imagesInitialized) return;
        killerInactiveImg = loadSprite("killer_inactive.png", 40, 40);
        killerChaseImg = loadSprite("killer_chase.png", 40, 40);
        killerAttackImg = loadSprite("killer_attack.png", 40, 40);
        imagesInitialized = true;
    }

    /** Call this to force images to reload (e.g. after changing asset paths). */
    public static void resetImages() { imagesInitialized = false; }

    // Visual render size (40x40 centered on the 24x24 hitbox)
    private static final double RENDER_SIZE = 40.0;

    private static final double SPEED = 1.75;

    private boolean active;
    private boolean attackingDecoy;
    private int decoyAttackFrames;

    public SerialKillerEntity(double x, double y) {
        super(x, y, 24.0);
    }

    @Override
    public void update() {
        if (decoyAttackFrames > 0) {
            decoyAttackFrames--;
            if (decoyAttackFrames == 0) {
                attackingDecoy = false;
            }
        }
    }

    public void updateChase(double targetX, double targetY, Maze maze) {
        if (!active || attackingDecoy) {
            return;
        }

        int currentC = (int) ((this.x + size / 2) / Maze.TILE_SIZE);
        int currentR = (int) ((this.y + size / 2 - Maze.Y_OFFSET) / Maze.TILE_SIZE);
        int targetC = (int) ((targetX + 10) / Maze.TILE_SIZE);
        int targetR = (int) ((targetY + 10 - Maze.Y_OFFSET) / Maze.TILE_SIZE);

        int[] nextTile = maze.getNextMove(currentR, currentC, targetR, targetC);
        if (nextTile == null) {
            return;
        }

        double nextX = nextTile[1] * Maze.TILE_SIZE + (Maze.TILE_SIZE - size) / 2;
        double nextY = nextTile[0] * Maze.TILE_SIZE + Maze.Y_OFFSET + (Maze.TILE_SIZE - size) / 2;

        double dx = nextX - x;
        double dy = nextY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist > 0.0) {
            double move = Math.min(SPEED, dist);
            x += (dx / dist) * move;
            y += (dy / dist) * move;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        Image img;
        if (!active) {
            img = killerInactiveImg;
        } else if (attackingDecoy) {
            img = killerAttackImg;
        } else {
            img = killerChaseImg;
        }
        // Draw centered (hitbox 24x24, sprite 40x40)
        double offset = (RENDER_SIZE - size) / 2;
        if (img != null) {
            gc.drawImage(img, x - offset, y - offset, RENDER_SIZE, RENDER_SIZE);
        } else {
            gc.setFill(active ? Color.rgb(180, 20, 20) : Color.rgb(80, 40, 40));
            gc.fillOval(x - offset, y - offset, RENDER_SIZE, RENDER_SIZE);
        }
    }

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

    public void startDecoyAttack() {
        this.attackingDecoy = true;
        this.decoyAttackFrames = 600;
    }

    public boolean isAttackingDecoy() {
        return attackingDecoy;
    }
}
