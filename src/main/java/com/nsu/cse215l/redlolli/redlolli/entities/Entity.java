package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.GameEventBus;

/**
 * Abstract base class for all interactive game objects, enforcing a standard
 * update cycle.
 */
public abstract class Entity {

    protected double x;
    protected double y;
    protected double size;

    public Entity(double x, double y, double size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public abstract void update();

    /**
     * Optional event-bus publish strategy for polymorphic collisions.
     * Fires a system-wide collision event without requiring strict
     * instanceof checks on the receiver end.
     */
    public void publishCollision(Entity other) {
        GameEventBus.getInstance().publishCollision(this, other);
    }
    
    /**
     * Direct method variant for deeper polymorphism allowing entities
     * to react to physical contacts without an EventBus if needed.
     */
    public void onCollide(Entity other) {
        publishCollision(other);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getSize() {
        return size;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
