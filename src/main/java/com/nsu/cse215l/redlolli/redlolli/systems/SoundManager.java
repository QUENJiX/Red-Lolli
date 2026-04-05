package com.nsu.cse215l.redlolli.redlolli.systems;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

/**
 * The SoundManager governs all audio interaction in the game using JavaFX Media tools.
 * 
 * Crucially, it incorporates "safe no-op" behavior. If a requested audio file is missing
 * or fails to load, it silently catches the exception rather than crashing the game.
 * This guarantees that absent audio assets do not break critical gameplay or logic loops.
 * 
 * Development History:
 * - Phase 3, Week 3, Day 16: Wrappers for FXGL/JavaFX Audio Services generated and integrated.
 */
public class SoundManager {

    // ==============================================================
    // RESOURCE PATH CONSTANTS
    // ==============================================================
    // Centralized repository of all expected sound hook locations.
    // If the file structure changes, these only need to be updated in this file.

    public static final String HEARTBEAT_FAST = "/assets/audio/heartbeat_fast.wav";
    public static final String WHISPER_1 = "/assets/audio/whisper_1.wav";
    public static final String STINGER_1 = "/assets/audio/stinger_1.wav";
    public static final String GAME_START = "/assets/audio/game_start.wav";
    public static final String GAME_OVER = "/assets/audio/game_over.wav";
    public static final String CHEST_OPEN = "/assets/audio/chest_open.wav";
    public static final String FOOTSTEP = "/assets/audio/footstep.wav";
    public static final String LUNA_SCREAM_NEARBY = "/assets/audio/luna_scream_nearby.wav";
    public static final String VHS_BOOT = "/assets/audio/vhs_boot.wav";

    /** Variable storing the currently looping background music track, if any. */
    private MediaPlayer currentMusic;

    // ==============================================================
    // AUDIO TRIGGER METHODS
    // ==============================================================

    /**
     * Triggers a momentary sound effect (like footsteps, door opens, stab noises).
     * Fire-and-forget: Used mostly for unpredictable dynamic actions.
     * 
     * @param resourcePath String location of the `.wav` file
     * @param volume       Volume float clamped between 0.0 (mute) and 1.0 (max)
     */
    public void playOneShot(String resourcePath, double volume) {
        try {
            // Attempt to load the file stream from the assembled JAR / resources package
            URL url = getClass().getResource(resourcePath);
            if (url == null) {
                return; // Early exit if asset string doesn't map to a real file
            }
            
            // Initialize and play the clip independently
            AudioClip clip = new AudioClip(url.toExternalForm());
            clip.setVolume(Math.max(0, Math.min(1, volume)));
            clip.play();
            
        } catch (Exception ignored) {
            // "Silent Failure Model"
            // We ignore exceptions because missing SFX shouldn't prevent players from playing.
        }
    }

    /**
     * Initializes and loops a background music track or ambient noise stream.
     * Automatically overwrites the previous track if one was already playing.
     * 
     * @param resourcePath String location of the media file
     */
    public void playMusicIfPresent(String resourcePath) {
        // Enforce one track at a time
        stopMusic();
        
        try {
            URL url = getClass().getResource(resourcePath);
            if (url != null) {
                // Instantiate the heaviest JavaFX audio handler for stream files
                Media media = new Media(url.toExternalForm());
                currentMusic = new MediaPlayer(media);
                currentMusic.play();
            }
        } catch (Exception ignored) {
            // "Silent Failure Model"
        }
    }

    /**
     * Hard-stops the current looping background ambient noise or music.
     * Required when transitioning menus or triggering sudden silent horror scenes.
     */
    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null; // Clean up so it can be garbage collected
        }
    }
}
