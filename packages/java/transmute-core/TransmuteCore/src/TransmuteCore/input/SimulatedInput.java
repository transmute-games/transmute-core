package TransmuteCore.input;

import TransmuteCore.core.interfaces.services.IInputHandler;
import TransmuteCore.math.Tuple2i;

import java.util.HashSet;
import java.util.Set;

/**
 * Scriptable input adapter for headless agent / CI playthroughs.
 * Press/hold/release keys between {@link TransmuteCore.util.verify.GameHarness} steps.
 */
public final class SimulatedInput implements IInputHandler
{
    private final Set<Integer> held = new HashSet<>();
    private final Set<Integer> pressedThisFrame = new HashSet<>();
    private final Set<Integer> releasedThisFrame = new HashSet<>();
    private final Set<Integer> buttonsHeld = new HashSet<>();
    private final Set<Integer> buttonsPressed = new HashSet<>();
    private final Set<Integer> buttonsReleased = new HashSet<>();
    private final Tuple2i mouse = new Tuple2i(0, 0);

    /** Hold a key until {@link #releaseKey(int)} or {@link #clear()}. */
    public void holdKey(int keyCode)
    {
        if (held.add(keyCode))
        {
            pressedThisFrame.add(keyCode);
        }
    }

    /** Single-frame press (also held until release). */
    public void pressKey(int keyCode)
    {
        holdKey(keyCode);
    }

    public void releaseKey(int keyCode)
    {
        if (held.remove(keyCode))
        {
            releasedThisFrame.add(keyCode);
        }
    }

    public void setMouse(int x, int y)
    {
        mouse.x = x;
        mouse.y = y;
    }

    public void clear()
    {
        held.clear();
        pressedThisFrame.clear();
        releasedThisFrame.clear();
        buttonsHeld.clear();
        buttonsPressed.clear();
        buttonsReleased.clear();
    }

    @Override
    public void update()
    {
        pressedThisFrame.clear();
        releasedThisFrame.clear();
        buttonsPressed.clear();
        buttonsReleased.clear();
    }

    @Override
    public boolean isKeyPressed(int... keyCode)
    {
        return any(pressedThisFrame, keyCode);
    }

    @Override
    public boolean isKeyHeld(int... keyCode)
    {
        return any(held, keyCode);
    }

    @Override
    public boolean isKeyReleased(int... keyCode)
    {
        return any(releasedThisFrame, keyCode);
    }

    @Override
    public boolean isButtonPressed(int... buttonCode)
    {
        return any(buttonsPressed, buttonCode);
    }

    @Override
    public boolean isButtonHeld(int... buttonCode)
    {
        return any(buttonsHeld, buttonCode);
    }

    @Override
    public boolean isButtonReleased(int... buttonCode)
    {
        return any(buttonsReleased, buttonCode);
    }

    @Override
    public Tuple2i getMousePosition()
    {
        return mouse;
    }

    private static boolean any(Set<Integer> set, int... codes)
    {
        if (codes == null)
        {
            return false;
        }
        for (int code : codes)
        {
            if (set.contains(code))
            {
                return true;
            }
        }
        return false;
    }
}
