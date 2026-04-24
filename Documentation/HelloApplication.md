# HelloApplication.java - Complete Structural Analysis

## 1. The "Elevator Pitch" (High-Level Overview)

**HelloApplication** is the **central nervous system** of your entire game. It's the main JavaFX Application class that orchestrates every major flow: the main menu, the intro cutscene, the game loop, UI rendering, death screens, and victory sequences. Think of it as the **game director**—it doesn't do the actual gameplay or drawing, but it controls *when* things happen, *what happens next*, and *how* everything transitions. It's the beating heart that keeps your game alive, frame by frame.

---

## 2. Core Computer Science Concepts

### **Design Patterns Used:**
- **JavaFX Application Lifecycle Pattern:** HelloApplication extends `Application`, which is JavaFX's framework pattern for GUI applications
- **State Machine Pattern:** The game flows through distinct states (MainMenu → Intro → Level1 → Level2 → Level3 → Victory), and HelloApplication manages these transitions
- **Game Loop / Animation Loop:** Uses `AnimationTimer` to create a continuous loop that updates and renders at 60 FPS
- **Composition:** Rather than inheriting from GameStateManager, HelloApplication *contains* it as a field (`gsm`), promoting loose coupling
- **Strategy Pattern:** Different render methods (`render()`, `renderDeathAnimation()`) can be swapped based on game state

### **Why These Concepts Matter:**
- **State Machine:** Games are inherently stateful. By thinking of your game as a series of states with defined transitions, your code becomes predictable and easier to debug
- **Animation Loop:** The game loop is what makes the game *feel* like a continuous experience. Without it, you'd need manual button clicks for each frame
- **Composition over Inheritance:** GameStateManager handles the *model* (game logic), while HelloApplication handles the *view and control flow*. This separation keeps code organized

---

## 3. Deep Dive: Variables and State

### **Critical Instance Variables:**

| Field | Type | Purpose |
|-------|------|---------|
| `mainWindow` | `Stage` | The main window/window object. Stores reference to the JavaFX window so HelloApplication can change scenes |
| `gameLoop` | `AnimationTimer` | The 60 FPS game loop. When running, it calls `handle()` 60 times per second |
| `isPlaying` | `boolean` | Flag that tracks whether the game is actively running vs. paused in menu/cutscene |
| `activeKeys` | `Set<KeyCode>` | Set of keys being held down *right now*. Used to track continuous input (e.g., holding arrow keys) |
| `pressedThisFrame` | `Set<KeyCode>` | Set of keys pressed *this specific frame*. Clears each frame to detect one-time presses (e.g., pressing E once) |
| `gsm` | `GameStateManager` | **The most important field.** This object contains all game logic: entities, collision, levels, state tracking |
| `deathCount` | `int` | Tracks how many times the player died in the current play session |
| `showDebugOverlay` | `boolean` | When true, displays debug information (toggled by pressing F3) |
| `ITEM_NAMES` | `String[]` | Array of item names ("Mud", "Shovel", "Rope") used in UI display |

### **Why This State Matters:**
- **`activeKeys` vs `pressedThisFrame`:** These are different! `activeKeys` stays populated while a key is held down, while `pressedThisFrame` is a one-time event each frame. This distinction lets you handle "held movement" differently from "pressed interact"
- **`gsm`:** This is your *single source of truth* for all game state. Everything about the game world lives here
- **`gameLoop`:** This is a reference you keep because you need to stop it (call `.stop()`) when transitioning away from gameplay

---

## 4. Deep Dive: Methods and Logic (Step-by-Step)

### **Method 1: `start(Stage stage)` [JavaFX Lifecycle]**

**The Goal:** 
Initialize the JavaFX window and display the main menu. This is called automatically by the JavaFX framework after the Application is instantiated.

