package com.nsu.cse215l.redlolli.redlolli.entities;

public class PlayerStatsComponent {
    private final long NANOS_IN_SECOND = 1_000_000_000L;
    private double sanity = 100.0;
    private double stamina = 100.0;
    private long staminaCooldownEndNanos = 0;
    
    public void damageSanity(double amount) {
        this.sanity = Math.max(0, this.sanity - amount);
    }

    public void triggerStaminaCooldown(double cooldownSeconds) {
        long duration = (long) (cooldownSeconds * NANOS_IN_SECOND);
        this.staminaCooldownEndNanos = System.nanoTime() + duration;
    }

    public boolean isStaminaCoolingDown() {
        return System.nanoTime() < staminaCooldownEndNanos;
    }
    
    public double getSanity() { return sanity; }
    public double getStamina() { return stamina; }
}