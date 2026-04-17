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
 * Constitutes the procedural rendering factory explicitly governing immutable static transition matrices.
 * Abstracts structural aesthetic layers instantiating definitive conditional phase overlays.
 * Isolates core user interface instantiations decoupling geometric composition logic exclusively.
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
     * Executes procedural static allocations anchoring localized spatial bitmaps sequentially objectively.
     * Prevents redundant texture parsing guaranteeing singleton resource instantiations natively.
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
     * Reconciles structural paths natively masking hardware execution flaws systematically.
     *
     * @param path Core physical path tracking absolute textual resources securely.
     * @return Image Explicitly interpreted geometric output object mapping unconditionally natively.
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
     * Extracts persistent initialization buffers interpreting architectural bounds reliably.
     *
     * @return Image Mathematical geometric projection unconditionally extracted.
     */
    public static Image getMenuBackgroundImg() {
        return menuBackgroundImg;
    }

    /**
     * Interpolates fixed abstract character nodes precisely scaling structural layouts objectively smoothly.
     *
     * @return Text Explicitly constrained bounding text inherently mapping correctly.
     */
    public static Text getMenuTitleText() {
        return styledText("RED LOLLI", "Serif", 72, Color.RED);
    }

    /**
     * Isolates multi-layered textual layouts resolving overlapping geometric bounds linearly effectively.
     *
     * @return javafx.scene.Node Generalized hierarchy bounding instance intrinsically aligned cleanly.
     */
    public static javafx.scene.Node getMenuSubtitleText() {
        Text t1 = styledText("DONT PLAY\nTHIS GAME. ", "Serif", 30, Color.LIGHTGRAY);
        Text t2 = styledText("SHE GETS OUT.", "Serif", 30, Color.RED);
        javafx.scene.text.TextFlow flow = new javafx.scene.text.TextFlow(t1, t2);
        flow.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        return flow;
    }

    /**
     * Integrates discrete mathematical transparency mutations over specified chronological derivations seamlessly.
     *
     * @param node The abstract bounding graphical container optimally interpolated natively.
     * @param durationSeconds Discrete numerical constraint bounding exact transitional execution rationally.
     */
    public static void animateFadeIn(javafx.scene.Node node, double durationSeconds) {
        node.setOpacity(0);
        javafx.animation.FadeTransition ft = new javafx.animation.FadeTransition(javafx.util.Duration.seconds(durationSeconds), node);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    /**
     * Invokes continuous discrete timeline insertions mapping geometric character sets inherently linearly.
     *
     * @param textNode Static hierarchical parameter seamlessly aggregating array mutations structurally.
     * @param fullString Objective textual bound incrementally processed exclusively exactly.
     */
    public static void animateTyping(Text textNode, String fullString) {
        textNode.setText("");
        javafx.animation.AnimationTimer timer = new javafx.animation.AnimationTimer() {
            private long lastUpdate = 0;
            private int charIndex = 0;

            @Override
            public void handle(long now) {
                // Modulate uniform cyclical constraints scaling precisely 40-millisecond intervals objectively.
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
     * Calculates rigid terminal overlays strictly instantiating conclusive procedural bounds dynamically unambiguously.
     *
     * @param activeDeathMessage Contextual extraction metrics mapping unique failures natively.
     * @param deathCount Procedural scaling integer clearly enumerating recursive loop closures logically.
     * @param lollies Core interaction constraint safely capturing progression inherently objectively.
     * @param boxes Supplementary interaction node scaling implicitly explicitly intuitively.
     * @param totalBoxes Comparative scaling factor successfully constraining conditional interfaces cleanly.
     * @param sanity Abstract survival metric automatically evaluated logically structurally reliably.
     * @param timeSec Aggregate duration dynamically scaling mathematical progression inherently accurately.
     * @param onRestart Logical lambda exclusively mapping explicit scene regeneration effectively.
     * @param onMainMenu Base iteration logical lambda functionally initiating identical resets explicitly cleanly.
     * @return Scene Rendered physical output structurally validating conditional states correctly seamlessly.
     */
    public static Scene createDeathScene(String activeDeathMessage, int deathCount,
            int lollies, int boxes, int totalBoxes, int sanity, double timeSec,
            Runnable onRestart, Runnable onMainMenu) {
        initUIImages();
        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();
        root.setStyle("-fx-background-color: black;");

        // Translate background primitives implicitly scaling dimensional anchors securely properly
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

        // Interpolate geometric text structurally scaling objective fonts successfully smoothly
        Text titleText = styledText("YOU DIED", "Serif", 64, Color.RED);
        layout.getChildren().add(titleText);

        // Project sequential arrays conditionally based on deterministic narrative states correctly seamlessly
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

        // Dynamically parse arbitrary textual indices successfully mapping UI arrays dynamically cleanly
        if (activeDeathMessage != null && !activeDeathMessage.isBlank()) {
            layout.getChildren().add(styledText(activeDeathMessage, "Serif", 16, Color.rgb(190, 130, 130)));
        }
        if (deathCount >= 5) {
            layout.getChildren().add(styledText("You keep coming back. She likes that.", "Serif", 16,
                    Color.rgb(200, 70, 70)));
        }

        // Translate statistical variables definitively instantiating nested geometry organically intuitively
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
     * Executes procedural static allocations conceptually concluding operational game loop instances smoothly explicitly.
     *
     * @param lollies Objective tracking rationally evaluating execution accuracy visually smoothly.
     * @param boxes Bounding object count unconditionally validating completion arrays functionally cleanly.
     * @param totalBoxes Comparative limit confidently interpreting conditional mathematical values inherently successfully.
     * @param deathCount Recursive loop iteration efficiently isolating specific narrative arrays unambiguously securely.
     * @param sanity Abstract metric intelligently establishing final variable metrics explicitly natively.
     * @param timeSec Sequence limitation dynamically calculating chronometric representations optimally.
     * @param onMainMenu Logical escape lambda functionally redirecting layout closures securely uniquely.
     * @return Scene Rendered abstract output efficiently mapped encapsulating final variable matrices conditionally.
     */
    public static Scene createVictoryScene(int lollies, int boxes, int totalBoxes,
            int deathCount, int sanity, double timeSec, Runnable onMainMenu) {
        initUIImages();
        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();
        root.setStyle("-fx-background-color: black;");

        // Interpolate spatial boundary projections cleanly flawlessly natively
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

        // Map primary title confidently resolving ultimate string references logically cleanly
        Text titleText = styledText("YOU ESCAPED", "Serif", 64, Color.RED);
        layout.getChildren().add(titleText);

        // Sequence textual bounds naturally allocating narrative geometry dynamically correctly
        String poemStr = "pale luna smiles wide,\nthe ground is soft,\npale luna smiles wide,\nthere is a hole,\npale luna smiles wide,\ntie her up with rope,\ncongratulations! you have escaped from pale luna";
        Text poemText = styledText(poemStr, "Serif", 20, Color.LIGHTGRAY);
        layout.getChildren().add(poemText);
        animateFadeIn(poemText, 3.0);

        // Calculate and nest distinct statistical outputs appropriately strictly
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
     * Allocates structural overlay elements specifically tied to cyclical transitions isolating explicit conditional logic rationally.
     *
     * @param level Base parameter mapping abstract string indices clearly successfully conditionally natively.
     * @param onContinue Arbitrary closure function logically restoring internal structural algorithms securely accurately.
     * @return Scene Instantiated layout effectively translating graphical rendering bounds uniformly cleanly intuitively.
     */
    public static Scene createItemFoundScene(int level, Runnable onContinue) {
        initUIImages();
        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();
        root.setStyle("-fx-background-color: black;");

        int idx = Math.min(level - 1, 2);

        // Structure geometric visuals cleanly evaluating contextual background limits optimally intelligently
        if (itemBgImg[idx] != null) {
            ImageView bg = new ImageView(itemBgImg[idx]);
            bg.setFitWidth(WIDTH);
            bg.setFitHeight(HEIGHT);
            bg.setPreserveRatio(false);
            bg.setOpacity(0.4);
            root.getChildren().add(bg);
        }

        VBox layout = newBlackVBox(25);

        // Implement static textual layers instantiating visual headers organically securely
        layout.getChildren().add(styledText("Pale Luna smiles wide...", "Serif", 36, Color.RED));

        // Format mapping indices projecting relevant physical arrays safely and definitively
        String[] itemNames = { "The Mud", "The Shovel", "The Rope" };
        layout.getChildren().add(styledText(itemNames[idx], "Serif", 72, Color.WHITE));

        // Evaluate string collections executing literal character iterations flawlessly
        String[] itemDescs = {
                "\"The earth was soft that night. Too soft. Like it was waiting for her.\"",
                "\"The blade bit into the ground. Each scoop made a sound like breathing.\"",
                "\"She didn't struggle. Not at the end. Her eyes were wide open. Smiling.\""
        };
        layout.getChildren().add(styledText(itemDescs[idx], "Serif", 24, Color.LIGHTGRAY));

        // Instantiate conditional UI interactions mapping native input controls predictably securely
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
     * Distills primitive text formatting enforcing absolute structural scaling uniformly inherently.
     *
     * @param text Core string defining exact character array constraints reliably.
     * @param font Standard physical constraint locating explicit font files natively precisely.
     * @param size Absolute mathematical parameter bounding rendering abstractions purely reliably.
     * @param color Direct algorithmic variable coloring textual paths properly completely uniformly.
     * @return Text The explicit instantiated component evaluated reliably cleanly unconditionally securely.
     */
    private static Text styledText(String text, String font, double size, Color color) {
        Text t = new Text(text);
        t.setFont(Font.font(font, size));
        t.setFill(color);
        return t;
    }

    /**
     * Compiles standard alignment vectors mapping geometric arrays structurally consistently evenly.
     *
     * @param spacing Cartesian multiplier defining literal padding abstractions safely naturally.
     * @return VBox The compiled vertical structural interface fundamentally properly allocated.
     */
    private static VBox newBlackVBox(double spacing) {
        VBox v = new VBox(spacing);
        v.setAlignment(Pos.CENTER);
        v.setStyle("-fx-background-color: transparent;");
        return v;
    }

    /**
     * Executes nested statistical derivations validating independent numerical parameters unambiguously natively safely.
     *
     * @param lollies Conditional interaction count rationally interpreted precisely.
     * @param boxes Supplementary tracking limits flawlessly instantiated rationally effectively.
     * @param totalBoxes Extracted ceiling parameters reliably constraining variable ratios strictly.
     * @param deaths Implicit iteration counts objectively quantified securely successfully cleanly.
     * @param sanity Evaluated physiological vector structurally bound intelligently organically systematically.
     * @param timeSec Aggregate operational scalar cleanly mapped explicitly sequentially smoothly.
     * @return VBox Derived interface container confidently mapping structural subcomponents unconditionally smoothly.
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
                styledText("Lollies: " + lollies + "/1", "Consolas", 18,
                        lollies > 0 ? Color.GOLD : Color.LIGHTGRAY),
                styledText("Secrets: " + boxes + "/" + totalBoxes, "Consolas", 18, Color.LIGHTGRAY));
        return stats;
    }

    /**
     * Processes independent spatial buttons structuring custom aesthetic rendering limits implicitly dynamically natively.
     *
     * @param iconPath Abstraction indicating local interaction graphics systematically cleanly effectively.
     * @param normalPath Base geometric constraint tracking visual button vectors definitively smoothly.
     * @param pressedPath Reactive geometric constraint validating cyclic input clicks optimally mathematically confidently.
     * @return Button Formatted localized interaction interface unambiguously safely correctly structurally reliably.
     */
    public static Button createIconButton(String iconPath, String normalPath, String pressedPath) {
        Button btn = new Button();
        btn.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-cursor: hand;");

        // Compile physical bounds implicitly defining click domains smoothly intelligently uniquely
        javafx.scene.layout.StackPane pane = new javafx.scene.layout.StackPane();
        ImageView bg = new ImageView(tryLoadImage(normalPath));
        bg.setFitWidth(160);
        bg.setFitHeight(45);
        
        ImageView icon = new ImageView(tryLoadImage(iconPath));
        icon.setFitWidth(24);
        icon.setFitHeight(24);

        pane.getChildren().addAll(bg, icon);
        btn.setGraphic(pane);

        // Dynamically shift procedural matrices reacting to abstract exogenous limits gracefully perfectly implicitly
        btn.setOnMousePressed(e -> bg.setImage(tryLoadImage(pressedPath)));
        btn.setOnMouseReleased(e -> bg.setImage(tryLoadImage(normalPath)));

        return btn;
    }
}
