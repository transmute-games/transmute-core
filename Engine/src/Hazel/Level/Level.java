package Hazel.Level;

import java.util.ArrayList;
import java.util.List;

import Hazel.GameEngine.Manager;
import Hazel.GameEngine.Interfaces.Renderable;
import Hazel.GameEngine.Interfaces.Updatable;
import Hazel.Graphics.Context;
import Hazel.Objects.Object;
import Hazel.Objects.ObjectManager;
import Hazel.Objects.Type.Mob;

/**
 * {@code Level} is a generic level class.
 * <br>
 * This class is a generic representation of a level.
 */
public abstract class Level implements Updatable, Renderable
{
    protected int width, height; //The width and height of the level
    protected int xOffset, yOffset; //The x and y offsets
    protected ObjectManager objManager = new ObjectManager(); //The object handler

    /**
     * The constructor used to initialize a level given a width and a height.
     *
     * @param width  The width of the level (e.g. Level.IMAGE).
     * @param height The height of the level.
     */
    protected Level(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    /**
     * The constructor used to initialize a level given a file-path.
     *
     * @param filePath The path to the level file.
     */
    protected Level(String filePath)
    {
        load(filePath);
    }

    /**
     * This method will be implemented by concrete classes to load a level.
     *
     * @param filePath The path to the level file.
     */
    protected abstract void load(String filePath);

    /**
     * Method that MUST be called {@code super.update(manager, delta);} in order for the collision bounds to be updated.
     *
     * @param manager The engine manager object.
     * @param delta   Time elapsed between each frame.
     */
    @Override
    public void update(Manager manager, double delta)
    {
        objManager.update(manager, delta);
    }

    /**
     * Method that MUST be called {@code super.render(manager, ctx);} in order for the collision bounds to be rendered.
     *
     * @param manager The engine manager object.
     * @param ctx     The Game render 'canvas'.
     */
    @Override
    public void render(Manager manager, Context ctx)
    {
        objManager.render(manager, ctx);
    }

    /**
     * @return The width of the level.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * @return The height of the level.
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Method used to add a object to a level.
     *
     * @param obj The object to add to the level.
     */
    public void add(Object obj)
    {
        obj.init(this);
        objManager.add(obj);
    }

    /**
     * Method used to remove a object from a level.
     *
     * @param obj The object to remove from a level.
     */
    public void remove(Object obj)
    {
        obj.remove();
        objManager.remove(obj);
    }

    /**
     * Method grabs a list of mobs in a given radius.
     *
     * @param m      The central mob.
     * @param radius The distance from the given mob to the edge of the circle.
     * @return The list of mobs in a given radius.
     */
    public synchronized List<Mob> getMobs(Mob m, int radius)
    {
        List<Mob> result = new ArrayList<>();
        int mx = m.getX();
        int my = m.getY();

        for (int i = 0; i < objManager.objectList.size(); i++)
        {
            Object object = objManager.objectList.get(i);

            if (object.equals(m) || object == null) continue;

            if (object instanceof Mob)
            {
                Mob mob = (Mob) object;
                int x = mob.getX();
                int y = mob.getY();
                int dx = Math.abs(x - mx);
                int dy = Math.abs(y - my);
                double distance = Math.sqrt((dx * dx) + (dy * dy));
                if (distance <= radius) result.add(mob);
            }
        }

        return result;
    }

    /**
     * Method grabs a list of objects in a given radius.
     *
     * @param obj    The central object.
     * @param radius The distance from the given object to the edge of the circle.
     * @return The list of objects in a given radius.
     */
    public synchronized List<Object> getObjects(Object obj, int radius)
    {
        List<Object> result = new ArrayList<>();
        int ox = obj.getX();
        int oy = obj.getY();

        for (int i = 0; i < objManager.objectList.size(); i++)
        {
            Object object = objManager.objectList.get(i);

            if (object.equals(obj) || object instanceof Mob || object == null) continue;


            int x = object.getX();
            int y = object.getY();
            int dx = Math.abs(x - ox);
            int dy = Math.abs(y - oy);
            double distance = Math.sqrt((dx * dx) + (dy * dy));
            if (distance <= radius) result.add(object);
        }

        return result;
    }

    /**
     * Method used to set the x and y offsets.
     *
     * @param xOffset The x-offset value.
     * @param yOffset The y-offset value.
     */
    public void setOffset(int xOffset, int yOffset)
    {
        this.xOffset = xOffset;
        this.yOffset = yOffset;
    }
}
