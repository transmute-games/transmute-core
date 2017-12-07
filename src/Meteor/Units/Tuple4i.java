package Meteor.Units;

/**
 * {@code Tuple4i} is a Meteor specific data unit.
 * <br>
 * A data unit that holds 4 integers.
 */
public class Tuple4i
{
    public Vector2i location; //The location (x, y) on the screen
    public int width; //width of a object
    public int height; //height of a object

    /**
     * The constructor used to define a {@code Tuple4i}.
     *
     * @param x      The x-position of the item.
     * @param y      The y-position of the item.
     * @param width  The width of a item.
     * @param height The height of a item.
     */
    public Tuple4i(int x, int y, int width, int height)
    {
        setPositions(x, y, width, height);
    }

    /**
     * The constructor used to define a {@code Tuple4i}.
     */
    public Tuple4i()
    {
        setPositions(0, 0, 0, 0);
    }

    /**
     * Method used to change the pre-existing x and y locations as well as the item's width and height.
     *
     * @param x      The x-position of the item.
     * @param y      The y-position of the item.
     * @param width  The width of a item.
     * @param height The height of a item.
     */
    public void setPositions(int x, int y, int width, int height)
    {
        location = new Vector2i(x, y);
        this.width = width;
        this.height = height;
    }

    /**
     * @return The x-location on the screen.
     */
    public int getX()
    {
        return location.x;
    }

    /**
     * Method used to set the x-location on the screen.
     *
     * @param x The x-location on the screen.
     */
    public void setX(int x)
    {
        this.location.setX(x);
    }

    /**
     * @return The y-location on the screen.
     */
    public int getY()
    {
        return location.y;
    }

    /**
     * Method used to set the y-location on the screen.
     *
     * @param y The y-location on the screen.
     */
    public void setY(int y)
    {
        this.location.setY(y);
    }

    /**
     * @return The width of a item.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Method used to set the width of a item.
     *
     * @param width The width of a item
     */
    public void setWidth(int width)
    {
        this.width = width;
    }

    /**
     * @return The height of a item.
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * @return The tuple containing the (x, y) location.
     */
    public Vector2i getLocation()
    {
        return location;
    }

    /**
     * Method used to set the height of a item.
     *
     * @param height The height of a item.
     */
    public void setHeight(int height)
    {
        this.height = height;
    }

    @Override
    public String toString()
    {
        return String.format("(%d, %d), width: %d, height: %d", location.x, location.y, width, height);
    }
}
