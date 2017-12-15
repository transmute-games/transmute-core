package Meteor.Graphics;

import Meteor.Graphics.Sprites.Sprite;
import Meteor.Units.Tuple2i;

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

    public void setBounds(int x, int y, float width, float height, float scale)
    {
        this.x = x;
        this.y = y;
        this.width = width * scale;
        this.height = height * scale;
    }

    public void setBounds(Sprite sprite, Tuple2i location)
    {
        Rectangle bounds = sprite.getBounds();

        this.x = location.x + bounds.x;
        this.y = location.y + bounds.y;
        this.width = bounds.width;
        this.height = bounds.height;
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

    public void setX(int x)
    {
        this.x = x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public float getWidth()
    {
        return width;
    }

    public float getHeight()
    {
        return height;
    }

    public void setWidth(float width)
    {
        this.width = width;
    }

    public void setHeight(float height)
    {
        this.height = height;
    }

    @Override
    public String toString()
    {
        return "Rectangle{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
