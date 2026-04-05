package com.nsu.cse215l.redlolli.redlolli.ui;

import com.nsu.cse215l.redlolli.redlolli.entities.Entity;
import com.nsu.cse215l.redlolli.redlolli.entities.Item;
import com.nsu.cse215l.redlolli.redlolli.entities.Monster;
import com.nsu.cse215l.redlolli.redlolli.entities.Player;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import com.nsu.cse215l.redlolli.redlolli.systems.FlashlightSystem;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

/**
 * Handles all in-game rendering operations separate from game logic.
 * Responsible for rendering the map, entities, environmental visual effects (like the Lolli reveal 
 * and warning flashes), integrating the HUD, and managing the dynamic lighting/darkness masks.
 * 
 * This class abstracts JavaFX Canvas drawing away from update loops to maintain a clean architecture.
 *
 * Development History:
 * - Phase 1, Week 1, Day 5: Separating rendering pipeline; drawing the maze and translating world coordinates.
 */
public class GameRenderer {

    // ========================= CONSTANTS =========================

    private static final double SCREEN_WIDTH = 880;
    private static final double SCREEN_HEIGHT = 730;
    private static final double HUD_BOTTOM_Y = 50;

    // ========================= MAIN RENDER =========================

    /**
     * The primary entry point for drawing a complete game frame. It receives the GraphicsContext
     * and sequentially builds the current visual state: background -> maze -> entities -> lighting mask 
     * -> specific visual effects -> and finally the UI/HUD bar overlay on top of everything.
     *
     * @param gc                 the JavaFX GraphicsContext to draw instructions upon
     * @param maze               the current level's topological map to draw walls/floors
     * @param entities           polymorphic list of all active entities (Player, Monsters, Items)
     * @param paleLuna           the primary Pale Luna antagonist (passed specifically to draw glowing eyes above geometry)
     * @param player             the player entity tracking camera/flashlight orientation
     * @param warningFlashTimer  frame counter triggering a red vignette effect when spotted
     * @param revealState        managed animation state for the game-winning Lolli item reveal
     * @param level              current integer level index specifying HUD labels
     * @param chests             chest collection data to render status indicators
     * @param itemNames          array linking literal level indexes to specific target item names
     * @param flashlight         the player's active flashlight system to compute blackout masking
     * @param lunaNearby         boolean flag indicating whether to trigger specific heartbeat animations
     * @param blackoutActive     boolean hard flag indicating if the game forced absolute darkness
     * @param fruitCount         (Level 1 item payload stat for HUD)
     * @param eggCount           (Level 2 item payload stat for HUD)
     * @param hasCloneItem       (Level 3 boolean payload stat for HUD decoy availability)
     * @param invisibilityFrames tracking frames to display remaining invisibility duration on UI
     * @param knockoutFrames     tracking frames to display remaining stun state on UI
     * @param pulsePhase         internal UI rendering counter used to manage sine-wave opacity animations (e.g. heartbeat)
     * @return updated pulsePhase incremented value so next frame animation moves forward
     */
    public static double render(GraphicsContext gc, Maze maze, List<Entity> entities,
                                 Monster paleLuna, Player player, int warningFlashTimer,
                                 LolliRevealState revealState, int level,
                                 List<Item> chests,
                                 String[] itemNames,
                                 FlashlightSystem flashlight,
                                 boolean lunaNearby,
                                 boolean blackoutActive,
                                 int fruitCount,
                                 int eggCount,
                                 boolean hasCloneItem,
                                 int invisibilityFrames,
                                 int knockoutFrames,
                                 double pulsePhase) {
        
        // 1) HARD CLEAR: Reset the canvas to pure absolute black for the new frame to prevent ghosting
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // 2) BASE WORLD: Command the Maze to render its tiles (walls, floors, spawn markers) onto the canvas
        maze.renderMaze(gc);
        
        // 3) ACTIVE ENTITIES: Iteratively call each entity's polymorphic render() function so they draw themselves
        for (Entity e : entities) {
            e.render(gc);
        }

        // 4) LUNA'S TERROR EYES: Uniquely, Pale Luna's eyes draw over top of immediate entities to create an eerie staring effect
        if (paleLuna != null) {
            paleLuna.renderEyes(gc);
        }

        // 5) HUNTING VIGNETTE: If the player has been spotted, tint the entire screen slightly red as a warning
        if (warningFlashTimer > 0) {
            gc.setFill(Color.rgb(255, 0, 0, 0.15));
            gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        }

        // 6) DARKNESS MASK: Compute the flashlight visibility and overlay the shadow mask isolating sight
        renderDarknessMask(gc, player, flashlight, blackoutActive);

        // 7) THE MACGUFFIN: If the player found the "Red Lolli", overlay the golden glow reveal animation sequence
        if (revealState != null && revealState.active) {
            renderLolliReveal(gc, revealState);
        }

        // 8) PERMANENT HUD OVERLAY: Draw the informational UI static bar at the top, forwarding state variable
        pulsePhase = HUDRenderer.drawHUD(gc, level, chests, itemNames, paleLuna, player, flashlight, lunaNearby,
            fruitCount, eggCount, hasCloneItem, invisibilityFrames, knockoutFrames, pulsePhase);

        // 9) SAFE ZONE INDICATOR: Draw a subtle green underline spanning the bottom of the HUD when the user touches an escape box
        if (player.isInEscapeRoom()) {
            gc.setFill(Color.rgb(0, 80, 0, 0.25));
            gc.fillRect(0, 50, SCREEN_WIDTH, 4);
        }

        // Return the modified animation phase scalar
        return pulsePhase;
    }

