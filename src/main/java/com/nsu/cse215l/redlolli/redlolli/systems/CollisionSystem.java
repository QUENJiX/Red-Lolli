package com.nsu.cse215l.redlolli.redlolli.systems;

import com.nsu.cse215l.redlolli.redlolli.entities.*;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import com.nsu.cse215l.redlolli.redlolli.ui.GameRenderer;

/**
 * Does exactly what it says on the tin! It handles all the number-crunching to
 * see if the player is bumping into a monster, picking up a chest, or triggering 
 * an event. It separates all this math from the game loop so our rendering logic 
 * can stay fast.
 */
public class CollisionSystem {
    // Transient evaluation flags interrogated natively per-frame by the
    // GameStateManager
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
     * Checks if the player is currently standing on top of an unopened treasure chest.
     * If they are, it pops it open, adds to our stats, and sets flags to draw fun effects!
     *
     * @param entityManager       The system listing all the chests on the map.
     * @param currentHasCloneItem True if the player already has a clone decoy in their pocket.
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

            // Skip this chest if it's already open, or if the player isn't standing on it.
            if (chest.isCollected() || !player.getHitbox().intersects(chest.getHitbox()))
                continue;
            chest.collect();
            collectedChests++;
            playChestOpen = true;

            // If it's a primary objective, tell the renderer to do a huge celebratory pop!
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
     * Checks if the player ran into a patrolling guard. If they did, it triggers 
     * an instant game over with a grim, thematic message!
     * Players get a tiny window of invincibility to avoid unfair back-to-back hits.
     *
     * @param entityManager          The system managing all the enemies currently spawned.
     * @param inEscapeRoom           Is the player currently hiding inside a sanctuary room?
     * @param enteringEscapeRoom     Did the player just step into the sanctuary right now?
     * @param guardHitCooldownFrames Invincibility timer tracking frames since the last bump.
     */
    public void checkGuardThreats(EntityManager entityManager, boolean inEscapeRoom, boolean enteringEscapeRoom,
            double guardHitCooldownFrames) {
        // Give the player a tiny window of mercy from instant deaths.
        if (guardHitCooldownFrames > 0)
            return;

        // Enemies cannot reach the player if they are hiding out inside a sanctuary!
        if (inEscapeRoom && !enteringEscapeRoom) {
            return;
        }

        Player player = entityManager.getPlayer();

        for (GuardEntity guard : entityManager.getGuards()) {

            // Make sure the player touches the guard when the guard ISN'T looking away at a distraction.
            if (!guard.isDistracted() && (guard.isPlayerOnGuardedRoom(player.getHitbox())
                    || guard.getHitbox().intersects(player.getHitbox()))) {
                playerDied = true;

                // Pick an appropriately terrifying way to describe how you died.
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
     * Determines whether the Serial Killer has spotted the player, tracks their movement, 
     * and sets game-over flags if he finally catches them! Also checks to see if he's 
     * currently attacking a cardboard decoy instead.
     *
     * @param entityManager The system holding all monsters, decoys, and the player.
     * @param maze          The map layout used for figuring out straight lines of sight.
     */
    public void updateSerialKiller(EntityManager entityManager, Maze maze) {
        SerialKillerEntity serialKiller = entityManager.getSerialKiller();
        Player player = entityManager.getPlayer();
        CardboardClone cloneDecoy = entityManager.getCloneDecoy();

        if (serialKiller == null)
            return;

        // Wake him up if the player blindly walks near him! He starts hunting after this.
        if (!serialKiller.isActive()) {
            if (distInTiles(player.getX(), player.getY(), serialKiller.getX(), serialKiller.getY()) < 8.0)
                serialKiller.setActive(true);
        }
        if (!serialKiller.isActive())
            return;

        // Go after the decoy if one exists. Otherwise, he zeroes in on the player.
        double targetX = cloneDecoy != null ? cloneDecoy.getX() : player.getX();
        double targetY = cloneDecoy != null ? cloneDecoy.getY() : player.getY();
        serialKiller.updateChase(targetX, targetY, maze);

        // Tell him to stop hunting the player and start hacking into the clone decoy!
        if (cloneDecoy != null) {
            if (!serialKiller.isAttackingDecoy() && serialKiller.getHitbox().intersects(cloneDecoy.getHitbox())) {
                serialKiller.startDecoyAttack();
            } else if (serialKiller.isAttackingDecoy() && serialKiller.getDecoyAttackFrames() <= 1) {
                entityManager.removeEntity(cloneDecoy);
                entityManager.setCloneDecoy(null);
            }
        }

        // Test if his knife reached the player while he WASN'T distracted by a decoy.
        if (!serialKiller.isAttackingDecoy() && serialKiller.getHitbox().intersects(player.getHitbox())) {
            playerDied = true;
            deathMessage = "Steel and panic. He never stops hunting.";
        }
    }

    /**
     * Processes Pale Luna's logic, including her sightlines, if she's currently 
     * screaming at the player, and whether she caught the player exiting a sanctuary.
     * 
     * @param entityManager            System controlling the monster and player tracking.
     * @param maze                     The game map, used to see if walls are blocking her view.
     * @param inEscapeRoom             True if the player is currently safe inside.
     * @param exitingEscapeRoom        True if the player just left the sanctuary room this frame.
     * @param lolliRecentlyCollected   True if the player just found the objective, which makes her angry.
     * @param lunaScreamCooldownFrames Cooldown timer to prevent her audio from deafening the player repeatedly.
     */
    public void updatePaleLuna(EntityManager entityManager, Maze maze, boolean inEscapeRoom, boolean exitingEscapeRoom,
            boolean lolliRecentlyCollected, double lunaScreamCooldownFrames) {
        Monster paleLuna = entityManager.getPaleLuna();
        Player player = entityManager.getPlayer();

        screenShake = false;
        playHeartbeat = false;
        playScream = false;

        if (paleLuna == null)
            return;

        Monster.State prevState = paleLuna.getState();
        paleLuna.update(player.getX(), player.getY(), inEscapeRoom, lolliRecentlyCollected, maze);

        // Instant game over if she was camping outside the escape room and you just walked out.
        if (exitingEscapeRoom && paleLuna.isWaitingAtDoor()) {
            playerDied = true;
            deathMessage = "She waited at the door. You stepped out anyway.";
            return;
        }

        // Add some spooky ambiance if she just started hunting you.
        if (prevState != Monster.State.HUNTING && paleLuna.getState() == Monster.State.HUNTING) {
            screenShake = true;
            playHeartbeat = true;
        }

        player.setBeingChased(paleLuna.isHunting());

        double lunaDistTiles = distInTiles(player.getX(), player.getY(), paleLuna.getX(), paleLuna.getY());
        if (paleLuna.isHunting() && lunaDistTiles <= 3.0 && lunaScreamCooldownFrames <= 0) {
            playScream = true;
        }

        // Did she touch you while you were out in the halls? You're dead.
        if (!inEscapeRoom && paleLuna.isHunting() && player.getHitbox().intersects(paleLuna.getHitbox())) {
            playerDied = true;
            deathMessage = "She found your pulse before you heard her footsteps.";
            return;
        }

        // She will also kill you if she can just see you after she's reached a door,
        // using the map's line of sight checker.
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
     * Calculates the distance between two things, but measures it in map tiles instead of pixels 
     * so we can easily tell how far away something is on the grid.
     *
     * @param x1 The X pixel position of the first thing.
     * @param y1 The Y pixel position of the first thing.
     * @param x2 The X pixel position of the second thing.
     * @param y2 The Y pixel position of the second thing.
     * @return double The straight-line distance, in tiles, between the two points.
     */
    private static double distInTiles(double x1, double y1, double x2, double y2) {
        double dx = (x1 - x2) / Maze.TILE_SIZE, dy = (y1 - y2) / Maze.TILE_SIZE;
        return Math.sqrt(dx * dx + dy * dy);
    }
}
