# SerialKillerEntity.java - Complete Structural Analysis

## 1. The "Elevator Pitch" (High-Level Overview)

**SerialKillerEntity** is the **Level 3 boss threat**—an active, aggressive enemy that hunts the player on the final level. Unlike Pale Luna who is a primary antagonist with state machines, the Serial Killer is a secondary threat that actively pursues and attacks. It has **unique mechanics**: it can be distracted by the player's clone decoy, it has pathfinding AI, and it cycles through animation frames. Think of the Serial Killer as **Luna's lieutenant**—a dangerous individual threat that adds complexity to the final level but doesn't dominate the entire game.

---

## 2. Core Computer Science Concepts

### **Design Patterns & Techniques Used:**
- **Pathfinding AI:** Uses maze.getNextMove() like Luna for intelligent pursuit
- **Animation State Machine:** Cycles through sprite frames based on state
- **Decoy Distraction:** Can be fooled into attacking clone instead of player
- **Frame-Based Animation:** Tick counter for smooth animation timing
- **Directional State:** Tracks facing direction for sprite flipping

### **Why These Concepts Matter:**
- **Pathfinding:** Makes boss feel intelligent and threatening
- **Animation:** Visual feedback (idle vs. attacking) enhances threat perception
- **Decoy Mechanic:** Provides a counter-strategy for clever players

---

## 3. Deep Dive: Variables and State

### **Activation & Aggression:**

| Field | Type | Purpose |
|-------|------|---------|
| `active` | `boolean` | Is boss currently active (pursuing player)? |
| `attackingDecoy` | `boolean` | Is boss currently attacking the clone? |
| `decoyAttackFrames` | `double` | Countdown timer for decoy attack duration (600 frames = 10 seconds) |

### **Animation State:**

| Field | Type | Purpose |
|-------|------|---------|
| `currentFrame` | `int` | Current animation frame (0-4 depending on state) |
| `frameTick` | `double` | Accumulator for frame timing |
| `ticksPerFrame` | `final int` | Ticks between frame advances (6) |
| `facingLeft` | `boolean` | Is boss facing left or right? |

### **Timing Infrastructure:**

| Field | Type | Purpose |
|-------|------|---------|
| `lastUpdateTime` | `long` | Nanosecond timestamp |
| `timeDelta` | `double` | Normalized time delta |

### **Movement:**

| Field | Type | Purpose |
|-------|------|---------|
| `SPEED` | `static double` | Base movement speed (1.75 pixels per frame) |

---

## 4. Deep Dive: Methods and Logic (Step-by-Step)

### **Method 1: Constructor `SerialKillerEntity(double x, double y)`**

**The Goal:**
Create the boss at a starting location.

**How it Works (Layman's Terms):**
1. Call parent Entity constructor with size 24
2. Initialize all animation and state variables

---

### **Method 2: `update()` [Main Update]**

**The Goal:**
Update animation and decoy attack state each frame.

**How it Works (Layman's Terms):**

#### **Part 1: Delta-Time**
```
Calculate time since last frame
```

#### **Part 2: Decoy Attack Countdown**
```
if (attacking decoy):
    Decrement decoyAttackFrames
    if (timer <= 0):
        Stop attacking decoy
```

**Why:** Boss is distracted by decoy for a limited time, then resumes hunting.

#### **Part 3: Animation Advance**
```
Increment frameTick
if (ticks >= ticksPerFrame):
    Advance currentFrame
    Reset frameTick
    Determine maxFrames:
        if (not active): 1 frame (idle)
        if (active): 5 frames (animation cycle)
    Loop frame counter
```

**Why:** Different animation sets for idle (1 frame) vs. hunting (5-frame cycle).

---

### **Method 3: `updateChase(double targetX, double targetY, Maze maze)` [Pursuit AI]**

**The Goal:**
Move boss toward target (player) using pathfinding, called only when not attacking decoy.

**How it Works (Layman's Terms):**

#### **Part 1: Check Conditions**
```
if (not active OR attacking decoy):
    return (don't pursue)
```

#### **Part 2: Calculate Positions**
```
Get boss center position
Get target center position
Convert both to tile coordinates
```

#### **Part 3: Pathfinding**
```
Ask maze.getNextMove(currentTile, targetTile)
Get next tile in optimal path
if (no path exists): return early
```

#### **Part 4: Movement**
```
Calculate target tile center
Calculate distance and direction
Update facingLeft based on dx
Move toward target at SPEED pixels/frame
Implement sophisticated movement:
    - Prioritize one axis (x or y)
    - Move as much as possible on primary axis
    - Use remaining distance on secondary axis
    - This creates less "sticky" diagonal movement
```

**Why:** The two-axis movement prevents the boss from getting stuck on diagonal paths.

---

### **Method 4: `isActive()` / `setActive()` [Activation Control]**

**The Goal:**
Query or set whether boss is actively pursuing.

**How it Works (Layman's Terms):**
```
Simple getter/setter for active flag
```

---

### **Method 5: `startDecoyAttack()` [Decoy Distraction]**

**The Goal:**
Make boss start attacking the clone instead of player.

**How it Works (Layman's Terms):**
```
Set attackingDecoy = true
Set decoyAttackFrames = 600 (10 seconds)
```

**Why:** Called by collision system when clone is detected nearby.

---

### **Method 6: `isAttackingDecoy()` / `getDecoyAttackFrames()` [Decoy State Queries]**

**The Goal:**
Query boss's distraction state.

**How it Works (Layman's Terms):**
```
return attackingDecoy;
return (int) decoyAttackFrames;
```

---

### **Method 7: `isFacingLeft()` / `getCurrentFrame()` / `getSize()` [Rendering Queries]**

**The Goal:**
Query data for sprite rendering.

**How it Works (Layman's Terms):**
```
return facingLeft;      // For sprite flip
return currentFrame;    // For animation frame selection
return size;           // For collision
```

---

## 5. Deep Dive: Model-View-Controller (MVC) Pattern

**Where SerialKillerEntity Fits in MVC:**

| MVC Layer | SerialKillerEntity's Role |
|-----------|---|
| **Model** | ✅ **YES** Game entity with position and behavior |
| **View** | ❌ No |
| **Controller** | ❌ No (doesn't respond to input) |

---

## 6. Lab Final Presentation Arsenal

Here are 3 professional talking points for your lab presentation:

• **"SerialKillerEntity implements a sophisticated decoy-distraction mechanic allowing players to create strategic advantage through object placement. When the boss attacks the clone, a 10-second window opens for player escape. This design pattern transforms the otherwise simple boss fight into a puzzle: where should the clone be placed to maximize escape opportunity, demonstrating depth from simple mechanics."**

• **"The movement algorithm implements two-axis prioritization for less grid-locked pursuit: by maximizing distance on the primary axis (X or Y) before consuming remaining budget on the secondary axis, the boss smoothly navigates maze corridors without pathfinding jitter or sticky-corner behavior. This technique improves perceived smoothness while maintaining exact distance constraints."**

• **"Animation dispatch (1-frame idle vs. 5-frame hunt cycle) creates behavioral visibility: the animation state visually communicates whether the boss is dormant or actively hunting. This explicit state communication reduces player confusion and teaches threat recognition—when animation changes to multi-frame cycle, players immediately understand increased danger."**

---

## 7. Key Takeaway

**SerialKillerEntity is the **tactical boss challenge** for Level 3.** Unlike Luna who is a persistent threat throughout the game, the Serial Killer is a focused challenge that introduces new mechanics (decoy distraction) and rewards clever play. The decoy mechanic demonstrates that not all threats must be faced directly—some can be outmaneuvered through strategy.
