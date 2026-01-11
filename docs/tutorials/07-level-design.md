# Tutorial 7: Level Design with Tiles

Learn how to create tile-based levels using TransmuteCore's TiledLevel system. Load levels from images, implement tile collision, and use viewport culling for large worlds.

## What You'll Learn

- Understanding tile-based levels
- Creating level images
- Loading levels with TiledLevel
- Implementing tile collision
- Viewport culling optimization
- Creating scrolling worlds
- Level transitions

## Prerequisites

- Completed [Tutorial 6: Audio System](06-audio-system.md)
- Basic understanding of 2D coordinate systems
- Image editing software (GIMP, Photoshop, or similar)

## Understanding Tile-Based Levels

**Tile-based levels** divide the game world into a grid of uniform squares (tiles). Each pixel in a level image represents one tile, with the pixel color determining which tile type to display.

### Benefits

- **Memory efficient** - Large worlds with small files
- **Easy to edit** - Use any image editor
- **Performance** - Viewport culling renders only visible tiles
- **Reusable** - One tileset, many combinations

## Step 1: Create a New Project

For this tutorial, we'll use the RPG template which is designed for tile-based games:

```bash
transmute new level-demo -t rpg
cd level-demo
```

The RPG template provides:
- TileMap class for level management
- Entity base class for game objects
- Grid-based movement system stubs

## Step 2: Create a Tileset

A **tileset** is a sprite sheet containing all your tile graphics.

Create `src/main/resources/images/tileset.png` (16x16 pixel tiles):

```
[Grass][Dirt ][Stone][Water]
[Tree ][Bush ][Rock ][Sand ]
```

Each tile should be 16x16 pixels (or your chosen size).

## Step 3: Create a Level Image

Create a level image where each pixel represents one tile:

```bash
mkdir -p src/main/resources/levels
```

Create `src/main/resources/levels/level1.png`:
- **1 pixel = 1 tile** in the game world
- **Pixel color** determines tile type
- For a 20x15 tile level, create a 20x15 pixel image

Example color mapping:
- `#00FF00` (green) = Grass tile (index 0)
- `#8B4513` (brown) = Dirt tile (index 1)
- `#808080` (gray) = Stone tile (index 2)
- `#0000FF` (blue) = Water tile (index 3)

**Important**: Disable anti-aliasing and save as PNG with no compression.

## Step 4: Create the Tile Class

Create `src/main/java/com/example/leveldemo/Tile.java`:

```java
package com.example.leveldemo;

import TransmuteCore.core.Manager;
import TransmuteCore.graphics.Context;
import TransmuteCore.graphics.sprites.Sprite;
import TransmuteCore.core.interfaces.Renderable;
import TransmuteCore.core.interfaces.Updatable;

public class Tile implements Updatable, Renderable {
    
    protected Sprite sprite;
    protected boolean solid;
    protected int width;
    protected int height;
    
    public Tile(Sprite sprite, boolean solid) {
        this.sprite = sprite;
        this.solid = solid;
        this.width = sprite != null ? sprite.getWidth() : 16;
        this.height = sprite != null ? sprite.getHeight() : 16;
    }
    
    @Override
    public void update(Manager manager, double delta) {
        // Most tiles are static, but animated tiles can override this
    }
    
    @Override
    public void render(Manager manager, Context ctx) {
        // Render at default position - TiledLevel handles actual positioning
        if (sprite != null) {
            ctx.renderBitmap(sprite, 0, 0);
        }
    }
    
    public void render(Manager manager, Context ctx, int x, int y) {
        if (sprite != null) {
            ctx.renderBitmap(sprite, x, y);
        }
    }
    
    public boolean isSolid() {
        return solid;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
}
```

## Step 5: Update the Main Game Class

Open `src/main/java/com/example/leveldemo/Game.java` and replace its contents:

```java
package com.example.leveldemo;

import TransmuteCore.core.GameConfig;
import TransmuteCore.core.Manager;
import TransmuteCore.core.TransmuteCore;
import TransmuteCore.core.interfaces.services.IRenderer;
import TransmuteCore.graphics.Context;
import TransmuteCore.graphics.Color;
import TransmuteCore.graphics.Bitmap;
import TransmuteCore.graphics.sprites.Sprite;
import TransmuteCore.graphics.sprites.Spritesheet;
import TransmuteCore.graphics.sprites.Animation;
import TransmuteCore.level.TiledLevel;
import TransmuteCore.assets.AssetManager;
import TransmuteCore.assets.types.Font;
import TransmuteCore.assets.types.Image;
import TransmuteCore.math.Tuple2i;
import TransmuteCore.input.Input;
import java.awt.event.KeyEvent;

public class Game extends TransmuteCore {
    
    private TiledLevel level;
    private Player player;
    private int cameraX = 0;
    private int cameraY = 0;
    
    public Game(GameConfig config) {
        super(config);
    }
    
    @Override
    public void init() {
        Font.initializeDefaultFont("fonts/font.png");
        
        // Load assets
        new Image("tileset", "images/tileset.png");
        new Image("player", "images/player.png");
        
        AssetManager.getGlobalInstance().load();
        
        // Create tileset
        Bitmap tilesetBitmap = AssetManager.getGlobalInstance().getImage("tileset");
        Spritesheet tileset = new Spritesheet(
            tilesetBitmap,
            new Tuple2i(16, 16),
            new Tuple2i(0, 0),
            0, 0
        );
        
        // Create tiles
        Tile grassTile = new Tile(tileset.crop(0, 0), false);
        Tile dirtTile = new Tile(tileset.crop(1, 0), false);
        Tile stoneTile = new Tile(tileset.crop(2, 0), true);  // Solid
        Tile waterTile = new Tile(tileset.crop(3, 0), true);  // Solid
        
        // Load level
        level = new TiledLevel("levels/level1.png");
        level.setTileSize(16);
        
        // Map pixel colors to tiles
        level.addTile(Color.toPixelInt(0, 255, 0, 255), grassTile);    // Green
        level.addTile(Color.toPixelInt(139, 69, 19, 255), dirtTile);   // Brown
        level.addTile(Color.toPixelInt(128, 128, 128, 255), stoneTile); // Gray
        level.addTile(Color.toPixelInt(0, 0, 255, 255), waterTile);    // Blue
        
        // Create player
        Bitmap playerBitmap = AssetManager.getGlobalInstance().getImage("player");
        Spritesheet playerSheet = new Spritesheet(
            playerBitmap,
            new Tuple2i(16, 16),
            new Tuple2i(0, 0),
            0, 0
        );
        
        Sprite[] idleFrames = { playerSheet.crop(0, 0) };
        Animation idleAnim = new Animation("idle", idleFrames, 500);
        Sprite[] walkFrames = {
            playerSheet.crop(0, 0),
            playerSheet.crop(1, 0),
            playerSheet.crop(2, 0),
            playerSheet.crop(3, 0)
        };
        Animation walkAnim = new Animation("walk", walkFrames, 150);
        
        player = new Player(
            manager,
            new Tuple2i(160 - 8, 120 - 8),
            idleAnim,
            walkAnim
        );
    }
    
    @Override
    public void update(Manager manager, double delta) {
        if (manager.getInput().isKeyPressed(KeyEvent.VK_ESCAPE)) {
            System.exit(0);
        }
        
        // Update player
        player.update(manager, delta);
        
        // Check tile collision
        checkTileCollision();
        
        // Update camera to follow player
        updateCamera();
        
        // Set level offset for rendering
        level.setOffset(cameraX, cameraY);
        
        // Update level
        level.update(manager, delta);
    }
    
    private void checkTileCollision() {
        int tileSize = level.getTileSize();
        
        // Get player's tile coordinates
        int playerTileX = player.getX() / tileSize;
        int playerTileY = player.getY() / tileSize;
        
        // Check surrounding tiles
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int tileX = playerTileX + dx;
                int tileY = playerTileY + dy;
                
                Tile tile = level.getTile(tileX, tileY);
                if (tile != null && tile.isSolid()) {
                    // Check if player collides with this tile
                    int tilePosX = tileX * tileSize;
                    int tilePosY = tileY * tileSize;
                    
                    if (player.getX() < tilePosX + tileSize &&
                        player.getX() + player.getWidth() > tilePosX &&
                        player.getY() < tilePosY + tileSize &&
                        player.getY() + player.getHeight() > tilePosY) {
                        
                        // Resolve collision
                        resolveTileCollision(tilePosX, tilePosY, tileSize, tileSize);
                    }
                }
            }
        }
    }
    
    private void resolveTileCollision(int tileX, int tileY, int tileW, int tileH) {
        int playerX = player.getX();
        int playerY = player.getY();
        int playerW = player.getWidth();
        int playerH = player.getHeight();
        
        // Calculate overlap
        int overlapLeft = (playerX + playerW) - tileX;
        int overlapRight = (tileX + tileW) - playerX;
        int overlapTop = (playerY + playerH) - tileY;
        int overlapBottom = (tileY + tileH) - playerY;
        
        // Find smallest overlap
        int minOverlap = Math.min(
            Math.min(overlapLeft, overlapRight),
            Math.min(overlapTop, overlapBottom)
        );
        
        // Push player out on smallest overlap axis
        if (minOverlap == overlapLeft) {
            player.setX(tileX - playerW);
            player.setVelocityX(0);
        } else if (minOverlap == overlapRight) {
            player.setX(tileX + tileW);
            player.setVelocityX(0);
        } else if (minOverlap == overlapTop) {
            player.setY(tileY - playerH);
            player.setVelocityY(0);
        } else if (minOverlap == overlapBottom) {
            player.setY(tileY + tileH);
            player.setVelocityY(0);
        }
    }
    
    private void updateCamera() {
        // Center camera on player
        int targetX = player.getX() + player.getWidth() / 2 - getWidth() / 2;
        int targetY = player.getY() + player.getHeight() / 2 - getHeight() / 2;
        
        // Smooth camera movement
        cameraX += (targetX - cameraX) * 0.1f;
        cameraY += (targetY - cameraY) * 0.1f;
        
        // Clamp camera to level bounds
        int levelPixelWidth = level.getWidth() * level.getTileSize();
        int levelPixelHeight = level.getHeight() * level.getTileSize();
        
        cameraX = Math.max(0, Math.min(cameraX, levelPixelWidth - getWidth()));
        cameraY = Math.max(0, Math.min(cameraY, levelPixelHeight - getHeight()));
    }
    
    @Override
    public void render(Manager manager, Context ctx) {
        // Background
        ctx.setClearColor(Color.toPixelInt(135, 206, 235, 255)); // Sky blue
        
        // Render level (with viewport culling)
        level.render(manager, ctx);
        
        // Render player (adjusted for camera)
        int playerScreenX = player.getX() - cameraX;
        int playerScreenY = player.getY() - cameraY;
        player.render(manager, ctx);
        
        // UI
        int white = Color.toPixelInt(255, 255, 255, 255);
        ctx.renderText("WASD to move", 10, 10, white);
        ctx.renderText("Pos: " + player.getX() + ", " + player.getY(), 10, 20, white);
    }
    
    public static void main(String[] args) {
        new LevelDemo();
    }
}
```

