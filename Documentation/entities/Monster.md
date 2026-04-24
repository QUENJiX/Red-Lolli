# Monster.java - Complete Structural Analysis

## 1. The "Elevator Pitch" (High-Level Overview)

**Monster** represents **Pale Luna**, the primary antagonist and the source of all terror in the game. It's the boss enemy that hunts the player. Monster implements a sophisticated **state machine** with four distinct behavioral states: DORMANT (sleeping, ignoring player), STALKING (aware but not hunting), HUNTING (actively pursuing at high speed), and WAITING_AT_DOOR (blocked by escape room, waiting for player to leave). Think of Luna as a **sophisticated predator with mood swings**—she's not always hunting. She waits, she patrols, she hunts, she gives up. This behavioral complexity is what makes Luna feel alive and terrifying instead of just being a chasing sprite.

---

## 2. Core Computer Science Concepts

### **Design Patterns & Techniques Used:**
- **Finite State Machine (FSM):** Four distinct states with explicit transitions
- **Timer-Based State Transitions:** States auto-advance when their timers expire (e.g., STALKING lasts 300 frames, then transitions to HUNTING)
- **Pathfinding AI:** Uses maze.getNextMove() to pursue player optimally
- **Behavioral AI:** Luna's behavior changes based on game conditions (player in escape room, lollipop collected, etc.)
- **Pulse Animation:** Uses pulsePhase for visual breathing/pulsing effect

### **Why These Concepts Matter:**
- **State Machine:** Prevents behavioral spaghetti. Each state has clear responsibilities. Transitions are explicit
- **Pathfinding:** Luna doesn't just move toward player; she pathfinds around maze walls. This makes her **smart and believable**
- **Timers:** Luna doesn't hunt forever. She cycles between states, creating **threat waves** that rise and fall

---

## 3. Deep Dive: Variables and State

### **State Management:**

| Field | Type | Purpose |
|-------|------|---------|
| `state` | `enum State` | Current behavior: DORMANT, STALKING, HUNTING, WAITING_AT_DOOR |
| `dormantTimer` | `double` | Countdown until leaving DORMANT state (300 frames = 5 seconds) |
| `stalkTimer` | `double` | Countdown in STALKING state (300 frames = 5 seconds) |
| `huntTimer` | `double` | Countdown in HUNTING state (420 frames = 7 seconds) |
| `waitTimer` | `double` | Countdown while blocked by escape room (120 frames = 2 seconds) |

### **Speed Constants:**

| Field | Type | Value | Purpose |
|-------|------|-------|---------|
| `STALK_SPEED` | `static double` | 3.2 | Pixels per frame while stalking |
| `HUNT_SPEED` | `static double` | 6.4 | Pixels per frame while hunting (2x stalking) |

### **Animation & Display:**

| Field | Type | Purpose |
|-------|------|---------|
| `pulsePhase` | `double` | Animation timer for visual pulsing effect |
| `facingRight` | `boolean` | Is Luna facing right or left? (for sprite flip) |

### **Timing Infrastructure:**

| Field | Type | Purpose |
|-------|------|---------|
| `lastUpdateTime` | `long` | Nanosecond timestamp (for delta-time) |

---

## 4. Deep Dive: Methods and Logic (Step-by-Step)

### **Method 1: Constructor `Monster(double x, double y)`**

**The Goal:**
Create Pale Luna at a spawn location, starting in DORMANT state.

