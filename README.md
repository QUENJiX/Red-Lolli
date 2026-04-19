# 🍭 Escape Pale Luna (RedLolli)

**North South University - CSE215L (Java Programming Language Lab) Final Project**

Welcome to **Escape Pale Luna**! It's a spooky, totally-not-going-to-give-you-nightmares 2D dungeon crawler built purely with **JavaFX**. You run around dark, creepy mazes, try to grab shiny things (lollis!), and desperately avoid getting your face eaten by the terrifying Pale Luna that roams the halls.

Oh, and did we mention you slowly go insane in the dark? Because you do! Watch your sanity meter, or the screen edges will close in on you!

## ✨ What's Awesome About It

*   **Terrifying Enemies:** Good luck outrunning Pale Luna. She's hunting you, and she does not want to be your friend. 
*   **Dynamic Sanity System:** Stay in the dark too long? You go crazy. The screen actually gets a creepy vignette the lower your sanity drops! 
*   **Sneaky Tactics:** Throw distractions! Plop down a fake cardboard clone of yourself to trick the monsters! Survive by any means necessary.
*   **Custom Game Engine:** We didn't use a massive heavy engine; we built our own game loop, collision detection, and rendering straight onto a JavaFX `Canvas`.
*   **Spooky Cutscenes:** Complete with a tense intro, creepy music, and a victory screen if you somehow manage to survive all three floors.

## ⚙️ The Tech Inside

*   **Language:** Java 25 (Because we like living in the future)
*   **Graphics & UI:** JavaFX 21.0.6 (Drawing everything frame-by-frame on a Canvas like absolute legends)
*   **Build Tool:** Maven (To keep all the libraries playing nice together)
*   **Testing:** JUnit 5.12.1 (To make sure we didn't break too many things)

## 📁 How It's Put Together

Here's a quick tour of where everything lives:

```text
src/main/java/com/nsu/cse215l/redlolli/
├── redlolli/
│   ├── HelloApplication.java # The main game loop and window magic!
│   ├── GameStateManager.java # The brains of the operation. Tracks your health, levels, and if you are currently being eaten.
│   ├── Launcher.java         # The simple little guy who turns the key to start the car.
│   ├── core/                 # Important background stuff like the GameLogger.
│   ├── entities/             # Everything that moves or does stuff (Player, Pale Luna, Chests, etc).
│   ├── map/                  # The level layouts and maze generation.
│   ├── systems/              # Managers for things like Collision and Sound.
│   └── ui/                   # Making things look pretty (SceneFactory, GameRenderer, HUD).
```

## 🚀 How to Play!

### What you need:
*   [Java Development Kit (JDK) 25+](https://jdk.java.net/25/) installed on your machine.
*   That's pretty much it! We included the Maven Wrapper (`mvnw`), so you don't even need to install Maven yourself.

### Start the Game:

Pop open your terminal, navigate to the folder, and run this magic spell:

**Windows:**
```powershell
./mvnw clean compile javafx:run
```

**macOS / Linux:**
```bash
./mvnw clean compile javafx:run
```

The game will compile, load up `Launcher.java`, and drop you straight into the nightmare. Have fun!

## 🛠️ Testing

Yes, we wrote tests. We are responsible developers. You can run them to make sure everything is working perfectly under the hood:

```bash
./mvnw test
```

## 👥 Who Made This?
Built with love, sweat, and maybe a few tears for the **CSE215L** Java Programming Language Lab Final Project at **North South University (NSU)**.