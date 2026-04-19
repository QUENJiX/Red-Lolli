package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.GameEventBus;

/**
 * The base class for everything that exists in the game world.
 * If something has an X/Y position and a size, it's an Entity.
 * This class ensures that all objects share a common structure for updates and collisions.
 */
public abstract class Entity {

    protected double x;
    protected double y;
    protected double size;

    /**
     * Creates a new entity at the given coordinates with a specific size.
     * 
     * @param x    The starting X coordinate on the map.
     * @param y    The starting Y coordinate on the map.
     * @param size How big the entity is.
     */
    public Entity(double x, double y, double size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    /**
     * Updates the entity's state each frame. 
     * Every specific entity (like a player or an enemy) implements this 
     * to handle its own movement or rules over time.
     */
    public abstract void update();

    /**
     * Announces to the event bus that this entity just collided with something else.
     * Other systems listen for this to trigger sound effects, damage, or item pickups.
     * 
     * @param other The other entity involved in the crash.
     */
    public void publishCollision(Entity other) {
        GameEventBus.getInstance().publishCollision(this, other);
    }

    /**
     * Called whenever this entity bumps into another object.
     * By default, it just broadcasts the collision to the rest of the game.
     * 
     * @param other The entity we just hit.
     */
    public void onCollide(Entity other) {
        publishCollision(other);
    }

    /**
     * Gets the current horizontal position of the entity.
     * 
     * @return The X coordinate.
     */
    public double getX() {
        return x;
    }

    /**
     * Gets the current vertical position of the entity.
     * 
     * @return The Y coordinate.
     */
    public double getY() {
        return y;
    }

    /**
     * Gets the size (width/height) of the entity natively used for logic and rendering.
     * 
     * @return The size value.
     */
    public double getSize() {
        return size;
    }

    /**
     * Immediately moves the entity to a new set of coordinates.
     * 
     * @param x The new X coordinate.
     * @param y The new Y coordinate.
     */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
