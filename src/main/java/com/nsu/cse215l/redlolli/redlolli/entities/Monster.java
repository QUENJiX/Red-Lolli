package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import javafx.geometry.Rectangle2D;

/**
 * The primary antagonist entity for Level 1, "Pale Luna".
 * Operates on a time-based 4-state AI cycle with BFS pathfinding.
 */
public class Monster extends Entity implements Collidable {

    public enum State {
        DORMANT, STALKING, HUNTING, WAITING_AT_DOOR
    }

    // ================= IMAGE ASSETS =================


            /** Call this to force images to reload (e.g. after changing asset paths). */
        // Visual render size (now 50x50 centered on the 25x25 hitbox)

    // ================= STATE =================

    private State state = State.DORMANT;

    private static final double STALK_SPEED = 3.2;
    private static final double HUNT_SPEED = 6.4; 
    private static final int DORMANT_DURATION = 300;
    private static final int STALK_DURATION = 300;
    private static final int HUNT_DURATION = 420;
    private static final int WAIT_DURATION = 120;

    private long stateStartTime = System.nanoTime();
    private double pulsePhase = 0.0;
    private boolean facingRight = false;
    private long lastUpdateTime = 0;

    public Monster(double x, double y) {
        super(x, y, 25.0);
        this.stateStartTime = System.nanoTime();
    }

    /** Main AI tick evaluated every graphical frame. */
    @Override
    public void update() {
    }

    public void update(double playerX, double playerY, boolean playerInEscapeRoom,
            boolean lolliRecentlyCollected, Maze maze) {

        long now = System.nanoTime();
        if (lastUpdateTime == 0) {
            lastUpdateTime = now;
        }
        double dtSeconds = (now - lastUpdateTime) / 1_000_000_000.0;
        lastUpdateTime = now;

        // At 144Hz, dtSeconds is ~0.007 and timeDelta is ~0.42
        // By skipping multiplying speed by timeDelta, the monster will move at the flat 
        // rate per frame it used to, preserving its original 144Hz movement speed.
        // Wait, if I just revert the timeDelta multiplier on STALK_SPEED and HUNT_SPEED,
        // it will still drop its speed down to 60fps if playing on a 60Hz monitor. 
        // velocities.
        double timeDelta = dtSeconds * 60.0;
        pulsePhase += 0.1 * timeDelta;

        this.facingRight = playerX > this.x;

        switch (state) {
            case DORMANT -> {
                if (System.nanoTime() - stateStartTime >= DORMANT_DURATION || lolliRecentlyCollected) {
                    state = State.STALKING;
                    stateStartTime = System.nanoTime();
                }
            }
            case STALKING -> {
                if (playerInEscapeRoom) {
                    positionAtDoor(playerX, playerY, maze);
                    state = State.WAITING_AT_DOOR;
                    stateStartTime = System.nanoTime();
                    break;
                }

                pursuePlayer(playerX, playerY, maze, STALK_SPEED * timeDelta);

                if (System.nanoTime() - stateStartTime >= STALK_DURATION) {
                    state = State.HUNTING;
                    stateStartTime = System.nanoTime();
                }
            }
            case HUNTING -> {
                if (playerInEscapeRoom) {
                    positionAtDoor(playerX, playerY, maze);
                    state = State.WAITING_AT_DOOR;
                    stateStartTime = System.nanoTime();
                    break;
                }

                pursuePlayer(playerX, playerY, maze, HUNT_SPEED * timeDelta);

                if (System.nanoTime() - stateStartTime >= HUNT_DURATION) {
                    returnToDormant();
                }
            }
            case WAITING_AT_DOOR -> {
                if (System.nanoTime() - stateStartTime >= WAIT_DURATION) {
                    returnToDormant();
                }
            }
        }
    }

    private void returnToDormant() {
        state = State.DORMANT;
        stateStartTime = System.nanoTime();
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

    public int getDormantTimer() { return (int)Math.max(0, (DORMANT_DURATION - (System.nanoTime() - stateStartTime)) / 1_000_000_000.0 * 60); }

    public int getStalkTimer() { return (int)Math.max(0, (STALK_DURATION - (System.nanoTime() - stateStartTime)) / 1_000_000_000.0 * 60); }

    public int getHuntTimer() { return (int)Math.max(0, (HUNT_DURATION - (System.nanoTime() - stateStartTime)) / 1_000_000_000.0 * 60); }

    public int getWaitTimer() { return (int)Math.max(0, (WAIT_DURATION - (System.nanoTime() - stateStartTime)) / 1_000_000_000.0 * 60); }

    public boolean getFacingRight() {
        return facingRight;
    }

    public double getPulsePhase() {
        return pulsePhase;
    }
}
