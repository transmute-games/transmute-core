# Tutorial 3: Input and Movement

In this tutorial, you'll create a controllable character with smooth movement, physics, and proper input handling. This is essential for creating interactive games.

## What You'll Learn

- Handling keyboard and mouse input
- Implementing smooth character movement
- Adding velocity and acceleration
- Creating a simple Player class
- Implementing basic collision detection
- Understanding delta time for frame-independent movement

## Prerequisites

- Completed [Tutorial 2: Sprites and Animation](02-sprites-and-animation.md)
- Understanding of basic game physics concepts

## Understanding Input States

TransmuteCore provides three input states:

1. **isKeyPressed()** - True for ONE frame when key is first pressed
2. **isKeyHeld()** - True every frame while key is held down
3. **isKeyReleased()** - True for ONE frame when key is released

The same applies to mouse buttons with `isButtonPressed()`, `isButtonHeld()`, and `isButtonReleased()`.

## Step 1: Create the Player Class

Create `src/main/java/Player.java`:

```java
import TransmuteCore.GameEngine.Manager;
import TransmuteCore.Graphics.Context;
import TransmuteCore.Graphics.Sprites.Animation;
import TransmuteCore.Input.Input;
import TransmuteCore.Objects.Object;
import TransmuteCore.Units.Tuple2i;
import java.awt.event.KeyEvent;

public class Player extends Object {
    
    // Movement properties
    private float velocityX = 0;
    private float velocityY = 0;
    private final float acceleration = 0.5f;
    private final float friction = 0.85f;
    private final float maxSpeed = 4.0f;
    
    // Rendering properties
    private Animation currentAnimation;
    private Animation idleAnimation;
    private Animation walkAnimation;
    private boolean facingRight = true;
    
    // Screen bounds
    private int screenWidth;
    private int screenHeight;
    
    public Player(Manager manager, Tuple2i location, 
                  Animation idleAnim, Animation walkAnim,
                  int screenWidth, int screenHeight) {
        super(manager, "player", Object.ANIMATABLE, location, 1.0f);
        
        this.idleAnimation = idleAnim;
        this.walkAnimation = walkAnim;
        this.currentAnimation = idleAnimation;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
    }
    
    @Override
    public void update(Manager manager, double delta) {
        handleInput();
        applyPhysics(delta);
        updateAnimation();
        constrainToBounds();
        
        super.update(manager, delta);
    }
    
    private void handleInput() {
        // Horizontal movement
        if (Input.isKeyHeld(KeyEvent.VK_LEFT) || Input.isKeyHeld(KeyEvent.VK_A)) {
            velocityX -= acceleration;
            facingRight = false;
        }
        if (Input.isKeyHeld(KeyEvent.VK_RIGHT) || Input.isKeyHeld(KeyEvent.VK_D)) {
            velocityX += acceleration;
            facingRight = true;
        }
        
        // Vertical movement
        if (Input.isKeyHeld(KeyEvent.VK_UP) || Input.isKeyHeld(KeyEvent.VK_W)) {
            velocityY -= acceleration;
        }
        if (Input.isKeyHeld(KeyEvent.VK_DOWN) || Input.isKeyHeld(KeyEvent.VK_S)) {
            velocityY += acceleration;
        }
    }
    
    private void applyPhysics(double delta) {
        // Apply friction
        velocityX *= friction;
        velocityY *= friction;
        
        // Limit to max speed
        float speed = (float) Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        if (speed > maxSpeed) {
            velocityX = (velocityX / speed) * maxSpeed;
            velocityY = (velocityY / speed) * maxSpeed;
        }
        
        // Update position (delta makes it frame-independent)
        location.x += (int)(velocityX * delta);
        location.y += (int)(velocityY * delta);
        
        // Stop if moving very slowly
        if (Math.abs(velocityX) < 0.1f) velocityX = 0;
        if (Math.abs(velocityY) < 0.1f) velocityY = 0;
    }
    
    private void updateAnimation() {
        // Switch animation based on movement
        boolean isMoving = Math.abs(velocityX) > 0.5f || Math.abs(velocityY) > 0.5f;
        
        if (isMoving) {
            if (currentAnimation != walkAnimation) {
                currentAnimation = walkAnimation;
            }
        } else {
            if (currentAnimation != idleAnimation) {
                currentAnimation = idleAnimation;
            }
        }
        
        currentAnimation.update();
    }
    
    private void constrainToBounds() {
        // Keep player on screen
        if (location.x < 0) location.x = 0;
        if (location.y < 0) location.y = 0;
        if (location.x > screenWidth - 16) location.x = screenWidth - 16;
        if (location.y > screenHeight - 16) location.y = screenHeight - 16;
    }
    
    @Override
    public void render(Manager manager, Context ctx) {
        // Render the current animation
        if (facingRight) {
            currentAnimation.render(ctx, location.x, location.y);
        } else {
            // Flip horizontally when facing left
            // Note: You'll need to implement sprite flipping or use pre-flipped sprites
            currentAnimation.render(ctx, location.x, location.y);
        }
    }
    
    public float getVelocityX() {
        return velocityX;
    }
    
    public float getVelocityY() {
        return velocityY;
    }
    
    public boolean isMoving() {
        return Math.abs(velocityX) > 0.5f || Math.abs(velocityY) > 0.5f;
    }
}
```

