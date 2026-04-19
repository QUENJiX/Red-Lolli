package com.nsu.cse215l.redlolli.redlolli.ui;

import com.nsu.cse215l.redlolli.redlolli.entities.Item;
import com.nsu.cse215l.redlolli.redlolli.entities.Monster;
import com.nsu.cse215l.redlolli.redlolli.entities.Player;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

/**
 * This class draws all the stuff on the top of the screen while you play the game!
 * It handles the player's health/sanity bar, the current level, your items, and the spooky 
 * little icon showing what Pale Luna is currently doing.
 */
public class HUDRenderer {

    private static final double HUD_W = 880;
    private static final double HUD_H = 50;
    private static final double ROW1_Y = 20;
    private static final double ROW2_Y = 38;
    private static final double BAR_Y = 24;
    private static final double BAR_H = 14;

    private static final double DIV_LEVEL = 90;
    private static final double DIV_LOLLI = 150;
    private static final double DIV_FIND = 335;
    private static final double DIV_LUNA = 610;
    private static final double DIV_SANITY = 810;

    private static Image[] paleLunaIconImg = new Image[4];
    private static Image sanitySkullImg;
    private static Image sanitySkullLowImg;
    private static boolean imagesInitialized = false;

    /**
     * A helper method to load sprites specifically for the HUD.
     * We pass it along to the AssetManager so it caches the file properly!
     *
     * @param filename The name of the file to load (like "monster_hunting.png").
     * @param width    How wide to draw the sprite.
     * @param height   How tall to draw the sprite.
     * @return Image The loaded JavaFX graphic ready to be drawn.
     */
    private static Image loadSprite(String filename, int width, int height) {
        return com.nsu.cse215l.redlolli.redlolli.systems.AssetManager.getInstance()
                .getSprite("/assets/images/sprites/" + filename, width, height);
    }

    /**
     * Loads up all the little skulls and monster face icons for the top bar!
     * We track whether they are loaded so we only have to do this once.
     */
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

    /**
     * Wipes our memory of whether images were loaded or not, forcing a quick reload next time.
     */
    public static void resetImages() {
        imagesInitialized = false;
    }

    /**
     * The main drawing method that updates the top bar every single frame!
     * It splits up into separate sections: drawing Level, Lollipops, Items, Luna's status, and Sanity.
     *
     * @param gc                    The JavaFX GraphicsContext to draw onto.
     * @param level                 The current level number.
     * @param chests                The list of items lying around on the map.
     * @param itemNames             The list of spooky secret items available.
     * @param paleLuna              The actual Pale Luna monster entity, to check her current mood.
     * @param player                The player entity, to check health/sanity and inventory.
     * @param distractionSpellCount The number of distractions the player has saved up.
     * @param hasCloneItem          Whether the player is carrying the cardboard clone decoy.
     * @param pulsePhase            A number that goes up over time, used to make the health bar throb.
     * @return double The updated pulsePhase value for the next frame's animation.
     */
    public static double drawHUD(GraphicsContext gc, int level, List<Item> chests,
            String[] itemNames, Monster paleLuna, Player player,
            int distractionSpellCount, boolean hasCloneItem, double pulsePhase) {

        drawBackground(gc);

        drawLevelSection(gc, level);
        drawDivider(gc, DIV_LEVEL);

        drawLolliSection(gc, chests);
        drawDivider(gc, DIV_LOLLI);

        drawFindSection(gc, level, itemNames, distractionSpellCount, hasCloneItem);
        drawDivider(gc, DIV_FIND);

        pulsePhase = drawLunaSection(gc, paleLuna, pulsePhase);
        drawDivider(gc, DIV_LUNA);

        drawSanitySection(gc, player);
        drawDivider(gc, DIV_SANITY);

        drawSafeSection(gc, player);

        return pulsePhase;
    }

    /**
     * Colors in the dark background of the top bar and gives it a nice red border at the bottom.
     *
     * @param gc The drawing context for the canvas.
     */
    private static void drawBackground(GraphicsContext gc) {
        // Draw the main dark grey block for the HUD background
        gc.setFill(Color.rgb(10, 10, 14));
        gc.fillRect(0, 0, HUD_W, HUD_H);

        gc.setFill(Color.rgb(30, 5, 5));
        gc.fillRect(0, HUD_H - 3, HUD_W, 3);

        gc.setStroke(Color.rgb(140, 20, 20, 0.8));
        gc.setLineWidth(1.5);
        gc.strokeLine(0, HUD_H, HUD_W, HUD_H);
    }

