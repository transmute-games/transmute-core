package TransmuteCore.System.Exceptions;

/**
 * Exception thrown when an asset fails to load.
 * This includes images, audio, fonts, and other resources.
 */
public class AssetLoadException extends TransmuteCoreException {
    
    private final String assetPath;
    private final String assetType;
    
    /**
     * Creates a new AssetLoadException.
     *
     * @param assetType The type of asset (e.g., "image", "audio", "font").
     * @param assetPath The path to the asset that failed to load.
     * @param message   Additional details about the failure.
     */
    public AssetLoadException(String assetType, String assetPath, String message) {
        super(buildMessage(assetType, assetPath, message));
        this.assetPath = assetPath;
        this.assetType = assetType;
    }
    
    /**
     * Creates a new AssetLoadException with a cause.
     *
     * @param assetType The type of asset (e.g., "image", "audio", "font").
     * @param assetPath The path to the asset that failed to load.
     * @param message   Additional details about the failure.
     * @param cause     The underlying cause of the failure.
     */
    public AssetLoadException(String assetType, String assetPath, String message, Throwable cause) {
        super(buildMessage(assetType, assetPath, message), cause);
        this.assetPath = assetPath;
        this.assetType = assetType;
    }
    
    /**
     * @return The path to the asset that failed to load.
     */
    public String getAssetPath() {
        return assetPath;
    }
    
    /**
     * @return The type of asset that failed to load.
     */
    public String getAssetType() {
        return assetType;
    }
    
    private static String buildMessage(String assetType, String assetPath, String details) {
        StringBuilder msg = new StringBuilder();
        msg.append("Failed to load ").append(assetType).append(" asset: '").append(assetPath).append("'");
        
        if (details != null && !details.isEmpty()) {
            msg.append("\nDetails: ").append(details);
        }
        
        msg.append("\n\nPossible causes:");
        msg.append("\n- The file does not exist at the specified path");
        msg.append("\n- The file path is incorrect (check spelling and case)");
        msg.append("\n- The file is not in the correct format");
        msg.append("\n- The file is corrupted");
        msg.append("\n\nSuggestions:");
        msg.append("\n- Verify the asset exists in your resources directory");
        msg.append("\n- Check that the path is relative to the resources root");
        msg.append("\n- Ensure the file extension matches the actual file type");
        
        return msg.toString();
    }
}
