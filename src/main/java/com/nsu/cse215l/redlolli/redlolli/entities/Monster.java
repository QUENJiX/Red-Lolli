package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import com.nsu.cse215l.redlolli.redlolli.core.Hitbox2D;

/**
 * Implements the predominant adversarial AI entity ("Pale Luna") conforming to
 * a rigid,
 * time-constrained procedural finite state machine.
 * System calculations decouple geographic displacement (Breath-First Search
 * routing) from core rendering tasks
 * guaranteeing deterministic physical states irrespective of graphic frame
 * variances.
 */
public class Monster extends Entity implements Collidable {

    /**
     * Enumerates the mutually exclusive operational phases governing traversal,
     * visibility, and tracking metrics.
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
     * Instantiates the primary antagonist establishing initial dimensional
     * coordinates and setting initial AI state.
     *
     * @param x Arbitrary longitudinal coordinate native to the map grid.
     * @param y Arbitrary latitudinal coordinate native to the map grid.
     */
    public Monster(double x, double y) {
        super(x, y, 25.0);
        this.dormantTimer = DORMANT_DURATION;
    }

    /**
     * Inherited implementation executing null baseline modifications preserving
     * compatibility
     * across standardized entity iteration arrays natively.
     */
    @Override
    public void update() {
    }

    /**
     * Processes independent AI logic interpreting spatial target vectors and
     * altering localized geometry constraints.
     * Governs all discrete temporal durations orchestrating cyclical transitions
     * between latent and hostile states.
     *
     * @param playerX                Current localized horizontal intersection
     *                               threshold of the target.
     * @param playerY                Current localized vertical intersection
     *                               threshold of the target.
     * @param playerInEscapeRoom     Geometrical toggle overriding hostile
     *                               trajectory towards proximity-based dormancy.
     * @param lolliRecentlyCollected Override conditional expediting transition
     *                               bounds immediately into hostility.
     * @param maze                   Geographical bounds necessary for dynamic
     *                               procedural node evaluations sequentially.
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
     * Executes state rollbacks wiping sequential pursuit parameters entirely.
     */
    private void returnToDormant() {
        state = State.DORMANT;
        dormantTimer = DORMANT_DURATION;
    }

    /**
     * Reconfigures strict X/Y Cartesian variables forcefully snapping the AI onto
     * traversable perimeter boundaries.
     * Prevents logic locks where geometric rendering attempts intersection beyond
     * established sanctuary domains iteratively.
     *
     * @param playerX Reference vector extracting optimal horizontal proximity
     *                thresholds sequentially.
     * @param playerY Reference vector extracting optimal vertical proximity
     *                thresholds sequentially.
     * @param maze    The matrix evaluated assuring valid walkable node discovery
     *                statically.
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
     * Manipulates localized translation vectors calculating shortest unobstructed
     * paths algorithmically.
     * Implements deterministic Euclidean translations mitigating diagonal
     * coordinate overshoot entirely.
     *
     * @param playerX Endpoint parameter X defining algorithmic pursuit targets
     *                fundamentally.
     * @param playerY Endpoint parameter Y defining algorithmic pursuit targets
     *                fundamentally.
     * @param maze    Pre-calculated node weights yielding shortest path indices
     *                statically.
     * @param speed   Bounding numerical limits defining permissible lateral
     *                translation scales.
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
     * Restitutes geometric containment metrics mirroring native dimensions
     * precisely.
     *
     * @return Hitbox2D Validated rectangular coordinates executing physical
     *         intersection routines externally.
     */
    @Override
    public Hitbox2D getHitbox() {
        return new Hitbox2D(x, y, size, size);
    }

    /**
     * Fetches explicitly defined enum structures governing discrete rendering
     * instructions synchronously.
     *
     * @return State The specific current operational threshold globally extracted.
     */
    public State getState() {
        return state;
    }

    /**
     * Translates active cyclic state tracking asserting unequivocal
     * highest-agitation verifications natively.
     *
     * @return boolean True only executing accelerated traversal mapping natively.
     */
    public boolean isHunting() {
        return state == State.HUNTING;
    }

    /**
     * Translates active cyclic state tracking asserting moderate-agitation
     * verifications natively.
     *
     * @return boolean True traversing standard mapping logic.
     */
    public boolean isStalking() {
        return state == State.STALKING;
    }

    /**
     * Extracts boolean indicators corroborating stationary ambush parameters
     * externally mapping logic dependencies entirely.
     *
     * @return boolean True intrinsically tied to sanctuary ambush states
     *         exclusively.
     */
    public boolean isWaitingAtDoor() {
        return state == State.WAITING_AT_DOOR;
    }

    /**
     * Captures remaining algorithmic integers isolating latency scales seamlessly.
     *
     * @return int Extracted dormancy limit.
     */
    public int getDormantTimer() {
        return (int) dormantTimer;
    }

    /**
     * Captures remaining algorithmic integers isolating moderate pursuit scales
     * seamlessly.
     *
     * @return int Extracted trailing limit.
     */
    public int getStalkTimer() {
        return (int) stalkTimer;
    }

    /**
     * Captures remaining algorithmic integers isolating accelerated pursuit scales
     * seamlessly.
     *
     * @return int Extracted high-velocity trailing limit.
     */
    public int getHuntTimer() {
        return (int) huntTimer;
    }

    /**
     * Captures remaining algorithmic integers isolating sanctuary ambush scales
     * seamlessly.
     *
     * @return int Extracted static door proximity duration.
     */
    public int getWaitTimer() {
        return (int) waitTimer;
    }

    /**
     * Generates persistent scalar variables formulating synchronized visual pulsing
     * mechanics uncoupled physically from movement.
     *
     * @return double Raw aesthetic multiplier intrinsically incrementing natively.
     */
    public double getPulsePhase() {
        return pulsePhase;
    }

    /**
     * Extracts persistent directional booleans formatting aesthetic render
     * inversions strictly mapping spatial velocity arrays.
     *
     * @return boolean True validating horizontal targeting matrices projecting
     *         positively natively.
     */
    public boolean isFacingRight() {
        return facingRight;
    }

    /**
     * Correlates geometric abstractions explicitly mirroring fundamental
     * rectangular arrays identically.
     *
     * @return double Extracted coordinate parameters dictating bounds linearly.
     */
    public double getSize() {
        return size;
    }
}
