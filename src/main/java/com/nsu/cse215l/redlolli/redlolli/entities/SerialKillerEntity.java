package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Persistent antagonist in Level 3 using continuous BFS pathfinding.
 * Features unique logic to interact with CardboardClone decoys.
 */
public class SerialKillerEntity extends Entity implements Collidable {

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
        gc.setFill(Color.rgb(15, 15, 15, active ? 0.95 : 0.35));
        gc.fillRect(x + 5, y + 2, 14, 20);

        gc.setFill(Color.rgb(180, 150, 120, active ? 1.0 : 0.5));
        gc.fillOval(x + 6, y - 2, 12, 10);

        gc.setFill(Color.rgb(160, 20, 20));
        gc.fillRect(x + 18, y + 8, 5, 1.8);

        if (attackingDecoy) {
            gc.setFill(Color.rgb(170, 20, 20, 0.4));
            gc.fillOval(x - 5, y - 5, size + 10, size + 10);
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
