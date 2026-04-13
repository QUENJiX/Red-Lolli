package com.nsu.cse215l.redlolli.redlolli.core;

import javafx.geometry.Rectangle2D;

/**
 * Contract for game objects that require physical bounds and collision
 * detection.
 */
public interface Collidable {

    /** Returns the axis-aligned bounding box for overlap checks. */
    Rectangle2D getHitbox();
}
