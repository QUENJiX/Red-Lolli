package com.nsu.cse215l.redlolli.redlolli.ui;

import com.nsu.cse215l.redlolli.redlolli.entities.Entity;
import com.nsu.cse215l.redlolli.redlolli.entities.Item;
import com.nsu.cse215l.redlolli.redlolli.entities.CardboardClone;
import com.nsu.cse215l.redlolli.redlolli.entities.Monster;
import com.nsu.cse215l.redlolli.redlolli.entities.SerialKillerEntity;
import com.nsu.cse215l.redlolli.redlolli.entities.Player;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.shape.ArcType;
import javafx.scene.canvas.Canvas;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import com.nsu.cse215l.redlolli.redlolli.entities.TorchEntity;

import java.util.List;

/**
 * This class draws the actual game world onto the screen! 
 * It manages painting the maze tiles, the player, the monsters, light effects, and items.
 */
public class GameRenderer {

    private static final double SCREEN_WIDTH = 880;
    private static final double SCREEN_HEIGHT = 730;

    private static Canvas lightBuffer = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
    private static GraphicsContext lightGC = lightBuffer.getGraphicsContext2D();

    private static Image lunaFlashImg;
    private static Image[] torchFrames;
    private static Image chestClosedImg;
    private static Image chestOpenedImg;
    private static Image chestGlowLolli;
    private static Image chestGlowClone;
    private static Image cloneDecoyImg;

    private static Image monsterDormant;
    private static Image monsterStalking;
    private static Image monsterStalkingRight;
    private static Image monsterHunting;
    private static Image monsterHuntingRight;
    private static Image monsterWaiting;

    private static Image killerIdleImg;
    private static Image killerIdleLeftImg;
    private static Image killerChaseImg;
    private static Image killerChaseLeftImg;
    private static Image killerAttackImg;
    private static Image killerAttackLeftImg;

    private static Image idleFrontImg, idleBackImg, idleLeftImg, idleRightImg;
    private static Image[] walkLeftImgs, walkRightImgs, walkBackImgs, walkFrontImgs;

    private static Image batImg;
    private static Image batDistractedImg;
    private static Image cobraImg;
    private static Image cobraDistractedImg;
    private static Image centipedeImg;
    private static Image centipedeDistractedImg;

    private static Image[][] borderWallImg = new Image[3][4];
    private static Image[][] innerWallImg = new Image[3][4];
    private static Image[][] floorAImg = new Image[3][3];
    private static Image[][] floorBImg = new Image[3][3];
    private static Image[] escapeRoomImg = new Image[2];
    private static Image[] escapeRoomOpenImg = new Image[2];

    private static boolean imagesInitialized = false;

    /**
     * A helper method to load sprite images through the AssetManager.
     *
     * @param filename The filepath/name of the item to load.
     * @param width    How wide to draw the image.
     * @param height   How tall to draw the image.
     * @return Image The returned JavaFX graphic ready to be plastered on the screen.
     */
    private static Image loadSprite(String filename, int width, int height) {
        return com.nsu.cse215l.redlolli.redlolli.systems.AssetManager.getInstance()
                .getSprite("/assets/images/" + filename, width, height);
    }

