package TransmuteCore.math;

/**
 * {@code Dimension2f} is a TransmuteCore specific data unit.
 * <br>
 * A data unit that holds 2 floats.
 */
public class Dimension2f
{
    public float width, height;

    public Dimension2f(float x, float y)
    {
        this.width = x;
        this.height = y;
    }

    public Dimension2f()
    {
        setPositions(0, 0);
    }

    public void setPositions(float x, float y)
    {
        this.width = x;
        this.height = y;
    }

    public float getWidth()
    {
        return width;
    }

    public float getHeight()
    {
        return height;
    }

    @Override
    public String toString()
    {
        return "Dimension2f{" +
                "width=" + width +
                ", height=" + height +
                '}';
    }
}
