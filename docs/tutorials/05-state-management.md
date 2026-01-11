# Tutorial 5: State Management

In this tutorial, you'll learn how to create and manage multiple game states like menus, gameplay screens, pause menus, and more. State management is essential for organizing complex games with multiple screens.

## What You'll Learn

- Understanding the State system
- Creating menu states
- Transitioning between states
- Creating a pause menu
- Passing data between states
- State stack management
- Building a complete game flow

## Prerequisites

- Completed [Tutorial 4: Collision Detection](04-collision-detection.md)
- Understanding of Java inheritance
- Familiarity with the Manager system

## Understanding States

A **State** represents a screen or mode in your game:
- **Menu State** - Main menu, options, credits
- **Game State** - Active gameplay
- **Pause State** - Pause menu overlay
- **Loading State** - Asset loading screen

States are managed in a **stack**, where only the top state updates and renders (by default).

## State Lifecycle

Each state has these methods:

```java
public abstract class State {
    public abstract void init();              // Called when state is created
    public abstract void update(Manager m, double delta);  // Called each frame
    public abstract void render(Manager m, Context ctx);   // Called each frame
    public void onEnter() {}                  // Called when state becomes active
    public void onExit() {}                   // Called when state is deactivated
}
```

## Step 1: Create the Main Menu State

Create `src/main/java/states/MainMenuState.java`:

```java
package states;

import TransmuteCore.GameEngine.Manager;
import TransmuteCore.Graphics.Context;
import TransmuteCore.Graphics.Color;
import TransmuteCore.States.State;
import TransmuteCore.States.StateManager;
import TransmuteCore.Input.Input;
import java.awt.event.KeyEvent;

public class MainMenuState extends State {
    
    private String[] menuOptions = {"Start Game", "Options", "Exit"};
    private int selectedIndex = 0;
    private int titleY = 60;
    private float titleBob = 0;
    
    public MainMenuState(StateManager stateManager) {
        super("mainMenu", stateManager);
        init();
    }
    
    @Override
    public void init() {
        // Initialize menu-specific resources
    }
    
    @Override
    public void onEnter() {
        selectedIndex = 0; // Reset selection
    }
    
    @Override
    public void update(Manager manager, double delta) {
        // Animate title
        titleBob += 0.05f;
        
        // Navigate menu
        if (Input.isKeyPressed(KeyEvent.VK_UP) || Input.isKeyPressed(KeyEvent.VK_W)) {
            selectedIndex--;
            if (selectedIndex < 0) {
                selectedIndex = menuOptions.length - 1;
            }
        }
        
        if (Input.isKeyPressed(KeyEvent.VK_DOWN) || Input.isKeyPressed(KeyEvent.VK_S)) {
            selectedIndex++;
            if (selectedIndex >= menuOptions.length) {
                selectedIndex = 0;
            }
        }
        
        // Select option
        if (Input.isKeyPressed(KeyEvent.VK_ENTER) || 
            Input.isKeyPressed(KeyEvent.VK_SPACE)) {
            handleSelection(manager);
        }
    }
    
    private void handleSelection(Manager manager) {
        switch (selectedIndex) {
            case 0: // Start Game
                stateManager.push(new GamePlayState(stateManager));
                break;
            case 1: // Options
                stateManager.push(new OptionsState(stateManager));
                break;
            case 2: // Exit
                System.exit(0);
                break;
        }
    }
    
    @Override
    public void render(Manager manager, Context ctx) {
        // Background
        ctx.setClearColor(Color.toPixelInt(10, 20, 30, 255));
        
        // Title with bob animation
        int white = Color.toPixelInt(255, 255, 255, 255);
        int yellow = Color.toPixelInt(255, 255, 100, 255);
        
        int titleOffset = (int)(Math.sin(titleBob) * 3);
        ctx.renderText("MY GAME", 120, titleY + titleOffset, yellow);
        
        // Menu options
        int startY = 120;
        int spacing = 20;
        
        for (int i = 0; i < menuOptions.length; i++) {
            int y = startY + (i * spacing);
            int color = (i == selectedIndex) ? yellow : white;
            
            // Draw selection indicator
            if (i == selectedIndex) {
                ctx.renderText(">", 100, y, yellow);
            }
            
            ctx.renderText(menuOptions[i], 120, y, color);
        }
        
        // Instructions
        int gray = Color.toPixelInt(150, 150, 150, 255);
        ctx.renderText("ARROW KEYS to navigate", 80, 200, gray);
        ctx.renderText("ENTER to select", 95, 210, gray);
    }
}
```

## Step 2: Create the Gameplay State

Create `src/main/java/states/GamePlayState.java`:

