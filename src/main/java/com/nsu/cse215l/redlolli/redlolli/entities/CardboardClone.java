package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.core.Hitbox2D;

/**
 * A decoy item that the player can drop to distract enemies.
 * It acts as a temporary obstacle or target to keep monsters busy, 
 * and it's completely separated from the game's visuals so it works smoothly under the hood.
 */
public class CardboardClone extends Entity implements Collidable {

    /**
     * Places the decoy at a specific location on the map.
     * 
     * @param x The horizontal X coordinate.
     * @param y The vertical Y coordinate.
     */
    public CardboardClone(double x, double y) {
        super(x, y, 20.0);
    }

    /**
     * Does nothing. The decoy just sits there, so it doesn't need to update its position.
     */
    @Override
    public void update() {
    }

    /**
     * Gets the physical boundary of the decoy so enemies know they've bumped into it.
     * 
     * @return The 2D hitbox representing the decoy's space.
     */
    @Override
    public Hitbox2D getHitbox() {
        return new Hitbox2D(x, y, size, size);
    }

    /**
     * Returns the size of the decoy (its width and height).
     * 
     * @return The size of the decoy.
     */
    public double getSize() {
        return size;
    }
}
