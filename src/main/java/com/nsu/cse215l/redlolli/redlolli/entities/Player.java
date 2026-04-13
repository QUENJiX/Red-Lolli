package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

/**
 * User-controlled character with movement, stamina, and contextual rendering.
 * Aura and facial expression change based on chase/escape state.
 */
public class Player extends Entity implements Collidable {

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

    /** Render the player with layered aura, body, and contextual face. */
    @Override
    public void render(GraphicsContext gc) {
        drawAura(gc);
        drawBody(gc);
        if (isBeingChased) {
            drawTerrifiedFace(gc);
        } else {
            drawCalmFace(gc);
        }
    }

    private void drawAura(GraphicsContext gc) {
        if (isInEscapeRoom) {
            gc.setFill(Color.rgb(0, 180, 0, 0.15));
        } else if (isBeingChased) {
            gc.setFill(Color.rgb(255, 0, 0, 0.12));
        } else {
            gc.setFill(Color.rgb(200, 200, 255, 0.1));
        }
        gc.fillOval(x - 4, y - 4, size + 8, size + 8);
    }

    private void drawBody(GraphicsContext gc) {
        gc.setStroke(isBeingChased ? Color.RED : Color.rgb(100, 80, 120));
        gc.setLineWidth(2);
        gc.strokeOval(x, y, size, size);

        gc.setFill(Color.rgb(240, 235, 230));
        gc.fillOval(x, y, size, size);
        gc.setFill(Color.rgb(255, 250, 245));
        gc.fillOval(x + 2, y + 1, size - 4, size - 3);

        gc.setFill(Color.rgb(255, 150, 150, 0.35));
        gc.fillOval(x + 1, y + 10, 5, 3);
        gc.fillOval(x + 14, y + 10, 5, 3);
    }

    private void drawCalmFace(GraphicsContext gc) {
        gc.setFill(Color.WHITE);
        gc.fillOval(x + 4, y + 4, 5, 7);
        gc.fillOval(x + 11, y + 4, 5, 7);

        gc.setFill(Color.rgb(30, 30, 60));
        gc.fillOval(x + 5, y + 5, 3.5, 5);
        gc.fillOval(x + 12, y + 5, 3.5, 5);

        gc.setFill(Color.BLACK);
        gc.fillOval(x + 5.5, y + 6, 2, 3);
        gc.fillOval(x + 12.5, y + 6, 2, 3);

        gc.setFill(Color.WHITE);
        gc.fillOval(x + 5.5, y + 5.5, 1.5, 1.5);
        gc.fillOval(x + 12.5, y + 5.5, 1.5, 1.5);

        gc.setStroke(Color.rgb(120, 80, 80));
        gc.setLineWidth(1);
        gc.strokeArc(x + 6, y + 12, 8, 4, 180, 180, ArcType.OPEN);
    }

    private void drawTerrifiedFace(GraphicsContext gc) {
        double trembleX = (Math.random() * 2) - 1;
        double trembleY = (Math.random() * 2) - 1;

        gc.setFill(Color.WHITE);
        gc.fillOval(x + 3 + trembleX, y + 3 + trembleY, 6, 8);
        gc.fillOval(x + 11 + trembleX, y + 3 + trembleY, 6, 8);

        gc.setFill(Color.BLACK);
        gc.fillOval(x + 5 + trembleX, y + 6 + trembleY, 2.5, 2.5);
        gc.fillOval(x + 13 + trembleX, y + 6 + trembleY, 2.5, 2.5);

        gc.setFill(Color.rgb(255, 255, 255, 0.8));
        gc.fillOval(x + 5 + trembleX, y + 5.5 + trembleY, 1, 1);
        gc.fillOval(x + 13 + trembleX, y + 5.5 + trembleY, 1, 1);

        gc.setFill(Color.rgb(60, 30, 30));
        gc.fillOval(x + 7.5, y + 13, 5, 4);
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