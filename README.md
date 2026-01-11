# TransmuteCore Game Engine

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?logo=openjdk&logoColor=white)](https://adoptium.net/)
[![JitPack](https://jitpack.io/v/transmute-games/transmute-core.svg)](https://jitpack.io/#transmute-games/transmute-core)
[![CLI Release](https://img.shields.io/github/v/release/transmute-games/transmute-core?filter=cli-*&label=CLI&color=success)](https://github.com/transmute-games/transmute-core/releases/latest)
[![GitHub Actions](https://img.shields.io/github/actions/workflow/status/transmute-games/transmute-core/release-cli.yml?label=CLI%20Build)](https://github.com/transmute-games/transmute-core/actions)

A high-performance 2D pixel game engine for Java

## Overview

TransmuteCore is a lightweight, high-performance 2D pixel game engine written in Java. It features custom pixel-level rendering with BufferedImage and DataBufferInt for maximum performance, making it ideal for retro-style games, pixel art projects, and educational purposes.

### Key Features

- 🎮 **Fixed Timestep Game Loop** - Consistent 60 FPS game logic with delta time support
- 🖼️ **Custom Pixel Rendering** - Direct pixel manipulation for maximum performance
- 📦 **Deferred Asset Loading** - Efficient resource management with batch loading
- 🎨 **Built-in Animation System** - Sprite sheets and frame-based animations
- 🎯 **State Management** - Stack-based state system for menus, gameplay, and more
- ⌨️ **Input Handling** - Comprehensive keyboard and mouse input with multiple states
- 🗺️ **Tile-Based Levels** - Load levels from PNG images with viewport culling
- 💾 **Serialization** - Custom binary serialization for save games and data
- 🔊 **Audio Support** - Built-in audio playback for sound effects and music
- 🛠️ **Developer Tools** - Logging system, debug utilities, and comprehensive error messages

## Quick Start

### Get the CLI

The fastest way to get started is with the Transmute CLI:

#### Unix/macOS
```bash
curl -fsSL https://raw.githubusercontent.com/transmute-games/transmute-core/master/scripts/install-cli.sh | sh
```

#### Windows (PowerShell)
```powershell
irm https://raw.githubusercontent.com/transmute-games/transmute-core/master/scripts/install-cli.ps1 | iex
```

#### Manual Installation
Download the latest release from [GitHub Releases](https://github.com/transmute-games/transmute-core/releases) and follow the installation instructions.

### Create Your First Game

```bash
# Create a new project
transmute new my-game
cd my-game

# Run your game
./gradlew run
```

### Prerequisites

- Java Development Kit (JDK) 17 or higher
- Internet connection (for downloading dependencies via JitPack)

### For Contributors

If you want to contribute to TransmuteCore development:

```bash
# Clone the repository
git clone https://github.com/transmute-games/transmute-core
cd transmute-core

# Build the entire project
./gradlew build

# Publish the engine to local Maven (for local development)
./gradlew :transmute-core:publishToMavenLocal

# Install the CLI locally
./gradlew :transmute-cli:install
```

### Add to Existing Project

To use TransmuteCore in an existing Gradle project, add to your `build.gradle`:

```gradle
repositories {
    mavenCentral()
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.transmute-games.transmute-core:transmute-core:v1.0.0'
}
```

Replace `v1.0.0` with the [latest release version](https://github.com/transmute-games/transmute-core/releases).

### Hello World Example

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
        ctx.renderText("Hello, TransmuteCore!", 50, 100,
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

## Documentation

### Getting Started
- **[Getting Started Guide](docs/GETTING_STARTED.md)** - Complete setup and first steps
- **[Hello World Tutorial](docs/tutorials/01-hello-world.md)** - Your first TransmuteCore game

### Tutorials
Progressive, hands-on tutorials covering all engine features:
1. **[Hello World](docs/tutorials/01-hello-world.md)** - Basic game structure
2. **[Sprites & Animation](docs/tutorials/02-sprites-and-animation.md)** - Visual assets and animation
3. **[Input & Movement](docs/tutorials/03-input-and-movement.md)** - Player controls
4. **[Collision Detection](docs/tutorials/04-collision-detection.md)** - AABB, circle, and spatial partitioning
5. **[State Management](docs/tutorials/05-state-management.md)** - Menus and game states
6. **[Audio System](docs/tutorials/06-audio-system.md)** - Sound effects and music
7. **[Level Design](docs/tutorials/07-level-design.md)** - Tile-based levels

### Reference Documentation
- **[WARP.md](WARP.md)** - Architecture overview and core concepts

## Building from Source

```bash
# Clone the repository
git clone https://github.com/transmute-games/transmute-core
cd transmute-core

# Build the entire project (core + CLI)
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

## Project Structure

```
transmute-core/
├── build.gradle                # Root build configuration
├── settings.gradle             # Multi-project configuration
├── packages/
│   ├── core/                   # TransmuteCore engine
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

## Core Concepts

### Game Loop

TransmuteCore uses a fixed timestep game loop:

1. **init()** - One-time initialization
2. **update(Manager, delta)** - Game logic updates (60 times per second)
3. **render(Manager, IRenderer)** - Rendering to pixel buffer

### Manager System

The `Manager` provides centralized access to all engine subsystems:

```java
Manager manager = TransmuteCore.getManager();
StateManager stateManager = manager.getStateManager();
AssetManager assetManager = manager.getAssetManager();
Input input = manager.getInput();
```

### Rendering Pipeline

1. **Context** - Custom pixel buffer with direct pixel manipulation
2. **Hardware Acceleration** - VolatileImage for GPU acceleration
3. **BufferStrategy** - Configurable double/triple buffering

## Project Generator

- **[transmute-cli](packages/cli/README.md)** - CLI tool for scaffolding new projects
- Multiple templates: basic, platformer, rpg
- Interactive project setup

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request. For major changes, please open an issue first to discuss what you would like to change.

### Development Setup

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Performance

TransmuteCore is designed for performance:

- Direct pixel manipulation via `DataBufferInt`
- Hardware-accelerated rendering with `VolatileImage`
- Viewport culling for large tile-based levels
- Efficient asset caching and management
- Configurable target FPS (default: 60)

### Benchmarks

On a typical modern system (2020+ hardware):
- 60 FPS stable at 1920x1080 with ~1000 sprites
- Sub-millisecond asset loading for typical game assets
- < 50ms startup time

## License

This project is licensed under the MIT License - see the [LICENSE](.github/LICENSE) file for details.

## Acknowledgments

- Inspired by classic 2D game engines
- Built for educational purposes and indie game development
- Community-driven development

## Support

- **Issues**: [GitHub Issues](https://github.com/transmute-games/transmute-core/issues)
- **Discussions**: [GitHub Discussions](https://github.com/transmute-games/transmute-core/discussions)
- **Documentation**: [Getting Started Guide](docs/GETTING_STARTED.md)

Made with ❤️ by the Transmute Games team

[Website](https://github.com/transmute-games) • [Documentation](docs/GETTING_STARTED.md) • [CLI Tool](packages/cli/README.md)
