package Meteor.Units;

/**
 * {@code Tuple2f} is a Meteor specific data unit.
 * <br>
 * A data unit that holds 2 floats.
 */
public class Tuple2f
{
    public float x, y;

    public Tuple2f(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    public Tuple2f()
    {
        setPositions(0, 0);
    }

    public void setPositions(float x, float y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString()
    {
        return "Tuple2f{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}