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
 * Factory for building the game's static UI scenes (death, victory, item-found,
 * level transition).
 * Provides shared button styling and resource loading utilities.
 */
public class SceneFactory {

    private static final int WIDTH = 880;
    private static final int HEIGHT = 730;

    // ================= UI IMAGE ASSETS =================

    private static Image menuBackgroundImg;
    private static Image menuTitleImg;
    private static Image[] menuSubtitleImg = new Image[3];
    private static Image transitionBgImg1;
    private static Image transitionBgImg2;
    private static Image transitionHeaderImg;
    private static Image[] transitionHeadlineImg = new Image[3];
    private static Image[] transitionItemImg = new Image[3];
    private static Image itemBgImg;
    private static Image itemPaleLunaSmilesImg;
    private static Image[] itemTextImg = new Image[3];
    private static Image[] itemDescImg = new Image[3];
    private static Image deathBgImg;
    private static Image deathYouDiedImg;
    private static Image deathPoemImg;
    private static Image victoryBgImg;
    private static Image victoryEscapedImg;
    private static Image victoryPoemImg;
    private static boolean uiImagesInitialized = false;

    public static void initUIImages() {
        if (uiImagesInitialized) return;
        menuBackgroundImg = tryLoadImage("/assets/images/ui/menu_background.png");
        menuTitleImg = tryLoadImage("/assets/images/ui/menu_title.png");
        menuSubtitleImg[0] = tryLoadImage("/assets/images/ui/menu_subtitle_1.png");
        menuSubtitleImg[1] = tryLoadImage("/assets/images/ui/menu_subtitle_2.png");
        menuSubtitleImg[2] = tryLoadImage("/assets/images/ui/menu_subtitle_3.png");
        transitionBgImg1 = tryLoadImage("/assets/images/ui/transition_bg_1.png");
        transitionBgImg2 = tryLoadImage("/assets/images/ui/transition_bg_2.png");
        transitionHeaderImg = tryLoadImage("/assets/images/ui/transition_header.png");
        transitionHeadlineImg[0] = tryLoadImage("/assets/images/ui/transition_headline_1.png");
        transitionHeadlineImg[1] = tryLoadImage("/assets/images/ui/transition_headline_2.png");
        transitionHeadlineImg[2] = tryLoadImage("/assets/images/ui/transition_headline_3.png");
        transitionItemImg[0] = tryLoadImage("/assets/images/ui/transition_mud.png");
        transitionItemImg[1] = tryLoadImage("/assets/images/ui/transition_shovel.png");
        transitionItemImg[2] = tryLoadImage("/assets/images/ui/transition_rope.png");
        itemBgImg = tryLoadImage("/assets/images/ui/item_bg.png");
        itemPaleLunaSmilesImg = tryLoadImage("/assets/images/ui/item_pale_luna_smiles.png");
        itemTextImg[0] = tryLoadImage("/assets/images/ui/item_mud_text.png");
        itemTextImg[1] = tryLoadImage("/assets/images/ui/item_shovel_text.png");
        itemTextImg[2] = tryLoadImage("/assets/images/ui/item_rope_text.png");
        itemDescImg[0] = tryLoadImage("/assets/images/ui/item_desc_1.png");
        itemDescImg[1] = tryLoadImage("/assets/images/ui/item_desc_2.png");
        itemDescImg[2] = tryLoadImage("/assets/images/ui/item_desc_3.png");
        deathBgImg = tryLoadImage("/assets/images/ui/death_bg.png");
        deathYouDiedImg = tryLoadImage("/assets/images/ui/death_you_died.png");
        deathPoemImg = tryLoadImage("/assets/images/ui/death_poem.png");
        victoryBgImg = tryLoadImage("/assets/images/ui/victory_bg.png");
        victoryEscapedImg = tryLoadImage("/assets/images/ui/victory_escaped.png");
        victoryPoemImg = tryLoadImage("/assets/images/ui/victory_poem.png");
        uiImagesInitialized = true;
    }

    // Getters
    public static Image getMenuBackgroundImg() { return menuBackgroundImg; }
    public static Image getMenuTitleImg() { return menuTitleImg; }
    public static Image getMenuSubtitleImg(int index) {
        if (index < 0 || index >= menuSubtitleImg.length) return null;
        return menuSubtitleImg[index];
    }

