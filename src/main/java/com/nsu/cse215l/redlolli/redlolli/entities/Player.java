package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.InputStream;

/**
 * User-controlled character with movement, stamina, and contextual rendering.
 * Facial expression changes based on chase/escape state.
 */
public class Player extends Entity implements Collidable {

    // ================= IMAGE ASSETS =================

    private static Image idleFrontImg, idleBackImg, idleLeftImg, idleRightImg;
    private static Image[] walkLeftImgs, walkRightImgs, walkBackImgs, walkFrontImgs;
    private static boolean imagesInitialized = false;

    private static Image loadSprite(String filename, int width, int height) {
        try {
            InputStream is = Player.class.getResourceAsStream("/assets/images/sprites/" + filename);
            if (is != null) {
                return new Image(is, width, height, true, false);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static void initImages() {
        if (imagesInitialized) return;
        idleFrontImg = loadSprite("idle_front.png", 28, 28);
        idleBackImg = loadSprite("idle_back.png", 28, 28);
        idleLeftImg = loadSprite("idle_left.png", 28, 28);
        idleRightImg = loadSprite("idle_right.png", 28, 28);
        
        walkBackImgs = new Image[4];
        for (int i=1; i<=4; i++) walkBackImgs[i-1] = loadSprite("walk_back_" + i + ".png", 28, 28);
        
        walkFrontImgs = new Image[2];
        for (int i=1; i<=2; i++) walkFrontImgs[i-1] = loadSprite("walk_front_" + i + ".png", 28, 28);
        
        walkLeftImgs = new Image[3];
        for (int i=1; i<=3; i++) walkLeftImgs[i-1] = loadSprite("walk_left_" + i + ".png", 28, 28);
        
        walkRightImgs = new Image[3];
        for (int i=1; i<=3; i++) walkRightImgs[i-1] = loadSprite("walk_right_" + i + ".png", 28, 28);

        imagesInitialized = true;
    }

    /** Call this to force images to reload (e.g. after changing asset paths). */
    public static void resetImages() { imagesInitialized = false; }

    // Visual render size
    private static final double RENDER_SIZE = 28.0;

    // ================= LOGIC =================

    private static final double BASE_SPEED = 2.0;
    /** Speed multiplier when sprinting. */
    private static final double SPRINT_MULTIPLIER = 1.8;
    /** Max stamina in frames (60 frames = 1 second). */
    private static final int MAX_STAMINA_FRAMES = 180;
    /** Cooldown penalty when stamina hits 0. */
    private static final int EXHAUSTED_FRAMES = 120;

    private boolean isInEscapeRoom = false;
    private int staminaFrames = MAX_STAMINA_FRAMES;
    private int exhaustedFrames = 0;
    private double facingX = 1; // start facing right
    private double facingY = 0;

    // Sanity system
    private static final int MAX_SANITY = 100;
    private static final int PASSIVE_DRAIN_INTERVAL = 60; // 1 sanity per 5 seconds (300 frames) 
    private static final int NEAR_LUNA_DRAIN_INTERVAL = 120; // 1 sanity per 2 seconds when near Luna
    private static final int ESCAPE_ROOM_RECOVERY_INTERVAL = 60; // +1 sanity per second in escape room
    private static final int NEAR_LUNA_DISTANCE = 150; // pixels threshold for "near Luna"
    private int sanity = MAX_SANITY;
    private int sanityDrainCounter = 0;
    private boolean isNearLuna = false;
    private boolean sanityDead = false; // true when sanity hits 0

    // Animation state
    private int animFrame = 0;
    private int animTimer = 0;
    private boolean isMoving = false;
    private boolean movedThisFrame = false;

    public Player(double x, double y) {
        super(x, y, 20.0);
    }

    @Override
    public void update() {
        if (exhaustedFrames > 0) {
            exhaustedFrames--;
        }

        if (movedThisFrame) {
            animTimer++;
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
            sanityDrainCounter++;
            if (sanityDrainCounter >= ESCAPE_ROOM_RECOVERY_INTERVAL && sanity < MAX_SANITY) {
                sanity++;
                sanityDrainCounter = 0;
            }
        } else {
            // Passive drain: 1 per 5 seconds (300 frames)
            // Faster when near Luna: 1 per 2 seconds (120 frames)
            sanityDrainCounter++;
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
        double speed = getMovementSpeed(sprinting);

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
        if (sprinting && exhaustedFrames == 0 && staminaFrames > 0 && (dx != 0 || dy != 0)) {
            staminaFrames--;
            if (staminaFrames == 0) {
                exhaustedFrames = EXHAUSTED_FRAMES;
            }
        } else if (!sprinting && exhaustedFrames == 0 && staminaFrames < MAX_STAMINA_FRAMES) {
            staminaFrames++;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        String dir = "front";
        if (Math.abs(facingX) > Math.abs(facingY)) {
            dir = facingX > 0 ? "right" : "left";
        } else if (facingY != 0) {
            dir = facingY > 0 ? "front" : "back";
        }

        Image img = idleFrontImg;
        if (!isMoving) {
            switch (dir) {
                case "left": img = idleLeftImg; break;
                case "right": img = idleRightImg; break;
                case "back": img = idleBackImg; break;
                default: img = idleFrontImg; break;
            }
        } else {
            switch (dir) {
                case "left":
                    if (walkLeftImgs != null && walkLeftImgs.length > 0)
                        img = walkLeftImgs[animFrame % walkLeftImgs.length];
                    break;
                case "right":
                    if (walkRightImgs != null && walkRightImgs.length > 0)
                        img = walkRightImgs[animFrame % walkRightImgs.length];
                    break;
                case "back":
                    if (walkBackImgs != null && walkBackImgs.length > 0)
                        img = walkBackImgs[animFrame % walkBackImgs.length];
                    break;
                case "front":
                    if (walkFrontImgs != null && walkFrontImgs.length > 0)
                        img = walkFrontImgs[animFrame % walkFrontImgs.length];
                    else 
                        img = idleFrontImg;
                    break;
            }
        }

        // Draw sprite centered on hitbox (hitbox is 20x20, sprite renders at 40x40)
        double offset = (RENDER_SIZE - size) / 2;
        if (img != null) {
            gc.drawImage(img, x - offset, y - offset, RENDER_SIZE, RENDER_SIZE);
        } else {
            gc.setFill(Color.rgb(100, 149, 237));
            gc.fillOval(x - offset, y - offset, RENDER_SIZE, RENDER_SIZE);
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
