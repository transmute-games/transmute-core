# Tutorial 4: Collision Detection

In this tutorial, you'll learn how to implement collision detection between game objects. This is essential for creating interactive games with enemies, obstacles, pickups, and more.

## What You'll Learn

- AABB (Axis-Aligned Bounding Box) collision detection
- Circle collision detection
- Collision response and resolution
- Tile-based collision with levels
- Creating collision layers and groups
- Optimization techniques for collision checking

## Prerequisites

- Completed [Tutorial 3: Input and Movement](03-input-and-movement.md)
- Understanding of basic geometry and rectangles
- Familiarity with the Object class

## Understanding Collision Detection

Collision detection determines when two game objects overlap or touch. The engine provides several methods:

1. **AABB (Rectangle) Collision** - Fast, works for most objects
2. **Circle Collision** - Good for round objects, natural feeling
3. **Point Collision** - For checking if a point is inside an object
4. **Tile Collision** - For level geometry and obstacles

## Step 1: Create Collidable Objects

First, let's create a base class for objects that can collide.

Create `src/main/java/GameObject.java`:

```java
import TransmuteCore.GameEngine.Manager;
import TransmuteCore.Graphics.Context;
import TransmuteCore.Graphics.Color;
import TransmuteCore.Graphics.Sprites.Sprite;
import TransmuteCore.Objects.Object;
import TransmuteCore.Units.Tuple2i;
import TransmuteCore.System.MathUtils;

public class GameObject extends Object {
    
    protected int width;
    protected int height;
    protected boolean solid = true;
    
    public GameObject(Manager manager, String name, Sprite sprite, 
                     Tuple2i location, int width, int height) {
        super(manager, name, Object.STATIC, sprite, location, 1.0f);
        this.width = width;
        this.height = height;
    }
    
    /**
     * Check AABB collision with another object
     */
    public boolean checkCollision(GameObject other) {
        return MathUtils.rectanglesOverlap(
            this.location.x, this.location.y, this.width, this.height,
            other.location.x, other.location.y, other.width, other.height
        );
    }
    
    /**
     * Check if a point is inside this object
     */
    public boolean containsPoint(int x, int y) {
        return x >= location.x && x < location.x + width &&
               y >= location.y && y < location.y + height;
    }
    
    /**
     * Get the center point of this object
     */
    public Tuple2i getCenter() {
        return new Tuple2i(
            location.x + width / 2,
            location.y + height / 2
        );
    }
    
    /**
     * Get distance to another object (center to center)
     */
    public float distanceTo(GameObject other) {
        Tuple2i thisCenter = getCenter();
        Tuple2i otherCenter = other.getCenter();
        
        int dx = thisCenter.x - otherCenter.x;
        int dy = thisCenter.y - otherCenter.y;
        
        return (float) Math.sqrt(dx * dx + dy * dy);
    }
    
    public int getWidth() { return width; }
    public int getHeight() { return height; }
    public boolean isSolid() { return solid; }
    public void setSolid(boolean solid) { this.solid = solid; }
    
    @Override
    public void render(Manager manager, Context ctx) {
        super.render(manager, ctx);
        
        // Debug: Draw collision box
        if (false) { // Set to true to see collision boxes
            ctx.renderRectangle(location.x, location.y, width, height, 
                               Color.toPixelInt(0, 255, 0, 255));
        }
    }
}
```

## Step 2: Create the Player with Collision

Update your Player class to handle collisions:

