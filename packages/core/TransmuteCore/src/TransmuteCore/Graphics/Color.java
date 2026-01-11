package TransmuteCore.Graphics;

/**
 * A pixel integer converter that converts color models (r, g, b, a) to/from a packed
 * 32-bitmap integer. 32-bitmap <strong>DataBufferInt</strong> are used for pixel data model
 * in the render context.
 */
public class Color
{
    /**
     * Pure black
     */
    public static final int BLACK = Color.toPixelInt(0, 0, 0, 255);

    /**
     * Pure white
     */
    public static final int WHITE = Color.toPixelInt(255, 255, 255, 255);

    /**
     * Pure red
     */
    public static final int RED = Color.toPixelInt(255, 0, 0, 255);

    /**
     * Pure green
     */
    public static final int GREEN = Color.toPixelInt(0, 255, 0, 255);

    /**
     * Pure blue
     */
    public static final int BLUE = Color.toPixelInt(0, 0, 255, 255);

    /**
     * Pure magenta
     */
    public static final int MAGENTA = Color.toPixelInt(255, 0, 255, 255);

    /**
     * Pure yellow
     */
    public static final int YELLOW = Color.toPixelInt(255, 255, 0, 255);

    /**
     * Pure cyan
     */
    public static final int CYAN = Color.toPixelInt(0, 255, 255, 255);

    /**
     * Converts an integer pixel color representation to rgb colors.
     * The array length returned is 4, representing r, g, b, a with
     * indicies of 0, 1, 2, 3 respectively.
     *
     * @param pixelColor Color integer with model ARGB.
     * @return An array of RGBA information from 0 to 255.
     */
    public static int[] fromPixelInt(int pixelColor)
    {
        int[] rgba = new int[4];
        rgba[3] = (pixelColor >> 24) & 0xFF;
        rgba[0] = (pixelColor >> 16) & 0xFF;
        rgba[1] = (pixelColor >> 8) & 0xFF;
        rgba[2] = pixelColor & 0xFF;
        return rgba;
    }

    /**
     * Converts given color information to a pixel color integer.
     *
     * @param r Red channel value between 0f - 1f.
     * @param g Green channel value between 0f - 1f.
     * @param b Blue channel value between 0f - 1f.
     * @param a Alpha channel value between 0f - 1f.
     * @return Color integer of type ARGB.
     */
    public static int toPixelInt(float r, float g, float b, float a)
    {
        if (r < 0f) r = 0f;
        if (r > 1f) r = 1f;
        if (g < 0f) g = 0f;
        if (g > 1f) g = 1f;
        if (b < 0f) b = 0f;
        if (b > 1f) b = 1f;
        return ((int) (a * 255) & 0xFF << 24) |
                ((int) (r * 255) & 0xFF << 16) |
                ((int) (g * 255) & 0xFF << 8) |
                ((int) b & 0xFF * 255);
    }

