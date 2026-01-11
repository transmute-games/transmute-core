package TransmuteCore.core.Interfaces.Services;

import TransmuteCore.graphics.Bitmap;
import TransmuteCore.assets.types.Font;

import java.awt.image.BufferedImage;

/**
 * Interface for rendering operations.
 * Provides methods for drawing graphics to the screen.
 */
public interface IRenderer
{
    /**
     * Gets the width of the rendering context.
     *
     * @return The width in pixels.
     */
    int getWidth();

    /**
     * Gets the height of the rendering context.
     *
     * @return The height in pixels.
     */
    int getHeight();

    /**
     * Gets the underlying BufferedImage.
     *
     * @return The image buffer.
     */
    BufferedImage getImage();

    /**
     * Sets the clear color for the background.
     *
     * @param color The color as a 32-bit ARGB integer.
     */
    void setClearColor(int color);

    /**
     * Gets the current clear color.
     *
     * @return The clear color as a 32-bit ARGB integer.
     */
    int getClearColor();

    /**
     * Clears the context with the clear color.
     */
    void clear();

    /**
     * Renders a bitmap at the specified position.
     *
     * @param bitmap The bitmap to render.
     * @param x      The x-coordinate.
     * @param y      The y-coordinate.
     */
    void renderBitmap(Bitmap bitmap, int x, int y);

    /**
     * Renders a bitmap with alpha transparency.
     *
     * @param bitmap The bitmap to render.
     * @param x      The x-coordinate.
     * @param y      The y-coordinate.
     * @param alpha  The alpha value (0.0 - 1.0).
     */
    void renderBitmap(Bitmap bitmap, int x, int y, float alpha);

    /**
     * Renders a bitmap with tint color.
     *
     * @param bitmap    The bitmap to render.
     * @param x         The x-coordinate.
     * @param y         The y-coordinate.
     * @param tintColor The tint color as a 32-bit ARGB integer.
     */
    void renderBitmap(Bitmap bitmap, int x, int y, int tintColor);

    /**
     * Renders a bitmap with alpha and scale.
     *
     * @param bitmap The bitmap to render.
     * @param x      The x-coordinate.
     * @param y      The y-coordinate.
     * @param alpha  The alpha value (0.0 - 1.0).
     * @param scale  The scale factor.
     */
    void renderBitmap(Bitmap bitmap, int x, int y, float alpha, float scale);

    /**
     * Renders a bitmap with alpha and tint color.
     *
     * @param bitmap    The bitmap to render.
     * @param x         The x-coordinate.
     * @param y         The y-coordinate.
     * @param alpha     The alpha value (0.0 - 1.0).
     * @param tintColor The tint color as a 32-bit ARGB integer.
     */
    void renderBitmap(Bitmap bitmap, int x, int y, float alpha, int tintColor);

    /**
     * Renders a bitmap with all rendering options.
     *
     * @param bitmap    The bitmap to render.
     * @param x         The x-coordinate.
     * @param y         The y-coordinate.
     * @param alpha     The alpha value (0.0 - 1.0).
     * @param scale     The scale factor.
     * @param tintColor The tint color as a 32-bit ARGB integer.
     */
    void renderBitmap(Bitmap bitmap, int x, int y, float alpha, float scale, int tintColor);

    /**
     * Renders a filled rectangle.
     *
     * @param x      The x-coordinate.
     * @param y      The y-coordinate.
     * @param width  The width.
     * @param height The height.
     * @param color  The color as a 32-bit ARGB integer.
     */
    void renderFilledRectangle(int x, int y, int width, int height, int color);

    /**
     * Renders a rectangle outline.
     *
     * @param x      The x-coordinate.
     * @param y      The y-coordinate.
     * @param width  The width.
     * @param height The height.
     * @param color  The color as a 32-bit ARGB integer.
     */
    void renderRectangle(int x, int y, int width, int height, int color);

    /**
     * Renders text at the specified position.
     *
     * @param text  The text to render.
     * @param x     The x-coordinate.
     * @param y     The y-coordinate.
     * @param color The color as a 32-bit ARGB integer.
     */
    void renderText(String text, int x, int y, int color);

    /**
     * Sets the font to use for text rendering.
     *
     * @param font The font to set.
     */
    void setFont(Font font);

    /**
     * Gets the current font.
     *
     * @return The current font.
     */
    Font getFont();
}
