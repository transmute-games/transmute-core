package GameStates;

import java.awt.event.KeyEvent;

import TransmuteCore.GameEngine.Manager;
import TransmuteCore.GameEngine.Interfaces.Services.IRenderer;
import TransmuteCore.Graphics.Color;
import TransmuteCore.Graphics.Context;
import TransmuteCore.Input.Input;
import TransmuteCore.States.State;
import TransmuteCore.States.StateManager;
import Utilities.Library;

/**
 * Main game state with a simple demo showcasing:
 * - Player movement with arrow keys
 * - Text rendering
 * - Simple rectangle rendering
 */
public class GameState extends State
{
    // Player position
    private int playerX;
    private int playerY;
    
    // Player size
    private static final int PLAYER_SIZE = 16;
    
    // Movement speed (pixels per frame)
    private static final int MOVE_SPEED = 2;
    
    // Screen dimensions (will be set from context)
    private int screenWidth;
    private int screenHeight;

    public GameState(StateManager sManager)
    {
        super("gameState", sManager);
        init();
    }

    @Override
    public void init()
    {
        // Get screen dimensions from StateManager's TransmuteCore instance
        // These are the unscaled dimensions from the Context
        screenWidth = 640 / 1;  // Default from config
        screenHeight = 480 / 1;
        
        // Initialize player in center of screen
        playerX = screenWidth / 2 - PLAYER_SIZE / 2;
        playerY = screenHeight / 2 - PLAYER_SIZE / 2;
    }

    @Override
    public void update(Manager manager, double delta)
    {
        Input input = manager.getInput();
        if (input == null) return;
        
        // Handle player movement with arrow keys
        if (input.isKeyHeld(KeyEvent.VK_LEFT)) {
            playerX -= MOVE_SPEED;
        }
        if (input.isKeyHeld(KeyEvent.VK_RIGHT)) {
            playerX += MOVE_SPEED;
        }
        if (input.isKeyHeld(KeyEvent.VK_UP)) {
            playerY -= MOVE_SPEED;
        }
        if (input.isKeyHeld(KeyEvent.VK_DOWN)) {
            playerY += MOVE_SPEED;
        }
        
        // Keep player on screen
        if (playerX < 0) playerX = 0;
        if (playerY < 0) playerY = 0;
        if (playerX + PLAYER_SIZE > screenWidth) playerX = screenWidth - PLAYER_SIZE;
        if (playerY + PLAYER_SIZE > screenHeight) playerY = screenHeight - PLAYER_SIZE;
    }

    @Override
    public void render(Manager manager, IRenderer renderer)
    {
        Context ctx = (Context) renderer;
        // Clear screen with dark gray background
        ctx.renderFilledRectangle(0, 0, screenWidth, screenHeight, Color.toPixelInt(32, 32, 32, 255));
        
        // Draw player as a colored square
        ctx.renderFilledRectangle(playerX, playerY, PLAYER_SIZE, PLAYER_SIZE, 
                    Color.toPixelInt(100, 200, 255, 255));
        
        // Draw player border
        ctx.renderRectangle(playerX, playerY, PLAYER_SIZE, PLAYER_SIZE, 
                    Color.toPixelInt(255, 255, 255, 255));
        
        // Draw title at top
        ctx.renderText(Library.GAME_TITLE, 10, 10, 
                      Color.toPixelInt(255, 255, 255, 255));
        
        // Draw instructions
        ctx.renderText("Arrow Keys to Move", 10, 20, 
                      Color.toPixelInt(200, 200, 200, 255));
        ctx.renderText("ESC to Exit", 10, 30, 
                      Color.toPixelInt(200, 200, 200, 255));
        
        // Draw player position
        String posText = "Position: (" + playerX + ", " + playerY + ")";
        ctx.renderText(posText, 10, screenHeight - 15, 
                      Color.toPixelInt(150, 150, 150, 255));
    }
}
