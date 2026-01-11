package TransmuteCore.System.Exceptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception thrown when game configuration is invalid.
 * Provides context about what was expected vs what was found.
 */
public class InvalidConfigurationException extends TransmuteCoreException
{
    private final String configParameter;
    private final Object providedValue;
    private final String expectedValue;
    private final List<String> suggestions;
    
    /**
     * Creates a new InvalidConfigurationException.
     *
     * @param configParameter Name of the configuration parameter.
     * @param providedValue   The value that was provided.
     * @param expectedValue   Description of what was expected.
     */
    public InvalidConfigurationException(String configParameter, Object providedValue, String expectedValue)
    {
        super(buildMessage(configParameter, providedValue, expectedValue, null));
        this.configParameter = configParameter;
        this.providedValue = providedValue;
        this.expectedValue = expectedValue;
        this.suggestions = generateDefaultSuggestions();
    }
    
    /**
     * Creates a new InvalidConfigurationException with custom suggestions.
     *
     * @param configParameter Name of the configuration parameter.
     * @param providedValue   The value that was provided.
     * @param expectedValue   Description of what was expected.
     * @param suggestions     Custom suggestions for fixing the problem.
     */
    public InvalidConfigurationException(String configParameter, Object providedValue, 
                                        String expectedValue, List<String> suggestions)
    {
        super(buildMessage(configParameter, providedValue, expectedValue, suggestions));
        this.configParameter = configParameter;
        this.providedValue = providedValue;
        this.expectedValue = expectedValue;
        this.suggestions = suggestions != null ? new ArrayList<>(suggestions) : generateDefaultSuggestions();
    }
    
    /**
     * Builds the detailed error message.
     */
    private static String buildMessage(String configParameter, Object providedValue, 
                                       String expectedValue, List<String> customSuggestions)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("\n========== INVALID CONFIGURATION ==========\n");
        sb.append(String.format("Parameter: %s\n", configParameter));
        sb.append(String.format("Provided: %s\n", providedValue));
        sb.append(String.format("Expected: %s\n", expectedValue));
        sb.append("\n");
        
        List<String> suggestions = customSuggestions != null ? customSuggestions : generateDefaultSuggestions();
        if (!suggestions.isEmpty())
        {
            sb.append("How to Fix:\n");
            for (int i = 0; i < suggestions.size(); i++)
            {
                sb.append(String.format("  %d. %s\n", i + 1, suggestions.get(i)));
            }
        }
        
        sb.append("==========================================");
        return sb.toString();
    }
    
    /**
     * Generates default suggestions for fixing configuration issues.
     */
    private static List<String> generateDefaultSuggestions()
    {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("Check the value provided matches the expected type and range");
        suggestions.add("Review the API documentation for valid configuration values");
        suggestions.add("Ensure all required configuration parameters are set");
        return suggestions;
    }
    
    /**
     * @return The name of the configuration parameter.
     */
    public String getConfigParameter()
    {
        return configParameter;
    }
    
    /**
     * @return The value that was provided.
     */
    public Object getProvidedValue()
    {
        return providedValue;
    }
    
    /**
     * @return Description of what was expected.
     */
    public String getExpectedValue()
    {
        return expectedValue;
    }
    
    /**
     * @return List of suggestions for resolving the error.
     */
    public List<String> getSuggestions()
    {
        return new ArrayList<>(suggestions);
    }
}
