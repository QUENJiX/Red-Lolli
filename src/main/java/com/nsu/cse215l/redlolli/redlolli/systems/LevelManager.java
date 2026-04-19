package com.nsu.cse215l.redlolli.redlolli.systems;

import com.nsu.cse215l.redlolli.redlolli.entities.*;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import com.nsu.cse215l.redlolli.redlolli.ui.GameRenderer;
import com.nsu.cse215l.redlolli.redlolli.ui.HUDRenderer;

/**
 * This class handles moving the player between different levels in the game.
 * It's responsible for loading the actual maps, hooking them up to the game
 * engines, placing the player at the starting positions, and making sure 
 * memory gets cleared smoothly between stages.
 */
public class LevelManager {

    private static final String[] MAP_FILES = { "/map.csv", "/map2.csv", "/map3.csv" };
    private int currentLevel = 1;
    private Maze maze;

    /**
     * Gets the map object that is currently loaded and being played.
     *
     * @return The active Maze instance storing level layout and data.
     */
    public Maze getMaze() {
        return maze;
    }

    /**
     * Gets the number of the dungeon the player is currently inside.
     *
     * @return The current level integer (e.g. 1 for level 1).
     */
    public int getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Instantly sets the player's level number to a specific value.
     * Helpful for cheat codes or skipping around manually!
     *
     * @param level The number of the new level you want to transition to.
     */
    public void setCurrentLevel(int level) {
        this.currentLevel = level;
    }

    /**
     * Fires up the loading process to get the player into the level they are
     * currently assigned to. Rebuilds the map, resets the player, loads up new
     * monsters, and tells the game to get ready to play.
     *
     * @param entityManager The system that manages and tracks all actors (players, monsters, items) so they can be spawned.
     */
    public void loadLevel(EntityManager entityManager) {
        // We initialize the basic sprites and HUD textures before gameplay starts.
        GameRenderer.initImages();
        HUDRenderer.initImages();

        // Create the new room array according to our CSV level files.
        maze = new Maze(MAP_FILES[currentLevel - 1], currentLevel);

        // Figures out exactly where on the screen the player needs to appear.
        double spawnX = maze.getPlayerSpawnCol() * Maze.TILE_SIZE + 10;
        double spawnY = maze.getPlayerSpawnRow() * Maze.TILE_SIZE + Maze.Y_OFFSET + 10;

        // Plops the player safely into the new room.
        Player player = new Player(spawnX, spawnY);
        entityManager.setPlayer(player);
        entityManager.addEntity(player);

        // Fills the dungeon with monsters and treasure chests.
        entityManager.spawnEntities(maze, currentLevel);
    }
}
