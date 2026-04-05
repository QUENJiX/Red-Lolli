# RedLolli Development Process (3-Week Schedule)

This document outlines the detailed 3-week development journey of **RedLolli**, a 2D game built with Java and FXGL. The process is broken down into structured phases, weeks, and days, showing exactly which files were created and worked on in chronological order, along with the precise line ranges spanning the workload.

---

## Phase 1: Foundation & Core Mechanics (Week 1)
**Goal:** Establish the project structure, basic rendering, map generation, and player controller.

* **Day 1: Project Initialization & Setup**
  * Set up the Maven project and FXGL dependencies.
  * **Files Worked On:** 
    * `pom.xml` (Lines 1-129) - Dependencies & Build config
    * `module-info.java` (Lines 1-22) - Java module system setup
    * `Launcher.java` (Lines 1-20) - Standard JVM entry point bridging to FXGL
    * `HelloApplication.java` (Lines 1-150) - FXGL GameApplication configuration setup

* **Day 2: Core Architecture & Interfaces**
  * Define the base architecture for all game objects and physics interfaces.
  * **Files Worked On:**
    * `core/Collidable.java` (Lines 1-23) - Interface defining collision bounds
    * `entities/Entity.java` (Lines 1-73) - Abstract base class for all game entities

* **Day 3: Map Parsing & Loading**
  * Implement reading from CSV files to generate the game grid/walls.
  * **Files Worked On:**
    * `resources/map.csv` (Lines 1-17) - Designing the initial layout
    * `map/Maze.java` (Lines 1-250) - Parsing the CSV and generating wall/floor tiles

* **Day 4: The Player Character**
  * Create the player entity, assigning attributes (health, speed, bounding boxes).
  * **Files Worked On:**
    * `entities/Player.java` (Lines 1-150) - Initial player spawning and physical attributes

* **Day 5: Rendering Pipeline Foundation**
  * Separate visual rendering logic from game logic.
  * **Files Worked On:**
    * `ui/GameRenderer.java` (Lines 1-262) - Drawing the maze and translating world coordinates

* **Day 6: Input Handling & Movement**
  * Hook up FXGL input handlers (UserActions) to the player character. Handle held keys (e.g. sprinting).
  * **Files Worked On:**
    * `HelloApplication.java` (Lines 151-300) - Registering key bindings
    * `entities/Player.java` (Lines 151-280) - Adding velocity and move functions

* **Day 7: Phase 1 Review & Bug Fixing**
  * Test map loading, player movement, and boundary collisions. Ensure the player cannot walk through walls.
  * **Files Worked On:**
    * `map/Maze.java` (Lines 251-400) - Refining wall collision checks
    * `entities/Player.java` (Lines 281-313) - Tuning movement speed

---

## Phase 2: Entities, Systems, & AI (Week 2)
**Goal:** Populate the world with enemies, items, and specialized gameplay mechanics like lighting.

* **Day 8: Basic AI & Enemies**
  * Implement the baseline enemy type that patrols or chases the player.
  * **Files Worked On:**
    * `entities/Monster.java` (Lines 1-366) - Base enemy behavior, pathfinding setup

* **Day 9: Advanced Enemies - Guards**
  * Create a specialized guard enemy with line-of-sight and specific patrol routes.
  * **Files Worked On:**
    * `entities/GuardEntity.java` (Lines 1-136) - Vision cone and alert state algorithms

* **Day 10: Advanced Enemies - The Stalker**
  * Implement a relentless pursuing enemy that requires stealth to evade.
  * **Files Worked On:**
    * `entities/SerialKillerEntity.java` (Lines 1-137) - Aggressive pathing and kill-states

* **Day 11: Collectibles & Interactables**
  * Introduce items the player can pick up to aid their escape.
  * **Files Worked On:**
    * `entities/Item.java` (Lines 1-178) - Generic pickup logic
    * `entities/CardboardClone.java` (Lines 1-64) - A specific decoy item mechanics

* **Day 12: The Flashlight Mechanic**
  * Create a dynamic lighting system forcing the player to manage visibility vs. stealth.
  * **Files Worked On:**
    * `systems/FlashlightSystem.java` (Lines 1-120) - Cone generation, battery drain logic, rendering integration
    * `HelloApplication.java` (Lines 301-450) - Tie the flashlight state to global rendering logic

* **Day 13: Expanded Level Design**
  * Design more complex levels now that mechanics are in place.
  * **Files Worked On:**
    * `resources/map2.csv` (Lines 1-17) - Medium difficulty maze
    * `resources/map3.csv` (Lines 1-17) - Hard difficulty maze
    * `map/Maze.java` (Lines 401-500) - Support for varied sized mazes

* **Day 14: Complex Collision Management**
  * Wire up the FXGL collision handlers between Player, Monsters, and Items.
  * **Files Worked On:**
    * `HelloApplication.java` (Lines 451-650) - Adding standard collision handler callbacks
    * `map/Maze.java` (Lines 501-584) - Hitbox modifications for the walls/mazes

---

## Phase 3: UI, Audio, & Polish (Week 3)
**Goal:** Tie the game together with a HUD, sound effects, level progression, and final bug fixes.

* **Day 15: Heads-Up Display (HUD)**
  * Give the player visual feedback on their health, inventory, and flashlight battery.
  * **Files Worked On:**
    * `ui/HUDRenderer.java` (Lines 1-384) - Overlaying text/shapes on the camera UI layer
    * `HelloApplication.java` (Lines 651-750) - Attaching the HUD to the game scene

* **Day 16: Audio System & SFX**
  * Add immersive background music and situational sound effects (footsteps, jumpscares).
  * **Files Worked On:**
    * `systems/SoundManager.java` (Lines 1-93) - Wrappers for FXGL audio service
    * `resources/assets/images/sounds.md` (Lines 1-10) - Documenting/planning audio assets

* **Day 17: Game Loop & Game Over States**
  * Handle what happens when the player is caught by a monster or wins a level.
  * **Files Worked On:**
    * `HelloApplication.java` (Lines 751-850) - Win/Loss condition checks, resetting the game state

* **Day 18: Level Transitions**
  * Allow progression from map 1 to map 2, and so on.
  * **Files Worked On:**
    * `HelloApplication.java` (Lines 851-950) - Level progression logic and dynamic map reloading calls

* **Day 19: Balancing & AI Tweaking**
  * Adjust enemy speeds, flashlight battery life, and monster vision ranges to make the game fair but challenging.
  * **Files Worked On:**
    * `entities/Monster.java` (Lines 150-250) - Pathfinding speed tuning
    * `systems/FlashlightSystem.java` (Lines 40-90) - Battery drain tuning

* **Day 20: Comprehensive Testing & Bug Fixing**
  * Edge cases (e.g., getting stuck in corners, items spawning in walls).
  * **Files Worked On:** 
    * `HelloApplication.java` (Lines 951-1000) - State management bug fixes

* **Day 21: Final Polish, Packaging & Release**
  * Final code review, removing unused imports, and compiling the executable JAR.
  * **Files Worked On:**
    * `HelloApplication.java` (Lines 1001-1043) - Final settings tweaks and code cleanup
