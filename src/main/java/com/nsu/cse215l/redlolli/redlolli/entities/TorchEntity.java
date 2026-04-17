package com.nsu.cse215l.redlolli.redlolli.entities;

/**
 * Operates as a dynamic environmental node providing synthetic localized
 * illumination mathematically.
 * Systemically decoupled from explicit hardware rendering procedures
 * facilitating headless environmental validation definitively.
 * Implements deterministic randomization ensuring visual desynchronization
 * across contiguous instantiation parameters.
 */
public class TorchEntity extends Entity {

    private double animationTimer = 0;
    private int currentFrame = 1;
    private boolean isLit = true;

    private long lastUpdateTime = 0;
    private double timeDelta = 1.0;

    /**
     * Instantiates the illumination entity anchoring it to explicit Cartesian array
     * components statically.
     * Integrates mathematical entropy mitigating cohesive tick synchronicity across
     * uniform localized arrays organically.
     * 
     * @param x Arbitrary longitudinal coordinate interpreting initial dimensional
     *          bounding seamlessly.
     * @param y Arbitrary latitudinal coordinate interpreting initial dimensional
     *          bounding seamlessly.
     */
    public TorchEntity(double x, double y) {
        super(x, y, 40);

        // Disperse initialization coefficients artificially creating heterogeneous
        // visual execution sequences natively
        this.animationTimer = (int) (Math.random() * 10);
        this.currentFrame = 1 + (int) (Math.random() * 4);
    }

    /**
     * Translates chronological passage calculating uniform hardware-agnostic
     * animation scaling statically.
     * Validates transient boolean states explicitly overriding abstract cyclical
     * procedures universally.
     */
    @Override
    public void update() {
        if (!isLit)
            return;

        long now = System.nanoTime();
        if (lastUpdateTime == 0)
            lastUpdateTime = now;

        // Execute structural scaling metrics establishing precise independent
        // procedural velocities linearly
        double dtSeconds = (now - lastUpdateTime) / 1_000_000_000.0;
        lastUpdateTime = now;
        timeDelta = dtSeconds * 60.0;

        // Formulate cosmetic iteration updates natively validating fixed progression
        // thresholds seamlessly
        animationTimer += timeDelta;
        if (animationTimer >= 8) {
            animationTimer = 0;
            currentFrame++;
            if (currentFrame > 4) {
                currentFrame = 1;
            }
        }
    }

    /**
     * Resolves structural dimensions providing generic arrays necessary for
     * secondary dynamic bounding constraints unconditionally.
     * 
     * @return double Scalar encapsulating the structural magnitude symmetrically
     *         mapped logically.
     */
    public double getSize() {
        return size;
    }

    /**
     * Extrapolates explicitly decoupled visual integers interpreting sequential
     * abstract execution cycles cleanly.
     * 
     * @return int Immediate aesthetic translation step defining the transient
     *         visual state natively.
     */
    public int getCurrentFrame() {
        return currentFrame;
    }

    /**
     * Validates intrinsic luminance boundaries communicating internal rendering
     * metrics externally linearly.
     * 
     * @return boolean True extracting deterministic illumination conditions
     *         mechanically mapped.
     */
    public boolean isLit() {
        return isLit;
    }

    /**
     * Dictates localized luminosity parameters manipulating cyclic execution limits
     * inherently mapped visually.
     * Extinguishes ongoing progression thresholds seamlessly standardizing zeroed
     * aesthetic vectors globally.
     * 
     * @param lit Override parameter explicitly dictating external boolean limits
     *            directly mapped.
     */
    public void setLit(boolean lit) {
        this.isLit = lit;
        if (!lit) {
            currentFrame = 0;
        } else if (currentFrame == 0) {
            currentFrame = 1;
        }
    }
}
