package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import com.nsu.cse215l.redlolli.redlolli.core.Hitbox2D;

/**
 * The main player character that you control.
 * It manages where you are on the map, how much energy (stamina) you have for running, 
 * and your sanity (health) as you try to survive being chased by monsters!
 */
public class Player extends Entity implements Collidable {

    private static final double BASE_SPEED = 3.2;
    private static final double SPRINT_MULTIPLIER = 1.8;
    private static final int MAX_STAMINA_FRAMES = 180;
    private static final int EXHAUSTED_FRAMES = 180;

    private boolean isInEscapeRoom = false;
    private double staminaFrames = MAX_STAMINA_FRAMES;
    private double exhaustedFrames = 0;
    private double facingX = 1;
    private double facingY = 0;

    private static final int MAX_SANITY = 100;
    private static final int PASSIVE_DRAIN_INTERVAL = 60;
    private static final int NEAR_LUNA_DRAIN_INTERVAL = 30;
    private static final int ESCAPE_ROOM_RECOVERY_INTERVAL = 60;
    private static final int NEAR_LUNA_DISTANCE = 150;
    private int sanity = MAX_SANITY;
    private double sanityDrainCounter = 0;
    private boolean isNearLuna = false;
    private boolean sanityDead = false;

    private int animFrame = 0;
    private double animTimer = 0;
    private boolean isMoving = false;
    private boolean movedThisFrame = false;

    private long lastUpdateTime = 0;
    private double timeDelta = 1.0;

    /**
     * Drops the player character into the world at a starting coordinate.
     *
     * @param x The straight-up starting X position on the map grid.
     * @param y The straight-up starting Y position on the map grid.
     */
    public Player(double x, double y) {
        super(x, y, 20.0);
    }

    /**
     * Updates the player's internal systems each frame. 
     * This mainly checks your stamina to see if you can sprint and your sanity 
     * to see if you should take damage (or heal up if you found a safe room).
     */
    @Override
    public void update() {
        long now = System.nanoTime();
        if (lastUpdateTime == 0)
            lastUpdateTime = now;

        // Formulate relative delta multipliers standardizing 60hz equivalents
        // analytically across differing processing intervals
        double dtSeconds = (now - lastUpdateTime) / 1_000_000_000.0;
        lastUpdateTime = now;
        timeDelta = dtSeconds * 60.0;

        // Process punitive physical restrictions governing overexertion organically
        if (exhaustedFrames > 0) {
            exhaustedFrames -= timeDelta;
        }

        // Interpolate cosmetic progression frames contingent specifically upon
        // deliberate spatial translation
        if (movedThisFrame) {
            animTimer += timeDelta;
            if (animTimer > 10) {
                animFrame++;
                animTimer = 0;
            }
        } else {
            animFrame = 0;
            animTimer = 0;
        }

        isMoving = movedThisFrame;
        movedThisFrame = false;

        // Arbitrate final mortality consequences mapping psychological exhaustion
        // limits natively
        if (sanity <= 0) {
            sanityDead = true;
            sanity = 0;
            return;
        }

        if (isInEscapeRoom) {
            // Induce systemic psychological recovery strictly mapping sanctuary thresholds
            // linearly
            sanityDrainCounter += timeDelta;
            if (sanityDrainCounter >= ESCAPE_ROOM_RECOVERY_INTERVAL && sanity < MAX_SANITY) {
                sanity++;
                sanityDrainCounter = 0;
            }
        } else {
            // Apply psychological degradation metrics exponentially weighted by adversarial
            // proximity radii
            sanityDrainCounter += timeDelta;
            int drainInterval = isNearLuna ? NEAR_LUNA_DRAIN_INTERVAL : PASSIVE_DRAIN_INTERVAL;
            if (sanityDrainCounter >= drainInterval) {
                sanity--;
                sanityDrainCounter = 0;
                if (sanity <= 0) {
                    sanity = 0;
                    sanityDead = true;
                }
            }
        }
    }

    /**
     * Tries to move the player around the map, stopping them if they bump into a wall.
     * Also drains stamina if the player is holding the run button.
     *
     * @param dx        The direction the player wants to move horizontally (-1, 0, or 1).
     * @param dy        The direction the player wants to move vertically (-1, 0, or 1).
     * @param maze      The map itself, so we can check if the player's path is clear.
     * @param sprinting Whether the player is currently trying to sprint.
     */
    public void move(double dx, double dy, Maze maze, boolean sprinting) {
        double speed = getMovementSpeed(sprinting) * timeDelta;

        double nextX = this.x + (dx * speed);
        double nextY = this.y + (dy * speed);

        Hitbox2D nextHitbox = new Hitbox2D(nextX, nextY, size, size);

        // Sanction spatial relocation solely upon passing definitive obstruction
        // validations natively
        if (!maze.isWallCollision(nextHitbox)) {
            this.x = nextX;
            this.y = nextY;

            // Retain recent movement vectors governing explicit aesthetic or interaction
            // alignments sequentially
            if (dx != 0 || dy != 0) {
                facingX = dx;
                facingY = dy;
                movedThisFrame = true;
            }
        }

        // Dictate metabolic resource degradation factoring intentional sprint inputs
        // mathematically alongside existing exhaustion states
        if (sprinting && exhaustedFrames == 0 && staminaFrames > 0 && (dx != 0 || dy != 0)) {
            staminaFrames -= timeDelta;
            if (staminaFrames <= 0) {
                staminaFrames = 0;
                exhaustedFrames = EXHAUSTED_FRAMES;
            }
        } else if (!sprinting && exhaustedFrames == 0 && staminaFrames < MAX_STAMINA_FRAMES) {
            // Enact gradual regeneration organically offsetting depleted parameter values
            // proportionally
            staminaFrames += 0.5 * timeDelta;
            if (staminaFrames > MAX_STAMINA_FRAMES) {
                staminaFrames = MAX_STAMINA_FRAMES;
            }
        }
    }

