package com.nsu.cse215l.redlolli.redlolli.ui;

import com.nsu.cse215l.redlolli.redlolli.entities.Entity;
import com.nsu.cse215l.redlolli.redlolli.entities.Item;
import com.nsu.cse215l.redlolli.redlolli.entities.Monster;
import com.nsu.cse215l.redlolli.redlolli.entities.Player;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.shape.ArcType;

import java.io.InputStream;
import java.util.List;

/**
 * Handles all in-game rendering operations separate from game logic.
 */
public class GameRenderer {

    private static final double SCREEN_WIDTH = 880;
    private static final double SCREEN_HEIGHT = 730;

    // ================= IMAGE ASSETS =================

    private static Image lunaFlashImg;
    private static boolean imagesInitialized = false;

    private static Image loadSprite(String filename, int width, int height) {
        try {
            InputStream is = GameRenderer.class.getResourceAsStream("/assets/images/sprites/" + filename);
            if (is != null) {
                return new Image(is, width, height, true, false);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    /** No-op placeholder for centralized preloading. This class uses primitives for reveal animation. */
    public static void initImages() {
        if (imagesInitialized) return;
        lunaFlashImg = loadSprite("luna_flash.png", 235, 244);
        imagesInitialized = true;
    }

    // ================= OVERLAY SYSTEM =================

    /** Represents a manual image overlay that can be placed anywhere on screen. */
    public static class Overlay {
        public Image image;
        public double x, y;          // Screen position (or use worldX/worldY for maze-relative)
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
            Monster paleLuna, Player player, int warningFlashTimer,
            LolliRevealState revealState, int level,
            List<Item> chests,
            String[] itemNames,
            int fruitCount,
            int eggCount,
            boolean hasCloneItem,
            double pulsePhase,
            boolean isLunaHunting,
            int screenShakeFrames,
            double vignetteIntensity,
            List<Overlay> overlays) {

        gc.setFill(Color.BLACK);
        
        // Screen shake effect
        double shakeX = 0, shakeY = 0;
        if (screenShakeFrames > 0) {
            shakeX = (Math.random() - 0.5) * screenShakeFrames * 0.5;
            shakeY = (Math.random() - 0.5) * screenShakeFrames * 0.5;
            gc.translate(shakeX, shakeY);
        }
        
        gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        maze.renderMaze(gc);
        maze.renderOverlays(gc);

        for (Entity e : entities) {
            e.render(gc);
        }

        // Draw manual overlays (screen-relative or world-relative)
        if (overlays != null) {
            for (Overlay o : overlays) {
                if (o.image == null) continue;
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

        if (warningFlashTimer > 0) {
            gc.setFill(Color.rgb(255, 0, 0, 0.15));
            gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        }

        if (revealState != null && revealState.active) {
            renderLolliReveal(gc, revealState);
        }

        pulsePhase = HUDRenderer.drawHUD(gc, level, chests, itemNames, paleLuna, player,
                fruitCount, eggCount, hasCloneItem, pulsePhase);

        if (player.isInEscapeRoom()) {
            gc.setFill(Color.rgb(0, 80, 0, 0.25));
            gc.fillRect(0, 50, SCREEN_WIDTH, 4);
        }

        // Vignette overlay (darkens screen edges, intensifies with low sanity)
        if (vignetteIntensity > 0) {
            drawVignetteOverlay(gc, vignetteIntensity);
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
        if (screenShakeFrames > 0) {
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
            double drawX = (SCREEN_WIDTH - 235) / 2;
            double drawY = (SCREEN_HEIGHT - 244) / 2;
            gc.setGlobalAlpha(0.65);
            gc.drawImage(lunaFlashImg, drawX, drawY, 235, 244);
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

    private static void drawRedLolli(GraphicsContext gc, double cx, double cy, double size) {
        gc.setFill(Color.rgb(220, 20, 20));
        gc.fillOval(cx - size / 2, cy - size / 2 - 2, size, size);
        gc.setFill(Color.rgb(255, 100, 100, 0.6));
        gc.fillOval(cx - size / 4, cy - size / 3 - 2, size / 3, size / 3);
        gc.setStroke(Color.rgb(200, 170, 120));
        gc.setLineWidth(2);
        gc.strokeLine(cx, cy + size / 2 - 2, cx, cy + size / 2 + 8);
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
