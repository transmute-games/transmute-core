package Meteor.Units;

/**
 * {@code Tuple4f} is a Meteor specific data unit.
 * <br>
 * A data unit that holds 4 floats.
 */
public class Tuple4f
{
    public Tuple2f location;
    public float w, h;

    public Tuple4f(float x, float y, float w, float h)
    {
        this.location = new Tuple2f(x, y);
        this.w = w;
        this.h = h;
    }

    public Tuple4i asTuple4i()
    {
        return new Tuple4i((int) location.x, (int) location.y, (int) w, (int) h);
    }

    @Override
    public String toString()
    {
        return String.format("(%d, %d), width: %d, height: %d", location.x, location.y, w, w);
    }
}
