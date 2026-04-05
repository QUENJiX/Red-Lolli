package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

/**
 * The primary antagonist entity for Level 1, "Pale Luna".
 * She operates on a time-based 4-state AI cycle (DORMANT -> STALKING -> HUNTING -> WAITING).
 * Uses BFS (Breadth-First Search) provided by the Maze class to pathfind 
 * toward the player during her active hunting/stalking phases.
 * 
 * Development History:
 * - Phase 2, Week 2, Day 8: Basic enemy skeleton developed alongside initial BFS logic.
 * - Phase 3, Week 3, Day 19: Re-balanced AI pursuit speeds and state transition durations.
 */
public class Monster extends Entity implements Collidable {

    // ==============================================================
    // AI STATE ENUMS & Tracking
    // ==============================================================

    /** State machine governing Luna's behavior cycle. */
    public enum State { 
        DORMANT,         // Invisible/inactive, resting in place
        STALKING,        // Moving slowly towards player, slightly visible
        HUNTING,         // Moving fast towards player, highly visible Red aura
        WAITING_AT_DOOR  // Waiting if player enters Escape Room
    }

    /** Current AI state of the monster. */
    private State state = State.DORMANT;

    // ==============================================================
    // CONSTANTS: Tuning parameters for speed and timers
    // ==============================================================

    /** Movement speed during the creeping stalk phase. */
    private static final double STALK_SPEED = 1.6;
    /** Movement speed during the aggressive hunt phase (must be faster than player walk). */
    private static final double HUNT_SPEED = 3.2;
    /** Time spent dormant before waking. 60 ticks = 1 second. (900 = 15s) */
    private static final int DORMANT_DURATION  = 900; 
    /** Time spent creeping before full sprint. (480 = 8s) */
    private static final int STALK_DURATION = 480;    
    /** Time spent actively sprinting after the player. (360 = 6s) */
    private static final int HUNT_DURATION = 360;     
    /** Time she will wait outside the escape room before giving up. (180 = 3s) */
    private static final int WAIT_DURATION  = 180;  

    // ==============================================================
    // TIMERS & VISUAL VARS
    // ==============================================================

    private int dormantTimer = 0;
    private int stalkTimer = 0;
    private int huntTimer = 0;
    private int waitTimer = 0;

    /** Controls the sine-wave math used to throb the red danger aura. */
    private double pulsePhase = 0.0;

    // ==============================================================
    // CONSTRUCTOR
    // ==============================================================

    /**
     * Initializes Pale Luna at a physical location.
     */
    public Monster(double x, double y) {
        super(x, y, 25.0); // Marginally larger than the player
        this.dormantTimer = DORMANT_DURATION; // Begin in Dormant state
    }

    // ==============================================================
    // GAME LOGIC & AI
    // ==============================================================

    /** Required by interface, but logic is handled by the parameterized method below. */
    @Override
    public void update() { }

    /**
     * The main AI tick evaluated every graphical frame.
     * Manages the timer countdowns, state transitions, and delegates movement.
     *
     * @param playerX                Player's current absolute X coordinate.
     * @param playerY                Player's current absolute Y coordinate.
     * @param playerInEscapeRoom     If true, aborts chases and transitions to waiting.
     * @param lolliRecentlyCollected Picking up the Lolli triggers an instant wake-up.
     * @param maze                   The active maze data used for calculating BFS path.
     */
    public void update(double playerX, double playerY, boolean playerInEscapeRoom,
                        boolean lolliRecentlyCollected, Maze maze) {
        
        // Progress the math variable used to pulse the visual aura
        pulsePhase += 0.1;

        // Execute logic corresponding to the current AI state
        switch (state) {
            case DORMANT -> {
                dormantTimer--;
                // If time runs out OR player triggered the boss by grabbing the item
                if (dormantTimer <= 0 || lolliRecentlyCollected) {
                    state = State.STALKING;
                    stalkTimer = STALK_DURATION;
                }
            }
            case STALKING -> {
                // Instantly abort tracking if player reaches the safe zone
                if (playerInEscapeRoom) {
                    state = State.WAITING_AT_DOOR;
                    waitTimer = WAIT_DURATION;
                    break; 
                }
                
                stalkPlayer(playerX, playerY, maze);
                
                stalkTimer--;
                if (stalkTimer <= 0) {
                    state = State.HUNTING;
                    huntTimer = HUNT_DURATION;
                }
            }
            case HUNTING -> {
                // Instantly abort tracking if player reaches the safe zone
                if (playerInEscapeRoom) {
                    state = State.WAITING_AT_DOOR;
                    waitTimer = WAIT_DURATION;
                    break;
                }
                
                // Actively chase utilizing pathfinding at HUNT_SPEED
                pursuePlayer(playerX, playerY, maze, HUNT_SPEED);
                
                huntTimer--;
                if (huntTimer <= 0) {
                    // Hunt phase ended without catching the player. Reset.
                    returnToDormant();
                }
            }
            case WAITING_AT_DOOR -> {
                // Player is safe inside. Luna waits briefly before teleporting/resetting.
                waitTimer--;
                if (waitTimer <= 0) {
                    returnToDormant();
                }
            }
        }
    }