```java
package states;

import TransmuteCore.GameEngine.Manager;
import TransmuteCore.Graphics.Context;
import TransmuteCore.Graphics.Color;
import TransmuteCore.Graphics.Bitmap;
import TransmuteCore.Graphics.Sprites.Sprite;
import TransmuteCore.Graphics.Sprites.Spritesheet;
import TransmuteCore.Graphics.Sprites.Animation;
import TransmuteCore.States.State;
import TransmuteCore.States.StateManager;
import TransmuteCore.System.Asset.AssetManager;
import TransmuteCore.Units.Tuple2i;
import TransmuteCore.Input.Input;
import java.awt.event.KeyEvent;

public class GamePlayState extends State {
    
    private Player player;
    private int score = 0;
    private float gameTime = 0;
    private boolean isPaused = false;
    
    public GamePlayState(StateManager stateManager) {
        super("gameplay", stateManager);
        init();
    }
    
    @Override
    public void init() {
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
            null, // manager will be set in update
            new Tuple2i(160 - 8, 120 - 8),
            idleAnimation,
            walkAnimation
        );
    }
    
    @Override
    public void onEnter() {
        // Reset game state if needed
    }
    
    @Override
    public void update(Manager manager, double delta) {
        // Check for pause
        if (Input.isKeyPressed(KeyEvent.VK_ESCAPE)) {
            stateManager.push(new PauseState(stateManager));
            return;
        }
        
        // Check for game over
        if (Input.isKeyPressed(KeyEvent.VK_G)) {
            stateManager.push(new GameOverState(stateManager, score));
            return;
        }
        
        // Update game
        gameTime += delta;
        player.update(manager, delta);
        
        // Update score (example)
        if (gameTime % 60 < 1) { // Every ~60 frames
            score += 1;
        }
    }
    
    @Override
    public void render(Manager manager, Context ctx) {
        // Background
        ctx.setClearColor(Color.toPixelInt(40, 60, 80, 255));
        
        // Render game objects
        player.render(manager, ctx);
        
        // UI
        int white = Color.toPixelInt(255, 255, 255, 255);
        ctx.renderText("Score: " + score, 10, 10, white);
        ctx.renderText("Time: " + (int)gameTime, 10, 20, white);
        ctx.renderText("ESC to pause", 10, 220, white);
        ctx.renderText("G for game over (test)", 10, 230, white);
    }
    
    public int getScore() {
        return score;
    }
}
```

## Step 3: Create the Pause State

Create `src/main/java/states/PauseState.java`:

```java
package states;

import TransmuteCore.GameEngine.Manager;
import TransmuteCore.Graphics.Context;
import TransmuteCore.Graphics.Color;
import TransmuteCore.States.State;
import TransmuteCore.States.StateManager;
import TransmuteCore.Input.Input;
import java.awt.event.KeyEvent;

public class PauseState extends State {
    
    private String[] menuOptions = {"Resume", "Options", "Main Menu"};
    private int selectedIndex = 0;
    
    public PauseState(StateManager stateManager) {
        super("pause", stateManager);
        init();
    }
    
    @Override
    public void init() {
        // Initialization
    }
    
    @Override
    public void update(Manager manager, double delta) {
        // Navigate menu
        if (Input.isKeyPressed(KeyEvent.VK_UP)) {
            selectedIndex--;
            if (selectedIndex < 0) {
                selectedIndex = menuOptions.length - 1;
            }
        }
        
        if (Input.isKeyPressed(KeyEvent.VK_DOWN)) {
            selectedIndex++;
            if (selectedIndex >= menuOptions.length) {
                selectedIndex = 0;
            }
        }
        
        // Quick resume with ESC
        if (Input.isKeyPressed(KeyEvent.VK_ESCAPE)) {
            stateManager.pop(); // Remove pause state
            return;
        }
        
        // Select option
        if (Input.isKeyPressed(KeyEvent.VK_ENTER)) {
            handleSelection();
        }
    }
    
    private void handleSelection() {
        switch (selectedIndex) {
            case 0: // Resume
                stateManager.pop(); // Remove pause state
                break;
            case 1: // Options
                stateManager.push(new OptionsState(stateManager));
                break;
            case 2: // Main Menu
                // Pop both pause and gameplay states
                stateManager.pop(); // Remove pause
                stateManager.pop(); // Remove gameplay
                break;
        }
    }
    
    @Override
    public void render(Manager manager, Context ctx) {
        // Render game state below (if configured to do so)
        // Then draw pause overlay
        
        // Semi-transparent overlay
        int overlay = Color.toPixelInt(0, 0, 0, 180);
        ctx.renderFilledRectangle(0, 0, 320, 240, overlay);
        
        // Pause text
        int white = Color.toPixelInt(255, 255, 255, 255);
        int yellow = Color.toPixelInt(255, 255, 100, 255);
        
        ctx.renderText("PAUSED", 135, 60, yellow);
        
        // Menu options
        int startY = 100;
        int spacing = 20;
        
        for (int i = 0; i < menuOptions.length; i++) {
            int y = startY + (i * spacing);
            int color = (i == selectedIndex) ? yellow : white;
            
            if (i == selectedIndex) {
                ctx.renderText(">", 100, y, yellow);
            }
            
            ctx.renderText(menuOptions[i], 120, y, color);
        }
        
        // Instructions
        int gray = Color.toPixelInt(150, 150, 150, 255);
        ctx.renderText("ESC to resume", 110, 200, gray);
    }
}
```

