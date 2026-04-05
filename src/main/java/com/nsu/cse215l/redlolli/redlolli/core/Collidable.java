package com.nsu.cse215l.redlolli.redlolli.core;

import javafx.geometry.Rectangle2D;

/**
 * The Collidable interface defines a contract for anything in the game world
 * that requires physical bounds and collision detection logic.
 * 
 * Primarily implemented by entities such as Player, Monster, and Item to allow
 * interaction with the Maze walls or with each other. By standardizing this, 
 * systems can handle overlap checks generically without knowing the exact entity class.
 * 
 * Development History:
 * - Phase 1, Week 1, Day 2: Initial architecture routing and interface definition created.
 * - Phase 1, Week 1, Day 14: Hitboxes structurally refined to manage complex bounds.
 */
public interface Collidable {

    /** 
     * Calculates and returns the axis-aligned bounding box (AABB) of the object
     * at its current position.
     * 
     * @return Rectangle2D The 2D boundary used for mathematical overlap checks.
     */
    Rectangle2D getHitbox();
}