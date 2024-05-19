package TransmuteCore.Input;

import java.awt.event.*;
import java.util.HashMap;

import TransmuteCore.GameEngine.TransmuteCore;
import TransmuteCore.Units.Tuple2i;

/**
 * {@code Input} is a input handler class.
 * <br>
 * This class should be used to handle both mouse and keyboard input.
 */
public class Input implements KeyListener, MouseListener, MouseMotionListener
{
    private static HashMap<Integer, Property> keyMap = new HashMap<>(); //The hash table of keys
    private static HashMap<Integer, Property> mouseMap = new HashMap<>(); //The hash table of mouse buttons

    private Tuple2i mousePosition = new Tuple2i(0, 0); //The x and y positions of the mouse

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
     * The constructor that sets up input.
     *
     * @param gameEngine The game engine object.
     */
    public Input(TransmuteCore gameEngine)
    {
        gameEngine.getGameWindow().getCanvas().addKeyListener(this);
        gameEngine.getGameWindow().getCanvas().addMouseListener(this);
        gameEngine.getGameWindow().getCanvas().addMouseMotionListener(this);
    }

    /**
     * Method used to correlate between keys pressed and keys released.
     */
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
    public static boolean isKeyPressed(int... keyCode)
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
    public static boolean isKeyHeld(int... keyCode)
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
    public static boolean isKeyReleased(int... keyCode)
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
    public static boolean isButtonPressed(int... buttonCode)
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
    public static boolean isButtonReleased(int... buttonCode)
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
        mousePosition.setPositions(e.getX() / TransmuteCore.getScale(), e.getY() / TransmuteCore.getScale());
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        mousePosition.setPositions(e.getX() / TransmuteCore.getScale(), e.getY() / TransmuteCore.getScale());
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
}