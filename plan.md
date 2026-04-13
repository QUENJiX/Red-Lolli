# RedLolli Image-Based Rendering Refactoring Plan

## Overview
Replace **all** JavaFX primitive rendering (`gc.fillRect`, `gc.fillOval`, `gc.strokeLine`, etc.) with pre-loaded image sprites across the entire codebase. Additionally, convert all UI screen text/titles/buttons into image-based rendering. No game logic, hitboxes, or update cycles will change.

---

## Image Asset Requirements

All images must be placed in `src/main/resources/assets/images/sprites/` (game entities) or `src/main/resources/assets/images/ui/` (UI screens).

### A. Game Sprite Assets (`/assets/images/sprites/`)

| File Name | Size | Purpose |
|-----------|------|---------|
| **Maze Tiles** | | |
| `border_wall_1.png` | 40x40 | Outer border wall — Theme 1 (dark green) |
| `border_wall_2.png` | 40x40 | Outer border wall — Theme 2 (brown) |
| `border_wall_3.png` | 40x40 | Outer border wall — Theme 3 (gray) |
| `inner_wall_1.png` | 40x40 | Inner wall — Theme 1 |
| `inner_wall_2.png` | 40x40 | Inner wall — Theme 2 |
| `inner_wall_3.png` | 40x40 | Inner wall — Theme 3 |
| `floor_a_1.png` | 40x40 | Floor tile A — Theme 1 |
| `floor_a_2.png` | 40x40 | Floor tile A — Theme 2 |
| `floor_a_3.png` | 40x40 | Floor tile A — Theme 3 |
| `floor_b_1.png` | 40x40 | Floor tile B — Theme 1 |
| `floor_b_2.png` | 40x40 | Floor tile B — Theme 2 |
| `floor_b_3.png` | 40x40 | Floor tile B — Theme 3 |
| `escape_room_green.png` | 40x40 | Escape/safe room — green variant (lvl 1-2) |
| `escape_room_red.png` | 40x40 | Escape/safe room — red variant (lvl 3) |
| **Items** | | |
| `chest_closed.png` | 16x16 | Closed treasure chest |
| `chest_opened.png` | 16x16 | Opened treasure chest |
| `chest_glow_lolli.png` | 16x16 | Red glow overlay for opened chest with lolli |
| `chest_glow_clone.png` | 16x16 | Tan glow overlay for opened chest with clone |
| **Cardboard Clone** | | |
| `clone_decoy.png` | 20x20 | Cardboard box decoy |
| **Guard Entities** | | |
| `guard_bat.png` | 28x28 | Bat guard (normal) |
| `guard_bat_distracted.png` | 28x28 | Bat guard with green glow overlay |
| `guard_cobra.png` | 28x28 | Cobra guard (normal) |
| `guard_cobra_distracted.png` | 28x28 | Cobra guard with yellow glow overlay |
| **Serial Killer** | | |
| `killer_inactive.png` | 24x24 | Serial killer inactive/calm (faded, semi-transparent) |
| `killer_chase.png` | 24x24 | Serial killer active & chasing (dark body + pale head + knife) |
| `killer_attack.png` | 24x24 | Serial killer attacking decoy (with red aura) |
| **Player** | | |
| `player_calm.png` | 20x20 | Player with calm face |
| `player_terrified.png` | 20x20 | Player with terrified face (wide eyes, open mouth) |
| **Monster (Pale Luna)** | | |
| `monster_dormant.png` | 25x25 | Dormant — faded skin, closed red-slit eyes, faded hair |
| `monster_stalking.png` | 25x25 | Stalking — normal skin, subtle smile, pulsing red eyes |
| `monster_hunting.png` | 25x25 | Hunting — open mouth with teeth, bright red wide eyes |
| `monster_waiting.png` | 25x25 | Waiting at door — dark red eyes, closed mouth |
| `monster_aura.png` | 35x35 | Red pulsing aura glow (semi-transparent circular overlay) |
| **HUD** | | |
| `lolli_icon.png` | 8x16 | Small red lollipop icon for HUD |
| `pale_luna_dormant_icon.png` | 16x16 | Small Luna face for HUD — dormant state |
| `pale_luna_stalking_icon.png` | 16x16 | Small Luna face for HUD — stalking state |
| `pale_luna_hunting_icon.png` | 16x16 | Small Luna face for HUD — hunting state |
| `pale_luna_waiting_icon.png` | 16x16 | Small Luna face for HUD — waiting state |