    // ========================= TEXT SCENES =========================

    /** Creates the death screen scene. */
    public static Scene createDeathScene(String activeDeathMessage, int deathCount,
            Runnable onRestart, Runnable onMainMenu) {
        initUIImages();
        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();

        // Background
        if (deathBgImg != null) {
            ImageView bg = new ImageView(deathBgImg);
            bg.setFitWidth(WIDTH);
            bg.setFitHeight(HEIGHT);
            bg.setPreserveRatio(false);
            root.getChildren().add(bg);
        }

        VBox layout = newBlackVBox(14);

        // "YOU DIED" title image
        if (deathYouDiedImg != null) {
            ImageView titleImg = new ImageView(deathYouDiedImg);
            titleImg.setPreserveRatio(true);
            layout.getChildren().add(titleImg);
        }

        // Poem image
        if (deathPoemImg != null) {
            ImageView poemImg = new ImageView(deathPoemImg);
            poemImg.setPreserveRatio(true);
            layout.getChildren().add(poemImg);
        }

        // Dynamic death message (not baked into composite)
        if (activeDeathMessage != null && !activeDeathMessage.isBlank()) {
            layout.getChildren().add(styledText(activeDeathMessage, "Serif", 18, Color.rgb(190, 130, 130)));
        }
        if (deathCount >= 5) {
            layout.getChildren().add(styledText("You keep coming back. She likes that.", "Serif", 18,
                    Color.rgb(200, 70, 70)));
        }

        Button restartBtn = createImageButton("RESTART FROM LEVEL 1", "/assets/images/ui/btn_restart.png");
        restartBtn.setOnAction(e -> onRestart.run());
        Button menuBtn = createImageButton("MAIN MENU", "/assets/images/ui/btn_main_menu.png");
        menuBtn.setOnAction(e -> onMainMenu.run());
        layout.getChildren().addAll(new Text(""), restartBtn, menuBtn);
        root.getChildren().add(layout);
        return new Scene(root, WIDTH, HEIGHT);
    }

    /** Creates the victory screen scene. */
    public static Scene createVictoryScene(Runnable onMainMenu) {
        initUIImages();
        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();

        // Background
        if (victoryBgImg != null) {
            ImageView bg = new ImageView(victoryBgImg);
            bg.setFitWidth(WIDTH);
            bg.setFitHeight(HEIGHT);
            bg.setPreserveRatio(false);
            root.getChildren().add(bg);
        }

        VBox layout = newBlackVBox(14);

        // "YOU ESCAPED" title image
        if (victoryEscapedImg != null) {
            ImageView titleImg = new ImageView(victoryEscapedImg);
            titleImg.setPreserveRatio(true);
            layout.getChildren().add(titleImg);
        }

        // Poem image
        if (victoryPoemImg != null) {
            ImageView poemImg = new ImageView(victoryPoemImg);
            poemImg.setPreserveRatio(true);
            layout.getChildren().add(poemImg);
        }

        Button menuBtn = createImageButton("MAIN MENU", "/assets/images/ui/btn_main_menu.png");
        menuBtn.setOnAction(e -> onMainMenu.run());
        layout.getChildren().add(menuBtn);
        root.getChildren().add(layout);
        return new Scene(root, WIDTH, HEIGHT);
    }

    /** Creates the item-found screen scene. */
    public static Scene createItemFoundScene(int level, Runnable onContinue) {
        initUIImages();
        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();

        // Background
        if (itemBgImg != null) {
            ImageView bg = new ImageView(itemBgImg);
            bg.setFitWidth(WIDTH);
            bg.setFitHeight(HEIGHT);
            bg.setPreserveRatio(false);
            root.getChildren().add(bg);
        }

        VBox layout = newBlackVBox(25);

        int idx = Math.min(level - 1, 2);

        // "pale luna smiles wide..." image
        if (itemPaleLunaSmilesImg != null) {
            layout.getChildren().add(imgView(itemPaleLunaSmilesImg));
        }

        // Item name image
        if (itemTextImg[idx] != null) {
            layout.getChildren().add(imgView(itemTextImg[idx]));
        }

        // Description image
        if (itemDescImg[idx] != null) {
            layout.getChildren().add(imgView(itemDescImg[idx]));
        }

        // Action button (level-specific)
        String[] btnPaths = {
                "/assets/images/ui/btn_here.png",
                "/assets/images/ui/btn_use.png",
                "/assets/images/ui/btn_now.png"
        };
        String[] btnLabels = { "here.", "use", "now" };
        Button continueBtn = createImageButton(btnLabels[idx], btnPaths[idx]);
        continueBtn.setFont(Font.font("Serif", 28));
        continueBtn.setOnAction(e -> onContinue.run());

        layout.getChildren().addAll(new Text(""), continueBtn);
        root.getChildren().add(layout);
        return new Scene(root, WIDTH, HEIGHT);
    }

