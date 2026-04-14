# 🖼️ Red Lolli — Image Loading Guide

## Folder Structure (The Two Places Images Live)

```
src/main/resources/assets/images/
├── sprites/          ← GAME ENTITIES (player, enemies, chests, UI icons)
│    ├── player_calm.png
│    ├── player_terrified.png
│    ├── monster_dormant.png
│    ├── monster_stalking.png
│    ├── monster_hunting.png
│    ├── monster_waiting.png
│    ├── monster_aura.png
│    ├── guard_bat.png
│    ├── guard_bat_distracted.png
│    ├── guard_cobra.png
│    ├── guard_cobra_distracted.png
│    ├── killer_inactive.png
│    ├── killer_chase.png
│    ├── killer_attack.png
│    ├── clone_decoy.png
│    ├── chest_closed.png
│    ├── chest_open.png
│    ├── chest_glow_lolli.png   ← optional, can be missing
│    ├── chest_glow_clone.png   ← optional, can be missing
│    ├── lolli_icon.png
│    ├── sanity_skull.png
│    ├── sanity_skull_2.png
│    └── luna_flash.png
│
└── dungeon/          ← MAZE TILES (walls, floors, doors — level themes)
     ├── wall/        ← wall variants per level
     │    ├── wall_vines_0.png … wall_vines_6.png   (Level 1 walls)
     │    ├── brick_brown-vines_1.png                (Level 1 inner wall)
     │    ├── brick_brown_0.png … brick_brown_7.png  (Level 2 walls)
     │    └── brick_dark_0.png … brick_dark_6.png    (Level 3 walls)
     ├── floor/       ← floor variants per level
     │    ├── lair_0_new.png … lair_5.png            (Level 1 floors)
     │    ├── pebble_brown_0_new.png … _5_new.png    (Level 2 floors)
     │    └── grey_dirt_0_new.png … grey_dirt_b_2.png (Level 3 floors)
     ├── doors/       ← escape room doors
     │    ├── runed_door.png                         (Level 1-2 closed door)
     │    └── sealed_door.png                        (Level 3 closed door)
     └── gateways/    ← open escape hatches
          └── escape_hatch_up.png
```

---

## Rule #1 — All Sprites render at 40×40 (hitbox stays small for movement)

Every entity image loads and renders at **40×40 pixels** visually, but the **collision hitbox is kept small** so entities can navigate 40px-wide corridors.

| Entity           | Hitbox Size | Visual Render | File Class              | Image folder  |
|------------------|-------------|---------------|-------------------------|---------------|
| Player           | 20×20       | **40×40**     | `Player.java`           | `sprites/`    |
| Pale Luna (boss) | 25×25       | **40×40**     | `Monster.java`          | `sprites/`    |
| Bat Guard        | 28×28       | **40×40**     | `GuardEntity.java`      | `sprites/`    |
| Cobra Guard      | 28×28       | **40×40**     | `GuardEntity.java`      | `sprites/`    |
| Serial Killer    | 24×24       | **40×40**     | `SerialKillerEntity.java` | `sprites/`  |
| Cardboard Clone  | 20×20       | **40×40**     | `CardboardClone.java`   | `sprites/`    |
| Chest (Item)     | 16×16       | **32×32**     | `Item.java`             | `sprites/`    |

> **Why small hitboxes?** The tile size is 40px. If an entity's hitbox also equals 40, it cannot navigate any corridor — it collides with walls on both sides simultaneously. The sprite is drawn **centered** on the hitbox (`x - (renderSize-hitboxSize)/2`) so it visually fills the tile while being able to move freely.

---

## Rule #2 — How to Change an Image

### Changing an entity sprite
1. Put your new PNG in `src/main/resources/assets/images/sprites/`
2. Open the entity's Java file (e.g. `Player.java`)
3. In `initImages()`, change the filename string:
   ```java
   playerCalmImg = loadSprite("your_new_image.png", 40, 40);
   ```
4. **Run `mvnw clean compile`** — this is critical! Without a clean build, Java still runs the old `.class` file.

### Changing a maze tile (wall/floor) for a level
1. Put your new PNG in `src/main/resources/assets/images/dungeon/wall/` or `...floor/`
2. Open `Maze.java` → find `initImages()`
3. Change the filename in the correct theme block (Level 1 = theme 0, Level 2 = theme 1, Level 3 = theme 2):
   ```java
   // === THEME 3 — Level 3 ===
   borderWallImg[2][0] = loadDcssTile(dc + "wall/your_new_wall.png", 40, 40);
   floorAImg[2][0]     = loadDcssTile(dc + "floor/your_new_floor.png", 40, 40);
   ```
4. **Run `mvnw clean compile`** again.

---

## Rule #3 — Why Changes Didn't Show (the Static Cache Bug)

Java caches static fields. When `initImages()` runs once, it sets `imagesInitialized = true` and **never runs again** for that JVM session.

If you change image filenames in code **without recompiling**, the old `.class` file runs — your changes are invisible.

**Always do a `mvnw clean compile` after changing any image filename in code.**

Each Java class now also has a `resetImages()` method — if you ever need to force a reload at runtime (e.g., when loading new level), call it:
```java
Maze.resetImages();
Maze.initImages();
```

---

## Rule #4 — What Happens When an Image File Is Missing

If a file isn't found, `loadSprite()` / `loadDcssTile()` returns `null`.  
In that case, the game draws a **solid magenta square** instead.

If you see magenta boxes in the game → a PNG file is missing or the filename is wrong.

---

## Quick Reference: Which Level Uses Which Images

| Level | Walls (border)         | Walls (inner)          | Floor A             | Floor B               |
|-------|------------------------|------------------------|---------------------|-----------------------|
| 1     | `wall_vines_0-3.png`   | `wall_vines_4-6.png` + `brick_brown-vines_1.png` | `lair_0_new` – `lair_2_new` | `lair_3_new`, `lair_4`, `lair_5` |
| 2     | `brick_brown_4-7.png`  | `brick_brown_0-3.png`  | `pebble_brown_0-2_new` | `pebble_brown_3-5_new` |
| 3     | `brick_dark_3-6.png`   | `brick_dark_0-3.png`   | `grey_dirt_0-2_new` | `grey_dirt_b_0` – `grey_dirt_b_2` |
