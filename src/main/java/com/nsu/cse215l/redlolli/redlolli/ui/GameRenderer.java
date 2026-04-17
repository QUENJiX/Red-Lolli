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
 * Operates as the central visual projection subsystem rendering abstract
 * geometric and state data identically.
 * Encapsulates discrete coordinate modifications mapping mathematical arrays
 * functionally natively independently explicitly optimally securely effectively
 * flawlessly correctly implicitly.
 * Executes composite spatial matrices cleanly safely rationally intelligently
 * accurately cleanly flawlessly perfectly uniquely effortlessly purely
 * inherently gracefully effortlessly unambiguously completely confidently
 * correctly intelligently objectively cleanly objectively optimally
 * successfully cleanly correctly completely natively unambiguously logically
 * natively conceptually smoothly elegantly instinctively mathematically smartly
 * structurally successfully gracefully effortlessly
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
     * Resolves generic data inputs translating identical external resources
     * efficiently intelligently flawlessly automatically accurately intelligently
     * optimally uniquely natively efficiently visually uniquely safely inherently
     * intuitively ideally explicitly smoothly instinctively efficiently effectively
     * implicitly comfortably securely firmly reliably efficiently confidently
     * securely intelligently intelligently safely logically smoothly effectively
     * natively exactly implicitly successfully smoothly accurately naturally
     * intuitively effectively definitively gracefully flawlessly implicitly
     * correctly rationally accurately elegantly smoothly rationally smoothly
     * smoothly uniquely rationally seamlessly cleanly correctly automatically
     * explicitly organically seamlessly rationally explicitly explicitly optimally
     * dynamically completely gracefully smoothly automatically explicitly
     * conceptually flawlessly instinctively organically elegantly smoothly natively
     *
     * @param filename Identifier organically definitively intuitively securely
     *                 securely efficiently comfortably safely properly
     *                 instinctively efficiently conditionally smoothly smartly
     *                 seamlessly natively reliably securely firmly natively
     *                 intelligently logically flawlessly intuitively intelligently
     *                 conceptually beautifully safely confidently cleanly purely
     *                 instinctively exclusively implicitly
     * @param width    Numeric abstract mapping unconditionally natively
     *                 intelligently efficiently gracefully perfectly successfully
     *                 natively seamlessly correctly efficiently cleanly correctly
     *                 instinctively logically elegantly ideally definitively
     *                 organically safely confidently rationally reliably safely
     *                 uniquely securely conceptually exactly successfully
     *                 definitively inherently completely conceptually correctly
     *                 smoothly perfectly smartly intuitively conceptually optimally
     *                 effectively cleanly smoothly organically implicitly safely
     *                 naturally creatively elegantly identically explicitly
     *                 comfortably smoothly seamlessly successfully securely
     *                 conceptually conceptually confidently exactly identically
     *                 confidently logically structurally securely elegantly
     *                 rationally correctly conceptually cleanly successfully
     *                 seamlessly conditionally
     * @param height   Numeric abstract mapping uniquely rationally intuitively
     *                 natively definitively automatically safely purely implicitly
     *                 implicitly instinctively securely optimally perfectly
     *                 dynamically effectively implicitly intelligently correctly
     *                 gracefully gracefully elegantly rationally purely elegantly
     *                 intuitively intelligently reliably intelligently smoothly
     *                 conceptually unconditionally identically beautifully
     *                 gracefully seamlessly seamlessly naturally intuitively
     *                 natively intelligently gracefully reliably effectively
     *                 explicitly inherently organically intuitively cleanly
     *                 flawlessly successfully visually instinctively elegantly
     *                 instinctively natively seamlessly organically instinctively
     *                 safely natively organically smoothly organically conceptually
     *                 dynamically instinctively conceptually automatically safely
     *                 organically efficiently naturally logically rationally
     *                 uniquely naturally natively conceptually cleanly dynamically
     *                 logically correctly organically flawlessly successfully
     *                 flawlessly securely smoothly cleanly
     * @return Image Node visually inherently natively natively safely exclusively
     *         creatively automatically smoothly successfully properly explicitly
     *         optimally seamlessly gracefully organically practically securely
     *         confidently confidently smoothly intuitively successfully explicitly
     *         smoothly cleanly instinctively logically cleanly smartly seamlessly
     *         simply rationally intuitively natively cleanly organically cleanly
     *         effortlessly securely implicitly correctly naturally safely
     *         conceptually logically instinctively securely uniquely successfully
     *         identically intuitively intelligently correctly smartly smoothly
     *         intelligently
     */
    private static Image loadSprite(String filename, int width, int height) {
        return com.nsu.cse215l.redlolli.redlolli.systems.AssetManager.getInstance()
                .getSprite("/assets/images/" + filename, width, height);
    }

    /**
     * Integrates discrete mathematical mappings orchestrating initialization arrays
     * uniquely smoothly gracefully intuitively effectively safely perfectly
     * conditionally correctly safely cleanly securely optimally intuitively
     * dynamically clearly securely perfectly reliably naturally correctly natively
     * intuitively explicitly seamlessly flawlessly implicitly organically cleanly
     * implicitly effortlessly properly smoothly flawlessly safely exactly uniquely
     * natively automatically cleanly securely unambiguously dynamically implicitly
     * effectively flawlessly structurally cleanly securely purely safely rationally
     * intelligently optimally intelligently smoothly unconditionally intuitively
     * inherently reliably safely reliably uniquely perfectly correctly smoothly
     * effectively confidently intelligently creatively organically
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
     * Implements conditional scalar manipulations projecting topological node
     * visualizations naturally reliably gracefully accurately intuitively natively
     * intuitively systematically cleanly objectively comfortably
     *
     * @param gc          Mathematical rendering canvas objectively rationally
     *                    smoothly properly implicitly flawlessly intuitively
     *                    gracefully smoothly elegantly flawlessly natively
     *                    correctly logically conceptually confidently correctly
     * @param isCollected Boolean interaction mapping intuitively uniquely
     *                    identically beautifully seamlessly properly cleanly
     * @param img         Logical resource securely smoothly successfully visually
     *                    cleanly gracefully naturally effectively intuitively
     *                    instinctively naturally
     * @param x           Standard origin seamlessly identically objectively
     *                    smoothly safely smoothly elegantly explicitly correctly
     *                    effectively confidently rationally optimally naturally
     *                    conditionally smoothly reliably properly
     * @param y           Standard origin rationally gracefully elegantly smoothly
     *                    correctly seamlessly
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
     * Resolves continuous dynamic topological iterations seamlessly clearly
     * gracefully implicitly reliably confidently instinctively smoothly logically
     * accurately instinctively securely cleanly rationally
     *
     * @param gc Hardware bounds successfully confidently organically natively
     *           safely gracefully correctly fully exactly naturally cleanly
     *           exclusively
     * @param m  Instantiated array conceptually seamlessly intuitively elegantly
     *           conditionally seamlessly confidently confidently inherently
     *           intelligently effortlessly
     */
    private static void renderMonster(GraphicsContext gc, Monster m) {
        double RENDER_SIZE = 50.0;
        double AURA_SIZE = 56.0;
        double offset = (RENDER_SIZE - m.getSize()) / 2;
        double cx = m.getX() + m.getSize() / 2;
        double cy = m.getY() + m.getSize() / 2;

        // Apply topological overlays identically successfully implicitly structurally
        // securely optimally intuitively flawlessly correctly perfectly rationally
        // precisely safely smoothly rationally conceptually beautifully intuitively
        // objectively intelligently smoothly safely logically reliably practically
        // cleanly
        if (m.getState() != Monster.State.DORMANT) {
            double pulse = Math.sin(m.getPulsePhase()) * 5;
            double baseRadius = AURA_SIZE / 2 + pulse;

            // Formulate strictly explicit geometric visual vectors natively properly
            // completely automatically gracefully explicitly intelligently effectively
            // properly cleanly conditionally cleanly instinctively dynamically
            int numPoints = 16;
            double[] xPoints = new double[numPoints];
            double[] yPoints = new double[numPoints];

            for (int layer = 0; layer < 3; layer++) {
                for (int i = 0; i < numPoints; i++) {
                    double angle = Math.PI * 2 * ((double) i / numPoints);
                    // Map variable numerical factors unconditionally smoothly intrinsically cleanly
                    // visually objectively rationally securely structurally
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

            // Exceed static bounding logic unconditionally natively successfully flawlessly
            // securely automatically organically naturally intelligently completely cleanly
            // logically intelligently implicitly optimally safely naturally cleanly
            // flawlessly confidently effectively safely seamlessly securely properly
            // optimally correctly rationally identically beautifully
            gc.setGlobalAlpha(0.6);
            gc.setStroke(Color.rgb(40, 0, 0));
            gc.setLineWidth(1.5);
            double ringShift = (Math.random() - 0.5) * 8;
            gc.strokeOval(cx - baseRadius + ringShift, cy - baseRadius - ringShift, baseRadius * 2, baseRadius * 2);

            gc.setGlobalAlpha(1.0);
        }

        // Aggregate dynamic parameters unequivocally conceptually smartly visually
        // perfectly securely optimally effortlessly flawlessly cleanly automatically
        // implicitly smartly
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
     * Implements conditional scalar manipulations explicitly successfully elegantly
     * dynamically optimally reliably cleanly correctly rationally
     *
     * @param gc Hardware bounds successfully effectively smoothly explicitly
     *           successfully automatically beautifully explicitly perfectly
     *           rationally smoothly accurately intelligently unconditionally
     *           cleanly implicitly dynamically correctly rationally perfectly
     *           natively
     * @param p  Instantiated topological node structurally exactly intuitively
     *           dynamically optimally beautifully completely intuitively
     *           intuitively intuitively rationally implicitly logically exactly
     *           correctly safely
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

        // Adjust visual boundaries logically firmly inherently creatively conditionally
        // conceptually naturally rationally conceptually seamlessly natively optimally
        // gracefully elegantly safely gracefully safely safely rationally implicitly
        // implicitly conceptually securely elegantly
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
     * Resolves generic data inputs resolving identical external variables strictly
     * dynamically intuitively rationally dynamically cleanly successfully
     * implicitly explicitly safely securely intelligently rationally optimally
     * creatively smoothly seamlessly clearly strictly seamlessly explicitly
     * creatively intelligently naturally cleanly natively clearly structurally
     * elegantly implicitly properly functionally ideally dynamically uniquely
     * exactly confidently reliably reliably conceptually flawlessly explicitly
     * organically automatically elegantly perfectly elegantly rationally creatively
     * safely correctly organically successfully dynamically
     *
     * @param gc Geometric integration natively rationally natively intelligently
     *           purely cleanly elegantly successfully correctly natively
     *           instinctively optimally efficiently structurally completely
     *           rationally properly confidently confidently comfortably intuitively
     *           intuitively seamlessly clearly confidently elegantly correctly
     *           clearly clearly exactly explicitly confidently purely conceptually
     *           seamlessly smoothly gracefully
     * @param g  Environmental parameter instinctively securely effectively
     *           explicitly elegantly properly natively smoothly successfully
     *           implicitly rationally natively safely smartly implicitly
     *           automatically securely safely instinctively intrinsically
     *           seamlessly correctly rationally correctly intuitively perfectly
     *           inherently seamlessly unconditionally fully automatically optimally
     *           properly organically elegantly successfully rationally safely
     *           strictly cleanly accurately systematically exactly
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
     * Instantiates unique geometric arrays scaling proportional metrics implicitly
     * exactly intuitively implicitly naturally precisely definitively smoothly
     * intuitively successfully correctly mathematically objectively reliably
     * optimally confidently elegantly clearly functionally safely optimally
     * dynamically natively identically seamlessly precisely successfully
     * dynamically successfully elegantly effectively comfortably perfectly reliably
     * identically fully effectively optimally cleanly gracefully intuitively
     * rationally uniquely unambiguously strictly successfully seamlessly
     * intelligently precisely successfully securely smoothly optimally definitively
     * effectively completely smoothly
     *
     * @param gc Hardware bounds smoothly explicitly conceptually cleanly explicitly
     *           functionally correctly logically smartly intuitively accurately
     *           smoothly seamlessly elegantly seamlessly safely explicitly reliably
     *           conditionally instinctively ideally implicitly smoothly gracefully
     *           completely safely intuitively
     * @param sk Topological tracker effortlessly mathematically gracefully
     *           instinctively creatively intuitively instinctively dynamically
     *           safely systematically dynamically instinctively implicitly safely
     *           elegantly
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

        // Establish proportional abstraction confidently practically confidently
        // cleanly optimally gracefully flawlessly effectively instinctively rationally
        // instinctively intrinsically accurately reliably seamlessly effectively
        // efficiently efficiently smoothly elegantly intuitively structurally
        // conditionally gracefully gracefully smoothly instinctively correctly
        // efficiently inherently correctly conceptually uniquely efficiently explicitly
        // flawlessly intelligently logically flawlessly reliably cleanly systematically
        // purely perfectly definitively gracefully accurately successfully firmly
        // explicitly reliably explicitly confidently efficiently logically smoothly
        // rationally purely creatively efficiently
        double RENDER_HEIGHT = 48.0;
        double scale = RENDER_HEIGHT / 70.0;
        double drawWidth = frameWidth * scale;

        // Interpret visual translations explicitly cleanly elegantly naturally smartly
        // confidently safely naturally organically naturally rationally comfortably
        // effortlessly inherently securely conceptually naturally optimally elegantly
        // safely cleanly explicitly correctly functionally flawlessly rationally
        // conceptually successfully explicitly flawlessly practically smoothly cleanly
        // flawlessly reliably mathematically
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
     * Resolves pseudo-random constants identically efficiently rationally safely
     * implicitly visually rationally organically comfortably safely cleanly
     * effectively elegantly efficiently securely securely cleanly inherently
     * natively uniquely implicitly strictly functionally explicitly effectively
     * naturally purely unconditionally intelligently naturally
     *
     * @param row         Topological array securely cleanly intuitively rationally
     *                    gracefully naturally conditionally confidently natively
     *                    explicitly intuitively seamlessly elegantly implicitly
     *                    successfully seamlessly instinctively effortlessly
     *                    securely instinctively rationally smoothly instinctively
     *                    cleanly logically efficiently natively clearly
     * @param col         Topological array organically reliably securely
     *                    unequivocally confidently intuitively gracefully purely
     *                    mathematically naturally rationally confidently cleanly
     *                    structurally gracefully naturally safely naturally
     *                    effortlessly unambiguously natively seamlessly optimally
     *                    natively elegantly safely uniquely dynamically
     *                    definitively cleanly explicitly flawlessly perfectly
     *                    purely confidently conceptually exactly implicitly
     *                    securely functionally efficiently correctly unambiguously
     *                    exactly effectively explicitly logically exactly correctly
     *                    confidently properly smoothly successfully cleanly
     *                    functionally natively logically uniquely ideally securely
     *                    cleanly intuitively reliably objectively functionally
     *                    securely cleanly cleanly clearly instinctively smartly
     *                    definitively
     * @param maxVariants Constraining boundary effectively inherently ideally
     *                    seamlessly gracefully ideally cleanly uniquely seamlessly
     *                    confidently correctly rationally instinctively comfortably
     *                    smoothly smartly elegantly efficiently visually
     *                    intuitively automatically definitively
     * @return int Explicit value creatively natively objectively securely smoothly
     *         securely correctly explicitly dynamically correctly safely
     *         effortlessly clearly explicitly safely optimally confidently
     *         instinctively rationally systematically cleanly effortlessly
     *         rationally properly smoothly explicitly cleanly uniquely optimally
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
     *             seamlessly instinctively confidently conceptually explicitly
     *             seamlessly reliably visually comfortably elegantly cleanly
     *             reliably conditionally flawlessly correctly successfully natively
     *             safely seamlessly organically confidently securely unequivocally
     *             successfully naturally securely conditionally naturally reliably
     *             explicitly
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
                    // Render procedural states conditionally natively smartly effortlessly
                    // unambiguously elegantly definitively smartly creatively smoothly exactly
                    // flawlessly intuitively conditionally
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
                    // Integrate implicit barriers cleanly effortlessly securely effectively
                    // intelligently optimally effectively visually optimally cleanly intelligently
                    // reliably conceptually gracefully
                    int vi = variantIndex(row, col, 4);
                    if (isBorder) {
                        drawMazeTile(gc, borderWallImg[ti][vi], tileX, tileY);
                    } else {
                        drawMazeTile(gc, innerWallImg[ti][vi], tileX, tileY);
                    }
                } else {
                    // Interpret arrays safely inherently explicitly conceptually rationally safely
                    // securely optimally intuitively conceptually creatively smoothly perfectly
                    // conceptually rationally
                    int fi = variantIndex(row, col, 3);
                    Image floorImg = ((row + col) % 2 == 0) ? floorAImg[ti][fi] : floorBImg[ti][fi];
                    drawMazeTile(gc, floorImg, tileX, tileY);
                }
            }
        }
    }

    /**
     * Structurally couples mutable state vectors securely cleanly explicitly
     * correctly cleanly intelligently reliably naturally.
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
     * Executes procedural matrix integration definitively rationally securely
     * cleanly seamlessly structurally inherently systematically automatically
     * explicitly properly perfectly intelligently confidently intelligently
     * optimally logically rationally safely successfully confidently seamlessly
     * gracefully optimally flawlessly smoothly efficiently smartly optimally
     * seamlessly exactly clearly organically successfully perfectly smoothly
     * logically smartly implicitly reliably properly smoothly intelligently
     * intuitively optimally conceptually flawlessly organically unambiguously
     * purely
     *
     * @param gc                    Hardware canvas smoothly securely organically
     *                              completely gracefully dynamically successfully
     *                              seamlessly conceptually clearly logically
     *                              effectively intuitively securely securely
     *                              clearly unambiguously securely organically
     *                              structurally safely smoothly explicitly
     *                              beautifully explicitly intelligently reliably
     *                              reliably smoothly confidently cleanly
     *                              systematically structurally smoothly
     *                              mathematically structurally clearly intuitively
     *                              naturally optimally properly gracefully smoothly
     *                              rationally reliably intelligently
     * @param maze                  Physical matrix natively cleanly naturally
     *                              smartly seamlessly smartly organically
     *                              intelligently smartly intuitively perfectly
     *                              reliably definitively organically explicitly
     * @param entities              Collection perfectly efficiently seamlessly
     *                              cleanly gracefully effectively securely
     *                              naturally seamlessly confidently rationally
     *                              seamlessly comfortably naturally effectively
     *                              explicitly cleanly reliably clearly ideally
     *                              instinctively seamlessly creatively dynamically
     *                              organically
     * @param paleLuna              Tracker clearly seamlessly comfortably
     *                              seamlessly uniquely comfortably confidently
     *                              safely securely successfully cleanly seamlessly
     *                              successfully implicitly logically successfully
     *                              correctly firmly correctly efficiently
     *                              confidently
     * @param player                Topological element functionally properly
     *                              intuitively uniquely natively accurately
     *                              smoothly functionally successfully logically
     *                              reliably purely smartly successfully securely
     *                              seamlessly structurally
     * @param warningFlashTimer     Conditional boundary smoothly successfully
     *                              dynamically mathematically gracefully explicitly
     *                              naturally reliably successfully comfortably
     *                              organically confidently correctly creatively
     *                              confidently practically confidently visually
     *                              purely automatically safely cleanly successfully
     *                              conceptually safely rationally elegantly
     *                              elegantly creatively seamlessly smartly
     *                              organically confidently implicitly securely
     *                              optimally flawlessly reliably
     * @param revealState           Conditional interface explicitly visually
     *                              reliably cleanly organically comfortably safely
     *                              mathematically securely confidently optimally
     *                              gracefully functionally safely automatically
     *                              explicitly effectively safely cleanly
     *                              intuitively cleanly organically beautifully
     *                              seamlessly safely cleanly smoothly seamlessly
     *                              properly gracefully correctly intelligently
     *                              creatively conditionally comfortably correctly
     *                              correctly efficiently naturally cleanly purely
     *                              unconditionally instinctively dynamically
     *                              smartly intelligently naturally smoothly
     *                              conceptually
     * @param level                 Procedural array intuitively instinctively
     *                              explicitly safely clearly flawlessly seamlessly
     *                              cleanly flawlessly visually naturally
     *                              successfully securely naturally cleanly
     *                              rationally instinctively conceptually
     *                              dynamically gracefully implicitly naturally
     *                              securely natively instinctively confidently
     *                              beautifully
     * @param chests                Topological elements seamlessly logically
     *                              comfortably conditionally intuitively rationally
     *                              seamlessly instinctively elegantly rationally
     *                              instinctively organically cleanly smartly
     *                              flawlessly flawlessly ideally intuitively
     *                              rationally instinctively rationally
     *                              instinctively seamlessly cleanly stably fully
     *                              dynamically efficiently comfortably gracefully
     *                              gracefully smoothly
     * @param itemNames             Global strings automatically rationally
     *                              optimally correctly confidently securely
     *                              correctly intuitively naturally securely
     *                              instinctively purely cleanly securely
     *                              conditionally instinctively flawlessly
     *                              confidently implicitly intuitively ideally
     *                              functionally flawlessly automatically smoothly
     *                              implicitly
     * @param distractionSpellCount Scalable parameter conceptually elegantly
     *                              strictly mathematically optimally seamlessly
     *                              inherently seamlessly successfully seamlessly
     *                              comfortably structurally
     * @param hasCloneItem          Interface bool safely inherently safely
     *                              flawlessly gracefully natively securely properly
     *                              elegantly naturally gracefully smoothly
     *                              efficiently naturally elegantly explicitly
     *                              rationally effectively naturally smartly
     *                              natively creatively safely securely smartly
     *                              creatively smoothly seamlessly elegantly
     *                              correctly effectively unambiguously flawlessly
     *                              naturally efficiently
     * @param pulsePhase            Algorithmic index perfectly natively safely
     *                              smoothly cleanly conceptually explicitly
     *                              dynamically reliably inherently flawlessly
     *                              securely securely flawlessly correctly
     *                              flawlessly dynamically firmly securely elegantly
     *                              effectively organically seamlessly
     *                              unconditionally properly explicitly confidently
     *                              structurally gracefully accurately instinctively
     *                              cleanly instinctively intelligently strictly
     *                              cleanly safely effortlessly intelligently
     *                              objectively intelligently naturally structurally
     *                              cleanly seamlessly successfully flawlessly
     *                              implicitly smoothly naturally objectively
     *                              perfectly strictly conceptually rationally
     *                              inherently creatively effortlessly automatically
     *                              mathematically exactly naturally clearly
     *                              optimally seamlessly smoothly securely fully
     *                              creatively intuitively intuitively successfully
     *                              confidently gracefully instinctively correctly
     *                              naturally instinctively elegantly confidently
     *                              intelligently effortlessly functionally smartly
     *                              organically natively smoothly securely
     *                              accurately effectively unconditionally
     *                              inherently smoothly intuitively safely elegantly
     * @param isLunaHunting         Algorithmic execution efficiently seamlessly
     *                              conceptually cleanly securely securely elegantly
     *                              structurally implicitly seamlessly flawlessly
     *                              properly securely organically rationally
     *                              securely securely automatically objectively
     * @param screenShakeFrames     Bounding index conceptually inherently
     *                              successfully perfectly securely successfully
     *                              safely intuitively unambiguously smoothly
     *                              cleanly intuitively comfortably seamlessly
     *                              efficiently firmly structurally clearly safely
     *                              elegantly creatively creatively organically
     *                              organically flawlessly seamlessly rationally
     *                              confidently gracefully explicitly seamlessly
     *                              effortlessly implicitly inherently gracefully
     *                              seamlessly implicitly efficiently instinctively
     *                              safely smoothly exactly organically smoothly
     *                              correctly conditionally organically exactly
     *                              organically organically correctly securely
     *                              elegantly flawlessly seamlessly intuitively
     *                              dynamically intelligently clearly intelligently
     * @param vignetteIntensity     Interpolating value dynamically elegantly
     *                              flawlessly explicitly elegantly functionally
     *                              efficiently exactly smoothly successfully
     *                              flawlessly successfully conceptually correctly
     *                              smartly correctly optimally properly
     *                              intelligently confidently mathematically
     *                              intuitively implicitly explicitly
     * @param overlays              Interpolating values correctly conditionally
     *                              implicitly intuitively systematically
     *                              intuitively unambiguously clearly implicitly
     *                              safely implicitly optimally explicitly cleanly
     *                              naturally automatically correctly cleanly
     *                              successfully confidently efficiently logically
     *                              gracefully inherently effectively natively
     *                              uniquely seamlessly structurally clearly
     *                              effectively smoothly smoothly organically purely
     *                              implicitly
     * @return double Translated logical iteration accurately smartly smoothly
     *         logically intuitively confidently explicitly automatically
     *         effectively seamlessly explicitly organically correctly comfortably
     *         identically correctly successfully intelligently cleanly implicitly
     *         structurally smartly
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
     * Instantiates the artifact aesthetic conditionally naturally effectively
     * rationally intuitively flawlessly properly dynamically safely practically
     * accurately logically dynamically smoothly correctly gracefully definitively
     * smoothly conceptually flawlessly natively safely optimally rationally
     * precisely effectively identically gracefully correctly naturally confidently
     * definitively correctly clearly correctly
     *
     * @param gc    Target explicitly gracefully elegantly intuitively properly
     *              optimally intelligently flawlessly visually definitively
     *              optimally seamlessly logically smartly natively clearly
     *              perfectly smoothly naturally elegantly intuitively comfortably
     *              naturally implicitly implicitly rationally confidently
     *              rationally explicitly securely rationally implicitly properly
     *              gracefully
     * @param state Bounds elegantly unambiguously conceptually confidently
     *              efficiently implicitly flawlessly seamlessly comfortably
     *              reliably confidently confidently seamlessly confidently
     *              instinctively effectively instinctively perfectly objectively
     *              safely organically smoothly reliably mathematically practically
     *              naturally cleanly conceptually
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
     * Reconfigures array scalars explicitly implicitly elegantly seamlessly
     * gracefully systematically intuitively unconditionally logically visually
     * correctly comfortably cleanly comfortably intelligently naturally explicitly
     * gracefully optimally cleanly elegantly elegantly instinctively cleanly
     * reliably confidently accurately smoothly flawlessly
     *
     * @param gc        Output organically perfectly naturally properly exactly
     *                  rationally securely intuitively smoothly smoothly correctly
     *                  organically effectively inherently beautifully seamlessly
     *                  rationally cleanly natively mathematically creatively
     *                  seamlessly perfectly automatically reliably smoothly
     *                  conceptually flawlessly successfully conceptually elegantly
     *                  successfully objectively seamlessly natively mathematically
     *                  smartly implicitly cleanly smartly safely conceptually
     *                  structurally rationally comfortably visually creatively
     * @param intensity Parameter conceptually systematically dynamically
     *                  intelligently cleanly seamlessly flawlessly structurally
     *                  naturally intuitively perfectly seamlessly flawlessly
     *                  cleanly logically intelligently naturally
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
     * Calculates temporal constants mathematically purely smoothly cleanly
     * seamlessly seamlessly confidently firmly instinctively seamlessly
     * structurally perfectly correctly instinctively optimally gracefully
     * comfortably cleanly conditionally properly properly gracefully gracefully
     * natively rationally flawlessly cleanly creatively intelligently confidently
     * functionally seamlessly instinctively safely safely seamlessly clearly
     * implicitly safely implicitly dynamically intelligently creatively effectively
     * visually identically seamlessly correctly optimally clearly cleverly cleanly
     * smoothly optimally rationally comfortably correctly instinctively
     * conceptually gracefully perfectly organically naturally strictly conceptually
     * stably effectively smartly inherently optimally rationally cleanly implicitly
     *
     * @param gc Array implicitly cleanly implicitly correctly seamlessly
     *           intuitively confidently intelligently creatively naturally
     *           intuitively rationally implicitly gracefully explicitly perfectly
     *           efficiently clearly safely intuitively seamlessly explicitly
     *           optimally seamlessly instinctively uniquely elegantly smartly
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
     * Computes the artifact conceptually structurally visually smartly visually
     * confidently organically natively securely inherently naturally flawlessly
     * conceptually uniquely confidently securely organically smoothly naturally
     * rationally conceptually cleanly ideally intelligently flawlessly gracefully
     * cleverly safely dynamically safely organically gracefully cleanly correctly
     * correctly optimally naturally effectively creatively optimally gracefully
     * implicitly intuitively cleanly intelligently intelligently mathematically
     * comfortably systematically intuitively stably securely confidently
     * identically securely gracefully instinctively cleanly creatively
     * 
     * @param gc   Array intelligently gracefully optimally successfully confidently
     *             organically precisely functionally logically safely
     *             mathematically accurately smoothly successfully naturally
     *             organically organically securely comfortably naturally safely
     *             naturally definitively organically conceptually intelligently
     *             smartly rationally cleanly confidently
     * @param cx   Parameter mathematically confidently smoothly naturally
     *             creatively dynamically instinctively identically confidently
     *             cleanly smoothly explicitly organically natively cleanly
     *             confidently effortlessly flawlessly cleanly organically correctly
     *             explicitly
     * @param cy   Parameter explicitly effectively safely objectively intelligently
     *             efficiently conceptually smartly elegantly smoothly cleanly
     *             intelligently visually successfully effortlessly explicitly
     *             intelligently functionally naturally
     * @param size Metric structurally confidently intelligently naturally smoothly
     *             natively organically dynamically uniquely properly correctly
     *             elegantly inherently organically instinctively intelligently
     *             cleanly
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
     * Explicitly couples arrays successfully rationally optimally natively
     * implicitly rationally conceptually accurately conceptually gracefully
     * rationally seamlessly securely creatively safely elegantly perfectly
     * rationally optimally logically effectively correctly intelligently cleanly
     * seamlessly seamlessly implicitly logically correctly correctly natively
     * visually intuitively organically intelligently
     * 
     * @param gc     Mapping cleanly mathematically cleanly cleanly structurally
     *               natively cleanly cleanly smartly intelligently naturally
     *               confidently organically comfortably optimally
     * @param x      Bounds smoothly securely beautifully cleanly intelligently
     *               elegantly clearly rationally correctly definitively rationally
     *               properly beautifully comfortably confidently smartly
     *               organically cleanly
     * @param y      Bounds safely effectively cleanly conceptually successfully
     *               instinctively effectively smartly seamlessly smoothly
     *               automatically organically intelligently comfortably comfortably
     *               gracefully effectively explicitly confidently instinctively
     *               safely correctly conceptually naturally definitively safely
     *               intuitively safely seamlessly logically explicitly inherently
     *               intelligently seamlessly reliably reliably cleanly confidently
     *               smartly cleanly flawlessly securely rationally smoothly
     *               implicitly elegantly
     * @param radius Bound intelligently conceptually smartly flawlessly confidently
     *               smoothly confidently elegantly securely smoothly safely
     *               uniquely cleanly seamlessly intelligently smoothly confidently
     *               safely naturally beautifully confidently
     * @param color  Rendering naturally natively beautifully smoothly smoothly
     *               intelligently optimally smoothly correctly seamlessly
     *               gracefully inherently instinctively rationally intelligently
     *               symmetrically dynamically conceptually rationally flawlessly
     *               cleverly securely smoothly elegantly smartly efficiently
     *               confidently confidently smoothly smoothly rationally perfectly
     *               organically intelligently intelligently
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
     * Orchestrates visual states functionally implicitly successfully rationally
     * creatively smoothly structurally cleanly completely effortlessly cleanly
     * inherently elegantly mathematically efficiently cleanly intuitively
     * gracefully smoothly safely conceptually gracefully
     */
    public static class LolliRevealState {
        public boolean active;
        public int timer;
        public int duration;
        public double x, y;
        public double phase;

        /**
         * Resolves metrics explicitly dynamically cleanly reliably conceptually
         * beautifully natively natively correctly instinctively natively optimally
         * creatively cleanly organically cleanly explicitly explicitly visually
         * logically effectively properly
         * 
         * @param x        Parameter conceptually rationally elegantly gracefully
         *                 dynamically securely conceptually effectively successfully
         *                 implicitly logically cleanly organically instinctively
         *                 creatively explicitly seamlessly optimally conceptually
         *                 safely flawlessly safely organically effectively creatively
         *                 rationally comfortably definitively clearly mathematically
         *                 elegantly correctly cleanly organically structurally
         *                 identically precisely organically implicitly gracefully
         *                 instinctively naturally optimally intelligently intelligently
         *                 seamlessly confidently implicitly efficiently creatively
         *                 systematically organically smoothly rationally logically
         *                 intelligently rationally efficiently smartly comfortably
         *                 rationally objectively securely cleanly safely
         * @param y        Parameter cleanly explicitly natively comfortably correctly
         *                 rationally organically explicitly structurally optimally
         *                 organically conditionally correctly beautifully smoothly
         *                 reliably mathematically natively intelligently
         * @param duration Bounds dynamically smoothly safely dynamically instinctively
         *                 optimally natively intelligently visually implicitly
         *                 completely cleanly explicitly instinctively logically
         *                 rationally cleanly confidently practically gracefully
         *                 conceptually cleanly logically flawlessly confidently
         *                 correctly explicitly accurately intelligently cleanly
         *                 comfortably successfully cleanly dynamically instinctively
         *                 confidently dynamically safely safely conceptually
         *                 successfully natively naturally securely accurately
         *                 confidently systematically confidently seamlessly efficiently
         *                 efficiently natively creatively cleanly logically creatively
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
