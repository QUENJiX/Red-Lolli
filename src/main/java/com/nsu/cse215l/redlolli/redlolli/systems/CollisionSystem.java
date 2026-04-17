package com.nsu.cse215l.redlolli.redlolli.systems;

import com.nsu.cse215l.redlolli.redlolli.entities.*;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import com.nsu.cse215l.redlolli.redlolli.ui.GameRenderer;

/**
 * Operates as the centralized physics and intersection calculator.
 * Evaluates geometric overlays (AABB implementations) across distinctly
 * classified actors (i.e. Player vs Chest, Player vs Antagonist) to dictate 
 * state transactions such as mortality, event discovery, or item acquisition.
 * Designed to separate physical overlap queries from the core render iteration.
 */
public class CollisionSystem {
    // Transient evaluation flags interrogated natively per-frame by the GameStateManager
    public boolean playerDied = false;
    public String deathMessage = "";
    public int newDistractions = 0;
    public int collectedChests = 0;
    public GameRenderer.LolliRevealState lolliRevealState = null;
    public boolean lolliRecentlyCollected = false;
    public boolean hasCloneItem = false;
    public boolean screenShake = false;
    public boolean playHeartbeat = false;
    public boolean playScream = false;
    public boolean playChestOpen = false;

    /**
     * Iterates dynamically tracked interactable containers against the actor's footprint.
     * Alters local extraction states mimicking acquisition when a successful geometric intersection occurs.
     *
     * @param entityManager The localized tracker yielding present resource nodes.
     * @param currentHasCloneItem Persisted physical payload status evaluated across iterations.
     */
    public void checkChestCollisions(EntityManager entityManager, boolean currentHasCloneItem) {
        newDistractions = 0;
        collectedChests = 0;
        lolliRevealState = null;
        lolliRecentlyCollected = false;
        hasCloneItem = currentHasCloneItem;
        playChestOpen = false;

        Player player = entityManager.getPlayer();

        for (Item chest : entityManager.getChests()) {
            
            // Bypass logic mapping strictly on previously acquired nodes or lack of spatial collision
            if (chest.isCollected() || !player.getHitbox().intersects(chest.getHitbox()))
                continue;
            chest.collect();
            collectedChests++;
            playChestOpen = true;
            
            // Trigger paramount aesthetic visualization exclusively if the core objective is seized
            if (chest.getContentType() == Item.ContentType.LOLLI) {
                lolliRecentlyCollected = true;
                lolliRevealState = new GameRenderer.LolliRevealState(chest.getX(), chest.getY(), 120);
                newDistractions += 3;
                return;
            }
            if (chest.getContentType() == Item.ContentType.EMPTY) {
                newDistractions++;
            }
            if (chest.getContentType() == Item.ContentType.CLONE_DECOY) {
                hasCloneItem = true;
            }
        }
    }

    /**
     * Evaluates geometric encroachment against patrol-oriented adversaries mapping distinct lethality messages.
     * Integrates temporary invincibility logic mimicking immunity buffers following evasive actions or respawns.
     *
     * @param entityManager The localized tracker yielding active patrol agents.
     * @param inEscapeRoom Validation flag guaranteeing player sanctuary immunity natively.
     * @param enteringEscapeRoom Validation flag disabling lethality momentarily upon entering a threshold.
     * @param guardHitCooldownFrames Transient immunity countdown metric disabling intersection penalties.
     */
    public void checkGuardThreats(EntityManager entityManager, boolean inEscapeRoom, boolean enteringEscapeRoom, double guardHitCooldownFrames) {
        // Enforce the operational invincibility window bypassing intersection entirely
        if (guardHitCooldownFrames > 0)
            return;

        // Ensure sanctuary bounds negate adversarial aggression completely
        if (inEscapeRoom && !enteringEscapeRoom) {
            return;
        }

        Player player = entityManager.getPlayer();

        for (GuardEntity guard : entityManager.getGuards()) {
            
            // Trigger failure scenario if geometry overlaps strictly when the adversary isn't hijacked via distraction
            if (!guard.isDistracted() && (guard.isPlayerOnGuardedRoom(player.getHitbox())
                    || guard.getHitbox().intersects(player.getHitbox()))) {
                playerDied = true;
                
                // Construct thematic lethality context dependent on adversarial instantiation archetype
                if (guard.getType() == GuardEntity.Type.BAT)
                    deathMessage = "The bat bit first. Luna answered instantly.";
                else if (guard.getType() == GuardEntity.Type.COBRA)
                    deathMessage = "The snake strikes! No spell cast, no escape.";
                else
                    deathMessage = "The centipede swarmed you... the darkness follows.";
                return;
            }
        }
    }

