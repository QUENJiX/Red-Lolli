package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import com.nsu.cse215l.redlolli.redlolli.core.Hitbox2D;

/** A stationary decoy entity that distracts wandering threats. 
 * This entity has been fully DECOUPLED from JavaFX image rendering.
 */
public class CardboardClone extends Entity implements Collidable {

    public CardboardClone(double x, double y) {
        super(x, y, 20.0);
    }

    @Override
    public void update() {
    }

    @Override
    public Hitbox2D getHitbox() {
        return new Hitbox2D(x, y, size, size);
    }

    public double getSize() {
        return size;
    }
}