    /** 
     * Resets the AI state machine back to its passive default.
     * Luna stays exactly where she last was until the timer pops again.
     */
    private void returnToDormant() {
        state = State.DORMANT;
        dormantTimer = DORMANT_DURATION;
    }

    /** Helper delegator for the STALKING phase speed. */
    private void stalkPlayer(double playerX, double playerY, Maze maze) {
        pursuePlayer(playerX, playerY, maze, STALK_SPEED);
    }

    /**
     * The core follow logic. Converts absolute screen coordinates to Maze Tile Grid coordinates,
     * queries the Maze's BFS algorithm for the next logical step, and mathematically moves 
     * the monster smoothly towards that target tile's center.
     */
    private void pursuePlayer(double playerX, double playerY, Maze maze, double speed) {
        
        // 1. Convert pixel positions to Column/Row array indexes
        int currentC = (int) ((this.x + size / 2) / Maze.TILE_SIZE);
        int currentR = (int) ((this.y + size / 2 - Maze.Y_OFFSET) / Maze.TILE_SIZE);
        int playerC  = (int) ((playerX + 10) / Maze.TILE_SIZE);
        int playerR  = (int) ((playerY + 10 - Maze.Y_OFFSET) / Maze.TILE_SIZE);

        // 2. Query BFS for the adjacent tile to move into
        int[] nextTile = maze.getNextMove(currentR, currentC, playerR, playerC);
        
        // If there's no valid path, do absolutely nothing.
        if (nextTile == null) return;

        // 3. Plan math for the physical movement toward the center of the tile
        double targetX, targetY;
        
        // If the very next tile is where the player is standing, target the player directly.
        if (nextTile[0] == playerR && nextTile[1] == playerC) {
            targetX = playerX - (size / 2) + 10;
            targetY = playerY - (size / 2) + 10;
        } else {
            // Otherwise, target the absolute graphical center pixel of the 'next step' tile
            targetX = nextTile[1] * Maze.TILE_SIZE + (Maze.TILE_SIZE - size) / 2;
            targetY = nextTile[0] * Maze.TILE_SIZE + Maze.Y_OFFSET + (Maze.TILE_SIZE - size) / 2;
        }

        // 4. Vector math to move `speed` pixels towards (targetX, targetY)
        double stepDX = targetX - this.x;
        double stepDY = targetY - this.y;
        double stepDist = Math.sqrt(stepDX * stepDX + stepDY * stepDY);

        if (stepDist > 0) {
            // Ensure we don't overshoot the target tile if we are closer than 'speed' pixels
            double moveDist = Math.min(speed, stepDist);
            
            // Normalize the vector and apply speed
            this.x += (stepDX / stepDist) * moveDist;
            this.y += (stepDY / stepDist) * moveDist;
        }
    }

    // ==============================================================
    // RENDERING
    // ==============================================================

    /** Layered rendering block constructing the visual entity. */
    @Override
    public void render(GraphicsContext gc) {
        // Warning aura only draws if she is active
        if (state != State.DORMANT) {
            drawAura(gc);
        }
        drawHead(gc);
        drawHair(gc);
        drawMouth(gc);
    }

    /** 
     * Uses a sine wave over the `pulsePhase` timer to create a throbbing
     * semi-transparent red aura around the monster. 
     */
    private void drawAura(GraphicsContext gc) {
        double pulseOffset = Math.sin(pulsePhase) * 5;
        gc.setFill(Color.rgb(100, 0, 0, 0.35)); // Dark translucent red
        // Size grows and shrinks according to the pulseOffset modifier
        gc.fillOval(x - pulseOffset - 2, y - pulseOffset - 2,
                size + (pulseOffset + 2) * 2, size + (pulseOffset + 2) * 2);
    }

