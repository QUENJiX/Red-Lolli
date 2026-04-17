package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import com.nsu.cse215l.redlolli.redlolli.core.Hitbox2D;

/**
 * Operates as the formidable tracking antagonist deployed structurally in advanced environments.
 * Utilizes continuous deterministic Breath-First Search pathfinding for unyielding target acquisition.
 * Features explicitly decoupled geometry mechanics capable of interpreting interactive illusions (i.e. CardboardClone).
 */
public class SerialKillerEntity extends Entity implements Collidable {

    private static final double SPEED = 1.75;

    private boolean active;
    private boolean attackingDecoy;
    private double decoyAttackFrames;

    private int currentFrame = 0;
    private double frameTick = 0;
    private final int ticksPerFrame = 6;
    private boolean facingLeft = true;
    
    private long lastUpdateTime = 0;
    private double timeDelta = 1.0;

    /**
     * Initializes the antagonist defining localized matrix limits and coordinate allocations natively.
     * 
     * @param x Arbitrary longitudinal geometric center.
     * @param y Arbitrary latitudinal geometric center.
     */
    public SerialKillerEntity(double x, double y) {
        super(x, y, 24.0);
    }

    /**
     * Governs internal chronological simulations overriding default temporal iteration mechanisms universally.
     * Integrates hardware-agnostic delta-time verifications rendering deterministic outcomes invariant of frame execution.
     */
    @Override
    public void update() {
        long now = System.nanoTime();
        if (lastUpdateTime == 0) lastUpdateTime = now;
        
        // Define baseline normalized coefficients translating divergent cycle delays fundamentally
        double dtSeconds = (now - lastUpdateTime) / 1_000_000_000.0;
        lastUpdateTime = now;
        timeDelta = dtSeconds * 60.0;

        // Erode synthetic illusion pursuit thresholds ensuring eventual trajectory recalculation natively
        if (decoyAttackFrames > 0) {
            decoyAttackFrames -= timeDelta;
            if (decoyAttackFrames <= 0) {
                attackingDecoy = false;
                decoyAttackFrames = 0;
            }
        }

        // Iterate abstract aesthetic integers dynamically mapped to spatial progression
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

    /**
     * Solves strict geographic traversal metrics mathematically dictating next-hop node allocations unilaterally.
     * Reconciles procedural sub-pixel translations mitigating collision clipping against cardinal node boundaries intrinsically.
     * 
     * @param targetX Proximate Cartesian endpoint explicitly generating horizontal vector derivations natively.
     * @param targetY Proximate Cartesian endpoint explicitly generating vertical vector derivations natively.
     * @param maze Source abstraction querying absolute collision boundaries statically mapped sequentially.
     */
    public void updateChase(double targetX, double targetY, Maze maze) {
        // Assert immediate termination conditionals validating dormant states and ongoing illusion destruction
        if (!active || attackingDecoy) {
            return;
        }

        double centerX = x + size / 2;
        double centerY = y + size / 2 - Maze.Y_OFFSET;
        
        // Interpret strict floating coordinates yielding fundamental integer array nodes algorithmically
        int currentC = (int) (centerX / Maze.TILE_SIZE);
        int currentR = (int) (centerY / Maze.TILE_SIZE);
        int targetC = (int) ((targetX + 10) / Maze.TILE_SIZE);
        int targetR = (int) ((targetY + 10 - Maze.Y_OFFSET) / Maze.TILE_SIZE);

        int[] nextTile = maze.getNextMove(currentR, currentC, targetR, targetC);
        if (nextTile == null) {
            return;
        }

        // Formulate literal coordinate points interpreting next-hop array cells directly
        double targetTileCenterX = nextTile[1] * Maze.TILE_SIZE + Maze.TILE_SIZE / 2.0;
        double targetTileCenterY = nextTile[0] * Maze.TILE_SIZE + Maze.TILE_SIZE / 2.0;

        // Derive relative dimensional divergence resolving distance formulations mathematically
        double dx = targetTileCenterX - centerX;
        double dy = targetTileCenterY - centerY;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist > 0.0) {
            if (dx < -0.1)
                facingLeft = true;
            else if (dx > 0.1)
                facingLeft = false;

            double move = Math.min(SPEED * timeDelta, dist);

            // Structure orthogonal vector isolation unilaterally suppressing arbitrary diagonal wall penetrations logically
            if (Math.abs(dx) >= Math.abs(dy)) {
                // Execute predominant lateral velocity allocations
                if (Math.abs(dx) <= move) {
                    x += dx;
                    move -= Math.abs(dx);
                } else {
                    x += Math.signum(dx) * move;
                    move = 0;
                }
                
                // Implement minor orthogonal alignment corrections preventing progressive array misalignment natively
                if (move > 0 && Math.abs(dy) > 0) {
                    y += Math.signum(dy) * Math.min(Math.abs(dy), move);
                } else if (Math.abs(dy) > 0) {
                    y += Math.signum(dy) * Math.min(Math.abs(dy), SPEED * 0.5 * timeDelta);
                }
            } else {
                // Execute predominant longitudinal velocity allocations
                if (Math.abs(dy) <= move) {
                    y += dy;
                    move -= Math.abs(dy);
                } else {
                    y += Math.signum(dy) * move;
                    move = 0;
                }
                
                // Implement minor lateral alignment corrections ensuring trajectory adherence sequentially
                if (move > 0 && Math.abs(dx) > 0) {
                    x += Math.signum(dx) * Math.min(Math.abs(dx), move);
                } else if (Math.abs(dx) > 0) {
                    x += Math.signum(dx) * Math.min(Math.abs(dx), SPEED * 0.5 * timeDelta);
                }
            }
        }
    }

