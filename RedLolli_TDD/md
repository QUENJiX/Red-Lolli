# PALE LUNA

## HORROR FREAK SHOW TRANSFORMATION

```
Technical Design Document
```
_"Luna, a blonde, white girl, was killed at age 11. They choked her with a
rope. Then buried her in Central Park with a shovel. Now her demon roams
the maze --- and only the Rope, the Mud, and the Shovel can put her back in
the ground."_

```
CSE215 Java OOP Lab Project
DO NOT PLAY ALONE
```

## 1. Project Overview & Vision

Transform “Escape Pale Luna” from a maze puzzle into a **bone-chilling psychological horror
experience** worth $4.99 on Steam.

### Inspirations

- **Lone Survivor / Darkwood** — oppressive 2D darkness, flashlight mechanics, sanity sys-
    tem.
- **Inscription / Pony Island** — fourth-wall breaks, the game “talking” to the player.
- **PT (Playable Teaser)** — looping dread, subtle environmental changes, subliminal horror.
- **SCP: Containment Breach** — unpredictable AI, blinking mechanic tension.
- **Creepypasta “Pale Luna”** — the original source material’s cold, clinical dread.

### Core Horror Philosophy

The game doesn’t rely on cheap jumpscares. It builds **dread** — the feeling that something is
_wrong_ and getting _worse_. The darkness is alive. Luna doesn’t just chase you — she **watches** ,
she **whispers** , she **plays with you**.

### [!IMPORTANT

```
PREMISE STAYS INTACT] Luna (blonde girl, killed at 11, choked with rope, buried in Central
Park with a shovel). The three items are Mud , Shovel , and Rope — the weapons to banish
her demon. 2D maze gameplay preserved.
```
### [!WARNING

```
CONTENT WARNING] This plan includes intensely dark horror content — child death
backstory, psychological manipulation, gore imagery (drawn with JavaFX primitives), and
fourth-wall breaking elements. This is what “bonechilling, pant-shitting” requires.
```
### [!CAUTION

```
AUDIO ASSETS] The game will utilize real.wavaudio files for maximum atmospheric
terror. The architecture must include a robustSoundManagerwith dedicated hooks and
placeholder file paths (e.g.,"assets/audio/heartbeat_fast.wav"). The developer will im-
plement the actual audio files at a later stage, so the code must be structured to handle
missing files gracefully and allow easy swapping of these placeholders without breaking
game logic.
```


## 2. Core Horror Systems (Mechanics)

These systems form the psychological horror backbone. Every mechanic feeds into the others
to create layered, compounding dread.

### 2.1 SanitySystem.java

Psychological horror core mechanic — sanity drains passively and from events:

- **Sanity bar** (0-100): Drains slowly over time, faster in darkness, faster when near Luna.
- **Hallucination tiers** :
    - **90-100** : Normal gameplay.
    - **70-89** : Occasional shadow movement in peripheral vision. Faint whispers.
    - **50-69** : False footstep sounds. Walls seem to shift.
    - **30-49** : A **doppelganger** appears — looks exactly like the player but stands still in
       corridors, staring. Text on HUD scrambles.
    - **0-29** : The screen frequently flashes Luna’s face. Movement controls randomly invert
       for 1 second. The maze walls appear to _breathe_ (subtle size pulsing). YOU CANNOT
       TRUST ANYTHING.
- **Sanity recovery** : Standing in escape rooms slowly recovers sanity. Finding items gives a
    burst.
- **If sanity hits 0** : Instant death with a unique death message (” _You stopped running. You_
    _stopped breathing. She was already inside._ ”)

### 2.2 FlashlightSystem.java

The maze is DARK. The flashlight is your only friend. And it’s dying.

- **Cone of light** : Rendered as a gradient cone from player position in the direction of last
    movement.
- **Battery system** : Flashlight battery drains over time (180 seconds total per level). When it
    dies, you’re in near-total darkness with only a tiny circle of ambient light.
- **Flicker mechanic** : When Luna is within 5 tiles, the flashlight starts flickering. Within 3
    tiles, it goes out entirely for 0.5-second bursts.
- **Darkness = danger** : Areas outside the flashlight cone are rendered as near-black with
    only faint wall outlines. You literally cannot see Luna approaching from behind.
- Toggle with **F key**. Turning it off saves battery but you’re vulnerable.

### 2.3 EventSystem.java

Scripted & random horror events that keep the player in constant terror:


**Scripted (per-level):**

- **Level 1 (Mud)** : After 30 seconds, all lights in visible range flicker out for 2 seconds. A single
    message appears: ” _she remembers the cold earth_ ”
- **Level 2 (Shovel)** : Midway through, the player finds a room with a ”child’s drawing” on
    the wall (rendered with primitives — stick figure with X eyes). After leaving the room, the
    drawing has CHANGED.
