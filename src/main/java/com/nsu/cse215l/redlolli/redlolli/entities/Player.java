package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import com.nsu.cse215l.redlolli.redlolli.core.Hitbox2D;

/**
 * User-controlled character with movement, stamina, and contextual rendering.
 * Facial expression changes based on chase/escape state.
 */
public class Player extends Entity implements Collidable {

    // ================= LOGIC =================

    private static final double BASE_SPEED = 3.2;
    /** Speed multiplier when sprinting. */
    private static final double SPRINT_MULTIPLIER = 1.8;
    /** Max stamina in frames (60 frames = 1 second). */
    private static final int MAX_STAMINA_FRAMES = 180;
    /** Cooldown penalty when stamina hits 0. */
    private static final int EXHAUSTED_FRAMES = 180;

    private boolean isInEscapeRoom = false;
    private double staminaFrames = MAX_STAMINA_FRAMES;
    private double exhaustedFrames = 0;
    private double facingX = 1; // start facing right
    private double facingY = 0;

    // Sanity system
    private static final int MAX_SANITY = 100;
    private static final int PASSIVE_DRAIN_INTERVAL = 60; // 1 sanity per second
    private static final int NEAR_LUNA_DRAIN_INTERVAL = 30; // 2 sanity per second when near Luna
    private static final int ESCAPE_ROOM_RECOVERY_INTERVAL = 60; // +1 sanity per second in escape room
    private static final int NEAR_LUNA_DISTANCE = 150; // pixels threshold for "near Luna"
    private int sanity = MAX_SANITY;
    private double sanityDrainCounter = 0;
    private boolean isNearLuna = false;
    private boolean sanityDead = false; // true when sanity hits 0

    // Animation state
    private int animFrame = 0;
    private double animTimer = 0;
    private boolean isMoving = false;
    private boolean movedThisFrame = false;

    private long lastUpdateTime = 0;
    private double timeDelta = 1.0;

    public Player(double x, double y) {
        super(x, y, 20.0);
    }

    @Override
    public void update() {
        long now = System.nanoTime();
        if (lastUpdateTime == 0) lastUpdateTime = now;
        double dtSeconds = (now - lastUpdateTime) / 1_000_000_000.0;
        lastUpdateTime = now;
        timeDelta = dtSeconds * 60.0;

        if (exhaustedFrames > 0) {
            exhaustedFrames -= timeDelta;
        }

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
        movedThisFrame = false; // Reset for the next frame

        // Sanity drain/recovery logic
        if (sanity <= 0) {
            sanityDead = true;
            sanity = 0;
            return;
        }

        if (isInEscapeRoom) {
            // Recovery in escape rooms: +1 per second (60 frames)
            sanityDrainCounter += timeDelta;
            if (sanityDrainCounter >= ESCAPE_ROOM_RECOVERY_INTERVAL && sanity < MAX_SANITY) {
                sanity++;
                sanityDrainCounter = 0;
            }
        } else {
            // Passive drain: 1 per 5 seconds (300 frames)
            // Faster when near Luna: 1 per 2 seconds (120 frames)
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

    /** Move the player with wall collision and stamina management. */
    public void move(double dx, double dy, Maze maze, boolean sprinting) {
        double speed = getMovementSpeed(sprinting) * timeDelta;

        double nextX = this.x + (dx * speed);
        double nextY = this.y + (dy * speed);

        Hitbox2D nextHitbox = new Hitbox2D(nextX, nextY, size, size);

        if (!maze.isWallCollision(nextHitbox)) {
            this.x = nextX;
            this.y = nextY;

            if (dx != 0 || dy != 0) {
                facingX = dx;
                facingY = dy;
                movedThisFrame = true;
            }
        }

        // Drain when sprinting + moving + not exhausted; recover when idle/walking
        if (sprinting && exhaustedFrames == 0 && staminaFrames > 0 && (dx != 0 || dy != 0)) {
            staminaFrames -= timeDelta;
            if (staminaFrames <= 0) {
                staminaFrames = 0;
                exhaustedFrames = EXHAUSTED_FRAMES;
            }
        } else if (!sprinting && exhaustedFrames == 0 && staminaFrames < MAX_STAMINA_FRAMES) {
            staminaFrames += 0.5 * timeDelta; // slow recovery
            if (staminaFrames > MAX_STAMINA_FRAMES) {
                staminaFrames = MAX_STAMINA_FRAMES;
            }
        }
    }

    public boolean isMoving() {
        return isMoving;
    }

    public int getAnimFrame() {
        return animFrame;
    }

    @Override
    public Hitbox2D getHitbox() {
        return new Hitbox2D(x, y, size, size);
    }

    public void setBeingChased(boolean chased) {
        // Can be used to change animations/effects when being chased
    }

    public boolean isInEscapeRoom() {
        return isInEscapeRoom;
    }

    public void setInEscapeRoom(boolean inEscapeRoom) {
        this.isInEscapeRoom = inEscapeRoom;
    }

    public boolean isExhausted() {
        return exhaustedFrames > 0;
    }

    public boolean canSprint() {
        return staminaFrames > 0 && exhaustedFrames == 0;
    }

    public double getStaminaPercent() {
        return (double) staminaFrames / MAX_STAMINA_FRAMES;
    }

    public double getFacingX() {
        return facingX;
    }

    public double getFacingY() {
        return facingY;
    }

    /** Updates whether the player is near Luna (used for faster sanity drain). */
    public void updateNearLunaStatus(double lunaX, double lunaY) {
        double dx = this.x - lunaX;
        double dy = this.y - lunaY;
        double dist = Math.sqrt(dx * dx + dy * dy);
        this.isNearLuna = dist < NEAR_LUNA_DISTANCE;
    }

    public int getSanity() {
        return sanity;
    }

    public void setSanity(int value) {
        this.sanity = value;
    }

    public double getSanityPercent() {
        return (double) sanity / MAX_SANITY;
    }

    public boolean isSanityDead() {
        return sanityDead;
    }

    /** Resets sanity to full (used when loading new level). */
    public void resetSanity() {
        this.sanity = MAX_SANITY;
        this.sanityDrainCounter = 0;
        this.isNearLuna = false;
        this.sanityDead = false;
    }

    private double getMovementSpeed(boolean sprinting) {
        if (isExhausted()) {
            return BASE_SPEED * 0.6;
        }
        if (sprinting && canSprint()) {
            return BASE_SPEED * SPRINT_MULTIPLIER;
        }
        return BASE_SPEED;
    }
}
