package com.nsu.cse215l.redlolli.redlolli.core;

import com.nsu.cse215l.redlolli.redlolli.entities.Entity;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A lightweight event bus for publishing and subscribing to game events
 * (e.g., collisions, state changes) without tightly coupling systems.
 */
public class GameEventBus {
    private static final GameEventBus INSTANCE = new GameEventBus();

    public static class CollisionEvent {
        public final Entity source;
        public final Entity target;

        public CollisionEvent(Entity source, Entity target) {
            this.source = source;
            this.target = target;
        }
    }

    private final List<Consumer<CollisionEvent>> collisionListeners = new ArrayList<>();

    private GameEventBus() {}

    public static GameEventBus getInstance() {
        return INSTANCE;
    }

    public void subscribeToCollisions(Consumer<CollisionEvent> listener) {
        collisionListeners.add(listener);
    }

    public void publishCollision(Entity source, Entity target) {
        CollisionEvent event = new CollisionEvent(source, target);
        for (Consumer<CollisionEvent> listener : collisionListeners) {
            listener.accept(event);
        }
    }
    
    public void clear() {
        collisionListeners.clear();
    }
}
