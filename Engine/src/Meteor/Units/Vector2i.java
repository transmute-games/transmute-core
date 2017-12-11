package Meteor.Units;

/**
 * {@code Vector2i} is a Meteor specific data unit.
 * <br>
 * A data unit that holds 2 integers.
 */
public class Vector2i
{
    public int x; //x-position on the screen
    public int y; //y-position on the screen

    /**
     * The constructor used to define a {@code vector2i}.
     *
     * @param x The x-position on the screen.
     * @param y The y-position on the screen.
     */
    public Vector2i(int x, int y)
    {
        setPositions(x, y);
    }

    /**
     * The constructor used to define a {@code vector2i}.
     */
    public Vector2i()
    {
        setPositions(0, 0);
    }

    /**
     * Calculates the distance between two vectors, 'tile' and 'goal'.
     *
     * @param start The starting vector.
     * @param goal  The ending vector.
     * @return The distance between 'tile' and 'goal'.
     */
    public static double getDistance(Vector2i start, Vector2i goal)
    {
        int dx = start.x - goal.x;
        int dy = start.y - goal.y;

        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Method used to add two vectors together.
     *
     * @param tempVector The vector to add from.
     * @return The sum of two vectors.
     */
    public Vector2i add(Vector2i tempVector)
    {
        x += tempVector.x;
        y += tempVector.y;
        return this;
    }

    /**
     * Method used to carry out scalar addition on a vector.
     *
     * @param tempValue The value to add from.
     * @return The output of the scalar addition.
     */
    public Vector2i add(int tempValue)
    {
        x += tempValue;
        y += tempValue;
        return this;
    }

    /**
     * Method used to subtract two vectors from each other.
     *
     * @param tempVector The vector to subtract from.
     * @return The difference between two vectors.
     */
    public Vector2i subtract(Vector2i tempVector)
    {
        x -= tempVector.x;
        y -= tempVector.y;
        return this;
    }

    /**
     * Method used to carry out scalar subtraction on a vector.
     *
     * @param tempValue The value to subtract from.
     * @return The output of the scalar subtraction.
     */
    public Vector2i subtract(int tempValue)
    {
        x -= tempValue;
        y -= tempValue;
        return this;
    }

    /**
     * Method used to multiply two vector's together.
     *
     * @param tempVector The vector to multiply from.
     * @return The output of the multiplication.
     */
    public Vector2i multiply(Vector2i tempVector)
    {
        x *= tempVector.x;
        y *= tempVector.y;
        return this;
    }

    /**
     * Method used to carry out scalar multiplication on a vector.
     *
     * @param tempValue The value to multiply by.
     * @return The output of the scalar multiplication.
     */
    public Vector2i multiply(int tempValue)
    {
        x *= tempValue;
        y *= tempValue;
        return this;
    }

    /**
     * Method used to divide two vector's.
     *
     * @param tempVector The vector to divide from.
     * @return The output of the division.
     */
    public Vector2i divide(Vector2i tempVector)
    {
        x /= tempVector.x;
        y /= tempVector.y;
        return this;
    }

    /**
     * Method used to carry out scalar division on a vector.
     *
     * @param tempValue The value to divide by.
     * @return The output of the scalar division.
     */
    public Vector2i divide(int tempValue)
    {
        x /= tempValue;
        y /= tempValue;
        return this;
    }

    /**
     * @return The absolute value of the voect.
     */
    public Vector2i absoluteValue()
    {
        x = Math.abs(x);
        y = Math.abs(y);
        return this;
    }

    /**
     * @return The length of a vector.
     */
    private int length()
    {
        return (int) Math.sqrt((x * x) + (y * y));
    }

    /**
     * @return The max value of the vector.
     */
    public int max()
    {
        return Math.max(x, y);
    }

    /**
     * @return The zeroed (0, 0) vector.
     */
    public Vector2i zero()
    {
        return new Vector2i(0, 0);
    }

    /**
     * @return The normalized vector.
     */
    public Vector2i normalize()
    {
        return new Vector2i(x / length(), y / length());
    }

    /**
     * @return A copy of a vector.
     */
    public Vector2i copy()
    {
        return new Vector2i(x, y);
    }

    /**
     * Method used to determine if a vector is equal to another vector.
     *
     * @param tempVector The vector to compare.
     * @return The output of the comparison.
     */
    public boolean equals(Vector2i tempVector)
    {
        return x == tempVector.getX() && y == tempVector.getY();
    }

    /**
     * Method used to determine if a vector is equal to another vector.
     *
     * @param object The object to compare.
     * @return The output of the comparison.
     */
    @Override
    public boolean equals(Object object)
    {
        if (!(object instanceof Vector2i)) return false;
        if (((Vector2i) object).x == x && ((Vector2i) object).y == y) return true;
        return false;
    }

    /**
     * Method used to change the pre-existing x and y positions.
     *
     * @param x The x-position on the screen.
     * @param y The y-position on the screen.
     */
    public void setPositions(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * @return The x-position on the screen.
     */
    private int getX()
    {
        return x;
    }

    /**
     * Method used to set the x-position on the screen.
     *
     * @param x The x-position on the screen.
     */
    void setX(int x)
    {
        this.x = x;
    }

    /**
     * @return The y-position on the screen.
     */
    private int getY()
    {
        return y;
    }

    /**
     * Method used to set the y-position on the screen.
     *
     * @param y The y-position on the screen.
     */
    void setY(int y)
    {
        this.y = y;
    }

    @Override
    public String toString()
    {
        return String.format("(%d, %d)", (int) x, (int) y);
    }
}
