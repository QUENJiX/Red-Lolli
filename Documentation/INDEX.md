# Red Lolli - Complete Class Index

## Overview
The RedLolli game is an escape-room puzzle game where players must collect lollipops while evading Pale Luna in a maze. Total of 23 documented classes across 6 architectural layers.

---

## Phase 1: Entry & Architecture (3 classes)

### 1. **Launcher.java**
- **Type:** Static entry point
- **Purpose:** Application bootstrap; delegates to JavaFX Application.launch()
- **Key Methods:** main(String[])
- **Responsibility:** Program start; language-agnostic entry
- **File:** [Launcher.md](Launcher.md)

### 2. **HelloApplication.java**
- **Type:** JavaFX Application (controller + orchestrator)
- **Purpose:** Main game director; manages scenes, input, 60 FPS game loop
- **Key Responsibilities:** Scene management, input polling, game loop orchestration, cutscene handling
- **Key Methods:** start(Stage), playIntroAndStart(), startGame(int), setupGameScene()
- **Handles:** Scene transitions, input tracking (activeKeys, pressedThisFrame), cutscene timing
- **File:** [HelloApplication.md](HelloApplication.md)

### 3. **GameStateManager.java**
- **Type:** Model/Engine (game logic container)
- **Purpose:** Core game engine; maintains all game state and executes update logic
- **Key Responsibilities:** Entity updates, collision detection orchestration, event triggering, game-over detection
- **Key Methods:** update(Set<KeyCode>), resetGameState(), loadLevel()
- **State Tracking:** Entity references, timers, cross-level statistics (totalChestsCollected, totalPlayTime)
- **Delta-Time:** Normalized 60 FPS timing system
- **File:** [GameStateManager.md](GameStateManager.md)

---

## Phase 2: Core Infrastructure (4 classes)

### 4. **Collidable.java** (Interface)
- **Type:** Contract/Interface
- **Purpose:** Collision capability contract
- **Required Methods:** getHitbox()
- **Implemented By:** Player, Monster, GuardEntity, SerialKillerEntity, Item, TorchEntity, CardboardClone
- **Pattern:** Dependency Inversion Principle
- **File:** [Collidable.md](Collidable.md)

### 5. **Hitbox2D.java**
- **Type:** Collision geometry
- **Purpose:** AABB (Axis-Aligned Bounding Box) collision shape
- **Key Methods:** intersects(Hitbox2D), getMinX(), getMaxX(), getMinY(), getMaxY()
- **Algorithm:** O(1) constant-time intersection: `x1_max > x2_min && x1_min < x2_max && y1_max > y2_min && y1_min < y2_max`
- **Immutable:** All fields final; no setters
- **File:** [Hitbox2D.md](Hitbox2D.md)

### 6. **GameEventBus.java**
- **Type:** Publish-Subscribe event system (Singleton)
- **Purpose:** Central event distribution hub
- **Key Methods:** publish(GameEvent), register(EventListener), getInstance()
- **Event Types:** 10 defined (LOLLI_COLLECTED, CLONE_USED, MONSTER_DISTRACTED, LUNA_HUNT_STARTED, etc.)
- **Thread-Safe:** ConcurrentLinkedQueue for re-entrant event handling
- **File:** [GameEventBus.md](GameEventBus.md)

### 7. **Entity.java** (Abstract Base Class)
- **Type:** Abstract entity foundation
- **Purpose:** Base class for all game objects
- **Key Fields:** x, y, size (protected for subclass access)
- **Abstract Methods:** update(), getHitbox()
- **Inheritance:** Player, Monster, GuardEntity, SerialKillerEntity, Item, TorchEntity, CardboardClone
- **File:** [Entity.md](Entity.md)

---

## Phase 3: Entity System (7 classes)

### 8. **Player.java** (Extends Entity)
- **Type:** Player avatar
- **Purpose:** Protagonist character; primary controllable entity
- **Key Mechanics:**
  - Stamina system: 180-frame sprint, 180-frame exhaustion, 0.5x regeneration
  - Sanity system: -1/sec baseline, -2/sec near Luna, +1/sec in escape room, max 100
  - Animation: 4-frame walk cycle, directional facing
  - Movement: Wall collision, speed modulation (exhausted 60%, sprint 180%)
