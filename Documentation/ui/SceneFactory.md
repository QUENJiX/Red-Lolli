# SceneFactory.java - Complete Structural Analysis

## 1. The "Elevator Pitch" (High-Level Overview)

**SceneFactory** is the **menu and scene construction system**—it builds all non-gameplay screens (main menu, death screen, victory screen, cutscene screens). It creates JavaFX Scenes with styled text, buttons, background images, and poetry/flavor text that reinforce the game's dark aesthetic. Think of it as the **narrative presentation layer**—while GameRenderer and HUDRenderer show gameplay, SceneFactory shows story moments and meta-game screens. It generates scenes dynamically based on game state (death message, victory stats, level progression).

---

## 2. Core Computer Science Concepts

### **Design Patterns & Techniques Used:**
- **Factory Pattern:** Creates fully-formed Scene objects on demand
- **Builder Pattern:** Sequential method calls compose complex layouts
- **Data-to-Presentation Mapping:** Game stats (deaths, time, lollies) transformed into display text
- **JavaFX Node Hierarchy:** VBox/HBox containers compose layouts
- **Image-Backed UI:** Background images set mood and visual coherence

### **Why These Concepts Matter:**
- **Factory:** Scenes are complex; factory encapsulates construction
- **Builder:** Sequential layout construction is more readable than flat lists
- **Data Mapping:** Same stats displayed differently for death vs. victory (same data, different narrative)
- **JavaFX API:** Standard Java UI framework enables cross-platform deployment

---

## 3. Deep Dive: Variables and State

### **Screen Constants:**

| Field | Type | Value | Purpose |
|-------|------|-------|---------|
| `WIDTH` | `static int` | 880 | Screen width in pixels |
| `HEIGHT` | `static int` | 730 | Screen height in pixels |

### **Background Images (Cached):**

| Field | Type | Purpose |
|-------|------|---------|
| `menuBackgroundImg` | `static Image` | Main menu background |
| `itemBgImg[3]` | `static Image[3]` | Level-specific objective reveal background |
| `deathBgImg` | `static Image` | Death screen background (ominous) |
| `victoryBgImg` | `static Image` | Victory screen background (mysterious escape) |

### **Initialization Tracking:**

| Field | Type | Purpose |
|-------|------|---------|
| `uiImagesInitialized` | `static boolean` | Have UI images been loaded? |

---

## 4. Deep Dive: Methods and Logic (Step-by-Step)

### **Method 1: `initUIImages()` [Image Loading]**

**The Goal:**
Load background images from classpath, called once.

