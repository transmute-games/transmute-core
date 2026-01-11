package TransmuteCore.input;

import java.awt.Canvas;
import java.awt.event.*;
import java.util.HashMap;

import TransmuteCore.core.interfaces.services.IInputHandler;
import TransmuteCore.core.TransmuteCore;
import TransmuteCore.math.Tuple2i;

/**
 * {@code Input} is a input handler class.
 * <br>
 * This class should be used to handle both mouse and keyboard input.
 */
public class Input implements KeyListener, MouseListener, MouseMotionListener, IInputHandler
{
    private final HashMap<Integer, Property> keyMap = new HashMap<>(); //The hash table of keys
    private final HashMap<Integer, Property> mouseMap = new HashMap<>(); //The hash table of mouse buttons

    private Tuple2i mousePosition = new Tuple2i(0, 0); //The x and y positions of the mouse
    private int scale; //The window scale factor for mouse position scaling

    /**
     * {@code Property} is a property attached to a kind of input.
     * <br>
     * This class is used to indicated weather or not a certain button or key is pressed or not.
     */
    private class Property
    {
        boolean isPressed = false; //boolean checks if button/key is pressed
        boolean isReleased = false; //boolean checks if button/key was just released
    }

    /**
     * Constructs an Input handler with the specified canvas and scale factor.
     *
     * @param canvas The canvas to attach input listeners to.
     * @param scale  The window scale factor for mouse position scaling.
     */
    public Input(Canvas canvas, int scale)
    {
        if (canvas == null)
        {
            throw new IllegalArgumentException("Canvas cannot be null");
        }
        if (scale <= 0)
        {
            throw new IllegalArgumentException("Scale must be positive");
        }
        this.scale = scale;
        canvas.addKeyListener(this);
        canvas.addMouseListener(this);
        canvas.addMouseMotionListener(this);
    }

    /**
     * Method used to correlate between keys pressed and keys released.
     */
    @Override
    public void update()
    {
        for (Integer keyCode : keyMap.keySet())
        {
            Property property = keyMap.get(keyCode);
            property.isReleased = property.isPressed;
        }

        for (Integer keyCode : mouseMap.keySet())
        {
            Property property = mouseMap.get(keyCode);
            property.isReleased = property.isPressed;
        }
    }

    /**
     * Method used to check if a key is pressed.
     *
     * @param keyCode The key in question.
     * @return If a specific key is pressed.
     */
    @Override
    public boolean isKeyPressed(int... keyCode)
    {
        for (int key : keyCode)
        {
            Property property = keyMap.get(key);
            boolean isPressed = property != null && !property.isReleased && property.isPressed;
            if (isPressed) return true;
        }
        return false;
    }

    /**
     * Method used to check if a key is held.
     *
     * @param keyCode The key in question.
     * @return If a specific key is held.
     */
    @Override
    public boolean isKeyHeld(int... keyCode)
    {
        for (int key : keyCode)
        {
            Property property = keyMap.get(key);
            boolean isHeld = property != null && property.isPressed && property.isReleased;
            if (isHeld) return true;
        }
        return false;
    }

    /**
     * Method used to check if a key was released.
     *
     * @param keyCode The key in question.
     * @return If a specific key was released.
     */
    @Override
    public boolean isKeyReleased(int... keyCode)
    {
        for (int key : keyCode)
        {
            Property property = keyMap.get(key);
            boolean isReleased = property != null && property.isReleased && !property.isPressed;
            if (isReleased) return true;
        }
        return false;
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        int key = e.getKeyCode();

        if (keyMap.get(key) == null) keyMap.put(e.getKeyCode(), new Property());

        Property property = keyMap.get(key);
        property.isPressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        int key = e.getKeyCode();

        if (keyMap.get(key) == null) keyMap.put(e.getKeyCode(), new Property());

        Property property = keyMap.get(key);
        property.isPressed = false;
    }