## Step 4: Create Additional States

Create `src/main/java/states/OptionsState.java`:

```java
package states;

import TransmuteCore.GameEngine.Manager;
import TransmuteCore.Graphics.Context;
import TransmuteCore.Graphics.Color;
import TransmuteCore.States.State;
import TransmuteCore.States.StateManager;
import TransmuteCore.Input.Input;
import java.awt.event.KeyEvent;

public class OptionsState extends State {
    
    private int soundVolume = 80;
    private int musicVolume = 60;
    private int selectedOption = 0;
    
    public OptionsState(StateManager stateManager) {
        super("options", stateManager);
        init();
    }
    
    @Override
    public void init() {
        // Load saved options
    }
    
    @Override
    public void update(Manager manager, double delta) {
        // Navigate
        if (Input.isKeyPressed(KeyEvent.VK_UP)) {
            selectedOption = Math.max(0, selectedOption - 1);
        }
        if (Input.isKeyPressed(KeyEvent.VK_DOWN)) {
            selectedOption = Math.min(2, selectedOption + 1);
        }
        
        // Adjust values
        if (selectedOption == 0) { // Sound volume
            if (Input.isKeyHeld(KeyEvent.VK_LEFT)) {
                soundVolume = Math.max(0, soundVolume - 1);
            }
            if (Input.isKeyHeld(KeyEvent.VK_RIGHT)) {
                soundVolume = Math.min(100, soundVolume + 1);
            }
        } else if (selectedOption == 1) { // Music volume
            if (Input.isKeyHeld(KeyEvent.VK_LEFT)) {
                musicVolume = Math.max(0, musicVolume - 1);
            }
            if (Input.isKeyHeld(KeyEvent.VK_RIGHT)) {
                musicVolume = Math.min(100, musicVolume + 1);
            }
        }
        
        // Back
        if (Input.isKeyPressed(KeyEvent.VK_ESCAPE) || 
            (selectedOption == 2 && Input.isKeyPressed(KeyEvent.VK_ENTER))) {
            stateManager.pop();
        }
    }
    
    @Override
    public void render(Manager manager, Context ctx) {
        ctx.setClearColor(Color.toPixelInt(20, 30, 40, 255));
        
        int white = Color.toPixelInt(255, 255, 255, 255);
        int yellow = Color.toPixelInt(255, 255, 100, 255);
        
        ctx.renderText("OPTIONS", 120, 40, yellow);
        
        // Sound volume
        int color0 = selectedOption == 0 ? yellow : white;
        ctx.renderText("Sound Volume:", 60, 80, color0);
        renderSlider(ctx, 60, 95, soundVolume, selectedOption == 0);
        
        // Music volume
        int color1 = selectedOption == 1 ? yellow : white;
        ctx.renderText("Music Volume:", 60, 120, color1);
        renderSlider(ctx, 60, 135, musicVolume, selectedOption == 1);
        
        // Back button
        int color2 = selectedOption == 2 ? yellow : white;
        if (selectedOption == 2) {
            ctx.renderText(">", 100, 170, yellow);
        }
        ctx.renderText("Back", 120, 170, color2);
    }
    
    private void renderSlider(Context ctx, int x, int y, int value, boolean selected) {
        int gray = Color.toPixelInt(100, 100, 100, 255);
        int fill = selected ? 
                   Color.toPixelInt(255, 255, 100, 255) :
                   Color.toPixelInt(150, 150, 150, 255);
        
        // Background bar
        ctx.renderFilledRectangle(x, y, 200, 8, gray);
        
        // Filled portion
        int fillWidth = (int)(value * 2.0f);
        ctx.renderFilledRectangle(x, y, fillWidth, 8, fill);
        
        // Value text
        int white = Color.toPixelInt(255, 255, 255, 255);
        ctx.renderText(value + "%", x + 210, y, white);
    }
}
```