**How it Works (Layman's Terms):**
1. Store the `Stage` (the window object) so we can change scenes later
2. Set the window title to "Escape Pale Luna"
3. Create the main menu scene and display it
4. Show the window to the user

**Why it Works:**
- **`@Override`:** We're overriding JavaFX's framework method, which guarantees it will be called at the right time
- **`this.mainWindow = stage`:** We save the Stage so future methods can change what's displayed
- **`mainWindow.setScene()`:** Replaces what's currently shown on screen
- **`mainWindow.show()`:** Makes the window visible

---

### **Method 2: `createMainMenu()` [Scene Creation]**

**The Goal:** 
Build and return the main menu Scene, displaying the title, buttons, and background image.

**How it Works (Layman's Terms):**
1. Initialize UI images using SceneFactory
2. Create a black background StackPane as the root container
3. Load and display the background image (with 40% opacity so it's visible but not overwhelming)
4. Create a VBox (vertical container) to hold menu elements
5. Add title and subtitle text
6. Create "New Game" and "Exit" buttons with custom icons
7. Wire button click handlers: New Game → plays intro, Exit → closes program
8. Put buttons in an HBox (horizontal container)
9. Add everything to the root
10. Return a new Scene wrapping all this

**Why it Works:**
- **StackPane:** A container that layers elements on top of each other. Background image goes first, then UI on top
- **VBox/HBox:** Organize elements vertically and horizontally. Easier than manually positioning everything
- **`setOnAction(e -> ...)`:** Lambda expression that defines what happens when a button is clicked
- **`SceneFactory.getMenuBackgroundImg()`:** This is a helper class that loads images. Good practice: don't hardcode image paths in UI code

---

### **Method 3: `playIntroAndStart()` [Cutscene Management]**

**The Goal:** 
Display the intro cutscene (images that transition every ~1.7 seconds), then start the game. Also allows player to skip by pressing Enter.

**How it Works (Layman's Terms):**
1. Create a new Scene with a black background and an ImageView to hold cutscene frames
2. Start playing intro music
3. Create a Timeline (a sequence of timed events)
4. For each of the 6 intro images:
   - Schedule it to appear after a calculated delay (i * 1.7 seconds)
   - When time comes, load and display that image
5. Schedule a final event at 11.5 seconds to transition to loading screen
6. Set up keyboard listener: if player presses Enter, skip the cutscene
7. Display the scene on screen
8. Play the timeline animation

**Why it Works:**
- **Timeline:** JavaFX's way to schedule multiple events with precise timing
- **`Duration.seconds(i * 1.7 + 0.5)`:** Math for timing. Each image appears 1.7 seconds apart, with a 0.5 second offset
- **`scene.setOnKeyPressed()`:** Captures keyboard input while the cutscene is playing
- **`if (e.getCode() == KeyCode.ENTER)`:** Checks which key was pressed; Enter allows skipping
- **Lambda in Timeline:** `e -> { /* load image */ }` schedules code to run at a specific time
- **`PauseTransition(Duration.millis(50))`:** A tiny delay before calling `startGame()`, giving the UI time to update

---

### **Method 4: `startGame(int level)` [Game Initialization]**

**The Goal:** 
Prepare the game for a specific level: reset state, spawn entities, load the map, preserve player stats across levels.

**How it Works (Layman's Terms):**
1. If player exists from a previous level, save their Sanity value
2. Save total playtime
3. Tell LevelManager which level to load
4. Reset all game state (clear entities, reset timers, etc.)
5. Clear input tracking (forget which keys were pressed)
6. Tell GameStateManager to load the level (spawn entities, load map)
7. If returning to a level with more than 1, restore the saved Sanity value
8. Restore saved playtime
9. Set `isPlaying = true` so the game loop knows to run
10. Call `setupGameScene()` to create the game canvas and start the loop
11. Play sound effects (game start sound, ambient music)

**Why it Works:**
- **Saving/restoring player stats:** When progressing from Level 1 → 2 → 3, the player's health shouldn't reset
- **`gsm.resetGameState()`:** Clears old entities and timers so each level starts fresh
- **`activeKeys.clear()`:** Prevents input from the menu being interpreted as game input
- **Level progression logic:** `if (level > 1)` — only restore stats if we're not on Level 1

---

### **Method 5: `setupGameScene()` [Game Loop Creation]**

**The Goal:** 
Create the game Canvas, wire input handlers, create the game loop, and display the game screen.

**How it Works (Layman's Terms):**
1. Create a Canvas (drawing surface) 880x730 pixels
2. Get the GraphicsContext (drawing pen for the canvas)
3. Create a Scene with the canvas
4. Set up keyboard listeners:
   - When key pressed: add to `activeKeys`, add to `pressedThisFrame`, check for special keys (E for distraction, C for clone, F3 for debug)
   - When key released: remove from `activeKeys`
5. Stop any existing game loop (from previous gameplay)
6. Create a new AnimationTimer (60 FPS loop):
   - Each frame: call `gsm.update(activeKeys)` to update game logic
   - If player died: show death animation then trigger death screen
   - If item just found: show item found screen
   - Otherwise: render normal gameplay
   - Clear `pressedThisFrame` at end of frame
7. Start the loop
8. Display the game scene

**Why it Works:**
- **Canvas + GraphicsContext:** Low-level drawing. Canvas is the blank page; GraphicsContext is the drawing pen
- **AnimationTimer:** Calls `handle(long now)` ~60 times per second, creating smooth animation
- **`activeKeys` tracking:** By maintaining a Set of currently-pressed keys, you can check `if (activeKeys.contains(KeyCode.LEFT))` to handle smooth continuous movement
- **`pressedThisFrame` clearing:** Each frame, one-time events are recorded then cleared, so pressing E once doesn't trigger distraction every frame
- **Conditional rendering:** Different game states (playing, dead, found item) render differently

---

### **Method 6: `render(GraphicsContext gc)` [Frame Rendering]**

**The Goal:** 
Calculate screen effects (vignette based on Sanity), delegate to GameRenderer to draw everything, update animation state.

**How it Works (Layman's Terms):**
1. Calculate vignette intensity (darkening around edges) based on player's Sanity:
   - If Sanity < 25: start darkening the edges
   - More Sanity lost = darker vignette (fear effect)
2. Call `GameRenderer.render()` to draw all game elements (map, entities, HUD):
   - Pass the maze, all entities, player, camera position, UI text, etc.
   - This returns `pulsePhaseHUD`, a timer for animated UI pulsing
3. If debug overlay is enabled (F3 pressed), draw debug info on top
4. Store the returned `pulsePhaseHUD` for next frame

**Why it Works:**
- **Sanity → Vignette:** Low Sanity creates visual feedback (edges darken) without expensive fancy effects. This is a **psychological gameplay mechanic**
- **Delegation to GameRenderer:** HelloApplication doesn't draw sprites, tiles, or HUD. It delegates to the specialized renderer. This is **good separation of concerns**
- **`pulsePhaseHUD` tracking:** Animation needs to be smooth across frames. By storing and updating this value, animations don't restart every frame

---

### **Method 7: `renderDeathAnimation(GraphicsContext gc, double framesRemaining)` [Death Visual Effect]**

**The Goal:** 
Render a dramatic death animation: black circle expanding from center, color flashing, jitter effect.

**How it Works (Layman's Terms):**
1. Calculate progress as a percentage (0.0 = start, 1.0 = complete)
2. Calculate expanding black circle radius based on progress (uses power of 1.5 for easing)
3. Add random jitter to circle center (makes it shake)
4. Randomly flash the screen red for dramatic effect
5. Create a radial gradient (smooth transition from black at center to blood-red at edges)
6. Draw this gradient across the entire screen
7. Randomly add additional red flash on top

**Why it Works:**
- **Radial Gradient:** `RadialGradient` creates a smooth color transition from center outward, making the death feel organic
- **`1.0 - Math.pow(progress, 1.5)`:** Non-linear easing. The circle expands slow at first, then fast, then slow again (easing out). This *feels* better than linear motion
- **Random jitter:** `(Math.random() - 0.5) * jitterStrength` creates screen shake. Subtracting 0.5 centers the random value
- **Random red flashes:** Creates disorientation and urgency

---

### **Method 8: `advanceLevel()` [Level Progression]**

**The Goal:** 
Move to the next level or trigger victory if all 3 levels are complete.

**How it Works (Layman's Terms):**
1. Check if current level >= 3
2. If so: trigger victory cutscene (game won!)
3. Otherwise: increment level counter and start the next level

**Why it Works:**
- Simple but critical: this is the gate between levels
- **`gsm.startingDistractions = gsm.distractionSpellCount`:** Save the player's current distraction count to carry over to next level

---

### **Method 9: `showItemFoundScreen()` [Item Pickup UI]**

**The Goal:** 
Pause gameplay and show the "Item Found!" screen when player collects a key item (Lollipop).

**How it Works (Layman's Terms):**
1. Stop gameplay (`isPlaying = false`)
2. Stop the game loop
3. Mark that item screen is showing
4. Ask SceneFactory to create an "Item Found" scene
5. Display that scene
6. When player acknowledges: call `advanceLevel()` to progress

**Why it Works:**
- This is a **pause point** in gameplay. The game loop stops, nothing updates
- The callback `() -> { gsm.showingItemFound = false; advanceLevel(); }` defines what happens after

---

### **Method 10: `triggerDeath()` [Death Handling]**

**The Goal:** 
Stop gameplay, increment death counter, play death sound, show death screen with stats.

**How it Works (Layman's Terms):**
1. Stop gameplay and game loop
2. Increment death counter
3. Play "Game Over" sound effect
4. Calculate stats to display:
   - Lollipops collected (current level - 1)
   - Player's current Sanity
5. Ask SceneFactory to create death screen scene:
   - Display death message, death count, stats
   - Provide buttons: "Retry" (play intro again) or "Main Menu"
6. Display death scene

**Why it Works:**
- **Death is a state transition:** Playing → Dead screen → Menu or Retry
- **Stats calculation:** `gsm.levelManager.getCurrentLevel() - 1` = lollipops collected (you get 1 per completed level)

---

### **Method 11: `triggerVictoryCutscene()` [Victory Animation]**

**The Goal:** 
Play the victory cutscene (5 images transitioning), then show final victory screen.

**How it Works (Layman's Terms):**
1. Stop gameplay and game loop
2. Create a scene with black background and image viewer
3. Start playing outro music
4. Create a timeline:
   - For each of 5 victory images: schedule them to appear with 1.4 second spacing
   - At 8.5 seconds: stop music and show victory stats screen
5. Allow player to skip with Enter key
6. Play the timeline

**Why it Works:**
- **Mirrors the intro structure:** Similar timing system. This is consistent game design

---

### **Method 12: `showVictoryScreen()` [Victory Stats]**

**The Goal:** 
Display the victory screen showing final statistics (lollipops, deaths, playtime, etc.).

**How it Works (Layman's Terms):**
1. Calculate final Sanity value
2. Ask SceneFactory to create victory scene with stats:
   - Level completed (always 3)
   - Total lollipops collected
   - Chests encountered
   - Death count
   - Final Sanity
   - Total playtime
3. Include button to return to main menu
4. Display scene

**Why it Works:**
- This is the **final reward screen**. Show the player their accomplishments
- Loops back to main menu, enabling replay

---

## 5. Deep Dive: Model-View-Controller (MVC) Pattern

**Where HelloApplication Fits in MVC:**

| MVC Layer | HelloApplication's Role |
|-----------|------------------------|
| **Model** | ❌ No. GameStateManager is the model |
| **View** | ✅ **Yes!** HelloApplication orchestrates what's displayed (scenes, cutscenes, game rendering) |
| **Controller** | ✅ **Yes!** HelloApplication handles input (keyboard) and decides what happens next (state transitions) |

**The MVC Flow in Your Game:**
```
User Input (keyboard)
    ↓
[HelloApplication captures it]
    ↓
[HelloApplication.gameLoop calls gsm.update(activeKeys)]
    ↓
[GameStateManager updates MODEL state]
    ↓
[HelloApplication calls render()]
    ↓
[GameRenderer draws the VIEW]
    ↓
[Scene displayed to player]
```

**Key Insight:** HelloApplication is the **Controller-View** glue. It's not pure MVC because JavaFX blurs the lines between view and controller. HelloApplication:
- **Controls:** Input handling, scene transitions, game loop timing
- **Views:** Which scene is displayed, how to render each frame
- **Delegates to Model:** GameStateManager handles all game logic

---

## 6. Lab Final Presentation Arsenal

Here are 4 professional talking points for your lab presentation:

• **"HelloApplication exemplifies the Model-View-Controller architectural pattern by serving as the primary Controller-View mediator. It orchestrates input handling through key event listeners, maintains a Set-based tracking system for distinguishing continuous input (activeKeys) from discrete frame-events (pressedThisFrame), and delegates all model-level game state to the GameStateManager, ensuring clean separation of concerns between user interaction, rendering logic, and game simulation."**

• **"The game loop is implemented via JavaFX's AnimationTimer, which invokes a handle() method approximately 60 times per second, creating a continuous cycle of state updates and rendering. This pattern is fundamental to game development; by decoupling input capture, state updates, and rendering into discrete phases, the architecture remains stable even when frame rates fluctuate or rendering becomes expensive."**

• **"State transitions between major game phases—main menu, intro cutscene, active gameplay, death screen, and victory cutscene—are managed by HelloApplication through explicit method calls (createMainMenu(), playIntroAndStart(), startGame(), triggerDeath(), triggerVictoryCutscene()). This explicit state machine design makes the game flow predictable, debuggable, and easy to extend with new game states."**

• **"The death animation implementation demonstrates advanced graphics programming techniques: I calculated non-linear easing curves using power functions (Math.pow(progress, 1.5)) to create organic motion, applied radial gradients for smooth color transitions, incorporated procedural randomization for jitter and flash effects, and utilized JavaFX's transformation capabilities—all while maintaining 60 FPS performance by rendering entirely in a single GraphicsContext call."**

---

## 7. Critical Implementation Details

### **Input Handling: The Two-Set System**
Your keyboard system is sophisticated:
```java
// Key is held down RIGHT NOW
if (activeKeys.contains(KeyCode.LEFT)) { /* move left */ }

// Key was pressed THIS FRAME (one-time event)
if (pressedThisFrame.contains(KeyCode.E)) { /* use distraction */ }

// At end of frame:
pressedThisFrame.clear();  // Forget one-time events
// activeKeys is NOT cleared — key is still held
```

This is why movement feels smooth (continuous key checking) while abilities activate once per press (one-time event checking).

### **State Preservation Across Levels**
When progressing Level 1 → 2 → 3:
```java
int savedSanity = gsm.entityManager.getPlayer().getSanity();
double savedTime = gsm.totalPlayTimeSeconds;
// ... do level reset ...
gsm.entityManager.getPlayer().setSanity(savedSanity);  // Restore
gsm.totalPlayTimeSeconds = savedTime;                   // Restore
```

This lets players maintain their progression—their health doesn't reset, their time keeps counting.

### **The Animation Timer Loop**
```java
gameLoop = new AnimationTimer() {
    @Override
    public void handle(long now) {
        if (isPlaying) {
            gsm.update(activeKeys);   // Model updates
            render(gc);               // View renders
            pressedThisFrame.clear(); // Reset one-time inputs
        }
    }
};
gameLoop.start();  // Runs ~60 times/second
```

This is the **heartbeat** of your game. Every frame: update → render → clear. This pattern is used in virtually every game engine.

---

## 8. Common Patterns to Notice

| Pattern | Where Used | Why |
|---------|-----------|-----|
| **Delegation** | Calls `GameRenderer.render()` | Don't duplicate drawing code |
| **Composition** | Contains `GameStateManager` field | Avoid tight coupling with inheritance |
| **Finite State Machine** | Menu → Intro → Game → Death/Victory | Games are inherently stateful |
| **Observer Pattern** | Button.setOnAction() | Respond to events (button clicks) |
| **Strategy Pattern** | Different render methods for different states | Choose rendering strategy based on game state |

---

## 9. Key Takeaway

**HelloApplication is your game's director and stage manager combined.** It doesn't implement gameplay logic, but it orchestrates everything: when cutscenes play, when the game updates, when to show which screen. It bridges the gap between:
- **Low-level:** Input events, frame rendering, timing
- **High-level:** Game flow, scene transitions, player experience

This is exactly what you want in a well-designed game architecture.
