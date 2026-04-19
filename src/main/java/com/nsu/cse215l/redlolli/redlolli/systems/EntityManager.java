package com.nsu.cse215l.redlolli.redlolli.systems;

import com.nsu.cse215l.redlolli.redlolli.entities.*;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Acts as the master list for everything moving or interactive in the game.
 * Tracks the player, monsters, chests, items, and torches so other systems, 
 * like drawing or collisions, can loop through them easily without having to 
 * worry about where they are physically stored.
 */
public class EntityManager {

    private Player player;
    private Monster paleLuna;
    private SerialKillerEntity serialKiller;
    private CardboardClone cloneDecoy;

    private final List<Entity> entities = new ArrayList<>();
    private final List<Item> chests = new ArrayList<>();
    private final List<GuardEntity> guards = new ArrayList<>();
    private final List<TorchEntity> torches = new ArrayList<>();

    /**
     * Wipes the entire screen clean of items, monsters, and characters.
     * Perfect for getting ready to load the next level!
     */
    public void clear() {
        entities.clear();
        chests.clear();
        guards.clear();
        torches.clear();
        paleLuna = null;
        serialKiller = null;
        cloneDecoy = null;
    }

    /**
     * Spawns all characters and interactable objects onto the map according to 
     * exactly where the level's map file says they should be. It can also spawn 
     * harder monsters or better items on later levels!
     *
     * @param maze         The structural grid the game uses to place tiles.
     * @param currentLevel The number of the current stage, making enemies harder at high numbers.
     */
    public void spawnEntities(Maze maze, int currentLevel) {
        int[][] grid = maze.getMapGrid();

        // Keep track of specific special spots like chests and characters mapped 
        // to spawn positions.
        List<int[]> emptyChestTiles = new ArrayList<>();
        List<int[]> lolliChestTiles = new ArrayList<>();
        List<int[]> torchTiles = new ArrayList<>();
        int lunaRow = -1, lunaCol = -1;

        // Loop through all points in the level to find numbers corresponding to 
        // monsters and items.
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                int tile = grid[row][col];

                // Determine what exactly each number means. Usually numbers represent empty boxes
                // or specific enemy types.
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
                    // Torches are actually physical walls, so turn the tile into a solid block!
                    grid[row][col] = 1;
                } else if (tile == 9) {
                    int erRow = -1, erCol = -1;

                    // Tell the guard where an escape room is, so they can block it until we get
                    // their item.
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

                    // Swap out the basic bat for tougher enemies like cobras and centipedes 
                    // when you progress in the game.
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
                    grid[row][col] = 0;
                } else if (tile == 10) {

                    // Oh boy. He's here. Only load the serial killer on level 3!
                    if (currentLevel == 3) {
                        serialKiller = new SerialKillerEntity(col * Maze.TILE_SIZE + 6,
                                row * Maze.TILE_SIZE + Maze.Y_OFFSET + 6);
                    }
                    grid[row][col] = 0;
                }
            }
        }

        // Put the boxes in the locations we marked out earlier. We even add a 
        // cardboard clone tool randomly if the player doesn't already have one.
        for (int[] pos : emptyChestTiles) {
            Item.ContentType type = Item.ContentType.EMPTY;

            // Make sure we only give them one decoy box per stage.
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

        // Deploy the monster at her special spawning point on the level map.
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

    /**
     * Allows us to quickly check if a certain type of item has already 
     * been hidden inside a chest somewhere. Super useful for unique items!
     *
     * @param type The specific type of content we're wondering about.
     * @return True if another chest with this identical item already exists.
     */
    private boolean containsContent(Item.ContentType type) {
        return chests.stream().anyMatch(c -> c.getContentType() == type);
    }

    /**
     * Inserts any normal entity into the tracking lists without making a fuss.
     * Often used to load simple objects.
     *
     * @param entity The living or static puzzle piece you're dropping into the world.
     */
    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    /**
     * Wipes a specific character or object completely off the map.
     * Used when monsters die or items get scavenged!
     *
     * @param entity The exact character or object looking to be deleted.
     */
    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    /**
     * Gets direct access to the main character doing the exploring!
     *
     * @return The Player object representing the user.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Plugs the user's new character into the master tracker.
     *
     * @param player The shiny new Player object being instantiated.
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Returns the main antagonist of the entire game so that 
     * systems can figure out where she is and what she's screaming about.
     *
     * @return The Monster affectionately known as Pale Luna.
     */
    public Monster getPaleLuna() {
        return paleLuna;
    }

    /**
     * Checks up on the optional serial killer antagonist introduced 
     * in the latest stages.
     *
     * @return The SerialKillerEntity hunting the player.
     */
    public SerialKillerEntity getSerialKiller() {
        return serialKiller;
    }

    /**
     * Provides access to the fake cardboard cut-out 
     * if the player placed one to trick monsters!
     *
     * @return The currently active CardboardClone, or null if none 
     *         are deployed.
     */
    public CardboardClone getCloneDecoy() {
        return cloneDecoy;
    }

    /**
     * Locks the deployed decoy into the tracker so all monsters will 
     * look at it instead.
     *
     * @param decoy The new CardboardClone being placed on the floor down.
     */
    public void setCloneDecoy(CardboardClone decoy) {
        this.cloneDecoy = decoy;
    }

    /**
     * Gets a complete list of everything that exists right now.
     * Returns it safely so no one accidentally breaks the list!
     *
     * @return An unmodifiable list of all active game Entities.
     */
    public List<Entity> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    /**
     * Spits out a ready-to-read list of all lootable treasure boxes.
     *
     * @return An unmodifiable list of Chests (Items) waiting to be opened.
     */
    public List<Item> getChests() {
        return Collections.unmodifiableList(chests);
    }

    /**
     * Gives you all the smaller, stationed enemy guards in the level 
     * (like bats and snakes) so collision detectors can check if you stepped on them.
     *
     * @return An unmodifiable list of standard Guards.
     */
    public List<GuardEntity> getGuards() {
        return Collections.unmodifiableList(guards);
    }

    /**
     * Tells the lighting system where all the warm, glowing torches 
     * are scattered around.
     *
     * @return An unmodifiable list of Torch objects nailed to the walls.
     */
    public List<TorchEntity> getTorches() {
        return Collections.unmodifiableList(torches);
    }
}
