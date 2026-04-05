package com.nsu.cse215l.redlolli.redlolli.systems;

/**
 * The FlashlightSystem manages the player's primary vision mechanic.
 * 
 * It handles the mathematical tracking of battery life (drain over time),
 * manual toggle states, and atmospheric horror effects such as flickering 
 * or forced outages when the monster comes within a specific proximity limit.
 * 
 * Development History:
 * - Phase 2, Week 2, Day 12: Flashlight cone, toggle, and base battery drain logic implemented.
 * - Phase 3, Week 3, Day 19: Balanced glitch rates and total battery capacity limits.
 */
public class FlashlightSystem {

    // ==============================================================
    // CONFIG & TIMERS
    // ==============================================================

    /** The maximum capacity of the battery. 180 seconds assuming 60 frames per second. */
    private static final int MAX_BATTERY_FRAMES = 180 * 60;

    /** Manual state: whether the user has the flashlight switched on or off. */
    private boolean on = true;
    
    /** Current remaining battery capacity. */
    private int batteryFrames = MAX_BATTERY_FRAMES;
    
    /** How many frames remain of a 'forced outage' (complete temporary blackout from fear/proximity). */
    private int forcedOutageFrames = 0;
    
    /** How many frames remain of a 'flicker' effect (rapid toggling from proximity warning). */
    private int flickerFrames = 0;

    // ==============================================================
    // INPUT ACTIONS
    // ==============================================================

    /** Allows the player to manually click the flashlight on and off. */
    public void toggle() {
        if (batteryFrames > 0) {
            on = !on;
        }
    }

    // ==============================================================
    // LOGIC TICK
    // ==============================================================

    /**
     * Executes every frame to drain battery and evaluate horror effects.
     * 
     * @param lunaDistanceTiles How far the monster is from the player in grid units.
     */
    public void update(double lunaDistanceTiles) {
        
        // 1. Core Battery Drain
        if (on && batteryFrames > 0) {
            batteryFrames--;
        }
        
        // 2. Battery Depletion Override
        if (batteryFrames <= 0) {
            batteryFrames = 0;
            on = false; // Force it off permanently
        }

        // 3. Proximity Horror Effects
        // If the monster is EXTREMELY close (<= 3 tiles), 4% chance per frame to completely short out
        if (lunaDistanceTiles <= 3.0 && forcedOutageFrames == 0 && Math.random() < 0.04) {
            forcedOutageFrames = 30; // 0.5 seconds of total darkness
        }
        
        // If the monster is REASONABLY close (3-5 tiles), 6% chance per frame to cause a glitchy flicker
        if (lunaDistanceTiles <= 5.0 && lunaDistanceTiles > 3.0 && flickerFrames == 0 && Math.random() < 0.06) {
            flickerFrames = 8; // ~0.13 seconds of rapid strobing
        }

        // 4. Effect Cooldown Management
        if (forcedOutageFrames > 0) {
            forcedOutageFrames--;
        }
        if (flickerFrames > 0) {
            flickerFrames--;
        }
    }

    // ==============================================================
    // STATUS QUERIES (Used by Renderer usually)
    // ==============================================================

    /**
     * Calculates if the flashlight is ACTUALLY casting light right now.
     * Takes into account manual toggles, dead batteries, and proximity horror glitches.
     * 
     * @return true if light should be drawn to the screen.
     */
    public boolean isEffectivelyOn() {
        // Automatically off if manually turned off or battery is dead
        if (!on || batteryFrames <= 0) {
            return false;
        }
        // Force darkness if the short-circuit proximity effect is active
        if (forcedOutageFrames > 0) {
            return false;
        }
        // If flickering is active, blink rapidly based on even/odd frames
        if (flickerFrames > 0) {
            return flickerFrames % 2 == 0;
        }
        // Otherwise, it's on and stable
        return true;
    }

    /** @return Percentage of battery remaining, typically for HUD bars. */
    public double getBatteryPercent() {
        return (double) batteryFrames / MAX_BATTERY_FRAMES;
    }

    /** @return True if the battery is dangerously low, optionally triggering HUD warnings. */
    public boolean isCriticalBattery() {
        return getBatteryPercent() <= 0.15;
    }

    public boolean isOn() {
        return on;
    }

    /** Completely tops off and resets the flashlight, usually on level respawns. */
    public void reset() {
        on = true;
        batteryFrames = MAX_BATTERY_FRAMES;
        forcedOutageFrames = 0;
        flickerFrames = 0;
    }
}
