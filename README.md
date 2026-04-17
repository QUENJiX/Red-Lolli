# RedLolli

**North South University - CSE215L (Java Programming Language Lab) Final Project**

RedLolli is a 2D grid-based/dungeon-crawler game built using **JavaFX** and the **FXGL** game engine. It incorporates stealth, survival, and puzzle elements where players navigate complex mazes, avoid relentless enemies (like guards and serial killers), and utilize the environment (torches, cardboard clones) to survive and escape.

## 🎮 Gameplay Features

*   **Diverse Enemies:** Dodge or outsmart unique enemy types including Guards and Serial Killers.
*   **Dynamic Environments:** Traverse procedurally managed levels (Mazes) featuring line-of-sight and interactive light mechanics (Torches).
*   **Tactical Items & Abilities:** Deploy items like the `CardboardClone` to deceive enemies, manage your inventory with `Item` entities, and monitor your condition via the built-in `PlayerStatsComponent`.
*   **Collision & Hitbox System:** Advanced 2D collision detection implemented entirely from scratch utilizing `CollisionSystem` and `Hitbox2D`.
*   **Event-Driven Architecture:** A decoupled `GameEventBus` handles interactions seamlessly across the game’s core mechanics.
*   **Custom Rendering & HUD:** Complete control over graphics with dual rendering pipelines: `GameRenderer` for the real-time game world and `HUDRenderer` for the overlay mechanics and player stats.

## ⚙️ Architecture & Technologies

*   **Java Version:** Java/JDK 25
*   **Graphics & UI:** JavaFX 21.0.6, FXGL 17.3
*   **Build Tool:** Maven
*   **Data Handling:** Jackson (v2.18.6) for saving/loading configurations and game states
*   **Testing:** JUnit 5.12.1

## 📁 Project Structure

```text
src/main/java/com/nsu/cse215l/redlolli/
├── core/
│   ├── GameStateManager  # Handles Game States (Menu, Play, Pause, Game Over)
│   ├── GameEventBus      # In-game event broker (damage, interactions, pickups)
│   └── GameLogger        # Custom debug and event logger
├── entities/
│   ├── Player            # Main character logic and controller
│   ├── Monster           # Base enemy implementation
│   ├── GuardEntity       # Patrolling enemy type
│   ├── SerialKillerEntity# Aggressive tracking enemy type
│   ├── Item              # Collectible items and powerups
│   ├── TorchEntity       # Dynamic lighting and interactions
│   └── CardboardClone    # Decoy item for stealth maneuvers
├── systems/
│   ├── CollisionSystem   # Logic to prevent overlapping and trigger hitboxes
│   ├── EntityManager     # Lifecycle management of all on-screen objects
│   ├── LevelManager      # Maze parsing, generation, and transitions
│   └── SoundManager      # BGM and SFX player
├── ui/
│   ├── GameRenderer      # Graphics pipeline (Canvas/Nodes)
│   ├── HUDRenderer       # Overlay (HP, Inventory, Score)
│   └── SceneFactory      # View constructor for FXGL/JavaFX scenes
└── Launcher              # Main entry point bootstrapping FXGL/JavaFX
```

## 🚀 Getting Started

### Prerequisites

Ensure you have the following installed to compile and run the project:
*   [Java Development Kit (JDK) 25+](https://jdk.java.net/25/)
*   [Apache Maven](https://maven.apache.org/download.cgi) (or use the provided `mvnw` wrapper)

### Compiling and Running

You can compile and run the game directly from the terminal utilizing the Maven JavaFX plugin.

**Windows:**
```powershell
./mvnw clean compile javafx:run
```

**macOS / Linux:**
```bash
./mvnw clean compile javafx:run
```

*Note: The application will launch via `HelloApplication.java` mapped to the default Maven execution profile.*

## 🛠️ Testing

RedLolli has built-in unit tests to ensure that state, logic, and headless entity initializations execute cleanly.

Run the test suite using:
```bash
./mvnw test
```

## 📝 Design Patterns Utilized

*   **Component Pattern:** (e.g., `PlayerStatsComponent`) for data decoupling on entities.
*   **Factory Pattern:** (e.g., `SceneFactory`) for streamlined UI creation.
*   **Singleton/Manager Pattern:** For global handlers like `GameLogger`, `SoundManager`, and `LevelManager`.
*   **Observer/Pub-Sub Pattern:** Operated through the `GameEventBus` to notify systems of state changes gracefully.

## 👥 Authors
Developed for the **CSE215L** - Java Programming Language Lab Final Project at **North South University (NSU)**.