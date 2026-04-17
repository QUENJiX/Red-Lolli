package com.nsu.cse215l.redlolli.redlolli.entities;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.io.InputStream;

/**
 * Animated torch that provides light on the map.
 */
public class TorchEntity extends Entity {

    private static Image[] torchFrames;
    private static boolean imagesInitialized = false;

    private int animationTimer = 0;
    private int currentFrame = 1; // Frames 1-4 are lit, frame 0 is unlit
    private boolean isLit = true;

    private static Image loadSprite(String filename) {
        try {
            InputStream is = TorchEntity.class.getResourceAsStream("/assets/images/dungeon/wall/torches/" + filename);
            if (is != null) {
                // Resize to 40x40 to fit nicely on wall tiles
                return new Image(is, 40, 40, true, false);
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static void initImages() {
        if (imagesInitialized)
            return;
        torchFrames = new Image[5];
        for (int i = 0; i < 5; i++) {
            torchFrames[i] = loadSprite("torch_" + i + ".png");
        }
        imagesInitialized = true;
    }

    public TorchEntity(double x, double y) {
        super(x, y, 40); // Size can be 40
        // Randomize starting frame so multiple torches aren't synced
        this.animationTimer = (int) (Math.random() * 10);
        this.currentFrame = 1 + (int) (Math.random() * 4);
    }

    @Override
    public void update() {
        if (!isLit)
            return;

        animationTimer++;
        if (animationTimer >= 8) { // Change frame every 8 ticks
            animationTimer = 0;
            currentFrame++;
            if (currentFrame > 4) {
                currentFrame = 1;
            }
        }
    }

    @Override
    public void render(GraphicsContext gc) {
        Image img = isLit ? torchFrames[currentFrame] : torchFrames[0];
        if (img != null) {
            gc.drawImage(img, x, y, size, size);
        }
    }

    public boolean isLit() {
        return isLit;
    }

    public void setLit(boolean lit) {
        this.isLit = lit;
        if (!lit) {
            currentFrame = 0;
        } else if (currentFrame == 0) {
            currentFrame = 1;
        }
    }
}
