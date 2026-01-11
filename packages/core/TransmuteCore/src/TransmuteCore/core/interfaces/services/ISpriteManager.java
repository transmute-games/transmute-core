package TransmuteCore.core.interfaces.services;

import TransmuteCore.graphics.sprites.Animation;
import TransmuteCore.graphics.sprites.Sprite;
import TransmuteCore.graphics.sprites.Spritesheet;
import TransmuteCore.math.Tuple2i;

/**
 * Interface for managing sprites.
 * Provides methods for storing and retrieving sprites from a spritesheet.
 */
public interface ISpriteManager
{
    /**
     * Gets an array of sprites by key.
     *
     * @param key The sprite key.
     * @return The sprite array.
     */
    Sprite[] get(String key);

    /**
     * Gets a single sprite by key and index.
     *
     * @param key   The sprite key.
     * @param index The sprite index in the array.
     * @return The sprite.
     */
    Sprite get(String key, int index);

    /**
     * Adds sprites at the specified coordinates.
     *
     * @param key The sprite key.
     * @param x   The x-coordinate in the spritesheet.
     * @param y   The y-coordinate in the spritesheet.
     */
    void add(String key, int x, int y);

    /**
     * Adds sprites with custom cell dimensions.
     *
     * @param key        The sprite key.
     * @param x          The x-coordinate in the spritesheet.
     * @param y          The y-coordinate in the spritesheet.
     * @param cellWidth  The cell width.
     * @param cellHeight The cell height.
     */
    void add(String key, int x, int y, int cellWidth, int cellHeight);

    /**
     * Adds sprites at multiple locations.
     *
     * @param key      The sprite key.
     * @param locArray Array of sprite locations.
     */
    void add(String key, Tuple2i... locArray);

    /**
     * Creates an animation from sprites.
     *
     * @param name        The animation name.
     * @param duration    The frame duration in milliseconds.
     * @param spriteArray The sprites to animate.
     * @return The animation.
     */
    Animation createAnimation(String name, int duration, Sprite... spriteArray);

    /**
     * Gets the spritesheet.
     *
     * @return The spritesheet.
     */
    Spritesheet getSpritesheet();
}