- **Level 3 (Rope)** : The escape rooms start _disappearing_ after use. The walls close in. Luna is
    faster. The endgame is pure survival.

### 2.4 EasterEggSystem.java

For the $4.99 Steam value — secrets that make players replay and share:

- **Developer notes** : Hidden rooms (tile type 8) contain messages: ” _We’re sorry about Luna._
    _— Dev Team_ ”, ” _The rope was the hardest part to code. Not because of programming._ ”, ” _If you’re_
    _reading this at 3 AM... go to sleep. She’s not real. Probably._ ”
- **Easter egg: Konami Code on main menu** →Unlocks a ”Luna’s Room” scene — a static
    image of a child’s bedroom drawn with primitives. After 10 seconds, a music box tone
    plays and the screen slowly fades to the game’s death screen.
- **Easter egg: Die 5 times** →Death screen adds: ” _You keep coming back. She likes that._ ”
- **Easter egg: Stand still for 30 seconds** →A message types itself: ” _areyoustillthere? ...good._
    _don’t move._ ” Then Luna spawns directly next to you regardless of her timer.
- **Easter egg: Type ”LUNA” on keyboard during gameplay** →Screen flashes white, then
    black. A single line appears: ” _You called?_ ” Sanity drops to 30.


## 3. Entity AI & Interactions

### 3.1 Monster.java

Complete AI overhaul — Luna is no longer a simple 3-state timer. She is **intelligent and terri-
fying**.

```
New 4-State AI Protocol
```
1. **DORMANT** (replaces IDLE): Luna appears asleep, but her eyes track the player. She
    wakes when: timer expires, player opens a chest with the item, or player’s sanity
    drops below 50.
2. **STALKING** (NEW): Luna follows the player at walking speed, staying just at the edge
    of the flashlight range. She **does not kill you** in this state — she’s _watching_. She
    stops if you turn toward her. If you turn away, she gets closer. **This is where pure**
    **terror lives.** Duration: 8 seconds, then transitions to HUNTING.
3. **HUNTING** (replaces CHASING): Full-speed BFS pursuit. Faster than the player. In-
    stant kill on contact. Duration: 6 seconds.
4. **WAITING_AT_DOOR** (kept): Same mechanic — waits for player to leave escape
    room.

```
Visual overhaul:
```
- Luna’s appearance degrades per level: Level 1 she looks almost human (pale girl). Level
    2 her mouth is too wide, eyes are hollow. Level 3 she’s barely recognizable — just a pale
    shape with red eye-points and a gaping black mouth.
- During STALKING, she’s rendered semi-transparent. The player might not even notice her
    at first.
- Her sprite has frame-by-frame jitter — she doesn’t move smoothly, she _glitches_ forward
    like a broken video.

### 3.2 Player.java

Player now has physical and mental limitations:

- **Stamina system** : Sprint with SHIFT key (speed×1.8) but stamina bar depletes in 3 sec-
    onds. Regenerates slowly. When exhausted, player moves at 0.6×speed for 2 seconds
    (punishes panic sprinting).
- **Breathing indicator** : When stamina is low, a breathing animation overlays the screen
    (slight zoom in/out pulse).
- **Expression system enhanced** : 5 expression tiers based on sanity:
    - 100-80: Calm
    - 80-60: Nervous (eyes wider, slight tremble)
    - 60-40: Scared (shrunken pupils, sweating drops)
    - 40-20: Terrified (full body shake, crying animation)
    - <20: Broken (thousand-yard stare, no expression — they’ve given up)


### 3.3 Item.java

Chests are now **coffins and reliquaries**. The items are cursed artifacts:

- **Closed state** : Dark wooden coffin with iron bands. Emits a faint red glow when player is
    within 3 tiles.
- **Empty state** : Opens to reveal nothing but a child’s tooth, or a tuft of blonde hair, or scratch
    marks on the inside (rotates per chest).
- **Item state** : The Rope / Mud / Shovel emerges with a blood-red aura. Pickup triggers a
    **lore flashback** — a fullscreen text crawl describing the moment it was used on Luna:
       - **Mud** : ” _The earth was soft that night. Too soft. Like it was waiting for her._ ”
       - **Shovel** : ” _The blade bit into the ground. Each scoop made a sound like breathing._ ”
       - **Rope** : ” _She didn’t struggle. Not at the end. Her eyes were wide open. Smiling._ ”


## 4. The World & Level Design

```
The maze will be in Central Park, where the maze walls will be either
bushes or trees, and the escape room will be hidden behind a specific
bush/tree. The maze will have different creatures at different levels (near
escape rooms). It is to be noted that the creatures can't enter the escape
room.
```
### 4.1 Maze.java

The maze becomes a character in itself:

