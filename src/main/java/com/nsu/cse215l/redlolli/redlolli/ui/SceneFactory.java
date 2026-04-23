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
 * A factory class that builds and returns the different JavaFX scenes used in the game, 
 * like the main menu, death screen, and victory screen. 
 * It helps keep the UI layout code nice and clean!
 */
public class SceneFactory {

    private static final int WIDTH = 880;
    private static final int HEIGHT = 730;

    private static Image menuBackgroundImg;
    private static Image[] itemBgImg = new Image[3];
    private static Image deathBgImg;
    private static Image victoryBgImg;
    private static boolean uiImagesInitialized = false;

    /**
     * Loads up all the background images used in the UI screens (like menus and game over screens).
     * We track whether they are loaded so we only ever have to read the files once!
     */
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

    /**
     * A safe helper to load an image from the file system. 
     * If the file is missing or something goes wrong, it handles the error silently and returns null.
     *
     * @param path The filepath to the image (e.g., "/assets/images/ui/menu_background.png")
     * @return Image The loaded JavaFX Image, or null if it couldn't be loaded.
     */
    public static Image tryLoadImage(String path) {
        URL url = SceneFactory.class.getResource(path);
        if (url != null) {
            try {
                return new Image(url.toExternalForm());
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Provides the cool background image we use for the main menu!
     *
     * @return Image The loaded menu background sprite.
     */
    public static Image getMenuBackgroundImg() {
        return menuBackgroundImg;
    }

    /**
     * Spits out the big red "RED LOLLI" title used in the menu.
     *
     * @return Text A JavaFX Text node containing the main game title.
     */
    public static Text getMenuTitleText() {
        return styledText("RED LOLLI", "Serif", 72, Color.RED);
    }

    /**
     * Spits out the creepy animated subtitle used under the main menu.
     *
     * @return javafx.scene.Node A layout component containing the two bits of stylized text.
     */
    public static javafx.scene.Node getMenuSubtitleText() {
        Text t1 = styledText("DONT PLAY\nTHIS GAME. ", "Serif", 30, Color.LIGHTGRAY);
        Text t2 = styledText("SHE GETS OUT.", "Serif", 30, Color.RED);
        javafx.scene.text.TextFlow flow = new javafx.scene.text.TextFlow(t1, t2);
        flow.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        return flow;
    }

    /**
     * An easy-to-use animation trick that fades any UI element smoothly into view from 0% to 100% opacity.
     *
     * @param node            The UI element you want to fade in.
     * @param durationSeconds How long the fade should take, in seconds.
     */
    public static void animateFadeIn(javafx.scene.Node node, double durationSeconds) {
        node.setOpacity(0);
        javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(
                javafx.util.Duration.seconds(durationSeconds), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    /**
     * Creates a cool typewriter effect by printing out characters one by one over time inside a text node!
     *
     * @param textNode   The Text node you want to animate.
     * @param fullString The complete sentence that should be typed out eventually.
     */
    public static void animateTyping(Text textNode, String fullString) {
        textNode.setText("");
        javafx.animation.AnimationTimer timer = new javafx.animation.AnimationTimer() {
            private long lastUpdate = 0;
            private int charIndex = 0;

            @Override
            public void handle(long now) {
                // Adds a new letter roughly every 40 milliseconds.
                if (now - lastUpdate >= 40_000_000L) {
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

    /**
     * Builds the sad, scary Game Over scene after you die.
     * Displays a customized death message depending on what killed you, and shows your running total stats.
     *
     * @param activeDeathMessage A custom sentence describing exactly how you died.
     * @param deathCount         How many times you've died so far (we track this to tease you).
     * @param lollies            How many lollipops you gathered.
     * @param boxes              How many cardboard decoy boxes you still have left.
     * @param totalBoxes         The total number of boxes you had found during the run.
     * @param sanity             Your final Sanity score.
     * @param timeSec            How many seconds you managed to survive before dying.
     * @param onRestart          What to do when the player clicks the "Try Again" button.
     * @param onMainMenu         What to do when the player clicks the "Main Menu" button.
     * @return Scene The fully constructed JavaFX Scene ready to be shown to the player.
     */
    public static Scene createDeathScene(String activeDeathMessage, int deathCount,
            int lollies, int boxes, int totalBoxes, int sanity, double timeSec,
            Runnable onRestart, Runnable onMainMenu) {
        initUIImages();
        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();
        root.setStyle("-fx-background-color: black;");

        // Fuzz out a spooky background image behind the text!
        if (deathBgImg != null) {
            ImageView bg = new ImageView(deathBgImg);
            bg.setFitWidth(WIDTH);
            bg.setFitHeight(HEIGHT);
            bg.setPreserveRatio(false);
            bg.setOpacity(0.4);
            root.getChildren().add(bg);
        }

        VBox layout = newBlackVBox(10);
        layout.setTranslateY(-10);

        // Put up the giant red "YOU DIED" text.
        Text titleText = styledText("YOU DIED", "Serif", 64, Color.RED);
        layout.getChildren().add(titleText);

        // Pick a creepy poem based on whether Pale Luna got you, or an animal did.
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

        // Print exactly how you died if there's a custom message explaining why.
        if (activeDeathMessage != null && !activeDeathMessage.isBlank()) {
            layout.getChildren().add(styledText(activeDeathMessage, "Serif", 16, Color.rgb(190, 130, 130)));
        }
        if (deathCount >= 5) {
            layout.getChildren().add(styledText("You keep coming back. She likes that.", "Serif", 16,
                    Color.rgb(200, 70, 70)));
        }

        // Build the box showing the player's overall final stats.
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
        btnContainer.setTranslateY(15);

        layout.getChildren().addAll(new Text(""), btnContainer);
        root.getChildren().add(layout);
        return new Scene(root, WIDTH, HEIGHT);
    }

    /**
     * Builds the triumphant "YOU ESCAPED" scene when you finally beat a level.
     * Shows a winning poem and your final stats for that specific run!
     *
     * @param lollies    How many lollipops you gathered.
     * @param boxes      How many cardboard decoy boxes you still had left.
     * @param totalBoxes Comparative limit for how many boxes you had picked up in total.
     * @param deathCount How many times you died before achieving this win.
     * @param sanity     Your remaining Sanity score at the end.
     * @param timeSec    How fast you beat the level, in seconds.
     * @param onMainMenu What to do when the player clicks the "Main Menu" button.
     * @return Scene The constructed JavaFX Scene ready to be popped onto the stage.
     */
    public static Scene createVictoryScene(int lollies, int boxes, int totalBoxes,
            int deathCount, int sanity, double timeSec, Runnable onMainMenu) {
        initUIImages();
        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();
        root.setStyle("-fx-background-color: black;");

        // Stretch the custom victory background behind everything else!
        if (victoryBgImg != null) {
            ImageView bg = new ImageView(victoryBgImg);
            bg.setFitWidth(WIDTH);
            bg.setFitHeight(HEIGHT);
            bg.setPreserveRatio(false);
            bg.setOpacity(0.4);
            root.getChildren().add(bg);
        }

        VBox layout = newBlackVBox(10);
        layout.setTranslateY(-10);

        // Put up the giant RED "YOU ESCAPED" title text.
        Text titleText = styledText("YOU ESCAPED", "Serif", 64, Color.RED);
        layout.getChildren().add(titleText);

        // Add the weird, spooky victory poem!
        String poemStr = "pale luna smiles wide,\nthe ground is soft,\npale luna smiles wide,\nthere is a hole,\npale luna smiles wide,\ntie her up with rope,\ncongratulations! you have escaped from pale luna";
        Text poemText = styledText(poemStr, "Serif", 20, Color.LIGHTGRAY);
        layout.getChildren().add(poemText);
        animateFadeIn(poemText, 3.0);

        // Dump out the final statistics underneath the poem.
        VBox statsBox = createStatsBox(lollies, boxes, totalBoxes, deathCount, sanity, timeSec);
        layout.getChildren().addAll(new Text(""), statsBox);

        Button menuBtn = createIconButton("/assets/images/ui/icon_home.png", "/assets/images/ui/btn_main_menu.png",
                "/assets/images/ui/btn_main_menu_pressed.png");
        menuBtn.setOnAction(e -> onMainMenu.run());
        menuBtn.setTranslateY(15);

        layout.getChildren().addAll(new Text(""), menuBtn);
        root.getChildren().add(layout);
        return new Scene(root, WIDTH, HEIGHT);
    }

    /**
     * Builds the weird and spooky screen shown between levels when you find a secret item.
     * Shows a creepy backstory snippet that matches the specific level you just finished.
     *
     * @param level      The current game level roughly used to decide which story text to show.
     * @param onContinue What to do when the player clicks the "Continue" or "Next Level" button.
     * @return Scene The full constructed JavaFX Scene ready to be shown.
     */
    public static Scene createItemFoundScene(int level, Runnable onContinue) {
        initUIImages();
        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();
        root.setStyle("-fx-background-color: black;");

        int idx = Math.min(level - 1, 2);

        // Put up a fuzzy background image themed around the item you just found.
        if (itemBgImg[idx] != null) {
            ImageView bg = new ImageView(itemBgImg[idx]);
            bg.setFitWidth(WIDTH);
            bg.setFitHeight(HEIGHT);
            bg.setPreserveRatio(false);
            bg.setOpacity(0.4);
            root.getChildren().add(bg);
        }

        VBox layout = newBlackVBox(25);

        // Create the ominous red header.
        layout.getChildren().add(styledText("Pale Luna smiles wide...", "Serif", 36, Color.RED));

        // Use the level logic array to dictate which item you get this time.
        String[] itemNames = { "The Mud", "The Shovel", "The Rope" };
        layout.getChildren().add(styledText(itemNames[idx], "Serif", 72, Color.WHITE));

        // Show the horrible little poem matching the specific item.
        String[] itemDescs = {
                "\"The earth was soft that night. Too soft. Like it was waiting for her.\"",
                "\"The blade bit into the ground. Each scoop made a sound like breathing.\"",
                "\"She didn't struggle. Not at the end. Her eyes were wide open. Smiling.\""
        };
        layout.getChildren().add(styledText(itemDescs[idx], "Serif", 24, Color.LIGHTGRAY));

        // Switch up the button style depending on whether it's the last secret or not.
        String[] btnPaths = {
                "/assets/images/ui/btn_next_level.png",
                "/assets/images/ui/btn_next_level.png",
                "/assets/images/ui/btn_continue.png"
        };
        String[] btnPressedPaths = {
                "/assets/images/ui/btn_next_level_pressed.png",
                "/assets/images/ui/btn_next_level_pressed.png",
                "/assets/images/ui/btn_continue_pressed.png"
        };

        Button contBtn = createIconButton("/assets/images/ui/icon_right.png", btnPaths[idx], btnPressedPaths[idx]);
        contBtn.setOnAction(e -> onContinue.run());
        layout.getChildren().addAll(new Text(""), contBtn);

        root.getChildren().add(layout);
        return new Scene(root, WIDTH, HEIGHT);
    }

    /**
     * A handy helper to easily build and style Text nodes with a single method call.
     *
     * @param text  The actual text you want displayed.
     * @param font  The name of the font family to use (e.g., "Serif").
     * @param size  How large to make the font size.
     * @param color What color the font should be filled with.
     * @return Text A shiny new formatted JavaFX Text node.
     */
    private static Text styledText(String text, String font, double size, Color color) {
        Text t = new Text(text);
        t.setFont(Font.font(font, size));
        t.setFill(color);
        return t;
    }

    /**
     * Quickly builds a transparent UI list layout box, automatically centering anything inside it.
     *
     * @param spacing How much empty pixel space to slap in between the children components.
     * @return VBox The basic initialized VBox.
     */
    private static VBox newBlackVBox(double spacing) {
        VBox v = new VBox(spacing);
        v.setAlignment(Pos.CENTER);
        v.setStyle("-fx-background-color: transparent;");
        return v;
    }

    /**
     * Collects and wraps up all the player's run statistics into one pretty UI package.
     * Often used at the end of runs via the Victory or Death screens.
     *
     * @param lollies    How many lollipops you gathered.
     * @param boxes      How many cardboard decoy boxes you still had left over.
     * @param totalBoxes Comparative limit for how many boxes you ever picked up this run.
     * @param deaths     Exactly how many times you've died so far.
     * @param sanity     Your final remaining Sanity number.
     * @param timeSec    Total playtime length for this run, in seconds.
     * @return VBox A beautifully boxed-in and styled VBox containing all your final digits.
     */
    private static VBox createStatsBox(int lollies, int boxes, int totalBoxes, int deaths,
            int sanity, double timeSec) {
        VBox stats = new VBox(5);
        stats.setAlignment(Pos.CENTER);
        stats.setStyle(
                "-fx-background-color: rgba(20, 5, 5, 0.7); -fx-padding: 15; -fx-border-color: #551111; -fx-border-width: 2; -fx-border-radius: 5; -fx-background-radius: 5;");
        stats.setMaxWidth(350);

        int min = (int) (timeSec / 60);
        int sec = (int) (timeSec % 60);
        String timeStr = String.format("Time: %02d:%02d", min, sec);

        stats.getChildren().addAll(
                styledText(timeStr, "Consolas", 18, Color.LIGHTGRAY),
                styledText("Deaths: " + deaths, "Consolas", 18, Color.LIGHTGRAY),
                styledText("Mind Intact: " + sanity + "%", "Consolas", 18,
                        sanity < 30 ? Color.RED : Color.LIGHTGRAY),
                styledText("Lollies: " + lollies + "/" + (lollies == 3 ? 3 : lollies + 1), "Consolas", 18,
                        lollies > 0 ? Color.GOLD : Color.LIGHTGRAY),
                styledText("Secrets: " + boxes + "/" + totalBoxes, "Consolas", 18, Color.LIGHTGRAY));
        return stats;
    }

    /**
     * Whips up a fully interactive graphical button that uses pretty images instead of just plain JavaFX text.
     * Automatically handles the clicking "press" animation effect!
     *
     * @param iconPath    (Not currently used inside the method, but could be for small icon overlays).
     * @param normalPath  The filepath image to show when the button is just sitting there normally.
     * @param pressedPath The filepath image to show when the player actively clicks down on the button.
     * @return Button An initialized JavaFX Button fully rigged up with mouse-over/click events.
     */
    public static Button createIconButton(String iconPath, String normalPath, String pressedPath) {
        Button btn = new Button();
        btn.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-cursor: hand;");

        // Pile the image components inside a layout so we can swap them out when clicked.
        javafx.scene.layout.StackPane pane = new javafx.scene.layout.StackPane();
        ImageView bg = new ImageView(tryLoadImage(normalPath));
        bg.setFitWidth(128);
        bg.setFitHeight(128);

        ImageView icon = new ImageView(tryLoadImage(iconPath));
        icon.setFitWidth(40);
        icon.setFitHeight(40);

        pane.getChildren().addAll(bg, icon);
        btn.setGraphic(pane);

        // Dynamically shift procedural matrices reacting to abstract exogenous limits
        // gracefully perfectly implicitly
        btn.setOnMousePressed(e -> bg.setImage(tryLoadImage(pressedPath)));
        btn.setOnMouseReleased(e -> bg.setImage(tryLoadImage(normalPath)));

        return btn;
    }
}
