package TransmuteCore.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the result of a validation check.
 * Contains success/failure status, error messages, and suggestions.
 */
public class ValidationResult
{
    private final boolean success;
    private final String validationName;
    private final List<String> errors;
    private final List<String> warnings;
    private final List<String> suggestions;
    
    private ValidationResult(boolean success, String validationName, 
                            List<String> errors, List<String> warnings, List<String> suggestions)
    {
        this.success = success;
        this.validationName = validationName;
        this.errors = errors != null ? new ArrayList<>(errors) : new ArrayList<>();
        this.warnings = warnings != null ? new ArrayList<>(warnings) : new ArrayList<>();
        this.suggestions = suggestions != null ? new ArrayList<>(suggestions) : new ArrayList<>();
    }
    
    /**
     * Creates a successful validation result.
     *
     * @param validationName Name of the validation check.
     * @return A successful ValidationResult.
     */
    public static ValidationResult success(String validationName)
    {
        return new ValidationResult(true, validationName, null, null, null);
    }
    
    /**
     * Creates a failed validation result.
     *
     * @param validationName Name of the validation check.
     * @param error          Error message.
     * @return A failed ValidationResult.
     */
    public static ValidationResult failure(String validationName, String error)
    {
        List<String> errors = new ArrayList<>();
        errors.add(error);
        return new ValidationResult(false, validationName, errors, null, null);
    }
    
    /**
     * Creates a failed validation result with suggestions.
     *
     * @param validationName Name of the validation check.
     * @param error          Error message.
     * @param suggestions    List of suggestions to fix the error.
     * @return A failed ValidationResult.
     */
    public static ValidationResult failure(String validationName, String error, List<String> suggestions)
    {
        List<String> errors = new ArrayList<>();
        errors.add(error);
        return new ValidationResult(false, validationName, errors, null, suggestions);
    }
    
    /**
     * Creates a validation result with multiple errors.
     *
     * @param validationName Name of the validation check.
     * @param errors         List of error messages.
     * @param suggestions    List of suggestions to fix the errors.
     * @return A failed ValidationResult.
     */
    public static ValidationResult failure(String validationName, List<String> errors, List<String> suggestions)
    {
        return new ValidationResult(false, validationName, errors, null, suggestions);
    }
    
    /**
     * Creates a warning validation result (success but with warnings).
     *
     * @param validationName Name of the validation check.
     * @param warnings       List of warning messages.
     * @return A successful ValidationResult with warnings.
     */
    public static ValidationResult warning(String validationName, List<String> warnings)
    {
        return new ValidationResult(true, validationName, null, warnings, null);
    }
    
    /**
     * @return True if validation was successful.
     */
    public boolean isSuccess()
    {
        return success;
    }
    
    /**
     * @return Name of the validation check.
     */
    public String getValidationName()
    {
        return validationName;
    }
    
    /**
     * @return List of error messages.
     */
    public List<String> getErrors()
    {
        return new ArrayList<>(errors);
    }
    
    /**
     * @return List of warning messages.
     */
    public List<String> getWarnings()
    {
        return new ArrayList<>(warnings);
    }
    
    /**
     * @return List of suggestions for fixing errors.
     */
    public List<String> getSuggestions()
    {
        return new ArrayList<>(suggestions);
    }
    
    /**
     * @return True if there are any warnings.
     */
    public boolean hasWarnings()
    {
        return !warnings.isEmpty();
    }
    
    /**
     * @return True if there are any errors.
     */
    public boolean hasErrors()
    {
        return !errors.isEmpty();
    }
    
    /**
     * Formats the validation result as a string.
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(validationName).append(": ");
        sb.append(success ? "PASSED" : "FAILED");
        
        if (!errors.isEmpty())
        {
            sb.append("\n  Errors:");
            for (String error : errors)
            {
                sb.append("\n    - ").append(error);
            }
        }
        
        if (!warnings.isEmpty())
        {
            sb.append("\n  Warnings:");
            for (String warning : warnings)
            {
                sb.append("\n    - ").append(warning);
            }
        }
        
        if (!suggestions.isEmpty())
        {
            sb.append("\n  Suggestions:");
            for (String suggestion : suggestions)
            {
                sb.append("\n    - ").append(suggestion);
            }
        }
        
        return sb.toString();
    }
}
