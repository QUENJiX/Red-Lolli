package com.nsu.cse215l.redlolli.redlolli.entities;

import javafx.scene.canvas.GraphicsContext;

/**
 * The Entity class serves as the foundational skeleton for every interactive object 
 * inside the game world. It enforces a standard loop cycle (update and render) 
 * for derived classes like Player, Monster, and Item.
 * 
 * Using an abstract class prevents directly instantiating a raw 'Entity' and instead
 * allows polymorphism where systems can track lists of Entities and process them
 * uniformly during the game tick.
 * 
 * Development History:
 * - Phase 1, Week 1, Day 2: Base Entity class and update/render contracts established.
 * - Phase 3, Week 3, Day 20: General refactoring pass executed.
 */
public abstract class Entity {

    // ==============================================================
    // CORE FIELDS: Standard spatial parameters
    // ==============================================================

    /** The X-coordinate position in the game world, accessible to derived classes. */
    protected double x;
    
    /** The Y-coordinate position in the game world, accessible to derived classes. */
    protected double y;
    
    /** The standard bounding diameter or width/height of the entity. */
    protected double size;

    // ==============================================================
    // CONSTRUCTOR
    // ==============================================================

    /**
     * Initializes the fundamental properties shared by all derived entities.
     * 
     * @param x    The starting X-coordinate.
     * @param y    The starting Y-coordinate.
     * @param size The default size defining its visual or physical bounds.
     */
    public Entity(double x, double y, double size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    // ==============================================================
    // ABSTRACT METHODS: Contracts for the game loop
    // ==============================================================

    /** 
     * Mandates that subclasses must implement logic executed on every update cycle
     * (e.g., movement, animation timers, decision-making).
     */
    public abstract void update();

    /** 
     * Mandates that subclasses must define how they trace themselves onto the screen.
     * 
     * @param gc The GraphicsContext used to issue drawing commands.
     */
    public abstract void render(GraphicsContext gc);

    // ==============================================================
    // UTILITY GETTERS & SETTERS
    // ==============================================================

    /** @return The current X position */
    public double getX() { return x; }
    
    /** @return The current Y position */
    public double getY() { return y; }

    /**
     * Manually overrides the position of the entity, typically used during
     * initialization, respawns, or teleportation effects.
     */
    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }
}