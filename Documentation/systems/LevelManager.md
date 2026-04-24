# LevelManager.java - Complete Structural Analysis

## 1. The "Elevator Pitch" (High-Level Overview)

**LevelManager** is the **level loading and progression system**. It handles three primary responsibilities: managing the current level number (1-3), loading map CSV files via the Maze class, and spawning the player and all entities at level start. Think of it as the **game's chapter manager**—when you transition between levels, LevelManager orchestrates everything needed for a fresh level: loading the map, creating the player at spawn point, populating entities at their map-defined locations. It's the **bridge between data (CSV maps) and runtime (instantiated game world)**.

---

## 2. Core Computer Science Concepts

### **Design Patterns & Techniques Used:**
- **Facade Pattern:** Encapsulates complex level loading behind simple interface
- **Resource Loading:** Static map files loaded from classpath
- **State Management:** Tracks current level number
- **Initialization Orchestration:** Coordinates multiple system startup steps
- **Singleton Asset Initialization:** Ensures graphics/audio systems initialized before rendering

### **Why These Concepts Matter:**
- **Facade:** Hides complexity; callers just call loadLevel() without worrying about details
- **Resource Management:** Maps defined in files, not hardcoded; enabling easy level design iteration
- **State Tracking:** LevelManager becomes source of truth for "which level are we on?"
- **Initialization Order:** Must load images before rendering, or game crashes

---

## 3. Deep Dive: Variables and State

### **Level Tracking:**

| Field | Type | Purpose |
|-------|------|---------|
| `currentLevel` | `int` | Current level (1-3) |

### **Map Management:**

| Field | Type | Purpose |
|-------|------|---------|
| `maze` | `Maze` | Current level's maze (loaded from CSV) |

### **Map Files:**

| Field | Type | Purpose |
|-------|------|---------|
| `MAP_FILES` | `static String[]` | Array of map CSV paths: {"/map.csv", "/map2.csv", "/map3.csv"} |

---

## 4. Deep Dive: Methods and Logic (Step-by-Step)

### **Method 1: `getMaze()` [Map Query]**

**The Goal:**
Return reference to current level's maze.

**How it Works (Layman's Terms):**
```
return maze;
```

---

### **Method 2: `getCurrentLevel()` [Level Query]**

**The Goal:**
Return current level number.

**How it Works (Layman's Terms):**
```
return currentLevel;
```

---

### **Method 3: `setCurrentLevel(int level)` [Level Setter]**

**The Goal:**
Set the active level number (used when progressing between levels).

**How it Works (Layman's Terms):**
```
currentLevel = level;
```

---

### **Method 4: `loadLevel(EntityManager entityManager)` [Main Level Loading - CRITICAL]**

**The Goal:**
Initialize a complete game level: load map, initialize rendering systems, spawn player, populate entities.

**How it Works (Layman's Terms):**

#### **Part 1: Initialize Graphics System**
```
Call GameRenderer.initImages()
Call HUDRenderer.initImages()
```

**Why Initialization?**
- GameRenderer caches sprite images; must be initialized before first render
- HUDRenderer does same for UI graphics
- Called every level to ensure assets are loaded

#### **Part 2: Load Map**
```
Get map file path from MAP_FILES array using (currentLevel - 1)
    Level 1 → map.csv
    Level 2 → map2.csv
    Level 3 → map3.csv

Create new Maze object, passing:
    - Map file path
    - Current level number (affects enemy types, etc.)
```

**Why Level Number?** Maze class uses level number to determine tile interpretation (e.g., which guard type spawns at type-9 tiles).

#### **Part 3: Calculate Player Spawn**
```
Get player spawn position from maze:
    spawnCol = maze.getPlayerSpawnCol() * TILE_SIZE + 10
    spawnRow = maze.getPlayerSpawnRow() * TILE_SIZE + Y_OFFSET + 10
```

**Offset Logic:**
- Column × 32 pixels/tile + 10-pixel centering
- Row × 32 pixels/tile + Y_OFFSET (visual margin) + 10-pixel centering

#### **Part 4: Create Player**
```
Create new Player at spawn position
Set as active player in EntityManager
Add to EntityManager's entity list
```

#### **Part 5: Spawn All Other Entities**
```
Call entityManager.spawnEntities(maze, currentLevel)
    - Reads map CSV
    - Instantiates guards, Luna, items, torches
    - Populates all collections
```

---

## 5. Deep Dive: Model-View-Controller (MVC) Pattern

**Where LevelManager Fits in MVC:**

| MVC Layer | LevelManager's Role |
|-----------|---|
| **Model** | ✅ **Partial** Manages level state |
| **View** | ✅ **Partial** Initializes rendering systems |
| **Controller** | ✅ **Partial** Orchestrates level transitions |

**Level Loading Pipeline:**
```
HelloApplication (game start or level complete):
    ├─ Set level via levelManager.setCurrentLevel(newLevel)
    ├─ Clear old entities via entityManager.clear()
    ├─ Load new level via levelManager.loadLevel(entityManager)
    │   ├─ Initialize rendering systems
    │   ├─ Load maze from CSV
    │   ├─ Create and spawn player
    │   └─ Populate all entities (via EntityManager)
    │
    └─ Game now ready to run at new level
```

---

## 6. Lab Final Presentation Arsenal

Here are 3 professional talking points for your lab presentation:

• **"LevelManager implements the Facade design pattern, exposing a simple loadLevel() interface that hides substantial complexity: CSV loading, maze instantiation, coordinate system conversion, rendering initialization, and entity population. This architectural approach isolates level-loading concerns, making the system extensible—adding new levels requires only new CSV files, not code changes."**

• **"The level loading process demonstrates proper initialization ordering: graphics systems must be initialized before rendering occurs. By explicitly calling GameRenderer.initImages() and HUDRenderer.initImages() during loadLevel(), the system ensures that assets are available when rendering starts. This ordering prevents common bugs (null sprites, missing textures) through deliberate sequencing."**

• **"Map-driven spawning via EntityManager.spawnEntities() separates level design from instantiation logic: level designers edit CSV files to place entities, and the factory code interprets those files into runtime objects. This data-driven approach scales—designers can compose complex levels without touching code, and programmers can optimize spawning logic without rebalancing all levels."**

---

## 7. Map File Structure

**Map CSV Format:**

Each cell represents a 32×32 pixel tile:

| Value | Type | Behavior |
|-------|------|----------|
| 0 | Empty | Walkable, no collision |
| 1 | Wall | Solid collision |
| 2 | Empty Chest | Collectible item (may contain clone) |
| 3 | Lollipop | Objective item |
| 4 | Player Spawn | Player starts here |
| 5 | Luna Spawn | Pale Luna appears here |
| 6 | Escape Room | Safe zone, no Luna |
| 8 | Torch | Decorative animation |
| 9 | Guard Spawn | Level-specific guard |
| 10 | Serial Killer | Level 3 boss |

---

## 8. Key Takeaway

**LevelManager is the **orchestrator of new beginnings**.** Every time a player starts or advances levels, LevelManager choreographs the entire transition. It's not complex, but it's critical—it ensures proper initialization order, maps CSV data to runtime objects, and sets up the game world. It's the **conductor** ensuring all systems are ready before the player takes control.
