package com.nsu.cse215l.redlolli.redlolli.ui;

import com.nsu.cse215l.redlolli.redlolli.entities.Item;
import com.nsu.cse215l.redlolli.redlolli.entities.Monster;
import com.nsu.cse215l.redlolli.redlolli.entities.Player;
import com.nsu.cse215l.redlolli.redlolli.systems.FlashlightSystem;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

/**
 * Strictly manages the in-game Heads-Up Display (HUD) visual layer at the absolute top of the screen.
 * Isolates UI rendering from the core game engine structure, drawing text, status bars (Battery/Stamina), 
 * item inventories, dynamic timers (Pale Luna tracking), and safe room states.
 * 
 * Development History:
 * - Phase 3, Week 3, Day 15: Overlaying text/shapes on the camera UI layer.
 */
public class HUDRenderer {

    // ========================= CONSTANTS =========================

    private static final double HUD_HEIGHT = 50;
    private static final double HUD_WIDTH = 880;
    private static final double MID_Y = 31;

    // ========================= MAIN DRAW METHOD =========================

    /**
     * Draws the full HUD bar at the top of the game screen.
     *
     * @param gc         the graphics context to draw on
     * @param level      current game level (1-3)
     * @param chests     list of all chests in the level
     * @param itemNames  array of item names per level
     * @param paleLuna   the Pale Luna monster (may be null)
     * @param player     the player entity
     * @param pulsePhase current animation pulse phase (for timer/chase effects)
     * @return updated pulse phase value to be stored back by the caller
     */
    /**
     * Executes the sequenced render pipeline specifically for the persistent UI status bar.
     * This orchestrates multiple sub-section UI drawings separated by visual dividers.
     *
     * @param gc                 the fixed hardware GraphicsContext overlaying the frame
     * @param level              the current integer progression of the campaign (determines item logic)
     * @param chests             the tracked collection of interactables, queried to find Lolli
     * @param itemNames          a localized array holding string target details ("e.g. Find Meat")
     * @param paleLuna           target entity reference evaluated to extract her AI action phase
     * @param player             target player entity reference evaluated to check escape states
     * @param flashlight         system reference queried for live battery drain and active threshold
     * @param lunaNearby         flag to trigger erratic heartbeat animations in the UI
     * @param fruitCount         (Level 1 item counter UI value)
     * @param eggCount           (Level 2 item counter UI value)
     * @param hasCloneItem       (Level 3 specific Boolean flag for UI drawing the Clone availability)
     * @param invisibilityFrames duration left on the invisibility UI status
     * @param knockoutFrames     duration left on the stun UI status
     * @param pulsePhase         persistent continuous float feeding mathematical formulas for fading and flashing animations
     * @return an evaluated updating parameter extending the pulse sequence mathematically
     */
    public static double drawHUD(GraphicsContext gc, int level, List<Item> chests,
                                  String[] itemNames, Monster paleLuna, Player player,
                                  FlashlightSystem flashlight,
                                  boolean lunaNearby,
                                  int fruitCount,
                                  int eggCount,
                                  boolean hasCloneItem,
                                  int invisibilityFrames,
                                  int knockoutFrames,
                                  double pulsePhase) {
        
        // 1) Initialize the master panel
        drawBackground(gc);
        
        // 2) Write out the structural level fraction "1/3", "2/3"
        drawLevelIndicator(gc, level);
        
        // Render hard line UI sections segregating data
        drawDivider(gc, 90);
        
        // 3) Evaluate and render whether the primary game objective has been visually captured
        drawLolliStatus(gc, chests);
        drawDivider(gc, 145);
        
        // 4) Detail dynamically what objective they must finish
        drawFindItem(gc, level, itemNames);
        drawDivider(gc, 320);
        
        // 5) Render dynamic battery tracking visually and numerically 
        drawBattery(gc, flashlight, pulsePhase);
        drawDivider(gc, 460);
        
        // 6) Calculate Luna's complex 4-state indicator with unique flashing behaviors per state
        pulsePhase = drawPaleLunaStatus(gc, paleLuna, pulsePhase);
        
        drawDivider(gc, 785);
        
        // 7) Paint absolute guarantees (like standing inside a green Safe Room)
        drawSafeIndicator(gc, player);
        drawHeartbeat(gc, lunaNearby, pulsePhase); // UI Heart-monitor lines
        
        // 8) Finally, render specific numeric payloads indicating their exact item count bounds
        drawUtilityStatus(gc, level, fruitCount, eggCount, hasCloneItem, invisibilityFrames, knockoutFrames);

        // Feedback the manipulated integer scaling up through the main game loop
        return pulsePhase;
    }

    // ========================= HUD SECTIONS =========================

