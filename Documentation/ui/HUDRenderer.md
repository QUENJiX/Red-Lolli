# HUDRenderer.java - Complete Structural Analysis

## 1. The "Elevator Pitch" (High-Level Overview)

**HUDRenderer** is the **heads-up display system**—it renders the informational overlay at the top of the screen showing critical gameplay metrics. It displays 6 sections in a single 880×50 pixel bar: (1) Level indicator, (2) Lollipop collection status, (3) Current objective, (4) Pale Luna's state and threat meter, (5) Player sanity bar with skull icon, (6) Escape room safety status. Think of HUDRenderer as the **game's dashboard**—at a glance, players see their mission progress, their threat level, their mental state. HUDRenderer transforms complex game state into intuitive visual communication.

---

## 2. Core Computer Science Concepts

### **Design Patterns & Techniques Used:**
- **Segmented Information Display:** Multiple data types grouped into logical sections
- **Color-Coded Feedback:** Threat level via color (green=safe, yellow=warning, red=danger)
- **Progress Bar Visualization:** Abstract metrics (time, sanity, threat) shown as visual bars
- **Dynamic Text Rendering:** Text changes based on game state (Luna hunting vs. dormant)
- **Pulse Animation:** Temporal effects communicate state changes (warning pulse when threat escalates)

### **Why These Concepts Matter:**
- **Segmentation:** Prevents HUD from feeling cluttered; each section has clear purpose
- **Color Coding:** Immediate threat assessment without reading text
- **Progress Bars:** Concrete visual representation of invisible stats
- **Animation:** Draws attention to state changes (Luna entering hunt phase)

---

## 3. Deep Dive: Variables and State

### **Display Layout Constants:**

| Field | Type | Value | Purpose |
|-------|------|-------|---------|
| `HUD_W` | `static double` | 880 | HUD width (full screen) |
| `HUD_H` | `static double` | 50 | HUD height (thin bar) |
| `ROW1_Y` | `static double` | 20 | Text row 1 Y-position |
| `ROW2_Y` | `static double` | 38 | Text row 2 Y-position |
| `BAR_Y` | `static double` | 24 | Progress bar Y-position |
| `BAR_H` | `static double` | 14 | Progress bar height |

### **Section Dividers (X Positions):**

| Field | Type | Value | Purpose |
|-------|------|-------|---------|
| `DIV_LEVEL` | `static double` | 90 | Divider after Level section |
| `DIV_LOLLI` | `static double` | 150 | Divider after Lollipop section |
| `DIV_FIND` | `static double` | 335 | Divider after Objective section |
| `DIV_LUNA` | `static double` | 610 | Divider after Luna section |
| `DIV_SANITY` | `static double` | 810 | Divider after Sanity section |

### **Icons Cache:**

| Field | Type | Purpose |
|-------|------|---------|
| `paleLunaIconImg[4]` | `static Image[4]` | Luna state icons (dormant/stalking/hunting/waiting) |
| `sanitySkullImg` | `static Image` | Skull icon (high sanity) |
| `sanitySkullLowImg` | `static Image` | Skull icon (low sanity) |

---

## 4. Deep Dive: Methods and Logic (Step-by-Step)

### **Method 1: `initImages()` [Icon Initialization]**

**The Goal:**
Load 6 small icon images used in HUD display.

