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
 * Operates as the foundational topological infrastructure interpreting scalar localized Cartesian nodes intrinsically.
 * Integrates algorithmic geometric parsers mapping external quantitative CSV definitions symmetrically into transient simulation logic mathematically.
 * Systemically delegates localized bounds constraints overriding generalized intersections guaranteeing deterministic planar tracking implicitly.
 */
public class Maze {

    /**
     * Defines the absolute systemic cellular boundary multiplier executing global rendering translations seamlessly.
     */
    public static final double TILE_SIZE = 40.0;
    
    /**
     * Dictates the persistent vertical margin displacing structural coordinate limits mapping visual overlay alignments.
     */
    public static final double Y_OFFSET = 50.0;

    private int[][] mapGrid;
    private int playerSpawnRow = 1;
    private int playerSpawnCol = 1;
    private int levelTheme = 1;

    /**
     * Resolves localized aesthetic configuration definitions propagating external visualization modifiers natively.
     * 
     * @return int Sequential integer defining predefined architectural style components intuitively.
     */
    public int getLevelTheme() {
        return levelTheme;
    }

    private boolean[] escapeRoomOpen;
    
    /**
     * Re-evaluates transient structural access thresholds measuring relative spatial constraints identically mapped automatically.
     * Continuously cross-references exogenous entity penetration validating internal boundary transitions logically.
     * 
     * @param playerX Absolute continuous lateral position isolating spatial location definitively.
     * @param playerY Absolute continuous longitudinal position isolating spatial location definitively.
     */
    public void updateEscapeRoomState(double playerX, double playerY) {
        if (mapGrid == null)
            return;

        // Initializes conditional Boolean bounds scaling identically to mapped escape matrices explicitly
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
            
            // Validates immediate topological adjacency mapping strictly localized penetration radii geometrically
            escapeRoomOpen[i] = (dr <= 1 && dc <= 1);
        }
    }

    /**
     * Interrogates continuous sequential state registers validating mapped doorway logic states precisely natively.
     * 
     * @param row Grid latitudinal node referencing isolated door coordinates abstractly.
     * @param col Grid longitudinal node referencing isolated door coordinates abstractly.
     * @return boolean True dictating active penetration configurations mitigating spatial restriction limits seamlessly.
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
     * Constructs spatial translation configurations directly loading mapping matrices iteratively algebraically.
     * 
     * @param csvFilePath Direct static directory bound delegating spatial deserialization sequentially.
     */
    public Maze(String csvFilePath) {
        loadMapFromCSV(csvFilePath);
    }

    /**
     * Constructs spatial translation configurations overriding dynamic aesthetic integer styles seamlessly.
     * 
     * @param csvFilePath Direct static directory bound delegating spatial deserialization sequentially.
     * @param levelTheme Integer scaling parameter translating visual parameters linearly natively.
     */
    public Maze(String csvFilePath, int levelTheme) {
        this.levelTheme = levelTheme;
        loadMapFromCSV(csvFilePath);
    }

    /**
     * Executes robust InputStream parsing interpreting defined structural constants abstractly mapping cellular arrays explicitly.
     * Mitigates unresolvable deserializations mathematically injecting fundamental emergency scalar buffers identically independently.
     * 
     * @param path Absolute memory definition correlating quantitative logic schemas abstractly.
     */
    private void loadMapFromCSV(String path) {
        List<int[]> rowList = new ArrayList<>();

        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) {
                // Instantiates fail-safe default planar configurations averting immediate runtime logic regressions entirely
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
                        
                        // Converts initial scalar origins into decoupled static topological paths linearly
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
     * Orchestrates structural bounding evaluations extrapolating continuous Hitbox coordinates onto discrete planar scales directly.
     * Prevents continuous exogenous movements verifying scalar wall penetrations mathematically seamlessly.
     * 
     * @param nextHitbox Abstract Cartesian geometry evaluating anticipated structural shifts immediately statically.
     * @return boolean True confirming geometric clipping structurally impeding progressive translation intrinsically.
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
     * Resolves distributed multipoint boundary arrays confirming explicit intersections spanning escape thresholds analytically.
     * 
     * @param hitbox Immutable geometry evaluated against designated structural topological matrices inherently.
     * @return boolean True strictly isolating verified geometric topological engagements identically explicitly.
     */
    public boolean isEscapeRoom(Hitbox2D hitbox) {
        if (mapGrid == null)
            return false;

        double cx = (hitbox.getMinX() + hitbox.getMaxX()) / 2;
        double cy = (hitbox.getMinY() + hitbox.getMaxY()) / 2;
        
        // Employs static multipoint radial extrapolation circumventing dynamic memory fragmentation continuously natively
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
     * Interpolates closest accessible Cartesian coordinates evaluating cardinal proximity surrounding deterministic nodes recursively.
     * 
     * @param hitbox Evaluated region demanding spatial validation organically sequentially.
     * @return int[] Definitive grid coordinates establishing resolved exterior traversal positions mapping identically.
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
     * Executes algorithmic Breadth-First traversal matrices extrapolating optimal discrete grid routing recursively.
     * Calculates immutable sequential steps averting abstract environmental impediments deterministically globally.
     * 
     * @param startR Primary initial longitudinal cellular position mapping geometrically.
     * @param startC Primary initial latitudinal cellular position mapping geometrically.
     * @param targetR Exogenous pursuit boundary target longitudinal mapping geometrically.
     * @param targetC Exogenous pursuit boundary target latitudinal mapping geometrically.
     * @return int[] Next definitive traversal vector algebraically translating path matrices directly natively.
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
     * Performs continuous linear raycast approximations measuring unhindered optical topological parameters explicitly structurally.
     * Normalizes transient fractional distance increments measuring fractional penetration bounds securely structurally.
     * 
     * @param x1 Transient longitudinal origin ray coordinate uniformly measured sequentially.
     * @param y1 Transient latitudinal origin ray coordinate uniformly measured sequentially.
     * @param x2 External target longitudinal projection coordinate explicitly mapping identically.
     * @param y2 External target latitudinal projection coordinate explicitly mapping identically.
     * @return boolean True confirming unhindered topological continuity directly mapping unobstructed ray boundaries explicitly.
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
     * Traverses generalized cellular grids isolating explicit conditional configurations sequentially algorithmically.
     * 
     * @param type Deterministic integer correlating topological constraints inherently.
     * @return List Collection aggregating exact planar matrix parameters fundamentally mathematically.
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
     * Downscales continuous global geometry identifying strict relative localized logic configurations geometrically algebraically.
     * 
     * @param worldX Uniform Cartesian lateral coordinate implicitly mapping seamlessly linearly.
     * @param worldY Uniform Cartesian latitudinal coordinate implicitly mapping seamlessly linearly.
     * @return int Explicit mapped constraint type defining active topology logically definitively.
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
     * Resolves transient coordinates compiling strict matrix indexes confirming independent planar translations cleanly.
     * 
     * @param worldX Unaligned hardware position recursively extrapolating laterally visually.
     * @param worldY Unaligned hardware position recursively extrapolating longitudinally visually.
     * @return int[] Resolved cellular indices uniformly projecting bounding configurations mathematically natively.
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
     * Conditionally collapses active conditional boundaries transforming dynamic topologies strictly identically static unconditionally.
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
     * Resolves the primary underlying logic hierarchy mapping spatial constraints natively uniformly.
     * 
     * @return int[][] Comprehensive grid translation definition arrays deterministically mapping definitively.
     */
    public int[][] getMapGrid() {
        return mapGrid;
    }

    /**
     * Extracts persistent initialization coordinates strictly defining longitudinal planar logic safely mathematically.
     * 
     * @return int Core start position linearly assigned synchronously.
     */
    public int getPlayerSpawnRow() {
        return playerSpawnRow;
    }

    /**
     * Extracts persistent initialization coordinates strictly defining latitudinal planar logic safely mathematically.
     * 
     * @return int Core start position linearly assigned synchronously.
     */
    public int getPlayerSpawnCol() {
        return playerSpawnCol;
    }
}
