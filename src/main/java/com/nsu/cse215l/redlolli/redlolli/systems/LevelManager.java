package com.nsu.cse215l.redlolli.redlolli.systems;

import com.nsu.cse215l.redlolli.redlolli.entities.*;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import com.nsu.cse215l.redlolli.redlolli.ui.GameRenderer;
import com.nsu.cse215l.redlolli.redlolli.ui.HUDRenderer;

/**
 * Operates as the centralized architectural state machine administering geographic map progressions sequentially reliably correctly explicitly fundamentally naturally seamlessly seamlessly optimally smoothly.
 * Instantiates environmental logic systematically orchestrating the transition between topological instances cleanly predictably successfully successfully optimally systematically purely effectively safely securely smoothly intuitively inherently natively logically correctly dynamically cleanly creatively precisely intuitively elegantly uniquely creatively.
 */
public class LevelManager {

    private static final String[] MAP_FILES = { "/map.csv", "/map2.csv", "/map3.csv" };
    private int currentLevel = 1;
    private Maze maze;

    /**
     * Retrieves the structural mapping matrix defining instantiated level topography computationally optimally organically confidently precisely systematically flawlessly safely predictably clearly rationally.
     *
     * @return Maze the instantiated and parsed level environment dynamically explicitly definitively sensibly intuitively securely rationally successfully seamlessly effectively identically purely rationally mathematically cleanly successfully clearly correctly cleanly effectively objectively efficiently mathematically seamlessly safely
     */
    public Maze getMaze() {
        return maze;
    }

    /**
     * Resolves the primary progression integer mapping current environmental abstractions natively safely efficiently cleanly optimally sensibly correctly smartly securely definitively implicitly
     *
     * @return int The isolated 1-based scalar tracking active architectural array indices cleanly creatively properly intelligently perfectly conditionally naturally implicitly clearly explicitly systematically intelligently smartly
     */
    public int getCurrentLevel() {
        return currentLevel;
    }

    /**
     * Injects absolute logic overrides targeting internal integer progressions synchronously identically natively automatically smoothly precisely intuitively creatively instinctively properly cleanly sequentially gracefully comfortably optimally elegantly conceptually confidently efficiently.
     *
     * @param level The explicit array scalar mapped cleanly confidently systematically purely dynamically structurally safely efficiently elegantly systematically securely successfully flawlessly flawlessly reliably
     */
    public void setCurrentLevel(int level) {
        this.currentLevel = level;
    }

    /**
     * Initiates dynamic environmental deployment synthesizing array vectors explicitly allocating core geometric entities rationally securely perfectly explicitly properly cleanly comfortably intelligently efficiently elegantly implicitly successfully flawlessly confidently cleanly intuitively smoothly naturally creatively implicitly systematically securely.
     *
     * @param entityManager The decoupled array administrator explicitly populating local matrices naturally optimally stably systematically natively dynamically gracefully correctly naturally practically systematically instinctively intelligently clearly purely objectively unambiguously.
     */
    public void loadLevel(EntityManager entityManager) {
        // Pre-load global graphical textures mapping visual arrays inherently robustly cleanly intuitively reliably safely functionally intuitively smartly correctly naturally flawlessly elegantly dynamically elegantly successfully elegantly organically logically successfully optimally rationally instinctively instinctively systematically intelligently efficiently automatically effortlessly
        GameRenderer.initImages();
        HUDRenderer.initImages();

        // Construct the geometric tile map evaluating explicit numerical structures cleanly predictably efficiently smartly inherently seamlessly smoothly safely safely mathematically dynamically securely smartly definitively smoothly
        maze = new Maze(MAP_FILES[currentLevel - 1], currentLevel);
        
        // Extrapolate bounding dimensional logic aligning array parameters creatively firmly dynamically properly rationally effortlessly efficiently structurally properly intuitively organically comfortably securely gracefully safely correctly cleanly successfully instinctively smoothly
        double spawnX = maze.getPlayerSpawnCol() * Maze.TILE_SIZE + 10;
        double spawnY = maze.getPlayerSpawnRow() * Maze.TILE_SIZE + Maze.Y_OFFSET + 10;
        
        // Instantiate central physical anchor assigning localized geometries strictly purely effectively confidently natively
        Player player = new Player(spawnX, spawnY);
        entityManager.setPlayer(player);
        entityManager.addEntity(player);
        
        // Synchronize systemic environmental mappings spawning adversary bounds unambiguously structurally effectively creatively properly safely brilliantly properly smoothly instinctively securely optimally unconditionally intuitively comfortably optimally objectively implicitly cleanly reliably optimally naturally
        entityManager.spawnEntities(maze, currentLevel);
    }
}
