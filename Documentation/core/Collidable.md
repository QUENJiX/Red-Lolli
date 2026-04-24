# Collidable.java - Complete Structural Analysis

## 1. The "Elevator Pitch" (High-Level Overview)

**Collidable** is a **contract** that says "I can be collided with." It's a tiny interface with one job: any class that implements Collidable must provide a method `getHitbox()` that returns information about where it is in space. Think of it as a **collision signature**—it tells the collision system "I'm a thing that can bump into things, and here's my shape and location so you can test if I hit something else." This interface is the foundation of the entire collision detection system.

---

## 2. Core Computer Science Concepts

### **Design Patterns Used:**
- **Interface/Contract Pattern:** Collidable defines an explicit contract that any class agreeing to be collided with must fulfill
- **Marker Interface:** Similar to Java's `Serializable` interface, Collidable *marks* classes as "collision-aware"
- **Dependency Inversion Principle (DIP):** The collision system depends on the Collidable interface, not on concrete implementations. This decouples collision logic from specific entity types

### **Why These Concepts Matter:**
- **Contracts:** By requiring `getHitbox()`, we ensure every collidable object can tell us where it is. The collision system never needs to ask "What type is this?"—it just asks "What's your hitbox?"
- **Loose Coupling:** Player, Monster, Guard, Item, Torch—all these different classes implement Collidable. The collision system doesn't care which ones. This allows new entity types to be added without changing collision code
- **Single Responsibility:** Collidable does ONE thing: defines what it means to be collidable. It doesn't implement collision detection (that's Hitbox2D's job)

---

## 3. Deep Dive: Variables and State

**Collidable has NO fields.**

This is intentional and perfect. Collidable is a **pure contract**—a promise. It says "I will provide a hitbox" but doesn't store or manage any data. Data lives in the concrete implementations (Entity, Monster, Player, etc.). This is the core principle of **interface-based design**: interfaces define responsibilities, not data.

---

## 4. Deep Dive: Methods and Logic (Step-by-Step)

### **Method 1: `getHitbox()` [Collision Shape Provider]**

**The Goal:**
Return a `Hitbox2D` object representing this entity's collision shape in world space.

**How it Works (Layman's Terms):**
1. This is an *abstract method* (no implementation in the interface)
2. Any class implementing Collidable MUST override this method
3. That class should return a Hitbox2D describing where this entity is located
4. The returned Hitbox2D should be up-to-date (reflect current position)

**Example implementations:**
```java
// In Player class:
@Override
public Hitbox2D getHitbox() {
    return new Hitbox2D(x, y, size, size);  // Square hitbox at player's position
}

// In Monster class:
@Override
public Hitbox2D getHitbox() {
    return new Hitbox2D(x, y, size, size);
}

// In Item class:
@Override
public Hitbox2D getHitbox() {
    return new Hitbox2D(x, y, size, size);
}
```

**Why it Works:**
- **Lazy evaluation:** Hitbox2D is created fresh each time `getHitbox()` is called, so it always reflects the current position (even if the entity moved since last frame)
- **Polymorphism:** The collision system can call `getHitbox()` on ANY Collidable without knowing its concrete type
- **Flexibility:** Some entities could return weird shapes in the future (non-square hitboxes)—the interface allows it

**Key Pattern:** This is called **lazy instantiation**. We don't store a hitbox; we compute it on-demand. This is efficient and ensures freshness.

---

## 5. Deep Dive: Model-View-Controller (MVC) Pattern

**Where Collidable Fits in MVC:**

| MVC Layer | Collidable's Role |
|-----------|-------------------|
| **Model** | ✅ **YES** Collidable defines the collision properties of model entities |
| **View** | ❌ No |
| **Controller** | ❌ No |

**Context in Game Architecture:**
```
Entity (abstract base class, implements Collidable)
    ↓
[Player, Monster, Item, Guard, etc. all implement Collidable]
    ↓
[CollisionSystem calls getHitbox() on all Collidables]
    ↓
[CollisionSystem uses Hitbox2D to test intersection]
    ↓
[Collision events triggered]
```

Collidable is the **glue** between the model (entities with positions) and the collision system (which needs to query positions).

---

## 6. Lab Final Presentation Arsenal

Here are 3 professional talking points for your lab presentation:

• **"Collidable is an interface that implements the Dependency Inversion Principle, allowing the collision detection system to depend on an abstraction rather than concrete entity types. By requiring all collidable entities to implement a single method—getHitbox()—I achieved type-agnostic collision queries, enabling the system to uniformly handle Player, Monster, Guard, Item, and Torch entities without direct type knowledge or conditional logic."**

• **"The Collidable interface represents a contract-based approach to collision detection: each implementing class promises to provide accurate collision geometry via getHitbox(), evaluated lazily each frame. This design decision ensures that collision queries always reflect current entity positions, eliminating synchronization bugs that could arise from cached hitbox data."**

• **"Implementing Collidable as a minimal, single-method interface follows the Interface Segregation Principle. Rather than forcing all entities to inherit collision behavior or implement a massive composite interface, each entity commits only to providing collision geometry, maintaining clean architectural separation between collision concerns and other entity behaviors."**

---

## 7. Quick Reference

**Collidable in One Sentence:**
*"A promise that I have a position and shape, and I can tell you about it via `getHitbox()`."*

**Who Implements Collidable?**
- Player
- Monster (Pale Luna)
- GuardEntity
- SerialKillerEntity
- Item
- TorchEntity
- CardboardClone

**Who Uses Collidable?**
- CollisionSystem

**The Contract:**
```
IF you implement Collidable
THEN you MUST override getHitbox()
AND getHitbox() must return accurate, up-to-date collision shape
```

---

## 8. Common Misconception

**"Why not just store a hitbox field in Entity?"**

Good question! If we did that:
- ❌ Hitbox would go stale if entity moved before next collision check
- ❌ We'd need a setter and synchronization logic
- ❌ Memory overhead (every entity stores two copies of position data)

By computing hitbox on-demand, we guarantee freshness and simplicity.

---

## 9. Key Takeaway

**Collidable is the simplest but most important interface in your game.** It's not fancy, but it's perfect architecture. By requiring just ONE method, it:
- Forces implementing classes to be collision-aware
- Allows the collision system to be entity-type-agnostic
- Keeps the design clean and extensible

This is an example of **strong interface design**: minimal, focused, and universally useful.
