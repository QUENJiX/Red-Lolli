package com.nsu.cse215l.redlolli.redlolli.ui;

import com.nsu.cse215l.redlolli.redlolli.entities.Item;
import com.nsu.cse215l.redlolli.redlolli.entities.Monster;
import com.nsu.cse215l.redlolli.redlolli.entities.Player;
import com.nsu.cse215l.redlolli.redlolli.systems.FlashlightSystem;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

/**
 * Manages the in-game HUD visual layer at the top of the screen.
 * Draws text, status bars, item inventories, timers, and safe room states.
 */
public class HUDRenderer {

    private static final double HUD_HEIGHT = 50;
    private static final double HUD_WIDTH = 880;
    private static final double MID_Y = 31;

    /**
     * Draws the full HUD bar at the top of the game screen.
     */
    public static double drawHUD(GraphicsContext gc, int level, List<Item> chests,
            String[] itemNames, Monster paleLuna, Player player,
            FlashlightSystem flashlight,
            int fruitCount,
            int eggCount,
            boolean hasCloneItem,
            int invisibilityFrames,
            double pulsePhase) {

        drawBackground(gc);
        drawLevelIndicator(gc, level);
        drawDivider(gc, 90);
        drawLolliStatus(gc, chests);
        drawDivider(gc, 145);
        drawFindItem(gc, level, itemNames);
        drawDivider(gc, 320);
        drawBattery(gc, flashlight, pulsePhase);
        drawDivider(gc, 440);
        pulsePhase = drawPaleLunaStatus(gc, paleLuna, pulsePhase);
        drawDivider(gc, 785);
        drawSafeIndicator(gc, player);
        drawUtilityStatus(gc, level, fruitCount, eggCount, hasCloneItem, invisibilityFrames);

        return pulsePhase;
    }

    // ========================= HUD SECTIONS =========================

    /** Draws the persistent dark baseline separating the HUD from the canvas. */
    private static void drawBackground(GraphicsContext gc) {
        gc.setFill(Color.rgb(12, 12, 16));
        gc.fillRect(0, 0, HUD_WIDTH, HUD_HEIGHT);
        gc.setStroke(Color.DARKRED);
        gc.setLineWidth(2);
        gc.strokeLine(0, HUD_HEIGHT, HUD_WIDTH, HUD_HEIGHT);
    }

    /** Prints the current level numerator vs denominator on the far left. */
    private static void drawLevelIndicator(GraphicsContext gc, int level) {
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        gc.setFill(Color.rgb(180, 180, 200));
        gc.fillText("LEVEL " + level + "/3", 12, MID_Y);
    }

    /** Renders a vertical dividing line at the given x position. */
    private static void drawDivider(GraphicsContext gc, double x) {
        gc.setStroke(Color.rgb(50, 20, 20));
        gc.setLineWidth(1);
        gc.strokeLine(x, 8, x, 42);
    }

    /** Checks if the Red Lolli objective has been collected. */
    private static void drawLolliStatus(GraphicsContext gc, List<Item> chests) {
        boolean foundLolli = chests.stream().anyMatch(c -> c.isCollected() && c.hasLolli());

        gc.setFill(Color.DARKRED);
        gc.fillOval(112, MID_Y - 9, 8, 8);
        gc.setStroke(Color.rgb(180, 150, 100));
        gc.setLineWidth(1.5);
        gc.strokeLine(116, MID_Y - 1, 116, MID_Y + 5);

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        gc.setFill(foundLolli ? Color.LIMEGREEN : Color.rgb(200, 200, 200));
        gc.fillText((foundLolli ? "1" : "0") + "/1", 124, MID_Y);
    }

    /** Displays the target item name for the current level. */
    private static void drawFindItem(GraphicsContext gc, int level, String[] itemNames) {
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        gc.setFill(Color.GOLD);
        gc.fillText("FIND: " + itemNames[level - 1], 172, MID_Y);
    }

    /** Renders battery level bar with color-coded urgency. */
    private static void drawBattery(GraphicsContext gc, FlashlightSystem flashlight, double pulsePhase) {
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        Color labelColor = flashlight.isCriticalBattery()
                ? Color.rgb(230, (int) (50 + 80 * (Math.sin(pulsePhase * 2) * 0.5 + 0.5)), 50)
                : Color.rgb(220, 220, 220);
        gc.setFill(labelColor);
        gc.fillText("BATTERY", 332, 18);

        gc.setFill(Color.rgb(30, 30, 40));
        gc.fillRect(332, 22, 96, 10);
        gc.setStroke(Color.rgb(90, 90, 110));
        gc.setLineWidth(1);
        gc.strokeRect(332, 22, 96, 10);

        double fill = Math.max(0.0, Math.min(1.0, flashlight.getBatteryPercent()));
        Color barColor = fill >= 0.35 ? Color.rgb(90, 200, 255) : Color.ORANGE;
        if (fill < 0.15) barColor = Color.RED;

        gc.setFill(barColor);
        gc.fillRect(333, 23, 94 * fill, 8);

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        gc.setFill(Color.rgb(210, 210, 210));
        gc.fillText((flashlight.isOn() ? "ON " : "OFF ") + Math.round(fill * 100) + "%", 348, 42);
    }

