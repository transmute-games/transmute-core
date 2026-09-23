package TransmuteCore.ecs;

import TransmuteCore.core.interfaces.Renderable;
import TransmuteCore.core.interfaces.Updatable;
import TransmuteCore.core.interfaces.services.IObjectManager;
import TransmuteCore.core.interfaces.services.IRenderer;
import TransmuteCore.core.Manager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * {@code ObjectManager} is a object handler class.
 * <br>
 * This class is used to handle all of the object's.
 */
public class ObjectManager implements Updatable, Renderable, IObjectManager
{
    private final List<Object> objectList = new ArrayList<>();

    /**
     * Method used to add a object to the list
     *
     * @param obj The object to add to the list
     */
    @Override
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
    @Override
    public void remove(Object obj)
    {
        if (obj == null) {
            return; // Silently ignore null removal
        }
        objectList.remove(obj);
    }

    @Override
    public List<Object> getObjects()
    {
        return Collections.unmodifiableList(objectList);
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
    public void render(Manager manager, IRenderer renderer)
    {
        if (manager == null) {
            throw new IllegalArgumentException("Manager cannot be null");
        }
        if (renderer == null) {
            throw new IllegalArgumentException("Renderer cannot be null");
        }
        for (Object obj : objectList)
        {
            if (obj != null) {
                obj.render(manager, renderer);
            }
        }
    }
}
