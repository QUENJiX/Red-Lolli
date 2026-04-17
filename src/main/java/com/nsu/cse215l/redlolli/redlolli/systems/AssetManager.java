package com.nsu.cse215l.redlolli.redlolli.systems;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import java.util.HashMap;
import java.io.InputStream;
import java.util.Map;
import com.nsu.cse215l.redlolli.redlolli.core.GameLogger;
import java.util.logging.Level;

/**
 * Singleton responsible for centrally loading and caching Image assets.
 * Eliminates redundant file loading logic across Entities and minimizes memory footprint.
 */
public class AssetManager {
    private static final AssetManager INSTANCE = new AssetManager();
    private final Map<String, Image> imageCache = new HashMap<>();

    private AssetManager() {}

    public static AssetManager getInstance() {
        return INSTANCE;
    }

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
        
        // Fallback placeholder
        Image placeholder = new WritableImage(expectedWidth > 0 ? expectedWidth : 32, expectedHeight > 0 ? expectedHeight : 32);
        imageCache.put(path, placeholder);
        return placeholder;
    }

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
        
        // Fallback placeholder
        Image placeholder = new WritableImage(32, 32);
        imageCache.put(path, placeholder);
        return placeholder;
    }
}
