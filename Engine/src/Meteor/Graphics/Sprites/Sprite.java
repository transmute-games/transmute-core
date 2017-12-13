package Meteor.Graphics.Sprites;

import java.awt.image.BufferedImage;

import Meteor.Graphics.Bitmap;

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
    private int width; //The width of the sprite
    private int height; //The height of the sprite

    /**
     * The constructor used to define a sprite given a BufferedImage.
     *
     * @param image The image of a sprite.
     */
    public Sprite(BufferedImage image)
    {
        super(image);
        this.image = image;

        width = image.getWidth();
        height = image.getHeight();
        if (width == height) size = width;
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