    /** 
     * Draws the skin oval. If dormant, she fades to a ghostly semi-transparent 50% opacity.
     */
    private void drawHead(GraphicsContext gc) {
        double headX = x + 2, headY = y + 1;
        double headW = size - 4, headH = size - 2;

        Color skinColor = (state == State.DORMANT)
                ? Color.rgb(220, 210, 200, 0.5)
                : Color.rgb(240, 230, 220); // Stark pale color when hunting
        gc.setFill(skinColor);
        gc.fillOval(headX, headY, headW, headH);
    }

    /** 
     * Draws her distinctive yellow blonde hair using an arc mapped over the top 
     * half of her head oval. Fades when dormant.
     */
    private void drawHair(GraphicsContext gc) {
        double headX = x + 2, headY = y + 1;
        double headW = size - 4, headH = size - 2;

        Color hairColor = (state == State.DORMANT)
                ? Color.rgb(180, 160, 40, 0.5)
                : Color.rgb(220, 200, 60);
        gc.setFill(hairColor);
        gc.fillArc(headX - 1, headY - 3, headW + 2, headH * 0.6, 0, 180, ArcType.ROUND);
        gc.fillOval(headX - 2, headY + 2, 5, headH - 4);
        gc.fillOval(headX + headW - 3, headY + 2, 5, headH - 4);
    }

    /** Draws the mouth expression based on current state. */
    private void drawMouth(GraphicsContext gc) {
        double headX = x + 2, headY = y + 1;
        double headW = size - 4, headH = size - 2;

        switch (state) {
            case HUNTING -> {
                // Wide creepy grin with teeth
                gc.setStroke(Color.rgb(80, 0, 0));
                gc.setLineWidth(1.5);
                gc.strokeArc(headX + 4, headY + headH * 0.55, headW - 8, 6, 180, 180, ArcType.OPEN);
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(0.5);
                double mouthY = headY + headH * 0.55 + 3;
                for (int i = 0; i < 4; i++) {
                    double tx = headX + 6 + i * 3;
                    gc.strokeLine(tx, mouthY - 1, tx, mouthY + 1);
                }
            }
            case WAITING_AT_DOOR -> {
                // Thin sinister smile
                gc.setStroke(Color.rgb(100, 0, 0));
                gc.setLineWidth(1);
                gc.strokeArc(headX + 5, headY + headH * 0.58, headW - 10, 4, 180, 180, ArcType.OPEN);
            }
            case STALKING -> {
                gc.setStroke(Color.rgb(120, 20, 20));
                gc.setLineWidth(1.2);
                gc.strokeArc(headX + 4, headY + headH * 0.58, headW - 8, 4, 180, 180, ArcType.OPEN);
            }
            default -> {
                // Faint closed mouth (idle/sleeping)
                gc.setStroke(Color.rgb(150, 120, 120, 0.4));
                gc.setLineWidth(0.8);
                gc.strokeLine(headX + 7, headY + headH * 0.65, headX + headW - 7, headY + headH * 0.65);
            }
        }
    }

    // ========================= RENDERING: EYES =========================

    /** Renders Pale Luna's eyes as a separate layer (drawn on top of all entities). */
    public void renderEyes(GraphicsContext gc) {
        double headX = x + 2, headY = y + 1;
        double headW = size - 4, headH = size - 2;
        double eyeY = headY + headH * 0.35;
        double leftEyeX = headX + headW * 0.22;
        double rightEyeX = headX + headW * 0.58;

        switch (state) {
            case DORMANT -> drawIdleEyes(gc, leftEyeX, rightEyeX, eyeY);
            case HUNTING -> drawChasingEyes(gc, leftEyeX, rightEyeX, eyeY);
            case STALKING -> drawStalkingEyes(gc, leftEyeX, rightEyeX, eyeY);
            case WAITING_AT_DOOR -> drawWaitingEyes(gc, leftEyeX, rightEyeX, eyeY);
        }
    }

