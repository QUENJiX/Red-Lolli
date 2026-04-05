package com.nsu.cse215l.redlolli.redlolli.map;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Loads, stores, and renders the maze grid from CSV files.
 * Provides collision detection, escape room queries, BFS pathfinding, and line-of-sight checks.
 *
 * <p>Tile types:
 * <ul>
 *   <li>0 — Floor (walkable)</li>
 *   <li>1 — Wall (solid)</li>
 *   <li>2, 3 — Chest spawn positions (converted to floor after entity spawn)</li>
 *   <li>5 — Pale Luna spawn (converted to floor after entity spawn)</li>
 *   <li>6 — Escape room / safe zone</li>
 *   <li>7 — Player spawn (converted to floor after loading)</li>
 * </ul>
 * 
 * Development History:
 * - Phase 1, Week 1, Day 3: Parsing the CSV and generating wall/floor tiles.
 * - Phase 1, Week 1, Day 7: Refining wall collision checks.
 * - Phase 3, Week 3, Day 18: Dynamic reloading of different CSV files.
 * - Phase 3, Week 3, Day 20: General refactoring across Maze.java, Player.java, and Entity.java.
 */
public class Maze {

    // ========================= CONSTANTS =========================

    public static final double TILE_SIZE = 40.0;
    public static final double Y_OFFSET = 50.0;

    // ========================= FIELDS =========================

    private int[][] mapGrid;
    private int playerSpawnRow = 1;
    private int playerSpawnCol = 1;
    private int levelTheme = 1;

    // ========================= CONSTRUCTOR =========================

    public Maze(String csvFilePath) {
        loadMapFromCSV(csvFilePath);
    }

    public Maze(String csvFilePath, int levelTheme) {
        this.levelTheme = levelTheme;
        loadMapFromCSV(csvFilePath);
    }

    // ========================= MAP LOADING =========================

    /** 
     * Loads the maze grid from a CSV resource file, detecting player spawn and building the grid. 
     * Reads line-by-line, splitting on commas, and structuring it into a 2D integer array.
     * 
     * @param path The relative classpath path to the CSV file (e.g., "/map.csv")
     */
    private void loadMapFromCSV(String path) {
        List<int[]> rowList = new ArrayList<>();

        try {
            // Attempt to open the map file stream from the resources folder
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) {
                // If map is missing, print error and load a hardcoded fallback 5x6 map to prevent crashing
                System.err.println("Map file not found: " + path + ". Loading fallback map.");
                mapGrid = new int[][]{
                        {1, 1, 1, 1, 1, 1},
                        {1, 0, 0, 0, 2, 1},
                        {1, 0, 1, 1, 0, 1},
                        {1, 0, 0, 0, 0, 1},
                        {1, 1, 1, 1, 1, 1}
                };
                return; // Exit early since we used fallback
            }

            // Wrap the input stream in a reader to read the file string line by line
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            
            // Loop through every line until EOF
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(","); // Separate values by comma
                int[] row = new int[tokens.length];
                
                // Parse each string token into an integer representation of the tile
                for (int i = 0; i < tokens.length; i++) {
                    row[i] = Integer.parseInt(tokens[i].trim());
                }
                rowList.add(row); // Add the processed row to our dynamic list
            }
            br.close(); // Clean up resources

            // Convert our dynamic List format into a fixed 2D array structure
            mapGrid = rowList.toArray(new int[0][]);

