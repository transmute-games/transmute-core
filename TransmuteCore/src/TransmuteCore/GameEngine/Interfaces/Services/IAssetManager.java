package TransmuteCore.GameEngine.Interfaces.Services;

import TransmuteCore.Graphics.Bitmap;
import TransmuteCore.Graphics.Sprites.Spritesheet;
import TransmuteCore.System.Asset.Asset;
import TransmuteCore.System.Asset.Type.Fonts.Font;

import javax.sound.sampled.Clip;
import java.util.Queue;

/**
 * Interface for asset management operations.
 * Provides methods for registering, loading, and retrieving game assets.
 */
public interface IAssetManager
{
    /**
     * Registers an asset for loading.
     *
     * @param asset The asset to register.
     * @return Success indicator flag.
     */
    boolean register(Asset asset);

    /**
     * Loads all queued assets into memory.
     */
    void load();

    /**
     * Checks if an asset with the given type and name exists.
     *
     * @param type The asset type.
     * @param name The asset name.
     * @return True if the asset exists.
     */
    boolean containsKey(String type, String name);

    /**
     * Checks if all assets have been loaded.
     *
     * @return True if all assets are loaded.
     */
    boolean isLoaded();

    /**
     * Removes an asset from the registrar.
     *
     * @param item The asset to remove.
     */
    void remove(Object item);

    /**
     * Removes an asset with the given type and name.
     *
     * @param type The asset type.
     * @param name The asset name.
     */
    void remove(String type, String name);

    /**
     * Gets the asset load queue.
     *
     * @return The queue of assets to be loaded.
     */
    Queue<Asset> getLoadQueue();

    /**
     * Retrieves an audio asset.
     *
     * @param name The asset name.
     * @return The audio clip.
     */
    Clip getAudio(String name);

    /**
     * Retrieves an image asset.
     *
     * @param name The asset name.
     * @return The bitmap image.
     */
    Bitmap getImage(String name);

    /**
     * Retrieves a font asset.
     *
     * @param name The asset name.
     * @return The font spritesheet.
     */
    Spritesheet getFont(String name);

    /**
     * Gets the default font.
     *
     * @return The default font.
     */
    Font getDefaultFont();

    /**
     * Cleans up all loaded assets.
     */
    void cleanUp();
}
