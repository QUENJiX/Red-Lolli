package com.nsu.cse215l.redlolli.redlolli.ui;

import com.nsu.cse215l.redlolli.redlolli.entities.Item;
import com.nsu.cse215l.redlolli.redlolli.entities.Monster;
import com.nsu.cse215l.redlolli.redlolli.entities.Player;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.InputStream;
import java.util.List;

/**
 * Manages the in-game HUD visual layer at the top of the screen.
 * Layout (880 x 50 px):
 *
 * [LEVEL 1/3] | [🍬 0/1] | [FIND: Mud / Fruit:2 (E)] | [Luna bar + label] |
 * [MIND bar] | [SAFE]
 * x=0..89 x=90..149 x=150..334 x=335..609 x=610..809 x=810..879
 *
 * All text baseline at ROW1_Y (top row) or ROW2_Y (sub-label row).
 * All bars are at BAR_Y with height BAR_H.
 */
public class HUDRenderer {

    // ── Layout constants ─────────────────────────────────────────────────────
    private static final double HUD_W = 880;
    private static final double HUD_H = 50;
    private static final double ROW1_Y = 20; // primary text baseline
    private static final double ROW2_Y = 38; // secondary / sub-label baseline
    private static final double BAR_Y = 24; // top of progress bars
    private static final double BAR_H = 14; // height of progress bars

    // ── Section x-boundaries ─────────────────────────────────────────────────
    private static final double DIV_LEVEL = 90;
    private static final double DIV_LOLLI = 150;
    private static final double DIV_FIND = 335;
    private static final double DIV_LUNA = 610;
    private static final double DIV_SANITY = 810;

    // ── Image assets ─────────────────────────────────────────────────────────
    private static Image[] paleLunaIconImg = new Image[4];
    private static Image sanitySkullImg;
    private static Image sanitySkullLowImg;
    private static boolean imagesInitialized = false;

    private static Image loadSprite(String filename, int width, int height) {
        try {
            InputStream is = HUDRenderer.class.getResourceAsStream("/assets/images/sprites/" + filename);
            if (is != null)
                return new Image(is, width, height, true, false);
        } catch (Exception e) {
            System.err.println("Error initializing HUDRenderer assets: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void initImages() {
        if (imagesInitialized)
            return;
        paleLunaIconImg[0] = loadSprite("monster_dormant.png", 18, 18);
        paleLunaIconImg[1] = loadSprite("monster_stalking.png", 18, 18);
        paleLunaIconImg[2] = loadSprite("monster_hunting.png", 18, 18);
        paleLunaIconImg[3] = loadSprite("monster_waiting.png", 18, 18);
        sanitySkullImg = loadSprite("sanity_skull.png", 20, 20);
        sanitySkullLowImg = loadSprite("sanity_skull_2.png", 20, 20);
        imagesInitialized = true;
    }

    /** Force images to reload. */
    public static void resetImages() {
        imagesInitialized = false;
    }

    // ── Entry point ───────────────────────────────────────────────────────────

    public static double drawHUD(GraphicsContext gc, int level, List<Item> chests,
            String[] itemNames, Monster paleLuna, Player player,
            int distractionSpellCount, boolean hasCloneItem, double pulsePhase) {

        drawBackground(gc);

        // Section 1 — Level indicator
        drawLevelSection(gc, level);
        drawDivider(gc, DIV_LEVEL);

        // Section 2 — Lolli counter
        drawLolliSection(gc, chests);
        drawDivider(gc, DIV_LOLLI);

        // Section 3 — Find target + utility sub-label
        drawFindSection(gc, level, itemNames, distractionSpellCount, hasCloneItem);
        drawDivider(gc, DIV_FIND);

        // Section 4 — Pale Luna phase tracker bar
        pulsePhase = drawLunaSection(gc, paleLuna, pulsePhase);
        drawDivider(gc, DIV_LUNA);

        // Section 5 — Sanity bar
        drawSanitySection(gc, player);
        drawDivider(gc, DIV_SANITY);

        // Section 6 — SAFE indicator (rightmost)
        drawSafeSection(gc, player);

        return pulsePhase;
    }

    // ── Sections ─────────────────────────────────────────────────────────────

    private static void drawBackground(GraphicsContext gc) {
        // Dark background
        gc.setFill(Color.rgb(10, 10, 14));
        gc.fillRect(0, 0, HUD_W, HUD_H);
        // Subtle gradient-strip at bottom
        gc.setFill(Color.rgb(30, 5, 5));
        gc.fillRect(0, HUD_H - 3, HUD_W, 3);
        // Bottom border line
        gc.setStroke(Color.rgb(140, 20, 20, 0.8));
        gc.setLineWidth(1.5);
        gc.strokeLine(0, HUD_H, HUD_W, HUD_H);
    }

    private static void drawDivider(GraphicsContext gc, double x) {
        gc.setStroke(Color.rgb(60, 25, 25, 0.7));
        gc.setLineWidth(1);
        gc.strokeLine(x, 6, x, HUD_H - 6);
    }

    /** Section 1: "LEVEL 1/3" on two lines */
    private static void drawLevelSection(GraphicsContext gc, int level) {
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        gc.setFill(Color.rgb(160, 160, 185));
        gc.fillText("LEVEL", 10, ROW1_Y - 4);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gc.setFill(Color.rgb(220, 220, 255));
        gc.fillText(level + " / 3", 10, ROW2_Y);
    }

    /** Section 2: Lolli icon + 0/1 counter */
    private static void drawLolliSection(GraphicsContext gc, List<Item> chests) {
        boolean found = chests.stream().anyMatch(c -> c.isCollected() && c.hasLolli());
        double secX = DIV_LEVEL + 6;

        double cx = secX + 5;
        double cy = HUD_H / 2;

        // Glow effect similar to reveal
        double pulse = Math.sin(System.currentTimeMillis() * 0.005) * 0.2 + 0.8;
        if (found) {
            gc.setFill(Color.rgb(255, 215, 0, 0.05 * pulse));
            gc.fillOval(cx - 12, cy - 12, 24, 24);
            gc.setFill(Color.rgb(255, 180, 0, 0.1 * pulse));
            gc.fillOval(cx - 8, cy - 8, 16, 16);
            gc.setFill(Color.rgb(255, 230, 100, 0.2 * pulse));
            gc.fillOval(cx - 5, cy - 5, 10, 10);
        }

        // Draw the lolli itself
        GameRenderer.drawRedLolli(gc, cx, cy - 2, 8);

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        gc.setFill(found ? Color.LIMEGREEN : Color.rgb(190, 190, 190));
        gc.fillText((found ? "1" : "0") + "/1", secX + 14, HUD_H / 2 + 5);
    }

    /**
     * Section 3: "FIND: Mud" (top) + "Distraction Spell: 2 (E)" sub-label (bottom)
     */
    private static void drawFindSection(GraphicsContext gc, int level, String[] itemNames,
            int distractionSpellCount, boolean hasCloneItem) {
        double secX = DIV_LOLLI + 8;

        // Top: FIND label
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        gc.setFill(Color.rgb(240, 200, 60));
        gc.fillText("FIND:", secX, ROW1_Y);

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        gc.setFill(Color.WHITE);
        gc.fillText(itemNames[level - 1], secX + 42, ROW1_Y);

        // Bottom: utility item counter
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 11));
        gc.setFill(Color.rgb(180, 175, 155));

        if (level == 3 && hasCloneItem) {
            gc.setFill(Color.rgb(200, 200, 120));
            gc.fillText("👧 Clone ready  [C] to place", secX, ROW2_Y);
        } else {
            gc.fillText("✨ Distraction Spell [E]: " + distractionSpellCount, secX, ROW2_Y);
        }
    }