            // Scan the entire loaded grid to locate the specific Player spawn tile (Tile 7)
            for (int r = 0; r < mapGrid.length; r++) {
                for (int c = 0; c < mapGrid[r].length; c++) {
                    if (mapGrid[r][c] == 7) {
                        playerSpawnRow = r; // Register row location
                        playerSpawnCol = c; // Register column location
                        mapGrid[r][c] = 0;  // Immeditaely convert to walkable floor (Tile 0) to avoid rendering issues
                    }
                }
            }

        } catch (Exception e) {
            // Catch any I/O or parsing errors and supply a basic 3x3 box map to guarantee stability
            e.printStackTrace();
            mapGrid = new int[][]{{1, 1, 1}, {1, 0, 1}, {1, 1, 1}};
        }
    }

    // ========================= RENDERING =========================

    /** 
     * Renders the entire 2D array maze grid over the canvas, matching specific integer values
     * to different visual styles to create walls, floors, or interactive tiles. 
     * 
     * @param gc The FXGL JavaFX active GraphicsContext used to push drawing operations.
     */
    public void renderMaze(GraphicsContext gc) {
        // Return quickly if loading failed before we try drawing null bounds
        if (mapGrid == null) return;

        // Establish boundaries for border rendering checks
        int maxRow = mapGrid.length - 1;
        int maxCol = mapGrid[0].length - 1;

        // Iterate line by line (Y direction), then column by column (X direction)
        for (int row = 0; row < mapGrid.length; row++) {
            for (int col = 0; col < mapGrid[row].length; col++) {
                // Calculate actual physical pixel coordinates on the screen based on scale
                double tileX = col * TILE_SIZE; 
                double tileY = row * TILE_SIZE + Y_OFFSET; // Adding Y-offset to push UI overlay down
                
                int tile = mapGrid[row][col]; 
                
                // Identify edges of map to assign outer border textures versus internal walls
                boolean isBorder = (row == 0 || row == maxRow || col == 0 || col == maxCol);

                // Type 1: Wall logic handling inner designs versus outer edges
                if (tile == 1) {
                    if (isBorder) {
                        renderBorderWall(gc, tileX, tileY);
                    } else {
                        renderInnerWall(gc, tileX, tileY);
                    }

                // Type 6: The defined Goal/Escape room tile which is normally green
                } else if (tile == 6) {
                    renderEscapeRoom(gc, tileX, tileY);
                    
                // Type 9: A graphical decore tile resembling blood-spatters 
                } else if (tile == 9) {
                    renderBloodTrail(gc, tileX, tileY, row, col);
                    
                // Type 8: Special easter-egg or prompt tile from the developers
                } else if (tile == 8) {
                    renderDeveloperNoteTile(gc, tileX, tileY);
                    
                // Default: Fallback to rendering empty floor
                } else {
                    renderFloor(gc, tileX, tileY, row, col);
                }
            }
        }
    }

    /** 
     * Renders a dark outer border wall tile representing impenetrable outer limits.
     * Uses stone texture lines to denote heavy barricades.
     * 
     * @param gc The current GraphicsContext.
     * @param x  Exact starting pixel X coordinate for the tile.
     * @param y  Exact starting pixel Y coordinate for the tile.
     */
    private void renderBorderWall(GraphicsContext gc, double x, double y) {
        // Change colors drastically based on the current level theme flag
        if (levelTheme == 1) {
            gc.setFill(Color.rgb(16, 28, 16));        // Dark Forest theme
        } else if (levelTheme == 2) {
            gc.setFill(Color.rgb(20, 18, 18));        // Dark Red/Blood Room theme
        } else {
            gc.setFill(Color.rgb(22, 20, 24));        // Deep Purple/Midnight theme
        }
        // Base fill tile square
        gc.fillRect(x, y, TILE_SIZE, TILE_SIZE);

        // Render dividing horizontal stonework mortar lines inside the tile
        gc.setStroke(Color.rgb(30, 25, 32));
        gc.setLineWidth(0.5);
        gc.strokeLine(x, y + TILE_SIZE * 0.33, x + TILE_SIZE, y + TILE_SIZE * 0.33); // Top third
        gc.strokeLine(x, y + TILE_SIZE * 0.66, x + TILE_SIZE, y + TILE_SIZE * 0.66); // Bottom third

        // Add a bounding stroke for definition 
        gc.setStroke(Color.rgb(10, 8, 12));
        gc.setLineWidth(1);
        gc.strokeRect(x, y, TILE_SIZE, TILE_SIZE);
    }

    /** 
     * Renders an inner maze wall tile equipped with shifting brick patterns, mortar lines, 
     * and specific shadow edges to simulate 3D depth geometry.
     * 
     * @param gc The active GraphicsContext.
     * @param x  Initial x-axis position.
     * @param y  Initial y-axis position.
     */
    private void renderInnerWall(GraphicsContext gc, double x, double y) {
        // Evaluate level theme colors the same way borders do, but lighter
        if (levelTheme == 1) {
            gc.setFill(Color.rgb(32, 60, 34)); // Standard green
        } else if (levelTheme == 2) {
            gc.setFill(Color.rgb(55, 40, 48)); // Fleshy red 
        } else {
            gc.setFill(Color.rgb(48, 46, 52)); // Purplish grey
        }
        gc.fillRect(x, y, TILE_SIZE, TILE_SIZE);

        // Mortar lines separating the visual bricks 
        gc.setStroke(Color.rgb(38, 28, 35));
        gc.setLineWidth(0.7);
        // Middle horizontal split line
        gc.strokeLine(x, y + TILE_SIZE * 0.5, x + TILE_SIZE, y + TILE_SIZE * 0.5);
        // Top half vertical split
        gc.strokeLine(x + TILE_SIZE * 0.5, y, x + TILE_SIZE * 0.5, y + TILE_SIZE * 0.5);
        // Bottom half split 1
        gc.strokeLine(x + TILE_SIZE * 0.25, y + TILE_SIZE * 0.5, x + TILE_SIZE * 0.25, y + TILE_SIZE);
        // Bottom half split 2
        gc.strokeLine(x + TILE_SIZE * 0.75, y + TILE_SIZE * 0.5, x + TILE_SIZE * 0.75, y + TILE_SIZE);

        // Render light highlight strokes on the top and left to mimic a lightsource reflecting
        gc.setStroke(Color.rgb(70, 52, 62));
        gc.setLineWidth(1);
        gc.strokeLine(x + 1, y + 1, x + TILE_SIZE - 1, y + 1);             // Top highlight
        gc.strokeLine(x + 1, y + 1, x + 1, y + TILE_SIZE - 1);             // Left highlight

        // Render dark shadow strokes on the bottom and right to simulate depth drop-off
        gc.setStroke(Color.rgb(30, 20, 28));
        gc.strokeLine(x + TILE_SIZE - 1, y + 1, x + TILE_SIZE - 1, y + TILE_SIZE - 1); // Right shadow
        gc.strokeLine(x + 1, y + TILE_SIZE - 1, x + TILE_SIZE - 1, y + TILE_SIZE - 1); // Bottom shadow
    }

    /** 
     * Renders a green escape room (safe zone/exit) tile.
     * Denoted by a prominent 'S' (Safe) letter. Reaching this progresses the level.
     * 
     * @param gc The GraphicsContext to draw on.
     * @param x  Exact X coordinate for the tile.
     * @param y  Exact Y coordinate for the tile.
     */
    private void renderEscapeRoom(GraphicsContext gc, double x, double y) {
        // Change colors depending on Level 3 (theme 3) which adopts a darker palette 
        gc.setFill(levelTheme == 3 ? Color.rgb(26, 26, 26) : Color.rgb(12, 30, 12));
        gc.fillRect(x, y, TILE_SIZE, TILE_SIZE);
        
        // Define bounding box strokes for the S frame
        gc.setStroke(levelTheme == 3 ? Color.rgb(80, 20, 20) : Color.rgb(0, 80, 0));
        gc.setLineWidth(1);
        gc.strokeRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
        
        // Draw the text overlay itself over an inner, transparent darkened square
        gc.setFill(levelTheme == 3 ? Color.rgb(140, 40, 40, 0.6) : Color.rgb(0, 100, 0, 0.6));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gc.fillText("S", x + 14, y + 27);
    }

    /** 
     * Renders a floor tile. It calculates a checkerboard offset based on its row
     * and col coordinates, assigning a slightly alternating dark shade to differentiate tiles.
     * 
     * @param gc  The current GraphicsContext.
     * @param x   Base x rendering position.
     * @param y   Base y rendering position.
     * @param row Grid row to calculate checkboard offset.
     * @param col Grid column to calculate checkerboard offset.
     */
    private void renderFloor(GraphicsContext gc, double x, double y, int row, int col) {
        // Modulo check to determine if the tile is technically 'even' or 'odd' in map space
        boolean checker = (row + col) % 2 == 0;
        
        // Vary the alternating floor colors based on theme integer
        if (levelTheme == 1) {
            gc.setFill(checker ? Color.rgb(16, 24, 18) : Color.rgb(18, 28, 20));
        } else if (levelTheme == 2) {
            gc.setFill(checker ? Color.rgb(18, 18, 22) : Color.rgb(22, 22, 28));
        } else {
            gc.setFill(checker ? Color.rgb(22, 22, 24) : Color.rgb(28, 28, 32));
        }
        
        // Push the fill instruction, then outline slightly
        gc.fillRect(x, y, TILE_SIZE, TILE_SIZE);
        gc.setStroke(Color.rgb(30, 30, 36));
        gc.setLineWidth(0.3);
        gc.strokeRect(x, y, TILE_SIZE, TILE_SIZE);

        // Render minor details (rocks or cracks) depending on the environment theme
        if (levelTheme == 1) {
            // Draw small rock circles
            gc.setFill(Color.rgb(26, 44, 28, 0.45));
            gc.fillOval(x + 8, y + 10, 4, 3);
            gc.fillOval(x + 26, y + 24, 5, 4);
        } else {
            // Draw hairline cracks instead of rocks
            gc.setStroke(Color.rgb(12, 12, 16, 0.45));
            gc.setLineWidth(0.6);
            gc.strokeLine(x + 6, y + 10, x + 20, y + 16);
            gc.strokeLine(x + 24, y + 28, x + 34, y + 34);
        }
    }

    /**
     * Specialized dev-placed tile mimicking a written prompt.
     * 
     * @param gc Canvas contextual access.
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    private void renderDeveloperNoteTile(GraphicsContext gc, double x, double y) {
        // Flat dark background
        gc.setFill(Color.rgb(20, 20, 24));
        gc.fillRect(x, y, TILE_SIZE, TILE_SIZE);
        
        // Subtle grey border
        gc.setStroke(Color.rgb(120, 120, 140, 0.5));
        gc.setLineWidth(1);
        gc.strokeRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
        
        // Draw the text string "NOTE" prominently in the middle
        gc.setFill(Color.rgb(150, 150, 170, 0.7));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        gc.fillText("NOTE", x + 7, y + 24);
    }

    /**
     * Specialized tile mimicking blood spatters that rests on top of normal floor drawing.
     */
    private void renderBloodTrail(GraphicsContext gc, double x, double y, int row, int col) {
        // Redraw standard floor tile underneath first
        renderFloor(gc, x, y, row, col);
        
        // Overlay slightly transparent blood-red oval shapes
        gc.setFill(Color.rgb(100, 0, 0, 0.5));
        gc.fillOval(x + 7, y + 13, 18, 8);
        gc.fillOval(x + 18, y + 20, 10, 6);
    }



    // ========================= COLLISION DETECTION =========================

    /** 
     * Converts a generic pixel hitbox into grid coordinates and checks if any 
     * corner or edge overlaps with a hard wall tile in the array. 
     * 
     * @param nextHitbox The projected future Rectangle2D bounding box of an entity.
     * @return true if the movement causes an overlap with a wall (type 1 or type 10).
     */
    public boolean isWallCollision(Rectangle2D nextHitbox) {
        if (mapGrid == null) return false; // Early return to prevent hard crashes

        // Divide the raw pixel coordinates by tile size (e.g., 40.0) to get array indices
        // Also subtracts the UI overlay Y_OFFSET to get true local space
        int leftCol   = (int) (nextHitbox.getMinX() / TILE_SIZE);
        int rightCol  = (int) (nextHitbox.getMaxX() / TILE_SIZE);
        // Using MinY vs MaxY mapping offsets
        int topRow    = (int) ((nextHitbox.getMinY() - Y_OFFSET) / TILE_SIZE);
        int bottomRow = (int) ((nextHitbox.getMaxY() - Y_OFFSET) / TILE_SIZE);

        // Disallow moving entirely outside map boundaries (out-of-bounds protection)
        if (leftCol < 0 || rightCol >= mapGrid[0].length || topRow < 0 || bottomRow >= mapGrid.length) {
            return true;
        }

        // Loop over the bounded box area in grid space
        // This handles cases where an entity is large enough to span multiple tiles
        for (int r = topRow; r <= bottomRow; r++) {
            for (int c = leftCol; c <= rightCol; c++) {
                // If any overlapped cell contains a wall (1) or a specific barricade type (10)
                if (mapGrid[r][c] == 1 || mapGrid[r][c] == 10) return true;
            }
        }
        return false; // No wall overlap detected
    }

    /** 
     * Computes whether the center mass of an entity box lies over an Escape point.
     * 
     * @param playerHitbox the primary bounds of the entity interacting with the escape room.
     * @return true if the literal center pixel of the hitbox stands on tile type 6.
     */
    public boolean isEscapeRoom(Rectangle2D playerHitbox) {
        if (mapGrid == null) return false;

        // Obtain center point coordinates instead of full edges
        int centerX = (int) ((playerHitbox.getMinX() + playerHitbox.getMaxX()) / 2);
        int centerY = (int) ((playerHitbox.getMinY() + playerHitbox.getMaxY()) / 2);

        // Normalize center point into grid indices
        int col = centerX / (int) TILE_SIZE;
        int row = (int) ((centerY - Y_OFFSET) / TILE_SIZE);

        // Prevent checking out of bounds array space
        if (col < 0 || col >= mapGrid[0].length || row < 0 || row >= mapGrid.length) return false;
        
        // Ensure it's definitively checking for the '6' code
        return mapGrid[row][col] == 6;
    }

    // ========================= PATHFINDING (BFS) =========================

    /** 
     * Internal data structure representing a Breadth-First Search node.
     * Tracks the specific grid row/col and holds a reference to its 'parent' node.
     * This parent chaining allows backtracking the optimal path from destination to start.
     */
    private static class Node {
        int r, c;
        Node parent;
        Node(int r, int c, Node parent) {
            this.r = r;
            this.c = c;
            this.parent = parent;
        }
    }

    /**
     * Executes a Breadth-First Search (BFS) starting from the enemy's grid position
     * extending outward until the player's target position is found. Because BFS evaluates
     * iteratively level-by-level, the first path found is mathematically guaranteed
     * to be the shortest unweighted path in the grid.
     *
     * @param startR Enemy current row.
     * @param startC Enemy current column.
     * @param targetR Player target row.
     * @param targetC Player target column.
     * @return [row, col] integer array of the immediate next optimal step, or null if trapped.
     */
    public int[] getNextMove(int startR, int startC, int targetR, int targetC) {
        // If we are already there, don't move
        if (startR == targetR && startC == targetC) return new int[]{startR, startC};

        int rows = mapGrid.length;
        int cols = mapGrid[0].length;
        boolean[][] visited = new boolean[rows][cols];
        Queue<Node> queue = new LinkedList<>();

        // Enqueue the root parent node
        queue.add(new Node(startR, startC, null));
        visited[startR][startC] = true;

        // Defined directional vectors: Up, Down, Left, Right (No diagonals allowed)
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
        Node targetNode = null;

        // Standard Queue exhaustion loop
        while (!queue.isEmpty()) {
            Node curr = queue.poll();
            
            // If the popped node aligns with the targeted player position, we found the path
            if (curr.r == targetR && curr.c == targetC) {
                targetNode = curr;
                break;
            }
            
            // Check all 4 orthogonal neighbors around the current node
            for (int i = 0; i < 4; i++) {
                int nr = curr.r + dr[i];
                int nc = curr.c + dc[i];
                
                // Boundaries check && skip if already visited && skip walls
                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                        && !visited[nr][nc] && mapGrid[nr][nc] != 1 && mapGrid[nr][nc] != 10) {
                    
                    visited[nr][nc] = true; // Mark as seen so we don't enqueue it again
                    // Enqueue the valid neighbor, linking it back to 'curr' as its parent
                    queue.add(new Node(nr, nc, curr));
                }
            }
        }

        // Return null if search was completely blocked and no path to player exists
        if (targetNode == null) return null;

        // Traverse the 'parent' links backwards from the Player's position 
        // until we reach the step IMMEDIATELY following the Enemy's start coordinate.
        Node step = targetNode;
        while (step.parent != null && step.parent.parent != null) {
            step = step.parent;
        }
        
        // Return this solitary first step vector
        return new int[]{step.r, step.c};
    }

    // ========================= LINE OF SIGHT =========================

    /** 
     * Computes a raycast from one continuous pixel coordinate to another to verify 
     * if the vision cone is interrupted by a wall block. Used by the GuardEntity and Flashlight.
     * 
     * @param x1 Origin X.
     * @param y1 Origin Y.
     * @param x2 Target X.
     * @param y2 Target Y.
     * @return true if ray reaches target without crossing a tile '1'. 
     */
    public boolean hasLineOfSight(double x1, double y1, double x2, double y2) {
        if (mapGrid == null) return false;

        // OPTIMIZATION: Use Chebyshev distance instead of Math.hypot (removes square root overhead)
        double distX = Math.abs(x2 - x1);
        double distY = Math.abs(y2 - y1);
        double maxDist = Math.max(distX, distY);

        // FIX: Force floating point division using 2.0. Use Math.ceil to ensure enough sub-steps.
        // We step half a tile size to ensure we don't accidentally skip over thin walls.
        int steps = (int) Math.ceil(maxDist / (TILE_SIZE / 2.0));

        // Evaluate interpolation steps across the line 't'
        for (int i = 0; i <= steps; i++) {
            double t = (steps == 0) ? 0 : (double) i / steps;
            
            // Linear interpolation calculate point along the ray
            double cx = x1 + t * (x2 - x1);
            double cy = y1 + t * (y2 - y1);

            // FIX: Use Math.floor to properly handle negative coordinates/offsets
            int col = (int) Math.floor(cx / TILE_SIZE);
            int row = (int) Math.floor((cy - Y_OFFSET) / TILE_SIZE);

            // Test if the tested segment touches a wall code
            if (row >= 0 && row < mapGrid.length && col >= 0 && col < mapGrid[0].length) {
                if (mapGrid[row][col] == 1 || mapGrid[row][col] == 10) return false; // Vision block!
            }
        }
        
        return true; // The entire ray completed traversing without colliding
    }

    /**
     * Scans the entire 2D map array for a specific tile type code.
     * Often used to locate chest spawns or entity spawn points before gameplay begins.
     * 
     * @param type The integer value of the target tile (e.g. 5 for Luna, 2 for Chests).
     * @return A list of coordinate integer arrays [row, col] where that tile exists.
     */
    public List<int[]> getTilesOfType(int type) {
        List<int[]> positions = new ArrayList<>();
        if (mapGrid == null) {
            return positions; // Return empty list rather than null if uninitialized
        }
        
        // Loop completely through the map grid looking for matches
        for (int r = 0; r < mapGrid.length; r++) {
            for (int c = 0; c < mapGrid[r].length; c++) {
                if (mapGrid[r][c] == type) {
                    positions.add(new int[]{r, c}); // Store matched coordinates
                }
            }
        }
        return positions;
    }

    /**
     * Safely translates an entity's absolute world X/Y (pixel) coordinates into the layout grid.
     * Validates index limits and fetches the actual tile code the entity is currently standing over.
     * 
     * @param worldX Target X pixel position in the scene.
     * @param worldY Target Y pixel position in the scene.
     * @return The specific integer representing the tile type, or -1 if out of bounds.
     */
    public int getTileAt(double worldX, double worldY) {
        // Translate world X into column array index
        int col = (int) (worldX / TILE_SIZE);
        // Translate world Y into row array index, factoring in the HUD overlay offset
        int row = (int) ((worldY - Y_OFFSET) / TILE_SIZE);
        
        // Check grid boundary safely before fetching index
        if (mapGrid == null || row < 0 || row >= mapGrid.length || col < 0 || col >= mapGrid[0].length) {
            return -1;
        }
        
        return mapGrid[row][col];
    }

    /**
     * Convenience method to map physical world positions to the structural indexing of the internal grid.
     * 
     * @param worldX Target X pixel position in the scene.
     * @param worldY Target Y pixel position in the scene.
     * @return Raw index formatted as an integer array [row, col], or null if invalid/out-of-bounds.
     */
    public int[] getTilePositionAt(double worldX, double worldY) {
        int col = (int) (worldX / TILE_SIZE);
        int row = (int) ((worldY - Y_OFFSET) / TILE_SIZE);
        
        // Boundary checking to avoid NullPointer or ArrayIndexOutOfBounds 
        if (mapGrid == null || row < 0 || row >= mapGrid.length || col < 0 || col >= mapGrid[0].length) {
            return null;
        }
        
        return new int[]{row, col};
    }

    /**
     * Iterates over the grid and forcefully modifies every discovered Escape Room tile (type 6) 
     * into a normal Floor tile (type 0). Generally called to dynamically lock the player 
     * inside the map under specific game conditions. 
     */
    public void collapseEscapeRooms() {
        if (mapGrid == null) {
            return;
        }
        
        // Traverse the grid line by line
        for (int r = 0; r < mapGrid.length; r++) {
            for (int c = 0; c < mapGrid[r].length; c++) {
                // If it's an escape tile code, revert it to a standard walkable code
                if (mapGrid[r][c] == 6) {
                    mapGrid[r][c] = 0;
                }
            }
        }
    }

    // ========================= GETTERS =========================

    /** @return Exposes the direct map footprint reference */
    public int[][] getMapGrid()    { return mapGrid; }
    
    /** @return Standardized player row entry generated during map load phase */
    public int getPlayerSpawnRow() { return playerSpawnRow; }
    
    /** @return Standardized player col entry generated during map load phase */
    public int getPlayerSpawnCol() { return playerSpawnCol; }
}