**How it Works (Layman's Terms):**
```
if (already initialized):
    return
else:
    Load menuBackgroundImg
    Load itemBgImg[0-2] (level-specific)
    Load deathBgImg
    Load victoryBgImg
    Mark initialized
```

---

### **Method 2: `tryLoadImage()` [Image Loading Helper]**

**The Goal:**
Safely load image from path, returning null if not found.

**How it Works (Layman's Terms):**
```
try:
    Get resource URL from classpath
    if (URL not null):
        Create Image from URL
        return Image
    else:
        return null
catch:
    return null
```

**Why Try-Catch?** UI should survive missing images; graceful degradation.

---

### **Method 3: `getMenuBackgroundImg()` / `getMenuTitleText()` / `getMenuSubtitleText()` [Menu Queries]**

**The Goal:**
Return menu component data.

**How it Works (Layman's Terms):**
```
Return cached background image
Return styled "RED LOLLI" title text (72pt red serif)
Return styled subtitle text with mixture of gray and red
```

---

### **Method 4: `animateFadeIn()` [Animation Helper]**

**The Goal:**
Animate a UI node fading in from transparent to opaque.

**How it Works (Layman's Terms):**
```
Set node opacity to 0
Create FadeTransition:
    Duration: durationSeconds
    Start value: 0
    End value: 1
Play transition
```

**Used for:** Poetry text fading in dramatically.

---

### **Method 5: `createDeathScene()` [Death Screen]**

**The Goal:**
Create death/game-over screen showing death message and stats.

**How it Works (Layman's Terms):**

#### **Part 1: Create Root Container**
```
StackPane (stacked layout)
Set background to black
```

#### **Part 2: Add Background Image**
```
if (deathBgImg exists):
    Create ImageView from deathBgImg
    Scale to full screen
    Set opacity to 0.4 (faint, not distracting)
    Add to root
```

#### **Part 3: Create Main Layout Container**
```
VBox with spacing 10
Translate up slightly (-10 pixels)
```

#### **Part 4: Add Title and Poem**
```
Draw "YOU DIED" title (64pt red serif)

Generate poem based on death cause:
    if (Luna or Serial Killer caused death):
        Poem: "pale luna smiles wide, / there is no escape, / ..."
        Fade-in animation (3 seconds)
    else (guard caused death):
        Poem: "She found you in the dark. / Now your soul is hers to keep. / ..."
        (No animation)
```

**Why Different Poems?** Narrative reinforcement—different antagonists have different flavor.

#### **Part 5: Add Death Message and Feedback**
```
Draw activeDeathMessage (e.g., "She found your pulse...")
Draw taunt if deathCount >= 5:
    "You keep coming back. She likes that."
```

#### **Part 6: Add Statistics Box**
```
Create stats display showing:
    Time played (MM:SS)
    Death count
    Sanity percentage
    Lollies collected
    Secrets found
```

#### **Part 7: Add Buttons**
```
Create restart button (icon overlay on button image)
Create main menu button (icon overlay on button image)
Set button callbacks (onRestart, onMainMenu)
```

---

### **Method 6: `createVictoryScene()` [Victory Screen]**

**The Goal:**
Create victory/escape scene showing completion stats and narrative closure.

**How it Works (Layman's Terms):**

#### **Similar Structure to Death Scene:**

#### **Part 1: Root and Background**
```
StackPane with black background
VictoryBgImg (0.4 alpha) as background
```

#### **Part 2: Title and Poem**
```
Title: "YOU ESCAPED" (64pt red serif)
Poem: "pale luna smiles wide, / the ground is soft, / ... / tie her up with rope, / congratulations! you have escaped from pale luna"
Fade-in animation (3 seconds)
```

**Why Rope Joke?** Meta narrative—players used rope to tie Luna up.

#### **Part 3: Statistics**
```
Draw same stats box (time, deaths, sanity, lollies, secrets)
```

#### **Part 4: Button**
```
Main menu button only (no restart for victory)
```

---

### **Method 7: `createItemFoundScene()` [Objective Reveal]**

**The Goal:**
Create interstitial scene revealing collected objective before next level.

**How it Works (Layman's Terms):**

#### **Part 1: Select Background**
```
Map level to background:
    Level 1 → itemBgImg[0]
    Level 2 → itemBgImg[1]
    Level 3 → itemBgImg[2]
```

#### **Part 2: Create Layout**
```
VBox with spacing 25
```

#### **Part 3: Add Flavor Text**
```
Draw "Pale Luna smiles wide..." (36pt red)
```

#### **Part 4: Add Objective Name**
```
Level-specific item name (72pt white serif):
    Level 1: "The Mud"
    Level 2: "The Shovel"
    Level 3: "The Rope"
```

#### **Part 5: Add Narrative Description**
```
Level-specific dark poetry (24pt gray):
    Level 1: "\"The earth was soft that night. Too soft. Like it was waiting for her.\""
    Level 2: "\"The blade bit into the ground. Each scoop made a sound like breathing.\""
    Level 3: "\"She didn't struggle. Not at the end. Her eyes were wide open. Smiling.\""
```

**Why Narrative?** Each objective collected is a dark story beat.

#### **Part 6: Add Continuation Button**
```
Level-specific button text:
    Levels 1-2: "NEXT LEVEL"
    Level 3: "CONTINUE" (to victory screen)
```

---

### **Method 8: `styledText()` [Helper - Text Styling]**

**The Goal:**
Create styled Text node with font, size, and color.

**How it Works (Layman's Terms):**
```
Create Text object with content
Set Font (family, size)
Set fill color
return Text
```

---

### **Method 9: `newBlackVBox()` [Helper - Container Creation]**

**The Goal:**
Create VBox with standard dark styling.

**How it Works (Layman's Terms):**
```
Create VBox with parameter spacing
Center alignment
Transparent background
return VBox
```

---

### **Method 10: `createStatsBox()` [Helper - Statistics Display]**

**The Goal:**
Create styled statistics display box showing game metrics.

**How it Works (Layman's Terms):**

#### **Part 1: Create Container**
```
VBox with 5pt spacing
Center aligned
Dark red-tinted background (RGBA 20,5,5, 0.7 alpha)
Red border (2pt, #551111)
Padding 15pt
Max width 350pt
```

#### **Part 2: Calculate and Format Time**
```
Convert timeSec to MM:SS format
```

#### **Part 3: Add Statistics**
```
Time: MM:SS (gray text)
Deaths: <count> (gray text)
Mind Intact: <sanity>% (red if < 30, gray otherwise)
Lollies: <collected>/<total> (gold if > 0, gray otherwise)
Secrets: <found>/<total> (gray text)
```

---

### **Method 11: `createIconButton()` [Helper - Custom Button]**

**The Goal:**
Create styled button with icon overlay on background image.

**How it Works (Layman's Terms):**

#### **Part 1: Create Button**
```
Button with transparent background
```

#### **Part 2: Create Graphic Layers**
```
StackPane container
Load background image (128×128)
Load icon image (40×40)
Stack: background as bottom layer, icon as top layer
```

#### **Part 3: Set Visual States**
```
On mouse pressed: swap background to pressedImage
On mouse released: swap back to normalImage
```

**Why Dual Images?** Provides visual feedback that button is interactive.

---

## 5. Deep Dive: Model-View-Controller (MVC) Pattern

**Where SceneFactory Fits in MVC:**

| MVC Layer | SceneFactory's Role |
|-----------|---|
| **Model** | ❌ No (doesn't store game state) |
| **View** | ✅ **YES** Creates view scenes for non-gameplay screens |
| **Controller** | ❌ No |

**Scene Transitions:**
```
HelloApplication:
    ├─ Game logic determines event (death/victory/objective found)
    ├─ Calls sceneFactory.createDeathScene(...)
    │   ├─ Converts game state to display data
    │   ├─ Creates styled JavaFX Scene
    │   └─ Returns Scene
    │
    ├─ Scene transitions via Stage.setScene()
    └─ User interacts with button
        └─ Button callback handled by HelloApplication
```

---

## 6. Lab Final Presentation Arsenal

Here are 3 professional talking points for your lab presentation:

• **"SceneFactory implements the Factory design pattern, encapsulating complex Scene construction behind simple methods. Rather than scattering JavaFX node creation throughout the application, all UI construction flows through factory methods. This centralization enables consistent styling, simplifies testing, and prevents duplicate scene construction code."**

• **"The data-to-presentation mapping demonstrates narrative flexibility: the same game stats (time, deaths, sanity) are presented differently for death vs. victory. Death screens emphasize failure with dark poetry, while victory screens emphasize escape success. This data-driven presentation enables easy narrative customization without restructuring code."**

• **"UI button design uses visual state transitions (normal image ↔ pressed image) for tactile feedback. When users click, the button visually responds, creating the illusion of interaction and physical press. This interaction design pattern—borrowed from physical interfaces—enhances perceived responsiveness and intuitiveness of digital UI."**

---

## 7. Scene Types Summary

| Scene | Purpose | Transition | Data |
|-------|---------|-----------|------|
| Main Menu | Game start | Manual button | None |
| Death Screen | Death feedback | Auto on game over | Death message, stats |
| Victory Screen | Escape success | Auto on level complete | Stats |
| Objective Found | Story beat | Auto between levels | Level number |

---

## 8. Key Takeaway

**SceneFactory is the **narrative presentation engine**.** It's not just UI—it's storytelling. Every scene carries dark atmosphere (black backgrounds, red text, ominous poetry). Every death screen is personalized with the death cause. Every victory includes thematic poetry about the escape. SceneFactory demonstrates that UI isn't just functional—it's an opportunity for artistic expression and narrative reinforcement. The game tells its story not just through gameplay, but through how it presents success and failure.
