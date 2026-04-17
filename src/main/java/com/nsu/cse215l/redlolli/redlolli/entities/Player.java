package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import javafx.geometry.Rectangle2D;

/**
 * User-controlled character with movement, stamina, and contextual rendering.
 * Facial expression changes based on chase/escape state.
 */
public class Player extends Entity implements Collidable {

    // ================= IMAGE ASSETS =================


            /** Call this to force images to reload (e.g. after changing asset paths). */
        // Visual render size

    // ================= LOGIC =================

    private static final double BASE_SPEED = 3.2;
    /** Speed multiplier when sprinting. */
    private static final double SPRINT_MULTIPLIER = 1.8;
    /** Max stamina in frames (60 frames = 1 second). */
    private static final int MAX_STAMINA_FRAMES = 180;
    /** Cooldown penalty when stamina hits 0. */
    private static final int EXHAUSTED_FRAMES = 180;

    private boolean isInEscapeRoom = false;
    private long staminaNanos = (long)(MAX_STAMINA_FRAMES / 60.0 * 1_000_000_000L);
    private static final long MAX_STAMINA_NANOS = (long)(MAX_STAMINA_FRAMES / 60.0 * 1_000_000_000L);
    private long exhaustedEndTime = 0;
    private double facingX = 1; // start facing right
    private double facingY = 0;

    // Sanity system
    private static final int MAX_SANITY = 100;
    private static final int PASSIVE_DRAIN_INTERVAL = 60; // 1 sanity per second
    private static final int NEAR_LUNA_DRAIN_INTERVAL = 30; // 2 sanity per second when near Luna
    private static final int ESCAPE_ROOM_RECOVERY_INTERVAL = 60; // +1 sanity per second in escape room
    private static final int NEAR_LUNA_DISTANCE = 150; // pixels threshold for "near Luna"
    private int sanity = MAX_SANITY;
    private long lastSanityDrainTime = System.nanoTime();
    private boolean isNearLuna = false;
    private boolean sanityDead = false; // true when sanity hits 0

    // Animation state
    private int animFrame = 0;
    private long lastAnimTime = System.nanoTime();
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

        if (System.nanoTime() >= exhaustedEndTime) {
        }

        if (movedThisFrame) {
            if(System.nanoTime() - lastAnimTime > 166_666_666L) { animFrame++; lastAnimTime = System.nanoTime(); }
        } else {
            animFrame = 0;
            lastAnimTime = System.nanoTime();
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
            if(System.nanoTime() - lastSanityDrainTime >= (ESCAPE_ROOM_RECOVERY_INTERVAL / 60.0 * 1_000_000_000L) && sanity < MAX_SANITY) { sanity++; lastSanityDrainTime += (long)(ESCAPE_ROOM_RECOVERY_INTERVAL / 60.0 * 1_000_000_000L); }
        } else {
            // Passive drain: 1 per 5 seconds (300 frames)
            // Faster when near Luna: 1 per 2 seconds (120 frames)
            int drainInterval = isNearLuna ? NEAR_LUNA_DRAIN_INTERVAL : PASSIVE_DRAIN_INTERVAL;
            if (lastSanityDrainTime >= drainInterval) {
                sanity--;
                lastSanityDrainTime = System.nanoTime();
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

        Rectangle2D nextHitbox = new Rectangle2D(nextX, nextY, size, size);

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
        if (sprinting && exhaustedEndTime == 0 && staminaNanos > 0 && (dx != 0 || dy != 0)) {
            staminaNanos -= (long)(timeDelta / 60.0 * 1_000_000_000L);
            if(staminaNanos <= 0) { staminaNanos = 0; exhaustedEndTime = System.nanoTime() + (long)(EXHAUSTED_FRAMES / 60.0 * 1_000_000_000L); }
        } else if (!sprinting && exhaustedEndTime == 0 && staminaNanos < MAX_STAMINA_NANOS) {
            staminaNanos += (long)(0.5 * timeDelta / 60.0 * 1_000_000_000L);
            if(staminaNanos > MAX_STAMINA_NANOS) { staminaNanos = MAX_STAMINA_NANOS; }
        }
    }

        @Override
    public Rectangle2D getHitbox() {
        return new Rectangle2D(x, y, size, size);
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

    public boolean isExhausted() { return exhaustedEndTime > System.nanoTime(); }

    public boolean canSprint() { return staminaNanos > 0 && exhaustedEndTime <= System.nanoTime(); }

    public double getStaminaPercent() { return (double) staminaNanos / MAX_STAMINA_NANOS; }

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
        this.lastSanityDrainTime = System.nanoTime();
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

    public boolean isMoving() {
        return isMoving;
    }

    public int getAnimFrame() {
        return animFrame;
    }
}