    /**
     * Lets the game know if the player moved this frame, usually so it can 
     * draw the correct walking animation.
     *
     * @return True if the player actively took a step this frame.
     */
    public boolean isMoving() {
        return isMoving;
    }

    /**
     * Figures out which animation frame to draw right now based on how far we've walked.
     *
     * @return The number corresponding to the current sprite animation frame.
     */
    public int getAnimFrame() {
        return animFrame;
    }

    /**
     * Grabs the physical size (hitbox) of the player.
     * This is what gets checked when the game wants to see if we got eaten by the monster 
     * or bumped into a wall.
     *
     * @return A Hitbox2D covering the player's physical space on the map.
     */
    @Override
    public Hitbox2D getHitbox() {
        return new Hitbox2D(x, y, size, size);
    }

    /**
     * A simple hook to let the player know if a monster is currently running after them.
     * This can be used for playing intense heartbeat sounds or changing animations.
     *
     * @param chased True if a scary monster is aggressively hunting the player.
     */
    public void setBeingChased(boolean chased) {
    }

    /**
     * Checks if the player is currently hiding inside a sanctuary room.
     * If they are, the monster won't attack them!
     *
     * @return True if the player is standing inside an escape room.
     */
    public boolean isInEscapeRoom() {
        return isInEscapeRoom;
    }

    /**
     * Sets whether the player has safely entered an escape room or just left one.
     *
     * @param inEscapeRoom True to make the player completely safe from monsters.
     */
    public void setInEscapeRoom(boolean inEscapeRoom) {
        this.isInEscapeRoom = inEscapeRoom;
    }

    /**
     * Tells the game if the player ran totally out of breath and needs a breather.
     * If they're exhausted, they can't sprint for a short time.
     *
     * @return True if the player is too tired to run.
     */
    public boolean isExhausted() {
        return exhaustedFrames > 0;
    }

    /**
     * Helper to quickly check if the player still has gas in the tank to run right now.
     *
     * @return True if they have stamina remaining and aren't completely exhausted.
     */
    public boolean canSprint() {
        return staminaFrames > 0 && exhaustedFrames == 0;
    }

    /**
     * Helper for drawing the stamina bar on the screen by returning stamina as a percentage.
     *
     * @return A number between 0 and 1 indicating how much stamina is left.
     */
    public double getStaminaPercent() {
        return (double) staminaFrames / MAX_STAMINA_FRAMES;
    }

    /**
     * Remembers which way the player was facing horizontally so we draw 
     * the sprite looking the right way when they stop.
     *
     * @return The last known horizontal direction.
     */
    public double getFacingX() {
        return facingX;
    }

    /**
     * Remembers which way the player was facing vertically so we draw 
     * the sprite looking the right way when they stop.
     *
     * @return The last known vertical direction.
     */
    public double getFacingY() {
        return facingY;
    }

    /**
     * Checks the direct distance between the player and Luna. 
     * If she's too close, the player's sanity will drain faster out of fear!
     *
     * @param lunaX The monster's X coordinate.
     * @param lunaY The monster's Y coordinate.
     */
    public void updateNearLunaStatus(double lunaX, double lunaY) {
        double dx = this.x - lunaX;
        double dy = this.y - lunaY;

        // Execute literal Pythagorean formulations circumventing broader grid
        // abstractions exclusively for radial evaluations natively
        double dist = Math.sqrt(dx * dx + dy * dy);
        this.isNearLuna = dist < NEAR_LUNA_DISTANCE;
    }

    /**
     * Finds out exactly how much scary health (sanity) the player has left.
     * Starts at 100 and drains over time when near the monster.
     *
     * @return The exact number of sanity points left.
     */
    public int getSanity() {
        return sanity;
    }

    /**
     * Forcibly sets the player's current sanity points.
     * This is useful for giving them a health potion or refilling 
     * it to full when a new level starts.
     *
     * @param value What the new sanity value should be.
     */
    public void setSanity(int value) {
        this.sanity = value;
    }

    /**
     * Helper for drawing the sanity bar on the screen by giving it as a percentage.
     *
     * @return A fraction from 0.0 to 1.0 indicating how full the bar is.
     */
    public double getSanityPercent() {
        return (double) sanity / MAX_SANITY;
    }

    /**
     * Finds out if the poor player's sanity finally reached zero. 
     * Usually means the monster caught them, or they got too scared and the game should show a Game Over screen.
     *
     * @return True if sanity hit zero.
     */
    public boolean isSanityDead() {
        return sanityDead;
    }

    /**
     * A complete reset to make the player completely sane and healthy again.
     * Resets sanity to MAXIMUM, stops draining, and clears all "is dead" flags.
     */
    public void resetSanity() {
        this.sanity = MAX_SANITY;
        this.sanityDrainCounter = 0;
        this.isNearLuna = false;
        this.sanityDead = false;
    }

    /**
     * Figures out exactly how fast the player should be moving based on if they 
     * are trying to run and whether they're too tired to do so.
     *
     * @param sprinting Whether the player is holding the sprint button.
     * @return The final walking/running speed.
     */
    private double getMovementSpeed(boolean sprinting) {
        // Enforce punitive reductions overriding all subsequent checks mechanically
        // mapped to physiological limits
        if (isExhausted()) {
            return BASE_SPEED * 0.6;
        }

        // Assign boosted metrics explicitly acknowledging biological verifications
        // linearly
        if (sprinting && canSprint()) {
            return BASE_SPEED * SPRINT_MULTIPLIER;
        }
        return BASE_SPEED;
    }
}