Create `src/main/java/states/GameOverState.java`:

```java
package states;

import TransmuteCore.GameEngine.Manager;
import TransmuteCore.Graphics.Context;
import TransmuteCore.Graphics.Color;
import TransmuteCore.States.State;
import TransmuteCore.States.StateManager;
import TransmuteCore.Input.Input;
import java.awt.event.KeyEvent;

public class GameOverState extends State {
    
    private int finalScore;
    private String[] options = {"Retry", "Main Menu"};
    private int selectedIndex = 0;
    
    public GameOverState(StateManager stateManager, int score) {
        super("gameover", stateManager);
        this.finalScore = score;
        init();
    }
    
    @Override
    public void init() {
        // Save high score, etc.
    }
    
    @Override
    public void update(Manager manager, double delta) {
        if (Input.isKeyPressed(KeyEvent.VK_UP)) {
            selectedIndex = 0;
        }
        if (Input.isKeyPressed(KeyEvent.VK_DOWN)) {
            selectedIndex = 1;
        }
        
        if (Input.isKeyPressed(KeyEvent.VK_ENTER)) {
            handleSelection();
        }
    }
    
    private void handleSelection() {
        switch (selectedIndex) {
            case 0: // Retry
                stateManager.pop(); // Remove game over
                stateManager.pop(); // Remove old gameplay
                stateManager.push(new GamePlayState(stateManager)); // New game
                break;
            case 1: // Main Menu
                stateManager.pop(); // Remove game over
                stateManager.pop(); // Remove gameplay
                break;
        }
    }
    
    @Override
    public void render(Manager manager, Context ctx) {
        ctx.setClearColor(Color.toPixelInt(20, 10, 10, 255));
        
        int white = Color.toPixelInt(255, 255, 255, 255);
        int red = Color.toPixelInt(255, 100, 100, 255);
        int yellow = Color.toPixelInt(255, 255, 100, 255);
        
        ctx.renderText("GAME OVER", 110, 60, red);
        ctx.renderText("Final Score: " + finalScore, 100, 90, white);
        
        // Options
        for (int i = 0; i < options.length; i++) {
            int y = 130 + (i * 20);
            int color = (i == selectedIndex) ? yellow : white;
            
            if (i == selectedIndex) {
                ctx.renderText(">", 100, y, yellow);
            }
            
            ctx.renderText(options[i], 120, y, color);
        }
    }
}
```

## Step 5: Update the Main Game Class

Create `src/main/java/StateDemo.java`:

```java
import TransmuteCore.GameEngine.TransmuteCore;
import TransmuteCore.GameEngine.Manager;
import TransmuteCore.Graphics.Context;
import TransmuteCore.States.StateManager;
import TransmuteCore.System.Asset.AssetManager;
import TransmuteCore.System.Asset.Type.Fonts.Font;
import TransmuteCore.System.Asset.Type.Images.Image;
import states.*;

public class StateDemo extends TransmuteCore {
    
    private StateManager stateManager;
    
    public StateDemo() {
        super("State Management Demo", "1.0", 320, TransmuteCore.Square, 3);
    }
    
    @Override
    public void init() {
        // Initialize font
        Font.initializeDefaultFont("fonts/font.png");
        
        // Register assets
        new Image("player", "images/player.png");
        
        // Load assets
        AssetManager.load();
        
        // Create state manager
        stateManager = new StateManager(this);
        getManager().setStateManager(stateManager);
        
        // Start with main menu
        stateManager.push(new MainMenuState(stateManager));
    }
    
    @Override
    public void update(Manager manager, double delta) {
        if (stateManager != null) {
            stateManager.update(manager, delta);
        }
    }
    
    @Override
    public void render(Manager manager, Context ctx) {
        if (stateManager != null) {
            stateManager.render(manager, ctx);
        }
    }
    
    public static void main(String[] args) {
        new StateDemo();
    }
}
```

## Advanced State Techniques

### 1. Passing Data Between States

Use constructors to pass data:

```java
public class LevelSelectState extends State {
    public LevelSelectState(StateManager sm, PlayerData playerData) {
        super("levelSelect", sm);
        this.playerData = playerData;
        init();
    }
}

// Usage:
stateManager.push(new LevelSelectState(stateManager, currentPlayerData));
```

### 2. Rendering States Below

To render the game state behind a pause menu:

```java
public class PauseState extends State {
    @Override
    public void render(Manager manager, Context ctx) {
        // Render the state below this one
        State gameState = stateManager.getStateBelow(this);
        if (gameState != null) {
            gameState.render(manager, ctx);
        }
        
        // Then render pause overlay
        // ... overlay rendering ...
    }
}
```

