package TransmuteCore.System;

import java.util.Random;

/**
 * {@code Util} is a utility class.
 * <br>
 * This class is used as a utility.
 */
public class Util
{
    /**
     * Method used to print a message to the console using {@code System.out.println()}.
     *
     * @param message The message to print to the console.
     */
    public static void log(String message)
    {
        System.out.println(message);
    }

    /**
     * Method used to indicate a successful caching of a file.
     *
     * @param className     The name of the class.
     * @param key           The key attached to the file.
     * @param containerName Then name of the file container.
     */
    public static void logAdd(String className, String key, String containerName)
    {
        log("[" + className + "]: [" + key + "] was added to the [" + containerName + "].");
    }

    /**
     * Method used to indicated that an item was successfully removed from a list.
     *
     * @param className     The name of the class.
     * @param key           The key attached to the item.
     * @param containerName The name of the container.
     */
    public static void logRemove(String className, String key, String containerName)
    {
        log("[" + className + "]:" + " [" + key + "] was removed from the [" + containerName + "].");
    }

    /**
     * Method used to indicate a successful caching of a file.
     *
     * @param className The key of the class.
     * @param filePath  The path to the file.
     */
    public static void logCached(String className, String filePath)
    {
        log("[" + className + "]: [" + filePath + "] has been cached.");
    }

    /**
     * Method used to generate an alphanumeric code.
     *
     * @param numCharacters The length of the alphanumeric code.
     * @return The alphanumeric code.
     */
    public static String generateCode(int numCharacters)
    {
        StringBuilder stringBuilder = new StringBuilder();
        Random random = new Random();
        char[] chars = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
                'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
                'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F',
                'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
                'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4',
                '5', '6', '7', '8', '9', '0'};
        for (int i = 0; i < numCharacters; i++) stringBuilder.append(chars[random.nextInt(chars.length)]);
        return stringBuilder.toString();
    }
}