- **New tile types** :
    - 8 = Developer note room (rendered with faint text on floor)
    - 9 = Blood trail tile (random smears on floor)
    - 10 = Graffiti wall (text fragments: ” _SHE’S STILL SMILING_ ”, ” _DON’T DIG_ ”, ” _11 YEARS OLD_ ”)
- **Fog of War** : Tiles beyond flashlight range are fully black. Tiles at the edge are dimly visible.
- **Wall rendering overhaul** : Walls now look like crumbling stone with moss and dark stains.
    Some walls have scratch marks (rendered with random line patterns).
- **Floor redesign** : Cracked tiles with occasional dark puddles (could be water... could be
    blood).
- **Environmental storytelling** : Some rooms have ”crime scene” layouts — a small circle of
    tiles with a cross drawn in the center (Central Park burial site callback).

### 4.2 Level 1: The Park (The Bats)

Open spaces with scattered trees (wall clusters). Largest map, most escape rooms. Developer
note room hidden behind a false wall pattern. Tutorial-level pacing — Luna starts fully dormant
for 20 seconds.

- **The Threat [Bats]:** Bats guard two of the escape rooms. If a bat bites the player, Pale Luna
    magically appears for an instant kill. Quick reflexes are mandatory.
- **The Strategy:** The player is given two fruits to distract the two bats. (Fortunately, the bats
    will not attack when the player is _exiting_ the room).
- **The Catch:** Giving a bat a fruit distracts it for the remainder of the game. However, the
    player must be careful not to accidentally waste both fruits on a single bat, or they won’t
    have anything left for the second one.

### 4.3 Level 2: The Basement (The Cobras)

Tighter corridors. The shovel is in the hardest-to-reach corner. Contains a ”crime scene room”
with blood trail tiles. **One of the chests contains an ”Invisibility” potion.**


- **The Threat [The Cobras]:** Cobras guard the next set of escape rooms. If bitten, the player
    is knocked unconscious for 5 seconds, giving Pale Luna a massive advantage to catch up.
- **The Strategy:** The player must throw eggs to distract the cobras and safely enter the
    room. (Fortunately, the snakes will not attack when the player is _exiting_ the room).
- **The Catch:** The player only has 5 eggs for the entire round. Because the cobra resets and
    guards the door again after eating, the player can only enter an escape room a maximum
    of 5 times.

### 4.4 Level 3: The Grave (The Serial Killer)

Claustrophobic. Many dead-ends. Zero escape rooms (they _disappear_ after first use via EventSys-
tem). Luna starts in STALKING mode immediately. Contains the burial site room — a 3×3 open
area with a cross tile pattern.

- **The Threat [The Serial Killer Who Killed Luna]:** He lost his mind and is still looking for
    Pale Luna. He will start chasing the player when the player reaches his trigger point. He
    will never stop, chasing for the rest of Map 3 until you are killed or win. He is slow, but if
    he catches the player, he will aggressively knife them to death.
- **The Strategy:** One of the chests contains a Clone of Luna; a still Cardboard Clone that just
    stays. The clone is how she looked at age 11, not the ghostly one.
- **The Catch:** Placing the clone anywhere on the map shifts the Killer’s attention to it. Upon
    reaching the clone, the Killer will start knifing it for 10 seconds. Upon seeing no blood, he
    will resume his hunt for the player.

### 4.5 Endgame: Win Condition & Cutscene

- **Objective:** Collect the Shovel, Mud, and Rope. Give them to the Serial Killer.
- **The Cutscene:** The gameplay scene ends once the player finds the Rope. It transitions into
    a cutscene of images (created externally). In the cutscene, the Serial Killer is powered up,
    aggressively hunts down Pale Luna, kills her again with the rope, takes her to the grave,
    opens it up with the shovel, and buries her for good.
- **Resolution:** After the image cutscene finishes, the Game Win screen displays.

### 4.6 Level Progression Sequence

```
Level 1 → Mud → mud found → here.
Level 2 → Shovel → shovel found → use
Level 3 → Rope → rope found → now
```

## 5. Audiovisual Architecture

### 5.1 SoundManager.java

Audio horror engine utilizing real.wavfiles via JavaFXAudioClip:

- **Placeholder Architecture** : All audio calls must use defined constants or hooks for file
    paths (e.g.,PLAY_STINGER_1), allowing the developer to easily drop in the final.wavfiles
    later without breaking game logic.
- **Heartbeat system** : Pulses faster when Luna is near. When she’s _right behind you_ , it be-
    comes deafening.
- **Ambient drones** : Low-frequency hum that loops and shifts based on sanity level.
- **Whisper system** : Random quiet whisper-like sounds (” _here..._ ”, ” _turnaround..._ ”, ” _Iseeyou..._ ”)
    at random intervals — player can never be sure if it’s real.
