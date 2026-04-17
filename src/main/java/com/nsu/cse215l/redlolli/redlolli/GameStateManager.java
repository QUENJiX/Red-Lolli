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
 * Serves as the central orchestrator for the application's finite state
 * machine.
 * Encapsulates global state metrics and delegates cyclical logic updates to
 * decoupled subsystems.
 * Synchronizes entity spatial matrices, collision constraints, and
 * deterministic delta timings.
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
     * Executes procedural initializations to reset systemic temporal configurations
     * natively.
     * Purges topological entity mappings to reestablish deterministic baseline
     * variables.
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
     * Translates strict topological parameters into instantiated environmental
     * matrices.
     * Evaluates initial logic constraints calculating objective geometries
     * deterministically.
     */
    void loadLevel() {
        levelManager.loadLevel(entityManager);
        totalChestsEncountered += entityManager.getChests().size();
    }

    /**
     * Executes structural failure sequences triggering absolute game state halting.
     * 
     * @param message Textual mapping of the definitive fatal error state.
     * @return boolean True indicating absolute game iteration termination natively.
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
     * Orchestrates systemic tick propagation directing cyclical interactions
     * flawlessly.
     * Normalizes chronological deviations via floating-point delta matrices
     * natively.
     * 
     * @param activeKeys Exogenous Boolean constraint mappings capturing keyboard
     *                   states dynamically.
     * @return boolean True communicating a terminal halt mechanism within the core
     *         process loop.
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
     * Confirms the termination of specific independent cyclical visual
     * progressions.
     * 
     * @return boolean True authenticating the exhaustion of localized sequential
     *         tracking variables.
     */
    boolean isLolliRevealJustFinished() {
        return lolliRevealState != null && !lolliRevealState.active;
    }

    /**
     * Integrates ephemeral user interface bounding nodes into the rendering
     * abstraction structurally.
     * 
     * @param imagePath Resource URI classifying absolute textual mapping
     *                  parameters.
     * @param x         Arbitrary Cartesian limit horizontally instantiating logical
     *                  vectors.
     * @param y         Arbitrary Cartesian limit vertically instantiating logical
     *                  vectors.
     * @param width     Numeric structural array indicating linear dimensional
     *                  geometry.
     * @param height    Numeric structural array indicating vertical dimensional
     *                  geometry.
     * @param opacity   Floating-point constant extracting objective visibility
     *                  multipliers smoothly.
     */
    public void addOverlay(String imagePath, double x, double y, double width, double height,
            double opacity) {
        GameRenderer.Overlay o = new GameRenderer.Overlay(imagePath, x, y, width, height);
        o.setOpacity(opacity);
        overlays.add(o);
    }

    /**
     * Coordinates transient world geometries strictly mapped to matrix dependencies
     * linearly.
     * 
     * @param imagePath Resource string deriving absolute bounds uniformly natively.
     * @param worldX    Mathematical vector mapping Cartesian offsets dynamically
     *                  safely.
     * @param worldY    Mathematical vector mapping Cartesian offsets dynamically
     *                  safely.
     * @param width     Logical distance constraint projecting lateral visualization
     *                  correctly.
     * @param height    Logical distance constraint projecting longitudinal
     *                  visualization correctly.
     * @param opacity   Extrapolated integer scaling absolute alpha blending values
     *                  properly.
     */
    public void addWorldOverlay(String imagePath, double worldX, double worldY, double width,
            double height, double opacity) {
        GameRenderer.Overlay o = new GameRenderer.Overlay(imagePath, worldX, worldY, width, height, true);
        o.setOpacity(opacity);
        overlays.add(o);
    }

    /**
     * Intercepts environmental constraints conditionally resolving mechanical
     * overrides securely.
     * Analyzes proximal hazard distances dynamically overriding default hazard
     * logic natively.
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
     * Synthesizes surrogate entities inserting strict tracking clones conditionally
     * natively.
     * Establishes dynamic interaction overrides terminating redundant structural
     * arrays correctly.
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
     * Exports absolute operational telemetry data aggregating structural
     * abstractions reliably cleanly.
     * Subordinates default visual rendering loops natively explicitly.
     * 
     * @param gc         Hardware geometry bounds delegating explicit alphanumeric
     *                   matrices sequentially.
     * @param activeKeys Exogenous key polling arrays translated functionally
     *                   dynamically.
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
     * Executes arbitrary trajectory adjustments bypassing algorithmic dependencies
     * entirely globally.
     * Prevents logic deadlocks inherently safely natively.
     */
    private void teleportLunaNearPlayer() {
        if (entityManager.getPaleLuna() == null)
            return;
        entityManager.getPaleLuna().setPosition(entityManager.getPlayer().getX() + 36,
                entityManager.getPlayer().getY());
    }

    /**
     * Derives spatial Cartesian magnitudes mapping decoupled geographic limits
     * effectively reliably.
     * 
     * @param x1 Lateral vector variable systematically objectively correctly
     *           natively.
     * @param y1 Longitudinal vector variable systematically objectively correctly
     *           natively.
     * @param x2 Target lateral coordinate cleanly seamlessly mapped exactly.
     * @param y2 Target longitudinal coordinate cleanly seamlessly mapped exactly.
     * @return double Extracted distance differential explicitly mathematically
     *         calculated.
     */
    static double distInTiles(double x1, double y1, double x2, double y2) {
        double dx = (x1 - x2) / Maze.TILE_SIZE, dy = (y1 - y2) / Maze.TILE_SIZE;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
