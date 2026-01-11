# Tutorial 6: Audio System

Learn how to add sound effects and music to your game using TransmuteCore's audio system.

## What You'll Learn

- Loading audio files
- Playing sound effects
- Playing background music
- Managing audio volume
- Creating an audio manager
- Audio best practices

## Prerequisites

- Completed [Tutorial 5: State Management](05-state-management.md)
- Audio files in WAV format (MP3 not supported by Java Clip)

## Understanding the Audio System

TransmuteCore uses Java's `javax.sound.sampled` API with the `Clip` class for audio playback. Audio is loaded as assets and played through the `AudioPlayer` utility.

### Supported Formats

- **WAV** - Recommended, fully supported
- **AIFF** - Supported
- **AU** - Supported
- MP3 - NOT supported (requires external libraries)

## Step 1: Create a New Project

Use the transmute CLI to create a new project:

```bash
transmute new audio-demo -t basic
cd audio-demo
```

## Step 2: Prepare Audio Files

Create audio directories and add your audio files:

```bash
mkdir -p src/main/resources/audio/sfx
mkdir -p src/main/resources/audio/music
```

Organize your audio files:

```
src/main/resources/
├── audio/
│   ├── sfx/
│   │   ├── jump.wav
│   │   ├── coin.wav
│   │   ├── hurt.wav
│   │   └── shoot.wav
│   └── music/
│       ├── menu.wav
│       ├── gameplay.wav
│       └── gameover.wav
```

## Step 3: Load Audio Assets

In your `src/main/java/com/example/audiodemo/Game.java` file's `init()` method:

```java
import TransmuteCore.assets.types.Audio;
import TransmuteCore.assets.AssetManager;

@Override
public void init() {
    // Load sound effects
    new Audio("jump", "audio/sfx/jump.wav");
    new Audio("coin", "audio/sfx/coin.wav");
    new Audio("hurt", "audio/sfx/hurt.wav");
    
    // Load music
    new Audio("menuMusic", "audio/music/menu.wav");
    new Audio("gameMusic", "audio/music/gameplay.wav");
    
    // Load all assets
    AssetManager.getGlobalInstance().load();
}
```

## Step 4: Play Sound Effects

Use `AudioPlayer` to play sounds:

```java
import TransmuteCore.assets.types.AudioPlayer;

// Play a sound effect
AudioPlayer.play("jump");

// Example in player class
public class Player {
    public void jump() {
        if (isGrounded) {
            velocityY = jumpStrength;
            isGrounded = false;
            AudioPlayer.play("jump");
        }
    }
    
    public void collectCoin() {
        score++;
        AudioPlayer.play("coin");
    }
}
```

## Step 5: Play Background Music

Music should loop continuously:

```java
// Start looping music
AudioPlayer.loop("menuMusic");

// Stop music when changing states
AudioPlayer.stop("menuMusic");

// Resume paused music
AudioPlayer.resume("gameMusic");
```

## Step 6: Create an Audio Manager

Create `src/main/java/com/example/audiodemo/GameAudioManager.java`:

