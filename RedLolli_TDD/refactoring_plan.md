# RedLolli Refactoring & Stability Plan

**Goal:** Fix gameplay bugs, remove unused features, and significantly reduce code volume while preserving all gameplay identity.

**Target:** Reduce ~2,362 code lines to ~1,600-1,800 lines (≈25-30% reduction) through structural improvements and dead-code removal.

---

## Part A: Bug Fixes & Stability (Phases 1–3)

### Phase 1: Safe-Room Detection Foundation
**Files:** `Maze.java`, `HelloApplication.java`

**Problem:** Current escape-room check uses center-point only. Sprinting or edge movement can skip safe-room states, breaking Luna/guard logic downstream.

**Changes:**
1. **`Maze.isEscapeRoom(Rectangle2D)` → `Maze.isEscapeRoomOverlap(Rectangle2D)`**
   - Replace single center-point check with 4-corner + center overlap check.
   - Returns `true` if ANY part of the hitbox overlaps tile type 6.
2. **`HelloApplication.update()` transition logic**
   - Compute `enteringEscapeRoom` and `exitingEscapeRoom` booleans explicitly each frame.
   - Replace ad-hoc `wasInEscapeRoom` flag with these transitions for clarity.

**Dependencies:** None (foundation step).

---

### Phase 2: Pale Luna Door Logic Fix
**Files:** `Monster.java`, `HelloApplication.java`

**Problem:** "At the door" HUD state doesn't match physical Luna position. Exit-from-safe-room death is inconsistent (LOS-dependent).

**Changes:**
1. **`Monster.java` — Add `positionAtDoor(Player, Maze)` method**
   - When entering `WAITING_AT_DOOR` state, Luna teleports to the nearest safe-room doorway tile.
2. **`HelloApplication.java` — Exiting safe room while Luna waits**
   - If `exitingEscapeRoom && paleLuna.isWaitingAtDoor()`: trigger immediate death (no LOS check).
3. **Keep recurring cycles** — Luna resets to dormant after wait timer regardless.

**Dependencies:** Phase 1 (needs reliable transition detection).

---

### Phase 3: Level 2 Guard/Knockout Consistency
**Files:** `GuardEntity.java`, `HelloApplication.java`

**Problem:** Cobra pass gives no visual feedback. Knockout fires while already in safe room (wasteful). Cobra can hit player inside safe room.

**Changes:**
1. **`GuardEntity.java` — Visual pass indicator**
   - When `cobraEntryPasses > 0`, render a subtle green ring/aura around cobra.
2. **`HelloApplication.java` — Knockout guard clause**
   - `if (player.isInEscapeRoom()) return;` before applying cobra knockout.
3. **Egg-use cue** — Brief text flash "Cobra distracted!" when egg consumed on cobra.

**Dependencies:** Phase 1 (relies on `isInEscapeRoom()` being accurate).

---

### Phase 4: Level 3 Serial Killer Detection
**Files:** `SerialKillerEntity.java`, `HelloApplication.java`

**Problem:** Killer uses narrow fixed-map activation tile. Chase coordinate math has offset inconsistencies causing selective pathing.

**Changes:**
1. **Activation by proximity** — Replace fixed tile check with distance-based activation (e.g., killer activates when player is within X tiles).
2. **`SerialKillerEntity.updateChase()` coordinate fix**
   - Audit tile conversion math: ensure `Maze.Y_OFFSET` is applied consistently to both current and target positions.
   - Match the exact conversion used in `Monster.pursuePlayer()` to eliminate drift.

**Dependencies:** Phase 1.

---

## Part B: Feature Removal (Phase 4)

### Phase 5: Remove HUD Heartbeat Icon
**Files:** `HUDRenderer.java`, `GameRenderer.java`, `HelloApplication.java`

**Changes:**
1. **`HUDRenderer.java`**
   - Delete `drawHeartbeat()` method entirely (~40 lines).
   - Remove `drawHeartbeat(gc, lunaNearby, pulsePhase)` call from `drawHUD()`.
   - Remove `lunaNearby` parameter from `drawHUD()` signature.
   - Adjust divider at x=785 or remove it so no orphan divider floats near battery display.
2. **`GameRenderer.java`**
   - Remove `lunaNearby` variable computation and parameter pass to `HUDRenderer.drawHUD()`.
3. **`HelloApplication.java`**
   - Remove any `lunaNearby` boolean tracking if no longer used elsewhere.

**Estimated savings:** ~55 lines removed.

---

### Phase 6: Remove Keyboard Easter-Egg Tracking
**Files:** `HelloApplication.java`

