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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Development History:
 * Phase 1, Week 1, Day 1-7 - Base Canvas setup and game loop instantiated.
 * Phase 2, Week 2, Day 8-14 - Entities, BFS logic, Player controllers tied to tick cycle.
 * Phase 3, Week 3, Day 15-21 - Visual refinements, audio injection, states & UI linked. 
 * Final phase - Optimization, final item triggers, bug squashing for release.
 * 
 * Main game execution loop for the horror transformation of Escape Pale Luna.
 * Directs pure JavaFX logic, orchestrating update/render cycles at exactly 60 FPS natively.
 */
public class HelloApplication extends Application {

    // --- GAME CONSTANTS ---
    // Phase 1 mapped levels, parsing directly from text representations inside resources/ 
    private static final String[] MAP_FILES = { "/map.csv", "/map2.csv", "/map3.csv" };
    // Narrative object names replacing standard fruit logic with grim puzzle mechanics
    private static final String[] ITEM_NAMES = { "Mud", "Shovel", "Rope" };
    private static final String[] ITEM_FOUND_MAIN_TEXT = { "Mud Found", "Shovel Found", "Rope Found" };
    private static final String[] ITEM_FOUND_BUTTON_TEXT = { "here.", "use", "now" };
    
    // Changing meta-flavor texts as the user repeatedly dies, providing meta punishment.
    private static final String[] MENU_SUBTITLES = {
            "Find the cursed items. Survive the demon.",
            "She remembers your last game.",
            "You can only survive."
    };

    // Duration of the "golden light" animation used when collecting a critical game object
    private static final int LOLLI_REVEAL_DURATION = 120;

    // --- ENGINE CORE ---
    private Stage mainWindow;
    private AnimationTimer gameLoop;
    private boolean isPlaying = false;

    // Raw physical keyboard processing pools
    private final Set<KeyCode> activeKeys = new HashSet<>();
    private final Set<KeyCode> pressedThisFrame = new HashSet<>();

    // --- ACTIVE WORLD STATE ---
    private Player player;
    private Maze maze;
    
    // Core antagonist objects managed separately for AI optimization passes
    private Monster paleLuna;
    private SerialKillerEntity serialKiller;
    private CardboardClone cloneDecoy;

    // Broadly batched entity render stacks 
    private final List<Entity> entities = new ArrayList<>();
    private final List<Item> chests = new ArrayList<>();
    private final List<GuardEntity> guards = new ArrayList<>();
    
    // Phase 2 caching mapping map-logic direct coordinate exits to specific guard units
    private final Map<String, GuardEntity> guardsByEscapeTile = new HashMap<>();

    // --- GAMEPLAY TRIGGERS ---
    private int currentLevel = 1;
    private boolean showingItemFound = false;

    // Warning math factors to blink UI and throw death flavor texts
    private int warningFlashTimer = 0;
    private double pulsePhaseHUD = 0;
    private String activeDeathMessage = "";

    // Internal encapsulated mechanics engines separated during Phase 3
    private final FlashlightSystem flashlightSystem = new FlashlightSystem();
    private final SoundManager soundManager = new SoundManager();

    // Narrative trigger states
    private boolean lolliRecentlyCollected = false;
    private String recentTyped = "";
    private GameRenderer.LolliRevealState lolliRevealState = null;

    // Phase 3 inventory and cooldown tick registers
    private int fruitCount = 0;
    private int eggCount = 0;
    private int invisibilityFrames = 0;
    private int knockoutFrames = 0;
    private int exitGraceFrames = 0;
    private int standStillFrames = 0;
    private int guardHitCooldownFrames = 0;
    private int footstepCooldownFrames = 0;
    private int lunaScreamCooldownFrames = 0;
    
    // Advanced mechanics flags
    private boolean hasCloneItem = false;
    private boolean wasInEscapeRoom = false;
    private boolean escapeRoomsCollapsed = false;

    // Persistent meta variables storing failure tracking across restarts 
    private int deathCount = 0;
    private int menuVisits = 0;
    private boolean showDebugOverlay = false;

    /**
     * Bootstraps the main JavaFX Application Thread UI Stage.
     */
    @Override
    public void start(Stage stage) {
        this.mainWindow = stage;
        mainWindow.setScene(createMainMenu());
        mainWindow.setTitle("Escape Pale Luna");
        mainWindow.show();
    }

