package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/** A stationary decoy entity that distracts wandering threats. */
public class CardboardClone extends Entity implements Collidable {

    public CardboardClone(double x, double y) {
        super(x, y, 20.0);
    }

    @Override
    public void update() {
    }

    @Override
    public void render(GraphicsContext gc) {
        gc.setFill(Color.rgb(185, 160, 120));
        gc.fillRect(x + 3, y + 4, 14, 16);

        gc.setFill(Color.rgb(215, 200, 170));
        gc.fillOval(x + 4, y - 1, 12, 10);

        gc.setFill(Color.rgb(80, 20, 20));
        gc.fillOval(x + 7, y + 3, 2, 2);
        gc.fillOval(x + 11, y + 3, 2, 2);
    }

    @Override
    public Rectangle2D getHitbox() {
        return new Rectangle2D(x, y, size, size);
    }
}
