# TransmuteCore Cookbook

Common patterns and solutions for game development with TransmuteCore.

## Table of Contents

- [Player Movement](#player-movement)
- [Camera Following](#camera-following)
- [Menu Systems](#menu-systems)
- [Save/Load Game State](#saveload-game-state)
- [Particle Effects](#particle-effects)
- [Screen Shake](#screen-shake)
- [Collision Detection](#collision-detection)
- [Animation System](#animation-system)

---

## Player Movement

### Basic WASD Movement

```java
@Override
public void update(Manager manager, double delta) {
    int speed = 2;
    
    if (Input.isKeyHeld(KeyEvent.VK_W)) y -= speed;
    if (Input.isKeyHeld(KeyEvent.VK_S)) y += speed;
    if (Input.isKeyHeld(KeyEvent.VK_A)) x -= speed;
    if (Input.isKeyHeld(KeyEvent.VK_D)) x += speed;
}
```

### Smooth Movement with Delta Time

```java
@Override
public void update(Manager manager, double delta) {
    float speed = 100.0f; // pixels per second
    float movement = (float)(speed * delta / 60.0);
    
    if (Input.isKeyHeld(KeyEvent.VK_W)) y -= movement;
    if (Input.isKeyHeld(KeyEvent.VK_S)) y += movement;
    if (Input.isKeyHeld(KeyEvent.VK_A)) x -= movement;
    if (Input.isKeyHeld(KeyEvent.VK_D)) x += movement;
}
```

### Movement with Acceleration

```java
private float velocityX = 0;
private float velocityY = 0;
private float acceleration = 0.5f;
private float friction = 0.9f;
private float maxSpeed = 5.0f;

@Override
public void update(Manager manager, double delta) {
    // Apply input
    if (Input.isKeyHeld(KeyEvent.VK_W)) velocityY -= acceleration;
    if (Input.isKeyHeld(KeyEvent.VK_S)) velocityY += acceleration;
    if (Input.isKeyHeld(KeyEvent.VK_A)) velocityX -= acceleration;
    if (Input.isKeyHeld(KeyEvent.VK_D)) velocityX += acceleration;
    
    // Cap speed
    float speed = (float)Math.sqrt(velocityX * velocityX + velocityY * velocityY);
    if (speed > maxSpeed) {
        velocityX = (velocityX / speed) * maxSpeed;
        velocityY = (velocityY / speed) * maxSpeed;
    }
    
    // Apply velocity
    x += velocityX;
    y += velocityY;
    
    // Apply friction
    velocityX *= friction;
    velocityY *= friction;
}
```

---

## Camera Following

### Simple Camera Follow

```java
public class Camera {
    private int x, y;
    private int targetX, targetY;
    
    public void follow(int targetX, int targetY) {
        this.targetX = targetX;
        this.targetY = targetY;
    }
    
    public void update() {
        // Center camera on target
        x = targetX - TransmuteCore.getWidth() / 2;
        y = targetY - TransmuteCore.getHeight() / 2;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
}

// Usage in Level:
level.setOffset(camera.getX(), camera.getY());
```

### Smooth Camera with Lerp

```java
public class Camera {
    private float x, y;
    private float targetX, targetY;
    private float smoothness = 0.1f;
    
    public void update() {
        // Smooth following using lerp
        x = MathUtils.lerp(x, targetX - TransmuteCore.getWidth() / 2, smoothness);
        y = MathUtils.lerp(y, targetY - TransmuteCore.getHeight() / 2, smoothness);
    }
}
```

### Camera with Deadzone

```java
public class Camera {
    private int x, y;
    private int deadzoneWidth = 100;
    private int deadzoneHeight = 80;
    
    public void follow(int targetX, int targetY) {
        int screenCenterX = x + TransmuteCore.getWidth() / 2;
        int screenCenterY = y + TransmuteCore.getHeight() / 2;
        
        // Only move camera if target is outside deadzone
        int dx = targetX - screenCenterX;
        int dy = targetY - screenCenterY;
        
        if (Math.abs(dx) > deadzoneWidth / 2) {
            x += dx - Math.signum(dx) * deadzoneWidth / 2;
        }
        if (Math.abs(dy) > deadzoneHeight / 2) {
            y += dy - Math.signum(dy) * deadzoneHeight / 2;
        }
    }
}
```

---

## Menu Systems

### Simple State-Based Menu

```java
public class MenuState extends State {
    private int selectedOption = 0;
    private String[] options = {"Start Game", "Options", "Quit"};
    
    @Override
    public void update(Manager manager, double delta) {
        if (Input.isKeyPressed(KeyEvent.VK_UP)) {
            selectedOption--;
            if (selectedOption < 0) selectedOption = options.length - 1;
        }
        if (Input.isKeyPressed(KeyEvent.VK_DOWN)) {
            selectedOption++;
            if (selectedOption >= options.length) selectedOption = 0;
        }
        
        if (Input.isKeyPressed(KeyEvent.VK_ENTER)) {
            handleSelection(manager);
        }
    }
    
    @Override
    public void render(Manager manager, Context ctx) {
        ctx.clear(Color.BLACK);
        
        int y = 100;
        for (int i = 0; i < options.length; i++) {
            int color = (i == selectedOption) ? Color.YELLOW : Color.WHITE;
            ctx.renderText(options[i], 100, y, color);
            y += 20;
        }
    }
    
    private void handleSelection(Manager manager) {
        switch (selectedOption) {
            case 0: // Start Game
                manager.getStateManager().push(new GameState());
                break;
            case 1: // Options
                manager.getStateManager().push(new OptionsState());
                break;
            case 2: // Quit
                System.exit(0);
                break;
        }
    }
}
```

---

## Save/Load Game State

### Using TinyDatabase

```java
public void saveGame(String filename) {
    TinyDatabase db = new TinyDatabase();
    TinyObject saveData = new TinyObject("gameData");
    
    // Save player data
    saveData.addField("playerX", new TinyString(String.valueOf(player.getX())));
    saveData.addField("playerY", new TinyString(String.valueOf(player.getY())));
    saveData.addField("health", new TinyString(String.valueOf(player.getHealth())));
    
    // Save level progress
    saveData.addField("currentLevel", new TinyString(String.valueOf(currentLevel)));
    
    db.addObject(saveData);
    db.serializeToFile(filename);
    Logger.info("Game saved to: %s", filename);
}

public void loadGame(String filename) {
    TinyDatabase db = TinyDatabase.DeserializeFromFile(filename);
    TinyObject saveData = db.getObject("gameData");
    
    if (saveData != null) {
        int playerX = Integer.parseInt(saveData.getField("playerX").getValue());
        int playerY = Integer.parseInt(saveData.getField("playerY").getValue());
        int health = Integer.parseInt(saveData.getField("health").getValue());
        int level = Integer.parseInt(saveData.getField("currentLevel").getValue());
        
        player.setPosition(playerX, playerY);
        player.setHealth(health);
        loadLevel(level);
        
        Logger.info("Game loaded from: %s", filename);
    }
}
```

---

## Particle Effects

### Simple Particle System

```java
public class Particle extends Object {
    private float velocityX, velocityY;
    private int life;
    private int maxLife;
    
    public Particle(int x, int y, float vx, float vy, int life) {
        this.x = x;
        this.y = y;
        this.velocityX = vx;
        this.velocityY = vy;
        this.life = life;
        this.maxLife = life;
    }
    
    @Override
    public void update(Manager manager, double delta) {
        x += velocityX;
        y += velocityY;
        velocityY += 0.1f; // Gravity
        life--;
        
        if (life <= 0) {
            remove();
        }
    }
    
    @Override
    public void render(Manager manager, Context ctx) {
        float alpha = (float)life / maxLife;
        int color = Color.withAlpha(Color.WHITE, (int)(alpha * 255));
        ctx.blitPixel((int)x, (int)y, color);
    }
}

// Spawn particles
for (int i = 0; i < 20; i++) {
    float angle = (float)(Math.random() * Math.PI * 2);
    float speed = (float)(Math.random() * 3 + 1);
    float vx = (float)Math.cos(angle) * speed;
    float vy = (float)Math.sin(angle) * speed;
    level.add(new Particle(x, y, vx, vy, 30));
}
```

---

## Screen Shake

### Simple Screen Shake Effect

```java
public class ScreenShake {
    private int shakeIntensity = 0;
    private int shakeDuration = 0;
    
    public void shake(int intensity, int duration) {
        this.shakeIntensity = intensity;
        this.shakeDuration = duration;
    }
    
    public void update() {
        if (shakeDuration > 0) {
            shakeDuration--;
            if (shakeDuration == 0) {
                shakeIntensity = 0;
            }
        }
    }
    
    public int getOffsetX() {
        if (shakeDuration <= 0) return 0;
        return (int)(Math.random() * shakeIntensity * 2 - shakeIntensity);
    }
    
    public int getOffsetY() {
        if (shakeDuration <= 0) return 0;
        return (int)(Math.random() * shakeIntensity * 2 - shakeIntensity);
    }
}

// Usage in render:
ctx.clear(Color.BLACK);
int shakeX = screenShake.getOffsetX();
int shakeY = screenShake.getOffsetY();
level.setOffset(camera.getX() + shakeX, camera.getY() + shakeY);
```

---

## Collision Detection

### AABB Collision

```java
public boolean checkCollision(Object other) {
    return MathUtils.rectanglesOverlap(
        this.x, this.y, this.width, this.height,
        other.getX(), other.getY(), other.getWidth(), other.getHeight()
    );
}
```

### Circle Collision

```java
public boolean checkCollision(Object other) {
    int dx = this.x - other.getX();
    int dy = this.y - other.getY();
    int distance = (int)Math.sqrt(dx * dx + dy * dy);
    return distance < (this.radius + other.radius);
}
```

### Collision with Response

```java
public void resolveCollision(Object other) {
    if (!checkCollision(other)) return;
    
    // Calculate overlap
    int overlapX = Math.min(x + width, other.getX() + other.getWidth()) - 
                   Math.max(x, other.getX());
    int overlapY = Math.min(y + height, other.getY() + other.getHeight()) - 
                   Math.max(y, other.getY());
    
    // Push out on smallest overlap axis
    if (overlapX < overlapY) {
        if (x < other.getX()) {
            x -= overlapX;
        } else {
            x += overlapX;
        }
    } else {
        if (y < other.getY()) {
            y -= overlapY;
        } else {
            y += overlapY;
        }
    }
}
```

---

## Animation System

### Frame-Based Animation

```java
public class AnimatedSprite {
    private Sprite[] frames;
    private int currentFrame = 0;
    private int frameDelay = 5;
    private int frameTimer = 0;
    
    public void update() {
        frameTimer++;
        if (frameTimer >= frameDelay) {
            frameTimer = 0;
            currentFrame++;
            if (currentFrame >= frames.length) {
                currentFrame = 0;
            }
        }
    }
    
    public void render(Context ctx, int x, int y) {
        ctx.renderBitmap(frames[currentFrame], x, y);
    }
}
```

### Animation with SpriteManager

```java
// In init:
Spritesheet playerSheet = new Spritesheet(playerImage, 16);
Animation walkAnim = playerSheet.generateAnimation("walk", 100,
    new Tuple2i(0, 0), new Tuple2i(1, 0), new Tuple2i(2, 0), new Tuple2i(3, 0)
);

// In update:
walkAnim.update();

// In render:
ctx.renderBitmap(walkAnim.getCurrentSprite(), x, y);
```

---

## Audio Playback Patterns

### Basic Sound Effects

```java
import TransmuteCore.System.Asset.Type.Audio.AudioPlayer;

// Play sound on event
public void jump() {
    velocityY = jumpStrength;
    AudioPlayer.play("jump");
}

public void collectCoin() {
    score++;
    AudioPlayer.play("coin");
}
```

### Background Music Management

```java
public class MusicManager {
    private static String currentTrack = null;
    
    public static void playMusic(String trackName) {
        if (trackName.equals(currentTrack)) return;
        
        // Stop current
        if (currentTrack != null) {
            AudioPlayer.stop(currentTrack);
        }
        
        // Start new
        AudioPlayer.loop(trackName);
        currentTrack = trackName;
    }
    
    public static void stopMusic() {
        if (currentTrack != null) {
            AudioPlayer.stop(currentTrack);
            currentTrack = null;
        }
    }
}

// Usage in states:
public class MenuState extends State {
    @Override
    public void onEnter() {
        MusicManager.playMusic("menuMusic");
    }
}
```

### Audio with Volume Control

```java
import javax.sound.sampled.*;

public class VolumeManager {
    public static void setVolume(Clip clip, float volume) {
        if (clip == null) return;
        
        try {
            FloatControl volumeControl = 
                (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            
            float min = volumeControl.getMinimum();
            float max = volumeControl.getMaximum();
            
            // Convert 0.0-1.0 to decibels
            float dB;
            if (volume <= 0) {
                dB = min;
            } else {
                dB = min + (max - min) * volume;
            }
            
            volumeControl.setValue(dB);
        } catch (Exception e) {
            // Volume control not supported
        }
    }
}

// Usage:
Clip music = AssetManager.getAudio("music");
VolumeManager.setVolume(music, 0.5f);  // 50% volume
AudioPlayer.loop("music");
```

### Audio Pools for Rapid Fire

```java
public class AudioPool {
    private Map<String, List<Clip>> pools = new HashMap<>();
    
    public void createPool(String audioName, int poolSize) {
        List<Clip> pool = new ArrayList<>();
        
        for (int i = 0; i < poolSize; i++) {
            Clip clip = Audio.load(getClass(), "audio/" + audioName + ".wav");
            if (clip != null) {
                pool.add(clip);
            }
        }
        
        pools.put(audioName, pool);
    }
    
    public void play(String audioName) {
        List<Clip> pool = pools.get(audioName);
        if (pool == null) return;
        
        // Find available clip
        for (Clip clip : pool) {
            if (!clip.isRunning()) {
                clip.setFramePosition(0);
                clip.start();
                return;
            }
        }
    }
}

// Usage - for weapon that fires rapidly:
AudioPool audioPool = new AudioPool();
audioPool.createPool("laser", 5);  // 5 simultaneous sounds

// In game loop:
if (Input.isKeyPressed(KeyEvent.VK_SPACE)) {
    fireWeapon();
    audioPool.play("laser");  // Never cuts off
}
```

---

## Tile-Based Collision

### Basic Tile Collision Check

```java
public boolean checkTileCollision(TiledLevel level, int x, int y, int width, int height) {
    int tileSize = level.getTileSize();
    
    // Check all corners of the bounding box
    int left = x / tileSize;
    int right = (x + width - 1) / tileSize;
    int top = y / tileSize;
    int bottom = (y + height - 1) / tileSize;
    
    // Check each corner
    if (isTileSolid(level, left, top)) return true;
    if (isTileSolid(level, right, top)) return true;
    if (isTileSolid(level, left, bottom)) return true;
    if (isTileSolid(level, right, bottom)) return true;
    
    return false;
}

private boolean isTileSolid(TiledLevel level, int tileX, int tileY) {
    Tile tile = level.getTile(tileX, tileY);
    return tile != null && tile.isSolid();
}
```

### Tile Collision with Resolution

```java
public class TileCollisionResolver {
    
    public static void resolvePlayerTileCollision(Player player, TiledLevel level) {
        int tileSize = level.getTileSize();
        
        // Get player tile coordinates
        int playerTileX = player.getX() / tileSize;
        int playerTileY = player.getY() / tileSize;
        
        // Check 3x3 grid around player
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                int tileX = playerTileX + dx;
                int tileY = playerTileY + dy;
                
                Tile tile = level.getTile(tileX, tileY);
                if (tile != null && tile.isSolid()) {
                    resolveAABB(
                        player,
                        tileX * tileSize,
                        tileY * tileSize,
                        tileSize,
                        tileSize
                    );
                }
            }
        }
    }
    
    private static void resolveAABB(Player player, int tileX, int tileY, 
                                     int tileW, int tileH) {
        int playerX = player.getX();
        int playerY = player.getY();
        int playerW = player.getWidth();
        int playerH = player.getHeight();
        
        // Check if actually colliding
        if (!(playerX < tileX + tileW &&
              playerX + playerW > tileX &&
              playerY < tileY + tileH &&
              playerY + playerH > tileY)) {
            return;  // No collision
        }
        
        // Calculate overlaps
        int overlapLeft = (playerX + playerW) - tileX;
        int overlapRight = (tileX + tileW) - playerX;
        int overlapTop = (playerY + playerH) - tileY;
        int overlapBottom = (tileY + tileH) - playerY;
        
        // Find smallest overlap
        int minOverlap = Math.min(
            Math.min(overlapLeft, overlapRight),
            Math.min(overlapTop, overlapBottom)
        );
        
        // Push out on smallest axis
        if (minOverlap == overlapLeft) {
            player.setX(tileX - playerW);
            player.setVelocityX(0);
        } else if (minOverlap == overlapRight) {
            player.setX(tileX + tileW);
            player.setVelocityX(0);
        } else if (minOverlap == overlapTop) {
            player.setY(tileY - playerH);
            player.setVelocityY(0);
            player.setGrounded(true);
        } else if (minOverlap == overlapBottom) {
            player.setY(tileY + tileH);
            player.setVelocityY(0);
        }
    }
}
```

### One-Way Platform Tiles

```java
public class PlatformTile extends Tile {
    public PlatformTile(Sprite sprite) {
        super(sprite, false);  // Not solid by default
    }
    
    public boolean isOneWayPlatform() {
        return true;
    }
}

// In collision check:
public void checkPlatformCollision(Player player, PlatformTile platform, 
                                   int tileX, int tileY, int tileSize) {
    // Only collide if player is falling and above platform
    if (player.getVelocityY() <= 0) return;  // Not falling
    
    int playerBottom = player.getY() + player.getHeight();
    int platformTop = tileY * tileSize;
    
    // Check if player was above platform in previous frame
    int prevPlayerBottom = playerBottom - (int)player.getVelocityY();
    
    if (prevPlayerBottom <= platformTop && playerBottom >= platformTop) {
        // Land on platform
        player.setY(platformTop - player.getHeight());
        player.setVelocityY(0);
        player.setGrounded(true);
    }
}
```

---

## Reusable Components

### Health Component

```java
public class HealthComponent {
    private int maxHealth;
    private int currentHealth;
    private boolean invulnerable = false;
    private long invulnerableUntil = 0;
    
    public HealthComponent(int maxHealth) {
        this.maxHealth = maxHealth;
        this.currentHealth = maxHealth;
    }
    
    public void takeDamage(int amount) {
        if (invulnerable || currentHealth <= 0) return;
        
        currentHealth = Math.max(0, currentHealth - amount);
        
        // Brief invulnerability after hit
        setInvulnerable(1000);  // 1 second
    }
    
    public void heal(int amount) {
        currentHealth = Math.min(maxHealth, currentHealth + amount);
    }
    
    public void setInvulnerable(long durationMs) {
        invulnerable = true;
        invulnerableUntil = System.currentTimeMillis() + durationMs;
    }
    
    public void update() {
        if (invulnerable && System.currentTimeMillis() > invulnerableUntil) {
            invulnerable = false;
        }
    }
    
    public boolean isAlive() { return currentHealth > 0; }
    public boolean isDead() { return currentHealth <= 0; }
    public int getHealth() { return currentHealth; }
    public int getMaxHealth() { return maxHealth; }
    public float getHealthPercent() { return (float)currentHealth / maxHealth; }
    public boolean isInvulnerable() { return invulnerable; }
}

// Usage:
public class Player extends GameObject {
    private HealthComponent health = new HealthComponent(100);
    
    @Override
    public void update(Manager manager, double delta) {
        health.update();
        
        if (health.isDead()) {
            die();
        }
    }
    
    public void onHit(int damage) {
        health.takeDamage(damage);
        if (!health.isDead()) {
            AudioPlayer.play("hurt");
        }
    }
}
```

### Timer Component

```java
public class Timer {
    private long duration;
    private long startTime;
    private boolean running = false;
    private Runnable onComplete;
    
    public Timer(long durationMs) {
        this.duration = durationMs;
    }
    
    public Timer(long durationMs, Runnable onComplete) {
        this.duration = durationMs;
        this.onComplete = onComplete;
    }
    
    public void start() {
        startTime = System.currentTimeMillis();
        running = true;
    }
    
    public void update() {
        if (!running) return;
        
        if (System.currentTimeMillis() - startTime >= duration) {
            running = false;
            if (onComplete != null) {
                onComplete.run();
            }
        }
    }
    
    public void reset() {
        running = false;
    }
    
    public boolean isRunning() { return running; }
    public boolean isComplete() { return !running && startTime > 0; }
    
    public float getProgress() {
        if (!running) return 1.0f;
        long elapsed = System.currentTimeMillis() - startTime;
        return Math.min(1.0f, (float)elapsed / duration);
    }
    
    public long getTimeRemaining() {
        if (!running) return 0;
        long elapsed = System.currentTimeMillis() - startTime;
        return Math.max(0, duration - elapsed);
    }
}

// Usage:
public class PowerUp {
    private Timer duration = new Timer(5000, () -> deactivate());
    
    public void activate() {
        duration.start();
    }
    
    public void update() {
        duration.update();
    }
}
```

### Movement Component

```java
public class MovementComponent {
    private float velocityX = 0;
    private float velocityY = 0;
    private float acceleration = 0.5f;
    private float friction = 0.85f;
    private float maxSpeed = 5.0f;
    private float gravity = 0.5f;
    private boolean applyGravity = true;
    
    public void accelerate(float ax, float ay) {
        velocityX += ax * acceleration;
        velocityY += ay * acceleration;
    }
    
    public void applyFriction() {
        velocityX *= friction;
        velocityY *= friction;
        
        if (Math.abs(velocityX) < 0.1f) velocityX = 0;
        if (Math.abs(velocityY) < 0.1f) velocityY = 0;
    }
    
    public void limitSpeed() {
        float speed = (float)Math.sqrt(velocityX * velocityX + velocityY * velocityY);
        if (speed > maxSpeed) {
            velocityX = (velocityX / speed) * maxSpeed;
            velocityY = (velocityY / speed) * maxSpeed;
        }
    }
    
    public void applyGravity() {
        if (applyGravity) {
            velocityY += gravity;
        }
    }
    
    public void update(GameObject object, double delta) {
        applyGravity();
        applyFriction();
        limitSpeed();
        
        object.setX(object.getX() + (int)(velocityX * delta));
        object.setY(object.getY() + (int)(velocityY * delta));
    }
    
    // Getters and setters
    public float getVelocityX() { return velocityX; }
    public float getVelocityY() { return velocityY; }
    public void setVelocityX(float vx) { this.velocityX = vx; }
    public void setVelocityY(float vy) { this.velocityY = vy; }
    public void setMaxSpeed(float speed) { this.maxSpeed = speed; }
    public void setGravity(float g) { this.gravity = g; }
    public void setApplyGravity(boolean apply) { this.applyGravity = apply; }
}

// Usage:
public class Player extends GameObject {
    private MovementComponent movement = new MovementComponent();
    
    @Override
    public void update(Manager manager, double delta) {
        // Handle input
        if (Input.isKeyHeld(KeyEvent.VK_RIGHT)) {
            movement.accelerate(1, 0);
        }
        if (Input.isKeyHeld(KeyEvent.VK_LEFT)) {
            movement.accelerate(-1, 0);
        }
        
        // Update movement
        movement.update(this, delta);
    }
}
```

---

## Tips and Best Practices

1. **Always use delta time** for movement to ensure consistent speed across different frame rates
2. **Profile your game** using `Profiler.begin()/end()` to identify bottlenecks
3. **Use the debug overlay** (F3) to monitor performance during development
4. **Save screenshots** (F12) of bugs and progress for documentation
5. **Validate inputs** before processing to avoid crashes
6. **Use builders** (GameBuilder, LevelBuilder) for cleaner initialization code
7. **Separate logic and rendering** - update in `update()`, draw in `render()`
8. **Cache expensive calculations** rather than recomputing every frame
9. **Use object pools** for frequently created/destroyed objects (particles, bullets)
10. **Test on target hardware** - performance can vary significantly

---

For more patterns and complete examples, see the tutorial series in `docs/tutorials/` or check out [transmute-starter](https://github.com/transmute-games/transmute-starter).
