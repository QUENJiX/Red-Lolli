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
 * Handles all in-game rendering operations separate from game logic.
 */
public class GameRenderer {

    private static final double SCREEN_WIDTH = 880;
    private static final double SCREEN_HEIGHT = 730;

    private static Canvas lightBuffer = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
    private static GraphicsContext lightGC = lightBuffer.getGraphicsContext2D();

    // ================= IMAGE ASSETS =================

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

    private static boolean imagesInitialized = false;

    private static Image loadSprite(String filename, int width, int height) {
        return com.nsu.cse215l.redlolli.redlolli.systems.AssetManager.getInstance().getSprite("/assets/images/" + filename, width, height);
    }

    /**
     * No-op placeholder for centralized preloading. This class uses primitives for
     * reveal animation.
     */
    public static void initImages() {
        if (imagesInitialized)
            return;
        lunaFlashImg = loadSprite("sprites/luna_flash.png", 500, 500); // Scaled larger
        
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

        killerIdleImg = loadSprite("sprites/killer_idle_right.png", 40, 70); // Using correct source dims
        killerIdleLeftImg = loadSprite("sprites/killer_idle_left.png", 40, 70);
        killerChaseImg = loadSprite("sprites/killer_chase_right.png", 640, 70);
        killerChaseLeftImg = loadSprite("sprites/killer_chase_left.png", 640, 70);
        killerAttackImg = loadSprite("sprites/killer_attack_right.png", 640, 70);
        killerAttackLeftImg = loadSprite("sprites/killer_attack_left.png", 640, 70);

        imagesInitialized = true;
    }

    // ================= OVERLAY SYSTEM =================

    private static void drawItemImg(GraphicsContext gc, boolean isCollected, Image img, double x, double y) {
        if (img != null) {
            gc.drawImage(img, x, y, 32.0, 32.0); // RENDER_SIZE from old Item.java
        } else {
            gc.setFill(isCollected ? Color.rgb(160, 82, 45) : Color.rgb(139, 69, 19));
            gc.fillRect(x, y, 32.0, 32.0);
        }
    }