### 3. State Transitions

Add fade effects:

```java
public abstract class TransitionState extends State {
    protected float fadeAlpha = 0;
    protected boolean fadingIn = true;
    protected State targetState;
    
    @Override
    public void update(Manager manager, double delta) {
        if (fadingIn) {
            fadeAlpha += 0.05f;
            if (fadeAlpha >= 1.0f) {
                fadingIn = false;
                stateManager.replace(targetState);
            }
        }
    }
    
    @Override
    public void render(Manager manager, Context ctx) {
        int alpha = (int)(fadeAlpha * 255);
        int black = Color.toPixelInt(0, 0, 0, alpha);
        ctx.renderFilledRectangle(0, 0, 320, 240, black);
    }
}
```

### 4. State Data Persistence

Save state data:

```java
public class GamePlayState extends State {
    public GameData save() {
        GameData data = new GameData();
        data.score = this.score;
        data.playerX = this.player.getX();
        data.playerY = this.player.getY();
        data.gameTime = this.gameTime;
        return data;
    }
    
    public void load(GameData data) {
        this.score = data.score;
        this.player.setPosition(data.playerX, data.playerY);
        this.gameTime = data.gameTime;
    }
}

// In StateManager or game class:
public void saveState() {
    GamePlayState state = (GamePlayState) stateManager.getCurrentState();
    GameData data = state.save();
    // Serialize data to file
}
```

### 5. Modal Dialogs

Create overlay states:

```java
public class ConfirmDialog extends State {
    private String message;
    private Runnable onConfirm;
    private Runnable onCancel;
    
    public ConfirmDialog(StateManager sm, String message, 
                        Runnable onConfirm, Runnable onCancel) {
        super("confirmDialog", sm);
        this.message = message;
        this.onConfirm = onConfirm;
        this.onCancel = onCancel;
        init();
    }
    
    @Override
    public void update(Manager manager, double delta) {
        if (Input.isKeyPressed(KeyEvent.VK_Y)) {
            stateManager.pop();
            if (onConfirm != null) onConfirm.run();
        }
        if (Input.isKeyPressed(KeyEvent.VK_N)) {
            stateManager.pop();
            if (onCancel != null) onCancel.run();
        }
    }
}

// Usage:
stateManager.push(new ConfirmDialog(
    stateManager,
    "Quit to menu?",
    () -> { /* quit logic */ },
    () -> { /* cancel logic */ }
));
```

## Common State Management Patterns

### Game Flow Example

```
MainMenu → (Start) → GamePlay → (Win) → Victory → (Continue) → GamePlay
                  ↓              ↓                    ↓
                  Options      GameOver            MainMenu
                              → (Retry) → GamePlay
```

### State Stack Example

```
[MainMenu]
[MainMenu, GamePlay]
[MainMenu, GamePlay, Pause]
[MainMenu, GamePlay, Pause, Options]
[MainMenu, GamePlay, Pause]  // Options popped
[MainMenu, GamePlay]          // Pause popped
```

## Exercises

### 1. Add a Loading State

Show a loading bar while assets load:

```java
public class LoadingState extends State {
    private float loadProgress = 0;
    
    @Override
    public void update(Manager manager, double delta) {
        loadProgress = AssetManager.getLoadProgress();
        
        if (loadProgress >= 1.0f) {
            stateManager.replace(new MainMenuState(stateManager));
        }
    }
}
```

### 2. Create a Settings Save System

Save settings to disk:

```java
public class OptionsState extends State {
    @Override
    public void onExit() {
        saveSettings();
    }
    
    private void saveSettings() {
        // Use TinyDatabase to save
    }
}
```

### 3. Add Animation to State Transitions

Create smooth transitions between states.

## Common Issues

### States not updating

- Ensure you're calling `stateManager.update()` in your main game loop
- Check that states are properly pushed to the stack

### Memory leaks from states

- Clean up resources in `onExit()` method
- Don't hold references to large objects unnecessarily

### Input bleeding through states

- Clear input state when pushing/popping states
- Use `Input.isKeyPressed()` instead of `isKeyHeld()` for menu navigation

## What's Next?

In the next tutorial, you'll learn:
- Loading and playing audio
- Sound effects and music
- Audio management
- Creating an audio mixer

Continue to [Tutorial 6: Audio System](06-audio-system.md)

## Resources

- [StateManager.java](../../TransmuteCore/src/TransmuteCore/States/StateManager.java)
- [COOKBOOK.md](../COOKBOOK.md) - Menu system patterns
