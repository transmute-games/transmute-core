package TransmuteCore.ecs.pathfinding;

import TransmuteCore.level.Tile;
import TransmuteCore.level.TiledLevel;
import TransmuteCore.math.Vector2i;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A* (pronounced as "A star") is a computer algorithm that is widely used in
 * path-finding and graph traversal, the process of plotting an efficiently
 * directed path between multiple points, called nodes.
 */
public class AStar
{
    private static final int SEARCH_AREA = 3 * 3; //The search area within the level. Represents a small portion of the level.
    private List<Node> open = new ArrayList<>(); //The list of possible Nodes that may reach the destination.
    private List<Node> closed = new ArrayList<>(); //The list of already looked at Nodes.
    private TiledLevel level; //The level associated with this search.

    /**
     * Compares two Nodes based on f-cost.
     */
    private Comparator<Node> nodeComparator = (a, b) ->
    {
        if (a.f() < b.f()) return 1;
        else if (a.f() > b.f()) return -1;
        return 0;
    };

    public AStar(TiledLevel level)
    {
        this.level = level;
    }

    /**
     * Calculates the fastest path given a starting point and a destination.
     *
     * @param start The starting location.
     * @param goal  The destination.
     * @return The fastest path from a starting point to a given destination.
     */
    public List<Node> findPath(Vector2i start, Vector2i goal)
    {
        List<Node> path = new ArrayList<>();
        Node current = new Node(level, start, null);

        open.add(current);

        while (open.size() > 0)
        {
            open.sort(nodeComparator);

            current = open.get(0);

            if (current.location.equals(goal))
            {
                while (current.parent != null)
                {
                    path.add(current);
                    current = current.parent;
                }

                open.clear();
                closed.clear();
            }

            open.remove(current);
            closed.add(current);

            sort(current, goal);
        }

        return path;
    }

    /**
     * Calculates a low-cost path given the current Node and the target.
     *
     * @param current The current Node in question.
     * @param goal    The target or destination.
     */
    private void sort(Node current, Vector2i goal)
    {
        for (int i = 0; i < SEARCH_AREA; i++)
        {
            if (i == 4) continue;

            int xi = current.location.x; //x-initial
            int yi = current.location.y; //y-initial

            int xf = (i % 3) - 1; //x-final
            int yf = (i / 3) - 1; //y-final

            int xChange = xi + xf;
            int yChange = yi + yf;

            Tile tile = current.level.getTile(xChange, yChange);
            Vector2i tileVector = new Vector2i(xChange, yChange);

            if (tile == null) continue;
            if (tile.isSolid()) continue;

            double g = current.getG() +
                    (getDistance(current.location, tileVector) == 1 ? 1 : 0.95);
            double h = getDistance(tileVector, goal);

            Node node = new Node(level, tileVector, current, g, h);

            if (contains(closed, tileVector) && g >= node.getG()) continue;
            if (!contains(open, tileVector) || g < node.getG()) open.add(node);
        }
    }

    /**
     * Calculates the distance between two vectors.
     *
     * @param start The starting point.
     * @param goal  The ending point or destination.
     * @return The distance between two vectors.
     */
    private double getDistance(Vector2i start, Vector2i goal)
    {
        double xChange = start.x - goal.x;
        double yChange = start.y - goal.y;

        return Math.sqrt(xChange * xChange + yChange * yChange);
    }

    /**
     * Checks if a given list contains a specific vector.
     *
     * @param list     The list of Nodes in question.
     * @param vector2i The specific vector to look for.
     * @return Weather or not the given list contains a specific vector.
     */
    private boolean contains(List<Node> list, Vector2i vector2i)
    {
        for (Node n : list)
        {
            if (n.location.equals(vector2i)) return true;
        }

        return false;
    }

    /**
     * @return The tiled-level being searched.
     */
    public TiledLevel getLevel()
    {
        return level;
    }
}
