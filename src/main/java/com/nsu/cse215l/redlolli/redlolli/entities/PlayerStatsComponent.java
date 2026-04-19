package com.nsu.cse215l.redlolli.redlolli.entities;

/**
 * A simple helper class that keeps track of the player's health (sanity) 
 * and energy (stamina). It handles cooldowns and damage quietly in the background.
 */
public class PlayerStatsComponent {
    private final long NANOS_IN_SECOND = 1_000_000_000L;
    private double sanity = 100.0;
    private double stamina = 100.0;
    private long staminaCooldownEndNanos = 0;

    /**
     * Reduces the player's sanity, making sure it never drops below zero.
     * 
     * @param amount The amount of sanity to lose.
     */
    public void damageSanity(double amount) {
        this.sanity = Math.max(0, this.sanity - amount);
    }

    /**
     * Forces the player to wait a certain number of seconds before they can sprint again.
     * 
     * @param cooldownSeconds How long they have to wait before recovering.
     */
    public void triggerStaminaCooldown(double cooldownSeconds) {
        long duration = (long) (cooldownSeconds * NANOS_IN_SECOND);
        this.staminaCooldownEndNanos = System.nanoTime() + duration;
    }

    /**
     * Checks if the player is currently too tired to sprint.
     * 
     * @return True if stamina is still on cooldown.
     */
    public boolean isStaminaCoolingDown() {
        return System.nanoTime() < staminaCooldownEndNanos;
    }

    /**
     * Gets the player's current sanity points.
     * 
     * @return The current sanity.
     */
    public double getSanity() {
        return sanity;
    }

    /**
     * Gets the player's current stamina points.
     * 
     * @return The current stamina.
     */
    public double getStamina() {
        return stamina;
    }
}
