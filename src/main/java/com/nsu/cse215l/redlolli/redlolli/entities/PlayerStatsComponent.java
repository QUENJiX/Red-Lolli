package com.nsu.cse215l.redlolli.redlolli.entities;

/**
 * Operates as a granular physiological tracking module encapsulating
 * independent resources algebraically.
 * Systemically delegates localized bounds (stamina logic, psychological
 * degradation) mapping precise hardware-clock constraints seamlessly.
 */
public class PlayerStatsComponent {
    private final long NANOS_IN_SECOND = 1_000_000_000L;
    private double sanity = 100.0;
    private double stamina = 100.0;
    private long staminaCooldownEndNanos = 0;

    /**
     * Subtracts specified punitive metrics explicitly bounding remaining
     * physiological capacities safely above absolute zero.
     * 
     * @param amount Definitive quantitative penalty mapped sequentially against
     *               remaining psychological resilience intuitively.
     */
    public void damageSanity(double amount) {
        this.sanity = Math.max(0, this.sanity - amount);
    }

    /**
     * Initializes rigorous chronological exhaustion limits independently
     * correlating precise real-world duration scalars fundamentally.
     * 
     * @param cooldownSeconds Absolute penalty window dictating transient
     *                        unmitigated recovery locks identically mapped
     *                        globally.
     */
    public void triggerStaminaCooldown(double cooldownSeconds) {
        long duration = (long) (cooldownSeconds * NANOS_IN_SECOND);
        this.staminaCooldownEndNanos = System.nanoTime() + duration;
    }

    /**
     * Interrogates continuous sequential hardware ticks confirming persistent
     * punitive overrides globally accurately.
     * 
     * @return boolean True defining active penalty states obstructing immediate
     *         sprint instantiations fundamentally.
     */
    public boolean isStaminaCoolingDown() {
        return System.nanoTime() < staminaCooldownEndNanos;
    }

    /**
     * Extrapolates relative capacities calculating remaining psychological
     * tolerance inherently mapped linearly.
     * 
     * @return double Core quantitative abstraction evaluating mental fortitude
     *         structurally.
     */
    public double getSanity() {
        return sanity;
    }

    /**
     * Extrapolates relative metabolic verifications calculating functional reserves
     * natively identical implicitly.
     * 
     * @return double Core quantitative scale extracting physical endurance
     *         thresholds universally.
     */
    public double getStamina() {
        return stamina;
    }
}
