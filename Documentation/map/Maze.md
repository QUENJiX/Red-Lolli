# Maze.java - Complete Structural Analysis

## 1. The "Elevator Pitch" (High-Level Overview)

**Maze** is the **level map system**—it loads CSV map files and provides pathfinding, collision detection, and spatial queries. It's the backbone of spatial gameplay: checking if movement would hit walls, determining safe vs. dangerous areas, finding paths for enemies to pursue the player, and detecting line-of-sight. Think of it as the **map database**—every location-based question in the game (Is this a wall? Is player in escape room? What's the next step toward the player?) goes to Maze. It translates abstract game world into a grid-based spatial system.

---

## 2. Core Computer Science Concepts

### **Design Patterns & Techniques Used:**
- **Grid-Based Collision:** Tiles as collision units (32×32 pixels)
- **BFS Pathfinding:** Breadth-First Search for optimal enemy pursuit
- **Line-of-Sight Raycast:** Discrete sampling along line to detect walls
- **Spatial Queries:** Finding all tiles of specific type
- **Data-Driven Levels:** CSV format enables designer-friendly level creation

### **Why These Concepts Matter:**
- **Grid System:** Simplifies collision math; O(n) checks instead of complex geometry
- **BFS:** Guarantees shortest path for enemies; intuitive and optimized
- **Line-of-Sight:** Adds spatial realism (Luna can't see through walls)
- **CSV Loading:** Designers can create levels without touching code

---

## 3. Deep Dive: Variables and State

### **Grid Representation:**

| Field | Type | Purpose |
|-------|------|---------|
| `mapGrid` | `int[][]` | 2D array of tile types |
| `TILE_SIZE` | `static double` | 40 pixels per tile |
| `Y_OFFSET` | `static double` | 50-pixel UI offset |

### **Spawn Point:**

| Field | Type | Purpose |
|-------|------|---------|
| `playerSpawnRow` | `int` | Row of player start position |
| `playerSpawnCol` | `int` | Column of player start position |

### **Level Theme:**

| Field | Type | Purpose |
|-------|------|---------|
| `levelTheme` | `int` | Current level (1-3, affects tileset selection) |

### **Escape Room State:**

| Field | Type | Purpose |
|-------|------|---------|
| `escapeRoomOpen` | `boolean[]` | Which escape rooms are currently open? |

---

## 4. Deep Dive: Methods and Logic (Step-by-Step)

### **Method 1: Constructor `Maze(String csvFilePath)` / `Maze(String csvFilePath, int levelTheme)`**

**The Goal:**
Load a maze from CSV file and set level theme.

**How it Works (Layman's Terms):**
1. Store level theme (for tileset selection by GameRenderer)
2. Call loadMapFromCSV() to parse CSV into grid

---

### **Method 2: `loadMapFromCSV()` [CSV Parsing - CRITICAL]**

**The Goal:**
Parse CSV file and populate mapGrid, extracting player spawn point.

**How it Works (Layman's Terms):**

#### **Part 1: Try to Load File**
```
Get InputStream from classpath using path
if (file not found):
    Create fallback grid (6×5 small maze)
    Return (graceful degradation)
```

#### **Part 2: Parse CSV**
```
For each line in CSV:
    Split by commas
    Convert each token to integer
    Add row to rowList
Close file
Convert rowList to 2D array (mapGrid)
```

#### **Part 3: Extract Player Spawn Point**
```
Scan mapGrid for tile type 7 (player spawn marker)
if (found):
    Store row/col as playerSpawnRow/playerSpawnCol
    Change tile to 0 (walkable) so spawn doesn't block path
```

---

### **Method 3: `isWallCollision(Hitbox2D nextHitbox)` [Collision Detection]**

**The Goal:**
Check if entity would collide with walls at next position.

**How it Works (Layman's Terms):**

#### **Part 1: Convert Hitbox to Grid**
```
Get hitbox bounds: minX, maxX, minY, maxY
Convert to grid coordinates:
    leftCol = minX / TILE_SIZE
    rightCol = maxX / TILE_SIZE
    topRow = (minY - Y_OFFSET) / TILE_SIZE
    bottomRow = (maxY - Y_OFFSET) / TILE_SIZE
```

#### **Part 2: Boundary Check**
```
if (any edge extends beyond grid):
    return true (outside world = collision)
```

#### **Part 3: Check All Tiles**
```
For each tile in the bounding box:
    if (any tile == 1 [wall]):
        return true (wall hit)
return false (no collision)
```

**Why Multiple Tiles?** Prevents clipping through corners; checks entire collision shape.

---

### **Method 4: `isEscapeRoom(Hitbox2D hitbox)` [Safe Zone Detection]**

**The Goal:**
Determine if entity is in an escape room (safe area).

**How it Works (Layman's Terms):**

#### **Part 1: Multi-Point Sampling**
```
Test 5 points from entity hitbox:
    1. Center point
    2. Left edge center
    3. Right edge center
    4. Top edge center
    5. Bottom edge center
```

#### **Part 2: Check Each Point**
```
For each point:
    Convert to grid coordinates
    Check if tile type == 6 (escape room)
    if (any point in escape room):
        return true
return false (not in safe zone)
```

**Why Multiple Points?** Prevents partially-overlapping entities from being falsely detected as inside.

---

### **Method 5: `updateEscapeRoomState()` [Safe Zone Status Update]**

**The Goal:**
Update which escape rooms are "open" (player nearby) each frame.

**How it Works (Layman's Terms):**

#### **Part 1: Initialize State Array**
```
if (first call):
    Find all escape room tiles (type 6)
    Create boolean array (one per room)
```

#### **Part 2: Check Player Proximity**
```
Convert player position to grid coordinates
For each escape room:
    Calculate distance (in tiles):
        dr = |playerRow - roomRow|
        dc = |playerCol - roomCol|
    if (dr <= 1 AND dc <= 1):
        Mark room as open
    else:
        Mark room as closed
```

**Why Proximity Check?** Only "nearby" rooms should count as accessible.

---

### **Method 6: `isEscapeRoomOpen()` [Safe Zone Query]**

**The Goal:**
Check if a specific tile is part of an open escape room.

**How it Works (Layman's Terms):**
```
Find escape room containing (row, col)
If found:
    return its open/closed status
else:
    return false
```

---

### **Method 7: `findSafeRoomDoor()` [Safe Room Exit]**

**The Goal:**
Find an adjacent non-wall tile (exit from escape room).

**How it Works (Layman's Terms):**

#### **Part 1: Get Center Position**
```
Calculate center of hitbox
Convert to grid coordinates
```

#### **Part 2: Check Cardinal Directions**
```
Check 4 adjacent tiles (up, down, left, right)
For first non-wall tile found:
    return that tile
If no adjacent tile works:
    return center tile (shouldn't happen)
```

---

### **Method 8: `getNextMove()` [Pathfinding - BFS - CRITICAL]**

**The Goal:**
Find next step on optimal path from start to target using BFS.

**How it Works (Layman's Terms):**

#### **Part 1: Sanity Checks**
```
if (already at target):
    return current position
if (start outside grid):
    return null (no path)
```

#### **Part 2: BFS Setup**
```
Create visited grid (track visited tiles)
Create queue of Node objects
Enqueue starting position
Mark start as visited
```

**Why BFS?** Guarantees shortest path; optimal for grid-based movement.

#### **Part 3: BFS Traversal**
```
While queue not empty:
    Dequeue current node
    if (reached target):
        targetNode = current
        break
    
    Enqueue all valid neighbors:
        4 cardinal directions (up, down, left, right)
        Skip if: visited, wall (type 1), out of bounds
        Mark new tile as visited
```

#### **Part 4: Reconstruct Path**
```
if (target not found):
    return null (no path possible)

Backtrack from targetNode to start:
    Walk up parent chain
    Stop when parent.parent == null
    This gives the first step toward target
return that first step
```

**Why Reconstruct?** BFS gives full path; we only need next step.

---

### **Method 9: `hasLineOfSight()` [Raycast - Visibility Check]**

**The Goal:**
Determine if two points can see each other (unobstructed by walls).

**How it Works (Layman's Terms):**

#### **Part 1: Calculate Ray Length**
```
distX = |x2 - x1|
distY = |y2 - y1|
maxDist = max(distX, distY)
```

#### **Part 2: Sample Discrete Points**
```
steps = ceil(maxDist / (TILE_SIZE / 2))
For i from 0 to steps:
    t = i / steps (lerp parameter)
    cx = x1 + t * (x2 - x1)
    cy = y1 + t * (y2 - y1)
```

**Why Small Samples?** Prevents missing thin walls; TILE_SIZE/2 ensures dense coverage.

#### **Part 3: Check Each Sample**
```
Convert each sample point to grid coordinates
if (any sample hits wall [type 1]):
    return false (blocked)
return true (clear line of sight)
```

---

### **Method 10: `getTilesOfType()` [Spatial Query]**

**The Goal:**
Find all tiles of a specific type (e.g., all escape rooms, all item spawns).

**How it Works (Layman's Terms):**
```
Scan entire mapGrid
For each tile:
    if (type matches):
        Add to results list
return results
```

Used by EntityManager to spawn items, find escape rooms, etc.

---

### **Method 11: `getTilePositionAt()` [World-to-Grid Conversion]**

**The Goal:**
Convert world pixel coordinates to grid tile coordinates.

**How it Works (Layman's Terms):**
```
col = worldX / TILE_SIZE
row = (worldY - Y_OFFSET) / TILE_SIZE
return [row, col]
```

---

### **Method 12: `getMapGrid()` / `getLevelTheme()` / etc. [Accessors]**

**The Goal:**
Provide read access to maze data.

**How it Works (Layman's Terms):**
```
return mapGrid, levelTheme, or spawn coordinates
```

---

## 5. Tile Type Reference

| Type | Meaning | Behavior |
|------|---------|----------|
| 0 | Empty | Walkable, no collision |
| 1 | Wall | Solid collision |
| 2 | Empty Chest | Collectible (spawn item) |
| 3 | Lollipop Chest | Objective item |
| 4 | Player Spawn | Player starts here (converted to 0) |
| 5 | Luna Spawn | Pale Luna appears here |
| 6 | Escape Room | Safe zone, no collision |
| 7 | Player Marker | Spawn indicator (converted to 0) |
| 8 | Torch | Decorative entity |
| 9 | Guard Spawn | Level-specific enemy |
| 10 | Serial Killer | Level 3 boss |

---

## 6. Lab Final Presentation Arsenal

Here are 3 professional talking points for your lab presentation:

• **"The grid-based collision system reduces complexity from continuous geometry to discrete cell checking: rather than polygon intersection testing, the system checks which tiles a hitbox spans and evaluates them O(width × height). This trade-off—quantization loss for computational simplicity—is standard in industry for real-time games."**

• **"BFS pathfinding guarantees shortest path on the grid: enemies pursue player via optimal routes, not greedy approximations. While BFS is O(n×m) worst-case, the small grid sizes (typically <20×20 tiles) make this negligible. The algorithm ensures players can't fool enemies through pathfinding quirks."**

• **"Line-of-sight raycast uses discrete sampling at TILE_SIZE/2 intervals, creating a balance between precision and efficiency: dense enough to catch thin walls, sparse enough to compute in <1ms. This technique demonstrates intelligent algorithm design—not minimum-cost, but cost-effective for the specific problem."**

---

## 7. Performance Characteristics

| Operation | Complexity | Usage |
|-----------|-----------|-------|
| Collision check | O(w×h) | Every entity update |
| Pathfinding BFS | O(n×m) | Enemy pursuit (cached) |
| Line-of-sight raycast | O(steps) | Luna door escape check |
| Tile query | O(n×m) | Level loading (once) |

Where n×m = grid size (typically 20×20 = 400 tiles).

---

## 8. Key Takeaway

**Maze is the **spatial foundation** of gameplay.** Every location-based mechanic flows through Maze: collision detection (can I move?), pathfinding (where should enemy go?), visibility (can Luna see me?), safe zones (am I protected?). Maze transforms continuous 2D space into a grid-based system that's simple to reason about, efficient to compute, and designer-friendly via CSV editing. It's the **glue** connecting abstract game logic to concrete spatial reality.
