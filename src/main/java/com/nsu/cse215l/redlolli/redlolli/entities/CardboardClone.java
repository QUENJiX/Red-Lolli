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
        } catch (Exception e) {
            System.err.println("Error loading CardboardClone sprite: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static void initImages() {
        if (imagesInitialized)
            return;
        cloneDecoyImg = loadSprite("clone_decoy.png", 50, 50);
        imagesInitialized = true;
    }

    /** Call this to force images to reload (e.g. after changing asset paths). */
    public static void resetImages() {
        imagesInitialized = false;
    }

    // Visual render size (50x50 centered on the 20x20 hitbox)
    private static final double RENDER_SIZE = 50.0;

    // ================= LOGIC =================

    public CardboardClone(double x, double y) {
        super(x, y, 20.0);
    }

    @Override
    public void update() {
    }

    @Override
    public void render(GraphicsContext gc) {
        // Draw sprite centered on hitbox (hitbox 20x20, sprite 40x40)
        double offset = (RENDER_SIZE - size) / 2;
        if (cloneDecoyImg != null) {
            gc.drawImage(cloneDecoyImg, x - offset, y - offset, RENDER_SIZE, RENDER_SIZE);
        } else {
            gc.setFill(Color.rgb(210, 180, 140));
            gc.fillOval(x - offset, y - offset, RENDER_SIZE, RENDER_SIZE);
        }
    }

    @Override
    public Rectangle2D getHitbox() {
        return new Rectangle2D(x, y, size, size);
    }
}
