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
 * Loads, stores, and renders the maze grid from CSV files.
 * Provides collision detection, escape room queries, BFS pathfinding, and
 * line-of-sight checks.
 */
public class Maze {

    public static final double TILE_SIZE = 40.0;
    public static final double Y_OFFSET = 50.0;

    private int[][] mapGrid;
    private int playerSpawnRow = 1;
    private int playerSpawnCol = 1;
    private int levelTheme = 1;

    public int getLevelTheme() {
        return levelTheme;
    }

    // Instance state for escape room tracking
    private boolean[] escapeRoomOpen;
    /**
     * Updates which escape rooms are "open" based on player position. Call every
     * frame.
     */
    public void updateEscapeRoomState(double playerX, double playerY) {
        if (mapGrid == null)
            return;

        // Initialize escape room tracking on first call
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
            // Player is "inside" if within 1 tile of the escape room
            escapeRoomOpen[i] = (dr <= 1 && dc <= 1);
        }
    }

    /**
     * Returns true if the escape room at (row, col) is currently open (player
     * inside).
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

    // ================= CONSTRUCTORS =================

    public Maze(String csvFilePath) {
        loadMapFromCSV(csvFilePath);
    }

    public Maze(String csvFilePath, int levelTheme) {
        this.levelTheme = levelTheme;
        loadMapFromCSV(csvFilePath);
    }

    // ================= MAP LOADING =================

    private void loadMapFromCSV(String path) {
        List<int[]> rowList = new ArrayList<>();

        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) {
                // Map file not found: Loading fallback map.
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
                        mapGrid[r][c] = 0;
                    }
                }
            }

        } catch (Exception e) {
            mapGrid = new int[][] { { 1, 1, 1 }, { 1, 0, 1 }, { 1, 1, 1 } };
        }
    }

    // ================= COLLISION & QUERIES =================

    /** Returns true if the projected hitbox overlaps any wall tile. */
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
     * Returns true if any part of the hitbox overlaps an escape-room tile
     * (multi-point check).
     */
    public boolean isEscapeRoom(Hitbox2D hitbox) {
        if (mapGrid == null)
            return false;

        double cx = (hitbox.getMinX() + hitbox.getMaxX()) / 2;
        double cy = (hitbox.getMinY() + hitbox.getMaxY()) / 2;
        
        // Removed dynamic array instantiation to prevent GC churn
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
     * Returns the nearest safe-room doorway tile adjacent to this safe room, or
     * null.
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

    // ================= BFS PATHFINDING =================

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
     * Returns the immediate next step of the shortest BFS path from start to
     * target, or null.
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
     * Returns true if a straight-line ray from (x1,y1) to (x2,y2) crosses no walls.
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
     * Returns a list of [row, col] arrays for every tile matching the given type.
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
     * Returns the tile type at the given world pixel coordinates, or -1 if out of
     * bounds.
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
     * Returns [row, col] grid indices for the given world pixel coordinates, or
     * null if out of bounds.
     */
    public int[] getTilePositionAt(double worldX, double worldY) {
        int col = (int) (worldX / TILE_SIZE);
        int row = (int) ((worldY - Y_OFFSET) / TILE_SIZE);

        if (mapGrid == null || row < 0 || row >= mapGrid.length || col < 0 || col >= mapGrid[0].length) {
            return null;
        }

        return new int[] { row, col };
    }

    /** Converts all escape room tiles (type 6) to floor tiles (type 0). */
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

    public int[][] getMapGrid() {
        return mapGrid;
    }

    public int getPlayerSpawnRow() {
        return playerSpawnRow;
    }

    public int getPlayerSpawnCol() {
        return playerSpawnCol;
    }
}