**How it Works (Layman's Terms):**
1. Call parent Entity constructor with position and size 25.0
2. Initialize state to DORMANT
3. Set dormantTimer to DORMANT_DURATION (300 frames)

---

### **Method 2: `update()` [Empty - Handled by update(double playerX, double playerY, ...)]**

**The Goal:**
Satisfy Entity's abstract method requirement.

**How it Works (Layman's Terms):**
```
Empty method body (does nothing)
// Actual logic is in the overloaded update() method below
```

**Why:** The abstract update() method is required by Entity, but Luna's real update logic takes additional parameters (player position). Java allows method overloading.

---

### **Method 3: `update(double playerX, double playerY, boolean playerInEscapeRoom, boolean lolliRecentlyCollected, Maze maze)` [The REAL Update - CRITICAL]**

**The Goal:**
Update Luna's state machine every frame, potentially changing behavior and pursuing the player.

**How it Works (Layman's Terms):**

#### **Part 1: Delta-Time Calculation**
```
Calculate time since last frame
Convert to 60 FPS normalized frames
```

#### **Part 2: Update Animation**
```
Increment pulsePhase (for visual pulsing effect)
Update facingRight based on player position
```

#### **Part 3: State Machine - DORMANT State**
```
if (state == DORMANT):
    Decrement dormantTimer
    if (timer <= 0 OR lollipop just collected):
        Transition to STALKING
        Initialize stalkTimer
```

**Why:** Luna wakes up when lollipop is collected. This is a **key game mechanic**: collecting the lollipop wakes Luna and makes her hunt. The player must strategically decide when to grab lollipops.

#### **Part 4: State Machine - STALKING State**
```
if (state == STALKING):
    If player in escape room:
        Position Luna at escape room door
        Transition to WAITING_AT_DOOR
    Else:
        Pursue player at STALK_SPEED
        Decrement stalkTimer
        If timer <= 0:
            Transition to HUNTING
            Initialize huntTimer
```

**Why:** Stalking is the "alert" phase. Luna is aware but not fully committed. If player escapes to a room, she waits. Otherwise, she transitions to hunting.

#### **Part 5: State Machine - HUNTING State**
```
if (state == HUNTING):
    If player in escape room:
        Position Luna at escape room door
        Transition to WAITING_AT_DOOR
    Else:
        Pursue player at HUNT_SPEED (faster!)
        Decrement huntTimer
        If timer <= 0:
            Transition back to DORMANT
```

**Why:** Hunting is Luna's aggressive phase. She moves twice as fast. After 7 seconds of hunting, she gives up and returns to dormant. This creates **threat waves**.

#### **Part 6: State Machine - WAITING_AT_DOOR State**
```
if (state == WAITING_AT_DOOR):
    Decrement waitTimer
    If timer <= 0:
        Transition back to DORMANT
```

**Why:** Luna is patient but has limited patience. After 2 seconds waiting at a door, she gives up.

---

### **Method 4: `returnToDormant()` [State Transition Helper]**

**The Goal:**
Transition Luna back to DORMANT state.

**How it Works (Layman's Terms):**
```
Set state = DORMANT
Reset dormantTimer to DORMANT_DURATION
```

---

### **Method 5: `positionAtDoor(double playerX, double playerY, Maze maze)` [Block Player Escape]**

**The Goal:**
Position Luna adjacent to the escape room door, blocking the player inside.

**How it Works (Layman's Terms):**
1. Calculate which escape room tile the player is in
2. Find neighboring tile that's NOT a wall and NOT an escape room exit
3. Position Luna at that neighboring tile
4. If no valid tile found: position Luna somewhere near player anyway

**Why:** This creates **dramatic tension**. Luna appears at the door, forcing player to either wait for her to leave or attempt to slip past her.

---

### **Method 6: `pursuePlayer(double playerX, double playerY, Maze maze, double speed)` [A* Pathfinding]**

**The Goal:**
Move Luna one frame's worth of distance toward the player, navigating maze walls intelligently.

**How it Works (Layman's Terms):**
1. Calculate Luna's current tile position
2. Calculate player's tile position
3. Ask maze.getNextMove() for the next tile in optimal path
4. If no valid path exists: give up (return early)
5. Calculate target position (center of next tile)
6. Calculate distance and direction to target
7. Move Luna toward target by `speed` pixels (capped at current distance)
8. Repeat if still distance remaining (diagonal movement optimization)

**Why:**
- **Pathfinding:** Luna doesn't just move toward player; she navigates maze walls intelligently
- **Smooth Movement:** Movement is sub-pixel precise (not grid-locked)
- **Speed Scaling:** Called with different speeds (STALK_SPEED, HUNT_SPEED) for behavior variety

---

### **Method 7: `getState()` / `isHunting()` / `isStalking()` / `isWaitingAtDoor()` [State Queries]**

**The Goal:**
Query Luna's current state for rendering and game logic.

**How it Works (Layman's Terms):**
```
return boolean or enum indicating current state
```

---

### **Method 8: `getDormantTimer()` / `getStalkTimer()` / etc. [Timer Queries]**

**The Goal:**
Query remaining time in current state (for HUD display or debugging).

**How it Works (Layman's Terms):**
```
return (int) timer;
```

---

### **Method 9: `getPulsePhase()` [Animation Query]**

**The Goal:**
Return animation phase for visual pulsing effect.

**How it Works (Layman's Terms):**
```
return pulsePhase;
```

Used by GameRenderer to scale Luna's sprite size or opacity, creating a "breathing" effect.

---

### **Method 10: `isFacingRight()` / `getSize()` [Direction & Size Queries]**

**The Goal:**
Query facing direction and collision size.

**How it Works (Layman's Terms):**
```
return facingRight;  // For sprite flip in rendering
return size;         // For collision and rendering
```

---

## 5. Deep Dive: Model-View-Controller (MVC) Pattern

**Where Monster Fits in MVC:**

| MVC Layer | Monster's Role |
|-----------|---|
| **Model** | ✅ **YES** Monster is a game entity with position, state, and behavior |
| **View** | ❌ No |
| **Controller** | ✅ **Partial** Monster's update() reacts to game conditions (player position, escape room state) like a mini-controller |

**The Luna Behavior Pipeline:**
```
GameStateManager (each frame):
    ├─ Reads: playerX, playerY, inEscapeRoom, lolliCollected, maze
    ├─ Calls: luna.update(playerX, playerY, inEscapeRoom, lolliCollected, maze)
    │   ├─ Luna evaluates her current state
    │   ├─ Luna updates her state machine
    │   └─ Luna moves toward player if appropriate
    │
    └─ GameRenderer reads:
        ├─ luna.getX(), luna.getY() (position)
        ├─ luna.getState() (which animation set?)
        ├─ luna.getPulsePhase() (pulsing effect)
        └─ Renders Luna sprite
```

---

## 6. Lab Final Presentation Arsenal

Here are 4 professional talking points for your lab presentation:

• **"Monster implements a Finite State Machine with four explicit states (DORMANT, STALKING, HUNTING, WAITING_AT_DOOR) and deterministic transitions triggered by timers and game conditions. This architectural approach eliminates behavioral ambiguity: each state has singular responsibility, making Luna's behavior predictable for designers to balance and for players to learn and counter—a hallmark of professional game AI."**

• **"I implemented Luna's pathfinding using maze.getNextMove(), which provides optimal tile-to-tile navigation. Rather than naive chase-player logic, Luna intelligently navigates maze walls, creating the impression of intelligence. The smooth sub-pixel movement layered over tile-based pathfinding creates fluid pursuit that feels both smart and organic."**

• **"Luna's state timers create emergent 'threat waves': dormancy (safe period) → stalking (building dread) → hunting (maximum danger) → return to dormant (relief). This cyclical threat intensity ensures gameplay never feels monotonously dangerous; players experience moments of hope followed by terror, driving emotional engagement and preventing tension fatigue."**

• **"The lollipop-collection trigger (waking Luna from dormant state) implements core game design: collecting objective items carries risk. This creates meaningful decision-making—player must balance the desire for lollipops against the danger of awakening Luna, ensuring objectives feel consequential rather than trivial."**

---

## 7. State Transition Diagram

```
         [DORMANT]
         ↑  ↓
         └──┘ (every 5 sec OR lollipop collected)
            ↓
         [STALKING] ←─ (player escapes to room)
         ↓  ↓
    (5 sec) (player in room)
         ↓  ↓
      [HUNTING] ──→ [WAITING_AT_DOOR]
         ↓              ↓
    (7 sec)        (2 sec wait OR player exits)
         ↓              ↓
         └──→ [DORMANT] ←─┘
```

---

## 8. Speed Comparison

| State | Speed | Relative |
|-------|-------|----------|
| STALKING | 3.2 px/frame | 1.0x |
| HUNTING | 6.4 px/frame | 2.0x |
| WAITING | 0 px/frame | 0x |
| DORMANT | 0 px/frame | 0x |

Luna hunts at exactly **double speed** during hunting phase, creating dramatic acceleration.

---

## 9. Key Takeaway

**Monster/Pale Luna is the **engine of tension**.** Her state machine creates rhythms of safety and danger. Her pathfinding makes her feel intelligent. Her escape room awareness creates tactics. She's not just a chasing sprite—she's a **dynamic antagonist with realistic limitations and behaviors**.

This is enemy AI done right: complex enough to feel alive, simple enough to be predictable and beatable.
