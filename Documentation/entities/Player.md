# Player.java - Complete Structural Analysis

## 1. The "Elevator Pitch" (High-Level Overview)

**Player** is the **protagonist entity** that the player directly controls. It represents "Red Lolli" in the game world—the character trying to escape Pale Luna. Player manages several complex systems: movement with stamina and exhaustion mechanics, sanity decay (affected by proximity to Luna and escape room safety), animation state, directional facing, and collision detection. Think of it as a **complex state machine embodied**—every mechanic that directly affects the player (running out of energy, losing sanity, getting tired) is tracked here. The Player class is the bridge between player input (processed by HelloApplication) and the game world (collision, enemy detection, rendering).

---

## 2. Core Computer Science Concepts

### **Design Patterns & Techniques Used:**
- **State Management:** Player tracks multiple overlapping states (moving, exhausted, near Luna, in escape room, sanity dead)
- **Resource System:** Stamina and Sanity are managed as time-based resources that increment/decrement over time
- **Temporal State Machine:** Uses timers and counters (animTimer, sanityDrainCounter) to manage time-dependent behavior
- **Delta-Time Physics:** All movement and timers use frame-delta calculation for frame-rate independence
- **Exponential Drain:** Sanity drain rate changes based on conditions (passive baseline, accelerated near Luna, recovery in escape room)

### **Why These Concepts Matter:**
- **Resource Decay:** Sanity constantly decays, encouraging aggressive gameplay. The closer to Luna, the faster it decays—this is **dynamic difficulty**
- **Stamina System:** Sprint creates risk vs. reward (move faster but exhaust). This forces **tactical decision-making**
- **Animation State:** Smooth sprite animation depends on movement tracking—this is **visual polish**
- **Delta-Time:** Ensures consistent gameplay across frame rates (crucial for fair speedrunning)

---

## 3. Deep Dive: Variables and State

### **Movement & Stamina System:**

