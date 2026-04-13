package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;

/**
 * The primary antagonist entity for Level 1, "Pale Luna".
 * Operates on a time-based 4-state AI cycle with BFS pathfinding.
 */
public class Monster extends Entity implements Collidable {

    public enum State {
        DORMANT, STALKING, HUNTING, WAITING_AT_DOOR
    }

    private State state = State.DORMANT;

    private static final double STALK_SPEED = 1.6;
    private static final double HUNT_SPEED = 3.2;
    private static final int DORMANT_DURATION = 900;
    private static final int STALK_DURATION = 480;
    private static final int HUNT_DURATION = 360;
    private static final int WAIT_DURATION = 180;

    private int dormantTimer = 0;
    private int stalkTimer = 0;
    private int huntTimer = 0;
    private int waitTimer = 0;
    private double pulsePhase = 0.0;

    public Monster(double x, double y) {
        super(x, y, 25.0);
        this.dormantTimer = DORMANT_DURATION;
    }

    /** Main AI tick evaluated every graphical frame. */
    @Override
    public void update() {
    }

    public void update(double playerX, double playerY, boolean playerInEscapeRoom,
            boolean lolliRecentlyCollected, Maze maze) {

        pulsePhase += 0.1;

        switch (state) {
            case DORMANT -> {
                dormantTimer--;
                if (dormantTimer <= 0 || lolliRecentlyCollected) {
                    state = State.STALKING;
                    stalkTimer = STALK_DURATION;
                }
            }
            case STALKING -> {
                if (playerInEscapeRoom) {
                    positionAtDoor(playerX, playerY, maze);
                    state = State.WAITING_AT_DOOR;
                    waitTimer = WAIT_DURATION;
                    break;
                }

                pursuePlayer(playerX, playerY, maze, STALK_SPEED);

                stalkTimer--;
                if (stalkTimer <= 0) {
                    state = State.HUNTING;
                    huntTimer = HUNT_DURATION;
                }
            }
            case HUNTING -> {
                if (playerInEscapeRoom) {
                    positionAtDoor(playerX, playerY, maze);
                    state = State.WAITING_AT_DOOR;
                    waitTimer = WAIT_DURATION;
                    break;
                }

                pursuePlayer(playerX, playerY, maze, HUNT_SPEED);

                huntTimer--;
                if (huntTimer <= 0) {
                    returnToDormant();
                }
            }
            case WAITING_AT_DOOR -> {
                waitTimer--;
                if (waitTimer <= 0) {
                    returnToDormant();
                }
            }
        }
    }

    private void returnToDormant() {
        state = State.DORMANT;
        dormantTimer = DORMANT_DURATION;
    }

    /**
     * Positions Luna on a walkable tile adjacent to the player's safe-room
     * location.
     */
    private void positionAtDoor(double playerX, double playerY, Maze maze) {
        int playerCol = (int) (playerX / Maze.TILE_SIZE);
        int playerRow = (int) ((playerY - Maze.Y_OFFSET) / Maze.TILE_SIZE);

        int[][] dirs = { { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 },
                { -1, -1 }, { -1, 1 }, { 1, -1 }, { 1, 1 } };
        for (int[] d : dirs) {
            int nr = playerRow + d[0];
            int nc = playerCol + d[1];
            if (nr >= 0 && nr < maze.getMapGrid().length
                    && nc >= 0 && nc < maze.getMapGrid()[0].length
                    && maze.getMapGrid()[nr][nc] != 1 && maze.getMapGrid()[nr][nc] != 10) {
                this.x = nc * Maze.TILE_SIZE + (Maze.TILE_SIZE - size) / 2;
                this.y = nr * Maze.TILE_SIZE + Maze.Y_OFFSET + (Maze.TILE_SIZE - size) / 2;
                return;
            }
        }
        this.x = playerX + 40;
        this.y = playerY;
    }