    /**
     * Synthesizes traversal and lethality operations for a complex hunter adversary algorithm.
     * Administers target reassignment prioritizing player-produced illusions intrinsically.
     *
     * @param entityManager Tracker offering relative geographic positions of the protagonist or active decoy arrays.
     * @param maze The geometric terrain supplying operational bounds or traversal hindrances.
     */
    public void updateSerialKiller(EntityManager entityManager, Maze maze) {
        SerialKillerEntity serialKiller = entityManager.getSerialKiller();
        Player player = entityManager.getPlayer();
        CardboardClone cloneDecoy = entityManager.getCloneDecoy();

        if (serialKiller == null)
            return;
            
        // Trigger hostility awakening exclusively if the player breaches spatial thresholds
        if (!serialKiller.isActive()) {
            if (distInTiles(player.getX(), player.getY(), serialKiller.getX(), serialKiller.getY()) < 8.0)
                serialKiller.setActive(true);
        }
        if (!serialKiller.isActive())
            return;

        // Redirect algorithm tracking parameters evaluating static decoys paramount
        double targetX = cloneDecoy != null ? cloneDecoy.getX() : player.getX();
        double targetY = cloneDecoy != null ? cloneDecoy.getY() : player.getY();
        serialKiller.updateChase(targetX, targetY, maze);

        // Initiate operational stall logic and eventual physical purge handling decoy degradation
        if (cloneDecoy != null) {
            if (!serialKiller.isAttackingDecoy() && serialKiller.getHitbox().intersects(cloneDecoy.getHitbox())) {
                serialKiller.startDecoyAttack();
            } else if (serialKiller.isAttackingDecoy() && serialKiller.getDecoyAttackFrames() <= 1) {
                entityManager.removeEntity(cloneDecoy);
                entityManager.setCloneDecoy(null);
            }
        }

        // Validate final protagonist overlap invoking mortality independently of decoy manipulation
        if (!serialKiller.isAttackingDecoy() && serialKiller.getHitbox().intersects(player.getHitbox())) {
            playerDied = true;
            deathMessage = "Steel and panic. He never stops hunting.";
        }
    }

    /**
     * Conducts sophisticated procedural logic for the primary relentless antagonist evaluating 
     * sensory elements (e.g., Line-of-sight algorithms) and sanctuary dynamics.
     * Also signals audiovisual warnings natively when transitionary thresholds breach.
     *
     * @param entityManager The domain encapsulating relative adversarial references natively.
     * @param maze Geographical bounds governing line-of-sight obstruction implementations.
     * @param inEscapeRoom Sanctuary indicator averting fundamental hostility.
     * @param exitingEscapeRoom Vulnerability indicator mapping fatal transitions immediately exiting sanctuary nodes.
     * @param lolliRecentlyCollected Boolean trigger altering pursuit intensity following objective completion.
     * @param lunaScreamCooldownFrames Cooldown limiter dictating audiovisual tension broadcasts.
     */
    public void updatePaleLuna(EntityManager entityManager, Maze maze, boolean inEscapeRoom, boolean exitingEscapeRoom, boolean lolliRecentlyCollected, double lunaScreamCooldownFrames) {
        Monster paleLuna = entityManager.getPaleLuna();
        Player player = entityManager.getPlayer();
        
        screenShake = false;
        playHeartbeat = false;
        playScream = false;

        if (paleLuna == null)
            return;

        Monster.State prevState = paleLuna.getState();
        paleLuna.update(player.getX(), player.getY(), inEscapeRoom, lolliRecentlyCollected, maze);

        // Levy an unconditional mortality penalty if departing a sanctuary carelessly directly into adversary bounds
        if (exitingEscapeRoom && paleLuna.isWaitingAtDoor()) {
            playerDied = true;
            deathMessage = "She waited at the door. You stepped out anyway.";
            return;
        }
        
        // Deploy immersive aesthetic modifiers when the adversary's state evolves aggressively natively
        if (prevState != Monster.State.HUNTING && paleLuna.getState() == Monster.State.HUNTING) {
            screenShake = true;
            playHeartbeat = true;
        }
        
        player.setBeingChased(paleLuna.isHunting());

        double lunaDistTiles = distInTiles(player.getX(), player.getY(), paleLuna.getX(), paleLuna.getY());
        if (paleLuna.isHunting() && lunaDistTiles <= 3.0 && lunaScreamCooldownFrames <= 0) {
            playScream = true;
        }
        
        // Assert overlapping lethality excluding explicitly designated safe nodes logically
        if (!inEscapeRoom && paleLuna.isHunting() && player.getHitbox().intersects(paleLuna.getHitbox())) {
            playerDied = true;
            deathMessage = "She found your pulse before you heard her footsteps.";
            return;
        }
        
        // Assert visibility-based lethality executing explicit Raycast (Line of Sight) verification operations natively
        if (!inEscapeRoom && paleLuna.isWaitingAtDoor()) {
            boolean canSee = maze.hasLineOfSight(
                    paleLuna.getX() + 12, paleLuna.getY() + 12,
                    player.getX() + 10, player.getY() + 10);
            if (canSee) {
                playerDied = true;
                deathMessage = "She waited at the door. You stepped out anyway.";
                return;
            }
        }
    }

    /**
     * Determines geometric euclidean distance quantified using grid indices natively instead of abstract pixels.
     *
     * @param x1 Extracted logic coordinate X from entity 1.
     * @param y1 Extracted logic coordinate Y from entity 1.
     * @param x2 Extracted logic coordinate X from entity 2.
     * @param y2 Extracted logic coordinate Y from entity 2.
     * @return double Vector magnitude illustrating tile boundaries between subjects.
     */
    private static double distInTiles(double x1, double y1, double x2, double y2) {
        double dx = (x1 - x2) / Maze.TILE_SIZE, dy = (y1 - y2) / Maze.TILE_SIZE;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