## Advanced Techniques

### 1. Animated Tiles

Create tiles with animations:

```java
public class AnimatedTile extends Tile {
    private Animation animation;
    
    public AnimatedTile(Animation animation, boolean solid) {
        super(animation.getCurrentSprite(), solid);
        this.animation = animation;
    }
    
    @Override
    public void update(Manager manager, double delta) {
        animation.update();
        this.sprite = animation.getCurrentSprite();
    }
}

// Usage:
Sprite[] waterFrames = { /* water animation frames */ };
Animation waterAnim = new Animation("water", waterFrames, 200);
AnimatedTile waterTile = new AnimatedTile(waterAnim, false);
```

### 2. Multi-Layer Levels

Create layers for background, foreground, etc:

```java
public class MultiLayerLevel {
    private TiledLevel backgroundLayer;
    private TiledLevel foregroundLayer;
    private TiledLevel collisionLayer;
    
    public void render(Manager manager, Context ctx) {
        backgroundLayer.render(manager, ctx);
        // Render entities here
        foregroundLayer.render(manager, ctx);
    }
}
```

### 3. Level Transitions

Smooth transitions between levels:

```java
public class LevelTransition {
    private TiledLevel currentLevel;
    private TiledLevel nextLevel;
    private float fadeProgress = 0;
    
    public void transitionTo(TiledLevel newLevel) {
        nextLevel = newLevel;
        fadeProgress = 0;
    }
    
    public void update(double delta) {
        if (nextLevel != null) {
            fadeProgress += 0.02f;
            if (fadeProgress >= 1.0f) {
                currentLevel = nextLevel;
                nextLevel = null;
                fadeProgress = 0;
            }
        }
    }
    
    public void render(Manager manager, Context ctx) {
        currentLevel.render(manager, ctx);
        
        if (nextLevel != null && fadeProgress > 0) {
            // Render fade overlay
            int alpha = (int)(fadeProgress * 255);
            int black = Color.toPixelInt(0, 0, 0, alpha);
            ctx.renderFilledRectangle(0, 0, 320, 240, black);
        }
    }
}
```