| Field | Type | Purpose |
|-------|------|---------|
| `BASE_SPEED` | `static double` | Pixels per frame at 60 FPS (3.2) |
| `SPRINT_MULTIPLIER` | `static double` | Speed multiplier while sprinting (1.8x) |
| `MAX_STAMINA_FRAMES` | `static int` | Stamina bar max (180 frames = 3 seconds of sprinting) |
| `EXHAUSTED_FRAMES` | `static int` | Recovery time after stamina depletes (180 frames = 3 seconds) |
| `staminaFrames` | `double` | Current stamina remaining (depletes when sprinting, regenerates when walking) |
| `exhaustedFrames` | `double` | Countdown timer while exhausted (can't sprint during this) |
| `facingX`, `facingY` | `double` | Direction player is facing (used for rendering sprite direction) |

### **Sanity System:**

| Field | Type | Purpose |
|-------|------|---------|
| `MAX_SANITY` | `static int` | Sanity maximum (100) |
| `PASSIVE_DRAIN_INTERVAL` | `static int` | Frames between sanity drain when safe (60 frames = 1 drain per second) |
| `NEAR_LUNA_DRAIN_INTERVAL` | `static int` | Frames between drain when near Luna (30 frames = 2x faster drain) |
| `ESCAPE_ROOM_RECOVERY_INTERVAL` | `static int` | Frames between sanity recovery in escape room (60 frames = 1 recovery per second) |
| `NEAR_LUNA_DISTANCE` | `static int` | Pixel distance threshold (150 pixels) for triggering near-Luna drain |
| `sanity` | `int` | Current sanity value (0-100) |
| `sanityDrainCounter` | `double` | Accumulator for timing drain/recovery |
| `isNearLuna` | `boolean` | Is Luna within NEAR_LUNA_DISTANCE? |
| `sanityDead` | `boolean` | Has sanity reached 0? (lose condition) |

### **Animation System:**

| Field | Type | Purpose |
|-------|------|---------|
| `animFrame` | `int` | Current animation frame (0-3 typically) |
| `animTimer` | `double` | Accumulator for frame timing |
| `isMoving` | `boolean` | Was moving last frame? (for animation) |
| `movedThisFrame` | `boolean` | Did we move THIS frame? (cleared each update) |

### **Escape Room & Location Tracking:**

| Field | Type | Purpose |
|-------|------|---------|
| `isInEscapeRoom` | `boolean` | Currently inside a safe zone? |

### **Performance/Timing:**

| Field | Type | Purpose |
|-------|------|---------|
| `lastUpdateTime` | `long` | Nanosecond timestamp of last update (delta-time calculation) |
| `timeDelta` | `double` | Normalized time since last frame (1.0 = one standard frame) |

---

## 4. Deep Dive: Methods and Logic (Step-by-Step)

### **Method 1: Constructor `Player(double x, double y)`**

**The Goal:**
Create a new Player at a specific spawn location.

**How it Works (Layman's Terms):**
1. Call parent Entity constructor with position (x, y) and size 20
2. Initialize all state to defaults (full stamina, full sanity, no animation, no exhaustion)

---

### **Method 2: `update()` [Main Update Loop - CRITICAL]**

**The Goal:**
Update all player state each frame: animation, stamina regeneration/depletion, sanity changes.

**How it Works (Layman's Terms):**

#### **Part 1: Delta-Time Calculation**
```
Calculate time since last frame in seconds
Convert to "60 FPS frames" (timeDelta)
```

#### **Part 2: Handle Exhaustion Recovery**
```
if (currently exhausted):
    Decrement exhaustedFrames timer
    (Can't sprint while exhausted)
```

**Example:** Player sprints for 3 seconds (stamina depletes). They're now exhausted for 3 more seconds. During those 3 seconds, SHIFT key does nothing. After 3 seconds, exhaustion ends and stamina begins regenerating.

#### **Part 3: Update Animation**
```
if (moved this frame):
    Increment animTimer
    Every 10 frames:
        Advance to next animation frame
        Loop back to 0 after frame 3
else:
    Reset animation (stand still)

Store "was I moving" for rendering
Clear "moved this frame" flag (used by move() method)
```

**Why:** Animation should only play while moving. Standing still shows idle sprite.

#### **Part 4: Check Sanity Death**
```
if (sanity <= 0):
    Set sanityDead = true
    Clamp sanity to 0
    Return (stop processing)
```

#### **Part 5: Update Sanity Based on Location**

**In Escape Room (Safe):**
```
Increment sanityDrainCounter
Every 60 frames (1 second):
    If sanity < 100:
        Increment sanity by 1 (recovery)
    Reset counter
```

**Not in Escape Room (Danger):**
```
Increment sanityDrainCounter
Determine drain rate:
    If near Luna: drain every 30 frames (2x faster)
    Else: drain every 60 frames (normal)
If timer reaches interval:
    Decrement sanity by 1
    Check if sanity <= 0 (game over)
```

**Why this design:**
- Escape room is a **safe haven** where sanity recovers—creates strategic pause points
- Near Luna = faster drain—creates urgency and forces movement
- Constant baseline drain—prevents camping indefinitely even far from Luna

---

### **Method 3: `move(double dx, double dy, Maze maze, boolean sprinting)` [Movement]**

**The Goal:**
Move the player one frame's worth of distance, handling collision, stamina depletion, and animation tracking.

**How it Works (Layman's Terms):**

#### **Part 1: Calculate Movement Speed**
```
Base speed = 3.2 pixels per frame
If exhausted: speed = 3.2 * 0.6 = 1.92 (slowed down)
If sprinting and can sprint: speed = 3.2 * 1.8 = 5.76 (fast!)
Else: speed = 3.2 (normal)

Multiply by timeDelta for frame-rate independence
```

#### **Part 2: Collision Detection**
```
Calculate next position: nextX = x + (dx * speed)
Create test hitbox at next position
Ask maze: "Would I hit a wall here?"
If no wall collision:
    Update position
    Update facing direction (which way are we moving?)
    Set movedThisFrame = true
Else:
    Don't move (blocked by wall)
```

**Why:** This is **smooth movement with wall sliding**. Player can move diagonally; if they hit a wall with one direction, the other direction continues.

#### **Part 3: Handle Stamina**

**While Sprinting:**
```
if (sprinting AND not exhausted AND stamina > 0 AND moving):
    Decrement stamina by timeDelta
    if (stamina <= 0):
        Deplete stamina completely
        Set exhausted timer to 180 frames
```

**While Walking (Not Sprinting):**
```
if (not sprinting AND not exhausted AND stamina < MAX):
    Regenerate stamina slowly (50% of depletion rate)
    Cap at maximum
```

**Why:**
- Sprinting costs stamina at 1:1 rate (180 frames of sprint = 3 seconds)
- Walking regenerates at 0.5 rate (recovery takes 6 seconds to restore full stamina)
- This creates **resource tension**: do you burn all stamina for a burst, or conserve?

---

### **Method 4: `isMoving()` [Animation Query]**

**The Goal:**
Return whether player moved last frame (for rendering animation).

**How it Works (Layman's Terms):**
```
return isMoving;  // Set to movedThisFrame each update
```

---

### **Method 5: `getAnimFrame()` [Animation Frame Query]**

**The Goal:**
Return current animation frame for sprite rendering.

**How it Works (Layman's Terms):**
```
return animFrame;  // 0-3 typically
```

---

### **Method 6: `getHitbox()` [Collision Shape - Implements Collidable]**

**The Goal:**
Return collision shape for collision detection.

**How it Works (Layman's Terms):**
```
Create new Hitbox2D at current position with current size
```

---

### **Method 7: `isInEscapeRoom()` / `setInEscapeRoom()` [Location Queries]**

**The Goal:**
Track whether player is currently in a safe escape room.

**How it Works (Layman's Terms):**
```
Simple getter/setter for isInEscapeRoom boolean
```

---

### **Method 8: `isExhausted()` [Stamina State Query]**

**The Goal:**
Check if player is currently exhausted (can't sprint).

**How it Works (Layman's Terms):**
```
return exhaustedFrames > 0;
```

---

### **Method 9: `canSprint()` [Sprint Ability Check]**

**The Goal:**
Check if player CAN sprint right now (has stamina and not exhausted).

**How it Works (Layman's Terms):**
```
return staminaFrames > 0 AND exhaustedFrames == 0;
```

---

### **Method 10: `getStaminaPercent()` [UI Query]**

**The Goal:**
Return stamina as percentage (0.0 to 1.0) for HUD rendering.

**How it Works (Layman's Terms):**
```
return staminaFrames / MAX_STAMINA_FRAMES;
```

---

### **Method 11: `getFacingX()` / `getFacingY()` [Direction Queries]**

**The Goal:**
Return which direction player is facing (for sprite rotation/flip).

**How it Works (Layman's Terms):**
```
return facingX/facingY (stored during move())
```

---

### **Method 12: `updateNearLunaStatus(double lunaX, double lunaY)` [Luna Proximity Check]**

**The Goal:**
Called by GameStateManager each frame to update whether Luna is nearby.

**How it Works (Layman's Terms):**
1. Calculate Euclidean distance between player and Luna
2. If distance < 150 pixels: isNearLuna = true (accelerated sanity drain)
3. Else: isNearLuna = false (normal drain)

**Why:** This is how the game creates **dynamic difficulty**. The closer Luna is, the more urgently sanity drains, forcing the player to either run or hide.

---

### **Method 13: `getSanity()` / `setSanity()` [Sanity Accessors]**

**The Goal:**
Get/set sanity value directly.

**How it Works (Layman's Terms):**
```
// Getter
return sanity;

// Setter (used when progressing levels)
this.sanity = value;
```

---

### **Method 14: `getSanityPercent()` [UI Query]**

**The Goal:**
Return sanity as percentage (0.0 to 1.0) for HUD rendering.

**How it Works (Layman's Terms):**
```
return sanity / MAX_SANITY;
```

---

### **Method 15: `isSanityDead()` [Loss Condition Check]**

**The Goal:**
Check if player should trigger game-over screen (sanity reached 0).

**How it Works (Layman's Terms):**
```
return sanityDead;
```

---

### **Method 16: `resetSanity()` [Level Reset]**

**The Goal:**
Reset all sanity-related state when starting a new level.

**How it Works (Layman's Terms):**
```
Restore sanity to 100
Reset drain counter to 0
Clear near-Luna flag
Clear sanity-dead flag
```

---

### **Method 17: `getMovementSpeed()` [Private Speed Calculator]**

**The Goal:**
Calculate current movement speed based on stamina/exhaustion state.

**How it Works (Layman's Terms):**
```
if (exhausted):
    return BASE_SPEED * 0.6  (slowed to 60%)
else if (sprinting AND canSprint):
    return BASE_SPEED * 1.8  (boosted to 180%)
else:
    return BASE_SPEED  (normal)
```

---

## 5. Deep Dive: Model-View-Controller (MVC) Pattern

**Where Player Fits in MVC:**

| MVC Layer | Player's Role |
|-----------|---------------|
| **Model** | ✅ **YES** Player is a game object model with position, state, and behavior |
| **View** | ❌ No (GameRenderer reads Player data to draw it) |
| **Controller** | ✅ **Partial** Player processes movement input (via move() method), but HelloApplication is the primary input controller |

**Context in Game Architecture:**
```
HelloApplication (Controller)
    ├─ Captures input (activeKeys)
    ├─ Calls gsm.update(activeKeys)
    │   ├─ GameStateManager (Mixed Model/Controller)
    │   │   └─ Calls player.move(dx, dy, maze, sprinting)
    │   │       └─ Player updates position, stamina, animation
    │   ├─ Calls player.update()
    │   │   └─ Player updates sanity, exhaustion
    │
    ├─ Calls render()
    │   └─ GameRenderer reads player.getX(), player.getY(), etc.
    │       └─ Draws sprite at player's position
```

---

## 6. Lab Final Presentation Arsenal

Here are 4 professional talking points for your lab presentation:

• **"Player implements a complex resource management system with dual-resource decay: stamina for movement and sanity for survivability. By leveraging temporal state accumulation (sanityDrainCounter, exhaustedFrames), I created dynamic difficulty where proximity to Luna accelerates sanity drain by 2x, forcing players to choose between aggressive engagement and defensive hiding—a balance that drives emergent gameplay."**

• **"I implemented frame-rate-independent movement through delta-time normalization: all timing and physics calculations are scaled by timeDelta = dtSeconds * 60.0, ensuring that whether the game runs at 30 FPS or 144 FPS, movement speed, stamina depletion, and sanity drain remain consistent. This architectural decision enables fair cross-platform competition and robust performance handling."**

• **"The stamina system demonstrates asymmetric resource economics: sprinting depletes stamina at 1.0x rate (3 seconds to exhaust), but walking regenerates at only 0.5x rate (6 seconds to fully recover). This creates strategic tension where committing fully to sprinting is risky; players must choose when to burn resources versus when to recover, driving tactical gameplay decisions."**

• **"Player employs a multi-condition state machine for movement speed calculation: exhaustion reduces speed to 60%, sprinting boosts to 180%, and normal movement is baseline. By evaluating conditions hierarchically (exhaustion > sprint > normal), I ensure mutually-exclusive states never conflict, making the control feel responsive and predictable regardless of simultaneous key presses."**

---

## 7. Critical Implementation Details

### **The Sanity Drain Model**

```
Safe (in escape room): +1 sanity/second
Baseline (away from Luna): -1 sanity/second
Near Luna (< 150 pixels): -2 sanity/second (2x faster)

Max sanity: 100
At 0 sanity: game over
```

This creates **spatial tension**: the escape room is a refuge, but you can't hide forever (you need lollipops). The player must balance: venture out (expose to Luna, lose sanity faster) or hide (lose sanity slowly, regain in room).

### **Stamina-Exhaustion Cycle**

```
MAX_STAMINA = 180 frames (3 seconds at 60 FPS)
EXHAUSTED = 180 frames (3 seconds at 60 FPS)

Sprint 3 sec → exhausted 3 sec → regenerate 6 sec → ready again

Result: ~12 seconds of cycle for a full sprint-to-ready cycle
```

This forces **tactical pausing**—you can't sprint continuously. Every aggressive action requires recovery time.

### **Animation Frame Advancement**

```
isMoving? YES:
    Every 10 frames: advance animation
    Frames cycle: 0 → 1 → 2 → 3 → 0
    (Walking animation takes 40 frames total for full loop)

isMoving? NO:
    Reset to frame 0 (idle pose)
```

### **Wall Collision Algorithm**

```
1. Calculate desired next position
2. Create test hitbox at that position
3. Ask maze.isWallCollision(testHitbox)
4. If no collision: accept position, update facing
5. If collision: ignore movement (don't move)
```

This is **hard collision** (no sliding). If blocked by a wall, movement fails entirely. This is intentional for gameplay clarity.

---

## 8. Sanity System Psychology

The sanity system is the **core tension generator**:

```
Baseline drain: Discourages camping far from Luna
  → Player must keep moving

Near Luna acceleration: Penalizes failure to evade Luna
  → Creates urgency when Luna approaches

Escape room recovery: Provides relief mechanic
  → Creates save points, enables strategic breathing room

Sanity death condition: Win condition alternative to "reach exit"
  → Losing sanity = losing the game (fail condition)
```

---

## 9. Key Takeaway

**Player is the avatar of player agency.** Every mechanic that makes the game tense and exciting lives here:
- ✅ Stamina system forces tactical decisions
- ✅ Sanity system creates time pressure
- ✅ Animation system provides visual feedback
- ✅ Delta-time ensures fair, consistent gameplay

Player isn't just a position in the world—it's the **embodiment of the player's struggle to survive**. Every stat, every resource, every animation frame contributes to that narrative.