    /** Creates the level transition newspaper screen. */
    public static Scene createLevelTransitionScene(int currentLevel, Runnable onContinue) {
        initUIImages();
        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();

        // Background (level-specific)
        Image bgImg = (currentLevel == 1) ? transitionBgImg1 : transitionBgImg2;
        if (bgImg != null) {
            ImageView bg = new ImageView(bgImg);
            bg.setFitWidth(WIDTH);
            bg.setFitHeight(HEIGHT);
            bg.setPreserveRatio(false);
            root.getChildren().add(bg);
        }

        int idx = Math.min(currentLevel - 1, 2);
        VBox layout = newBlackVBox(20);

        // Header image
        if (transitionHeaderImg != null) {
            layout.getChildren().add(imgView(transitionHeaderImg));
        }

        // Headline image
        if (transitionHeadlineImg[idx] != null) {
            layout.getChildren().add(imgView(transitionHeadlineImg[idx]));
        }

        // Item image (mud/shovel/rope)
        if (transitionItemImg[idx] != null) {
            ImageView itemView = new ImageView(transitionItemImg[idx]);
            itemView.setPreserveRatio(true);
            layout.getChildren().add(itemView);
        }

        Button next = createImageButton("CONTINUE", "/assets/images/ui/btn_continue.png");
        next.setOnAction(e -> onContinue.run());
        layout.getChildren().add(next);
        root.getChildren().add(layout);
        return new Scene(root, WIDTH, HEIGHT);
    }

    // ========================= SHARED UTILITIES =========================

    /** Creates a standard game menu button with a background image. */
    public static Button createImageButton(String text, String imagePath) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", 16));
        btn.setTextFill(Color.TRANSPARENT);
        URL url = SceneFactory.class.getResource(imagePath);
        if (url != null) {
            String bgUrl = url.toExternalForm();
            btn.setStyle("-fx-background-image: url('" + bgUrl + "'); "
                    + "-fx-background-size: stretch; "
                    + "-fx-background-color: transparent; "
                    + "-fx-border-color: darkred; -fx-border-width: 1px;");
            btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-image: url('" + bgUrl + "'); "
                    + "-fx-background-size: stretch; "
                    + "-fx-background-color: rgba(100, 0, 0, 0.3); "
                    + "-fx-border-color: red; -fx-border-width: 2px;"));
            btn.setOnMouseExited(e -> btn.setStyle("-fx-background-image: url('" + bgUrl + "'); "
                    + "-fx-background-size: stretch; "
                    + "-fx-background-color: transparent; "
                    + "-fx-border-color: darkred; -fx-border-width: 1px;"));
        } else {
            // Fallback if image missing
            btn.setTextFill(Color.WHITE);
            btn.setStyle("-fx-background-color: #1a1a1a; -fx-text-fill: white; -fx-border-color: darkred; -fx-border-width: 1px; -fx-padding: 10 40;");
        }
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
        } catch (Exception ignored) {
        }
        return null;
    }

    // ========================= PRIVATE HELPERS =========================

    private static VBox newBlackVBox(int spacing) {
        VBox layout = new VBox(spacing);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: black;");
        return layout;
    }

    private static ImageView imgView(Image img) {
        ImageView v = new ImageView(img);
        v.setPreserveRatio(true);
        return v;
    }

    private static Text styledText(String content, String fontFamily, int fontSize, Color color) {
        Text t = new Text(content);
        t.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        t.setFont(Font.font(fontFamily, fontSize));
        t.setFill(color);
        return t;
    }
}