```java
package com.example.audiodemo;

import TransmuteCore.assets.AssetManager;
import TransmuteCore.assets.types.AudioPlayer;
import javax.sound.sampled.Clip;

public class GameAudioManager {
    
    private static String currentMusic = null;
    private static float masterVolume = 1.0f;
    private static float sfxVolume = 1.0f;
    private static float musicVolume = 1.0f;
    private static boolean muted = false;
    
    /**
     * Play a sound effect
     */
    public static void playSfx(String name) {
        if (muted || sfxVolume <= 0) return;
        
        Clip clip = AssetManager.getGlobalInstance().getAudio(name);
        if (clip != null) {
            setVolume(clip, sfxVolume * masterVolume);
            AudioPlayer.play(name);
        }
    }
    
    /**
     * Start playing music (with crossfade)
     */
    public static void playMusic(String name) {
        if (name.equals(currentMusic)) return;
        
        // Stop current music
        if (currentMusic != null) {
            AudioPlayer.stop(currentMusic);
        }
        
        // Start new music
        if (!muted && musicVolume > 0) {
            Clip clip = AssetManager.getGlobalInstance().getAudio(name);
            if (clip != null) {
                setVolume(clip, musicVolume * masterVolume);
                AudioPlayer.loop(name);
                currentMusic = name;
            }
        }
    }
    
    /**
     * Stop all music
     */
    public static void stopMusic() {
        if (currentMusic != null) {
            AudioPlayer.stop(currentMusic);
            currentMusic = null;
        }
    }
    
    /**
     * Set master volume (0.0 to 1.0)
     */
    public static void setMasterVolume(float volume) {
        masterVolume = Math.max(0, Math.min(1, volume));
        updateVolumes();
    }
    
    /**
     * Set SFX volume (0.0 to 1.0)
     */
    public static void setSfxVolume(float volume) {
        sfxVolume = Math.max(0, Math.min(1, volume));
    }
    
    /**
     * Set music volume (0.0 to 1.0)
     */
    public static void setMusicVolume(float volume) {
        musicVolume = Math.max(0, Math.min(1, volume));
        updateVolumes();
    }
    
    /**
     * Toggle mute
     */
    public static void toggleMute() {
        muted = !muted;
        if (muted) {
            stopMusic();
        } else if (currentMusic != null) {
            AudioPlayer.loop(currentMusic);
        }
    }
    
    /**
     * Set volume for a specific clip
     */
    private static void setVolume(Clip clip, float volume) {
        if (clip == null) return;
        
        try {
            FloatControl volumeControl = 
                (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            
            float min = volumeControl.getMinimum();
            float max = volumeControl.getMaximum();
            
            // Convert 0-1 to decibels
            float dB = min + (max - min) * volume;
            volumeControl.setValue(dB);
        } catch (Exception e) {
            // Volume control not available
        }
    }
    
    /**
     * Update all active audio volumes
     */
    private static void updateVolumes() {
        if (currentMusic != null) {
            Clip musicClip = AssetManager.getGlobalInstance().getAudio(currentMusic);
            setVolume(musicClip, musicVolume * masterVolume);
        }
    }
    
    public static float getMasterVolume() { return masterVolume; }
    public static float getSfxVolume() { return sfxVolume; }
    public static float getMusicVolume() { return musicVolume; }
    public static boolean isMuted() { return muted; }
}
```

## Step 6: Integrate with States

Add music to your game states:

```java
public class MainMenuState extends State {
    @Override
    public void onEnter() {
        GameAudioManager.playMusic("menuMusic");
    }
    
    @Override
    public void onExit() {
        GameAudioManager.stopMusic();
    }
}

public class GamePlayState extends State {
    @Override
    public void onEnter() {
        GameAudioManager.playMusic("gameMusic");
    }
    
    @Override
    public void update(Manager manager, double delta) {
        // Play sounds during gameplay
        if (manager.getInput().isKeyPressed(KeyEvent.VK_SPACE)) {
            player.jump();
            GameAudioManager.playSfx("jump");
        }
    }
}

public class PauseState extends State {
    @Override
    public void onEnter() {
        // Pause music when entering pause state
        Clip music = AssetManager.getGlobalInstance().getAudio("gameMusic");
        if (music != null && music.isRunning()) {
            music.stop();
        }
    }
    
    @Override
    public void onExit() {
        // Resume music when exiting pause
        GameAudioManager.playMusic("gameMusic");
    }
}
```

## Advanced Techniques

### 1. Audio Pool for Rapid Fire Sounds

For sounds that play frequently:

```java
public class AudioPool {
    private Map<String, List<Clip>> pools = new HashMap<>();
    
    public void createPool(String name, int size) {
        List<Clip> pool = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            Clip clip = Audio.load(getClass(), "audio/" + name + ".wav");
            pool.add(clip);
        }
        pools.put(name, pool);
    }
    
    public void play(String name) {
        List<Clip> pool = pools.get(name);
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
```

### 2. Audio Fading

Fade music in/out:

```java
public class AudioFader {
    public static void fadeOut(String name, int durationMs) {
        Clip clip = AssetManager.getGlobalInstance().getAudio(name);
        if (clip == null) return;
        
        new Thread(() -> {
            FloatControl volume = 
                (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float start = volume.getValue();
            float end = volume.getMinimum();
            
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < durationMs) {
                float progress = (float)(System.currentTimeMillis() - startTime) / durationMs;
                volume.setValue(start + (end - start) * progress);
                
                try { Thread.sleep(50); } catch (InterruptedException e) {}
            }
            
            clip.stop();
        }).start();
    }
}
```

### 3. Positional Audio

Simple 2D positional audio:

```java
public static void playPositional(String name, int sourceX, int sourceY, 
                                  int listenerX, int listenerY, float maxDistance) {
    float dx = sourceX - listenerX;
    float dy = sourceY - listenerY;
    float distance = (float) Math.sqrt(dx * dx + dy * dy);
    
    if (distance > maxDistance) return;
    
    float volume = 1.0f - (distance / maxDistance);
    
    Clip clip = AssetManager.getGlobalInstance().getAudio(name);
    GameAudioManager.setVolume(clip, volume);
    AudioPlayer.play(name);
}
```