### B. UI Screen Assets (`/assets/images/ui/`)

| File Name | Size | Purpose |
|-----------|------|---------|
| **Main Menu** | | |
| `menu_background.png` | 880x730 | Full-screen semi-transparent background (dark vignette/forest scene, ~50% alpha so title/subtitles remain the focal point) |
| `menu_title.png` | ~400x80 | "ESCAPE PALE LUNA" title text as PNG |
| `menu_subtitle_1.png` | ~350x30 | "Find the cursed items. Survive the demon." |
| `menu_subtitle_2.png` | ~300x30 | "She remembers your last game." |
| `menu_subtitle_3.png` | ~250x30 | "You can only survive." |
| `btn_new_game.png` | ~200x50 | "NEW GAME" button image |
| `btn_exit.png` | ~150x50 | "EXIT" button image |
| **Level Transition** | | |
| `transition_bg_1.png` | 880x730 | Semi-transparent full-screen background for transition 1→2 (e.g., dig site / earth tones, ~50% alpha) |
| `transition_bg_2.png` | 880x730 | Semi-transparent full-screen background for transition 2→3 (e.g., basement / dark stone, ~50% alpha) |
| `transition_header.png` | ~400x50 | "NEWSPAPER CLIPPING" header |
| `transition_headline_1.png` | ~500x40 | "CENTRAL PARK DIG SITE RETURNS TO NEWS" |
| `transition_headline_2.png` | ~450x40 | "BASEMENT SKETCHES FOUND IN COLD CASE" |
| `transition_headline_3.png` | ~350x40 | "ROPE RECOVERED. HUNTER UNSEEN." |
| `transition_mud.png` | ~120x120 | Mud item sprite for transition screen |
| `transition_shovel.png` | ~120x120 | Shovel item sprite for transition screen |
| `transition_rope.png` | ~120x120 | Rope item sprite for transition screen |
| `btn_continue.png` | ~200x50 | "CONTINUE" button image |
| **Item Found Screen** | | |
| `item_bg.png` | 880x730 | Semi-transparent full-screen background for item-found overlay (dark red tint, ~50% alpha) |
| `item_pale_luna_smiles.png` | ~350x40 | "pale luna smiles wide..." |
| `item_mud_text.png` | ~400x60 | "Mud Found" |
| `item_shovel_text.png` | ~500x60 | "Shovel Found" |
| `item_rope_text.png` | ~400x60 | "Rope Found" |
| `item_desc_1.png` | ~600x30 | "The earth was soft that night. Too soft. Like it was waiting for her." |
| `item_desc_2.png` | ~600x30 | "The blade bit into the ground. Each scoop made a sound like breathing." |
| `item_desc_3.png` | ~600x30 | "She did not struggle at the end. Her eyes were wide open. Smiling." |
| `btn_here.png` | ~120x50 | "here." button (mud screen) |
| `btn_use.png` | ~100x50 | "use" button (shovel screen) |
| `btn_now.png` | ~100x50 | "now" button (rope screen) |
| **Death Screen** | | |
| `death_bg.png` | 880x730 | Semi-transparent full-screen background (dark blood-red haze, ~50% alpha) |
| `death_you_died.png` | ~400x80 | "YOU DIED" title |
| `death_poem.png` | ~400x200 | All 6 death poem lines as a single composite image (vertically stacked, centered) |
| `btn_restart.png` | ~300x50 | "RESTART FROM LEVEL 1" button |
| `btn_main_menu.png` | ~200x50 | "MAIN MENU" button (shared) |
| **Victory Screen** | | |
| `victory_bg.png` | 880x730 | Semi-transparent full-screen background (warm dawn/golden haze, ~50% alpha) |
| `victory_escaped.png` | ~450x70 | "YOU ESCAPED" title |
| `victory_poem.png` | ~500x220 | All 7 victory poem lines as a single composite image (vertically stacked, centered) |
| `btn_main_menu.png` | ~200x50 | "MAIN MENU" button (shared with death screen) |