## Step 2: Create the Main Game

Create `src/main/java/MovementDemo.java`:

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
import TransmuteCore.System.Logger;
import TransmuteCore.Units.Tuple2i;
import TransmuteCore.Input.Input;
import java.awt.event.KeyEvent;

public class MovementDemo extends TransmuteCore {
    
    private Player player;
    private int screenWidth = 320;
    private int screenHeight = 240;
    
    public MovementDemo() {
        super("Movement Demo", "1.0", 320, TransmuteCore.Square, 3);
    }
    
    @Override
    public void init() {
        // Set logging level
        Logger.setLevel(Logger.Level.INFO);
        
        // Initialize font
        Font.initializeDefaultFont("fonts/font.png");
        
        // Register player sprite
        new Image("player", "images/player.png");
        
        // Load assets
        AssetManager.load();
        
        // Create sprite sheet
        Bitmap playerBitmap = AssetManager.getImage("player");
        Spritesheet playerSheet = new Spritesheet(
            playerBitmap,
            new Tuple2i(16, 16),
            new Tuple2i(0, 0),
            0, 0
        );
        
        // Create animations
        Sprite[] idleFrames = new Sprite[] {
            playerSheet.crop(0, 0)
        };
        Animation idleAnimation = new Animation("idle", idleFrames, 500);
        
        Sprite[] walkFrames = new Sprite[] {
            playerSheet.crop(0, 0),
            playerSheet.crop(1, 0),
            playerSheet.crop(2, 0),
            playerSheet.crop(3, 0)
        };
        Animation walkAnimation = new Animation("walk", walkFrames, 150);
        
        // Create player at center of screen
        player = new Player(
            manager,
            new Tuple2i(screenWidth / 2 - 8, screenHeight / 2 - 8),
            idleAnimation,
            walkAnimation,
            screenWidth,
            screenHeight
        );
        
        Logger.info("Game initialized successfully");
    }
    
    @Override
    public void update(Manager manager, double delta) {
        // Exit on ESC
        if (Input.isKeyPressed(KeyEvent.VK_ESCAPE)) {
            Logger.info("Exiting game");
            System.exit(0);
        }
        
        // Update player
        player.update(manager, delta);
    }
    
    @Override
    public void render(Manager manager, Context ctx) {
        // Background
        ctx.setClearColor(Color.toPixelInt(32, 48, 64, 255));
        
        // Render player
        player.render(manager, ctx);
        
        // Draw UI
        int white = Color.toPixelInt(255, 255, 255, 255);
        int yellow = Color.toPixelInt(255, 255, 0, 255);
        
        ctx.renderText("WASD or Arrow Keys to move", 10, 10, white);
        ctx.renderText("ESC to exit", 10, 20, white);
        
        // Show velocity
        String velocityText = String.format("Velocity: %.1f, %.1f",
                                           player.getVelocityX(),
                                           player.getVelocityY());
        ctx.renderText(velocityText, 10, screenHeight - 20, yellow);
        
        // Show position
        String posText = String.format("Position: %d, %d",
                                      player.getX(),
                                      player.getY());
        ctx.renderText(posText, 10, screenHeight - 30, yellow);
    }
    
    public static void main(String[] args) {
        new MovementDemo();
    }
}
```

## Step 3: Run the Game

```bash
./gradlew run
```

You should see a character that you can control with WASD or arrow keys. Notice the smooth acceleration and deceleration!

## Understanding Frame-Independent Movement

The `delta` parameter represents time elapsed between frames. Using it makes movement consistent regardless of frame rate:

```java
// Bad - movement tied to frame rate
location.x += velocityX;

// Good - movement independent of frame rate
location.x += (int)(velocityX * delta);
```

With delta time:
- At 60 FPS, delta ≈ 1.0
- At 30 FPS, delta ≈ 2.0
- At 120 FPS, delta ≈ 0.5

This ensures the character moves the same distance per second, regardless of frame rate.

## Advanced Movement Techniques

### 1. Jump Mechanics

```java
private boolean isGrounded = true;
private float gravity = 0.5f;
private float jumpStrength = -8.0f;

