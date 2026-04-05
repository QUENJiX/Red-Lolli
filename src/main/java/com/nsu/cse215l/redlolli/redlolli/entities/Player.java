package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

/**
 * The Player class represents the user-controlled character in the game.
 * It extends the base Entity class and implements the Collidable interface
 * for interaction with the maze environment (like walls and items).
 * 
 * This class handles crucial mechanics such as:
 * - Movement logic with wall collision detection.
 * - Stamina management for sprinting (including an exhaustion penalty).
 * - Rendering a dynamic sprite whose facial expression and aura change 
 *   contextually based on game states (e.g., being chased or entering the escape room).
 * 
 * Development History:
 * - Phase 1, Week 1, Day 4: Base Player character model and health bounds defined.
 * - Phase 1, Week 1, Day 6: Input handling and movement vectors hooked up to UX.
 * - Phase 1, Week 1, Day 7: Iterative boundary collision testing and speed tuning pass.
 * - Phase 2, Week 2, Day 12: Integrated toggle state for the Flashlight mechanic.
 * - Phase 3, Week 3, Day 20: General polishing and code standardization.
 */
public class Player extends Entity implements Collidable {

    // ==============================================================
    // CONSTANTS: Define core tuning parameters for player mechanics
    // ==============================================================

    /** The default walking speed of the player in pixels per frame. */
    private static final double BASE_SPEED = 2.0;
    
    /** The multiplier applied to BASE_SPEED when the player is sprinting. */
    private static final double SPRINT_MULTIPLIER = 1.8;
    
    /** The maximum limit for stamina, represented in logic frames (e.g., 60 frames = 1 second). */
    private static final int MAX_STAMINA_FRAMES = 180;
    
    /** The cooldown punishment duration if stamina hits 0, preventing sprinting until it wears off. */
    private static final int EXHAUSTED_FRAMES = 120;

    // ==============================================================
    // STATE FIELDS: Track the current dynamic condition of the player
    // ==============================================================

    /** Flag indicating if an enemy is actively pursuing the player; alters visuals. */
    private boolean isBeingChased = false;
    
    /** Flag indicating if the player has entered the safe/exit zone; alters visuals. */
    private boolean isInEscapeRoom = false;
    
    /** Current stamina pool. Decreases while sprinting, regenerates while walking/idle. */
    private int staminaFrames = MAX_STAMINA_FRAMES;
    
    /** Counter for the exhaustion penalty cooldown. 0 means player can sprint normally. */
    private int exhaustedFrames = 0;
    
    /** Represents the X component of the direction the player is currently facing. */
    private double facingX = 0;
    
    /** Represents the Y component of the direction the player is currently facing. Initially faces Up (-1). */
    private double facingY = -1;

    // ==============================================================
    // CONSTRUCTOR
    // ==============================================================

    /**
     * Initializes the player at a specific coordinate in the maze.
     * 
     * @param x The initial X-coordinate in pixels.
     * @param y The initial Y-coordinate in pixels.
     */
    public Player(double x, double y) {
        // Calls the Entity superclass constructor setting position (x, y) and standard size (20.0).
        super(x, y, 20.0);
    }

    // ==============================================================
    // GAME LOGIC: Updates and Movement
    // ==============================================================

    /**
     * Called every game frame to update the player's internal state.
     * Currently manages the passive cooldown of the exhaustion penalty.
     */
    @Override
    public void update() {
        // If the player is exhausted, tick down the penalty timer until it hits 0.
        // Once 0, stamina begins recovering in the move() method.
        if (exhaustedFrames > 0) {
            exhaustedFrames--;
        }
    }

