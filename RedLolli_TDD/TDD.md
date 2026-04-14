# Red Lolli

```
Technical Design Document — Current State & Future Improvements
```

_"Luna, a blonde, white girl, was killed at age 11. They choked her with a rope. Then buried her in Central Park with a shovel. Now her demon roams the maze — and only the Rope, the Mud, and the Shovel can put her back in the ground."_

```
CSE215 Java OOP Lab Project
```

---

## 1. Project Overview

A 2D horror maze game built with JavaFX. The player navigages three levels of a maze, collects items (lollipops) from chests, avoids threats, and reaches escape rooms. The game features image-based sprite rendering, a 4-state monster AI, level-specific guard entities, and atmospheric horror UI screens.

### Core Premise

- **Luna** (blonde girl, killed at 11, choked with rope, buried in Central Park with a shovel)
- Three items to banish her: **Mud**, **Shovel**, and **Rope**
- 2D maze gameplay with three progressively harder levels
- Each level has unique threats and mechanics

---

## 2. Current Architecture (Implemented)

### 2.1 Tech Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 25 |
| UI Framework | JavaFX 21 (Canvas-based rendering) |
| Build Tool | Maven |
| Dependencies | javafx-controls, javafx-fxml, javafx-media, FXGL, ControlsFX, Jackson |
| Testing | JUnit 5 |

### 2.2 Project Structure

```
src/main/java/com/nsu/cse215l/redlolli/redlolli/
├── core/
│   ├── Collidable.java          # Interface for collision detection
│   └── Entity.java              # Abstract base class (x, y, size, render, update)
├── entities/
│   ├── Player.java              # User-controlled character with stamina/sprint system
│   ├── Monster.java             # Pale Luna — 4-state AI (DORMANT/STALKING/HUNTING/WAITING)
│   ├── SerialKillerEntity.java  # Level 3 boss — chase & decoy attack mechanics
│   ├── GuardEntity.java         # Level 1 bats & Level 2 cobras — distraction system
│   ├── CardboardClone.java      # Level 3 decoy item for Serial Killer
│   └── Item.java                # Chests with content types (LOLLI, EMPTY, CLONE_DECOY)
├── map/
│   └── Maze.java                # Tile-based map loaded from CSV, collision, pathfinding (BFS)
├── systems/
│   └── SoundManager.java        # AudioClip-based SFX + looping music, null-safe for missing files
├── ui/
│   ├── GameRenderer.java        # Main game canvas rendering (entities, maze, effects)
│   ├── HUDRenderer.java         # HUD overlay (lollipops, Luna status, level, utility items)
│   └── SceneFactory.java        # UI screen creation (menu, transition, death, victory, item-found)
├── GameStateManager.java        # Central game state coordinator — entities, collisions, update loop
├── HelloApplication.java        # JavaFX entry point, input handling, scene management
└── Launcher.java                # JVM launcher entry point
```

### 2.3 Implemented Game Systems

#### Monster AI (Monster.java) — 4-State Protocol

| State | Behavior |
|-------|----------|
| **DORMANT** | Luna is inactive/partially transparent. Wakes when timer expires or lolli is collected. |
| **STALKING** | Follows player at walking speed. Semi-transparent. Duration: 480 frames → HUNTING. |
| **HUNTING** | Full-speed BFS pursuit (3.2 speed). Instant kill on contact. Duration: 360 frames → DORMANT. |
| **WAITING_AT_DOOR** | Positions outside escape room, waits for player to exit. Duration: 180 frames → DORMANT. |

#### Player Mechanics (Player.java)

- **WASD movement** with wall collision detection
- **Sprint** (Shift) — 1.8× speed with stamina bar (180 frames / 3 seconds)
- **Exhaustion penalty** — 0.6× speed for 120 frames after stamina depletion
- **Expression system** — 2 states: calm / terrified (switches when hunted)

#### Level Threats

| Level | Threat | Mechanic |
|-------|--------|----------|
| 1 (Park) | **Bats** (×2) guard escape rooms. Player has **2 fruits** to distract them. |
| 2 (Basement) | **Cobras** (×3) guard escape rooms. Player has **5 eggs** to distract them. Bite → 5s unconscious. |
| 3 (Grave) | **Serial Killer** — chases player continuously. **Cardboard Clone** item distracts him for 10s. |

