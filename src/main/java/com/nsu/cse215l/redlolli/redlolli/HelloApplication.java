package com.nsu.cse215l.redlolli.redlolli;

import com.nsu.cse215l.redlolli.redlolli.entities.CardboardClone;
import com.nsu.cse215l.redlolli.redlolli.entities.Entity;
import com.nsu.cse215l.redlolli.redlolli.entities.GuardEntity;
import com.nsu.cse215l.redlolli.redlolli.entities.Item;
import com.nsu.cse215l.redlolli.redlolli.entities.Monster;
import com.nsu.cse215l.redlolli.redlolli.entities.Player;
import com.nsu.cse215l.redlolli.redlolli.entities.SerialKillerEntity;
import com.nsu.cse215l.redlolli.redlolli.map.Maze;
import com.nsu.cse215l.redlolli.redlolli.systems.FlashlightSystem;
import com.nsu.cse215l.redlolli.redlolli.systems.SoundManager;
import com.nsu.cse215l.redlolli.redlolli.ui.GameRenderer;
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
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Main game execution loop for the horror transformation of Escape Pale Luna.
 */
public class HelloApplication extends Application {

    private static final String[] MAP_FILES = { "/map.csv", "/map2.csv", "/map3.csv" };
    private static final String[] ITEM_NAMES = { "Mud", "Shovel", "Rope" };
    private static final String[] ITEM_FOUND_MAIN_TEXT = { "Mud Found", "Shovel Found", "Rope Found" };
    private static final String[] ITEM_FOUND_BUTTON_TEXT = { "here.", "use", "now" };

    private static final String[] MENU_SUBTITLES = {
            "Find the cursed items. Survive the demon.",
            "She remembers your last game.",
            "You can only survive."
    };

    private static final int LOLLI_REVEAL_DURATION = 120;

    private Stage mainWindow;
    private AnimationTimer gameLoop;
    private boolean isPlaying = false;

    private final Set<KeyCode> activeKeys = new HashSet<>();
    private final Set<KeyCode> pressedThisFrame = new HashSet<>();

    private Player player;
    private Maze maze;

    private Monster paleLuna;
    private SerialKillerEntity serialKiller;
    private CardboardClone cloneDecoy;

    private final List<Entity> entities = new ArrayList<>();
    private final List<Item> chests = new ArrayList<>();
    private final List<GuardEntity> guards = new ArrayList<>();

    private int currentLevel = 1;
    private boolean showingItemFound = false;

    private int warningFlashTimer = 0;
    private double pulsePhaseHUD = 0;
    private String activeDeathMessage = "";

    private final FlashlightSystem flashlightSystem = new FlashlightSystem();
    private final SoundManager soundManager = new SoundManager();

    private boolean lolliRecentlyCollected = false;
    private GameRenderer.LolliRevealState lolliRevealState = null;

    private int fruitCount = 0;
    private int eggCount = 0;
    private int exitGraceFrames = 0;
    private int standStillFrames = 0;
    private int guardHitCooldownFrames = 0;
    private int footstepCooldownFrames = 0;
    private int lunaScreamCooldownFrames = 0;

    private boolean hasCloneItem = false;
    private boolean wasInEscapeRoom = false;
    private boolean escapeRoomsCollapsed = false;

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

        Button newGameBtn = createStyledButton("> NEW GAME");
        Button exitBtn = createStyledButton("EXIT");
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
        soundManager.playMusicIfPresent("/assets/audio/intro_music.wav");

