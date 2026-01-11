# WARP.md

This file provides guidance to WARP (warp.dev) when working with code in this repository.

## Project Overview

TransmuteCore is a Java-based 2D pixel game engine designed for high-performance game development. The engine uses Java AWT for rendering with custom pixel-level manipulation through BufferedImage and DataBufferInt for maximum performance.

## Build & Development

### Prerequisites
- Java Development Kit (JDK) 17 or higher
- Gradle (wrapper included in project)
- IDE with Java support (IntelliJ IDEA, Eclipse, or VS Code recommended)

### Project Structure
The project uses Gradle for builds. Source files are in `TransmuteCore/src/` with compiled output managed by Gradle.

```
transmute-core/
├── build.gradle                # Build configuration
├── TransmuteCore/
│   └── src/
│       └── TransmuteCore/
│           ├── GameEngine/     # Core game loop and engine
│           ├── Graphics/       # Rendering and visual systems
│           ├── Input/          # Keyboard and mouse handling
│           ├── Objects/        # Game entities and objects
│           ├── States/         # State management system
│           ├── Level/          # Level and tile systems
│           ├── Serialization/  # Save/load functionality
│           └── System/         # Utilities, logging, exceptions
└── docs/                       # Documentation
    ├── tutorials/              # Step-by-step tutorials (01-07)
    ├── GETTING_STARTED.md      # Initial setup guide
    ├── COOKBOOK.md             # Code recipes and patterns
    ├── SERIALIZATION.md        # Save/load system guide
    ├── DEPLOYMENT.md           # Build and distribution
    ├── TROUBLESHOOTING.md      # Common issues and solutions
    └── DX_FEATURES.md          # Developer experience features
```

### Building
Build the project using Gradle:
```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Publish to local Maven repository
./gradlew publishToMavenLocal

# Generate Javadocs
./gradlew javadoc
```

### Creating a Game
This is a library/engine project. To create a game:
1. Extend the `TransmuteCore` class
2. Implement the required methods from the `Cortex` interface
3. See the [transmute-starter](https://github.com/transmute-games/transmute-starter) repository for working examples
4. Follow the [tutorials](docs/tutorials/) for step-by-step guidance

## Core Architecture

### Game Loop & Lifecycle
The engine uses a fixed timestep game loop (default 60 FPS) with delta time calculations:
- `init()` - One-time initialization of game state
- `update(Manager manager, double delta)` - Game logic updates with delta time
- `render(Manager manager, Context ctx)` - Rendering to the custom pixel buffer

### Manager System
The `Manager` class is the central coordinator that provides access to all subsystems:
- `StateManager` - Game state stack (menus, gameplay, pause, etc.)
- `AssetManager` - Resource loading and caching (images, audio, fonts)
- `ObjectManager` - Game entity management
- `SpriteManager` - Sprite sheet parsing and animation
- `Input` - Keyboard and mouse input handling
- `GameWindow` - Window and canvas management

Access the manager globally via `TransmuteCore.getManager()`.

### Rendering Pipeline
1. **Context** (`TransmuteCore.Graphics.Context`) - Custom pixel buffer rendering system
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

### Level System
Two level types are provided:
- `Level` - Base level class with entity management
- `TiledLevel` - Tile-based levels loaded from PNG/JPG images
  - Each pixel color in the image maps to a tile type
  - Supports viewport culling for large levels
  - Configure with `setTileSize()` and `addTile(index, tile)`

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

### Extending TransmuteCore
```java
public class MyGame extends TransmuteCore {
    public MyGame() {
        super("Game Title", "1.0", 320, TransmuteCore.Square, 3);
    }
    
    @Override
    public void init() {
        // Initialize managers, load assets
        StateManager sm = new StateManager(this);
        getManager().setStateManager(sm);
        AssetManager.load();
    }
    
    @Override
    public void update(Manager manager, double delta) {
        if (manager.getStateManager() != null) {
            manager.getStateManager().update(manager, delta);
        }
    }
    
    @Override
    public void render(Manager manager, Context ctx) {
        if (manager.getStateManager() != null) {
            manager.getStateManager().render(manager, ctx);
        }
    }
}
```

### Creating Game Objects
All game objects should extend `TransmuteCore.Objects.Object` which implements:
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
Use predefined constants when creating game window:
- `TransmuteCore.WideScreen` - 16:9 aspect ratio (0x0)
- `TransmuteCore.Square` - 4:3 aspect ratio (0x1)

Height is automatically calculated from width and ratio.

## Performance Notes

- The Context renders every pixel per frame - minimize overdraw
- Use `TiledLevel` viewport culling for large levels (only visible tiles render)
- Assets are cached after loading - avoid repeated `AssetManager.load()` calls
- Target FPS is configurable with `setTargetFPS(fps)`
- Enable FPS logging with `setFPSVerbose(true)` for performance debugging

## Pathfinding

The engine includes A* pathfinding in `TransmuteCore.Objects.Pathfinding.AStar` for grid-based navigation.

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
- `docs/COOKBOOK.md` - Code recipes and common patterns
- `docs/SERIALIZATION.md` - TinyDatabase save/load system
- `docs/DEPLOYMENT.md` - Building and distributing games
- `docs/TROUBLESHOOTING.md` - Solutions to common problems
- `docs/DX_FEATURES.md` - Developer experience features

### Example Projects
- [transmute-starter](https://github.com/transmute-games/transmute-starter) - Production starter template

## Common Issues

- **Missing Assets**: Check asset names are lowercase when retrieving (AssetManager normalizes keys)
- **Input Not Working**: Ensure canvas has focus and input listeners are attached
- **Rendering Issues**: Verify BufferStrategy is created before rendering (first frame may be skipped)
- **State Errors**: Don't push states with duplicate names to StateManager

For more troubleshooting guidance, see `docs/TROUBLESHOOTING.md`.
