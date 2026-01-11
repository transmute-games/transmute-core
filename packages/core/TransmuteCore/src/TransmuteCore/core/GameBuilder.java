package TransmuteCore.core;

import TransmuteCore.core.Interfaces.Cortex;
import TransmuteCore.core.Interfaces.Services.IRenderer;

/**
 * Builder pattern for creating TransmuteCore game instances with a fluent API.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 * TransmuteCore game = GameBuilder.create()
 *     .title("My Game")
 *     .version("1.0")
 *     .dimensions(320, TransmuteCore.Square)
 *     .scale(3)
 *     .targetFPS(60)
 *     .fpsVerbose(true)
 *     .build(new MyCortexImplementation());
 * }
 * </pre>
 */
public class GameBuilder
{
    private String gameTitle;
    private String gameVersion;
    private int gameWidth;
    private int gameRatio = TransmuteCore.Square;
    private int gameScale = 1;
    private int targetFPS = 60;
    private int numBuffers = 3;
    private boolean fpsVerbose = false;

    private GameBuilder()
    {
    }

    /**
     * Creates a new GameBuilder instance.
     *
     * @return A new GameBuilder instance.
     */
    public static GameBuilder create()
    {
        return new GameBuilder();
    }

    /**
     * Sets the game title.
     *
     * @param title The title of the game.
     * @return This builder instance for chaining.
     */
    public GameBuilder title(String title)
    {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Game title cannot be null or empty");
        }
        this.gameTitle = title;
        return this;
    }

    /**
     * Sets the game version.
     *
     * @param version The version of the game.
     * @return This builder instance for chaining.
     */
    public GameBuilder version(String version)
    {
        if (version == null || version.trim().isEmpty()) {
            throw new IllegalArgumentException("Game version cannot be null or empty");
        }
        this.gameVersion = version;
        return this;
    }

    /**
     * Sets the game window dimensions.
     *
     * @param width The width of the game window.
     * @param ratio The aspect ratio (TransmuteCore.WideScreen or TransmuteCore.Square).
     * @return This builder instance for chaining.
     */
    public GameBuilder dimensions(int width, int ratio)
    {
        if (width <= 0) {
            throw new IllegalArgumentException(
                String.format("Game width must be positive. Got: %d", width)
            );
        }
        this.gameWidth = width;
        this.gameRatio = ratio;
        return this;
    }

    /**
     * Sets the game window scale factor.
     *
     * @param scale The scale factor for the game window.
     * @return This builder instance for chaining.
     */
    public GameBuilder scale(int scale)
    {
        if (scale <= 0) {
            throw new IllegalArgumentException(
                String.format("Game scale must be positive. Got: %d", scale)
            );
        }
        this.gameScale = scale;
        return this;
    }

    /**
     * Sets the target FPS for the game.
     *
     * @param fps The target frames per second.
     * @return This builder instance for chaining.
     */
    public GameBuilder targetFPS(int fps)
    {
        if (fps <= 0) {
            throw new IllegalArgumentException(
                String.format("Target FPS must be positive. Got: %d", fps)
            );
        }
        if (fps > 1000) {
            throw new IllegalArgumentException(
                String.format("Target FPS seems unreasonably high: %d. Maximum allowed is 1000.", fps)
            );
        }
        this.targetFPS = fps;
        return this;
    }

    /**
     * Sets the number of buffers for the BufferStrategy.
     *
     * @param buffers Number of buffers (higher prevents flicker but may slow performance).
     * @return This builder instance for chaining.
     */
    public GameBuilder buffers(int buffers)
    {
        if (buffers < 2 || buffers > 4) {
            throw new IllegalArgumentException(
                String.format("Number of buffers should be between 2 and 4. Got: %d", buffers)
            );
        }
        this.numBuffers = buffers;
        return this;
    }

    /**
     * Enables or disables FPS logging to console.
     *
     * @param verbose Whether to print FPS information to console.
     * @return This builder instance for chaining.
     */
    public GameBuilder fpsVerbose(boolean verbose)
    {
        this.fpsVerbose = verbose;
        return this;
    }

    /**
     * Builds and returns a TransmuteCore game instance with a custom Cortex implementation.
     * The game loop will automatically start after initialization.
     * <p>
     * This is an advanced method that allows you to provide custom game logic without
     * extending TransmuteCore directly.
     *
     * @param cortex The Cortex implementation containing game logic.
     * @return A configured and started TransmuteCore instance.
     * @throws IllegalStateException if required fields are not set.
     */
    public TransmuteCore build(final Cortex cortex)
    {
        validateRequiredFields();
        
        // Create GameConfig from builder parameters
        GameConfig config = new GameConfig.Builder()
            .title(gameTitle)
            .version(gameVersion)
            .dimensions(gameWidth, gameRatio)
            .scale(gameScale)
            .targetFPS(targetFPS)
            .bufferCount(numBuffers)
            .fpsVerbose(fpsVerbose)
            .build();

        // Create game instance with new constructor
        TransmuteCore game = new TransmuteCore(config)
        {
            @Override
            public void init()
            {
                cortex.init();
            }

            @Override
            public void update(Manager manager, double delta)
            {
                cortex.update(manager, delta);
            }

            @Override
            public void render(Manager manager, IRenderer renderer)
            {
                cortex.render(manager, renderer);
            }
        };
        
        // Automatically start the game loop
        game.start();
        return game;
    }

    /**
     * Validates that all required fields are set before building.
     *
     * @throws IllegalStateException if any required field is missing.
     */
    private void validateRequiredFields()
    {
        if (gameTitle == null) {
            throw new IllegalStateException("Game title must be set before building");
        }
        if (gameVersion == null) {
            throw new IllegalStateException("Game version must be set before building");
        }
        if (gameWidth <= 0) {
            throw new IllegalStateException("Game width must be set before building");
        }
    }
}