- **Key Methods:** move(dx, dy, maze, sprinting), update(), updateNearLunaStatus(lunaX, lunaY)
- **File:** [Player.md](Player.md)

### 9. **Monster.java** (Extends Entity)
- **Type:** Pale Luna (primary antagonist)
- **Purpose:** Boss enemy; sophisticated pursuit AI
- **State Machine:** DORMANT → STALKING → HUNTING → WAITING_AT_DOOR
- **Speed:** STALK 3.2 px/frame, HUNT 6.4 px/frame (2x)
- **Behavior:**
  - Dormant 300 frames (sleeps)
  - Stalking 300 frames (aware, slow pursuit)
  - Hunting 420 frames (aggressive, fast pursuit)
  - Waiting at door (blocks escape room)
- **Key Methods:** update(playerX, playerY, inEscapeRoom, lolliCollected, maze), pursuePlayer(), positionAtDoor()
- **File:** [Monster.md](Monster.md)

### 10. **GuardEntity.java** (Extends Entity)
- **Type:** Level guardians
- **Purpose:** Patrol enemies protecting escape rooms
- **Type System:** BAT (300-frame distraction), COBRA (180), CENTIPEDE (120)
- **Mechanics:** Distraction spell, room guarding, range-based threat
- **Key Methods:** distract(), isPlayerOnGuardedRoom(), isWithinDistractionRange()
- **File:** [GuardEntity.md](GuardEntity.md)

### 11. **SerialKillerEntity.java** (Extends Entity)
- **Type:** Level 3 boss
- **Purpose:** Alternative threat on final level
- **Mechanics:** Pathfinding pursuit, decoy attack (clone distraction 600 frames), animation
- **Key Methods:** updateChase(targetX, targetY, maze), startDecoyAttack(), isAttackingDecoy()
- **File:** [SerialKillerEntity.md](SerialKillerEntity.md)

### 12. **Item.java** (Extends Entity)
- **Type:** Collectible object
- **Purpose:** Objective and resource items
- **Content Types:** EMPTY (decoy), LOLLI (objective), CLONE_DECOY (ability)
- **Mechanics:** Collection flag, passive entity
- **Key Methods:** collect(), isCollected(), getContentType(), hasLolli()
- **File:** [Item.md](Item.md)

### 13. **TorchEntity.java** (Extends Entity)
- **Type:** Environmental decoration
- **Purpose:** Animated ambient decoration
- **Mechanics:** 4-frame flame animation (8 frames per frame), lit/unlit state, random initialization
- **Key Methods:** update(), setLit(boolean), isLit(), getCurrentFrame()
- **File:** [TorchEntity.md](TorchEntity.md)

### 14. **CardboardClone.java** (Extends Entity)
- **Type:** Player decoy ability
- **Purpose:** Strategic distraction for boss fights
- **Mechanics:** Completely passive; triggers boss.startDecoyAttack() on collision
- **Minimalist Design:** Only position, size, hitbox; no animation or behavior
- **Key Methods:** getHitbox()
- **File:** [CardboardClone.md](CardboardClone.md)

---

## Phase 4: Game Systems (5 classes)

### 15. **EntityManager.java**
- **Type:** Registry + Factory
- **Purpose:** Centralized entity lifecycle management
- **Key Responsibilities:**
  - Registry: Track player, Luna, Serial Killer, clone, all items, guards, torches
  - Factory: Map-driven spawning via spawnEntities()
  - Lifecycle: addEntity(), removeEntity(), clear()
- **Key Methods:** spawnEntities(maze, level), containsContent(type)
- **Pattern:** Registry pattern; dependency avoidance
- **File:** [EntityManager.md](EntityManager.md)

### 16. **CollisionSystem.java**
- **Type:** Rule enforcement engine
- **Purpose:** Collision detection and consequence implementation
- **Responsibilities:** Item collection, guard threats, Luna pursuit, Serial Killer interaction
- **State Flags:** playerDied, deathMessage, newDistractions, screenShake, playHeartbeat, playScream
- **Key Methods:** checkChestCollisions(), checkGuardThreats(), updateSerialKiller(), updatePaleLuna()
- **Logic:** Multi-phase collision checks with condition evaluation
- **File:** [CollisionSystem.md](CollisionSystem.md)

