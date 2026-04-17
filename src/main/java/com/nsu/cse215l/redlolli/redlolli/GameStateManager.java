package com.nsu.cse215l.redlolli.redlolli;

import com.nsu.cse215l.redlolli.redlolli.entities.*;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import com.nsu.cse215l.redlolli.redlolli.systems.SoundManager;
import com.nsu.cse215l.redlolli.redlolli.ui.GameRenderer;
import com.nsu.cse215l.redlolli.redlolli.ui.HUDRenderer;

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
    final List<TorchEntity> torches = new ArrayList<>();

    int currentLevel = 1;
    boolean showingItemFound = false;
    double warningFlashTimer = 0;
    double pulsePhaseHUD = 0;
    String activeDeathMessage = "";

    boolean lolliRecentlyCollected = false;
    GameRenderer.LolliRevealState lolliRevealState = null;

    int distractionSpellCount = 0;
    int startingDistractions = 1;
    double exitGraceFrames = 0;
    double standStillFrames = 0;
    double guardHitCooldownFrames = 0;
    double footstepCooldownFrames = 0;
    double lunaScreamCooldownFrames = 0;
    double screenShakeFrames = 0; // For screen shake effect
    double playerDeathAnimFrames = 0; // Death animation counter (screen fills red)
    boolean playerIsDead = false; // Persistent flag to prevent re-triggering death

    private long lastUpdateTime = 0;
    private double timeDelta = 1.0;

    boolean hasCloneItem = false;
    boolean wasInEscapeRoom = false;
    boolean escapeRoomsCollapsed = false;

    final List<GameRenderer.Overlay> overlays = new ArrayList<>();

    final SoundManager soundManager = new SoundManager();

    // ========================= LIFECYCLE =========================

    void resetGameState() {
        entities.clear();
        chests.clear();
        guards.clear();
        torches.clear();
        overlays.clear();

        paleLuna = null;
        serialKiller = null;
        cloneDecoy = null;

        showingItemFound = false;
        warningFlashTimer = 0;
        pulsePhaseHUD = 0;
        lolliRevealState = null;
        activeDeathMessage = "";
        lolliRecentlyCollected = false;

        if (currentLevel == 1) {
            startingDistractions = 1;
        }

        distractionSpellCount = startingDistractions;
        exitGraceFrames = 0;
        standStillFrames = 0;
        guardHitCooldownFrames = 0;
        footstepCooldownFrames = 0;
        lunaScreamCooldownFrames = 0;
        screenShakeFrames = 0;
        playerDeathAnimFrames = 0;
        playerIsDead = false;
        hasCloneItem = false;
        wasInEscapeRoom = false;
        escapeRoomsCollapsed = false;
        lastUpdateTime = 0;

        // Reset player sanity
        if (player != null) {
            player.resetSanity();
        }
    }

    void loadLevel() {
        Maze.initImages();
        Item.initImages();
        CardboardClone.initImages();
        GuardEntity.initImages();
        SerialKillerEntity.initImages();
        Player.initImages();
        Monster.initImages();
        TorchEntity.initImages();
        GameRenderer.initImages();
        HUDRenderer.initImages();

        maze = new Maze(MAP_FILES[currentLevel - 1], currentLevel);
        double spawnX = maze.getPlayerSpawnCol() * Maze.TILE_SIZE + 10;
        double spawnY = maze.getPlayerSpawnRow() * Maze.TILE_SIZE + Maze.Y_OFFSET + 10;
        player = new Player(spawnX, spawnY);
        entities.add(player);
        spawnEntities();
    }

    // ========================= SPAWNING =========================

    private void spawnEntities() {
        int[][] grid = maze.getMapGrid();
        List<int[]> emptyChestTiles = new ArrayList<>();
        List<int[]> lolliChestTiles = new ArrayList<>();
        List<int[]> torchTiles = new ArrayList<>();
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
                } else if (tile == 8) {
                    torchTiles.add(new int[] { row, col });
                    grid[row][col] = 1; // Turn back into a wall tile for rendering/collision
                } else if (tile == 9) {
                    int erRow = -1, erCol = -1;
                    List<int[]> escapeRooms = maze.getTilesOfType(6);
                    double minDist = Double.MAX_VALUE;
                    for (int[] er : escapeRooms) {
                        double dist = Math.pow(er[0] - row, 2) + Math.pow(er[1] - col, 2);
                        if (dist < minDist) {
                            minDist = dist;
                            erRow = er[0];
                            erCol = er[1];
                        }
                    }
                    GuardEntity guard = null;
                    if (currentLevel == 1) {
                        guard = new GuardEntity(col * Maze.TILE_SIZE + 10, row * Maze.TILE_SIZE + Maze.Y_OFFSET + 10,
                                GuardEntity.Type.BAT, erRow, erCol);
                    } else if (currentLevel == 2) {
                        guard = new GuardEntity(col * Maze.TILE_SIZE + 10, row * Maze.TILE_SIZE + Maze.Y_OFFSET + 10,
                                GuardEntity.Type.COBRA, erRow, erCol);
                    } else if (currentLevel == 3) {
                        guard = new GuardEntity(col * Maze.TILE_SIZE + 10, row * Maze.TILE_SIZE + Maze.Y_OFFSET + 10,
                                GuardEntity.Type.CENTIPEDE, erRow, erCol);
                    }
                    if (guard != null) {
                        guards.add(guard);
                    }
                    grid[row][col] = 0; // Turn into floor
                } else if (tile == 10) {
                    if (currentLevel == 3) {
                        serialKiller = new SerialKillerEntity(col * Maze.TILE_SIZE + 6,
                                row * Maze.TILE_SIZE + Maze.Y_OFFSET + 6);
                    }
                    grid[row][col] = 0; // Turn into floor
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

        for (int[] pos : torchTiles) {
            TorchEntity torch = new TorchEntity(pos[1] * Maze.TILE_SIZE, pos[0] * Maze.TILE_SIZE + Maze.Y_OFFSET);
            torches.add(torch);
            entities.add(torch);
        }

        if (lunaRow >= 0) {
            paleLuna = new Monster(lunaCol * Maze.TILE_SIZE + 7.5, lunaRow * Maze.TILE_SIZE + Maze.Y_OFFSET + 7.5);
            entities.add(paleLuna);
        }

        // Add them to entities manually in the correct order to preserve optimal
        // Lighting BlendMode passes.
        for (GuardEntity guard : guards) {
            entities.add(guard);
        }

        if (serialKiller != null) {
            entities.add(serialKiller);
        }
    }

    private boolean containsContent(Item.ContentType type) {
        return chests.stream().anyMatch(c -> c.getContentType() == type);
    }

    // ========================= FRAME UPDATE =========================

    /** Triggers player death animation and sets death message. */
    private boolean triggerPlayerDeath(String message) {
        // Only trigger once — prevents re-triggering after animation finishes
        if (playerIsDead)
            return true;
        activeDeathMessage = message;
        playerDeathAnimFrames = 60; // 1 second death animation (60 frames)
        playerIsDead = true;
        return true;
    }

    /** Returns true if the player died this frame. */
    boolean update(Set<KeyCode> activeKeys) {
        long now = System.nanoTime();
        if (lastUpdateTime == 0) lastUpdateTime = now;
        double dtSeconds = (now - lastUpdateTime) / 1_000_000_000.0;
        lastUpdateTime = now;
        timeDelta = dtSeconds * 60.0;

        if (showingItemFound)
            return false;

        if (playerIsDead) {
            if (playerDeathAnimFrames > 0) {
                playerDeathAnimFrames -= timeDelta;
            }
            return true;
        }

        if (lolliRevealState != null && lolliRevealState.active) {
            lolliRevealState.timer -= timeDelta;
            lolliRevealState.phase += 0.15 * timeDelta;
            if (lolliRevealState.timer <= 0) {
                lolliRevealState.active = false;
                return false; // signal HelloApplication to show item-found screen
            }
            return false;
        }

        if (exitGraceFrames > 0)
            exitGraceFrames -= timeDelta;
        if (guardHitCooldownFrames > 0)
            guardHitCooldownFrames -= timeDelta;
        if (lunaScreamCooldownFrames > 0)
            lunaScreamCooldownFrames -= timeDelta;
        if (screenShakeFrames > 0)
            screenShakeFrames -= timeDelta;

        if (serialKiller != null)
            serialKiller.update();
        for (GuardEntity guard : guards)
            guard.update();
        for (TorchEntity torch : torches)
            torch.update();

        // Update player's near-Luna status for sanity drain
        if (paleLuna != null) {
            player.updateNearLunaStatus(paleLuna.getX(), paleLuna.getY());
        }

        player.update();

        // Check for sanity death
        if (player.isSanityDead()) {
            return triggerPlayerDeath("Your mind broke before she could.");
        }

        // Stand-still penalty
        boolean movingInput = activeKeys.contains(KeyCode.W) || activeKeys.contains(KeyCode.A)
                || activeKeys.contains(KeyCode.S) || activeKeys.contains(KeyCode.D);
        if (movingInput) {
            standStillFrames = 0;
        } else {
            standStillFrames += timeDelta;
            if (standStillFrames >= 1800) {
                teleportLunaNearPlayer();
                standStillFrames = 0;
            }
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
                footstepCooldownFrames -= timeDelta;
            }
        } else {
            footstepCooldownFrames = Math.max(0, footstepCooldownFrames - timeDelta);
        }

        // Escape room state
        boolean inEscapeRoom = maze.isEscapeRoom(player.getHitbox());
        boolean enteringEscapeRoom = !wasInEscapeRoom && inEscapeRoom;
        boolean exitingEscapeRoom = wasInEscapeRoom && !inEscapeRoom;
        player.setInEscapeRoom(inEscapeRoom);

        // Update escape room visual state (open door when player inside)
        maze.updateEscapeRoomState(player.getX(), player.getY());

        if (exitingEscapeRoom) {
            exitGraceFrames = 45;
        }
        wasInEscapeRoom = inEscapeRoom;

        // Collision & threat checks
        checkChestCollisions();
        if (checkGuardThreats(inEscapeRoom, enteringEscapeRoom))
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
                distractionSpellCount += 3;
                return;
            }
            if (chest.getContentType() == Item.ContentType.EMPTY) {
                distractionSpellCount++;
            }
            if (chest.getContentType() == Item.ContentType.CLONE_DECOY)
                hasCloneItem = true;
        }
    }

    /** Returns true if player died. */
    private boolean checkGuardThreats(boolean inEscapeRoom, boolean enteringEscapeRoom) {
        if (guardHitCooldownFrames > 0)
            return false;

        // Player is safely inside the escape room and not actively crossing the
        // threshold
        if (inEscapeRoom && !enteringEscapeRoom) {
            return false;
        }

        for (GuardEntity guard : guards) {
            // Guard kills if not distracted and player touches the guarded room or the
            // guard itself
            if (!guard.isDistracted() && (guard.isPlayerOnGuardedRoom(player.getHitbox())
                    || guard.getHitbox().intersects(player.getHitbox()))) {
                String msg;
                if (guard.getType() == GuardEntity.Type.BAT)
                    msg = "The bat bit first. Luna answered instantly.";
                else if (guard.getType() == GuardEntity.Type.COBRA)
                    msg = "The snake strikes! No spell cast, no escape.";
                else
                    msg = "The centipede swarmed you... the darkness follows.";
                return triggerPlayerDeath(msg);
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

        if (cloneDecoy != null) {
            if (!serialKiller.isAttackingDecoy() && serialKiller.getHitbox().intersects(cloneDecoy.getHitbox())) {
                serialKiller.startDecoyAttack();
            } else if (serialKiller.isAttackingDecoy() && serialKiller.getDecoyAttackFrames() <= 1) {
                // Remove the clone right as the attack finishes
                entities.remove(cloneDecoy);
                cloneDecoy = null;
            }
        }

        if (!serialKiller.isAttackingDecoy() && serialKiller.getHitbox().intersects(player.getHitbox())) {
            return triggerPlayerDeath("Steel and panic. He never stops hunting.");
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
            return triggerPlayerDeath("She waited at the door. You stepped out anyway.");
        }
        if (prevState != Monster.State.HUNTING && paleLuna.getState() == Monster.State.HUNTING) {
            warningFlashTimer = 30;
            screenShakeFrames = 15; // Screen shake for 15 frames when Luna starts hunting
            soundManager.playOneShot(SoundManager.HEARTBEAT_FAST, 0.45);
        }
        if (warningFlashTimer > 0)
            warningFlashTimer -= timeDelta;
        player.setBeingChased(paleLuna.isHunting());

        double lunaDistTiles = distInTiles(player.getX(), player.getY(), paleLuna.getX(), paleLuna.getY());
        if (paleLuna.isHunting() && lunaDistTiles <= 3.0 && lunaScreamCooldownFrames <= 0) {
            soundManager.playOneShot(SoundManager.LUNA_SCREAM_NEARBY, 0.8);
            lunaScreamCooldownFrames = 130;
        }
        if (!inEscapeRoom && paleLuna.isHunting() && player.getHitbox().intersects(paleLuna.getHitbox())) {
            return triggerPlayerDeath("She found your pulse before you heard her footsteps.");
        }
        if (!inEscapeRoom && paleLuna.isWaitingAtDoor()) {
            boolean canSee = maze.hasLineOfSight(
                    paleLuna.getX() + 12, paleLuna.getY() + 12,
                    player.getX() + 10, player.getY() + 10);
            if (canSee) {
                return triggerPlayerDeath("She waited at the door. You stepped out anyway.");
            }
        }
        return false;
    }

    // ========================= ACTIONS =========================

    /**
     * Adds a manual image overlay to the current frame. Call each frame for
     * persistent overlays.
     */
    public void addOverlay(javafx.scene.image.Image image, double x, double y, double width, double height,
            double opacity) {
        GameRenderer.Overlay o = new GameRenderer.Overlay(image, x, y, width, height);
        o.setOpacity(opacity);
        overlays.add(o);
    }

    /** Adds a manual overlay at world (maze) coordinates. */
    public void addWorldOverlay(javafx.scene.image.Image image, double worldX, double worldY, double width,
            double height, double opacity) {
        GameRenderer.Overlay o = new GameRenderer.Overlay(image, worldX, worldY, width, height, true);
        o.setOpacity(opacity);
        overlays.add(o);
    }

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
        if (distractionSpellCount > 0) {
            distractionSpellCount--;
            nearest.distract();
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
                "Spells=" + distractionSpellCount + " Clone=" + hasCloneItem,
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
