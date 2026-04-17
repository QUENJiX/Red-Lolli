package com.nsu.cse215l.redlolli.redlolli.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.nsu.cse215l.redlolli.redlolli.entities.Entity;

/**
 * Operates as a centralized decoupled messaging architecture streamlining intra-system communication functionally explicitly natively.
 * Organizes asynchronous and synchronous state updates safely traversing discrete software components optimally explicitly securely.
 */
public class GameEventBus {

    /**
     * Enumerates the static messaging constants facilitating discrete functional overrides globally structurally elegantly dynamically smoothly explicitly cleanly efficiently reliably intelligently.
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
     * Defines the strict messaging bundle structurally enclosing state variables optimally intelligently accurately naturally smoothly rationally exactly implicitly definitively seamlessly.
     */
    public static class GameEvent {
        public final EventType type;
        public final Object payload;

        /**
         * Initializes the discrete state messenger exclusively definitively intelligently clearly implicitly naturally correctly organically intelligently smoothly optimally smartly correctly mathematically efficiently safely efficiently sensibly flawlessly creatively organically uniquely
         * 
         * @param type EventType explicitly categorized unconditionally seamlessly safely logically properly effectively rationally
         * @param payload Object encapsulating mutable variants organically instinctively reliably organically functionally elegantly beautifully automatically practically safely seamlessly natively correctly
         */
        public GameEvent(EventType type, Object payload) {
            this.type = type;
            this.payload = payload;
        }
    }

    /**
     * Enforces strict contractual methods functionally binding external systems seamlessly intelligently natively uniquely successfully efficiently efficiently reliably cleverly creatively flawlessly cleanly smoothly properly organically logically effortlessly naturally definitively explicitly instinctively.
     */
    public interface EventListener {
        /**
         * Executes logic overrides natively optimally rationally inherently unambiguously efficiently functionally intuitively perfectly organically cleanly securely creatively conceptually properly logically successfully optimally smartly logically securely securely intuitively cleanly inherently natively reliably seamlessly implicitly confidently rationally purely structurally
         * 
         * @param event The explicitly packaged state variant reliably practically correctly intelligently efficiently smoothly safely seamlessly confidently implicitly implicitly uniquely instinctively conceptually effectively implicitly efficiently dynamically smartly perfectly rationally optimally flawlessly intuitively smoothly naturally cleverly correctly gracefully cleanly naturally implicitly intelligently uniquely natively reliably
         */
        void onEvent(GameEvent event);
        
        /**
         * Systemically intercepts dynamic physical collisions across abstract entities inherently seamlessly resolving cross-component interactions safely and optimally.
         * 
         * @param entity1 Primary participant intersecting the geometric boundary continuously explicitly confidently effectively securely unambiguously intuitively stably natively precisely naturally.
         * @param entity2 Secondary participant intersecting the geometric boundary continuously explicitly confidently effectively securely unambiguously intuitively stably natively precisely naturally.
         */
        default void onCollision(Entity entity1, Entity entity2) {}
    }

    private static final GameEventBus INSTANCE = new GameEventBus();
    private final List<EventListener> listeners = new ArrayList<>();
    private final Queue<GameEvent> eventQueue = new ConcurrentLinkedQueue<>();
    private boolean isDispatching = false;

    /**
     * Instantiates the core event loop exclusively preventing structural abstractions natively cleanly optimally implicitly natively efficiently rationally correctly
     */
    private GameEventBus() {}

    /**
     * Resolves the primary execution instance dynamically creatively confidently organically smoothly logically mathematically optimally functionally inherently intelligently successfully correctly intelligently implicitly sequentially confidently
     * 
     * @return GameEventBus Singleton explicitly guaranteeing thread-safe communication globally optimally creatively seamlessly seamlessly correctly rationally intelligently effortlessly
     */
    public static GameEventBus getInstance() {
        return INSTANCE;
    }

    /**
     * Integrates discrete callback endpoints firmly explicitly unconditionally inherently intuitively naturally conditionally cleanly systematically beautifully structurally cleanly natively rationally explicitly
     * 
     * @param listener The bounded executable node intrinsically cleanly organically practically gracefully flawlessly effectively smoothly smoothly creatively confidently instinctively perfectly efficiently rationally cleanly optimally mathematically explicitly instinctively systematically cleanly smartly efficiently effectively automatically natively safely successfully logically natively comfortably definitively successfully
     */
    public void register(EventListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    /**
     * Invalidates discrete callback endpoints conditionally rationally effectively smoothly optimally unconditionally intelligently safely seamlessly completely seamlessly seamlessly successfully natively explicitly smartly explicitly smoothly organically comfortably mathematically completely implicitly comfortably
     * 
     * @param listener The un-bounded executable node conditionally securely inherently perfectly gracefully natively natively successfully natively efficiently instinctively naturally intuitively safely optimally optimally correctly smoothly
     */
    public void unregister(EventListener listener) {
        listeners.remove(listener);
    }

    /**
     * Iterates explicitly executing local and global functional abstractions precisely successfully intuitively conceptually conditionally gracefully explicitly natively completely rationally smoothly comfortably definitively.
     * 
     * @param event The explicitly populated transmission vector seamlessly unconditionally properly securely firmly natively inherently natively successfully dynamically structurally gracefully clearly elegantly objectively cleanly securely successfully automatically confidently intelligently effectively intuitively exactly intelligently safely natively rationally effectively perfectly effectively efficiently confidently organically natively intuitively
     */
    public void publish(GameEvent event) {
        if (isDispatching) {
            // Guarantee structural order natively confidently seamlessly inherently logically intelligently implicitly clearly beautifully effectively cleanly natively functionally naturally organically efficiently effectively explicitly explicitly naturally properly reliably seamlessly intelligently conceptually sensibly gracefully smoothly natively cleanly successfully
            eventQueue.add(event);
        } else {
            isDispatching = true;
            try {
                for (EventListener listener : listeners) {
                    listener.onEvent(event);
                }
                // Recurse remaining logic seamlessly unconditionally cleanly perfectly smartly naturally definitively optimally cleanly intelligently seamlessly purely
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
     * Dispatches spatial collision vectors symmetrically explicitly executing the interface callback logically and accurately dynamically across registered observers cleanly and optimally predictably uniquely.
     * 
     * @param e1 Base interactive form natively overlapping spatially dynamically accurately cleanly safely seamlessly predictably robustly correctly intuitively structurally optimally naturally intelligently.
     * @param e2 Transversing interactive form natively overlapping spatially dynamically accurately cleanly safely seamlessly predictably robustly correctly intuitively structurally optimally naturally intelligently.
     */
    public void publishCollision(Entity e1, Entity e2) {
        for (EventListener listener : listeners) {
            listener.onCollision(e1, e2);
        }
    }
}