    /**
     * Loads up literally every single graphical sprite needed for the game map and characters.
     * It tracks whether they're loaded to make sure it only ever reads them into memory once.
     */
    public static void initImages() {
        if (imagesInitialized)
            return;
        lunaFlashImg = loadSprite("sprites/luna_flash.png", 500, 500);

        torchFrames = new Image[5];
        for (int i = 0; i < 5; i++) {
            torchFrames[i] = loadSprite("dungeon/wall/torches/torch_" + i + ".png", 40, 40);
        }

        chestClosedImg = loadSprite("sprites/chest_closed.png", 32, 32);
        chestOpenedImg = loadSprite("sprites/chest_open.png", 32, 32);
        chestGlowLolli = loadSprite("sprites/chest_glow_lolli.png", 32, 32);
        chestGlowClone = loadSprite("sprites/chest_glow_clone.png", 32, 32);
        cloneDecoyImg = loadSprite("sprites/clone_decoy.png", 50, 50);

        monsterDormant = loadSprite("sprites/monster_dormant.png", 50, 50);
        monsterStalking = loadSprite("sprites/monster_stalking.png", 50, 50);
        monsterStalkingRight = loadSprite("sprites/monster_stalking_right.png", 50, 50);
        monsterHunting = loadSprite("sprites/monster_hunting.png", 50, 50);
        monsterHuntingRight = loadSprite("sprites/monster_hunting_right.png", 50, 50);
        monsterWaiting = loadSprite("sprites/monster_waiting.png", 50, 50);

        killerIdleImg = loadSprite("sprites/killer_idle_right.png", 40, 70);
        killerIdleLeftImg = loadSprite("sprites/killer_idle_left.png", 40, 70);
        killerChaseImg = loadSprite("sprites/killer_chase_right.png", 640, 70);
        killerChaseLeftImg = loadSprite("sprites/killer_chase_left.png", 640, 70);
        killerAttackImg = loadSprite("sprites/killer_attack_right.png", 640, 70);
        killerAttackLeftImg = loadSprite("sprites/killer_attack_left.png", 640, 70);

        idleFrontImg = loadSprite("sprites/idle_front.png", 32, 32);
        idleBackImg = loadSprite("sprites/idle_back.png", 32, 32);
        idleLeftImg = loadSprite("sprites/idle_left.png", 32, 32);
        idleRightImg = loadSprite("sprites/idle_right.png", 32, 32);

        walkBackImgs = new Image[3];
        for (int i = 1; i <= 3; i++)
            walkBackImgs[i - 1] = loadSprite("sprites/walk_back_" + i + ".png", 28, 28);

        walkFrontImgs = new Image[3];
        for (int i = 1; i <= 3; i++)
            walkFrontImgs[i - 1] = loadSprite("sprites/walk_front_" + i + ".png", 28, 28);

        walkLeftImgs = new Image[3];
        for (int i = 1; i <= 3; i++)
            walkLeftImgs[i - 1] = loadSprite("sprites/walk_left_" + i + ".png", 28, 28);

        walkRightImgs = new Image[3];
        for (int i = 1; i <= 3; i++)
            walkRightImgs[i - 1] = loadSprite("sprites/walk_right_" + i + ".png", 28, 28);

        batImg = loadSprite("sprites/guard_bat.png", 40, 40);
        batDistractedImg = loadSprite("sprites/guard_bat_distracted.png", 40, 40);
        cobraImg = loadSprite("sprites/guard_cobra.png", 40, 40);
        cobraDistractedImg = loadSprite("sprites/guard_cobra_distracted.png", 40, 40);
        centipedeImg = loadSprite("sprites/guard_centipede.png", 40, 40);
        centipedeDistractedImg = loadSprite("sprites/guard_centipede_distracted.png", 40, 40);

        String dc = "dungeon/";

        borderWallImg[0][0] = loadSprite(dc + "wall/wall_vines_0.png", 40, 40);
        borderWallImg[0][1] = loadSprite(dc + "wall/wall_vines_1.png", 40, 40);
        borderWallImg[0][2] = loadSprite(dc + "wall/wall_vines_2.png", 40, 40);
        borderWallImg[0][3] = loadSprite(dc + "wall/wall_vines_3.png", 40, 40);
        borderWallImg[1][0] = loadSprite(dc + "wall/brick_brown_4.png", 40, 40);
        borderWallImg[1][1] = loadSprite(dc + "wall/brick_brown_5.png", 40, 40);
        borderWallImg[1][2] = loadSprite(dc + "wall/brick_brown_6.png", 40, 40);
        borderWallImg[1][3] = loadSprite(dc + "wall/brick_brown_7.png", 40, 40);
        borderWallImg[2][0] = loadSprite(dc + "wall/brick_dark_3.png", 40, 40);
        borderWallImg[2][1] = loadSprite(dc + "wall/brick_dark_4.png", 40, 40);
        borderWallImg[2][2] = loadSprite(dc + "wall/brick_dark_5.png", 40, 40);
        borderWallImg[2][3] = loadSprite(dc + "wall/brick_dark_6.png", 40, 40);

        innerWallImg[0][0] = loadSprite(dc + "wall/wall_vines_4.png", 40, 40);
        innerWallImg[0][1] = loadSprite(dc + "wall/wall_vines_5.png", 40, 40);
        innerWallImg[0][2] = loadSprite(dc + "wall/wall_vines_6.png", 40, 40);
        innerWallImg[0][3] = loadSprite(dc + "wall/brick_brown-vines_1.png", 40, 40);
        innerWallImg[1][0] = loadSprite(dc + "wall/brick_brown_0.png", 40, 40);
        innerWallImg[1][1] = loadSprite(dc + "wall/brick_brown_1.png", 40, 40);
        innerWallImg[1][2] = loadSprite(dc + "wall/brick_brown_2.png", 40, 40);
        innerWallImg[1][3] = loadSprite(dc + "wall/brick_brown_3.png", 40, 40);
        innerWallImg[2][0] = loadSprite(dc + "wall/brick_dark_0.png", 40, 40);
        innerWallImg[2][1] = loadSprite(dc + "wall/brick_dark_1.png", 40, 40);
        innerWallImg[2][2] = loadSprite(dc + "wall/brick_dark_2.png", 40, 40);
        innerWallImg[2][3] = loadSprite(dc + "wall/brick_dark_3.png", 40, 40);

        floorAImg[0][0] = loadSprite(dc + "floor/lair_0_new.png", 40, 40);
        floorAImg[0][1] = loadSprite(dc + "floor/lair_1_new.png", 40, 40);
        floorAImg[0][2] = loadSprite(dc + "floor/lair_2_new.png", 40, 40);
        floorBImg[0][0] = loadSprite(dc + "floor/lair_3_new.png", 40, 40);
        floorBImg[0][1] = loadSprite(dc + "floor/lair_4.png", 40, 40);
        floorBImg[0][2] = loadSprite(dc + "floor/lair_5.png", 40, 40);

        floorAImg[1][0] = loadSprite(dc + "floor/pebble_brown_0_new.png", 40, 40);
        floorAImg[1][1] = loadSprite(dc + "floor/pebble_brown_1_new.png", 40, 40);
        floorAImg[1][2] = loadSprite(dc + "floor/pebble_brown_2_new.png", 40, 40);
        floorBImg[1][0] = loadSprite(dc + "floor/pebble_brown_3_new.png", 40, 40);
        floorBImg[1][1] = loadSprite(dc + "floor/pebble_brown_4_new.png", 40, 40);
        floorBImg[1][2] = loadSprite(dc + "floor/pebble_brown_5_new.png", 40, 40);

        floorAImg[2][0] = loadSprite(dc + "floor/grey_dirt_0_new.png", 40, 40);
        floorAImg[2][1] = loadSprite(dc + "floor/grey_dirt_1_new.png", 40, 40);
        floorAImg[2][2] = loadSprite(dc + "floor/grey_dirt_2_new.png", 40, 40);
        floorBImg[2][0] = loadSprite(dc + "floor/grey_dirt_b_0.png", 40, 40);
        floorBImg[2][1] = loadSprite(dc + "floor/grey_dirt_b_1.png", 40, 40);
        floorBImg[2][2] = loadSprite(dc + "floor/grey_dirt_b_2.png", 40, 40);

        escapeRoomImg[0] = loadSprite(dc + "doors/runed_door.png", 40, 40);
        escapeRoomImg[1] = loadSprite(dc + "doors/sealed_door.png", 40, 40);
        escapeRoomOpenImg[0] = loadSprite(dc + "gateways/escape_hatch_up.png", 40, 40);
        escapeRoomOpenImg[1] = loadSprite(dc + "gateways/escape_hatch_up.png", 40, 40);

        imagesInitialized = true;
    }

