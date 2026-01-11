package TransmuteCore.core;

import TransmuteCore.core.interfaces.Services.*;

/**
 * Immutable container for all game services and configuration.
 * <p>
 * This replaces the Manager service locator with proper dependency injection.
 * GameContext holds references to all subsystems via their interfaces, making
 * dependencies explicit and testable.
 * <p>
 * Usage:
 * <pre>{@code
 * GameContext context = new GameContext.Builder()
 *     .config(config)
 *     .assetManager(assetManager)
 *     .inputHandler(input)
 *     .renderer(ctx)
 *     .gameWindow(window)
 *     .build();
 * }</pre>
 */
public final class GameContext
{
    private final GameConfig config;
    private final IAssetManager assetManager;
    private final IInputHandler inputHandler;
    private final IRenderer renderer;
    private final IStateManager stateManager;
    private final IObjectManager objectManager;
    private final ISpriteManager spriteManager;
    private final IGameWindow gameWindow;
    
    private GameContext(Builder builder)
    {
        this.config = builder.config;
        this.assetManager = builder.assetManager;
        this.inputHandler = builder.inputHandler;
        this.renderer = builder.renderer;
        this.stateManager = builder.stateManager;
        this.objectManager = builder.objectManager;
        this.spriteManager = builder.spriteManager;
        this.gameWindow = builder.gameWindow;
    }
    
    /**
     * @return The game configuration.
     */
    public GameConfig getConfig()
    {
        return config;
    }
    
    /**
     * @return The asset manager, or null if not set.
     */
    public IAssetManager getAssetManager()
    {
        return assetManager;
    }
    
    /**
     * @return The input handler, or null if not set.
     */
    public IInputHandler getInputHandler()
    {
        return inputHandler;
    }
    
    /**
     * @return The renderer, or null if not set.
     */
    public IRenderer getRenderer()
    {
        return renderer;
    }
    
    /**
     * @return The state manager, or null if not set.
     */
    public IStateManager getStateManager()
    {
        return stateManager;
    }
    
    /**
     * @return The object manager, or null if not set.
     */
    public IObjectManager getObjectManager()
    {
        return objectManager;
    }
    
    /**
     * @return The sprite manager, or null if not set.
     */
    public ISpriteManager getSpriteManager()
    {
        return spriteManager;
    }
    
    /**
     * @return The game window, or null if not set.
     */
    public IGameWindow getGameWindow()
    {
        return gameWindow;
    }
    
    @Override
    public String toString()
    {
        return "GameContext{" +
                "config=" + config +
                ", assetManager=" + (assetManager != null ? "present" : "null") +
                ", inputHandler=" + (inputHandler != null ? "present" : "null") +
                ", renderer=" + (renderer != null ? "present" : "null") +
                ", stateManager=" + (stateManager != null ? "present" : "null") +
                ", objectManager=" + (objectManager != null ? "present" : "null") +
                ", spriteManager=" + (spriteManager != null ? "present" : "null") +
                ", gameWindow=" + (gameWindow != null ? "present" : "null") +
                '}';
    }
    
    /**
     * Builder for GameContext.
     */
    public static class Builder
    {
        private GameConfig config;
        private IAssetManager assetManager;
        private IInputHandler inputHandler;
        private IRenderer renderer;
        private IStateManager stateManager;
        private IObjectManager objectManager;
        private ISpriteManager spriteManager;
        private IGameWindow gameWindow;
        
        public Builder config(GameConfig config)
        {
            if (config == null)
            {
                throw new IllegalArgumentException("Config cannot be null");
            }
            this.config = config;
            return this;
        }
        
        public Builder assetManager(IAssetManager assetManager)
        {
            this.assetManager = assetManager;
            return this;
        }
        
        public Builder inputHandler(IInputHandler inputHandler)
        {
            this.inputHandler = inputHandler;
            return this;
        }
        
        public Builder renderer(IRenderer renderer)
        {
            this.renderer = renderer;
            return this;
        }
        
        public Builder stateManager(IStateManager stateManager)
        {
            this.stateManager = stateManager;
            return this;
        }
        
        public Builder objectManager(IObjectManager objectManager)
        {
            this.objectManager = objectManager;
            return this;
        }
        
        public Builder spriteManager(ISpriteManager spriteManager)
        {
            this.spriteManager = spriteManager;
            return this;
        }
        
        public Builder gameWindow(IGameWindow gameWindow)
        {
            this.gameWindow = gameWindow;
            return this;
        }
        
        public GameContext build()
        {
            if (config == null)
            {
                throw new IllegalStateException("Config must be set before building GameContext");
            }
            return new GameContext(this);
        }
    }
}