## Best Practices

### Audio File Guidelines

1. **Keep files small** - Compress audio, use low sample rates for SFX
2. **Use short loops** - Music loops should be seamless
3. **Normalize audio** - Keep volume levels consistent
4. **Mono for SFX** - Stereo for music only

### Performance

1. **Preload audio** - Load in init(), not during gameplay
2. **Limit simultaneous sounds** - Use audio pools
3. **Stop unused clips** - Free memory when done
4. **Use short clips** - Long clips use more memory

### Design

1. **Audio feedback** - Confirm player actions with sound
2. **Volume control** - Always provide volume sliders
3. **Mute option** - Let players disable audio
4. **Audio ducking** - Lower music when SFX plays

## Common Issues

### Audio not playing

- Check file format (must be WAV/AIFF/AU)
- Verify file path is correct
- Ensure `AssetManager.getGlobalInstance().load()` was called
- Check that audio isn't muted

### Audio stutters or lags

- Preload audio in init()
- Use smaller audio files
- Don't load audio during gameplay

### Volume control doesn't work

- Not all systems support volume control via `FloatControl`
- Fallback to muting/unmuting

### Audio file too large

```bash
# Convert to 22kHz mono WAV (good for SFX)
ffmpeg -i input.wav -ar 22050 -ac 1 output.wav

# Convert to 44kHz stereo WAV (good for music)
ffmpeg -i input.mp3 -ar 44100 -ac 2 output.wav
```

## Complete Example

```java
package com.example.audiodemo;

import TransmuteCore.core.GameConfig;
import TransmuteCore.core.Manager;
import TransmuteCore.core.TransmuteCore;
import TransmuteCore.core.interfaces.services.IRenderer;
import TransmuteCore.graphics.Context;
import TransmuteCore.graphics.Color;
import TransmuteCore.assets.AssetManager;
import TransmuteCore.assets.types.Audio;
import TransmuteCore.assets.types.Font;
import TransmuteCore.input.Input;
import java.awt.event.KeyEvent;

public class Game extends TransmuteCore {
    
    public Game(GameConfig config) {
        super(config);
    }
    
    @Override
    public void init() {
        Font.initializeDefaultFont("fonts/font.png");
        
        // Load audio
        new Audio("jump", "audio/jump.wav");
        new Audio("coin", "audio/coin.wav");
        new Audio("music", "audio/music.wav");
        
        AssetManager.getGlobalInstance().load();
        
        // Start music
        GameAudioManager.playMusic("music");
    }
    
    @Override
    public void update(Manager manager, double delta) {
        if (manager.getInput().isKeyPressed(KeyEvent.VK_1)) {
            GameAudioManager.playSfx("jump");
        }
        if (manager.getInput().isKeyPressed(KeyEvent.VK_2)) {
            GameAudioManager.playSfx("coin");
        }
        if (manager.getInput().isKeyPressed(KeyEvent.VK_M)) {
            GameAudioManager.toggleMute();
        }
    }
    
    @Override
    public void render(Manager manager, IRenderer renderer) {
        Context ctx = (Context) renderer;
        
        ctx.setClearColor(Color.toPixelInt(20, 30, 40, 255));
        
        int white = Color.toPixelInt(255, 255, 255, 255);
        ctx.renderText("Press 1 for jump sound", 10, 10, white);
        ctx.renderText("Press 2 for coin sound", 10, 20, white);
        ctx.renderText("Press M to mute", 10, 30, white);
        ctx.renderText("Muted: " + GameAudioManager.isMuted(), 10, 50, white);
    }
    
    public static void main(String[] args) {
        GameConfig config = new GameConfig.Builder()
            .title("Audio Demo")
            .version("1.0")
            .dimensions(320, GameConfig.ASPECT_RATIO_SQUARE)
            .scale(3)
            .build();
        
        Game game = new Game(config);
        game.start();
    }
}
```

## What's Next?

In the next tutorial, you'll learn:
- Creating tile-based levels
- Loading levels from images
- Tile collision
- Viewport culling

Continue to [Tutorial 7: Level Design with Tiles](07-level-design.md)

## Resources

- [Audio.java](../../packages/core/TransmuteCore/src/TransmuteCore/System/Asset/Type/Audio/Audio.java)
- [AudioPlayer.java](../../packages/core/TransmuteCore/src/TransmuteCore/System/Asset/Type/Audio/AudioPlayer.java)
- [AUDIO.md](../AUDIO.md) - Complete audio reference
