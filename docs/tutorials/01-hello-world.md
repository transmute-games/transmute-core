# Tutorial 1: Hello World

In this tutorial, you'll create your first TransmuteCore game that displays "Hello, World!" on the screen. This will teach you the basics of the engine's structure.

## What You'll Learn

- Setting up a basic game class
- Understanding the game loop (init, update, render)
- Rendering text to the screen
- Working with colors

## Prerequisites

- Completed the [Getting Started Guide](../GETTING_STARTED.md)
- TransmuteCore published to your local Maven repository
- Basic Java knowledge

## Step 1: Create the Project Structure

Create a new directory for your project:

```bash
mkdir hello-world-game
cd hello-world-game
```

Create the following structure:

```
hello-world-game/
├── build.gradle
├── settings.gradle
└── src/
    └── main/
        ├── java/
        │   └── HelloWorld.java
        └── resources/
            └── fonts/
                └── font.png
```

## Step 2: Set Up Gradle

Create `build.gradle`:

```gradle
plugins {
    id 'application'
    id 'java'
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation 'games.transmute:transmute-core:0.1.0-ALPHA'
}

application {
    mainClass = 'HelloWorld'
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}
```

Create `settings.gradle`:

```gradle
rootProject.name = 'hello-world-game'
```

## Step 3: Create Your Game Class

Create `src/main/java/HelloWorld.java`:

```java
import TransmuteCore.GameEngine.TransmuteCore;
import TransmuteCore.GameEngine.Manager;
import TransmuteCore.Graphics.Context;
import TransmuteCore.Graphics.Color;
import TransmuteCore.System.Asset.AssetManager;
import TransmuteCore.System.Asset.Type.Fonts.Font;

public class HelloWorld extends TransmuteCore {
    
    public HelloWorld() {
        // Parameters: title, version, width, aspectRatio, scale
        super("Hello World", "1.0", 320, TransmuteCore.Square, 3);
    }
    
    @Override
    public void init() {
        // Initialize the default font
        Font.initializeDefaultFont("fonts/font.png");
        AssetManager.load();
    }
    
    @Override
    public void update(Manager manager, double delta) {
        // No game logic yet - we'll add this in future tutorials
    }
    
    @Override
    public void render(Manager manager, Context ctx) {
        // Clear the screen to black
        ctx.setClearColor(Color.toPixelInt(0, 0, 0, 255));
        
        // Draw "Hello, World!" in white at position (50, 100)
        int white = Color.toPixelInt(255, 255, 255, 255);
        ctx.renderText("Hello, World!", 50, 100, white);
    }
    
    public static void main(String[] args) {
        new HelloWorld();
    }
}
```

## Step 4: Add a Font

Copy the `font.png` file from the transmute-starter project to `src/main/resources/fonts/font.png`, or use your own bitmap font.

## Step 5: Run Your Game

```bash
./gradlew run
```

You should see a window with "Hello, World!" displayed in white text on a black background!

## Understanding the Code

### Constructor

```java
public HelloWorld() {
    super("Hello World", "1.0", 320, TransmuteCore.Square, 3);
}
```

- **"Hello World"** - Window title
- **"1.0"** - Game version
- **320** - Base width in pixels
- **TransmuteCore.Square** - 4:3 aspect ratio (height will be 240)
- **3** - Scale factor (window will be 960x720)

### init() Method

```java
@Override
public void init() {
    Font.initializeDefaultFont("fonts/font.png");
    AssetManager.load();
}
```

Called once when the game starts. This is where you:
- Load assets
- Initialize game state
- Set up managers

### update() Method

```java
@Override
public void update(Manager manager, double delta) {
    // Game logic goes here
}
```

Called 60 times per second (by default). This is where you:
- Handle input
- Update game objects
- Check collisions
- Apply physics

The `delta` parameter tells you how much time has passed, useful for frame-independent movement.

### render() Method

```java
@Override
public void render(Manager manager, Context ctx) {
    ctx.setClearColor(Color.toPixelInt(0, 0, 0, 255));
    ctx.renderText("Hello, World!", 50, 100, white);
}
```

Called after update, also 60 times per second. This is where you:
- Clear the screen
- Draw sprites
- Draw text
- Draw shapes