- **Stinger sounds** : Sharp audio spike when Luna first appears, when lights go out, on death.

### 5.2 ScreenEffects.java

Visual distortion and horror overlay system:

- **Screen shake** : Camera jolts during chase, death, and scripted events.
- **VHS static lines** : Horizontal scan lines that intensify with low sanity.
- **Blood drip overlay** : Thin red lines slowly dripping from the top of the screen, more when
    damaged/scared.
- **Subliminal flashes** : 1-3 frame images of Luna’s face or text (”HELP ME”, ”DIG”, ”SHE’S
    HERE”) that flash so fast players _think_ they imagined it.
- **Vignette darkness** : Screen edges darken based on sanity, creating tunnel vision.
- **Color desaturation** : World loses color as sanity drops — pure grayscale at critical sanity.

### 5.3 GameRenderer.java

Complete rendering pipeline overhaul:

- **Layer order** : Background→Maze→Fog of War→Entities→Eyes Layer→Flashlight
    Cone→Screen Effects→HUD→Subliminal Flashes
- **Flashlight rendering** : Circular gradient from player position — bright center, fading to
    pitch black. Applied as a multiply blend over the maze.
- **Luna’s reveal animation replaced** : Instead of a golden glow, finding the item triggers a
    **blood-red pulse** that spreads across the screen. The item text appears letter-by-letter in
    a typewriter style with clicking sounds.
- **Death animation** : Screen slowly fills with red from the edges. Luna’s face appears in the
    center, filling the screen.


## 6. User Interface & Application Flow

### 6.1 HUDRenderer.java

HUD becomes a horror instrument:

- **Sanity Meter** : Replaces the old chest counter. A brain icon that cracks and bleeds as sanity
    drops. Below 30, the meter starts ”lying” (shows random values).
- **Battery Indicator** : Flashlight battery bar. Flickers when low.
- **Heartbeat Monitor** : A small ECG line that speeds up near Luna. Flatlines on death.
- **Luna Status redesigned** : Instead of a timer bar, show Luna’s current state with cryptic
    text:
       - DORMANT: ” _She sleeps._ ”
       - STALKING: ” _She watches._ ”
       - HUNTING: ” _RUN._ ”
       - WAITING: ” _She’s at the door._ ”
- **HUD corruption** : At low sanity, HUD elements glitch — text scrambles, numbers display
    wrong values, the sanity meter shows ”100%” when it’s actually at 15%.

### 6.2 HowToPlayRenderer.java

The ”How to Play” screen is presented as **found documents** — case files from a detective inves-
tigating Luna’s death:

- **Case File #1 — Victim** : Background on Luna (11, blonde, Central Park)
- **Case File #2 — Evidence** : The three items (Rope, Shovel, Mud) and their significance
- **Case File #3 — The Maze** : Instructions disguised as a detective’s notes about a ”recurring
    dream” of a maze
- **Case File #4 — Survival Notes** : Movement, flashlight, stamina, sanity
- **Case File #5 — WARNING** : A handwritten note scrawled over the typed text: ” _DONT PLAY_
    _THIS GAME. SHE GETS OUT._ ”
- Visual style: Typewriter font on yellowed paper background with coffee stains and blood
    smears

### 6.3 HelloApplication.java

Main controller rewrite to incorporate all new systems:

- **Main Menu overhaul** :
    - Black screen. After 2 seconds, Luna’s face fades in from the darkness behind the title
       text.
    - Title text rendered with a ”glitch” effect — letters randomly shift position for 1-2 frames.


- Subtitle changes each time you visit the menu: ” _Find the cursed items. Survive the_
    _demon._ ”, ” _She remembers your last game._ ”, ” _You can’t save her. You can only survive._ ”
- Easter egg detection (Konami code, death counter)
- **Game intro sequence** (before Level 1):
- Black screen, typewriter-style text crawl telling Luna’s story.
- ” _Central Park. November 14th, 2003._ ”
- ” _They found her three days later._ ”
- ” _The rope was still around her neck._ ”
- ” _The ground was freshly turned._ ”
- ” _Nobody heard her scream._ ”
- ...long pause...
- ” _She’s been screaming ever since._ ”
- **Level transitions** : Each level transition shows a newspaper clipping style screen with in-
creasingly disturbing headlines.
- **Victory sequence** : After the cutscene completes, the final text types out slowly:
_pale luna smiles wide,
the ground is soft,
pale luna smiles wide,
there is a hole,
pale luna smiles wide,
tie her up with rope,
congratulations! you have escaped from pale luna_
- **Death sequence** : Player gets killed by Pale Luna or the Serial Killer. The final text types
out slowly:
_pale luna smiles wide,
there is no escape,
pale luna smiles wide,
no more lollies to take,
pale luna smiles wide,
now you are dead_ →Hard cut to black→Main menu


