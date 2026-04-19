package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.core.Hitbox2D;

/**
 * An item sitting on the map that the player can pick up, like a piece of candy or a decoy. 
 * They don't move or do anything on their own until the player steps on them.
 */
public class Item extends Entity implements Collidable {

    /**
     * The different kinds of items that can exist in the game.
     */
    public enum ContentType {
        EMPTY,
        LOLLI,
        CLONE_DECOY
    }

    private boolean isCollected = false;
    private final ContentType contentType;

    /**
     * Places a new item on the map at the given x and y coordinates.
     * 
     * @param x           The horizontal position of the item.
     * @param y           The vertical position of the item.
     * @param contentType The specific type of item (like a lolli or a clone decoy).
     */
    public Item(double x, double y, ContentType contentType) {
        super(x, y, 16.0);
        this.contentType = contentType;
    }

    /**
     * Items don't move, so they do nothing each frame.
     */
    @Override
    public void update() {
    }

    /**
     * Marks this item as collected so the game knows to remove it from the map
     * and give the player their reward.
     */
    public void collect() {
        this.isCollected = true;
    }

    /**
     * Gets the physical boundary of the item so the player can pick it up
     * by walking into it.
     * 
     * @return The 2D hitbox representing the item on the map.
     */
    @Override
    public Hitbox2D getHitbox() {
        return new Hitbox2D(x, y, size, size);
    }

    /**
     * Tells us if this item has already been collected by the player.
     * 
     * @return True if the player already picked it up.
     */
    public boolean isCollected() {
        return isCollected;
    }

    /**
     * Small helper method to quickly check if this item is the main objective (a Lolli).
     * 
     * @return True if the item is a lolli.
     */
    public boolean hasLolli() {
        return contentType == ContentType.LOLLI;
    }

    /**
     * Gets the specific type of this item so the game knows what reward to give the player.
     * 
     * @return The item's type.
     */
    public ContentType getContentType() {
        return contentType;
    }

    /**
     * Returns the visual or physical size of the item.
     * 
     * @return The dimensions of the item.
     */
    public double getSize() {
        return size;
    }
}
