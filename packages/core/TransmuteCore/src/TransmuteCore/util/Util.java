package TransmuteCore.util;

import java.util.Random;

/**
 * {@code Util} is a utility class.
 * <br>
 * This class is used as a utility.
 */
public class Util
{

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
