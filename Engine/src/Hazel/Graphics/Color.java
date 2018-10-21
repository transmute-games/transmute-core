package Hazel.Graphics;

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
    public static final int[] fromPixelInt(int pixelColor)
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
    public static final int toPixelInt(float r, float g, float b, float a)
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
    public static final int toPixelInt(int r, int g, int b, int a)
    {
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    /**
     * Converts given color information to a pixel color integer.
     *
     * @param argb ARGB color array.
     * @return Color integer of type ARGB.
     */
    public static final int toPixelInt(int[] argb)
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
    public static final int tint(int pixelColor, int tintColor)
    {
        int[] pRGBA = fromPixelInt(pixelColor);
        int[] tRGBA = fromPixelInt(tintColor);
        float tintAlpha = (float) tRGBA[3] / 255f;
        int r = (int) (tRGBA[0] * tintAlpha + pRGBA[0] * (1 - tintAlpha));
        int g = (int) (tRGBA[1] * tintAlpha + pRGBA[1] * (1 - tintAlpha));
        int b = (int) (tRGBA[2] * tintAlpha + pRGBA[2] * (1 - tintAlpha));
        return toPixelInt(r, g, b, 255);
    }

    private Color()
    {
    }
}
