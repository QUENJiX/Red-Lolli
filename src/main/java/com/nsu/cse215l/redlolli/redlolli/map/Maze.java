package com.nsu.cse215l.redlolli.redlolli.map;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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

    // ================= IMAGE ASSETS =================

    // 2D arrays: [theme][variant] — themes are 0-based indices (0=level1, 1=level2,
    // 2=level3)
    private static Image[][] borderWallImg = new Image[3][4]; // 4 variants per theme
    private static Image[][] innerWallImg = new Image[3][4]; // 4 variants per theme
    private static Image[][] floorAImg = new Image[3][3]; // 3 variants per theme
    private static Image[][] floorBImg = new Image[3][3]; // 3 variants per theme
    private static Image[] escapeRoomImg = new Image[2]; // 0=green door (lvl 1-2), 1=red door (lvl 3)
    private static Image[] escapeRoomOpenImg = new Image[2]; // open door versions

    // Instance state for escape room tracking
    private boolean[] escapeRoomOpen;

    private static boolean imagesInitialized = false;

    /** Loads from /assets/images/dungeon/ — 32x32 upscaled to target size. */
    private static Image loadDcssTile(String dcPath, int width, int height) {
        try {
            InputStream is = Maze.class.getResourceAsStream(dcPath);
            if (is != null) {
                return new Image(is, width, height, true, false);
            }
        } catch (Exception ignored) {
        }
        return null;
    }


    public static void initImages() {
        if (imagesInitialized)
            return;
        String dc = "/assets/images/dungeon/";

        // === THEME 1 — Level 1 ===
        borderWallImg[0][0] = loadDcssTile(dc + "wall/wall_vines_0.png", 40, 40);
        borderWallImg[0][1] = loadDcssTile(dc + "wall/wall_vines_1.png", 40, 40);
        borderWallImg[0][2] = loadDcssTile(dc + "wall/wall_vines_2.png", 40, 40);
        borderWallImg[0][3] = loadDcssTile(dc + "wall/wall_vines_3.png", 40, 40);

        innerWallImg[0][0] = loadDcssTile(dc + "wall/wall_vines_4.png", 40, 40);
        innerWallImg[0][1] = loadDcssTile(dc + "wall/wall_vines_5.png", 40, 40);
        innerWallImg[0][2] = loadDcssTile(dc + "wall/wall_vines_6.png", 40, 40);
        innerWallImg[0][3] = loadDcssTile(dc + "wall/brick_brown-vines_1.png", 40, 40);

        floorAImg[0][0] = loadDcssTile(dc + "floor/lair_0_new.png", 40, 40);
        floorAImg[0][1] = loadDcssTile(dc + "floor/lair_1_new.png", 40, 40);
        floorAImg[0][2] = loadDcssTile(dc + "floor/lair_2_new.png", 40, 40);

        floorBImg[0][0] = loadDcssTile(dc + "floor/lair_3_new.png", 40, 40);
        floorBImg[0][1] = loadDcssTile(dc + "floor/lair_4.png", 40, 40);
        floorBImg[0][2] = loadDcssTile(dc + "floor/lair_5.png", 40, 40);

        // === THEME 2 — Level 2 ===
        borderWallImg[1][0] = loadDcssTile(dc + "wall/brick_brown_4.png", 40, 40);
        borderWallImg[1][1] = loadDcssTile(dc + "wall/brick_brown_5.png", 40, 40);
        borderWallImg[1][2] = loadDcssTile(dc + "wall/brick_brown_6.png", 40, 40);
        borderWallImg[1][3] = loadDcssTile(dc + "wall/brick_brown_7.png", 40, 40);

        innerWallImg[1][0] = loadDcssTile(dc + "wall/brick_brown_0.png", 40, 40);
        innerWallImg[1][1] = loadDcssTile(dc + "wall/brick_brown_1.png", 40, 40);
        innerWallImg[1][2] = loadDcssTile(dc + "wall/brick_brown_2.png", 40, 40);
        innerWallImg[1][3] = loadDcssTile(dc + "wall/brick_brown_3.png", 40, 40);

        floorAImg[1][0] = loadDcssTile(dc + "floor/pebble_brown_0_new.png", 40, 40);
        floorAImg[1][1] = loadDcssTile(dc + "floor/pebble_brown_1_new.png", 40, 40);
        floorAImg[1][2] = loadDcssTile(dc + "floor/pebble_brown_2_new.png", 40, 40);

        floorBImg[1][0] = loadDcssTile(dc + "floor/pebble_brown_3_new.png", 40, 40);
        floorBImg[1][1] = loadDcssTile(dc + "floor/pebble_brown_4_new.png", 40, 40);
        floorBImg[1][2] = loadDcssTile(dc + "floor/pebble_brown_5_new.png", 40, 40);

        // === THEME 3 — Level 3 ===
        borderWallImg[2][0] = loadDcssTile(dc + "wall/brick_dark_3.png", 40, 40);
        borderWallImg[2][1] = loadDcssTile(dc + "wall/brick_dark_4.png", 40, 40);
        borderWallImg[2][2] = loadDcssTile(dc + "wall/brick_dark_5.png", 40, 40);
        borderWallImg[2][3] = loadDcssTile(dc + "wall/brick_dark_6.png", 40, 40);

        innerWallImg[2][0] = loadDcssTile(dc + "wall/brick_dark_0.png", 40, 40);
        innerWallImg[2][1] = loadDcssTile(dc + "wall/brick_dark_1.png", 40, 40);
        innerWallImg[2][2] = loadDcssTile(dc + "wall/brick_dark_2.png", 40, 40);
        innerWallImg[2][3] = loadDcssTile(dc + "wall/brick_dark_3.png", 40, 40);

        floorAImg[2][0] = loadDcssTile(dc + "floor/grey_dirt_0_new.png", 40, 40);
        floorAImg[2][1] = loadDcssTile(dc + "floor/grey_dirt_1_new.png", 40, 40);
        floorAImg[2][2] = loadDcssTile(dc + "floor/grey_dirt_2_new.png", 40, 40);

        floorBImg[2][0] = loadDcssTile(dc + "floor/grey_dirt_b_0.png", 40, 40);
        floorBImg[2][1] = loadDcssTile(dc + "floor/grey_dirt_b_1.png", 40, 40);
        floorBImg[2][2] = loadDcssTile(dc + "floor/grey_dirt_b_2.png", 40, 40);

        // === ESCAPE ROOMS ===
        escapeRoomImg[0] = loadDcssTile(dc + "doors/runed_door.png", 40, 40);
        escapeRoomImg[1] = loadDcssTile(dc + "doors/sealed_door.png", 40, 40);
        escapeRoomOpenImg[0] = loadDcssTile(dc + "gateways/escape_hatch_up.png", 40, 40);
        escapeRoomOpenImg[1] = loadDcssTile(dc + "gateways/escape_hatch_up.png", 40, 40);

        imagesInitialized = true;
    }

    /** Call this to force all maze tile images to reload (e.g. after changing asset filenames in code). */
    public static void resetImages() { imagesInitialized = false; }

    private void drawTile(GraphicsContext gc, Image img, double x, double y) {
        if (img != null) {
            gc.drawImage(img, x, y, TILE_SIZE, TILE_SIZE);
        } else {
            gc.setFill(Color.MAGENTA);
            gc.fillRect(x, y, TILE_SIZE, TILE_SIZE);
        }
    }

    /** Deterministic variant index based on tile position. */
    private static int variantIndex(int row, int col, int maxVariants) {
        return Math.abs(row * 31 + col * 17) % maxVariants;
    }

    // ================= ESCAPE ROOM STATE =================

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
    private boolean isEscapeRoomOpen(int row, int col) {
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
                System.err.println("Map file not found: " + path + ". Loading fallback map.");
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
            e.printStackTrace();
            mapGrid = new int[][] { { 1, 1, 1 }, { 1, 0, 1 }, { 1, 1, 1 } };
        }
    }

    // ================= RENDERING =================

    public void renderMaze(GraphicsContext gc) {
        if (mapGrid == null)
            return;

        int maxRow = mapGrid.length - 1;
        int maxCol = mapGrid[0].length - 1;
        int ti = Math.min(levelTheme - 1, 2);

        for (int row = 0; row < mapGrid.length; row++) {
            for (int col = 0; col < mapGrid[row].length; col++) {
                double tileX = col * TILE_SIZE;
                double tileY = row * TILE_SIZE + Y_OFFSET;

                int tile = mapGrid[row][col];
                boolean isBorder = (row == 0 || row == maxRow || col == 0 || col == maxCol);

                if (tile == 6) {
                    // === ESCAPE ROOM ===
                    boolean isOpen = isEscapeRoomOpen(row, col);
                    if (isOpen) {
                        // Player entered: show an open room/floor tile instead of a door
                        int fi = variantIndex(row, col, 3);
                        Image floorImg = ((row + col) % 2 == 0) ? floorAImg[ti][fi] : floorBImg[ti][fi];
                        drawTile(gc, floorImg, tileX, tileY);
                        // Overlay the open escape room image (open door/escape hatch) on top
                        Image openImg = escapeRoomOpenImg[levelTheme == 3 ? 1 : 0];
                        if (openImg != null) {
                            drawTile(gc, openImg, tileX, tileY);
                        }
                    } else {
                        // Not entered yet: show the closed door
                        Image doorImg = escapeRoomImg[levelTheme == 3 ? 1 : 0];
                        drawTile(gc, doorImg, tileX, tileY);
                    }
                } else if (tile == 1) {
                    // === WALL ===
                    int vi = variantIndex(row, col, 4);
                    if (isBorder) {
                        drawTile(gc, borderWallImg[ti][vi], tileX, tileY);
                    } else {
                        drawTile(gc, innerWallImg[ti][vi], tileX, tileY);
                    }
                } else {
                    // === FLOOR ===
                    int fi = variantIndex(row, col, 3);
                    Image floorImg = ((row + col) % 2 == 0) ? floorAImg[ti][fi] : floorBImg[ti][fi];
                    drawTile(gc, floorImg, tileX, tileY);
                }
            }
        }
    }

    /** Called automatically after tiles render. Fill your placeSprite() calls below. */
    public void renderOverlays(GraphicsContext gc) {
        // ================= YOUR OVERLAYS =================
        // Load once, then call placeSprite each frame:
        //
        //   Image blood = new Image(getClass().getResourceAsStream("/assets/images/sprites/blood_splat.png"));
        //   placeSprite(gc, blood, 5, 3, 40, 40, 0.5);
        //   placeSprite(gc, blood, 12, 7, 40, 40, 0.3);
        // ================================================
    }

    // ================= MANUAL OVERLAY HELPER =================

    /**
     * Places any image at a specific grid tile (col, row) on the maze.
     *
     * Example — blood splat at col 5, row 3, drawn at 40x40, 50% opacity:
     *   Image blood = new Image(getClass().getResourceAsStream("/assets/images/sprites/blood_splat.png"));
     *   maze.placeSprite(gc, blood, 5, 3, 40, 40, 0.5);
     *
     * Example — mold patch at col 10, row 7, upscaled to 48x48:
     *   Image mold = new Image(getClass().getResourceAsStream("/assets/images/sprites/mold.png"));
     *   maze.placeSprite(gc, mold, 10, 7, 48, 48, 0.7);
     *
     * Call this after maze.renderMaze(gc) and before HUD rendering.
     */
    public void placeSprite(GraphicsContext gc, Image image, int col, int row, double width, double height,
            double alpha) {
        if (image == null)
            return;
        double x = col * TILE_SIZE;
        double y = row * TILE_SIZE + Y_OFFSET;
        gc.setGlobalAlpha(alpha);
        gc.drawImage(image, x, y, width, height);
        gc.setGlobalAlpha(1.0);
    }

    // ================= COLLISION & QUERIES =================

    /** Returns true if the projected hitbox overlaps any wall tile. */
    public boolean isWallCollision(Rectangle2D nextHitbox) {
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
    public boolean isEscapeRoom(Rectangle2D hitbox) {
        if (mapGrid == null)
            return false;

        double cx = (hitbox.getMinX() + hitbox.getMaxX()) / 2;
        double cy = (hitbox.getMinY() + hitbox.getMaxY()) / 2;

        double[][] points = {
                { cx, cy },
                { hitbox.getMinX() + 2, cy },
                { hitbox.getMaxX() - 2, cy },
                { cx, hitbox.getMinY() + 2 },
                { cx, hitbox.getMaxY() - 2 }
        };

        for (double[] p : points) {
            int col = (int) (p[0] / TILE_SIZE);
            int row = (int) ((p[1] - Y_OFFSET) / TILE_SIZE);
            if (row >= 0 && row < mapGrid.length && col >= 0 && col < mapGrid[0].length
                    && mapGrid[row][col] == 6) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the nearest safe-room doorway tile adjacent to this safe room, or
     * null.
     */
    public int[] findSafeRoomDoor(Rectangle2D hitbox) {
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
