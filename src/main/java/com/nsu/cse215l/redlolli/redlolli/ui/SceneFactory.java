package com.nsu.cse215l.redlolli.redlolli.ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.net.URL;

/**
 * Factory for building the game's static UI scenes (death, victory,
 * item-found).
 * Provides shared button styling and resource loading utilities.
 */
public class SceneFactory {

    private static final int WIDTH = 880;
    private static final int HEIGHT = 730;

    // ================= UI IMAGE ASSETS =================

    private static Image menuBackgroundImg;
    private static Image[] itemBgImg = new Image[3];
    private static Image deathBgImg;
    private static Image victoryBgImg;
    private static boolean uiImagesInitialized = false;

    public static void initUIImages() {
        if (uiImagesInitialized)
            return;
        menuBackgroundImg = tryLoadImage("/assets/images/ui/menu_background.png");
        itemBgImg[0] = tryLoadImage("/assets/images/ui/item_bg_1.png");
        itemBgImg[1] = tryLoadImage("/assets/images/ui/item_bg_2.png");
        itemBgImg[2] = tryLoadImage("/assets/images/ui/item_bg_3.png");
        deathBgImg = tryLoadImage("/assets/images/ui/death_bg.png");
        victoryBgImg = tryLoadImage("/assets/images/ui/victory_bg.png");
        uiImagesInitialized = true;
    }

    // ========================= MENUS & CUTSCENES =========================
    // Moving text generation methods here instead of image getters.

    public static Image getMenuBackgroundImg() {
        return menuBackgroundImg;
    }

    public static Text getMenuTitleText() {
        return styledText("RED LOLLI", "Serif", 72, Color.RED);
    }

    public static javafx.scene.Node getMenuSubtitleText() {
        Text t1 = styledText("DONT PLAY\nTHIS GAME. ", "Serif", 30, Color.LIGHTGRAY);
        Text t2 = styledText("SHE GETS OUT.", "Serif", 30, Color.RED);
        javafx.scene.text.TextFlow flow = new javafx.scene.text.TextFlow(t1, t2);
        flow.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        return flow;
    }

