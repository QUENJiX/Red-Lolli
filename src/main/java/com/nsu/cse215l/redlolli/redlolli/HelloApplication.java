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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashSet;
import java.util.Set;

/**
 * Main game application coordinator for the horror transformation of Escape Pale Luna.
 * Delegates game logic to GameStateManager and UI screen building to SceneFactory.
 */
public class HelloApplication extends Application {

    private static final String[] ITEM_NAMES = { "Mud", "Shovel", "Rope" };
    private static final String[] ITEM_FOUND_MAIN_TEXT = { "Mud Found", "Shovel Found", "Rope Found" };
    private static final String[] ITEM_FOUND_BUTTON_TEXT = { "here.", "use", "now" };

    private static final String[] MENU_SUBTITLES = {
            "Find the cursed items. Survive the demon.",
            "She remembers your last game.",
            "You can only survive."
    };

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

    public static void main(String[] args) { launch(); }

    // ========================= MENUS & CUTSCENES =========================

    private Scene createMainMenu() {
        menuVisits++;
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: black;");

        Text title = new Text("ESCAPE PALE LUNA");
        title.setFont(Font.font("Serif", FontWeight.BOLD, 56));
        title.setFill(Color.DARKRED);
        Text subtitle = new Text(MENU_SUBTITLES[(menuVisits - 1) % MENU_SUBTITLES.length]);
        subtitle.setFont(Font.font("Serif", 19));
        subtitle.setFill(Color.GRAY);

        javafx.scene.control.Button newGameBtn = SceneFactory.createStyledButton("> NEW GAME");
        javafx.scene.control.Button exitBtn = SceneFactory.createStyledButton("EXIT");
        newGameBtn.setOnAction(e -> playIntroAndStart());
        exitBtn.setOnAction(e -> System.exit(0));

        layout.getChildren().addAll(title, subtitle, newGameBtn, exitBtn);
        return new Scene(layout, 880, 730);
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
            String imgPath = "/assets/images/intro_" + (i + 1) + ".jpeg";
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(i * 1.7 + 0.5), e -> {
                Image img = SceneFactory.tryLoadImage(imgPath);
                if (img != null) cutsceneImage.setImage(img);
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
    }

    private void setupGameScene() {
        Canvas canvas = new Canvas(880, 730);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Scene gameScene = new Scene(new Group(canvas), 880, 730, Color.BLACK);

        gameScene.setOnKeyPressed(e -> {
            activeKeys.add(e.getCode());
            pressedThisFrame.add(e.getCode());
            if (e.getCode() == KeyCode.F) gsm.flashlightSystem.toggle();
            if (e.getCode() == KeyCode.E) gsm.tryUseDistraction();
            if (e.getCode() == KeyCode.C) gsm.tryPlaceClone();
            if (e.getCode() == KeyCode.F3) showDebugOverlay = !showDebugOverlay;
        });
        gameScene.setOnKeyReleased(e -> activeKeys.remove(e.getCode()));

        if (gameLoop != null) gameLoop.stop();
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (isPlaying) {
                    boolean died = gsm.update(activeKeys);
                    if (died) { triggerDeath(); return; }
                    if (gsm.isLolliRevealJustFinished()) { showItemFoundScreen(); return; }
                    render(gc);
                    pressedThisFrame.clear();
                }
            }
        };
        gameLoop.start();
        mainWindow.setScene(gameScene);
    }

    private void render(GraphicsContext gc) {
        gsm.pulsePhaseHUD = GameRenderer.render(gc, gsm.maze, gsm.entities, gsm.paleLuna, gsm.player,
                gsm.warningFlashTimer, gsm.lolliRevealState, gsm.currentLevel, gsm.chests, ITEM_NAMES,
                gsm.flashlightSystem, false, gsm.fruitCount, gsm.eggCount, gsm.hasCloneItem,
                gsm.pulsePhaseHUD);
        if (showDebugOverlay) gsm.drawDebugOverlay(gc, activeKeys);
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
        if (gameLoop != null) gameLoop.stop();
        gsm.showingItemFound = true;
        mainWindow.setScene(SceneFactory.createItemFoundScene(
                gsm.currentLevel, ITEM_FOUND_MAIN_TEXT, ITEM_FOUND_BUTTON_TEXT,
                () -> { gsm.showingItemFound = false; advanceLevel(); }));
    }

    private void triggerDeath() {
        isPlaying = false;
        if (gameLoop != null) gameLoop.stop();
        deathCount++;
        gsm.soundManager.playOneShot(SoundManager.GAME_OVER, 0.85);
        mainWindow.setScene(SceneFactory.createDeathScene(
                gsm.activeDeathMessage, deathCount,
                this::playIntroAndStart,
                () -> mainWindow.setScene(createMainMenu())));
    }

    private void triggerVictoryCutscene() {
        isPlaying = false;
        if (gameLoop != null) gameLoop.stop();

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
            String imgPath = "/assets/images/victory_" + (i + 1) + ".png";
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1.4 * i + 0.7), e -> {
                Image img = SceneFactory.tryLoadImage(imgPath);
                if (img != null) cutsceneImage.setImage(img);
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