    /**
     * Attempts to move the player in the requested direction, handling wall collisions
     * and managing the stamina/sprint mechanics.
     *
     * @param dx The requested change in the X direction (-1, 0, or 1).
     * @param dy The requested change in the Y direction (-1, 0, or 1).
     * @param maze Reference to the Maze to evaluate valid walkable areas.
     * @param sprinting Whether the user is holding the sprint key.
     */
    public void move(double dx, double dy, Maze maze, boolean sprinting) {
        // Calculate effective speed based on sprint state and exhaustion
        double speed = getMovementSpeed(sprinting);
        
        // Calculate the hypothetical next position *if* the move is executed
        double nextX = this.x + (dx * speed);
        double nextY = this.y + (dy * speed);
        
        // Create a temporary bounding box (hitbox) at this future position
        Rectangle2D nextHitbox = new Rectangle2D(nextX, nextY, size, size);

        // Check against the maze data; only proceed if this move doesn't intersect a wall
        if (!maze.isWallCollision(nextHitbox)) {
            // No wall hit: explicitly apply the movement updates
            this.x = nextX;
            this.y = nextY;
            
            // Update facing direction ONLY if the player actually moved (dx or dy != 0)
            // This ensures they don't snap back to a default facing direction when stopping.
            if (dx != 0 || dy != 0) {
                facingX = dx;
                facingY = dy;
            }
        }

        // --- STAMINA MANAGEMENT LOGIC ---
        // If the player is actively sprinting (pressing key + moving), isn't penalised (exhausted), 
        // and still has stamina left.
        if (sprinting && exhaustedFrames == 0 && staminaFrames > 0 && (dx != 0 || dy != 0)) {
            // Drain stamina by 1 per frame
            staminaFrames--;
            
            // If we just completely ran out of stamina this frame...
            if (staminaFrames == 0) {
                // Apply the cooldown penalty
                exhaustedFrames = EXHAUSTED_FRAMES;
            }
        } 
        // Otherwise, if not sprinting, and not in the exhaustion cooldown, and stamina isn't full...
        else if (!sprinting && exhaustedFrames == 0 && staminaFrames < MAX_STAMINA_FRAMES) {
            // Gradually recover stamina
            staminaFrames++;
        }
    }

    // ==============================================================
    // RENDERING: Draw the player sprite iteratively with visual states
    // ==============================================================

    /**
     * Renders the player entity to the provided GraphicsContext canvas.
     * Rendering is broken down into layered methods to construct the player visually:
     * 1. The Aura
     * 2. The Body
     * 3. The Face (dynamically changes based on game state)
     */
    @Override
    public void render(GraphicsContext gc) {
        drawAura(gc); // Draw the glow effect around the player first
        drawBody(gc); // Draw the physical circle and cheeks
        
        // Face rendering depends critically on the contextual 'chase' state
        if (isBeingChased) {
            drawTerrifiedFace(gc);
        } else {
            drawCalmFace(gc);
        }
    }

    /** 
     * Draws the soft glow aura behind/around the player.
     * The color conveys status information to the player visually:
     * - Green: Safe (Escape room)
     * - Red: In Danger (Chased)
     * - Blue: Default/Exploring
     */
    private void drawAura(GraphicsContext gc) {
        if (isInEscapeRoom) {
            gc.setFill(Color.rgb(0, 180, 0, 0.15)); // Soft green
        } else if (isBeingChased) {
            gc.setFill(Color.rgb(255, 0, 0, 0.12)); // Soft red
        } else {
            gc.setFill(Color.rgb(200, 200, 255, 0.1)); // Soft subtle blue
        }
        // Offset properties simulate a larger glowing circle around the true hitbox
        gc.fillOval(x - 4, y - 4, size + 8, size + 8);
    }

    /** 
     * Draws the player body circle, which includes outline, shading, and cheeks.
     */
    private void drawBody(GraphicsContext gc) {
        // Outline: Red when in danger, else dark purple.
        gc.setStroke(isBeingChased ? Color.RED : Color.rgb(100, 80, 120));
        gc.setLineWidth(2);
        gc.strokeOval(x, y, size, size);

        // Body fill: 2-layer approach to create a subtle 3D/gradient effect
        gc.setFill(Color.rgb(240, 235, 230));          // Base pale color
        gc.fillOval(x, y, size, size);
        gc.setFill(Color.rgb(255, 250, 245));          // Inner highlight for depth
        gc.fillOval(x + 2, y + 1, size - 4, size - 3);

        // Rosy cheeks: Adds character expressiveness
        gc.setFill(Color.rgb(255, 150, 150, 0.35));    // Semi-transparent pink
        gc.fillOval(x + 1, y + 10, 5, 3);              // Left cheek
        gc.fillOval(x + 14, y + 10, 5, 3);             // Right cheek
    }

    /** 
     * Draws the calm expression for standard exploration.
     * Consists of regular eye whites, irises, pupils, catchlights, and a smiling mouth.
     */
    private void drawCalmFace(GraphicsContext gc) {
        // Eye whites
        gc.setFill(Color.WHITE);
        gc.fillOval(x + 4, y + 4, 5, 7);
        gc.fillOval(x + 11, y + 4, 5, 7);

        // Iris
        gc.setFill(Color.rgb(30, 30, 60));
        gc.fillOval(x + 5, y + 5, 3.5, 5);
        gc.fillOval(x + 12, y + 5, 3.5, 5);

        // Pupil
        gc.setFill(Color.BLACK);
        gc.fillOval(x + 5.5, y + 6, 2, 3);
        gc.fillOval(x + 12.5, y + 6, 2, 3);

        // Catchlight sparkle (reflection of light in the eyes)
        gc.setFill(Color.WHITE);
        gc.fillOval(x + 5.5, y + 5.5, 1.5, 1.5);
        gc.fillOval(x + 12.5, y + 5.5, 1.5, 1.5);

        // Smile: Drawn using a stroked arc.
        gc.setStroke(Color.rgb(120, 80, 80));
        gc.setLineWidth(1);
        gc.strokeArc(x + 6, y + 12, 8, 4, 180, 180, ArcType.OPEN);
    }