## Working with Colors

Colors in TransmuteCore use RGBA values (0-255 for each channel):

```java
// Red, Green, Blue, Alpha (0-255)
int white = Color.toPixelInt(255, 255, 255, 255);
int red = Color.toPixelInt(255, 0, 0, 255);
int green = Color.toPixelInt(0, 255, 0, 255);
int blue = Color.toPixelInt(0, 0, 255, 255);
int transparent = Color.toPixelInt(255, 255, 255, 128); // 50% transparent white
```

## Exercises

Try these modifications to learn more:

### 1. Change the Text Color

```java
int yellow = Color.toPixelInt(255, 255, 0, 255);
ctx.renderText("Hello, World!", 50, 100, yellow);
```

### 2. Add Multiple Lines

```java
ctx.renderText("Hello, World!", 50, 100, white);
ctx.renderText("Welcome to TransmuteCore!", 50, 120, white);
ctx.renderText("Press ESC to exit", 50, 140, white);
```

### 3. Change the Background Color

```java
// Dark blue background
ctx.setClearColor(Color.toPixelInt(0, 0, 64, 255));
```

### 4. Animate the Text Position

Add a field to your class:

```java
private int textY = 100;
```

Update it in the update method:

```java
@Override
public void update(Manager manager, double delta) {
    textY++;
    if (textY > 300) textY = 0;
}
```

Use it in render:

```java
ctx.renderText("Hello, World!", 50, textY, white);
```

### 5. Add an Exit Key

```java
import java.awt.event.KeyEvent;
import TransmuteCore.Input.Input;

@Override
public void update(Manager manager, double delta) {
    if (Input.isKeyPressed(KeyEvent.VK_ESCAPE)) {
        System.exit(0);
    }
}
```

## Common Issues

### "Font not found" error

- Make sure `font.png` is in `src/main/resources/fonts/`
- Check that the path in code matches: `"fonts/font.png"`

### Window is too small/large

Adjust the scale parameter:
- Scale 1 = 320x240 window
- Scale 2 = 640x480 window  
- Scale 3 = 960x720 window

### Text doesn't appear

- Make sure you called `Font.initializeDefaultFont()` in `init()`
- Ensure `AssetManager.load()` was called
- Check that the text position is within the window bounds

## What's Next?

In the next tutorial, you'll learn how to:
- Load and display sprites
- Create animations
- Work with sprite sheets

Continue to [Tutorial 2: Sprites and Animation](02-sprites-and-animation.md)

## Complete Code

Here's the complete `HelloWorld.java` with all features:

```java
import TransmuteCore.GameEngine.TransmuteCore;
import TransmuteCore.GameEngine.Manager;
import TransmuteCore.Graphics.Context;
import TransmuteCore.Graphics.Color;
import TransmuteCore.System.Asset.AssetManager;
import TransmuteCore.System.Asset.Type.Fonts.Font;
import TransmuteCore.Input.Input;
import java.awt.event.KeyEvent;

public class HelloWorld extends TransmuteCore {
    
    private int textY = 100;
    
    public HelloWorld() {
        super("Hello World", "1.0", 320, TransmuteCore.Square, 3);
    }
    
    @Override
    public void init() {
        Font.initializeDefaultFont("fonts/font.png");
        AssetManager.load();
    }
    
    @Override
    public void update(Manager manager, double delta) {
        // Exit on ESC
        if (Input.isKeyPressed(KeyEvent.VK_ESCAPE)) {
            System.exit(0);
        }
        
        // Animate text position
        textY++;
        if (textY > 300) textY = 0;
    }
    
    @Override
    public void render(Manager manager, Context ctx) {
        // Dark blue background
        ctx.setClearColor(Color.toPixelInt(0, 0, 64, 255));
        
        // White text
        int white = Color.toPixelInt(255, 255, 255, 255);
        ctx.renderText("Hello, World!", 50, textY, white);
        ctx.renderText("Welcome to TransmuteCore!", 50, textY + 20, white);
        ctx.renderText("Press ESC to exit", 50, textY + 40, white);
    }
    
    public static void main(String[] args) {
        new HelloWorld();
    }
}
```
