# 🧙🏼 Transmute CLI

Command-line tool for scaffolding new TransmuteCore game projects.

## Installation

### Quick Install (Recommended)

#### Unix/macOS
```bash
curl -fsSL https://raw.githubusercontent.com/transmute-games/transmute-core/master/scripts/install-cli.sh | sh
```

#### Windows (PowerShell)
```powershell
irm https://raw.githubusercontent.com/transmute-games/transmute-core/master/scripts/install-cli.ps1 | iex
```

The installer will:
- Download the latest CLI release
- Install to `~/.local/bin` (Unix/macOS) or `%USERPROFILE%\bin` (Windows)
- Guide you through PATH setup if needed

### Manual Installation

1. Download the latest release from [GitHub Releases](https://github.com/transmute-games/transmute-core/releases)
2. Download `transmute-cli.jar` and the appropriate wrapper script:
   - Unix/macOS: `transmute`
   - Windows: `transmute.bat`
3. Place them in a directory on your PATH (e.g., `~/.local/bin`)
4. Make the script executable (Unix/macOS only): `chmod +x transmute`

### Install Specific Version

```bash
# Unix/macOS
TRANSMUTE_CLI_VERSION=0.1.0-ALPHA curl -fsSL https://raw.githubusercontent.com/transmute-games/transmute-core/master/scripts/install-cli.sh | sh

# Windows PowerShell
$env:TRANSMUTE_CLI_VERSION="0.1.0-ALPHA"; irm https://raw.githubusercontent.com/transmute-games/transmute-core/master/scripts/install-cli.ps1 | iex
```

### For Contributors

If you're developing the CLI itself:

```bash
# From the monorepo root
./gradlew :transmute-cli:install

# Or build the fat JAR manually
./gradlew :transmute-cli:fatJar
java -jar packages/cli/build/libs/transmute-cli-0.1.0-ALPHA-all.jar new my-game
```

## Usage

After installation, use the `transmute` command directly:

### Create a New Project

Interactive mode (recommended):
```bash
transmute new my-game
```

Non-interactive with defaults:
```bash
transmute new my-game -y
```

Specify a template:
```bash
transmute new my-game --template platformer
```

### Available Commands

```
transmute new <project-name> [options]   Create a new project
transmute templates                      List available templates
transmute help                           Show help message
transmute version                        Show version information
```

### Options

- `-t, --template <name>` - Specify template (default: basic)
- `-y, --no-interactive` - Skip interactive prompts

## Available Templates

### basic
Minimal game with simple rendering. Good starting point for any game type.

**Includes:**
- Basic game loop setup
- Simple rendering example
- Project structure with build configuration

### platformer
2D platformer with physics and collision detection.

**Includes:**
- Player class with jump mechanics
- Platform class for level geometry
- Basic gravity implementation
- Collision detection stubs

### rpg
Top-down RPG with tile-based maps.

**Includes:**
- Entity base class for characters
- TileMap class for level design
- Grid-based movement system stubs

## Project Structure

Generated projects follow this structure:

```
my-game/
├── build.gradle           # Build configuration
├── settings.gradle        # Gradle settings
├── gradlew               # Gradle wrapper (Unix)
├── gradlew.bat           # Gradle wrapper (Windows)
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/mygame/
│   │   │       └── Game.java
│   │   └── resources/    # Assets go here
├── README.md
└── .gitignore
```

## Building Generated Projects

After generating a project:

```bash
cd my-game

# Build
./gradlew build

# Run
./gradlew run

# Create distributable JAR
./gradlew fatJar
```

## Requirements

- Java 17 or higher
- Internet connection (for downloading TransmuteCore from JitPack)

Generated projects automatically download TransmuteCore from [JitPack](https://jitpack.io) - no manual setup required!

## Development

### Building the CLI

```bash
# From monorepo root
./gradlew :transmute-cli:build

# Create distributable JAR
./gradlew :transmute-cli:dist
```

### Testing

```bash
# Generate a test project
transmute new test-game -y

# Build and run it
cd test-game
./gradlew run
```

## Examples

### Create a Basic Game

```bash
transmute new pong -t basic
cd pong
# Edit src/main/java/com/example/pong/Game.java
./gradlew run
```

### Create a Platformer

```bash
transmute new super-jump -t platformer
cd super-jump
# Implement platformer logic using Player and Platform classes
./gradlew run
```

### Create an RPG

```bash
transmute new quest -t rpg
cd quest
# Implement RPG logic using Entity and TileMap classes
./gradlew run
```

## Uninstallation

```bash
./gradlew :transmute-cli:uninstall
```

## Troubleshooting

### "Command not found: transmute"

1. Make sure you've installed the CLI:
   ```bash
   ./gradlew :transmute-cli:install
   ```

2. Verify the install directory is in your PATH:
   ```bash
   # Check current PATH
   echo $PATH  # Unix/macOS
   echo %PATH%  # Windows
   ```

3. If not in PATH, add it to your shell configuration:
   ```bash
   # Bash/Zsh (~/.bashrc or ~/.zshrc)
   export PATH="$PATH:$HOME/.local/bin"

   # Fish (~/.config/fish/config.fish)
   set -U fish_user_paths $HOME/.local/bin $fish_user_paths
   ```

4. Reload your shell:
   ```bash
   source ~/.bashrc  # or ~/.zshrc
   # Or just open a new terminal
   ```

### "Could not resolve: com.github.transmute-games.transmute-core:transmute-core"

This usually means:
1. No internet connection (JitPack requires internet to download dependencies)
2. The specified version doesn't exist - check [available releases](https://github.com/transmute-games/transmute-core/releases)
3. JitPack is building the artifact for the first time (can take 1-2 minutes)

You can check the build status at: `https://jitpack.io/#transmute-games/transmute-core`

### Permission Denied (gradlew)

Make gradlew executable:
```bash
chmod +x gradlew
```

## License

MIT License - See main project for details.