    /**
     * Calculates and renders the darkness system overlay. 
     * This acts as the key tension mechanic—plunging everything below the HUD (Y >= 50) into shadow.
     * By changing the GlobalBlendMode of the Canvas to 'SCREEN', it can dynamically cut out a 
     * transparent triangular "flashlight cone" projecting out of the player.
     *
     * @param gc             the active game canvas
     * @param player         the player object dictating the origin point for the flashlight beam
     * @param flashlight     the specific abstract flashlight logic handling energy toggle states
     * @param blackoutActive overriding flag to force absolute, inescapable blackness 
     */
    private static void renderDarknessMask(GraphicsContext gc, Player player, FlashlightSystem flashlight, boolean blackoutActive) {
        
        // IMMEDIATE OVERRIDE: If the monster gets too close, or game logic demands a blackout,
        // we skip drawing the flashlight entirely and fill the game window with nearly opaque black.
        // We start drawing this at Y = 50 so that the top HUD bar remains perfectly visible to the player.
        if (blackoutActive) {
            gc.setFill(Color.rgb(0, 0, 0, 0.82));
            gc.fillRect(0, 50, SCREEN_WIDTH, SCREEN_HEIGHT - 50);
            return;
        }

        // Establish the physical center point of the flashlight beam logic.
        // Given that the player is 20x20 in scale, +10 puts the 'bulb' perfectly in their center map-wise.
        final double px = player.getX() + 10;
        final double py = player.getY() + 10;

        // DRAW BASE DARKNESS: If the flashlight is ON, the base darkness is semi-transparent allowing
        // players to see vague shapes. If OFF, the darkness becomes immensely thick.
        gc.setFill(Color.rgb(0, 0, 0, flashlight.isEffectivelyOn() ? 0.62 : 0.8));
        gc.fillRect(0, HUD_BOTTOM_Y, SCREEN_WIDTH, SCREEN_HEIGHT - HUD_BOTTOM_Y);

        gc.save(); // Checkpoint our current graphics context matrix
        gc.setGlobalBlendMode(BlendMode.SCREEN); // Activate additive blending which will "subtract darkness" using light color

        // DRAW LIGHT CONE: If the flashlight holds a true state (on & has battery)
        if (flashlight.isEffectivelyOn()) {
            
            // Extract the player's last recorded normalized direction vector (where are they looking)
            double fx = player.getFacingX();
            double fy = player.getFacingY();
            
            // Re-normalize the vector to guarantee a length of 1 for pure directional scaling
            double len = Math.sqrt(fx * fx + fy * fy);
            if (len < 0.0001) { // Failsafe zero-vector fallback: Default to looking directly Up
                fx = 0;
                fy = -1;
                len = 1;
            }
            fx /= len;
            fy /= len;

            // Geometrically construct the Flashlight Triangle Cone
            // tipX/tipY: Project out 235 units forwards along the vector
            double tipX = px + fx * 235;
            double tipY = py + fy * 235;
            
            // sideX/sideY: Calculate perpendicular normal vectors (-y, x) to spread the cone sideways
            double sideX = -fy; 
            double sideY = fx;
            
            // Physical spread radius scaling value
            double spread = 90;

            // DRAW LIGHT BEAM: Fill the cut-out shape with a bright, transparent color.
            // Due to BlendMode.SCREEN, this lightens the dark mask drawn earlier, rendering the maze visible underneath
            gc.setFill(Color.rgb(220, 220, 220, 0.24));
            gc.fillPolygon(
                    new double[]{px, tipX + sideX * spread, tipX - sideX * spread}, // X points (origin, front-left, front-right)
                    new double[]{py, tipY + sideY * spread, tipY - sideY * spread}, // Y points (origin, front-left, front-right)
                    3 // Vertex count declaring a triangle
            );
        }

        // Pop back to the normal drawing matrix, terminating BlendMode.SCREEN logic
        gc.restore();
    }

