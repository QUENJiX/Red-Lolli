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
 */
public class Maze {

    public static final double TILE_SIZE = 40.0;
    public static final double Y_OFFSET = 50.0;

    private int[][] mapGrid;
    private int playerSpawnRow = 1;
    private int playerSpawnCol = 1;
    private int levelTheme = 1;

    public Maze(String csvFilePath) {
        loadMapFromCSV(csvFilePath);
    }

    public Maze(String csvFilePath, int levelTheme) {
        this.levelTheme = levelTheme;
        loadMapFromCSV(csvFilePath);
    }

    private void loadMapFromCSV(String path) {
        List<int[]> rowList = new ArrayList<>();

        try {
            InputStream is = getClass().getResourceAsStream(path);
            if (is == null) {
                System.err.println("Map file not found: " + path + ". Loading fallback map.");
                mapGrid = new int[][]{
                        {1, 1, 1, 1, 1, 1},
                        {1, 0, 0, 0, 2, 1},
                        {1, 0, 1, 1, 0, 1},
                        {1, 0, 0, 0, 0, 1},
                        {1, 1, 1, 1, 1, 1}
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
            mapGrid = new int[][]{{1, 1, 1}, {1, 0, 1}, {1, 1, 1}};
        }
    }

    public void renderMaze(GraphicsContext gc) {
        if (mapGrid == null) return;

        int maxRow = mapGrid.length - 1;
        int maxCol = mapGrid[0].length - 1;

        for (int row = 0; row < mapGrid.length; row++) {
            for (int col = 0; col < mapGrid[row].length; col++) {
                double tileX = col * TILE_SIZE;
                double tileY = row * TILE_SIZE + Y_OFFSET;

                int tile = mapGrid[row][col];
                boolean isBorder = (row == 0 || row == maxRow || col == 0 || col == maxCol);

                if (tile == 1) {
                    if (isBorder) {
                        renderBorderWall(gc, tileX, tileY);
                    } else {
                        renderInnerWall(gc, tileX, tileY);
                    }
                } else if (tile == 6) {
                    renderEscapeRoom(gc, tileX, tileY);
                } else {
                    renderFloor(gc, tileX, tileY, row, col);
                }
            }
        }
    }

    private void renderBorderWall(GraphicsContext gc, double x, double y) {
        if (levelTheme == 1) {
            gc.setFill(Color.rgb(16, 28, 16));
        } else if (levelTheme == 2) {
            gc.setFill(Color.rgb(20, 18, 18));
        } else {
            gc.setFill(Color.rgb(22, 20, 24));
        }
        gc.fillRect(x, y, TILE_SIZE, TILE_SIZE);

        gc.setStroke(Color.rgb(30, 25, 32));
        gc.setLineWidth(0.5);
        gc.strokeLine(x, y + TILE_SIZE * 0.33, x + TILE_SIZE, y + TILE_SIZE * 0.33);
        gc.strokeLine(x, y + TILE_SIZE * 0.66, x + TILE_SIZE, y + TILE_SIZE * 0.66);

        gc.setStroke(Color.rgb(10, 8, 12));
        gc.setLineWidth(1);
        gc.strokeRect(x, y, TILE_SIZE, TILE_SIZE);
    }

    private void renderInnerWall(GraphicsContext gc, double x, double y) {
        if (levelTheme == 1) {
            gc.setFill(Color.rgb(32, 60, 34));
        } else if (levelTheme == 2) {
            gc.setFill(Color.rgb(55, 40, 48));
        } else {
            gc.setFill(Color.rgb(48, 46, 52));
        }
        gc.fillRect(x, y, TILE_SIZE, TILE_SIZE);

        gc.setStroke(Color.rgb(38, 28, 35));
        gc.setLineWidth(0.7);
        gc.strokeLine(x, y + TILE_SIZE * 0.5, x + TILE_SIZE, y + TILE_SIZE * 0.5);
        gc.strokeLine(x + TILE_SIZE * 0.5, y, x + TILE_SIZE * 0.5, y + TILE_SIZE * 0.5);
        gc.strokeLine(x + TILE_SIZE * 0.25, y + TILE_SIZE * 0.5, x + TILE_SIZE * 0.25, y + TILE_SIZE);
        gc.strokeLine(x + TILE_SIZE * 0.75, y + TILE_SIZE * 0.5, x + TILE_SIZE * 0.75, y + TILE_SIZE);

        gc.setStroke(Color.rgb(70, 52, 62));
        gc.setLineWidth(1);
        gc.strokeLine(x + 1, y + 1, x + TILE_SIZE - 1, y + 1);
        gc.strokeLine(x + 1, y + 1, x + 1, y + TILE_SIZE - 1);

        gc.setStroke(Color.rgb(30, 20, 28));
        gc.strokeLine(x + TILE_SIZE - 1, y + 1, x + TILE_SIZE - 1, y + TILE_SIZE - 1);
        gc.strokeLine(x + 1, y + TILE_SIZE - 1, x + TILE_SIZE - 1, y + TILE_SIZE - 1);
    }

    private void renderEscapeRoom(GraphicsContext gc, double x, double y) {
        gc.setFill(levelTheme == 3 ? Color.rgb(26, 26, 26) : Color.rgb(12, 30, 12));
        gc.fillRect(x, y, TILE_SIZE, TILE_SIZE);

        gc.setStroke(levelTheme == 3 ? Color.rgb(80, 20, 20) : Color.rgb(0, 80, 0));
        gc.setLineWidth(1);
        gc.strokeRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);

        gc.setFill(levelTheme == 3 ? Color.rgb(140, 40, 40, 0.6) : Color.rgb(0, 100, 0, 0.6));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gc.fillText("S", x + 14, y + 27);
    }

    private void renderFloor(GraphicsContext gc, double x, double y, int row, int col) {
        boolean checker = (row + col) % 2 == 0;

        if (levelTheme == 1) {
            gc.setFill(checker ? Color.rgb(16, 24, 18) : Color.rgb(18, 28, 20));
        } else if (levelTheme == 2) {
            gc.setFill(checker ? Color.rgb(18, 18, 22) : Color.rgb(22, 22, 28));
        } else {
            gc.setFill(checker ? Color.rgb(22, 22, 24) : Color.rgb(28, 28, 32));
        }

        gc.fillRect(x, y, TILE_SIZE, TILE_SIZE);
        gc.setStroke(Color.rgb(30, 30, 36));
        gc.setLineWidth(0.3);
        gc.strokeRect(x, y, TILE_SIZE, TILE_SIZE);

        if (levelTheme == 1) {
            gc.setFill(Color.rgb(26, 44, 28, 0.45));
            gc.fillOval(x + 8, y + 10, 4, 3);
            gc.fillOval(x + 26, y + 24, 5, 4);
        } else {
            gc.setStroke(Color.rgb(12, 12, 16, 0.45));
            gc.setLineWidth(0.6);
            gc.strokeLine(x + 6, y + 10, x + 20, y + 16);
            gc.strokeLine(x + 24, y + 28, x + 34, y + 34);
        }
    }

    /** Returns true if the projected hitbox overlaps any wall tile. */
    public boolean isWallCollision(Rectangle2D nextHitbox) {
        if (mapGrid == null) return false;

        int leftCol   = (int) (nextHitbox.getMinX() / TILE_SIZE);
        int rightCol  = (int) (nextHitbox.getMaxX() / TILE_SIZE);
        int topRow    = (int) ((nextHitbox.getMinY() - Y_OFFSET) / TILE_SIZE);
        int bottomRow = (int) ((nextHitbox.getMaxY() - Y_OFFSET) / TILE_SIZE);

        if (leftCol < 0 || rightCol >= mapGrid[0].length || topRow < 0 || bottomRow >= mapGrid.length) {
            return true;
        }

        for (int r = topRow; r <= bottomRow; r++) {
            for (int c = leftCol; c <= rightCol; c++) {
                if (mapGrid[r][c] == 1) return true;
            }
        }
        return false;
    }

    /** Returns true if any part of the hitbox overlaps an escape-room tile (multi-point check). */
    public boolean isEscapeRoom(Rectangle2D hitbox) {
        if (mapGrid == null) return false;

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

    /** Returns the nearest safe-room doorway tile adjacent to this safe room, or null. */
    public int[] findSafeRoomDoor(Rectangle2D hitbox) {
        if (mapGrid == null) return null;

        double cx = (hitbox.getMinX() + hitbox.getMaxX()) / 2;
        double cy = (hitbox.getMinY() + hitbox.getMaxY()) / 2;
        int centerCol = (int) (cx / TILE_SIZE);
        int centerRow = (int) ((cy - Y_OFFSET) / TILE_SIZE);

        int[][] dirs = { { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 } };
        for (int[] d : dirs) {
            int nr = centerRow + d[0];
            int nc = centerCol + d[1];
            if (nr >= 0 && nr < mapGrid.length && nc >= 0 && nc < mapGrid[0].length
                    && mapGrid[nr][nc] != 1 && mapGrid[nr][nc] != 10) {
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

    /** Returns the immediate next step of the shortest BFS path from start to target, or null. */
    public int[] getNextMove(int startR, int startC, int targetR, int targetC) {
        if (startR == targetR && startC == targetC) return new int[]{startR, startC};

        int rows = mapGrid.length;
        int cols = mapGrid[0].length;
        boolean[][] visited = new boolean[rows][cols];
        Queue<Node> queue = new LinkedList<>();

        queue.add(new Node(startR, startC, null));
        visited[startR][startC] = true;

        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};
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
                        && !visited[nr][nc] && mapGrid[nr][nc] != 1 && mapGrid[nr][nc] != 10) {

                    visited[nr][nc] = true;
                    queue.add(new Node(nr, nc, curr));
                }
            }
        }

        if (targetNode == null) return null;

        Node step = targetNode;
        while (step.parent != null && step.parent.parent != null) {
            step = step.parent;
        }

        return new int[]{step.r, step.c};
    }

    /** Returns true if a straight-line ray from (x1,y1) to (x2,y2) crosses no walls. */
    public boolean hasLineOfSight(double x1, double y1, double x2, double y2) {
        if (mapGrid == null) return false;

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
                if (mapGrid[row][col] == 1 || mapGrid[row][col] == 10) return false;
            }
        }

