# EntityManager.java - Complete Structural Analysis

## 1. The "Elevator Pitch" (High-Level Overview)

**EntityManager** is the **game object registry and spawning system**. It maintains collections of all game entities (player, monsters, guards, items, torches, etc.) and handles their creation/destruction lifecycle. Think of it as a **master inventory**—every entity that exists in the game is tracked here. When a level starts, EntityManager reads the map CSV and instantiates all entities at their map-defined locations. When entities die or are collected, EntityManager removes them. It's the **central repository** that answers questions like "Where are all the guards?" or "Does a clone decoy exist?".

---

## 2. Core Computer Science Concepts

### **Design Patterns & Techniques Used:**
- **Registry Pattern:** Central registry tracking all active entities
- **Factory Pattern:** spawnEntities() creates entities based on map tile types
- **Collections API:** Typed lists for efficient entity queries
- **Unmodifiable Collections:** Returns immutable views preventing external mutation
- **Tile-to-Position Conversion:** Converts map grid coordinates to world pixel coordinates

### **Why These Concepts Matter:**
- **Registry:** Centralized knowledge base eliminates need to pass entity references everywhere
- **Factory:** Map-driven spawn logic keeps level design data separate from instantiation logic
- **Unmodifiable Views:** Prevents external code from corrupting entity lists
- **Collections:** Different lists (entities, chests, guards, torches) enable efficient filtered queries

---

## 3. Deep Dive: Variables and State

### **Entity Storage:**

| Field | Type | Purpose |
|-------|------|---------|
| `player` | `Player` | Reference to the player avatar |
| `paleLuna` | `Monster` | Reference to Pale Luna boss |
| `serialKiller` | `SerialKillerEntity` | Reference to Level 3 boss (null if Level 1-2) |
| `cloneDecoy` | `CardboardClone` | Reference to player's current clone (null if not spawned) |

### **Entity Collections:**

| Field | Type | Purpose |
|-------|------|---------|
| `entities` | `List<Entity>` | All entities in the world (players, monsters, items, torches, guards) |
| `chests` | `List<Item>` | Specific tracking of collectible items for efficient inventory checks |
| `guards` | `List<GuardEntity>` | Level guardians for efficient guard-specific logic |
| `torches` | `List<TorchEntity>` | Environmental torches for rendering |

---

## 4. Deep Dive: Methods and Logic (Step-by-Step)

### **Method 1: `clear()` [Level Cleanup]**

**The Goal:**
Remove all entities when transitioning to a new level.

