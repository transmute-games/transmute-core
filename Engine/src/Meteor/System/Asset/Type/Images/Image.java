package Meteor.System.Asset.Type.Images;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.VolatileImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import javax.imageio.ImageIO;

import Meteor.Graphics.Bitmap;
import Meteor.Graphics.Context;
import Meteor.Graphics.Sprites.Sprite;
import Meteor.Graphics.Sprites.Spritesheet;
import Meteor.System.Error;
import Meteor.System.Asset.Asset;
import Meteor.System.Asset.Type.Fonts.Font;
import Meteor.Units.Tuple2i;

public class Image extends Asset
{
    public static final String TYPE = "image";

    public static final int FORMAT_RGB = 0x0; //The image format in RGB
    public static final int FORMAT_ARGB = 0x1; //The image format in RGBA

    public Image(String name, String filePath)
    {
        super(Image.TYPE, name, filePath);
    }

    @Override
    public void load()
    {
        if (filePath == null)
        {
            new Error(Error.filePathException(Image.TYPE));
            return;
        }

        BufferedImage image = null;

        try
        {
            image = load(getClass().getClassLoader().getResourceAsStream(filePath));
        } catch (IOException e)
        {
            e.printStackTrace();
            new Error(Error.FileNotFoundException(Image.TYPE, filePath));
        }

        assert image != null;
        image = convertTo(BufferedImage.TYPE_INT_ARGB, image);
        target = image;
    }

    public static BufferedImage load(Class<?> className, String filePath)
    {
        if (filePath == null)
        {
            new Error(Error.filePathException(Image.TYPE));
        }

        BufferedImage image = null;

        try
        {
            image = ImageIO.read(className.getClassLoader().getResourceAsStream(filePath));
        } catch (IOException e)
        {
            e.printStackTrace();
            new Error(Error.FileNotFoundException(Font.TYPE, filePath));
        }

        assert image != null;
        image = convertTo(BufferedImage.TYPE_INT_ARGB, image);
        return image;
    }

    @Override
    public BufferedImage getData()
    {
        if (target == null)
        {
            new Error("[" + Image.TYPE + "]: [" + fileName + "] has not been loaded.");
        }

        return (BufferedImage) target;
    }

    /**
     * Crops the image into a given width and height based
     * on the inputed x-coordinate, and y-coordinate.
     *
     * @param image      The image to be cropped.
     * @param xPos       The x-coordinate of the item to crop.
     * @param yPos       The y-coordinate of the item to crop.
     * @param cellWidth  The width of the cell.
     * @param cellHeight The height of the cell.
     * @return The cropped portion of the image.
     */
    public static BufferedImage crop(BufferedImage image, int xPos, int yPos, int cellWidth, int cellHeight)
    {
        return image.getSubimage(xPos * cellWidth, yPos * cellHeight, cellWidth, cellHeight);
    }

    /**
     * Crops the image into a given width and height based
     * on the inputed x-coordinate, and y-coordinate.
     *
     * @param image     the image to be cropped.
     * @param xPos      The x-coordinate of the item to crop.
     * @param yPos      The y-coordinate of the item to crop.
     * @param imageSize The size of the image.
     * @return The cropped portion of the image.
     */
    public static BufferedImage crop(BufferedImage image, int xPos, int yPos, int imageSize)
    {
        return crop(image, xPos, yPos, imageSize, imageSize);
    }

    /**
     * Method used to rotate an image based on an inputted degree value.
     *
     * @param image   The image file to rotate.
     * @param degrees The amount of rotation in degrees.
     * @return The rotated image file.
     */
    public static BufferedImage rotate(BufferedImage image, int degrees)
    {
        BufferedImage blankCanvas = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

        Graphics2D _g = (Graphics2D) blankCanvas.getGraphics();
        _g.rotate(Math.toRadians(degrees), image.getWidth() / 2, image.getHeight() / 2);
        _g.drawImage(image, 0, 0, null);

        return image = blankCanvas;
    }

    /**
     * Method used to grab the pixel data of a given image file.
     *
     * @param image  The image file used to grab the pixel data of.
     * @param format The format of the image (e.g. RGBA).
     * @return The array of pixel data.
     */
    public static int[] getData(BufferedImage image, int format)
    {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] data = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();

        if (format == FORMAT_ARGB) return data;

        int[] colorData = new int[width * height];
        for (int i = 0; i < width * height; i++)
        {
            int r = (data[i] & 0xff0000) >> 16;
            int g = (data[i] & 0xff00) >> 8;
            int b = (data[i] & 0xff);
            colorData[i] = r << 16 | g << 8 | b;
        }