    /**
     * Method used to check if a mouse button is pressed.
     *
     * @param buttonCode The mouse button in question.
     * @return If a specific mouse button is pressed.
     */
    @Override
    public boolean isButtonPressed(int... buttonCode)
    {
        for (int key : buttonCode)
        {
            Property property = mouseMap.get(key);
            boolean isPressed = property != null && !property.isReleased && property.isPressed;
            if (isPressed) return true;
        }
        return false;
    }

    /**
     * Method used to check if a mouse button is held.
     *
     * @param buttonCode The mouse button in question.
     * @return If a specific mouse button is held.
     */
    @Override
    public boolean isButtonHeld(int... buttonCode)
    {
        for (int key : buttonCode)
        {
            Property property = mouseMap.get(key);
            boolean isHeld = property != null && property.isPressed && property.isReleased;
            if (isHeld) return true;
        }
        return false;
    }

    /**
     * Method used to check if a mouse button was just released.
     *
     * @param buttonCode The mouse button in question.
     * @return If a specific mouse button was just released.
     */
    @Override
    public boolean isButtonReleased(int... buttonCode)
    {
        for (int key : buttonCode)
        {
            Property property = mouseMap.get(key);
            boolean isReleased = property != null && property.isReleased && !property.isPressed;
            if (isReleased) return true;
        }
        return false;
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        mousePosition.setPositions(e.getX() / scale, e.getY() / scale);
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        mousePosition.setPositions(e.getX() / scale, e.getY() / scale);
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        int button = e.getButton();

        if (mouseMap.get(button) == null) mouseMap.put(e.getButton(), new Property());

        Property property = mouseMap.get(button);
        property.isPressed = true;
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        int button = e.getButton();

        if (mouseMap.get(button) == null) mouseMap.put(e.getButton(), new Property());

        Property property = mouseMap.get(button);
        property.isReleased = true;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
    }

    /**
     * @return The x-position of the mouse.
     */
    public int getMouseX()
    {
        return mousePosition.x;
    }

    /**
     * Method used to set the x-position of the mouse.
     *
     * @param xMouse The x-position of the mouse.
     */
    public void setMouseX(int xMouse)
    {
        this.mousePosition.x = xMouse;
    }

    /**
     * @return The y-position of the mouse
     */
    public int getMouseY()
    {
        return mousePosition.y;
    }

    /**
     * Method used to set the y-position of the mouse.
     *
     * @param yMouse The y-position of the mouse
     */
    public void setMouseY(int yMouse)
    {
        this.mousePosition.y = yMouse;
    }

    /**
     * Checks if any key is currently pressed.
     *
     * @return True if any key is pressed.
     */
    public boolean isAnyKeyPressed()
    {
        for (Property property : keyMap.values())
        {
            if (property != null && !property.isReleased && property.isPressed) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if any key is currently held.
     *
     * @return True if any key is held.
     */
    public boolean isAnyKeyHeld()
    {
        for (Property property : keyMap.values())
        {
            if (property != null && property.isPressed && property.isReleased) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if any mouse button is currently pressed.
     *
     * @return True if any mouse button is pressed.
     */
    public boolean isAnyButtonPressed()
    {
        for (Property property : mouseMap.values())
        {
            if (property != null && !property.isReleased && property.isPressed) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if any mouse button is currently held.
     *
     * @return True if any mouse button is held.
     */
    public boolean isAnyButtonHeld()
    {
        for (Property property : mouseMap.values())
        {
            if (property != null && property.isPressed && property.isReleased) {
                return true;
            }
        }
        return false;
    }


    /**
     * Gets the mouse position as a Tuple2i.
     *
     * @return Mouse position.
     */
    @Override
    public Tuple2i getMousePosition()
    {
        return mousePosition;
    }

    /**
     * Sets the mouse position.
     *
     * @param x X position.
     * @param y Y position.
     */
    public void setMousePosition(int x, int y)
    {
        this.mousePosition.setPositions(x, y);
    }
}