**How it Works (Layman's Terms):**
```
Empty all entity collections
Reset all entity references to null
```

**Why:** Prevents entity carryover between levels.

---

### **Method 2: `spawnEntities(Maze maze, int currentLevel)` [Factory Method - CRITICAL]**

**The Goal:**
Read map CSV and instantiate all entities at their map-defined locations.

**How it Works (Layman's Terms):**

#### **Part 1: Scan Map Grid**
```
Read the maze's map grid
Look for special tile types:
    2 = empty chest
    3 = lollipop chest
    5 = Pale Luna spawn
    8 = torch
    9 = guard
    10 = Serial Killer spawn
```

#### **Part 2: Collect Tile Positions**
```
As we scan, collect lists of tile positions for each entity type
Track Luna's spawn position separately
```

#### **Part 3: Spawn Guards (Tile Type 9)**

For each guard spawn location:
```
Find nearest escape room (type 6 tile)
Calculate distance to all escape rooms
Pick closest one (guards protect nearest escape rooms)

Create guard of appropriate type based on level:
    Level 1: BAT
    Level 2: COBRA
    Level 3: CENTIPEDE

Add guard to guards list
```

**Why Level-Specific Guards?**
- Creates difficulty progression
- Each level introduces a new guard type
- Different guard types have different distraction durations

#### **Part 4: Spawn Serial Killer (Tile Type 10)**
```
if (currentLevel == 3):
    Create SerialKillerEntity at location
else:
    serialKiller remains null
```

#### **Part 5: Spawn Items (Empty & Lollipop Chests)**

For each empty chest tile:
```
Default content: EMPTY
If (Level 3 AND no clone decoy exists yet):
    Change to CLONE_DECOY (exactly one clone per game)
Create Item at location
Add to both chests and entities lists
```

For each lollipop chest tile:
```
Create Item with LOLLI content
Add to both lists
```

**Why CLONE_DECOY Uniqueness?**
- containsContent() method ensures exactly one clone exists
- Level 3 only, transforms one empty chest into clone
- First empty chest scanned becomes the clone

#### **Part 6: Spawn Torches (Tile Type 8)**
```
For each torch tile:
    Create TorchEntity at location
    Add to torches and entities lists
```

#### **Part 7: Spawn Pale Luna (Tile Type 5)**
```
If Luna position found (lunaRow >= 0):
    Create Monster at location
    Add to entities list
```

---

### **Method 3: `containsContent(Item.ContentType type)` [Item Query Helper]**

**The Goal:**
Check if any existing chest contains a specific content type.

**How it Works (Layman's Terms):**
```
Search all chests for any chest with matching content type
return true if found, false otherwise
```

Used to ensure exactly one CLONE_DECOY exists per level.

---

### **Method 4: `addEntity()` / `removeEntity()` [Entity Lifecycle]**

**The Goal:**
Dynamically add/remove entities during gameplay.

**How it Works (Layman's Terms):**
```
addEntity(entity): Add entity to entities list
removeEntity(entity): Remove entity from entities list
```

**Used for:** When clone dies, remove from entities.

---

### **Method 5: Entity Getter Methods**

**The Goal:**
Provide access to specific entities.

```
getPlayer() → return player
getPaleLuna() → return paleLuna
getSerialKiller() → return serialKiller
getCloneDecoy() → return cloneDecoy
```

---

### **Method 6: Entity Setter Methods**

**The Goal:**
Update entity references when needed.

```
setPlayer(player) → update player reference
setCloneDecoy(decoy) → update clone reference (null when destroyed)
```

---

### **Method 7: Collection Getter Methods**

**The Goal:**
Return unmodifiable views of entity collections.

```
getEntities() → all entities
getChests() → all collectible items
getGuards() → all guards
getTorches() → all torches
```

**Why Unmodifiable?** Prevents external code from adding/removing entities directly.

---

## 5. Deep Dive: Model-View-Controller (MVC) Pattern

**Where EntityManager Fits in MVC:**

| MVC Layer | EntityManager's Role |
|-----------|---|
| **Model** | ✅ **Partial** Registry of model entities |
| **View** | ❌ No |
| **Controller** | ✅ **Partial** Controls entity spawning/destruction |

**The Repository Pattern:**
```
GameStateManager queries EntityManager for all entity data
GameRenderer reads entity data from EntityManager's collections
CollisionSystem queries EntityManager for specific entities to test
```

---

## 6. Lab Final Presentation Arsenal

Here are 3 professional talking points for your lab presentation:

• **"EntityManager implements the Registry design pattern, centralizing knowledge of all game objects. Rather than passing entity references globally or embedding them throughout the codebase, EntityManager provides a single source of truth. This architecture eliminates subtle bugs from reference staleness and makes entity lifecycle management straightforward—all spawning and destruction flows through one system."**

• **"The spawnEntities() factory method demonstrates data-driven design: map CSV defines entity types and positions, and factory logic instantiates appropriate entity objects. This separation of concern (data vs. instantiation logic) enables level designers to compose new levels without touching code—they only edit the CSV file."**

• **"The CLONE_DECOY uniqueness constraint—exactly one clone per level via containsContent() checking—demonstrates sophisticated spawn logic. Rather than spawning items at fixed locations, the system examines existing items and dynamically decides which chest becomes the clone, creating varied puzzle layouts within the same map."**

---

## 7. Map Tile Types Reference

| Tile # | Type | Spawns | Behavior |
|--------|------|--------|----------|
| 0 | Empty | N/A | Walkable |
| 1 | Wall | N/A | Collision |
| 2 | Empty Chest | Item (EMPTY or CLONE_DECOY) | Collectible |
| 3 | Lollipop Chest | Item (LOLLI) | Collectible + objective |
| 5 | Luna Spawn | Monster | Boss enemy |
| 6 | Escape Room | N/A | Safe zone |
| 8 | Torch | TorchEntity | Decorative |
| 9 | Guard | GuardEntity (type by level) | Level guardian |
| 10 | Serial Killer | SerialKillerEntity | Level 3 boss |

---

## 8. Key Takeaway

**EntityManager is the **operational control center** for entity lifecycle.** It's not glamorous—no AI, no physics, no animation. But it's essential: spawning, tracking, and querying entities. It's the **glue** that connects map data to runtime objects. Every entity that exists came from EntityManager. Every query for entities goes through EntityManager. It's the single source of truth in a complex game world.