    /** Section 4: Luna phase bar + state label */
    private static double drawLunaSection(GraphicsContext gc, Monster paleLuna, double pulsePhase) {
        if (paleLuna == null)
            return pulsePhase;

        double secX = DIV_FIND + 8;

        // Luna icon (18x18 centered vertically)
        int stateIdx = paleLuna.getState().ordinal();
        if (stateIdx < paleLunaIconImg.length && paleLunaIconImg[stateIdx] != null) {
            gc.drawImage(paleLunaIconImg[stateIdx], secX, HUD_H / 2 - 9, 18, 18);
        }
        double barX = secX + 22;
        double barW = DIV_LUNA - barX - 8;

        switch (paleLuna.getState()) {
            case DORMANT -> {
                int sLeft = paleLuna.getDormantTimer() / 60;
                Color tc = sLeft > 10 ? Color.LIMEGREEN
                        : sLeft > 6 ? Color.YELLOW
                                : sLeft > 3 ? Color.ORANGE
                                        : Color.rgb(255, (int) (50 * (Math.sin(pulsePhase) * 0.5 + 0.5)),
                                                (int) (50 * (Math.sin(pulsePhase) * 0.5 + 0.5)));
                drawBarLabel(gc, barX, ROW1_Y, "Pale Luna", Color.rgb(160, 160, 180));
                drawBar(gc, barX, BAR_Y, barW, BAR_H,
                        Color.rgb(40, 40, 45), Color.rgb(70, 70, 80), tc,
                        (double) paleLuna.getDormantTimer() / 900.0, "Sleeps " + sLeft + "s");
            }
            case STALKING -> {
                double flash = Math.sin(pulsePhase * 1.7) * 0.5 + 0.5;
                Color barFill = Color.rgb(200, 60, 60);
                drawBarLabel(gc, barX, ROW1_Y, "Pale Luna", Color.rgb(220, 100, 100));
                drawBar(gc, barX, BAR_Y, barW, BAR_H,
                        Color.rgb(60, 15, 15, 0.5 + flash * 0.3), Color.rgb(120, 30, 30), barFill,
                        (double) paleLuna.getStalkTimer() / 480.0, "She watches...");
                pulsePhase += 0.24;
            }
            case HUNTING -> {
                double cf = Math.sin(pulsePhase * 2) * 0.5 + 0.5;
                int sLeft = paleLuna.getHuntTimer() / 60;
                Color bg = Color.rgb(255, (int) (30 * cf), (int) (30 * cf));
                drawBarLabel(gc, barX, ROW1_Y, "⚠ HUNTING", Color.rgb(255, 80, 80));
                drawBar(gc, barX, BAR_Y, barW, BAR_H,
                        bg, Color.rgb(100, 0, 0), Color.rgb(220, 40, 40),
                        (double) paleLuna.getHuntTimer() / 360.0, "RUN!  " + sLeft + "s left");
                pulsePhase += 0.30;
            }
            case WAITING_AT_DOOR -> {
                drawBarLabel(gc, barX, ROW1_Y, "At the door", Color.ORANGE);
                drawBar(gc, barX, BAR_Y, barW, BAR_H,
                        Color.rgb(80, 40, 0), Color.rgb(140, 80, 0), Color.ORANGE,
                        (double) paleLuna.getWaitTimer() / 180.0, paleLuna.getWaitTimer() / 60 + "s...");
            }
        }
        return pulsePhase + 0.20;
    }