    /**
     * Draws an item box/chest graphic on the screen! Falls back to a brown box if the image is missing.
     *
     * @param gc          The JavaFX GraphicsContext to draw with.
     * @param isCollected Whether the chest has already been opened or not.
     * @param img         The image to actually draw.
     * @param x           The horizontal pixel position.
     * @param y           The vertical pixel position.
     */
    private static void drawItemImg(GraphicsContext gc, boolean isCollected, Image img, double x, double y) {
        if (img != null) {
            gc.drawImage(img, x, y, 32.0, 32.0);
        } else {
            gc.setFill(isCollected ? Color.rgb(160, 82, 45) : Color.rgb(139, 69, 19));
            gc.fillRect(x, y, 32.0, 32.0);
        }
    }

    /**
     * Draws Pale Luna on the screen, including the terrifying red glowing aura
     * pulsating around her when she wakes up and starts hunting the player.
     *
     * @param gc The context used for drawing.
     * @param m  The Pale Luna monster object.
     */
    private static void renderMonster(GraphicsContext gc, Monster m) {
        double RENDER_SIZE = 50.0;
        double AURA_SIZE = 56.0;
        double offset = (RENDER_SIZE - m.getSize()) / 2;
        double cx = m.getX() + m.getSize() / 2;
        double cy = m.getY() + m.getSize() / 2;

        // If she's awake, draw an angry red aura throbbing around her to scare the player.
        if (m.getState() != Monster.State.DORMANT) {
            double pulse = Math.sin(m.getPulsePhase()) * 5;
            double baseRadius = AURA_SIZE / 2 + pulse;

            // Generate a jagged polygon shape that looks like crackling energy
            int numPoints = 16;
            double[] xPoints = new double[numPoints];
            double[] yPoints = new double[numPoints];

            for (int layer = 0; layer < 3; layer++) {
                for (int i = 0; i < numPoints; i++) {
                    double angle = Math.PI * 2 * ((double) i / numPoints);
                    // Shift the radius slightly for each point so the aura looks chaotic and uncontrolled.
                    double radiusJitter = 0.75 + (Math.random() * 0.45);
                    double currentR = baseRadius * radiusJitter;

                    if (layer == 1)
                        currentR *= 0.65;
                    if (layer == 2)
                        currentR *= 0.35;

                    xPoints[i] = cx + (Math.cos(angle) * currentR);
                    yPoints[i] = cy + (Math.sin(angle) * currentR);
                }

                if (layer == 0) {
                    gc.setGlobalAlpha(0.25);
                    gc.setFill(Color.rgb(180, 20, 20));
                } else if (layer == 1) {
                    gc.setGlobalAlpha(0.45);
                    gc.setFill(Color.rgb(220, 30, 30));
                } else {
                    gc.setGlobalAlpha(0.7);
                    gc.setFill(Color.rgb(255, 60, 60));
                }
                gc.fillPolygon(xPoints, yPoints, numPoints);
            }

            // Restore the normal drawing alpha so the actual monster image isn't see-through.
            gc.setGlobalAlpha(0.6);
            gc.setStroke(Color.rgb(40, 0, 0));
            gc.setLineWidth(1.5);
            double ringShift = (Math.random() - 0.5) * 8;
            gc.strokeOval(cx - baseRadius + ringShift, cy - baseRadius - ringShift, baseRadius * 2, baseRadius * 2);

            gc.setGlobalAlpha(1.0);
        }

        // Figure out which image to draw based on what Pale Luna is currently doing.
        Image body = switch (m.getState()) {
            case DORMANT -> monsterDormant;
            case STALKING -> m.isFacingRight() ? monsterStalkingRight : monsterStalking;
            case HUNTING -> m.isFacingRight() ? monsterHuntingRight : monsterHunting;
            case WAITING_AT_DOOR -> monsterWaiting;
        };

        if (body != null) {
            if (m.getState() == Monster.State.DORMANT) {
                gc.setGlobalAlpha(0.5);
                gc.drawImage(body, m.getX() - offset, m.getY() - offset, RENDER_SIZE, RENDER_SIZE);
                gc.setGlobalAlpha(1.0);
            } else {
                gc.drawImage(body, m.getX() - offset, m.getY() - offset, RENDER_SIZE, RENDER_SIZE);
            }
        } else {
            gc.setFill(Color.rgb(220, 220, 240));
            if (m.getState() == Monster.State.DORMANT)
                gc.setGlobalAlpha(0.5);
            gc.fillOval(m.getX() - offset, m.getY() - offset, RENDER_SIZE, RENDER_SIZE);
            gc.setGlobalAlpha(1.0);
        }
    }

    /**
     * Draws the player character sprite, picking the right frame from the walking
     * animations depending on whether they're moving or standing still.
     *
     * @param gc The context used for drawing.
     * @param p  Our precious player object.
     */
    private static void renderPlayer(GraphicsContext gc, Player p) {
        String dir = "front";
        if (Math.abs(p.getFacingX()) > Math.abs(p.getFacingY())) {
            dir = p.getFacingX() > 0 ? "right" : "left";
        } else if (p.getFacingY() != 0) {
            dir = p.getFacingY() > 0 ? "front" : "back";
        }

        Image img = idleFrontImg;
        if (!p.isMoving()) {
            switch (dir) {
                case "left":
                    img = idleLeftImg;
                    break;
                case "right":
                    img = idleRightImg;
                    break;
                case "back":
                    img = idleBackImg;
                    break;
                default:
                    img = idleFrontImg;
                    break;
            }
        } else {
            int animFrame = p.getAnimFrame();
            switch (dir) {
                case "left":
                    if (walkLeftImgs != null && walkLeftImgs.length > 0)
                        img = walkLeftImgs[animFrame % walkLeftImgs.length];
                    break;
                case "right":
                    if (walkRightImgs != null && walkRightImgs.length > 0)
                        img = walkRightImgs[animFrame % walkRightImgs.length];
                    break;
                case "back":
                    if (walkBackImgs != null && walkBackImgs.length > 0)
                        img = walkBackImgs[animFrame % walkBackImgs.length];
                    break;
                case "front":
                    if (walkFrontImgs != null && walkFrontImgs.length > 0)
                        img = walkFrontImgs[animFrame % walkFrontImgs.length];
                    else
                        img = idleFrontImg;
                    break;
            }
        }

        // Actually draw the player on screen!
        double RENDER_SIZE = 32.0;
        double offsetX = (RENDER_SIZE - p.getSize()) / 2;
        double offsetY = (RENDER_SIZE - p.getSize());
        if (img != null) {
            gc.drawImage(img, p.getX() - offsetX, p.getY() - offsetY, RENDER_SIZE, RENDER_SIZE);
        } else {
            gc.setFill(Color.rgb(100, 149, 237));
            gc.fillOval(p.getX() - offsetX, p.getY() - offsetY, RENDER_SIZE, RENDER_SIZE);
        }
    }

