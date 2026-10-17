package TransmuteCore.util.verify;

import TransmuteCore.core.TransmuteCore;
import TransmuteCore.core.interfaces.services.IRenderer;
import TransmuteCore.graphics.Context;
import TransmuteCore.util.Screenshot;

import java.io.File;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Synchronous headless driver for agent and CI verification.
 * <p>
 * Runs {@code init} once, then steps update/render on the calling thread —
 * no window, no game-loop thread.
 * </p>
 * <pre>{@code
 * try (GameHarness harness = GameHarness.of(() -> new MyGame(headlessConfig))) {
 *     harness.step(10);
 *     FrameAssert.assertPixel(harness.renderer(), 5, 5, expectedColor);
 * }
 * }</pre>
 */
public final class GameHarness implements AutoCloseable
{
    private final TransmuteCore game;
    private boolean initialized;
    private boolean closed;

    private GameHarness(TransmuteCore game)
    {
        this.game = Objects.requireNonNull(game, "game");
        if (!game.getConfig().isHeadless())
        {
            throw new IllegalArgumentException(
                "GameHarness requires GameConfig.headless(true)");
        }
    }

    /**
     * Creates a harness around a game constructed by {@code factory}.
     * The factory must build with {@code headless(true)}.
     */
    public static GameHarness of(Supplier<? extends TransmuteCore> factory)
    {
        Objects.requireNonNull(factory, "factory");
        return new GameHarness(factory.get());
    }

    /**
     * Ensures {@link TransmuteCore#init()} has run, then steps {@code frames}
     * update/render cycles with {@code delta = 1.0}.
     *
     * @return this harness for chaining
     */
    public GameHarness step(int frames)
    {
        ensureOpen();
        if (frames < 0)
        {
            throw new IllegalArgumentException("frames must be >= 0, got " + frames);
        }
        ensureInit();
        for (int i = 0; i < frames; i++)
        {
            game.stepFrame(1.0);
        }
        return this;
    }

    /**
     * @return the pixel renderer after the last step
     */
    public IRenderer renderer()
    {
        ensureOpen();
        ensureInit();
        return game.getPixelContext();
    }

    /**
     * @return the underlying game instance
     */
    public TransmuteCore game()
    {
        ensureOpen();
        return game;
    }

    /**
     * Writes the current frame to {@code file} as PNG.
     *
     * @return true if the file was written
     */
    public boolean capture(File file)
    {
        ensureOpen();
        ensureInit();
        Objects.requireNonNull(file, "file");
        File parent = file.getParentFile();
        if (parent != null)
        {
            Screenshot.setOutputDirectory(parent.getAbsolutePath());
        }
        String name = file.getName();
        if (name.toLowerCase().endsWith(".png"))
        {
            name = name.substring(0, name.length() - 4);
        }
        Context ctx = game.getPixelContext();
        File previousDir = new File(Screenshot.getOutputDirectory());
        try
        {
            if (parent != null)
            {
                Screenshot.setOutputDirectory(parent.getAbsolutePath());
            }
            return Screenshot.captureAs(ctx, name);
        }
        finally
        {
            Screenshot.setOutputDirectory(previousDir.getPath());
        }
    }

    @Override
    public void close()
    {
        if (closed)
        {
            return;
        }
        closed = true;
        if (game.isRunning())
        {
            game.shutdown();
        }
    }

    private void ensureInit()
    {
        if (!initialized)
        {
            game.initForHarness();
            initialized = true;
        }
    }

    private void ensureOpen()
    {
        if (closed)
        {
            throw new IllegalStateException("GameHarness is closed");
        }
    }
}
