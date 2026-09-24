# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

Transmute Core is a Java-based 2D pixel game engine designed for high-performance game development. The engine uses Java AWT for rendering with custom pixel-level manipulation through BufferedImage and DataBufferInt for maximum performance.

## Build & Development

### Prerequisites
- Java Development Kit (JDK) 17 or higher
- Gradle (wrapper included in project)
- IDE with Java support (IntelliJ IDEA, Eclipse, or VS Code recommended)

### Project Structure
The project uses Gradle for builds with a multi-project structure. Source files are in `packages/core/TransmuteCore/src/` with compiled output managed by Gradle.

```
transmute-core/
├── build.gradle                # Root build configuration
├── settings.gradle             # Multi-project configuration
├── packages/
│   ├── core/                   # Transmute Core engine
│   │   ├── TransmuteCore/
│   │   │   └── src/
│   │   │       └── TransmuteCore/
│   │   │           ├── assets/         # Asset loading and management
│   │   │           ├── core/           # Core game loop, engine, and interfaces
│   │   │           ├── data/           # Serialization and data structures
│   │   │           ├── ecs/            # Entity-Component-System and game objects
│   │   │           ├── graphics/       # Rendering and visual systems
│   │   │           ├── input/          # Keyboard and mouse handling
│   │   │           ├── level/          # Level and tile systems
│   │   │           ├── math/           # Math utilities and vector types
│   │   │           ├── state/          # State management system
│   │   │           └── util/           # Utilities, logging, debugging, exceptions
│   │   └── build.gradle
│   └── cli/                    # Project generator CLI
│       ├── src/
│       ├── bin/                # Shell wrappers
│       └── build.gradle
└── docs/                       # Documentation
```

### Building
Build the project using Gradle:
```bash
# Build entire project (core + CLI)
./gradlew build

# Build just the core engine
./gradlew :transmute-core:build

# Run tests
./gradlew test

# Publish to local Maven repository
./gradlew :transmute-core:publishToMavenLocal

# Generate Javadocs
./gradlew :transmute-core:javadoc
```

### Creating a Game
This is a library/engine project. To create a game:
1. Use the CLI generator: `./gradlew :transmute-cli:install && transmute new my-game`
2. Or extend the `TransmuteCore` class manually
3. Implement the required methods from the `Cortex` interface
4. Follow the [tutorials](docs/tutorials/) for step-by-step guidance

## Core Architecture

### Game Loop & Lifecycle
The engine uses a fixed timestep game loop (default 60 FPS) with delta time calculations:
- `init()` - One-time initialization of game state
- `update(Manager manager, double delta)` - Game logic updates with delta time
- `render(Manager manager, IRenderer renderer)` - Rendering to the custom pixel buffer (cast to Context for pixel operations)

### Manager System
The `Manager` is the **authoring seam** for game code. Call `manager.bootstrapDefaults()`
in `init()` to wire AssetManager, StateManager, and ObjectManager.
See [AGENTS.md](AGENTS.md) and [examples/hello](examples/hello).

`GameContext` is an internal DI snapshot — prefer Manager in game subclasses.

Access via `TransmuteCore.getManager()` (lazy-initialized, thread-safe).

### Verify loop (agents / CI)
Use `GameHarness` + `FrameAssert` with `GameConfig.headless(true)`. Headless still
renders into `Context` so frames can be hashed and asserted without a window.
Drive input with `SimulatedInput` or data scripts via `PlaytestScript`
(`playtests/*.script`). Assert audio cues with `AudioProbe` (works while muted).
Agent eval fixtures: `eval/` + `scripts/agent-eval.sh`.

### Rendering Pipeline
1. **Context** (`TransmuteCore.graphics.Context`) - Custom pixel buffer rendering system
   - Uses 32-bit ARGB integer pixel format
   - Renders to a master BufferedImage via DataBufferInt
   - All rendering operations (bitmaps, rectangles, text) write directly to pixel array
   - Final image is scaled and drawn to canvas via Java2D Graphics

2. **Hardware Acceleration** - Uses VolatileImage for hardware-accelerated rendering
3. **Double/Triple Buffering** - Configurable BufferStrategy (default 3 buffers)

### Asset Management
Assets are loaded through a deferred loading system:
- Register assets with `new Image()`, `new Audio()`, or `new Font()`
- Assets auto-register with `AssetManager.REGISTRAR`
- Call `AssetManager.load()` to batch-load all queued assets
- Retrieve with `AssetManager.getImage(name)`, `AssetManager.getAudio(name)`, etc.

Asset keys are type-prefixed and lowercase (e.g., "image:player", "audio:music").

### Input Handling
The `Input` class provides three input states:
- `isKeyPressed()` / `isButtonPressed()` - Single frame press detection
- `isKeyHeld()` / `isButtonHeld()` - Continuous hold detection
- `isKeyReleased()` / `isButtonReleased()` - Single frame release detection

Mouse coordinates are automatically scaled by game window scale factor.

### Game States
Use `StateManager` to manage game states (menu, gameplay, pause):
- Create states extending the `State` class
- Push states with `stateManager.push(newState)`
- States are stack-based (only top state receives update/render calls)

### Level / world system (canonical)
Prefer **`TransmuteCore.world.World`** for new games (tile grid + `Actor` + `Trigger`).
Declare maps in `gamespec.properties` via `world.*` keys, or build in code.
Use **`Camera`** when the world is larger than the view (`World.render(..., camera)`).

