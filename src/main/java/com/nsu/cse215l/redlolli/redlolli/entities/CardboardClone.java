package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import javafx.geometry.Rectangle2D;

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
    public Rectangle2D getHitbox() {
        return new Rectangle2D(x, y, size, size);
    }

    public double getSize() {
        return size;
    }
}
