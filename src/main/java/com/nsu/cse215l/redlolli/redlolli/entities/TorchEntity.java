package com.nsu.cse215l.redlolli.redlolli.entities;


/**
 * Animated torch that provides light on the map.
 */
public class TorchEntity extends Entity {


    private long lastFrameTime = System.nanoTime();
    private int currentFrame = 1; // Frames 1-4 are lit, frame 0 is unlit
    private boolean isLit = true;

            public TorchEntity(double x, double y) {
        super(x, y, 40); // Size can be 40
        // Randomize starting frame so multiple torches aren't synced
        this.lastFrameTime = (int) (Math.random() * 10);
        this.currentFrame = 1 + (int) (Math.random() * 4);
    }

    @Override
    public void update() {
        if (!isLit)
            return;

        
        if (lastFrameTime >= 8) { // Change frame every 8 ticks
            lastFrameTime = 0;
            currentFrame++;
            if (currentFrame > 4) {
                currentFrame = 1;
            }
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

    public int getCurrentFrame() {
        return currentFrame;
    }
}