    /** Re-exported main caller. */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Constructs the ominous, meta-text-changing starting menu.
     * Keeps track of visits so the game acts like it "knows" you keep retrying.
     */
    private Scene createMainMenu() {
        menuVisits++;

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: black;");

        Text title = new Text("ESCAPE PALE LUNA");
        title.setFont(Font.font("Serif", FontWeight.BOLD, 56));
        title.setFill(Color.DARKRED);
        title.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // Pulls a narrative quote sequentially linked to the amount of times the player has booted the menu
        Text subtitle = new Text(MENU_SUBTITLES[(menuVisits - 1) % MENU_SUBTITLES.length]);
        subtitle.setFont(Font.font("Serif", 19));
        subtitle.setFill(Color.GRAY);
        subtitle.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        // Core GUI buttons generated dynamically 
        Button newGameBtn = createStyledButton("> NEW GAME");
        Button exitBtn = createStyledButton("EXIT");

        newGameBtn.setOnAction(e -> playIntroAndStart());
        exitBtn.setOnAction(e -> System.exit(0));

        layout.getChildren().addAll(title, subtitle, newGameBtn, exitBtn);

        return new Scene(layout, 880, 730);
    }

    /** 
     * Renders a slideshow storyboard explaining the creepy backstory before locking the player into Level 1.
     * Uses JavaFX Timelines so the animation runs fully decoupled from an active gameplay loop.
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

        soundManager.playMusicIfPresent("/assets/audio/intro_music.wav");

        Timeline timeline = new Timeline();
        for (int i = 0; i < 6; i++) {
            String imgPath = "/assets/images/intro_" + (i + 1) + ".png";
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(i * 1.7 + 0.5), e -> {
                Image img = tryLoadImage(imgPath);
                if (img != null) {
                    cutsceneImage.setImage(img);
                }
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

    /** 
     * Applies the dark thematic standard aesthetic to all JavaFX buttons used in menus.
     * Hardcodes border and hover states directly to skip needing external CSS.
     */
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

    /** 
     * Directs the logic sequence to construct a new map level from scratch.
     * Always fires the one-shot horror sound to trigger psychological apprehension on spawn.
     */
    private void startGame(int level) {
        currentLevel = level;
        resetGameState();
        loadLevel();
        setupGameScene();
        soundManager.playOneShot(SoundManager.GAME_START, 0.75);
    }

    /** 
     * Absolute variable clean up separating game ticks. By clearing out all List states
     * here, we ensure Java GC handles old maps cleanly. Resetting input sets prevents ghost inputs.
     */
    private void resetGameState() {
        entities.clear();
        chests.clear();
        guards.clear();
        guardsByEscapeTile.clear();
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
        recentTyped = "";

        fruitCount = currentLevel == 1 ? 2 : 0;
        eggCount = currentLevel == 2 ? 5 : 0;
        invisibilityFrames = 0;
        knockoutFrames = 0;
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

    /** 
     * Initializes the map logic parser and instantiates the main player object
     * at coordinates predefined securely inside the newly mapped CSV logic arrays.
     */
    private void loadLevel() {
        maze = new Maze(MAP_FILES[currentLevel - 1], currentLevel);

        double spawnX = maze.getPlayerSpawnCol() * Maze.TILE_SIZE + 10;
        double spawnY = maze.getPlayerSpawnRow() * Maze.TILE_SIZE + Maze.Y_OFFSET + 10;
        player = new Player(spawnX, spawnY);
        entities.add(player);

        spawnEntities();
        spawnLevelThreats();
    }

    /** 
     * Iterates explicitly through the map grid looking for secondary entity codes (Chests, Pale Luna spawns)
     * and constructs the object representations while deleting the numeric triggers from the floor array. 
     */
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
            if (currentLevel == 2 && !containsContent(Item.ContentType.POTION_INVISIBILITY)) {
                type = Item.ContentType.POTION_INVISIBILITY;
            } else if (currentLevel == 3 && !containsContent(Item.ContentType.CLONE_DECOY)) {
                type = Item.ContentType.CLONE_DECOY;
            }
            Item chest = new Item(pos[1] * Maze.TILE_SIZE + 12, pos[0] * Maze.TILE_SIZE + Maze.Y_OFFSET + 12, type);
            chests.add(chest);
            entities.add(chest);
        }

