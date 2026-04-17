package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.core.Hitbox2D;

/**
 * Operates as a dynamic environmental interaction node representing
 * discoverable architectural resources centrally.
 * Encapsulates singular internal payloads (e.g., plot items or diversions)
 * defining discrete interaction permutations organically.
 * Wholly decoupled from implicit rendering constraints to support purely
 * logical headless iterations uniformly.
 */
public class Item extends Entity implements Collidable {

    /**
     * Enumerates literal payload enumerations restricting arbitrary asset
     * instantiation procedurally.
     */
    public enum ContentType {
        EMPTY,
        LOLLI,
        CLONE_DECOY
    }

    private boolean isCollected = false;
    private final ContentType contentType;

    /**
     * Instantiates the interaction node explicitly bounded to localized global
     * coordinates.
     * 
     * @param x           Arbitrary longitudinal coordinate native to the map grid
     *                    physically.
     * @param y           Arbitrary latitudinal coordinate native to the map grid
     *                    physically.
     * @param contentType Structural dependency mapping predetermined physical
     *                    contents strictly.
     */
    public Item(double x, double y, ContentType contentType) {
        super(x, y, 16.0);
        this.contentType = contentType;
    }

    /**
     * Implements blank operational overloads assuring standardized collection
     * traversal across global managers implicitly.
     */
    @Override
    public void update() {
    }

    /**
     * Evaluates geometric interaction completion flipping internal procedural
     * validation boolean securely.
     */
    public void collect() {
        this.isCollected = true;
    }

    /**
     * Replicates internal positional magnitudes returning external physical bounds
     * uniformly translated.
     * 
     * @return Hitbox2D Bounding arrays restricting dynamic traversals mapped
     *         logically.
     */
    @Override
    public Hitbox2D getHitbox() {
        return new Hitbox2D(x, y, size, size);
    }

    /**
     * Communicates immutable collection states implicitly restricting reiteration
     * loops naturally.
     * 
     * @return boolean True only following successful physical intersection
     *         executions.
     */
    public boolean isCollected() {
        return isCollected;
    }

    /**
     * Serves as an isolated explicit payload verification mapping direct
     * progression catalysts organically.
     * 
     * @return boolean True when internal architecture isolates explicit primary
     *         progression nodes securely.
     */
    public boolean hasLolli() {
        return contentType == ContentType.LOLLI;
    }

    /**
     * Extracts immutable explicit categorizations governing downstream mechanical
     * rewards strictly.
     * 
     * @return ContentType Extracted intrinsic classification natively mapped.
     */
    public ContentType getContentType() {
        return contentType;
    }

    /**
     * Extrapolates generic dimension scalar values evaluating rendering
     * abstractions sequentially isolated.
     * 
     * @return double Numerical magnitude identifying physical boundary magnitudes
     *         explicitly.
     */
    public double getSize() {
        return size;
    }
}