```java
import TransmuteCore.GameEngine.Manager;
import TransmuteCore.Graphics.Sprites.Animation;
import TransmuteCore.Input.Input;
import TransmuteCore.Units.Tuple2i;
import java.awt.event.KeyEvent;
import java.util.List;

public class Player extends GameObject {
    
    private float velocityX = 0;
    private float velocityY = 0;
    private final float acceleration = 0.5f;
    private final float friction = 0.85f;
    private final float maxSpeed = 4.0f;
    
    private Animation currentAnimation;
    private Animation idleAnimation;
    private Animation walkAnimation;
    
    public Player(Manager manager, Tuple2i location, 
                  Animation idleAnim, Animation walkAnim) {
        super(manager, "player", idleAnim.getCurrentSprite(), 
              location, 16, 16);
        
        this.idleAnimation = idleAnim;
        this.walkAnimation = walkAnim;
        this.currentAnimation = idleAnimation;
        this.solid = true;
    }
    
    @Override
    public void update(Manager manager, double delta) {
        handleInput();
        
        // Store old position for collision resolution
        int oldX = location.x;
        int oldY = location.y;
        
        applyPhysics(delta);
        updateAnimation();
        
        super.update(manager, delta);
    }
    
    private void handleInput() {
        if (Input.isKeyHeld(KeyEvent.VK_LEFT) || Input.isKeyHeld(KeyEvent.VK_A)) {
            velocityX -= acceleration;
        }
        if (Input.isKeyHeld(KeyEvent.VK_RIGHT) || Input.isKeyHeld(KeyEvent.VK_D)) {
            velocityX += acceleration;
        }
        if (Input.isKeyHeld(KeyEvent.VK_UP) || Input.isKeyHeld(KeyEvent.VK_W)) {
            velocityY -= acceleration;
        }
        if (Input.isKeyHeld(KeyEvent.VK_DOWN) || Input.isKeyHeld(KeyEvent.VK_S)) {
            velocityY += acceleration;
        }
    }
    
    private void applyPhysics(double delta) {
        velocityX *= friction;
        velocityY *= friction;
        
        float speed = (float) Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        if (speed > maxSpeed) {
            velocityX = (velocityX / speed) * maxSpeed;
            velocityY = (velocityY / speed) * maxSpeed;
        }
        
        location.x += (int)(velocityX * delta);
        location.y += (int)(velocityY * delta);
        
        if (Math.abs(velocityX) < 0.1f) velocityX = 0;
        if (Math.abs(velocityY) < 0.1f) velocityY = 0;
    }
    
    private void updateAnimation() {
        boolean isMoving = Math.abs(velocityX) > 0.5f || Math.abs(velocityY) > 0.5f;
        currentAnimation = isMoving ? walkAnimation : idleAnimation;
        currentAnimation.update();
    }
    
    /**
     * Check collision with obstacles and resolve
     */
    public void checkCollisions(List<GameObject> obstacles) {
        for (GameObject obstacle : obstacles) {
            if (obstacle.isSolid() && checkCollision(obstacle)) {
                resolveCollision(obstacle);
            }
        }
    }
    
    /**
     * Resolve collision by pushing player out
     */
    private void resolveCollision(GameObject obstacle) {
        // Calculate overlap on each axis
        int overlapX = Math.min(
            location.x + width - obstacle.location.x,
            obstacle.location.x + obstacle.width - location.x
        );
        
        int overlapY = Math.min(
            location.y + height - obstacle.location.y,
            obstacle.location.y + obstacle.height - location.y
        );
        
        // Push out on the axis with smallest overlap
        if (overlapX < overlapY) {
            // Push horizontally
            if (location.x < obstacle.location.x) {
                location.x -= overlapX;
            } else {
                location.x += overlapX;
            }
            velocityX = 0;
        } else {
            // Push vertically
            if (location.y < obstacle.location.y) {
                location.y -= overlapY;
            } else {
                location.y += overlapY;
            }
            velocityY = 0;
        }
    }
    
    public float getVelocityX() { return velocityX; }
    public float getVelocityY() { return velocityY; }
}
```

## Step 3: Create Obstacles

Create `src/main/java/Obstacle.java`:

```java
import TransmuteCore.GameEngine.Manager;
import TransmuteCore.Graphics.Context;
import TransmuteCore.Graphics.Color;
import TransmuteCore.Graphics.Sprites.Sprite;
import TransmuteCore.Units.Tuple2i;

public class Obstacle extends GameObject {
    
    private int colorValue;
    
    public Obstacle(Manager manager, Tuple2i location, int width, int height, int color) {
        super(manager, "obstacle", null, location, width, height);
        this.colorValue = color;
        this.solid = true;
    }
    
    @Override
    public void render(Manager manager, Context ctx) {
        ctx.renderFilledRectangle(location.x, location.y, width, height, colorValue);
        
        // Draw border
        int borderColor = Color.darken(colorValue, 0.3f);
        ctx.renderRectangle(location.x, location.y, width, height, borderColor);
    }
}
```

## Step 4: Create the Collision Demo

Create `src/main/java/CollisionDemo.java`:

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
import java.util.ArrayList;
import java.util.List;

public class CollisionDemo extends TransmuteCore {
    
    private Player player;
    private List<GameObject> obstacles;
    private List<Pickup> pickups;
    private int score = 0;
    
    public CollisionDemo() {
        super("Collision Demo", "1.0", 320, TransmuteCore.Square, 3);
    }
    
