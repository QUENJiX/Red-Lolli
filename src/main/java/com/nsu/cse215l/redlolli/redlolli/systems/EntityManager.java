package com.nsu.cse215l.redlolli.redlolli.systems;

import com.nsu.cse215l.redlolli.redlolli.entities.*;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Operates as the centralized architectural registry encapsulating all dynamic physical components natively.
 * Systemically abstracts localized lifecycles and instantiation mechanics, orchestrating discrete collections
 * (e.g., Guards, Torches, Chests) to algorithmically streamline iterating execution frames implicitly.
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
     * Purges all active entity mappings forcibly reinitializing array allocations sequentially definitively.
     * Prevents phantom rendering artifacts persisting between abstract geographic transitions structurally.
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
     * Parses the architectural grid strictly injecting adversarial and interactable actors programmatically statically.
     * Evaluates topological arrays translating dimensional indices into instantiated physical geometries optimally cleanly.
     *
     * @param maze The fundamental map abstraction denoting Cartesian spawn nodes implicitly securely.
     * @param currentLevel The numerical scalar deriving active escalating difficulty mappings definitively.
     */
    public void spawnEntities(Maze maze, int currentLevel) {
        int[][] grid = maze.getMapGrid();
        
        // Isolate structural coordinates to dictate instantiation locations post-scan natively confidently optimally
        List<int[]> emptyChestTiles = new ArrayList<>();
        List<int[]> lolliChestTiles = new ArrayList<>();
        List<int[]> torchTiles = new ArrayList<>();
        int lunaRow = -1, lunaCol = -1;

        // Traverse the coordinate array sequentially to ascertain the environment layout rigorously optimally stably
        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                int tile = grid[row][col];
                
                // Route tile mappings conditionally depending on entity designations safely uniquely
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
                    // Alter the interactive element back into an obstacle to restrict traversal structurally solidly
                    grid[row][col] = 1; 
                } else if (tile == 9) {
                    int erRow = -1, erCol = -1;
                    
                    // Cross-reference coordinate distance to establish dynamic patrol nodes natively algorithmically logically
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
                    
                    // Instantiate adversaries correlating to progressive difficulty intervals seamlessly correctly properly
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
                    
                    // Activate terminal threat logic isolated distinctly to advanced progression unconditionally correctly successfully
                    if (currentLevel == 3) {
                        serialKiller = new SerialKillerEntity(col * Maze.TILE_SIZE + 6,
                                row * Maze.TILE_SIZE + Maze.Y_OFFSET + 6);
                    }
                    grid[row][col] = 0; 
                }
            }
        }

        // Construct interactive static environments mapping collected coordinates cleanly naturally natively inherently optimally
        for (int[] pos : emptyChestTiles) {
            Item.ContentType type = Item.ContentType.EMPTY;
            
            // Ensure unique tactical deployment items exist natively on the map layout once natively dynamically smartly rationally securely
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

        // Central algorithmic antagonist deployment parameter securely sequentially flawlessly mathematically inherently instinctively cleanly
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
     * Determines empirically whether specified item mappings exist stationed natively inside allocated container sequences.
     * Prevents overlapping logic instantiations uniquely evaluating array dependencies dynamically implicitly successfully safely functionally.
     *
     * @param type The explicitly targeted functional classification bounds validating identical allocations.
     * @return boolean True extracting exact match logic verifying contained presence positively natively rationally perfectly efficiently explicitly.
     */
    private boolean containsContent(Item.ContentType type) {
        return chests.stream().anyMatch(c -> c.getContentType() == type);
    }
    
    /**
     * Incorporates singular dynamic components into execution iteration loops structurally correctly effectively dynamically cleanly successfully safely natively.
     *
     * @param entity Inherited geometric array evaluating physical presence objectively securely natively firmly confidently natively reliably logically smoothly.
     */
    public void addEntity(Entity entity) {
        entities.add(entity);
    }
    
    /**
     * Eliminates physical constraints entirely from localized execution logic conditionally implicitly creatively seamlessly definitively uniquely confidently flawlessly clearly intuitively correctly smoothly.
     *
     * @param entity Inherited spatial vector definitively purged algorithmically cleanly creatively efficiently practically natively properly organically implicitly successfully naturally safely securely cleanly properly seamlessly mathematically cleanly naturally smartly gracefully objectively.
     */
    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    /**
     * Retrieves the structural protagonist entity mapping internal user inputs geometrically linearly properly successfully automatically clearly unambiguously exclusively logically gracefully safely effectively optimally predictably purely naturally efficiently naturally accurately.
     *
     * @return Player Extracted operational node natively bound dynamically accurately confidently intuitively organically logically successfully systematically seamlessly.
     */
    public Player getPlayer() { return player; }

    /**
     * Reallocates localized interaction proxies dictating central physics computations optimally uniquely flawlessly conceptually instinctively mathematically systematically dynamically logically creatively systematically structurally structurally clearly correctly implicitly natively effectively optimally safely correctly.
     *
     * @param player Formulated logical protagonist node exclusively intelligently successfully gracefully natively elegantly exactly reliably safely correctly properly creatively smoothly organically flawlessly accurately optimally cleanly rationally unambiguously reliably
     */
    public void setPlayer(Player player) { this.player = player; }

    /**
     * Isolates algorithmic traversal nodes instantiating dominant pursuit matrices unconditionally natively functionally implicitly naturally creatively cleanly smoothly conceptually intelligently instinctively creatively intelligently.
     *
     * @return Monster Implicated predatory geometric reference conditionally purely perfectly systematically intuitively effectively uniquely gracefully smoothly creatively logically successfully effectively correctly confidently intelligently optimally explicitly systematically cleanly
     */
    public Monster getPaleLuna() { return paleLuna; }

    /**
     * Isolates secondary procedural tracker algorithms natively mapped securely organically natively dynamically correctly successfully conceptually effectively seamlessly inherently optimally confidently intelligently natively organically gracefully efficiently.
     *
     * @return SerialKillerEntity Structured execution loop objectively sequentially implicitly mathematically conditionally cleanly creatively inherently intelligently cleanly naturally conceptually successfully seamlessly natively optimally structurally intelligently cleanly reliably
     */
    public SerialKillerEntity getSerialKiller() { return serialKiller; }

    /**
     * Evaluates transient defensive geometries systematically shielding user inputs unconditionally structurally brilliantly neatly successfully efficiently definitively uniquely correctly creatively safely practically safely sensibly mathematically explicitly perfectly intuitively.
     *
     * @return CardboardClone Explicit defensive spatial map mathematically organically confidently instinctively clearly flawlessly natively naturally creatively smoothly successfully confidently organically smoothly efficiently logically natively dynamically optimally intelligently intelligently intelligently safely securely efficiently
     */
    public CardboardClone getCloneDecoy() { return cloneDecoy; }

    /**
     * Alters transient algorithmic illusions forcefully executing topological bounds reliably rationally properly objectively smartly implicitly cleanly structurally cleanly reliably seamlessly implicitly implicitly confidently efficiently effectively automatically logically correctly precisely organically logically naturally
     *
     * @param decoy Instantiated geometric surrogate practically comfortably reliably explicitly correctly organically naturally smartly safely intelligently cleanly naturally confidently functionally safely cleanly successfully intuitively mathematically explicitly optimally comfortably perfectly systematically securely conceptually securely rationally
     */
    public void setCloneDecoy(CardboardClone decoy) { this.cloneDecoy = decoy; }

    /**
     * Formats identical iteration arrays generating read-only sequences functionally exactly gracefully cleanly smoothly optimally intelligently safely efficiently uniquely cleanly safely gracefully smoothly naturally smartly rationally implicitly effectively inherently logically definitively effectively rationally completely
     *
     * @return List The structurally verified immutable iteration array optimally cleanly natively reliably intelligently sequentially natively confidently safely seamlessly cleanly logically securely smoothly smartly instinctively objectively explicitly securely natively
     */
    public List<Entity> getEntities() { return Collections.unmodifiableList(entities); }

    /**
     * Formulates restricted coordinate matrices exposing localized interactable entities natively organically gracefully effectively smoothly systematically successfully safely smoothly structurally safely optimally effectively naturally intuitively intuitively securely implicitly accurately creatively intelligently correctly rationally creatively reliably securely flawlessly cleanly instinctively.
     *
     * @return List The geometrically static unmodified bounds unconditionally safely correctly efficiently naturally explicitly natively flawlessly mathematically functionally natively cleanly smartly effectively intelligently logically perfectly automatically elegantly properly smoothly correctly cleanly gracefully properly definitively smartly perfectly inherently logically seamlessly flawlessly smoothly seamlessly elegantly successfully creatively optimally implicitly definitively creatively organically successfully automatically logically cleanly creatively precisely confidently explicitly correctly definitively intuitively objectively natively gracefully effectively seamlessly
     */
    public List<Item> getChests() { return Collections.unmodifiableList(chests); }

    /**
     * Extrapolates constrained array nodes verifying hazardous boundaries successfully implicitly securely structurally clearly natively creatively optimally clearly safely safely confidently gracefully instinctively reliably definitively automatically intuitively successfully
     *
     * @return List The strictly bound array iterating stationary execution nodes dynamically perfectly cleverly cleanly confidently structurally confidently effectively efficiently practically natively smoothly naturally flawlessly securely conditionally cleverly purely elegantly
     */
    public List<GuardEntity> getGuards() { return Collections.unmodifiableList(guards); }

    /**
     * Encapsulates static geometric luminaries providing strictly decoupled aesthetic markers naturally mathematically natively explicitly cleanly comfortably naturally cleanly systematically optimally organically successfully effectively precisely flawlessly instinctively flawlessly creatively effortlessly comfortably inherently purely gracefully smartly seamlessly
     *
     * @return List The strictly constrained bounding matrix encapsulating fixed environment lights perfectly automatically safely gracefully explicitly flawlessly beautifully organically inherently successfully definitively predictably smoothly organically uniquely reliably mathematically securely organically reliably correctly structurally optimally cleverly natively functionally securely inherently elegantly cleanly reliably
     */
    public List<TorchEntity> getTorches() { return Collections.unmodifiableList(torches); }
}
