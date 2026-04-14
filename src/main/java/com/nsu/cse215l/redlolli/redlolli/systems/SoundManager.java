package com.nsu.cse215l.redlolli.redlolli.systems;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

/**
 * Manages all game audio with safe no-op behavior for missing assets.
 */
public class SoundManager {

    public static final String HEARTBEAT_FAST = "/assets/audio/heartbeat_fast.wav";
    public static final String STINGER_1 = "/assets/audio/stinger_1.wav";
    public static final String GAME_START = "/assets/audio/game_start.wav";
    public static final String GAME_OVER = "/assets/audio/game_over.wav";
    public static final String CHEST_OPEN = "/assets/audio/chest_open.wav";
    public static final String FOOTSTEP = "/assets/audio/footstep.wav";
    public static final String LUNA_SCREAM_NEARBY = "/assets/audio/luna_scream_nearby.wav";
    public static final String AMBIENT_DRONE = "/assets/audio/ambient_drone.wav";

    private MediaPlayer currentMusic;

    /** Plays a one-shot sound effect at the given volume (0.0-1.0). */
    public void playOneShot(String resourcePath, double volume) {
        try {
            URL url = getClass().getResource(resourcePath);
            if (url == null) {
                return;
            }

            AudioClip clip = new AudioClip(url.toExternalForm());
            clip.setVolume(Math.max(0, Math.min(1, volume)));
            clip.play();

        } catch (Exception ignored) {
        }
    }

    /** Plays a looping background music track, stopping any previous track. */
    public void playMusicIfPresent(String resourcePath) {
        stopMusic();

        try {
            URL url = getClass().getResource(resourcePath);
            if (url != null) {
                Media media = new Media(url.toExternalForm());
                currentMusic = new MediaPlayer(media);
                currentMusic.play();
            }
        } catch (Exception ignored) {
        }
    }

    /** Stops the current background music. */
    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
        }
    }
}
