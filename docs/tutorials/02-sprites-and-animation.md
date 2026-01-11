# Tutorial 2: Sprites and Animation

In this tutorial, you'll learn how to load sprites, display them on screen, and create frame-based animations. We'll build a simple demo that shows an animated character.

## What You'll Learn

- Loading sprite images
- Displaying sprites on screen
- Creating sprite sheets
- Building frame-based animations
- Managing sprite resources

## Prerequisites

- Completed [Tutorial 1: Hello World](01-hello-world.md)
- Basic understanding of sprite sheets
- A sprite sheet image (or use the example provided)

## Understanding Sprites

A **sprite** is a 2D image that represents a game object. A **sprite sheet** is a single image containing multiple sprites arranged in a grid. This is more efficient than loading many individual images.

Example sprite sheet layout (4 columns x 2 rows):
```
[Frame 0] [Frame 1] [Frame 2] [Frame 3]
[Frame 4] [Frame 5] [Frame 6] [Frame 7]
```

## Step 1: Project Setup

Create a new project or continue from Tutorial 1. Add these directories:

```
sprite-demo/
├── build.gradle
├── settings.gradle
└── src/
    └── main/
        ├── java/
        │   └── SpriteDemo.java
        └── resources/
            ├── fonts/
            │   └── font.png
            └── images/
                └── player.png     # Your sprite sheet
```

## Step 2: Prepare Your Sprite Sheet

For this tutorial, you'll need a sprite sheet. You can:
- Use your own (PNG format recommended)
- Create a simple one (8x8 or 16x16 pixel sprites work well)
- Download from open game art resources

Your sprite sheet should have uniform grid dimensions (e.g., 16x16 pixels per frame).

## Step 3: Create the Sprite Demo

Create `src/main/java/SpriteDemo.java`:

```java
import TransmuteCore.GameEngine.TransmuteCore;
import TransmuteCore.GameEngine.Manager;
import TransmuteCore.Graphics.Context;
import TransmuteCore.Graphics.Color;
import TransmuteCore.Graphics.Bitmap;
import TransmuteCore.Graphics.Sprites.Sprite;
import TransmuteCore.Graphics.Sprites.Spritesheet;
import TransmuteCore.Graphics.Sprites.Animation;
import TransmuteCore.System.Asset.AssetManager;
import TransmuteCore.System.Asset.Type.Fonts.Font;
import TransmuteCore.System.Asset.Type.Images.Image;
import TransmuteCore.Units.Tuple2i;
import TransmuteCore.Input.Input;
import java.awt.event.KeyEvent;

public class SpriteDemo extends TransmuteCore {
    
    private Sprite staticSprite;
    private Animation walkAnimation;
    private int spriteX = 100;
    private int spriteY = 100;
    
    public SpriteDemo() {
        super("Sprite Demo", "1.0", 320, TransmuteCore.Square, 3);
    }
    
    @Override
    public void init() {
        // Initialize font
        Font.initializeDefaultFont("fonts/font.png");
        
        // Register sprite sheet image
        new Image("player", "images/player.png");
        
        // Load all assets
        AssetManager.load();
        
        // Create sprite sheet (assuming 16x16 pixel sprites)
        Bitmap playerBitmap = AssetManager.getImage("player");
        Spritesheet playerSheet = new Spritesheet(
            playerBitmap,
            new Tuple2i(16, 16),  // Size of each sprite
            new Tuple2i(0, 0),    // Offset (0,0 means no offset)
            0,                     // Horizontal spacing between sprites
            0                      // Vertical spacing between sprites
        );
        
        // Create a static sprite (first frame)
        staticSprite = playerSheet.crop(0, 0);
        
        // Create an animation (frames 0-3 from first row)
        Sprite[] walkFrames = new Sprite[] {
            playerSheet.crop(0, 0),
            playerSheet.crop(1, 0),
            playerSheet.crop(2, 0),
            playerSheet.crop(3, 0)
        };
        
        // Animation with 200ms per frame
        walkAnimation = new Animation("walk", walkFrames, 200);
    }
    
    @Override
    public void update(Manager manager, double delta) {
        // Exit on ESC
        if (Input.isKeyPressed(KeyEvent.VK_ESCAPE)) {
            System.exit(0);
        }
        
        // Update animation
        walkAnimation.update();
        
        // Move sprite with arrow keys
        int speed = 2;
        if (Input.isKeyHeld(KeyEvent.VK_LEFT)) {
            spriteX -= speed;
        }
        if (Input.isKeyHeld(KeyEvent.VK_RIGHT)) {
            spriteX += speed;
        }
        if (Input.isKeyHeld(KeyEvent.VK_UP)) {
            spriteY -= speed;
        }
        if (Input.isKeyHeld(KeyEvent.VK_DOWN)) {
            spriteY += speed;
        }
        
        // Keep sprite in bounds
        if (spriteX < 0) spriteX = 0;
        if (spriteX > 304) spriteX = 304; // 320 - 16 (sprite width)
        if (spriteY < 0) spriteY = 0;
        if (spriteY > 224) spriteY = 224; // 240 - 16 (sprite height)
    }
    
    @Override
    public void render(Manager manager, Context ctx) {
        // Dark background
        ctx.setClearColor(Color.toPixelInt(32, 32, 64, 255));
        
        // Draw static sprite
        ctx.renderBitmap(staticSprite, 50, 100);
        
        // Draw animated sprite at cursor position
        walkAnimation.render(ctx, spriteX, spriteY);
        
        // Draw instructions
        int white = Color.toPixelInt(255, 255, 255, 255);
        ctx.renderText("Arrow keys to move", 10, 10, white);
        ctx.renderText("ESC to exit", 10, 20, white);
    }
    
    public static void main(String[] args) {
        new SpriteDemo();
    }
}
```