    /** Section 5: Sanity bar with skull icon */
    private static void drawSanitySection(GraphicsContext gc, Player player) {
        double secX = DIV_LUNA + 6;
        double barW = DIV_SANITY - secX - 6;
        int sanity = player.getSanity();

        // Skull icon
        Image skull = sanity < 50 ? sanitySkullLowImg : sanitySkullImg;
        if (skull != null) {
            gc.drawImage(skull, secX, HUD_H / 2 - 10, 20, 20);
        } else {
            gc.setFill(sanity < 50 ? Color.rgb(255, 80, 80) : Color.rgb(180, 180, 200));
            gc.fillOval(secX, HUD_H / 2 - 8, 16, 16);
        }

        double barX = secX + 24;
        double bw = barW - 24;

        Color fill;
        if (sanity > 50)
            fill = Color.rgb(100, 180, 255);
        else if (sanity > 25)
            fill = Color.rgb(255, 180, 50);
        else {
            double f = Math.sin(System.currentTimeMillis() * 0.01) * 0.5 + 0.5;
            fill = Color.rgb(255, (int) (50 * f), (int) (50 * f));
        }

        drawBarLabel(gc, barX, ROW1_Y, "MIND", Color.rgb(150, 150, 180));
        String label = sanity > 50 ? (sanity + "%") : sanity > 25 ? "Slipping" : "Breaking";
        drawBar(gc, barX, BAR_Y, bw, BAR_H, Color.rgb(40, 40, 45), Color.rgb(70, 70, 80), fill,
                (double) sanity / 100.0, label);
    }

    /** Section 6: [SAFE] indicator on the right */
    private static void drawSafeSection(GraphicsContext gc, Player player) {
        double secX = DIV_SANITY + 6;
        if (player.isInEscapeRoom()) {
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            gc.setFill(Color.LIMEGREEN);
            gc.fillText("✓ SAFE", secX, ROW1_Y);
            gc.setFont(Font.font("Arial", FontWeight.NORMAL, 10));
            gc.setFill(Color.rgb(120, 200, 120));
            gc.fillText("sanity +1/s", secX, ROW2_Y);
        } else {
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 11));
            gc.setFill(Color.rgb(100, 100, 120));
            gc.fillText("UNSAFE", secX, ROW1_Y);
        }
    }

    // ── Drawing utilities ─────────────────────────────────────────────────────

    /** Draws a progress bar with background fill, border, and fill value [0..1]. */
    private static void drawBar(GraphicsContext gc, double x, double y, double w, double h,
            Color bg, Color border, Color fill, double ratio, String label) {
        double r = Math.max(0, Math.min(1.0, ratio));
        gc.setFill(bg);
        gc.fillRect(x, y, w, h);
        gc.setStroke(border);
        gc.setLineWidth(1);
        gc.strokeRect(x, y, w, h);
        gc.setFill(fill);
        gc.fillRect(x + 1, y + 1, (w - 2) * r, h - 2);

        if (label != null && !label.isEmpty()) {
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
            double textY = y + h - 3;
            // Shadow
            gc.setFill(Color.rgb(0, 0, 0, 0.7));
            gc.fillText(label, x + 5, textY + 1);
            // Text
            gc.setFill(Color.WHITE);
            gc.fillText(label, x + 4, textY);
        }
    }

    /** Draws a small label. */
    private static void drawBarLabel(GraphicsContext gc, double x, double y, String text, Color color) {
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        gc.setFill(color);
        gc.fillText(text, x, y);
    }
}
