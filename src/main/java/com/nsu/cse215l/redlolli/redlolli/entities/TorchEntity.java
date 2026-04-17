package com.nsu.cse215l.redlolli.redlolli.entities;

/**
 * Animated torch that provides light on the map.
 * This entity has been fully DECOUPLED from JavaFX.
 */
public class TorchEntity extends Entity {

    private double animationTimer = 0;
    private int currentFrame = 1; // Frames 1-4 are lit, frame 0 is unlit
    private boolean isLit = true;
    
    private long lastUpdateTime = 0;
    private double timeDelta = 1.0;

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
            
        long now = System.nanoTime();
        if (lastUpdateTime == 0) lastUpdateTime = now;
        double dtSeconds = (now - lastUpdateTime) / 1_000_000_000.0;
        lastUpdateTime = now;
        timeDelta = dtSeconds * 60.0;

        animationTimer += timeDelta;
        if (animationTimer >= 8) { // Change frame every 8 ticks
            animationTimer = 0;
            currentFrame++;
            if (currentFrame > 4) {
                currentFrame = 1;
            }
        }
    }

    public double getSize() {
        return size;
    }

    public int getCurrentFrame() {
        return currentFrame;
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
