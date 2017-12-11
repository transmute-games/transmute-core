package Meteor.Objects.Pathfinding;

import Meteor.Level.TiledLevel;
import Meteor.Units.Vector2i;

public class Node
{
    public TiledLevel level;
    public Vector2i tile;
    public Node parent;

    private double f, g, h;

    public Node(TiledLevel level, Vector2i tile, Node parent, double g, double h)
    {
        this.level = level;
        this.tile = tile;
        this.parent = parent;
        this.g = g;
        this.h = h;

        f = f(g, h);
    }

    public Node(TiledLevel level, Vector2i tile, Node parent)
    {
        this.level = level;
        this.tile = tile;
        this.parent = parent;

        f = f(g, h);
    }

    public double f()
    {
        return g + h;
    }

    private double f(double g, double h)
    {
        return g + h;
    }

    public double getF()
    {
        return f;
    }

    public double getG()
    {
        return g;
    }

    public double getH()
    {
        return h;
    }
}
