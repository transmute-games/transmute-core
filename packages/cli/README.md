# Transmute CLI

Command-line tool for scaffolding new TransmuteCore game projects.

## Installation

### Build from Source

```bash
# From the monorepo root
./gradlew :transmute-cli:fatJar

# The distributable JAR will be in packages/cli/build/libs/
```

### Create Alias (Optional)

```bash
# macOS/Linux
alias transmute='java -jar /path/to/transmute-cli-all.jar'

# Or add to ~/.bashrc, ~/.zshrc, or ~/.config/fish/config.fish
```

## Usage

### Create a New Project

Interactive mode (recommended):
```bash
java -jar transmute-cli-all.jar new my-game
```

Non-interactive with defaults:
```bash
java -jar transmute-cli-all.jar new my-game -y
```

Specify a template:
```bash
java -jar transmute-cli-all.jar new my-game --template platformer
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
java -jar packages/cli/build/libs/transmute-cli-all.jar new test-game -y

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

## Troubleshooting

### "Command not found: transmute"

If using an alias, make sure it's in your shell's configuration file and reload:
```bash
source ~/.bashrc  # or ~/.zshrc, ~/.config/fish/config.fish
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
