# RedLolli All-In Graphics Enhancement Plan

## Status: Phases A + B + C + D COMPLETE ✅

---

## Completed

### Phase A: Maze Tile Variety ✅
- Each theme now uses 4 wall variants + 3 floor A variants + 3 floor B variants
- Deterministic selection via `variantIndex(row, col, maxVariants)` using `(row*31 + col*17) % N`
- Dungeon Crawl Stone Soup tiles loaded from `/assets/images/dungeon/` with fallback to `/assets/images/sprites/`
- Escape rooms use `runed_door.png` (levels 1-2) and `sealed_door.png` (level 3) for closed state
- Open escape rooms show floor tile + `escape_hatch_up.png` overlay (looks like an opened portal, not a door)

### Phase B: Atmospheric Overlays ✅
- Second rendering pass after base tiles
- **14 overlay types**: blood splats (30 variants), wall blood (76 directional variants), floor vines (4), cracks/puddles (5), floor sigils (10), wall torches (5), water puddles (2), fountains (7), mold (4), moss (4), statues (7), grates/golden statues (3), boulders (1), banners (1), slime (8 directional)
- All loaded once into static caches at `initImages()`
- Deterministic placement via position hash `(row*131 + col*29 + levelTheme*7 + row*col) % 1000`
- Alpha compositing with proper reset (`setGlobalAlpha` → `1.0`)
- Frequency tuned per overlay type to be sparse and atmospheric (0.5%–3% for floor, 1.5%–2.5% for walls)

### Phase C: Corner Statues ✅
- L-shaped interior wall corners detected (2 orthogonal wall neighbors, not opposite)
- Floor tile rendered underneath statue (no blank canvas reveal)
- ~10% of qualifying corners get a statue (deterministic hash)
- Same frequency and density across ALL themes (theme-independent detection)
- 7 statue variants: ancient hero, dragon, angel, wraith, imp, crumbled column × 2

### Phase D: Chest Sprites ✅
- Closed chest: `chest.png` (theme 1), `chest_2_closed.png` (themes 2-3)
- Open chest: `chest_2_open.png` (all themes)
- Accessor methods: `getClosedChestImage()`, `getOpenChestImage()` — theme-aware

### Phase E: Cutscene Path Updates ✅
- Intro images in `/assets/images/cutscenes/intro/`
- Victory images in `/assets/images/cutscenes/victory/`
- `HelloApplication.java` paths updated

### Phase F: PlaceholderGenerator Update ✅
- Rewritten with new directory structure
- Generates cutscene placeholders

---

## Directory Structure (Final)
```
assets/images/
  sprites/                    ← Custom game entity sprites (38 files)
  ui/                         ← UI text & button images (36 files)
  cutscenes/
    intro/                    ← intro_1.jpeg .. intro_6.jpeg
    victory/                  ← victory_1.png .. victory_5.png
  dungeon/                    ← Dungeon Crawl: walls, floors, doors, altars, statues, etc.
  effect/                     ← Clouds, projectiles, magic effects
  item/                       ← Weapons, armor, potions, runes, gold
  misc/                       ← Blood splats (128 files), travel paths, damage meters
  monster/                    ← Monster sprites (1,282 files)
  player/                     ← Player equipment (975 files)
  gui/                        ← UI icons (500 files)
  emissaries/                 ← God emissary effects (12 files)
```

---

## Tile-to-Asset Mapping

### Theme 1 — Forest/Natural (Level 1)
| Tile | Variants | DCSS Sources |
|------|----------|-------------|
| Border Wall | 4 | `dungeon/wall/wall_vines_0.png` through `wall_vines_3.png` |
| Inner Wall | 4 | `dungeon/wall/wall_vines_4.png` through `wall_vines_6.png` + `brick_brown-vines_1.png` |
| Floor A | 3 | `dungeon/floor/lair_0_new.png`, `lair_1_new.png`, `lair_2_new.png` |
| Floor B | 3 | `dungeon/floor/lair_3_new.png`, `lair_4.png`, `lair_5.png` |

### Theme 2 — Dungeon/Brown (Level 2)
| Tile | Variants | DCSS Sources |
|------|----------|-------------|
| Border Wall | 4 | `dungeon/wall/brick_brown_4.png` through `brick_brown_7.png` |
| Inner Wall | 4 | `dungeon/wall/brick_brown_0.png` through `brick_brown_3.png` |
| Floor A | 3 | `dungeon/floor/pebble_brown_0_new.png` through `pebble_brown_2_new.png` |
| Floor B | 3 | `dungeon/floor/pebble_brown_3_new.png` through `pebble_brown_5_new.png` |

