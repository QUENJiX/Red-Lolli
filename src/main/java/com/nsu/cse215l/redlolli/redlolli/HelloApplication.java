package com.nsu.cse215l.redlolli.redlolli;

import com.nsu.cse215l.redlolli.redlolli.systems.SoundManager;
import com.nsu.cse215l.redlolli.redlolli.ui.GameRenderer;
import com.nsu.cse215l.redlolli.redlolli.ui.SceneFactory;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashSet;
import java.util.Set;

/**
 * Main game application coordinator for the horror transformation of Escape
 * Pale Luna.
 * Delegates game logic to GameStateManager and UI screen building to
 * SceneFactory.
 */
public class HelloApplication extends Application {

    private static final String[] ITEM_NAMES = { "Mud", "Shovel", "Rope" };

    private Stage mainWindow;
    private AnimationTimer gameLoop;
    private boolean isPlaying = false;

    private final Set<KeyCode> activeKeys = new HashSet<>();
    private final Set<KeyCode> pressedThisFrame = new HashSet<>();

    private final GameStateManager gsm = new GameStateManager();

    private int deathCount = 0;
    private int menuVisits = 0;
    private boolean showDebugOverlay = false;

    @Override
    public void start(Stage stage) {
        this.mainWindow = stage;
        mainWindow.setScene(createMainMenu());
        mainWindow.setTitle("Escape Pale Luna");
        mainWindow.show();
    }

    public static void main(String[] args) {
        launch();
    }

    // ========================= MENUS & CUTSCENES =========================

    private Scene createMainMenu() {
        menuVisits++;
        SceneFactory.initUIImages();

        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();

        // Background (semi-transparent)
        ImageView bgView = new ImageView(SceneFactory.getMenuBackgroundImg());
        bgView.setFitWidth(880);
        bgView.setFitHeight(730);
        bgView.setPreserveRatio(false);
        root.getChildren().add(bgView);

        // Content VBox
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);

        // Title
        ImageView titleImg = new ImageView(SceneFactory.getMenuTitleImg());
        titleImg.setPreserveRatio(true);
        layout.getChildren().add(titleImg);

        // Subtitle (cycles per visit)
        Image subtitleImg = SceneFactory.getMenuSubtitleImg((menuVisits - 1) % 3);
        if (subtitleImg != null) {
            ImageView subView = new ImageView(subtitleImg);
            subView.setPreserveRatio(true);
            layout.getChildren().add(subView);
        }

        // Buttons using -fx-background-image CSS
        javafx.scene.control.Button newGameBtn = createImageButton(
                "> NEW GAME", "/assets/images/ui/btn_new_game.png", 200, 50);
        javafx.scene.control.Button exitBtn = createImageButton(
                "EXIT", "/assets/images/ui/btn_exit.png", 150, 50);
        newGameBtn.setOnAction(e -> playIntroAndStart());
        exitBtn.setOnAction(e -> System.exit(0));

        layout.getChildren().addAll(newGameBtn, exitBtn);
        root.getChildren().add(layout);

