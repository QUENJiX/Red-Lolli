package com.nsu.cse215l.redlolli.redlolli.systems;

import com.nsu.cse215l.redlolli.redlolli.entities.*;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import com.nsu.cse215l.redlolli.redlolli.ui.GameRenderer;
import com.nsu.cse215l.redlolli.redlolli.ui.HUDRenderer;

public class LevelManager {

    private static final String[] MAP_FILES = { "/map.csv", "/map2.csv", "/map3.csv" };
    private int currentLevel = 1;
    private Maze maze;

    public Maze getMaze() {
        return maze;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int level) {
        this.currentLevel = level;
    }

    public void loadLevel(EntityManager entityManager) {
        // Maze.initImages() moved to GameRenderer
        // GuardEntity.initImages() moved to GameRenderer
        SerialKillerEntity.initImages();
        // Player.initImages() moved to GameRenderer
        GameRenderer.initImages();
        HUDRenderer.initImages();

        maze = new Maze(MAP_FILES[currentLevel - 1], currentLevel);
        double spawnX = maze.getPlayerSpawnCol() * Maze.TILE_SIZE + 10;
        double spawnY = maze.getPlayerSpawnRow() * Maze.TILE_SIZE + Maze.Y_OFFSET + 10;
        
        Player player = new Player(spawnX, spawnY);
        entityManager.setPlayer(player);
        entityManager.addEntity(player);
        entityManager.spawnEntities(maze, currentLevel);
    }
}