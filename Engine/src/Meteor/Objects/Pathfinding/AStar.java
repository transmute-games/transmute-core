package Meteor.Objects.Pathfinding;

import Meteor.Level.Tile;
import Meteor.Level.TiledLevel;
import Meteor.Units.Vector2i;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AStar
{
    private static final int SEARCH_AREA = 3 * 3;
    private List<Node> open = new ArrayList<>();
    private  List<Node> closed = new ArrayList<>();
    private TiledLevel level;

    private Comparator<Node> nodeComparator =  (a, b) ->
    {
        if (a.f() < b.f()) return 1;
        else if (a.f() > b.f()) return -1;
        return 0;
    };

    public AStar(TiledLevel level)
    {
        this.level = level;
    }

    public List<Node> findPath(Vector2i start, Vector2i goal)
    {
        List<Node> path = new ArrayList<>();
        Node current = new Node(level, start, null);

        open.add(current);

        while(open.size() > 0)
        {
            open.sort(nodeComparator);

            current = open.get(0);

            if (current.tile.equals(goal))
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

    private void sort(Node current, Vector2i goal)
    {
        for (int i = 0; i < SEARCH_AREA; i++)
        {
            if (i == 4) continue;

            int xi = current.tile.x; //x-initial
            int yi = current.tile.y; //y-initial

            int xf = (i % 3) - 1; //x-final
            int yf = (i / 3) - 1; //y-final

            int xChange = xi + xf;
            int yChange = yi + yf;

            Tile tile = current.level.getTile(xChange, yChange);
            Vector2i tileVector = new Vector2i(xChange, yChange);

            if (tile == null) continue;
            if (tile.isSolid()) continue;

            double g = current.getG() +
                    (getDistance(current.tile, tileVector) == 1 ? 1 : 0.95);
            double h = getDistance(tileVector, goal);

            Node node = new Node(level, tileVector, current, g, h);

            if (contains(closed, tileVector) && g >= node.getG()) continue;
            if (!contains(open, tileVector) || g < node.getG()) open.add(node);
        }
    }

    private double getDistance(Vector2i start, Vector2i goal)
    {
        double xChange = start.x - goal.x;
        double yChange = start.y - goal.y;
        
        return Math.sqrt(xChange * xChange + yChange * yChange);
    }

    private boolean contains(List<Node> list, Vector2i vector2i)
    {
        for (Node n : list)
        {
            if (n.tile.equals(vector2i)) return true;
        }

        return false;
    }

    public TiledLevel getLevel()
    {
        return level;
    }
}
