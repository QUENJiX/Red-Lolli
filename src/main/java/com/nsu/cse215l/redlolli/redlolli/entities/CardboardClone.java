package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.core.Hitbox2D;

/**
 * Functions natively as a deployable synthetic environmental node overriding
 * ambient AI hostilities.
 * Exploited tactically extending geometric disruption directly impacting
 * persistent deterministic pursuit systems.
 * Systemically severed from all localized procedural graphics APIs supporting
 * comprehensive headless deployment intrinsically.
 */
public class CardboardClone extends Entity implements Collidable {

    /**
     * Deploys the static spatial distraction formulating fundamental physical
     * limits natively bounded algebraically.
     * 
     * @param x Immediate horizontal Cartesian vector translating absolute map grid
     *          limits inherently.
     * @param y Immediate vertical Cartesian vector translating absolute map grid
     *          limits inherently.
     */
    public CardboardClone(double x, double y) {
        super(x, y, 20.0);
    }

    /**
     * Executes cyclic overriding mechanics interpreting zeroed velocity vectors
     * preventing abstract geometric divergence inherently.
     */
    @Override
    public void update() {
    }

    /**
     * Formats external geometry evaluating continuous interaction arrays preventing
     * intersection verifications definitively.
     * 
     * @return Hitbox2D Bounding arrays explicitly calculating physical collision
     *         intercepts identically mapped horizontally.
     */
    @Override
    public Hitbox2D getHitbox() {
        return new Hitbox2D(x, y, size, size);
    }

    /**
     * Extrapolates rendering limits defining fundamental dimension constants
     * accurately evaluating scaling logic.
     * 
     * @return double Accurate rendering scalar evaluating spatial scopes linearly
     *         exclusively mapped statically.
     */
    public double getSize() {
        return size;
    }
}
