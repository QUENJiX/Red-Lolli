package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.InputStream;

/** A stationary decoy entity that distracts wandering threats. */
public class CardboardClone extends Entity implements Collidable {

    // ================= IMAGE ASSETS =================

    private static Image cloneDecoyImg;
    private static boolean imagesInitialized = false;

    private static Image loadSprite(String filename, int width, int height) {
        try {
            InputStream is = CardboardClone.class.getResourceAsStream("/assets/images/sprites/" + filename);
            if (is != null) {
                return new Image(is, width, height, true, false);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static void initImages() {
        if (imagesInitialized) return;
        cloneDecoyImg = loadSprite("clone_decoy.png", 20, 20);
        imagesInitialized = true;
    }

    // ================= LOGIC =================

    public CardboardClone(double x, double y) {
        super(x, y, 20.0);
    }

    @Override
    public void update() {
    }

    @Override
    public void render(GraphicsContext gc) {
        if (cloneDecoyImg != null) {
            gc.drawImage(cloneDecoyImg, x, y, size, size);
        } else {
            gc.setFill(Color.MAGENTA);
            gc.fillRect(x, y, size, size);
        }
    }

    @Override
    public Rectangle2D getHitbox() {
        return new Rectangle2D(x, y, size, size);
    }
}
