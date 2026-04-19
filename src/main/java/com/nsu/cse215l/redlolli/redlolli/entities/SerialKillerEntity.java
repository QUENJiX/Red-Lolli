package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import com.nsu.cse215l.redlolli.redlolli.core.Hitbox2D;

/**
 * An advanced tracking enemy that relentlessly hunts the player down.
 * It uses the same Breadth-First pathfinding as the monster to chase the player,
 * but it can also be tricked into attacking a cardboard clone!
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
     * Spawns the killer at a specific set of coordinates on the map.
     * 
     * @param x The starting horizontal position.
     * @param y The starting vertical position.
     */
    public SerialKillerEntity(double x, double y) {
        super(x, y, 24.0);
    }

    /**
     * Ticks the killer's animation forward and updates any distraction timers.
     * His movement logic is handled separately by the updateChase method.
     */
    @Override
    public void update() {
        long now = System.nanoTime();
        if (lastUpdateTime == 0)
            lastUpdateTime = now;

        // Define baseline normalized coefficients translating divergent cycle delays
        // fundamentally
        double dtSeconds = (now - lastUpdateTime) / 1_000_000_000.0;
        lastUpdateTime = now;
        timeDelta = dtSeconds * 60.0;

        // Erode synthetic illusion pursuit thresholds ensuring eventual trajectory
        // recalculation natively
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
     * Finds the shortest path to the target (usually the player) and moves a small amount towards them.
     * Doesn't let the killer get stuck on walls thanks to carefully rounding movement.
     * 
     * @param targetX The target's X position.
     * @param targetY The target's Y position.
     * @param maze    The map to ask for pathfinding directions.
     */
    public void updateChase(double targetX, double targetY, Maze maze) {
        // Assert immediate termination conditionals validating dormant states and
        // ongoing illusion destruction
        if (!active || attackingDecoy) {
            return;
        }

        double centerX = x + size / 2;
        double centerY = y + size / 2 - Maze.Y_OFFSET;

        // Interpret strict floating coordinates yielding fundamental integer array
        // nodes algorithmically
        int currentC = (int) (centerX / Maze.TILE_SIZE);
        int currentR = (int) (centerY / Maze.TILE_SIZE);
        int targetC = (int) ((targetX + 10) / Maze.TILE_SIZE);
        int targetR = (int) ((targetY + 10 - Maze.Y_OFFSET) / Maze.TILE_SIZE);

        int[] nextTile = maze.getNextMove(currentR, currentC, targetR, targetC);
        if (nextTile == null) {
            return;
        }

        // Formulate literal coordinate points interpreting next-hop array cells
        // directly
        double targetTileCenterX = nextTile[1] * Maze.TILE_SIZE + Maze.TILE_SIZE / 2.0;
        double targetTileCenterY = nextTile[0] * Maze.TILE_SIZE + Maze.TILE_SIZE / 2.0;

        // Derive relative dimensional divergence resolving distance formulations
        // mathematically
        double dx = targetTileCenterX - centerX;
        double dy = targetTileCenterY - centerY;
        double dist = Math.sqrt(dx * dx + dy * dy);

        if (dist > 0.0) {
            if (dx < -0.1)
                facingLeft = true;
            else if (dx > 0.1)
                facingLeft = false;

            double move = Math.min(SPEED * timeDelta, dist);

            // Structure orthogonal vector isolation unilaterally suppressing arbitrary
            // diagonal wall penetrations logically
            if (Math.abs(dx) >= Math.abs(dy)) {
                // Execute predominant lateral velocity allocations
                if (Math.abs(dx) <= move) {
                    x += dx;
                    move -= Math.abs(dx);
                } else {
                    x += Math.signum(dx) * move;
                    move = 0;
                }

                // Implement minor orthogonal alignment corrections preventing progressive array
                // misalignment natively
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

                // Implement minor lateral alignment corrections ensuring trajectory adherence
                // sequentially
                if (move > 0 && Math.abs(dx) > 0) {
                    x += Math.signum(dx) * Math.min(Math.abs(dx), move);
                } else if (Math.abs(dx) > 0) {
                    x += Math.signum(dx) * Math.min(Math.abs(dx), SPEED * 0.5 * timeDelta);
                }
            }
        }
    }

    /**
     * Gets a rectangle containing the killer's current space on the map for collisions.
     * 
     * @return A Hitbox2D representing the killer.
     */
    @Override
    public Hitbox2D getHitbox() {
        return new Hitbox2D(x, y, size, size);
    }

    /**
     * Checks if the killer has woken up and started hunting.
     * 
     * @return True if the killer is active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Tells the killer to wake up and start hunting, or fall asleep and stop moving.
     * 
     * @param active True if he should hunt.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Fools the killer into thinking a decoy is the player and distracts him for 
     * a massive chunk of time!
     */
    public void startDecoyAttack() {
        this.attackingDecoy = true;
        this.decoyAttackFrames = 600;
    }

    /**
     * Checks if the killer is busy stabbing a piece of cardboard.
     * 
     * @return True if he's currently distracted by the decoy.
     */
    public boolean isAttackingDecoy() {
        return attackingDecoy;
    }

    /**
     * Figures out exactly how much longer the killer will waste time fighting the decoy.
     * 
     * @return The remaining wait ticks before he goes back to hunting the player.
     */
    public int getDecoyAttackFrames() {
        return (int) decoyAttackFrames;
    }

    /**
     * Communicates which way the killer is facing to draw his sprite correctly.
     * 
     * @return True if he's walking entirely left.
     */
    public boolean isFacingLeft() {
        return facingLeft;
    }

    /**
     * Retrieves the current animation frame so the screen renderer 
     * knows which picture of the killer to display right now.
     * 
     * @return An integer representing a single sprite frame.
     */
    public int getCurrentFrame() {
        return currentFrame;
    }

    /**
     * Gets the current logical or pixel size of the tracking entity.
     * 
     * @return The 2D size of the killer.
     */
    public double getSize() {
        return size;
    }
}