        Timeline timeline = new Timeline();
        for (int i = 0; i < 6; i++) {
            String imgPath = "/assets/images/intro_" + (i + 1) + ".jpeg";
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(i * 1.7 + 0.5), e -> {
                Image img = tryLoadImage(imgPath);
                if (img != null) cutsceneImage.setImage(img);
            }));
        }
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(11.5), e -> {
            soundManager.stopMusic();
            startGame(1);
        }));
        timeline.play();
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                timeline.stop();
                soundManager.stopMusic();
                startGame(1);
            }
        });
    }

    private Button createStyledButton(String text) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        String normalStyle = "-fx-background-color: #1a1a1a; -fx-text-fill: white; -fx-border-color: darkred; -fx-border-width: 1px; -fx-padding: 10 40;";
        String hoverStyle = "-fx-background-color: #330000; -fx-text-fill: white; -fx-border-color: red; -fx-border-width: 1px; -fx-padding: 10 40;";
        btn.setStyle(normalStyle);
        btn.setOnMouseEntered(e -> btn.setStyle(hoverStyle));
        btn.setOnMouseExited(e -> btn.setStyle(normalStyle));
        return btn;
    }

    private void startGame(int level) {
        currentLevel = level;
        resetGameState();
        loadLevel();
        setupGameScene();
        soundManager.playOneShot(SoundManager.GAME_START, 0.75);
    }

    private void resetGameState() {
        entities.clear();
        chests.clear();
        guards.clear();
        activeKeys.clear();
        pressedThisFrame.clear();

        paleLuna = null;
        serialKiller = null;
        cloneDecoy = null;

        isPlaying = true;
        showingItemFound = false;
        warningFlashTimer = 0;
        pulsePhaseHUD = 0;
        lolliRevealState = null;
        activeDeathMessage = "";

        lolliRecentlyCollected = false;

        fruitCount = currentLevel == 1 ? 2 : 0;
        eggCount = currentLevel == 2 ? 5 : 0;
        exitGraceFrames = 0;
        standStillFrames = 0;
        guardHitCooldownFrames = 0;
        footstepCooldownFrames = 0;
        lunaScreamCooldownFrames = 0;
        hasCloneItem = false;
        wasInEscapeRoom = false;

        flashlightSystem.reset();
        escapeRoomsCollapsed = false;
    }

    private void loadLevel() {
        maze = new Maze(MAP_FILES[currentLevel - 1], currentLevel);

        double spawnX = maze.getPlayerSpawnCol() * Maze.TILE_SIZE + 10;
        double spawnY = maze.getPlayerSpawnRow() * Maze.TILE_SIZE + Maze.Y_OFFSET + 10;
        player = new Player(spawnX, spawnY);
        entities.add(player);

        spawnEntities();
        spawnLevelThreats();
    }

    private void spawnEntities() {
        int[][] grid = maze.getMapGrid();

        List<int[]> emptyChestTiles = new ArrayList<>();
        List<int[]> lolliChestTiles = new ArrayList<>();
        int lunaRow = -1;
        int lunaCol = -1;

        for (int row = 0; row < grid.length; row++) {
            for (int col = 0; col < grid[row].length; col++) {
                int tile = grid[row][col];
                if (tile == 2) {
                    emptyChestTiles.add(new int[] { row, col });
                    grid[row][col] = 0;
                } else if (tile == 3) {
                    lolliChestTiles.add(new int[] { row, col });
                    grid[row][col] = 0;
                } else if (tile == 5) {
                    lunaRow = row;
                    lunaCol = col;
                    grid[row][col] = 0;
                }
            }
        }

        for (int[] pos : emptyChestTiles) {
            Item.ContentType type = Item.ContentType.EMPTY;
            if (currentLevel == 3 && !containsContent(Item.ContentType.CLONE_DECOY)) {
                type = Item.ContentType.CLONE_DECOY;
            }
            Item chest = new Item(pos[1] * Maze.TILE_SIZE + 12, pos[0] * Maze.TILE_SIZE + Maze.Y_OFFSET + 12, type);
            chests.add(chest);
            entities.add(chest);
        }

        for (int[] pos : lolliChestTiles) {
            Item chest = new Item(pos[1] * Maze.TILE_SIZE + 12, pos[0] * Maze.TILE_SIZE + Maze.Y_OFFSET + 12,
                    Item.ContentType.LOLLI);
            chests.add(chest);
            entities.add(chest);
        }

        if (lunaRow >= 0) {
            paleLuna = new Monster(lunaCol * Maze.TILE_SIZE + 7.5, lunaRow * Maze.TILE_SIZE + Maze.Y_OFFSET + 7.5);
            entities.add(paleLuna);
        }
    }

    private boolean containsContent(Item.ContentType type) {
        return chests.stream().anyMatch(c -> c.getContentType() == type);
    }

    private void spawnLevelThreats() {
        List<int[]> escapeRooms = maze.getTilesOfType(6);
        if (currentLevel == 1) {
            int count = Math.min(2, escapeRooms.size());
            for (int i = 0; i < count; i++) {
                int[] p = escapeRooms.get(i);
                double[] pos = findGuardPositionForEscapeTile(p[0], p[1]);
                GuardEntity bat = new GuardEntity(pos[0], pos[1], GuardEntity.Type.BAT, p[0], p[1]);
                guards.add(bat);
                entities.add(bat);
            }
        } else if (currentLevel == 2) {
            int count = Math.min(3, escapeRooms.size());
            for (int i = 0; i < count; i++) {
                int[] p = escapeRooms.get(i);
                double[] pos = findGuardPositionForEscapeTile(p[0], p[1]);
                GuardEntity cobra = new GuardEntity(pos[0], pos[1], GuardEntity.Type.COBRA, p[0], p[1]);
                guards.add(cobra);
                entities.add(cobra);
            }
        } else if (currentLevel == 3) {
            serialKiller = new SerialKillerEntity(18 * Maze.TILE_SIZE + 6, 15 * Maze.TILE_SIZE + Maze.Y_OFFSET + 6);
            entities.add(serialKiller);
        }
    }

    private double[] findGuardPositionForEscapeTile(int row, int col) {
        int[][] g = maze.getMapGrid();
        int[][] dirs = { { 0, -1 }, { 0, 1 }, { -1, 0 }, { 1, 0 } };
        for (int[] d : dirs) {
            int nr = row + d[0];
            int nc = col + d[1];
            if (nr < 0 || nr >= g.length || nc < 0 || nc >= g[0].length) {
                continue;
            }
            int tile = g[nr][nc];
            if (tile != 1 && tile != 10) {
                return new double[] {
                        nc * Maze.TILE_SIZE + 10,
                        nr * Maze.TILE_SIZE + Maze.Y_OFFSET + 10
                };
            }
        }
        return new double[] {
                col * Maze.TILE_SIZE + 10,
                row * Maze.TILE_SIZE + Maze.Y_OFFSET + 10
        };
    }

    private void setupGameScene() {
        Canvas canvas = new Canvas(880, 730);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Scene gameScene = new Scene(new Group(canvas), 880, 730, Color.BLACK);

        gameScene.setOnKeyPressed(e -> {
            activeKeys.add(e.getCode());
            pressedThisFrame.add(e.getCode());

            if (e.getCode() == KeyCode.F) {
                flashlightSystem.toggle();
            }
            if (e.getCode() == KeyCode.E) {
                tryUseDistraction();
            }
            if (e.getCode() == KeyCode.C) {
                tryPlaceClone();
            }
            if (e.getCode() == KeyCode.F3) {
                showDebugOverlay = !showDebugOverlay;
            }
        });

        gameScene.setOnKeyReleased(e -> activeKeys.remove(e.getCode()));

        if (gameLoop != null) {
            gameLoop.stop();
        }

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (isPlaying) {
                    update();
                    render(gc);
                    pressedThisFrame.clear();
                }
            }
        };
        gameLoop.start();
        mainWindow.setScene(gameScene);
    }

    private void update() {
        if (showingItemFound) {
            return;
        }

        if (lolliRevealState != null && lolliRevealState.active) {
            lolliRevealState.timer--;
            lolliRevealState.phase += 0.15;
            if (lolliRevealState.timer <= 0) {
                lolliRevealState.active = false;
                showItemFoundScreen();
            }
            return;
        }

        if (exitGraceFrames > 0) exitGraceFrames--;
        if (guardHitCooldownFrames > 0) guardHitCooldownFrames--;
        if (lunaScreamCooldownFrames > 0) lunaScreamCooldownFrames--;

        if (serialKiller != null) {
            serialKiller.update();
        }

        for (GuardEntity guard : guards) {
            guard.update();
        }

        player.update();

        boolean movingInput = activeKeys.contains(KeyCode.W) || activeKeys.contains(KeyCode.A)
                || activeKeys.contains(KeyCode.S) || activeKeys.contains(KeyCode.D);
        if (movingInput) {
            standStillFrames = 0;
        } else {
            standStillFrames++;
            if (standStillFrames == 1800) {
                teleportLunaNearPlayer();
            }
        }

        boolean sprinting = activeKeys.contains(KeyCode.SHIFT) && player.canSprint();
        {
            double beforeX = player.getX(), beforeY = player.getY();
            int invert = 1;
            if (activeKeys.contains(KeyCode.W)) player.move(0, -invert, maze, sprinting);
            if (activeKeys.contains(KeyCode.S)) player.move(0, invert, maze, sprinting);
            if (activeKeys.contains(KeyCode.A)) player.move(-invert, 0, maze, sprinting);
            if (activeKeys.contains(KeyCode.D)) player.move(invert, 0, maze, sprinting);

            boolean moved = Math.abs(player.getX() - beforeX) > 0.01 || Math.abs(player.getY() - beforeY) > 0.01;
            if (moved) {
                if (footstepCooldownFrames <= 0) {
                    soundManager.playOneShot(SoundManager.FOOTSTEP, 0.25);
                    footstepCooldownFrames = sprinting ? 9 : 15;
                } else {
                    footstepCooldownFrames--;
                }
            } else {
                footstepCooldownFrames = Math.max(0, footstepCooldownFrames - 1);
            }
        }

        boolean inEscapeRoom = maze.isEscapeRoom(player.getHitbox());
        boolean enteringEscapeRoom = !wasInEscapeRoom && inEscapeRoom;
        boolean exitingEscapeRoom = wasInEscapeRoom && !inEscapeRoom;

        player.setInEscapeRoom(inEscapeRoom);

        if (exitingEscapeRoom) {
            exitGraceFrames = 45;
            if (currentLevel == 3 && !escapeRoomsCollapsed) {
                maze.collapseEscapeRooms();
                escapeRoomsCollapsed = true;
            }
        }
        wasInEscapeRoom = inEscapeRoom;

        checkChestCollisions();
        checkGuardThreats(enteringEscapeRoom);
        updateSerialKiller();
        updatePaleLuna(inEscapeRoom, exitingEscapeRoom);

        double lunaDistTiles = paleLuna == null ? 99.0
                : calculateDistanceInTiles(player.getX(), player.getY(), paleLuna.getX(), paleLuna.getY());
        flashlightSystem.update(lunaDistTiles);
        lolliRecentlyCollected = false;
    }

    private void teleportLunaNearPlayer() {
        if (paleLuna == null) {
            return;
        }
        double px = player.getX();
        double py = player.getY();
        paleLuna.setPosition(px + 36, py);
    }

    private void checkGuardThreats(boolean enteringEscapeRoom) {
        if (guardHitCooldownFrames > 0) {
            return;
        }

        if (enteringEscapeRoom) {
            for (GuardEntity guard : guards) {
                if (!guard.isDistracted() && guard.isPlayerOnGuardedRoom(player.getHitbox())) {
                    if (guard.getType() == GuardEntity.Type.BAT) {
                        activeDeathMessage = "The bat bit first. Luna answered instantly.";
                    } else {
                        activeDeathMessage = "The snake was still hungry. No egg, no escape.";
                    }
                    triggerDeath();
                    return;
                }
            }
        }
    }

    private void tryUseDistraction() {
        GuardEntity nearest = null;
        double best = Double.MAX_VALUE;

        for (GuardEntity guard : guards) {
            if (guard.isDistracted()) {
                continue;
            }
            double d = calculateDistanceInTiles(player.getX(), player.getY(), guard.getX(), guard.getY());
            if (d < best && guard.isWithinDistractionRange(player.getX(), player.getY())) {
                best = d;
                nearest = guard;
            }
        }

        if (nearest == null) {
            return;
        }

        if (nearest.getType() == GuardEntity.Type.BAT) {
            if (fruitCount > 0) {
                fruitCount--;
                nearest.distract();
            }
        } else {
            if (eggCount > 0) {
                eggCount--;
                nearest.distract();
            }
        }
    }

    private void tryPlaceClone() {
        if (!hasCloneItem || currentLevel != 3 || cloneDecoy != null) {
            return;
        }
        cloneDecoy = new CardboardClone(player.getX() + 5, player.getY() + 5);
        entities.add(cloneDecoy);
        hasCloneItem = false;
        soundManager.playOneShot(SoundManager.CHEST_OPEN, 0.45);
    }

    private void checkChestCollisions() {
        for (Item chest : chests) {
            if (chest.isCollected()) {
                continue;
            }
            if (!player.getHitbox().intersects(chest.getHitbox())) {
                continue;
            }

            chest.collect();
            soundManager.playOneShot(SoundManager.CHEST_OPEN, 0.65);
            Item.ContentType content = chest.getContentType();

            if (content == Item.ContentType.LOLLI) {
                lolliRecentlyCollected = true;
                soundManager.playOneShot(SoundManager.STINGER_1, 0.8);
                lolliRevealState = new GameRenderer.LolliRevealState(chest.getX(), chest.getY(), LOLLI_REVEAL_DURATION);
                return;
            }
            
            if (content == Item.ContentType.CLONE_DECOY) {
                hasCloneItem = true;
            }
        }
    }

    private void updateSerialKiller() {
        if (serialKiller == null) {
            return;
        }

        if (!serialKiller.isActive()) {
            double triggerDist = calculateDistanceInTiles(
                    player.getX(), player.getY(),
                    serialKiller.getX(), serialKiller.getY());
            if (triggerDist < 8.0) {
                serialKiller.setActive(true);
            }
        }

        if (!serialKiller.isActive()) {
            return;
        }

        double targetX = player.getX();
        double targetY = player.getY();
        if (cloneDecoy != null) {
            targetX = cloneDecoy.getX();
            targetY = cloneDecoy.getY();
        }

        serialKiller.updateChase(targetX, targetY, maze);

        if (cloneDecoy != null && serialKiller.getHitbox().intersects(cloneDecoy.getHitbox())) {
            serialKiller.startDecoyAttack();
            entities.remove(cloneDecoy);
            cloneDecoy = null;
            return; // Gives the player a split-second immunity frame to walk away after placing the clone!
        }

        if (!serialKiller.isAttackingDecoy() && serialKiller.getHitbox().intersects(player.getHitbox())) {
            activeDeathMessage = "Steel and panic. He never stops hunting.";
            triggerDeath();
        }
    }

    private void updatePaleLuna(boolean inEscapeRoom, boolean exitingEscapeRoom) {
        if (paleLuna == null) {
            return;
        }

        Monster.State prevState = paleLuna.getState();

        double targetX = player.getX();
        double targetY = player.getY();

        paleLuna.update(targetX, targetY, inEscapeRoom, lolliRecentlyCollected, maze);

        if (exitingEscapeRoom && paleLuna.isWaitingAtDoor()) {
            activeDeathMessage = "She waited at the door. You stepped out anyway.";
            triggerDeath();
            return;
        }

        if (prevState != Monster.State.HUNTING && paleLuna.getState() == Monster.State.HUNTING) {
            warningFlashTimer = 30;
            soundManager.playOneShot(SoundManager.HEARTBEAT_FAST, 0.45);
        }
        if (warningFlashTimer > 0) {
            warningFlashTimer--;
        }

        player.setBeingChased(paleLuna.isHunting());

        double lunaDistTiles = calculateDistanceInTiles(player.getX(), player.getY(), paleLuna.getX(), paleLuna.getY());
        if (paleLuna.isHunting() && lunaDistTiles <= 3.0 && lunaScreamCooldownFrames <= 0) {
            soundManager.playOneShot(SoundManager.LUNA_SCREAM_NEARBY, 0.8);
            lunaScreamCooldownFrames = 130;
        }

        if (!inEscapeRoom && paleLuna.isHunting()
                && player.getHitbox().intersects(paleLuna.getHitbox())) {
            activeDeathMessage = "She found your pulse before you heard her footsteps.";
            triggerDeath();
            return;
        }

        if (!inEscapeRoom && paleLuna.isWaitingAtDoor()) {
            boolean canSee = maze.hasLineOfSight(
                    paleLuna.getX() + 12, paleLuna.getY() + 12,
                    player.getX() + 10, player.getY() + 10);
            if (canSee) {
                activeDeathMessage = "She waited at the door. You stepped out anyway.";
                triggerDeath();
            }
        }
    }

    private void render(GraphicsContext gc) {
        pulsePhaseHUD = GameRenderer.render(gc, maze, entities, paleLuna, player,
                warningFlashTimer, lolliRevealState, currentLevel, chests, ITEM_NAMES,
                flashlightSystem, false, fruitCount, eggCount, hasCloneItem,
                pulsePhaseHUD);

        if (showDebugOverlay) {
            drawDebugOverlay(gc);
        }
    }

    private void drawDebugOverlay(GraphicsContext gc) {
        double lunaDist = paleLuna == null ? 99.0
                : calculateDistanceInTiles(player.getX(), player.getY(), paleLuna.getX(), paleLuna.getY());
        boolean lunaNearby = lunaDist <= 5.0;

        gc.setFill(Color.rgb(0, 0, 0, 0.62));
        gc.fillRect(8, 58, 360, 182);
        gc.setStroke(Color.rgb(180, 40, 40, 0.8));
        gc.setLineWidth(1.1);
        gc.strokeRect(8, 58, 360, 182);

        gc.setFill(Color.rgb(220, 220, 220));
        gc.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
        int[] tile = maze.getTilePositionAt(player.getX() + 10, player.getY() + 10);
        String tileText = tile == null ? "-" : tile[0] + "," + tile[1];
        String lunaState = paleLuna == null ? "NONE" : paleLuna.getState().name();
        int lunaTimer = 0;
        if (paleLuna != null) {
            switch (paleLuna.getState()) {
                case DORMANT -> lunaTimer = paleLuna.getDormantTimer();
                case STALKING -> lunaTimer = paleLuna.getStalkTimer();
                case HUNTING -> lunaTimer = paleLuna.getHuntTimer();
                case WAITING_AT_DOOR -> lunaTimer = paleLuna.getWaitTimer();
            }
        }

        String[] lines = {
                "DEBUG (F3)",
                "Level=" + currentLevel + " Tile=" + tileText + " InEscape=" + player.isInEscapeRoom(),
                "Sprint=" + activeKeys.contains(KeyCode.SHIFT),
                "Flash On=" + flashlightSystem.isOn() + " Effective=" + flashlightSystem.isEffectivelyOn(),
                "Battery=" + (int) (flashlightSystem.getBatteryPercent() * 100) + "%",
                "Luna=" + lunaState + " Timer=" + lunaTimer + " Nearby=" + lunaNearby,
                "Fruit=" + fruitCount + " Eggs=" + eggCount + " Clone=" + hasCloneItem,
                "GuardCD=" + guardHitCooldownFrames
        };

        double y = 76;
        for (String line : lines) {
            gc.fillText(line, 16, y);
            y += 18;
        }
    }

    private double calculateDistanceInTiles(double x1, double y1, double x2, double y2) {
        double dx = (x1 - x2) / Maze.TILE_SIZE;
        double dy = (y1 - y2) / Maze.TILE_SIZE;
        return Math.sqrt(dx * dx + dy * dy);
    }

    private void advanceLevel() {
        if (currentLevel >= 3) {
            triggerVictoryCutscene();
        } else {
            showLevelTransition();
        }
    }

    private void showLevelTransition() {
        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: black;");
        String[] headlines = {
                "CENTRAL PARK DIG SITE RETURNS TO NEWS",
                "BASEMENT SKETCHES FOUND IN COLD CASE",
                "ROPE RECOVERED. HUNTER UNSEEN."
        };
        Text title = new Text("NEWSPAPER CLIPPING");
        title.setFont(Font.font("Serif", FontWeight.BOLD, 36));
        title.setFill(Color.rgb(170, 170, 170));
        Text h = new Text(headlines[Math.min(currentLevel - 1, headlines.length - 1)]);
        h.setFont(Font.font("Serif", FontWeight.BOLD, 24));
        h.setFill(Color.rgb(170, 60, 60));
        Button next = createStyledButton("CONTINUE");
        next.setOnAction(e -> startGame(currentLevel + 1));
        layout.getChildren().addAll(title, h, next);
        mainWindow.setScene(new Scene(layout, 880, 730));
    }

    private void showItemFoundScreen() {
        isPlaying = false;
        if (gameLoop != null) gameLoop.stop();
        showingItemFound = true;

        VBox layout = new VBox(25);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: black;");

        Text paleLunaText = new Text("pale luna smiles wide...");
        paleLunaText.setFont(Font.font("Serif", FontWeight.BOLD, 28));
        paleLunaText.setFill(Color.rgb(120, 0, 0));

        Text mainText = new Text(ITEM_FOUND_MAIN_TEXT[currentLevel - 1]);
        mainText.setFont(Font.font("Serif", FontWeight.BOLD, 65));
        mainText.setFill(Color.DARKRED);

        String[] itemDescriptions = {
                "The earth was soft that night. Too soft. Like it was waiting for her.",
                "The blade bit into the ground. Each scoop made a sound like breathing.",
                "She did not struggle at the end. Her eyes were wide open. Smiling."
        };
        Text descText = new Text(itemDescriptions[currentLevel - 1]);
        descText.setWrappingWidth(700);
        descText.setFont(Font.font("Serif", 19));
        descText.setFill(Color.rgb(170, 150, 145));

        Button continueBtn = new Button(ITEM_FOUND_BUTTON_TEXT[currentLevel - 1]);
        continueBtn.setFont(Font.font("Serif", FontWeight.BOLD, 28));
        String normalStyle = "-fx-background-color: #1a0000; -fx-text-fill: #cc0000; -fx-border-color: #660000; -fx-border-width: 2px; -fx-padding: 12 50;";
        String hoverStyle = "-fx-background-color: #330000; -fx-text-fill: #ff3333; -fx-border-color: #990000; -fx-border-width: 2px; -fx-padding: 12 50;";
        continueBtn.setStyle(normalStyle);
        continueBtn.setOnMouseEntered(e -> continueBtn.setStyle(hoverStyle));
        continueBtn.setOnMouseExited(e -> continueBtn.setStyle(normalStyle));
        continueBtn.setOnAction(e -> { showingItemFound = false; advanceLevel(); });

        layout.getChildren().addAll(paleLunaText, mainText, descText, new Text(""), continueBtn);
        mainWindow.setScene(new Scene(layout, 880, 730));
    }

    private void triggerDeath() {
        isPlaying = false;
        if (gameLoop != null) gameLoop.stop();
        deathCount++;
        soundManager.playOneShot(SoundManager.GAME_OVER, 0.85);

        VBox layout = new VBox(14);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: black;");

        String[] deathLines = {
                "pale luna smiles wide", "there is no escape",
                "pale luna smiles wide", "no more lollies to take",
                "pale luna smiles wide", "now you are dead"
        };
        Text title = new Text("YOU DIED");
        title.setFont(Font.font("Serif", FontWeight.BOLD, 70));
        title.setFill(Color.DARKRED);
        layout.getChildren().add(title);

        for (String line : deathLines) {
            Text t = new Text(line);
            t.setFont(Font.font("Serif", 21));
            t.setFill(Color.LIGHTGRAY);
            layout.getChildren().add(t);
        }

        if (!activeDeathMessage.isBlank()) {
            Text detail = new Text(activeDeathMessage);
            detail.setFont(Font.font("Serif", FontWeight.BOLD, 18));
            detail.setFill(Color.rgb(190, 130, 130));
            layout.getChildren().add(detail);
        }
        if (deathCount >= 5) {
            Text extra = new Text("You keep coming back. She likes that.");
            extra.setFont(Font.font("Serif", FontWeight.BOLD, 18));
            extra.setFill(Color.rgb(200, 70, 70));
            layout.getChildren().add(extra);
        }

        Button restartBtn = createStyledButton("RESTART FROM LEVEL 1");
        restartBtn.setOnAction(e -> playIntroAndStart());
        Button menuBtn = createStyledButton("MAIN MENU");
        menuBtn.setOnAction(e -> mainWindow.setScene(createMainMenu()));
        layout.getChildren().addAll(new Text(""), restartBtn, menuBtn);
        mainWindow.setScene(new Scene(layout, 880, 730));
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

        soundManager.playMusicIfPresent("/assets/audio/outro_music.wav");

        Timeline timeline = new Timeline();
        for (int i = 0; i < 5; i++) {
            String imgPath = "/assets/images/victory_" + (i + 1) + ".png";
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1.4 * i + 0.7), e -> {
                Image img = tryLoadImage(imgPath);
                if (img != null) cutsceneImage.setImage(img);
            }));
        }
        timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(8.5), e -> {
            soundManager.stopMusic();
            showVictoryScreen();
        }));
        timeline.play();

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                timeline.stop();
                soundManager.stopMusic();
                showVictoryScreen();
            }
        });
    }

    private void showVictoryScreen() {
        VBox layout = new VBox(14);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: black;");

        Text title = new Text("YOU ESCAPED");
        title.setFont(Font.font("Serif", FontWeight.BOLD, 60));
        title.setFill(Color.LIMEGREEN);
        layout.getChildren().add(title);

        String[] victoryLines = {
                "pale luna smiles wide,", "the ground is soft,",
                "pale luna smiles wide,", "there is a hole,",
                "pale luna smiles wide,", "tie her up with rope,",
                "congratulations! you have escaped from pale luna"
        };
        for (String line : victoryLines) {
            Text t = new Text(line);
            boolean isFinal = line.startsWith("congratulations");
            t.setFont(Font.font("Serif", isFinal ? FontWeight.BOLD : FontWeight.NORMAL, isFinal ? 24 : 20));
            t.setFill(isFinal ? Color.GOLD : Color.LIGHTGRAY);
            layout.getChildren().add(t);
        }

        Button menuBtn = createStyledButton("MAIN MENU");
        menuBtn.setOnAction(e -> mainWindow.setScene(createMainMenu()));
        layout.getChildren().add(menuBtn);
        mainWindow.setScene(new Scene(layout, 880, 730));
    }

    private Image tryLoadImage(String path) {
        try {
            java.net.URL url = getClass().getResource(path);
            if (url != null) return new Image(url.toExternalForm());
        } catch (Exception ignored) {}
        return null;
    }
}
