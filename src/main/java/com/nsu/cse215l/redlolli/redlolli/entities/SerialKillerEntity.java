package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Persistent antagonist in Level 3 using continuous BFS pathfinding.
 * Features unique logic to interact with CardboardClone decoys.
 */
public class SerialKillerEntity extends Entity implements Collidable {

    // ================= IMAGE ASSETS =================

    private static Image idleImg;
    private static Image idleLeftImg;
    private static Image chaseImg;
    private static Image chaseLeftImg;
    private static Image attackImg;
    private static Image attackLeftImg;
    private static boolean imagesInitialized = false;

    private static Image loadSprite(String filename) {
        return com.nsu.cse215l.redlolli.redlolli.systems.AssetManager.getInstance().getSprite("/assets/images/sprites/" + filename);
    }

    public static void initImages() {
        if (imagesInitialized)
            return;
        idleImg = loadSprite("killer_idle_right.png");
        idleLeftImg = loadSprite("killer_idle_left.png");
        chaseImg = loadSprite("killer_chase_right.png");
        chaseLeftImg = loadSprite("killer_chase_left.png");
        attackImg = loadSprite("killer_attack_right.png");
        attackLeftImg = loadSprite("killer_attack_left.png");
        imagesInitialized = true;
    }

    /** Call this to force images to reload (e.g. after changing asset paths). */
    public static void resetImages() {
        imagesInitialized = false;
    }

    // Height of the killer in pixels. Width is calculated automatically to keep
    // aspect ratio.
    private static final double RENDER_HEIGHT = 48.0;

    private static final double SPEED = 1.75;

    private boolean active;
    private boolean attackingDecoy;
    private double decoyAttackFrames;

    // Animation state
    private int currentFrame = 0;
    private double frameTick = 0;
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

        if (decoyAttackFrames > 0) {
            decoyAttackFrames -= timeDelta;
            if (decoyAttackFrames <= 0) {
                attackingDecoy = false;
                decoyAttackFrames = 0;
            }
        }

        // Animation logic
        frameTick += timeDelta;
        if (frameTick >= ticksPerFrame) {
            frameTick = 0;
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
    public void render(GraphicsContext gc) {
        Image imgToDraw;
        int frameWidth = 128;
        int maxFrames = 5;

        if (!active) {
            imgToDraw = facingLeft ? idleLeftImg : idleImg;
            frameWidth = 40; // Idle is now just a single 40 width image
            maxFrames = 1;
        } else if (attackingDecoy) {
            imgToDraw = facingLeft ? attackLeftImg : attackImg;
            frameWidth = 128; // 640 width / 5 frames
            maxFrames = 5;
        } else {
            imgToDraw = facingLeft ? chaseLeftImg : chaseImg;
            frameWidth = 128; // 640 width / 5 frames
            maxFrames = 5;
        }

        // Safety check to prevent IndexOutOfBoundsException during state transitions
        if (currentFrame >= maxFrames) {
            currentFrame = 0;
        }

        // Calculate aspect-correct width based on the active animation frame
        double scale = RENDER_HEIGHT / 70.0;
        double drawWidth = frameWidth * scale;

        // Draw centered (hitbox 24x24)
        double offsetX = (drawWidth - size) / 2;
        double offsetY = (RENDER_HEIGHT - size) / 2;

        if (imgToDraw != null) {
            int sourceX = currentFrame * frameWidth;
            gc.drawImage(imgToDraw,
                    sourceX, 0, frameWidth, 70, // Source slice dimensions updated to 70 height
                    x - offsetX, y - offsetY, drawWidth, RENDER_HEIGHT); // Destination bounding box
        } else {
            gc.setFill(active ? Color.rgb(180, 20, 20) : Color.rgb(80, 40, 40));
            gc.fillOval(x - offsetX, y - offsetY, RENDER_HEIGHT, RENDER_HEIGHT);
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

    public int getDecoyAttackFrames() {
        return (int) decoyAttackFrames;
    }
}
