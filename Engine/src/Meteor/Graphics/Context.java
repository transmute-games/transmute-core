package Meteor.Graphics;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

import Meteor.System.Asset.Type.Fonts.Font;

/**
 * This is the heart of the rendering engine. A master BufferedImage is created using the ARGB color model
 * with DataBufferInt. Each pixel is rendered onto the screen using the 32-bit integer color model.
 * An image is represented by an array of size width * height, and rendering is accomplished by the
 * transformation of pixel data from bitmap pixel array to the master image.
 * <p>
 * The master image is then draw using Java2D Graphics object onto the canvas. Dimensions for the
 * render context is different to the dimension of the wrapper, but a resize of the wrapper should not
 * update the size of the context. The size of the context refers to the dimensions of the master image.
 */
public class Context
{
    /**
     * Dimensions for the render context
     */
    private int width, height;

    /**
     * Array of DataBufferInt pixel data linked to the master image
     */
    private int[] data;

    /**
     * The master BufferImage that holds all displayed pixel data
     */
    private BufferedImage image;

    /**
     * Currently used font for drawing text
     */
    private Font font;

    /**
     * Default color to fill the background with
     */
    private int clearColor = Color.toPixelInt(0, 0, 0, 255);

    /**
     * Creates a render context with given dimensions.
     *
     * @param width  Width of the context, in pixels.
     * @param height Height of the context, in pixels.
     */
    public Context(int width, int height)
    {
        this.width = width;
        this.height = height;
        this.data = new int[width * height];
        this.image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        data = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    }

    /**
     * Clears the context with the default black color.
     */
    public void clear()
    {
        Arrays.fill(data, clearColor);
    }

    /**
     * Draws a given bitmap to the context. Transfers the pixel data from
     * the bitmap to the specified location on the context.
     *
     * @param bitmap Bitmap to be rendered.
     * @param x      x-coordinate on screen.
     * @param y      y-coordinate on screen.
     */
    public void renderBitmap(Bitmap bitmap, int x, int y)
    {
        renderBitmap(bitmap, x, y, 1.0f);
    }

    /**
     * Draws a given bitmap to the context. In addition, the pixel alpha is
     * modified to the one provided, giving transparency effects.
     *
     * @param bitmap Bitmap to be rendered.
     * @param x      x-coordinate on screen.
     * @param y      y-coordinate on screen.
     * @param alpha  Alpha value desired (0f - 1f).
     */
    public void renderBitmap(Bitmap bitmap, int x, int y, float alpha)
    {
        renderBitmap(bitmap, x, y, alpha, 1.0f);
    }

    /**
     * Draws a given bitmap to the context. In addition, the bitmap
     * is tinted with a given color.
     *
     * @param bitmap    Bitmap to be rendered.
     * @param x         x-coordinate on screen.
     * @param y         y-coordinate on screen.
     * @param tintColor Color used to tint the bitmap.
     */
    public void renderBitmap(Bitmap bitmap, int x, int y, int tintColor)
    {
        renderBitmap(bitmap, x, y, 1.0f, tintColor);
    }

    /**
     * Draws a given bitmap to the context. In addition, the bitmap
     * has custom scaling and transparency.
     *
     * @param bitmap Bitmap to be rendered.
     * @param x      x-coordinate on screen.
     * @param y      y-coordinate on screen.
     * @param alpha  Transparency of the bitmap.
     * @param scale  Custom scaling of the bitmap (1.0f is 1:1 ratio).
     */
    public void renderBitmap(Bitmap bitmap, int x, int y, float alpha, float scale)
    {
        renderBitmap(bitmap, x, y, alpha, scale, 0);
    }

    /**
     * Draws a given bitmap onto the context. In addition, the bitmap has
     * custom transparency and color tinting.
     *
     * @param bitmap    Bitmap to be rendered.
     * @param x         x-coordinate on screen.
     * @param y         y-coordinate on screen.
     * @param alpha     Transparency of the bitmap.
     * @param tintColor Custom scaling of the bitmap (1.0f is 1:1 ratio).
     */
    public void renderBitmap(Bitmap bitmap, int x, int y, float alpha, int tintColor)
    {
        renderBitmap(bitmap, x, y, alpha, 1.0f, tintColor);
    }