### Legacy level / entity stack
`TransmuteCore.level.TiledLevel` (PNG-indexed) and `TransmuteCore.ecs.*` (`Object`, `Mob`)
remain available for existing projects. New agent/human games should not invent parallel
TileMap/Entity types — use World / Body2D instead.

### Serialization
The engine includes a custom binary serialization system ("TinyDatabase"):
- `TinyDatabase` - Container for multiple TinyObjects
- `TinyObject` - Contains named fields (strings, arrays, etc.)
- Methods: `serializeToFile()` and `DeserializeFromFile()`
- Used for save games, level data, and configuration

### Sprites & Animation
- `Spritesheet` - Load and split sprite sheets into individual sprites
- `SpriteManager` - Key-based sprite retrieval with automatic key normalization
- `Animation` - Frame-based animation with configurable durations
- Sprites are cropped from sprite sheets based on grid coordinates

## Code Patterns

### Extending Transmute Core
```java
import TransmuteCore.core.TransmuteCore;
import TransmuteCore.core.GameConfig;
import TransmuteCore.core.Manager;
import TransmuteCore.core.interfaces.services.IRenderer;
import TransmuteCore.graphics.Context;
import TransmuteCore.state.StateManager;
import TransmuteCore.assets.AssetManager;

public class MyGame extends TransmuteCore {
    public MyGame(GameConfig config) {
        super(config);
    }

    @Override
    public void init() {
        // Initialize managers, load assets
        StateManager sm = new StateManager(this);
        getManager().setStateManager(sm);
        AssetManager.getGlobalInstance().load();
    }

    @Override
    public void update(Manager manager, double delta) {
        if (manager.getStateManager() != null) {
            manager.getStateManager().update(manager, delta);
        }
    }

    @Override
    public void render(Manager manager, IRenderer renderer) {
        Context ctx = (Context) renderer;
        if (manager.getStateManager() != null) {
            manager.getStateManager().render(manager, ctx);
        }
    }

    public static void main(String[] args) {
        GameConfig config = new GameConfig.Builder()
            .title("Game Title")
            .version("1.0")
            .dimensions(320, GameConfig.ASPECT_RATIO_SQUARE)
            .scale(3)
            .build();

        MyGame game = new MyGame(config);
        game.start();
    }
}
```

### Creating Game Objects
All game objects should extend `TransmuteCore.ecs.Object` which implements:
- `Updatable` interface (update method)
- `Renderable` interface (render method)
- `Initializable` interface (init method)

Objects are managed by `ObjectManager` which handles batch update/render calls.

### Working with the Context
The `Context` is the rendering canvas. Always render through it:
- `ctx.renderBitmap(bitmap, x, y)` - Draw images
- `ctx.renderFilledRectangle(x, y, w, h, color)` - Draw filled shapes
- `ctx.renderText(text, x, y, color)` - Draw text (requires font set with `ctx.setFont()`)
- Colors use `Color.toPixelInt(r, g, b, a)` format

### Aspect Ratios
Use predefined constants in GameConfig.Builder when creating game window:
- `GameConfig.ASPECT_RATIO_WIDESCREEN` - 16:9 aspect ratio
- `GameConfig.ASPECT_RATIO_SQUARE` - 4:3 aspect ratio

Height is automatically calculated from width and ratio when using `dimensions(width, aspectRatio)`.

## Performance Notes

- The Context renders every pixel per frame - minimize overdraw
- Use `TiledLevel` viewport culling for large levels (only visible tiles render)
- Assets are cached after loading - avoid repeated `AssetManager.load()` calls
- Target FPS is configurable via GameConfig: `.targetFPS(60)`
- Enable FPS logging via GameConfig: `.fpsVerbose(true)` for performance debugging

## Pathfinding

The engine includes A* pathfinding in `TransmuteCore.ecs.pathfinding.AStar` for grid-based navigation.

## Documentation Resources

When helping users or making code changes, reference these documentation resources:

### For Beginners
- `docs/GETTING_STARTED.md` - Initial setup and configuration
- `docs/tutorials/01-hello-world.md` - First game walkthrough

### Tutorials (Progressive Learning Path)
1. `docs/tutorials/01-hello-world.md` - Basic game structure
2. `docs/tutorials/02-sprites-and-animation.md` - Visual assets
3. `docs/tutorials/03-input-and-movement.md` - Player controls
4. `docs/tutorials/04-collision-detection.md` - Collision systems
5. `docs/tutorials/05-state-management.md` - Game states and menus
6. `docs/tutorials/06-audio-system.md` - Sound and music
7. `docs/tutorials/07-level-design.md` - Tile-based levels

### Reference Guides
- `AGENTS.md` - Agent/automation golden path (prompt + assets → verify)
- `CONTEXT.md` - Domain vocabulary
- `docs/COOKBOOK.md` - Short recipes (verify, GameSpec, SimulatedInput)
- `examples/hello` - Reference game with headless verify
- `docs/GETTING_STARTED.md` - Initial setup

### Project Generator
- `packages/cli/` - CLI tool for scaffolding new projects with multiple templates

## Common Issues

- **Missing Assets**: Prefer `AssetPack` / `gamespec.properties`; keys are lowercase, paths keep case
- **Input Not Working**: Use `manager.getInputHandler()` (works headless via SimulatedInput)
- **Rendering Issues**: Verify BufferStrategy is created before rendering (first frame may be skipped)
- **State Errors**: Don't push states with duplicate names; use public `StateManager.pop()`
- **Can't verify without a window**: Use `GameHarness` + `FrameAssert` with `headless(true)`

For more recipes, see `docs/COOKBOOK.md` and `AGENTS.md`.