    private static void renderMonster(GraphicsContext gc, Monster m) {
        double RENDER_SIZE = 50.0;
        double AURA_SIZE = 56.0;
        double offset = (RENDER_SIZE - m.getSize()) / 2;
        double cx = m.getX() + m.getSize() / 2;
        double cy = m.getY() + m.getSize() / 2;

        // Aura (only when not dormant)
        if (m.getState() != Monster.State.DORMANT) {
            double pulse = Math.sin(m.getPulsePhase()) * 5;
            double baseRadius = AURA_SIZE / 2 + pulse;

            // Base jagged shape mimicking an organic, flickering, torch-like randomized flame
            int numPoints = 16;
            double[] xPoints = new double[numPoints];
            double[] yPoints = new double[numPoints];

            for (int layer = 0; layer < 3; layer++) {
                for (int i = 0; i < numPoints; i++) {
                    double angle = Math.PI * 2 * ((double) i / numPoints);
                    // Apply random jitter to radius for a spiky torch effect
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

            // Erratic shuddering static rings for the terrifying aura glitch aesthetic
            gc.setGlobalAlpha(0.6);
            gc.setStroke(Color.rgb(40, 0, 0));
            gc.setLineWidth(1.5);
            double ringShift = (Math.random() - 0.5) * 8;
            gc.strokeOval(cx - baseRadius + ringShift, cy - baseRadius - ringShift, baseRadius * 2, baseRadius * 2);

            gc.setGlobalAlpha(1.0);
        }

        // Body composite
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

    private static void renderSerialKiller(GraphicsContext gc, SerialKillerEntity sk) {
        Image imgToDraw;
        int frameWidth = 128;
        int maxFrames = 5;

        if (!sk.isActive()) {
            imgToDraw = sk.isFacingLeft() ? killerIdleLeftImg : killerIdleImg;
            frameWidth = 40; // Idle is now just a single 40 width image
            maxFrames = 1;
        } else if (sk.isAttackingDecoy()) {
            imgToDraw = sk.isFacingLeft() ? killerAttackLeftImg : killerAttackImg;
            frameWidth = 128; // 640 width / 5 frames
            maxFrames = 5;
        } else {
            imgToDraw = sk.isFacingLeft() ? killerChaseLeftImg : killerChaseImg;
            frameWidth = 128; // 640 width / 5 frames
            maxFrames = 5;
        }

        int currentFrame = sk.getCurrentFrame();
        if (currentFrame >= maxFrames) {
            currentFrame = 0;
        }

        // Calculate aspect-correct width based on the active animation frame
        double RENDER_HEIGHT = 48.0;
        double scale = RENDER_HEIGHT / 70.0;
        double drawWidth = frameWidth * scale;

        // Draw centered (hitbox 24x24)
        double offsetX = (drawWidth - sk.getSize()) / 2;
        double offsetY = (RENDER_HEIGHT - sk.getSize()) / 2;

        if (imgToDraw != null) {
            int sourceX = currentFrame * frameWidth;
            gc.drawImage(imgToDraw,
                    sourceX, 0, frameWidth, 70, // Source slice dimensions updated to 70 height
                    sk.getX() - offsetX, sk.getY() - offsetY, drawWidth, RENDER_HEIGHT); // Destination bounding box
        } else {
            gc.setFill(sk.isActive() ? Color.rgb(180, 20, 20) : Color.rgb(80, 40, 40));
            gc.fillOval(sk.getX() - offsetX, sk.getY() - offsetY, RENDER_HEIGHT, RENDER_HEIGHT);
        }
    }

    /** Represents a manual image overlay that can be placed anywhere on screen. */
    public static class Overlay {
        public Image image;
        public double x, y; // Screen position (or use worldX/worldY for maze-relative)
        public double width, height;
        public double opacity = 1.0;
        public boolean useWorldCoords = false; // If true, x/y are maze world coords, converted to screen

        public Overlay(Image image, double x, double y, double width, double height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public Overlay(Image image, double worldX, double worldY, double width, double height, boolean worldCoords) {
            this.image = image;
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
     * The primary entry point for drawing a complete game frame.
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
            double intensity = screenShakeFrames > 0 ? screenShakeFrames * 0.5 : 2.5; // Constant jitter when hunted
            shakeX = (Math.random() - 0.5) * intensity;
            shakeY = (Math.random() - 0.5) * intensity;
            gc.translate(shakeX, shakeY);
        }

        gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        maze.renderMaze(gc);
        maze.renderOverlays(gc);

        for (Entity e : entities) {
            if (e instanceof TorchEntity) {
                TorchEntity t = (TorchEntity) e;
                Image img = t.isLit() ? torchFrames[t.getCurrentFrame()] : torchFrames[0];
                if (img != null) {
                    gc.drawImage(img, t.getX(), t.getY(), t.getSize(), t.getSize());
                }
            } else if (e instanceof Item) {
                Item item = (Item) e;
                double offset = (32.0 - item.getSize()) / 2; // RENDER_SIZE is 32.0
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
                double offset = (50.0 - clone.getSize()) / 2; // RENDER_SIZE is 50.0
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
            } else {
                e.render(gc);
            }
        }

        // Draw manual overlays (screen-relative or world-relative)
        if (overlays != null) {
            for (Overlay o : overlays) {
                if (o.image == null)
                    continue;
                double drawX = o.x;
                double drawY = o.y;
                if (o.useWorldCoords && maze != null) {
                    // Convert world coords to screen coords
                    drawX = o.x; // already in world space
                    drawY = o.y + Maze.Y_OFFSET;
                }
                gc.setGlobalAlpha(o.opacity);
                gc.drawImage(o.image, drawX, drawY, o.width, o.height);
                gc.setGlobalAlpha(1.0);
            }
        }

        // --- MULTIPLICATIVE LIGHTING LAYER ---
        // 1. Fill light buffer with ambient darkness
        lightGC.setGlobalBlendMode(BlendMode.SRC_OVER);
        lightGC.setFill(Color.rgb(12, 12, 15, 0.96)); // 96% darkness
        lightGC.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        // 2. Additive blending for lights
        lightGC.setGlobalBlendMode(BlendMode.ADD);

        // Player's personal light (dimmer depending on sanity)
        double playerLightRadius = 90 + (player.getSanityPercent() * 50.0);
        drawRadialLight(lightGC, player.getX() + 10, player.getY() + 10, playerLightRadius,
                Color.rgb(220, 230, 255, 1.0));

        // Pale Luna's personal light (smaller and reddish)
        if (paleLuna != null && !paleLuna.getState().name().equals("DORMANT")) {
            double lunaLightRadius = isLunaHunting ? 140 : 90;
            Color lightColor = isLunaHunting ? Color.rgb(255, 0, 0, 1.0) : Color.rgb(255, 60, 60, 0.85);
            drawRadialLight(lightGC, paleLuna.getX() + 10, paleLuna.getY() + 10, lunaLightRadius, lightColor);
        }

        // Torch lights
        for (Entity e : entities) {
            if (e instanceof TorchEntity) {
                TorchEntity t = (TorchEntity) e;
                if (t.isLit()) {
                    // Small flicker in radius
                    double flicker = Math.random() * 8.0 - 4.0;
                    drawRadialLight(lightGC, t.getX() + 20, t.getY() + 20, 220 + flicker,
                            Color.rgb(255, 170, 50, 0.85));
                }
            }
        }

        // 3. Composite light map to main screen
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

        // Vignette overlay (darkens screen edges, intensifies with low sanity)
        if (vignetteIntensity > 0 || isLunaHunting) {
            double effVignette = Math.max(vignetteIntensity,
                    isLunaHunting ? 0.3 + (Math.sin(System.currentTimeMillis() / 150.0) * 0.15) : 0);
            drawVignetteOverlay(gc, effVignette);
        }

        // Add hunting panic overrides
        if (isLunaHunting) {
            // Heartbeat red pulsing
            double pulse = Math.abs(Math.sin(System.currentTimeMillis() / 200.0));
            gc.setFill(Color.rgb(180, 0, 0, 0.05 + (0.05 * pulse)));
            gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

            // Random horizontal glitch slices
            if (Math.random() < 0.25) {
                gc.setFill(Color.rgb(0, 0, 0, 0.4));
                double glitchY = Math.random() * SCREEN_HEIGHT;
                double glitchHeight = Math.random() * 8 + 2;
                gc.fillRect(0, glitchY, SCREEN_WIDTH, glitchHeight);
            }

            // Chromatic aberration simulation
            if (Math.random() < 0.05) {
                gc.setGlobalBlendMode(BlendMode.DIFFERENCE);
                gc.setFill(Color.rgb(255, 0, 0, 0.2));
                gc.fillRect(-4, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
                gc.setGlobalBlendMode(BlendMode.SRC_OVER);
            }
        }

        // Subliminal flash - 1-frame Luna face during low sanity or chase
        if (isLunaHunting || player.getSanity() < 25) {
            if (Math.random() < 0.02) { // 2% chance per frame
                drawSubliminalFlash(gc);
            }
        }

        // Player death animation - screen fills red from edges
        // This is handled by HelloApplication after death is detected

        // Reset transform if shaken
        if (screenShakeFrames > 0 || isLunaHunting) {
            gc.translate(-shakeX, -shakeY);
        }

        return pulsePhase;
    }

    /**
     * Renders the golden glow reveal animation when the player finds the Red Lolli.
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

    /** Draws a dark radial gradient overlay at screen edges (vignette effect). */
    private static void drawVignetteOverlay(GraphicsContext gc, double intensity) {
        // intensity: 0.0 = none, 1.0 = maximum darkness at edges

        // Draw vignette as a series of concentric dark rectangles with increasing alpha
        int layers = 20;
        for (int i = 0; i < layers; i++) {
            double progress = (double) i / layers;
            double alpha = progress * intensity * 0.4; // Max 40% opacity at edges
            double inset = progress * 100; // 100px inset from edges

            gc.setFill(Color.rgb(0, 0, 0, alpha));
            gc.fillRect(inset, inset, SCREEN_WIDTH - inset * 2, SCREEN_HEIGHT - inset * 2);
        }
    }

    /** Flashes a brief Luna face for subliminal horror effect. */
    private static void drawSubliminalFlash(GraphicsContext gc) {
        if (lunaFlashImg != null) {
            // Draw centered, semi-transparent
            double drawX = (SCREEN_WIDTH - 500) / 2;
            double drawY = (SCREEN_HEIGHT - 500) / 2;
            gc.setGlobalAlpha(0.65);
            gc.drawImage(lunaFlashImg, drawX, drawY, 500, 500);
            gc.setGlobalAlpha(1.0);
        } else {
            // Fallback: draw primitive pale face
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

    public static void drawRedLolli(GraphicsContext gc, double cx, double cy, double size) {
        gc.setFill(Color.rgb(220, 20, 20));
        gc.fillOval(cx - size / 2, cy - size / 2 - 2, size, size);
        gc.setFill(Color.rgb(255, 100, 100, 0.6));
        gc.fillOval(cx - size / 4, cy - size / 3 - 2, size / 3, size / 3);
        gc.setStroke(Color.rgb(200, 170, 120));
        gc.setLineWidth(2);
        gc.strokeLine(cx, cy + size / 2 - 2, cx, cy + size / 2 + 8);
    }

    private static void drawRadialLight(GraphicsContext gc, double x, double y, double radius, Color color) {
        RadialGradient lightPulse = new RadialGradient(0, 0, x, y, radius, false, CycleMethod.NO_CYCLE,
                new Stop(0.0, color), // Bright core center
                new Stop(0.4, color.deriveColor(0, 1, 1, 0.6)), // Mid drop off
                new Stop(1.0, Color.TRANSPARENT) // Edges fade entirely
        );
        gc.setFill(lightPulse);
        gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
    }

    /** Animation state for the Red Lolli reveal effect. */
    public static class LolliRevealState {
        public boolean active;
        public int timer;
        public int duration;
        public double x, y;
        public double phase;

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