    /**
     * Draws a given bitmap onto the context. In addition, the bitmap
     * has custom scaling, transparency and color tinting.
     *
     * @param bitmap    Bitmap to be rendered.
     * @param x         x-coordinate on screen.
     * @param y         y-coordinate on screen.
     * @param alpha     Transparency of the bitmap.
     * @param scale     Scale factor of Bitmap (1.0f is 1:1 ratio).
     * @param tintColor Custom scaling of the bitmap (1.0f is 1:1 ratio).
     */
    public void renderBitmap(Bitmap bitmap, int x, int y, float alpha, float scale, int tintColor)
    {
        Bitmap scaled = bitmap.getScaled(scale);
        int xStart = x;
        int yStart = y;
        int xEnd = xStart + scaled.getWidth();
        int yEnd = yStart + scaled.getHeight();

        if (xStart >= width || yStart >= height || xEnd < 0 || yEnd < 0) return;
        if (xStart < 0) xStart = 0;
        if (yStart < 0) yStart = 0;
        if (xEnd > width) xEnd = width;
        if (yEnd > height) yEnd = height;

        for (int xPos = xStart; xPos < xEnd; xPos++)
        {
            for (int yPos = yStart; yPos < yEnd; yPos++)
            {
                int index = yPos * width + xPos;
                if (index < 0 || index > data.length - 1) continue;

                int bmpIndex = (yPos - yStart) * scaled.getWidth() + (xPos - xStart);
                int pixel = scaled.getData()[bmpIndex];
                int pixelAlpha = ((pixel >> 24) & 0xFF);
                if (pixelAlpha <= 0) continue;
                if (pixelAlpha != 255) pixel = Color.tint(data[index], pixel);
                if (tintColor != 0) pixel = Color.tint(pixel, tintColor);
                if (alpha < 1f)
                {
                    int[] rgba = Color.fromPixelInt(pixel);
                    pixel = Color.tint(data[index], Color.toPixelInt(rgba[0], rgba[1], rgba[2], (int) (alpha * 255f)));
                }

                data[index] = pixel;
            }
        }
    }

    /**
     * Fills a rectangle on-screen with a given color.
     *
     * @param x      x-coordinate on screen
     * @param y      y-coordinate on screen
     * @param width  Width of rectangle
     * @param height Height of rectangle
     * @param color  Color of rectangle (Use <code>Color.toPixelInt()</code>)
     */
    public void renderFilledRectangle(int x, int y, int width, int height, int color)
    {
        int xStart = x;
        int yStart = y;
        int xEnd = xStart + width;
        int yEnd = yStart + height;

        if (xStart < 0) xStart = 0;
        if (yStart < 0) yStart = 0;
        if (xEnd > this.width) xEnd = this.width;
        if (yEnd > this.height) yEnd = this.height;
        if (xStart >= this.width || yStart >= this.height || xEnd < 0 || yEnd < 0) return;

        for (int xPos = xStart; xPos < xEnd; xPos++)
        {
            for (int yPos = yStart; yPos < yEnd; yPos++)
            {
                int index = yPos * this.width + xPos;
                if (index < 0 || index > data.length - 1) continue;
                if (((color >> 24) & 0xFF) == 255)
                {
                    data[index] = color;
                } else if (((color >> 24) & 0xFF) > 0)
                {
                    data[index] = Color.tint(data[index], color);
                }
            }
        }
    }

    /**
     * Draws a rectangle on-screen with a given color.
     *
     * @param x      x-coordinate on screen
     * @param y      y-coordinate on screen
     * @param width  Width of rectangle
     * @param height Height of rectangle
     * @param color  Color of rectangle (Use <code>Color.toPixelInt()</code>)
     */
    public void renderRectangle(int x, int y, int width, int height, int color)
    {
        int xStart = x;
        int yStart = y;
        int xEnd = xStart + width;
        int yEnd = yStart + height;
        if (xStart >= this.width || yStart >= this.height || xEnd < 0 || yEnd < 0) return;
        if (xStart < 0) xStart = 0;
        if (yStart < 0) yStart = 0;
        if (xEnd > this.width) xEnd = this.width;
        if (yEnd > this.height) yEnd = this.height;

        for (int xPos = xStart; xPos < xEnd; xPos++)
        {
            for (int yPos = yStart; yPos < yEnd; yPos++)
            {
                int index = yPos * this.width + xPos;
                if (index < 0 || index > data.length - 1) continue;
                if (xPos == xStart || xPos == xEnd - 1 || yPos == yStart || yPos == yEnd - 1)
                {
                    if (((color >> 24) & 0xFF) == 255)
                    {
                        data[index] = color;
                    } else if (((color >> 24) & 0xFF) > 0)
                    {
                        data[index] = Color.tint(data[index], color);
                    }
                }
            }
        }
    }

