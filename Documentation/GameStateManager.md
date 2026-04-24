# GameStateManager.java - Complete Structural Analysis

## 1. The "Elevator Pitch" (High-Level Overview)

**GameStateManager** is the **brain and nervous system** of your entire game. Think of it as the **game's real-time simulation engine**. While HelloApplication controls the UI flow (menus, transitions, scenes), GameStateManager handles all the actual *game logic*: entity updates, collision detection, player input processing, inventory management, timing systems, and special events (death, item pickup, Luna's behavior). Every frame (60 times per second), GameStateManager updates the current game state and determines if the player should win, die, or continue playing. It's the single source of truth for everything happening in the game world.

---

## 2. Core Computer Science Concepts

### **Design Patterns Used:**
- **Facade Pattern:** GameStateManager acts as a simplified interface to complex subsystems (EntityManager, CollisionSystem, LevelManager, SoundManager). HelloApplication calls `gsm.update()` instead of directly managing all these systems
- **State Machine:** Tracks multiple game states simultaneously (player death status, item reveal state, escape room status, cooldowns)
- **Observer Pattern (Event-Based):** Events trigger state changes (player touches chest → item collected; player near Luna → heartbeat plays)
- **Composition:** Contains instances of EntityManager, CollisionSystem, LevelManager, SoundManager rather than inheriting from them
- **Temporal State Pattern:** Manages time-based effects (cooldowns, timers, animations) using frame-delta calculations

### **Why These Concepts Matter:**
- **Facade:** GameStateManager shields HelloApplication from complex subsystem details. HelloApplication doesn't need to know about collision systems or guard logic—it just calls `update()`
- **State Machine:** Games have multiple overlapping states. Some are exclusive (alive vs. dead), others are overlapping (in escape room + near Luna). Managing this explicitly prevents bugs
- **Delta Time:** Using nanosecond precision and calculating `timeDelta` ensures smooth gameplay regardless of frame rate fluctuations
- **Composition:** This allows each system to be independently testable and modifiable

---

## 3. Deep Dive: Variables and State

### **Critical Instance Variables - Game Stats:**

| Field | Type | Purpose |
|-------|------|---------|
| `totalChestsCollected` | `int` | Cumulative count across all levels |
| `totalChestsEncountered` | `int` | Total chests seen (for calculating collection rate) |
| `totalPlayTimeSeconds` | `double` | Total playtime across all levels (persists across death/retry) |

### **Critical Instance Variables - System References:**

| Field | Type | Purpose |
|-------|------|---------|
| `entityManager` | `EntityManager` | Manages all entities (player, enemies, items, torches) |
| `levelManager` | `LevelManager` | Manages current level, maze, tile data |
| `collisionSystem` | `CollisionSystem` | Detects and resolves collisions, checks win/lose conditions |
| `soundManager` | `SoundManager` | Plays all audio |

### **Critical Instance Variables - UI/Animation State:**

| Field | Type | Purpose |
|-------|------|---------|
| `showingItemFound` | `boolean` | True when lollipop collection animation is playing |
| `warningFlashTimer` | `double` | Countdown timer for red flash when Luna is hunting nearby |
| `pulsePhaseHUD` | `double` | Animation phase for pulsing HUD elements |
| `activeDeathMessage` | `String` | Message displayed when player dies (e.g., "Your mind broke before she could.") |
| `lolliRevealState` | `GameRenderer.LolliRevealState` | Animation state for item reveal (null = not active) |
| `screenShakeFrames` | `double` | Countdown for screen shake effect |
| `playerDeathAnimFrames` | `double` | Countdown for death animation (black circle expanding) |

### **Critical Instance Variables - Ability/Mechanic State:**

| Field | Type | Purpose |
|-------|------|---------|
| `distractionSpellCount` | `int` | Current distraction spells available (E key) |
| `startingDistractions` | `int` | How many distractions player started with (carries to next level) |
| `hasCloneItem` | `boolean` | True if player has the clone decoy item (Level 3 only) |
| `lolliRecentlyCollected` | `boolean` | True frame a lollipop was collected (triggers special animation) |

### **Critical Instance Variables - Cooldown/Timing Timers:**