### 17. **LevelManager.java**
- **Type:** Level loading orchestrator
- **Purpose:** Map loading and entity initialization
- **Responsibilities:** Level progression, CSV parsing, asset initialization
- **Key Methods:** loadLevel(entityManager), setCurrentLevel(level)
- **Pipeline:** Graphics init → CSV load → Player spawn → Entity population
- **File:** [LevelManager.md](LevelManager.md)

### 18. **AssetManager.java** (Singleton)
- **Type:** Resource cache
- **Purpose:** Image loading and caching
- **Responsibilities:** Lazy loading, caching, graceful fallback
- **Key Methods:** getInstance(), getSprite(path), getSprite(path, width, height)
- **Pattern:** Singleton + Lazy-Load Cache
- **Fallback:** Transparent placeholders on missing images
- **File:** [AssetManager.md](AssetManager.md)

### 19. **SoundManager.java**
- **Type:** Audio system
- **Purpose:** Sound effect and music playback
- **Responsibilities:** One-shot SFX, persistent music, volume control
- **Key Methods:** playOneShot(path, volume), playMusicIfPresent(path), stopMusic()
- **Audio Types:** AudioClip (SFX), MediaPlayer (music)
- **Error Handling:** Silent failure on missing audio
- **File:** [SoundManager.md](SoundManager.md)

---

## Phase 5: Presentation & UI (3 classes)

### 20. **GameRenderer.java**
- **Type:** Main rendering engine
- **Purpose:** Game world visualization
- **Responsibilities:** Sprite rendering, animation, visual effects, terrain
- **Key Methods:** initImages(), renderMap(), renderPlayer(), renderMonster(), renderGuardEntity()
- **Features:**
  - 4-directional player animation
  - Luna's procedural aura effect (3-layer jittering polygons)
  - State-based sprite selection (idle vs. moving, distracted vs. alert)
  - Tileset rendering (3 levels × 2 wall types × 2 floor patterns)
- **Asset Budget:** ~60 sprite images (cached)
- **File:** [GameRenderer.md](GameRenderer.md)

### 21. **HUDRenderer.java**
- **Type:** Heads-up display system
- **Purpose:** Information overlay at screen top
- **Sections:** Level, Lollipop status, Objective, Luna threat, Sanity, Safety status
- **Key Methods:** drawHUD(), drawLunaSection(), drawSanitySection()
- **Features:**
  - Color-coded threat levels (green → yellow → orange → red)
  - Temporal animations (pulsing threat indicators)
  - Progress bars for Luna state transitions
- **Layout:** 6 sections, 880×50 pixel bar
- **File:** [HUDRenderer.md](HUDRenderer.md)

### 22. **SceneFactory.java**
- **Type:** Scene factory + UI builder
- **Purpose:** Non-gameplay screen creation (menus, death, victory, story)
- **Key Methods:** createDeathScene(), createVictoryScene(), createItemFoundScene()
- **Features:**
  - Narrative poetry (different for different death causes)
  - Statistics display (time, deaths, sanity, lollies, secrets)
  - Fade-in animations
  - Custom icon buttons with visual feedback
- **Screens:** Main menu, Death, Victory, Item revelation
- **File:** [SceneFactory.md](SceneFactory.md)

---

## Bonus: Spatial System (1 class)

### 23. **Maze.java**
- **Type:** Level map and spatial system
- **Purpose:** Grid-based world, collision, pathfinding, visibility
- **Tile System:** 40×40 pixel tiles (TILE_SIZE constant)
- **Algorithms:**
  - Collision: O(w×h) AABB-to-grid check
  - Pathfinding: BFS breadth-first search (guaranteed shortest path)
  - Line-of-sight: Discrete raycast with TILE_SIZE/2 sampling
- **Key Methods:** getNextMove(), isWallCollision(), hasLineOfSight(), isEscapeRoom()
- **Data Format:** CSV-based level design
- **File:** [Maze.md](Maze.md)

---

## Architecture Overview

