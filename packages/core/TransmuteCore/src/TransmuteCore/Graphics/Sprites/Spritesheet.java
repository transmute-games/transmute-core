package TransmuteCore.Graphics.Sprites;

import java.awt.image.BufferedImage;

import TransmuteCore.Graphics.Bitmap;
import TransmuteCore.System.Asset.Type.Images.Image;
import TransmuteCore.Units.Tuple2i;

public class Spritesheet extends Bitmap
{
    /**
     * Horizontal strip orientation
     */
    public static final int ORIENTATION_VERTICAL = 0x1;

    /**
     * Vertical strip orientation
     */
    public static final int ORIENTATION_HORIZONTAL = 0x2;

    /**
     * The main sprite-sheet image
     */
    public BufferedImage image;

    /**
     * Size of each sprite cell (assuming they are constant)
     */
    private Tuple2i cellSize;

    /**
     * Size of pixel gap between cells (0 if none)
     */
    private int verticalGapSize = 0, horizontalGapSize = 0;

    /**
     * Initial offset to grab sprite from, this is the first coordinate for sprite position calculations
     */
    private Tuple2i startOffset;
    {
        new Tuple2i(0, 0);
    }

    /**
     * Sorted cell sprite's are stored here
     */
    private Sprite[][] sprites;

    /**
     * Scaling
     **/
    private float scale = 1f;

    /**
     * Weather or not to create a pixel-perfect bounding box within the sprite.
     */
    private boolean initializeBounds = false;

    /**
     * Instantiates a Spritesheet object from a given Bitmap object (the main image) with sprite-sheet
     * parameters.
     *
     * @param image       The big image to be used as Spritesheet.
     * @param cellSize    Width and height of each sprite in a cell (Assuming cells are equal in size).
     * @param startOffset Starting position to calculate sprite position (in pixels).
     * @param vertGap     Vertical pixel gap size (0 if none).
     * @param horizGap    Horizontal pixel gap size (0 if none).
     * @param initializeBounds Weather or not to create a pixel-perfect bounding box within the sprite.
     */
    public Spritesheet(BufferedImage image, Tuple2i cellSize, Tuple2i startOffset, int vertGap, int horizGap, boolean initializeBounds)
    {
        super(image);

        if (image == null) {
            throw new IllegalArgumentException("Spritesheet image cannot be null");
        }
        if (cellSize == null) {
            throw new IllegalArgumentException("Cell size cannot be null");
        }
        if (cellSize.x <= 0 || cellSize.y <= 0) {
            throw new IllegalArgumentException(
                String.format("Cell size must be positive. Got: (%d, %d)", cellSize.x, cellSize.y)
            );
        }
        if (startOffset == null) {
            throw new IllegalArgumentException("Start offset cannot be null");
        }
        if (startOffset.x < 0 || startOffset.y < 0) {
            throw new IllegalArgumentException(
                String.format("Start offset cannot be negative. Got: (%d, %d)", startOffset.x, startOffset.y)
            );
        }
        if (vertGap < 0) {
            throw new IllegalArgumentException(
                String.format("Vertical gap cannot be negative. Got: %d", vertGap)
            );
        }
        if (horizGap < 0) {
            throw new IllegalArgumentException(
                String.format("Horizontal gap cannot be negative. Got: %d", horizGap)
            );
        }

        this.image = image;
        this.cellSize = cellSize;
        this.verticalGapSize = vertGap;
        this.horizontalGapSize = horizGap;
        this.startOffset = startOffset;
        this.initializeBounds = initializeBounds;

        Bitmap bmp = Image.getAsBitmap(image);
        int xOffset = startOffset.x;
        int yOffset = startOffset.y;
        int width = cellSize.x;
        int height = cellSize.y;
        int scaledWidth = image.getWidth() / width;
        int scaledHeight = image.getHeight() / height;
        sprites = new Sprite[scaledWidth][scaledHeight];
        for (int x = 0; x < scaledWidth; x++)
        {
            for (int y = 0; y < scaledHeight; y++)
            {
                //Map cells into a grid for future reference
                int[] data = bmp.getData(xOffset + x * (width + vertGap),
                        yOffset + y * (height + horizGap), (x + 1) * width, (y + 1) * height);

                sprites[x][y] = new Sprite(new Bitmap(data, width, height), initializeBounds);
            }
        }
    }

