package Meteor.Graphics;

import Meteor.Graphics.Sprites.Sprite;
import Meteor.Units.Tuple2i;

/**
 * {@code Rectangle} is a generic representation of a rectangular shape.
 * <br>
 * This class is a generic representation of a rectangle.
 */
public class Rectangle
{
    public int x; //The x-location of the rectangle
    public int y; //The y-location of the rectangle
    public float width; //The width of the rectangle
    public float height; //The height of the rectangle

    /**
     * Default constructor
     */
    public Rectangle()
    {
        x = 0;
        y = 0;
        width = 0;
        height = 0;
    }

    /**
     * Defines a rectangle object.
     *
     * @param x      The x-location of the rectangle
     * @param y      The y-location of the rectangle
     * @param width  The width of the rectangle.
     * @param height The height of the rectangle.
     */
    public Rectangle(int x, int y, float width, float height)
    {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Method used to update the properties of the rectangle (e.g.
     * the x-location of the rectangle.).
     *
     * @param x      The x-location of the rectangle
     * @param y      The y-location of the rectangle
     * @param width  The width of the rectangle.
     * @param height The height of the rectangle.
     * @param scale  The scaling ratio (1f is 1:1 ratio).
     */
    public void setBounds(int x, int y, float width, float height, float scale)
    {
        this.x = x;
        this.y = y;
        this.width = width * scale;
        this.height = height * scale;
    }

    /**
     * Method used to update the properties of the rectangle (e.g.
     * the x-location of the rectangle.).
     *
     * @param sprite   The sprite object containing the precise collision-bounds (rectangle).
     * @param location The location of the object (e.g. x, y).
     */
    public void setBounds(Sprite sprite, Tuple2i location)
    {
        if (sprite.getBounds() == null) return;

        Rectangle bounds = sprite.getBounds();

        this.x = location.x + bounds.x;
        this.y = location.y + bounds.y;
        this.width = bounds.width;
        this.height = bounds.height;
    }

    /**
     * @return A rectangle object given this object's x, y, width and height
     */
    public Rectangle getBounds()
    {
        return new Rectangle(x, y, width, height);
    }

    /**
     * Determines whether or not this <code>Rectangle</code> and the specified
     * <code>Rectangle</code> intersect. Two rectangles intersect if
     * their intersection is non-empty.
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

    /**
     * @return The x-location of the rectangle.
     */
    public int getX()
    {
        return x;
    }

    /**
     * @return The y-location of the rectangle.
     */
    public int getY()
    {
        return y;
    }

    /**
     * Sets the x-location of the rectangle.
     *
     * @param x The x-location of the rectangle.
     */
    public void setX(int x)
    {
        this.x = x;
    }

    /**
     * Sets the y-location of the rectangle.
     *
     * @param y The y-location of the rectangle.
     */
    public void setY(int y)
    {
        this.y = y;
    }

    /**
     * @return The width of the rectangle.
     */
    public float getWidth()
    {
        return width;
    }

    /**
     * @return The height of the rectangle.
     */
    public float getHeight()
    {
        return height;
    }

    /**
     * Sets the width of the rectangle.
     *
     * @param width The width of the rectangle.
     */
    public void setWidth(float width)
    {
        this.width = width;
    }

    /**
     * Sets the height of the rectangle.
     *
     * @param height The height of the rectangle.
     */
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