#### Item System (Item.java)

- **Chests** loaded from CSV map tiles (tile type 2 = empty chest, 3 = lolli chest)
- Content types: `LOLLI`, `EMPTY`, `CLONE_DECOY`
- Image-based rendering with closed/opened states + content-specific glow overlays

#### Maze System (Maze.java)

- Tile-based map loaded from CSV files (`map.csv`, `map2.csv`, `map3.csv`)
- 3 visual themes (different wall/floor color schemes per level)
- BFS pathfinding for Monster and SerialKiller
- Line-of-sight checks for Luna waiting at door
- Level 3 escape rooms **collapse** after first use

#### Sound System (SoundManager.java)

- One-shot SFX via `AudioClip` with volume control
- Looping background music via `MediaPlayer`
- **Null-safe** — gracefully handles missing `.wav` files
- Sound hooks: `FOOTSTEP`, `CHEST_OPEN`, `HEARTBEAT_FAST`, `STINGER_1`, `LUNA_SCREAM_NEARBY`, `GAME_START`, `GAME_OVER`

#### Image-Based Rendering (All Entities)

All game entities and maze tiles use pre-loaded PNG sprites with `smooth=false` for pixel-art crispness. Missing images fall back to magenta rectangles.

- **~60 sprite images** in `/assets/images/sprites/`
- **~35 UI images** in `/assets/images/ui/`
- Centralized preloading via `initImages()` called from `GameStateManager.loadLevel()`

#### UI Screens (SceneFactory.java)

All UI screens use image-based rendering with `StackPane` + `VBox` + `ImageView` layouts:

- **Main Menu** — background image, title, cycling subtitles, button images
- **Level Transition** — newspaper clipping style with headline and item preview
- **Item Found Screen** — "pale luna smiles wide..." with lore text
- **Death Screen** — "YOU DIED" with death poem composite
- **Victory Screen** — "YOU ESCAPED" with victory poem composite

---

## 3. What's NOT Yet Implemented (From Original Vision)

The following systems were part of the original ambitious design but are **not currently implemented**:

### 3.1 Sanity System
- **Original plan:** 0-100 sanity bar with 5 hallucination tiers (shadow movement, doppelganger, screen flashes, control inversion, breathing walls)
- **Current state:** No sanity mechanic exists. Player has only stamina.

### 3.2 Flashlight System
- **Original plan:** Cone of light with battery drain (180s), flicker near Luna, darkness = danger
- **Current state:** Full maze visibility. No flashlight, no battery, no darkness rendering.

### 3.3 Fog of War
- **Original plan:** Tiles beyond flashlight range fully black, dim edges
- **Current state:** Entire maze is visible at all times.