    @Override
    public void init() {
        // Initialize font
        Font.initializeDefaultFont("fonts/font.png");
        
        // Register assets
        new Image("player", "images/player.png");
        AssetManager.load();
        
        // Create player
        Bitmap playerBitmap = AssetManager.getImage("player");
        Spritesheet playerSheet = new Spritesheet(
            playerBitmap,
            new Tuple2i(16, 16),
            new Tuple2i(0, 0),
            0, 0
        );
        
        Sprite[] idleFrames = { playerSheet.crop(0, 0) };
        Animation idleAnimation = new Animation("idle", idleFrames, 500);
        
        Sprite[] walkFrames = {
            playerSheet.crop(0, 0),
            playerSheet.crop(1, 0),
            playerSheet.crop(2, 0),
            playerSheet.crop(3, 0)
        };
        Animation walkAnimation = new Animation("walk", walkFrames, 150);
        
        player = new Player(
            manager,
            new Tuple2i(160 - 8, 120 - 8),
            idleAnimation,
            walkAnimation
        );
        
        // Create obstacles
        obstacles = new ArrayList<>();
        
        // Walls around the edge
        obstacles.add(new Obstacle(manager, new Tuple2i(0, 0), 320, 10, 
                                   Color.toPixelInt(100, 100, 100, 255)));
        obstacles.add(new Obstacle(manager, new Tuple2i(0, 230), 320, 10, 
                                   Color.toPixelInt(100, 100, 100, 255)));
        obstacles.add(new Obstacle(manager, new Tuple2i(0, 0), 10, 240, 
                                   Color.toPixelInt(100, 100, 100, 255)));
        obstacles.add(new Obstacle(manager, new Tuple2i(310, 0), 10, 240, 
                                   Color.toPixelInt(100, 100, 100, 255)));
        
        // Interior obstacles
        obstacles.add(new Obstacle(manager, new Tuple2i(50, 50), 40, 40, 
                                   Color.toPixelInt(150, 100, 100, 255)));
        obstacles.add(new Obstacle(manager, new Tuple2i(150, 100), 60, 20, 
                                   Color.toPixelInt(100, 150, 100, 255)));
        obstacles.add(new Obstacle(manager, new Tuple2i(230, 150), 30, 60, 
                                   Color.toPixelInt(100, 100, 150, 255)));
        
        // Create pickups
        pickups = new ArrayList<>();
        pickups.add(new Pickup(manager, new Tuple2i(100, 100)));
        pickups.add(new Pickup(manager, new Tuple2i(200, 50)));
        pickups.add(new Pickup(manager, new Tuple2i(250, 200)));
    }
    
    @Override
    public void update(Manager manager, double delta) {
        if (Input.isKeyPressed(KeyEvent.VK_ESCAPE)) {
            System.exit(0);
        }
        
        // Update player
        player.update(manager, delta);
        
        // Check collisions with obstacles
        player.checkCollisions(obstacles);
        
        // Check pickups
        for (int i = pickups.size() - 1; i >= 0; i--) {
            Pickup pickup = pickups.get(i);
            if (player.checkCollision(pickup)) {
                score += 10;
                pickups.remove(i);
            } else {
                pickup.update(manager, delta);
            }
        }
    }
    
    @Override
    public void render(Manager manager, Context ctx) {
        // Background
        ctx.setClearColor(Color.toPixelInt(20, 20, 40, 255));
        
        // Render obstacles
        for (GameObject obstacle : obstacles) {
            obstacle.render(manager, ctx);
        }
        
        // Render pickups
        for (Pickup pickup : pickups) {
            pickup.render(manager, ctx);
        }
        
        // Render player
        player.render(manager, ctx);
        
        // UI
        int white = Color.toPixelInt(255, 255, 255, 255);
        ctx.renderText("WASD to move", 15, 15, white);
        ctx.renderText("Score: " + score, 15, 25, white);
        ctx.renderText("ESC to exit", 15, 35, white);
    }
    
    public static void main(String[] args) {
        new CollisionDemo();
    }
}

// Pickup class for collectibles
class Pickup extends GameObject {
    
    private float bobOffset = 0;
    private int baseY;
    
    public Pickup(Manager manager, Tuple2i location) {
        super(manager, "pickup", null, location, 8, 8);
        this.baseY = location.y;
        this.solid = false;
    }
    
