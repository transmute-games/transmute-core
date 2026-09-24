package TransmuteCore.graphics.sprites;

import java.awt.image.BufferedImage;

import TransmuteCore.math.Tuple2i;

/**
 * Builder pattern for creating Spritesheet instances with a fluent API.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 * Spritesheet sheet = SpritesheetBuilder.create(image)
 *     .cellSize(16)
 *     .startOffset(0, 0)
 *     .gaps(1, 1)
 *     .scale(2.0f)
 *     .initializeBounds(true)
 *     .build();
 * }
 * </pre>
 */
public class SpritesheetBuilder
{
    private final BufferedImage image;
    private Tuple2i cellSize;
    private Tuple2i startOffset = new Tuple2i(0, 0);
    private int verticalGap = 0;
    private int horizontalGap = 0;
    private float scale = 1.0f;
    private boolean initializeBounds = false;

    private SpritesheetBuilder(BufferedImage image)
    {
        if (image == null) {
            throw new IllegalArgumentException("Spritesheet image cannot be null");
        }
        this.image = image;
    }

    /**
     * Creates a new SpritesheetBuilder instance.
     *
     * @param image The BufferedImage to use as the spritesheet.
     * @return A new SpritesheetBuilder instance.
     */
    public static SpritesheetBuilder create(BufferedImage image)
    {
        return new SpritesheetBuilder(image);
    }

    /**
     * Sets the size of each sprite cell (square cells).
     *
     * @param size Width and height of each cell in pixels.
     * @return This builder instance for chaining.
     */
    public SpritesheetBuilder cellSize(int size)
    {
        if (size <= 0) {
            throw new IllegalArgumentException(
                String.format("Cell size must be positive. Got: %d", size)
            );
        }
        this.cellSize = new Tuple2i(size, size);
        return this;
    }

    /**
     * Sets the size of each sprite cell (rectangular cells).
     *
     * @param width  Width of each cell in pixels.
     * @param height Height of each cell in pixels.
     * @return This builder instance for chaining.
     */
    public SpritesheetBuilder cellSize(int width, int height)
    {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException(
                String.format("Cell dimensions must be positive. Got: (%d, %d)", width, height)
            );
        }
        this.cellSize = new Tuple2i(width, height);
        return this;
    }

    /**
     * Sets the starting offset for sprite calculation.
     *
     * @param x X offset in pixels.
     * @param y Y offset in pixels.
     * @return This builder instance for chaining.
     */
    public SpritesheetBuilder startOffset(int x, int y)
    {
        if (x < 0 || y < 0) {
            throw new IllegalArgumentException(
                String.format("Start offset cannot be negative. Got: (%d, %d)", x, y)
            );
        }
        this.startOffset = new Tuple2i(x, y);
        return this;
    }

    /**
     * Sets the gap between sprite cells (same for both vertical and horizontal).
     *
     * @param gap Gap size in pixels.
     * @return This builder instance for chaining.
     */
    public SpritesheetBuilder gap(int gap)
    {
        if (gap < 0) {
            throw new IllegalArgumentException(
                String.format("Gap cannot be negative. Got: %d", gap)
            );
        }
        this.verticalGap = gap;
        this.horizontalGap = gap;
        return this;
    }

    /**
     * Sets different gap sizes for vertical and horizontal spacing.
     *
     * @param vertical   Vertical gap in pixels.
     * @param horizontal Horizontal gap in pixels.
     * @return This builder instance for chaining.
     */
    public SpritesheetBuilder gaps(int vertical, int horizontal)
    {
        if (vertical < 0 || horizontal < 0) {
            throw new IllegalArgumentException(
                String.format("Gaps cannot be negative. Got: (%d, %d)", vertical, horizontal)
            );
        }
        this.verticalGap = vertical;
        this.horizontalGap = horizontal;
        return this;
    }

    /**
     * Sets whether to initialize pixel-perfect bounding boxes for each sprite.
     *
     * @param initialize Whether to create pixel-perfect bounds.
     * @return This builder instance for chaining.
     */
    public SpritesheetBuilder initializeBounds(boolean initialize)
    {
        this.initializeBounds = initialize;
        return this;
    }

    /**
     * Sets the scale factor for the spritesheet.
     * Note: This will be applied when building the spritesheet.
     *
     * @param scale Scale factor (1.0f is original size).
     * @return This builder instance for chaining.
     */
    public SpritesheetBuilder scale(float scale)
    {
        if (scale <= 0) {
            throw new IllegalArgumentException(
                String.format("Scale must be positive. Got: %.2f", scale)
            );
        }
        this.scale = scale;
        return this;
    }

    /**
     * Builds and returns a Spritesheet with the configured parameters.
     *
     * @return A configured Spritesheet instance.
     * @throws IllegalStateException if required fields are not set.
     */
    public Spritesheet build()
    {
        if (cellSize == null) {
            throw new IllegalStateException("Cell size must be set before building");
        }

        Spritesheet sheet = new Spritesheet(
            image,
            cellSize,
            startOffset,
            verticalGap,
            horizontalGap,
            initializeBounds
        );

        // Apply scaling if specified
        if (scale != 1.0f) {
            sheet = sheet.getScaled(scale);
        }

        return sheet;
    }
}
