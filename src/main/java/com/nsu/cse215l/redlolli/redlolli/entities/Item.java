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
        chestClosedImg = loadSprite("chest_closed.png", 32, 32);
        chestOpenedImg = loadSprite("chest_open.png", 32, 32);
        chestGlowLolli = loadSprite("chest_glow_lolli.png", 32, 32);
        chestGlowClone = loadSprite("chest_glow_clone.png", 32, 32);
        imagesInitialized = true;
    }

    /** Call this to force images to reload (e.g. after changing asset paths). */
    public static void resetImages() { imagesInitialized = false; }

    // Visual render size (32x32 centered on the 16x16 hitbox)
    private static final double RENDER_SIZE = 32.0;

    private void drawImg(GraphicsContext gc, Image img, double x, double y) {
        if (img != null) {
            gc.drawImage(img, x, y, RENDER_SIZE, RENDER_SIZE);
        } else {
            gc.setFill(isCollected ? Color.rgb(160, 82, 45) : Color.rgb(139, 69, 19));
            gc.fillRect(x, y, RENDER_SIZE, RENDER_SIZE);
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
        // Draw sprite centered on hitbox (hitbox 16x16, sprite 32x32)
        double offset = (RENDER_SIZE - size) / 2;
        if (isCollected) {
            drawImg(gc, chestOpenedImg, x - offset, y - offset);
            if (contentType == ContentType.LOLLI) {
                gc.setGlobalAlpha(0.6);
                drawImg(gc, chestGlowLolli, x - offset, y - offset);
                gc.setGlobalAlpha(1.0);
            } else if (contentType == ContentType.CLONE_DECOY) {
                gc.setGlobalAlpha(0.6);
                drawImg(gc, chestGlowClone, x - offset, y - offset);
                gc.setGlobalAlpha(1.0);
            }
        } else {
            drawImg(gc, chestClosedImg, x - offset, y - offset);
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