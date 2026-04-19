package com.nsu.cse215l.redlolli.redlolli.core;

/**
 * Represents an object in the game world that can bump into other objects.
 * Anything that implements this interface has a physical boundary and can
 * trigger collisions.
 */
public interface Collidable {

    /**
     * Gets the boundary box (hitbox) for this object.
     * We use this to figure out if this object is overlapping or crashing into
     * anything else on the screen.
     * 
     * @return The 2D hitbox representing the object's physical space.
     */
    Hitbox2D getHitbox();
}
