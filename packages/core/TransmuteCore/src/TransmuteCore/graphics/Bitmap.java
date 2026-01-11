package TransmuteCore.graphics;

import TransmuteCore.assets.types.Image;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;

/**
 * Bitmap is a representation of a region of pixel data as derived from a BufferedImage.
 * Assets (images) are loaded in the form of BufferedImage before translated into an array
 * integer representation of its pixel data. The render context then calculates the suitable
 * coordinate on-screen to render the bitmap.
 */
public class Bitmap
{
    /**
     * Width of bitmap image
     */
    private int width;

    /**
     * Height of bitmap image
     */
    private int height;

    /**
     * Pixel color data
     */
    private int[] data;

    /**
     * Original image representation of Bitmap
     */
    private BufferedImage image;

    /**
     * Creates an empty bitmap of specified size.
     *
     * @param width  Width of bitmap
     * @param height Height of bitmap
     */
    public Bitmap(int width, int height)
    {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException(
                String.format("Bitmap dimensions must be positive. Got width: %d, height: %d", width, height)
            );
        }
        this.width = width;
        this.height = height;
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.data = Image.getData(image);
    }

    /**
     * Creates a deep clone of an existing bitmap.
     *
     * @param bitmap Source bitmap to be cloned
     */
    public Bitmap(Bitmap bitmap)
    {
        if (bitmap == null) {
            throw new IllegalArgumentException("Source bitmap cannot be null");
        }
        this.width = bitmap.width;
        this.height = bitmap.height;
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        this.image.setRGB(0, 0, width, height, bitmap.getImage().getRGB(0, 0, width, height, null, 0, width), 0, width);
        this.data = Image.getData(image);
    }

    /**
     * Creates a bitmap from a sample BufferedImage.
     * All color data and other properties are then derived from this image.
     *
     * @param image Sample BufferedImage.
     */
    public Bitmap(BufferedImage image)
    {
        if (image == null) {
            throw new IllegalArgumentException("BufferedImage cannot be null");
        }
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.data = new int[width * height];
        this.data = Image.getData(image);
    }

    /**
     * Creates a bitmap from existing color data and given size.
     * The BufferedImage representation will be generated from the color data.
     *
     * @param data Color data.
     * @param w    Width of the bitmap.
     * @param h    Height of the bitmap.
     */
    public Bitmap(int[] data, int w, int h)
    {
        if (data == null) {
            throw new IllegalArgumentException("Pixel data cannot be null");
        }
        if (w <= 0 || h <= 0) {
            throw new IllegalArgumentException(
                String.format("Bitmap dimensions must be positive. Got width: %d, height: %d", w, h)
            );
        }
        if (data.length != w * h) {
            throw new IllegalArgumentException(
                String.format("Data array length (%d) must match width * height (%d)", data.length, w * h)
            );
        }
        this.width = w;
        this.height = h;
        this.data = new int[w * h];
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, width, height, data, 0, width);
        this.data = Image.getData(image);
    }

    /**
     * Generates a scaled version of this bitmap based on a scaling ratio.
     *
     * @param scale Scaling ratio.
     * @return The scaled version of this bitmap based on a scaling ratio.
     */
    public Bitmap getScaled(float scale)
    {
        if (scale <= 0) {
            throw new IllegalArgumentException(
                String.format("Scale must be positive. Got: %.2f", scale)
            );
        }
        return getScaled((int) ((float) width * scale), (int) ((float) height * scale));
    }

    /**
     * Generates a scaled version of this bitmap based on a new given dimension.
     *
     * @param width  Width of scaled bitmap.
     * @param height Height of scaled bitmap.
     * @return The scaled version of this bitmap based on a new given dimension.
     */
    public Bitmap getScaled(int width, int height)
    {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException(
                String.format("Scaled dimensions must be positive. Got width: %d, height: %d", width, height)
            );
        }
        VolatileImage img = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice().getDefaultConfiguration()
                .createCompatibleVolatileImage(width, height, VolatileImage.TRANSLUCENT);
        Graphics2D _g = img.createGraphics();
        _g.setComposite(AlphaComposite.DstOut);
        _g.setColor(new Color(0f, 0f, 0f, 0f));
        _g.fillRect(0, 0, width, height);
        _g.setComposite(AlphaComposite.Src);
        _g.drawImage(image, 0, 0, width, height, null);
        _g.dispose();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        result.setRGB(0, 0, width, height, img.getSnapshot().getRGB(0, 0, width, height, null, 0, width), 0, width);
        return new Bitmap(result);
    }

    /**
     * Returns a region of pixels within the bitmap. The parameters
     * define the bounds of the region.
     *
     * @param xStart Starting x-coordinate
     * @param yStart Starting y-coordinate
     * @param xEnd   Ending x-coordinate
     * @param yEnd   Ending y-coordinate
     * @return The integer array of pixels of a given region within the bitmap
     */
    public int[] getData(int xStart, int yStart, int xEnd, int yEnd)
    {
        if (xEnd <= xStart || yEnd <= yStart)
            throw new IllegalArgumentException(String.format("xEnd <= xStart or yEnd <= yStart! xStart: %d, xEnd: %d | yStart: %d, yEnd: %d", xStart, xEnd, yStart, yEnd));
        if (xStart < 0 || xEnd > width || yStart < 0 || yEnd > height)
            throw new IllegalArgumentException(String.format("Selected region out of bounds! xStart: %d, xEnd: %d | yStart: %d (width: %d), yEnd: %d (height: %d)", xStart, xEnd, yStart, width, yEnd, height));

        int[] result = new int[(xEnd - xStart) * (yEnd - yStart)];

        for (int x = xStart; x < xEnd; x++)
        {
            for (int y = yStart; y < yEnd; y++)
            {
                result[(y - yStart) * (xEnd - xStart) + (x - xStart)] = data[x + y * width];
            }
        }

        return result;
    }

    /**
     * Creates a transformed bitmap that has been flipping vertically or horizontally.
     * A copy of the pixel data is generated and the original is not affected by
     * this method.
     *
     * @param horizontal Flag for flipping horizontally.
     * @param vertical   Flag for flipping vertically.
     * @return A bitmap that is flipped by provided parameters.
     */
    public Bitmap getFlipped(boolean horizontal, boolean vertical)
    {
        if (!horizontal && !vertical) return this;
        int[] data = new int[getData().length];
        if (horizontal && !vertical)
        {
            for (int x = 0; x < getWidth(); x++)
            {
                for (int y = getHeight() - 1; y >= 0; y--)
                {
                    data[(getHeight() - y - 1) * getWidth() + x] = data[y * getWidth() + x];
                }
            }
        } else if (!horizontal)
        {
            for (int x = getWidth() - 1; x >= 0; x--)
            {
                for (int y = 0; y < getHeight(); y++)
                {
                    data[y * getWidth() + (getWidth() - x - 1)] = data[y * getWidth() + x];
                }
            }
        } else
        {
            for (int i = data.length - 1; i > 0; i--)
            {
                data[data.length - i] = data[i];
            }
        }
        return new Bitmap(data, width, height);
    }

    /**
     * Returns a bitmap that is a smaller portion of the original. This is
     * similar to 'getPixels(int xStart, int yStart, int xEnd, int yEnd)' but the result
     * is wrapped in a Bitmap class for simplified use in Context.
     * <p>
     * The parameters specified define the bounds for the sub-bitmap.
     *
     * @param xStart Starting x-coordinate.
     * @param yStart Starting y-coordinate.
     * @param xEnd   Ending x-coordinate.
     * @param yEnd   Ending y-coordinate.
     * @return A bitmap that is a portion of the original.
     * @see TransmuteCore.Graphics.Context
     */
    public Bitmap getRegionAsBitmap(int xStart, int yStart, int xEnd, int yEnd)
    {
        return new Bitmap(getData(xStart, yStart, xEnd, yEnd), xEnd - xStart, yEnd - yStart);
    }

    /**
     * Supplies the width of this bitmap.
     *
     * @return Width of the bitmap
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Supplies the height of this bitmap.
     *
     * @return Height of the bitmap.
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Supplies the pixel data of this bitmap.
     *
     * @return Pixel data of the bitmap.
     */
    public int[] getData()
    {
        return data;
    }

    /**
     * Supplies the original BufferedImage of this bitmap.
     *
     * @return Original BufferedImage of the bitmap.
     */
    public BufferedImage getImage()
    {
        return image;
    }

    /**
     * Method used to clean up memory used by
     * certain processes.
     */
    public void cleanUp()
    {
        image.flush();

        for (int i = 0; i < data.length; i++) data[i] = 0;
    }
}
