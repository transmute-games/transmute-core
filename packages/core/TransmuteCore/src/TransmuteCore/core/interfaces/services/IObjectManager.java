package TransmuteCore.core.Interfaces.Services;

import TransmuteCore.ecs.Object;

import java.util.List;

/**
 * Interface for managing game objects.
 * Provides methods for adding and removing objects.
 */
public interface IObjectManager
{
    /**
     * Adds an object to the manager.
     *
     * @param obj The object to add.
     */
    void add(Object obj);

    /**
     * Removes an object from the manager.
     *
     * @param obj The object to remove.
     */
    void remove(Object obj);

    /**
     * Gets the list of all managed objects.
     *
     * @return The object list.
     */
    List<Object> getObjects();
}
