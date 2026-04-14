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
 *  [LEVEL 1/3] | [🍬 0/1] | [FIND: Mud / Fruit:2 (E)] | [Luna bar + label] | [MIND bar] | [SAFE]
 *   x=0..89      x=90..149   x=150..334                   x=335..609           x=610..809   x=810..879
 *
 * All text baseline at ROW1_Y (top row) or ROW2_Y (sub-label row).
 * All bars are at BAR_Y with height BAR_H.
 */
public class HUDRenderer {

    // ── Layout constants ─────────────────────────────────────────────────────
    private static final double HUD_W    = 880;
    private static final double HUD_H    = 50;
    private static final double ROW1_Y   = 20;   // primary text baseline
    private static final double ROW2_Y   = 38;   // secondary / sub-label baseline
    private static final double BAR_Y    = 26;   // top of progress bars
    private static final double BAR_H    = 10;   // height of progress bars

    // ── Section x-boundaries ─────────────────────────────────────────────────
    private static final double DIV_LEVEL  =  90;
    private static final double DIV_LOLLI  = 150;
    private static final double DIV_FIND   = 335;
    private static final double DIV_LUNA   = 610;
    private static final double DIV_SANITY = 810;

    // ── Image assets ─────────────────────────────────────────────────────────
    private static Image lolliIconImg;
    private static Image[] paleLunaIconImg = new Image[4];
    private static Image sanitySkullImg;
    private static Image sanitySkullLowImg;
    private static boolean imagesInitialized = false;

    private static Image loadSprite(String filename, int width, int height) {
        try {
            InputStream is = HUDRenderer.class.getResourceAsStream("/assets/images/sprites/" + filename);
            if (is != null) return new Image(is, width, height, true, false);
        } catch (Exception ignored) {}
        return null;
    }

    public static void initImages() {
        if (imagesInitialized) return;
        lolliIconImg        = loadSprite("lolli_icon.png",        10, 20);
        paleLunaIconImg[0]  = loadSprite("monster_dormant.png",   18, 18);
        paleLunaIconImg[1]  = loadSprite("monster_stalking.png",  18, 18);
        paleLunaIconImg[2]  = loadSprite("monster_hunting.png",   18, 18);
        paleLunaIconImg[3]  = loadSprite("monster_waiting.png",   18, 18);
        sanitySkullImg      = loadSprite("sanity_skull.png",      20, 20);
        sanitySkullLowImg   = loadSprite("sanity_skull_2.png",    20, 20);
        imagesInitialized = true;
    }

    /** Force images to reload. */
    public static void resetImages() { imagesInitialized = false; }

    // ── Entry point ───────────────────────────────────────────────────────────

    public static double drawHUD(GraphicsContext gc, int level, List<Item> chests,
            String[] itemNames, Monster paleLuna, Player player,
            int fruitCount, int eggCount, boolean hasCloneItem, double pulsePhase) {

        drawBackground(gc);

        // Section 1 — Level indicator
        drawLevelSection(gc, level);
        drawDivider(gc, DIV_LEVEL);

        // Section 2 — Lolli counter
        drawLolliSection(gc, chests);
        drawDivider(gc, DIV_LOLLI);

        // Section 3 — Find target + utility sub-label
        drawFindSection(gc, level, itemNames, fruitCount, eggCount, hasCloneItem);
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

        // Small red lolli icon (or fallback red square)
        if (lolliIconImg != null) {
            gc.drawImage(lolliIconImg, secX, HUD_H / 2 - 10, 10, 20);
        } else {
            gc.setFill(Color.RED);
            gc.fillRect(secX, HUD_H / 2 - 5, 8, 12);
        }

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        gc.setFill(found ? Color.LIMEGREEN : Color.rgb(190, 190, 190));
        gc.fillText((found ? "1" : "0") + "/1", secX + 14, HUD_H / 2 + 5);
    }

    /** Section 3: "FIND: Mud" (top) + "Fruit: 2 (E)" sub-label (bottom) */
    private static void drawFindSection(GraphicsContext gc, int level, String[] itemNames,
            int fruitCount, int eggCount, boolean hasCloneItem) {
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
        if (level == 1) {
            gc.fillText("🍎 Fruit: " + fruitCount + "  [E] to throw", secX, ROW2_Y);
        } else if (level == 2) {
            gc.fillText("🥚 Eggs: " + eggCount + "  [E] to throw", secX, ROW2_Y);
        } else if (hasCloneItem) {
            gc.setFill(Color.rgb(200, 200, 120));
            gc.fillText("📦 Clone ready  [C] to place", secX, ROW2_Y);
        } else {
            gc.fillText("Place a clone decoy to distract", secX, ROW2_Y);
        }
    }