    /**
     * Instantiates a Spritesheet object from a given Bitmap object (the main image) with sprite-sheet
     * parameters.
     *
     * @param image       The big image to be used as Spritesheet.
     * @param cellSize    Width and height of each sprite in a cell (Assuming cells are equal in size).
     * @param startOffset Starting position to calculate sprite position (in pixels).
     * @param vertGap     Vertical pixel gap size (0 if none).
     * @param horizGap    Horizontal pixel gap size (0 if none).
     */
    public Spritesheet(BufferedImage image, Tuple2i cellSize, Tuple2i startOffset, int vertGap, int horizGap)
    {
        this(image, cellSize, startOffset, vertGap, horizGap, false);
    }

    /**
     * Instantiates a Spritesheet object from a given BufferedImage object (the main image).
     *
     * @param image    The big image to be used as Spritesheet.
     * @param cellSize Width and height of each sprite in a cell (Assuming cells are equal in size).
     */
    public Spritesheet(BufferedImage image, int cellSize)
    {
        this(image, new Tuple2i(cellSize, cellSize), new Tuple2i(0, 0), 0, 0, false);
    }

    /**
     * Instantiates a Spritesheet object from a given BufferedImage object (the main image).
     *
     * @param image    The big image to be used as Spritesheet.
     * @param cellSize Width and height of each sprite in a cell (Assuming cells are equal in size).
     * @param initializeBounds Weather or not to create a pixel-perfect bounding box within the sprite.
     */
    public Spritesheet(BufferedImage image, int cellSize, boolean initializeBounds)
    {
        this(image, new Tuple2i(cellSize, cellSize), new Tuple2i(0, 0), 0, 0, initializeBounds);
    }

    /**
     * Supplies a sub-image within the Spritesheet.
     *
     * @param x Number of tiles from left (starting with 0).
     * @param y Number of tiles from top (starting with 0).
     * @return A sub-image within the Spritesheet.
     */
    public Sprite crop(int x, int y)
    {
        if (x < 0 || y < 0 || x >= sprites.length || y >= sprites[0].length) {
            throw new IllegalArgumentException(
                String.format("Invalid sprite coordinates (%d, %d). Valid range: [0-%d, 0-%d]",
                    x, y, sprites.length - 1, sprites[0].length - 1)
            );
        }
        return sprites[x][y];
    }

    /**
     * Generates a scaled copy of the existing Spritesheet.
     *
     * @param scale Scaling ratio (1f is 1:1 ratio).
     * @return A scaled copy of the Spritesheet.
     */
    @Override
    public Spritesheet getScaled(float scale)
    {
        if (scale <= 0) {
            throw new IllegalArgumentException(
                String.format("Scale must be positive. Got: %.2f", scale)
            );
        }
        int newWidth = (int) (getWidth() * scale);
        int newHeight = (int) (getHeight() * scale);
        BufferedImage scaledImage = Image.getScaledImage(image, newWidth, newHeight);

        int newCellWidth = (int) (cellSize.x * scale);
        int newCellHeight = (int) (cellSize.y * scale);
        Tuple2i newCellSize = new Tuple2i(newCellWidth, newCellHeight);

        Spritesheet s = new Spritesheet(scaledImage, newCellSize, startOffset, verticalGapSize, horizontalGapSize, initializeBounds);
        s.scale = scale;
        return s;
    }