    /** Draws the persistent heavy dark baseline separating the actual HUD from the rest of the canvas */
    private static void drawBackground(GraphicsContext gc) {
        // Construct the thick header window element
        gc.setFill(Color.rgb(12, 12, 16));
        gc.fillRect(0, 0, HUD_WIDTH, HUD_HEIGHT);
        // Paint a sharp dividing line on the floor of the HUD using aggressive red highlighting
        gc.setStroke(Color.DARKRED);
        gc.setLineWidth(2);
        gc.strokeLine(0, HUD_HEIGHT, HUD_WIDTH, HUD_HEIGHT);
    }

    /** 
     * Prints out the current Level numerator versus denominator on the far left.
     */
    private static void drawLevelIndicator(GraphicsContext gc, int level) {
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        gc.setFill(Color.rgb(180, 180, 200));
        // Using static Y coordinate parameter offset to horizontally center it
        gc.fillText("LEVEL " + level + "/3", 12, MID_Y);
    }

    /** 
     * Renders a small dividing aesthetic line at the required x location. 
     */
    private static void drawDivider(GraphicsContext gc, double x) {
        // Very dim red
        gc.setStroke(Color.rgb(50, 20, 20));
        gc.setLineWidth(1);
        gc.strokeLine(x, 8, x, 42); // Bound closely inside the 50px HUD height
    }

    /** 
     * Examines the list of actively populated entity boxes to decide if the Red Lolli objective
     * has been toggled to TRUE (isCollected() && hasLolli()).
     */
    private static void drawLolliStatus(GraphicsContext gc, List<Item> chests) {
        // Stream search for chest collision match that mathematically fulfills the primary goal condition
        boolean foundLolli = chests.stream().anyMatch(c -> c.isCollected() && c.hasLolli());

        // Construct tiny Lolli custom shape using standard primitive circle/stick drawing
        gc.setFill(Color.DARKRED);
        gc.fillOval(112, MID_Y - 9, 8, 8); // Head
        gc.setStroke(Color.rgb(180, 150, 100)); // Wood Stick Color
        gc.setLineWidth(1.5);
        gc.strokeLine(116, MID_Y - 1, 116, MID_Y + 5);

        // Dynamically shift value to brightly illuminate once properly achieved
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        gc.setFill(foundLolli ? Color.LIMEGREEN : Color.rgb(200, 200, 200));
        gc.fillText((foundLolli ? "1" : "0") + "/1", 124, MID_Y);
    }

    /** Retrieves index specific strings outlining targeted items (like "EGG" or "FRUIT") */
    private static void drawFindItem(GraphicsContext gc, int level, String[] itemNames) {
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        gc.setFill(Color.GOLD);
        gc.fillText("FIND: " + itemNames[level - 1], 172, MID_Y);
    }

    /** 
     * Detailed drawing element calculating internal percentage logic into physical bar pixel spans and percentages
     */
    private static void drawBattery(GraphicsContext gc, FlashlightSystem flashlight, double pulsePhase) {
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        
        // Evaluate logic alert function inside system—if heavily dying, start modulating its drawn color 
        // to rapidly strobe intensely red using phase modulation formula
        Color labelColor = flashlight.isCriticalBattery()
                ? Color.rgb(230, (int) (50 + 80 * (Math.sin(pulsePhase * 2) * 0.5 + 0.5)), 50)
                : Color.rgb(220, 220, 220);
        gc.setFill(labelColor);
        gc.fillText("BATTERY", 412, 18);

        // Container shell for the drawing 
        gc.setFill(Color.rgb(30, 30, 40));
        gc.fillRect(412, 22, 96, 10);
        gc.setStroke(Color.rgb(90, 90, 110));
        gc.setLineWidth(1);
        gc.strokeRect(412, 22, 96, 10);

        // Clamping computation determining length rendering out of total width
        double fill = Math.max(0.0, Math.min(1.0, flashlight.getBatteryPercent()));
        
        // Define color states: Standard (Blue) -> Warn (Orange) -> Fatal (Red) based on float thresholds
        Color barColor = fill >= 0.35 ? Color.rgb(90, 200, 255) : Color.ORANGE;
        if (fill < 0.15) {
            barColor = Color.RED;
        }
        
        // Cast pixel dimension based rigidly on fill percentage out of the 94 max active rendering pixels
        gc.setFill(barColor);
        gc.fillRect(413, 23, 94 * fill, 8);
        
        // Accompany graphics context bar with hard ON/OFF label + numerical percent format
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        gc.setFill(Color.rgb(210, 210, 210));
        gc.fillText((flashlight.isOn() ? "ON " : "OFF ") + (int) (fill * 100) + "%", 437, 42);
    }