### 3.4 Advanced Event System
- **Original plan:** Scripted per-level horror events (lights flickering, changing child's drawings, disappearing rooms)
- **Current state:** Level 3 escape room collapse is the only scripted environmental event.

### 3.5 Easter Egg System
- **Original plan:** Konami code, hidden developer notes, "LUNA" keyword, death count messages, stand-still penalty
- **Current state:** Stand-still penalty exists (Luna teleports near player after 30s). Death count message not implemented.

### 3.6 Advanced Visual Effects
- **Original plan:** Screen shake, VHS static, blood drips, subliminal flashes, vignette darkness, color desaturation
- **Current state:** Warning flash (red screen flash when Luna enters HUNTING state). Lolli reveal animation (concentric expanding circles).

### 3.7 Audio Expansion
- **Original plan:** Heartbeat system, ambient drones, whisper system, stinger sounds
- **Current state:** Sound hooks are defined and working, but actual `.wav` asset files need to be created/placed.

### 3.8 Environmental Storytelling
- **Original plan:** Developer note rooms (tile 8), blood trail tiles (tile 9), graffiti walls (tile 10), crime scene layouts
- **Current state:** Tile types 8-10 exist in `Maze.java` rendering but are not populated in current map CSVs.

### 3.9 Intro/Outro Cutscenes
- **Original plan:** Typewriter text crawl, newspaper headlines, image-based cutscenes, Serial Killer resolution sequence
- **Current state:** Level transition screens exist with newspaper style. Victory/death poem screens exist. Full cutscene sequence not implemented.

### 3.10 How-To-Play as Case Files
- **Original plan:** Detective case file styled instructions with typewriter font, coffee stains, blood smears
- **Current state:** No dedicated How-To-Play screen exists.

---

## 4. Future Improvement Suggestions

These are realistic, incremental improvements that build on the current codebase.

### 4.1 Audio Assets (Highest Priority)
- Create/place `.wav` files for the existing sound hooks (`footstep.wav`, `chest_open.wav`, etc.)
- The `SoundManager` is already structured to handle them — just needs actual files in `/assets/audio/`
- Add ambient drone loop for atmosphere

### 4.2 Sanity-Lite System
Instead of the full 5-tier hallucination system, start with a simplified version:
- Sanity drains passively (1 per 10 seconds) and faster when near Luna
- Below 50: HUD text occasionally scrambles (already has the rendering hooks)
- Below 25: Screen edges darken (vignette overlay — simple `fillRect` with radial gradient)
- Recovery in escape rooms (+1 per second)
- Death at 0 sanity with unique message

### 4.3 Flashlight (Simplified)
- Toggle with F key, no battery drain initially
- Render as a simple radial gradient overlay (bright center → dark edges)
- Later: add battery drain and flicker near Luna

### 4.4 Death Count Messages
- The `deathCount` variable already exists in `HelloApplication`
- Add conditional text on death screen: "You keep coming back. She likes that." (5+ deaths)

### 4.5 Map Content Expansion
- Populate tile types 8 (developer notes), 9 (blood trails), 10 (graffiti) in existing CSV maps
- Add hidden rooms with developer apology messages from the original design

### 4.6 Screen Effects
- Screen shake: offset canvas transform during chase/death (simple `gc.translate()` jitter)
- Vignette: dark radial gradient overlay at screen edges (intensifies with low sanity if added)
- Subliminal flash: 1-frame Luna face draw during low sanity or chase

### 4.7 How-To-Play Screen
- Create a case-file styled screen accessible from main menu
- Explain controls (WASD, Shift, F for distractions, Clone placement)
- Cover level-specific mechanics (fruits, eggs, clone decoy)

### 4.8 Intro Sequence
- Before Level 1, show a brief typewriter text crawl telling Luna's backstory
- Already partially exists as text strings in the original TDD — just needs a simple `SceneFactory` screen

### 4.9 Main Menu Enhancements
- Luna face fade-in behind title after 2 seconds (simple opacity animation on an `ImageView`)
- Subtitle already cycles — add more atmospheric variants

### 4.10 Polish & Juice
- Chest open particle burst (already has lolli reveal — reuse for empty chests)
- Player death animation (screen fills red from edges — simple overlay)
- Luna's blood-red pulse on item found (already has the hook in `GameRenderer`)

---

## 5. Design Decisions & Rationale

### Image-Based Rendering Over Primitives
All game entities and UI screens use pre-loaded PNG images instead of JavaFX primitive drawing. This gives:
- Consistent visual quality (pixel art for sprites, horror typography for UI)
- Easier art iteration (swap PNGs without touching code)
- Graceful degradation (missing images → magenta rectangles, no crashes)

### Canvas Over Scene Graph for Gameplay
The game canvas (`GraphicsContext`) is used for all gameplay rendering. UI screens use JavaFX layout nodes (`VBox`, `StackPane`, `ImageView`). This separation keeps the game loop performant while leveraging JavaFX's layout engine for static screens.

### Null-Safe Asset Loading
All image and audio loading checks for `null` before use. Missing assets are silent no-ops. This allows development to proceed without all assets being in place.

### BFS Pathfinding
Both Monster and SerialKiller use BFS on the tile grid for pursuit. This is deterministic, fair, and easy to debug. The `Maze` class exposes `getNextMove()` for this purpose.

---

## 6. File Inventory

### Sprite Assets (60 files in `/assets/images/sprites/`)

| Category | Files |
|----------|-------|
| Maze tiles | `border_wall_[1-3]`, `inner_wall_[1-3]`, `floor_a_[1-3]`, `floor_b_[1-3]`, `escape_room_green`, `escape_room_red` |
| Items | `chest_closed`, `chest_opened`, `chest_glow_lolli`, `chest_glow_clone` |
| Clone | `clone_decoy` |
| Guards | `guard_bat`, `guard_bat_distracted`, `guard_cobra`, `guard_cobra_distracted` |
| Killer | `killer_inactive`, `killer_chase`, `killer_attack` |
| Player | `player_calm`, `player_terrified` |
| Monster | `monster_dormant`, `monster_stalking`, `monster_hunting`, `monster_waiting`, `monster_aura` |
| HUD icons | `lolli_icon`, `pale_luna_[dormant/stalking/hunting/waiting]_icon` |

### UI Assets (35 files in `/assets/images/ui/`)

| Category | Files |
|----------|-------|
| Menu | `menu_background`, `menu_title`, `menu_subtitle_[1-3]`, `btn_new_game`, `btn_exit` |
| Transitions | `transition_bg_[1-2]`, `transition_header`, `transition_headline_[1-3]`, `transition_[mud/shovel/rope]`, `btn_continue` |
| Item Found | `item_bg`, `item_pale_luna_smiles`, `item_[mud/shovel/rope]_text`, `item_desc_[1-3]`, `btn_[here/use/now]` |
| Death | `death_bg`, `death_you_died`, `death_poem`, `btn_restart`, `btn_main_menu` |
| Victory | `victory_bg`, `victory_escaped`, `victory_poem`, `btn_main_menu` |

### Map Files (3 CSVs)
- `map.csv` — Level 1 (Park)
- `map2.csv` — Level 2 (Basement)
- `map3.csv` — Level 3 (Grave)

### Audio Files
- **None currently placed.** Sound hooks defined in `SoundManager.java` for: `heartbeat_fast.wav`, `stinger_1.wav`, `game_start.wav`, `game_over.wav`, `chest_open.wav`, `footstep.wav`, `luna_scream_nearby.wav`

---

## 7. Tile Type Reference

| ID | Name | Rendering | Currently Used |
|----|------|-----------|----------------|
| 0 | Floor | floor_a / floor_b (alternating) | ✅ |
| 1 | Wall | inner_wall (3 themes) | ✅ |
| 2 | Empty chest tile | Converted to Item entity | ✅ (spawns chest) |
| 3 | Lolli chest tile | Converted to Item entity | ✅ (spawns chest) |
| 4 | Outer border | border_wall (3 themes) | ✅ |
| 5 | Monster spawn | Converted to Monster entity | ✅ |
| 6 | Escape room | escape_room_green / escape_room_red | ✅ |
| 7 | Developer note room | Faint text on floor | ⚠️ (renders, not placed in maps) |
| 8 | Blood trail tile | Dark smears | ⚠️ (renders, not placed in maps) |
| 9 | Graffiti wall | Text fragments | ⚠️ (renders, not placed in maps) |
| 10 | Non-walkable (decorative) | Rendered as wall | ✅ |

---

## 8. Controls Reference

| Key | Action |
|-----|--------|
| **W/A/S/D** | Move up/left/down/right |
| **Shift** | Sprint (consumes stamina) |
| **F** | Use distraction item (fruit/egg) on nearest guard |
| **C** | Place cardboard clone decoy (Level 3 only, requires clone item) |
| **F3** | Toggle debug overlay |
| **Enter** | Confirm/continue on UI screens |
| **Escape** | Return to main menu (from UI screens) |

---

## 9. Notes for Contributors

- **Adding new entities:** Extend `Entity`, implement `Collidable`, add `initImages()` + `loadSprite()` pattern, call from `GameStateManager`.
- **Adding new sounds:** Add constant to `SoundManager`, place `.wav` in `/assets/audio/`, call `playOneShot()`.
- **Adding new tile types:** Add to `Maze` tile switch, update CSV maps, add sprite to `/assets/images/sprites/`.
- **Adding new UI screens:** Add to `SceneFactory`, create images in `/assets/images/ui/`, wire button actions in `HelloApplication`.
- **Image loading:** Game sprites use `loadSprite()` with `smooth=false`. UI images use `tryLoadImage()` with default smoothing. Never call `new Image()` directly in render methods.
