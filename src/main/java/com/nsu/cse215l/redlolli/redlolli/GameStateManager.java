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
 * The master brain of the game! This class manages what's happening globally,
 * like tracking timers, checking win/loss conditions, and telling other 
 * systems when to update.
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
    double screenShakeFrames = 0;
    double playerDeathAnimFrames = 0;
    boolean playerIsDead = false;

    private long lastUpdateTime = 0;
    private double timeDelta = 1.0;

    boolean hasCloneItem = false;
    boolean wasInEscapeRoom = false;
    boolean escapeRoomsCollapsed = false;

    final List<GameRenderer.Overlay> overlays = new ArrayList<>();

    final SoundManager soundManager = new SoundManager();

    /**
     * Resets all the game variables, clears out monsters, and basically 
     * gets everything ready for a fresh new run or level.
     */
    void resetGameState() {
        entityManager.clear();
        overlays.clear();

        showingItemFound = false;
        warningFlashTimer = 0;
        pulsePhaseHUD = 0;
        lolliRevealState = null;
        activeDeathMessage = "";
        lolliRecentlyCollected = false;

        // Reinitialize accumulators based on fundamental architectural boundaries
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

        // Reset psychological constraints natively verifying logic boundaries
        if (entityManager.getPlayer() != null) {
            entityManager.getPlayer().resetSanity();
        }
    }

    /**
     * Tells the LevelManager to build the actual 2D maze and spawn all 
     * the entities inside it!
     */
    void loadLevel() {
        levelManager.loadLevel(entityManager);
        totalChestsEncountered += entityManager.getChests().size();
    }

    /**
     * Triggers the player's death sequence and stops normal gameplay.
     * 
     * @param message The reason they died (e.g., eaten by a monster or went insane).
     * @return boolean Always returns true to indicate the player is dead!
     */
    private boolean triggerPlayerDeath(String message) {
        if (playerIsDead)
            return true;
        activeDeathMessage = message;
        playerDeathAnimFrames = 60;
        playerIsDead = true;
        return true;
    }

    /**
     * The main game loop tick! Runs every single frame to move characters, 
     * check collisions, and update timers.
     * 
     * @param activeKeys Which keyboard keys the player is currently holding down.
     * @return boolean True if the game loop should stop (like when dead), false otherwise.
     */
    boolean update(Set<KeyCode> activeKeys) {
        long now = System.nanoTime();
        if (lastUpdateTime == 0)
            lastUpdateTime = now;

        // Execute temporal coefficient normalization ensuring uniform AI trajectory
        // scaling
        double dtSeconds = (now - lastUpdateTime) / 1_000_000_000.0;
        lastUpdateTime = now;
        timeDelta = dtSeconds * 60.0;

        if (showingItemFound)
            return false;

        // Process discrete execution sequences prior to halting game cycle
        // fundamentally
        if (playerIsDead) {
            if (playerDeathAnimFrames > 0) {
                playerDeathAnimFrames -= timeDelta;
            }
            return true;
        }

        totalPlayTimeSeconds += dtSeconds;

        // Iteratively render priority sequential animations without progressing logical
        // matrices
        if (lolliRevealState != null && lolliRevealState.active) {
            lolliRevealState.timer -= timeDelta;
            lolliRevealState.phase += 0.15 * timeDelta;
            if (lolliRevealState.timer <= 0) {
                lolliRevealState.active = false;
                return false;
            }
            return false;
        }

        // Exhaust transient effect constraints proportionally based on active rendering
        // offsets
        if (exitGraceFrames > 0)
            exitGraceFrames -= timeDelta;
        if (guardHitCooldownFrames > 0)
            guardHitCooldownFrames -= timeDelta;
        if (lunaScreamCooldownFrames > 0)
            lunaScreamCooldownFrames -= timeDelta;
        if (screenShakeFrames > 0)
            screenShakeFrames -= timeDelta;

        // Transmit updated coordinate limits to respective subcomponents uniformly
        if (entityManager.getSerialKiller() != null)
            entityManager.getSerialKiller().update();
        for (GuardEntity guard : entityManager.getGuards())
            guard.update();
        for (TorchEntity torch : entityManager.getTorches())
            torch.update();

        // Integrate asynchronous physiological constraints checking deterministic
        // interaction logic
        if (entityManager.getPaleLuna() != null) {
            entityManager.getPlayer().updateNearLunaStatus(entityManager.getPaleLuna().getX(),
                    entityManager.getPaleLuna().getY());
        }

        entityManager.getPlayer().update();

        if (entityManager.getPlayer().isSanityDead()) {
            return triggerPlayerDeath("Your mind broke before she could.");
        }

        // Interrupt stationary matrix dependencies invoking dynamic hazard protocols
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

        // Apply discrete Cartesian derivation matrices yielding uniform kinetic
        // bounding globally
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

        // Disaggregate geometric constraints resolving auditory feedback dependencies
        // procedurally
        boolean moved = Math.abs(entityManager.getPlayer().getX() - beforeX) > 0.01
                || Math.abs(entityManager.getPlayer().getY() - beforeY) > 0.01;
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

        // Reconcile dynamic Boolean limits isolating sanctuary regions properly
        // organically
        boolean inEscapeRoom = levelManager.getMaze().isEscapeRoom(entityManager.getPlayer().getHitbox());
        boolean enteringEscapeRoom = !wasInEscapeRoom && inEscapeRoom;
        boolean exitingEscapeRoom = wasInEscapeRoom && !inEscapeRoom;
        entityManager.getPlayer().setInEscapeRoom(inEscapeRoom);

        levelManager.getMaze().updateEscapeRoomState(entityManager.getPlayer().getX(),
                entityManager.getPlayer().getY());

        if (exitingEscapeRoom) {
            exitGraceFrames = 45;
        }
        wasInEscapeRoom = inEscapeRoom;

        // Perform bounding box evaluations updating secondary logical entities linearly
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

        // Delegate specific hazard collision evaluations maintaining strict structural
        // decoupling
        collisionSystem.playerDied = false;
        collisionSystem.checkGuardThreats(entityManager, inEscapeRoom, enteringEscapeRoom, guardHitCooldownFrames);
        if (collisionSystem.playerDied)
            return triggerPlayerDeath(collisionSystem.deathMessage);

        collisionSystem.playerDied = false;
        collisionSystem.updateSerialKiller(entityManager, levelManager.getMaze());
        if (collisionSystem.playerDied)
            return triggerPlayerDeath(collisionSystem.deathMessage);

        collisionSystem.playerDied = false;
        collisionSystem.updatePaleLuna(entityManager, levelManager.getMaze(), inEscapeRoom, exitingEscapeRoom,
                lolliRecentlyCollected, lunaScreamCooldownFrames);
        if (collisionSystem.playHeartbeat) {
            warningFlashTimer = 30;
            soundManager.playOneShot(SoundManager.HEARTBEAT_FAST, 0.45);
        }
        if (collisionSystem.screenShake)
            screenShakeFrames = 15;
        if (collisionSystem.playScream) {
            soundManager.playOneShot(SoundManager.LUNA_SCREAM_NEARBY, 0.8);
            lunaScreamCooldownFrames = 130;
        }
        if (collisionSystem.playerDied)
            return triggerPlayerDeath(collisionSystem.deathMessage);

        lolliRecentlyCollected = false;
        return false;
    }

    /**
     * Checks if the glowing red lollipop reveal animation just finished playing.
     * 
     * @return boolean True if the animation is completely done.
     */
    boolean isLolliRevealJustFinished() {
        return lolliRevealState != null && !lolliRevealState.active;
    }

    /**
     * Adds a simple static overlay image to the screen, like a HUD element!
     * 
     * @param imagePath File path to the image to show.
     * @param x         The horizontal position on the screen.
     * @param y         The vertical position on the screen.
     * @param width     How wide to draw it.
     * @param height    How tall to draw it.
     * @param opacity   How visible it is (1.0 is solid, 0.0 is invisible).
     */
    public void addOverlay(String imagePath, double x, double y, double width, double height,
            double opacity) {
        GameRenderer.Overlay o = new GameRenderer.Overlay(imagePath, x, y, width, height);
        o.setOpacity(opacity);
        overlays.add(o);
    }

    /**
     * Adds an overlay image that moves around with the world map rather than 
     * staying glued to the screen. Good for floor marks!
     * 
     * @param imagePath File path to the image.
     * @param worldX    X coordinate in the game world.
     * @param worldY    Y coordinate in the game world.
     * @param width     How wide to draw it.
     * @param height    How tall to draw it.
     * @param opacity   How visible it is.
     */
    public void addWorldOverlay(String imagePath, double worldX, double worldY, double width,
            double height, double opacity) {
        GameRenderer.Overlay o = new GameRenderer.Overlay(imagePath, worldX, worldY, width, height, true);
        o.setOpacity(opacity);
        overlays.add(o);
    }

    /**
     * Casts a distraction spell! Finds the nearest guard and temporarily
     * dazes them so the player can sneak past safely.
     */
    void tryUseDistraction() {
        GuardEntity nearest = null;
        double best = Double.MAX_VALUE;
        for (GuardEntity guard : entityManager.getGuards()) {
            if (guard.isDistracted())
                continue;

            // Normalize euclidean vector distances mathematically mitigating topological
            // ambiguity organically
            double d = distInTiles(entityManager.getPlayer().getX(), entityManager.getPlayer().getY(), guard.getX(),
                    guard.getY());
            if (d < best && guard.isWithinDistractionRange(entityManager.getPlayer().getX(),
                    entityManager.getPlayer().getY())) {
                best = d;
                nearest = guard;
            }
        }
        if (nearest == null)
            return;

        // Execute numerical decay restricting persistent loop overrides precisely
        if (distractionSpellCount > 0) {
            distractionSpellCount--;
            nearest.distract();
        }
    }

    /**
     * Drops a cardboard cutout decoy if the player has one, 
     * making Pale Luna attack it instead of the player!
     */
    void tryPlaceClone() {
        // Enforce chronological conditionals preventing invalid synthetic geometry
        // propagation fundamentally
        if (!hasCloneItem || levelManager.getCurrentLevel() != 3 || entityManager.getCloneDecoy() != null)
            return;
        CardboardClone cloneDecoy = new CardboardClone(entityManager.getPlayer().getX() + 5,
                entityManager.getPlayer().getY() + 5);
        entityManager.setCloneDecoy(cloneDecoy);
        entityManager.addEntity(cloneDecoy);
        hasCloneItem = false;
        soundManager.playOneShot(SoundManager.CHEST_OPEN, 0.45);
    }

    /**
     * Draws the secret debug menu to print out timers, coordinates, 
     * and monster states for development and testing!
     * 
     * @param gc         The context used for drawing text.
     * @param activeKeys The keys currently held down.
     */
    void drawDebugOverlay(GraphicsContext gc, Set<KeyCode> activeKeys) {
        Monster paleLuna = entityManager.getPaleLuna();
        Player player = entityManager.getPlayer();
        Maze maze = levelManager.getMaze();

        double lunaDist = paleLuna == null ? 99.0
                : distInTiles(player.getX(), player.getY(), paleLuna.getX(), paleLuna.getY());

        // Process rectangular overlays ensuring contrast mappings objectively cleanly
        gc.setFill(Color.rgb(0, 0, 0, 0.62));
        gc.fillRect(8, 58, 360, 182);
        gc.setStroke(Color.rgb(180, 40, 40, 0.8));
        gc.setLineWidth(1.1);
        gc.strokeRect(8, 58, 360, 182);

        gc.setFill(Color.rgb(220, 220, 220));
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 12));

        // Interpret local geometric matrix vectors securely smoothly efficiently
        int[] tile = maze.getTilePositionAt(player.getX() + 10, player.getY() + 10);
        String tileText = tile == null ? "-" : tile[0] + "," + tile[1];

        // Output AI sequential indices objectively accurately seamlessly
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

        // Compile strict sequence parameters unconditionally smoothly optimally
        String[] lines = {
                "DEBUG (F3)",
                "Level=" + levelManager.getCurrentLevel() + " Tile=" + tileText + " InEscape="
                        + player.isInEscapeRoom(),
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

    /**
     * Warps Pale Luna right next to the player. Used if the player
     * stands completely still for too long!
     */
    private void teleportLunaNearPlayer() {
        if (entityManager.getPaleLuna() == null)
            return;
        entityManager.getPaleLuna().setPosition(entityManager.getPlayer().getX() + 36,
                entityManager.getPlayer().getY());
    }

    /**
     * Helper method to calculate the distance between two points in terms of map tiles.
     * 
     * @param x1 First X coordinate.
     * @param y1 First Y coordinate.
     * @param x2 Second X coordinate.
     * @param y2 Second Y coordinate.
     * @return double How many tiles apart they are.
     */
    static double distInTiles(double x1, double y1, double x2, double y2) {
        double dx = (x1 - x2) / Maze.TILE_SIZE, dy = (y1 - y2) / Maze.TILE_SIZE;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