        return new Scene(root, 880, 730);
    }

    /** Creates a Button with a background image, transparent text, and styled borders. */
    private javafx.scene.control.Button createImageButton(String text, String imagePath, int prefW, int prefH) {
        javafx.scene.control.Button btn = new javafx.scene.control.Button(text);
        btn.setPrefSize(prefW, prefH);
        btn.setTextFill(Color.TRANSPARENT);
        String bgUrl = getClass().getResource(imagePath).toExternalForm();
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
        return btn;
    }

    private void playIntroAndStart() {
        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();
        root.setStyle("-fx-background-color: black;");
        ImageView cutsceneImage = new ImageView();
        cutsceneImage.setFitWidth(880);
        cutsceneImage.setFitHeight(730);
        cutsceneImage.setPreserveRatio(true);
        root.getChildren().add(cutsceneImage);
        Scene scene = new Scene(root, 880, 730, Color.BLACK);
        mainWindow.setScene(scene);
        gsm.soundManager.playMusicIfPresent("/assets/audio/intro_music.wav");

        Timeline timeline = new Timeline();
        for (int i = 0; i < 6; i++) {
            String imgPath = "/assets/images/cutscenes/intro/intro_" + (i + 1) + ".jpeg";
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(i * 1.7 + 0.5), e -> {
                Image img = SceneFactory.tryLoadImage(imgPath);
                if (img != null)
                    cutsceneImage.setImage(img);
            }));
        }
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(11.5), e -> {
            gsm.soundManager.stopMusic();
            startGame(1);
        }));
        timeline.play();
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                timeline.stop();
                gsm.soundManager.stopMusic();
                startGame(1);
            }
        });
    }

    // ========================= GAME LIFECYCLE =========================

    private void startGame(int level) {
        gsm.currentLevel = level;
        gsm.resetGameState();
        activeKeys.clear();
        pressedThisFrame.clear();
        gsm.loadLevel();
        isPlaying = true;
        setupGameScene();
        gsm.soundManager.playOneShot(SoundManager.GAME_START, 0.75);
        gsm.soundManager.playMusicIfPresent(SoundManager.AMBIENT_DRONE);
    }

    private void setupGameScene() {
        Canvas canvas = new Canvas(880, 730);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Scene gameScene = new Scene(new Group(canvas), 880, 730, Color.BLACK);

        gameScene.setOnKeyPressed(e -> {
            activeKeys.add(e.getCode());
            pressedThisFrame.add(e.getCode());
            if (e.getCode() == KeyCode.E)
                gsm.tryUseDistraction();
            if (e.getCode() == KeyCode.C)
                gsm.tryPlaceClone();
            if (e.getCode() == KeyCode.F3)
                showDebugOverlay = !showDebugOverlay;
        });
        gameScene.setOnKeyReleased(e -> activeKeys.remove(e.getCode()));

        if (gameLoop != null)
            gameLoop.stop();
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (isPlaying) {
                    boolean died = gsm.update(activeKeys);
                    if (died) {
                        // Play death animation before transitioning to death screen
                        if (gsm.playerIsDead && gsm.playerDeathAnimFrames > 0) {
                            render(gc);
                            renderDeathAnimation(gc, gsm.playerDeathAnimFrames);
                            return;
                        } else if (gsm.playerIsDead) {
                            triggerDeath();
                            return;
                        }
                    }
                    if (gsm.isLolliRevealJustFinished()) {
                        showItemFoundScreen();
                        return;
                    }
                    render(gc);
                    pressedThisFrame.clear();
                }
            }
        };
        gameLoop.start();
        mainWindow.setScene(gameScene);
    }

    private void render(GraphicsContext gc) {
        // Calculate vignette intensity based on sanity (only below 25)
        double vignetteIntensity = 0;
        if (gsm.player != null) {
            int sanity = gsm.player.getSanity();
            if (sanity < 25) {
                // Below 25: start vignette, intensifies as sanity decreases
                vignetteIntensity = (25 - sanity) / 25.0; // 0.0 to 1.0
            }
        }

        gsm.pulsePhaseHUD = GameRenderer.render(gc, gsm.maze, gsm.entities, gsm.paleLuna, gsm.player,
                gsm.warningFlashTimer, gsm.lolliRevealState, gsm.currentLevel, gsm.chests, ITEM_NAMES,
                gsm.fruitCount, gsm.eggCount, gsm.hasCloneItem,
                gsm.pulsePhaseHUD,
                gsm.paleLuna != null && gsm.paleLuna.isHunting(),
                gsm.screenShakeFrames,
                vignetteIntensity,
                gsm.overlays);
        if (showDebugOverlay)
            gsm.drawDebugOverlay(gc, activeKeys);
    }

    /** Renders the player death animation - screen fills red from edges. */
    private void renderDeathAnimation(GraphicsContext gc, int framesRemaining) {
        double progress = 1.0 - (double) framesRemaining / 60.0; // 0.0 to 1.0 over 60 frames

        // Draw red overlay filling from edges
        double edgeSize = progress * 400; // Expands from edges to center

        // Draw red from all four edges
        gc.setFill(Color.rgb(180, 0, 0, 0.7));

        // Top edge
        gc.fillRect(0, 0, 880, edgeSize);
        // Bottom edge
        gc.fillRect(0, 730 - edgeSize, 880, edgeSize);
        // Left edge
        gc.fillRect(0, 0, edgeSize, 730);
        // Right edge
        gc.fillRect(880 - edgeSize, 0, edgeSize, 730);

        // Darken center as animation progresses
        if (progress > 0.5) {
            double centerAlpha = (progress - 0.5) * 1.4; // 0.0 to 0.7
            gc.setFill(Color.rgb(0, 0, 0, centerAlpha));
            gc.fillRect(edgeSize, edgeSize, 880 - edgeSize * 2, 730 - edgeSize * 2);
        }
    }

    // ========================= SCREEN TRANSITIONS =========================

    private void advanceLevel() {
        if (gsm.currentLevel >= 3) {
            triggerVictoryCutscene();
        } else {
            mainWindow.setScene(SceneFactory.createLevelTransitionScene(
                    gsm.currentLevel, () -> startGame(gsm.currentLevel + 1)));
        }
    }

    private void showItemFoundScreen() {
        isPlaying = false;
        if (gameLoop != null)
            gameLoop.stop();
        gsm.showingItemFound = true;
        mainWindow.setScene(SceneFactory.createItemFoundScene(
                gsm.currentLevel,
                () -> {
                    gsm.showingItemFound = false;
                    advanceLevel();
                }));
    }

    private void triggerDeath() {
        isPlaying = false;
        if (gameLoop != null)
            gameLoop.stop();
        deathCount++;
        gsm.soundManager.playOneShot(SoundManager.GAME_OVER, 0.85);
        mainWindow.setScene(SceneFactory.createDeathScene(
                gsm.activeDeathMessage, deathCount,
                this::playIntroAndStart,
                () -> mainWindow.setScene(createMainMenu())));
    }

    private void triggerVictoryCutscene() {
        isPlaying = false;
        if (gameLoop != null)
            gameLoop.stop();

        javafx.scene.layout.StackPane layout = new javafx.scene.layout.StackPane();
        layout.setStyle("-fx-background-color: black;");
        ImageView cutsceneImage = new ImageView();
        cutsceneImage.setFitWidth(880);
        cutsceneImage.setFitHeight(730);
        cutsceneImage.setPreserveRatio(true);
        layout.getChildren().add(cutsceneImage);
        Scene scene = new Scene(layout, 880, 730);
        mainWindow.setScene(scene);

        gsm.soundManager.playMusicIfPresent("/assets/audio/outro_music.wav");

        Timeline timeline = new Timeline();
        for (int i = 0; i < 5; i++) {
            String imgPath = "/assets/images/cutscenes/victory/victory_" + (i + 1) + ".png";
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1.4 * i + 0.7), e -> {
                Image img = SceneFactory.tryLoadImage(imgPath);
                if (img != null)
                    cutsceneImage.setImage(img);
            }));
        }
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(8.5), e -> {
            gsm.soundManager.stopMusic();
            showVictoryScreen();
        }));
        timeline.play();
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                timeline.stop();
                gsm.soundManager.stopMusic();
                showVictoryScreen();
            }
        });
    }

    private void showVictoryScreen() {
        mainWindow.setScene(SceneFactory.createVictoryScene(
                () -> mainWindow.setScene(createMainMenu())));
    }
}