    /**
     * Converts given color information to a pixel color integer.
     *
     * @param r Red channel value between 0 - 255.
     * @param g Green channel value between 0 - 255.
     * @param b Blue channel value between 0 - 255.
     * @param a Alpha channel value between 0 - 255.
     * @return Color integer of type ARGB.
     */
    public static int toPixelInt(int r, int g, int b, int a)
    {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    /**
     * Converts given color information to a pixel color integer.
     *
     * @param argb ARGB color array.
     * @return Color integer of type ARGB.
     */
    public static int toPixelInt(int[] argb)
    {
        return toPixelInt(argb[0], argb[1], argb[2], argb[3]);
    }

    /**
     * Performs a pre-multiplied overlay alpha composite on an existing pixel.
     *
     * @param pixelColor Original pixel color.
     * @param tintColor  Tinting pixel color.
     * @return Tinted color pixel of type ARGB.
     */
    public static int tint(int pixelColor, int tintColor)
    {
        int[] pRGBA = fromPixelInt(pixelColor);
        int[] tRGBA = fromPixelInt(tintColor);
        float tintAlpha = (float) tRGBA[3] / 255f;
        int r = (int) (tRGBA[0] * tintAlpha + pRGBA[0] * (1 - tintAlpha));
        int g = (int) (tRGBA[1] * tintAlpha + pRGBA[1] * (1 - tintAlpha));
        int b = (int) (tRGBA[2] * tintAlpha + pRGBA[2] * (1 - tintAlpha));
        return toPixelInt(r, g, b, 255);
    }

    /**
     * Linearly interpolates between two colors.
     *
     * @param color1 Starting color.
     * @param color2 Ending color.
     * @param t      Interpolation factor (0.0 = color1, 1.0 = color2).
     * @return Interpolated color.
     */
    public static int lerp(int color1, int color2, float t)
    {
        if (t < 0f) t = 0f;
        if (t > 1f) t = 1f;
        
        int[] c1 = fromPixelInt(color1);
        int[] c2 = fromPixelInt(color2);
        
        int r = (int) (c1[0] + (c2[0] - c1[0]) * t);
        int g = (int) (c1[1] + (c2[1] - c1[1]) * t);
        int b = (int) (c1[2] + (c2[2] - c1[2]) * t);
        int a = (int) (c1[3] + (c2[3] - c1[3]) * t);
        
        return toPixelInt(r, g, b, a);
    }

    /**
     * Darkens a color by a given factor.
     *
     * @param color  The color to darken.
     * @param factor Darkening factor (0.0 = black, 1.0 = original color).
     * @return Darkened color.
     */
    public static int darken(int color, float factor)
    {
        if (factor < 0f) factor = 0f;
        if (factor > 1f) factor = 1f;
        
        int[] rgba = fromPixelInt(color);
        int r = (int) (rgba[0] * factor);
        int g = (int) (rgba[1] * factor);
        int b = (int) (rgba[2] * factor);
        
        return toPixelInt(r, g, b, rgba[3]);
    }

    /**
     * Lightens a color by a given factor.
     *
     * @param color  The color to lighten.
     * @param factor Lightening factor (0.0 = original color, 1.0 = white).
     * @return Lightened color.
     */
    public static int lighten(int color, float factor)
    {
        if (factor < 0f) factor = 0f;
        if (factor > 1f) factor = 1f;
        
        int[] rgba = fromPixelInt(color);
        int r = (int) (rgba[0] + (255 - rgba[0]) * factor);
        int g = (int) (rgba[1] + (255 - rgba[1]) * factor);
        int b = (int) (rgba[2] + (255 - rgba[2]) * factor);
        
        return toPixelInt(r, g, b, rgba[3]);
    }

    /**
     * Blends two colors using alpha blending.
     *
     * @param foreground Foreground color.
     * @param background Background color.
     * @return Blended color.
     */
    public static int blend(int foreground, int background)
    {
        int[] fRGBA = fromPixelInt(foreground);
        int[] bRGBA = fromPixelInt(background);
        
        float alpha = fRGBA[3] / 255f;
        int r = (int) (fRGBA[0] * alpha + bRGBA[0] * (1 - alpha));
        int g = (int) (fRGBA[1] * alpha + bRGBA[1] * (1 - alpha));
        int b = (int) (fRGBA[2] * alpha + bRGBA[2] * (1 - alpha));
        
        return toPixelInt(r, g, b, 255);
    }

    /**
     * Inverts a color.
     *
     * @param color The color to invert.
     * @return Inverted color (preserves alpha).
     */
    public static int invert(int color)
    {
        int[] rgba = fromPixelInt(color);
        return toPixelInt(255 - rgba[0], 255 - rgba[1], 255 - rgba[2], rgba[3]);
    }

    /**
     * Adjusts the alpha (transparency) of a color.
     *
     * @param color The color to modify.
     * @param alpha New alpha value (0-255).
     * @return Color with adjusted alpha.
     */
    public static int withAlpha(int color, int alpha)
    {
        if (alpha < 0) alpha = 0;
        if (alpha > 255) alpha = 255;
        
        int[] rgba = fromPixelInt(color);
        return toPixelInt(rgba[0], rgba[1], rgba[2], alpha);
    }

    /**
     * Converts a color to grayscale using luminosity method.
     *
     * @param color The color to convert.
     * @return Grayscale version of the color.
     */
    public static int toGrayscale(int color)
    {
        int[] rgba = fromPixelInt(color);
        // Luminosity method: 0.299*R + 0.587*G + 0.114*B
        int gray = (int) (rgba[0] * 0.299f + rgba[1] * 0.587f + rgba[2] * 0.114f);
        return toPixelInt(gray, gray, gray, rgba[3]);
    }

    private Color()
    {
    }
}
