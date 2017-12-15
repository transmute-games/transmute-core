package Meteor.Units;

/**
 * {@code Tuple4f} is a Meteor specific data unit.
 * <br>
 * A data unit that holds 4 floats.
 */
public class Tuple4f
{
    public Tuple2f location;
    public float width, height;

    public Tuple4f(float x, float y, float width, float height)
    {
        this.location = new Tuple2f(x, y);
        this.width = width;
        this.height = height;
    }

    public Tuple4i asTuple4i()
    {
        return new Tuple4i((int) location.x, (int) location.y, (int) width, (int) height);
    }

    public float getWidth()
    {
        return width;
    }

    public float getHeight()
    {
        return height;
    }

    public float getX()
    {
        return location.x;
    }

    public float getY()
    {
        return location.y;
    }

    @Override
    public String toString()
    {
        return "Tuple4f{" +
                "location=" + location +
                ", width=" + width +
                ", height=" + height +
                '}';
    }
}
