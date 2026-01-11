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

## Step 1: Create the Project with CLI

Use the transmute CLI to create a new project:

```bash
transmute new hello-world-game -t basic
cd hello-world-game
```

This creates a project with the following structure:

```
hello-world-game/
├── build.gradle           # Pre-configured for TransmuteCore
├── settings.gradle
├── gradlew               # Gradle wrapper included
├── gradlew.bat
└── src/
    └── main/
        ├── java/
        │   └── com/example/helloworldgame/
        │       └── Game.java
        └── resources/
            └── fonts/
                └── font.png  # Default font included
```

## Step 2: Update the Game Class

Open the generated `src/main/java/com/example/helloworldgame/Game.java` and replace its contents with:

```java
package com.example.helloworldgame;

import TransmuteCore.core.GameConfig;
import TransmuteCore.core.Manager;
import TransmuteCore.core.TransmuteCore;
import TransmuteCore.core.interfaces.services.IRenderer;
import TransmuteCore.graphics.Context;
import TransmuteCore.graphics.Color;
import TransmuteCore.assets.AssetManager;
import TransmuteCore.assets.types.Font;

public class Game extends TransmuteCore {

    public Game(GameConfig config) {
        super(config);
    }

    @Override
    public void init() {
        // Initialize the default font
        Font.initializeDefaultFont("fonts/font.png");
        AssetManager.getGlobalInstance().load();
    }

    @Override
    public void update(Manager manager, double delta) {
        // No game logic yet - we'll add this in future tutorials
    }

    @Override
    public void render(Manager manager, IRenderer renderer) {
        Context ctx = (Context) renderer;

        // Clear the screen to black
        ctx.setClearColor(Color.toPixelInt(0, 0, 0, 255));

        // Draw "Hello, World!" in white at position (50, 100)
        int white = Color.toPixelInt(255, 255, 255, 255);
        ctx.renderText("Hello, World!", 50, 100, white);
    }

    public static void main(String[] args) {
        GameConfig config = new GameConfig.Builder()
            .title("Hello World")
            .version("1.0")
            .dimensions(320, GameConfig.ASPECT_RATIO_SQUARE)
            .scale(3)
            .build();

        Game game = new Game(config);
        game.start();
    }
}
```

## Step 3: Run Your Game

The CLI has already included a default font, so you can run immediately:

```bash
./gradlew run
```

You should see a window with "Hello, World!" displayed in white text on a black background!

## Understanding the Code

### Constructor

```java
public Game(GameConfig config) {
    super(config);
}
```

The game now uses a `GameConfig` builder pattern for configuration. In `main()`, the config is created:

```java
GameConfig config = new GameConfig.Builder()
    .title("Hello World")        // Window title
    .version("1.0")              // Game version
    .dimensions(320, GameConfig.ASPECT_RATIO_SQUARE)  // 320x240 (4:3)
    .scale(3)                    // 3x scale = 960x720 window
    .build();
```

### init() Method

```java
@Override
public void init() {
    Font.initializeDefaultFont("fonts/font.png");
    AssetManager.getGlobalInstance().load();
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
public void render(Manager manager, IRenderer renderer) {
    Context ctx = (Context) renderer;
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

Colors in Transmute Core use RGBA values (0-255 for each channel):

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
import TransmuteCore.input.Input;

@Override
public void update(Manager manager, double delta) {
    if (manager.getInput().isKeyPressed(KeyEvent.VK_ESCAPE)) {
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
- Ensure `AssetManager.getGlobalInstance().load()` was called
- Check that the text position is within the window bounds

## What's Next?

In the next tutorial, you'll learn how to:
- Load and display sprites
- Create animations
- Work with sprite sheets

Continue to [Tutorial 2: Sprites and Animation](02-sprites-and-animation.md)

## Complete Code

Here's the complete `Game.java` with all features:

```java
package com.example.helloworldgame;

import TransmuteCore.core.GameConfig;
import TransmuteCore.core.Manager;
import TransmuteCore.core.TransmuteCore;
import TransmuteCore.core.interfaces.services.IRenderer;
import TransmuteCore.graphics.Context;
import TransmuteCore.graphics.Color;
import TransmuteCore.assets.AssetManager;
import TransmuteCore.assets.types.Font;
import TransmuteCore.input.Input;
import java.awt.event.KeyEvent;

public class Game extends TransmuteCore {

    private int textY = 100;

    public Game(GameConfig config) {
        super(config);
    }

    @Override
    public void init() {
        Font.initializeDefaultFont("fonts/font.png");
        AssetManager.getGlobalInstance().load();
    }

    @Override
    public void update(Manager manager, double delta) {
        // Exit on ESC
        if (manager.getInput().isKeyPressed(KeyEvent.VK_ESCAPE)) {
            System.exit(0);
        }

        // Animate text position
        textY++;
        if (textY > 300) textY = 0;
    }

    @Override
    public void render(Manager manager, IRenderer renderer) {
        Context ctx = (Context) renderer;

        // Dark blue background
        ctx.setClearColor(Color.toPixelInt(0, 0, 64, 255));

        // White text
        int white = Color.toPixelInt(255, 255, 255, 255);
        ctx.renderText("Hello, World!", 50, textY, white);
        ctx.renderText("Welcome to TransmuteCore!", 50, textY + 20, white);
        ctx.renderText("Press ESC to exit", 50, textY + 40, white);
    }

    public static void main(String[] args) {
        GameConfig config = new GameConfig.Builder()
            .title("Hello World")
            .version("1.0")
            .dimensions(320, GameConfig.ASPECT_RATIO_SQUARE)
            .scale(3)
            .build();

        Game game = new Game(config);
        game.start();
    }
}
```
