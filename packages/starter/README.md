# Transmute Starter

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-8%2B-orange.svg)](https://www.java.com/)

A minimal, no-fluff starter template for building games with the [TransmuteCore Pixel Rendering Engine](https://github.com/transmute-games/transmute-core).

## ⚡ Quick Start

Get up and running in under a minute:

```bash
# Clone the repository with submodules
git clone --recursive https://github.com/transmute-games/transmute-starter my-awesome-game
cd my-awesome-game

# Run setup script (Unix/macOS/Linux)
./setup.sh

# Or on Windows
setup.bat
```

That's it! The setup script will initialize the engine submodule and configure your project.

### Running Your Game

**Using convenience scripts:**
```bash
# Unix/macOS/Linux
./run.sh

# Windows
run.bat

# Or use Make
make run
```

**Using your IDE:**
1. Open the project in Eclipse or IntelliJ IDEA
2. Import `transmute-starter` as the main project
3. Ensure `transmute-core/TransmuteCore` is imported as the `Engine` module
4. Run `GameEngine.Game.main()`

## 📦 What's Included

- ✅ **Pre-configured IDE Projects** - Ready-to-use Eclipse and IntelliJ IDEA configurations
- ✅ **TransmuteCore Engine** - Included as a Git submodule
- ✅ **Game State System** - Example Loading and Game states
- ✅ **Asset Management** - Deferred asset loading with AssetManager
- ✅ **Resource Loader** - Pre-configured for fonts, sprites, and audio
- ✅ **Build Scripts** - Cross-platform build and run scripts
- ✅ **Example Font** - Basic bitmap font included

## 🎮 Project Structure

```
transmute-starter/
├── setup.sh / setup.bat      # Initial project setup
├── build.sh / build.bat      # Build scripts
├── run.sh / run.bat          # Run scripts
├── Makefile                  # Make targets
├── transmute-core/           # Engine submodule
└── transmute-starter/
    ├── src/
    │   ├── GameEngine/
    │   │   └── Game.java           # Main entry point
    │   ├── GameStates/
    │   │   ├── LoadingState.java   # Asset loading state
    │   │   └── GameState.java      # Main game state
    │   └── Utilities/
    │       ├── Library.java        # Game constants
    │       └── ResourceLoader.java # Asset registration
    └── res/                    # Game resources
        └── fonts/
            └── font.png        # Default bitmap font
```

## 🚀 Next Steps

### 1. Customize Your Game

Edit `transmute-starter/src/Utilities/Library.java`:
```java
public static final String GAME_TITLE = "My Awesome Game";
public static final String GAME_VERSION = "1.0.0";
```

### 2. Add Assets

Place your assets in `transmute-starter/res/`:
- `sprites/` - Your sprite images
- `fonts/` - Bitmap fonts
- `audio/` - Sound effects and music

### 3. Register Assets

Update `transmute-starter/src/Utilities/ResourceLoader.java`:
```java
public static void load() {
    Font.initializeDefaultFont("fonts/font.png");
    new Image("player", Library.RESOURCE_PATH + "/sprites/player.png");
    new Audio("jump", Library.RESOURCE_PATH + "/audio/jump.wav");
    AssetManager.load();
}
```

### 4. Implement Game Logic

Edit `transmute-starter/src/GameStates/GameState.java` to add your game mechanics.

### 5. Configure Window Settings

Adjust in `transmute-starter/src/GameEngine/Game.java`:
```java
new Game(
    Library.GAME_TITLE,
    Library.GAME_VERSION,
    640,                    // Width
    TransmuteCore.Square,   // Aspect ratio (Square or WideScreen)
    1                       // Scale multiplier
);
```

## 🛠️ Build Commands

### Using Scripts

```bash
# Build only
./build.sh        # Unix/macOS/Linux
build.bat         # Windows

# Run (auto-builds if needed)
./run.sh          # Unix/macOS/Linux
run.bat           # Windows

# Clean build artifacts
make clean        # Unix/macOS/Linux only
```

### Using Make (Unix/macOS/Linux)

```bash
make help         # Show all commands
make setup        # Run setup script
make build        # Compile project
make run          # Build and run
make clean        # Remove compiled files
make all          # Setup, build, and run
```

## 📚 Documentation

- **TransmuteCore Engine Docs**: [github.com/transmute-games/transmute-core](https://github.com/transmute-games/transmute-core)
- **WARP.md**: See `WARP.md` for AI agent development guidelines
- **Original README**: See `.github/README.md`

## 🔧 Troubleshooting

### Submodule Not Initialized

**Error**: `transmute-core` directory is empty or missing files

**Solution**:
```bash
git submodule update --init --recursive
```

### Java Version Issues

**Error**: Compilation fails with version errors

**Solution**: Ensure you have JDK 8 or higher installed:
```bash
java -version
javac -version
```

### IDE Doesn't Recognize Engine Module

**Error**: Eclipse/IntelliJ can't find TransmuteCore classes

**Solution**:
1. Ensure `transmute-core/TransmuteCore` is imported as a separate module
2. The module should be named `Engine` (as referenced in `.classpath`)
3. Check that the Engine module is in the build path

### Build Scripts Don't Work

**Error**: Permission denied on Unix/macOS/Linux

**Solution**:
```bash
chmod +x setup.sh build.sh run.sh
```

### Game Window Doesn't Appear

**Error**: Game compiles but no window shows

**Solution**:
- Check that `stateManager.push(new LoadingState(stateManager))` is called in `Game.init()`
- Verify assets load correctly in `ResourceLoader.load()`
- Enable FPS verbose mode to see console output: `setFPSVerbose(true)` in `Game.init()`

## 🎯 Key Concepts

### Game Loop

TransmuteCore uses a fixed timestep game loop running at 60 FPS:
- `init()` - Called once at startup
- `update(Manager, delta)` - Called every frame for logic
- `render(Manager, Context)` - Called every frame for drawing

### State Management

States are managed via a stack - only the top state is active:
```java
stateManager.push(new GameState(stateManager));  // Push new state
// State must have unique name
```

### Asset Loading

Assets use deferred loading:
1. Register assets (instantiate Image/Audio/Font objects)
2. Call `AssetManager.load()` to actually load them
3. Check `AssetManager.isLoaded()` to know when complete
4. Access via `AssetManager.getImage("name")`, etc.

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](.github/LICENSE) file for details.

## 🤝 Contributing

Contributions are welcome! Please feel free to submit issues or pull requests.

---

**Ready to build your game?** Start by running `./setup.sh` (or `setup.bat` on Windows) and dive in! 🎮
