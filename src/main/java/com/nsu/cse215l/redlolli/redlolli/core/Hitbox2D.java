package com.nsu.cse215l.redlolli.redlolli.core;

/**
 * A simple rectangular bounding box used for collision detection.
 * It stores basic x, y, width, and height values so we can check if
 * objects overlap without relying on complex rendering logic.
 */
public class Hitbox2D {
    private final double x;
    private final double y;
    private final double width;
    private final double height;

    /**
     * Creates a new hitbox with fixed dimensions.
     * 
     * @param x      The left X coordinate of the box.
     * @param y      The top Y coordinate of the box.
     * @param width  How wide the box is.
     * @param height How tall the box is.
     */
    public Hitbox2D(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Gets the left boundary of the hitbox.
     * 
     * @return The minimum X coordinate.
     */
    public double getMinX() {
        return x;
    }

    /**
     * Gets the top boundary of the hitbox.
     * 
     * @return The minimum Y coordinate.
     */
    public double getMinY() {
        return y;
    }

    /**
     * Gets the right boundary of the hitbox.
     * 
     * @return The maximum X coordinate.
     */
    public double getMaxX() {
        return x + width;
    }

    /**
     * Gets the bottom boundary of the hitbox.
     * 
     * @return The maximum Y coordinate.
     */
    public double getMaxY() {
        return y + height;
    }

    /**
     * Gets the current width of the hitbox.
     * 
     * @return The horizontal size.
     */
    public double getWidth() {
        return width;
    }

    /**
     * Gets the current height of the hitbox.
     * 
     * @return The vertical size.
     */
    public double getHeight() {
        return height;
    }

    /**
     * Checks if this hitbox is currently overlapping with another hitbox.
     * We use this to figure out if two objects in the game have collided.
     * 
     * @param other The other hitbox to check against.
     * @return True if the hitboxes overlap, false otherwise.
     */
    public boolean intersects(Hitbox2D other) {
        if (other == null)
            return false;

        // Check if the boundaries of the two boxes overlap
        return this.getMaxX() > other.getMinX() &&
                this.getMinX() < other.getMaxX() &&
                this.getMaxY() > other.getMinY() &&
                this.getMinY() < other.getMaxY();
    }
}