    /**
     * Generates a series of timed action.
     *
     * @param name      The name of the currentAnimation.
     * @param duration  Time in seconds to be applied to all generated frames.
     * @param spriteLoc Location of the sprite's (in terms of cells).
     * @return Generated action with specified cells and provided time for all frames.
     */
    public Animation generateAnimation(String name, int duration, Tuple2i... spriteLoc)
    {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Animation name cannot be null or empty");
        }
        if (spriteLoc == null || spriteLoc.length == 0) {
            throw new IllegalArgumentException("Sprite locations cannot be null or empty");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException(
                String.format("Duration must be positive. Got: %d", duration)
            );
        }
        Sprite[] sprites = new Sprite[spriteLoc.length];
        int i = 0;
        for (Tuple2i loc : spriteLoc)
        {
            Sprite sprite = crop(loc.x, loc.y);
            sprites[i] = sprite;
            i++;
        }
        return new Animation(name, sprites, duration);
    }

    /**
     * Generates a series of timed action.
     * <p>
     * Note that the length of the {@code duration[]} must be the same as {@code spriteLoc[]}.
     *
     * @param name      The name of the currentAnimation.
     * @param duration  Time in seconds to be applied for each frame. Size should be equal to <strong>spriteLoc</strong>.
     * @param spriteLoc Location of the sprite's (in terms of cells).
     * @return Generated action with specified cells and provided time for each frame.
     */
    public Animation generateAnimation(String name, int[] duration, Tuple2i[] spriteLoc)
    {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Animation name cannot be null or empty");
        }
        if (duration == null) {
            throw new IllegalArgumentException("Duration array cannot be null");
        }
        if (spriteLoc == null) {
            throw new IllegalArgumentException("Sprite locations array cannot be null");
        }
        if (duration.length != spriteLoc.length) {
            throw new IllegalArgumentException(
                String.format("Frame time array size (%d) should have the same length as spriteLoc array size (%d)!",
                    duration.length, spriteLoc.length)
            );
        }
        Sprite[] sprites = new Sprite[spriteLoc.length];
        int i = 0;
        for (Tuple2i p : spriteLoc)
        {
            Sprite sprite = crop(p.x, p.y);
            sprites[i] = sprite;
            i++;
        }
        return new Animation(name, sprites, duration);
    }

    /**
     * Generates a series of timed action, assuming the Spritesheet is either a vertical
     * or horizontal strip (i.e. 1 in width or height) and stores frames for an action.
     *
     * @param name                The name of the currentAnimation.
     * @param sequenceOrientation Orientation of the Spritesheet.
     * @param duration            Time in seconds to be applied to every frame.
     * @return Generated action with specified cells and provided time for every frame.
     */
    public Animation generateAnimation(String name, int sequenceOrientation, int duration)
    {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Animation name cannot be null or empty");
        }
        if (duration <= 0) {
            throw new IllegalArgumentException(
                String.format("Duration must be positive. Got: %d", duration)
            );
        }
        if (sequenceOrientation != ORIENTATION_HORIZONTAL && sequenceOrientation != ORIENTATION_VERTICAL) {
            throw new IllegalArgumentException(
                String.format("Invalid orientation: %d. Use ORIENTATION_HORIZONTAL or ORIENTATION_VERTICAL", 
                    sequenceOrientation)
            );
        }
        Sprite[] sprites;
        Animation animation = null;
        switch (sequenceOrientation)
        {
            case ORIENTATION_HORIZONTAL:
                sprites = new Sprite[this.sprites.length];
                for (int i = 0; i < sprites.length; i++)
                    sprites[i] = this.sprites[i][0];
                animation = new Animation(name, sprites, duration);
                return animation;
            case ORIENTATION_VERTICAL:
                sprites = new Sprite[this.sprites[0].length];
                for (int i = 0; i < sprites.length; i++)
                    sprites[i] = this.sprites[0][i];
                animation = new Animation(name, sprites, duration);
                return animation;
            default:
                return null;
        }
    }

    /**
     * Supplies the current scale of the Spritesheet image.
     *
     * @return Current scale of Spritesheet image.
     */
    public float getScale()
    {
        return scale;
    }

    /**
     * @return The Spritesheet image.
     */
    @Override
    public BufferedImage getImage()
    {
        return image;
    }

    /**
     * Supplies all the pre-divided sprite's in this sprite-sheet.
     *
     * @return All sprite's within this Spritesheet.
     */
    public Sprite[][] getSprites()
    {
        return sprites;
    }

    /**
     * @return Vertical gap between each sprite.
     */
    public int getVerticalGapSize()
    {
        return verticalGapSize;
    }

    /**
     * @return Horizontal gap between each sprite.
     */
    public int getHorizontalGapSize()
    {
        return horizontalGapSize;
    }

    /**
     * @return Initial starting offset for sprite calculation.
     */
    public Tuple2i getStartOffset()
    {
        return startOffset;
    }

    /**
     * @return Dimension for each sprite in this sprite-sheet.
     */
    public Tuple2i getCellSize()
    {
        return cellSize;
    }
}