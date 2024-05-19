package TransmuteCore.System.Asset;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import javax.sound.sampled.Clip;

import TransmuteCore.Graphics.Bitmap;
import TransmuteCore.Graphics.Sprites.Spritesheet;
import TransmuteCore.System.Error;
import TransmuteCore.System.Util;
import TransmuteCore.System.Asset.Type.Audio.Audio;
import TransmuteCore.System.Asset.Type.Fonts.Font;
import TransmuteCore.System.Asset.Type.Images.Image;

/**
 * <p>
 * The functional registrar of all assets used in the program.
 * Actual loading of assets is done in the loading menu.
 * </p>
 * <p>
 * <p>
 * Asset loading done in the same thread causes startup latency.
 * Larger assets (such as background music) will take significant
 * processing power. By utilizing deferred resource loading, the
 * resource reference can be declared prior to resource loading,
 * ensuring that the program launches responsively and in a more
 * user-friendly manner by showing a loading screen.
 * </p>
 */
public class AssetManager
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
    private static final Map<String, Asset> REGISTRAR = new HashMap<>();

    /**
     * Indexing all deferred resource to be loaded during tile-up
     */
    private static final Queue<Asset> LOAD_QUEUE = new ArrayDeque<>();

    /**
     * An instance of AssetManager
     */
    public static AssetManager instance = new AssetManager();

    /**
     * Puts an asset into the registrar and (if unloaded) indexed to the load queue.
     * This method is invoked automatically in the `Asset` class.
     *
     * @param asset The asset to be registered.
     * @return Success indicator flag.
     */
    public static boolean register(Asset asset)
    {
        //Reject duplicate assets
        if (containsKey(asset.getType(), asset.getName()))
        {
            new Error(Error.KeyAlreadyExistsException(AssetManager.CLASS_NAME, asset.getKey(), AssetManager.MAP_NAME));
            return false;
        } else
        {
            //Register a new asset
            REGISTRAR.put(asset.getKey(), asset);
            Util.logAdd(AssetManager.CLASS_NAME, asset.getKey(), AssetManager.MAP_NAME);
            if (!asset.isLoaded()) LOAD_QUEUE.add(asset);
            return true;
        }
    }

    /**
     * Method used to cache an asset into memory.
     * <br>
     * Note: Must be called in load sequence or all assets will be null.
     */
    public static void load()
    {
        while (!isLoaded())
        {
            Asset asset = LOAD_QUEUE.peek();
            assert asset != null; asset.load();
            Util.logCached(AssetManager.CLASS_NAME, asset.getFileName());
            LOAD_QUEUE.remove(asset);
        }
    }

    /**
     * Determines if an asset with a given key exists in the Registrar.
     *
     * @param type The type associated with the resource.
     * @param name The name of the resource.
     * @return If the asset with a given key exists in the Registrar.
     */
    public static boolean containsKey(String type, String name)
    {
        return REGISTRAR.containsKey(createKey(type, name));
    }

    /**
     * @return Weather or not the assets have been cached.
     */
    public static boolean isLoaded()
    {
        return LOAD_QUEUE.isEmpty();
    }

    /**
     * Removes a resource of a given type from the Registrar.
     *
     * @param item The item to be removed.
     */
    public static void remove(Object item)
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
    public static void remove(String type, String name)
    {
        String key = createKey(type, name);

        //Check if key associated with the asset is in the Registrar
        if (REGISTRAR.containsKey(key))
        {
            REGISTRAR.remove(key);
            Util.logRemove(AssetManager.CLASS_NAME, key, AssetManager.MAP_NAME);
        } else new Error(Error.KeyNotFoundException(AssetManager.CLASS_NAME, key, AssetManager.MAP_NAME));
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
    public synchronized static Queue<Asset> getLoadQueue()
    {
        return LOAD_QUEUE;
    }

    /**
     * Grabs a audio resource from the Registrar.
     *
     * @param name the unique identifier of the asset.
     * @return Audio resource.
     */
    public synchronized static Clip getAudio(String name)
    {
        return (Clip) REGISTRAR.get(createKey(Audio.TYPE, name).toLowerCase()).getData();
    }

    /**
     * Grabs an image resource from the Registrar.
     *
     * @param name the unique identifier of the asset.
     * @return Image resource.
     */
    public synchronized static Bitmap getImage(String name)
    {
        return (Bitmap) REGISTRAR.get(createKey(Image.TYPE, name).toLowerCase()).getData();
    }

    /**
     * Grabs a font resource from the Registrar.
     *
     * @param name the unique identifier of the asset.
     * @return Font resource.
     */
    public synchronized static Spritesheet getFont(String name)
    {
        return (Spritesheet) REGISTRAR.get(createKey(Font.TYPE, name).toLowerCase()).getData();
    }

    /**
     * @return The default font used by the engine.
     */
    public synchronized static Font getDefaultFont()
    {
        return (Font) REGISTRAR.get("$default");
    }

    /**
     * Method used to clean up memory used by
     * certain processes.
     */
    public synchronized static void cleanUp()
    {
        REGISTRAR.clear();
        LOAD_QUEUE.clear();
    }

    /**
     * @return An instance of AssetManager.
     */
    public synchronized static AssetManager getInstance()
    {
        return instance;
    }
}
