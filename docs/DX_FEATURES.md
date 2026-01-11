# Developer Experience Features

TransmuteCore includes comprehensive developer experience tools to make game development faster, easier, and more enjoyable.

## Table of Contents

- [Debug Tools](#debug-tools)
- [Performance Analysis](#performance-analysis)
- [Error Handling](#error-handling)
- [Logging & Diagnostics](#logging--diagnostics)
- [Validation](#validation)
- [Hot Reload](#hot-reload)
- [State Serialization](#state-serialization)
- [Utilities](#utilities)

---

## Debug Tools

### Debug Overlay (F3)

Real-time performance overlay showing FPS, memory usage, and game state.

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
    // Your rendering code...
    debugOverlay.render(manager, ctx); // Always render last!
}
```

**Features:**
- Current, average, min/max FPS
- Frame time in milliseconds (color-coded: green = good, yellow = warning, red = bad)
- Memory usage (used/total/max in MB, percentage)
- Current game state name
- Entity count
- Toggle with **F3** key

**Location:** `TransmuteCore/System/Debug/DebugOverlay.java`

---

### Debug Console (~)

In-game command console for runtime debugging and control.

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
        // Spawn logic...
        console.println("Spawned: " + args[0]);
    });
    
    console.registerCommand("god", args -> {
        player.setInvincible(!player.isInvincible());
        console.println("God mode: " + (player.isInvincible() ? "ON" : "OFF"));
    });
}

@Override
public void update(Manager manager, double delta) {
    console.update(manager, delta);
}

@Override
public void render(Manager manager, Context ctx) {
    // Your rendering code...
    console.render(manager, ctx); // Always render last!
}
```

**Built-in Commands:**
- `help` - List all available commands
- `clear` - Clear console output
- `fps <value>` - Set target FPS
- `timescale <value>` - Set game time scale (0.0 - 2.0)
- `screenshot` - Capture screenshot
- `profiler <print|clear>` - Control profiler
- `state` - Dump game state to JSON
- `gc` - Run garbage collection
- `exit|quit` - Close the game

**Features:**
- Toggle with **~** key (grave/tilde)
- Command history with Up/Down arrows
- Custom command registration
- Auto-complete command suggestions
- Scrolling output buffer (100 lines)
- Color-coded errors
- Blinking cursor

**Location:** `TransmuteCore/System/Debug/Console.java`

---

## Performance Analysis

### Profiler

Track performance of different game systems to identify bottlenecks.

```java
import TransmuteCore.System.Debug.Profiler;

@Override
public void update(Manager manager, double delta) {
    Profiler.begin("physics");
    // Physics code...
    Profiler.end("physics");
    
    Profiler.begin("ai");
    // AI code...
    Profiler.end("ai");
    
    Profiler.begin("rendering");
    // Rendering prep...
    Profiler.end("rendering");
}

// Print results to console
Profiler.printResults();

// Or export to CSV for analysis
String csv = Profiler.exportToCSV();
// Save csv to file for Excel/Google Sheets
```

**Features:**
- Track timing of labeled sections
- Automatic calculation of min/max/average times
- 60-frame history for recent performance
- CSV export for external analysis
- Zero-cost when disabled
- Thread-safe operation

**Output Example:**
```
========== PROFILER RESULTS ==========
Section              Calls    Total (ms)     Avg (ms)     Min (ms)     Max (ms)
physics               1000        150.234        0.150        0.120        0.450
ai                    1000         85.123        0.085        0.050        0.200
rendering             1000        200.456        0.200        0.180        0.350
======================================
```

**Location:** `TransmuteCore/System/Debug/Profiler.java`

---

### Screenshot Utility (F12)

Capture screenshots instantly during development or gameplay.

```java
import TransmuteCore.System.Screenshot;

@Override
public void update(Manager manager, double delta) {
    Screenshot.update(); // Check for F12 press
}

@Override
public void render(Manager manager, Context ctx) {
    // Your rendering...
    Screenshot.capture(ctx); // Capture if requested
}

// Programmatic capture
Screenshot.captureNow(ctx); // Immediate capture
Screenshot.captureAs(ctx, "bug_report"); // Custom filename

// Configure
Screenshot.setOutputDirectory("screenshots"); // Default
```

**Features:**
- F12 hotkey for instant capture
- Automatic timestamped filenames
- PNG format with full quality
- Custom output directory
- Programmatic API for automated captures

**Output:** `screenshots/screenshot_2026-01-10_22-07-45.png`

**Location:** `TransmuteCore/System/Screenshot.java`

---

### State Inspector

Dump game state, levels, and object lists to JSON for debugging.

```java
import TransmuteCore.System.Debug.StateInspector;

// Dump entire game state
StateInspector.dumpGameState(manager);
// Output: debug/state_2026-01-10_22-07-45.json

// Dump level data
StateInspector.dumpLevel(level);
// Output: debug/level_2026-01-10_22-07-45.json

// Dump all objects
StateInspector.dumpObjectList(objectManager);
// Output: debug/objects_2026-01-10_22-07-45.json

// Inspect specific object
StateInspector.inspectObject(player, "debug/player.json");
```

**Features:**
- JSON export for easy parsing
- Timestamped automatic filenames
- Includes memory usage, FPS, state stack
- Object positions and types
- Perfect for bug reports

**Example Output (debug/state_*.json):**
```json
{
  "timestamp": "Fri Jan 10 22:07:45 EST 2026",
  "engine": {
    "name": "TransmuteCore",
    "targetFPS": 60
  },
  "stateManager": {
    "stackSize": 1,
    "currentState": {
      "name": "gameState",
      "type": "GameState"
    }
  },
  "memory": {
    "used": 45678912,
    "total": 134217728,
    "max": 536870912
  }
}
```

**Location:** `TransmuteCore/System/Debug/StateInspector.java`

---

## Error Handling

### Enhanced Exceptions

Get helpful, context-rich error messages with suggestions for fixing issues.

#### AssetNotFoundException

```java
try {
    Bitmap sprite = AssetManager.getImage("player");
} catch (AssetNotFoundException e) {
    // Detailed error with:
    // - Asset name and type
    // - Paths that were searched
    // - Suggestions for fixing
    System.err.println(e.getMessage());
}
```

**Output:**
```
========== ASSET NOT FOUND ==========
Asset Name: player
Asset Type: image

Searched Paths:
  - /project/assets/images/player.png
  - /project/res/player.png

How to Fix:
  1. Check that the asset file exists in the specified location
  2. Verify the file name and extension are correct (case-sensitive on some systems)
  3. Ensure the asset is registered before calling AssetManager.load()
  4. Check that the file path is relative to the project root or is an absolute path
  5. Verify the asset file is not corrupted and is in the correct format
=====================================
```

#### InvalidConfigurationException

```java
throw new InvalidConfigurationException(
    "gameWidth", 
    -100, 
    "Positive integer greater than 0"
);
```

#### RenderException

```java
throw new RenderException(
    "renderBitmap", 
    "playerSprite",
    Arrays.asList("Check that sprite is not null", "Verify coordinates are valid")
);
```

**Location:** `TransmuteCore/System/Exceptions/`

---

## Logging & Diagnostics

### Enhanced Logger

Structured logging with file output and automatic rotation.

```java
import TransmuteCore.System.Logger;

// Configure log level
Logger.setLevel(Logger.Level.DEBUG); // DEBUG, INFO, WARN, ERROR

// Enable file logging
Logger.enableFileLogging("logs/game.log");
Logger.setMaxFileSize(10 * 1024 * 1024); // 10 MB
Logger.setMaxBackupFiles(5); // Keep 5 old logs

// Log messages
Logger.debug("Player position: (%d, %d)", x, y);
Logger.info("Level loaded successfully");
Logger.warn("Low memory: %d MB remaining", freeMemory);
Logger.error("Failed to load texture", exception);

// Configure output format
Logger.setShowTimestamp(true);
Logger.setShowThreadName(false);

// Disable file logging when done
Logger.disableFileLogging();
```

**Features:**
- Four log levels: DEBUG, INFO, WARN, ERROR
- Dual output: console + file
- Automatic log rotation (size-based)
- Keeps configurable number of backups (default: 5)
- Timestamped messages
- Optional thread names
- Printf-style formatting
- Exception stack traces

**File Output:**
```
logs/
  game.log          (current)
  game.log.1        (previous)
  game.log.2        (older)
  game.log.3        (older)
  game.log.4        (oldest)
```

**Location:** `TransmuteCore/System/Logger.java`

---

## Validation

### Startup Validator

Validate game environment before starting the game loop.

```java
import TransmuteCore.System.StartupValidator;
import TransmuteCore.System.ValidationResult;

@Override
public void init() {
    // Create validator
    StartupValidator validator = new StartupValidator();
    validator.requireDirectory("assets");
    validator.requireDirectory("levels");
    validator.checkAssets(true);
    validator.checkGraphics(true);
    
    // Run validation
    List<ValidationResult> results = validator.validateAll();
    
    // Check results
    if (!StartupValidator.allPassed(results)) {
        StartupValidator.printResults(results);
        System.exit(1); // Exit if validation fails
    }
    
    // Continue with initialization...
}
```

**Checks:**
- Java version (requires Java 17+)
- Required directories exist
- Asset registration
- Graphics capabilities (not headless)

**Output:**
```
========== STARTUP VALIDATION ==========
Java Version: PASSED
Required Directories: PASSED
Asset Loading: PASSED
  Warnings:
    - No assets have been registered
    - Did you forget to register assets before loading?
Graphics Capabilities: PASSED

⚠️  Startup validation passed with warnings
========================================
```

**Location:** `TransmuteCore/System/StartupValidator.java`

---

### Asset Validation Report

Detailed report of asset loading with failure tracking.

```java
import TransmuteCore.System.Asset.AssetValidationReport;
import TransmuteCore.System.Asset.AssetValidationReport.AssetStatus;

AssetValidationReport report = new AssetValidationReport();

// Add asset statuses (typically done internally by AssetManager)
report.addAsset(new AssetStatus("player", "image", "assets/player.png", true));
report.addAsset(new AssetStatus("enemy", "image", "assets/enemy.png", false, 
    "File not found", Arrays.asList("Check file path", "Verify file exists")));

// Print report
report.print();

// Or check programmatically
if (!report.allSuccess()) {
    System.err.println("Failed to load " + report.getFailureCount() + " assets");
    for (AssetStatus status : report.getFailedAssets()) {
        System.err.println("  - " + status.getName() + ": " + status.getErrorMessage());
    }
}
```

**Location:** `TransmuteCore/System/Asset/AssetValidationReport.java`

---

## Hot Reload

### HotReloadManager

Automatically reload assets when files change - no need to restart your game!

```java
import TransmuteCore.System.HotReload.HotReloadManager;
import TransmuteCore.System.HotReload.AssetReloader.AssetType;

private HotReloadManager hotReload;

@Override
public void init() {
    // Create hot reload manager
    hotReload = new HotReloadManager();
    
    // Quick setup for standard structure (res/images, res/audio, res/fonts)
    hotReload.setupStandardWatching("res");
    
    // Or manually configure directories
    hotReload.watchDirectory("res/images");
    hotReload.watchDirectory("res/audio");
    
    // Register assets for hot reload
    hotReload.registerAsset("player", "res/images/player.png", AssetType.IMAGE);
    hotReload.registerAsset("enemy", "res/images/enemy.png", AssetType.IMAGE);
    hotReload.registerAsset("music", "res/audio/music.wav", AssetType.AUDIO);
    
    // Optional: Listen for reload events
    hotReload.addReloadListener((name, type) -> {
        System.out.println("Reloaded: " + name + " (" + type + ")");
    });
    
    // Start watching for changes
    hotReload.start();
}

// In shutdown or cleanup
@Override
public void dispose() {
    hotReload.shutdown();
}
```

**Features:**
- Automatic file change detection
- Watches multiple directories
- Debouncing (prevents repeated reloads)
- Manual reload via console or API
- Zero-cost when disabled
- Background thread monitoring

**Manual Reload:**
```java
// Reload specific asset
hotReload.reloadAsset("player");

// Reload all registered assets
hotReload.reloadAll();

// Print status
hotReload.printStatus();
```

**Console Integration:**
When using the debug console, use the `reload` command:
- `reload` - Reload all assets
- `reload player` - Reload specific asset

**How It Works:**
1. FileWatcher monitors directories using Java WatchService API
2. When a file changes, AssetReloader is notified
3. Asset is reloaded from disk
4. All references are updated automatically
5. Listeners are notified of the change

**Location:** `TransmuteCore/System/HotReload/`

---

## State Serialization

### StateSaver

Quick save/load game state with keyboard shortcuts (F5/F9).

```java
import TransmuteCore.System.Serialization.StateSaver;
import TransmuteCore.Serialization.*;

private StateSaver stateSaver;

@Override
public void init() {
    stateSaver = new StateSaver();
    stateSaver.setSaveDirectory("saves");
    stateSaver.enableHotkeys(true); // F5 = save, F9 = load
    
    // Define what to save
    stateSaver.setOnSave(db -> {
        // Save player data
        TinyObject player = new TinyObject("player");
        player.addField(new TinyField("x", playerX));
        player.addField(new TinyField("y", playerY));
        player.addField(new TinyField("health", playerHealth));
        db.addObject(player);
        
        // Save level data
        TinyObject level = new TinyObject("level");
        level.addString(new TinyString("name", currentLevelName));
        level.addField(new TinyField("time", elapsedTime));
        db.addObject(level);
    });
    
    // Define how to load
    stateSaver.setOnLoad(db -> {
        // Load player data
        TinyObject player = db.findObject("player");
        if (player != null) {
            playerX = player.findField("x").getIntValue();
            playerY = player.findField("y").getIntValue();
            playerHealth = player.findField("health").getIntValue();
        }
        
        // Load level data
        TinyObject level = db.findObject("level");
        if (level != null) {
            currentLevelName = level.findString("name").getValue();
            elapsedTime = level.findField("time").getFloatValue();
        }
    });
}

@Override
public void update(Manager manager, double delta) {
    stateSaver.update(manager, delta); // Handles F5/F9 hotkeys
}
```

**Features:**
- **F5** - Quick save
- **F9** - Quick load
- Multiple save slots
- Auto-save support
- Save slot management (list, delete, timestamps)

**Manual Operations:**
```java
// Save to specific slot
stateSaver.save("checkpoint1");

// Load from specific slot
stateSaver.load("checkpoint1");

// Auto-save with timestamp
stateSaver.autoSave();

// List all saves
stateSaver.printSaveSlots();

// Get most recent save
SaveSlot recent = stateSaver.getMostRecentSave();

// Delete a save
stateSaver.deleteSave("old_save");
```

**Console Commands:**
- `save <slot>` - Save to named slot
- `load <slot>` - Load from named slot
- `saves` - List all save slots

**Location:** `TransmuteCore/System/Serialization/StateSaver.java`

---

### StateDiff

Compare game states to debug save/load issues.

```java
import TransmuteCore.System.Serialization.StateDiff;

// Save initial state
stateSaver.save("before");
TinyDatabase before = stateSaver.getCurrentState();

// ... game plays ...

// Save new state
stateSaver.save("after");
TinyDatabase after = stateSaver.getCurrentState();

// Compare and print differences
StateDiff diff = StateDiff.compare(before, after);
diff.print();

// Or just quick print
StateDiff.printChanges(before, after);

// Get specific changes
for (StateDiff.Change change : diff.getChanges(StateDiff.ChangeType.MODIFIED)) {
    System.out.println("Modified: " + change.path);
}
```

**Output Example:**
```
========== STATE DIFF ==========
Added:     2
Removed:   0
Modified:  3
Unchanged: 5
================================
[+] player.inventory = sword
[~] player.x: 100 → 150
[~] player.y: 50 → 75
[~] player.health: 100 → 85
================================
```

**Features:**
- Tracks added, removed, and modified fields
- Filter changes by type
- Export diffs to text
- Snapshot summaries

**Location:** `TransmuteCore/System/Serialization/StateDiff.java`

---

## Utilities

### Builder Patterns

Fluent APIs for easier initialization:

- **GameBuilder** - `TransmuteCore/GameEngine/GameBuilder.java`
- **LevelBuilder** - `TransmuteCore/Level/LevelBuilder.java`
- **SpritesheetBuilder** - `TransmuteCore/Graphics/Sprites/SpritesheetBuilder.java`

### Math Utilities

- **MathUtils** - `TransmuteCore/System/MathUtils.java`
- Vector operations, distance calculations, collision detection

### Color Helpers

Enhanced Color class with:
- `lerp()`, `darken()`, `lighten()`, `blend()`
- `invert()`, `withAlpha()`, `toGrayscale()`

### Input Helpers

New convenience methods:
- `Input.isAnyKeyPressed()`, `Input.isAnyKeyHeld()`
- `Input.isAnyButtonPressed()`, `Input.isAnyButtonHeld()`

### Rendering Helpers

New Context methods:
- `renderBitmapCentered()`, `renderTextCentered()`
- `renderCircle()`, `renderFilledCircle()`
- `renderLine()`, `clear(color)`

---

## Quick Reference

| Feature | Hotkey | Output Location |
|---------|--------|-----------------|
| Console | ~ | On-screen |
| Debug Overlay | F3 | On-screen |
| Screenshot | F12 | `screenshots/` |
| State Dump | - | `debug/*.json` |
| Profiler | - | Console/CSV |
| Logs | - | `logs/game.log` |

---

## Best Practices

1. **Always use debug overlay during development** - Press F3 to monitor performance
2. **Profile before optimizing** - Use Profiler to find actual bottlenecks
3. **Enable file logging in production** - Catch bugs from real users
4. **Validate on startup** - Catch configuration errors early
5. **Use enhanced exceptions** - Provide context when throwing errors
6. **Capture screenshots of bugs** - F12 makes bug reports better
7. **Dump state for complex bugs** - StateInspector exports JSON for analysis

---

## See Also

- [COOKBOOK.md](COOKBOOK.md) - Common game development patterns
- [GETTING_STARTED.md](GETTING_STARTED.md) - Getting started guide
- [transmute-starter](https://github.com/transmute-games/transmute-starter) - Complete starter template

---

**All features are zero-cost when not used and designed for minimal performance impact during development.**
