package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import com.nsu.cse215l.redlolli.redlolli.core.Hitbox2D;

/**
 * The main enemy of the game ("Pale Luna"). She follows a strict state machine 
 * (sleeping -> stalking -> hunting -> waiting).
 * She uses pathfinding to chase you through the maze, and her movement speed stays 
 * consistent no matter how fast or slow the game is running.
 */
public class Monster extends Entity implements Collidable {

    /**
     * The different moods the monster can be in. This controls how fast she moves 
     * and whether she's currently chasing you.
     */
    public enum State {
        DORMANT, STALKING, HUNTING, WAITING_AT_DOOR
    }

    private State state = State.DORMANT;

    private static final double STALK_SPEED = 3.2;
    private static final double HUNT_SPEED = 6.4;
    private static final int DORMANT_DURATION = 300;
    private static final int STALK_DURATION = 300;
    private static final int HUNT_DURATION = 420;
    private static final int WAIT_DURATION = 120;

    private double dormantTimer = 0;
    private double stalkTimer = 0;
    private double huntTimer = 0;
    private double waitTimer = 0;
    private double pulsePhase = 0.0;
    private boolean facingRight = false;
    private long lastUpdateTime = 0;

    /**
     * Spawns the monster at a starting point, putting her straight to sleep 
     * so she doesn't attack immediately.
     *
     * @param x The horizontal X starting coordinate.
     * @param y The vertical Y starting coordinate.
     */
    public Monster(double x, double y) {
        super(x, y, 25.0);
        this.dormantTimer = DORMANT_DURATION;
    }

    /**
     * An empty update method kept simply because the base Entity class requires it.
     * We use a custom, more complex update method below for Pale Luna.
     */
    @Override
    public void update() {
    }

    /**
     * Updates the monster's mood based on how much time has passed and where the player is.
     * This handles smoothly transitioning her between sleeping, stalking, and full-on hunting.
     *
     * @param playerX                The player's current X coordinate.
     * @param playerY                The player's current Y coordinate.
     * @param playerInEscapeRoom     If true, she will stop chasing and wait near the door.
     * @param lolliRecentlyCollected If true, she gets mad and immediately wakes up!
     * @param maze                   The map data she uses to figure out the shortest path to you.
     */
    public void update(double playerX, double playerY, boolean playerInEscapeRoom,
            boolean lolliRecentlyCollected, Maze maze) {

        long now = System.nanoTime();
        if (lastUpdateTime == 0) {
            lastUpdateTime = now;
        }

        // Structure constant multipliers scaling raw nanoseconds uniformly simulating
        // exactly 60 native ticks independently
        double dtSeconds = (now - lastUpdateTime) / 1_000_000_000.0;
        lastUpdateTime = now;
        double timeDelta = dtSeconds * 60.0;
        pulsePhase += 0.1 * timeDelta;

        // Persist orientation states specifically tracking X-axis divergence governing
        // rendering aesthetics unilaterally
        this.facingRight = playerX > this.x;

        switch (state) {
            case DORMANT -> {
                dormantTimer -= timeDelta;

                // Immediately shatter latency constraints universally upon pivotal
                // environmental catalysts (e.g. Item interaction)
                if (dormantTimer <= 0 || lolliRecentlyCollected) {
                    state = State.STALKING;
                    stalkTimer = STALK_DURATION;
                }
            }
            case STALKING -> {
                // Preemptively snap traversal algorithms towards static ambush points avoiding
                // sanctuary intrusions
                if (playerInEscapeRoom) {
                    positionAtDoor(playerX, playerY, maze);
                    state = State.WAITING_AT_DOOR;
                    waitTimer = WAIT_DURATION;
                    break;
                }

                pursuePlayer(playerX, playerY, maze, STALK_SPEED * timeDelta);

                stalkTimer -= timeDelta;
                if (stalkTimer <= 0) {
                    state = State.HUNTING;
                    huntTimer = HUNT_DURATION;
                }
            }
            case HUNTING -> {
                // Abort high-velocity hostility routing geometry unconditionally into proximate
                // ambush positions
                if (playerInEscapeRoom) {
                    positionAtDoor(playerX, playerY, maze);
                    state = State.WAITING_AT_DOOR;
                    waitTimer = WAIT_DURATION;
                    break;
                }

                pursuePlayer(playerX, playerY, maze, HUNT_SPEED * timeDelta);

                huntTimer -= timeDelta;
                if (huntTimer <= 0) {
                    returnToDormant();
                }
            }
            case WAITING_AT_DOOR -> {
                // Restrict ambush perseverance mapping rigid temporal limits before
                // unconditionally cycling states natively
                waitTimer -= timeDelta;
                if (waitTimer <= 0) {
                    returnToDormant();
                }
            }
        }
    }

    /**
     * Resets the monster back to her sleeping state after a hunt.
     */
    private void returnToDormant() {
        state = State.DORMANT;
        dormantTimer = DORMANT_DURATION;
    }