### 4. Procedural Level Generation

Generate levels algorithmically:

```java
public class ProceduralLevel {
    public static TiledLevel generate(int width, int height) {
        int[] data = new int[width * height];
        
        // Generate terrain using noise or algorithms
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Simple example: random tiles
                if (Math.random() < 0.1) {
                    data[x + y * width] = STONE_COLOR;
                } else {
                    data[x + y * width] = GRASS_COLOR;
                }
            }
        }
        
        // Create level from data
        TiledLevel level = new TiledLevel(width, height);
        level.setData(data);
        return level;
    }
}
```

### 5. Tile Metadata

Store additional data per tile:

```java
public class MetadataTile extends Tile {
    private Map<String, Object> metadata = new HashMap<>();
    
    public MetadataTile(Sprite sprite, boolean solid) {
        super(sprite, solid);
    }
    
    public void setMetadata(String key, Object value) {
        metadata.put(key, value);
    }
    
    public Object getMetadata(String key) {
        return metadata.get(key);
    }
}

// Usage:
MetadataTile spawnPoint = new MetadataTile(sprite, false);
spawnPoint.setMetadata("spawn", "player");
spawnPoint.setMetadata("direction", "north");
```

## Level Editing Tips

### Using GIMP

1. Create new image (20x15 pixels for 20x15 tile level)
2. View → Zoom → 1600% (to see pixels clearly)
3. Use pencil tool with 1px brush
4. Use color picker for exact colors
5. Export as PNG (disable compression)

### Using Aseprite

1. File → New (20x15 pixels)
2. View → Grid (16x16 pixels)
3. Use pixel-perfect tools
4. File → Export → PNG

### Color Palette

Create a palette file for consistency:

```
Grass:  #00FF00 (0,255,0)
Dirt:   #8B4513 (139,69,19)
Stone:  #808080 (128,128,128)
Water:  #0000FF (0,0,255)
Tree:   #006400 (0,100,0)
Sand:   #F4A460 (244,164,96)
```

## Best Practices

1. **Keep tile size consistent** - 16x16 or 32x32 are common
2. **Use powers of 2** - Better for performance
3. **Minimize tile types** - Reuse tiles creatively
4. **Test collision** - Walk around entire level
5. **Plan camera bounds** - Avoid showing empty space
6. **Optimize large levels** - Use viewport culling (built-in)

## Common Issues

### Tiles render incorrectly

- Check pixel colors match exactly
- Verify tileset sprite coordinates
- Ensure no anti-aliasing on level image

### Collision feels wrong

- Check tile solid flags
- Test collision resolution algorithm
- Adjust player hitbox size

### Performance issues

- TiledLevel has built-in viewport culling
- Limit animated tiles
- Use texture atlases for tilesets

### Level doesn't load

- Check file path is correct
- Verify image format (PNG/JPG/JPEG only)
- Ensure image is not corrupted

## Complete Mini-Example

Simple level with just the essentials:

```java
// In init():
TiledLevel level = new TiledLevel("levels/test.png");
level.setTileSize(16);

// Create one tile
Tile grassTile = new Tile(grassSprite, false);
level.addTile(Color.toPixelInt(0, 255, 0, 255), grassTile);

// In update():
level.update(manager, delta);
level.setOffset(cameraX, cameraY);

// In render():
level.render(manager, ctx);
```

## Congratulations!

You've completed all the TransmuteCore tutorials! You now know how to:
- Create games with the engine
- Handle sprites and animation
- Process input and movement
- Detect and resolve collisions
- Manage game states
- Play audio
- Create tile-based levels

## Next Steps

- Build a complete game
- Explore the [COOKBOOK.md](../COOKBOOK.md) for more patterns
- Check [DX_FEATURES.md](../DX_FEATURES.md) for debugging tools
- Read the comprehensive reference docs:
  - [LEVELS.md](../LEVELS.md) - More on level systems
  - [ARCHITECTURE.md](../ARCHITECTURE.md) - Engine internals
  - [DEPLOYMENT.md](../DEPLOYMENT.md) - Publishing your game

## Resources

- [TiledLevel.java](../../packages/core/TransmuteCore/src/TransmuteCore/Level/TiledLevel.java)
- [Level.java](../../packages/core/TransmuteCore/src/TransmuteCore/Level/Level.java)
- [LEVELS.md](../LEVELS.md) - Complete level system guide
