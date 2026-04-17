package com.nsu.cse215l.redlolli.redlolli.core;

/**
 * Operates as an immutable, mathematically rigorous Axis-Aligned Bounding Box
 * (AABB) abstraction.
 * Systemically decouples geometric intersection evaluations from external UI or
 * hardware rendering dependencies natively.
 * Facilitates optimal spatial partitioning checks through strict Cartesian
 * coordinate boundary definitions explicitly.
 */
public class Hitbox2D {
    private final double x;
    private final double y;
    private final double width;
    private final double height;

    /**
     * Instantiates an immutable spatial region defining fixed bounding constraints
     * analytically.
     * 
     * @param x      Arbitrary longitudinal origin defining the lower-bound lateral
     *               Cartesian parameter natively.
     * @param y      Arbitrary latitudinal origin defining the lower-bound vertical
     *               Cartesian parameter natively.
     * @param width  Definitive horizontal magnitude establishing explicit lateral
     *               limits seamlessly.
     * @param height Definitive vertical magnitude establishing explicit
     *               longitudinal limits seamlessly.
     */
    public Hitbox2D(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Resolves the lower longitudinal boundary threshold mapping spatial minimums
     * geometrically.
     * 
     * @return double Core lateral coordinate defining absolute lower bounds
     *         analytically.
     */
    public double getMinX() {
        return x;
    }

    /**
     * Resolves the lower latitudinal boundary threshold mapping spatial minimums
     * geometrically.
     * 
     * @return double Core vertical coordinate defining absolute lower bounds
     *         analytically.
     */
    public double getMinY() {
        return y;
    }

    /**
     * Extrapolates the upper longitudinal boundary threshold through algebraic
     * scalar addition natively.
     * 
     * @return double Core lateral coordinate defining absolute upper bounds
     *         analytically.
     */
    public double getMaxX() {
        return x + width;
    }

    /**
     * Extrapolates the upper latitudinal boundary threshold through algebraic
     * scalar addition natively.
     * 
     * @return double Core vertical coordinate defining absolute upper bounds
     *         analytically.
     */
    public double getMaxY() {
        return y + height;
    }

    /**
     * Acquires the immutable lateral scalar dimension mapping physical span
     * geometrically.
     * 
     * @return double Absolute horizontal volume metric statically defined.
     */
    public double getWidth() {
        return width;
    }

    /**
     * Acquires the immutable vertical scalar dimension mapping physical span
     * geometrically.
     * 
     * @return double Absolute longitudinal volume metric statically defined.
     */
    public double getHeight() {
        return height;
    }

    /**
     * Executes deterministic Axis-Aligned Bounding Box (AABB) intersection theorem
     * mathematically evaluating spatial overlap explicitly.
     * Functions identically universally ensuring rigorous collision resolution
     * decoupling visual scaling constraints.
     * 
     * @param other Exogenous spatial boundary region interrogated conditionally
     *              against internal bounds orthogonally.
     * @return boolean True extracting verified geometric intersection metrics
     *         affirmatively, validating definitive continuous overlap statically.
     */
    public boolean intersects(Hitbox2D other) {
        if (other == null)
            return false;

        // Employs strict continuous boundary evaluations confirming geometric intrusion
        // linearly
        return this.getMaxX() > other.getMinX() &&
                this.getMinX() < other.getMaxX() &&
                this.getMaxY() > other.getMinY() &&
                this.getMinY() < other.getMaxY();
    }
}
