package TransmuteCore.Objects;

import TransmuteCore.GameEngine.Interfaces.Renderable;
import TransmuteCore.GameEngine.Interfaces.Updatable;
import TransmuteCore.GameEngine.Manager;
import TransmuteCore.Graphics.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code ObjectManager} is a object handler class.
 * <br>
 * This class is used to handle all of the object's.
 */
public class ObjectManager implements Updatable, Renderable
{
    public List<Object> objectList = new ArrayList<>(); //The list of objects

    /**
     * Method used to add a object to the list
     *
     * @param obj The object to add to the list
     */
    public void add(Object obj)
    {
        if (obj == null) {
            throw new IllegalArgumentException("Cannot add null object to ObjectManager");
        }
        objectList.add(obj);
    }

    /**
     * Method used to remove an object from the list
     *
     * @param obj The object to remove from the list
     */
    public void remove(Object obj)
    {
        if (obj == null) {
            return; // Silently ignore null removal
        }
        objectList.remove(obj);
    }

    @Override
    public void update(Manager manager, double delta)
    {
        if (manager == null) {
            throw new IllegalArgumentException("Manager cannot be null");
        }
        for (Object obj : objectList)
        {
            if (obj != null) {
                obj.update(manager, delta);
            }
        }
    }

    @Override
    public void render(Manager manager, Context ctx)
    {
        if (manager == null) {
            throw new IllegalArgumentException("Manager cannot be null");
        }
        if (ctx == null) {
            throw new IllegalArgumentException("Context cannot be null");
        }
        for (Object obj : objectList)
        {
            if (obj != null) {
                obj.render(manager, ctx);
            }
        }
    }
}
