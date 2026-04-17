package com.nsu.cse215l.redlolli.redlolli.systems;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import java.util.HashMap;
import java.io.InputStream;
import java.util.Map;

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
            }
        } catch (Exception e) {
            System.err.println("AssetManager Error loading sprite '" + path + "': " + e.getMessage());
            e.printStackTrace();
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
            }
        } catch (Exception e) {
            System.err.println("AssetManager Error loading sprite '" + path + "': " + e.getMessage());
            e.printStackTrace();
        }
        // Fallback placeholder
        Image placeholder = new WritableImage(32, 32);
        imageCache.put(path, placeholder);
        return placeholder;
    }
}
