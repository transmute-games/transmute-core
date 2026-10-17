package TransmuteCore.graphics;

/**
 * Simple 2D camera offset for scrolling worlds.
 */
public final class Camera
{
    private float x;
    private float y;
    private final int viewWidth;
    private final int viewHeight;

    public Camera(int viewWidth, int viewHeight)
    {
        if (viewWidth <= 0 || viewHeight <= 0)
        {
            throw new IllegalArgumentException("view size must be positive");
        }
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
    }

    public void lookAt(float worldX, float worldY)
    {
        this.x = worldX - viewWidth / 2f;
        this.y = worldY - viewHeight / 2f;
    }

    /**
     * Clamps the camera so the view stays inside a world rectangle of size
     * {@code worldWidth} x {@code worldHeight}.
     */
    public void clampToWorld(float worldWidth, float worldHeight)
    {
        if (worldWidth > viewWidth)
        {
            x = Math.max(0, Math.min(x, worldWidth - viewWidth));
        }
        else
        {
            x = 0;
        }
        if (worldHeight > viewHeight)
        {
            y = Math.max(0, Math.min(y, worldHeight - viewHeight));
        }
        else
        {
            y = 0;
        }
    }

    public int worldToScreenX(float worldX)
    {
        return Math.round(worldX - x);
    }

    public int worldToScreenY(float worldY)
    {
        return Math.round(worldY - y);
    }

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }
}
