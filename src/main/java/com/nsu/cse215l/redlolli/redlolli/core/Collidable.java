package com.nsu.cse215l.redlolli.redlolli.core;

/**
 * Contract for game objects that require physical bounds and collision
 * detection.
 */
public interface Collidable {

    /** Returns the axis-aligned bounding box for overlap checks. */
    Hitbox2D getHitbox();
}