    // ========================= LOLLI REVEAL EFFECT =========================

    /**
     * Renders the cinematic golden glow and "Red Lolli" reveal animation when the player 
     * successfully discovers the principal item of the game. It overrides the screen with a 
     * shining, pulsing light animation that fades the rest of the game into dim shadow.
     *
     * @param gc    the current GraphicsContext
     * @param state the specialized data tracker providing the x/y of the item and its decreasing timer
     */
    private static void renderLolliReveal(GraphicsContext gc, LolliRevealState state) {
        // Calculate the central blooming point by nudging +8 pixels into the center of the chest tile
        double cx = state.x + 8;
        double cy = state.y + 8;
        
        // Progress interpolates from 0.0 to 1.0 as the animation ticks down from max duration
        double progress = 1.0 - (double) state.timer / state.duration;

        // Animate an outward expanding pair of gold rings, tied to progress
        double glowRadius = 20 + progress * 40;
        
        // Compute an inner pulsation effect by weaving a sine-wave function so the glow slightly throbs
        double pulse = Math.sin(state.phase) * 0.3 + 0.7;

        // Draw the massive outer ring
        gc.setFill(Color.rgb(255, 215, 0, 0.08 * pulse));
        gc.fillOval(cx - glowRadius, cy - glowRadius, glowRadius * 2, glowRadius * 2);
        
        // Draw the inner, brighter, slightly smaller ring
        gc.setFill(Color.rgb(255, 180, 0, 0.15 * pulse));
        gc.fillOval(cx - glowRadius * 0.6, cy - glowRadius * 0.6, glowRadius * 1.2, glowRadius * 1.2);

        // Core bright nuclear glow immediately behind the Lolli model
        gc.setFill(Color.rgb(255, 230, 100, 0.3 * pulse));
        gc.fillOval(cx - 12, cy - 12, 24, 24);

        // Expand the actual Red Lolli payload scale as progress moves mathematically toward 1.0
        double lolliSize = 8 + progress * 4;
        drawRedLolli(gc, cx, cy, lolliSize); // Helper function specifically drawing the lollipop vector shape

        // Slide the text transparency smoothly mathematically, capped at 1.0 (fully opaque)
        double textAlpha = Math.min(1.0, progress * 2);
        gc.setFill(Color.rgb(255, 50, 50, textAlpha));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        // Display the victory text statically above the glowing rings
        gc.fillText("RED LOLLI FOUND!", cx - 70, cy - glowRadius - 10);

        // Progressively dim out ALL of the surrounding screen elements by overlaying black, drawing the player's eye
        gc.setFill(Color.rgb(0, 0, 0, 0.3 * progress));
        gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // REDRAW STEP: Paint the glowing effect out over the dimming mask again so it visibly cuts entirely through the shadows
        gc.setFill(Color.rgb(255, 215, 0, 0.12 * pulse));
        gc.fillOval(cx - glowRadius * 0.7, cy - glowRadius * 0.7, glowRadius * 1.4, glowRadius * 1.4);
        drawRedLolli(gc, cx, cy, lolliSize); 
        gc.setFill(Color.rgb(255, 50, 50, textAlpha));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        gc.fillText("RED LOLLI FOUND!", cx - 70, cy - glowRadius - 10);
    }

    /** Draws a Red Lolli candy at the given center position. */
    private static void drawRedLolli(GraphicsContext gc, double cx, double cy, double size) {
        // Candy head
        gc.setFill(Color.rgb(220, 20, 20));
        gc.fillOval(cx - size / 2, cy - size / 2 - 2, size, size);
        // Shine
        gc.setFill(Color.rgb(255, 100, 100, 0.6));
        gc.fillOval(cx - size / 4, cy - size / 3 - 2, size / 3, size / 3);
        // Stick
        gc.setStroke(Color.rgb(200, 170, 120));
        gc.setLineWidth(2);
        gc.strokeLine(cx, cy + size / 2 - 2, cx, cy + size / 2 + 8);
    }

    // ========================= LOLLI REVEAL STATE =========================

    /**
     * Holds the animation state for the Red Lolli reveal effect.
     * Created when a lolli is found, consumed when the animation ends.
     */
    public static class LolliRevealState {
        public boolean active;
        public int timer;
        public int duration;
        public double x, y;
        public double phase;

        public LolliRevealState(double x, double y, int duration) {
            this.active = true;
            this.timer = duration;
            this.duration = duration;
            this.x = x;
            this.y = y;
            this.phase = 0;
        }
    }
}
