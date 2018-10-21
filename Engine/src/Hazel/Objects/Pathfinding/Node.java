package Hazel.Objects.Pathfinding;

import Hazel.Level.TiledLevel;
import Hazel.Units.Vector2i;

/**
 * A generic Node that is used within the A* path-finding algorithm.
 */
public class Node
{
    public TiledLevel level; //The level associated with this Node.
    public Vector2i location; //The location of this Node.
    public Node parent; //The parent or previous Node.

    private double f, g, h; //The f,g and h cost

    /**
     * Defines a Node given a level, a location, the previous Node, g-cost and h-cost.
     *
     * @param level    The level associated with this Node.
     * @param location The location of this Node.
     * @param parent   The parent or previous Node.
     * @param g        The g-cost of this Node.
     * @param h        The h-cost of this Node.
     */
    public Node(TiledLevel level, Vector2i location, Node parent, double g, double h)
    {
        this.level = level;
        this.location = location;
        this.parent = parent;
        this.g = g;
        this.h = h;

        f = f(g, h);
    }

    /**
     * Defines a Node given a level, a location and previous Node.
     *
     * @param level    The level associated with this Node.
     * @param location The location of this Node.
     * @param parent   The parent or previous Node.
     */
    public Node(TiledLevel level, Vector2i location, Node parent)
    {
        this.level = level;
        this.location = location;
        this.parent = parent;

        f = f(g, h);
    }

    /**
     * @return Calculates the f-cost of the Node.
     */
    public double f()
    {
        return g + h;
    }

    /**
     * Calculates the f-cost of the Node.
     *
     * @param g The g-cost of the Node.
     * @param h The h-cost of the Node.
     * @return The f-cost of the Node.
     */
    private double f(double g, double h)
    {
        return g + h;
    }

    /**
     * @return The f-cost of the Node.
     */
    public double getF()
    {
        return f;
    }

    /**
     * @return The g-cost of the Node.
     */
    public double getG()
    {
        return g;
    }

    /**
     * @return The h-cost of the Node.
     */
    public double getH()
    {
        return h;
    }
}
