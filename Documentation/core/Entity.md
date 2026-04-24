# Entity.java - Complete Structural Analysis

## 1. The "Elevator Pitch" (High-Level Overview)

**Entity** is the **abstract base class** for every physical object in your game world. It's the parent class of Player, Monster, Guard, Item, Torch, and CardboardClone. Think of it as the **blueprint for anything that exists in the game**. All entities share common properties: position (X, Y) and size. All entities must implement an `update()` method (called each frame to animate/move). Entity also provides collision notification methods so entities can react when they bump into each other. By having all game objects inherit from Entity, you ensure consistency and enable the collision system to treat all objects uniformly.

---

## 2. Core Computer Science Concepts

### **Design Patterns Used:**
- **Abstract Base Class Pattern:** Entity is abstract (can't instantiate directly), forcing subclasses to implement abstract methods
- **Template Method Pattern:** The Entity class defines the "template" of what an entity should have (position, size, update method), and subclasses fill in the details
- **Inheritance Hierarchy:** All game entities form a tree: Entity → Player, Monster, Item, etc.
- **Polymorphism:** The game treats all Entities the same way (calling update() on each) even though they behave differently

### **Why These Concepts Matter:**
- **Code Reuse:** Position (x, y, size) is defined once in Entity and inherited by all subclasses. No duplication
- **Consistency:** Every entity has the same interface (getX(), getY(), update()), so code can treat them uniformly
- **Extensibility:** Adding a new entity type (e.g., Projectile) just means creating a new class that extends Entity
- **Polymorphic Updates:** GameStateManager calls `entity.update()` on all entities without knowing their concrete type. Each entity behaves uniquely because they override update() differently

---

## 3. Deep Dive: Variables and State

### **Critical Instance Variables (Protected):**

| Field | Type | Access | Purpose |
|-------|------|--------|---------|
| `x` | `double` | `protected` | X-coordinate in world space (pixels) |
| `y` | `double` | `protected` | Y-coordinate in world space (pixels) |
| `size` | `double` | `protected` | Radius/half-width of entity (used for collision and rendering) |

### **Why This State Matters:**

- **Position (x, y):** Every entity has a location in the world. Enemies use this to track player. Rendering uses this to draw sprites
- **Size:** Defines collision radius and sprite dimensions. A Torch is size 20, a Player is size 20, but a Monster might be size 25
- **`protected` access:** Subclasses can directly read and modify these fields (e.g., `this.x += moveAmount`). External code cannot (forces use of getters/setters)
- **`double` precision:** Positions use floating-point for smooth movement (can move 0.5 pixels per frame)

**Important:** These fields are NOT final—they change as entities move. This is different from Hitbox2D (which is immutable).

---

## 4. Deep Dive: Methods and Logic (Step-by-Step)

### **Method 1: Constructor `Entity(double x, double y, double size)`**

**The Goal:**
Create a new entity at a specific position with a specific size.

**How it Works (Layman's Terms):**
1. Store X position
2. Store Y position
3. Store size

**Why it Works:**
- Simple initialization
- Subclasses call `super(x, y, size)` to initialize the base entity data

**Example:**
```java
public class Player extends Entity {
    public Player(double x, double y) {
        super(x, y, 20.0);  // Player is size 20
    }
}

public class Monster extends Entity {
    public Monster(double x, double y) {
        super(x, y, 25.0);  // Monster is size 25
    }
}
```

---

### **Method 2: `update()` [Abstract Method - Must Override]**

**The Goal:**
Update this entity's state for the current frame. Called 60 times per second.

**How it Works (Layman's Terms):**
1. This is an *abstract method* (no implementation in Entity)
2. Subclasses MUST override this method
3. Each subclass implements its own behavior:
   - Player: process input, update position
   - Monster: run AI, pursue player
   - Torch: play animation, emit light
   - Item: maybe rotate or bob up/down

**Example Implementations:**
```java
// In Player:
@Override
public void update() {
    // Handle player movement from input
    // Update animation
    // Decay sanity
}

// In Monster:
@Override
public void update() {
    // Run pathfinding AI
    // Move toward target
    // Play animation
}

// In Item:
@Override
public void update() {
    // Rotate continuously
    // Bob up and down
}

// In Torch:
@Override
public void update() {
    // Flicker flame animation
}
```

**Why it Works:**
- **Polymorphism:** GameStateManager calls `entity.update()` on all entities without knowing their type. Each calls the correct override
- **Abstract enforcement:** By making update() abstract, you force every subclass to define update behavior (no "empty" entities)
- **Consistent Timing:** Called once per frame for all entities, ensuring synchronized simulation

---

### **Method 3: `publishCollision(Entity other)` [Collision Notification]**

**The Goal:**
Publish a collision event to the event bus when this entity hits another entity.

**How it Works (Layman's Terms):**
1. Get the singleton GameEventBus instance
2. Call `publishCollision(this, other)` on the bus
3. All subscribers to collision events are notified

**Why it Works:**
- **Event-Based Communication:** Entities don't need to know what happens when they collide. They just publish the event and let interested systems handle it
- **Loose Coupling:** Entity doesn't know if a SoundSystem is listening, if a VFX system is listening, etc.
- **Reusability:** Any entity can publish collisions; any system can listen

**Example Usage:**
```java
// In CollisionSystem:
if (player.getHitbox().intersects(guard.getHitbox())) {
    player.publishCollision(guard);  // Player publishes
    guard.publishCollision(player);  // Guard also publishes (two-way)
}

// In SoundSystem (listening):
public void onCollision(Entity e1, Entity e2) {
    if (e1 instanceof Player && e2 instanceof GuardEntity) {
        playSoundEffect("guard_hit.wav");
    }
}
```

---

### **Method 4: `onCollide(Entity other)` [Collision Handler]**

**The Goal:**
Handle a collision with another entity. This is the method subclasses can override if they want custom collision behavior.

**How it Works (Layman's Terms):**
1. This method is called when this entity collides with another
2. Default implementation: publish the collision event
3. Subclasses can override if they want special behavior

**Example Implementation:**
```java
// In Player (extends Entity):
@Override
public void onCollide(Entity other) {
    if (other instanceof GuardEntity) {
        // Special behavior: take damage
        takeDamage(10);
    } else if (other instanceof Item) {
        // Special behavior: collect item
        collect((Item)other);
    } else {
        // Default: just publish the collision
        super.onCollide(other);
    }
}
```

**Why it Works:**
- **Template Method Pattern:** Entity provides default behavior (`publishCollision`), but subclasses can override with custom behavior
- **Flexibility:** Some entities care about specific collision types. Others just publish and let systems handle it

---

### **Method 5: `getX()` [Position X Getter]**

**The Goal:**
Return this entity's X-coordinate.

**How it Works (Layman's Terms):**
```
return x;
```

**Why it Works:**
- External code can query position without modifying it
- Read-only access (there's no setX method)

---

### **Method 6: `getY()` [Position Y Getter]**

**The Goal:**
Return this entity's Y-coordinate.

**How it Works (Layman's Terms):**
```
return y;
```

**Why it Works:**
- External code can query position without modifying it

---

### **Method 7: `getSize()` [Size Getter]**

**The Goal:**
Return this entity's size (radius or half-width).

**How it Works (Layman's Terms):**
```
return size;
```

**Why it Works:**
- Collision system uses this to calculate hitbox
- Rendering system uses this to size sprites

---

### **Method 8: `setPosition(double x, double y)` [Position Setter]**

**The Goal:**
Set this entity's position to a new location.

**How it Works (Layman's Terms):**
```
this.x = x;
this.y = y;
```

**Why it Works:**
- Convenient way to move an entity to an exact location
- Used when teleporting (Luna teleports near player), spawning entities, etc.

**Example Usage:**
```java
// In GameStateManager:
if (playerStoodStill) {
    entityManager.getPaleLuna().setPosition(
        player.getX() + 36,
        player.getY()
    );  // Teleport Luna near player
}
```

---

## 5. Deep Dive: Model-View-Controller (MVC) Pattern

**Where Entity Fits in MVC:**

| MVC Layer | Entity's Role |
|-----------|---------------|
| **Model** | ✅ **YES** Entity represents model objects with position and behavior. It's the base of all game object models |
| **View** | ❌ No |
| **Controller** | ❌ No |

**Context in Game Architecture:**
```
Entity (abstract base)
    ├─ Player (model with position, sanity, items)
    ├─ Monster (model with position, state machine)
    ├─ GuardEntity (model with position, behavior)
    ├─ Item (model with position, content type)
    ├─ TorchEntity (model with position)
    └─ CardboardClone (model with position)

All these get:
    ├─ Placed in EntityManager
    ├─ Updated via update() each frame
    ├─ Rendered via GameRenderer reading their position
    └─ Checked for collisions via getHitbox()
```

Entity is the **foundation of the model layer**. All game objects are Entities.

---

## 6. Lab Final Presentation Arsenal

Here are 4 professional talking points for your lab presentation:

• **"Entity serves as an abstract base class implementing the Template Method design pattern, establishing a contract that all game objects must adhere to specific lifecycle and interface requirements. By defining abstract method update() and concrete getter/setter methods for position and size, Entity enforces architectural consistency: every game object updates every frame, maintains spatial data, and can be queried uniformly by game systems regardless of concrete type."**

• **"The inheritance hierarchy rooted in Entity (Player → MonsterEntity, Item, Guard, etc.) enables polymorphic entity updates: GameStateManager iterates a Collection<Entity> and calls update() on each, where dynamic dispatch ensures the correct overridden method executes for each concrete type. This polymorphic design eliminates conditional logic that would otherwise plague a naive if-else entity update system."**

• **"I designed Entity's collision notification methods (publishCollision(), onCollide()) to enable event-driven collision responses. Rather than coupling entities directly to response logic, entities publish collision events to the event bus, decoupling spatial detection from behavioral reactions. This architecture allows new collision behaviors (particle effects, sound, score increases) to be added without modifying Entity or subclasses."**

• **"The protected access modifier on position fields (x, y) and size establishes an encapsulation boundary: subclasses have direct field access for performance-critical position updates in real-time simulations, while external code must use getter/setter methods. This balance prioritizes game performance (avoiding getter method call overhead in tight update loops) while maintaining encapsulation contracts for non-subclass code."**

---

## 7. Critical Implementation Details

### **Protected Access Pattern**

```java
protected double x;  // Subclasses can access directly
protected double y;
protected double size;
```

vs.

```java
private double x;    // Only through getters
public double getX() { return x; }
```

**Why protected?**
- Subclasses often need rapid position updates in the update() method
- Direct field access is faster than method calls (though modern JVMs optimize this)
- Subclasses are "trusted" code (you control them), unlike external code

**Performance Consideration:**
```java
// Fast (direct):
this.x += moveSpeed;

// Slower (method call):
this.setX(this.getX() + moveSpeed);
```

In game loops running 60 FPS with dozens of entities, this small difference adds up.

### **Abstract Method Enforcement**

```java
public abstract void update();
```

This **forces** every subclass to implement update(). You cannot create an anonymous Entity:

```java
// Compile ERROR:
Entity e = new Entity(0, 0, 20);  // Can't instantiate abstract class
```

Only concrete subclasses:

```java
// OK:
Player p = new Player(0, 0);  // Player overrides update()
```

This ensures no "empty" entities that don't update.

### **Collision Event Publication**

```java
public void publishCollision(Entity other) {
    GameEventBus.getInstance().publishCollision(this, other);
}
```

Notice: Entity knows about GameEventBus (tight coupling to event system), but GameEventBus doesn't know about Entity. This is an acceptable trade-off: the core infrastructure (event bus) is decoupled from individual entity types.

---

## 8. Inheritance Hierarchy

```
Entity (abstract)
  ├─ Player extends Entity
  │    └─ Implements: update(), collision behavior, sanity system
  │
  ├─ Monster extends Entity
  │    └─ Implements: update() with state machine, AI pathfinding
  │
  ├─ GuardEntity extends Entity
  │    └─ Implements: update() with patrol/chase AI
  │
  ├─ SerialKillerEntity extends Entity
  │    └─ Implements: update() with boss behavior
  │
  ├─ Item extends Entity (implements Collidable)
  │    └─ Implements: update() (static, no movement)
  │
  ├─ TorchEntity extends Entity
  │    └─ Implements: update() with flame animation
  │
  └─ CardboardClone extends Entity (implements Collidable)
       └─ Implements: update() (decoy, stationary)
```

All share:
- ✅ Position (x, y)
- ✅ Size (for collisions, rendering)
- ✅ getX(), getY(), getSize()
- ✅ update() method (each implements uniquely)
- ✅ Collision publishing capability

---

## 9. Update Loop Architecture

```
GameStateManager.update() runs each frame:
    ├─ Update all guards:
    │    for (Guard g : guards) g.update()  // Polymorphic call
    │
    ├─ Update all torches:
    │    for (Torch t : torches) t.update()  // Polymorphic call
    │
    ├─ Update player:
    │    player.update()  // Polymorphic call
    │
    └─ (Each entity's update() is different, but called uniformly)
```

This is the power of polymorphism: one loop calling update() handles all entity types.

---

## 10. Key Takeaway

**Entity is the architectural foundation of your game.** It's not fancy, but it's essential:
- ✅ Provides common structure (position, size)
- ✅ Enforces consistent interface (getX, getY, update)
- ✅ Enables polymorphic handling (all entities updated uniformly)
- ✅ Enables extensibility (add new entity types by extending Entity)
- ✅ Provides collision notifications (via GameEventBus)

This is industrial-strength architecture. Most game bugs stem from objects that don't fit the expected structure. By having all game objects inherit from Entity with a strict contract, you eliminate entire categories of bugs before they can happen.

Entity is where design meets implementation: it's the bridge between architectural theory and practical game code.
