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
 * The main starting point for Escape Pale Luna! 
 * This application class sets up the window window, launches the game loop, 
 * and handles transitions between menus, cutscenes, and active gameplay.
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

    /**
     * Called automatically by JavaFX when the application launches. 
     * We just grab the primary window stage, set the title, and show the main menu.
     * 
     * @param stage The main application window provided by JavaFX.
     */
    @Override
    public void start(Stage stage) {
        this.mainWindow = stage;
        mainWindow.setScene(createMainMenu());
        mainWindow.setTitle("Escape Pale Luna");
        mainWindow.show();
    }

    /**
     * The standard Java entry point. Just tells JavaFX to launch the application!
     * 
     * @param args Command line arguments (not really used).
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Builds the main title screen menu where you can start a new game or exit.
     * 
     * @return Scene The JavaFX scene containing the complete main menu layout.
     */
    private Scene createMainMenu() {
        SceneFactory.initUIImages();

        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane();
        root.setStyle("-fx-background-color: black;");

        // Draw the creepy ambient background if we have one.
        Image bgImg = SceneFactory.getMenuBackgroundImg();
        if (bgImg != null) {
            ImageView bgView = new ImageView(bgImg);
            bgView.setFitWidth(880);
            bgView.setFitHeight(730);
            bgView.setPreserveRatio(false);
            bgView.setOpacity(0.4);
            root.getChildren().add(bgView);
        }

        // Stack our title text and subtitle neatly down the center.
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);

        // Put the game's title front and center!
        javafx.scene.text.Text titleText = SceneFactory.getMenuTitleText();
        layout.getChildren().add(titleText);

        javafx.scene.Node subText = SceneFactory.getMenuSubtitleText();
        if (subText != null) {
            layout.getChildren().add(subText);
        }

        // Add the clickable buttons to begin playing or to quit
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

    /**
     * Replaces the menu with a spooky slideshow intro detailing the backstory, 
     * playing tense music, and ending by dropping you directly into level 1.
     * Starts immediately when the player hits "Play"!
     */
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
                // If the player hits ENTER, skip the cutscene straight to a loading screen!
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

    /**
     * Wipes the board clean, loads up a fresh layout for the given level, and tells the 
     * engine to start ticking! Keeps track of Sanity so players don't suddenly get healed between levels.
     * 
     * @param level Which level or floor number to jump you into.
     */
    private void startGame(int level) {
        int savedSanity = (gsm.entityManager.getPlayer() != null) ? (int) gsm.entityManager.getPlayer().getSanity()
                : 100;

        gsm.levelManager.setCurrentLevel(level);
        gsm.resetGameState();
        activeKeys.clear();
        pressedThisFrame.clear();
        gsm.loadLevel();

        // Make sure we carry your current sanity rating forward to the next level!
        if (level > 1 && gsm.entityManager.getPlayer() != null) {
            gsm.entityManager.getPlayer().setSanity(savedSanity);
        }

        isPlaying = true;
        setupGameScene();
        gsm.soundManager.playOneShot(SoundManager.GAME_START, 0.75);
        gsm.soundManager.playMusicIfPresent(SoundManager.AMBIENT_DRONE);
    }

    /**
     * Initializes the canvas and hooks up the heartbeat for the main game.
     * Tells the physics code to run inside a continuous animation loop.
     */
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
                        // Check if we are currently mid-death-animation or completely completely dead
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

    /**
     * Spits the actual graphics to the screen! Also calculates cool camera effects, 
     * like increasing the dark vignette edges if sanity gets way too low.
     * 
     * @param gc The canvas context where every tile and sprite actually gets drawn.
     */
    private void render(GraphicsContext gc) {
        // As you get crazier, the dark edges creep closer around the screen to freak you out!
        double vignetteIntensity = 0;
        if (gsm.entityManager.getPlayer() != null) {
            int sanity = (int) gsm.entityManager.getPlayer().getSanity();
            if (sanity < 25) {
                vignetteIntensity = (25 - sanity) / 25.0;
            }
        }

        gsm.pulsePhaseHUD = GameRenderer.render(gc, gsm.levelManager.getMaze(), gsm.entityManager.getEntities(),
                gsm.entityManager.getPaleLuna(), gsm.entityManager.getPlayer(),
                gsm.warningFlashTimer, gsm.lolliRevealState, gsm.levelManager.getCurrentLevel(),
                gsm.entityManager.getChests(), ITEM_NAMES,
                gsm.distractionSpellCount, gsm.hasCloneItem,
                gsm.pulsePhaseHUD,
                gsm.entityManager.getPaleLuna() != null && gsm.entityManager.getPaleLuna().isHunting(),
                gsm.screenShakeFrames,
                vignetteIntensity,
                gsm.overlays);
        if (showDebugOverlay)
            gsm.drawDebugOverlay(gc, activeKeys);
    }

    /**
     * Draws the bloody screen crunch animation when the player gets caught by Pale Luna!
     * 
     * @param gc              The graphics context used to draw the terrifying death overlay.
     * @param framesRemaining How many frames are left before the player is completely dead.
     */
    private void renderDeathAnimation(GraphicsContext gc, double framesRemaining) {
        double progress = 1.0 - (double) framesRemaining / 60.0;

        double maxRadius = 600.0;
        // Exponential ease-in parameter generating explicit terminal bounds seamlessly
        // mathematically accurately
        double currentRadius = maxRadius * (1.0 - Math.pow(progress, 1.5));

        double jitterStrength = progress * 25.0;
        double jitterX = (Math.random() - 0.5) * jitterStrength;
        double jitterY = (Math.random() - 0.5) * jitterStrength;

        double centerX = 440.0 + jitterX;
        double centerY = 365.0 + jitterY;

        boolean isFlash = progress > 0.2 && Math.random() < 0.2;

        double centerAlpha = progress > 0.5 ? Math.min(1.0, (progress - 0.5) * 2.0) : 0.0;
        Color centerColor = Color.rgb(0, 0, 0, centerAlpha);
        Color bloodRed = isFlash ? Color.rgb(255, 0, 0, 0.9) : Color.rgb(150, 0, 0, 0.85);

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

        if (isFlash && Math.random() < 0.3) {
            gc.setFill(Color.rgb(255, 100, 100, 0.3));
            gc.fillRect(0, 0, 880, 730);
        }
    }

    /**
     * Bumps the player up to the next floor! If they beat level 3, play the victory ending!
     */
    private void advanceLevel() {
        if (gsm.levelManager.getCurrentLevel() >= 3) {
            triggerVictoryCutscene();
        } else {
            gsm.startingDistractions = gsm.distractionSpellCount;
            startGame(gsm.levelManager.getCurrentLevel() + 1);
        }
    }

    /**
     * Shows a cool "Item Found" popup whenever you find one of the Lollis.
     */
    private void showItemFoundScreen() {
        isPlaying = false;
        if (gameLoop != null)
            gameLoop.stop();
        gsm.showingItemFound = true;
        mainWindow.setScene(SceneFactory.createItemFoundScene(
                gsm.levelManager.getCurrentLevel(),
                () -> {
                    gsm.showingItemFound = false;
                    advanceLevel();
                }));
    }

    /**
     * Oh no, Pale Luna caught you! Show the death screen with all your stats!
     */
    private void triggerDeath() {
        isPlaying = false;
        if (gameLoop != null)
            gameLoop.stop();
        deathCount++;
        gsm.soundManager.playOneShot(SoundManager.GAME_OVER, 0.85);

        int lollies = gsm.levelManager.getCurrentLevel() - 1;
        int sanity = gsm.entityManager.getPlayer() != null
                ? Math.max(0, (int) gsm.entityManager.getPlayer().getSanity())
                : 0;

        mainWindow.setScene(SceneFactory.createDeathScene(
                gsm.activeDeathMessage, deathCount,
                lollies, gsm.totalChestsCollected, gsm.totalChestsEncountered, sanity, gsm.totalPlayTimeSeconds,
                this::playIntroAndStart,
                () -> mainWindow.setScene(createMainMenu())));
    }

    /**
     * You won! Kick off the victory slideshow before showing the final stats!
     */
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

    /**
     * Gives the player the final stat break-down (chests found, playtime, sanity left, etc)
     * before booting them back to the main menu.
     */
    private void showVictoryScreen() {
        int sanity = gsm.entityManager.getPlayer() != null
                ? Math.max(0, (int) gsm.entityManager.getPlayer().getSanity())
                : 0;
        mainWindow.setScene(SceneFactory.createVictoryScene(
                3, gsm.totalChestsCollected, gsm.totalChestsEncountered, deathCount, sanity, gsm.totalPlayTimeSeconds,
                () -> mainWindow.setScene(createMainMenu())));
    }
}
