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
    private static Image[] escapeRoomImg = new Image[2]; // 0=closed door (lvl 1-2), 1=closed door (lvl 3)

    // Atmospheric overlay caches
    private static Image[] overlayBloodSplat = new Image[30];
    private static Image[] overlayWallBlood = new Image[76]; // 19 × 4 directions
    private static Image[] overlayFloorVines = new Image[4];
    private static Image[] overlaySigils = new Image[10];
    private static Image[] overlayTorches = new Image[5];
    private static Image[] overlayWater = new Image[2];
    private static Image[] overlayFountains = new Image[7];
    private static Image[] overlayMoss = new Image[4];

    // Additional overlays
    private static Image[] overlayCracks = new Image[5];
    private static Image[] overlayMold = new Image[4];

    // Chest sprites for game phases
    private static Image[] chestClosed = new Image[3]; // one per theme
    private static Image[] chestOpen = new Image[3]; // one per theme

    // Escape room open state images (rendered when player is near)
    private static Image[] escapeRoomOpenImg = new Image[2];

    // Instance state for escape room tracking
    private boolean[] escapeRoomOpen;

    private static boolean imagesInitialized = false;

    /**
     * Loads a sprite from the Dungeon Crawl Stone Soup pack (32x32 → scaled to
     * target).
     */
    private static Image loadDcssTile(String dcPath, int width, int height) {
        try {
            InputStream is = Maze.class.getResourceAsStream(dcPath);
            if (is != null) {
                return new Image(is, width, height, true, false); // smooth=false for pixel art
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /** Fallback: loads custom sprite from /sprites/ folder. */
    private static Image loadSprite(String filename, int width, int height) {
        try {
            InputStream is = Maze.class.getResourceAsStream("/assets/images/sprites/" + filename);
            if (is != null) {
                return new Image(is, width, height, true, false);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /** Loads an image trying DCSS path first, falling back to custom sprite. */
    private static Image loadTile(String dcPath, String fallbackName, int width, int height) {
        Image img = loadDcssTile(dcPath, width, height);
        if (img != null)
            return img;
        return loadSprite(fallbackName, width, height);
    }

    public static void initImages() {
        if (imagesInitialized)
            return;
        String dc = "/assets/images/dungeon/";

        // === THEME 1 — Forest/Natural (Level 1) ===
        // Vines-covered walls + natural lair floors — cohesive earthy/organic set
        borderWallImg[0][0] = loadTile(dc + "wall/wall_vines_0.png", "border_wall_1.png", 40, 40);
        borderWallImg[0][1] = loadTile(dc + "wall/wall_vines_1.png", "border_wall_1.png", 40, 40);
        borderWallImg[0][2] = loadTile(dc + "wall/wall_vines_2.png", "border_wall_1.png", 40, 40);
        borderWallImg[0][3] = loadTile(dc + "wall/wall_vines_3.png", "border_wall_1.png", 40, 40);

        innerWallImg[0][0] = loadTile(dc + "wall/wall_vines_4.png", "inner_wall_1.png", 40, 40);
        innerWallImg[0][1] = loadTile(dc + "wall/wall_vines_5.png", "inner_wall_1.png", 40, 40);
        innerWallImg[0][2] = loadTile(dc + "wall/wall_vines_6.png", "inner_wall_1.png", 40, 40);
        innerWallImg[0][3] = loadTile(dc + "wall/brick_brown-vines_1.png", "inner_wall_1.png", 40, 40);

        floorAImg[0][0] = loadTile(dc + "floor/lair_0_new.png", "floor_a_1.png", 40, 40);
        floorAImg[0][1] = loadTile(dc + "floor/lair_1_new.png", "floor_a_1.png", 40, 40);
        floorAImg[0][2] = loadTile(dc + "floor/lair_2_new.png", "floor_a_1.png", 40, 40);

        floorBImg[0][0] = loadTile(dc + "floor/lair_3_new.png", "floor_b_1.png", 40, 40);
        floorBImg[0][1] = loadTile(dc + "floor/lair_4.png", "floor_b_1.png", 40, 40);
        floorBImg[0][2] = loadTile(dc + "floor/lair_5.png", "floor_b_1.png", 40, 40);

        // === THEME 2 — Dungeon/Brown (Level 2) ===
        // Brown brick walls (consistent family) + pebble floors (consistent family)
        borderWallImg[1][0] = loadTile(dc + "wall/brick_brown_4.png", "border_wall_2.png", 40, 40);
        borderWallImg[1][1] = loadTile(dc + "wall/brick_brown_5.png", "border_wall_2.png", 40, 40);
        borderWallImg[1][2] = loadTile(dc + "wall/brick_brown_6.png", "border_wall_2.png", 40, 40);
        borderWallImg[1][3] = loadTile(dc + "wall/brick_brown_7.png", "border_wall_2.png", 40, 40);

        innerWallImg[1][0] = loadTile(dc + "wall/brick_brown_0.png", "inner_wall_2.png", 40, 40);
        innerWallImg[1][1] = loadTile(dc + "wall/brick_brown_1.png", "inner_wall_2.png", 40, 40);
        innerWallImg[1][2] = loadTile(dc + "wall/brick_brown_2.png", "inner_wall_2.png", 40, 40);
        innerWallImg[1][3] = loadTile(dc + "wall/brick_brown_3.png", "inner_wall_2.png", 40, 40);

        floorAImg[1][0] = loadTile(dc + "floor/pebble_brown_0_new.png", "floor_a_2.png", 40, 40);
        floorAImg[1][1] = loadTile(dc + "floor/pebble_brown_1_new.png", "floor_a_2.png", 40, 40);
        floorAImg[1][2] = loadTile(dc + "floor/pebble_brown_2_new.png", "floor_a_2.png", 40, 40);

        floorBImg[1][0] = loadTile(dc + "floor/pebble_brown_3_new.png", "floor_b_2.png", 40, 40);
        floorBImg[1][1] = loadTile(dc + "floor/pebble_brown_4_new.png", "floor_b_2.png", 40, 40);
        floorBImg[1][2] = loadTile(dc + "floor/pebble_brown_5_new.png", "floor_b_2.png", 40, 40);

        // === THEME 3 — Dark/Crypt (Level 3) ===
        // Dark brick walls (single family) + gray dirt floors (single family)
        borderWallImg[2][0] = loadTile(dc + "wall/brick_dark_3.png", "border_wall_3.png", 40, 40);
        borderWallImg[2][1] = loadTile(dc + "wall/brick_dark_4.png", "border_wall_3.png", 40, 40);
        borderWallImg[2][2] = loadTile(dc + "wall/brick_dark_5.png", "border_wall_3.png", 40, 40);
        borderWallImg[2][3] = loadTile(dc + "wall/brick_dark_6.png", "border_wall_3.png", 40, 40);

        innerWallImg[2][0] = loadTile(dc + "wall/brick_dark_0.png", "inner_wall_3.png", 40, 40);
        innerWallImg[2][1] = loadTile(dc + "wall/brick_dark_1.png", "inner_wall_3.png", 40, 40);
        innerWallImg[2][2] = loadTile(dc + "wall/brick_dark_2.png", "inner_wall_3.png", 40, 40);
        innerWallImg[2][3] = loadTile(dc + "wall/brick_dark_3.png", "inner_wall_3.png", 40, 40);

        floorAImg[2][0] = loadTile(dc + "floor/grey_dirt_0_new.png", "floor_a_3.png", 40, 40);
        floorAImg[2][1] = loadTile(dc + "floor/grey_dirt_1_new.png", "floor_a_3.png", 40, 40);
        floorAImg[2][2] = loadTile(dc + "floor/grey_dirt_2_new.png", "floor_a_3.png", 40, 40);

        floorBImg[2][0] = loadTile(dc + "floor/grey_dirt_b_0.png", "floor_b_3.png", 40, 40);
        floorBImg[2][1] = loadTile(dc + "floor/grey_dirt_b_1.png", "floor_b_3.png", 40, 40);
        floorBImg[2][2] = loadTile(dc + "floor/grey_dirt_b_2.png", "floor_b_3.png", 40, 40);

        // === ESCAPE ROOMS ===
        escapeRoomImg[0] = loadTile(dc + "doors/runed_door.png", "escape_room_green.png", 40, 40);
        escapeRoomImg[1] = loadTile(dc + "doors/sealed_door.png", "escape_room_red.png", 40, 40);

        // Open escape room images — use escape hatch / revealed room sprites (player
        // has entered)
        // escape_hatch_up.png looks like an opened portal/room floor tile
        escapeRoomOpenImg[0] = loadTile(dc + "gateways/escape_hatch_up.png", "escape_room_green.png", 40, 40);
        escapeRoomOpenImg[1] = loadTile(dc + "gateways/escape_hatch_up.png", "escape_room_red.png", 40, 40);

        // === ATMOSPHERIC OVERLAYS ===
        String misc = "/assets/images/misc/";
        // Blood splats: 30 variants
        for (int i = 0; i < 30; i++) {
            overlayBloodSplat[i] = loadDcssTile(misc + "blood/blood_red_" + i + ".png", 40, 40);
        }
        overlayBloodSplat[0] = loadDcssTile(misc + "blood/blood_red.png", 40, 40);
        overlayBloodSplat[1] = loadDcssTile(misc + "blood/blood_red_1_new.png", 40, 40);
        overlayBloodSplat[2] = loadDcssTile(misc + "blood/blood_red_2_new.png", 40, 40);
        overlayBloodSplat[3] = loadDcssTile(misc + "blood/blood_red_3_new.png", 40, 40);
        overlayBloodSplat[4] = loadDcssTile(misc + "blood/blood_red_4_new.png", 40, 40);

        // Wall blood: 19 base × 4 directions = 76
        // Actual files: wall_blood_0_east.png, wall_blood_0_north.png, etc. (in
        // misc/blood/)
        String wallBlood = misc + "blood/wall_blood_";
        String[] dirs = { "_east", "_north", "_south", "_west" };
        for (int i = 0; i < 19; i++) {
            for (int d = 0; d < 4; d++) {
                int idx = i * 4 + d;
                overlayWallBlood[idx] = loadDcssTile(wallBlood + i + dirs[d] + ".png", 40, 40);
            }
        }

        // Floor vines
        for (int i = 0; i < 4; i++) {
            overlayFloorVines[i] = loadDcssTile(dc + "floor/floor_vines_" + i + "_new.png", 40, 40);
        }

        // Cracks/puddles
        for (int i = 0; i < 5; i++) {
            overlayCracks[i] = loadDcssTile(misc + "blood/blood_puddle_red" + (i > 0 ? "_" + i : "") + ".png", 40, 40);
        }

        // Sigils
        for (int i = 0; i < 10; i++) {
            overlaySigils[i] = loadDcssTile(dc + "floor/sigils/" + sigilNames[i] + ".png", 40, 40);
        }

        // Wall torches (5 variants)
        for (int i = 0; i < 5; i++) {
            overlayTorches[i] = loadDcssTile(dc + "wall/torches/torch_" + i + ".png", 40, 40);
        }

        // Water puddles
        overlayWater[0] = loadDcssTile(dc + "water/shallow_water.png", 40, 40);
        overlayWater[1] = loadDcssTile(dc + "water/shallow_water_2.png", 40, 40);

        // === NEW: Additional atmospheric props ===
        // Fountains (blue, blood, sparkling)
        overlayFountains[0] = loadDcssTile(dc + "blue_fountain.png", 40, 40);
        overlayFountains[1] = loadDcssTile(dc + "blue_fountain_2.png", 40, 40);
        overlayFountains[2] = loadDcssTile(dc + "blood_fountain.png", 40, 40);
        overlayFountains[3] = loadDcssTile(dc + "blood_fountain_2.png", 40, 40);
        overlayFountains[4] = loadDcssTile(dc + "sparkling_fountain.png", 40, 40);
        overlayFountains[5] = loadDcssTile(dc + "sparkling_fountain_2.png", 40, 40);
        overlayFountains[6] = loadDcssTile(dc + "dry_fountain.png", 40, 40);

        // Mold growth
        for (int i = 0; i < 4; i++) {
            overlayMold[i] = loadDcssTile(dc + "mold_large_" + (i + 1) + ".png", 40, 40);
        }

        // Extra floor variety: moss
        overlayMoss[0] = loadDcssTile(dc + "floor/moss_0.png", 40, 40);
        overlayMoss[1] = loadDcssTile(dc + "floor/moss_1.png", 40, 40);
        overlayMoss[2] = loadDcssTile(dc + "floor/moss_2.png", 40, 40);
        overlayMoss[3] = loadDcssTile(dc + "floor/moss_3.png", 40, 40);

        // === CHEST SPRITES (for chest game phases) ===
        // Theme 1: default chest, Theme 2: chest_2, Theme 3: chest_2
        chestClosed[0] = loadDcssTile(dc + "chest.png", 40, 40);
        chestClosed[1] = loadDcssTile(dc + "chest_2_closed.png", 40, 40);
        chestClosed[2] = loadDcssTile(dc + "chest_2_closed.png", 40, 40);
        chestOpen[0] = loadDcssTile(dc + "chest_2_open.png", 40, 40);
        chestOpen[1] = loadDcssTile(dc + "chest_2_open.png", 40, 40);
        chestOpen[2] = loadDcssTile(dc + "chest_2_open.png", 40, 40);

        imagesInitialized = true;
    }

    private static final String[] sigilNames = {
            "circle", "cross", "rhombus", "algiz",
            "curve_north_east", "curve_north_west", "curve_south_east", "curve_south_west",
            "straight_north_south", "straight_east_west"
    };

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

        // Second pass: atmospheric overlays + corner statues
        renderAtmosphericOverlays(gc);
    }

    // ================= ATMOSPHERIC OVERLAYS =================

    /**
     * Manually place a sprite at a specific grid position.
     * Add your placements inside renderAtmosphericOverlays() below,
     * right after the "=== MANUAL PLACEMENT ===" comment.
     *
     * <pre>
     * Examples:
     *   placeSprite(gc, overlayTorches[0],     8,  5, 48, 48, 1.0);   // torch at (col=8, row=5)
     *   placeSprite(gc, overlayMold[2],        12, 10, 48, 48, 0.7);   // mold at (12,10)
     *   placeSprite(gc, overlaySigils[0],      3,  7, 40, 40, 0.3);    // sigil at (3,7)
     *   placeSprite(gc, overlayFountains[0],   15,  4, 40, 40, 0.9);   // fountain at (15,4)
     *   placeSprite(gc, overlayCracks[1],      6,  9, 40, 40, 0.3);    // crack at (6,9)
     *   placeSprite(gc, overlayBloodSplat[10], 4,  3, 40, 40, 0.25);   // blood at (4,3)
     *   placeSprite(gc, overlayFloorVines[0],  10, 2, 40, 40, 0.5);    // vines at (10,2)
     *   placeSprite(gc, overlayMoss[1],        7,  11, 40, 40, 0.4);   // moss at (7,11)
     *   placeSprite(gc, overlayWater[0],       14, 8, 40, 40, 0.3);    // water at (14,8)
     *   placeSprite(gc, overlayWallBlood[0],   5,  6, 40, 40, 0.3);    // wall blood at (5,6)
     *   placeSprite(gc, getClosedChestImage(), 9,  6, 40, 40, 1.0);    // chest at (9,6)
     * </pre>
     *
     * @param gc     graphics context
     * @param image  the sprite to place
     * @param col    grid column
     * @param row    grid row
     * @param width  draw width in pixels
     * @param height draw height in pixels
     * @param alpha  transparency (0.0–1.0)
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

    /**
     * Second rendering pass: manual overlay placement only.
     * No procedural/random overlays — every sprite is placed explicitly.
     * Add your placeSprite() calls inside the "=== MANUAL PLACEMENT ===" block.
     */
    private void renderAtmosphericOverlays(GraphicsContext gc) {
        if (mapGrid == null)
            return;

        // ================================================================
        // === MANUAL PLACEMENT ===
        // Add your overlay placements below. One per line.
        // Format: placeSprite(gc, imageArray[index], col, row, width, height, alpha)
        //
        // Available overlay arrays:
        //   overlayBloodSplat[0-29]   - blood splats on floor
        //   overlayFloorVines[0-3]    - vine overlays on floor
        //   overlayWater[0-1]         - water puddles
        //   overlayMoss[0-3]          - moss patches
        //   overlayCracks[0-4]        - blood puddles/cracks
        //   overlayMold[0-3]          - large fungus (use 48x48, alpha 0.7)
        //   overlaySigils[0-9]        - floor runes/sigils
        //   overlayFountains[0-6]     - fountains/decorative
        //   overlayTorches[0-4]       - wall torches (use 48x48)
        //   overlayWallBlood[0-75]    - blood on walls
        //   getClosedChestImage()     - closed chest for current theme
        //   getOpenChestImage()       - open chest for current theme
        //
        // Examples:
        //   placeSprite(gc, overlayTorches[0],     8,  5, 48, 48, 1.0);
        //   placeSprite(gc, overlayMold[2],        12, 10, 48, 48, 0.7);
        //   placeSprite(gc, overlaySigils[0],      3,  7, 40, 40, 0.3);
        //   placeSprite(gc, getClosedChestImage(), 9,  6, 40, 40, 1.0);
        // ================================================================



        // === END MANUAL PLACEMENT ===
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

    // ================= CHEST ACCESSORS =================

    /** Returns the closed chest image for the current theme level. */
    public Image getClosedChestImage() {
        int ti = Math.min(levelTheme - 1, 2);
        return chestClosed[ti];
    }

    /** Returns the open chest image for the current theme level. */
    public Image getOpenChestImage() {
        int ti = Math.min(levelTheme - 1, 2);
        return chestOpen[ti];
    }
}
