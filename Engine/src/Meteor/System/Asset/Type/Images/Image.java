package Meteor.System.Asset.Type.Images;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import Meteor.Graphics.Bitmap;
import Meteor.System.Error;
import Meteor.System.Asset.Asset;
import Meteor.System.Asset.Type.Fonts.Font;

public class Image extends Asset
{
    public static final String TYPE = "image";

    public Image(String key, String filePath)
    {
        super(Image.TYPE, key, filePath);
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
            image = ImageUtils.load(assetLoader.getResourceAsStream(filePath));
        } catch (IOException e)
        {
            e.printStackTrace();
            new Error(Error.FileNotFoundException(Image.TYPE, filePath));
        }

        image = ImageUtils.convertTo(BufferedImage.TYPE_INT_ARGB, image);
        target = new Bitmap(image);
    }

    public static Bitmap load(Class<?> className, String filePath)
    {
        if (filePath == null)
        {
            new Error(Error.filePathException(Image.TYPE));
        }

        BufferedImage image = null;

        try
        {
            image = ImageIO.read(className.getResourceAsStream(filePath));
        } catch (IOException e)
        {
            e.printStackTrace();
            new Error(Error.FileNotFoundException(Font.TYPE, filePath));
        }

        image = ImageUtils.convertTo(BufferedImage.TYPE_INT_ARGB, image);
        return new Bitmap(image);
    }

    @Override
    public Bitmap getData()
    {
        if ((Bitmap) target == null)
        {
            new Error("[" + Image.TYPE + "]: [" + fileName + "] has not been loaded.");
        }

        return (Bitmap) target;
    }
}
