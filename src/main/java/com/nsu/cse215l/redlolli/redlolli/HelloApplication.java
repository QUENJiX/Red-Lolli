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
        SceneFactory.initUIImages();

        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();
        root.setStyle("-fx-background-color: black;");

        // Background (semi-transparent)
        Image bgImg = SceneFactory.getMenuBackgroundImg();
        if (bgImg != null) {
            ImageView bgView = new ImageView(bgImg);
            bgView.setFitWidth(880);
            bgView.setFitHeight(730);
            bgView.setPreserveRatio(false);
            bgView.setOpacity(0.4);
            root.getChildren().add(bgView);
        }

        // Content VBox
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);

        // Title
        javafx.scene.text.Text titleText = SceneFactory.getMenuTitleText();
        layout.getChildren().add(titleText);

        // Subtitle
        javafx.scene.Node subText = SceneFactory.getMenuSubtitleText();
        if (subText != null) {
            layout.getChildren().add(subText);
        }

        // Buttons using SceneFactory's new image swapping logic
        javafx.scene.control.Button newGameBtn = SceneFactory.createIconButton(
                "/assets/images/ui/icon_play.png",
                "/assets/images/ui/btn_new_game.png",
                "/assets/images/ui/btn_new_game_pressed.png");
        javafx.scene.control.Button exitBtn = SceneFactory.createIconButton(
                "/assets/images/ui/icon_exit.png",
                "/assets/images/ui/btn_exit.png",
                "/assets/images/ui/btn_exit_pressed.png");
        newGameBtn.setOnAction(e -> playIntroAndStart());
        exitBtn.setOnAction(e -> System.exit(0));

        javafx.scene.layout.HBox btnContainer = new javafx.scene.layout.HBox(40);
        btnContainer.setAlignment(Pos.CENTER);
        btnContainer.getChildren().addAll(newGameBtn, exitBtn);

        layout.getChildren().add(btnContainer);
        root.getChildren().add(layout);

        return new Scene(root, 880, 730);
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
            scene.setOnKeyPressed(null);
            gsm.soundManager.stopMusic();

            root.getChildren().clear();
            javafx.scene.text.Text loadingText = new javafx.scene.text.Text("LOADING...");
            loadingText.setFill(Color.WHITE);
            loadingText.setFont(javafx.scene.text.Font.font("Serif", 30));
            root.getChildren().add(loadingText);

            javafx.animation.PauseTransition pt = new javafx.animation.PauseTransition(Duration.millis(50));
            pt.setOnFinished(evt -> startGame(1));
            pt.play();
        }));
        timeline.play();
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                // Prevent multiple enters and give immediate visual feedback
                scene.setOnKeyPressed(null);
                timeline.stop();
                gsm.soundManager.stopMusic();

                root.getChildren().clear();
                javafx.scene.text.Text loadingText = new javafx.scene.text.Text("LOADING...");
                loadingText.setFill(Color.WHITE);
                loadingText.setFont(javafx.scene.text.Font.font("Serif", 30));
                root.getChildren().add(loadingText);

                javafx.animation.PauseTransition pt = new javafx.animation.PauseTransition(Duration.millis(50));
                pt.setOnFinished(evt -> startGame(1));
                pt.play();
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
                gsm.distractionSpellCount, gsm.hasCloneItem,
                gsm.pulsePhaseHUD,
                gsm.paleLuna != null && gsm.paleLuna.isHunting(),
                gsm.screenShakeFrames,
                vignetteIntensity,
                gsm.overlays);
        if (showDebugOverlay)
            gsm.drawDebugOverlay(gc, activeKeys);
    }

    /**
     * Renders the player death animation - screen fills red closing in a circle
     * with flashes and jitters.
     */
    private void renderDeathAnimation(GraphicsContext gc, int framesRemaining) {
        double progress = 1.0 - (double) framesRemaining / 60.0; // 0.0 to 1.0 over 60 frames

        // Max radius to cover the 880x730 screen is approx 575 (from center)
        double maxRadius = 600.0;
        // Exponential ease-in so it closes faster at the end
        double currentRadius = maxRadius * (1.0 - Math.pow(progress, 1.5));

        // Add some jitter that intensifies as it closes
        double jitterStrength = progress * 25.0;
        double jitterX = (Math.random() - 0.5) * jitterStrength;
        double jitterY = (Math.random() - 0.5) * jitterStrength;

        double centerX = 440.0 + jitterX;
        double centerY = 365.0 + jitterY;

        // Flashes randomness
        boolean isFlash = progress > 0.2 && Math.random() < 0.2;

        double centerAlpha = progress > 0.5 ? Math.min(1.0, (progress - 0.5) * 2.0) : 0.0;
        Color centerColor = Color.rgb(0, 0, 0, centerAlpha);
        Color bloodRed = isFlash ? Color.rgb(255, 0, 0, 0.9) : Color.rgb(150, 0, 0, 0.85);

        // Normalize ratios for the gradient stops (must be 0.0 to 1.0)
        double innerRatio = Math.max(0.001, currentRadius / maxRadius);
        double outerRatio = Math.min(1.0, innerRatio + 0.15); // soft edge transition

        javafx.scene.paint.RadialGradient gradient = new javafx.scene.paint.RadialGradient(
                0, 0, centerX, centerY, maxRadius, false,
                javafx.scene.paint.CycleMethod.NO_CYCLE,
                new javafx.scene.paint.Stop(0.0, centerColor),
                new javafx.scene.paint.Stop(innerRatio, centerColor),
                new javafx.scene.paint.Stop(outerRatio, bloodRed),
                new javafx.scene.paint.Stop(1.0, bloodRed));

        gc.setFill(gradient);
        gc.fillRect(0, 0, 880, 730);

        // Occasional intense white/red screen-wide flash
        if (isFlash && Math.random() < 0.3) {
            gc.setFill(Color.rgb(255, 100, 100, 0.3));
            gc.fillRect(0, 0, 880, 730);
        }
    }

    // ========================= SCREEN TRANSITIONS =========================

    private void advanceLevel() {
        if (gsm.currentLevel >= 3) {
            triggerVictoryCutscene();
        } else {
            gsm.startingDistractions = gsm.distractionSpellCount;
            startGame(gsm.currentLevel + 1);
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
