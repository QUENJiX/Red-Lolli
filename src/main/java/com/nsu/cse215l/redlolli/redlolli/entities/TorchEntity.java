package com.nsu.cse215l.redlolli.redlolli.entities;

/**
 * A flickering torch on the wall that can be placed on the map.
 * Its animation is randomly shifted when created so that multiple torches 
 * sitting next to each other don't blink in perfect sync.
 */
public class TorchEntity extends Entity {

    private double animationTimer = 0;
    private int currentFrame = 1;
    private boolean isLit = true;

    private long lastUpdateTime = 0;
    private double timeDelta = 1.0;

    /**
     * Creates a torch at a specific place on the map and randomly mixes up its animation.
     * 
     * @param x The horizontal position of the torch.
     * @param y The vertical position of the torch.
     */
    public TorchEntity(double x, double y) {
        super(x, y, 40);

        // Start the animation at a slightly random time and frame 
        // so a hallway full of torches looks natural instead of weirdly identical.
        this.animationTimer = (int) (Math.random() * 10);
        this.currentFrame = 1 + (int) (Math.random() * 4);
    }

    /**
     * Ticks the torch's flickering animation forward smoothly over time.
     * If the torch has been turned off, it just skips this entirely.
     */
    @Override
    public void update() {
        if (!isLit)
            return;

        long now = System.nanoTime();
        if (lastUpdateTime == 0)
            lastUpdateTime = now;

        // Figure out how much time actually passed between frames
        double dtSeconds = (now - lastUpdateTime) / 1_000_000_000.0;
        lastUpdateTime = now;
        timeDelta = dtSeconds * 60.0;

        // Keep counting up until it's time to swap to the next animation frame
        animationTimer += timeDelta;
        if (animationTimer >= 8) {
            animationTimer = 0;
            currentFrame++;
            if (currentFrame > 4) {
                currentFrame = 1;
            }
        }
    }

    /**
     * Returns the size of the torch for drawing on the screen.
     * 
     * @return The 2D size of the torch.
     */
    public double getSize() {
        return size;
    }

    /**
     * Gets the current frame of the flickering animation.
     * 
     * @return The current animation step.
     */
    public int getCurrentFrame() {
        return currentFrame;
    }

    /**
     * Checks if the torch is currently producing light and flickering.
     * 
     * @return True if the torch is turned on.
     */
    public boolean isLit() {
        return isLit;
    }

    /**
     * Turns the torch on or off, immediately changing whether it animates or not.
     * 
     * @param lit Whether the torch should be lit.
     */
    public void setLit(boolean lit) {
        this.isLit = lit;
        if (!lit) {
            currentFrame = 0;
        } else if (currentFrame == 0) {
            currentFrame = 1;
        }
    }
}