> **Total: ~55 image files.** Each is a small, manageable PNG. Pixel art style for game sprites (~25); dark horror-style typography for UI text images (~30). Semi-transparent backgrounds are shared/fullscreen (~6). Consolidating poem lines into single images saved ~11 individual files.

---

## File-by-File Refactoring Order

### Phase 1: Maze.java

**Current:** 4 render methods using `fillRect`, `strokeLine`, `strokeRect`, `fillOval`, `fillText`.

**Changes:**
1. Add 5 static `Image` arrays, each indexed by theme:
   ```java
   private static Image[] borderWallImg = new Image[3];
   private static Image[] innerWallImg = new Image[3];
   private static Image[] floorAImg = new Image[3];
   private static Image[] floorBImg = new Image[3];
   private static Image[] escapeRoomImg = new Image[2]; // 0=green, 1=red
   private static boolean imagesInitialized = false;
   ```
2. Add `initImages()` — loads all 15 images once.
3. Replace `renderMaze()` → `gc.drawImage(borderWallImg[levelTheme-1], x, y, TILE_SIZE, TILE_SIZE)` etc.
4. `renderFloor()` alternates `floorAImg` / `floorBImg` based on `(row + col) % 2`.
5. `renderEscapeRoom()` picks index 0 (green) or 1 (red).
6. **Remove** all primitive drawing from maze render methods.

---

### Phase 2: Item.java

**Changes:**
1. Add 4 static images: `chestClosedImg`, `chestOpenedImg`, `chestGlowLolli`, `chestGlowClone`.
2. `render()` → simple `if/else` swap between closed/opened chest images.
3. After opened chest, overlay the appropriate glow image based on `contentType`.
4. **Remove** all primitive drawing.

---

### Phase 3: CardboardClone.java

**Changes:**
1. Add `cloneDecoyImg` static image.
2. `render()` → single `gc.drawImage(cloneDecoyImg, x, y, size, size)`.
3. **Remove** all primitive drawing.

---

### Phase 4: GuardEntity.java

**Changes:**
1. Add 4 static images: `batImg`, `batDistractedImg`, `cobraImg`, `cobraDistractedImg`.
2. `render()` → nested `if/else` on `type` × `distracted` → select image.
3. **Remove** `fillOval`, `strokeLine` calls.

---

### Phase 5: SerialKillerEntity.java

**Changes:**
1. Add 3 static images (UPDATED per user request):
   ```java
   private static Image killerInactiveImg;  // NEW: calm/inactive state
   private static Image killerChaseImg;
   private static Image killerAttackImg;
   ```
2. `render()` → 3-way state selection:
   ```java
   Image img;
   if (!active) {
       img = killerInactiveImg;
   } else if (attackingDecoy) {
       img = killerAttackImg;
   } else {
       img = killerChaseImg;
   }
   gc.drawImage(img, x, y, size, size);
   ```
3. **Remove** all `fillRect`, `fillOval` calls.

---

### Phase 6: Player.java

**Changes:**
1. Add 2 static images (simplified — no aura overlays per user request):
   ```java
   private static Image playerCalmImg;
   private static Image playerTerrifiedImg;
   ```
2. `render()` → simple state swap:
   ```java
   Image img = isBeingChased ? playerTerrifiedImg : playerCalmImg;
   gc.drawImage(img, x, y, size, size);
   ```
