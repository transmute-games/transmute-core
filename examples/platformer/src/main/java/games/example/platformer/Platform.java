package games.example.platformer;

import TransmuteCore.graphics.Color;
import TransmuteCore.graphics.Context;

/** Solid AABB platform. */
public final class Platform
{
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    public Platform(int x, int y, int width, int height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(Context ctx)
    {
        ctx.renderFilledRectangle(x, y, width, height,
            Color.toPixelInt(100, 100, 100, 255));
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }
}
