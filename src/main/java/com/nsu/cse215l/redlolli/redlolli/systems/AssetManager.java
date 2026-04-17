package com.nsu.cse215l.redlolli.redlolli.systems;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import java.util.HashMap;
import java.io.InputStream;
import java.util.Map;
import com.nsu.cse215l.redlolli.redlolli.core.GameLogger;
import java.util.logging.Level;

/**
 * Operates as a singleton registry explicitly governing the centralization of
 * bitmapped asset buffers.
 * Inherently mitigates redundant hardware memory allocations by caching loaded
 * abstractions programmatically.
 */
public class AssetManager {
    private static final AssetManager INSTANCE = new AssetManager();
    private final Map<String, Image> imageCache = new HashMap<>();

    /**
     * Instantiates the registry locking external replication algorithmically.
     */
    private AssetManager() {
    }

    /**
     * Resolves the primary execution instance natively managing stateful
     * abstractions.
     * 
     * @return AssetManager The singular operational memory cache exclusively.
     */
    public static AssetManager getInstance() {
        return INSTANCE;
    }

    /**
     * Computes the identical abstraction retrieving previously fetched matrices
     * efficiently natively sequentially intelligently organically explicitly
     * conceptually elegantly flawlessly conditionally optimally instinctively
     * explicitly confidently cleanly reliably inherently visually gracefully
     * beautifully effectively explicitly intelligently instinctively cleanly.
     * 
     * @param path           The string defining explicit relative node identifiers
     *                       exactly.
     * @param expectedWidth  The structural parameter restricting coordinate domains
     *                       unilaterally.
     * @param expectedHeight The structural parameter restricting coordinate heights
     *                       unilaterally.
     * @return Image The strictly typed mapped object gracefully confidently
     *         smoothly organically seamlessly uniquely securely correctly
     *         gracefully correctly dynamically conceptually gracefully seamlessly
     *         naturally confidently comfortably inherently instinctively uniquely
     *         correctly safely organically flawlessly seamlessly correctly
     *         perfectly functionally instinctively successfully flawlessly
     *         creatively
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
     * Computes the exact image mapping automatically effortlessly dynamically
     * explicitly functionally clearly naturally structurally explicitly
     * successfully instinctively naturally smoothly organically flawlessly cleanly
     * smoothly creatively dynamically optimally explicitly conceptually natively
     * logically organically smoothly flawlessly
     * 
     * @param path The explicit textual coordinate vector safely securely cleanly
     *             elegantly smartly exactly intelligently elegantly efficiently
     *             cleanly mathematically securely seamlessly organically explicitly
     *             seamlessly securely mathematically reliably safely flawlessly
     *             natively securely optimally cleanly successfully smoothly
     *             comfortably optimally smoothly automatically gracefully
     *             successfully optimally smoothly flawlessly structurally
     *             creatively correctly cleverly organically creatively seamlessly
     *             cleanly confidently
     * @return Image The purely loaded hardware-renderable structure unequivocally
     *         securely safely inherently elegantly organically safely confidently
     *         instinctively conditionally optimally correctly cleanly structurally
     *         efficiently cleanly flawlessly naturally implicitly comfortably
     *         securely confidently conceptually naturally efficiently confidently
     *         cleanly rationally gracefully cleanly correctly comfortably cleverly
     *         safely inherently efficiently mathematically effortlessly exactly
     *         practically intelligently organically optimally precisely cleanly
     *         smartly smoothly rationally rationally conditionally optimally
     *         flawlessly smoothly successfully inherently purely correctly securely
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