3. **Remove** `drawAura`, `drawBody`, `drawCalmFace`, `drawTerrifiedFace`.
4. **Note:** Player aura rendering (`drawAura`) is eliminated entirely. If aura visual feedback is desired later, it can be added as a separate overlay image without complicating the current refactor.

---

### Phase 7: Monster.java

**Changes:**
1. Add 5 static images (UPDATED — eyes merged into body sprites):
   ```java
   private static Image monsterDormant;    // includes closed eyes
   private static Image monsterStalking;   // includes pulsing red eyes
   private static Image monsterHunting;    // includes bright red eyes + teeth
   private static Image monsterWaiting;    // includes dark red eyes
   private static Image monsterAura;       // red pulsing aura overlay
   ```
2. `render()` → draw aura (if not dormant), then body composite based on state.
3. `renderEyes()` → **REMOVED entirely**. Eyes are now baked into each body sprite.
4. **Remove** `drawAura`, `drawHead`, `drawHair`, `drawMouth`, `renderEyes`.
5. **Important:** GameRenderer currently calls `paleLuna.renderEyes(gc)` separately. That call must be removed after this phase.

---

### Phase 8: GameRenderer.java

**Changes:**
1. **Lolli reveal remains as primitives** — the concentric expanding circles + `drawRedLolli()` are kept as-is. This is a one-off transient animation; converting it to a sprite sheet would be over-engineering for a single effect.
2. Remove `paleLuna.renderEyes(gc)` call (eyes now part of monster body sprite).
3. Screen clear and warning flash `fillRect` **remain as primitives** (full-screen overlays, not sprites).
4. **Only change:** Remove the `renderEyes` call and the `lolli_reveal.png` asset. Keep `drawRedLolli()` and the concentric `fillOval` logic intact.

---

### Phase 9: HUDRenderer.java