## Step 4: Run the Demo

```bash
./gradlew run
```

You should see:
- A static sprite on the left
- An animated sprite that you can move with arrow keys

## Understanding the Code

### Loading a Sprite Sheet

```java
// Register the image
new Image("player", "images/player.png");
AssetManager.load();

// Get the loaded bitmap
Bitmap playerBitmap = AssetManager.getImage("player");
```

### Creating a Spritesheet

```java
Spritesheet playerSheet = new Spritesheet(
    playerBitmap,
    new Tuple2i(16, 16),  // Each sprite is 16x16 pixels
    new Tuple2i(0, 0),    // No offset
    0,                     // No horizontal spacing
    0                      // No vertical spacing
);
```

If your sprite sheet has spacing between sprites:
```java
new Spritesheet(bitmap, new Tuple2i(16, 16), new Tuple2i(0, 0), 2, 2);
// 2 pixels of horizontal spacing, 2 pixels of vertical spacing
```

### Cropping Individual Sprites

```java
// Get sprite at column 0, row 0
Sprite firstSprite = playerSheet.crop(0, 0);

// Get sprite at column 1, row 0
Sprite secondSprite = playerSheet.crop(1, 0);

// Get sprite at column 0, row 1
Sprite thirdSprite = playerSheet.crop(0, 1);
```

### Creating Animations

```java
// Create array of frames
Sprite[] walkFrames = new Sprite[] {
    playerSheet.crop(0, 0),
    playerSheet.crop(1, 0),
    playerSheet.crop(2, 0),
    playerSheet.crop(3, 0)
};

// Create animation with name and duration per frame (milliseconds)
Animation walkAnimation = new Animation("walk", walkFrames, 200);
```

### Updating and Rendering Animations

```java
// In update()
walkAnimation.update();

// In render()
walkAnimation.render(ctx, x, y);
```

## Advanced Sprite Techniques

### 1. Multiple Animations