**Changes:**
1. Delete `recentTyped` field and its reset (`recentTyped = ""`).
2. Delete the keypress accumulation block inside `setupGameScene()` (~8 lines).
3. Remove any `recentTyped` checks (e.g., "idkfa" easter egg detection) from `update()`.

**Estimated savings:** ~15 lines removed.

---

## Part C: Code Concision Pass (Phase 5)

### Phase 7: Structural Concision Across All Files

**Goal:** Reduce verbose patterns, extract duplicates, remove dead code, simplify rendering pipelines.

#### 7.1: `HelloApplication.java` (~950 → ~650 lines, ≈300 saved)

| Action | Est. Savings |
|--------|-------------|
| Extract item-spawn logic into `spawnItems()` helper | ~40 lines |
| Extract guard-spawn loop into `spawnGuards()` helper | ~35 lines |
| Collapse repetitive `if (activeKeys.contains(...))` blocks into a single direction builder | ~25 lines |
| Remove redundant null checks where fields are guaranteed initialized | ~15 lines |
| Collapse verbose `update()` switch/if-else chains into smaller delegated methods | ~50 lines |
| Merge `resetGameState()` variable resets into a loop or builder pattern where possible | ~20 lines |
| Remove unused imports, unused `Map<String, GuardEntity>` if redundant | ~10 lines |
| Shorten `createMainMenu()` and `playIntroAndStart()` by extracting UI builders | ~40 lines |
| Simplify collision methods (`checkChestCollisions`, `checkGuardThreats`) | ~40 lines |
| Remove overly verbose javadoc that restates the obvious (keep only API contracts) | ~25 lines |

#### 7.2: `HUDRenderer.java` (~479 → ~320 lines, ≈159 saved)

| Action | Est. Savings |
|--------|-------------|
| Remove verbose javadoc that repeats method names (e.g., "Draws the persistent heavy dark baseline...") | ~60 lines |
| Collapse `drawDormantStatus`, `drawStalkingStatus`, `drawHuntingStatus` — share common bar-drawing logic | ~40 lines |
| Remove redundant comments inside each draw method (the method names already describe what they do) | ~30 lines |
| Extract common progress-bar rendering into single `drawProgressBar()` helper | ~20 lines |
| Simplify color/throb math expressions | ~9 lines |

#### 7.3: `Monster.java` (~418 → ~300 lines, ≈118 saved)

