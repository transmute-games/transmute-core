package Meteor.Graphics.UI;

import java.awt.Rectangle;

import Meteor.GameEngine.Manager;
import Meteor.GameEngine.Interfaces.Renderable;
import Meteor.Graphics.Context;

/**
 * A representation of a status-bar.
 */
public class Bar implements Renderable
{
    private int bg, fg; //The background and foreground colors
    private Rectangle bounds; //The containing rectangle
    private float currentAmount = 0.0f; //The amount of foreground currently filled.
    private float max = 1.0f; //The maximum amount of foreground that could be filled.

    /**
     * Defines a status-bar given its background, foreground, x and y location, width and height.
     *
     * @param bg     The background color.
     * @param fg     The foreground color.
     * @param x      The x-location of the status-bar.
     * @param y      The y-location of the status-bar.
     * @param width  The width of the status-bar.
     * @param height The height of the status-bar.
     */
    public Bar(int bg, int fg, int x, int y, int width, int height)
    {
        this.bg = bg;
        this.fg = fg;

        this.bounds = new Rectangle(x, y, width, height);
    }

    /**
     * Defines a status-bar given its background, foreground, x and y location, width, height and a
     * specific amount of foreground that could be filled.
     *
     * @param bg     The background color.
     * @param fg     The foreground color.
     * @param x      The x-location of the status-bar.
     * @param y      The y-location of the status-bar.
     * @param width  The width of the status-bar.
     * @param height The height of the status-bar.
     * @param max    The maximum amount of foreground that could be filled.
     */
    public Bar(int bg, int fg, int x, int y, int width, int height, float max)
    {
        this.bg = bg;
        this.fg = fg;
        this.max = max;

        this.bounds = new Rectangle(x, y, width, height);
    }

    @Override
    public void render(Manager manager, Context ctx)
    {
        ctx.renderFilledRectangle(bounds.x, bounds.y, bounds.width, bounds.height, bg);
        ctx.renderFilledRectangle(bounds.x, bounds.y, getGrowth(), bounds.height, fg);
    }

    /**
     * @return The current amount of progress.
     */
    private float getGrowth()
    {
        return bounds.width * (this.currentAmount / max);
    }

    /**
     * Increases the progress of the status-bar.
     *
     * @param amount The amount to increase.
     */
    public void increasePercent(float amount)
    {
        setPercent(currentAmount + amount);
    }

    /**
     * @param amount The amount to increase.
     */
    private void setPercent(float amount)
    {
        this.currentAmount = amount;
        if (currentAmount > max) currentAmount = max;
    }

}
