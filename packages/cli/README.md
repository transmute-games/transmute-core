# Transmute CLI

Command-line tool for scaffolding new TransmuteCore game projects.

## Installation

### Quick Install (Recommended)

```bash
# From the monorepo root
./gradlew :transmute-cli:install
```

This installs the CLI to:
- **macOS/Linux**: `~/.local/bin/`
- **Windows**: `%USERPROFILE%\bin\`

Make sure the install directory is in your PATH:

```bash
# macOS/Linux (Bash/Zsh)
export PATH="$PATH:$HOME/.local/bin"

# macOS/Linux (Fish)
set -U fish_user_paths $HOME/.local/bin $fish_user_paths

# Windows
set PATH=%PATH%;%USERPROFILE%\bin
```

### Manual Installation

If you prefer manual installation:

```bash
# Build the JAR
./gradlew :transmute-cli:fatJar

# Use directly
java -jar packages/cli/build/libs/transmute-cli-0.1.0-ALPHA-all.jar new my-game

# Or create an alias
alias transmute='java -jar /path/to/transmute-cli-0.1.0-ALPHA-all.jar'
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
- TransmuteCore published to Maven Local
  ```bash
  # From monorepo root
  ./gradlew :transmute-core:publishToMavenLocal
  ```

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

### "Could not find games.transmute:transmute-core"

Make sure TransmuteCore is published to Maven Local:
```bash
cd /path/to/transmute-core
./gradlew :transmute-core:publishToMavenLocal
```

### Permission Denied (gradlew)

Make gradlew executable:
```bash
chmod +x gradlew
```

## License

MIT License - See main project for details.