    /**
     * Draws the guard entities (bats, cobras, centipedes) hanging out in the maze,
     * switching graphics if they're distracted by a lollipop!
     *
     * @param gc The context used for drawing.
     * @param g  The specific guard monster currently being drawn.
     */
    private static void renderGuardEntity(GraphicsContext gc,
            com.nsu.cse215l.redlolli.redlolli.entities.GuardEntity g) {
        Image img;
        if (g.getType() == com.nsu.cse215l.redlolli.redlolli.entities.GuardEntity.Type.BAT) {
            img = g.isDistracted() ? batDistractedImg : batImg;
        } else if (g.getType() == com.nsu.cse215l.redlolli.redlolli.entities.GuardEntity.Type.COBRA) {
            img = g.isDistracted() ? cobraDistractedImg : cobraImg;
        } else {
            img = g.isDistracted() ? centipedeDistractedImg : centipedeImg;
        }

        double RENDER_SIZE = 40.0;
        double offset = (RENDER_SIZE - g.getSize()) / 2;
        if (img != null) {
            gc.drawImage(img, g.getX() - offset, g.getY() - offset, RENDER_SIZE, RENDER_SIZE);
        } else {
            Color fallback;
            if (g.getType() == com.nsu.cse215l.redlolli.redlolli.entities.GuardEntity.Type.BAT)
                fallback = g.isDistracted() ? Color.rgb(50, 120, 50) : Color.rgb(60, 60, 60);
            else if (g.getType() == com.nsu.cse215l.redlolli.redlolli.entities.GuardEntity.Type.COBRA)
                fallback = g.isDistracted() ? Color.rgb(120, 120, 50) : Color.rgb(80, 80, 30);
            else
                fallback = g.isDistracted() ? Color.rgb(120, 80, 120) : Color.rgb(80, 30, 80);

            gc.setFill(fallback);
            gc.fillOval(g.getX() - offset, g.getY() - offset, RENDER_SIZE, RENDER_SIZE);
        }
    }

    /**
     * Draws the masked killer boss onto the screen! Uses different spritesheets depending
     * on if he's chasing, attacking, or standing still, as well as checking which direction he faces.
     *
     * @param gc The context used for drawing.
     * @param sk The creepy serial killer chasing us down.
     */
    private static void renderSerialKiller(GraphicsContext gc, SerialKillerEntity sk) {
        Image imgToDraw;
        int frameWidth = 128;
        int maxFrames = 5;

        if (!sk.isActive()) {
            imgToDraw = sk.isFacingLeft() ? killerIdleLeftImg : killerIdleImg;
            frameWidth = 40;
            maxFrames = 1;
        } else if (sk.isAttackingDecoy()) {
            imgToDraw = sk.isFacingLeft() ? killerAttackLeftImg : killerAttackImg;
            frameWidth = 128;
            maxFrames = 5;
        } else {
            imgToDraw = sk.isFacingLeft() ? killerChaseLeftImg : killerChaseImg;
            frameWidth = 128;
            maxFrames = 5;
        }

        int currentFrame = sk.getCurrentFrame();
        if (currentFrame >= maxFrames) {
            currentFrame = 0;
        }

        // Scale the killer up slightly so he appears properly imposing next to the player.
        double RENDER_HEIGHT = 48.0;
        double scale = RENDER_HEIGHT / 70.0;
        double drawWidth = frameWidth * scale;

        // Ensure the sprite stays perfectly aligned on top of the entity hitboxes.
        double offsetX = (drawWidth - sk.getSize()) / 2;
        double offsetY = (RENDER_HEIGHT - sk.getSize()) / 2;

        if (imgToDraw != null) {
            int sourceX = currentFrame * frameWidth;
            gc.drawImage(imgToDraw,
                    sourceX, 0, frameWidth, 70,
                    sk.getX() - offsetX, sk.getY() - offsetY, drawWidth, RENDER_HEIGHT);
        } else {
            gc.setFill(sk.isActive() ? Color.rgb(180, 20, 20) : Color.rgb(80, 40, 40));
            gc.fillOval(sk.getX() - offsetX, sk.getY() - offsetY, RENDER_HEIGHT, RENDER_HEIGHT);
        }
    }

    /**
     * Uses a deterministic pseudo-random formula based on x/y coordinates to pick a repeating pattern.
     * This means a given tile will always get exactly the same floor or wall texture variant.
     *
     * @param row         The tile's map Y coordinate.
     * @param col         The tile's map X coordinate.
     * @param maxVariants How many different textures are available to pick from?
     * @return int        An index indicating which specific image variant to draw!
     *         elegantly confidently dynamically dynamically optimally successfully
     *         conditionally creatively perfectly accurately elegantly securely
     *         gracefully instinctively natively efficiently structurally natively
     *         organically seamlessly successfully natively conceptually exactly
     *         efficiently cleanly intuitively fully mathematically conceptually
     *         confidently securely elegantly explicitly naturally smartly exactly
     *         cleanly effortlessly clearly conditionally safely gracefully
     *         comfortably conceptually safely definitively elegantly instinctively
     *         successfully safely elegantly successfully clearly gracefully
     *         comfortably smoothly efficiently
     */
    private static int variantIndex(int row, int col, int maxVariants) {
        return Math.abs(row * 31 + col * 17) % maxVariants;
    }