### Theme 3 — Dark/Crypt (Level 3)
| Tile | Variants | DCSS Sources |
|------|----------|-------------|
| Border Wall | 4 | `dungeon/wall/brick_dark_3.png` through `brick_dark_6.png` |
| Inner Wall | 4 | `dungeon/wall/brick_dark_0.png` through `brick_dark_3.png` |
| Floor A | 3 | `dungeon/floor/grey_dirt_0_new.png` through `grey_dirt_2_new.png` |
| Floor B | 3 | `dungeon/floor/grey_dirt_b_0.png` through `grey_dirt_b_2.png` |

### Escape Rooms
| Level | Closed | Open (entered) |
|-------|--------|----------------|
| 1-2 | `dungeon/doors/runed_door.png` | Floor tile + `dungeon/gateways/escape_hatch_up.png` |
| 3 | `dungeon/doors/sealed_door.png` | Floor tile + `dungeon/gateways/escape_hatch_up.png` |

### Chest Sprites
| Theme | Closed | Open |
|-------|--------|------|
| 1 | `dungeon/chest.png` | `dungeon/chest_2_open.png` |
| 2-3 | `dungeon/chest_2_closed.png` | `dungeon/chest_2_open.png` |

### Atmospheric Overlays
| Overlay | Source | Count | Alpha | Frequency | Target |
|---------|--------|-------|-------|-----------|--------|
| Blood splat | `misc/blood/blood_red_*.png` | 30 | 0.20-0.30 | ~2.5% | Floor tiles |
| Wall blood | `misc/blood/wall_blood_*_{east,north,south,west}.png` | 76 | 0.25-0.40 | ~1.5% | Wall tiles |
| Floor vines | `dungeon/floor/floor_vines_*_new.png` | 4 | 0.50 | ~0.5% (theme 1) | Floor tiles |
| Cracks/puddles | `misc/blood/blood_puddle_red*.png` | 5 | 0.25 | ~0.5% | Floor tiles |
| Floor sigils | `dungeon/floor/sigils/{circle,cross,rhombus,algiz,curve_*,straight_*}.png` | 10 | 0.20 | 2-3 near escape rooms | Floor tiles |
| Wall torches | `dungeon/wall/torches/torch_*.png` | 5 | 1.00 | ~2.5% of walls | Wall tiles (+8px offset) |
| Water puddles | `dungeon/water/shallow_water*.png` | 2 | 0.30 | ~0.5% | Floor tiles |
| Moss | `dungeon/floor/moss_*.png` | 4 | 0.40 | ~0.5% | Floor tiles |
| Slime overlay | `dungeon/floor/slime_overlay_*.png` | 8 | 0.30 | ~0.5% (themes 2-3) | Floor tiles |
| Fountains | `dungeon/{blue_fountain,blood_fountain,sparkling_fountain,dry_fountain}*.png` | 7 | 0.90 | 1-3 near escape rooms | Floor tiles |
| Mold | `dungeon/mold_large_*.png` | 4 | 0.70 | 1-5 per level (hash-gated) | Floor tiles (+8px offset) |
| Statues | `dungeon/statues/{statue_ancient_hero,statue_dragon,statue_angel,statue_wraith,statue_imp,crumbled_column,crumbled_column_1}.png` | 7 | 1.00 | ~10% of L-corners | Corner walls (+8px offset) |
| Grates/golden | `dungeon/vaults/{grate,golden_statue_1,golden_statue_2}.png` | 3 | 0.80 | 1-3 per level (hash-gated) | Floor tiles |
| Boulders | `dungeon/boulder.png` | 1 | 1.00 | 1-4 per level (hash-gated) | Floor tiles (+8px offset) |
| Banners | `dungeon/wall/banners/banner_1.png` | 1 | 0.90 | 1-4 on border walls (hash-gated) | Border wall tiles |

---

## Code Changes Summary

| File | Lines Changed | What Changed |
|------|--------------|-------------|
| `Maze.java` | +350 / -80 | 2D variant arrays, DCSS tile loading, 14 overlay types, `renderAtmosphericOverlays()`, deterministic variant selection, corner statues with floor underneath, escape room open/closed states, chest sprite support, banner/slime/shadow overlays |

---

## Overlay Rendering Order (Pass 2)
1. Floor overlays: blood splats → vines → water → moss → cracks → slime
2. Floor decorations: mold → grates → boulders
3. Escape room proximity: sigils → fountains
4. Wall overlays: wall blood → torches → banners
5. Corner statues (on top of floor tile)

---

## Next Steps (Optional Future Enhancements)

1. **Entity footstep effects** — `effect/cloud_*` or `misc/blood/blood_red_*.png` under entities when walking
2. **Monster aura effects** — `effect/cloud_spectral_*.png`, `effect/cloud_neg_*.png` behind Luna in HUNTING state
3. **Serial killer trail** — `effect/drain_red_*.png` at feet when active
4. **Dynamic lighting** — torch overlays with flickering alpha for atmosphere
5. **Wall shadow overlays** — directional shadow overlays on wall edges for depth
