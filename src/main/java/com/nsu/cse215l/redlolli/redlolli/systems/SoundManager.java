package com.nsu.cse215l.redlolli.redlolli.systems;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

/**
 * A central audio controller for the game.
 * It's designed to safely play sound effects and music without crashing
 * the game if a sound file happens to be missing.
 */
public class SoundManager {

    /**
     * File path for the fast heartbeat sound effect.
     */
    public static final String HEARTBEAT_FAST = "/assets/audio/heartbeat_fast.wav";
    /**
     * File path for the jump-scare stinger effect.
     */
    public static final String STINGER_1 = "/assets/audio/stinger_1.wav";
    /**
     * File path for the game startup sound.
     */
    public static final String GAME_START = "/assets/audio/game_start.wav";
    /**
     * File path for the game over sound.
     */
    public static final String GAME_OVER = "/assets/audio/game_over.wav";
    /**
     * File path for the chest opening sound effect.
     */
    public static final String CHEST_OPEN = "/assets/audio/chest_open.wav";
    /**
     * File path for the player footstep sound.
     */
    public static final String FOOTSTEP = "/assets/audio/footstep.wav";
    /**
     * File path for the scary scream indicating the monster is near.
     */
    public static final String LUNA_SCREAM_NEARBY = "/assets/audio/luna_scream_nearby.wav";
    /**
     * File path for the creepy ambient background drone.
     */
    public static final String AMBIENT_DRONE = "/assets/audio/ambient_drone.wav";

    private MediaPlayer currentMusic;

    /**
     * Plays a brief sound effect once. Perfect for footsteps, screams, or jump scares.
     * 
     * @param resourcePath The file path to the sound effect you want to play.
     * @param volume       How loud the sound should be, from 0.0 (silent) to 1.0 (max).
     */
    public void playOneShot(String resourcePath, double volume) {
        try {
            URL url = getClass().getResource(resourcePath);
            if (url == null) {
                return;
            }

            AudioClip clip = new AudioClip(url.toExternalForm());
            clip.setVolume(Math.max(0, Math.min(1, volume)));
            clip.play();

        } catch (Exception e) {
            com.nsu.cse215l.redlolli.redlolli.core.GameLogger.getLogger()
                    .log(java.util.logging.Level.WARNING, "Failed to play sound: " + resourcePath, e);
        }
    }

    /**
     * Plays a looping background music track. If a track is already playing,
     * it will stop the old one before starting the new one.
     * 
     * @param resourcePath The file path to the music track you want to play.
     */
    public void playMusicIfPresent(String resourcePath) {
        stopMusic();

        try {
            URL url = getClass().getResource(resourcePath);
            if (url != null) {
                Media media = new Media(url.toExternalForm());
                currentMusic = new MediaPlayer(media);
                currentMusic.play();
            }
        } catch (Exception e) {
            com.nsu.cse215l.redlolli.redlolli.core.GameLogger.getLogger()
                    .log(java.util.logging.Level.WARNING, "Failed to play music: " + resourcePath, e);
        }
    }

    /**
     * Halts whatever background music track is currently playing.
     */
    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
        }
    }
}
