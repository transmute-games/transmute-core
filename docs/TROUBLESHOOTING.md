# TransmuteCore Troubleshooting Guide

Common issues, solutions, and performance tips for TransmuteCore game development.

## Table of Contents

- [Installation & Setup](#installation--setup)
- [Asset Loading Issues](#asset-loading-issues)
- [Rendering Problems](#rendering-problems)
- [Performance Issues](#performance-issues)
- [Input Problems](#input-problems)
- [Audio Issues](#audio-issues)
- [State Management](#state-management)
- [Platform-Specific Issues](#platform-specific-issues)

---

## Installation & Setup

### Java Version Errors

**Problem:** `UnsupportedClassVersionError` or similar Java version errors

**Solution:**
```bash
# Check your Java version
java -version

# Should be Java 17 or higher
# Download from: https://adoptium.net/
```

### Gradle Build Fails

**Problem:** `Could not resolve dependencies` or build errors

**Solution:**
```bash
# Clean and rebuild
./gradlew clean build

# If using local Maven:
./gradlew publishToMavenLocal

# Then in your project:
./gradlew build --refresh-dependencies
```

### IDE Not Recognizing Classes

**Problem:** IntelliJ/Eclipse doesn't find TransmuteCore classes

**Solution:**
- IntelliJ: File → Invalidate Caches / Restart
- Eclipse: Project → Clean
- Ensure `build.gradle` has correct dependency
- Reimport Gradle project

---

## Asset Loading Issues

### Asset Not Found Errors

**Problem:** `AssetNotFoundException: [image]: player.png not found`

**Causes & Solutions:**

1. **File path incorrect**
```java
// ❌ Wrong - absolute path
new Image("player", "/res/images/player.png");

// ✅ Correct - relative to resources
new Image("player", "images/player.png");
```

2. **File not in resources directory**
```
src/main/resources/
    images/
        player.png  ← Must be here
```

3. **Case sensitivity (Linux/Mac)**
```java
// File is Player.png but code says:
new Image("player", "images/player.png");  // Won't work on Linux/Mac
```

4. **Forgot to call load()**
```java
// ❌ Missing load call
new Image("player", "images/player.png");
// ... game starts, asset not loaded

// ✅ Correct
new Image("player", "images/player.png");
AssetManager.load();  // Load all registered assets
```

### Assets Load Slowly

**Problem:** Long loading times at startup

**Solutions:**
- **Compress images:** Use smaller file sizes
- **Async loading:** Create a loading screen state
- **Lazy loading:** Load assets only when needed
- **Optimize audio:** Convert to lower sample rates

### Image Appears Corrupted

**Problem:** Image displays with wrong colors or artifacts

**Solutions:**
- Save as PNG (not JPG for pixel art)
- Ensure RGB/RGBA color mode
- Check bit depth (24-bit or 32-bit)
- Disable compression artifacts

---

## Rendering Problems

### Black Screen / Nothing Renders

**Problem:** Window opens but nothing displays

**Checklist:**
1. ✅ Font initialized? `Font.initializeDefaultFont()`
2. ✅ Assets loaded? `AssetManager.load()`
3. ✅ State pushed? `stateManager.push(new GameState())`
4. ✅ Rendering code in `render()` method?
5. ✅ Colors not black? `Color.toPixelInt(255, 255, 255, 255)`

**Debug:**
```java
@Override
public void render(Manager manager, Context ctx) {
    // Test with solid color
    ctx.setClearColor(Color.toPixelInt(255, 0, 0, 255)); // Red screen
    
    // Test text rendering
    ctx.renderText("TEST", 100, 100, Color.toPixelInt(255, 255, 255, 255));
}
```

### Text Not Showing

**Problem:** Text rendering fails silently

**Solutions:**
```java
// Ensure font is initialized
Font.initializeDefaultFont("fonts/font.png");
AssetManager.load();

// Check coordinates are on screen
ctx.renderText("Text", 10, 10, Color.WHITE);  // Top-left

// Verify color isn't transparent
int color = Color.toPixelInt(255, 255, 255, 255);  // Opaque white
```

### Sprites Render Incorrectly

**Problem:** Sprites appear distorted, wrong size, or flickering

**Solutions:**

1. **Wrong sprite coordinates**
```java
// Check spritesheet crop coordinates
Sprite sprite = spritesheet.crop(0, 0);  // First sprite at (0,0)
```

2. **Sprite sheet spacing**
```java
// If tiles have gaps between them:
Spritesheet sheet = new Spritesheet(
    bitmap,
    new Tuple2i(16, 16),  // Tile size
    new Tuple2i(0, 0),    // Offset
    2,  // Horizontal spacing ← Add this
    2   // Vertical spacing   ← Add this
);
```

3. **Transparency issues**
```java
// Ensure PNG has alpha channel
// Save as 32-bit RGBA
```

### Performance: Low FPS

See [Performance Issues](#performance-issues) section below.

---

## Performance Issues

### Game Runs Slowly (< 60 FPS)

**Diagnosis:**
```java
// Enable FPS display
setFPSVerbose(true);

// Use profiler
Profiler.begin("update");
// ... your code
Profiler.end("update");
Profiler.printResults();
```

**Common Causes:**

1. **Too many render calls**
```java
// ❌ Bad - renders every tile
for (int i = 0; i < 10000; i++) {
    ctx.renderBitmap(tile, x, y);
}

// ✅ Good - use TiledLevel's viewport culling
level.render(manager, ctx);  // Only renders visible tiles
```

2. **Not using delta time**
```java
// ❌ Wrong - frame-dependent
x += 5;

// ✅ Correct - frame-independent
x += (int)(5 * delta);
```

3. **Excessive object creation**
```java
// ❌ Bad - creates garbage
@Override
public void update(Manager manager, double delta) {
    Tuple2i pos = new Tuple2i(x, y);  // Created every frame!
}

// ✅ Good - reuse
private Tuple2i pos = new Tuple2i(0, 0);

@Override
public void update(Manager manager, double delta) {
    pos.x = x;
    pos.y = y;
}
```

4. **Too many collisions checks**
```java
// ❌ O(n²) - checks every object against every other
for (Object a : objects) {
    for (Object b : objects) {
        if (a.collidesWith(b)) { }
    }
}

// ✅ Use spatial partitioning
CollisionGrid grid = new CollisionGrid(64);
grid.add(object);
List<Object> nearby = grid.getNearby(object);
```

### Memory Issues / OutOfMemoryError

**Solutions:**

1. **Increase heap size**
```bash
# Run with more memory
java -Xmx2G -jar game.jar
```

2. **Profile memory usage**
```java
Runtime runtime = Runtime.getRuntime();
long usedMB = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
System.out.println("Used memory: " + usedMB + " MB");
```

3. **Fix memory leaks**
```java
// ❌ Bad - holds references
private List<Object> allObjects = new ArrayList<>();

@Override
public void update(Manager manager, double delta) {
    allObjects.add(new Object());  // Never removed!
}

// ✅ Good - clean up
if (object.shouldRemove()) {
    objects.remove(object);
}
```

4. **Optimize assets**
- Reduce image sizes
- Use indexed color for pixel art
- Compress audio files

### Stuttering / Frame Drops

**Causes:**

1. **Garbage collection pauses**
   - Reduce object allocation in game loop
   - Use object pools

2. **Asset loading during gameplay**
   - Preload all assets in init()
   - Use loading screens

3. **Blocking operations**
   - Don't load files in update()
   - Use threads for heavy work

---

## Input Problems

### Input Not Working

**Problem:** Keys/mouse don't respond

**Solutions:**

1. **Window doesn't have focus**
   - Click the game window
   - Ensure `requestFocus()` is called

2. **Using wrong input method**
```java
// ❌ Wrong - checks once
if (Input.isKeyPressed(KeyEvent.VK_W)) {
    // Only true for ONE frame
}

// For movement, use:
if (Input.isKeyHeld(KeyEvent.VK_W)) {
    // True while key is held
}
```

3. **Input manager not initialized**
```java
// Ensure Manager has input
manager.getInput();  // Should not be null
```

### Input Feels Laggy

**Problem:** Delay between key press and response

**Solutions:**
- Check FPS - low FPS causes input lag
- Don't use `isKeyPressed()` for continuous movement
- Reduce update() method complexity

### Keys Stick or Repeat

**Problem:** Key press triggers multiple times

**Solutions:**
```java
// Use isKeyPressed() not isKeyHeld() for single actions
if (Input.isKeyPressed(KeyEvent.VK_SPACE)) {
    jump();  // Only jumps once per press
}
```

---

## Audio Issues

### Audio Not Playing

**Problem:** Sound effects or music don't play

**Checklist:**
1. ✅ File format is WAV/AIFF/AU (not MP3)
2. ✅ Audio registered: `new Audio("name", "path")`
3. ✅ Assets loaded: `AssetManager.load()`
4. ✅ Audio not muted
5. ✅ Volume > 0

**Debug:**
```java
Clip clip = AssetManager.getAudio("jump");
if (clip == null) {
    System.out.println("Audio not loaded!");
} else {
    System.out.println("Audio loaded: " + clip.getFrameLength() + " frames");
}
```

### Audio Stutters or Cuts Out

**Solutions:**
- Use shorter audio files
- Preload audio (don't load during gameplay)
- Check CPU usage
- Use audio pools for rapid-fire sounds

### Can't Control Volume

**Problem:** Volume control doesn't work

**Solution:**
Not all systems support `FloatControl`. Implement fallback:
```java
try {
    FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
    volume.setValue(volumeDB);
} catch (Exception e) {
    // Fallback: mute/unmute only
    if (volumeDB < -30) {
        clip.stop();
    } else {
        clip.start();
    }
}
```

### Converting Audio to WAV

```bash
# Install ffmpeg first

# Convert MP3 to WAV
ffmpeg -i input.mp3 -ar 44100 -ac 2 output.wav

# Convert with lower quality (smaller file)
ffmpeg -i input.mp3 -ar 22050 -ac 1 output.wav
```

---

## State Management

### States Not Updating

**Problem:** State's update() not being called

**Solutions:**
```java
// Ensure stateManager.update() is called
@Override
public void update(Manager manager, double delta) {
    if (stateManager != null) {
        stateManager.update(manager, delta);  // Must call this!
    }
}
```

### Can't Return to Previous State

**Problem:** Popping state doesn't work as expected

**Solution:**
```java
// To return to menu from game:
stateManager.pop();  // Remove current state
stateManager.pop();  // Remove game state

// Or use clear and push:
stateManager.clear();
stateManager.push(new MenuState(stateManager));
```

### Input Bleeds Through States

**Problem:** Pause menu registers input from gameplay

**Solution:**
```java
// In pause state, return early to block input propagation
@Override
public void update(Manager manager, double delta) {
    // Handle pause input
    if (Input.isKeyPressed(KeyEvent.VK_ESCAPE)) {
        stateManager.pop();
        return;  // Don't process other input
    }
    // ... other pause input
}
```

### Memory Leak from States

**Problem:** Old states consuming memory

**Solution:**
```java
@Override
public void onExit() {
    // Clean up resources when state is removed
    if (level != null) {
        level.clear();
        level = null;
    }
    // Clear collections
    enemies.clear();
    projectiles.clear();
}
```

---

## Platform-Specific Issues

### macOS: Window Not Appearing

**Problem:** Window opens behind other windows or invisible

**Solution:**
```java
// Add to game constructor:
public MyGame() {
    super("Game", "1.0", 320, TransmuteCore.Square, 3);
    
    // Force window to front on macOS
    if (System.getProperty("os.name").contains("Mac")) {
        SwingUtilities.invokeLater(() -> {
            getWindow().toFront();
            getWindow().requestFocus();
        });
    }
}
```

### macOS: Retina Display Issues

**Problem:** Game window is tiny or text is blurry

**Solution:**
Adjust scale factor for Retina:
```java
// Detect retina
GraphicsDevice gd = GraphicsEnvironment
    .getLocalGraphicsEnvironment()
    .getDefaultScreenDevice();
    
int scale = gd.getDisplayMode().getHeight() > 1440 ? 4 : 3;
super("Game", "1.0", 320, TransmuteCore.Square, scale);
```

### Linux: Font Rendering Issues

**Problem:** Default font doesn't render on Linux

**Solution:**
- Ensure font.png is included in resources
- Use absolute path test: `new File("res/fonts/font.png").exists()`
- Check file permissions: `chmod +r font.png`

### Windows: DPI Scaling Problems

**Problem:** Window size incorrect on high-DPI displays

**Solution:**
```java
// Disable DPI scaling (in Main method before creating game)
System.setProperty("sun.java2d.dpiaware", "false");
```

---

## General Tips

### Enable Debug Mode

```java
// In init()
Logger.setLevel(Logger.Level.DEBUG);
setFPSVerbose(true);

// Use debug overlay
DebugOverlay overlay = new DebugOverlay();
```

### Common Mistakes Checklist

- [ ] Forgot to call `AssetManager.load()`
- [ ] Using absolute paths instead of relative
- [ ] Not initializing font before rendering text
- [ ] Creating objects in update() loop
- [ ] Using wrong input method (pressed vs held)
- [ ] Not handling state cleanup in `onExit()`
- [ ] Forgetting to call `super.update()` in subclasses
- [ ] Not setting state manager: `manager.setStateManager()`

### Getting Help

1. **Check logs:** Enable debug logging
2. **Isolate the problem:** Create minimal reproduction
3. **Check examples:** Look at transmute-starter
4. **Read the docs:** COOKBOOK.md, DX_FEATURES.md
5. **Search issues:** GitHub issues tracker
6. **Ask for help:** Provide error messages and code

---

## Performance Benchmarking

```java
// Basic performance test
public class PerformanceTest {
    public static void main(String[] args) {
        // Test rendering performance
        long start = System.currentTimeMillis();
        
        for (int i = 0; i < 1000; i++) {
            ctx.renderBitmap(sprite, x, y);
        }
        
        long elapsed = System.currentTimeMillis() - start;
        System.out.println("1000 sprites in " + elapsed + "ms");
    }
}
```

### Target Performance

- **60 FPS** minimum (16.6ms per frame)
- **< 100ms** startup time
- **< 50MB** memory for small game
- **1000+ sprites** at 60 FPS

---

## See Also

- [DX_FEATURES.md](DX_FEATURES.md) - Debugging tools
- [ARCHITECTURE.md](ARCHITECTURE.md) - How the engine works
- [COOKBOOK.md](COOKBOOK.md) - Code patterns and solutions