    /**
     * Resolves physical limits instantiating unmitigable boundaries mathematically mapped globally.
     * 
     * @return Hitbox2D Valid geometric intersection array evaluating continuous interaction queries independently.
     */
    @Override
    public Hitbox2D getHitbox() {
        return new Hitbox2D(x, y, size, size);
    }

    /**
     * Signals active pursuit protocols confirming deterministic AI awakening fundamentally.
     * 
     * @return boolean True extracting awakened traversal verification entirely.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Alters absolute traversal triggers assigning unyielding pursuit execution linearly.
     * 
     * @param active Explicit algorithmic execution verifications externally injected.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Redirects internal trajectory vectors enforcing temporary suspension simulating geometric interaction natively.
     */
    public void startDecoyAttack() {
        this.attackingDecoy = true;
        this.decoyAttackFrames = 600;
    }

    /**
     * Queries internal logic verifying suspension iterations governing synthetic pursuit delays externally identical.
     * 
     * @return boolean True identifying absolute tracking interruptions natively.
     */
    public boolean isAttackingDecoy() {
        return attackingDecoy;
    }

    /**
     * Quantifies residual AI latencies communicating suspension magnitudes explicitly natively.
     * 
     * @return int Arbitrary remaining ticks preceding target reacquisition execution sequentially.
     */
    public int getDecoyAttackFrames() {
        return (int) decoyAttackFrames;
    }

    /**
     * Ascertains explicitly mapped rendering coordinates translating absolute coordinate limits intrinsically.
     * 
     * @return boolean True communicating negative horizontal matrices natively mapped.
     */
    public boolean isFacingLeft() {
        return facingLeft;
    }

    /**
     * Discloses explicit integer alignments calculating transient cosmetic abstractions externally mapped.
     * 
     * @return int Linear animation value natively evaluated structurally.
     */
    public int getCurrentFrame() {
        return currentFrame;
    }

    /**
     * Transmits precise spatial scales communicating identical logic bounds externally evaluated mathematically.
     * 
     * @return double Core scalar interpreting dimensional interaction thresholds identically.
     */
    public double getSize() {
        return size;
    }
}
