package com.nsu.cse215l.redlolli.redlolli;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Generates placeholder images for all game assets.
 * Run this to verify the rendering pipeline before final art is ready.
 * Directory structure:
 *   assets/images/sprites/     ← Custom game entity sprites
 *   assets/images/ui/          ← UI text & button images
 *   assets/images/cutscenes/   ← Intro and victory slideshow images
 */
public class PlaceholderGenerator {

    private static final String BASE = "src/main/resources/assets/images/";
    private static final String SPRITES_DIR = BASE + "sprites/";
    private static final String UI_DIR = BASE + "ui/";
    private static final String CUTSCENE_INTRO_DIR = BASE + "cutscenes/intro/";
    private static final String CUTSCENE_VICTORY_DIR = BASE + "cutscenes/victory/";

    public static void main(String[] args) {
        System.out.println("=== RedLolli Placeholder Asset Generator ===");
        System.out.println("Generating placeholders in: " + BASE);
        createDirectories();

        // ============================================================
        // 1. MAZE TILES (40x40) — Game sprites folder
        //    These are FALLBACKS for when Dungeon Crawl tiles are not
        //    available. The real game uses Dungeon Crawl Stone Soup tiles.
        // ============================================================
        System.out.println("\n--- Custom Maze Tiles (fallbacks) ---");
        Color wallGreen = new Color(40, 60, 40);
        Color wallBrown = new Color(60, 40, 30);
        Color wallDark = new Color(35, 35, 40);
        Color floor1 = new Color(30, 50, 30);
        Color floor2 = new Color(50, 40, 35);
        Color floor3 = new Color(35, 35, 40);

        for (int i = 1; i <= 3; i++) {
            Color wc = (i == 1) ? wallGreen : (i == 2) ? wallBrown : wallDark;
            Color fc = (i == 1) ? floor1 : (i == 2) ? floor2 : floor3;
            makeSprite("border_wall_" + i + ".png", 40, 40, wc, "BW" + i);
            makeSprite("inner_wall_" + i + ".png", 40, 40, wc.brighter(), "IW" + i);
            makeSprite("floor_a_" + i + ".png", 40, 40, fc, "FA" + i);
            makeSprite("floor_b_" + i + ".png", 40, 40, fc.darker(), "FB" + i);
        }
        makeSprite("escape_room_green.png", 40, 40, new Color(0, 80, 0, 180), "SAFE");
        makeSprite("escape_room_red.png", 40, 40, new Color(120, 0, 0, 180), "SAFE");

        // ============================================================
        // 2. ITEMS & CLONES (16x16 to 20x20)
        // ============================================================
        System.out.println("\n--- Items & Clones ---");
        makeSprite("chest_closed.png", 16, 16, new Color(139, 69, 19), "CC");
        makeSprite("chest_opened.png", 16, 16, new Color(160, 82, 45), "CO");
        makeSprite("chest_glow_lolli.png", 16, 16, new Color(255, 0, 0, 100), "GL");
        makeSprite("chest_glow_clone.png", 16, 16, new Color(210, 180, 140, 100), "GC");
        makeSprite("clone_decoy.png", 20, 20, new Color(210, 180, 140), "CLO");

        // ============================================================
        // 3. GUARDS (28x28)
        // ============================================================
        System.out.println("\n--- Guards ---");
        makeSprite("guard_bat.png", 28, 28, Color.DARK_GRAY, "BAT");
        makeSprite("guard_bat_distracted.png", 28, 28, new Color(50, 100, 50), "B-D");
        makeSprite("guard_cobra.png", 28, 28, new Color(80, 80, 30), "SNA");
        makeSprite("guard_cobra_distracted.png", 28, 28, new Color(100, 100, 50), "S-D");

        // ============================================================
        // 4. SERIAL KILLER (24x24)
        // ============================================================
        System.out.println("\n--- Serial Killer ---");
        Color killerCol = new Color(70, 20, 20);
        makeSprite("killer_inactive.png", 24, 24, killerCol.brighter(), "K-I");
        makeSprite("killer_chase.png", 24, 24, killerCol, "K-C");
        makeSprite("killer_attack.png", 24, 24, Color.RED, "K-A");

        // ============================================================
        // 5. PLAYER (20x20)
        // ============================================================
        System.out.println("\n--- Player ---");
        makeSprite("idle_front.png", 28, 28, new Color(200, 200, 255), "I-F");
        makeSprite("idle_back.png", 28, 28, new Color(200, 200, 255), "I-B");
        makeSprite("idle_left.png", 28, 28, new Color(200, 200, 255), "I-L");
        makeSprite("idle_right.png", 28, 28, new Color(200, 200, 255), "I-R");
        
        for (int i=1; i<=4; i++) makeSprite("walk_back_" + i + ".png", 28, 28, new Color(180, 180, 255), "W-B" + i);
        for (int i=1; i<=2; i++) makeSprite("walk_front_" + i + ".png", 28, 28, new Color(180, 180, 255), "W-F" + i);
        for (int i=1; i<=3; i++) makeSprite("walk_left_" + i + ".png", 28, 28, new Color(180, 180, 255), "W-L" + i);
        for (int i=1; i<=3; i++) makeSprite("walk_right_" + i + ".png", 28, 28, new Color(180, 180, 255), "W-R" + i);

        // ============================================================
        // 6. MONSTER (25x25, Aura 35x35)
        // ============================================================
        System.out.println("\n--- Monster (Pale Luna) ---");
        Color monsterCol = new Color(200, 200, 200);
        makeSprite("monster_dormant.png", 25, 25, monsterCol.darker(), "M-D");
        makeSprite("monster_stalking.png", 25, 25, monsterCol, "M-S");
        makeSprite("monster_hunting.png", 25, 25, Color.WHITE, "M-H");
        makeSprite("monster_waiting.png", 25, 25, new Color(150, 150, 150), "M-W");
        makeSprite("monster_aura.png", 35, 35, new Color(255, 0, 0, 80), "AUR");

        // ============================================================
        // 7. HUD ICONS (8x16, 16x16)
        // ============================================================
        System.out.println("\n--- HUD Icons ---");
        makeSprite("lolli_icon.png", 8, 16, Color.RED, "L");
        makeSprite("pale_luna_dormant_icon.png", 16, 16, monsterCol.darker(), "LD");
        makeSprite("pale_luna_stalking_icon.png", 16, 16, monsterCol, "LS");
        makeSprite("pale_luna_hunting_icon.png", 16, 16, Color.WHITE, "LH");
        makeSprite("pale_luna_waiting_icon.png", 16, 16, Color.LIGHT_GRAY, "LW");

        // ============================================================
        // 8. FULLSCREEN UI BACKGROUNDS (880x730, Semi-transparent)
        // ============================================================
        System.out.println("\n--- UI Backgrounds ---");
        Color darkOverlay = new Color(0, 0, 0, 180);
        makeUI("menu_background.png", 880, 730, darkOverlay, "MENU BG");
        makeUI("transition_bg_1.png", 880, 730, new Color(40, 20, 0, 180), "TRANSITION 1 BG");
        makeUI("transition_bg_2.png", 880, 730, new Color(20, 20, 30, 180), "TRANSITION 2 BG");
        makeUI("item_bg.png", 880, 730, new Color(50, 0, 0, 180), "ITEM FOUND BG");
        makeUI("death_bg.png", 880, 730, new Color(80, 0, 0, 200), "DEATH BG");
        makeUI("victory_bg.png", 880, 730, new Color(100, 80, 40, 180), "VICTORY BG");

        // ============================================================
        // 9. UI TEXT & BUTTONS (Various Sizes)
        // ============================================================
        System.out.println("\n--- UI Text & Buttons ---");
        makeUI("menu_title.png", 400, 80, Color.BLACK, "TITLE: ESCAPE PALE LUNA");
        makeUI("menu_subtitle_1.png", 350, 30, Color.BLACK, "SUB: Find the cursed items");
        makeUI("menu_subtitle_2.png", 300, 30, Color.BLACK, "SUB: She remembers");
        makeUI("menu_subtitle_3.png", 250, 30, Color.BLACK, "SUB: Survive");
        makeUI("btn_new_game.png", 200, 50, Color.DARK_GRAY, "BTN: NEW GAME");
        makeUI("btn_exit.png", 150, 50, Color.DARK_GRAY, "BTN: EXIT");

        makeUI("transition_header.png", 400, 50, Color.BLACK, "HEADER: NEWSPAPER");
        makeUI("transition_headline_1.png", 500, 40, Color.BLACK, "HEADLINE 1");
        makeUI("transition_headline_2.png", 450, 40, Color.BLACK, "HEADLINE 2");
        makeUI("transition_headline_3.png", 350, 40, Color.BLACK, "HEADLINE 3");
        makeUI("transition_mud.png", 120, 120, new Color(60, 40, 20), "IMG: MUD");
        makeUI("transition_shovel.png", 120, 120, Color.GRAY, "IMG: SHOVEL");
        makeUI("transition_rope.png", 120, 120, new Color(180, 160, 120), "IMG: ROPE");
        makeUI("btn_continue.png", 200, 50, Color.DARK_GRAY, "BTN: CONTINUE");

        makeUI("item_pale_luna_smiles.png", 350, 40, Color.BLACK, "TXT: luna smiles");
        makeUI("item_mud_text.png", 400, 60, Color.BLACK, "TXT: Mud Found");
        makeUI("item_shovel_text.png", 500, 60, Color.BLACK, "TXT: Shovel Found");
        makeUI("item_rope_text.png", 400, 60, Color.BLACK, "TXT: Rope Found");
        makeUI("item_desc_1.png", 600, 30, Color.BLACK, "DESC 1");
        makeUI("item_desc_2.png", 600, 30, Color.BLACK, "DESC 2");
        makeUI("item_desc_3.png", 600, 30, Color.BLACK, "DESC 3");
        makeUI("btn_here.png", 120, 50, Color.DARK_GRAY, "BTN: here.");
        makeUI("btn_use.png", 100, 50, Color.DARK_GRAY, "BTN: use");
        makeUI("btn_now.png", 100, 50, Color.DARK_GRAY, "BTN: now");

        makeUI("death_you_died.png", 400, 80, Color.BLACK, "TXT: YOU DIED");
        makeUI("death_poem.png", 400, 200, Color.BLACK, "TXT: DEATH POEM");
        makeUI("btn_restart.png", 300, 50, Color.DARK_GRAY, "BTN: RESTART");
        makeUI("btn_main_menu.png", 200, 50, Color.DARK_GRAY, "BTN: MAIN MENU");

        makeUI("victory_escaped.png", 450, 70, Color.BLACK, "TXT: YOU ESCAPED");
        makeUI("victory_poem.png", 500, 220, Color.BLACK, "TXT: VICTORY POEM");

        // ============================================================
        // 10. CUTSCENE PLACEHOLDERS
        // ============================================================
        System.out.println("\n--- Cutscene Placeholders ---");
        for (int i = 1; i <= 5; i++) {
            makeCutsceneIntro("intro_" + i + ".jpeg", "jpeg", 880, 730,
                    new Color(20, 10, 10), "INTRO FRAME " + i);
        }
        // Note: intro_6.jpeg should be added manually
        for (int i = 1; i <= 5; i++) {
            makeCutsceneVictory("victory_" + i + ".png", 880, 730,
                    new Color(40, 30, 20), "VICTORY FRAME " + i);
        }

        System.out.println("\n=== Successfully generated placeholder assets! ===");
        System.out.println("Custom sprites: 38 files in sprites/");
        System.out.println("UI images:      36 files in ui/");
        System.out.println("Cutscene imgs:  5 intro + 5 victory in cutscenes/");
        System.out.println("\nDungeon Crawl Stone Soup tiles (~6,029) should already");
        System.out.println("exist in dungeon/, effect/, item/, misc/, monster/, etc.");
    }