    @Override
    public void update(Manager manager, double delta) {
        // Bob up and down
        bobOffset += 0.1f;
        location.y = baseY + (int)(Math.sin(bobOffset) * 3);
        
        super.update(manager, delta);
    }
    
    @Override
    public void render(Manager manager, Context ctx) {
        int yellow = Color.toPixelInt(255, 255, 0, 255);
        ctx.renderFilledCircle(location.x + 4, location.y + 4, 4, yellow);
    }
}
```

## Step 5: Run the Demo

```bash
./gradlew run
```

You should see a player that collides with walls and obstacles, and can collect pickups!

## Advanced Collision Techniques

### 1. Circle Collision

For round objects, circle collision is more accurate:

```java
public class CircleGameObject extends GameObject {
    
    protected int radius;
    
    public CircleGameObject(Manager manager, String name, Sprite sprite,
                           Tuple2i location, int radius) {
        super(manager, name, sprite, location, radius * 2, radius * 2);
        this.radius = radius;
    }
    
    /**
     * Check circle-to-circle collision
     */
    public boolean checkCircleCollision(CircleGameObject other) {
        Tuple2i thisCenter = getCenter();
        Tuple2i otherCenter = other.getCenter();
        
        int dx = thisCenter.x - otherCenter.x;
        int dy = thisCenter.y - otherCenter.y;
        int distance = (int) Math.sqrt(dx * dx + dy * dy);
        
        return distance < (this.radius + other.radius);
    }
    
    /**
     * Check circle-to-rectangle collision
     */
    public boolean checkRectCollision(GameObject rect) {
        Tuple2i center = getCenter();
        
        // Find closest point on rectangle to circle
        int closestX = Math.max(rect.location.x, 
                               Math.min(center.x, rect.location.x + rect.width));
        int closestY = Math.max(rect.location.y, 
                               Math.min(center.y, rect.location.y + rect.height));
        
        // Calculate distance
        int dx = center.x - closestX;
        int dy = center.y - closestY;
        int distanceSquared = dx * dx + dy * dy;
        
        return distanceSquared < (radius * radius);
    }
}
```

### 2. Sweep Collision (Continuous)

Prevents objects from "tunneling" through thin obstacles at high speeds:

```java
public boolean sweepCollision(GameObject other, int oldX, int oldY) {
    // Check if path from old position to new position intersects
    int dx = location.x - oldX;
    int dy = location.y - oldY;
    int steps = Math.max(Math.abs(dx), Math.abs(dy));
    
    for (int i = 0; i <= steps; i++) {
        int checkX = oldX + (dx * i / steps);
        int checkY = oldY + (dy * i / steps);
        
        if (MathUtils.rectanglesOverlap(
            checkX, checkY, width, height,
            other.location.x, other.location.y, other.width, other.height)) {
            return true;
        }
    }
    return false;
}
```

### 3. Collision Layers

Use bit flags for collision layers:

```java
public class LayeredGameObject extends GameObject {
    
    public static final int LAYER_PLAYER = 1 << 0;    // 0001
    public static final int LAYER_ENEMY = 1 << 1;     // 0010
    public static final int LAYER_TERRAIN = 1 << 2;   // 0100
    public static final int LAYER_PICKUP = 1 << 3;    // 1000
    
    protected int layer;
    protected int collidesWith;
    
    public LayeredGameObject(Manager manager, String name, Sprite sprite,
                            Tuple2i location, int width, int height,
                            int layer, int collidesWith) {
        super(manager, name, sprite, location, width, height);
        this.layer = layer;
        this.collidesWith = collidesWith;
    }
    
    public boolean canCollideWith(LayeredGameObject other) {
        return (this.collidesWith & other.layer) != 0;
    }
}

// Usage:
Player player = new LayeredGameObject(..., 
    LayeredGameObject.LAYER_PLAYER,
    LayeredGameObject.LAYER_ENEMY | LayeredGameObject.LAYER_TERRAIN
);
```

### 4. Spatial Partitioning (Grid)

For many objects, use a grid to speed up collision checks:

```java
public class CollisionGrid {
    
    private int cellSize;
    private Map<Integer, List<GameObject>> grid;
    
    public CollisionGrid(int cellSize) {
        this.cellSize = cellSize;
        this.grid = new HashMap<>();
    }
    
    private int getKey(int x, int y) {
        return (x / cellSize) + (y / cellSize) * 10000;
    }
    
    public void add(GameObject obj) {
        int key = getKey(obj.location.x, obj.location.y);
        grid.computeIfAbsent(key, k -> new ArrayList<>()).add(obj);
    }
    
