package com.nsu.cse215l.redlolli.redlolli.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.nsu.cse215l.redlolli.redlolli.entities.Entity;

/**
 * A central messaging system that lets different parts of the game 
 * talk to each other without being tightly coupled. 
 * If a player does something, the event bus broadcasts it securely 
 * so other components can react accordingly.
 */
public class GameEventBus {

    /**
     * The different types of events that can occur during the game.
     */
    public enum EventType {
        LOLLI_COLLECTED,
        CLONE_USED,
        SHIELD_ACTIVATED,
        TELEPORT_USED,
        SPEED_BOOST_ACTIVATED,
        MONSTER_DISTRACTED,
        LUNA_HUNT_STARTED,
        LUNA_DORMANT,
        PLAYER_DAMAGE_TAKEN,
        PLAYER_HEALED
    }

    /**
     * A simple bundle holding information about something that just happened.
     * Includes the type of event and any extra data that goes with it.
     */
    public static class GameEvent {
        public final EventType type;
        public final Object payload;

        /**
         * Creates a new game event to be broadcast over the bus.
         * 
         * @param type    The type of event (e.g., player took damage).
         * @param payload Any extra information about the event (e.g., the amount of damage).
         */
        public GameEvent(EventType type, Object payload) {
            this.type = type;
            this.payload = payload;
        }
    }

    /**
     * The interface for any class that wants to listen to the event bus.
     * Tells listeners when an event or a collision happens.
     */
    public interface EventListener {
        /**
         * Called whenever a new event is broadcasted.
         * 
         * @param event The event holding all the juicy details.
         */
        void onEvent(GameEvent event);

        /**
         * Allows listeners to instantly know when two entities bump into each other.
         * 
         * @param entity1 The first entity in the collision.
         * @param entity2 The second entity in the collision.
         */
        default void onCollision(Entity entity1, Entity entity2) {
        }
    }

    private static final GameEventBus INSTANCE = new GameEventBus();
    private final List<EventListener> listeners = new ArrayList<>();
    private final Queue<GameEvent> eventQueue = new ConcurrentLinkedQueue<>();
    private boolean isDispatching = false;

    /**
     * Prevents other classes from instantiating it, ensuring
     * we stick to a singleton design.
     */
    private GameEventBus() {
    }

    /**
     * Gets the main, globally-available instance of our GameEventBus.
     * 
     * @return The one and only GameEventBus.
     */
    public static GameEventBus getInstance() {
        return INSTANCE;
    }

    /**
     * Adds an object to the list of listeners so it can receive updates.
     * 
     * @param listener The object that wants to hear about events.
     */
    public void register(EventListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Tells the event bus to stop sending events to this specific listener.
     * 
     * @param listener The object that's tired of getting notifications.
     */
    public void unregister(EventListener listener) {
        listeners.remove(listener);
    }

    /**
     * Broadcasts an event to everyone currently listening.
     * Enqueues events if we're already busy dispatching.
     * 
     * @param event The event packet we're broadcasting.
     */
    public void publish(GameEvent event) {
        if (isDispatching) {
            // If we're already notifying listeners, just add it to the queue
            // so we don't mess up our looping list.
            eventQueue.add(event);
        } else {
            isDispatching = true;
            try {
                for (EventListener listener : listeners) {
                    listener.onEvent(event);
                }
                // Handle any backlog that built up while we were sending the first batch.
                while (!eventQueue.isEmpty()) {
                    GameEvent next = eventQueue.poll();
                    for (EventListener listener : listeners) {
                        listener.onEvent(next);
                    }
                }
            } finally {
                isDispatching = false;
            }
        }
    }

    /**
     * Immediately tells all listeners that a physical collision occurred between
     * two game entities.
     * 
     * @param e1 The first entity in the crash.
     * @param e2 The second entity in the crash.
     */
    public void publishCollision(Entity e1, Entity e2) {
        for (EventListener listener : listeners) {
            listener.onCollision(e1, e2);
        }
    }
}
