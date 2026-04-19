# Project Report

## 1. Cover Page

*   **Project Title:** Red Lolli (Escape Pale Luna)
*   **Course Name:** CSE215L
*   **Section:** 11
*   **Instructor's Name:** Arfana Rahman
*   **Group Members:**
    *   Hasibul Islam - 2521575042
    *   [TBD] - [TBD]
    *   [TBD] - [TBD]
*   **Submission Date:** [TBD]

---

## 2. Abstract

**Red Lolli (Escape Pale Luna)** is a 2D grid-based survival dungeon crawler built entirely from scratch using Java and JavaFX. The core objective of the game is to navigate through creepy, procedurally loaded mazes, collect all the hidden "Lollis" (or chests), and avoid the relentless entity known as Pale Luna. To survive, players must manage a dynamic "Sanity" meter that depletes in the dark, utilizing items like cardboard clones as decoys. Our project demonstrates how core Object-Oriented Programming (OOP) principles can be structurally applied to create a custom game loop, collision physics, and rendering pipeline without relying on bloated third-party game engines.

---

## 3. Introduction

### Objective of the Project
The main objective of this project is to apply theoretical Object-Oriented Programming concepts to a chaotic, real-time environment. By building a game from the ground up, we aimed to learn how to manage complex component systems, handle user inputs synchronously, and translate abstract math into real-time graphics.

### Background and Significance
Game development is one of the most intense stress tests for software design. Handling 60 frames per second means our data structures, memory management, and class relationships need to be highly optimized. We initially explored heavy engines like FXGL but pivoted to a pure JavaFX Canvas implementation to have complete ownership over the code. This project serves as a comprehensive showcase of inheritance, polymorphism, design patterns, and sheer willpower.

---

## 4. Features and Implementation

*   **Dynamic Sanity & Lighting System:** If the player is away from light sources for too long, their sanity drops. We implemented a mathematical vignette effect that slowly closes a black radial gradient over the screen as the player goes crazy.
*   **Custom 2D Collision Detection:** We built a dedicated `CollisionSystem` that checks bounding boxes predicting where entities will be in the next frame, preventing players from walking through walls.
*   **Procedural Level Management:** The game reads map layouts from external `.csv` files using a `LevelManager` to spawn tiles, walls, and items dynamically.
*   **Event-Driven Game Loop:** The core `HelloApplication` class utilizes a JavaFX `AnimationTimer` to run an infinite loop that calculates delta time, processes physics arrays, and tells the `GameRenderer` to draw the current frame.
*   **AI Pathfinding:** Instead of moving randomly, enemies like Pale Luna continuously track the player's position, requiring the player to use strategy (like placing `CardboardClone` decoys) to break line-of-sight.

---

## 5. Implementation of OOP Concepts

### Encapsulation
All attributes within our game entities are strictly set to `private`. External classes cannot arbitrarily change a player's health or coordinates; they must use explicit setter methods like `player.setSanity()`. This prevents unexpected bugs where physics systems accidentally delete data.

### Inheritance
We avoided duplicating code by creating parent classes. For example, both the Player and Pale Luna extend a base `Entity` or `GameObject` class that contains standard data like `x`, `y`, `width`, and `height`. 

### Polymorphism
We utilize polymorphism heavily in our `update()` methods. When the `GameStateManager` iterates through a list of all entities on screen, it just calls `.update()` on them. A `Monster` updates by hunting the player, while an `Item` might update by playing a floating animation. 
```java
// Example: The game loop doesn't need to know exactly what the entity is, 
// it just knows they all share the update() behavior!
for (Entity entity : entityManager.getEntities()) {
    entity.update(activeKeys);
}
```

### Abstraction
Complex logic is hidden behind simple interfaces. For instance, the `GameRenderer` class abstracts away the terrifying math of drawing nested images and applying alpha layers. 

---

## 6. Member Contribution

| Feature | Implemented By (Name & ID) | Description |
| :--- | :--- | :--- |
| **Feature 1** | Hasibul Islam (2521575042) | **(Hard)** Designed the core engine architecture, `AnimationTimer` game loop, the `GameStateManager`, and the custom 2D bounding-box `CollisionSystem`. |
| **Feature 2** | [TBD] ([TBD]) | **(Medium)** Implemented Entity logic: Player movement, Pale Luna's hunting AI, line-of-sight detection, and the Sanity depletion calculations. |
| **Feature 3** | [TBD] ([TBD]) | **(Easy)** Handled the UI/UX pipeline: Designed the `SceneFactory` for menus, hooked up the `SoundManager` for spooky music, and created the intro/victory cutscenes. |

---

## 7. Testing and Results

*   **Test Cases (JUnit 5):** 
    *   *Input:* Start a new game and trigger `gsm.resetGameState()`. 
    *   *Expected Output:* Entities clear out, player coordinates reset to spawn, and sanity restores to 100.
    *   *Result:* Passed. State isolated and cleared correctly.
*   **Collision Testing:**
    *   *Input:* Player holds the 'W' key while adjacent to a wall block.
    *   *Expected Output:* The `CollisionSystem` intercepts the movement vector and zeroes out the 'Y' velocity.
    *   *Result:* Passed. Player slides cleanly along the wall without clipping through.
*   **Screenshots:** *(Please insert pictures of the Main Menu, The Gameplay Canvas, and the Death Screen here before submitting!)*
*   **Summary of Results:** The custom engine runs at a perfectly stable framerate without memory leaks. Headless tests proved that logic elements could successfully compute independent of the JavaFX UI thread.

---

## 8. Challenges and Solutions

*   **Major Issue:** Trying to force a third-party framework (FXGL) to do exactly what we wanted resulted in bloated, convoluted workaround code.
    *   *Solution:* We scraped the bloated engine entirely and built a lean, custom physics loop using purely a JavaFX `Canvas`.
*   **Major Issue:** Entities were getting permanently stuck together when their collision boxes overlapped by a single pixel.
    *   *Solution:* We implemented projection-based collision. Instead of checking if they *are* colliding, we check if their *next step* will collide, and cancel the movement before the overlap ever happens!

---

## 9. Conclusion

### Summary
The Red Lolli project succeeded in providing a terrifying but mechanically robust 2D dungeon crawling experience. By avoiding heavy external engines, we were forced to genuinely understand what makes a game tick behind the scenes.

### What was learned
We gained an incredible amount of practical experience in processing arrays locally, writing scalable Object-Oriented architectures, handling mathematical vectors, and figuring out how to stop Java's Garbage Collector from causing micro-stutters during gameplay.

### Possible Future Improvements
*   **More Levels:** Adding a system to infinitely generate random mazes rather than relying on strict CSV maps.
*   **More Items:** Implementing flashlights, speed boots, or placable traps for Pale Luna.
*   **Multiplayer:** Refactoring the `GameStateManager` to support a local split-screen mode where one player is trapped, and the other plays as Pale Luna.