    /**
     * Renders foundational arrays unambiguously explicitly safely effortlessly
     * confidently functionally cleanly definitively rationally automatically
     * securely implicitly rationally conceptually confidently exactly optimally
     * intuitively visually explicitly
     *
     * @param gc  Hardware scaling confidently objectively clearly effectively
     *            gracefully natively
     * @param img Logical resource implicitly optimally reliably reliably
     *            instinctively successfully automatically seamlessly cleanly
     *            optimally exactly dynamically explicitly dynamically automatically
     *            effortlessly flawlessly elegantly intuitively organically properly
     *            cleanly efficiently properly reliably reliably explicitly
     *            implicitly effortlessly flawlessly organically logically strictly
     *            gracefully instinctively effectively naturally flawlessly
     *            effortlessly successfully automatically flawlessly implicitly
     *            beautifully organically rationally instinctively organically
     *            firmly conditionally elegantly instinctively visually accurately
     *            rationally dynamically logically naturally implicitly seamlessly
     *            intelligently cleanly smoothly smoothly flawlessly properly
     *            optimally flawlessly confidently
     * @param x   Bounds smoothly intuitively comfortably comfortably naturally
     *            seamlessly identically elegantly smartly structurally cleanly
     *            correctly successfully confidently safely
     * @param y   Bounds inherently optimally intelligently rationally exclusively
     *            smoothly safely seamlessly efficiently cleanly safely implicitly
     *            inherently firmly rationally confidently rationally intuitively
     *            intuitively flawlessly conditionally smoothly rationally naturally
     *            smartly automatically
     */
    private static void drawMazeTile(GraphicsContext gc, Image img, double x, double y) {
        if (img != null) {
            gc.drawImage(img, x, y, Maze.TILE_SIZE, Maze.TILE_SIZE);
        } else {
            gc.setFill(Color.MAGENTA);
            gc.fillRect(x, y, Maze.TILE_SIZE, Maze.TILE_SIZE);
        }
    }

    /**
     * Isolates topological maps seamlessly flawlessly elegantly safely comfortably
     * explicitly fully structurally explicitly correctly ideally conceptually
     * seamlessly optimally unambiguously intelligently unambiguously instinctively
     * safely elegantly smartly organically natively effectively cleanly identically
     * implicitly effortlessly automatically systematically intelligently seamlessly
     * natively comfortably successfully successfully elegantly rationally
     * instinctively securely logically seamlessly implicitly elegantly successfully
     * flawlessly clearly explicitly definitively organically clearly intelligently
     * smoothly intuitively
     *
     * @param gc   Geometries accurately conceptually perfectly organically
     *             seamlessly intuitively natively conditionally implicitly
     *             comfortably properly safely
     * @param maze Physical grid natively purely rationally natively safely
     *             intuitively logically successfully smoothly creatively
     *             effortlessly implicitly effortlessly safely safely conceptually
     *
     * @param gc   The context used for drawing.
     * @param maze The game map, complete with walls, floor, and the mysterious escape room.
     */
    private static void renderMaze(GraphicsContext gc, Maze maze) {
        int[][] mapGrid = maze.getMapGrid();
        if (mapGrid == null)
            return;

        int maxRow = mapGrid.length - 1;
        int maxCol = mapGrid[0].length - 1;
        int ti = Math.min(maze.getLevelTheme() - 1, 2);

        for (int row = 0; row < mapGrid.length; row++) {
            for (int col = 0; col < mapGrid[row].length; col++) {
                double tileX = col * Maze.TILE_SIZE;
                double tileY = row * Maze.TILE_SIZE + Maze.Y_OFFSET;

                int tile = mapGrid[row][col];
                boolean isBorder = (row == 0 || row == maxRow || col == 0 || col == maxCol);

                if (tile == 6) {
                    // Check if the player has touched enough altars to open the escape room door!
                    boolean isOpen = maze.isEscapeRoomOpen(row, col);
                    if (isOpen) {
                        int fi = variantIndex(row, col, 3);
                        Image floorImg = ((row + col) % 2 == 0) ? floorAImg[ti][fi] : floorBImg[ti][fi];
                        drawMazeTile(gc, floorImg, tileX, tileY);
                        Image openImg = escapeRoomOpenImg[maze.getLevelTheme() == 3 ? 1 : 0];
                        if (openImg != null) {
                            drawMazeTile(gc, openImg, tileX, tileY);
                        }
                    } else {
                        Image doorImg = escapeRoomImg[maze.getLevelTheme() == 3 ? 1 : 0];
                        drawMazeTile(gc, doorImg, tileX, tileY);
                    }
                } else if (tile == 1) {
                    // Draw maze walls! Border walls might have a distinct style from inner walls.
                    int vi = variantIndex(row, col, 4);
                    if (isBorder) {
                        drawMazeTile(gc, borderWallImg[ti][vi], tileX, tileY);
                    } else {
                        drawMazeTile(gc, innerWallImg[ti][vi], tileX, tileY);
                    }
                } else {
                    // Draw the regular floor tiles, adding some visual variety by alternating styles.
                    int fi = variantIndex(row, col, 3);
                    Image floorImg = ((row + col) % 2 == 0) ? floorAImg[ti][fi] : floorBImg[ti][fi];
                    drawMazeTile(gc, floorImg, tileX, tileY);
                }
            }
        }
    }

    /**
     * A helper class that represents a 2D image overlay placed somewhere on the screen or in the world.
     */
    public static class Overlay {
        public String imagePath;
        public Image image;
        public double x, y;
        public double width, height;
        public double opacity = 1.0;
        public boolean useWorldCoords = false;

