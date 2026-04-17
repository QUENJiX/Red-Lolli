package com.nsu.cse215l.redlolli.redlolli.entities;

import javafx.scene.canvas.GraphicsContext;

/**
 * Abstract base class for all interactive game objects, enforcing a standard
 * update/render cycle.
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

    // TODO: Deprecate - Being moved to RenderSystem
    public void render(GraphicsContext gc) {}

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
