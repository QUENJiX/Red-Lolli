package com.nsu.cse215l.redlolli.redlolli.core;

/**
 * A lightweight, dependency-free 2D axis-aligned bounding box.
 * Decouples collision checks from JavaFX geometry classes.
 */
public class Hitbox2D {
    private final double x;
    private final double y;
    private final double width;
    private final double height;

    public Hitbox2D(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public double getMinX() {
        return x;
    }

    public double getMinY() {
        return y;
    }

    public double getMaxX() {
        return x + width;
    }

    public double getMaxY() {
        return y + height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    /**
     * Checks if this hitbox overlaps with another.
     */
    public boolean intersects(Hitbox2D other) {
        if (other == null) return false;
        
        return this.getMaxX() > other.getMinX() &&
               this.getMinX() < other.getMaxX() &&
               this.getMaxY() > other.getMinY() &&
               this.getMinY() < other.getMaxY();
    }
}