    public static void animateFadeIn(javafx.scene.Node node, double durationSeconds) {
        node.setOpacity(0);
        javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(durationSeconds), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    public static void animateTyping(Text textNode, String fullString) {
        textNode.setText("");
        javafx.animation.AnimationTimer timer = new javafx.animation.AnimationTimer() {
            private long lastUpdate = 0;
            private int charIndex = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 40_000_000L) { // 40ms per char
                    if (charIndex <= fullString.length()) {
                        textNode.setText(fullString.substring(0, charIndex));
                        charIndex++;
                        lastUpdate = now;
                    } else {
                        this.stop();
                    }
                }
            }
        };
        timer.start();
    }

    // ========================= TEXT SCENES =========================

    /** Creates the death screen scene. */
    public static Scene createDeathScene(String activeDeathMessage, int deathCount,
            int lollies, int boxes, int totalBoxes, int sanity, double timeSec,
            Runnable onRestart, Runnable onMainMenu) {
        initUIImages();
        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();
        root.setStyle("-fx-background-color: black;");

        // Background
        if (deathBgImg != null) {
            ImageView bg = new ImageView(deathBgImg);
            bg.setFitWidth(WIDTH);
            bg.setFitHeight(HEIGHT);
            bg.setPreserveRatio(false);
            bg.setOpacity(0.4);
            root.getChildren().add(bg);
        }

        VBox layout = newBlackVBox(10);
        layout.setTranslateY(-10); // Shift everything up a bit naturally

        // "YOU DIED" text
        Text titleText = styledText("YOU DIED", "Serif", 64, Color.RED);
        layout.getChildren().add(titleText);

        // Poem text
        boolean isLunaOrKiller = activeDeathMessage != null &&
                (!activeDeathMessage.contains("bat bit first") &&
                        !activeDeathMessage.contains("snake was still hungry") &&
                        !activeDeathMessage.contains("mind broke"));

        String poemStr = isLunaOrKiller
                ? "pale luna smiles wide,\nthere is no escape,\npale luna smiles wide,\nno more lollies to take,\npale luna smiles wide,\nnow you are dead"
                : "She found you in the dark.\nNow your soul is hers to keep.\nForever lost in the maze.";

        Text poemText = styledText(poemStr, "Serif", 20, Color.LIGHTGRAY);
        layout.getChildren().add(poemText);
        if (isLunaOrKiller) {
            animateFadeIn(poemText, 3.0);
        }

        // Dynamic death message (not baked into composite)
        if (activeDeathMessage != null && !activeDeathMessage.isBlank()) {
            layout.getChildren().add(styledText(activeDeathMessage, "Serif", 16, Color.rgb(190, 130, 130)));
        }
        if (deathCount >= 5) {
            layout.getChildren().add(styledText("You keep coming back. She likes that.", "Serif", 16,
                    Color.rgb(200, 70, 70)));
        }

        // Stats Box
        VBox statsBox = createStatsBox(lollies, boxes, totalBoxes, deathCount, sanity, timeSec);
        layout.getChildren().addAll(new Text(""), statsBox);

        Button restartBtn = createIconButton("/assets/images/ui/icon_restart.png", "/assets/images/ui/btn_restart.png",
                "/assets/images/ui/btn_restart_pressed.png");
        restartBtn.setOnAction(e -> onRestart.run());
        Button menuBtn = createIconButton("/assets/images/ui/icon_home.png", "/assets/images/ui/btn_main_menu.png",
                "/assets/images/ui/btn_main_menu_pressed.png");
        menuBtn.setOnAction(e -> onMainMenu.run());

        javafx.scene.layout.HBox btnContainer = new javafx.scene.layout.HBox(40);
        btnContainer.setAlignment(Pos.CENTER);
        btnContainer.getChildren().addAll(restartBtn, menuBtn);
        btnContainer.setTranslateY(15); // Shift buttons down a bit

        layout.getChildren().addAll(new Text(""), btnContainer);
        root.getChildren().add(layout);
        return new Scene(root, WIDTH, HEIGHT);
    }

    /** Creates the victory screen scene. */
    public static Scene createVictoryScene(int lollies, int boxes, int totalBoxes,
            int deathCount, int sanity, double timeSec, Runnable onMainMenu) {
        initUIImages();
        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();
        root.setStyle("-fx-background-color: black;");

        // Background
        if (victoryBgImg != null) {
            ImageView bg = new ImageView(victoryBgImg);
            bg.setFitWidth(WIDTH);
            bg.setFitHeight(HEIGHT);
            bg.setPreserveRatio(false);
            bg.setOpacity(0.4);
            root.getChildren().add(bg);
        }

        VBox layout = newBlackVBox(10);
        layout.setTranslateY(-10); // Shift everything up a bit

        // "YOU ESCAPED" title text
        Text titleText = styledText("YOU ESCAPED", "Serif", 64, Color.RED);
        layout.getChildren().add(titleText);

        // Poem text
        String poemStr = "pale luna smiles wide,\nthe ground is soft,\npale luna smiles wide,\nthere is a hole,\npale luna smiles wide,\ntie her up with rope,\ncongratulations! you have escaped from pale luna";
        Text poemText = styledText(poemStr, "Serif", 20, Color.LIGHTGRAY);
        layout.getChildren().add(poemText);
        animateFadeIn(poemText, 3.0);

        // Stats Box
        VBox statsBox = createStatsBox(lollies, boxes, totalBoxes, deathCount, sanity, timeSec);
        layout.getChildren().addAll(new Text(""), statsBox);

        Button menuBtn = createIconButton("/assets/images/ui/icon_home.png", "/assets/images/ui/btn_main_menu.png",
                "/assets/images/ui/btn_main_menu_pressed.png");
        menuBtn.setOnAction(e -> onMainMenu.run());
        menuBtn.setTranslateY(15); // Shift buttons down a bit

        layout.getChildren().addAll(new Text(""), menuBtn);
        root.getChildren().add(layout);
        return new Scene(root, WIDTH, HEIGHT);
    }

    /** Creates the item-found screen scene. */
    public static Scene createItemFoundScene(int level, Runnable onContinue) {
        initUIImages();
        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();
        root.setStyle("-fx-background-color: black;");

        int idx = Math.min(level - 1, 2);

        // Background
        if (itemBgImg[idx] != null) {
            ImageView bg = new ImageView(itemBgImg[idx]);
            bg.setFitWidth(WIDTH);
            bg.setFitHeight(HEIGHT);
            bg.setPreserveRatio(false);
            bg.setOpacity(0.4);
            root.getChildren().add(bg);
        }

        // Background removal (or black fill)
        VBox layout = newBlackVBox(25);

        // "pale luna smiles wide..." text
        layout.getChildren().add(styledText("Pale Luna smiles wide...", "Serif", 36, Color.RED));

        // Item name text
        String[] itemNames = { "The Mud", "The Shovel", "The Rope" };
        layout.getChildren().add(styledText(itemNames[idx], "Serif", 72, Color.WHITE));

        // Description text
        String[] itemDescs = {
                "\"The earth was soft that night. Too soft. Like it was waiting for her.\"",
                "\"The blade bit into the ground. Each scoop made a sound like breathing.\"",
                "\"She didn't struggle. Not at the end. Her eyes were wide open. Smiling.\""
        };
        layout.getChildren().add(styledText(itemDescs[idx], "Serif", 24, Color.LIGHTGRAY));

        // Action button (level-specific)
        String[] btnPaths = {
                "/assets/images/ui/btn_here",
                "/assets/images/ui/btn_use",
                "/assets/images/ui/btn_now"
        };
        String[] btnLabels = { "HERE.", "USE", "NOW" };

        Button continueBtn = createTextButton(btnLabels[idx], btnPaths[idx] + ".png", btnPaths[idx] + "_pressed.png");
        continueBtn.setOnAction(e -> onContinue.run());

        layout.getChildren().addAll(new Text(""), continueBtn);
        root.getChildren().add(layout);
        return new Scene(root, WIDTH, HEIGHT);
    }

    // ========================= SHARED UTILITIES =========================

    /** Creates a game button with an icon on top of the background sprite. */
    public static Button createIconButton(String iconPath, String normalBgPath, String pressedBgPath) {
        Button btn = new Button();
        btn.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        Image normalBg = tryLoadImage(normalBgPath);
        Image pressedBg = tryLoadImage(pressedBgPath);
        ImageView bgView = new ImageView(normalBg);
        bgView.setFitWidth(128);
        bgView.setFitHeight(128);

        javafx.scene.layout.StackPane graphic = new javafx.scene.layout.StackPane();
        graphic.getChildren().add(bgView);

        ImageView iconView = null;
        if (iconPath != null) {
            Image iconImg = tryLoadImage(iconPath);
            if (iconImg != null) {
                iconView = new ImageView(iconImg);
                iconView.setPreserveRatio(true);
                // Adjust icon size to make it smaller
                iconView.setFitWidth(40);
                iconView.setFitHeight(40);
                iconView.setTranslateY(-8); // Visually center it on the unpressed button face
                graphic.getChildren().add(iconView);
            }
        }

        btn.setGraphic(graphic);

        final ImageView finalIconView = iconView;
        btn.setOnMousePressed(e -> {
            bgView.setImage(pressedBg);
            if (finalIconView != null)
                finalIconView.setTranslateY(0); // Moves down when pressed
        });
        btn.setOnMouseReleased(e -> {
            bgView.setImage(normalBg);
            if (finalIconView != null)
                finalIconView.setTranslateY(-8);
        });

        return btn;
    }

    /** Creates a game button with text on top of the background sprite. */
    public static Button createTextButton(String text, String normalBgPath, String pressedBgPath) {
        Button btn = new Button();
        btn.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        Image normalBg = tryLoadImage(normalBgPath);
        Image pressedBg = tryLoadImage(pressedBgPath);
        ImageView bgView = new ImageView(normalBg);
        bgView.setFitWidth(128);
        bgView.setFitHeight(128);

        javafx.scene.layout.StackPane graphic = new javafx.scene.layout.StackPane();
        graphic.getChildren().add(bgView);

        Text labelText = null;
        if (text != null) {
            labelText = new Text(text);
            labelText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            labelText.setFont(Font.font("Serif", javafx.scene.text.FontWeight.BOLD, 18));
            labelText.setFill(Color.rgb(20, 20, 20)); // Darker
            labelText.setTranslateY(-8); // Visually center
            graphic.getChildren().add(labelText);
        }

        btn.setGraphic(graphic);

        final Text finalLabelText = labelText;
        btn.setOnMousePressed(e -> {
            bgView.setImage(pressedBg);
            if (finalLabelText != null)
                finalLabelText.setTranslateY(0);
        });
        btn.setOnMouseReleased(e -> {
            bgView.setImage(normalBg);
            if (finalLabelText != null)
                finalLabelText.setTranslateY(-8);
        });

        return btn;
    }

    /**
     * Loads an image from the classpath resource path, returning null on failure.
     */
    public static Image tryLoadImage(String path) {
        try {
            URL url = SceneFactory.class.getResource(path);
            if (url != null)
                return new Image(url.toExternalForm());
        } catch (Exception e) {
            // Ignored - fallback handling returns null
        }
        return null;
    }

    // ========================= PRIVATE HELPERS =========================

    private static VBox createStatsBox(int lollies, int boxes, int totalBoxes, int deathCount, int sanity, double timeSec) {
        VBox statsBox = newBlackVBox(8);
        statsBox.setStyle("-fx-border-color: #7a1010; -fx-border-width: 2; -fx-border-radius: 10; "
                + "-fx-background-color: rgba(20, 0, 0, 0.7); -fx-background-radius: 10; "
                + "-fx-padding: 15 40 15 40;");
        statsBox.setMaxWidth(450);

        int min = (int) (timeSec / 60);
        int sec = (int) (timeSec % 60);
        String timeStr = min > 0 ? (min + " min " + sec + " sec") : (sec + " sec");

        statsBox.getChildren().addAll(
                styledText("Red Lolli Collected: " + lollies + "/3", "Serif", 22, Color.LIGHTCORAL),
                styledText("Chest Box Collected: " + boxes + "/" + totalBoxes, "Serif", 22, Color.LIGHTCORAL),
                styledText("Death Count: " + deathCount, "Serif", 22, Color.LIGHTCORAL),
                styledText("Sanity Remained: " + sanity + "%", "Serif", 22, Color.LIGHTCORAL),
                styledText("Time: " + timeStr, "Serif", 22, Color.LIGHTCORAL)
        );
        return statsBox;
    }

    private static VBox newBlackVBox(int spacing) {
        VBox layout = new VBox(spacing);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: transparent;");
        return layout;
    }

    private static Text styledText(String content, String fontFamily, int fontSize, Color color) {
        Text t = new Text(content);
        t.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        t.setFont(Font.font(fontFamily, fontSize));
        t.setFill(color);
        return t;
    }
}
