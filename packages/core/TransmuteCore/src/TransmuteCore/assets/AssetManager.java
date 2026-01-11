package TransmuteCore.assets;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import javax.sound.sampled.Clip;

import TransmuteCore.core.Interfaces.Services.IAssetManager;
import TransmuteCore.graphics.Bitmap;
import TransmuteCore.graphics.sprites.Spritesheet;
import TransmuteCore.util.Logger;
import TransmuteCore.util.exceptions.AssetLoadException;
import TransmuteCore.assets.types.Audio;
import TransmuteCore.assets.types.Font;
import TransmuteCore.assets.types.Image;

/**
 * <p>
 * The functional registrar of all assets used in the program.
 * Actual loading of assets is done in the loading menu.
 * </p>
 * <p>
 * Asset loading done in the same thread causes startup latency.
 * Larger assets (such as background music) will take significant
 * processing power. By utilizing deferred resource loading, the
 * resource reference can be declared prior to resource loading,
 * ensuring that the program launches responsively and in a more
 * user-friendly manner by showing a loading screen.
 * </p>
 * <p>
 * <strong>IMPORTANT: Asset keys are case-insensitive.</strong>
 * All asset names are automatically converted to lowercase when stored and retrieved.
 * For example, "PlayerSprite" and "playersprite" will refer to the same asset.
 * This ensures consistent asset retrieval regardless of name casing.
 * </p>
 */
public class AssetManager implements IAssetManager
{
    /**
     * The name of the class
     */
    private static final String CLASS_NAME = "assetManager";

    /**
     * The name of the hash-map
     */
    private static final String MAP_NAME = "registrar";

    /**
     * Dictionary-equivalent registrar for all program resources
     */
    private final Map<String, Asset> registrar = new HashMap<>();

    /**
     * Indexing all deferred resource to be loaded during tile-up
     */
    private final Queue<Asset> loadQueue = new ArrayDeque<>();

    /**
     * Global instance for backward compatibility with Asset constructor auto-registration.
     * @deprecated Use dependency injection instead of static access.
     */
    @Deprecated
    private static AssetManager globalInstance = null;

    /**
     * Constructs a new AssetManager instance.
     */
    public AssetManager()
    {
        // Set as global instance if not already set (for backward compatibility)
        if (globalInstance == null)
        {
            globalInstance = this;
        }
    }

    /**
     * Sets the global instance for backward compatibility.
     * @param instance The AssetManager instance to set as global.
     * @deprecated Use dependency injection instead.
     */
    @Deprecated
    public static void setGlobalInstance(AssetManager instance)
    {
        globalInstance = instance;
    }

    /**
     * Gets the global instance for backward compatibility.
     * @return The global AssetManager instance.
     * @deprecated Use dependency injection instead.
     */
    @Deprecated
    public static AssetManager getGlobalInstance()
    {
        return globalInstance;
    }

    /**
     * Puts an asset into the registrar and (if unloaded) indexed to the load queue.
     *
     * @param asset The asset to be registered.
     * @return Success indicator flag.
     */
    @Override
    public boolean register(Asset asset)
    {
        //Reject duplicate assets
        if (containsKey(asset.getType(), asset.getName()))
        {
            Logger.warn("Asset with key '%s' already exists in '%s'. Skipping registration.", asset.getKey(), AssetManager.MAP_NAME);
            return false;
        } else
        {
            //Register a new asset
            registrar.put(asset.getKey(), asset);
            Logger.logAssetAdd(AssetManager.CLASS_NAME, asset.getKey(), AssetManager.MAP_NAME);
            if (!asset.isLoaded()) loadQueue.add(asset);
            return true;
        }
    }

    /**
     * Method used to cache an asset into memory.
     * <br>
     * Note: Must be called in load sequence or all assets will be null.
     */
    @Override
    public void load()
    {
        while (!isLoaded())
        {
            Asset asset = loadQueue.peek();
            assert asset != null;
            asset.load();
            Logger.logAssetCached(AssetManager.CLASS_NAME, asset.getFileName());
            loadQueue.remove(asset);
        }
    }

    /**
     * Determines if an asset with a given key exists in the Registrar.
     *
     * @param type The type associated with the resource.
     * @param name The name of the resource.
     * @return If the asset with a given key exists in the Registrar.
     */
    @Override
    public boolean containsKey(String type, String name)
    {
        return registrar.containsKey(createKey(type, name));
    }

    /**
     * @return Weather or not the assets have been cached.
     */
    @Override
    public boolean isLoaded()
    {
        return loadQueue.isEmpty();
    }

    /**
     * Removes a resource of a given type from the Registrar.
     *
     * @param item The item to be removed.
     */
    @Override
    public void remove(Object item)
    {
        String type = "";

        if (item instanceof Image)
        {
            type = ((Image) item).getType();
        } else if (item instanceof Audio)
        {
            type = ((Audio) item).getType();
        } else if (item instanceof Font)
        {
            type = ((Font) item).getType();
        }

        remove(type, ((Asset) item).getName());
    }

    /**
     * Removes a resource with a given name from the Registrar.
     *
     * @param type The type associated with the resource.
     * @param name The name of the resource.
     */
    @Override
    public void remove(String type, String name)
    {
        String key = createKey(type, name);

        //Check if key associated with the asset is in the Registrar
        if (registrar.containsKey(key))
        {
            registrar.remove(key);
            Logger.logAssetRemove(AssetManager.CLASS_NAME, key, AssetManager.MAP_NAME);
        } else {
            Logger.warn("Asset with key '%s' not found in '%s'. Cannot remove.", key, AssetManager.MAP_NAME);
        }
    }

    /**
     * Creates a unique identifier for a resource.
     *
     * @param type The type associated with the resource.
     * @param name The name of the resource.
     * @return A unique identifier for a resource.
     */
    public static String createKey(String type, String name)
    {
        return (type + ":" + name).toLowerCase();
    }

    /**
     * @return The asset load queue.
     */
    @Override
    public synchronized Queue<Asset> getLoadQueue()
    {
        return loadQueue;
    }

    /**
     * Grabs a audio resource from the Registrar.
     *
     * @param name the unique identifier of the asset.
     * @return Audio resource.
     */
    @Override
    public synchronized Clip getAudio(String name)
    {
        return (Clip) registrar.get(createKey(Audio.TYPE, name).toLowerCase()).getData();
    }

    /**
     * Grabs an image resource from the Registrar.
     *
     * @param name the unique identifier of the asset.
     * @return Image resource.
     */
    @Override
    public synchronized Bitmap getImage(String name)
    {
        return (Bitmap) registrar.get(createKey(Image.TYPE, name).toLowerCase()).getData();
    }

    /**
     * Grabs a font resource from the Registrar.
     *
     * @param name the unique identifier of the asset.
     * @return Font resource.
     */
    @Override
    public synchronized Spritesheet getFont(String name)
    {
        return (Spritesheet) registrar.get(createKey(Font.TYPE, name).toLowerCase()).getData();
    }

    /**
     * @return The default font used by the engine.
     */
    @Override
    public synchronized Font getDefaultFont()
    {
        return (Font) registrar.get("$default");
    }

    /**
     * Method used to clean up memory used by
     * certain processes.
     */
    @Override
    public synchronized void cleanUp()
    {
        registrar.clear();
        loadQueue.clear();
    }
}
