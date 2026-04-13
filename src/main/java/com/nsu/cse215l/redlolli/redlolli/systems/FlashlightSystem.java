package com.nsu.cse215l.redlolli.redlolli.systems;

/**
 * Manages the player's primary vision mechanic including battery drain and horror effects.
 */
public class FlashlightSystem {

    private static final int MAX_BATTERY_FRAMES = 180 * 60;

    private boolean on = true;
    private int batteryFrames = MAX_BATTERY_FRAMES;
    private int forcedOutageFrames = 0;
    private int flickerFrames = 0;

    public void toggle() {
        if (batteryFrames > 0) {
            on = !on;
        }
    }

    public void update(double lunaDistanceTiles) {
        if (on && batteryFrames > 0) {
            batteryFrames--;
        }

        if (batteryFrames <= 0) {
            batteryFrames = 0;
            on = false;
        }

        if (lunaDistanceTiles <= 3.0 && forcedOutageFrames == 0 && Math.random() < 0.04) {
            forcedOutageFrames = 30;
        }

        if (lunaDistanceTiles <= 5.0 && lunaDistanceTiles > 3.0 && flickerFrames == 0 && Math.random() < 0.06) {
            flickerFrames = 8;
        }

        if (forcedOutageFrames > 0) {
            forcedOutageFrames--;
        }
        if (flickerFrames > 0) {
            flickerFrames--;
        }
    }

    /** Returns true if the flashlight is actually casting light right now. */
    public boolean isEffectivelyOn() {
        if (!on || batteryFrames <= 0) {
            return false;
        }
        if (forcedOutageFrames > 0) {
            return false;
        }
        if (flickerFrames > 0) {
            return flickerFrames % 2 == 0;
        }
        return true;
    }

    public double getBatteryPercent() {
        return (double) batteryFrames / MAX_BATTERY_FRAMES;
    }

    public boolean isCriticalBattery() {
        return getBatteryPercent() <= 0.15;
    }

    public boolean isOn() {
        return on;
    }

    public void reset() {
        on = true;
        batteryFrames = MAX_BATTERY_FRAMES;
        forcedOutageFrames = 0;
        flickerFrames = 0;
    }
}
