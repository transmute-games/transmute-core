package games.example.platformer;

import TransmuteCore.graphics.Color;
import TransmuteCore.graphics.Context;
import TransmuteCore.physics.Body2D;

/** Solid AABB platform. */
public final class Platform implements Body2D.Solid
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

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public float getWidth()
    {
        return width;
    }

    public float getHeight()
    {
        return height;
    }
}