        return true;
    }

    /** Returns a list of [row, col] arrays for every tile matching the given type. */
    public List<int[]> getTilesOfType(int type) {
        List<int[]> positions = new ArrayList<>();
        if (mapGrid == null) {
            return positions;
        }

        for (int r = 0; r < mapGrid.length; r++) {
            for (int c = 0; c < mapGrid[r].length; c++) {
                if (mapGrid[r][c] == type) {
                    positions.add(new int[]{r, c});
                }
            }
        }
        return positions;
    }

    /** Returns the tile type at the given world pixel coordinates, or -1 if out of bounds. */
    public int getTileAt(double worldX, double worldY) {
        int col = (int) (worldX / TILE_SIZE);
        int row = (int) ((worldY - Y_OFFSET) / TILE_SIZE);

        if (mapGrid == null || row < 0 || row >= mapGrid.length || col < 0 || col >= mapGrid[0].length) {
            return -1;
        }

        return mapGrid[row][col];
    }

    /** Returns [row, col] grid indices for the given world pixel coordinates, or null if out of bounds. */
    public int[] getTilePositionAt(double worldX, double worldY) {
        int col = (int) (worldX / TILE_SIZE);
        int row = (int) ((worldY - Y_OFFSET) / TILE_SIZE);

        if (mapGrid == null || row < 0 || row >= mapGrid.length || col < 0 || col >= mapGrid[0].length) {
            return null;
        }

        return new int[]{row, col};
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

    public int[][] getMapGrid()           { return mapGrid; }
    public int getPlayerSpawnRow()        { return playerSpawnRow; }
    public int getPlayerSpawnCol()        { return playerSpawnCol; }
}
