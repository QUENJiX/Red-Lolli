package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.GameEventBus;

/**
 * Establishes the foundational polymorphic abstraction mapping the base
 * geometric
 * dimensions and chronologically updated states of all interactive objects.
 * Dictates standard coordinate allocations and mandates uniform behavioral
 * implementations natively.
 */
public abstract class Entity {

    protected double x;
    protected double y;
    protected double size;

    /**
     * Instantiates the primary environmental node assigning core geometric
     * constraints.
     * 
     * @param x    Arbitrary longitudinal coordinate native to the geometric plane.
     * @param y    Arbitrary latitudinal coordinate native to the geometric plane.
     * @param size Uniform scalar value establishing base dimensional magnitudes
     *             mathematically.
     */
    public Entity(double x, double y, double size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    /**
     * Prescribes mandatory operational overloads validating execution parameters
     * natively across all derivative implementations.
     * Guarantees synchronized tick cycles iteratively across explicitly
     * orchestrated system iterations.
     */
    public abstract void update();

    /**
     * Injects spatial anomalies securely mapped onto generalized communication
     * systems synchronously.
     * Offloads interaction evaluations abstracting the physical execution towards
     * arbitrary subscribing elements globally.
     * 
     * @param other Formulated geometric array participating reciprocally
     *              physically.
     */
    public void publishCollision(Entity other) {
        GameEventBus.getInstance().publishCollision(this, other);
    }

    /**
     * Translates local geometric interaction queries implicitly mirroring
     * standardized communication logic.
     * Enables polymorphic reactions explicitly resolving interaction consequences
     * securely without localized parsing limits natively.
     * 
     * @param other Implicated interactive body engaged geographically currently.
     */
    public void onCollide(Entity other) {
        publishCollision(other);
    }

    /**
     * Extrapolates relative centralized horizontal position evaluating current map
     * metrics naturally.
     * 
     * @return double Extracted coordinate metric mapping horizontal position
     *         accurately.
     */
    public double getX() {
        return x;
    }

    /**
     * Extrapolates relative centralized vertical position evaluating current map
     * metrics naturally.
     * 
     * @return double Extracted coordinate metric mapping vertical position
     *         accurately.
     */
    public double getY() {
        return y;
    }

    /**
     * Replicates internal dimension scalars scaling interaction scopes natively
     * defining precise rendering scopes radially.
     * 
     * @return double Core scalar interpreting dimensional scales globally
     *         accurately.
     */
    public double getSize() {
        return size;
    }

    /**
     * Transposes explicit localized execution ranges abruptly modifying coordinate
     * centers entirely mathematically.
     * 
     * @param x Arbitrary replacement horizontal vector parameter defined externally
     *          securely.
     * @param y Arbitrary replacement vertical vector parameter defined externally
     *          securely.
     */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