```java
private Animation walkAnimation;
private Animation jumpAnimation;
private Animation currentAnimation;

@Override
public void init() {
    // ... load sprite sheet ...
    
    walkAnimation = new Animation("walk", walkFrames, 200);
    jumpAnimation = new Animation("jump", jumpFrames, 150);
    
    // Set initial animation
    currentAnimation = walkAnimation;
}

@Override
public void update(Manager manager, double delta) {
    // Switch animations based on input
    if (Input.isKeyPressed(KeyEvent.VK_SPACE)) {
        currentAnimation = jumpAnimation;
    } else if (Input.isKeyHeld(KeyEvent.VK_RIGHT)) {
        currentAnimation = walkAnimation;
    }
    
    currentAnimation.update();
}

@Override
public void render(Manager manager, Context ctx) {
    currentAnimation.render(ctx, spriteX, spriteY);
}
```

### 2. Flipping Sprites

```java
// Flip sprite horizontally
Sprite flippedSprite = new Sprite(
    Image.getFlipped(sprite.getBufferedImage(), true, false)
);
```

### 3. Scaling Sprites

```java
// Render sprite at 2x scale
ctx.renderBitmap(sprite, x, y, 1.0f, 2.0f);
```

### 4. Transparency

```java
// Render sprite at 50% opacity
ctx.renderBitmap(sprite, x, y, 0.5f);
```

### 5. Color Tinting

```java
// Tint sprite red
int redTint = Color.toPixelInt(255, 100, 100, 255);
ctx.renderBitmap(sprite, x, y, 1.0f, redTint);
```

## Using SpriteManager

For managing many sprites, use the `SpriteManager`:

```java
import TransmuteCore.Graphics.Sprites.SpriteManager;

private SpriteManager spriteManager;

@Override
public void init() {
    // ... load sprite sheet ...
    
    spriteManager = new SpriteManager(playerSheet);
    
    // Add sprites with keys
    spriteManager.add("idle", 0, 0);           // Single sprite
    spriteManager.add("walk", 1, 0, 2, 0, 3, 0);  // Multiple sprites
    
    // Get sprites back
    Sprite idleSprite = spriteManager.get("idle", 0);
    Sprite[] walkSprites = spriteManager.get("walk");
}
```

## Exercises

### 1. Create a Bouncing Sprite

Make the sprite bounce around the screen:

```java
private int velocityX = 2;
private int velocityY = 2;

@Override
public void update(Manager manager, double delta) {
    spriteX += velocityX;
    spriteY += velocityY;
    
    // Bounce off edges
    if (spriteX <= 0 || spriteX >= 304) velocityX = -velocityX;
    if (spriteY <= 0 || spriteY >= 224) velocityY = -velocityY;
    
    walkAnimation.update();
}
```

### 2. Add Multiple Sprites

Create multiple animated sprites at different positions:

```java
private class AnimatedSprite {
    Animation animation;
    int x, y;
    
    AnimatedSprite(Animation anim, int x, int y) {
        this.animation = anim;
        this.x = x;
        this.y = y;
    }
}

private List<AnimatedSprite> sprites = new ArrayList<>();
```

### 3. Create a Sprite Trail

Draw previous positions with decreasing opacity.

## Common Issues

### Sprites appear distorted

- Verify your sprite sheet grid dimensions match the code
- Check that there's no extra spacing in your sprite sheet
- Ensure the sprite sheet image is the correct size

### Animation is too fast/slow

Adjust the duration parameter:
```java
new Animation("walk", frames, 100);  // Faster (100ms per frame)
new Animation("walk", frames, 300);  // Slower (300ms per frame)
```

### Sprites have wrong colors

- Save your PNG with proper color mode (RGBA recommended)
- Check if your image editor added unwanted color profiles

## What's Next?

In the next tutorial, you'll learn:
- Handling player input
- Creating a controllable character
- Implementing basic physics

Continue to [Tutorial 3: Input and Movement](03-input-and-movement.md)

## Resources

- **Free Sprite Sheets**: OpenGameArt.org, itch.io
- **Sprite Tools**: Aseprite, Piskel, GIMP
- **Pixel Art Guides**: pixel-art-tutorials.com
