package com.nsu.cse215l.redlolli.redlolli.ui;

import com.nsu.cse215l.redlolli.redlolli.entities.Entity;
import com.nsu.cse215l.redlolli.redlolli.entities.Item;
import com.nsu.cse215l.redlolli.redlolli.entities.Monster;
import com.nsu.cse215l.redlolli.redlolli.entities.Player;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import com.nsu.cse215l.redlolli.redlolli.systems.FlashlightSystem;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.BlendMode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

/**
 * Handles all in-game rendering operations separate from game logic.
 */
public class GameRenderer {

    private static final double SCREEN_WIDTH = 880;
    private static final double SCREEN_HEIGHT = 730;
    private static final double HUD_BOTTOM_Y = 50;

    /**
     * The primary entry point for drawing a complete game frame.
     */
    public static double render(GraphicsContext gc, Maze maze, List<Entity> entities,
            Monster paleLuna, Player player, int warningFlashTimer,
            LolliRevealState revealState, int level,
            List<Item> chests,
            String[] itemNames,
            FlashlightSystem flashlight,
            boolean blackoutActive,
            int fruitCount,
            int eggCount,
            boolean hasCloneItem,
            double pulsePhase) {

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);

        maze.renderMaze(gc);

        for (Entity e : entities) {
            e.render(gc);
        }

        if (paleLuna != null) {
            paleLuna.renderEyes(gc);
        }

        if (warningFlashTimer > 0) {
            gc.setFill(Color.rgb(255, 0, 0, 0.15));
            gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        }

        renderDarknessMask(gc, player, flashlight, blackoutActive);

        if (revealState != null && revealState.active) {
            renderLolliReveal(gc, revealState);
        }

        pulsePhase = HUDRenderer.drawHUD(gc, level, chests, itemNames, paleLuna, player, flashlight,
                fruitCount, eggCount, hasCloneItem, 0, pulsePhase);

        if (player.isInEscapeRoom()) {
            gc.setFill(Color.rgb(0, 80, 0, 0.25));
            gc.fillRect(0, 50, SCREEN_WIDTH, 4);
        }

        return pulsePhase;
    }

    /** Renders the darkness mask overlay. */
    private static void renderDarknessMask(GraphicsContext gc, Player player, FlashlightSystem flashlight,
            boolean blackoutActive) {
        if (blackoutActive) {
            gc.setFill(Color.rgb(0, 0, 0, 0.82));
            gc.fillRect(0, 50, SCREEN_WIDTH, SCREEN_HEIGHT - 50);
            return;
        }

        final double px = player.getX() + 10;
        final double py = player.getY() + 10;

        gc.setFill(Color.rgb(0, 0, 0, flashlight.isEffectivelyOn() ? 0.62 : 0.8));
        gc.fillRect(0, HUD_BOTTOM_Y, SCREEN_WIDTH, SCREEN_HEIGHT - HUD_BOTTOM_Y);

        gc.save();
        gc.setGlobalBlendMode(BlendMode.SCREEN);

        if (flashlight.isEffectivelyOn()) {
            double fx = player.getFacingX();
            double fy = player.getFacingY();

            double len = Math.sqrt(fx * fx + fy * fy);
            if (len < 0.0001) {
                fx = 0;
                fy = -1;
                len = 1;
            }
            fx /= len;
            fy /= len;

            double tipX = px + fx * 235;
            double tipY = py + fy * 235;
            double sideX = -fy;
            double sideY = fx;
            double spread = 90;

            gc.setFill(Color.rgb(220, 220, 220, 0.24));
            gc.fillPolygon(
                    new double[] { px, tipX + sideX * spread, tipX - sideX * spread },
                    new double[] { py, tipY + sideY * spread, tipY - sideY * spread },
                    3);
        }

        gc.restore();
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

        gc.setFill(Color.rgb(255, 215, 0, 0.12 * pulse));
        gc.fillOval(cx - glowRadius * 0.7, cy - glowRadius * 0.7, glowRadius * 1.4, glowRadius * 1.4);
        drawRedLolli(gc, cx, cy, lolliSize);
        gc.setFill(Color.rgb(255, 50, 50, textAlpha));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        gc.fillText("RED LOLLI FOUND!", cx - 70, cy - glowRadius - 10);
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