    private void pursuePlayer(double playerX, double playerY, Maze maze, double speed) {
        int currentC = (int) ((this.x + size / 2) / Maze.TILE_SIZE);
        int currentR = (int) ((this.y + size / 2 - Maze.Y_OFFSET) / Maze.TILE_SIZE);
        int playerC = (int) ((playerX + 10) / Maze.TILE_SIZE);
        int playerR = (int) ((playerY + 10 - Maze.Y_OFFSET) / Maze.TILE_SIZE);

        int[] nextTile = maze.getNextMove(currentR, currentC, playerR, playerC);
        if (nextTile == null)
            return;

        double targetX, targetY;

        if (nextTile[0] == playerR && nextTile[1] == playerC) {
            targetX = playerX - (size / 2) + 10;
            targetY = playerY - (size / 2) + 10;
        } else {
            targetX = nextTile[1] * Maze.TILE_SIZE + (Maze.TILE_SIZE - size) / 2;
            targetY = nextTile[0] * Maze.TILE_SIZE + Maze.Y_OFFSET + (Maze.TILE_SIZE - size) / 2;
        }

        double stepDX = targetX - this.x;
        double stepDY = targetY - this.y;
        double stepDist = Math.sqrt(stepDX * stepDX + stepDY * stepDY);

        if (stepDist > 0) {
            double moveDist = Math.min(speed, stepDist);
            this.x += (stepDX / stepDist) * moveDist;
            this.y += (stepDY / stepDist) * moveDist;
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        if (state != State.DORMANT) {
            drawAura(gc);
        }
        drawHead(gc);
        drawHair(gc);
        drawMouth(gc);
    }

    private void drawAura(GraphicsContext gc) {
        double pulseOffset = Math.sin(pulsePhase) * 5;
        gc.setFill(Color.rgb(100, 0, 0, 0.35));
        gc.fillOval(x - pulseOffset - 2, y - pulseOffset - 2,
                size + (pulseOffset + 2) * 2, size + (pulseOffset + 2) * 2);
    }

    private void drawHead(GraphicsContext gc) {
        double headX = x + 2, headY = y + 1;
        double headW = size - 4, headH = size - 2;

        Color skinColor = (state == State.DORMANT)
                ? Color.rgb(220, 210, 200, 0.5)
                : Color.rgb(240, 230, 220);
        gc.setFill(skinColor);
        gc.fillOval(headX, headY, headW, headH);
    }

    private void drawHair(GraphicsContext gc) {
        double headX = x + 2, headY = y + 1;
        double headW = size - 4, headH = size - 2;

        Color hairColor = (state == State.DORMANT)
                ? Color.rgb(180, 160, 40, 0.5)
                : Color.rgb(220, 200, 60);
        gc.setFill(hairColor);
        gc.fillArc(headX - 1, headY - 3, headW + 2, headH * 0.6, 0, 180, ArcType.ROUND);
        gc.fillOval(headX - 2, headY + 2, 5, headH - 4);
        gc.fillOval(headX + headW - 3, headY + 2, 5, headH - 4);
    }

    private void drawMouth(GraphicsContext gc) {
        double headX = x + 2, headY = y + 1;
        double headW = size - 4, headH = size - 2;

        switch (state) {
            case HUNTING -> {
                gc.setStroke(Color.rgb(80, 0, 0));
                gc.setLineWidth(1.5);
                gc.strokeArc(headX + 4, headY + headH * 0.55, headW - 8, 6, 180, 180, ArcType.OPEN);
                gc.setStroke(Color.WHITE);
                gc.setLineWidth(0.5);
                double mouthY = headY + headH * 0.55 + 3;
                for (int i = 0; i < 4; i++) {
                    double tx = headX + 6 + i * 3;
                    gc.strokeLine(tx, mouthY - 1, tx, mouthY + 1);
                }
            }
            case WAITING_AT_DOOR -> {
                gc.setStroke(Color.rgb(100, 0, 0));
                gc.setLineWidth(1);
                gc.strokeArc(headX + 5, headY + headH * 0.58, headW - 10, 4, 180, 180, ArcType.OPEN);
            }
            case STALKING -> {
                gc.setStroke(Color.rgb(120, 20, 20));
                gc.setLineWidth(1.2);
                gc.strokeArc(headX + 4, headY + headH * 0.58, headW - 8, 4, 180, 180, ArcType.OPEN);
            }
            default -> {
                gc.setStroke(Color.rgb(150, 120, 120, 0.4));
                gc.setLineWidth(0.8);
                gc.strokeLine(headX + 7, headY + headH * 0.65, headX + headW - 7, headY + headH * 0.65);
            }
        }
    }

    public void renderEyes(GraphicsContext gc) {
        double headX = x + 2, headY = y + 1;
        double headW = size - 4, headH = size - 2;
        double eyeY = headY + headH * 0.35;
        double lx = headX + headW * 0.22, rx = headX + headW * 0.58;

        if (state == State.DORMANT) {
            gc.setFill(Color.rgb(150, 0, 0, 0.5));
            gc.fillOval(lx, eyeY + 1, 4, 2);
            gc.fillOval(rx, eyeY + 1, 4, 2);
            return;
        }

        double pulseSpeed, baseSz;
        Color glowCol, irisCol;
        boolean hasHighlight;
        switch (state) {
            case HUNTING -> {
                pulseSpeed = 3;
                baseSz = 5;
                glowCol = Color.rgb(255, 0, 0, 0.3);
                irisCol = Color.rgb(255, 20, 20);
                hasHighlight = true;
            }
            case WAITING_AT_DOOR -> {
                pulseSpeed = 0.8;
                baseSz = 4;
                glowCol = Color.rgb(40, 0, 0, 1.0);
                irisCol = Color.rgb(220, 0, 0);
                hasHighlight = true;
            }
            default /* STALKING */ -> {
                pulseSpeed = 1.4;
                baseSz = 3.6;
                glowCol = Color.rgb(180, 0, 0, 0.35);
                irisCol = Color.rgb(220, 40, 40, 0.85);
                hasHighlight = false;
            }
        }

        double pulse = Math.sin(pulsePhase * pulseSpeed)
                * (state == State.HUNTING ? 1.5 : state == State.WAITING_AT_DOOR ? 0.5 : 0.8);
        double sz = baseSz + pulse;

        gc.setFill(glowCol);
        gc.fillOval(lx - (state == State.HUNTING ? 3 : 1), eyeY - (state == State.HUNTING ? 3 : 1),
                sz + (state == State.HUNTING ? 6 : 2),
                sz + (state == State.HUNTING ? 5 : state == State.STALKING ? 2 : 1));
        gc.fillOval(rx - (state == State.HUNTING ? 3 : 1), eyeY - (state == State.HUNTING ? 3 : 1),
                sz + (state == State.HUNTING ? 6 : 2),
                sz + (state == State.HUNTING ? 5 : state == State.STALKING ? 2 : 1));

        if (state == State.HUNTING) {
            gc.setFill(Color.rgb(50, 0, 0));
            gc.fillOval(lx - 1, eyeY - 1, sz + 2, sz);
            gc.fillOval(rx - 1, eyeY - 1, sz + 2, sz);
        }

        gc.setFill(irisCol);
        gc.fillOval(lx, eyeY, sz, state == State.STALKING ? sz : sz - 1);
        gc.fillOval(rx, eyeY, sz, state == State.STALKING ? sz : sz - 1);

        if (hasHighlight) {
            Color hlCol = state == State.HUNTING ? Color.rgb(255, 200, 200) : Color.rgb(255, 100, 100);
            double hlOff = state == State.HUNTING ? 1.5 : 1;
            gc.setFill(hlCol);
            gc.fillOval(lx + hlOff, eyeY + (state == State.HUNTING ? 1 : 0.5), 2, 1.5);
            gc.fillOval(rx + hlOff, eyeY + (state == State.HUNTING ? 1 : 0.5), 2, 1.5);
        }
    }

    @Override
    public Rectangle2D getHitbox() {
        return new Rectangle2D(x, y, size, size);
    }

    public State getState() {
        return state;
    }

    public boolean isHunting() {
        return state == State.HUNTING;
    }

    public boolean isStalking() {
        return state == State.STALKING;
    }

    public boolean isWaitingAtDoor() {
        return state == State.WAITING_AT_DOOR;
    }

    public int getDormantTimer() {
        return dormantTimer;
    }

    public int getStalkTimer() {
        return stalkTimer;
    }

    public int getHuntTimer() {
        return huntTimer;
    }

    public int getWaitTimer() {
        return waitTimer;
    }
}