    /**
     * Draws a line of text to the screen.
     * Additionally, the <pre>'\n'</pre> character can be used to switch to a new line.
     *
     * @param text Text to be drawn on screen.
     * @param x    x-coordinate on screen.
     * @param y    y-coordinate on screen.
     */
    public void renderText(String text, int x, int y)
    {
        renderText(text, x, y, 0x00000000);
    }

    /**
     * Draws a line of text to the screen with custom color.
     * Additionally, the <pre>'\n'</pre> character can be used to switch to a new line.
     *
     * @param text  Text to be drawn on screen.
     * @param x     x-coordinate on screen.
     * @param y     y-coordinate on screen.
     * @param color Color of the text.
     */
    public void renderText(String text, int x, int y, int color)
    {
        renderText(text, x, y, color, 1.0f);
    }

    /**
     * Draws a line of text to the screen with custom color and scaling.
     * Additionally, the <pre>'\n'</pre> character can be used to switch to a new line.
     *
     * @param text  Text to be drawn on screen.
     * @param x     x-coordinate on screen.
     * @param y     y-coordinate on screen.
     * @param color Color of the text.
     * @param scale Custom scaling per glyph (1.0f is 1:1 ratio).
     */
    public void renderText(String text, int x, int y, int color, float scale)
    {
        renderText(text, x, y, color, scale, 1.0f);
    }

    /**
     * Draws a line of text to the screen with custom color, scaling and transparency.
     * Additionally, the <pre>'\n'</pre> character can be used to switch to a new line.
     *
     * @param text  Text to be drawn on screen.
     * @param x     x-coordinate on screen.
     * @param y     y-coordinate on screen.
     * @param color Color of the text.
     * @param scale Custom scaling per glyph (1.0f is 1:1 ratio).
     * @param alpha Alpha transparency of the text (between <pre>0f</pre> and <pre>1.0f</pre>.
     */
    public void renderText(String text, int x, int y, int color, float scale, float alpha)
    {
        if (font == null) return;

        font.render(this, text, x, y, color, scale, alpha);
    }

    /**
     * Sets the color of a single pixel on the context.
     *
     * @param x     x-coordinate on the screen.
     * @param y     y-coordinate on the screen.
     * @param color Color of the pixel (use <pre>Color.toPixelInt()</pre>).
     */
    public void blitPixel(int x, int y, int color)
    {
        if (x < 0 || x > width - 1 || y < 0 || y >= height - 1)
            return;
        if (((color >> 24) & 0xFF) == 255)
        {
            data[y * width + x] = color;
        } else if (((color >> 24) & 0xFF) > 0)
        {
            data[y * width + x] = Color.tint(data[y * width + x], color);
        }
    }

    /**
     * Sets the current font used for drawing text.
     *
     * @param font Font used for drawing text.
     */
    public void setFont(Font font)
    {
        this.font = font;
    }

    /**
     * @return The current font in use for drawing text.
     */
    public Font getFont()
    {
        return font;
    }

    /**
     * @return Pixel color data of the context.
     */
    public int[] getPixels()
    {
        return data;
    }

    /**
     * @return Current master image.
     */
    public BufferedImage getImage()
    {
        return image;
    }

    /**
     * @return Width of the context.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * @return Height of the context.
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Sets the color used to clear last frame.
     * Use <pre>Color.toPixInt()</pre>.
     *
     * @param color Background clear color.
     */
    public void setClearColor(int color)
    {
        this.clearColor = color;
    }
}
