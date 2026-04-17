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

import com.nsu.cse215l.redlolli.redlolli.systems.*;

/**
 * Manages all game state and logic extracted from HelloApplication.
 * Handles entity spawning, per-frame updates, collision checks, and debug
 * overlay.
 */
public class GameStateManager {

public int totalChestsCollected = 0;
    public int totalChestsEncountered = 0;
    public double totalPlayTimeSeconds = 0;

    public final EntityManager entityManager = new EntityManager();
    public final LevelManager levelManager = new LevelManager();
    public final CollisionSystem collisionSystem = new CollisionSystem();

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
        entityManager.clear();
        overlays.clear();

        showingItemFound = false;
        warningFlashTimer = 0;
        pulsePhaseHUD = 0;
        lolliRevealState = null;
        activeDeathMessage = "";
        lolliRecentlyCollected = false;

        if (levelManager.getCurrentLevel() == 1) {
            startingDistractions = 1;
            totalPlayTimeSeconds = 0;
            totalChestsCollected = 0;
            totalChestsEncountered = 0;
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
        if (entityManager.getPlayer() != null) {
            entityManager.getPlayer().resetSanity();
        }
    }

    void loadLevel() {
        levelManager.loadLevel(entityManager);
        totalChestsEncountered += entityManager.getChests().size();
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

        totalPlayTimeSeconds += dtSeconds;

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

        if (entityManager.getSerialKiller() != null)
            entityManager.getSerialKiller().update();
        for (GuardEntity guard : entityManager.getGuards())
            guard.update();
        for (TorchEntity torch : entityManager.getTorches())
            torch.update();

        // Update player's near-Luna status for sanity drain
        if (entityManager.getPaleLuna() != null) {
            entityManager.getPlayer().updateNearLunaStatus(entityManager.getPaleLuna().getX(), entityManager.getPaleLuna().getY());
        }

        entityManager.getPlayer().update();

        // Check for sanity death
        if (entityManager.getPlayer().isSanityDead()) {
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
        boolean sprinting = activeKeys.contains(KeyCode.SHIFT) && entityManager.getPlayer().canSprint();
        double beforeX = entityManager.getPlayer().getX(), beforeY = entityManager.getPlayer().getY();
        if (activeKeys.contains(KeyCode.W))
            entityManager.getPlayer().move(0, -1, levelManager.getMaze(), sprinting);
        if (activeKeys.contains(KeyCode.S))
            entityManager.getPlayer().move(0, 1, levelManager.getMaze(), sprinting);
        if (activeKeys.contains(KeyCode.A))
            entityManager.getPlayer().move(-1, 0, levelManager.getMaze(), sprinting);
        if (activeKeys.contains(KeyCode.D))
            entityManager.getPlayer().move(1, 0, levelManager.getMaze(), sprinting);

        boolean moved = Math.abs(entityManager.getPlayer().getX() - beforeX) > 0.01 || Math.abs(entityManager.getPlayer().getY() - beforeY) > 0.01;
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
        boolean inEscapeRoom = levelManager.getMaze().isEscapeRoom(entityManager.getPlayer().getHitbox());
        boolean enteringEscapeRoom = !wasInEscapeRoom && inEscapeRoom;
        boolean exitingEscapeRoom = wasInEscapeRoom && !inEscapeRoom;
        entityManager.getPlayer().setInEscapeRoom(inEscapeRoom);

        // Update escape room visual state (open door when player inside)
        levelManager.getMaze().updateEscapeRoomState(entityManager.getPlayer().getX(), entityManager.getPlayer().getY());

        if (exitingEscapeRoom) {
            exitGraceFrames = 45;
        }
        wasInEscapeRoom = inEscapeRoom;

        // Collision & threat checks
        collisionSystem.checkChestCollisions(entityManager, hasCloneItem);
        if (collisionSystem.collectedChests > 0) {
            totalChestsCollected += collisionSystem.collectedChests;
            soundManager.playOneShot(SoundManager.CHEST_OPEN, 0.65);
        }
        if (collisionSystem.lolliRecentlyCollected) {
            lolliRecentlyCollected = true;
            soundManager.playOneShot(SoundManager.STINGER_1, 0.8);
            lolliRevealState = collisionSystem.lolliRevealState;
            distractionSpellCount += collisionSystem.newDistractions;
            return false;
        }
        distractionSpellCount += collisionSystem.newDistractions;
        hasCloneItem = collisionSystem.hasCloneItem;

        collisionSystem.playerDied = false;
        collisionSystem.checkGuardThreats(entityManager, inEscapeRoom, enteringEscapeRoom, guardHitCooldownFrames);
        if (collisionSystem.playerDied) return triggerPlayerDeath(collisionSystem.deathMessage);

        collisionSystem.playerDied = false;
        collisionSystem.updateSerialKiller(entityManager, levelManager.getMaze());
        if (collisionSystem.playerDied) return triggerPlayerDeath(collisionSystem.deathMessage);

        collisionSystem.playerDied = false;
        collisionSystem.updatePaleLuna(entityManager, levelManager.getMaze(), inEscapeRoom, exitingEscapeRoom, lolliRecentlyCollected, lunaScreamCooldownFrames);
        if (collisionSystem.playHeartbeat) {
            warningFlashTimer = 30;
            soundManager.playOneShot(SoundManager.HEARTBEAT_FAST, 0.45);
        }
        if (collisionSystem.screenShake) screenShakeFrames = 15;
        if (collisionSystem.playScream) {
            soundManager.playOneShot(SoundManager.LUNA_SCREAM_NEARBY, 0.8);
            lunaScreamCooldownFrames = 130;
        }
        if (collisionSystem.playerDied) return triggerPlayerDeath(collisionSystem.deathMessage);

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
        for (GuardEntity guard : entityManager.getGuards()) {
            if (guard.isDistracted())
                continue;
            double d = distInTiles(entityManager.getPlayer().getX(), entityManager.getPlayer().getY(), guard.getX(), guard.getY());
            if (d < best && guard.isWithinDistractionRange(entityManager.getPlayer().getX(), entityManager.getPlayer().getY())) {
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
        if (!hasCloneItem || levelManager.getCurrentLevel() != 3 || entityManager.getCloneDecoy() != null)
            return;
        CardboardClone cloneDecoy = new CardboardClone(entityManager.getPlayer().getX() + 5, entityManager.getPlayer().getY() + 5);
        entityManager.setCloneDecoy(cloneDecoy);
        entityManager.addEntity(cloneDecoy);
        hasCloneItem = false;
        soundManager.playOneShot(SoundManager.CHEST_OPEN, 0.45);
    }

    // ========================= RENDERING =========================

    void drawDebugOverlay(GraphicsContext gc, Set<KeyCode> activeKeys) {
        Monster paleLuna = entityManager.getPaleLuna();
        Player player = entityManager.getPlayer();
        Maze maze = levelManager.getMaze();

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
                "Level=" + levelManager.getCurrentLevel() + " Tile=" + tileText + " InEscape=" + player.isInEscapeRoom(),
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
        if (entityManager.getPaleLuna() == null)
            return;
        entityManager.getPaleLuna().setPosition(entityManager.getPlayer().getX() + 36, entityManager.getPlayer().getY());
    }

    static double distInTiles(double x1, double y1, double x2, double y2) {
        double dx = (x1 - x2) / Maze.TILE_SIZE, dy = (y1 - y2) / Maze.TILE_SIZE;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