    /**
     * Generates geometric separators strictly mapping distinct boundaries
     * objectively.
     *
     * @param gc Target rendering vector systematically scaling linear separators
     *           optimally.
     * @param x  Standardized dimensional axis defining bounded separations
     *           explicitly.
     */
    private static void drawDivider(GraphicsContext gc, double x) {
        gc.setStroke(Color.rgb(60, 25, 25, 0.7));
        gc.setLineWidth(1);
        gc.strokeLine(x, 6, x, HUD_H - 6);
    }

    /**
     * Draws the text telling you what level you are currently on.
     *
     * @param gc    The drawing context.
     * @param level The current level number.
     */
    private static void drawLevelSection(GraphicsContext gc, int level) {
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        gc.setFill(Color.rgb(160, 160, 185));
        gc.fillText("LEVEL", 10, ROW1_Y - 4);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        gc.setFill(Color.rgb(220, 220, 255));
        gc.fillText(level + " / 3", 10, ROW2_Y);
    }

    /**
     * Checks if you've picked up the Red Lolli objective yet and draws a glowing
     * red lollipop icon if you have, or a sad grey one if you haven't.
     *
     * @param gc     The drawing context.
     * @param chests The list of items in the level to check if the lolli was grabbed.
     */
    private static void drawLolliSection(GraphicsContext gc, List<Item> chests) {
        boolean found = chests.stream().anyMatch(c -> c.isCollected() && c.hasLolli());
        double secX = DIV_LEVEL + 6;

        double cx = secX + 5;
        double cy = HUD_H / 2;

        // Make the lollipop glow with a wiggling math wave if you found it!
        double pulse = Math.sin(System.currentTimeMillis() * 0.005) * 0.2 + 0.8;
        if (found) {
            gc.setFill(Color.rgb(255, 215, 0, 0.05 * pulse));
            gc.fillOval(cx - 12, cy - 12, 24, 24);
            gc.setFill(Color.rgb(255, 180, 0, 0.1 * pulse));
            gc.fillOval(cx - 8, cy - 8, 16, 16);
            gc.setFill(Color.rgb(255, 230, 100, 0.2 * pulse));
            gc.fillOval(cx - 5, cy - 5, 10, 10);
        }

        GameRenderer.drawRedLolli(gc, cx, cy - 2, 8);

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        gc.setFill(found ? Color.LIMEGREEN : Color.rgb(190, 190, 190));
        gc.fillText((found ? "1" : "0") + "/1", secX + 14, HUD_H / 2 + 5);
    }