    /** Renders Luna's state-specific warning bar in the HUD. */
    private static double drawPaleLunaStatus(GraphicsContext gc, Monster paleLuna, double pulsePhase) {
        if (paleLuna == null) return pulsePhase;

        double barX = 452, barW = 126, barY = MID_Y - 18, barH = 12;

        switch (paleLuna.getState()) {
            case DORMANT -> pulsePhase = drawDormantStatus(gc, paleLuna, barX, barY, barW, barH, pulsePhase);
            case STALKING -> pulsePhase = drawStalkingStatus(gc, paleLuna, barX, barY, barW, barH, pulsePhase);
            case HUNTING -> pulsePhase = drawHuntingStatus(gc, paleLuna, barX, barY, barW, barH, pulsePhase);
            case WAITING_AT_DOOR -> drawWaitingStatus(gc, paleLuna, barX);
        }
        return pulsePhase;
    }

    /** Dormant countdown timer: green → yellow → orange → throbbing red. */
    private static double drawDormantStatus(GraphicsContext gc, Monster paleLuna,
            double barX, double barY, double barW, double barH, double pulsePhase) {
        int secondsLeft = paleLuna.getDormantTimer() / 60;
        Color timerColor;

        if (secondsLeft > 10) {
            timerColor = Color.LIMEGREEN;
        } else if (secondsLeft > 6) {
            timerColor = Color.YELLOW;
        } else if (secondsLeft > 3) {
            timerColor = Color.ORANGE;
        } else {
            double flash = Math.sin(pulsePhase) * 0.5 + 0.5;
            timerColor = Color.rgb(255, (int) (50 * flash), (int) (50 * flash));
        }
        pulsePhase += 0.2;

        gc.setFill(Color.rgb(40, 40, 45));
        gc.fillRect(barX, barY, barW, barH);
        gc.setStroke(Color.rgb(80, 80, 90));
        gc.setLineWidth(1);
        gc.strokeRect(barX, barY, barW, barH);

        double fillRatio = (double) paleLuna.getDormantTimer() / 900.0;
        gc.setFill(timerColor);
        gc.fillRect(barX + 1, barY + 1, (barW - 2) * fillRatio, barH - 2);

        gc.setFill(timerColor);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        gc.fillText("She sleeps " + secondsLeft + "s", barX, MID_Y + 6);
        return pulsePhase;
    }

    /** Stalking warning: medium-pulse red backdrop. */
    private static double drawStalkingStatus(GraphicsContext gc, Monster paleLuna,
            double barX, double barY, double barW, double barH, double pulsePhase) {
        double flash = Math.sin(pulsePhase * 1.7) * 0.5 + 0.5;
        pulsePhase += 0.24;

        gc.setFill(Color.rgb(120, 20, 20, 0.25 + flash * 0.25));
        gc.fillRect(barX, barY, barW, barH);
        gc.setStroke(Color.rgb(140, 40, 40));
        gc.strokeRect(barX, barY, barW, barH);

        double fill = (double) paleLuna.getStalkTimer() / 480.0;
        gc.setFill(Color.rgb(210, 80, 80));
        gc.fillRect(barX + 1, barY + 1, (barW - 2) * fill, barH - 2);

        gc.setFill(Color.rgb(230, 120, 120));
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        gc.fillText("She watches", barX, MID_Y + 6);

        return pulsePhase;
    }

    /** Hunt timer: aggressive strobing red with "RUN" label. */
    private static double drawHuntingStatus(GraphicsContext gc, Monster paleLuna,
            double barX, double barY, double barW, double barH, double pulsePhase) {
        double chaseFlash = Math.sin(pulsePhase * 2) * 0.5 + 0.5;
        pulsePhase += 0.3;

        gc.setFill(Color.rgb(255, (int) (30 * chaseFlash), (int) (30 * chaseFlash)));
        gc.fillRect(barX, barY, barW, barH);
        gc.setStroke(Color.rgb(120, 0, 0));
        gc.setLineWidth(1);
        gc.strokeRect(barX, barY, barW, barH);

        double chaseFill = (double) paleLuna.getHuntTimer() / 360.0;
        gc.setFill(Color.rgb(200, 0, 0));
        gc.fillRect(barX + 1, barY + 1, (barW - 2) * chaseFill, barH - 2);

        int chaseSecsLeft = paleLuna.getHuntTimer() / 60;
        gc.setFill(Color.RED);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        gc.fillText("RUN " + chaseSecsLeft + "s", barX, MID_Y + 6);
        return pulsePhase;
    }

    /** Waiting-at-door static indicator. */
    private static void drawWaitingStatus(GraphicsContext gc, Monster paleLuna, double barX) {
        gc.setFill(Color.ORANGE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        int waitSecs = paleLuna.getWaitTimer() / 60;
        gc.fillText("At the door " + waitSecs + "s", barX, MID_Y);
    }

    /** Green [SAFE] text when player is in the escape room. */
    private static void drawSafeIndicator(GraphicsContext gc, Player player) {
        if (player.isInEscapeRoom()) {
            gc.setFill(Color.LIMEGREEN);
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            gc.fillText("SAFE", 802, MID_Y);
        }
    }

    /** Level-specific item counters. */
    private static void drawUtilityStatus(GraphicsContext gc, int level, int fruitCount, int eggCount,
            boolean hasCloneItem, int invisibilityFrames) {
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        gc.setFill(Color.rgb(200, 190, 170));

        if (level == 1) {
            gc.fillText("Fruit: " + fruitCount + " (E)", 172, 44);
        } else if (level == 2) {
            gc.fillText("Eggs: " + eggCount + " (E)", 172, 44);
        } else if (level == 3 && hasCloneItem) {
            gc.fillText("Clone Ready (C)", 172, 44);
        }
    }
}