| Field | Type | Purpose |
|-------|------|---------|
| `exitGraceFrames` | `double` | Grace period after exiting escape room (enemies can't immediately enter) |
| `standStillFrames` | `double` | Timer tracking how long player stood still. Reaches 1800 (30 seconds) → Luna teleports near player |
| `guardHitCooldownFrames` | `double` | Cooldown between guard collisions (prevents instant re-damage) |
| `footstepCooldownFrames` | `double` | Cooldown between footstep sounds |
| `lunaScreamCooldownFrames` | `double` | Cooldown between Luna's scream sounds |

### **Critical Instance Variables - Tracking Flags:**

| Field | Type | Purpose |
|-------|------|---------|
| `playerIsDead` | `boolean` | True if player should trigger death screen |
| `wasInEscapeRoom` | `boolean` | Previous frame's escape room status (detects entering/exiting) |
| `escapeRoomsCollapsed` | `boolean` | Whether escape rooms are currently sealed |

### **Critical Instance Variables - Timing Infrastructure:**

| Field | Type | Purpose |
|-------|------|---------|
| `lastUpdateTime` | `long` | Nanosecond timestamp of last update (for delta-time calculation) |
| `timeDelta` | `double` | Normalized time since last frame, in "60 FPS frames" (e.g., 0.5 = half a frame) |

### **Critical Instance Variables - Collections:**

| Field | Type | Purpose |
|-------|------|---------|
| `overlays` | `List<GameRenderer.Overlay>` | List of UI overlays to render (messages, warnings, etc.) |

---

## 4. Deep Dive: Methods and Logic (Step-by-Step)

### **Method 1: `resetGameState()` [Level Initialization]**

**The Goal:**
Reset all game state to prepare for a new level. Clears entities, timers, and resets player sanity.

**How it Works (Layman's Terms):**
1. Clear all entities from the world (removes items, enemies, torches from previous level)
2. Clear UI overlays
3. Reset item reveal animation state
4. Reset all cooldown/effect timers
5. **Special case:** If loading Level 1:
   - Reset distraction count to 1
   - Reset playtime to 0
   - Reset chest counters
6. Reset player-specific effects (death animation, clone item, escape room status)
7. Reset player's Sanity value to full (100)

**Why it Works:**
- Each level starts fresh but carries over cumulative stats (total playtime, total chests collected)
- Level 1 resets everything; Levels 2 and 3 inherit stats from previous level
- Entities must be cleared or they'd spawn on top of each other
- Player Sanity resets per level but total playtime persists

---

### **Method 2: `loadLevel()` [Level Loading & Setup]**

**The Goal:**
Load the current level's maze and entities, update the total chest count for statistics.

**How it Works (Layman's Terms):**
1. Ask LevelManager to load the level into the entity system
2. Count all chests in this level and add to total encountered

**Why it Works:**
- **`totalChestsEncountered`:** This lets you calculate a collection percentage (collected / encountered)
- Delegating to LevelManager keeps maze loading logic separate

---

### **Method 3: `triggerPlayerDeath(String message)` [Death Condition]**

**The Goal:**
Mark the player as dead with a specific death message.

**How it Works (Layman's Terms):**
1. Check if already dead (prevent double-death)
2. Store the death message
3. Set death animation countdown to 60 frames
4. Set `playerIsDead = true`
5. Return true (signals to HelloApplication: player died)

**Why it Works:**
- **Death message:** Each death has a unique message ("Your mind broke", "You were caught", etc.) showing what happened
- **60 frame countdown:** This allows the death animation (black expanding circle) to play before showing the death screen
- **Guard against double-death:** Ensures death is only triggered once per level

---

### **Method 4: `update(Set<KeyCode> activeKeys)` [Main Game Loop - The Heart]**

**The Goal:**
Update all game state each frame. Handle movement, collision detection, entity updates, and determine if the player should win or lose.

**How it Works (Layman's Terms) - This is LONG because it's the most important method:**

#### **Part 1: Delta Time Calculation**
1. Get current nanosecond timestamp
2. Calculate time since last update in seconds
3. Convert to "normalized frames" (60 FPS baseline)
4. Store for next frame comparison

**Why:** This ensures smooth gameplay even if frame rate fluctuates. Slow frame = larger timeDelta.

#### **Part 2: State Check - Item Reveal Animation**
```
If showingItemFound: return false (don't update game, let animation play)
If playerIsDead: countdown death animation, return true
```

**Why:** These are "paused" states. The game freezes while these animations play.

#### **Part 3: Lollipop Reveal Animation**
```
If lollipop reveal active:
  - Count down timer
  - Advance animation phase
  - If done: mark inactive and return false (pause game)
```

**Why:** Collecting a lollipop triggers a special animation sequence.

#### **Part 4: Countdown All Timers**
```
For each cooldown (exitGrace, guardHitCooldown, lunaScream, screenShake):
  - Subtract timeDelta
  - Don't go below 0
```

**Why:** Timers naturally count down each frame.

#### **Part 5: Update All Entities**
```
Update SerialKiller (Level 3 boss)
For each Guard: update guard AI
For each Torch: update torch animation
Update Player's "near Luna" status
Update Player (handles animation, sanity decay, etc.)
```

**Why:** Each entity needs to update every frame.

#### **Part 6: Check Player Sanity Death**
```
If player sanity <= 0:
  - Trigger death with message "Your mind broke before she could."
```

**Why:** Low sanity is a loss condition.

#### **Part 7: Handle "Stand Still" Mechanic**
```
If moving (W/A/S/D pressed):
  - Reset stand still timer
Else:
  - Increment stand still timer
  - If >= 1800 frames (30 seconds):
    - Teleport Luna near player
    - Reset timer
```

**Why:** This is a penalty for camping. The game forces the player to keep moving.

#### **Part 8: Handle Movement Input**
```
Get player position before moving
Check SHIFT key to see if sprinting
For each direction key (W/A/S/D):
  - If pressed: call player.move()
Get player position after moving
Check if player actually moved (not blocked by wall)
If moved:
  - Play footstep sound (with cooldown)
  - Footstep cooldown = 9 frames if sprinting, 15 if walking
```

**Why:** Footsteps are tied to actual movement, not just input. The cooldown prevents sound spam.

#### **Part 9: Escape Room Detection & Exit Grace**
```
Check if player is in an escape room
Detect entering escape room (was outside, now inside)
Detect exiting escape room (was inside, now outside)
If exiting: set exit grace period (45 frames)
Track previous escape room state
```

**Why:** Grace period prevents Luna from immediately entering after the player exits. This is a **difficulty balancing mechanic**.

#### **Part 10: Check Chest Collisions**
```
Run collision detection for chests
If chests collected:
  - Add to total count
  - Play chest open sound
If lollipop collected:
  - Set lolliRecentlyCollected flag
  - Play special stinger sound
  - Store animation state
  - Increment distractions from item
  - Return false (pause game for reveal animation)
Track clone item possession
```

**Why:** Collecting items has side effects (sounds, stat updates, animations).

#### **Part 11: Check Guard Threats**
```
Run collision detection for guards
Catch guard collisions, check if player should die
```

**Why:** Guards can kill the player.

#### **Part 12: Update Serial Killer (Level 3 Only)**
```
Run Serial Killer update
Check if player collided and should die
```

**Why:** Boss-level threat on Level 3.

#### **Part 13: Update Pale Luna & Check Threats**
```
Run Pale Luna AI update
If Luna should play heartbeat: set warning flash timer, play heartbeat sound
If Luna should cause screen shake: set shake timer
If Luna should scream: play scream sound, set cooldown
Check if player collided with Luna and should die
```

**Why:** Luna is the primary antagonist. Her presence triggers various effects.

#### **Part 14: Clear Recent Flags**
```
lolliRecentlyCollected = false
```

**Why:** This was a one-frame flag; reset for next frame.

#### **Part 15: Return Death Status**
```
Return false (continuing gameplay) or true (player died)
```

---

### **Method 5: `isLolliRevealJustFinished()` [Animation Check]**

**The Goal:**
Check if the lollipop reveal animation just completed (used by HelloApplication to know when to advance level).

**How it Works (Layman's Terms):**
```
Return true if:
  - lolliRevealState exists AND
  - animation is no longer active
```

**Why it Works:**
- HelloApplication checks this each frame to know when the animation finished and the level should advance

---

### **Method 6: `tryUseDistraction()` [Ability: Distraction Spell]**

**The Goal:**
When player presses E, find the nearest guard within distraction range and distract them (if spells available).

**How it Works (Layman's Terms):**
1. Initialize `nearest = null` and `best = MAX_VALUE`
2. For each guard in the world:
   - Skip if already distracted
   - Calculate distance to guard in tiles
   - Check if guard is within distraction range of player
   - If closer than previous best: update best and nearest
3. If no guard found: return (fail silently)
4. If distraction spells available:
   - Decrement spell count
   - Call guard.distract() to make it ignore player
5. If no spells: do nothing (can't use ability)

**Why it Works:**
- **Distance calculation:** Uses tile distance, not pixel distance (more intuitive)
- **Range check:** Guards must be reasonably close to be distracted
- **Nearest guard logic:** Player intuitively expects the *closest* guard to be distracted
- **Spell limit:** Adds resource management (use spells wisely)

---

### **Method 7: `tryPlaceClone()` [Ability: Clone Decoy]**

**The Goal:**
When player presses C, place a cardboard clone on Level 3 if they have the item.

**How it Works (Layman's Terms):**
1. Check three conditions:
   - Does player have clone item?
   - Are we on Level 3?
   - Doesn't already have a clone placed?
2. If all conditions met:
   - Create CardboardClone at player's position (+5 pixel offset)
   - Register clone with entity manager
   - Remove clone item from player
   - Play chest open sound
3. If any condition fails: do nothing

**Why it Works:**
- **Level 3 only:** Clone is only available on final level, making it special
- **One clone at a time:** Prevents spamming
- **Offset position:** Clone appears slightly offset from player, not overlapping
- **Consumes item:** One-time use ability

---

### **Method 8: `drawDebugOverlay(GraphicsContext gc, Set<KeyCode> activeKeys)` [Debug Display]**

**The Goal:**
Display developer debug information when F3 is pressed (tiles, Luna state, cooldowns, etc.).

**How it Works (Layman's Terms):**
1. Calculate Luna's distance to player in tiles
2. Draw semi-transparent black box on screen
3. Set text color and font (monospace for alignment)
4. Build debug text lines:
   - "DEBUG (F3)" header
   - Current level, player tile position, escape room status
   - Sprint status
   - Luna's state, state timer, proximity
   - Distraction spell count, clone item status
   - Guard hit cooldown
5. Render each line of text

**Why it Works:**
- **Monospace font:** Columns align, easy to read
- **Semi-transparent box:** Doesn't completely obscure gameplay
- **Luna state info:** Helps understand enemy behavior for balancing/debugging
- **Proximity indicator:** "Nearby" flag if Luna is within 5 tiles

---

### **Method 9: `teleportLunaNearPlayer()` [Punishment for Standing Still]**

**The Goal:**
If player stands still for 30 seconds, Luna teleports near them as punishment.

**How it Works (Layman's Terms):**
1. Check if Luna exists in the world
2. Set Luna's position to player's position + 36 pixels right and on same Y
3. This effectively "warps" Luna from wherever she was to near the player

**Why it Works:**
- **Offset position:** Luna appears slightly to the right, not on top of the player
- **Punishment mechanic:** Discourages passive camping
- **Makes escapes urgent:** Forces the player to keep moving or face Luna

---

### **Method 10: `distInTiles(...)` [Static Utility - Distance Calculation]**

**The Goal:**
Calculate Euclidean distance between two points in tiles (not pixels).

**How it Works (Layman's Terms):**
```
1. Calculate dx = (x1 - x2) / TILE_SIZE  (convert pixels to tiles)
2. Calculate dy = (y1 - y2) / TILE_SIZE
3. Return sqrt(dx² + dy²)  (Pythagorean theorem)
```

**Why it Works:**
- **Tile-based distance:** More intuitive for game logic (ranges are often 5 tiles, 10 tiles, etc.)
- **Euclidean distance:** Standard formula, accounts for diagonal distance
- **Static method:** Used throughout game for range checks, it's a utility

---

## 5. Deep Dive: Model-View-Controller (MVC) Pattern

**Where GameStateManager Fits in MVC:**

| MVC Layer | GameStateManager's Role |
|-----------|------------------------|
| **Model** | ✅ **YES - Primary Model** GameStateManager *is* the game model. It contains all game state: entity positions, player stats, timers, flags |
| **View** | ❌ No. GameRenderer handles rendering |
| **Controller** | ✅ **Partial** GameStateManager processes input (activeKeys) and updates state accordingly, but it doesn't handle UI input. HelloApplication captures input and passes it to gsm.update() |

**The Complete MVC Flow:**

```
User Input (keyboard from HelloApplication)
    ↓
[HelloApplication.activeKeys + pressedThisFrame]
    ↓
[HelloApplication calls gsm.update(activeKeys)]
    ↓
[GameStateManager (CONTROLLER/MODEL) processes input]
    - Checks if keys pressed, updates player position
    - Runs collision detection
    - Updates all entities
    - Triggers events (death, item pickup, etc.)
    ↓
[GameStateManager returns "player died?" boolean]
    ↓
[HelloApplication checks result, calls render()]
    ↓
[GameRenderer (VIEW) draws everything]
    - Reads current state from GameStateManager
    - Draws maze, entities, HUD, effects
    ↓
[Scene displayed to player]
```

**Key Insight:** GameStateManager is the **definitive Model** because it's the single source of truth for game state. Everything that matters about the game world is tracked here.

---

## 6. Lab Final Presentation Arsenal

Here are 4 professional talking points for your lab presentation:

• **"GameStateManager implements the Facade design pattern, providing a unified interface to complex subsystems (EntityManager, CollisionSystem, LevelManager, SoundManager). By consolidating these systems, I eliminated tight coupling between HelloApplication and implementation details, allowing modifications to individual systems without cascading changes throughout the application."**

• **"The game loop implements delta-time normalization by calculating nanosecond-precision time deltas and normalizing them to a 60 FPS baseline. This ensures frame-rate independence; whether the game runs at 30 FPS or 144 FPS, all simulations (movement speed, cooldown timers, animations) remain consistent through the timeDelta multiplier applied to all time-dependent calculations."**

• **"I implemented a sophisticated state machine managing overlapping game conditions: player mortality, item reveal animations, Luna's behavioral states, escape room mechanics, and precisely-timed cooldowns. By explicitly tracking each state as a separate boolean or timer field, I achieved robust logic that prevents state conflicts (e.g., processing collisions while dead, or updating movement during item reveal) through early-return checks."**

• **"The 'stand still' punishment mechanic (teleporting Luna near player after 30 seconds of inactivity) demonstrates temporal game design. By implementing a secondary standStillFrames timer independent of cooldowns and animations, I created a dynamic difficulty adjustment that encourages aggressive, mobile gameplay while maintaining player agency—the player can always reset the timer by moving even briefly."**

---

## 7. Critical Implementation Details

### **The Update Method Architecture:**

GameStateManager.update() follows a **strict linear sequence**, which prevents bugs:

```
1. Calculate timeDelta
2. Check early-return conditions (paused states)
3. Countdown all timers
4. Update all entities (AI, animation)
5. Check win/loss conditions
6. Handle player input
7. Run collision detection
8. Trigger events (sounds, animations, deaths)
9. Return result
```

This order is critical: if you checked collisions before updating entity positions, you'd be detecting collisions with *previous frame's* positions.

### **Two-Phase Collision Response:**

```java
collisionSystem.playerDied = false;
collisionSystem.checkGuardThreats(...);
if (collisionSystem.playerDied)
    return triggerPlayerDeath(collisionSystem.deathMessage);

collisionSystem.playerDied = false;  // Reset for next threat type
collisionSystem.updateSerialKiller(...);
if (collisionSystem.playerDied)
    return triggerPlayerDeath(...);
```

By resetting the flag between threat checks, each collision type (guard, Serial Killer, Luna) can set it independently.

### **Timer Architecture:**

Three types of timers in GameStateManager:

1. **Countdown Timers:** `warningFlashTimer`, `exitGraceFrames` — count down and trigger when reaching 0
2. **Persistent Timers:** `standStillFrames`, `footstepCooldownFrames` — count up or maintain state
3. **Animation Timers:** `playerDeathAnimFrames`, `lolliRevealState.timer` — drive animations

---

## 8. State Transitions Managed by GameStateManager

```
MENU
  ↓ (startGame called)
LOADING
  ↓ (resetGameState + loadLevel)
PLAYING (isPlaying = true)
  ├─→ ITEM_FOUND (lollipop collected)
  │     ↓ (animation plays)
  │     → PLAYING (continue) or LEVEL_COMPLETE
  │
  └─→ DEAD (player reached 0 sanity or caught)
        ↓ (death animation plays)
        → GAME_OVER (show death screen)

GAME_OVER
  ↓ (restart or menu)
MENU
```

GameStateManager doesn't *manage* these transitions—HelloApplication does. But GameStateManager *detects* win/loss conditions and flags them for HelloApplication to respond to.

---

## 9. Key Takeaway

**GameStateManager is where the game *thinks*.** It's not fancy; it doesn't render or handle menus. But every frame, it answers the critical questions:
- Where should entities move?
- Did entities collide?
- Is the player dead or alive?
- What sounds should play?
- What effects should trigger?

By concentrating all this logic in one place, GameStateManager becomes *predictable and testable*. If something weird happens in gameplay, you investigate GameStateManager first—because that's where the game's intelligence lives.