        // The specific 'Lolli' chest that holds the narrative cursed item 
        for (int[] pos : lolliChestTiles) {
            Item chest = new Item(pos[1] * Maze.TILE_SIZE + 12, pos[0] * Maze.TILE_SIZE + Maze.Y_OFFSET + 12,
                    Item.ContentType.LOLLI);
            chests.add(chest);
            entities.add(chest);
        }

        // Luna is spawned dynamically off-screen matching logic token 5
        if (lunaRow >= 0) {
            paleLuna = new Monster(lunaCol * Maze.TILE_SIZE + 7.5, lunaRow * Maze.TILE_SIZE + Maze.Y_OFFSET + 7.5);
            entities.add(paleLuna);
        }
    }

    /** Helper to ensure uniqueness of puzzle items. */
    private boolean containsContent(Item.ContentType type) {
        return chests.stream().anyMatch(c -> c.getContentType() == type);
    }

    /**
     * Development History: Phase 2, Week 2.
     * Instantiates guards tracking explicitly mapped choke points leading to safe havens
     * by grabbing the tile index and spawning unique Guard units based on the active level.
     */
    private void spawnLevelThreats() {
        List<int[]> escapeRooms = maze.getTilesOfType(6);
        if (currentLevel == 1) {
            // Bats guard level 1 safe zones
            int count = Math.min(2, escapeRooms.size());
            for (int i = 0; i < count; i++) {
                int[] p = escapeRooms.get(i);
                double[] pos = findGuardPositionForEscapeTile(p[0], p[1]);
                GuardEntity bat = new GuardEntity(pos[0], pos[1], GuardEntity.Type.BAT);
                guards.add(bat);
                entities.add(bat);
                guardsByEscapeTile.put(keyForTile(p[0], p[1]), bat);
            }
        } else if (currentLevel == 2) {
            // Cobras guard level 2 safe zones
            int count = Math.min(3, escapeRooms.size());
            for (int i = 0; i < count; i++) {
                int[] p = escapeRooms.get(i);
                double[] pos = findGuardPositionForEscapeTile(p[0], p[1]);
                GuardEntity cobra = new GuardEntity(pos[0], pos[1], GuardEntity.Type.COBRA);
                guards.add(cobra);
                entities.add(cobra);
                guardsByEscapeTile.put(keyForTile(p[0], p[1]), cobra);
            }
        } else if (currentLevel == 3) {
            // Level 3 doesn't use static guards, it uses a Serial Killer stalker logic
            serialKiller = new SerialKillerEntity(18 * Maze.TILE_SIZE + 6, 15 * Maze.TILE_SIZE + Maze.Y_OFFSET + 6);
            entities.add(serialKiller);
        }
    }

    /** 
     * Pathfinding sweep pushing out radially from safe rooms to identify legitimate
     * floor patches where physical bodies can spawn without clipping walls. 
     */
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
            // Valid ground check 
            if (tile != 1 && tile != 10) {
                // Return explicitly computed render offsets relative to pixel scale
                return new double[] {
                        nc * Maze.TILE_SIZE + 10,
                        nr * Maze.TILE_SIZE + Maze.Y_OFFSET + 10
                };
            }
        }
        // Fallback directly on the tile itself
        return new double[] {
                col * Maze.TILE_SIZE + 10,
                row * Maze.TILE_SIZE + Maze.Y_OFFSET + 10
        };
    }

    /** Used by lookup dictionaries resolving entity-to-room mappings. */
    private String keyForTile(int row, int col) {
        return row + "," + col;
    }

    /**
     * Initializes the low-level graphics context and wires JavaFX physical event hooks.
     * Starts the `AnimationTimer` acting as the game's core execution cycle. 
     */
    private void setupGameScene() {
        Canvas canvas = new Canvas(880, 730);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Scene gameScene = new Scene(new Group(canvas), 880, 730, Color.BLACK);

        // Core Input listener hooking physical keystrokes into boolean arrays
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

            // Keyboard tracking meta-game mechanic
            // Keyboard tracking meta-game mechanic used for easter eggs (like type "idkfa")
            String keyText = e.getText();
            if (keyText != null && !keyText.isBlank()) {
                recentTyped += keyText.toUpperCase();
                if (recentTyped.length() > 8) {
                    recentTyped = recentTyped.substring(recentTyped.length() - 8);
                }
            }
        });

        // Release hooks to seamlessly end continuous motion keys
        gameScene.setOnKeyReleased(e -> activeKeys.remove(e.getCode()));

        if (gameLoop != null) {
            gameLoop.stop();
        }

        // Implementation of the pure tick update pipeline running 60 executions per second
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

    /** 
     * Master state update loop.
     * Manages UI timeouts, buff flags, input translations into velocity, entity AI progression,
     * bounds checking against walls/doors, and sound execution.
     */
    private void update() {
        // Halt pure game ticks when UI blocks the screen
        if (showingItemFound) {
            return;
        }

        // Handle the golden glow item collection cutscene independently
        if (lolliRevealState != null && lolliRevealState.active) {
            lolliRevealState.timer--;
            lolliRevealState.phase += 0.15;
            if (lolliRevealState.timer <= 0) {
                lolliRevealState.active = false;
                showItemFoundScreen();
            }
            return;
        }

        // --- TICK DECREMENTS ---
        if (invisibilityFrames > 0) {
            invisibilityFrames--;
        }
        if (knockoutFrames > 0) {
            knockoutFrames--;
        }
        if (exitGraceFrames > 0) {
            exitGraceFrames--;
        }
        if (guardHitCooldownFrames > 0) {
            guardHitCooldownFrames--;
        }
        if (lunaScreamCooldownFrames > 0) {
            lunaScreamCooldownFrames--;
        }

        // Ensure Level 3 Stalker processes physical steps
        if (serialKiller != null) {
            serialKiller.update();
        }

        // Run Player controller internals (Stamina UI logic)
        player.update();

        // Detect if the user is completely idle, starting the hidden AFK punish timer
        boolean movingInput = activeKeys.contains(KeyCode.W) || activeKeys.contains(KeyCode.A)
                || activeKeys.contains(KeyCode.S) || activeKeys.contains(KeyCode.D);
        if (movingInput && knockoutFrames == 0) {
            standStillFrames = 0;
        } else {
            standStillFrames++;
            if (standStillFrames == 1800) { // Approx 30 seconds
                teleportLunaNearPlayer();
            }
        }

        // Core physics injection resolving inputs to tile translation
        boolean sprinting = activeKeys.contains(KeyCode.SHIFT) && player.canSprint() && knockoutFrames == 0;
        int invert = 1;
        if (knockoutFrames == 0) {
            double beforeX = player.getX();
            double beforeY = player.getY();
            
            if (activeKeys.contains(KeyCode.W))
                player.move(0, -invert, maze, sprinting);
            if (activeKeys.contains(KeyCode.S))
                player.move(0, invert, maze, sprinting);
            if (activeKeys.contains(KeyCode.A))
                player.move(-invert, 0, maze, sprinting);
            if (activeKeys.contains(KeyCode.D))
                player.move(invert, 0, maze, sprinting);

            // Simple audio hook identifying if bounding boxes actually translated X/Y 
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

        // Escape room status resolution 
        boolean inEscapeRoom = maze.isEscapeRoom(player.getHitbox());
        int[] tilePos = maze.getTilePositionAt(player.getX() + 10, player.getY() + 10);
        boolean enteringEscapeRoom = !wasInEscapeRoom && inEscapeRoom;
        
        player.setInEscapeRoom(inEscapeRoom);
        
        // Track the exit transition specifically to collapse Level 3 safe zones permanently 
        if (wasInEscapeRoom && !inEscapeRoom) {
            exitGraceFrames = 45;
            if (currentLevel == 3 && !escapeRoomsCollapsed) {
                maze.collapseEscapeRooms();
                escapeRoomsCollapsed = true;
            }
        }
        wasInEscapeRoom = inEscapeRoom;

        // Evaluate physical AABB bounds against static drops and hostile AI
        checkChestCollisions();
        checkGuardThreats(enteringEscapeRoom, tilePos);
        updateSerialKiller();
        updatePaleLuna(inEscapeRoom);

        // Flashlight battery logic relies directly on how close Pale Luna is currently located
        double lunaDistTiles = paleLuna == null ? 99.0
                : calculateDistanceInTiles(player.getX(), player.getY(), paleLuna.getX(), paleLuna.getY());
        flashlightSystem.update(lunaDistTiles);
        lolliRecentlyCollected = false;
    }

    /** Triggers on AFK, forcibly moving Pale Luna immediately onto the player. */
    private void teleportLunaNearPlayer() {
        if (paleLuna == null) {
            return;
        }
        double px = player.getX();
        double py = player.getY();
        paleLuna.setPosition(px + 36, py);
    }

    /** Evaluates logic guarding doorways if the player transitioned inside a safe area token. */
    private void checkGuardThreats(boolean enteringEscapeRoom, int[] tilePos) {
        if (!enteringEscapeRoom || tilePos == null || guardHitCooldownFrames > 0) {
            return;
        }

        GuardEntity guard = guardsByEscapeTile.get(keyForTile(tilePos[0], tilePos[1]));
        if (guard == null) {
            return;
        }

        // Bats instantly kill if not distracted by items
        if (guard.getType() == GuardEntity.Type.BAT) {
            if (guard.isDistracted()) {
                return;
            }
            activeDeathMessage = "The bat bit first. Luna answered instantly.";
            triggerDeath();
            return;
        }

        // Cobras act as massive movement debuffs ("Knockouts") if eggs are not dropped
        if (guard.getType() == GuardEntity.Type.COBRA) {
            if (guard.consumeCobraEntryPass()) {
                return;
            }
            knockoutFrames = 300;
            guardHitCooldownFrames = 240;
        }
    }

    /** 
     * Item usage system targeting closely adjacent guard entities depending on currently held objects.
     */
    private void tryUseDistraction() {
        GuardEntity nearest = null;
        double best = Double.MAX_VALUE;

        for (GuardEntity guard : guards) {
            if (guard.getType() == GuardEntity.Type.BAT && guard.isDistracted()) {
                continue;
            }
            double d = calculateDistanceInTiles(player.getX(), player.getY(), guard.getX(), guard.getY());
            if (d < best) {
                best = d;
                nearest = guard;
            }
        }

        // Prefer nearby target, but allow fallback to nearest guard so E is always
        // actionable.
        if (nearest == null) {
            return;
        }

        // boolean nearby = best <= 3.0;

        if (nearest.getType() == GuardEntity.Type.BAT) {
            if (fruitCount > 0) {
                fruitCount--;
                nearest.distract();
            }
        } else {
            if (eggCount > 0) {
                eggCount--;
                nearest.grantCobraEntryPass();
            }
        }
    }

    /**
     * Executes Level 3 specific item usage, placing a cardboard decoy
     * into the entities array to bait the Serial Killer AI away from the player.
     */
    private void tryPlaceClone() {
        if (!hasCloneItem || currentLevel != 3 || cloneDecoy != null) {
            return;
        }
        cloneDecoy = new CardboardClone(player.getX() + 5, player.getY() + 5);
        entities.add(cloneDecoy);
        hasCloneItem = false;
    }

    /** 
     * Iterates through all items scattered on the floor, doing overlapping rectangle checks.
     * Manages applying buffs (Invisibility, Clone tools) directly into the main Application state.
     */
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
            
            // Core objective hit
            if (content == Item.ContentType.LOLLI) {
                lolliRecentlyCollected = true;
                soundManager.playOneShot(SoundManager.STINGER_1, 0.8);
                // Triggers the blocking golden animation pause rendering
                lolliRevealState = new GameRenderer.LolliRevealState(chest.getX(), chest.getY(), LOLLI_REVEAL_DURATION);
                return;
            }
            
            if (content == Item.ContentType.POTION_INVISIBILITY) {
                invisibilityFrames = 360; // 6 seconds
            }
            if (content == Item.ContentType.CLONE_DECOY) {
                hasCloneItem = true;
            }
        }
    }

    /** Manages the fast-paced relentless pursuit AI unique to Level 3. */
    private void updateSerialKiller() {
        if (serialKiller == null) {
            return;
        }

        // Wait until player crosses a specific horizontal mapped threshold before activating
        if (!serialKiller.isActive()) {
            double triggerDist = calculateDistanceInTiles(player.getX(), player.getY(), 10 * Maze.TILE_SIZE,
                    9 * Maze.TILE_SIZE + Maze.Y_OFFSET);
            if (triggerDist < 2.0) {
                serialKiller.setActive(true);
            }
        }

        if (!serialKiller.isActive()) {
            return;
        }

        // Logic routing depending on whether the clone item is currently dropped
        double targetX = player.getX();
        double targetY = player.getY();
        if (cloneDecoy != null) {
            targetX = cloneDecoy.getX();
            targetY = cloneDecoy.getY();
        }

        // Let the entity itself run pathfinding checks
        serialKiller.updateChase(targetX, targetY, maze);

        // Terminate decoy if intersecting 
        if (cloneDecoy != null && serialKiller.getHitbox().intersects(cloneDecoy.getHitbox())) {
            serialKiller.startDecoyAttack();
            entities.remove(cloneDecoy);
            cloneDecoy = null;
        }

        // Lethal hit check
        if (serialKiller.getHitbox().intersects(player.getHitbox())) {
            activeDeathMessage = "Steel and panic. He never stops hunting.";
            triggerDeath();
        }
    }

    /** 
     * Master AI loop for the titular antagonist. Handles passing down cloak flags,
     * processing line-of-sight penalties, and snapping the audio engine if speed jumps into HUNTING mode.
     */
    private void updatePaleLuna(boolean inEscapeRoom) {
        if (paleLuna == null) {
            return;
        }

        Monster.State prevState = paleLuna.getState();

        double targetX = player.getX();
        double targetY = player.getY();
        // Break her tracking arrays by physically jumping the target node massive offsets offscreen
        if (invisibilityFrames > 0) {
            targetX = player.getX() + 200;
            targetY = player.getY() + 200;
        }

        paleLuna.update(targetX, targetY, inEscapeRoom, lolliRecentlyCollected, maze);

        // Core adrenaline injection when state switches unprompted to Sprint execution 
        if (prevState != Monster.State.HUNTING && paleLuna.getState() == Monster.State.HUNTING) {
            warningFlashTimer = 30;
            soundManager.playOneShot(SoundManager.HEARTBEAT_FAST, 0.45);
        }
        if (warningFlashTimer > 0) {
            warningFlashTimer--;
        }

        // Ties visualization heartbeat UI directly to the monster's active flag
        player.setBeingChased(paleLuna.isHunting());

        // Audio system proximity alarms
        double lunaDistTiles = calculateDistanceInTiles(player.getX(), player.getY(), paleLuna.getX(), paleLuna.getY());
        if (paleLuna.isHunting() && lunaDistTiles <= 3.0 && lunaScreamCooldownFrames <= 0) {
            soundManager.playOneShot(SoundManager.LUNA_SCREAM_NEARBY, 0.8);
            lunaScreamCooldownFrames = 130;
        }

        // Standard instant-kill check 
        if (!inEscapeRoom && invisibilityFrames == 0 && paleLuna.isHunting()
                && player.getHitbox().intersects(paleLuna.getHitbox())) {
            activeDeathMessage = "She found your pulse before you heard her footsteps.";
            triggerDeath();
            return;
        }

        // Special punitive mechanic: If she parks outside a door, she uses pure line of sight
        // cast rays. If the player steps out and the ray hits, instant execution regardless of speed.
        if (!inEscapeRoom && invisibilityFrames == 0 && paleLuna.isWaitingAtDoor()) {
            boolean canSee = maze.hasLineOfSight(
                    paleLuna.getX() + 12, paleLuna.getY() + 12,
                    player.getX() + 10, player.getY() + 10);
            if (canSee) {
                activeDeathMessage = "She waited at the door. You stepped out anyway.";
                triggerDeath();
            }
        }
    }

    /** 
     * Boots control flow out to the decoupled Phase 3 visual pipelines (GameRenderer and HUDRenderer).
     * Maintains pulsePhase timings across frames natively so UI glowing animations remain seamless.
     */
    private void render(GraphicsContext gc) {
        double lunaDistTiles = paleLuna == null ? 99.0
                : calculateDistanceInTiles(player.getX(), player.getY(), paleLuna.getX(), paleLuna.getY());
        boolean lunaNearby = lunaDistTiles <= 5.0;

        pulsePhaseHUD = GameRenderer.render(gc, maze, entities, paleLuna, player,
                warningFlashTimer, lolliRevealState, currentLevel, chests, ITEM_NAMES,
                flashlightSystem, lunaNearby,
                false, fruitCount, eggCount, hasCloneItem,
                invisibilityFrames, knockoutFrames, pulsePhaseHUD);

        if (showDebugOverlay) {
            drawDebugOverlay(gc, lunaNearby);
        }
    }

    /** Developer terminal drawn on top of HUD when F3 is depressed. Plots internal AI trackers. */
    private void drawDebugOverlay(GraphicsContext gc, boolean lunaNearby) {
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
                "Knockout=" + knockoutFrames + " Invis=" + invisibilityFrames + " GuardCD=" + guardHitCooldownFrames
        };

        double y = 76;
        for (String line : lines) {
            gc.fillText(line, 16, y);
            y += 18;
        }
    }

    /** Simple standard geometric math resolving pixel differences to normalized maze tiles mapping limits. */
    private double calculateDistanceInTiles(double x1, double y1, double x2, double y2) {
        double dx = (x1 - x2) / Maze.TILE_SIZE;
        double dy = (y1 - y2) / Maze.TILE_SIZE;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /** Flow state bridging either victory screen execution or jumping the counter to build the next map room. */
    private void advanceLevel() {
        if (currentLevel >= 3) {
            triggerVictoryCutscene();
        } else {
            showLevelTransition();
        }
    }

    /**
     * Pauses the game loop entirely. Clears out the JavaFX scene tree to drop raw VBox 
     * newspaper clippings outlining the narrative progression bridging the individual maps. 
     */
    /**
     * Pauses the game loop entirely. Clears out the JavaFX scene tree to drop raw VBox 
     * newspaper clippings outlining the narrative progression bridging the individual maps. 
     */
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
        title.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Text h = new Text(headlines[Math.min(currentLevel - 1, headlines.length - 1)]);
        h.setFont(Font.font("Serif", FontWeight.BOLD, 24));
        h.setFill(Color.rgb(170, 60, 60));
        h.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Button next = createStyledButton("CONTINUE");
        next.setOnAction(e -> startGame(currentLevel + 1));

        layout.getChildren().addAll(title, h, next);
        mainWindow.setScene(new Scene(layout, 880, 730));
    }

    /** 
     * Interstitial state triggered identically whenever the core 'Lolli' token object
     * is physically touched by the bounds checking engine. Serves narrative exposition.
     */
    private void showItemFoundScreen() {
        isPlaying = false;
        if (gameLoop != null) {
            gameLoop.stop();
        }
        showingItemFound = true;

        VBox layout = new VBox(25);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: black;");

        Text paleLunaText = new Text("pale luna smiles wide...");
        paleLunaText.setFont(Font.font("Serif", FontWeight.BOLD, 28));
        paleLunaText.setFill(Color.rgb(120, 0, 0));
        paleLunaText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Text mainText = new Text(ITEM_FOUND_MAIN_TEXT[currentLevel - 1]);
        mainText.setFont(Font.font("Serif", FontWeight.BOLD, 65));
        mainText.setFill(Color.DARKRED);
        mainText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        String[] itemDescriptions = {
                "The earth was soft that night. Too soft. Like it was waiting for her.",
                "The blade bit into the ground. Each scoop made a sound like breathing.",
                "She did not struggle at the end. Her eyes were wide open. Smiling."
        };

        Text descText = new Text(itemDescriptions[currentLevel - 1]);
        descText.setWrappingWidth(700);
        descText.setFont(Font.font("Serif", 19));
        descText.setFill(Color.rgb(170, 150, 145));
        descText.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Button continueBtn = new Button(ITEM_FOUND_BUTTON_TEXT[currentLevel - 1]);
        continueBtn.setFont(Font.font("Serif", FontWeight.BOLD, 28));
        String normalStyle = "-fx-background-color: #1a0000; -fx-text-fill: #cc0000; -fx-border-color: #660000; -fx-border-width: 2px; -fx-padding: 12 50; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: #330000; -fx-text-fill: #ff3333; -fx-border-color: #990000; -fx-border-width: 2px; -fx-padding: 12 50; -fx-cursor: hand;";
        continueBtn.setStyle(normalStyle);
        continueBtn.setOnMouseEntered(e -> continueBtn.setStyle(hoverStyle));
        continueBtn.setOnMouseExited(e -> continueBtn.setStyle(normalStyle));
        continueBtn.setOnAction(e -> {
            showingItemFound = false;
            advanceLevel();
        });

        layout.getChildren().addAll(paleLunaText, mainText, descText, new Text(""), continueBtn);
        mainWindow.setScene(new Scene(layout, 880, 730));
    }

    /** 
     * Halts all logic, fires jump scare sound, increments meta-variables punishing repeats.
     * Rebuilds the primary Stage dynamically passing custom death messages resolved closely
     * during the previous collision tick.
     */
    private void triggerDeath() {
        isPlaying = false;
        if (gameLoop != null) {
            gameLoop.stop();
        }
        deathCount++;
        soundManager.playOneShot(SoundManager.GAME_OVER, 0.85);

        VBox layout = new VBox(14);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: black;");

        String[] deathLines = {
                "pale luna smiles wide",
                "there is no escape",
                "pale luna smiles wide",
                "no more lollies to take",
                "pale luna smiles wide",
                "now you are dead"
        };

        Text title = new Text("YOU DIED");
        title.setFont(Font.font("Serif", FontWeight.BOLD, 70));
        title.setFill(Color.DARKRED);
        title.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        layout.getChildren().add(title);

        for (String line : deathLines) {
            Text t = new Text(line);
            t.setFont(Font.font("Serif", 21));
            t.setFill(Color.LIGHTGRAY);
            t.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            layout.getChildren().add(t);
        }

        if (!activeDeathMessage.isBlank()) {
            Text detail = new Text(activeDeathMessage);
            detail.setFont(Font.font("Serif", FontWeight.BOLD, 18));
            detail.setFill(Color.rgb(190, 130, 130));
            detail.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            layout.getChildren().add(detail);
        }

        // Meta mechanics hook directly modifying visuals based purely on number of retries
        if (deathCount >= 5) {
            Text extra = new Text("You keep coming back. She likes that.");
            extra.setFont(Font.font("Serif", FontWeight.BOLD, 18));
            extra.setFill(Color.rgb(200, 70, 70));
            extra.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
            layout.getChildren().add(extra);
        }

        Button restartBtn = createStyledButton("RESTART FROM LEVEL 1");
        restartBtn.setOnAction(e -> playIntroAndStart());

        Button menuBtn = createStyledButton("MAIN MENU");
        menuBtn.setOnAction(e -> mainWindow.setScene(createMainMenu()));

        layout.getChildren().addAll(new Text(""), restartBtn, menuBtn);
        mainWindow.setScene(new Scene(layout, 880, 730));
    }

    /** 
     * Uses JavaFX Timelines to chain a set of chronological image swaps
     * wrapping up the ending narrative for finishing the full 3 Map sequence. 
     */
    private void triggerVictoryCutscene() {
        isPlaying = false;
        if (gameLoop != null) {
            gameLoop.stop();
        }

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
                if (img != null) {
                    cutsceneImage.setImage(img);
                }
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

    /** Displayed indefinitely after the victory animation completes. */
    private void showVictoryScreen() {
        VBox layout = new VBox(14);
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: black;");

        String[] victoryLines = {
                "pale luna smiles wide,",
                "the ground is soft,",
                "pale luna smiles wide,",
                "there is a hole,",
                "pale luna smiles wide,",
                "tie her up with rope,",
                "congratulations! you have escaped from pale luna"
        };

        Text title = new Text("YOU ESCAPED");
        title.setFont(Font.font("Serif", FontWeight.BOLD, 60));
        title.setFill(Color.LIMEGREEN);
        title.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        layout.getChildren().add(title);

        for (String line : victoryLines) {
            Text t = new Text(line);
            t.setFont(Font.font("Serif", line.startsWith("congratulations") ? FontWeight.BOLD : FontWeight.NORMAL,
                    line.startsWith("congratulations") ? 24 : 20));
            t.setFill(line.startsWith("congratulations") ? Color.GOLD : Color.LIGHTGRAY);
        t.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
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
            if (url != null) {
                return new Image(url.toExternalForm());
            }
        } catch (Exception e) {}
        return null;
    }
}
