package com.nsu.cse215l.redlolli.redlolli.systems;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import java.util.HashMap;
import java.io.InputStream;
import java.util.Map;
import com.nsu.cse215l.redlolli.redlolli.core.GameLogger;
import java.util.logging.Level;

/**
 * A central cache for all the images in the game.
 * It makes sure we only load an image once so we don't accidentally
 * waste RAM loading the same monster sprite 50 times.
 */
public class AssetManager {
    private static final AssetManager INSTANCE = new AssetManager();
    private final Map<String, Image> imageCache = new HashMap<>();

    /**
     * Prevents other objects from making extra copies of the AssetManager!
     */
    private AssetManager() {
    }

    /**
     * Gets the one and only copy of the AssetManager.
     * 
     * @return The single AssetManager instance shared by the whole game.
     */
    public static AssetManager getInstance() {
        return INSTANCE;
    }

    /**
     * Fetches an image by its file path and forces it to a specific size.
     * If the image was already requested before, it serves it from memory.
     * 
     * @param path           The string telling the manager where to look (e.g. "/images/sprite.png").
     * @param expectedWidth  The width in pixels you want the image squashed or stretched to.
     * @param expectedHeight The height in pixels you want the image squashed or stretched to.
     * @return The freshly loaded or cached Image, or a tiny blank picture if loading failed.
     */
    public Image getSprite(String path, int expectedWidth, int expectedHeight) {
        if (imageCache.containsKey(path)) {
            return imageCache.get(path);
        }

        try {
            InputStream is = AssetManager.class.getResourceAsStream(path);
            if (is != null) {
                Image img = new Image(is, expectedWidth, expectedHeight, true, false);
                imageCache.put(path, img);
                return img;
            } else {
                GameLogger.getLogger().log(Level.WARNING, "Resource stream is null for: " + path);
            }
        } catch (Exception e) {
            GameLogger.getLogger().log(Level.SEVERE, "Failed loading sprite (sized) : " + path, e);
        }

        Image placeholder = new WritableImage(expectedWidth > 0 ? expectedWidth : 32,
                expectedHeight > 0 ? expectedHeight : 32);
        imageCache.put(path, placeholder);
        return placeholder;
    }

    /**
     * Quickly fetches an image in its natural, untampered size.
     * Uses the memory cache just like the resizing method to stay fast!
     * 
     * @param path The exact file path inside your resources folder (e.g. "/images/icon.png").
     * @return The standard loaded Image object, or a blank tiny square if loading fails.
     */
    public Image getSprite(String path) {
        if (imageCache.containsKey(path)) {
            return imageCache.get(path);
        }

        try {
            InputStream is = AssetManager.class.getResourceAsStream(path);
            if (is != null) {
                Image img = new Image(is);
                imageCache.put(path, img);
                return img;
            } else {
                GameLogger.getLogger().log(Level.WARNING, "Resource stream is null for: " + path);
            }
        } catch (Exception e) {
            GameLogger.getLogger().log(Level.SEVERE, "Failed loading sprite: " + path, e);
        }

        Image placeholder = new WritableImage(32, 32);
        imageCache.put(path, placeholder);
        return placeholder;
    }
}
