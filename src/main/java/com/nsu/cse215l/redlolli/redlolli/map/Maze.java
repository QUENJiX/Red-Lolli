package com.nsu.cse215l.redlolli.redlolli.map;

import com.nsu.cse215l.redlolli.redlolli.core.Hitbox2D;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.io.InputStream;
import java.util.Queue;

/**
 * The core map system that reads level designs from CSV files and turns them
 * into a functional grid of walls, floors, and points of interest. 
 * It manages where players can walk, handles pathfinding, and keeps track 
 * of dynamic features like unlocking escape rooms.
 */
public class Maze {

    /**
     * The size of each tile in pixels. Used to calculate visual positions
     * from the raw map grid.
     */
    public static final double TILE_SIZE = 40.0;

    /**
     * The top margin added to the map so the HUD doesn't cover up
     * the top row of tiles.
     */
    public static final double Y_OFFSET = 50.0;

    private int[][] mapGrid;
    private int playerSpawnRow = 1;
    private int playerSpawnCol = 1;
    private int levelTheme = 1;

    /**
     * Finds out which visual theme (like dungeon or forest) the current
     * map is using so the renderer knows what textures to load.
     * 
     * @return An integer representing the theme ID.
     */
    public int getLevelTheme() {
        return levelTheme;
    }

    private boolean[] escapeRoomOpen;

    /**
     * Checks if the player is standing close enough to an escape room
     * door to force it open.
     * 
     * @param playerX The player's horizontal pixel position.
     * @param playerY The player's vertical pixel position.
     */
    public void updateEscapeRoomState(double playerX, double playerY) {
        if (mapGrid == null)
            return;

        // Initializes conditional Boolean bounds scaling identically to mapped escape
        // matrices explicitly
        if (escapeRoomOpen == null) {
            List<int[]> rooms = getTilesOfType(6);
            escapeRoomOpen = new boolean[rooms.size()];
        }

        int playerCol = (int) (playerX / TILE_SIZE);
        int playerRow = (int) ((playerY - Y_OFFSET) / TILE_SIZE);

        List<int[]> rooms = getTilesOfType(6);
        for (int i = 0; i < rooms.size(); i++) {
            int[] room = rooms.get(i);
            int dr = Math.abs(playerRow - room[0]);
            int dc = Math.abs(playerCol - room[1]);

            // Validates immediate topological adjacency mapping strictly localized
            // penetration radii geometrically
            escapeRoomOpen[i] = (dr <= 1 && dc <= 1);
        }
    }

