# Sound Effects Manifest: Project Pale Luna

This document outlines the audio assets and their specific trigger conditions for the current build.

---

## Background Music & Cutscenes
| File Name | Description | Trigger Condition |
| :--- | :--- | :--- |
| `intro_music.wav` | Atmospheric track for the opening cinematic. | Plays during the opening image sequence; stops on sequence end or user skip (ENTER). |
| `outro_music.wav` | Triumphant/Eerie victory theme. | Plays persistently during the final victory image cutscene. |

---

## UI & Game Events
* **`vhs_boot.wav`**: A short, snappy click/hum sound. Triggers the moment **"NEW GAME"** is selected to transition into the intro.
* **`game_start.wav`**: An eerie ambient tone. Plays the exact second the player **spawns into the maze** to establish the initial mood.
* **`game_over.wav`**: A terrifying, high-volume sound effect. Triggers upon entering the **"YOU DIED"** death screen.
* **`chest_open.wav`**: A creaky hinge or mechanical unlocking sound. Triggers whenever the player **collects a chest or item**.
* **`stinger_1.wav`**: A high-impact musical cue. Plays specifically when the player acquires the **"Red Lolli"** key item.

---

## Player Actions
* **`footstep.wav`**: Sound of movement across dirt/stone surfaces.
    * *Logic:* Triggers dynamically based on movement speed (Walking vs. Sprinting).

---

## Monster Alerts (Pale Luna)
> **Warning:** These sounds are tied to the AI state machine and proximity sensors.

* **`heartbeat_fast.wav`**: An immediate auditory alert. Triggers the moment Pale Luna transitions into her **"Hunting"** state (direct player lock-on).
* **`luna_scream_nearby.wav`**: A visceral, high-intensity scream. Triggers if Pale Luna is in a Hunting state and reaches **extreme proximity** to the player.
* **`whisper_1.wav`**: A legacy atmospheric sound. A subtle, creepy whisper that triggers during **random proximity events** to build tension.