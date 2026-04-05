package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * The Item class represents interactive objects placed in the maze, primarily visually modeled as chests.
 * Items contain various loots (the main goal 'Red Lolli', or power-ups like Invisibility and Decoys).
 * 
 * It manages its own visual state (open vs. closed) and provides a specific visual glowing cue
 * once opened based on the type of content it held.
 * 
 * Development History:
 * - Phase 2, Week 2, Day 11: Introduced interactive item pickups and collection flags.
 */
public class Item extends Entity implements Collidable {

    /**
     * Defines the potential contents hidden inside a chest/item drop.
     */
    public enum ContentType {
        EMPTY,               // Nothing inside, a dud chest
        LOLLI,               // The primary win condition item
        POTION_INVISIBILITY, // Power-up: Grants temporary invisibility from monsters
        CLONE_DECOY          // Power-up: Spawns a dummy to distract monsters
    }

    // ==============================================================
    // FIELDS
    // ==============================================================

    /** State flag to determine if the player has already interacted with this item. */
    private boolean isCollected = false;
    
    /** The actual reward stored inside this specific item instance. */
    private final ContentType contentType;

    // ==============================================================
    // CONSTRUCTORS
    // ==============================================================

    /**
     * Legacy constructor primarily for the original iteration of the game where chests
     * only contained the Lolli or were empty.
     * 
     * @param x        X-coordinate in the maze.
     * @param y        Y-coordinate in the maze.
     * @param hasLolli True if the chest contained the main quest item.
     */
    public Item(double x, double y, boolean hasLolli) {
        this(x, y, hasLolli ? ContentType.LOLLI : ContentType.EMPTY);
    }

    /**
     * Primary constructor initializing the item at a coordinate with specific contents.
     * 
     * @param x           X-coordinate in the maze.
     * @param y           Y-coordinate in the maze.
     * @param contentType The specific enum representing the chest's loot.
     */
    public Item(double x, double y, ContentType contentType) {
        super(x, y, 16.0); // Base item size is 16 pixels
        this.contentType = contentType;
    }

    // ==============================================================
    // GAME LOGIC
    // ==============================================================

    /**
     * Required by Entity interface. Static environment items like these chests
     * don't have per-frame update logic (like animations or physics), so it's empty.
     */
    @Override
    public void update() {
        // Chests are static — no per-frame logic required
    }

    /** 
     * Flags the chest as opened, triggering the loot logic externally and 
     * switching its rendering state to open. 
     */
    public void collect() { this.isCollected = true; }

    // ==============================================================
    // RENDERING
    // ==============================================================

    /**
     * Controls the rendering pass by checking if it has been opened or not.
     */
    @Override
    public void render(GraphicsContext gc) {
        if (isCollected) {
            renderOpenedChest(gc);
        } else {
            renderClosedChest(gc);
        }
    }

    /** 
     * Draws an opened chest with the lid flipped backwards.
     * Renders a colored particle/glow inside the chest corresponding to the loot type.
     */
    private void renderOpenedChest(GraphicsContext gc) {
        // --- 1. Draw the Base/Box ---
        gc.setFill(Color.rgb(80, 50, 15));
        gc.fillRect(x, y + 6, size, size - 6);

        // --- 2. Draw the hollow 'inner' shadow ---
        gc.setFill(Color.rgb(30, 15, 5));
        gc.fillRect(x + 2, y + 8, size - 4, size - 10);

        // --- 3. Draw the lid flipped back above the chest ---
        gc.setFill(Color.rgb(110, 65, 20));
        gc.fillRect(x - 1, y, size + 2, 5);
        gc.setFill(Color.rgb(90, 55, 18));
        gc.fillRect(x, y - 3, size, 4);

        // --- 4. Draw the broken gold lock ---
        gc.setFill(Color.rgb(120, 100, 20));
        gc.fillOval(x + size / 2 - 1.5, y + 5, 3, 3);

        // --- 5. Draw the Loot Glow/Particles ---
        // Color coding immediately provides player feedback on what they picked up
        if (contentType == ContentType.LOLLI) {
            // Intense red glow for the main objective
            gc.setFill(Color.rgb(255, 0, 0, 0.4));
            gc.fillOval(x + 3, y + 8, size - 6, size - 12);
        } else if (contentType == ContentType.POTION_INVISIBILITY) {
            // Blue/Cyan glow for invis potion
            gc.setFill(Color.rgb(80, 150, 255, 0.45));
            gc.fillOval(x + 3, y + 8, size - 6, size - 12);
        } else if (contentType == ContentType.CLONE_DECOY) {
            // Orange/brown glow for the decoy dropping
            gc.setFill(Color.rgb(220, 180, 130, 0.5));
            gc.fillOval(x + 3, y + 8, size - 6, size - 12);
        }
    }

    /** 
     * Draws a fresh, closed chest. Includes details like wood grain banding,
     * a lock, and a question mark to denote it's a mystery box.
     */
    private void renderClosedChest(GraphicsContext gc) {
        // --- 1. Outer ambient glow to draw the player's eye in the dark ---
        gc.setFill(Color.rgb(200, 170, 50, 0.15));
        gc.fillOval(x - 3, y - 3, size + 6, size + 6);

        // --- 2. Main Wooden Body (with varying layers for 'planks') ---
        gc.setFill(Color.rgb(130, 75, 25));
        gc.fillRect(x, y + 5, size, size - 5);
        gc.setFill(Color.rgb(110, 60, 18));
        gc.fillRect(x + 2, y + 8, size - 4, 2);
        gc.fillRect(x + 2, y + 12, size - 4, 1);

        // --- 3. Iron/Steel structural bands on top and bottom ---
        gc.setFill(Color.rgb(80, 80, 90));
        gc.fillRect(x, y + 5, size, 2);
        gc.fillRect(x, y + size - 2, size, 2);

        // --- 4. The curved/raised lid of the chest ---
        gc.setFill(Color.rgb(155, 95, 35));
        gc.fillRect(x - 1, y, size + 2, 6);
        gc.setFill(Color.rgb(170, 110, 45));
        gc.fillRect(x + 1, y + 1, size - 2, 3);

        // --- 5. Front Gold Lock with a dark keyhole ---
        gc.setFill(Color.GOLD);
        gc.fillOval(x + size / 2 - 3, y + 5, 6, 6);
        gc.setFill(Color.rgb(80, 50, 10));
        gc.fillOval(x + size / 2 - 1, y + 7, 2, 2);

        // --- 6. Stylized Question Mark graphic on the front panel ---
        gc.setFill(Color.rgb(255, 215, 0));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        gc.fillText("?", x + size / 2 - 3, y + size - 2);
    }

    // ==============================================================
    // COLLISION & GETTERS
    // ==============================================================

    /**
     * Gets the bounding box used for collision detection.
     * Required by the Collidable interface.
     */
    @Override
    public Rectangle2D getHitbox() {
        return new Rectangle2D(x, y, size, size);
    }

    /** @return True if the player already interacted with this item. */
    public boolean isCollected() { return isCollected; }
    
    /** @return True if the item is specifically the final objective (the Red Lolli). */
    public boolean hasLolli()    { return contentType == ContentType.LOLLI; }
    
    /** @return The enumerated type defining the item's specific contents. */
    public ContentType getContentType() { return contentType; }
}