**Changes:**
1. Add 2 static images: `lolliIconImg`, and 4 `paleLunaIconImg[]` array (indexed by Monster.State).
2. `drawLolliStatus()` → replace `fillOval` + `strokeLine` lolli icon with `gc.drawImage(lolliIconImg, ...)`.
3. `drawPaleLunaStatus()` → draw the appropriate `paleLunaIconImg[state]` beside the timer bar (replacing any text label for Luna's state).
4. **Keep as primitives:** background bar, dividers, timer bars, all `fillText` calls (level, "FIND:", "SAFE", utility counters).

> **Design note:** HUD text remains as system fonts (Arial bold) — this is a **conscious decision**. HUD elements need crisp readability at small sizes, and system fonts require zero art assets. If custom typography is desired later, these can be converted to images independently.

---

### Phase 10: HelloApplication.java + SceneFactory.java (UI Screens)

This is a **new phase** covering all UI screen conversions to image-based rendering.

#### 10a: Main Menu (`createMainMenu`)

**Current:** `Text` nodes for title + subtitle, `Button` nodes for new game/exit, VBox layout with black background.

**Changes:**
1. Add to `SceneFactory`:
   ```java
   private static Image menuBackgroundImg;   // semi-transparent, 880x730
   private static Image menuTitleImg;
   private static Image[] menuSubtitleImg = new Image[3];
   ```
2. Replace `VBox` layout with `StackPane` (for layering):
   - Bottom: `ImageView` of `menuBackgroundImg` at 880x730 (semi-transparent, title is the focal point).
   - Middle: `VBox` with `ImageView` nodes for title, subtitle, and button images, centered via `Pos.CENTER`.
3. Buttons use `-fx-background-image` CSS on real `Button` nodes (preserves hover states, keyboard navigation, accessibility). Load button images via `-fx-background-image: url(...)`.
4. Subtitle swaps based on `menuVisits`: `menuSubtitleImg[(menuVisits - 1) % 3]`.

> **Note:** Canvas is **not** used for UI screens. These are static scenes — JavaFX's layout engine (`VBox`, `HBox`, `StackPane`) handles centering and spacing automatically. `ImageView` nodes render images at full quality without manual positioning.

#### 10b: Level Transition (`createLevelTransitionScene`)

**Current:** VBox with Text nodes for "NEWSPAPER CLIPPING" header, headline, and a Button.

**Changes:**
1. Add: `transitionBgImg[2]` (semi-transparent backgrounds: `transition_bg_1.png`, `transition_bg_2.png`), `transitionHeaderImg`, `transitionHeadlineImg[3]`, `transitionItemImg[3]` (mud/shovel/rope).
2. Use `StackPane` with semi-transparent `ImageView` of the appropriate background. Overlay a `VBox` with `ImageView` nodes for header, headline, and item image.
3. Button uses `-fx-background-image` CSS on a real `Button` node (preserves hover states).
4. Level 1→2 uses `transitionBgImg[0]`; level 2→3 uses `transitionBgImg[1]`.

#### 10c: Item Found Screen (`createItemFoundScreen`)

**Current:** VBox with Text nodes for "pale luna smiles wide...", item name, description, and a styled Button.

**Changes:**
1. Add: `itemBgImg` (semi-transparent full-screen background), `itemPaleLunaSmilesImg`, `itemTextImg[3]` (mud/shovel/rope found), `itemDescImg[3]`.
2. Use `StackPane` with semi-transparent `ImageView` of `itemBgImg`. Overlay a `VBox` with `ImageView` nodes for all text content.
3. Button uses `-fx-background-image` CSS on a real `Button` node.

#### 10d: Death Screen (`createDeathScene`)

**Current:** VBox with "YOU DIED" Text, 6 death poem lines, conditional message, and 2 Buttons.

**Changes:**
1. Add: `deathBgImg` (semi-transparent full-screen background), `deathYouDiedImg`, `deathPoemImg` (single composite image with all 6 poem lines stacked vertically).
2. Use `StackPane` with semi-transparent `ImageView` of `deathBgImg`. Overlay a `VBox` with `ImageView` nodes for title and poem.
3. The `activeDeathMessage` (custom death message) and "You keep coming back" (deathCount >= 5) are drawn as separate `Text` nodes below the poem image at hardcoded positions. These are dynamic content that shouldn't be baked into the composite.
4. Buttons use `-fx-background-image` CSS on real `Button` nodes.

#### 10e: Victory Screen (`createVictoryScene`)

**Current:** VBox with "YOU ESCAPED" Text, 7 victory poem lines, and a Button.

**Changes:**
1. Add: `victoryBgImg` (semi-transparent full-screen background), `victoryEscapedImg`, `victoryPoemImg` (single composite image with all 7 poem lines stacked vertically).
2. Use `StackPane` with semi-transparent `ImageView` of `victoryBgImg`. Overlay a `VBox` with `ImageView` nodes for title and poem.
3. Button uses `-fx-background-image` CSS on a real `Button` node.

---

## Image Loading Pattern (Game Entities — Phases 1-9)

Every entity file uses this pattern. **Critical:** We must guard against missing images at load time, not just at draw time.

### The correct `initImages()` pattern (prevents NullPointerException)

```java
public class SomeEntity {
    private static Image someImg;
    private static boolean imagesInitialized = false;

    /** Loads a sprite with pixel-art settings. Returns null if file is missing. */
    private static Image loadSprite(String filename, int width, int height) {
        try {
            java.io.InputStream is = SomeEntity.class.getResourceAsStream(
                "/assets/images/sprites/" + filename);
            if (is != null) {
                return new Image(is, width, height, true, false);
            }
        } catch (Exception ignored) {
            // Falls through — image stays null, magenta fallback renders
        }
        return null;
    }

    public static void initImages() {
        if (imagesInitialized) return;
        someImg = loadSprite("some.png", 32, 32);
        // ... load other images
        imagesInitialized = true;
    }
}
```

**Key rules:**
- `getResourceAsStream()` returns `null` if the file is missing — we check this **before** calling `new Image()`
- `new Image(null)` throws `NullPointerException` — the `if (is != null)` guard prevents this
- `preserveRatio = true` (safe for square sprites)
- `smooth = false` — **critical for pixel art crispness**
- Static init ensures one load per JVM lifetime
- `initImages()` is `public static` so `GameStateManager.loadLevel()` can call it

### Fallback rendering (applies to ALL `drawImage` calls)

Because `loadSprite()` returns `null` for missing files, every `drawImage` must guard against null:

```java
if (img != null) {
    gc.drawImage(img, x, y, size, size);
} else {
    gc.setFill(Color.MAGENTA); // or a thematic fallback color
    gc.fillRect(x, y, size, size);
}
```

This ensures the game **never crashes** due to missing sprites. Entities render as magenta rectangles during development until art assets are placed.

---

## Centralized Preloading

Call all `initImages()` from `GameStateManager.loadLevel()` **before** the first frame renders:

```java
// In GameStateManager.loadLevel(), before creating entities:
Maze.initImages();
Item.initImages();
CardboardClone.initImages();
GuardEntity.initImages();
SerialKillerEntity.initImages();
Player.initImages();
Monster.initImages();
GameRenderer.initImages();
HUDRenderer.initImages();
```

Each `initImages()` is a no-op if already loaded. Missing images silently return `null` — no exceptions, no crashes.

---

## Image Loading Pattern (UI Screens — Phase 10)

UI images load once in `SceneFactory`. **Note:** `tryLoadImage()` already handles null safely — no changes needed.

```java
public class SceneFactory {
    private static Image menuBackgroundImg;
    private static Image menuTitleImg;
    // ... etc
    private static boolean uiImagesInitialized = false;

    static void initUIImages() {
        if (uiImagesInitialized) return;
        // UI text images load WITH smoothing (typography looks better at display resolution)
        menuBackgroundImg = tryLoadImage("/assets/images/ui/menu_background.png");
        menuTitleImg = tryLoadImage("/assets/images/ui/menu_title.png");
        // ... etc
        uiImagesInitialized = true;
    }

    /**
     * Loads an image from the classpath resource path, returning null on failure.
     * Already null-safe: checks URL != null before new Image().
     */
    public static Image tryLoadImage(String path) {
        try {
            URL url = SceneFactory.class.getResource(path);
            if (url != null)
                return new Image(url.toExternalForm());
        } catch (Exception ignored) {
        }
        return null;
    }
}
```

**Key difference from game sprites:**
- `tryLoadImage()` uses `getResource()` + URL check — already null-safe, no NPE possible
- UI text images load with **smoothing enabled** (default). Typography looks better with anti-aliasing at display resolution.
- Game sprites use `loadSprite()` with `smooth = false` for pixel art crispness.
- Semi-transparent backgrounds are loaded as-is — their alpha channel is baked into the PNG. Do **not** apply `gc.setGlobalAlpha()` on top of already-transparent images (double-multiplication makes them too dark).

---

## Alpha Handling Rules

```java
// Monster dormant (partial transparency)
gc.setGlobalAlpha(0.5);
gc.drawImage(monsterDormant, x, y, size, size);
gc.setGlobalAlpha(1.0);

// SerialKiller inactive
gc.setGlobalAlpha(0.35);
gc.drawImage(killerInactiveImg, x, y, size, size);
gc.setGlobalAlpha(1.0);

// Additive glow/aura overlays
gc.setGlobalAlpha(0.15);
gc.drawImage(auraImg, x - 4, y - 4, size + 8, size + 8);
gc.setGlobalAlpha(1.0);
```

**Always reset alpha to 1.0 immediately after the draw call.**

---

## GameRenderer Change: Remove renderEyes Call

After Phase 7 (Monster refactoring), the following line in `GameRenderer.render()` must be removed:

```java
// DELETE THIS:
if (paleLuna != null) {
    paleLuna.renderEyes(gc);
}
```

Eyes are now baked into the monster body sprites, rendered via `paleLuna.render(gc)`.

---

## Files Modified

| # | File | Phase |
|---|------|-------|
| 1 | `map/Maze.java` | 1 |
| 2 | `entities/Item.java` | 2 |
| 3 | `entities/CardboardClone.java` | 3 |
| 4 | `entities/GuardEntity.java` | 4 |
| 5 | `entities/SerialKillerEntity.java` | 5 |
| 6 | `entities/Player.java` | 6 |
| 7 | `entities/Monster.java` | 7 |
| 8 | `ui/GameRenderer.java` | 8 |
| 9 | `ui/HUDRenderer.java` | 9 |
| 10 | `HelloApplication.java` | 10a |
| 11 | `ui/SceneFactory.java` | 10b-10e |

## Files NOT Modified

- `Entity.java` — abstract base class
- `Collidable.java` — interface
- `SoundManager.java` — audio only
- `Launcher.java` — entry point
- `GameStateManager.java` — debug overlay uses primitives (acceptable, not game canvas)

---

## Risks & Mitigations

| Risk | Mitigation |
|------|-----------|
| Images not found at runtime | Use `getResourceAsStream()` with null check; fallback to colored rect if missing |
| Frame stutter on first spawn | Static init in constructor ensures loading before first render |
| Wrong image dimensions | All sprites designed for exact entity sizes; `drawImage(x, y, size, size)` scales if needed |
| Alpha leakage | Always pair `setGlobalAlpha(val)` with `setGlobalAlpha(1.0)` |
| UI button hit detection | Use invisible `Button` overlay nodes on top of canvas-drawn button images |
| 80+ image files to manage | Organize into `/sprites/` and `/ui/` subdirectories; use consistent naming |

---

## Verification Checklist

### Rendering primitives removed
- [ ] Zero `gc.fillRect` in entity/maze rendering (except screen clear, warning flash, HUD background, timer bars)
- [ ] Zero `gc.fillOval` in entity/maze rendering
- [ ] Zero `gc.strokeLine` in entity/maze rendering (except HUD dividers, lolli reveal)
- [ ] Zero `gc.strokeRect` in entity rendering (except HUD timer bars)
- [ ] Zero `gc.fillText` in UI screens (all replaced with `ImageView` nodes)
- [ ] Lolli reveal animation kept as primitives (concentric `fillOval` + `drawRedLolli`)
- [ ] HUD text kept as system fonts (conscious design decision)

### Image loading discipline
- [ ] All game `Image` variables are `private static`
- [ ] All game images loaded with `smooth = false`
- [ ] All UI text images loaded with smoothing enabled (default via `tryLoadImage()`)
- [ ] No `new Image()` in any `render()` or `update()` method
- [ ] All `setGlobalAlpha()` paired with reset to `1.0`
- [ ] No `gc.setGlobalAlpha()` applied on top of already semi-transparent background PNGs

### Entity-specific checks
- [ ] `renderEyes()` call removed from `GameRenderer`
- [ ] Monster eyes merged into body sprites (no separate eye images)
- [ ] SerialKiller has 3 images (inactive, chase, attack)
- [ ] Player has only 2 images (calm, terrified) — no aura images
- [ ] HUD has lolli icon image + Pale Luna state icon images
- [ ] All `drawImage` calls have null-check fallback to colored `fillRect`

### UI screen checks
- [ ] Death poem is 1 composite image (not 6 separate)
- [ ] Victory poem is 1 composite image (not 7 separate)
- [ ] `activeDeathMessage` drawn as separate `Text` node (not baked into composite)
- [ ] Menu background is semi-transparent PNG (not solid black)
- [ ] Transition screens use level-specific semi-transparent backgrounds (1→2, 2→3)
- [ ] Victory and death screens use semi-transparent background images
- [ ] UI screens use `StackPane` + `VBox` + `ImageView` layout (not Canvas)
- [ ] Buttons use `-fx-background-image` CSS (preserves hover states)

### Preloading
- [ ] `GameStateManager.loadLevel()` calls all `initImages()` before creating entities
- [ ] Game compiles without errors

---

## Execution Order

**Phases 1→9 sequentially** (Maze → Item → Clone → Guard → Killer → Player → Monster → GameRenderer → HUDRenderer), then **Phase 10** (UI screens).

Each phase:
1. Add static image variables + `initImages()` method
2. Replace `render()` body with `gc.drawImage()` calls
3. Remove all primitive drawing calls
4. Verify compilation

**No game logic changes. No hitbox changes. No update cycle changes.**

---

## Suggestions & Criticisms

> **Status:** All suggestions below have been incorporated into the plan.

### 1. Semi-transparent backgrounds — PNG alpha vs. `gc.setGlobalAlpha()` **(INCORPORATED)**

UI background PNGs have alpha baked in. Do NOT apply `gc.setGlobalAlpha()` on top. Load as-is via `tryLoadImage()`.

### 2. Consolidated poem images — dynamic content handling **(INCORPORATED)**

Death poem and victory poem are single composite images. The `activeDeathMessage` and "You keep coming back" (deathCount >= 5) remain as `Text` nodes rendered below the poem image.

### 3. Player without aura — future option noted **(INCORPORATED)**

Aura rendering eliminated. If needed later, add 3 overlay images (`player_aura_calm`, `player_aura_chased`, `player_aura_safe`) without restructuring existing code.

### 4. Item Found Screen context **(ACKNOWLEDGED)**

`itemBg.png` is a full-screen background for a new Scene, not a frozen-game overlay. This matches current behavior.

### 5. Button hover states — CSS approach **(INCORPORATED)**

All UI buttons use `-fx-background-image` CSS on real `Button` nodes. This preserves hover effects, keyboard navigation, and accessibility. Only text/title images use `ImageView` nodes.

### 6. Art workload — placeholder strategy **(RECOMMENDATION)**

~55 PNGs is significant. Consider generating colored placeholder rectangles with filename labels first, verify the rendering pipeline, then swap in final art.

### 7. Monster aura overlay — kept separate **(CONFIRMED)**

`monster_aura.png` remains a separate overlay image. It pulses in size via `Math.sin(pulsePhase)` — cannot be baked into the body sprite.

### 8. Lolli reveal animation — kept as primitives **(INCORPORATED)**

The concentric expanding circles + `drawRedLolli()` remain as `fillOval`/`strokeLine` primitives. No `lolli_reveal.png` asset needed.

### 9. UI screens use layout engine, not Canvas **(INCORPORATED)**

All UI screens use `StackPane` → `VBox` → `ImageView` nodes. Canvas is reserved for the game loop only. JavaFX handles centering, spacing, and layout automatically.

### 10. Centralized preloading **(INCORPORATED)**

All `initImages()` calls moved to `GameStateManager.loadLevel()`, executed before entity creation. Prevents frame hitches on level start.

### 11. Fallback rendering pattern **(INCORPORATED)**

Every `drawImage` must null-check with a magenta `fillRect` fallback. Prevents invisible entities during development.

### 12. HUD text — conscious design decision **(INCORPORATED)**

HUD text remains as Arial bold system fonts. Documented as a conscious decision: HUD needs crisp readability, and system fonts require zero assets. Convertible to images later if desired.

### 14. UI vs sprite smoothing difference **(INCORPORATED)**

Game sprites: `smooth = false` (pixel art crispness). UI text images: smoothing enabled (typography quality at display resolution).

### 15. Asset manager — deferred **(ACKNOWLEDGED)**

Per-class `initImages()` kept for now (better encapsulation). Consistent method names enable future refactoring to a central `AssetManager`.
