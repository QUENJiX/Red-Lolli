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
    private static Image monsterAura;
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
        monsterDormant = loadSprite("monster_dormant.png", 25, 25);
        monsterStalking = loadSprite("monster_stalking.png", 25, 25);
        monsterHunting = loadSprite("monster_hunting.png", 25, 25);
        monsterWaiting = loadSprite("monster_waiting.png", 25, 25);
        monsterAura = loadSprite("monster_aura.png", 35, 35);
        imagesInitialized = true;
    }

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
        // Aura (only when not dormant)
        if (state != State.DORMANT && monsterAura != null) {
            double pulse = Math.sin(pulsePhase) * 5;
            gc.setGlobalAlpha(0.35);
            gc.drawImage(monsterAura,
                    x - pulse - 5, y - pulse - 5,
                    size + (pulse + 5) * 2, size + (pulse + 5) * 2);
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
                gc.drawImage(body, x, y, size, size);
                gc.setGlobalAlpha(1.0);
            } else {
                gc.drawImage(body, x, y, size, size);
            }
        } else {
            gc.setFill(Color.MAGENTA);
            gc.fillRect(x, y, size, size);
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
