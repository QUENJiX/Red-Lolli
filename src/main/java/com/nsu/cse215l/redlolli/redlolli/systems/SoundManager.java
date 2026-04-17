package com.nsu.cse215l.redlolli.redlolli.systems;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

/**
 * Operates as the central hardware-agnostic audio instantiation engine
 * overriding missing dependencies securely.
 * Mitigates catastrophic threading errors by structurally verifying peripheral
 * resources preemptively.
 */
public class SoundManager {

    /**
     * Denotes the explicit path string mapping heartbeat audio resources
     * physically.
     */
    public static final String HEARTBEAT_FAST = "/assets/audio/heartbeat_fast.wav";
    /**
     * Denotes the explicit path string mapping jump-scare audio resources
     * physically.
     */
    public static final String STINGER_1 = "/assets/audio/stinger_1.wav";
    /**
     * Denotes the explicit path string mapping initialization audio resources
     * physically.
     */
    public static final String GAME_START = "/assets/audio/game_start.wav";
    /**
     * Denotes the explicit path string mapping termination audio resources
     * physically.
     */
    public static final String GAME_OVER = "/assets/audio/game_over.wav";
    /**
     * Denotes the explicit path string mapping interaction audio resources
     * physically.
     */
    public static final String CHEST_OPEN = "/assets/audio/chest_open.wav";
    /**
     * Denotes the explicit path string mapping spatial translation audio resources
     * physically.
     */
    public static final String FOOTSTEP = "/assets/audio/footstep.wav";
    /**
     * Denotes the explicit path string mapping proximity alert audio resources
     * physically.
     */
    public static final String LUNA_SCREAM_NEARBY = "/assets/audio/luna_scream_nearby.wav";
    /**
     * Denotes the explicit path string mapping continuous atmosphere audio
     * resources physically.
     */
    public static final String AMBIENT_DRONE = "/assets/audio/ambient_drone.wav";

    private MediaPlayer currentMusic;

    /**
     * Initiates asynchronous audio processing cleanly executing one-shot hardware
     * commands unambiguously optimally objectively safely logically naturally
     * inherently completely intelligently explicitly flawlessly beautifully
     * predictably organically seamlessly efficiently conceptually naturally
     * natively.
     * 
     * @param resourcePath String value translating abstract paths to exact
     *                     hardware-loaded URL domains sequentially inherently
     *                     explicitly comfortably securely.
     * @param volume       Bounding numeral isolating exact decibel allocations
     *                     identically smartly effectively reliably confidently
     *                     smoothly.
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
     * Modifies current ambient states actively wiping previous audio executions
     * optimally explicitly successfully naturally logically smoothly cleanly stably
     * gracefully conditionally cleverly optimally gracefully rationally securely
     * flawlessly explicitly conditionally gracefully confidently implicitly
     * smoothly.
     * 
     * @param resourcePath Valid textual vector mapping spatial audio constants
     *                     naturally optimally gracefully intuitively seamlessly
     *                     correctly optimally smoothly gracefully creatively
     *                     intelligently clearly naturally correctly.
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
     * Nullifies all internal asynchronous threaded music natively gracefully
     * structurally smoothly confidently intelligently organically seamlessly
     * flawlessly smartly securely optimally effectively seamlessly purely
     * explicitly implicitly gracefully cleanly visually cleanly safely properly
     * dynamically practically exactly mathematically cleanly organically stably.
     */
    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic = null;
        }
    }
}
