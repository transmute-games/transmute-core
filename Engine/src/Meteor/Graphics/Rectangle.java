package Meteor.Graphics;

public class Rectangle
{
    public int x;
    public int y;
    public float width;
    public float height;

    public Rectangle()
    {
        x = 0;
        y = 0;
        width = 0;
        height = 0;
    }

    public Rectangle(int x, int y, float width, float height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setBounds(int x, int y, float width, float height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rectangle getBounds()
    {
        return new Rectangle(x, y, width, height);
    }

    /**
     * Determines whether or not this <code>Rectangle</code> and the specified
     * <code>Rectangle</code> intersect. Two rectangles intersect if
     * their intersection is nonempty.
     *
     * @param r the specified <code>Rectangle</code>
     * @return <code>true</code> if the specified <code>Rectangle</code>
     * and this <code>Rectangle</code> intersect;
     * <code>false</code> otherwise.
     */
    public boolean intersects(Rectangle r)
    {
        if (r.width <= 0 || r.height <= 0 || width <= 0 || height <= 0)
        {
            return false;
        }
        r.width += r.x;
        r.height += r.y;
        width += x;
        height += y;
        //      overflow || intersect
        return ((r.width < r.x || r.width > x) &&
                (r.height < r.y || r.height > y) &&
                (width < x || width > r.x) &&
                (height < y || height > r.y));
    }

    public int getX()
    {
        return x;
    }

    public int getY()
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