        return colorData;
    }

    /**
     * Method used to compare two given images.
     *
     * @param a The image to compare against.
     * @param b The image to compare with.
     * @return Weather or not the two images are the same.
     */
    public static boolean compare(BufferedImage a, BufferedImage b)
    {
        if (a.getType() != b.getType())
        {
            a = convertTo(BufferedImage.TYPE_INT_ARGB, a);
            b = convertTo(BufferedImage.TYPE_INT_ARGB, b);
        }

        int[] aData = getAsBitmap(a).getData();
        int[] bData = getAsBitmap(b).getData();


        return Arrays.equals(aData, bData);
    }

    /**
     * Converts an image's type to a given type.
     *
     * @param type  The type of image (e.g. BufferedImage.TYPE_INT_RGB).
     * @param image The image to convert.
     * @return The converted image.
     */
    public static BufferedImage convertTo(int type, BufferedImage image)
    {
        if (image.getType() != type)
        {
            int width = image.getWidth();
            int height = image.getHeight();
            int[] data = getData(image);

            BufferedImage result = new BufferedImage(width, height, type);
            result.setRGB(0, 0, width, height, data, 0, width);
        }

        return image;
    }

    /**
     * Generates a scaled version of a given image based on a new given dimension.
     *
     * @param width  Width of scaled bitmap.
     * @param height Height of scaled bitmap.
     * @return The scaled version of a given image based on a new given dimension.
     */
    public static BufferedImage getScaledImage(BufferedImage image, int width, int height)
    {
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
        return result;
    }

    public static BufferedImage getFlipped(BufferedImage image, boolean horizontal, boolean vertical)
    {
        if (!horizontal && !vertical) return image;

        BufferedImage result = null;

        if (horizontal && !vertical)
        {
            AffineTransform affineTransform = AffineTransform.getScaleInstance(-1, 1);
            affineTransform.translate(-image.getWidth(null), 0);
            AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            result = affineTransformOp.filter(image, null);
        } else if (vertical && !horizontal)
        {
            AffineTransform affineTransform = AffineTransform.getScaleInstance(1, -1);
            affineTransform.translate(0, -image.getHeight(null));
            AffineTransformOp affineTransformOp = new AffineTransformOp(affineTransform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            result = affineTransformOp.filter(image, null);
        }

        return result;
    }

    /**
     * Extracts the pixel data of a given image based on a file-path.
     *
     * @param filePath The path to the image.
     * @return Pixel data of a image.
     */
    public static int[] getData(String filePath)
    {
        return getData(Image.load(Image.class, filePath));
    }

    /**
     * Extracts the pixel data of a given image.
     *
     * @param image The image to extract pixel data from.
     * @return Pixel data of a image.
     */
    public static int[] getData(BufferedImage image)
    {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] data;

        if (image.getType() == BufferedImage.TYPE_INT_RGB || image.getType() == BufferedImage.TYPE_INT_ARGB)
        {
            data = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        } else
        {
            data = image.getRGB(0, 0, width, height, null, 0, width);;
        }

        return data;
    }
    /**
     * Method converts a {@code BufferedImage} into a {@code Bitmap}.
     *
     * @param image The image to convert.
     * @return The converted image.
     */
    public static Bitmap getAsBitmap(BufferedImage image)
    {
        return new Bitmap(image);
    }

    /**
     * Method converts a {@code BufferedImage} into a {@code Sprite}.
     *
     * @param image The image to convert.
     * @return The converted image.
     */
    public static Sprite getAsSprite(BufferedImage image)
    {
        return new Sprite(image);
    }

    /**
     * Converts a {@code BufferedImage} into a {@code Spritesheet} with rectangular cell size.
     *
     * @param image      The image to convert.
     * @param cellWidth  Width of each sub-image within the spritesheet.
     * @param cellHeight Height of each sub-image within the spritesheet.
     * @return The newly created spritesheet resource.
     */
    public static Spritesheet getAsSpritesheet(BufferedImage image, int cellWidth, int cellHeight)
    {
        return new Spritesheet(image, new Tuple2i(cellWidth, cellHeight), new Tuple2i(0, 0), 0, 0);
    }

    /**
     * Loads a BufferedImage externally or locally through an <strong>InputStream</strong>.
     * This can be used to load assets within a .jar executable by calling:
     * <code>className.class.getResourceAsStream(String res);</code>
     *
     * @param in Data source to stream.
     * @return Loaded resource.
     * @throws IOException When path to the resource is invalid and/or resource cannot be loaded.
     */
    public static BufferedImage load(InputStream in) throws IOException
    {
        return load(in, 1.0f);
    }

    /**
     * Loads a BufferedImage externally or locally through an <strong>InputStream</strong> with custom scaling.
     * This can be used to load assets within a .jar executable by calling:
     * <code>className.class.getResourceAsStream(String res);</code>
     *
     * @param in    Data source to stream.
     * @param scale Scale of the resource, 1.0f is 1:1 ratio.
     * @return Loaded resource.
     * @throws IOException When path to the resource is invalid and/or resource cannot be loaded.
     */
    public static BufferedImage load(InputStream in, float scale) throws IOException
    {
        BufferedImage image = ImageIO.read(in);
        AffineTransform transform = new AffineTransform();
        transform.scale(scale, scale);
        AffineTransformOp scaleOp = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        int newWidth = (int) ((float) image.getWidth() * scale);
        int newHeight = (int) ((float) image.getHeight() * scale);
        return scaleOp.filter(image, new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB));
    }

    /**
     * Loads a BufferedImage in a specified external directory with custom scaling.
     *
     * @param filePath Path to resource.
     * @param scale    Scale of resource, 1.0f is normal 1:1 scaling.
     * @return Loaded resource.
     * @throws IOException When path to the resource is invalid and/or resource cannot be loaded.
     */
    public static BufferedImage load(String filePath, float scale) throws IOException
    {
        return load(Files.newInputStream(Paths.get(filePath)), scale);
    }

    /**
     * Method used to render an image to the screen or game window object.
     *
     * @param ctx   The Game render 'canvas'.
     * @param bmp   The bitmap form of the image.
     * @param xPos  The x-coordinate relative to the screen to place the image.
     * @param yPos  The y-coordinate relative to the screen to place the image.
     */
    public static void render(Context ctx, Bitmap bmp, int xPos, int yPos)
    {
        ctx.renderBitmap(bmp, xPos, yPos);
    }

    /**
     * Method used to render an image to the screen or game window object.
     *
     * @param ctx   The Game render 'canvas'.
     * @param image The image file.
     * @param xPos  The x-coordinate relative to the screen to place the image.
     * @param yPos  The y-coordinate relative to the screen to place the image.
     */
    public static void render(Context ctx, BufferedImage image, int xPos, int yPos)
    {
        ctx.renderBitmap(getAsBitmap(image), xPos, yPos);
    }
}
