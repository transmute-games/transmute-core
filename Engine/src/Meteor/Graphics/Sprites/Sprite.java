package Meteor.Graphics.Sprites;

import java.awt.image.BufferedImage;

import Meteor.Graphics.Bitmap;
import Meteor.Graphics.Rectangle;
import Meteor.System.Asset.Type.Images.ImageUtils;

/**
 * {@code Sprite} is a generic representation of a sprite class.
 * <br>
 * This class is used as a generic representation of a sprite.
 */
public class Sprite extends Bitmap
{
    public Bitmap bitmap; //The bitmap of a sprite

    private BufferedImage image; //The sprite's image
    private int size; //The size of the sprite
    private float scale; //Scaling ratio
    private int width; //The width of the sprite
    private int height; //The height of the sprite
    private Rectangle bounds; //The rectangular bounding box for use in collision testing

    /**
     * The constructor used to define a sprite given a BufferedImage.
     *
     * @param image The image of a sprite.
     */
    public Sprite(BufferedImage image)
    {
        super(image);
        this.image = image;
        this.bitmap = ImageUtils.getAsBitmap(image);

        width = image.getWidth();
        height = image.getHeight();
        if (width == height) size = width;
        setScale(1.0f);
    }

    /**
     * The constructor used to define a sprite given a BufferedImage.
     *
     * @param image The image of a sprite.
     * @param createBounds Weather or not to create a pixel-perfect bounding box within the sprite.
     */
    public Sprite(BufferedImage image, boolean createBounds)
    {
        super(image);
        this.image = image;
        this.bitmap = ImageUtils.getAsBitmap(image);

        width = image.getWidth();
        height = image.getHeight();
        if (width == height) size = width;
        if (createBounds) setBounds(image);
        setScale(1.0f);
    }

    /**
     * The constructor used to define a sprite given a Bitmap.
     *
     * @param bitmap The bitmap of a sprite.
     */
    public Sprite(Bitmap bitmap)
    {
        super(bitmap);

        this.bitmap = bitmap;
        this.image = bitmap.getImage();

        width = this.image.getWidth();
        height = this.image.getHeight();
        if (width == height) size = width;
        setScale(1.0f);
    }

    /**
     * The constructor used to define a sprite given a Bitmap.
     *
     * @param bitmap The bitmap of a sprite.
     * @param createBounds Weather or not to create a pixel-perfect bounding box within the sprite.
     */
    public Sprite(Bitmap bitmap, boolean createBounds)
    {
        super(bitmap);

        this.bitmap = bitmap;
        this.image = bitmap.getImage();

        width = this.image.getWidth();
        height = this.image.getHeight();
        if (width == height) size = width;
        if (createBounds) setBounds(image);
        setScale(1.0f);
    }


    /**
     * The constructor used to define a sprite given a BufferedImage.
     *
     * @param image The image of a sprite.
     */
    public Sprite(BufferedImage image, float scale)
    {
        super(image);
        this.image = image;
        this.scale = scale;

        width = image.getWidth();
        height = image.getHeight();
        if (width == height) size = width;
    }

    /**
     * The constructor used to define a sprite given a BufferedImage.
     *
     * @param image The image of a sprite.
     * @param createBounds Weather or not to create a pixel-perfect bounding box within the sprite.
     */
    public Sprite(BufferedImage image, float scale, boolean createBounds)
    {
        super(image);
        this.image = image;
        this.scale = scale;

        width = image.getWidth();
        height = image.getHeight();
        if (width == height) size = width;
        if (createBounds) setBounds(image);
    }

    /**
     * The constructor used to define a sprite given a Bitmap.
     *
     * @param bitmap The bitmap of a sprite.
     * @param scale  Scaling ratio (1f is 1:1 ratio).
     */
    public Sprite(Bitmap bitmap, float scale)
    {
        super(bitmap);

        this.bitmap = bitmap;
        this.image = bitmap.getImage();

        width = this.image.getWidth();
        height = this.image.getHeight();
        if (width == height) size = width;
        this.scale = scale;
    }

    /**
     * The constructor used to define a sprite given a Bitmap.
     *
     * @param bitmap The bitmap of a sprite.
     * @param scale  Scaling ratio (1f is 1:1 ratio).
     * @param createBounds Weather or not to create a pixel-perfect bounding box within the sprite.
     */
    public Sprite(Bitmap bitmap, float scale, boolean createBounds)
    {
        super(bitmap);

        this.bitmap = bitmap;
        this.image = bitmap.getImage();

        width = this.image.getWidth();
        height = this.image.getHeight();
        if (width == height) size = width;
        this.scale = scale;

        if (createBounds) setBounds(image);
    }

    /**
     * Calculates a pixel-perfect bounding box when given a image of type
     * {@code BufferedImage.TYPE_INT_ARGB}
     *
     * @param image The image to calculate a pixel-perfect bounding box.
     */
    private void setBounds(BufferedImage image)
    {
        if (image.getType() != BufferedImage.TYPE_INT_ARGB) {
            image = ImageUtils.convertTo(BufferedImage.TYPE_INT_ARGB, image);
        }

        int xMin = Integer.MAX_VALUE;
        int xMax = Integer.MIN_VALUE;
        int yMin = Integer.MAX_VALUE;
        int yMax = Integer.MIN_VALUE;

        for (int x = 0; x < image.getWidth(); x++)
        {
            for (int y = 0; y < image.getHeight(); y++)
            {
                int color = image.getRGB(x, y);
                int alpha = (color >> 24) & 0xFF;

                if (alpha != 0)
                {
                    // This sets the edges to the current coordinate
                    // if this pixel lies outside the current boundaries
                    xMin = Math.min(x, xMin);
                    xMax = Math.max(x, xMax);
                    yMin = Math.min(y, yMin);
                    yMax = Math.max(y, yMax);
                }
            }
        }

        bounds = new Rectangle(xMin, yMin, (xMax - xMin), (yMax - yMin));
    }

    /**
     * @return The calculated bounding box around the image
     */
    public Rectangle getBounds()
    {
        return bounds;
    }

    /**
     * @return the size of the sprite.
     */
    public int getSize()
    {
        return size;
    }

    /**
     * Method used to set the size of the sprite.
     *
     * @param spriteSize the size of the sprite.
     */
    public void setSize(int spriteSize)
    {
        this.size = spriteSize;
    }

    /**
     * Method used to set the scaling (1f is 1:1 ratio).
     *
     * @param scale Scaling ratio (1f is 1:1 ratio).
     */
    public void setScale(float scale)
    {
        this.scale = scale;
    }

    /**
     * @return The Scaling ratio (1f is 1:1 ratio).
     */
    public float getScale()
    {
        return scale;
    }

    /**
     * @return The width of the sprite.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Method used to set the width of the sprite.
     *
     * @param spriteWidth the size of the sprite.
     */
    public void setWidth(int spriteWidth)
    {
        this.width = spriteWidth;
    }

    /**
     * @return The height of the sprite.
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Method used to set the height of the sprite.
     *
     * @param spriteHeight the size of the sprite.
     */
    public void setHeight(int spriteHeight)
    {
        this.height = spriteHeight;
    }

    /**
     * @return The sprite's image
     */
    public BufferedImage getImage()
    {
        return image;
    }

    /**
     * Method used to set the sprite's image.
     *
     * @param image The sprite's image.
     */
    public void setImage(BufferedImage image)
    {
        this.image = image;
    }
}