    /**
     * Teleports the monster to the nearest valid floor tile outside the escape room
     * so she doesn't accidentally get trapped inside the walls.
     *
     * @param playerX The player's X coordinate inside the sanctuary.
     * @param playerY The player's Y coordinate inside the sanctuary.
     * @param maze    The map to find a valid floor spot to camp.
     */
    private void positionAtDoor(double playerX, double playerY, Maze maze) {
        int playerCol = (int) (playerX / Maze.TILE_SIZE);
        int playerRow = (int) ((playerY - Maze.Y_OFFSET) / Maze.TILE_SIZE);

        int[][] dirs = { { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 },
                { -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 } };

        // Perform array inspections radiating chronologically matching uninhibited
        // boundaries strictly avoiding physical overlap
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

        // Execute generic non-obstructed offsets gracefully failing dynamic procedural
        // validation arrays natively
        this.x = playerX + 40;
        this.y = playerY;
    }

    /**
     * Moves the monster smoothly towards the player by asking the map for the shortest path.
     * Calculates movement directly to prevent awkward stuttering or overshoot.
     *
     * @param playerX Where the player is on the X axis.
     * @param playerY Where the player is on the Y axis.
     * @param maze    The map data that provides the A* pathfinding.
     * @param speed   How fast the monster should move this single frame.
     */
    private void pursuePlayer(double playerX, double playerY, Maze maze, double speed) {
        int currentC = (int) ((this.x + size / 2) / Maze.TILE_SIZE);
        int currentR = (int) ((this.y + size / 2 - Maze.Y_OFFSET) / Maze.TILE_SIZE);
        int playerC = (int) ((playerX + 10) / Maze.TILE_SIZE);
        int playerR = (int) ((playerY + 10 - Maze.Y_OFFSET) / Maze.TILE_SIZE);

        int[] nextTile = maze.getNextMove(currentR, currentC, playerR, playerC);
        if (nextTile == null)
            return;

        double targetX, targetY;

        // Interpret terminal target alignments mitigating constant center-point
        // oscillations sequentially
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

        // Normalize scalar variables clamping velocity caps preventing bounding box
        // penetration organically
        if (stepDist > 0) {
            double moveDist = Math.min(speed, stepDist);
            this.x += (stepDX / stepDist) * moveDist;
            this.y += (stepDY / stepDist) * moveDist;
        }
    }

    /**
     * Gets the physical boundary (hitbox) around the monster for collision detection.
     *
     * @return The 2D hitbox data.
     */
    @Override
    public Hitbox2D getHitbox() {
        return new Hitbox2D(x, y, size, size);
    }

    /**
     * Gets what mood the monster is currently in (e.g., Stalking, Hunting).
     *
     * @return The monster's current state.
     */
    public State getState() {
        return state;
    }

    /**
     * Convenience method to quickly check if the monster is aggressively hunting.
     *
     * @return True if she's out for blood.
     */
    public boolean isHunting() {
        return state == State.HUNTING;
    }

    /**
     * Convenience method to quickly check if the monster is just casually stalking you.
     *
     * @return True if she's taking her time following you.
     */
    public boolean isStalking() {
        return state == State.STALKING;
    }

    /**
     * Finds out if the monster is currently waiting to jump scare you at the door.
     *
     * @return True if she's camping outside the escape room.
     */
    public boolean isWaitingAtDoor() {
        return state == State.WAITING_AT_DOOR;
    }

    /**
     * Gets how much time is left before she wakes up from her long nap.
     *
     * @return The remaining wait time in ticks.
     */
    public int getDormantTimer() {
        return (int) dormantTimer;
    }

    /**
     * Gets how much time is left before she speeds up and starts hunting.
     *
     * @return The remaining stalk time in ticks.
     */
    public int getStalkTimer() {
        return (int) stalkTimer;
    }

    /**
     * Gets how much time is left before she tires out and goes back to sleep.
     *
     * @return The remaining hunt time in ticks.
     */
    public int getHuntTimer() {
        return (int) huntTimer;
    }

    /**
     * Gets how much time she's going to spend camping outside the door.
     *
     * @return The remaining waiting-at-door time in ticks.
     */
    public int getWaitTimer() {
        return (int) waitTimer;
    }

    /**
     * Gives a smooth number that can be used to make the monster pulse or breathe visually.
     *
     * @return A slowly increasing animation counter.
     */
    public double getPulsePhase() {
        return pulsePhase;
    }

    /**
     * Tells the graphics system which direction the monster is facing so its sprite 
     * doesn't moonwalk backwards.
     *
     * @return True if she's facing the right side of the screen.
     */
    public boolean isFacingRight() {
        return facingRight;
    }

    /**
     * Returns the visual or physical size of the monster.
     *
     * @return The size of the monster.
     */
    public double getSize() {
        return size;
    }
}
