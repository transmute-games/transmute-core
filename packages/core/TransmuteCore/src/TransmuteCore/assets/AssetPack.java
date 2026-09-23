package TransmuteCore.assets;

import TransmuteCore.assets.types.Audio;
import TransmuteCore.assets.types.Font;
import TransmuteCore.assets.types.Image;
import TransmuteCore.util.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Deep asset-loading module: one manifest in, assets registered and loaded.
 * <p>
 * Manifest format ({@code gamespec.properties} / {@code assets.properties}):
 * <pre>
 * font=fonts/font.png
 * image.player=sprites/player.png
 * audio.jump=sounds/jump.wav
 * </pre>
 * Paths are classpath resources relative to the classloader root
 * (typically {@code src/main/resources/}).
 */
public final class AssetPack
{
    public static final String DEFAULT_FONT_RESOURCE = "fonts/font.png";

    private final AssetManager assetManager;
    private final Map<String, String> images = new LinkedHashMap<>();
    private final Map<String, String> audio = new LinkedHashMap<>();
    private String fontPath;

    private AssetPack(AssetManager assetManager)
    {
        this.assetManager = Objects.requireNonNull(assetManager, "assetManager");
    }

    /**
     * Creates a pack bound to {@code assetManager}.
     */
    public static AssetPack create(AssetManager assetManager)
    {
        return new AssetPack(assetManager);
    }

    /**
     * Loads a manifest from the classpath and registers + loads all assets.
     *
     * @param resourcePath classpath path, e.g. {@code "gamespec.properties"}
     * @return the loaded pack
     */
    public static AssetPack loadClasspath(AssetManager assetManager, String resourcePath)
    {
        AssetPack pack = create(assetManager);
        try (InputStream in = AssetPack.class.getClassLoader().getResourceAsStream(resourcePath))
        {
            if (in == null)
            {
                throw new IllegalArgumentException("AssetPack manifest not found on classpath: " + resourcePath);
            }
            pack.readManifest(in);
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Failed to read AssetPack manifest: " + resourcePath, e);
        }
        pack.registerAndLoad();
        return pack;
    }

    /**
     * Loads a manifest from a filesystem path.
     */
    public static AssetPack loadFile(AssetManager assetManager, Path manifestPath)
    {
        AssetPack pack = create(assetManager);
        try (InputStream in = Files.newInputStream(manifestPath))
        {
            pack.readManifest(in);
        }
        catch (IOException e)
        {
            throw new IllegalStateException("Failed to read AssetPack manifest: " + manifestPath, e);
        }
        pack.registerAndLoad();
        return pack;
    }

    /**
     * Ensures the engine default bitmap font is initialized from the bundled resource
     * {@link #DEFAULT_FONT_RESOURCE}, or from {@code fontPath} if set on this pack.
     */
    public AssetPack ensureDefaultFont()
    {
        String path = fontPath != null ? fontPath : DEFAULT_FONT_RESOURCE;
        if (Font.defaultFont == null)
        {
            Font.initializeDefaultFont(path);
            assetManager.load();
            Logger.info("AssetPack: default font loaded from %s", path);
        }
        return this;
    }

    public AssetPack font(String classpathPath)
    {
        this.fontPath = classpathPath;
        return this;
    }

    public AssetPack image(String name, String classpathPath)
    {
        images.put(name, classpathPath);
        return this;
    }

    public AssetPack sound(String name, String classpathPath)
    {
        audio.put(name, classpathPath);
        return this;
    }

    /**
     * Registers configured assets and runs {@link AssetManager#load()}.
     */
    public AssetPack registerAndLoad()
    {
        if (fontPath != null)
        {
            Font.initializeDefaultFont(fontPath);
        }
        for (Map.Entry<String, String> entry : images.entrySet())
        {
            Image image = new Image(entry.getKey(), entry.getValue());
            assetManager.register(image);
        }
        for (Map.Entry<String, String> entry : audio.entrySet())
        {
            Audio clip = new Audio(entry.getKey(), entry.getValue());
            assetManager.register(clip);
        }
        assetManager.load();
        return this;
    }

    public AssetManager getAssetManager()
    {
        return assetManager;
    }

    private void readManifest(InputStream in) throws IOException
    {
        Properties props = new Properties();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8)))
        {
            props.load(reader);
        }
        for (String name : props.stringPropertyNames())
        {
            String value = props.getProperty(name).trim();
            if (value.isEmpty())
            {
                continue;
            }
            if (name.equals("font"))
            {
                fontPath = value;
            }
            else if (name.startsWith("image."))
            {
                images.put(name.substring("image.".length()), value);
            }
            else if (name.startsWith("audio."))
            {
                audio.put(name.substring("audio.".length()), value);
            }
        }
    }
}
