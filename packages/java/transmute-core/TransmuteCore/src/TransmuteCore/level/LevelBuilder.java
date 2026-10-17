package TransmuteCore.level;

import TransmuteCore.ecs.Object;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builder pattern for creating TiledLevel instances with a fluent API.
 * <p>
 * Example usage:
 * <pre>
 * {@code
 * TiledLevel level = LevelBuilder.createTiled()
 *     .dimensions(100, 100)
 *     .tileSize(16)
 *     .tile(0xFF000000, grassTile)
 *     .tile(0xFF00FF00, wallTile)
 *     .offset(0, 0)
 *     .addObject(player)
 *     .build();
 * }
 * </pre>
 */
public class LevelBuilder
{
    private Integer width;
    private Integer height;
    private String filePath;
    private Integer tileSize;
    private int xOffset = 0;
    private int yOffset = 0;
    private Map<Integer, Tile> tiles = new HashMap<>();
    private List<Object> objects = new ArrayList<>();

    private LevelBuilder()
    {
    }

    /**
     * Creates a new LevelBuilder for a TiledLevel.
     *
     * @return A new LevelBuilder instance.
     */
    public static LevelBuilder createTiled()
    {
        return new LevelBuilder();
    }

    /**
     * Sets the level dimensions.
     *
     * @param width  Width of the level in tiles.
     * @param height Height of the level in tiles.
     * @return This builder instance for chaining.
     */
    public LevelBuilder dimensions(int width, int height)
    {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException(
                String.format("Level dimensions must be positive. Got: (%d, %d)", width, height)
            );
        }
        this.width = width;
        this.height = height;
        return this;
    }

    /**
     * Sets the level to load from a file.
     *
     * @param filePath Path to the level file (PNG, JPG, or JPEG).
     * @return This builder instance for chaining.
     */
    public LevelBuilder fromFile(String filePath)
    {
        if (filePath == null || filePath.trim().isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }
        this.filePath = filePath;
        return this;
    }

    /**
     * Sets the size of each tile in pixels.
     *
     * @param size Tile size in pixels.
     * @return This builder instance for chaining.
     */
    public LevelBuilder tileSize(int size)
    {
        if (size <= 0) {
            throw new IllegalArgumentException(
                String.format("Tile size must be positive. Got: %d", size)
            );
        }
        this.tileSize = size;
        return this;
    }

    /**
     * Maps a pixel color index to a tile type.
     *
     * @param colorIndex The color value from the level image.
     * @param tile       The tile to use for this color.
     * @return This builder instance for chaining.
     */
    public LevelBuilder tile(int colorIndex, Tile tile)
    {
        if (tile == null) {
            throw new IllegalArgumentException("Tile cannot be null");
        }
        this.tiles.put(colorIndex, tile);
        return this;
    }

    /**
     * Sets the viewport offset for rendering.
     *
     * @param x X offset in pixels.
     * @param y Y offset in pixels.
     * @return This builder instance for chaining.
     */
    public LevelBuilder offset(int x, int y)
    {
        this.xOffset = x;
        this.yOffset = y;
        return this;
    }

    /**
     * Adds an object to the level.
     *
     * @param obj The object to add.
     * @return This builder instance for chaining.
     */
    public LevelBuilder addObject(Object obj)
    {
        if (obj == null) {
            throw new IllegalArgumentException("Object cannot be null");
        }
        this.objects.add(obj);
        return this;
    }

    /**
     * Adds multiple objects to the level.
     *
     * @param objects The objects to add.
     * @return This builder instance for chaining.
     */
    public LevelBuilder addObjects(Object... objects)
    {
        if (objects == null) {
            throw new IllegalArgumentException("Objects array cannot be null");
        }
        for (Object obj : objects) {
            if (obj != null) {
                this.objects.add(obj);
            }
        }
        return this;
    }

    /**
     * Builds and returns a TiledLevel with the configured parameters.
     *
     * @return A configured TiledLevel instance.
     * @throws IllegalStateException if required fields are not set.
     */
    public TiledLevel build()
    {
        TiledLevel level;

        // Create level from file or dimensions
        if (filePath != null) {
            level = new TiledLevel(filePath);
        } else if (width != null && height != null) {
            level = new TiledLevel(width, height);
        } else {
            throw new IllegalStateException(
                "Either dimensions or file path must be set before building"
            );
        }

        // Set tile size if specified
        if (tileSize != null) {
            level.setTileSize(tileSize);
        }

        // Add tile mappings
        for (Map.Entry<Integer, Tile> entry : tiles.entrySet()) {
            level.addTile(entry.getKey(), entry.getValue());
        }

        // Set offset
        level.setOffset(xOffset, yOffset);

        // Add objects
        for (Object obj : objects) {
            level.add(obj);
        }

        return level;
    }
}
