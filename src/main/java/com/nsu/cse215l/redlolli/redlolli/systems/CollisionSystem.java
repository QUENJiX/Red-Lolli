package com.nsu.cse215l.redlolli.redlolli.systems;

import com.nsu.cse215l.redlolli.redlolli.entities.*;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import com.nsu.cse215l.redlolli.redlolli.ui.GameRenderer;

public class CollisionSystem {
    // Results
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

    public void checkChestCollisions(EntityManager entityManager, boolean currentHasCloneItem) {
        newDistractions = 0;
        collectedChests = 0;
        lolliRevealState = null;
        lolliRecentlyCollected = false;
        hasCloneItem = currentHasCloneItem;
        playChestOpen = false;

        Player player = entityManager.getPlayer();

        for (Item chest : entityManager.getChests()) {
            if (chest.isCollected() || !player.getHitbox().intersects(chest.getHitbox()))
                continue;
            chest.collect();
            collectedChests++;
            playChestOpen = true;
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

    public void checkGuardThreats(EntityManager entityManager, boolean inEscapeRoom, boolean enteringEscapeRoom, double guardHitCooldownFrames) {
        if (guardHitCooldownFrames > 0)
            return;

        if (inEscapeRoom && !enteringEscapeRoom) {
            return;
        }

        Player player = entityManager.getPlayer();

        for (GuardEntity guard : entityManager.getGuards()) {
            if (!guard.isDistracted() && (guard.isPlayerOnGuardedRoom(player.getHitbox())
                    || guard.getHitbox().intersects(player.getHitbox()))) {
                playerDied = true;
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

    public void updateSerialKiller(EntityManager entityManager, Maze maze) {
        SerialKillerEntity serialKiller = entityManager.getSerialKiller();
        Player player = entityManager.getPlayer();
        CardboardClone cloneDecoy = entityManager.getCloneDecoy();

        if (serialKiller == null)
            return;
        if (!serialKiller.isActive()) {
            if (distInTiles(player.getX(), player.getY(), serialKiller.getX(), serialKiller.getY()) < 8.0)
                serialKiller.setActive(true);
        }
        if (!serialKiller.isActive())
            return;

        double targetX = cloneDecoy != null ? cloneDecoy.getX() : player.getX();
        double targetY = cloneDecoy != null ? cloneDecoy.getY() : player.getY();
        serialKiller.updateChase(targetX, targetY, maze);

        if (cloneDecoy != null) {
            if (!serialKiller.isAttackingDecoy() && serialKiller.getHitbox().intersects(cloneDecoy.getHitbox())) {
                serialKiller.startDecoyAttack();
            } else if (serialKiller.isAttackingDecoy() && serialKiller.getDecoyAttackFrames() <= 1) {
                entityManager.removeEntity(cloneDecoy);
                entityManager.setCloneDecoy(null);
            }
        }

        if (!serialKiller.isAttackingDecoy() && serialKiller.getHitbox().intersects(player.getHitbox())) {
            playerDied = true;
            deathMessage = "Steel and panic. He never stops hunting.";
        }
    }

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

        if (exitingEscapeRoom && paleLuna.isWaitingAtDoor()) {
            playerDied = true;
            deathMessage = "She waited at the door. You stepped out anyway.";
            return;
        }
        if (prevState != Monster.State.HUNTING && paleLuna.getState() == Monster.State.HUNTING) {
            screenShake = true;
            playHeartbeat = true;
        }
        
        player.setBeingChased(paleLuna.isHunting());

        double lunaDistTiles = distInTiles(player.getX(), player.getY(), paleLuna.getX(), paleLuna.getY());
        if (paleLuna.isHunting() && lunaDistTiles <= 3.0 && lunaScreamCooldownFrames <= 0) {
            playScream = true;
        }
        
        if (!inEscapeRoom && paleLuna.isHunting() && player.getHitbox().intersects(paleLuna.getHitbox())) {
            playerDied = true;
            deathMessage = "She found your pulse before you heard her footsteps.";
            return;
        }
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

    private static double distInTiles(double x1, double y1, double x2, double y2) {
        double dx = (x1 - x2) / Maze.TILE_SIZE, dy = (y1 - y2) / Maze.TILE_SIZE;
        return Math.sqrt(dx * dx + dy * dy);
    }
}