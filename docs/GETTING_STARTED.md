# Getting Started with TransmuteCore

Welcome to TransmuteCore! This guide will help you set up your development environment and create your first game with the engine.

## What is TransmuteCore?

TransmuteCore is a high-performance 2D pixel game engine for Java. It features:

- Custom pixel-level rendering for retro-style games
- Fixed timestep game loop (60 FPS)
- Built-in sprite and animation systems
- State management for menus and game screens
- Tile-based level support
- Comprehensive input handling
- Audio playback support
- Binary serialization for save games

## Prerequisites

Before you begin, make sure you have:

- **Java Development Kit (JDK) 17 or higher** - [Download from Adoptium](https://adoptium.net/)
- **A terminal** (Terminal on macOS/Linux, Command Prompt or PowerShell on Windows)
- **An IDE** (optional but recommended):
  - [IntelliJ IDEA](https://www.jetbrains.com/idea/)
  - [Eclipse](https://www.eclipse.org/)
  - [Visual Studio Code](https://code.visualstudio.com/) with Java extensions

### Verify Java Installation

Open a terminal and run:

```bash
java -version
```

You should see output indicating Java 17 or higher is installed.

## Installation

### Install the Transmute CLI

The Transmute CLI is a project generator that creates new games with templates and configurations.

#### Unix/macOS

```bash
curl -fsSL https://raw.githubusercontent.com/transmute-games/transmute-core/master/scripts/install-cli.sh | sh
```

#### Windows (PowerShell)

```powershell
irm https://raw.githubusercontent.com/transmute-games/transmute-core/master/scripts/install-cli.ps1 | iex
```

The installer will:
- Download the latest CLI from GitHub Releases
- Install it to `~/.local/bin` (Unix/macOS) or `%USERPROFILE%\bin` (Windows)
- Guide you through adding it to your PATH if needed

#### Verify Installation

Check that the CLI is installed correctly:

```bash
transmute version
```

You should see the version information for the Transmute CLI.

### Manual Installation (Alternative)

If you prefer manual installation:

1. Visit [GitHub Releases](https://github.com/transmute-games/transmute-core/releases)
2. Download the latest CLI release:
   - `transmute-cli.jar`
   - `transmute` (Unix/macOS) or `transmute.bat` (Windows)
3. Place them in a directory on your PATH
4. Make executable (Unix/macOS): `chmod +x transmute`

## Creating Your First Game

### Using the CLI (Recommended)

The easiest way to create a new game is with the `transmute` CLI:

```bash
transmute new my-first-game
```

This will prompt you to select a template:

- **basic** - Minimal game structure (recommended for beginners)
- **platformer** - 2D platformer with player, platforms, and physics
- **rpg** - Top-down RPG with tile maps and grid-based movement

For your first game, choose **basic**.

### Navigate to Your Project

```bash
cd my-first-game
```

### Project Structure

Your new project will have this structure:

```
my-first-game/
├── build.gradle           # Build configuration with TransmuteCore dependency
├── settings.gradle        # Gradle settings
├── gradlew               # Gradle wrapper (Unix/macOS)
├── gradlew.bat           # Gradle wrapper (Windows)
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/myfirstgame/
│       │       └── Game.java     # Your main game class
│       └── resources/             # Assets go here
│           └── fonts/
│               └── font.png      # Default font included
├── README.md
└── .gitignore
```

### Run Your Game

The generated project includes a working example. Run it with:

```bash
./gradlew run
```

You should see a window with a simple game running!

## Next Steps

Now that you have TransmuteCore set up, you're ready to learn how to build games with the engine.

### Follow the Tutorials

We've created a series of progressive tutorials that cover all engine features:

1. **[Hello World](tutorials/01-hello-world.md)** - Learn the basic game structure by displaying text
2. **[Sprites & Animation](tutorials/02-sprites-and-animation.md)** - Load and animate sprite sheets
3. **[Input & Movement](tutorials/03-input-and-movement.md)** - Handle keyboard and mouse input
4. **[Collision Detection](tutorials/04-collision-detection.md)** - Implement AABB and circle collision
5. **[State Management](tutorials/05-state-management.md)** - Create menus and manage game states
6. **[Audio System](tutorials/06-audio-system.md)** - Add sound effects and music
7. **[Level Design](tutorials/07-level-design.md)** - Create tile-based levels from images

**Start with:** [Tutorial 1: Hello World](tutorials/01-hello-world.md)

### Explore the Documentation

- **[WARP.md](../WARP.md)** - Complete architecture overview and core concepts
- **[Cookbook](COOKBOOK.md)** - Code recipes and common patterns
- **[Serialization Guide](SERIALIZATION.md)** - Save and load game data
- **[Troubleshooting](TROUBLESHOOTING.md)** - Solutions to common issues

## Common Issues

### "Command not found: transmute"

If you get this error, the CLI installation directory is not in your PATH. See Step 4 above to add it.

### "Could not resolve: com.github.transmute-games.transmute-core:transmute-core"

This usually means:
1. **No internet connection** - Generated projects download TransmuteCore from JitPack, which requires an internet connection
2. **First-time build** - JitPack builds artifacts on-demand the first time they're requested (can take 1-2 minutes)
3. **Invalid version** - The specified version doesn't exist. Check [available releases](https://github.com/transmute-games/transmute-core/releases)

You can check JitPack build status at: `https://jitpack.io/#transmute-games/transmute-core`

### Building from Source (For Contributors)

If you want to contribute to TransmuteCore development:

```bash
# Clone the repository
git clone https://github.com/transmute-games/transmute-core
cd transmute-core

# Build the entire project
./gradlew build

# Publish to local Maven for development
./gradlew :transmute-core:publishToMavenLocal

# Install CLI locally
./gradlew :transmute-cli:install

This means TransmuteCore isn't published to your local Maven repository. Run:

```bash
cd /path/to/transmute-core
./gradlew :transmute-core:publishToMavenLocal
```

### "Permission Denied" when running gradlew

Make the Gradle wrapper executable:

```bash
chmod +x gradlew
```

### "Font not found" error

Make sure you have a font file in `src/main/resources/fonts/`. The CLI-generated projects include a default font, but if you're creating a project manually, you'll need to add one.

## Available Templates

The Transmute CLI offers multiple project templates:

### basic
Minimal game with simple rendering. Good starting point for learning.

**Includes:**
- Basic game loop setup
- Simple rendering example
- Font and text rendering
- Project structure with build configuration

### platformer
2D platformer starter with physics and collision detection.

**Includes:**
- Player class with jump mechanics
- Platform class for level geometry
- Basic gravity implementation
- Collision detection stubs

### rpg
Top-down RPG starter with tile-based maps.

**Includes:**
- Entity base class for characters
- TileMap class for level design
- Grid-based movement system stubs

## Building and Running

All generated projects use Gradle for builds:

```bash
# Run the game
./gradlew run

# Build the project
./gradlew build

# Run tests
./gradlew test

# Create a distributable JAR
./gradlew fatJar
```

## IDE Setup

### IntelliJ IDEA

1. Open IntelliJ IDEA
2. Select "Open" and navigate to your project directory
3. Select the `build.gradle` file
4. Click "Open as Project"
5. IntelliJ will automatically import the Gradle project

### Eclipse

1. Install the Gradle plugin (Buildship)
2. File → Import → Gradle → Existing Gradle Project
3. Navigate to your project directory
4. Click "Finish"

### VS Code

1. Install the "Extension Pack for Java"
2. Open your project folder
3. VS Code will automatically detect the Gradle project

## Getting Help

If you run into issues:

1. Check the [Troubleshooting Guide](TROUBLESHOOTING.md)
2. Read the relevant tutorial in `docs/tutorials/`
3. Search [GitHub Issues](https://github.com/transmute-games/transmute-core/issues)
4. Ask in [GitHub Discussions](https://github.com/transmute-games/transmute-core/discussions)

## What You've Learned

✅ Installed Java and verified the installation  
✅ Cloned and built TransmuteCore  
✅ Installed the Transmute CLI  
✅ Created and ran your first game project  
✅ Understood the project structure  

## Ready to Build?

Head over to [Tutorial 1: Hello World](tutorials/01-hello-world.md) to start learning the engine and build your first game from scratch!
