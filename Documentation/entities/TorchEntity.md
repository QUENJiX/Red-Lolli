# TorchEntity.java - Complete Structural Analysis

## 1. The "Elevator Pitch" (High-Level Overview)

**TorchEntity** represents **environmental decorations**—stationary torches that flicker with animated flames. Unlike enemies or collectibles, torches are purely visual/atmospheric entities that don't affect gameplay. They animate their flame graphics and can be lit or extinguished. Think of them as **ambient visual effects made concrete**—they exist in the game world, have positions, and can be rendered, but they don't collide or interact with gameplay. Torches add **visual richness** to the maze without mechanical complexity.

---

## 2. Core Computer Science Concepts

### **Design Patterns & Techniques Used:**
- **Animation State:** Frame-based sprite animation
- **Idle Entity:** No collision, no gameplay effects
- **State Transition:** Lit/unlit state with animation adjustment
- **Procedural Initialization:** Random starting frame and timer for visual variety

### **Why These Concepts Matter:**
- **Animation:** Brings static environment to life
- **Randomization:** Each torch animates slightly differently, preventing monotony
- **Lit State:** Allows future mechanics (extinguishing torches for stealth?)

---

## 3. Deep Dive: Variables and State

### **Animation State:**

| Field | Type | Purpose |
|-------|------|---------|
| `animationTimer` | `double` | Accumulator for frame timing |
| `currentFrame` | `int` | Current flame frame (1-4 for flames, 0 for unlit) |
| `isLit` | `boolean` | Is torch currently burning? |

### **Timing Infrastructure:**

| Field | Type | Purpose |
|-------|------|---------|
| `lastUpdateTime` | `long` | Nanosecond timestamp |
| `timeDelta` | `double` | Normalized time delta |

---

## 4. Deep Dive: Methods and Logic (Step-by-Step)

### **Method 1: Constructor `TorchEntity(double x, double y)`**

**The Goal:**
Create a torch at a location with random starting animation state.

**How it Works (Layman's Terms):**
1. Call parent Entity constructor with size 40
2. Initialize animationTimer to random value (0-9)
3. Initialize currentFrame to random frame (1-4)
4. Set isLit to true

**Why Randomization?**
- Prevents all torches from flickering in sync (looks fake)
- Each torch has its own rhythm (feels organic)

---

### **Method 2: `update()` [Animation Advance]**

**The Goal:**
Update flame animation each frame (only if lit).

**How it Works (Layman's Terms):**
```
if (not lit):
    return (skip animation)

Calculate time since last frame
Increment animationTimer
if (timer >= 8 frames):
    Advance currentFrame
    if (frame > 4):
        Reset to frame 1
    Reset timer
```

**Why:** Each flame frame displays for 8 frames before advancing. Animation loops through frames 1-4 then repeats.

---

### **Method 3: `getSize()` / `getCurrentFrame()` / `isLit()` [Query Methods]**

**The Goal:**
Query data for rendering and game logic.

**How it Works (Layman's Terms):**
```
return size;         // Rendering size
return currentFrame; // Animation frame
return isLit;       // Lit state
```

---

### **Method 4: `setLit(boolean lit)` [State Setter]**

**The Goal:**
Light or extinguish the torch.

**How it Works (Layman's Terms):**
```
isLit = lit

if (now extinguished):
    currentFrame = 0 (unlit sprite)
else if (was extinguished, now lit):
    currentFrame = 1 (first flame frame)
```

---

## 5. Deep Dive: Model-View-Controller (MVC) Pattern

**Where TorchEntity Fits in MVC:**

| MVC Layer | TorchEntity's Role |
|-----------|---|
| **Model** | ✅ **YES** Game entity with position and visual state |
| **View** | ✅ **Partial** Drives visual animation (model that directly generates view data) |
| **Controller** | ❌ No |

**Note:** TorchEntity is unusual—it's primarily a visual entity. It's part of the model but fundamentally exists for aesthetics.

---

## 6. Lab Final Presentation Arsenal

Here are 3 professional talking points for your lab presentation:

• **"TorchEntity implements procedural animation initialization: by seeding animation timers and frame offsets with pseudo-random values at construction, each torch instance exhibits unique flickering rhythm despite sharing identical animation logic. This technique creates perceived visual variety with minimal code, demonstrating that simple proceduralism enhances environmental richness."**

• **"Torch implements a lit/unlit state machine with animation-aware transitions: extinguishing a torch advances to frame 0 (unlit sprite) rather than simply hiding the entity. This state-driven animation approach enables future mechanics (dynamically extinguishing torches for gameplay) without architectural changes, demonstrating forward-compatible design."**

• **"TorchEntity exemplifies an 'idle entity' architectural pattern: despite being full Entity subclasses with position and update() methods, torches have zero gameplay impact. They integrate into the rendering and entity management pipelines without affecting collision, player state, or win/lose conditions, demonstrating clean separation between atmospheric and mechanical game systems."**

---

## 7. Animation Timing

```
Frame 1: 8 frames
Frame 2: 8 frames
Frame 3: 8 frames
Frame 4: 8 frames
Loop back to Frame 1

Total cycle: 32 frames = ~0.53 seconds at 60 FPS
```

So torches complete a full flicker cycle roughly every half-second.

---

## 8. Key Takeaway

**TorchEntity is the **aesthetic layer made concrete**.** It demonstrates that not every entity needs gameplay impact. Environmental entities are valid architectural participants. They update, they render, they exist in the world—but they don't threaten the player or block progress. This is professional game design: systems that are simple, purposeful, and contribute to atmosphere without mechanical complexity.
