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

    private static Image playerCalmImg;
    private static Image playerTerrifiedImg;
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
        playerCalmImg = loadSprite("player_calm.png", 20, 20);
        playerTerrifiedImg = loadSprite("player_terrified.png", 20, 20);
        imagesInitialized = true;
    }

    // ================= LOGIC =================

    private static final double BASE_SPEED = 2.0;
    /** Speed multiplier when sprinting. */
    private static final double SPRINT_MULTIPLIER = 1.8;
    /** Max stamina in frames (60 frames = 1 second). */
    private static final int MAX_STAMINA_FRAMES = 180;
    /** Cooldown penalty when stamina hits 0. */
    private static final int EXHAUSTED_FRAMES = 120;

    private boolean isBeingChased = false;
    private boolean isInEscapeRoom = false;
    private int staminaFrames = MAX_STAMINA_FRAMES;
    private int exhaustedFrames = 0;
    private double facingX = 0;
    private double facingY = -1;

    public Player(double x, double y) {
        super(x, y, 20.0);
    }

    @Override
    public void update() {
        if (exhaustedFrames > 0) {
            exhaustedFrames--;
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
        Image img = isBeingChased ? playerTerrifiedImg : playerCalmImg;

        if (img != null) {
            gc.drawImage(img, x, y, size, size);
        } else {
            gc.setFill(Color.MAGENTA);
            gc.fillRect(x, y, size, size);
        }
    }

    @Override
    public Rectangle2D getHitbox() {
        return new Rectangle2D(x, y, size, size);
    }

    public void setBeingChased(boolean chased) {
        this.isBeingChased = chased;
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