    /** 
     * Orchestrates resolving which specific warning bar style should be rendered to the user
     * depending exclusively on Pale Luna's internally managed 4-state cycle system. 
     * Returns an updated pulse phase string to keep timing smooth across state changes.
     */
    private static double drawPaleLunaStatus(GraphicsContext gc, Monster paleLuna, double pulsePhase) {
        // Prevent crashes if entity hasn't booted yet 
        if (paleLuna == null) return pulsePhase;

        Monster.State lunaState = paleLuna.getState();
        // Fixed positional constraints corresponding to the top right side of the HUD
        double barX = 652, barW = 126;
        double barY = MID_Y - 18, barH = 12;

        // Switch to direct sub-functions managing unique visuals tied to AI logic
        switch (lunaState) {
            case DORMANT -> pulsePhase = drawDormantStatus(gc, paleLuna, barX, barY, barW, barH, pulsePhase);
            case STALKING -> pulsePhase = drawStalkingStatus(gc, paleLuna, barX, barY, barW, barH, pulsePhase);
            case HUNTING -> pulsePhase = drawHuntingStatus(gc, paleLuna, barX, barY, barW, barH, pulsePhase);
            case WAITING_AT_DOOR -> drawWaitingStatus(gc, paleLuna, barX);
        }

        return pulsePhase;
    }

    /** 
     * Draws the sleeping countdown timer showing how long before the monster activates.
     * Contains color-coded urgency transitioning slowly from green to aggressive red.
     */
    private static double drawDormantStatus(GraphicsContext gc, Monster paleLuna,
                                          double barX, double barY, double barW, double barH,
                                          double pulsePhase) {
        // Frame logic mapped to rough seconds
        int secondsLeft = paleLuna.getDormantTimer() / 60;
        Color timerColor;

        // Calculate severe color scaling linked to time pressure
        if (secondsLeft > 10) {
            timerColor = Color.LIMEGREEN;   // Relaxed state
        } else if (secondsLeft > 6) {
            timerColor = Color.YELLOW;      // Warning
        } else if (secondsLeft > 3) {
            timerColor = Color.ORANGE;      // Approaching danger
        } else {
            // Immediate danger: Starts to throb mathematically using sine limits
            double flash = Math.sin(pulsePhase) * 0.5 + 0.5;
            timerColor = Color.rgb(255, (int) (50 * flash), (int) (50 * flash));
        }
        
        // Progress the general sine timer tick steadily
        pulsePhase += 0.2;

        // Draw structural frame
        gc.setFill(Color.rgb(40, 40, 45));
        gc.fillRect(barX, barY, barW, barH);
        gc.setStroke(Color.rgb(80, 80, 90));
        gc.setLineWidth(1);
        gc.strokeRect(barX, barY, barW, barH);
        
        // Compute completion width scalar depending on total possible 900 ticks (15s)
        double fillRatio = (double) paleLuna.getDormantTimer() / 900.0;
        gc.setFill(timerColor);
        gc.fillRect(barX + 1, barY + 1, (barW - 2) * fillRatio, barH - 2);

        // String readout for explicit UI understanding
        gc.setFill(timerColor);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        gc.fillText("She sleeps " + secondsLeft + "s", barX, MID_Y + 6);

        return pulsePhase;
    }

    /** 
     * Renders UI changes during the "Stalking" stage where she has begun to follow via BFS
     * but hasn't drastically sped up yet. Features medium-throb warnings.
     */
    private static double drawStalkingStatus(GraphicsContext gc, Monster paleLuna,
                                             double barX, double barY, double barW, double barH,
                                             double pulsePhase) {
        // Throb faster (1.7 multiplier vs 1.0 logic elsewhere)
        double flash = Math.sin(pulsePhase * 1.7) * 0.5 + 0.5;
        // Step phase faster
        pulsePhase += 0.24;

        // Animate a reddish backdrop using that scalar
        gc.setFill(Color.rgb(120, 20, 20, 0.25 + flash * 0.25));
        gc.fillRect(barX, barY, barW, barH);
        gc.setStroke(Color.rgb(140, 40, 40));
        gc.strokeRect(barX, barY, barW, barH);
        
        // Width bar scalar mapped against total 480 Stalking tick limit
        double fill = (double) paleLuna.getStalkTimer() / 480.0;
        gc.setFill(Color.rgb(210, 80, 80));
        gc.fillRect(barX + 1, barY + 1, (barW - 2) * fill, barH - 2);

        // More menacing label text
        gc.setFill(Color.rgb(230, 120, 120));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        gc.fillText("She watches", barX, MID_Y + 6);

        return pulsePhase;
    }

