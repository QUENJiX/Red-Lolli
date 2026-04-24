# CollisionSystem.java - Complete Structural Analysis

## 1. The "Elevator Pitch" (High-Level Overview)

**CollisionSystem** is the **game event detector and responder**. It checks for collisions between player and various entities (chests, guards, monsters, bosses) and implements the consequences. Unlike a pure collision detection system that just calculates overlaps, CollisionSystem is a **decision engine**: it detects collisions, determines what type of collision occurred, and updates game state accordingly. When player touches an item, collects it. When player hits a guard, game over. When player exits escape room while Luna waits, game over. CollisionSystem is the **embodiment of game rules**.

---

## 2. Core Computer Science Concepts

### **Design Patterns & Techniques Used:**
- **Hitbox-Based Collision Detection:** AABB intersections check if entities overlap
- **Distance Calculation:** Tile-space distance for range checks
- **Line-of-Sight Checks:** Determines if Luna can see player exiting escape room
- **State Mutation Flags:** Collision results stored as public fields for caller consumption
- **Multi-Step Collision Phases:** Sequential collision checks (chests, guards, Luna, Serial Killer)

### **Why These Concepts Matter:**
- **Hitbox Detection:** O(n²) worst-case but practical for small entity counts
- **Distance Checks:** Enables range-based game mechanics (distraction range, hunt activation)
- **Line-of-Sight:** Adds spatial realism (Luna can't hit through walls)
- **State Flags:** Communicates collision results back to GameStateManager

---

## 3. Deep Dive: Variables and State

### **Collision Results (Set by Checks, Read by GameStateManager):**

| Field | Type | Purpose |
|-------|------|---------|
| `playerDied` | `boolean` | Did this collision phase kill the player? |
| `deathMessage` | `String` | Death reason for game-over screen |
| `newDistractions` | `int` | Distraction spells gained from chests |
| `collectedChests` | `int` | Number of chests collected this frame |
| `lolliRevealState` | `GameRenderer.LolliRevealState` | Animation data for lollipop popup |
| `lolliRecentlyCollected` | `boolean` | Was a lollipop just collected? (wakes Luna) |
| `hasCloneItem` | `boolean` | Does player have clone ability? |

### **Feedback Flags (For Sound/Visual Effects):**

| Field | Type | Purpose |
|-------|------|---------|
| `screenShake` | `boolean` | Should screen shake? (Luna enters hunt mode) |
| `playHeartbeat` | `boolean` | Play heartbeat sound? (Luna hunting) |
| `playScream` | `boolean` | Play Luna scream? (Luna close + hunting) |
| `playChestOpen` | `boolean` | Play chest open sound? |

---

## 4. Deep Dive: Methods and Logic (Step-by-Step)

### **Method 1: `checkChestCollisions()` [Item Collection]**

**The Goal:**
Detect player-item collisions and handle item collection.

**How it Works (Layman's Terms):**

#### **Part 1: Reset Results**
```
Zero all output flags for fresh detection
```

#### **Part 2: Iterate All Chests**
```
For each item in entityManager.chests:
    if (already collected):
        skip this chest
    
    if (player hitbox DOESN'T intersect chest hitbox):
        skip this chest
    
    // Collision detected!
    Mark chest as collected
    Increment collectedChests counter
    Set playChestOpen = true
```

#### **Part 3: Handle By Content Type**

**If LOLLI (Lollipop):**
```
Set lolliRecentlyCollected = true
Create LolliRevealState (animated popup)
Add 3 distraction spells
Return immediately (process only one lollipop per frame)
```

**Why One Per Frame?**
- Prevents collecting multiple lollipops simultaneously
- Each collection is significant and gets animation

**If EMPTY (Empty Chest):**
```
Add 1 distraction spell
Continue checking other chests
```

**If CLONE_DECOY (Clone Ability):**
```
Set hasCloneItem = true
Continue checking
```

---

### **Method 2: `checkGuardThreats()` [Guard Collision Detection]**

**The Goal:**
Detect if player is hit by a guard (game-over condition).

**How it Works (Layman's Terms):**

#### **Part 1: Check Cooldown**
```
if (guardHitCooldownFrames > 0):
    return (still in cooldown from previous hit)
```

**Why Cooldown?** Prevents multiple hits in quick succession.

#### **Part 2: Check Escape Room Status**
```
if (in escape room AND not entering):
    return (guards can't hit inside safe escape room)
```

#### **Part 3: Check All Guards**
```
For each guard:
    if (guard is distracted):
        skip (distracted guards don't attack)
    
    Check two conditions (guard is threat if EITHER is true):
    1. Guard's room contains player (isPlayerOnGuardedRoom)
    2. Guard hitbox intersects player hitbox (direct contact)
    
    if (threat detected):
        Set playerDied = true
        Set deathMessage based on guard type:
            BAT: "The bat bit first. Luna answered instantly."
            COBRA: "The snake strikes! No spell cast, no escape."
            CENTIPEDE: "The centipede swarmed you... the darkness follows."
        Return immediately
```

---

### **Method 3: `updateSerialKiller()` [Level 3 Boss Collision]**

**The Goal:**
Update Serial Killer behavior and detect boss collision with player.

**How it Works (Layman's Terms):**

#### **Part 1: Activation Check**
```
if (not yet active):
    Calculate distance from player to boss (in tiles)
    if (player within 8 tiles):
        Activate boss (begins pursuit)
        return
```

**Why 8-Tile Activation?** Players get warning before boss engages.

#### **Part 2: Target Selection**
```
if (clone exists AND not attacking player):
    Set target = clone position
else:
    Set target = player position
```

**Why Clone Priority?** Boss preferentially chases clone if available.

#### **Part 3: Boss Update**
```
Call boss.updateChase(targetX, targetY, maze)
Boss handles its own pathfinding and movement
```

#### **Part 4: Clone Interaction**
```
if (clone exists AND boss not attacking):
    if (boss hitbox intersects clone hitbox):
        Start boss attacking clone
```

#### **Part 5: Clone Removal**
```
if (boss attacking clone):
    if (attack timer expired):
        Remove clone from world
        Set cloneDecoy = null
```

#### **Part 6: Player Hit Detection**
```
if (boss not attacking clone AND boss touches player):
    playerDied = true
    deathMessage = "Steel and panic. He never stops hunting."
```

---

### **Method 4: `updatePaleLuna()` [Boss Collision & State Tracking]**

**The Goal:**
Update Pale Luna and detect Luna collisions/win conditions.

**How it Works (Layman's Terms):**

#### **Part 1: Reset Feedback**
```
screenShake = false
playHeartbeat = false
playScream = false
```

#### **Part 2: Update Luna State**
```
Call luna.update(playerX, playerY, inEscapeRoom, lolliRecentlyCollected, maze)
Luna handles her own state machine
```

#### **Part 3: Detect Luna State Transitions**
```
if (Luna was NOT hunting, now IS hunting):
    screenShake = true
    playHeartbeat = true
```

**Why Feedback?** Dramatic effect when Luna enters hunt phase.

#### **Part 4: Check Exiting Escape Room**
```
if (player exiting escape room AND Luna waiting at door):
    playerDied = true
    deathMessage = "She waited at the door. You stepped out anyway."
    return
```

**Why This Check?** Luna guards escape rooms; exiting into her is fatal.

#### **Part 5: Luna Scream Detection**
```
Calculate distance from player to Luna (in tiles)
if (Luna hunting AND distance <= 3 tiles AND scream not on cooldown):
    playScream = true
```

**Why Distance-Based?** Scream triggers at close range, creating dread.

#### **Part 6: Direct Contact**
```
if (not in escape room AND Luna hunting AND hitboxes intersect):
    playerDied = true
    deathMessage = "She found your pulse before you heard her footsteps."
    return
```

#### **Part 7: Line-of-Sight Escape Room Exit**
```
if (not in escape room AND Luna waiting at door):
    Check line-of-sight from Luna to player
    if (Luna can see player):
        playerDied = true
        deathMessage = "She waited at the door. You stepped out anyway."
```

**Why LoS Check?** Luna can see player exiting if not blocked by walls.

---

### **Method 5: `distInTiles()` [Helper - Distance Calculation]**

**The Goal:**
Calculate Euclidean distance between two world positions in tile units.

**How it Works (Layman's Terms):**
```
dx = |x1 - x2| / TILE_SIZE
dy = |y1 - y2| / TILE_SIZE
return sqrt(dx² + dy²)
```

Used for range checks (activation, scream triggers, etc.).

---

## 5. Deep Dive: Model-View-Controller (MVC) Pattern

**Where CollisionSystem Fits in MVC:**

| MVC Layer | CollisionSystem's Role |
|-----------|---|
| **Model** | ❌ No (doesn't represent entities) |
| **View** | ❌ No |
| **Controller** | ✅ **YES** Enforces game rules and win/lose conditions |

**Collision Detection Pipeline:**
```
GameStateManager (each frame):
    ├─ Calls collisionSystem.checkChestCollisions(...)
    ├─ Calls collisionSystem.checkGuardThreats(...)
    ├─ Calls collisionSystem.updateSerialKiller(...)
    ├─ Calls collisionSystem.updatePaleLuna(...)
    │
    └─ Reads collision results:
        ├─ playerDied → trigger game over
        ├─ newDistractions → add to player ability
        ├─ screenShake → notify renderer
        ├─ playHeartbeat/playScream → notify sound system
```

---

## 6. Lab Final Presentation Arsenal

Here are 3 professional talking points for your lab presentation:

• **"CollisionSystem decouples collision detection from collision response: rather than embedding death logic inside entity classes, CollisionSystem centralizes all collision rules. When a player hits a guard, the system detects the collision, evaluates conditions (is guard distracted? is player in escape room?), and returns decision flags to GameStateManager. This separation ensures consistent rule application and simplifies testing."**

• **"The system implements staged collision priority: items are checked first (resource collection), then guards (spatial threats), then Luna (existential threat), then Serial Killer (Level 3 threat). This prioritization ensures players receive appropriate feedback and that detection order doesn't create unintended interactions—if a player collects a lollipop and hits Luna simultaneously, item collection is processed first."**

• **"Luna's escape-room line-of-sight detection (maze.hasLineOfSight()) adds spatial realism: Luna can guard a door, but only if she has unobstructed view. This prevents scenarios where Luna could magically 'see' through walls. The implementation validates game logic against geometric reality, enhancing believability and creating depth in simple mechanics."**

---

## 7. Collision Detection Matrix

| Collision | Condition | Result |
|-----------|-----------|--------|
| Player-Item | Hitbox intersect | Collect item |
| Player-Guard | Guard not distracted + touching/in room | Game over (type-specific message) |
| Player-Luna (Hunt) | Hunting + not in escape room + touching | Game over |
| Player-Luna (Door) | Luna waiting + player exiting + LoS | Game over |
| Boss-Clone | Boss touching clone | Boss attacks clone for 10s |
| Boss-Player | Boss attacking clone ends | Boss kills player |

---

## 8. Key Takeaway

**CollisionSystem is the **rule enforcer**.** It's where the abstract game design ("if Luna catches player, they die") becomes concrete code. Collision detection is one thing; collision response is another. CollisionSystem handles both, implementing the consequences of player choices and interactions with the world. It's the **beating heart** of game logic—every significant game event flows through its checks.