    /** Dim, half-closed red eyes when sleeping. */
    private void drawIdleEyes(GraphicsContext gc, double leftX, double rightX, double eyeY) {
        gc.setFill(Color.rgb(150, 0, 0, 0.5));
        gc.fillOval(leftX, eyeY + 1, 4, 2);
        gc.fillOval(rightX, eyeY + 1, 4, 2);
    }

    /** Intense, fast-pulsing, glowing red eyes during chase. */
    private void drawChasingEyes(GraphicsContext gc, double leftX, double rightX, double eyeY) {
        double fastPulse = Math.sin(pulsePhase * 3) * 1.5;
        double eyeSize = 5 + fastPulse;

        // Outer glow
        gc.setFill(Color.rgb(255, 0, 0, 0.3));
        gc.fillOval(leftX - 3, eyeY - 3, eyeSize + 6, eyeSize + 5);
        gc.fillOval(rightX - 3, eyeY - 3, eyeSize + 6, eyeSize + 5);

        // Dark eye socket
        gc.setFill(Color.rgb(50, 0, 0));
        gc.fillOval(leftX - 1, eyeY - 1, eyeSize + 2, eyeSize);
        gc.fillOval(rightX - 1, eyeY - 1, eyeSize + 2, eyeSize);

        // Bright red iris
        gc.setFill(Color.rgb(255, 20, 20));
        gc.fillOval(leftX, eyeY, eyeSize, eyeSize - 1);
        gc.fillOval(rightX, eyeY, eyeSize, eyeSize - 1);

        // Hot white center
        gc.setFill(Color.rgb(255, 200, 200));
        gc.fillOval(leftX + 1.5, eyeY + 1, 2, 1.5);
        gc.fillOval(rightX + 1.5, eyeY + 1, 2, 1.5);
    }

    /** Glowing red eyes with slow pulse while waiting at door. */
    private void drawWaitingEyes(GraphicsContext gc, double leftX, double rightX, double eyeY) {
        double pulse = Math.sin(pulsePhase * 0.8) * 0.5;
        double eyeSize = 4 + pulse;

        // Eye socket
        gc.setFill(Color.rgb(40, 0, 0));
        gc.fillOval(leftX - 1, eyeY - 1, eyeSize + 2, eyeSize + 1);
        gc.fillOval(rightX - 1, eyeY - 1, eyeSize + 2, eyeSize + 1);

        // Red iris
        gc.setFill(Color.rgb(220, 0, 0));
        gc.fillOval(leftX, eyeY, eyeSize, eyeSize - 1);
        gc.fillOval(rightX, eyeY, eyeSize, eyeSize - 1);

        // Bright pupil center
        gc.setFill(Color.rgb(255, 100, 100));
        gc.fillOval(leftX + 1, eyeY + 0.5, 2, 1.5);
        gc.fillOval(rightX + 1, eyeY + 0.5, 2, 1.5);
    }

    /** Dim red eyes during stalking. */
    private void drawStalkingEyes(GraphicsContext gc, double leftX, double rightX, double eyeY) {
        double pulse = Math.sin(pulsePhase * 1.4) * 0.8;
        double eyeSize = 3.6 + pulse;
        gc.setFill(Color.rgb(180, 0, 0, 0.35));
        gc.fillOval(leftX - 1, eyeY - 1, eyeSize + 2, eyeSize + 2);
        gc.fillOval(rightX - 1, eyeY - 1, eyeSize + 2, eyeSize + 2);
        gc.setFill(Color.rgb(220, 40, 40, 0.85));
        gc.fillOval(leftX, eyeY, eyeSize, eyeSize);
        gc.fillOval(rightX, eyeY, eyeSize, eyeSize);
    }

    // ========================= COLLISION =========================

    @Override
    public Rectangle2D getHitbox() {
        return new Rectangle2D(x, y, size, size);
    }

    // ========================= STATE QUERIES =========================

    public State getState()      { return state; }
    public boolean isHunting()       { return state == State.HUNTING; }
    public boolean isStalking()      { return state == State.STALKING; }
    public boolean isWaitingAtDoor() { return state == State.WAITING_AT_DOOR; }

    // ========================= TIMER GETTERS =========================

    public int getDormantTimer()  { return dormantTimer; }
    public int getStalkTimer() { return stalkTimer; }
    public int getHuntTimer() { return huntTimer; }
    public int getWaitTimer()  { return waitTimer; }
}