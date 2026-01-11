package TransmuteCore.System;

import TransmuteCore.Units.Tuple2f;
import TransmuteCore.Units.Tuple2i;
import TransmuteCore.Units.Vector2i;
import TransmuteCore.Graphics.Rectangle;

/**
 * Utility class providing common mathematical operations for game development.
 * Includes vector operations, distance calculations, and collision detection.
 */
public class MathUtils
{
    /**
     * Calculates the Euclidean distance between two points.
     *
     * @param x1 X coordinate of first point.
     * @param y1 Y coordinate of first point.
     * @param x2 X coordinate of second point.
     * @param y2 Y coordinate of second point.
     * @return The distance between the two points.
     */
    public static double distance(int x1, int y1, int x2, int y2)
    {
        int dx = x2 - x1;
        int dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Calculates the Euclidean distance between two points.
     *
     * @param x1 X coordinate of first point.
     * @param y1 Y coordinate of first point.
     * @param x2 X coordinate of second point.
     * @param y2 Y coordinate of second point.
     * @return The distance between the two points.
     */
    public static double distance(float x1, float y1, float x2, float y2)
    {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Calculates the squared distance between two points (faster than distance).
     * Useful for distance comparisons where the actual distance value isn't needed.
     *
     * @param x1 X coordinate of first point.
     * @param y1 Y coordinate of first point.
     * @param x2 X coordinate of second point.
     * @param y2 Y coordinate of second point.
     * @return The squared distance between the two points.
     */
    public static int distanceSquared(int x1, int y1, int x2, int y2)
    {
        int dx = x2 - x1;
        int dy = y2 - y1;
        return dx * dx + dy * dy;
    }

    /**
     * Calculates the Manhattan distance between two points.
     *
     * @param x1 X coordinate of first point.
     * @param y1 Y coordinate of first point.
     * @param x2 X coordinate of second point.
     * @param y2 Y coordinate of second point.
     * @return The Manhattan distance.
     */
    public static int manhattanDistance(int x1, int y1, int x2, int y2)
    {
        return Math.abs(x2 - x1) + Math.abs(y2 - y1);
    }

    /**
     * Normalizes a 2D vector.
     *
     * @param x X component of the vector.
     * @param y Y component of the vector.
     * @return Normalized vector as Tuple2f.
     */
    public static Tuple2f normalize(float x, float y)
    {
        double length = Math.sqrt(x * x + y * y);
        if (length == 0) return new Tuple2f(0, 0);
        return new Tuple2f((float) (x / length), (float) (y / length));
    }

    /**
     * Calculates the dot product of two 2D vectors.
     *
     * @param x1 X component of first vector.
     * @param y1 Y component of first vector.
     * @param x2 X component of second vector.
     * @param y2 Y component of second vector.
     * @return The dot product.
     */
    public static float dotProduct(float x1, float y1, float x2, float y2)
    {
        return x1 * x2 + y1 * y2;
    }

    /**
     * Calculates the magnitude (length) of a 2D vector.
     *
     * @param x X component of the vector.
     * @param y Y component of the vector.
     * @return The magnitude of the vector.
     */
    public static double magnitude(float x, float y)
    {
        return Math.sqrt(x * x + y * y);
    }

    /**
     * Linearly interpolates between two values.
     *
     * @param start Starting value.
     * @param end   Ending value.
     * @param t     Interpolation factor (0.0 = start, 1.0 = end).
     * @return Interpolated value.
     */
    public static float lerp(float start, float end, float t)
    {
        return start + (end - start) * t;
    }

    /**
     * Clamps a value between a minimum and maximum.
     *
     * @param value The value to clamp.
     * @param min   Minimum value.
     * @param max   Maximum value.
     * @return Clamped value.
     */
    public static int clamp(int value, int min, int max)
    {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    /**
     * Clamps a value between a minimum and maximum.
     *
     * @param value The value to clamp.
     * @param min   Minimum value.
     * @param max   Maximum value.
     * @return Clamped value.
     */
    public static float clamp(float value, float min, float max)
    {
        if (value < min) return min;
        if (value > max) return max;
        return value;
    }

    /**
     * Checks if two rectangles overlap (AABB collision detection).
     *
     * @param x1 X position of first rectangle.
     * @param y1 Y position of first rectangle.
     * @param w1 Width of first rectangle.
     * @param h1 Height of first rectangle.
     * @param x2 X position of second rectangle.
     * @param y2 Y position of second rectangle.
     * @param w2 Width of second rectangle.
     * @param h2 Height of second rectangle.
     * @return True if the rectangles overlap.
     */
    public static boolean rectanglesOverlap(int x1, int y1, int w1, int h1, 
                                           int x2, int y2, int w2, int h2)
    {
        return x1 < x2 + w2 && x1 + w1 > x2 && y1 < y2 + h2 && y1 + h1 > y2;
    }

    /**
     * Checks if two rectangles overlap (AABB collision detection).
     *
     * @param rect1 First rectangle.
     * @param rect2 Second rectangle.
     * @return True if the rectangles overlap.
     */
    public static boolean rectanglesOverlap(Rectangle rect1, Rectangle rect2)
    {
        return rectanglesOverlap(
            (int) rect1.getX(), (int) rect1.getY(), (int) rect1.getWidth(), (int) rect1.getHeight(),
            (int) rect2.getX(), (int) rect2.getY(), (int) rect2.getWidth(), (int) rect2.getHeight()
        );
    }

    /**
     * Checks if a point is inside a rectangle.
     *
     * @param px X position of the point.
     * @param py Y position of the point.
     * @param rx X position of the rectangle.
     * @param ry Y position of the rectangle.
     * @param rw Width of the rectangle.
     * @param rh Height of the rectangle.
     * @return True if the point is inside the rectangle.
     */
    public static boolean pointInRectangle(int px, int py, int rx, int ry, int rw, int rh)
    {
        return px >= rx && px <= rx + rw && py >= ry && py <= ry + rh;
    }

    /**
     * Checks if a point is inside a circle.
     *
     * @param px     X position of the point.
     * @param py     Y position of the point.
     * @param cx     X position of the circle center.
     * @param cy     Y position of the circle center.
     * @param radius Radius of the circle.
     * @return True if the point is inside the circle.
     */
    public static boolean pointInCircle(int px, int py, int cx, int cy, int radius)
    {
        return distanceSquared(px, py, cx, cy) <= radius * radius;
    }

    /**
     * Checks if two circles overlap.
     *
     * @param x1      X position of first circle.
     * @param y1      Y position of first circle.
     * @param radius1 Radius of first circle.
     * @param x2      X position of second circle.
     * @param y2      Y position of second circle.
     * @param radius2 Radius of second circle.
     * @return True if the circles overlap.
     */
    public static boolean circlesOverlap(int x1, int y1, int radius1, int x2, int y2, int radius2)
    {
        int combinedRadius = radius1 + radius2;
        return distanceSquared(x1, y1, x2, y2) <= combinedRadius * combinedRadius;
    }

    /**
     * Calculates the angle in radians between two points.
     *
     * @param x1 X coordinate of first point.
     * @param y1 Y coordinate of first point.
     * @param x2 X coordinate of second point.
     * @param y2 Y coordinate of second point.
     * @return Angle in radians.
     */
    public static double angleBetween(float x1, float y1, float x2, float y2)
    {
        return Math.atan2(y2 - y1, x2 - x1);
    }

    /**
     * Converts degrees to radians.
     *
     * @param degrees Angle in degrees.
     * @return Angle in radians.
     */
    public static double toRadians(double degrees)
    {
        return degrees * Math.PI / 180.0;
    }

    /**
     * Converts radians to degrees.
     *
     * @param radians Angle in radians.
     * @return Angle in degrees.
     */
    public static double toDegrees(double radians)
    {
        return radians * 180.0 / Math.PI;
    }

    private MathUtils()
    {
    }
}