| Action | Est. Savings |
|--------|-------------|
| Remove repetitive javadoc on private helper methods (`drawAura`, `drawHead`, etc.) | ~35 lines |
| Extract common eye-drawing logic (all 4 eye methods share structure) | ~25 lines |
| Collapse `stalkPlayer()` into direct `pursuePlayer()` call (it's a 1-line delegator) | ~5 lines |
| Remove redundant comments inside `update()` switch branches | ~20 lines |
| Simplify `pursuePlayer()` coordinate math by extracting `tileCenter(row, col)` helper | ~15 lines |
| Merge getter methods onto fewer lines where trivially short | ~18 lines |

#### 7.4: `Player.java` (~355 → ~260 lines, ≈95 saved)

| Action | Est. Savings |
|--------|-------------|
| Remove verbose javadoc on obvious getters/setters | ~30 lines |
| Collapse `drawCalmFace()` and `drawTerrifiedFace()` repeated eye-drawing patterns | ~20 lines |
| Simplify `move()` method stamina logic (reduce nested if-else) | ~15 lines |
| Remove inline comments that restate the code | ~20 lines |
| Merge trivial one-line getters onto single lines | ~10 lines |

#### 7.5: `Maze.java` (~652 → ~450 lines, ≈202 saved)

| Action | Est. Savings |
|--------|-------------|
| Remove extremely verbose javadoc on every render method (renderBorderWall, renderInnerWall, etc.) | ~70 lines |
| Collapse rendering methods: shared floor/base logic can be one method with parameters | ~40 lines |
| Remove redundant inline comments inside CSV parsing | ~20 lines |
| Simplify BFS `Node` class — make it compact or use parallel arrays | ~10 lines |
| Remove overly long javadoc on `getNextMove()` (the method name + signature explains it) | ~20 lines |
| Consolidate `hasLineOfSight()` comments | ~15 lines |
| Remove fallback map hardcoded arrays (replace with single helper or exception) | ~15 lines |
| Merge tile-type constant rendering into a switch-based renderer | ~12 lines |

#### 7.6: `GameRenderer.java` (~299 → ~200 lines, ≈99 saved)

| Action | Est. Savings |
|--------|-------------|
| Remove extremely long javadoc on `render()` method (~30 lines of @param descriptions) | ~25 lines |
| Remove verbose javadoc on `renderDarknessMask()` and `renderLolliReveal()` | ~30 lines |
| Inline `drawRedLolli()` comments | ~10 lines |
| Collapse `LolliRevealState` class javadoc | ~10 lines |
| Simplify flashlight cone geometry math (extract helper or comment less) | ~12 lines |
| Remove redundant `gc.save()`/`gc.restore()` comments | ~5 lines |
| Merge trivial constant declarations | ~7 lines |

#### 7.7: `Item.java` (~205 → ~145 lines, ≈60 saved)

| Action | Est. Savings |
|--------|-------------|
| Remove verbose javadoc on rendering methods | ~25 lines |
| Collapse `renderOpenedChest()` and `renderClosedChest()` shared color calls | ~15 lines |
| Remove inline comments inside chest drawing | ~12 lines |
| Merge short getters | ~8 lines |

#### 7.8: `GuardEntity.java` (~155 → ~110 lines, ≈45 saved)

| Action | Est. Savings |
|--------|-------------|
| Remove javadoc on simple methods | ~15 lines |
| Collapse render method comments | ~12 lines |
| Simplify `isDistracted()` logic | ~5 lines |
| Remove inline comments | ~13 lines |

#### 7.9: `SerialKillerEntity.java` (~159 → ~115 lines, ≈44 saved)

| Action | Est. Savings |
|--------|-------------|
| Remove javadoc on obvious methods | ~15 lines |
| Simplify `updateChase()` coordinate math | ~10 lines |
| Remove inline comments | ~12 lines |
| Merge short getters/setters | ~7 lines |

#### 7.10: `FlashlightSystem.java` (~136 → ~95 lines, ≈41 saved)

| Action | Est. Savings |
|--------|-------------|
| Remove verbose javadoc | ~15 lines |
| Collapse `update()` comments | ~10 lines |
| Merge getter methods | ~8 lines |
| Remove inline comments | ~8 lines |

#### 7.11: `SoundManager.java` (~103 → ~75 lines, ≈28 saved)

| Action | Est. Savings |
|--------|-------------|
| Remove verbose javadoc | ~12 lines |
| Collapse `playOneShot()` comments | ~8 lines |
| Remove inline "Silent Failure Model" comment repetition | ~5 lines |
| Simplify structure | ~3 lines |

#### 7.12: `CardboardClone.java` (~73 → ~50 lines, ≈23 saved)

| Action | Est. Savings |
|--------|-------------|
| Remove javadoc on simple overrides | ~10 lines |
| Remove inline comments | ~8 lines |
| Compact structure | ~5 lines |

#### 7.13: `Entity.java` (~85 → ~55 lines, ≈30 saved)

| Action | Est. Savings |
|--------|-------------|
| Remove verbose javadoc on abstract methods | ~15 lines |
| Remove inline comments | ~8 lines |
| Compact getters/setters | ~7 lines |

#### 7.14: `Collidable.java` (~26 → ~15 lines, ≈11 saved)

| Action | Est. Savings |
|--------|-------------|
| Shorten javadoc | ~8 lines |
| Compact structure | ~3 lines |

#### 7.15: `Launcher.java` (~22 → ~12 lines, ≈10 saved)

| Action | Est. Savings |
|--------|-------------|
| Remove verbose javadoc | ~8 lines |
| Compact | ~2 lines |

---

## Summary

| Phase | Focus | Est. Lines Removed | Est. Lines Added | Net Change |
|-------|-------|-------------------|-----------------|------------|
| 1 | Safe-room detection | — | ~20 | +20 |
| 2 | Luna door logic | ~10 | ~30 | +20 |
| 3 | Guard/knockout fix | ~5 | ~25 | +20 |
| 4 | Serial killer fix | ~10 | ~30 | +20 |
| 5 | Remove heartbeat HUD | ~55 | — | **-55** |
| 6 | Remove easter-egg | ~15 | — | **-15** |
| 7 | Code concision | ~860 | ~100 | **-760** |
| **Total** | | **~955** | **~205** | **~-750** |

**Starting code lines:** ~2,362  
**Target code lines:** ~1,600  
**Reduction:** ~32%

---

## Execution Order

```
Phase 1 → Phase 2 → Phase 3 → Phase 4 → Phase 5 → Phase 6 → Phase 7
  (Foundation)  (Bug Fixes)    (Bug Fixes)  (Removal)   (Removal)  (Concision)
```

Each phase should be committed separately. Build and test after each phase.
