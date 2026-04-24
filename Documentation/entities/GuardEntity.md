# GuardEntity.java - Complete Structural Analysis

## 1. The "Elevator Pitch" (High-Level Overview)

**GuardEntity** represents the **level guardians**—three types of patrol enemies (Bat, Cobra, Centipede) that protect escape rooms on each level. Each guard type has different distraction durations (Bat: longest distraction, Centipede: shortest). Think of guards as **defensive obstacles** that the player must either distract or sneak past. Unlike Pale Luna who hunts player-wide, guards are stationary and only active when player approaches their room. They implement the distraction ability system—when the player uses an E-key distraction spell, nearby guards become confused and ignore the player temporarily.

---

## 2. Core Computer Science Concepts

### **Design Patterns & Techniques Used:**
- **Enum for Type Variants:** Three guard types with different behaviors via enum dispatch
- **Timer-Based State:** Distracted state has countdown timer
- **Distance Calculation:** Range checks for distraction eligibility
- **Point-in-Polygon Detection:** Checking if player is inside guarded room using multiple sample points

### **Why These Concepts Matter:**
- **Enum Types:** Rather than three separate classes (BatGuard, CobraGuard, CentipedeGuard), one class with enum variants is simpler and DRY
- **Distraction Mechanic:** Creates player resource management (limited spells) and tactical decisions
- **Range-Based Detection:** Encourages spatial tactics—player must get close to use distraction

---

## 3. Deep Dive: Variables and State

### **Type System:**

| Field | Type | Purpose |
|-------|------|---------|
| `type` | `enum Type` | Guard type: BAT, COBRA, or CENTIPEDE |
| `BAT_DISTRACTION_DURATION` | `static int` | 300 frames (5 seconds) - easiest to distract, stays distracted longest |
| `COBRA_DISTRACTION_DURATION` | `static int` | 180 frames (3 seconds) - medium difficulty |
| `CENTIPEDE_DISTRACTION_DURATION` | `static int` | 120 frames (2 seconds) - hardest to distract, shortest duration |

### **Room Assignment:**

| Field | Type | Purpose |
|-------|------|---------|
| `escapeRow` | `final int` | Tile row of the escape room this guard protects |
| `escapeCol` | `final int` | Tile column of the escape room this guard protects |

### **Distraction State:**

| Field | Type | Purpose |
|-------|------|---------|
| `distracted` | `boolean` | Is this guard currently distracted? |
| `distractionTimer` | `double` | Countdown until distraction wears off |

### **Timing Infrastructure:**

| Field | Type | Purpose |
|-------|------|---------|
| `lastUpdateTime` | `long` | Nanosecond timestamp |

---

## 4. Deep Dive: Methods and Logic (Step-by-Step)

### **Method 1: Constructor `GuardEntity(double x, double y, Type type, int escapeRow, int escapeCol)`**

**The Goal:**
Create a guard of a specific type protecting a specific escape room.

