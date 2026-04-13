package com.nsu.cse215l.redlolli.redlolli;

import com.nsu.cse215l.redlolli.redlolli.entities.*;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import com.nsu.cse215l.redlolli.redlolli.systems.SoundManager;
import com.nsu.cse215l.redlolli.redlolli.ui.GameRenderer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Manages all game state and logic extracted from HelloApplication.
 * Handles entity spawning, per-frame updates, collision checks, and debug
 * overlay.
 */
public class GameStateManager {

    private static final String[] MAP_FILES = { "/map.csv", "/map2.csv", "/map3.csv" };

    Player player;
    Maze maze;
    Monster paleLuna;
    SerialKillerEntity serialKiller;
    CardboardClone cloneDecoy;

    final List<Entity> entities = new ArrayList<>();
    final List<Item> chests = new ArrayList<>();
    final List<GuardEntity> guards = new ArrayList<>();

    int currentLevel = 1;
    boolean showingItemFound = false;
    int warningFlashTimer = 0;
    double pulsePhaseHUD = 0;
    String activeDeathMessage = "";

    boolean lolliRecentlyCollected = false;
    GameRenderer.LolliRevealState lolliRevealState = null;

    int fruitCount = 0;
    int eggCount = 0;
    int exitGraceFrames = 0;
    int standStillFrames = 0;
    int guardHitCooldownFrames = 0;
    int footstepCooldownFrames = 0;
    int lunaScreamCooldownFrames = 0;

    boolean hasCloneItem = false;
    boolean wasInEscapeRoom = false;
    boolean escapeRoomsCollapsed = false;

    final SoundManager soundManager = new SoundManager();

    // ========================= LIFECYCLE =========================

    void resetGameState() {
        entities.clear();
        chests.clear();
        guards.clear();

        paleLuna = null;
        serialKiller = null;
        cloneDecoy = null;

        showingItemFound = false;
        warningFlashTimer = 0;
        pulsePhaseHUD = 0;
        lolliRevealState = null;
        activeDeathMessage = "";
        lolliRecentlyCollected = false;

        fruitCount = currentLevel == 1 ? 2 : 0;
        eggCount = currentLevel == 2 ? 5 : 0;
        exitGraceFrames = 0;
        standStillFrames = 0;
        guardHitCooldownFrames = 0;
        footstepCooldownFrames = 0;
        lunaScreamCooldownFrames = 0;
        hasCloneItem = false;
        wasInEscapeRoom = false;
        escapeRoomsCollapsed = false;
    }

    void loadLevel() {
        maze = new Maze(MAP_FILES[currentLevel - 1], currentLevel);
        double spawnX = maze.getPlayerSpawnCol() * Maze.TILE_SIZE + 10;
        double spawnY = maze.getPlayerSpawnRow() * Maze.TILE_SIZE + Maze.Y_OFFSET + 10;
        player = new Player(spawnX, spawnY);
        entities.add(player);
        spawnEntities();
        spawnLevelThreats();
    }

    // ========================= SPAWNING =========================

