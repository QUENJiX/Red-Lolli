package com.nsu.cse215l.redlolli.redlolli.entities;

import com.nsu.cse215l.redlolli.redlolli.core.Collidable;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.InputStream;

/**
 * Interactive maze objects modeled as chests containing various loot types.
 * Manages visual state (open/closed) and glowing cues based on content.
 */
public class Item extends Entity implements Collidable {

    public enum ContentType {
        EMPTY,
        LOLLI,
        CLONE_DECOY
    }

    // ================= IMAGE ASSETS =================

    private static Image chestClosedImg;
    private static Image chestOpenedImg;
    private static Image chestGlowLolli;
    private static Image chestGlowClone;
    private static boolean imagesInitialized = false;

    private static Image loadSprite(String filename, int width, int height) {
        try {
            InputStream is = Item.class.getResourceAsStream("/assets/images/sprites/" + filename);
            if (is != null) {
                return new Image(is, width, height, true, false);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static void initImages() {
        if (imagesInitialized) return;
        chestClosedImg = loadSprite("chest_closed.png", 16, 16);
        chestOpenedImg = loadSprite("chest_opened.png", 16, 16);
        chestGlowLolli = loadSprite("chest_glow_lolli.png", 16, 16);
        chestGlowClone = loadSprite("chest_glow_clone.png", 16, 16);
        imagesInitialized = true;
    }

    private void drawImg(GraphicsContext gc, Image img, double x, double y) {
        if (img != null) {
            gc.drawImage(img, x, y, size, size);
        } else {
            gc.setFill(Color.MAGENTA);
            gc.fillRect(x, y, size, size);
        }
    }

    // ================= LOGIC =================

    private boolean isCollected = false;
    private final ContentType contentType;

    public Item(double x, double y, ContentType contentType) {
        super(x, y, 16.0);
        this.contentType = contentType;
    }

    @Override
    public void update() {
    }

    public void collect() {
        this.isCollected = true;
    }

    @Override
    public void render(GraphicsContext gc) {
        if (isCollected) {
            drawImg(gc, chestOpenedImg, x, y);
            // Overlay content-specific glow
            if (contentType == ContentType.LOLLI) {
                gc.setGlobalAlpha(0.6);
                drawImg(gc, chestGlowLolli, x, y);
                gc.setGlobalAlpha(1.0);
            } else if (contentType == ContentType.CLONE_DECOY) {
                gc.setGlobalAlpha(0.6);
                drawImg(gc, chestGlowClone, x, y);
                gc.setGlobalAlpha(1.0);
            }
        } else {
            drawImg(gc, chestClosedImg, x, y);
        }
    }

    @Override
    public Rectangle2D getHitbox() {
        return new Rectangle2D(x, y, size, size);
    }

    public boolean isCollected() {
        return isCollected;
    }

    public boolean hasLolli() {
        return contentType == ContentType.LOLLI;
    }

    public ContentType getContentType() {
        return contentType;
    }
}