private void handleInput() {
    // Jumping
    if (Input.isKeyPressed(KeyEvent.VK_SPACE) && isGrounded) {
        velocityY = jumpStrength;
        isGrounded = false;
    }
}

private void applyPhysics(double delta) {
    // Apply gravity
    if (!isGrounded) {
        velocityY += gravity;
    }
    
    // Update position
    location.y += (int)(velocityY * delta);
    
    // Ground collision
    if (location.y >= groundLevel) {
        location.y = groundLevel;
        velocityY = 0;
        isGrounded = true;
    }
}
```

### 2. Dash Ability

```java
private boolean canDash = true;
private long dashCooldown = 500; // milliseconds
private long lastDashTime = 0;

private void handleInput() {
    // Dash
    if (Input.isKeyPressed(KeyEvent.VK_SHIFT) && canDash) {
        float dashSpeed = 10.0f;
        velocityX = facingRight ? dashSpeed : -dashSpeed;
        lastDashTime = System.currentTimeMillis();
        canDash = false;
    }
    
    // Cooldown check
    if (!canDash && System.currentTimeMillis() - lastDashTime > dashCooldown) {
        canDash = true;
    }
}
```

### 3. Mouse-Following Movement

```java
private void handleMouseMovement(Input input) {
    int targetX = input.getMouseX();
    int targetY = input.getMouseY();
    
    // Calculate direction to mouse
    float dx = targetX - location.x;
    float dy = targetY - location.y;
    float distance = (float) Math.sqrt(dx * dx + dy * dy);
    
    // Move towards mouse if far enough away
    if (distance > 5) {
        velocityX += (dx / distance) * acceleration;
        velocityY += (dy / distance) * acceleration;
    }
}
```

### 4. Eight-Directional Movement

```java
private void handleInput() {
    float inputX = 0;
    float inputY = 0;
    
    if (Input.isKeyHeld(KeyEvent.VK_LEFT)) inputX -= 1;
    if (Input.isKeyHeld(KeyEvent.VK_RIGHT)) inputX += 1;
    if (Input.isKeyHeld(KeyEvent.VK_UP)) inputY -= 1;
    if (Input.isKeyHeld(KeyEvent.VK_DOWN)) inputY += 1;
    
    // Normalize diagonal movement
    if (inputX != 0 && inputY != 0) {
        float length = (float) Math.sqrt(inputX * inputX + inputY * inputY);
        inputX /= length;
        inputY /= length;
    }
    
    velocityX += inputX * acceleration;
    velocityY += inputY * acceleration;
}
```

## Mouse Input Example

Add to your Player class:

```java
public void handleMouseClick(Input input) {
    if (Input.isButtonPressed(MouseEvent.BUTTON1)) {
        int mouseX = input.getMouseX();
        int mouseY = input.getMouseY();
        
        Logger.info("Clicked at: " + mouseX + ", " + mouseY);
        
        // Could shoot projectile, place object, etc.
    }
}
```

Call it from your game's update method:

```java
@Override
public void update(Manager manager, double delta) {
    player.handleMouseClick(manager.getInput());
    player.update(manager, delta);
}
```

## Exercises

### 1. Add Sprinting

Hold SHIFT to move faster:

```java
float currentMaxSpeed = Input.isKeyHeld(KeyEvent.VK_SHIFT) 
                        ? maxSpeed * 2.0f 
                        : maxSpeed;
```

### 2. Add Footstep Sounds

Play a sound when moving:

```java
if (isMoving() && isGrounded) {
    // Play footstep sound every X milliseconds
}
```

### 3. Create Movement Particles

Spawn particles behind the player when moving fast.

### 4. Add Controller Support

Extend the Input class to support gamepad input.

## Common Issues

### Movement feels sluggish

- Increase `acceleration` value
- Decrease `friction` value
- Increase `maxSpeed`

### Movement is too fast

- Decrease velocity in `applyPhysics()`
- Increase friction
- Lower max speed

### Character slides too much

- Increase friction (try 0.9 or 0.95)
- Set velocity to 0 when no input

### Diagonal movement is faster

Make sure to normalize diagonal input (see eight-directional movement above).

## What's Next?

You now have a solid foundation for game development with TransmuteCore! Next steps:

- **Collision Detection** - Detect and respond to collisions between objects
- **Level Design** - Create tile-based levels
- **Game States** - Implement menus and multiple screens
- **Audio** - Add sound effects and music
- **Save System** - Use serialization to save game progress

## Additional Resources

- **Input Handling**: Check `TransmuteCore.Input.Input` class documentation
- **Physics**: Research basic game physics (velocity, acceleration, friction)
- **Game Feel**: "The Art of Screenshake" and similar resources

---

Congratulations! You've completed the core tutorials. You're now ready to build your own games with TransmuteCore! 🎮
