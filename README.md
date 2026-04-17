# RedLolli (Escape Pale Luna)

RedLolli is a custom-built 2D top-down survival maze game engine powered by JavaFX, tailored strictly for scalable, object-oriented gameplay mechanics and headless physical simulation.

## Features & Architecture

- **Headless Domain Model**: The core simulation layer (`Entity`, `Player`, `GuardEntity`, `Monster`) is cleanly decoupled from the JavaFX UI layer (removing references to `GraphicsContext` and visual dependencies), guaranteeing testability, separation of concerns, and robust architecture.
- **Custom Collision Physics Engine**: By engineering a bespoke `Hitbox2D` data structure to replace the monolithic JavaFX-bundled `Rectangle2D`, RedLolli calculates collisions purely mathematically—paving the way for advanced UI-independent physics.
- **Event-Driven Gameplay**: Utilizes `GameEventBus` for system-wide, decoupled collision event broadcasting. Entities safely execute deeper polymorphism via `entity.onCollide()` or subscribe actively via `publishCollision()` to avoid heavy `instanceof` cascaded checks.
- **Structured Diagnostics Logging**: Incorporates `GameLogger` mapping output directly to rotating `logs/debug.log` files. This ensures your terminal standard output remains completely pristine without swallowed-exception anti-patterns.
- **Robust Error Resilience & Asset Handling**: Features robust `AssetManager`, `SoundManager`, and map factories that safely degrade to placeholders (`WritableImage`, no-op audio) when assets are missing—never crashing the engine frame rates.

## Core Entities
- **Playable Character**: Must navigate grid-based maps while balancing hitboxes.
- **Hostile Mobs**:
  - `SerialKillerEntity`: Advanced chase AI pathing and cardboard decoy detection rules.
  - `GuardEntity`: Stationary or patrolling threats (`COBRA`, `BAT`, `CENTIPEDE`).
  - `PaleLuna`: The primary antagonist waiting in the abyss.
- **Loot & Interactables**: Include chests, clone decoys, distractions, and progression doors.

## Build and Run Requirements

### Prerequisites
- **Java Development Kit (JDK)**: 21 (or newer)
- **Apache Maven**: Required for downloading `javafx-controls` and `javafx-media` dependencies.

### Command Line Execution
This project uses the official `javafx-maven-plugin`. Run the game locally via:
```bash
mvn clean compile javafx:run
```

## Directory Structure
- `src/main/java/.../redlolli`
  - `core/` - Foundation (`Hitbox2D`, `GameLogger`, `GameEventBus`, `Collidable`, `CollisionSystem`).
  - `entities/` - All actors representing the domain model layer.
  - `map/` - Grid progression (`Maze`, internal parsing).
  - `systems/` - Lifecycle singletons and managers (`AssetManager`, `SoundManager`).
  - `ui/` - Presentation rendering and JavaFX implementations (`GameRenderer`, `SceneFactory`).
- `src/main/resources/` - Texture sprites (`.png`), maps (`.csv`), and `.wav` audio.
- `Audit.md` - Technical audit history covering the Headless domain evolution, and historical technical debt eradication tasks.

## Code Quality Standards
This application actively enforces Test-Driven Development readiness, clean console outputs, semantic package definitions, and the dissolution of "God Object" anti-patterns. All progression rules successfully separate rendering (`render()`) from frame simulation computations (`update()`).
