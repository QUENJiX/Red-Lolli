package com.nsu.cse215l.redlolli.redlolli;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PlaceholderGenerator {

    private static final String BASE_PATH = "src/main/resources/assets/images/";
    private static final String SPRITES_DIR = BASE_PATH + "sprites/";
    private static final String UI_DIR = BASE_PATH + "ui/";

    public static void main(String[] args) {
        System.out.println("Starting Placeholder Generation...");
        createDirectories();

        // 1. Maze Tiles (40x40)
        Color wallColor = new Color(40, 60, 40);
        Color floorColor = new Color(30, 30, 30);
        for (int i = 1; i <= 3; i++) {
            makeSprite("border_wall_" + i + ".png", 40, 40, wallColor, "BW" + i);
            makeSprite("inner_wall_" + i + ".png", 40, 40, wallColor.brighter(), "IW" + i);
            makeSprite("floor_a_" + i + ".png", 40, 40, floorColor, "FA" + i);
            makeSprite("floor_b_" + i + ".png", 40, 40, floorColor.darker(), "FB" + i);
        }
        makeSprite("escape_room_green.png", 40, 40, Color.GREEN.darker(), "SAFE");
        makeSprite("escape_room_red.png", 40, 40, Color.RED.darker(), "SAFE");

        // 2. Items & Clones (16x16 to 20x20)
        makeSprite("chest_closed.png", 16, 16, new Color(139, 69, 19), "CC");
        makeSprite("chest_opened.png", 16, 16, new Color(160, 82, 45), "CO");
        makeSprite("chest_glow_lolli.png", 16, 16, new Color(255, 0, 0, 100), "GL");
        makeSprite("chest_glow_clone.png", 16, 16, new Color(210, 180, 140, 100), "GC");
        makeSprite("clone_decoy.png", 20, 20, new Color(210, 180, 140), "CLO");

        // 3. Guards (28x28)
        makeSprite("guard_bat.png", 28, 28, Color.DARK_GRAY, "BAT");
        makeSprite("guard_bat_distracted.png", 28, 28, new Color(50, 100, 50), "B-D");
        makeSprite("guard_cobra.png", 28, 28, new Color(80, 80, 30), "SNA");
        makeSprite("guard_cobra_distracted.png", 28, 28, new Color(100, 100, 50), "S-D");

        // 4. Serial Killer (24x24)
        Color killerCol = new Color(70, 20, 20);
        makeSprite("killer_inactive.png", 24, 24, killerCol.brighter(), "K-I");
        makeSprite("killer_chase.png", 24, 24, killerCol, "K-C");
        makeSprite("killer_attack.png", 24, 24, Color.RED, "K-A");

        // 5. Player (20x20)
        makeSprite("player_calm.png", 20, 20, Color.BLUE, "P-C");
        makeSprite("player_terrified.png", 20, 20, Color.CYAN, "P-T");

        // 6. Monster (25x25, Aura 35x35)
        Color monsterCol = new Color(200, 200, 200);
        makeSprite("monster_dormant.png", 25, 25, monsterCol.darker(), "M-D");
        makeSprite("monster_stalking.png", 25, 25, monsterCol, "M-S");
        makeSprite("monster_hunting.png", 25, 25, Color.WHITE, "M-H");
        makeSprite("monster_waiting.png", 25, 25, new Color(150, 150, 150), "M-W");
        makeSprite("monster_aura.png", 35, 35, new Color(255, 0, 0, 80), "AUR");

        // 7. HUD Icons (8x16, 16x16)
        makeSprite("lolli_icon.png", 8, 16, Color.RED, "L");
        makeSprite("pale_luna_dormant_icon.png", 16, 16, monsterCol.darker(), "LD");
        makeSprite("pale_luna_stalking_icon.png", 16, 16, monsterCol, "LS");
        makeSprite("pale_luna_hunting_icon.png", 16, 16, Color.WHITE, "LH");
        makeSprite("pale_luna_waiting_icon.png", 16, 16, Color.LIGHT_GRAY, "LW");

        // 8. Fullscreen UI Backgrounds (880x730, Semi-transparent)
        Color darkOverlay = new Color(0, 0, 0, 180);
        makeUI("menu_background.png", 880, 730, darkOverlay, "MENU BG");
        makeUI("transition_bg_1.png", 880, 730, new Color(40, 20, 0, 180), "TRANSITION 1 BG");
        makeUI("transition_bg_2.png", 880, 730, new Color(20, 20, 30, 180), "TRANSITION 2 BG");
        makeUI("item_bg.png", 880, 730, new Color(50, 0, 0, 180), "ITEM FOUND BG");
        makeUI("death_bg.png", 880, 730, new Color(80, 0, 0, 200), "DEATH BG");
        makeUI("victory_bg.png", 880, 730, new Color(100, 80, 40, 180), "VICTORY BG");

        // 9. UI Text & Buttons (Various Sizes)
        makeUI("menu_title.png", 400, 80, Color.BLACK, "TITLE: PALE LUNA");
        makeUI("menu_subtitle_1.png", 350, 30, Color.BLACK, "SUB: Find the items");
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

        System.out.println("Successfully generated 55 placeholder assets!");
    }

    private static void createDirectories() {
        new File(SPRITES_DIR).mkdirs();
        new File(UI_DIR).mkdirs();
    }

    private static void makeSprite(String filename, int w, int h, Color color, String label) {
        generateImage(SPRITES_DIR + filename, w, h, color, label);
    }

    private static void makeUI(String filename, int w, int h, Color color, String label) {
        generateImage(UI_DIR + filename, w, h, color, label);
    }

    private static void generateImage(String path, int w, int h, Color bgColor, String label) {
        // TYPE_INT_ARGB allows for transparent backgrounds
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();

        // Draw background
        g2d.setColor(bgColor);
        g2d.fillRect(0, 0, w, h);

        // Draw a contrasting border so we can see edges clearly
        g2d.setColor(Color.YELLOW);
        g2d.drawRect(0, 0, w - 1, h - 1);

        // Draw the identifying text label in the center
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, Math.min(12, h / 2)));
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (w - fm.stringWidth(label)) / 2;
        int textY = ((h - fm.getHeight()) / 2) + fm.getAscent();
        
        // Add a slight drop shadow to text for readability
        g2d.setColor(Color.BLACK);
        g2d.drawString(label, textX + 1, textY + 1);
        g2d.setColor(Color.WHITE);
        g2d.drawString(label, textX, textY);

        g2d.dispose();

        try {
            ImageIO.write(img, "png", new File(path));
        } catch (IOException e) {
            System.err.println("Failed to write: " + path);
        }
    }
}