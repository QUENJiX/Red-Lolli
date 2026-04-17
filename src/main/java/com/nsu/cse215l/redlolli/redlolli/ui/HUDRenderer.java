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
 * Orchestrates the application's heads-up display rendering matrix.
 * Translates underlying game state variables into a fixed-scale orthogonal
 * projection layer.
 * Systematically divides real-time tracking data into distinct, updated visual
 * modules natively.
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
     * Resolves distinct aesthetic resource identifiers yielding explicitly scaled
     * bitmapped visual arrays.
     *
     * @param filename Target identifier isolating physical location inherently
     *                 visually.
     * @param width    Abstract mapping value for lateral scale parameters
     *                 structurally.
     * @param height   Abstract mapping value for vertical scale parameters
     *                 structurally.
     * @return Image Explicitly loaded structural rendering variable natively
     *         extracted.
     */
    private static Image loadSprite(String filename, int width, int height) {
        return com.nsu.cse215l.redlolli.redlolli.systems.AssetManager.getInstance()
                .getSprite("/assets/images/sprites/" + filename, width, height);
    }

    /**
     * Establishes persistent graphical references ensuring uniform hardware
     * mappings logically.
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
     * Resets the initialization validation cache confirming sequential reloading
     * dynamically.
     */
    public static void resetImages() {
        imagesInitialized = false;
    }

    /**
     * Executes the foundational projection separating HUD logic cleanly into
     * rendering groups.
     * Extrapolates physical states mapping visual representation vectors linearly.
     *
     * @param gc                    Hardware rendering component processing
     *                              graphical indices optimally.
     * @param level                 Absolute cycle iterator mapping progression
     *                              depth accurately.
     * @param chests                Topological entity list conveying objective
     *                              bounds conditionally.
     * @param itemNames             Assorted strings mapping literal visual
     *                              translations natively.
     * @param paleLuna              Target external antagonist mapping spatial
     *                              derivation algorithms mathematically.
     * @param player                Core topological object tracking diagnostic
     *                              progression variables identically.
     * @param distractionSpellCount Abstraction metric indicating valid mechanical
     *                              override availability natively.
     * @param hasCloneItem          Boolean asserting local state capability
     *                              securely and unconditionally.
     * @param pulsePhase            Iterative parameter resolving continuous
     *                              cyclical HUD animations smoothly.
     * @return double Translated visual execution vector correctly shifting abstract
     *         animation steps sequentially.
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
     * Synthesizes background geometries providing standard solid backing logically
     * mapping spatial matrices.
     *
     * @param gc Target abstraction projecting static canvas updates stably and
     *           objectively.
     */
    private static void drawBackground(GraphicsContext gc) {
        // Enforce basic gradient borders anchoring local interface variables
        // exclusively
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
     * Projects continuous text bounds updating basic state enumerations
     * dynamically.
     *
     * @param gc    Mathematical node scaling output coordinates inherently
     *              securely.
     * @param level Evaluated metric dictating raw cyclic progression explicitly.
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
     * Establishes conditional rendering parameters validating interaction checks
     * sequentially natively.
     *
     * @param gc     Hardware target interpolating graphical variables predictably
     *               and functionally.
     * @param chests Reference collection enumerating valid boolean interaction
     *               outputs completely.
     */
    private static void drawLolliSection(GraphicsContext gc, List<Item> chests) {
        boolean found = chests.stream().anyMatch(c -> c.isCollected() && c.hasLolli());
        double secX = DIV_LEVEL + 6;

        double cx = secX + 5;
        double cy = HUD_H / 2;

        // Apply sinusoidal modifiers conveying aesthetic oscillations accurately
        // automatically
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
     * Extracts categorical strings projecting mechanical requirements mapping
     * textual outputs clearly cleanly.
     *
     * @param gc                    Output target interpolating sequential strings
     *                              systematically objectively.
     * @param level                 Baseline algorithmic modifier extracting mapped
     *                              array targets cleanly.
     * @param itemNames             Extrapolated strings array indexing specific
     *                              objective labels directly.
     * @param distractionSpellCount Integral translating stored defensive override
     *                              resources completely flawlessly.
     * @param hasCloneItem          Validation toggle rendering specific alternate
     *                              interactions strictly cleanly.
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
     * Transmits decoupled adversarial mappings directly formatting threat
     * representations linearly seamlessly.
     * Resolves distinct tracking bounds utilizing cyclic color scaling
     * deterministically cleanly.
     *
     * @param gc         Mathematical rendering abstraction mapping geometric
     *                   parameters cleanly and uniformly.
     * @param paleLuna   AI reference extrapolating threat vectors structurally
     *                   correctly.
     * @param pulsePhase Chronological iteration tracking arbitrary visual
     *                   oscillations smoothly predictably.
     * @return double Final scalar yielding updated progression safely correctly.
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
     * Converts physiological variable checks into standard linear text interfaces
     * continuously natively.
     *
     * @param gc     Native visual array configuring objective text properly
     *               identically stably.
     * @param player Central topological object asserting fundamental variable
     *               metrics dynamically properly.
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
     * Determines regional mapping bounds establishing Boolean safety outputs
     * objectively clearly objectively.
     *
     * @param gc     Graphical array rendering continuous binary tracking natively
     *               cleanly effectively.
     * @param player Core spatial parameter providing matrix positioning securely
     *               dynamically optimally.
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
     * Formats basic textual nodes rendering uniform string identifiers explicitly
     * consistently natively.
     *
     * @param gc    Rendering anchor applying explicit parameter limitations
     *              properly optimally cleanly.
     * @param x     Arbitrary coordinate tracking horizontal string deployments
     *              accurately.
     * @param y     Arbitrary coordinate tracking vertical string deployments
     *              accurately.
     * @param label Contextual string defining explicitly mapped UI elements
     *              definitively safely.
     * @param color Abstract RGB abstraction translating visual output boundaries
     *              linearly intelligently.
     */
    private static void drawBarLabel(GraphicsContext gc, double x, double y, String label, Color color) {
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        gc.setFill(color);
        gc.fillText(label, x, y);
    }

    /**
     * Allocates functional geometry constraints validating scalar parameter shifts
     * visually perfectly consistently.
     *
     * @param gc       Output target interpolating hardware boundaries explicitly
     *                 naturally structurally.
     * @param x        Mathematical location defining physical rectangular bounds
     *                 linearly unambiguously.
     * @param y        Mathematical location defining physical rectangular bounds
     *                 linearly unambiguously.
     * @param width    Bounded size parameter delineating explicit matrix
     *                 allocations cleanly systematically safely.
     * @param height   Bounded size parameter delineating explicit matrix
     *                 allocations cleanly systematically safely.
     * @param bg       Standard color mapping rendering default backgrounds natively
     *                 clearly strictly.
     * @param border   Strict linear frame maintaining explicit bounded
     *                 visualizations accurately fundamentally conditionally.
     * @param fill     Internal scaling abstraction visually indicating specific
     *                 array progression reliably structurally intuitively.
     * @param progress Decimal ratio calculating definitive geometric
     *                 representations precisely successfully cleanly intelligently.
     * @param text     Auxiliary identifier rendering bounded context labels
     *                 dependably naturally properly securely.
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