    /** 
     * Draws the terrified expression used when monsters are near.
     * Incorporates randomized coordinates for a trembling/jitter effect.
     */
    private void drawTerrifiedFace(GraphicsContext gc) {
        // Generate random jitter (-1 to 1) this frame to make the eyes shake
        double trembleX = (Math.random() * 2) - 1;
        double trembleY = (Math.random() * 2) - 1;

        // Wide eye whites, utilizing tremble offsets
        gc.setFill(Color.WHITE);
        gc.fillOval(x + 3 + trembleX, y + 3 + trembleY, 6, 8);
        gc.fillOval(x + 11 + trembleX, y + 3 + trembleY, 6, 8);

        // Shrunken pinned pupils to indicate terror
        gc.setFill(Color.BLACK);
        gc.fillOval(x + 5 + trembleX, y + 6 + trembleY, 2.5, 2.5);
        gc.fillOval(x + 13 + trembleX, y + 6 + trembleY, 2.5, 2.5);

        // Fear sparkle (slight reflection)
        gc.setFill(Color.rgb(255, 255, 255, 0.8));
        gc.fillOval(x + 5 + trembleX, y + 5.5 + trembleY, 1, 1);
        gc.fillOval(x + 13 + trembleX, y + 5.5 + trembleY, 1, 1);

        // Open mouth (shock): A small dark oval representing a scream/gasp
        gc.setFill(Color.rgb(60, 30, 30));
        gc.fillOval(x + 7.5, y + 13, 5, 4);
    }



    // ==============================================================
    // COLLISION & GETTERS/SETTERS
    // ==============================================================

    /**
     * Gets the bounding box used for collision detection.
     * Required by the Collidable interface.
     * 
     * @return A Rectangle2D representing the player's physical space.
     */
    @Override
    public Rectangle2D getHitbox() {
        return new Rectangle2D(x, y, size, size);
    }

    /** Updates the flag dictating whether the player is running from an enemy. */
    public void setBeingChased(boolean chased) { this.isBeingChased = chased; }

    /** Returns whether the player is currently inside the safety/escape zone. */
    public boolean isInEscapeRoom() { return isInEscapeRoom; }

    /** Updates the flag dictating whether the player is in the safety/escape zone. */
    public void setInEscapeRoom(boolean inEscapeRoom) { this.isInEscapeRoom = inEscapeRoom; }

    /** 
     * Checks if the player is currently in the penalized exhaustion state. 
     * Used by UI (to turn stamina bar red) and game logic. 
     */
    public boolean isExhausted() {
        return exhaustedFrames > 0;
    }

    /** 
     * Returns whether the player currently has the stamina to break into a sprint.
     */
    public boolean canSprint() {
        return staminaFrames > 0 && exhaustedFrames == 0;
    }

    /** 
     * Returns the stamina remaining as a normalized percentage (0.0 to 1.0).
     * Typically used by the HUD renderer to fill the stamina bar.
     */
    public double getStaminaPercent() {
        return (double) staminaFrames / MAX_STAMINA_FRAMES;
    }

    /** Gets the X component of the current facing direction. */
    public double getFacingX() {
        return facingX;
    }

    /** Gets the Y component of the current facing direction. */
    public double getFacingY() {
        return facingY;
    }

    /**
     * Determines current movement speed based on status conditions.
     * 
     * @param sprinting Whether the player is actively trying to sprint.
     * @return The speed the player should travel at this specific moment.
     */
    private double getMovementSpeed(boolean sprinting) {
        // Severe speed penalty when wholly exhausted (0 stamina -> timer ticking)
        if (isExhausted()) {
            return BASE_SPEED * 0.6; 
        }
        // Sprinting if button is pressed and stamina is available
        if (sprinting && canSprint()) {
            return BASE_SPEED * SPRINT_MULTIPLIER;
        }
        // Default regular walking speed
        return BASE_SPEED;
    }
}