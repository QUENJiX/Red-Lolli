package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import javafx.geometry.Rectangle2D;

/**
 * Persistent antagonist in Level 3 using continuous BFS pathfinding.
 * Features unique logic to interact with CardboardClone decoys.
 */
public class SerialKillerEntity extends Entity implements Collidable {

    // ================= IMAGE ASSETS =================


            /** Call this to force images to reload (e.g. after changing asset paths). */
        // Height of the killer in pixels. Width is calculated automatically to keep
    // aspect ratio.
    private static final double RENDER_HEIGHT = 48.0;

    private static final double SPEED = 1.75;

    private boolean active;
    private boolean attackingDecoy;
    private long decoyAttackEndTime = 0;

    // Animation state
    private int currentFrame = 0;
    private long lastFrameTime = System.nanoTime();
    private final int ticksPerFrame = 6;
    private boolean facingLeft = true;
    
    private long lastUpdateTime = 0;
    private double timeDelta = 1.0;

    public SerialKillerEntity(double x, double y) {
        super(x, y, 24.0);
    }

    @Override
    public void update() {
        long now = System.nanoTime();
        if (lastUpdateTime == 0) lastUpdateTime = now;
        double dtSeconds = (now - lastUpdateTime) / 1_000_000_000.0;
        lastUpdateTime = now;
        timeDelta = dtSeconds * 60.0;

        if (decoyAttackEndTime != 0) { if(System.nanoTime() >= decoyAttackEndTime) {
                attackingDecoy = false;
                decoyAttackEndTime = 0;
            }
        }

        // Animation logic
        /* uses nanoTime now */
        if (System.nanoTime() - lastFrameTime >= ticksPerFrame * 16_666_666L) {
            lastFrameTime += ticksPerFrame * 16_666_666L;
            currentFrame++;
            int maxFrames = (!active) ? 1 : 5;
            if (currentFrame >= maxFrames) {
                currentFrame = 0;
            }
        }
    }

    public void updateChase(double targetX, double targetY, Maze maze) {
        if (!active || attackingDecoy) {
            return;
        }

        // Find current center tile
        double centerX = x + size / 2;
        double centerY = y + size / 2 - Maze.Y_OFFSET;
        int currentC = (int) (centerX / Maze.TILE_SIZE);
        int currentR = (int) (centerY / Maze.TILE_SIZE);
        int targetC = (int) ((targetX + 10) / Maze.TILE_SIZE);
        int targetR = (int) ((targetY + 10 - Maze.Y_OFFSET) / Maze.TILE_SIZE);

        int[] nextTile = maze.getNextMove(currentR, currentC, targetR, targetC);
        if (nextTile == null) {
            return;
        }

        // Find the absolute center coordinates of the next requested tile
        double targetTileCenterX = nextTile[1] * Maze.TILE_SIZE + Maze.TILE_SIZE / 2.0;
        double targetTileCenterY = nextTile[0] * Maze.TILE_SIZE + Maze.TILE_SIZE / 2.0;

        // Vector math from CURRENT absolute center to TARGET absolute center
        double dx = targetTileCenterX - centerX;
        double dy = targetTileCenterY - centerY;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist > 0.0) {
            if (dx < -0.1)
                facingLeft = true;
            else if (dx > 0.1)
                facingLeft = false;

            double move = Math.min(SPEED * timeDelta, dist);

            // True grid movement to prevent floating-point overshoot jitter and diagonal
            // wall clipping
            if (Math.abs(dx) >= Math.abs(dy)) {
                // Primary movement is horizontal
                if (Math.abs(dx) <= move) {
                    x += dx;
                    move -= Math.abs(dx);
                } else {
                    x += Math.signum(dx) * move;
                    move = 0;
                }
                // Use remaining move, or soft-correct Y to center of hallway
                if (move > 0 && Math.abs(dy) > 0) {
                    y += Math.signum(dy) * Math.min(Math.abs(dy), move);
                } else if (Math.abs(dy) > 0) {
                    y += Math.signum(dy) * Math.min(Math.abs(dy), SPEED * 0.5 * timeDelta);
                }
            } else {
                // Primary movement is vertical
                if (Math.abs(dy) <= move) {
                    y += dy;
                    move -= Math.abs(dy);
                } else {
                    y += Math.signum(dy) * move;
                    move = 0;
                }
                // Use remaining move, or soft-correct X to center of hallway
                if (move > 0 && Math.abs(dx) > 0) {
                    x += Math.signum(dx) * Math.min(Math.abs(dx), move);
                } else if (Math.abs(dx) > 0) {
                    x += Math.signum(dx) * Math.min(Math.abs(dx), SPEED * 0.5 * timeDelta);
                }
            }
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
        this.decoyAttackEndTime = System.nanoTime() + 10_000_000_000L;
    }

    public boolean isAttackingDecoy() {
        return attackingDecoy;
    }

    public int getDecoyAttackFrames() { return decoyAttackEndTime == 0 ? 0 : (int) Math.max(0, (decoyAttackEndTime - System.nanoTime()) / 16_666_666); }

    public boolean getActive() {
        return active;
    }

    public boolean getAttackingDecoy() {
        return attackingDecoy;
    }

    public boolean getFacingLeft() {
        return facingLeft;
    }

    public int getCurrentFrame() {
        return currentFrame;
    }
}
