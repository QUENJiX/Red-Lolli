package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.InputStream;

/**
 * The primary antagonist entity for Level 1, "Pale Luna".
 * Operates on a time-based 4-state AI cycle with BFS pathfinding.
 */
public class Monster extends Entity implements Collidable {

    public enum State {
        DORMANT, STALKING, HUNTING, WAITING_AT_DOOR
    }

    // ================= IMAGE ASSETS =================

    private static Image monsterDormant;
    private static Image monsterStalking;
    private static Image monsterHunting;
    private static Image monsterWaiting;
    private static boolean imagesInitialized = false;

    private static Image loadSprite(String filename, int width, int height) {
        try {
            InputStream is = Monster.class.getResourceAsStream("/assets/images/sprites/" + filename);
            if (is != null) {
                return new Image(is, width, height, true, false);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static void initImages() {
        if (imagesInitialized) return;
        monsterDormant = loadSprite("monster_dormant.png", 40, 40);
        monsterStalking = loadSprite("monster_stalking.png", 40, 40);
        monsterHunting = loadSprite("monster_hunting.png", 40, 40);
        monsterWaiting = loadSprite("monster_waiting.png", 40, 40);
        imagesInitialized = true;
    }

    /** Call this to force images to reload (e.g. after changing asset paths). */
    public static void resetImages() { imagesInitialized = false; }

    // Visual render size (40x40 centered on the 25x25 hitbox)
    private static final double RENDER_SIZE = 40.0;
    private static final double AURA_SIZE = 56.0;

    // ================= STATE =================

    private State state = State.DORMANT;

    private static final double STALK_SPEED = 1.6;
    private static final double HUNT_SPEED = 3.2;
    private static final int DORMANT_DURATION = 900;
    private static final int STALK_DURATION = 480;
    private static final int HUNT_DURATION = 360;
    private static final int WAIT_DURATION = 180;

    private int dormantTimer = 0;
    private int stalkTimer = 0;
    private int huntTimer = 0;
    private int waitTimer = 0;
    private double pulsePhase = 0.0;

    public Monster(double x, double y) {
        super(x, y, 25.0);
        this.dormantTimer = DORMANT_DURATION;
    }

    /** Main AI tick evaluated every graphical frame. */
    @Override
    public void update() {
    }

    public void update(double playerX, double playerY, boolean playerInEscapeRoom,
            boolean lolliRecentlyCollected, Maze maze) {

        pulsePhase += 0.1;

        switch (state) {
            case DORMANT -> {
                dormantTimer--;
                if (dormantTimer <= 0 || lolliRecentlyCollected) {
                    state = State.STALKING;
                    stalkTimer = STALK_DURATION;
                }
            }
            case STALKING -> {
                if (playerInEscapeRoom) {
                    positionAtDoor(playerX, playerY, maze);
                    state = State.WAITING_AT_DOOR;
                    waitTimer = WAIT_DURATION;
                    break;
                }

                pursuePlayer(playerX, playerY, maze, STALK_SPEED);

                stalkTimer--;
                if (stalkTimer <= 0) {
                    state = State.HUNTING;
                    huntTimer = HUNT_DURATION;
                }
            }
            case HUNTING -> {
                if (playerInEscapeRoom) {
                    positionAtDoor(playerX, playerY, maze);
                    state = State.WAITING_AT_DOOR;
                    waitTimer = WAIT_DURATION;
                    break;
                }

                pursuePlayer(playerX, playerY, maze, HUNT_SPEED);

                huntTimer--;
                if (huntTimer <= 0) {
                    returnToDormant();
                }
            }
            case WAITING_AT_DOOR -> {
                waitTimer--;
                if (waitTimer <= 0) {
                    returnToDormant();
                }
            }
        }
    }

    private void returnToDormant() {
        state = State.DORMANT;
        dormantTimer = DORMANT_DURATION;
    }

    /**
     * Positions Luna on a walkable tile adjacent to the player's safe-room
     * location.
     */
    private void positionAtDoor(double playerX, double playerY, Maze maze) {
        int playerCol = (int) (playerX / Maze.TILE_SIZE);
        int playerRow = (int) ((playerY - Maze.Y_OFFSET) / Maze.TILE_SIZE);

        int[][] dirs = { { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 },
                { -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 } };
        for (int[] d : dirs) {
            int nr = playerRow + d[0];
            int nc = playerCol + d[1];
            if (nr >= 0 && nr < maze.getMapGrid().length
                    && nc >= 0 && nc < maze.getMapGrid()[0].length
                    && maze.getMapGrid()[nr][nc] != 1 && maze.getMapGrid()[nr][nc] != 10) {
                this.x = nc * Maze.TILE_SIZE + (Maze.TILE_SIZE - size) / 2;
                this.y = nr * Maze.TILE_SIZE + Maze.Y_OFFSET + (Maze.TILE_SIZE - size) / 2;
                return;
            }
        }
        this.x = playerX + 40;
        this.y = playerY;
    }

    private void pursuePlayer(double playerX, double playerY, Maze maze, double speed) {
        int currentC = (int) ((this.x + size / 2) / Maze.TILE_SIZE);
        int currentR = (int) ((this.y + size / 2 - Maze.Y_OFFSET) / Maze.TILE_SIZE);
        int playerC = (int) ((playerX + 10) / Maze.TILE_SIZE);
        int playerR = (int) ((playerY + 10 - Maze.Y_OFFSET) / Maze.TILE_SIZE);

        int[] nextTile = maze.getNextMove(currentR, currentC, playerR, playerC);
        if (nextTile == null)
            return;

        double targetX, targetY;

        if (nextTile[0] == playerR && nextTile[1] == playerC) {
            targetX = playerX - (size / 2) + 10;
            targetY = playerY - (size / 2) + 10;
        } else {
            targetX = nextTile[1] * Maze.TILE_SIZE + (Maze.TILE_SIZE - size) / 2;
            targetY = nextTile[0] * Maze.TILE_SIZE + Maze.Y_OFFSET + (Maze.TILE_SIZE - size) / 2;
        }

        double stepDX = targetX - this.x;
        double stepDY = targetY - this.y;
        double stepDist = Math.sqrt(stepDX * stepDX + stepDY * stepDY);

        if (stepDist > 0) {
            double moveDist = Math.min(speed, stepDist);
            this.x += (stepDX / stepDist) * moveDist;
            this.y += (stepDY / stepDist) * moveDist;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        double offset = (RENDER_SIZE - size) / 2;
        double cx = x + size / 2;
        double cy = y + size / 2;

        // Aura (only when not dormant)
        if (state != State.DORMANT) {
            double pulse = Math.sin(pulsePhase) * 5;
            double baseRadius = AURA_SIZE / 2 + pulse;
            
            // Base jagged shape mimicking an organic, flickering, torch-like randomized flame
            int numPoints = 16;
            double[] xPoints = new double[numPoints];
            double[] yPoints = new double[numPoints];
            
            for (int layer = 0; layer < 3; layer++) {
                for (int i = 0; i < numPoints; i++) {
                    double angle = Math.PI * 2 * ((double) i / numPoints);
                    // Apply random jitter to radius for a spiky torch effect
                    double radiusJitter = 0.75 + (Math.random() * 0.45);
                    double currentR = baseRadius * radiusJitter;
                    
                    if (layer == 1) currentR *= 0.65;
                    if (layer == 2) currentR *= 0.35;
                    
                    xPoints[i] = cx + (Math.cos(angle) * currentR);
                    yPoints[i] = cy + (Math.sin(angle) * currentR);
                }

                if (layer == 0) {
                    gc.setGlobalAlpha(0.25);
                    gc.setFill(Color.rgb(180, 20, 20));
                } else if (layer == 1) {
                    gc.setGlobalAlpha(0.45);
                    gc.setFill(Color.rgb(220, 30, 30));
                } else {
                    gc.setGlobalAlpha(0.7);
                    gc.setFill(Color.rgb(255, 60, 60));
                }
                gc.fillPolygon(xPoints, yPoints, numPoints);
            }

            // Erratic shuddering static rings for the terrifying aura glitch aesthetic
            gc.setGlobalAlpha(0.6);
            gc.setStroke(Color.rgb(40, 0, 0));
            gc.setLineWidth(1.5);
            double ringShift = (Math.random() - 0.5) * 8;
            gc.strokeOval(cx - baseRadius + ringShift, cy - baseRadius - ringShift, baseRadius * 2, baseRadius * 2);
            
            gc.setGlobalAlpha(1.0);
        }

        // Body composite
        Image body;
        switch (state) {
            case DORMANT -> body = monsterDormant;
            case STALKING -> body = monsterStalking;
            case HUNTING -> body = monsterHunting;
            case WAITING_AT_DOOR -> body = monsterWaiting;
            default -> body = monsterDormant;
        }

        if (body != null) {
            if (state == State.DORMANT) {
                gc.setGlobalAlpha(0.5);
                gc.drawImage(body, x - offset, y - offset, RENDER_SIZE, RENDER_SIZE);
                gc.setGlobalAlpha(1.0);
            } else {
                gc.drawImage(body, x - offset, y - offset, RENDER_SIZE, RENDER_SIZE);
            }
        } else {
            gc.setFill(Color.rgb(220, 220, 240));
            if (state == State.DORMANT) gc.setGlobalAlpha(0.5);
            gc.fillOval(x - offset, y - offset, RENDER_SIZE, RENDER_SIZE);
            gc.setGlobalAlpha(1.0);
        }
    }

    @Override
    public Rectangle2D getHitbox() {
        return new Rectangle2D(x, y, size, size);
    }

    public State getState() {
        return state;
    }

    public boolean isHunting() {
        return state == State.HUNTING;
    }

    public boolean isStalking() {
        return state == State.STALKING;
    }

    public boolean isWaitingAtDoor() {
        return state == State.WAITING_AT_DOOR;
    }

    public int getDormantTimer() {
        return dormantTimer;
    }

    public int getStalkTimer() {
        return stalkTimer;
    }

    public int getHuntTimer() {
        return huntTimer;
    }

    public int getWaitTimer() {
        return waitTimer;
    }
}
