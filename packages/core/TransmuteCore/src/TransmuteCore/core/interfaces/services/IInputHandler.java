package TransmuteCore.core.Interfaces.Services;

import TransmuteCore.math.Tuple2i;

/**
 * Interface for handling keyboard and mouse input.
 * Provides methods for querying input state.
 */
public interface IInputHandler
{
    /**
     * Updates input state. Should be called once per frame.
     */
    void update();

    /**
     * Checks if any of the specified keys are pressed (single frame).
     *
     * @param keyCode One or more key codes to check.
     * @return True if any key is pressed.
     */
    boolean isKeyPressed(int... keyCode);

    /**
     * Checks if any of the specified keys are held down.
     *
     * @param keyCode One or more key codes to check.
     * @return True if any key is held.
     */
    boolean isKeyHeld(int... keyCode);

    /**
     * Checks if any of the specified keys were released (single frame).
     *
     * @param keyCode One or more key codes to check.
     * @return True if any key was released.
     */
    boolean isKeyReleased(int... keyCode);

    /**
     * Checks if any of the specified mouse buttons are pressed (single frame).
     *
     * @param buttonCode One or more button codes to check.
     * @return True if any button is pressed.
     */
    boolean isButtonPressed(int... buttonCode);

    /**
     * Checks if any of the specified mouse buttons are held down.
     *
     * @param buttonCode One or more button codes to check.
     * @return True if any button is held.
     */
    boolean isButtonHeld(int... buttonCode);

    /**
     * Checks if any of the specified mouse buttons were released (single frame).
     *
     * @param buttonCode One or more button codes to check.
     * @return True if any button was released.
     */
    boolean isButtonReleased(int... buttonCode);

    /**
     * Gets the current mouse position.
     *
     * @return The mouse position as a Tuple2i.
     */
    Tuple2i getMousePosition();
}
