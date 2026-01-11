package TransmuteCore.graphics.Sprites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import TransmuteCore.core.Interfaces.Services.ISpriteManager;
import TransmuteCore.util.Error;
import TransmuteCore.util.Logger;
import TransmuteCore.math.Tuple2i;
import TransmuteCore.math.Tuple4i;

/**
 * {@code SpriteManager} is a sprite handler class.
 * <br>
 * This class is used to grab a sprite-sheet and split it up into individual sprite's.
 */
public class SpriteManager implements ISpriteManager
{
    public static final String CLASS_NAME = "spriteManager"; //The title of the class
    private static final String MAP_NAME = "spriteMap"; //The key of the hash map

    public Spritesheet spritesheet; //The main Spritesheet object

    private final HashMap<String, Sprite[]> spriteMap = new HashMap<>(); //The {@code HashMap<>()} used to hold the sprite's
    private ArrayList<Pattern> regexList; //List of regex patterns used for key manipulation

    /**
     * The constructor used to define the {@code SpriteManager}.
     *
     * @param spritesheet The Spritesheet resource.
     */
    public SpriteManager(Spritesheet spritesheet)
    {
        this.spritesheet = spritesheet;

        init();
    }

    private void init()
    {
        // Removed auto-registration with manager for decoupling
        // Manager should be passed the SpriteManager instance explicitly

        regexList = new ArrayList<>();
        regexList.add(Pattern.compile("\\d"));
        regexList.add(Pattern.compile("\\s+"));
        regexList.add(Pattern.compile("_"));
        regexList.add(Pattern.compile("\\W+"));
    }

    /**
     * Determines if a sprite with a given key exists in the hash table.
     *
     * @param key The lower-cased key attached to the sprite.
     * @return If the sprite with a given key exists in the hash table.
     */
    private boolean checkMap(String key)
    {
        return spriteMap.containsKey(key.toLowerCase());
    }

    /**
     * Method used to get the array of sprite's attached to a given key from the {@code spriteMap}.
     *
     * @param key The lower-cased key attached to the sprite.
     * @return The array of sprite's attached to a given key.
     */
    public Sprite[] get(String key)
    {
        key = key.toLowerCase();
        Sprite[] spriteArray = null;
        if (checkMap(key)) spriteArray = spriteMap.get(key);
        else new Error(Error.KeyNotFoundException(SpriteManager.CLASS_NAME, key, SpriteManager.MAP_NAME));
        return spriteArray;
    }

    /**
     * Method used to get a sprite from the {@code spriteMap}.
     *
     * @param key   The lower-cased key attached to the sprite.
     * @param index The location of the sprite in the array of sprite's.
     * @return The sprite attached to the given key.
     */
    public Sprite get(String key, int index)
    {
        key = key.toLowerCase();
        Sprite[] spriteArray = null;
        if (checkMap(key)) spriteArray = spriteMap.get(key);
        else new Error(Error.KeyNotFoundException(SpriteManager.CLASS_NAME, key, SpriteManager.MAP_NAME));

        Sprite sprite = null;

        assert spriteArray != null;

        if (index <= spriteArray.length)
        {
            sprite = spriteArray[index];
        } else
        {
            new Error(Error.KeyNotFoundException(SpriteManager.CLASS_NAME, key, SpriteManager.MAP_NAME));
        }

        return sprite;
    }

    /**
     * Method used to add a sprite to the {@code spriteMap}.
     *
     * @param key The lower-cased key attached to the sprite.
     * @param x   The x-coordinate of the sprite.
     * @param y   The y-coordinate of the sprite.
     */
    public void add(String key, int x, int y)
    {
        add(key.toLowerCase(), new Tuple4i(x, y, 0, 0));
    }

    /**
     * Method used to add a sprite to the {@code spriteMap}.
     *
     * @param key        The lower-cased key attached to the sprite.
     * @param x          The x-coordinate of the sprite.
     * @param y          The y-coordinate of the sprite.
     * @param cellWidth  The width of the cell.
     * @param cellHeight The height of the cell.
     */
    public void add(String key, int x, int y, int cellWidth, int cellHeight)
    {
        add(key.toLowerCase(), new Tuple4i(x, y, cellWidth, cellHeight));
    }

    /**
     * Method used to add a sprite to the spriteMap.
     *
     * @param key      The lower-cased key attached to the sprite.
     * @param locArray The array of locations (x, y) of sprite's in the {@code Spritesheet > (Bitmap)}.
     */
    @Override
    public void add(String key, Tuple2i... locArray)
    {
        for (Pattern regex : regexList) key = (key.replaceAll(regex.pattern(), "")).toLowerCase();
        if (!checkMap(key))
        {
            Sprite[] spriteArray = new Sprite[locArray.length];

            for (int i = 0; i < locArray.length; i++)
            {
                Tuple2i loc = locArray[i];
                spriteArray[i] = new Sprite(spritesheet.crop(loc.x, loc.y));
            }

            spriteMap.put(key, spriteArray);
            Logger.logAssetAdd(SpriteManager.CLASS_NAME, key, SpriteManager.MAP_NAME);
        } else new Error(Error.KeyAlreadyExistsException(SpriteManager.CLASS_NAME, key, SpriteManager.MAP_NAME));
    }

    /**
     * Method used to add a sprite to the {@code spriteMap}.
     *
     * @param key             The lower-cased key attached to the sprite.
     * @param propertiesArray The array of  (x, y, width, height) of sprite's in the {@code Spritesheet > (BufferedImage)}.
     */
    private void add(String key, Tuple4i... propertiesArray)
    {
        for (Pattern regex : regexList) key = (key.replaceAll(regex.pattern(), "")).toLowerCase();
        if (!checkMap(key))
        {
            Sprite[] spriteArray = new Sprite[propertiesArray.length];

            for (int i = 0; i < propertiesArray.length; i++)
            {
                Tuple4i properties = propertiesArray[i];
                spriteArray[i] = new Sprite(spritesheet.crop(properties.getX(), properties.getY()));
            }

            spriteMap.put(key, spriteArray);
            Logger.logAssetAdd(SpriteManager.CLASS_NAME, key, SpriteManager.MAP_NAME);
        } else new Error(Error.KeyAlreadyExistsException(SpriteManager.CLASS_NAME, key, SpriteManager.MAP_NAME));
    }

    /**
     * Method used to generate an animation given an array of sprites.
     *
     * @param name        The name of the animation
     * @param duration    Time in milliseconds to be applied to all generated frames.
     * @param spriteArray The list of sprites to animation.
     * @return Generated action with specified cells and provided time for all frames.
     */
    @Override
    public Animation createAnimation(String name, int duration, Sprite... spriteArray)
    {
        return new Animation(name, spriteArray, duration);
    }

    /**
     * @return The main Spritesheet object
     */
    @Override
    public Spritesheet getSpritesheet()
    {
        return spritesheet;
    }
}
