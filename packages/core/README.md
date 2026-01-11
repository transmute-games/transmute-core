# 🧙🏼 Transmute Core Engine

The core Transmute Core 2D pixel game engine library. This package contains the complete game engine implementation.

## Maven Coordinates

```gradle
dependencies {
    implementation 'games.transmute:transmute-core:0.1.0-ALPHA'
}
```

## Building

From the repository root:

```bash
# Build the core engine
./gradlew :transmute-core:build

# Publish to local Maven repository
./gradlew :transmute-core:publishToMavenLocal

# Generate Javadocs
./gradlew :transmute-core:javadoc

# Development build (build + publish)
./gradlew :transmute-core:devBuild
```

## Project Structure

```
core/
├── build.gradle           # Build configuration
└── TransmuteCore/
    ├── src/               # Java source files
    │   └── TransmuteCore/
    │       ├── assets/    # Asset loading and management
    │       ├── core/      # Core game loop, engine, and interfaces
    │       ├── data/      # Serialization and data structures
    │       ├── ecs/       # Entity-Component-System and game objects
    │       ├── graphics/  # Rendering and visual systems
    │       ├── input/     # Keyboard and mouse handling
    │       ├── level/     # Level and tile systems
    │       ├── math/      # Math utilities and vector types
    │       ├── state/     # State management system
    │       └── util/      # Utilities, logging, debugging, exceptions
    └── res/               # Resources (if any)
```

## Quick Start

### 1. Publish to Maven Local

```bash
cd /path/to/transmute-core
./gradlew :transmute-core:publishToMavenLocal
```

### 2. Add to Your Project

**build.gradle:**

```gradle
repositories {
    mavenLocal()
}

dependencies {
    implementation 'games.transmute:transmute-core:0.1.0-ALPHA'
}
```

### 3. Create Your Game

```java
import TransmuteCore.core.TransmuteCore;
import TransmuteCore.core.GameConfig;
import TransmuteCore.core.Manager;
import TransmuteCore.core.interfaces.services.IRenderer;
import TransmuteCore.graphics.Context;
import TransmuteCore.graphics.Color;

public class MyGame extends TransmuteCore {
    public MyGame(GameConfig config) {
        super(config);
    }

    @Override
    public void init() {
        // Initialize your game
    }

    @Override
    public void update(Manager manager, double delta) {
        // Update game logic
    }

    @Override
    public void render(Manager manager, IRenderer renderer) {
        Context ctx = (Context) renderer;
        ctx.renderText("Hello, World!", 50, 100,
                      Color.toPixelInt(255, 255, 255, 255));
    }

    public static void main(String[] args) {
        GameConfig config = new GameConfig.Builder()
            .title("My Game")
            .version("1.0")
            .dimensions(320, GameConfig.ASPECT_RATIO_SQUARE)
            .scale(3)
            .build();

        MyGame game = new MyGame(config);
        game.start();
    }
}
```

## Core Features

- **Fixed Timestep Game Loop** - 60 FPS game logic with delta time
- **Custom Pixel Rendering** - Direct pixel manipulation via DataBufferInt
- **Asset Management** - Deferred loading system with caching
- **State Management** - Stack-based state system
- **Input Handling** - Keyboard and mouse with press/hold/release states
- **Sprite System** - Sprite sheets and frame-based animations
- **Level System** - Tile-based levels with viewport culling
- **Serialization** - Binary save/load system (TinyDatabase)
- **Audio Support** - Sound effects and music playback

## Key Packages

### `TransmuteCore.core`
- `TransmuteCore` - Main engine class (extend this)
- `GameConfig` - Immutable configuration with builder pattern
- `Manager` - Central coordinator for all subsystems
- `GameLoop` - Fixed timestep game loop implementation

### `TransmuteCore.graphics`
- `Context` - Custom pixel buffer rendering system
- `Color` - Color utilities and ARGB conversion
- `RenderPipeline` - Hardware-accelerated rendering pipeline

### `TransmuteCore.assets`
- `AssetManager` - Global asset registry and loader
- `types.Image` - Image asset type
- `types.Audio` - Audio asset type
- `types.Font` - Bitmap font asset type

### `TransmuteCore.input`
- `Input` - Keyboard and mouse input handler

### `TransmuteCore.state`
- `StateManager` - Stack-based state management
- `State` - Base state class

### `TransmuteCore.ecs`
- `Object` - Base game object class
- `ObjectManager` - Batch update/render for game objects

### `TransmuteCore.level`
- `Level` - Base level class
- `TiledLevel` - Tile-based level loader

## Configuration Options

Use `GameConfig.Builder` to configure your game:

```java
GameConfig config = new GameConfig.Builder()
    .title("Game Title")              // Window title
    .version("1.0")                   // Game version
    .dimensions(320, aspectRatio)     // Width + aspect ratio
    .scale(3)                         // Window scale multiplier
    .targetFPS(60)                    // Target frames per second
    .bufferCount(3)                   // Double (2) or triple (3) buffering
    .fpsVerbose(false)                // Enable FPS logging
    .showStartScreen(true)            // Show engine splash screen
    .stopOnException(true)            // Stop on uncaught exceptions
    .headless(false)                  // Headless mode (no window)
    .build();
```

**Aspect Ratios:**
- `GameConfig.ASPECT_RATIO_WIDESCREEN` - 16:9
- `GameConfig.ASPECT_RATIO_SQUARE` - 4:3

## Dependencies

The core engine has **zero external dependencies** and only requires:
- Java 17 or higher
- Java AWT (included in JDK)

## Documentation

- **[Main README](../../README.md)** - Project overview
- **[Getting Started](../../docs/GETTING_STARTED.md)** - Setup guide
- **[Tutorials](../../docs/tutorials/)** - Step-by-step learning
- **[WARP.md](../../WARP.md)** - Architecture reference

## Using the CLI

Instead of manual setup, use the [transmute-cli](../cli/) to scaffold new projects:

```bash
# Install CLI
./gradlew :transmute-cli:install

# Create new game
transmute new my-game
cd my-game
./gradlew run
```

## License

MIT License - See [LICENSE](../../.github/LICENSE) for details.
