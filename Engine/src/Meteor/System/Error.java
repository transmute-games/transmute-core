package Meteor.System;

@SuppressWarnings("serial")
public class Error extends Exception
{

    /**
     * A generic error message.
     *
     * @param errorMessage The error message to print to the screen.
     */
    public Error(String errorMessage)
    {
        show(errorMessage);
    }

    /**
     * Method used to print a error message using {@code System.err.println()}.
     *
     * @param errorMessage The error message to print to the screen.
     */
    private void show(String errorMessage)
    {
        String errorCode = Util.generateCode(16);
        System.err.println("-------------------------------");
        System.err.println("[" + Meteor.GameEngine.Meteor.ENGINE_TITLE + "] Error Message:\n[errorCode]: " + errorCode + "\n" + errorMessage);
        System.err.println("-------------------------------");
        System.exit(1);
    }

    /**
     * Method used to show that an error occurred while caching a file.
     *
     * @param className The name of the class.
     * @param filePath  The path to the file.
     */
    public static String FileNotFoundException(String className, String filePath)
    {
        return "[" + className + "]: [" + filePath + "] failed to be cached.";
    }

    /**
     * Method used to show that the 'filePath' variable is null.
     *
     * @param className The name of the class where this method is used.
     */
    public static String filePathException(String className)
    {
        return "[" + className + "]: the filePath is null.";
    }

    /**
     * Method used to show that an error occurred while caching a file into a hash map.
     *
     * @param className The name of the class.
     * @param key       The key attached to the file.
     * @param mapName   The name of the file container.
     */
    public static String KeyAlreadyExistsException(String className, String key, String mapName)
    {
        return "[" + className + "]: [" + key + "] is already contained in the [" + mapName + "].";
    }

    /**
     * Method used to show that an error occurred while grabbing a file from a hash map.
     *
     * @param className The name of the class.
     * @param key       The key attached to the file.
     * @param mapName   The name of the file container.
     */
    public static String KeyNotFoundException(String className, String key, String mapName)
    {
        return "[" + className + "]: [" + key + "] was not found in the [" + mapName + "].";
    }
}