    /**
     * Shows which secret item you're supposed to be finding on this level, 
     * and lets you know how many distraction spells you have left!
     *
     * @param gc                    The drawing context.
     * @param level                 The current level (so we know which item to tell you to find).
     * @param itemNames             The list of spooky secret item names.
     * @param distractionSpellCount The amount of magic distraction balls the player can throw.
     * @param hasCloneItem          Whether the player is currently carrying the cardboard decoy box.
     */
    private static void drawFindSection(GraphicsContext gc, int level, String[] itemNames,
            int distractionSpellCount, boolean hasCloneItem) {
        double secX = DIV_LOLLI + 8;

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        gc.setFill(Color.rgb(240, 200, 60));
        gc.fillText("FIND:", secX, ROW1_Y);

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        gc.setFill(Color.WHITE);
        gc.fillText(itemNames[level - 1], secX + 42, ROW1_Y);

        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 11));
        gc.setFill(Color.rgb(180, 175, 155));

        if (level == 3 && hasCloneItem) {
            gc.setFill(Color.rgb(200, 200, 120));
            gc.fillText("👧 Clone ready  [C] to place", secX, ROW2_Y);
        } else {
            gc.fillText("✨ Distraction Spell [E]: " + distractionSpellCount, secX, ROW2_Y);
        }
    }

    /**
     * Draws a tiny little picture of Pale Luna's face and a status bar showing what she's 
     * doing right now (Sleeping, Stalking, Hunting you, or waiting outside your door).
     *
     * @param gc         The drawing context.
     * @param paleLuna   The monster entity to read the state from.
     * @param pulsePhase A number used for animating the spooky red glowing bar.
     * @return double    The updated animation number.
     */
    private static double drawLunaSection(GraphicsContext gc, Monster paleLuna, double pulsePhase) {
        if (paleLuna == null)
            return pulsePhase;

        double secX = DIV_FIND + 8;

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

    /**
     * Draws the player's sanity bar and updates the little skull icon depending
     * on how low their sanity has dropped.
     *
     * @param gc     The drawing context.
     * @param player The player object, so we can check their current sanity score.
     */
    private static void drawSanitySection(GraphicsContext gc, Player player) {
        double secX = DIV_LUNA + 6;
        double barW = DIV_SANITY - secX - 6;
        int sanity = player.getSanity();

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        gc.setFill(Color.rgb(180, 180, 200));
        gc.fillText("Sanity", secX, ROW1_Y);

        Color sanityColor = Color.LIMEGREEN;
        Image activeImg = sanitySkullImg;

        if (sanity < 30) {
            sanityColor = Color.RED;
            activeImg = sanitySkullLowImg;
        } else if (sanity < 60) {
            sanityColor = Color.ORANGE;
        } else if (sanity < 85) {
            sanityColor = Color.YELLOW;
        }

        if (activeImg != null) {
            gc.drawImage(activeImg, DIV_SANITY - 24, 4, 18, 18);
        }

        drawBar(gc, secX, BAR_Y, barW, BAR_H,
                Color.rgb(30, 30, 35), Color.rgb(80, 80, 90), sanityColor,
                sanity / 100.0, sanity + "%");
    }

    /**
     * Draws a helpful little indicator on the far right that tells you 
     * if you are currently hiding inside a sanctuary room.
     *
     * @param gc     The drawing context.
     * @param player The player object, used to check if they are standing in a safe spot.
     */
    private static void drawSafeSection(GraphicsContext gc, Player player) {
        double secX = DIV_SANITY + 8;
        boolean safe = player.isInEscapeRoom();

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 13));
        if (safe) {
            gc.setFill(Color.LIMEGREEN);
            gc.fillText("✔ SAFE", secX, ROW1_Y + 8);
        } else {
            gc.setFill(Color.rgb(140, 140, 140));
            gc.fillText("DANGER", secX, ROW1_Y + 8);
        }
    }

    /**
     * A helper that simply styles the tiny text labels hovering over the progress bars.
     *
     * @param gc    The drawing context.
     * @param x     The X pixel position.
     * @param y     The Y pixel position.
     * @param label The text to draw.
     * @param color The color of the text.
     */
    private static void drawBarLabel(GraphicsContext gc, double x, double y, String label, Color color) {
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        gc.setFill(color);
        gc.fillText(label, x, y);
    }

    /**
     * A helper method that draws a rectangle progress bar with a border, background, and text.
     * Used for things like the sanity bar and Pale Luna's timer bars!
     *
     * @param gc       The drawing context.
     * @param x        The X pixel position.
     * @param y        The Y pixel position.
     * @param width    How wide to make the bar.
     * @param height   How tall to make the bar.
     * @param bg       The background color of the empty part of the bar.
     * @param border   The border color wrapping around the bar.
     * @param fill     The color of the actual progress filling the bar.
     * @param progress A number from 0 to 1 dictating how full the bar should be drawn.
     * @param text     The text to draw directly inside the middle of the bar.
     */
    private static void drawBar(GraphicsContext gc, double x, double y, double width, double height,
            Color bg, Color border, Color fill, double progress, String text) {
        gc.setFill(bg);
        gc.fillRect(x, y, width, height);

        gc.setStroke(border);
        gc.strokeRect(x, y, width, height);

        double bw = width * Math.max(0, Math.min(1, progress));
        if (bw > 0) {
            gc.setFill(fill);
            gc.fillRect(x + 1, y + 1, bw - 2, height - 2);
        }

        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 10));
        gc.setFill(Color.WHITE);
        gc.fillText(text, x + 4, y + 10);
    }
}