```
        ┌─────────────────────────────────┐
        │   Launcher.main()               │
        │   Entry Point                   │
        └──────────────┬──────────────────┘
                       │
                       ▼
        ┌─────────────────────────────────┐
        │  HelloApplication               │
        │  - Scene Management             │
        │  - 60 FPS Game Loop             │
        │  - Input Handling               │
        └──────────────┬──────────────────┘
                       │
        ┌──────────────┴────────────────────────────────┐
        │                                               │
        ▼                                               ▼
┌──────────────────────────┐              ┌──────────────────────────┐
│ GameStateManager         │              │ SceneFactory             │
│ - Update Loop            │              │ - Menus                  │
│ - Collision Detection    │              │ - Death Screen           │
│ - Game Logic             │              │ - Victory Screen         │
└──────────────┬───────────┘              └──────────────────────────┘
               │                                       │
       ┌───────┼───────────────────────────────────────┴────────┐
       │       │                                                │
       ▼       ▼                                                ▼
     Entity  Collision      Level    Asset    Sound         Game      HUD
    Manager   System       Manager   Manager  Manager      Renderer  Renderer
       │       │             │         │        │            │         │
       │       └─────────────┼─────────┼────────┼────────────┴────────┐
       │                     │         │        │                     │
       └─────────────────────┼─────────┼────────┼─────────────────────┘
                             │         │        │
                             ▼         ▼        ▼
                         All Entities, Sprites, Audio
                             │
                             ▼
                    Game World + UI Rendering
```

---

## Key Architectural Decisions

| Decision | Benefit |
|----------|---------|
| **Event Bus (Publish-Subscribe)** | Loose coupling; systems don't reference each other directly |
| **Registry Pattern (EntityManager)** | Centralized entity knowledge; enables efficient queries |
| **Grid-Based Collision** | Simplifies geometry; enables fast checks |
| **BFS Pathfinding** | Guaranteed optimal paths for enemies |
| **State Machine (Luna)** | Explicit behavior transitions; predictable AI |
| **Delta-Time Normalization** | Frame-rate independent gameplay |
| **CSV Level Design** | Non-programmers can create levels |
| **Singleton Caching (AssetManager)** | Prevents duplicate image loading; memory efficient |
| **Deferred Rendering** | Layers UI after game world |
| **Enum Types** | Type-safe variant behavior (Bat/Cobra/Centipede) |

---

## Statistics

| Metric | Count |
|--------|-------|
| **Total Classes** | 23 |
| **Total Methods** | ~150+ |
| **Total Instance Variables** | ~100+ |
| **Design Patterns Used** | 10+ (Singleton, Factory, Registry, State Machine, Observer, etc.) |
| **Documented Lines** | ~8000+ |
| **Game Entities** | 7 types |
| **Levels** | 3 |
| **Sprite Assets** | ~60 images |
| **Audio Assets** | 8 sounds |

---

## Phase Dependencies

```
Phase 1 (Entry) 
  ↓
Phase 2 (Infrastructure)
  ↓
Phase 3 (Entities)
  ↓
Phase 4 (Systems)
  ↓
Phase 5 (Presentation)
  +
Bonus (Spatial System - Maze)
```

---

## Usage Guide for Presentation

### Understanding the Game
1. Start with **Phase 1** to understand entry and orchestration
2. Read **Phase 2** to understand core data structures (collision, events)
3. Study **Phase 3** to understand game mechanics (entities, resources)
4. Review **Phase 4** to understand systems and architecture
5. Explore **Phase 5** to understand visual/UI presentation
6. Reference **Maze** for spatial mechanics

### For Different Audiences
- **Game Designers:** Focus on Phase 3 (entities) and Maze (levels)
- **Graphics Programmers:** Focus on Phase 5 (rendering)
- **AI Programmers:** Focus on Maze (pathfinding) and Monster (state machine)
- **Systems Architects:** Focus on Phase 4 (managers and systems)
- **Full Stack:** Read all phases in order

---

## Quick Reference

**Largest Classes (by complexity):**
1. GameStateManager (25+ methods, 15+ timers)
2. GameRenderer (8+ render methods, 60+ sprites)
3. CollisionSystem (4+ collision checks, 10+ state flags)

**Most Critical Classes:**
1. GameStateManager (game engine)
2. Entity/Player/Monster (core gameplay)
3. Maze (spatial foundation)

**Most Elegant Classes:**
1. Hitbox2D (simple, perfect design)
2. CardboardClone (minimal perfection)
3. EventBus (clean pub-sub)

---

This index serves as your navigation guide. Use the file references to dive deeper into specific components during your presentation.