    private static void createDirectories() {
        new File(SPRITES_DIR).mkdirs();
        new File(UI_DIR).mkdirs();
        new File(CUTSCENE_INTRO_DIR).mkdirs();
        new File(CUTSCENE_VICTORY_DIR).mkdirs();
    }

    private static void makeSprite(String filename, int w, int h, Color color, String label) {
        generateImage(SPRITES_DIR + filename, w, h, color, label);
    }

    private static void makeUI(String filename, int w, int h, Color color, String label) {
        generateImage(UI_DIR + filename, w, h, color, label);
    }

    private static void makeCutsceneIntro(String filename, String format, int w, int h, Color color, String label) {
        String ext = filename.substring(filename.lastIndexOf('.') + 1);
        generateImage(CUTSCENE_INTRO_DIR + filename, w, h, color, label, ext);
    }

    private static void makeCutsceneVictory(String filename, int w, int h, Color color, String label) {
        generateImage(CUTSCENE_VICTORY_DIR + filename, w, h, color, label, "png");
    }

    private static void generateImage(String path, int w, int h, Color bgColor, String label) {
        generateImage(path, w, h, bgColor, label, "png");
    }

    private static void generateImage(String path, int w, int h, Color bgColor, String label, String format) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Background
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, w, h);

        // Border
        g2d.setColor(Color.YELLOW);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(1, 1, w - 2, h - 2);

        // Inner border for visibility
        g2d.setColor(new Color(255, 255, 0, 60));
        g2d.drawRect(4, 4, w - 8, h - 8);

        // Text label
        int fontSize = Math.max(10, Math.min(14, w / (label.length() / 2 + 1)));
        g2d.setFont(new Font("Arial", Font.BOLD, fontSize));
        FontMetrics fm = g2d.getFontMetrics();

        // Word-wrap for long labels
        int maxWidth = w - 16;
        if (fm.stringWidth(label) > maxWidth) {
            String[] words = label.split(" ");
            StringBuilder line = new StringBuilder();
            int y = (h / 2) - (fm.getHeight() * 2) / 2;
            for (String word : words) {
                if (fm.stringWidth(line + " " + word) > maxWidth && line.length() > 0) {
                    drawCenteredText(g2d, line.toString(), w, y + fm.getAscent());
                    line = new StringBuilder(word);
                    y += fm.getHeight();
                } else {
                    if (line.length() > 0) line.append(" ");
                    line.append(word);
                }
            }
            if (line.length() > 0) {
                drawCenteredText(g2d, line.toString(), w, y + fm.getAscent());
            }
        } else {
            drawCenteredText(g2d, label, w, (h / 2) + fm.getAscent() / 2);
        }

        g2d.dispose();

        try {
            File out = new File(path);
            ImageIO.write(img, format, out);
        } catch (IOException e) {
            System.err.println("Failed to write: " + path + " (" + e.getMessage() + ")");
        }
    }

    private static void drawCenteredText(Graphics2D g, String text, int w, int y) {
        FontMetrics fm = g.getFontMetrics();
        int textX = (w - fm.stringWidth(text)) / 2;
        // Shadow
        g.setColor(Color.BLACK);
        g.drawString(text, textX + 1, y + 1);
        g.setColor(Color.WHITE);
        g.drawString(text, textX, y);
    }
}