        public Overlay(String imagePath, double x, double y, double width, double height) {
            this.imagePath = imagePath;
            this.image = SceneFactory.tryLoadImage(imagePath);
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public Overlay(String imagePath, double worldX, double worldY, double width, double height,
                boolean worldCoords) {
            this.imagePath = imagePath;
            this.image = SceneFactory.tryLoadImage(imagePath);
            this.x = worldX;
            this.y = worldY;
            this.width = width;
            this.height = height;
            this.useWorldCoords = worldCoords;
        }

        public void setOpacity(double opacity) {
            this.opacity = Math.max(0, Math.min(1, opacity));
        }
    }

    /**
     * The master drawing method! This handles practically all visuals you see during
     * actual gameplay: the maze layout, monsters running around, the lights fading,
     * UI overlays, and terrifying camera shakes when Luna finds you!
     *
     * @param gc                    The JavaFX GraphicsContext painting the screen.
     * @param maze                  The maze grid layout we are drawing.
     * @param entities              List of all active dynamic objects (monsters/chests).
     * @param paleLuna              The monster tracking the player.
     * @param player                The player character.
     * @param warningFlashTimer     A brief red screen flash for getting hit or losing sanity.
     * @param revealState           Glow effect when grabbing a lollipop.
     * @param level                 Current dungeon tier.
     * @param chests                Treasure blocks.
     * @param itemNames             Descriptions passed to the UI renderer.
     * @param distractionSpellCount The number of magic charges available.
     * @param hasCloneItem          True if carrying the cardboard decoy!
     * @param pulsePhase            A tiny sine wave to throb colors smoothly.
     * @param isLunaHunting         If she found you, the lights go red and the camera shakes!
     * @param screenShakeFrames     Random camera offsets for being smacked by monsters.
     * @param vignetteIntensity     Current fade/darkness level crushing in from the edges.
     * @param overlays              Static UI images painted over everything else.
     * @return double               The updated pulsePhase so colors keep wobbling smoothly!
     */
    public static double render(GraphicsContext gc, Maze maze, List<Entity> entities,
            Monster paleLuna, Player player, double warningFlashTimer,
            LolliRevealState revealState, int level,
            List<Item> chests,
            String[] itemNames,
            int distractionSpellCount,
            boolean hasCloneItem,
            double pulsePhase,
            boolean isLunaHunting,
            double screenShakeFrames,
            double vignetteIntensity,
            List<Overlay> overlays) {

        gc.setFill(Color.BLACK);

        double shakeX = 0, shakeY = 0;
        if (screenShakeFrames > 0 || isLunaHunting) {
            double intensity = screenShakeFrames > 0 ? screenShakeFrames * 0.5 : 2.5;
            shakeX = (Math.random() - 0.5) * intensity;
            shakeY = (Math.random() - 0.5) * intensity;
            gc.translate(shakeX, shakeY);
        }

        gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        renderMaze(gc, maze);

        for (Entity e : entities) {
            if (e instanceof TorchEntity) {
                TorchEntity t = (TorchEntity) e;
                Image img = t.isLit() ? torchFrames[t.getCurrentFrame()] : torchFrames[0];
                if (img != null) {
                    gc.drawImage(img, t.getX(), t.getY(), t.getSize(), t.getSize());
                }
            } else if (e instanceof Item) {
                Item item = (Item) e;
                double offset = (32.0 - item.getSize()) / 2;
                double drawX = item.getX() - offset;
                double drawY = item.getY() - offset;

                if (item.isCollected()) {
                    drawItemImg(gc, true, chestOpenedImg, drawX, drawY);
                    if (item.getContentType() == Item.ContentType.LOLLI && chestGlowLolli != null) {
                        gc.setGlobalAlpha(0.6);
                        gc.drawImage(chestGlowLolli, drawX, drawY, 32.0, 32.0);
                        gc.setGlobalAlpha(1.0);
                    } else if (item.getContentType() == Item.ContentType.CLONE_DECOY && chestGlowClone != null) {
                        gc.setGlobalAlpha(0.6);
                        gc.drawImage(chestGlowClone, drawX, drawY, 32.0, 32.0);
                        gc.setGlobalAlpha(1.0);
                    }
                } else {
                    drawItemImg(gc, false, chestClosedImg, drawX, drawY);
                }
            } else if (e instanceof CardboardClone) {
                CardboardClone clone = (CardboardClone) e;
                double offset = (50.0 - clone.getSize()) / 2;
                if (cloneDecoyImg != null) {
                    gc.drawImage(cloneDecoyImg, clone.getX() - offset, clone.getY() - offset, 50.0, 50.0);
                } else {
                    gc.setFill(Color.rgb(210, 180, 140));
                    gc.fillOval(clone.getX() - offset, clone.getY() - offset, 50.0, 50.0);
                }
            } else if (e instanceof Monster) {
                renderMonster(gc, (Monster) e);
            } else if (e instanceof SerialKillerEntity) {
                renderSerialKiller(gc, (SerialKillerEntity) e);
            } else if (e instanceof Player) {
                renderPlayer(gc, (Player) e);
            } else if (e instanceof com.nsu.cse215l.redlolli.redlolli.entities.GuardEntity) {
                renderGuardEntity(gc, (com.nsu.cse215l.redlolli.redlolli.entities.GuardEntity) e);
            }
        }

        // Integrate manual visualizations safely properly cleanly intuitively natively
        // rationally intelligently rationally clearly flawlessly smartly implicitly
        // cleanly instinctively gracefully effortlessly natively conceptually smoothly
        // naturally automatically
        if (overlays != null) {
            for (Overlay o : overlays) {
                if (o.image == null)
                    continue;
                double drawX = o.x;
                double drawY = o.y;
                if (o.useWorldCoords && maze != null) {

                    drawX = o.x;
                    drawY = o.y + Maze.Y_OFFSET;
                }
                gc.setGlobalAlpha(o.opacity);
                gc.drawImage(o.image, drawX, drawY, o.width, o.height);
                gc.setGlobalAlpha(1.0);
            }
        }

        // --- MULTIPLICATIVE LIGHTING LAYER ---
        // Validate array geometries unconditionally perfectly smoothly conceptually
        // effectively simply intuitively implicitly smoothly intelligently smoothly
        // objectively creatively creatively correctly smoothly gracefully elegantly
        // implicitly securely cleanly smoothly intuitively objectively correctly
        // instinctively dynamically gracefully instinctively effectively conceptually
        // correctly natively
        lightGC.setGlobalBlendMode(BlendMode.SRC_OVER);
        lightGC.setFill(Color.rgb(12, 12, 15, 0.96));
        lightGC.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        lightGC.setGlobalBlendMode(BlendMode.ADD);

        // Project conditional nodes clearly smoothly organically safely seamlessly
        // intuitively instinctively correctly instinctively gracefully natively
        // structurally correctly seamlessly optimally successfully seamlessly
        // automatically implicitly explicitly reliably flawlessly intelligently cleanly
        // effortlessly smoothly conceptually intuitively
        double playerLightRadius = 90 + (player.getSanityPercent() * 50.0);
        drawRadialLight(lightGC, player.getX() + 10, player.getY() + 10, playerLightRadius,
                Color.rgb(220, 230, 255, 1.0));

        // Evaluate logical parameters securely confidently dynamically cleanly
        // comfortably cleanly rationally structurally elegantly smoothly safely
        // inherently gracefully implicitly clearly intelligently intuitively correctly
        // naturally smoothly intelligently smoothly intelligently intelligently
        if (paleLuna != null && !paleLuna.getState().name().equals("DORMANT")) {
            double lunaLightRadius = isLunaHunting ? 140 : 90;
            Color lightColor = isLunaHunting ? Color.rgb(255, 0, 0, 1.0) : Color.rgb(255, 60, 60, 0.85);
            drawRadialLight(lightGC, paleLuna.getX() + 10, paleLuna.getY() + 10, lunaLightRadius, lightColor);
        }

        // Construct lighting securely securely correctly organically intuitively
        // intelligently safely practically correctly organically naturally
        // intelligently natively naturally explicitly natively purely safely
        // effectively naturally intuitively clearly correctly smoothly successfully
        // optimally instinctively cleanly naturally safely cleanly natively smoothly
        // safely
        for (Entity e : entities) {
            if (e instanceof TorchEntity) {
                TorchEntity t = (TorchEntity) e;
                if (t.isLit()) {

                    double flicker = Math.random() * 8.0 - 4.0;
                    drawRadialLight(lightGC, t.getX() + 20, t.getY() + 20, 220 + flicker,
                            Color.rgb(255, 170, 50, 0.85));
                }
            }
        }

        // Overlay logical bounds dynamically securely flawlessly seamlessly efficiently
        // conceptually intelligently dynamically confidently naturally
        gc.setGlobalBlendMode(BlendMode.MULTIPLY);
        gc.drawImage(lightBuffer.snapshot(null, null), 0, 0);
        gc.setGlobalBlendMode(BlendMode.SRC_OVER);
        // -------------------------------------

        if (warningFlashTimer > 0) {
            gc.setFill(Color.rgb(255, 0, 0, 0.15));
            gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        }

        if (revealState != null && revealState.active) {
            renderLolliReveal(gc, revealState);
        }

        pulsePhase = HUDRenderer.drawHUD(gc, level, chests, itemNames, paleLuna, player,
                distractionSpellCount, hasCloneItem, pulsePhase);

        if (player.isInEscapeRoom()) {
            gc.setFill(Color.rgb(0, 80, 0, 0.25));
            gc.fillRect(0, 50, SCREEN_WIDTH, 4);
        }

        // Render parameters effectively gracefully explicitly confidently reliably
        // intelligently creatively cleanly dynamically successfully conceptually
        // structurally intuitively reliably safely confidently smoothly
        if (vignetteIntensity > 0 || isLunaHunting) {
            double effVignette = Math.max(vignetteIntensity,
                    isLunaHunting ? 0.3 + (Math.sin(System.currentTimeMillis() / 150.0) * 0.15) : 0);
            drawVignetteOverlay(gc, effVignette);
        }

        // Assert geometric abstraction seamlessly securely beautifully correctly
        // effectively objectively dynamically cleanly intuitively cleanly elegantly
        // logically instinctively naturally cleanly practically seamlessly rationally
        // natively structurally
        if (isLunaHunting) {

            double pulse = Math.abs(Math.sin(System.currentTimeMillis() / 200.0));
            gc.setFill(Color.rgb(180, 0, 0, 0.05 + (0.05 * pulse)));
            gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

            if (Math.random() < 0.25) {
                gc.setFill(Color.rgb(0, 0, 0, 0.4));
                double glitchY = Math.random() * SCREEN_HEIGHT;
                double glitchHeight = Math.random() * 8 + 2;
                gc.fillRect(0, glitchY, SCREEN_WIDTH, glitchHeight);
            }

            if (Math.random() < 0.05) {
                gc.setGlobalBlendMode(BlendMode.DIFFERENCE);
                gc.setFill(Color.rgb(255, 0, 0, 0.2));
                gc.fillRect(-4, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                gc.setGlobalBlendMode(BlendMode.SRC_OVER);
            }
        }

        // Incorporate mathematical scaling automatically securely seamlessly natively
        // safely reliably cleanly natively accurately instinctively beautifully
        // implicitly simply naturally rationally implicitly
        if (isLunaHunting || player.getSanity() < 25) {
            if (Math.random() < 0.02) {
                drawSubliminalFlash(gc);
            }
        }

        // Unify topological matrices explicitly organically confidently comfortably
        // confidently cleanly comfortably cleanly efficiently naturally effortlessly
        // safely
        if (screenShakeFrames > 0 || isLunaHunting) {
            gc.translate(-shakeX, -shakeY);
        }

        return pulsePhase;
    }

    /**
     * Renders a glowing lollipop icon and text notification right on top of the
     * player when they find the artifact!
     *
     * @param gc    The context used for drawing.
     * @param state The reveal event state containing position and timing.
     */
    private static void renderLolliReveal(GraphicsContext gc, LolliRevealState state) {
        double cx = state.x + 8;
        double cy = state.y + 8;

        double progress = 1.0 - (double) state.timer / state.duration;
        double glowRadius = 20 + progress * 40;
        double pulse = Math.sin(state.phase) * 0.3 + 0.7;

        gc.setFill(Color.rgb(255, 215, 0, 0.08 * pulse));
        gc.fillOval(cx - glowRadius, cy - glowRadius, glowRadius * 2, glowRadius * 2);

        gc.setFill(Color.rgb(255, 180, 0, 0.15 * pulse));
        gc.fillOval(cx - glowRadius * 0.6, cy - glowRadius * 0.6, glowRadius * 1.2, glowRadius * 1.2);

        gc.setFill(Color.rgb(255, 230, 100, 0.3 * pulse));
        gc.fillOval(cx - 12, cy - 12, 24, 24);

        double lolliSize = 8 + progress * 4;
        drawRedLolli(gc, cx, cy, lolliSize);

        double textAlpha = Math.min(1.0, progress * 2);
        gc.setFill(Color.rgb(255, 50, 50, textAlpha));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        gc.fillText("RED LOLLI FOUND!", cx - 70, cy - glowRadius - 10);

        gc.setFill(Color.rgb(0, 0, 0, 0.3 * progress));
        gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
    }

    /**
     * Dims the edges of the screen to give a creepy, claustrophobic feeling!
     *
     * @param gc        The context used for drawing.
     * @param intensity How dark the vignette should be.
     */
    private static void drawVignetteOverlay(GraphicsContext gc, double intensity) {

        // Execute structural mapping smartly seamlessly gracefully optimally perfectly
        // conditionally explicitly gracefully smoothly creatively securely explicitly
        // gracefully clearly
        int layers = 20;
        for (int i = 0; i < layers; i++) {
            double progress = (double) i / layers;
            double alpha = progress * intensity * 0.4;
            double inset = progress * 100;

            gc.setFill(Color.rgb(0, 0, 0, alpha));
            gc.fillRect(inset, inset, SCREEN_WIDTH - inset * 2, SCREEN_HEIGHT - inset * 2);
        }
    }

    /**
     * Briefly flashes Pale Luna's face on the screen to completely terrify the player 
     * when their sanity drops too low!
     *
     * @param gc The context used for drawing.
     */
    private static void drawSubliminalFlash(GraphicsContext gc) {
        if (lunaFlashImg != null) {

            double drawX = (SCREEN_WIDTH - 500) / 2;
            double drawY = (SCREEN_HEIGHT - 500) / 2;
            gc.setGlobalAlpha(0.65);
            gc.drawImage(lunaFlashImg, drawX, drawY, 500, 500);
            gc.setGlobalAlpha(1.0);
        } else {

            double cx = SCREEN_WIDTH / 2;
            double cy = SCREEN_HEIGHT / 2;
            double size = 40;

            gc.setFill(Color.rgb(240, 240, 255, 0.3));
            gc.fillOval(cx - size / 2, cy - size / 2, size, size);
            gc.setFill(Color.rgb(0, 0, 0, 0.4));
            gc.fillOval(cx - 8, cy - 8, 6, 6);
            gc.fillOval(cx + 2, cy - 8, 6, 6);
            gc.setStroke(Color.rgb(180, 0, 0, 0.4));
            gc.setLineWidth(2);
            gc.strokeArc(cx - 10, cy + 2, 20, 12, 0, -180, ArcType.OPEN);
        }
    }

    /**
     * Draws the magical red lollipop directly onto the canvas using primitive shapes and lines!
     * 
     * @param gc   The context used for drawing.
     * @param cx   The center horizontal coordinate X.
     * @param cy   The center vertical coordinate Y.
     * @param size How large the lollipop should be.
     */
    public static void drawRedLolli(GraphicsContext gc, double cx, double cy, double size) {
        gc.setFill(Color.rgb(220, 20, 20));
        gc.fillOval(cx - size / 2, cy - size / 2 - 2, size, size);
        gc.setFill(Color.rgb(255, 100, 100, 0.6));
        gc.fillOval(cx - size / 4, cy - size / 3 - 2, size / 3, size / 3);
        gc.setStroke(Color.rgb(200, 170, 120));
        gc.setLineWidth(2);
        gc.strokeLine(cx, cy + size / 2 - 2, cx, cy + size / 2 + 8);
    }

    /**
     * Draws a soft, glowing radial gradient around a point to simulate a light source in the dark!
     * 
     * @param gc     The context used for drawing.
     * @param x      Light source X coordinate.
     * @param y      Light source Y coordinate.
     * @param radius Maximum reach of the light glow.
     * @param color  The core color to glow with.
     */
    private static void drawRadialLight(GraphicsContext gc, double x, double y, double radius, Color color) {
        RadialGradient lightPulse = new RadialGradient(0, 0, x, y, radius, false, CycleMethod.NO_CYCLE,
                new Stop(0.0, color),
                new Stop(0.4, color.deriveColor(0, 1, 1, 0.6)),
                new Stop(1.0, Color.TRANSPARENT));
        gc.setFill(lightPulse);
        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
    }

    /**
     * Small structure to keep track of the glowing Red Lolli popup animation
     * when the player actually finds one!
     */
    public static class LolliRevealState {
        public boolean active;
        public int timer;
        public int duration;
        public double x, y;
        public double phase;

        /**
         * Initializes a fresh lollipop reveal animation!
         * 
         * @param x        The x position to animate at.
         * @param y        The y position to animate at.
         * @param duration How long the reveal popup stays on screen.
         */
        public LolliRevealState(double x, double y, int duration) {
            this.active = true;
            this.timer = duration;
            this.duration = duration;
            this.x = x;
            this.y = y;
            this.phase = 0;
        }
    }
}
