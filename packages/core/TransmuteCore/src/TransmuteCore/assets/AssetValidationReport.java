package TransmuteCore.assets;

import java.util.ArrayList;
import java.util.List;

/**
 * Detailed report of asset loading validation.
 * Tracks successful loads, failures, warnings, and provides recommendations.
 */
public class AssetValidationReport
{
    private final List<AssetStatus> assets;
    private final long validationTime;
    
    public AssetValidationReport()
    {
        this.assets = new ArrayList<>();
        this.validationTime = System.currentTimeMillis();
    }
    
    /**
     * Adds an asset status to the report.
     *
     * @param status Asset status.
     */
    public void addAsset(AssetStatus status)
    {
        if (status != null)
        {
            assets.add(status);
        }
    }
    
    /**
     * @return Total number of assets.
     */
    public int getTotalAssets()
    {
        return assets.size();
    }
    
    /**
     * @return Number of successfully loaded assets.
     */
    public int getSuccessCount()
    {
        int count = 0;
        for (AssetStatus status : assets)
        {
            if (status.isSuccess())
            {
                count++;
            }
        }
        return count;
    }
    
    /**
     * @return Number of failed assets.
     */
    public int getFailureCount()
    {
        return getTotalAssets() - getSuccessCount();
    }
    
    /**
     * @return List of all asset statuses.
     */
    public List<AssetStatus> getAssets()
    {
        return new ArrayList<>(assets);
    }
    
    /**
     * @return List of failed assets only.
     */
    public List<AssetStatus> getFailedAssets()
    {
        List<AssetStatus> failed = new ArrayList<>();
        for (AssetStatus status : assets)
        {
            if (!status.isSuccess())
            {
                failed.add(status);
            }
        }
        return failed;
    }
    
    /**
     * @return True if all assets loaded successfully.
     */
    public boolean allSuccess()
    {
        return getFailureCount() == 0;
    }
    
    /**
     * Prints the validation report to console.
     */
    public void print()
    {
        System.out.println("\n========== ASSET VALIDATION REPORT ==========");
        System.out.println("Total Assets: " + getTotalAssets());
        System.out.println("Successful: " + getSuccessCount());
        System.out.println("Failed: " + getFailureCount());
        System.out.println();
        
        if (getFailureCount() > 0)
        {
            System.out.println("Failed Assets:");
            for (AssetStatus status : getFailedAssets())
            {
                System.out.println("  ✗ " + status.getName() + " (" + status.getType() + ")");
                System.out.println("    Path: " + status.getPath());
                System.out.println("    Error: " + status.getErrorMessage());
                if (!status.getSuggestions().isEmpty())
                {
                    System.out.println("    Suggestions:");
                    for (String suggestion : status.getSuggestions())
                    {
                        System.out.println("      - " + suggestion);
                    }
                }
                System.out.println();
            }
        }
        
        if (allSuccess())
        {
            System.out.println("✓ All assets loaded successfully");
        }
        
        System.out.println("=============================================\n");
    }
    
    /**
     * Exports the report as a formatted string.
     */
    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("AssetValidationReport {\n");
        sb.append("  total: ").append(getTotalAssets()).append(",\n");
        sb.append("  successful: ").append(getSuccessCount()).append(",\n");
        sb.append("  failed: ").append(getFailureCount()).append("\n");
        sb.append("}");
        return sb.toString();
    }
    
    /**
     * Represents the status of a single asset.
     */
    public static class AssetStatus
    {
        private final String name;
        private final String type;
        private final String path;
        private final boolean success;
        private final String errorMessage;
        private final List<String> suggestions;
        
        public AssetStatus(String name, String type, String path, boolean success)
        {
            this(name, type, path, success, null, null);
        }
        
        public AssetStatus(String name, String type, String path, boolean success, 
                          String errorMessage, List<String> suggestions)
        {
            this.name = name;
            this.type = type;
            this.path = path;
            this.success = success;
            this.errorMessage = errorMessage;
            this.suggestions = suggestions != null ? new ArrayList<>(suggestions) : new ArrayList<>();
        }
        
        public String getName()
        {
            return name;
        }
        
        public String getType()
        {
            return type;
        }
        
        public String getPath()
        {
            return path;
        }
        
        public boolean isSuccess()
        {
            return success;
        }
        
        public String getErrorMessage()
        {
            return errorMessage;
        }
        
        public List<String> getSuggestions()
        {
            return new ArrayList<>(suggestions);
        }
    }
}