    public List<GameObject> getNearby(GameObject obj) {
        List<GameObject> nearby = new ArrayList<>();
        int gridX = obj.location.x / cellSize;
        int gridY = obj.location.y / cellSize;
        
        // Check surrounding cells
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int key = getKey((gridX + dx) * cellSize, (gridY + dy) * cellSize);
                List<GameObject> cell = grid.get(key);
                if (cell != null) {
                    nearby.addAll(cell);
                }
            }
        }
        return nearby;
    }
}
```

## Tile-Based Collision

For TiledLevel collision:

```java
public class TileCollisionLevel extends TiledLevel {
    
    private boolean[][] solidTiles;
    
    public TileCollisionLevel(Manager manager, String name, Bitmap levelImage,
                             int tileSize, Tuple2i spawn) {
        super(manager, name, levelImage, tileSize, spawn);
        initializeSolidTiles();
    }
    
    private void initializeSolidTiles() {
        solidTiles = new boolean[getTileHeight()][getTileWidth()];
        
        // Mark which tiles are solid based on their index
        // For example, tile indices 1-10 are solid walls
        for (int y = 0; y < getTileHeight(); y++) {
            for (int x = 0; x < getTileWidth(); x++) {
                int tileIndex = getTileAt(x, y);
                solidTiles[y][x] = (tileIndex >= 1 && tileIndex <= 10);
            }
        }
    }
    
    public boolean isTileSolid(int tileX, int tileY) {
        if (tileX < 0 || tileY < 0 || tileX >= getTileWidth() || tileY >= getTileHeight()) {
            return true; // Out of bounds is solid
        }
        return solidTiles[tileY][tileX];
    }
    
    public boolean checkTileCollision(GameObject obj) {
        int tileSize = getTileSize();
        
        // Check all four corners of the object
        int left = obj.location.x / tileSize;
        int right = (obj.location.x + obj.width - 1) / tileSize;
        int top = obj.location.y / tileSize;
        int bottom = (obj.location.y + obj.height - 1) / tileSize;
        
        return isTileSolid(left, top) || isTileSolid(right, top) ||
               isTileSolid(left, bottom) || isTileSolid(right, bottom);
    }
}
```

## Exercises

### 1. Add Bounce Physics

Make objects bounce off walls:

```java
private void resolveCollisionWithBounce(GameObject obstacle) {
    // ... resolve collision ...
    
    // Bounce
    if (overlapX < overlapY) {
        velocityX = -velocityX * 0.8f; // 80% energy retained
    } else {
        velocityY = -velocityY * 0.8f;
    }
}
```

### 2. Create a Collision Event System

```java
public interface CollisionListener {
    void onCollision(GameObject a, GameObject b);
}

// In GameObject:
private List<CollisionListener> listeners = new ArrayList<>();

public void addCollisionListener(CollisionListener listener) {
    listeners.add(listener);
}

private void notifyCollision(GameObject other) {
    for (CollisionListener listener : listeners) {
        listener.onCollision(this, other);
    }
}
```

### 3. Add Trigger Zones

Create non-solid areas that trigger events:

```java
public class TriggerZone extends GameObject {
    private Runnable onEnter;
    
    public TriggerZone(Manager manager, Tuple2i location, 
                      int width, int height, Runnable onEnter) {
        super(manager, "trigger", null, location, width, height);
        this.solid = false;
        this.onEnter = onEnter;
    }
    
    public void trigger() {
        if (onEnter != null) {
            onEnter.run();
        }
    }
}
```

## Common Issues

### Objects stick together

- Ensure you're pushing objects far enough apart in `resolveCollision()`
- Add a small epsilon value (0.1f) to the push distance

### Player slides when hitting corners

- Check collision on X and Y axes separately
- Use swept collision for fast-moving objects

### Performance with many objects

- Use spatial partitioning (grid or quadtree)
- Only check collision with nearby objects
- Use simpler collision shapes where possible

## What's Next?

In the next tutorial, you'll learn:
- Creating multiple game states (menu, gameplay, pause)
- Transitioning between states
- Passing data between states
- Building a complete game flow

Continue to [Tutorial 5: State Management](05-state-management.md)

## Resources

- [MathUtils.java](../../TransmuteCore/src/TransmuteCore/System/MathUtils.java) - Built-in collision helpers
- [COOKBOOK.md](../COOKBOOK.md) - Collision response patterns
