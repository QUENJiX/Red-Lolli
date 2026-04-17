package com.nsu.cse215l.redlolli.redlolli.systems;

import com.nsu.cse215l.redlolli.redlolli.entities.*;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EntityManager {

    private Player player;
    private Monster paleLuna;
    private SerialKillerEntity serialKiller;
    private CardboardClone cloneDecoy;

    private final List<Entity> entities = new ArrayList<>();
    private final List<Item> chests = new ArrayList<>();
    private final List<GuardEntity> guards = new ArrayList<>();
    private final List<TorchEntity> torches = new ArrayList<>();

    public void clear() {
        entities.clear();
        chests.clear();
        guards.clear();
        torches.clear();
        paleLuna = null;
        serialKiller = null;
        cloneDecoy = null;
    }

    public void spawnEntities(Maze maze, int currentLevel) {
        int[][] grid = maze.getMapGrid();
        List<int[]> emptyChestTiles = new ArrayList<>();
        List<int[]> lolliChestTiles = new ArrayList<>();
        List<int[]> torchTiles = new ArrayList<>();
        int lunaRow = -1, lunaCol = -1;

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                int tile = grid[row][col];
                if (tile == 2) {
                    emptyChestTiles.add(new int[] { row, col });
                    grid[row][col] = 0;
                } else if (tile == 3) {
                    lolliChestTiles.add(new int[] { row, col });
                    grid[row][col] = 0;
                } else if (tile == 5) {
                    lunaRow = row;
                    lunaCol = col;
                    grid[row][col] = 0;
                } else if (tile == 8) {
                    torchTiles.add(new int[] { row, col });
                    grid[row][col] = 1; // Turn back into a wall tile for rendering/collision
                } else if (tile == 9) {
                    int erRow = -1, erCol = -1;
                    List<int[]> escapeRooms = maze.getTilesOfType(6);
                    double minDist = Double.MAX_VALUE;
                    for (int[] er : escapeRooms) {
                        double dist = Math.pow(er[0] - row, 2) + Math.pow(er[1] - col, 2);
                        if (dist < minDist) {
                            minDist = dist;
                            erRow = er[0];
                            erCol = er[1];
                        }
                    }
                    GuardEntity guard = null;
                    if (currentLevel == 1) {
                        guard = new GuardEntity(col * Maze.TILE_SIZE + 10, row * Maze.TILE_SIZE + Maze.Y_OFFSET + 10,
                                GuardEntity.Type.BAT, erRow, erCol);
                    } else if (currentLevel == 2) {
                        guard = new GuardEntity(col * Maze.TILE_SIZE + 10, row * Maze.TILE_SIZE + Maze.Y_OFFSET + 10,
                                GuardEntity.Type.COBRA, erRow, erCol);
                    } else if (currentLevel == 3) {
                        guard = new GuardEntity(col * Maze.TILE_SIZE + 10, row * Maze.TILE_SIZE + Maze.Y_OFFSET + 10,
                                GuardEntity.Type.CENTIPEDE, erRow, erCol);
                    }
                    if (guard != null) {
                        guards.add(guard);
                    }
                    grid[row][col] = 0; // Turn into floor
                } else if (tile == 10) {
                    if (currentLevel == 3) {
                        serialKiller = new SerialKillerEntity(col * Maze.TILE_SIZE + 6,
                                row * Maze.TILE_SIZE + Maze.Y_OFFSET + 6);
                    }
                    grid[row][col] = 0; // Turn into floor
                }
            }
        }

        for (int[] pos : emptyChestTiles) {
            Item.ContentType type = Item.ContentType.EMPTY;
            if (currentLevel == 3 && !containsContent(Item.ContentType.CLONE_DECOY)) {
                type = Item.ContentType.CLONE_DECOY;
            }
            Item chest = new Item(pos[1] * Maze.TILE_SIZE + 12, pos[0] * Maze.TILE_SIZE + Maze.Y_OFFSET + 12, type);
            chests.add(chest);
            entities.add(chest);
        }
        for (int[] pos : lolliChestTiles) {
            Item chest = new Item(pos[1] * Maze.TILE_SIZE + 12, pos[0] * Maze.TILE_SIZE + Maze.Y_OFFSET + 12,
                    Item.ContentType.LOLLI);
            chests.add(chest);
            entities.add(chest);
        }

        for (int[] pos : torchTiles) {
            TorchEntity torch = new TorchEntity(pos[1] * Maze.TILE_SIZE, pos[0] * Maze.TILE_SIZE + Maze.Y_OFFSET);
            torches.add(torch);
            entities.add(torch);
        }

        if (lunaRow >= 0) {
            paleLuna = new Monster(lunaCol * Maze.TILE_SIZE + 7.5, lunaRow * Maze.TILE_SIZE + Maze.Y_OFFSET + 7.5);
            entities.add(paleLuna);
        }

        for (GuardEntity guard : guards) {
            entities.add(guard);
        }

        if (serialKiller != null) {
            entities.add(serialKiller);
        }
    }

    private boolean containsContent(Item.ContentType type) {
        return chests.stream().anyMatch(c -> c.getContentType() == type);
    }
    
    public void addEntity(Entity entity) {
        entities.add(entity);
    }
    
    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public Player getPlayer() { return player; }
    public void setPlayer(Player player) { this.player = player; }

    public Monster getPaleLuna() { return paleLuna; }
    public SerialKillerEntity getSerialKiller() { return serialKiller; }

    public CardboardClone getCloneDecoy() { return cloneDecoy; }
    public void setCloneDecoy(CardboardClone decoy) { this.cloneDecoy = decoy; }

    public List<Entity> getEntities() { return Collections.unmodifiableList(entities); }
    public List<Item> getChests() { return Collections.unmodifiableList(chests); }
    public List<GuardEntity> getGuards() { return Collections.unmodifiableList(guards); }
    public List<TorchEntity> getTorches() { return Collections.unmodifiableList(torches); }
}