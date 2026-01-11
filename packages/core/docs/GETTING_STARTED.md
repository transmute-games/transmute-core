# Getting Started with TransmuteCore

Welcome to TransmuteCore! This guide will help you create your first 2D pixel game using the TransmuteCore engine.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Installation](#installation)
3. [Your First Game](#your-first-game)
4. [Project Structure](#project-structure)
5. [Next Steps](#next-steps)

## Prerequisites

Before you begin, make sure you have:

- **Java Development Kit (JDK) 17 or higher** installed
  - Check with: `java -version`
  - Download from: [Adoptium](https://adoptium.net/) or [Oracle](https://www.oracle.com/java/technologies/downloads/)

- **Gradle** (optional, the wrapper is included)
  - The project includes a Gradle wrapper (`./gradlew`) so you don't need to install Gradle separately

- **An IDE** (recommended but not required)
  - [IntelliJ IDEA](https://www.jetbrains.com/idea/) (Community Edition is free)
  - [Eclipse](https://www.eclipse.org/downloads/)
  - [Visual Studio Code](https://code.visualstudio.com/) with Java extensions

## Installation

### Option 1: Using transmute-starter (Recommended)

The easiest way to get started is to use the starter template:

```bash
git clone https://github.com/transmute-games/transmute-starter my-game
cd my-game
```

### Option 2: Adding TransmuteCore to an existing project

If you have an existing project, you can add TransmuteCore as a dependency.

First, build and publish TransmuteCore to your local Maven repository:

```bash
cd transmute-core
./gradlew publishToMavenLocal
```

Then add it to your project's `build.gradle`:

```gradle
dependencies {
    implementation 'games.transmute:transmute-core:0.1.0-ALPHA'
}
```

## Your First Game

Let's create a simple game that displays "Hello, TransmuteCore!" on the screen.

### Step 1: Create the Main Game Class

Create a new Java class that extends `TransmuteCore`:

```java
package com.mygame;

import TransmuteCore.GameEngine.TransmuteCore;
import TransmuteCore.GameEngine.Manager;
import TransmuteCore.Graphics.Context;
import TransmuteCore.Graphics.Color;
import TransmuteCore.States.StateManager;
import TransmuteCore.System.Asset.AssetManager;
import TransmuteCore.System.Asset.Type.Fonts.Font;

public class MyGame extends TransmuteCore {
    
    private StateManager stateManager;
    
    public MyGame() {
        // Parameters: title, version, width, aspectRatio, scale
        super("My First Game", "1.0", 320, TransmuteCore.Square, 3);
    }
    
    @Override
    public void init() {
        // Initialize the default font
        Font.initializeDefaultFont("fonts/font.png");
        AssetManager.load();
        
        // Set up state manager
        stateManager = new StateManager(this);
        getManager().setStateManager(stateManager);
        
        // Push the initial game state
        stateManager.push(new GameState(stateManager));
    }
    
    @Override
    public void update(Manager manager, double delta) {
        // Update the current state
        if (stateManager != null) {
            stateManager.update(manager, delta);
        }
    }
    
    @Override
    public void render(Manager manager, Context ctx) {
        // Render the current state
        if (stateManager != null) {
            stateManager.render(manager, ctx);
        }
    }
    
    public static void main(String[] args) {
        new MyGame();
    }
}
```

### Step 2: Create a Game State

Create a `GameState` class that extends `State`:

```java
package com.mygame;

import TransmuteCore.GameEngine.Manager;
import TransmuteCore.Graphics.Context;
import TransmuteCore.Graphics.Color;
import TransmuteCore.States.State;
import TransmuteCore.States.StateManager;

public class GameState extends State {
    
    public GameState(StateManager stateManager) {
        super("gameState", stateManager);
        init();
    }
    
    @Override
    public void init() {
        // Initialization code here
    }
    
    @Override
    public void update(Manager manager, double delta) {
        // Update game logic here
    }
    
    @Override
    public void render(Manager manager, Context ctx) {
        // Clear the screen to black
        ctx.setClearColor(Color.toPixelInt(0, 0, 0, 255));
        
        // Draw "Hello, TransmuteCore!" in white at position (50, 100)
        ctx.renderText("Hello, TransmuteCore!", 50, 100, 
                      Color.toPixelInt(255, 255, 255, 255));
    }
}
```

### Step 3: Add a Font Asset

TransmuteCore needs a bitmap font to render text. Create a `res/fonts` directory and add a `font.png` file. You can use the font from the transmute-starter project or create your own.

### Step 4: Run Your Game

If using Gradle:

```bash
./gradlew run
```

If using an IDE:
- Run the `MyGame` class with `main` method
- A window should appear displaying "Hello, TransmuteCore!"

## Project Structure

Here's the recommended structure for a TransmuteCore game:

```
my-game/
├── build.gradle              # Build configuration
├── src/
│   └── main/
│       └── java/
│           └── com/mygame/
│               ├── MyGame.java          # Main game class
│               ├── states/              # Game states (menu, gameplay, etc.)
│               │   ├── GameState.java
│               │   ├── MenuState.java
│               │   └── LoadingState.java
│               ├── objects/             # Game objects (player, enemies, etc.)
│               │   ├── Player.java
│               │   └── Enemy.java
│               └── util/                # Utility classes
│                   └── ResourceLoader.java
└── res/                       # Resources directory
    ├── fonts/                 # Bitmap fonts
    │   └── font.png
    ├── images/                # Sprites and images
    │   ├── player.png
    │   └── enemy.png
    ├── audio/                 # Sound effects and music
    │   ├── sfx_jump.wav
    │   └── music_main.wav
    └── levels/                # Level data
        └── level1.png
```

## Understanding the Core Concepts

### Resource Path Resolution

TransmuteCore loads resources from the classpath. Understanding how paths work is crucial:

**Path Rules:**
```java
// ✅ Correct - relative to src/main/resources/
new Image("player", "images/player.png");
//   Looks for: src/main/resources/images/player.png

// ❌ Wrong - absolute paths don't work
new Image("player", "/images/player.png");
new Image("player", "src/main/resources/images/player.png");

// ✅ Subdirectories work
new Image("boss", "images/enemies/boss.png");
//   Looks for: src/main/resources/images/enemies/boss.png
```

**Resource Directory Structure:**
```
src/main/resources/
├── fonts/          ← Font bitmaps
│   └── font.png
├── images/         ← Sprites and graphics
│   ├── player.png
│   └── enemies/
│       └── boss.png
├── audio/          ← Sound effects and music
│   ├── sfx/
│   │   └── jump.wav
│   └── music/
│       └── theme.wav
└── levels/         ← Level data
    └── level1.png
```

**Loading from Resources:**
```java
// The engine uses ClassLoader to find resources
getClass().getClassLoader().getResourceAsStream("images/player.png")

// Paths are relative to resources root
// NO leading slash
// Case-sensitive on Linux/Mac
```

**Common Path Mistakes:**
```java
// ❌ These won't work:
new Image("player", "res/images/player.png");     // Extra 'res/'
new Image("player", "./images/player.png");       // Don't use './'
new Image("player", "../resources/images/...");   // No parent dirs

// ✅ Correct:
new Image("player", "images/player.png");
```

### Window Modes and Display

**Window Configuration:**
```java
public MyGame() {
    super(
        "Game Title",        // Window title
        "1.0",               // Version string
        320,                 // Base width in pixels
        TransmuteCore.Square,// Aspect ratio
        3                    // Scale factor
    );
}
```

**Aspect Ratios:**
```java
// Square (4:3) - Classic aspect ratio
TransmuteCore.Square    // 320x240 @ scale 1
                        // 960x720 @ scale 3

// Widescreen (16:9) - Modern aspect ratio  
TransmuteCore.WideScreen // 320x180 @ scale 1
                         // 1280x720 @ scale 4
```

**Scale Factor Examples:**
```java
// Base: 320x240 (Square)
scale = 1  →  320x240   (tiny)
scale = 2  →  640x480   (small)
scale = 3  →  960x720   (medium, recommended)
scale = 4  →  1280x960  (large)
scale = 5  →  1600x1200 (very large)
```

**Adjusting for Screen Size:**
```java
import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;

public MyGame() {
    // Detect screen size
    GraphicsDevice gd = GraphicsEnvironment
        .getLocalGraphicsEnvironment()
        .getDefaultScreenDevice();
    
    int screenHeight = gd.getDisplayMode().getHeight();
    
    // Choose appropriate scale
    int scale;
    if (screenHeight >= 1440) {
        scale = 5;  // 4K/Retina displays
    } else if (screenHeight >= 1080) {
        scale = 4;  // Full HD
    } else {
        scale = 3;  // HD/Standard
    }
    
    super("My Game", "1.0", 320, TransmuteCore.Square, scale);
}
```

**Window Properties:**
```java
// Get window dimensions
int windowWidth = getWidth();   // Scaled width
int windowHeight = getHeight(); // Scaled height

// Get base (unscaled) dimensions
int baseWidth = 320;
int baseHeight = 240;  // For Square aspect

// Get scale factor
int scale = getScale();

// Actual window size = base * scale
// 320 * 3 = 960 pixels wide
```

**Fullscreen Mode:**
```java
// Note: TransmuteCore doesn't have built-in fullscreen
// Use native window scaling instead

// In your game constructor:
import javax.swing.JFrame;

public MyGame() {
    super("My Game", "1.0", 320, TransmuteCore.Square, 3);
    
    // Access the window after initialization
    SwingUtilities.invokeLater(() -> {
        JFrame frame = getWindow();
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        // Note: This stretches the window, doesn't change resolution
    });
}
```

### Game Loop

TransmuteCore uses a fixed timestep game loop (60 FPS by default):

1. **init()** - Called once when the game starts
2. **update(Manager, delta)** - Called every frame to update game logic
3. **render(Manager, Context)** - Called every frame to draw to the screen

### Manager System

The `Manager` class provides access to all engine subsystems:

```java
Manager manager = TransmuteCore.getManager();

// Access different managers
StateManager stateManager = manager.getStateManager();
AssetManager assetManager = manager.getAssetManager();
Input input = manager.getInput();
ObjectManager objectManager = manager.getObjectManager();
```

### State Management

States represent different screens in your game (menu, gameplay, pause, etc.):

```java
// Create and push a state
stateManager.push(new MenuState(stateManager));

// States are stack-based - only the top state updates/renders
```

### Asset Loading

Assets are loaded using a deferred loading system:

```java
// Register assets (usually in a ResourceLoader class)
new Image("player", "images/player.png");
new Audio("jump", "audio/sfx_jump.wav");
new Font("custom", "fonts/custom.png", glyphMap, rows, cols, glyphSize);

// Load all registered assets
AssetManager.load();

// Retrieve assets
Bitmap playerSprite = AssetManager.getImage("player");
Clip jumpSound = AssetManager.getAudio("jump");
```

### Input Handling

Check for keyboard and mouse input:

```java
// Keyboard
if (Input.isKeyPressed(KeyEvent.VK_SPACE)) {
    // Space key was just pressed
}

if (Input.isKeyHeld(KeyEvent.VK_W)) {
    // W key is being held down
}

// Mouse
if (Input.isButtonPressed(MouseEvent.BUTTON1)) {
    // Left mouse button was clicked
}

int mouseX = input.getMouseX();
int mouseY = input.getMouseY();
```

### Rendering

The `Context` class is your rendering canvas:

```java
// Draw an image
ctx.renderBitmap(sprite, x, y);

// Draw a filled rectangle
ctx.renderFilledRectangle(x, y, width, height, color);

// Draw text (requires font to be set)
ctx.setFont(myFont);
ctx.renderText("Score: 100", 10, 10, Color.toPixelInt(255, 255, 255, 255));

// Draw with transparency
ctx.renderBitmap(sprite, x, y, 0.5f); // 50% transparent
```

## Common Patterns

### Creating a Loading State

```java
public class LoadingState extends State {
    public LoadingState(StateManager stateManager) {
        super("loading", stateManager);
        init();
    }
    
    @Override
    public void init() {
        // Register all assets
        new Image("player", "images/player.png");
        new Audio("music", "audio/music.wav");
        // ... more assets
    }
    
    @Override
    public void update(Manager manager, double delta) {
        // Check if loading is complete
        if (AssetManager.isLoaded()) {
            stateManager.push(new GameState(stateManager));
        }
    }
    
    @Override
    public void render(Manager manager, Context ctx) {
        ctx.renderText("Loading...", 100, 100, Color.toPixelInt(255, 255, 255, 255));
    }
}
```

### Creating Game Objects

```java
public class Player extends TransmuteCore.Objects.Object {
    
    public Player(Manager manager, Sprite sprite, Tuple2i location) {
        super(manager, "player", Object.STATIC, sprite, location, 1.0f);
    }
    
    @Override
    public void update(Manager manager, double delta) {
        // Movement
        if (Input.isKeyHeld(KeyEvent.VK_W)) {
            location.y -= 2;
        }
        if (Input.isKeyHeld(KeyEvent.VK_S)) {
            location.y += 2;
        }
        
        super.update(manager, delta);
    }
    
    @Override
    public void render(Manager manager, Context ctx) {
        super.render(manager, ctx);
    }
}
```

## Debugging and Development Tools

### Debug Overlay (F3)

TransmuteCore includes a built-in debug overlay for monitoring performance:

```java
import TransmuteCore.System.Debug.DebugOverlay;

private DebugOverlay debugOverlay;

@Override
public void init() {
    debugOverlay = new DebugOverlay();
    debugOverlay.setFont(AssetManager.getDefaultFont());
    debugOverlay.setTargetFPS(60);
}

@Override
public void update(Manager manager, double delta) {
    debugOverlay.update(manager, delta);
}

@Override
public void render(Manager manager, Context ctx) {
    // ... your rendering code ...
    debugOverlay.render(manager, ctx); // Render last!
}
```

Press **F3** to toggle the overlay, which shows:
- Current, average, min/max FPS
- Frame time in milliseconds
- Memory usage (used/total/max)
- Current game state
- Entity count

### Screenshot Capture (F12)

Capture screenshots instantly during development:

```java
import TransmuteCore.System.Screenshot;

@Override
public void update(Manager manager, double delta) {
    Screenshot.update();
}

@Override
public void render(Manager manager, Context ctx) {
    // ... rendering ...
    Screenshot.capture(ctx); // Capture if F12 was pressed
}
```

Press **F12** to save a screenshot to `screenshots/` directory.

### Performance Profiler

Identify bottlenecks in your game:

```java
import TransmuteCore.System.Debug.Profiler;

@Override
public void update(Manager manager, double delta) {
    Profiler.begin("physics");
    // ... physics code ...
    Profiler.end("physics");
    
    Profiler.begin("ai");
    // ... AI code ...
    Profiler.end("ai");
}

// Print results to console
Profiler.printResults();

// Or export to CSV
String csv = Profiler.exportToCSV();
```

### Debug Console (~)

Runtime command console for debugging and testing:

```java
import TransmuteCore.System.Debug.Console;

private Console console;

@Override
public void init() {
    console = new Console();
    console.setFont(AssetManager.getDefaultFont());
    
    // Register custom commands
    console.registerCommand("spawn", args -> {
        if (args.length < 1) {
            console.println("Usage: spawn <entity_name>");
            return;
        }
        spawnEntity(args[0]);
        console.println("Spawned: " + args[0]);
    });
}

@Override
public void update(Manager manager, double delta) {
    console.update(manager, delta);
}

@Override
public void render(Manager manager, Context ctx) {
    // ... rendering ...
    console.render(manager, ctx); // Render last!
}
```

Press **~** (grave/tilde) to toggle the console. Built-in commands:
- `help` - List all commands
- `clear` - Clear console output
- `profiler print` - Show profiler results
- `gc` - Run garbage collection
- `exit` - Close the game

### Hot Reload

Reload assets automatically when files change - speeds up iteration!

```java
import TransmuteCore.System.HotReload.HotReloadManager;
import TransmuteCore.System.HotReload.AssetReloader.AssetType;

private HotReloadManager hotReload;

@Override
public void init() {
    hotReload = new HotReloadManager();
    
    // Quick setup - watches res/images, res/audio, res/fonts
    hotReload.setupStandardWatching("res");
    
    // Register assets
    hotReload.registerAsset("player", "res/images/player.png", AssetType.IMAGE);
    hotReload.registerAsset("enemy", "res/images/enemy.png", AssetType.IMAGE);
    
    // Start watching
    hotReload.start();
}
```

Now edit `player.png` in your image editor, save it, and see it update instantly in your running game!

**Manual Reload:**
- Press `~` to open console, then type `reload` to reload all assets
- Or `reload player` to reload a specific asset

### Enhanced Error Messages

Get helpful error messages with suggestions:

```java
try {
    AssetManager.getImage("player");
} catch (AssetNotFoundException e) {
    // Detailed error with search paths and fix suggestions
    System.err.println(e.getMessage());
    for (String suggestion : e.getSuggestions()) {
        System.err.println("  - " + suggestion);
    }
}
```

## Next Steps

Now that you have a basic understanding, check out these resources:

1. **Tutorials**: Complete step-by-step tutorials in the `docs/tutorials/` directory
   - Tutorial 1: Hello World - Display text and handle basic rendering
   - Tutorial 2: Sprites and Animation - Load sprites and create animations
   - Tutorial 3: Input and Movement - Handle player input and movement
   - Tutorial 4: Collision Detection - Implement collision between objects
   - Tutorial 5: State Management - Create menus and multiple game screens
   - Tutorial 6: Audio System - Sound effects and music
   - Tutorial 7: Level Design - Tile-based levels
2. **COOKBOOK.md**: Common patterns for player movement, cameras, menus, and more
3. **Starter Template**: Check out [transmute-starter](https://github.com/transmute-games/transmute-starter) for a complete working example

## Troubleshooting

### "Asset not found" errors

- Verify the asset exists in the `res/` directory
- Check that the path is relative to the resources root (no leading `/`)
- Ensure the file extension matches the actual file type
- Asset names are case-sensitive on some systems

### Black screen on startup

- Make sure you've called `AssetManager.load()` in `init()`
- Check that you're pushing at least one state to the StateManager
- Verify your render method is actually drawing something

### Poor performance

- Enable FPS logging: `setFPSVerbose(true)`
- Use smaller window sizes or lower scale factors
- Optimize your rendering (minimize overdraw)
- Profile with `Logger.setLevel(Logger.Level.DEBUG)`

## Getting Help

- **Documentation**: Check the [WARP.md](../WARP.md) file for architecture details
- **Examples**: Browse the [transmute-starter](https://github.com/transmute-games/transmute-starter) repository
- **Issues**: Report bugs on [GitHub Issues](https://github.com/transmute-games/transmute-core/issues)

Happy game development! 🎮