    /** 
     * Draws the ultra-aggressive red hunt timer indicating high speed pursuit AI logic.
     * Contains the fastest pulsing sequence (2.0) and rigid warning readouts ("RUN").
     */
    private static double drawHuntingStatus(GraphicsContext gc, Monster paleLuna,
                                             double barX, double barY, double barW, double barH,
                                             double pulsePhase) {
        // Highly aggressive multiplier
        double chaseFlash = Math.sin(pulsePhase * 2) * 0.5 + 0.5;
        // Fast tick addition
        pulsePhase += 0.3;

        // Very thick, stark red graphics container
        gc.setFill(Color.rgb(255, (int) (30 * chaseFlash), (int) (30 * chaseFlash)));
        gc.fillRect(barX, barY, barW, barH);
        gc.setStroke(Color.rgb(120, 0, 0));
        gc.setLineWidth(1);
        gc.strokeRect(barX, barY, barW, barH);
        
        // Fill drains downwards against the 360 parameter defining Hunt max length
        double chaseFill = (double) paleLuna.getHuntTimer() / 360.0;
        gc.setFill(Color.rgb(200, 0, 0));
        gc.fillRect(barX + 1, barY + 1, (barW - 2) * chaseFill, barH - 2);

        int chaseSecsLeft = paleLuna.getHuntTimer() / 60;
        gc.setFill(Color.RED);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        gc.fillText("RUN " + chaseSecsLeft + "s", barX, MID_Y + 6);

        return pulsePhase;
    }

    /** 
     * Static, un-animated indicator state used to denote that Luna has finished hunting
     * and is now temporarily paused resting/vanishing—serving as a breather block for players.
     */
    private static void drawWaitingStatus(GraphicsContext gc, Monster paleLuna, double barX) {
        gc.setFill(Color.ORANGE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        int waitSecs = paleLuna.getWaitTimer() / 60;
        gc.fillText("At the door " + waitSecs + "s", barX, MID_Y);
    }

    /** Draws the prominent green [SAFE] text overlay exactly when the collision system flags an escape box inside. */
    private static void drawSafeIndicator(GraphicsContext gc, Player player) {
        if (player.isInEscapeRoom()) {
            gc.setFill(Color.LIMEGREEN);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            gc.fillText("SAFE", 802, MID_Y);
        }
    }

    /**
     * Physically renders an aggressive polyline resembling a spiked EKG line reading.
     * Speed and amplitude of the "heartbeat" drastically multiply when the lunaNearby tracker flips true.
     */
    private static void drawHeartbeat(GraphicsContext gc, boolean lunaNearby, double pulsePhase) {
        double baseY = 12;
        double startX = 802;
        
        // Draws internal standard flatline tail
        gc.setStroke(Color.rgb(35, 150, 35));
        gc.setLineWidth(1.2);
        gc.strokeLine(startX, baseY + 16, startX + 66, baseY + 16);

        // Dynamically compute wave spikes (amplitude vs speed)
        double speed = lunaNearby ? 1.9 : 0.8;
        double wave = Math.sin(pulsePhase * speed) * (lunaNearby ? 8 : 3);
        
        // Transition line completely from soothing green to violent blood red 
        gc.setStroke(lunaNearby ? Color.rgb(220, 40, 40) : Color.rgb(70, 220, 70));
        gc.setLineWidth(1.8);
        
        // Instruct JavaFX to trace multiple floating points creating jagged up/down heartbeat patterns
        gc.strokePolyline(
                new double[]{startX + 4, startX + 16, startX + 24,           startX + 32,             startX + 40, startX + 58},
                new double[]{baseY + 16, baseY + 16, baseY + 16 - wave, baseY + 16 + wave * 0.3, baseY + 16, baseY + 16},
                6
        );
    }

    /** 
     * Prints specific side-counters or status flags (invisibility, stuns, specialized level limits)
     * depending heavily upon the passed game context integers linking straight to active system flags.
     */
    private static void drawUtilityStatus(GraphicsContext gc, int level, int fruitCount, int eggCount,
                                          boolean hasCloneItem, int invisibilityFrames, int knockoutFrames) {
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        gc.setFill(Color.rgb(200, 190, 170));
        
        // Sub-text describing secondary targets mapped explicitly to level constraints
        if (level == 1) {
            gc.fillText("Fruit: " + fruitCount + " (E)", 172, 44);
        } else if (level == 2) {
            gc.fillText("Eggs: " + eggCount + " (E)", 172, 44);
        } else if (level == 3 && hasCloneItem) {
            gc.fillText("Clone Ready (C)", 172, 44);
        }

        // Draw temporary buff/debuff timers using explicit screen positioning
        if (invisibilityFrames > 0) {
            // Draws Light blue indicator for cloaking
            gc.setFill(Color.rgb(120, 200, 255));
            gc.fillText("INVISIBLE " + (invisibilityFrames / 60) + "s", 690, 44);
        } else if (knockoutFrames > 0) {
            // Draw stark red warnings stating player control has snapped from an aggressive attack
            gc.setFill(Color.rgb(230, 80, 80));
            gc.fillText("KNOCKED OUT " + (knockoutFrames / 60) + "s", 672, 44);
        }
    }
}