**How it Works (Layman's Terms):**
1. Call parent Entity constructor with position and size 28
2. Store guard type (determines distraction duration)
3. Store escape room tile coordinates

---

### **Method 2: `update()` [Main Update]**

**The Goal:**
Manage distraction timer countdown.

**How it Works (Layman's Terms):**
1. Calculate time since last frame
2. If distracted: decrement distractionTimer
3. If timer <= 0: distracted = false (distraction wears off)

---

### **Method 3: `distract()` [Activate Distraction]**

**The Goal:**
Distract this guard when player uses E-key ability.

**How it Works (Layman's Terms):**
1. If already distracted: do nothing (prevent re-distraction)
2. Set distracted = true
3. Set distractionTimer based on guard type:
   - BAT: 300 frames
   - COBRA: 180 frames
   - CENTIPEDE: 120 frames

**Why:** Different guard types have different vulnerability durations, creating **difficulty scaling**.

---

### **Method 4: `isPlayerOnGuardedRoom(Hitbox2D playerHitbox)` [Room Occupation Check]**

**The Goal:**
Determine if player is inside the escape room this guard protects.

**How it Works (Layman's Terms):**
1. Calculate center point of player's hitbox
2. Create array of 5 test points:
   - Player center
   - Left edge center
   - Right edge center
   - Top edge center
   - Bottom edge center
3. For each test point:
   - Convert to tile coordinates
   - Check if tile matches escape room tile
4. If ANY test point is on escape room: return true
5. Else: return false

**Why:** 
- **Multiple points:** Prevents edge cases where player is partially in room but center is outside
- **Robust detection:** Works even if player partially overlaps room boundary

---

### **Method 5: `isDistracted()` [Distraction Query]**

**The Goal:**
Check if guard is currently distracted.

**How it Works (Layman's Terms):**
```
return distracted;
```

Used by collision system to determine if player can safely pass through guarded room.

---

### **Method 6: `distanceToPlayerInTiles(double playerX, double playerY)` [Distance Calculation]**

**The Goal:**
Calculate Euclidean distance between guard and player in tile units.

**How it Works (Layman's Terms):**
```
dx = |playerX - guardX| / TILE_SIZE
dy = |playerY - guardY| / TILE_SIZE
return sqrt(dx² + dy²)
```

---

### **Method 7: `isWithinDistractionRange(double playerX, double playerY)` [Range Check]**

**The Goal:**
Check if player is close enough to be distracted (within 3 tiles).

**How it Works (Layman's Terms):**
```
return distanceToPlayerInTiles(...) <= 3.0;
```

**Why:** Player must get within 3 tiles (~96 pixels) to use distraction spell. This creates spatial tactics.

---

### **Method 8: `getHitbox()` [Collision Shape]**

**The Goal:**
Return collision shape.

**How it Works (Layman's Terms):**
```
return new Hitbox2D(x, y, size, size);
```

---

### **Method 9: `getType()` [Type Query]**

**The Goal:**
Return guard type for rendering (different sprites for Bat vs Cobra vs Centipede).

**How it Works (Layman's Terms):**
```
return type;
```

---

## 5. Deep Dive: Model-View-Controller (MVC) Pattern

**Where GuardEntity Fits in MVC:**

| MVC Layer | GuardEntity's Role |
|-----------|---|
| **Model** | ✅ **YES** Game entity with position and behavior |
| **View** | ❌ No |
| **Controller** | ✅ **Partial** Responds to distraction input |

---

## 6. Lab Final Presentation Arsenal

Here are 3 professional talking points for your lab presentation:

• **"GuardEntity implements differentiated difficulty scaling through type-specific distraction durations: BAT guards are most vulnerable (300 frames), COBRA medium (180), CENTIPEDE most resistant (120). This enum-based variant pattern allows behavioral variance without code duplication, enabling designers to balance levels by mixing guard types rather than creating entirely separate classes."**

• **"The isPlayerOnGuardedRoom() method uses multi-point collision detection, testing five samples (center and four edges) against escape room boundaries. This approach is more robust than single-point detection, handling edge cases where player partially overlaps room boundary, ensuring correct room-occupation detection even during rapid movement or collision."**

• **"Distraction range enforcement (3-tile maximum distance) and distraction timer mechanics create tactical resource management: player must approach guards closely while managing their limited distraction spells. This design encourages spatial puzzle-solving and forces meaningful decisions about spell usage, preventing trivial distraction spam."**

---

## 7. Guard Type Comparison

| Type | Distraction Duration | Level | Difficulty |
|------|---------------------|-------|------------|
| BAT | 300 frames (5s) | 1 | Easiest |
| COBRA | 180 frames (3s) | 2 | Medium |
| CENTIPEDE | 120 frames (2s) | 3 | Hardest |

---

## 8. Key Takeaway

**GuardEntity is the **tactical obstacle** that makes players think spatially.** Unlike Luna who hunts everywhere, guards are localized threats that can be bypassed, distracted, or avoided. They teach players about resource management (distraction spells) and positioning (getting close to guards).