**How it Works (Layman's Terms):**
```
Load paleLunaIconImg[0-3]: Luna states (18×18 pixels each)
Load sanitySkullImg, sanitySkullLowImg: Skull icons (20×20 pixels)
Mark initialized
```

---

### **Method 2: `drawHUD()` [Main HUD Rendering - CRITICAL]**

**The Goal:**
Orchestrate drawing all 6 HUD sections, called each frame.

**How it Works (Layman's Terms):**

#### **Part 1: Draw Background**
```
Dark background bar (RGB 10,10,14) - nearly black
Red underline (RGB 30,5,5)
Thin red border top edge
```

**Why Dark?** Maximizes contrast for text readability.

#### **Part 2: Draw Each Section Sequentially**

**Section 1: Level Indicator**
```
Draw "LEVEL" label in small font
Draw current level (1/3, 2/3, 3/3) in large font
Divider line
```

**Section 2: Lollipop Collection**
```
Check: has player collected the objective lollipop?
If YES:
    Draw pulsing glow circles around lollipop icon
    Text: "1/1" in lime green
If NO:
    Text: "0/1" in gray
Divider line
```

**Section 3: Objective**
```
Display mission: "FIND: <The Mud/The Shovel/The Rope>"
Level 3 only: Show "Clone ready [C] to place" (if clone obtained)
Other levels: Show "Distraction Spell [E]: <count>"
Divider line
```

**Section 4: Luna Threat Indicator**
```
Draw Luna state icon (18×18)
Draw threat bar:
    State = DORMANT:
        Bar fills based on sleep timer (empties as she wakes)
        Color: green→yellow→orange→red based on time left
        Text: "Sleeps Xs" (seconds remaining)
    
    State = STALKING:
        Pulsing bar (flash effect)
        Color: orange-red
        Text: "She watches..."
        Animation: pulsePhase += 0.24
    
    State = HUNTING:
        Flashing bright red
        Text: "RUN! Xs left"
        Maximum threat visualization
        Animation: pulsePhase += 0.30
    
    State = WAITING_AT_DOOR:
        Steady orange
        Text: "Xs seconds..."
Divider line
```

**Section 5: Sanity Meter**
```
Label: "Sanity"
Progress bar showing sanity value (0-100)
Color coding:
    0-30: Red (critical)
    30-60: Orange (warning)
    60-85: Yellow (caution)
    85-100: Green (safe)

Draw skull icon (right side):
    Use lowSanitySkull if < 30
    Use normalSkull if >= 30

Text: "<sanity>%"
Divider line
```

**Section 6: Safety Status**
```
if (in escape room):
    Draw "✔ SAFE" in lime green
else:
    Draw "DANGER" in gray
```

---

### **Method 3: `drawBackground()` [HUD Bar Background]**

**The Goal:**
Draw dark background and border for HUD bar.

**How it Works (Layman's Terms):**
```
Fill dark rectangle (RGB 10,10,14)
Fill bottom stripe (RGB 30,5,5)
Draw thin top border line (RGB 140,20,20)
```

---

### **Method 4: `drawDivider()` [Section Separator]**

**The Goal:**
Draw vertical line separating HUD sections.

**How it Works (Layman's Terms):**
```
Draw vertical line at X position
Color: RGB(60,25,25) with 0.7 alpha (subtle)
Extend from top margin to bottom margin
```

**Why Dividers?** Create visual separation without harsh contrast.

---

### **Method 5: `drawLevelSection()` [Level Indicator]**

**The Goal:**
Display current level (1/3, 2/3, 3/3).

**How it Works (Layman's Terms):**
```
Draw "LEVEL" label (small, gray)
Draw level number (large, bright blue-white)
E.g., "2 / 3"
```

---

### **Method 6: `drawLolliSection()` [Objective Status]**

**The Goal:**
Show lollipop collection progress with visual feedback.

**How it Works (Layman's Terms):**

#### **Part 1: Check Objective Status**
```
Search chests: has any lollipop been collected?
found = (any collected chest with lolli content)
```

#### **Part 2: Draw Pulsing Glow (If Found)**
```
if (found):
    Calculate pulse: sin(currentTime * 0.005) * 0.2 + 0.8
    Draw 3 concentric circles with varying alpha:
        Outer circle: large, faint gold
        Middle circle: medium, medium gold
        Inner circle: small, bright gold
    All pulsing with temporal animation
```

**Why Glow?** Visual celebration of achievement; draws attention.

#### **Part 3: Draw Lollipop Icon and Status**
```
Call GameRenderer.drawRedLolli() at center position
Draw status: "0/1" or "1/1"
Color: gray if 0, lime green if 1
```

---

### **Method 7: `drawFindSection()` [Current Objective]**

**The Goal:**
Display mission objective and ability status.

**How it Works (Layman's Terms):**

#### **Part 1: Draw Mission**
```
Draw "FIND:" label (yellow)
Draw current objective based on level:
    Level 1: "The Mud"
    Level 2: "The Shovel"
    Level 3: "The Rope"
Text: white
```

#### **Part 2: Draw Ability Status**

**Level 3 + Has Clone:**
```
Draw "👧 Clone ready [C] to place"
Color: light yellow (green-tinted)
```

**Other Levels / No Clone:**
```
Draw "✨ Distraction Spell [E]: <count>"
Count: number of distraction spells available
Color: gray
```

---

### **Method 8: `drawLunaSection()` [Threat Level]**

**The Goal:**
Display Luna's current state and remaining threat time.

**How it Works (Layman's Terms):**

#### **Part 1: Draw State Icon**
```
Map Luna's state to icon:
    DORMANT → dormant icon
    STALKING → stalking icon
    HUNTING → hunting icon
    WAITING → waiting icon
Draw 18×18 icon at section start
```

#### **Part 2: Draw State-Specific Threat Bar**

**DORMANT:**
```
Bar shows: (dormantTimer / 900) as progress
Color transitions:
    > 10 sec: green
    > 6 sec: yellow
    > 3 sec: orange
    <= 3 sec: pulsing red
Text: "Sleeps <seconds>s"
```

**STALKING:**
```
Bar shows: (stalkTimer / 480) as progress
Bar background pulses with flash effect
Color: orange-red
Text: "She watches..."
Animation: pulsePhase += 0.24 (visible speed increase)
```

**HUNTING:**
```
Bar shows: (huntTimer / 360) as progress
Background bright flashing red
Color: solid red
Text: "RUN! <seconds>s left"
Animation: pulsePhase += 0.30 (fastest animation)
```

**WAITING_AT_DOOR:**
```
Bar shows: (waitTimer / 180) as progress
Color: orange
Text: "<seconds>s..."
```

---

### **Method 9: `drawSanitySection()` [Mental State]**

**The Goal:**
Display player sanity with color-coded threat levels.

**How it Works (Layman's Terms):**

#### **Part 1: Determine Color**
```
if (sanity < 30): red (critical)
else if (sanity < 60): orange (warning)
else if (sanity < 85): yellow (caution)
else: green (safe)

Select skull icon:
    if (sanity < 30): lowSanitySkull (more terrified)
    else: normalSkull
```

#### **Part 2: Draw Bar and Icon**
```
Draw progress bar filled (sanity / 100.0)
Draw skull icon at right edge (communicates "mind" metric)
Draw sanity percentage text
```

---

### **Method 10: `drawSafeSection()` [Location Status]**

**The Goal:**
Indicate whether player is safe in escape room.

**How it Works (Layman's Terms):**
```
if (player.isInEscapeRoom()):
    Draw "✔ SAFE" in lime green
else:
    Draw "DANGER" in gray
```

---

### **Method 11: `drawBarLabel()` [Helper - Bar Labeling]**

**The Goal:**
Draw text label above a progress bar.

**How it Works (Layman's Terms):**
```
Set font: Arial Bold 11pt
Set color to parameter
Draw text at (x, y)
```

---

### **Method 12: `drawBar()` [Helper - Progress Bar]**

**The Goal:**
Draw a progress bar with background, border, fill, and text.

**How it Works (Layman's Terms):**

#### **Part 1: Draw Background**
```
Fill background color rectangle
Draw border stroke color
```

#### **Part 2: Draw Progress Fill**
```
Calculate fill width: barWidth * progress (0.0-1.0)
Clamp progress to 0.0-1.0 range
Draw filled rectangle capped at progress width
```

#### **Part 3: Draw Label Text**
```
Draw text at bar center
Font: Consolas Bold 10pt
Color: white
```

---

## 5. Deep Dive: Model-View-Controller (MVC) Pattern

**Where HUDRenderer Fits in MVC:**

| MVC Layer | HUDRenderer's Role |
|-----------|---|
| **Model** | ❌ No |
| **View** | ✅ **YES** Secondary view layer for UI display |
| **Controller** | ❌ No |

---

## 6. Lab Final Presentation Arsenal

Here are 3 professional talking points for your lab presentation:

• **"HUDRenderer demonstrates information architecture: rather than displaying raw game state, the system translates abstract metrics (sanity: 0-100, threat level: dormant/stalking/hunting/waiting) into intuitive visual representations (color-coded progress bars, state icons, temporal animations). This translation layer ensures players understand game state at a glance without reading code."**

• **"The threat indicator exemplifies temporal visual feedback: as Luna's state changes (dormant → stalking → hunting), the bar's animation speed increases (pulsePhase += 0.24 → 0.30), and color intensifies (green → orange → red). This creates escalating visual urgency that mirrors gameplay danger, communicating threat through visual rhythm rather than explicit text."**

• **"The dual-section design (objective + threat + sanity) separates player concerns into distinct cognitive loads: mission progress (left), imminent danger (center-right), internal state (right). This spatial organization enables players to scan quickly for relevant information without cognitive overhead, a hallmark of professional UI design."**

---

## 7. Color Coding Reference

| State | Color | Meaning |
|-------|-------|---------|
| Green | Lime / Safe | Objective achieved, high sanity, escape room safety |
| Yellow | Caution | Moderate threat, 60-85 sanity |
| Orange | Warning | Luna stalking, 30-60 sanity |
| Red | Danger | Luna hunting, <30 sanity (critical) |

---

## 8. Key Takeaway

**HUDRenderer is the **translator of complexity into clarity**.** It takes abstract game state (sanity: 67, Luna state: STALKING, threat timer: 183 frames) and communicates it as intuitive visual feedback (yellow sanity bar 67%, pulsing orange "She watches..." text). Professional games don't dump raw numbers on players—they present state through carefully designed visual language. HUDRenderer demonstrates this by separating data (model) from presentation (view).
