package TransmuteCore.util.Debug;

import TransmuteCore.graphics.Color;
import TransmuteCore.graphics.Context;
import TransmuteCore.assets.types.Font;

/**
 * Renders debug information on screen.
 * Provides formatted text output with backgrounds and colors.
 */
public class DebugRenderer
{
    private static final int BACKGROUND_COLOR = Color.toPixelInt(0, 0, 0, 180);
    private static final int TEXT_COLOR = Color.toPixelInt(255, 255, 255, 255);
    private static final int GOOD_COLOR = Color.toPixelInt(0, 255, 0, 255);
    private static final int WARNING_COLOR = Color.toPixelInt(255, 255, 0, 255);
    private static final int BAD_COLOR = Color.toPixelInt(255, 0, 0, 255);
    
    private static final int PADDING = 4;
    private static final int LINE_HEIGHT = 10;
    
    private Font font;
    private int currentY = 0;
    
    /**
     * Creates a new DebugRenderer.
     *
     * @param font The font to use for rendering text.
     */
    public DebugRenderer(Font font)
    {
        this.font = font;
    }
    
    /**
     * Begins a new debug panel at the specified position.
     *
     * @param ctx Context to render to.
     * @param x   X position.
     * @param y   Y position.
     */
    public void beginPanel(Context ctx, int x, int y)
    {
        currentY = y;
    }
    
    /**
     * Renders a line of text with background.
     *
     * @param ctx  Context to render to.
     * @param x    X position.
     * @param text Text to render.
     */
    public void renderLine(Context ctx, int x, String text)
    {
        renderLine(ctx, x, text, TEXT_COLOR);
    }
    
    /**
     * Renders a line of text with background and custom color.
     *
     * @param ctx   Context to render to.
     * @param x     X position.
     * @param text  Text to render.
     * @param color Text color.
     */
    public void renderLine(Context ctx, int x, String text, int color)
    {
        if (font == null || text == null) return;
        
        int textWidth = font.getTextWidth(text);
        int textHeight = LINE_HEIGHT;
        
        // Draw background
        ctx.renderFilledRectangle(x, currentY, textWidth + PADDING * 2, textHeight, BACKGROUND_COLOR);
        
        // Draw text
        ctx.setFont(font);
        ctx.renderText(text, x + PADDING, currentY + 1, color);
        
        currentY += textHeight;
    }
    
    /**
     * Renders a labeled value (e.g., "FPS: 60").
     *
     * @param ctx   Context to render to.
     * @param x     X position.
     * @param label Label text.
     * @param value Value text.
     */
    public void renderValue(Context ctx, int x, String label, String value)
    {
        renderLine(ctx, x, label + ": " + value);
    }
    
    /**
     * Renders a labeled value with color based on goodness.
     *
     * @param ctx    Context to render to.
     * @param x      X position.
     * @param label  Label text.
     * @param value  Value text.
     * @param isGood Whether the value is good (green) or bad (red).
     */
    public void renderValue(Context ctx, int x, String label, String value, boolean isGood)
    {
        String text = label + ": " + value;
        renderLine(ctx, x, text, isGood ? GOOD_COLOR : BAD_COLOR);
    }
    
    /**
     * Renders a labeled value with color based on threshold.
     *
     * @param ctx         Context to render to.
     * @param x           X position.
     * @param label       Label text.
     * @param value       Numeric value.
     * @param goodThreshold   Threshold for good (green).
     * @param warningThreshold Threshold for warning (yellow).
     * @param higherIsBetter  Whether higher values are better.
     */
    public void renderThresholdValue(Context ctx, int x, String label, int value, 
                                    int goodThreshold, int warningThreshold, boolean higherIsBetter)
    {
        int color = TEXT_COLOR;
        
        if (higherIsBetter)
        {
            if (value >= goodThreshold) color = GOOD_COLOR;
            else if (value >= warningThreshold) color = WARNING_COLOR;
            else color = BAD_COLOR;
        }
        else
        {
            if (value <= goodThreshold) color = GOOD_COLOR;
            else if (value <= warningThreshold) color = WARNING_COLOR;
            else color = BAD_COLOR;
        }
        
        renderValue(ctx, x, label, String.valueOf(value), color == GOOD_COLOR);
    }
    
    /**
     * Renders a labeled value with color based on threshold (double version).
     *
     * @param ctx         Context to render to.
     * @param x           X position.
     * @param label       Label text.
     * @param value       Numeric value.
     * @param goodThreshold   Threshold for good (green).
     * @param warningThreshold Threshold for warning (yellow).
     * @param higherIsBetter  Whether higher values are better.
     */
    public void renderThresholdValue(Context ctx, int x, String label, double value, 
                                    double goodThreshold, double warningThreshold, boolean higherIsBetter)
    {
        int color = TEXT_COLOR;
        
        if (higherIsBetter)
        {
            if (value >= goodThreshold) color = GOOD_COLOR;
            else if (value >= warningThreshold) color = WARNING_COLOR;
            else color = BAD_COLOR;
        }
        else
        {
            if (value <= goodThreshold) color = GOOD_COLOR;
            else if (value <= warningThreshold) color = WARNING_COLOR;
            else color = BAD_COLOR;
        }
        
        String formattedValue = String.format("%.2f", value);
        renderLine(ctx, x, label + ": " + formattedValue, color);
    }
    
    /**
     * Renders a section header.
     *
     * @param ctx   Context to render to.
     * @param x     X position.
     * @param title Section title.
     */
    public void renderSection(Context ctx, int x, String title)
    {
        if (font == null || title == null) return;
        
        int textWidth = font.getTextWidth(title);
        int textHeight = LINE_HEIGHT;
        
        // Draw slightly lighter background for headers
        int headerBg = Color.toPixelInt(40, 40, 40, 200);
        ctx.renderFilledRectangle(x, currentY, textWidth + PADDING * 2, textHeight, headerBg);
        
        // Draw text
        ctx.setFont(font);
        ctx.renderText(title, x + PADDING, currentY + 1, TEXT_COLOR);
        
        currentY += textHeight;
    }
    
    /**
     * Adds vertical spacing.
     *
     * @param pixels Number of pixels to skip.
     */
    public void addSpacing(int pixels)
    {
        currentY += pixels;
    }
    
    /**
     * @return Current Y position for drawing.
     */
    public int getCurrentY()
    {
        return currentY;
    }
    
    /**
     * Sets the font to use for rendering.
     *
     * @param font The font to use.
     */
    public void setFont(Font font)
    {
        this.font = font;
    }
}