    private void spawnEntities() {
        int[][] grid = maze.getMapGrid();
        List<int[]> emptyChestTiles = new ArrayList<>();
        List<int[]> lolliChestTiles = new ArrayList<>();
        int lunaRow = -1, lunaCol = -1;

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                int tile = grid[row][col];
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
                }
            }
        }

        for (int[] pos : emptyChestTiles) {
            Item.ContentType type = Item.ContentType.EMPTY;
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

        if (lunaRow >= 0) {
            paleLuna = new Monster(lunaCol * Maze.TILE_SIZE + 7.5, lunaRow * Maze.TILE_SIZE + Maze.Y_OFFSET + 7.5);
            entities.add(paleLuna);
        }
    }

    private boolean containsContent(Item.ContentType type) {
        return chests.stream().anyMatch(c -> c.getContentType() == type);
    }

    private void spawnLevelThreats() {
        List<int[]> escapeRooms = maze.getTilesOfType(6);
        if (currentLevel == 1) {
            for (int i = 0, count = Math.min(2, escapeRooms.size()); i < count; i++) {
                int[] p = escapeRooms.get(i);
                double[] pos = findGuardPosition(p[0], p[1]);
                GuardEntity bat = new GuardEntity(pos[0], pos[1], GuardEntity.Type.BAT, p[0], p[1]);
                guards.add(bat);
                entities.add(bat);
            }
        } else if (currentLevel == 2) {
            for (int i = 0, count = Math.min(3, escapeRooms.size()); i < count; i++) {
                int[] p = escapeRooms.get(i);
                double[] pos = findGuardPosition(p[0], p[1]);
                GuardEntity cobra = new GuardEntity(pos[0], pos[1], GuardEntity.Type.COBRA, p[0], p[1]);
                guards.add(cobra);
                entities.add(cobra);
            }
        } else if (currentLevel == 3) {
            serialKiller = new SerialKillerEntity(18 * Maze.TILE_SIZE + 6, 15 * Maze.TILE_SIZE + Maze.Y_OFFSET + 6);
            entities.add(serialKiller);
        }
    }

    private double[] findGuardPosition(int row, int col) {
        int[][] g = maze.getMapGrid();
        int[][] dirs = { { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 } };
        for (int[] d : dirs) {
            int nr = row + d[0], nc = col + d[1];
            if (nr < 0 || nr >= g.length || nc < 0 || nc >= g[0].length)
                continue;
            if (g[nr][nc] != 1 && g[nr][nc] != 10) {
                return new double[] { nc * Maze.TILE_SIZE + 10, nr * Maze.TILE_SIZE + Maze.Y_OFFSET + 10 };
            }
        }
        return new double[] { col * Maze.TILE_SIZE + 10, row * Maze.TILE_SIZE + Maze.Y_OFFSET + 10 };
    }

    // ========================= FRAME UPDATE =========================

    /** Returns true if the player died this frame. */
    boolean update(Set<KeyCode> activeKeys) {
        if (showingItemFound)
            return false;

        if (lolliRevealState != null && lolliRevealState.active) {
            lolliRevealState.timer--;
            lolliRevealState.phase += 0.15;
            if (lolliRevealState.timer <= 0) {
                lolliRevealState.active = false;
                return false; // signal HelloApplication to show item-found screen
            }
            return false;
        }

        if (exitGraceFrames > 0)
            exitGraceFrames--;
        if (guardHitCooldownFrames > 0)
            guardHitCooldownFrames--;
        if (lunaScreamCooldownFrames > 0)
            lunaScreamCooldownFrames--;

        if (serialKiller != null)
            serialKiller.update();
        for (GuardEntity guard : guards)
            guard.update();
        player.update();

        // Stand-still penalty
        boolean movingInput = activeKeys.contains(KeyCode.W) || activeKeys.contains(KeyCode.A)
                || activeKeys.contains(KeyCode.S) || activeKeys.contains(KeyCode.D);
        if (movingInput) {
            standStillFrames = 0;
        } else {
            standStillFrames++;
            if (standStillFrames == 1800)
                teleportLunaNearPlayer();
        }

        // Movement
        boolean sprinting = activeKeys.contains(KeyCode.SHIFT) && player.canSprint();
        double beforeX = player.getX(), beforeY = player.getY();
        if (activeKeys.contains(KeyCode.W))
            player.move(0, -1, maze, sprinting);
        if (activeKeys.contains(KeyCode.S))
            player.move(0, 1, maze, sprinting);
        if (activeKeys.contains(KeyCode.A))
            player.move(-1, 0, maze, sprinting);
        if (activeKeys.contains(KeyCode.D))
            player.move(1, 0, maze, sprinting);

        boolean moved = Math.abs(player.getX() - beforeX) > 0.01 || Math.abs(player.getY() - beforeY) > 0.01;
        if (moved) {
            if (footstepCooldownFrames <= 0) {
                soundManager.playOneShot(SoundManager.FOOTSTEP, 0.25);
                footstepCooldownFrames = sprinting ? 9 : 15;
            } else {
                footstepCooldownFrames--;
            }
        } else {
            footstepCooldownFrames = Math.max(0, footstepCooldownFrames - 1);
        }

        // Escape room state
        boolean inEscapeRoom = maze.isEscapeRoom(player.getHitbox());
        boolean enteringEscapeRoom = !wasInEscapeRoom && inEscapeRoom;
        boolean exitingEscapeRoom = wasInEscapeRoom && !inEscapeRoom;
        player.setInEscapeRoom(inEscapeRoom);

        if (exitingEscapeRoom) {
            exitGraceFrames = 45;
            if (currentLevel == 3 && !escapeRoomsCollapsed) {
                maze.collapseEscapeRooms();
                escapeRoomsCollapsed = true;
            }
        }
        wasInEscapeRoom = inEscapeRoom;

        // Collision & threat checks
        checkChestCollisions();
        if (checkGuardThreats(enteringEscapeRoom))
            return true;
        if (updateSerialKiller())
            return true;
        if (updatePaleLuna(inEscapeRoom, exitingEscapeRoom))
            return true;

        lolliRecentlyCollected = false;
        return false;
    }

    /**
     * Returns true if the lolli reveal just finished this frame (needs item-found
     * screen).
     */
    boolean isLolliRevealJustFinished() {
        return lolliRevealState != null && !lolliRevealState.active;
    }

    // ========================= COLLISION / THREATS =========================

    private void checkChestCollisions() {
        for (Item chest : chests) {
            if (chest.isCollected() || !player.getHitbox().intersects(chest.getHitbox()))
                continue;
            chest.collect();
            soundManager.playOneShot(SoundManager.CHEST_OPEN, 0.65);
            if (chest.getContentType() == Item.ContentType.LOLLI) {
                lolliRecentlyCollected = true;
                soundManager.playOneShot(SoundManager.STINGER_1, 0.8);
                lolliRevealState = new GameRenderer.LolliRevealState(chest.getX(), chest.getY(), 120);
                return;
            }
            if (chest.getContentType() == Item.ContentType.CLONE_DECOY)
                hasCloneItem = true;
        }
    }

    /** Returns true if player died. */
    private boolean checkGuardThreats(boolean enteringEscapeRoom) {
        if (guardHitCooldownFrames > 0 || !enteringEscapeRoom)
            return false;
        for (GuardEntity guard : guards) {
            if (!guard.isDistracted() && guard.isPlayerOnGuardedRoom(player.getHitbox())) {
                activeDeathMessage = guard.getType() == GuardEntity.Type.BAT
                        ? "The bat bit first. Luna answered instantly."
                        : "The snake was still hungry. No egg, no escape.";
                return true;
            }
        }
        return false;
    }

    /** Returns true if player died. */
    private boolean updateSerialKiller() {
        if (serialKiller == null)
            return false;
        if (!serialKiller.isActive()) {
            if (distInTiles(player.getX(), player.getY(), serialKiller.getX(), serialKiller.getY()) < 8.0)
                serialKiller.setActive(true);
        }
        if (!serialKiller.isActive())
            return false;

        double targetX = cloneDecoy != null ? cloneDecoy.getX() : player.getX();
        double targetY = cloneDecoy != null ? cloneDecoy.getY() : player.getY();
        serialKiller.updateChase(targetX, targetY, maze);

        if (cloneDecoy != null && serialKiller.getHitbox().intersects(cloneDecoy.getHitbox())) {
            serialKiller.startDecoyAttack();
            entities.remove(cloneDecoy);
            cloneDecoy = null;
            return false;
        }
        if (!serialKiller.isAttackingDecoy() && serialKiller.getHitbox().intersects(player.getHitbox())) {
            activeDeathMessage = "Steel and panic. He never stops hunting.";
            return true;
        }
        return false;
    }

    /** Returns true if player died. */
    private boolean updatePaleLuna(boolean inEscapeRoom, boolean exitingEscapeRoom) {
        if (paleLuna == null)
            return false;

        Monster.State prevState = paleLuna.getState();
        paleLuna.update(player.getX(), player.getY(), inEscapeRoom, lolliRecentlyCollected, maze);

        if (exitingEscapeRoom && paleLuna.isWaitingAtDoor()) {
            activeDeathMessage = "She waited at the door. You stepped out anyway.";
            return true;
        }
        if (prevState != Monster.State.HUNTING && paleLuna.getState() == Monster.State.HUNTING) {
            warningFlashTimer = 30;
            soundManager.playOneShot(SoundManager.HEARTBEAT_FAST, 0.45);
        }
        if (warningFlashTimer > 0)
            warningFlashTimer--;
        player.setBeingChased(paleLuna.isHunting());

        double lunaDistTiles = distInTiles(player.getX(), player.getY(), paleLuna.getX(), paleLuna.getY());
        if (paleLuna.isHunting() && lunaDistTiles <= 3.0 && lunaScreamCooldownFrames <= 0) {
            soundManager.playOneShot(SoundManager.LUNA_SCREAM_NEARBY, 0.8);
            lunaScreamCooldownFrames = 130;
        }
        if (!inEscapeRoom && paleLuna.isHunting() && player.getHitbox().intersects(paleLuna.getHitbox())) {
            activeDeathMessage = "She found your pulse before you heard her footsteps.";
            return true;
        }
        if (!inEscapeRoom && paleLuna.isWaitingAtDoor()) {
            boolean canSee = maze.hasLineOfSight(
                    paleLuna.getX() + 12, paleLuna.getY() + 12,
                    player.getX() + 10, player.getY() + 10);
            if (canSee) {
                activeDeathMessage = "She waited at the door. You stepped out anyway.";
                return true;
            }
        }
        return false;
    }

    // ========================= ACTIONS =========================

    void tryUseDistraction() {
        GuardEntity nearest = null;
        double best = Double.MAX_VALUE;
        for (GuardEntity guard : guards) {
            if (guard.isDistracted())
                continue;
            double d = distInTiles(player.getX(), player.getY(), guard.getX(), guard.getY());
            if (d < best && guard.isWithinDistractionRange(player.getX(), player.getY())) {
                best = d;
                nearest = guard;
            }
        }
        if (nearest == null)
            return;
        if (nearest.getType() == GuardEntity.Type.BAT) {
            if (fruitCount > 0) {
                fruitCount--;
                nearest.distract();
            }
        } else {
            if (eggCount > 0) {
                eggCount--;
                nearest.distract();
            }
        }
    }

    void tryPlaceClone() {
        if (!hasCloneItem || currentLevel != 3 || cloneDecoy != null)
            return;
        cloneDecoy = new CardboardClone(player.getX() + 5, player.getY() + 5);
        entities.add(cloneDecoy);
        hasCloneItem = false;
        soundManager.playOneShot(SoundManager.CHEST_OPEN, 0.45);
    }

    // ========================= RENDERING =========================

    void drawDebugOverlay(GraphicsContext gc, Set<KeyCode> activeKeys) {
        double lunaDist = paleLuna == null ? 99.0
                : distInTiles(player.getX(), player.getY(), paleLuna.getX(), paleLuna.getY());

        gc.setFill(Color.rgb(0, 0, 0, 0.62));
        gc.fillRect(8, 58, 360, 182);
        gc.setStroke(Color.rgb(180, 40, 40, 0.8));
        gc.setLineWidth(1.1);
        gc.strokeRect(8, 58, 360, 182);

        gc.setFill(Color.rgb(220, 220, 220));
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
        int[] tile = maze.getTilePositionAt(player.getX() + 10, player.getY() + 10);
        String tileText = tile == null ? "-" : tile[0] + "," + tile[1];
        String lunaState = paleLuna == null ? "NONE" : paleLuna.getState().name();
        int lunaTimer = 0;
        if (paleLuna != null) {
            switch (paleLuna.getState()) {
                case DORMANT -> lunaTimer = paleLuna.getDormantTimer();
                case STALKING -> lunaTimer = paleLuna.getStalkTimer();
                case HUNTING -> lunaTimer = paleLuna.getHuntTimer();
                case WAITING_AT_DOOR -> lunaTimer = paleLuna.getWaitTimer();
            }
        }
        String[] lines = {
                "DEBUG (F3)",
                "Level=" + currentLevel + " Tile=" + tileText + " InEscape=" + player.isInEscapeRoom(),
                "Sprint=" + activeKeys.contains(KeyCode.SHIFT),
                "Luna=" + lunaState + " Timer=" + lunaTimer + " Nearby=" + (lunaDist <= 5.0),
                "Fruit=" + fruitCount + " Eggs=" + eggCount + " Clone=" + hasCloneItem,
                "GuardCD=" + guardHitCooldownFrames
        };
        double y = 76;
        for (String line : lines) {
            gc.fillText(line, 16, y);
            y += 18;
        }
    }

    // ========================= UTILITIES =========================

    private void teleportLunaNearPlayer() {
        if (paleLuna == null)
            return;
        paleLuna.setPosition(player.getX() + 36, player.getY());
    }

    static double distInTiles(double x1, double y1, double x2, double y2) {
        double dx = (x1 - x2) / Maze.TILE_SIZE, dy = (y1 - y2) / Maze.TILE_SIZE;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
