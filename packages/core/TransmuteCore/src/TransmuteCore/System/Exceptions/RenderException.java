package TransmuteCore.System.Exceptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception thrown when a rendering error occurs.
 * Provides context about what was being rendered when the error happened.
 */
public class RenderException extends TransmuteCoreException
{
    private final String renderOperation;
    private final String targetObject;
    private final List<String> suggestions;
    
    /**
     * Creates a new RenderException.
     *
     * @param renderOperation Description of the render operation that failed.
     * @param targetObject    The object being rendered (if applicable).
     * @param cause           The underlying cause of the error.
     */
    public RenderException(String renderOperation, String targetObject, Throwable cause)
    {
        super(buildMessage(renderOperation, targetObject, null), cause);
        this.renderOperation = renderOperation;
        this.targetObject = targetObject;
        this.suggestions = generateDefaultSuggestions();
    }
    
    /**
     * Creates a new RenderException with custom suggestions.
     *
     * @param renderOperation Description of the render operation that failed.
     * @param targetObject    The object being rendered (if applicable).
     * @param suggestions     Custom suggestions for fixing the problem.
     * @param cause           The underlying cause of the error.
     */
    public RenderException(String renderOperation, String targetObject, 
                          List<String> suggestions, Throwable cause)
    {
        super(buildMessage(renderOperation, targetObject, suggestions), cause);
        this.renderOperation = renderOperation;
        this.targetObject = targetObject;
        this.suggestions = suggestions != null ? new ArrayList<>(suggestions) : generateDefaultSuggestions();
    }
    
    /**
     * Creates a new RenderException without a cause.
     *
     * @param renderOperation Description of the render operation that failed.
     * @param targetObject    The object being rendered (if applicable).
     */
    public RenderException(String renderOperation, String targetObject)
    {
        super(buildMessage(renderOperation, targetObject, null));
        this.renderOperation = renderOperation;
        this.targetObject = targetObject;
        this.suggestions = generateDefaultSuggestions();
    }
    
    /**
     * Builds the detailed error message.
     */
    private static String buildMessage(String renderOperation, String targetObject, 
                                       List<String> customSuggestions)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("\n========== RENDER ERROR ==========\n");
        sb.append(String.format("Operation: %s\n", renderOperation));
        if (targetObject != null && !targetObject.isEmpty())
        {
            sb.append(String.format("Target: %s\n", targetObject));
        }
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
        
        sb.append("==================================");
        return sb.toString();
    }
    
    /**
     * Generates default suggestions for fixing rendering issues.
     */
    private static List<String> generateDefaultSuggestions()
    {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("Check that all required assets are loaded before rendering");
        suggestions.add("Verify that coordinates are within valid ranges");
        suggestions.add("Ensure the Context is properly initialized");
        suggestions.add("Check for null references in rendering code");
        suggestions.add("Verify that the rendering operation is called within the render() method");
        return suggestions;
    }
    
    /**
     * @return Description of the render operation that failed.
     */
    public String getRenderOperation()
    {
        return renderOperation;
    }
    
    /**
     * @return The object being rendered when the error occurred.
     */
    public String getTargetObject()
    {
        return targetObject;
    }
    
    /**
     * @return List of suggestions for resolving the error.
     */
    public List<String> getSuggestions()
    {
        return new ArrayList<>(suggestions);
    }
}