    /** Section 4: Luna phase bar + state label */
    private static double drawLunaSection(GraphicsContext gc, Monster paleLuna, double pulsePhase) {
        if (paleLuna == null) return pulsePhase;

        double secX  = DIV_FIND + 8;

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
                         : sLeft > 6  ? Color.YELLOW
                         : sLeft > 3  ? Color.ORANGE
                         : Color.rgb(255, (int)(50 * (Math.sin(pulsePhase) * 0.5 + 0.5)),
                                         (int)(50 * (Math.sin(pulsePhase) * 0.5 + 0.5)));
                drawBar(gc, barX, BAR_Y, barW, BAR_H,
                        Color.rgb(40, 40, 45), Color.rgb(70, 70, 80), tc,
                        paleLuna.getDormantTimer(), 900);
                drawBarLabel(gc, barX, ROW1_Y - 7, "Pale Luna", Color.rgb(160, 160, 180));
                drawBarLabel(gc, barX, ROW2_Y, "Sleeps " + sLeft + "s", tc);
            }
            case STALKING -> {
                double flash = Math.sin(pulsePhase * 1.7) * 0.5 + 0.5;
                Color barFill = Color.rgb(200, 60, 60);
                drawBar(gc, barX, BAR_Y, barW, BAR_H,
                        Color.rgb(60, 15, 15, 0.5 + flash * 0.3), Color.rgb(120, 30, 30), barFill,
                        paleLuna.getStalkTimer(), 480);
                drawBarLabel(gc, barX, ROW1_Y - 7, "Pale Luna", Color.rgb(220, 100, 100));
                drawBarLabel(gc, barX, ROW2_Y, "She watches...", Color.rgb(230, 120, 120));
                pulsePhase += 0.24;
            }
            case HUNTING -> {
                double cf = Math.sin(pulsePhase * 2) * 0.5 + 0.5;
                int sLeft = paleLuna.getHuntTimer() / 60;
                Color bg = Color.rgb(255, (int)(30 * cf), (int)(30 * cf));
                drawBar(gc, barX, BAR_Y, barW, BAR_H,
                        bg, Color.rgb(100, 0, 0), Color.rgb(220, 40, 40),
                        paleLuna.getHuntTimer(), 360);
                drawBarLabel(gc, barX, ROW1_Y - 7, "⚠ HUNTING", Color.rgb(255, 80, 80));
                drawBarLabel(gc, barX, ROW2_Y, "RUN!  " + sLeft + "s left", Color.RED);
                pulsePhase += 0.30;
            }
            case WAITING_AT_DOOR -> {
                drawBar(gc, barX, BAR_Y, barW, BAR_H,
                        Color.rgb(80, 40, 0), Color.rgb(140, 80, 0), Color.ORANGE,
                        paleLuna.getWaitTimer(), 180);
                drawBarLabel(gc, barX, ROW1_Y - 7, "At the door", Color.ORANGE);
                drawBarLabel(gc, barX, ROW2_Y, paleLuna.getWaitTimer() / 60 + "s...", Color.rgb(255, 180, 60));
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
        double bw   = barW - 24;

        Color fill;
        if (sanity > 50)      fill = Color.rgb(100, 180, 255);
        else if (sanity > 25) fill = Color.rgb(255, 180, 50);
        else {
            double f = Math.sin(System.currentTimeMillis() * 0.01) * 0.5 + 0.5;
            fill = Color.rgb(255, (int)(50 * f), (int)(50 * f));
        }

        drawBarLabel(gc, barX, ROW1_Y - 7, "MIND", Color.rgb(150, 150, 180));
        drawBar(gc, barX, BAR_Y, bw, BAR_H, Color.rgb(40, 40, 45), Color.rgb(70, 70, 80), fill,
                sanity, 100);

        String label = sanity > 50 ? (sanity + "%") : sanity > 25 ? "Slipping" : "Breaking";
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        gc.setFill(Color.rgb(220, 220, 230));
        gc.fillText(label, barX + 2, BAR_Y + BAR_H - 1);
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

    /** Draws a progress bar with background fill, border, and fill value [0..maxVal]. */
    private static void drawBar(GraphicsContext gc, double x, double y, double w, double h,
            Color bg, Color border, Color fill, int val, int maxVal) {
        double ratio = Math.max(0, Math.min(1.0, (double) val / maxVal));
        gc.setFill(bg);
        gc.fillRect(x, y, w, h);
        gc.setStroke(border);
        gc.setLineWidth(1);
        gc.strokeRect(x, y, w, h);
        gc.setFill(fill);
        gc.fillRect(x + 1, y + 1, (w - 2) * ratio, h - 2);
    }

    /** Draws a small label above / below a bar. */
    private static void drawBarLabel(GraphicsContext gc, double x, double y, String text, Color color) {
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 10));
        gc.setFill(color);
        gc.fillText(text, x, y);
    }
}