    /**
     * Tells you if a specific escape room door has been propped open.
     * 
     * @param row The row where the door is supposedly located.
     * @param col The column where the door is supposedly located.
     * @return True if the door should be visually and physically open.
     */
    public boolean isEscapeRoomOpen(int row, int col) {
        if (escapeRoomOpen == null)
            return false;
        List<int[]> rooms = getTilesOfType(6);
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i)[0] == row && rooms.get(i)[1] == col) {
                return escapeRoomOpen[i];
            }
        }
        return false;
    }

    /**
     * Creates a map by reading the CSV file from your project resources.
     * 
     * @param csvFilePath The filepath to the level design file.
     */
    public Maze(String csvFilePath) {
        loadMapFromCSV(csvFilePath);
    }

    /**
     * Creates a map by reading the CSV file and overrides its visual look
     * with the specified theme.
     * 
     * @param csvFilePath The filepath to the level design file.
     * @param levelTheme  The graphics theme ID you want the game to draw.
     */
    public Maze(String csvFilePath, int levelTheme) {
        this.levelTheme = levelTheme;
        loadMapFromCSV(csvFilePath);
    }

    /**
     * The actual worker method that parses out commas and numbers from the CSV file
     * and drops them into a 2D integer array. If it can't find your file, it builds
     * a tiny fallback box so the game doesn't crash into a fiery explosion.
     * 
     * @param path The filepath string mapped directly to your assets folder.
     */
    private void loadMapFromCSV(String path) {
        List<int[]> rowList = new ArrayList<>();

        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) {
                // Oh no! File is missing. Making a tiny 6x5 fallback room so 
                // gameplay can at least technically boot.
                mapGrid = new int[][] {
                        { 1, 1, 1, 1, 1, 1 },
                        { 1, 0, 0, 0, 2, 1 },
                        { 1, 0, 1, 1, 0, 1 },
                        { 1, 0, 0, 0, 0, 1 },
                        { 1, 1, 1, 1, 1, 1 }
                };
                return;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;

            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                int[] row = new int[tokens.length];

                for (int i = 0; i < tokens.length; i++) {
                    row[i] = Integer.parseInt(tokens[i].trim());
                }
                rowList.add(row);
            }
            br.close();

            mapGrid = rowList.toArray(new int[0][]);

            for (int r = 0; r < mapGrid.length; r++) {
                for (int c = 0; c < mapGrid[r].length; c++) {
                    if (mapGrid[r][c] == 7) {
                        playerSpawnRow = r;
                        playerSpawnCol = c;

                        // Swap the spawn tile out for a normal floor tile (0) 
                        // now that we've recorded where it is.
                        mapGrid[r][c] = 0;
                    }
                }
            }

        } catch (Exception e) {
            com.nsu.cse215l.redlolli.redlolli.core.GameLogger.getLogger()
                    .log(java.util.logging.Level.SEVERE, "Failed loading or parsing map file: " + path, e);
            mapGrid = new int[][] { { 1, 1, 1 }, { 1, 0, 1 }, { 1, 1, 1 } };
        }
    }

    /**
     * Checks if a specific hitbox is crashing into any walls (tile logic ID `1`)
     * on the map grid. Helps entities stop moving if they smack into solid rock.
     * 
     * @param nextHitbox The bounds of the object to test in world coordinates.
     * @return True if the box overlaps with least one solid wall tile.
     */
    public boolean isWallCollision(Hitbox2D nextHitbox) {
        if (mapGrid == null)
            return false;

        int leftCol = (int) (nextHitbox.getMinX() / TILE_SIZE);
        int rightCol = (int) (nextHitbox.getMaxX() / TILE_SIZE);
        int topRow = (int) ((nextHitbox.getMinY() - Y_OFFSET) / TILE_SIZE);
        int bottomRow = (int) ((nextHitbox.getMaxY() - Y_OFFSET) / TILE_SIZE);

        if (leftCol < 0 || rightCol >= mapGrid[0].length || topRow < 0 || bottomRow >= mapGrid.length) {
            return true;
        }

        for (int r = topRow; r <= bottomRow; r++) {
            for (int c = leftCol; c <= rightCol; c++) {
                if (mapGrid[r][c] == 1)
                    return true;
            }
        }
        return false;
    }

    /**
     * Quickly validates if a specific rect is intersecting with an escape
     * room tile (ID `6`) by testing five tiny points inside of its hitbox.
     * 
     * @param hitbox The entity's hitbox to check.
     * @return True if the entity is brushing up against an escape room.
     */
    public boolean isEscapeRoom(Hitbox2D hitbox) {
        if (mapGrid == null)
            return false;

        double cx = (hitbox.getMinX() + hitbox.getMaxX()) / 2;
        double cy = (hitbox.getMinY() + hitbox.getMaxY()) / 2;

        // Five-point validation: Center, and top, bottom, left, and right just 
        // inside the edge. This catches overlaps nicely.
        if (isPointEscapeRoom(cx, cy) ||
                isPointEscapeRoom(hitbox.getMinX() + 2, cy) ||
                isPointEscapeRoom(hitbox.getMaxX() - 2, cy) ||
                isPointEscapeRoom(cx, hitbox.getMinY() + 2) ||
                isPointEscapeRoom(cx, hitbox.getMaxY() - 2)) {
            return true;
        }

        return false;
    }

    private boolean isPointEscapeRoom(double px, double py) {
        int col = (int) (px / TILE_SIZE);
        int row = (int) ((py - Y_OFFSET) / TILE_SIZE);
        return row >= 0 && row < mapGrid.length && col >= 0 && col < mapGrid[0].length
                && mapGrid[row][col] == 6;
    }

    /**
     * Takes an object's current position and nudges it to the closest safe 
     * floor tile around it. Really useful for getting players or items out 
     * of walls they accidentally clipped into.
     * 
     * @param hitbox The hitbox of the object you're trying to safely relocate.
     * @return An integer array [row, col] giving the safest grid coordinates to spawn/move them.
     */
    public int[] findSafeRoomDoor(Hitbox2D hitbox) {
        if (mapGrid == null)
            return null;

        double cx = (hitbox.getMinX() + hitbox.getMaxX()) / 2;
        double cy = (hitbox.getMinY() + hitbox.getMaxY()) / 2;
        int centerCol = (int) (cx / TILE_SIZE);
        int centerRow = (int) ((cy - Y_OFFSET) / TILE_SIZE);

        int[][] dirs = { { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 } };
        for (int[] d : dirs) {
            int nr = centerRow + d[0];
            int nc = centerCol + d[1];
            if (nr >= 0 && nr < mapGrid.length && nc >= 0 && nc < mapGrid[0].length
                    && mapGrid[nr][nc] != 1) {
                return new int[] { nr, nc };
            }
        }
        return new int[] { centerRow, centerCol };
    }

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
     * An implementation of Breadth-First Search (BFS) to find the shortest path
     * from one tile to another. Useful for enemies figuring out how to chase 
     * the player around corners without getting stuck.
     * 
     * @param startR  The row index where the path starts.
     * @param startC  The column index where the path starts.
     * @param targetR The destination row index you want to reach.
     * @param targetC The destination column index you want to reach.
     * @return An integer array [row, col] representing the very next step to take,
     *         or null if no path exists.
     */
    public int[] getNextMove(int startR, int startC, int targetR, int targetC) {
        if (startR == targetR && startC == targetC)
            return new int[] { startR, startC };

        int rows = mapGrid.length;
        int cols = mapGrid[0].length;

        if (startR < 0 || startR >= rows || startC < 0 || startC >= cols) {
            return null;
        }

        boolean[][] visited = new boolean[rows][cols];
        Queue<Node> queue = new LinkedList<>();

        queue.add(new Node(startR, startC, null));
        visited[startR][startC] = true;

        int[] dr = { -1, 1, 0, 0 };
        int[] dc = { 0, 0, -1, 1 };
        Node targetNode = null;

        while (!queue.isEmpty()) {
            Node curr = queue.poll();

            if (curr.r == targetR && curr.c == targetC) {
                targetNode = curr;
                break;
            }

            for (int i = 0; i < 4; i++) {
                int nr = curr.r + dr[i];
                int nc = curr.c + dc[i];

                if (nr >= 0 && nr < rows && nc >= 0 && nc < cols
                        && !visited[nr][nc] && mapGrid[nr][nc] != 1) {

                    visited[nr][nc] = true;
                    queue.add(new Node(nr, nc, curr));
                }
            }
        }

        if (targetNode == null)
            return null;

        Node step = targetNode;
        while (step.parent != null && step.parent.parent != null) {
            step = step.parent;
        }

        return new int[] { step.r, step.c };
    }

    /**
     * Shoots an imaginary laser beam from one point to another to see if there are 
     * any walls blocking the view. Useful for determining if an enemy can actually 
     * see you from across the room.
     * 
     * @param x1 The starting horizontal position (e.g. an enemy's X).
     * @param y1 The starting vertical position (e.g. an enemy's Y).
     * @param x2 The target horizontal position (e.g. your X).
     * @param y2 The target vertical position (e.g. your Y).
     * @return True if the path between these points is completely unobstructed.
     */
    public boolean hasLineOfSight(double x1, double y1, double x2, double y2) {
        if (mapGrid == null)
            return false;

        double distX = Math.abs(x2 - x1);
        double distY = Math.abs(y2 - y1);
        double maxDist = Math.max(distX, distY);

        int steps = (int) Math.ceil(maxDist / (TILE_SIZE / 2.0));

        for (int i = 0; i <= steps; i++) {
            double t = (steps == 0) ? 0 : (double) i / steps;

            double cx = x1 + t * (x2 - x1);
            double cy = y1 + t * (y2 - y1);

            int col = (int) Math.floor(cx / TILE_SIZE);
            int row = (int) Math.floor((cy - Y_OFFSET) / TILE_SIZE);

            if (row >= 0 && row < mapGrid.length && col >= 0 && col < mapGrid[0].length) {
                if (mapGrid[row][col] == 1)
                    return false;
            }
        }

        return true;
    }

    /**
     * Sweeps through the entire map array to collect every single tile
     * that corresponds to the given tile ID (e.g. finding all doors, generators, etc.).
     * 
     * @param type The specific tile ID you're looking for.
     * @return A list of arrays [row, col] representing every location of this tile.
     */
    public List<int[]> getTilesOfType(int type) {
        List<int[]> positions = new ArrayList<>();
        if (mapGrid == null) {
            return positions;
        }

        for (int r = 0; r < mapGrid.length; r++) {
            for (int c = 0; c < mapGrid[r].length; c++) {
                if (mapGrid[r][c] == type) {
                    positions.add(new int[] { r, c });
                }
            }
        }
        return positions;
    }

    /**
     * Converts a world coordinate into its corresponding grid position and
     * returns the type of tile stored there in the map grid.
     * 
     * @param worldX The horizontal pixel position in the game world.
     * @param worldY The vertical pixel position in the game world.
     * @return The integer ID representing what kind of tile this is, or -1 if the position is off map.
     */
    public int getTileAt(double worldX, double worldY) {
        int col = (int) (worldX / TILE_SIZE);
        int row = (int) ((worldY - Y_OFFSET) / TILE_SIZE);

        if (mapGrid == null || row < 0 || row >= mapGrid.length || col < 0 || col >= mapGrid[0].length) {
            return -1;
        }

        return mapGrid[row][col];
    }

    /**
     * Converts standard pixel coordinates into row and column indices for 
     * the underlying map array. Handy if you need to know exactly which 
     * tile an entity is hovering over.
     * 
     * @param worldX A horizontal pixel position.
     * @param worldY A vertical pixel position.
     * @return A two-element integer array [row, col], or null if out-of-bounds.
     */
    public int[] getTilePositionAt(double worldX, double worldY) {
        int col = (int) (worldX / TILE_SIZE);
        int row = (int) ((worldY - Y_OFFSET) / TILE_SIZE);

        if (mapGrid == null || row < 0 || row >= mapGrid.length || col < 0 || col >= mapGrid[0].length) {
            return null;
        }

        return new int[] { row, col };
    }

    /**
     * Loops through the entire level and turns every single escape room 
     * door (tile ID `6`) into a normal, non-blocking floor space (tile ID `0`).
     * Time to leave!
     */
    public void collapseEscapeRooms() {
        if (mapGrid == null) {
            return;
        }

        for (int r = 0; r < mapGrid.length; r++) {
            for (int c = 0; c < mapGrid[r].length; c++) {
                if (mapGrid[r][c] == 6) {
                    mapGrid[r][c] = 0;
                }
            }
        }
    }

    /**
     * Gets read-only access to the entire 2D map array so the game
     * engine knows where to draw the ground, walls, and so on.
     * 
     * @return The 2D integer array containing the active map level.
     */
    public int[][] getMapGrid() {
        return mapGrid;
    }

    /**
     * Finds the vertical starting location the player begins at 
     * based on the level map's spawn tile (ID `7`).
     * 
     * @return The integer row where the player spawns.
     */
    public int getPlayerSpawnRow() {
        return playerSpawnRow;
    }

    /**
     * Finds the horizontal starting location the player begins at 
     * based on the level map's spawn tile (ID `7`).
     * 
     * @return The integer column where the player spawns.
     */
    public int getPlayerSpawnCol() {
        return playerSpawnCol;
    }
}
