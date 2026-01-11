package TransmuteCore.System.Exceptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Exception thrown when an asset cannot be found.
 * Provides detailed context including search paths and suggestions for resolution.
 */
public class AssetNotFoundException extends TransmuteCoreException
{
    private final String assetName;
    private final String assetType;
    private final List<String> searchedPaths;
    private final List<String> suggestions;
    
    /**
     * Creates a new AssetNotFoundException.
     *
     * @param assetName     Name of the asset that was not found.
     * @param assetType     Type of asset (image, audio, font, etc.).
     * @param searchedPaths Paths that were searched.
     */
    public AssetNotFoundException(String assetName, String assetType, List<String> searchedPaths)
    {
        super(buildMessage(assetName, assetType, searchedPaths, null));
        this.assetName = assetName;
        this.assetType = assetType;
        this.searchedPaths = searchedPaths != null ? new ArrayList<>(searchedPaths) : new ArrayList<>();
        this.suggestions = generateSuggestions();
    }
    
    /**
     * Creates a new AssetNotFoundException with custom suggestions.
     *
     * @param assetName     Name of the asset that was not found.
     * @param assetType     Type of asset (image, audio, font, etc.).
     * @param searchedPaths Paths that were searched.
     * @param suggestions   Custom suggestions for fixing the problem.
     */
    public AssetNotFoundException(String assetName, String assetType, 
                                  List<String> searchedPaths, List<String> suggestions)
    {
        super(buildMessage(assetName, assetType, searchedPaths, suggestions));
        this.assetName = assetName;
        this.assetType = assetType;
        this.searchedPaths = searchedPaths != null ? new ArrayList<>(searchedPaths) : new ArrayList<>();
        this.suggestions = suggestions != null ? new ArrayList<>(suggestions) : generateSuggestions();
    }
    
    /**
     * Builds the detailed error message.
     */
    private static String buildMessage(String assetName, String assetType, 
                                       List<String> searchedPaths, List<String> customSuggestions)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("\n========== ASSET NOT FOUND ==========\n");
        sb.append(String.format("Asset Name: %s\n", assetName));
        sb.append(String.format("Asset Type: %s\n", assetType));
        sb.append("\n");
        
        if (searchedPaths != null && !searchedPaths.isEmpty())
        {
            sb.append("Searched Paths:\n");
            for (String path : searchedPaths)
            {
                sb.append(String.format("  - %s\n", path));
            }
            sb.append("\n");
        }
        
        List<String> suggestions = customSuggestions != null ? customSuggestions : generateDefaultSuggestions();
        if (!suggestions.isEmpty())
        {
            sb.append("How to Fix:\n");
            for (int i = 0; i < suggestions.size(); i++)
            {
                sb.append(String.format("  %d. %s\n", i + 1, suggestions.get(i)));
            }
        }
        
        sb.append("=====================================");
        return sb.toString();
    }
    
    /**
     * Generates helpful suggestions based on the error context.
     */
    private List<String> generateSuggestions()
    {
        return generateDefaultSuggestions();
    }
    
    /**
     * Generates default suggestions for fixing asset loading issues.
     */
    private static List<String> generateDefaultSuggestions()
    {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("Check that the asset file exists in the specified location");
        suggestions.add("Verify the file name and extension are correct (case-sensitive on some systems)");
        suggestions.add("Ensure the asset is registered before calling AssetManager.load()");
        suggestions.add("Check that the file path is relative to the project root or is an absolute path");
        suggestions.add("Verify the asset file is not corrupted and is in the correct format");
        return suggestions;
    }
    
    /**
     * @return The name of the asset that was not found.
     */
    public String getAssetName()
    {
        return assetName;
    }
    
    /**
     * @return The type of asset (image, audio, font, etc.).
     */
    public String getAssetType()
    {
        return assetType;
    }
    
    /**
     * @return List of paths that were searched.
     */
    public List<String> getSearchedPaths()
    {
        return new ArrayList<>(searchedPaths);
    }
    
    /**
     * @return List of suggestions for resolving the error.
     */
    public List<String> getSuggestions()
    {
        return new ArrayList<>(suggestions);
    }
}
