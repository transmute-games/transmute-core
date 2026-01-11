package TransmuteCore.System;

import TransmuteCore.System.Asset.AssetManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Validates game configuration and environment before starting the game loop.
 * Catches common configuration errors early with helpful error messages.
 * <p>
 * Usage:
 * <pre>
 * {@code
 * StartupValidator validator = new StartupValidator();
 * List<ValidationResult> results = validator.validateAll();
 * 
 * if (!StartupValidator.allPassed(results)) {
 *     StartupValidator.printResults(results);
 *     System.exit(1);
 * }
 * }
 * </pre>
 */
public class StartupValidator
{
    private List<String> requiredDirectories = new ArrayList<>();
    private boolean checkAssets = true;
    private boolean checkGraphics = true;
    
    /**
     * Creates a new StartupValidator with default checks.
     */
    public StartupValidator()
    {
    }
    
    /**
     * Adds a required directory to check.
     *
     * @param directory Path to the directory.
     * @return This validator for chaining.
     */
    public StartupValidator requireDirectory(String directory)
    {
        if (directory != null && !directory.trim().isEmpty())
        {
            requiredDirectories.add(directory);
        }
        return this;
    }
    
    /**
     * Enables or disables asset validation.
     *
     * @param check Whether to check assets.
     * @return This validator for chaining.
     */
    public StartupValidator checkAssets(boolean check)
    {
        this.checkAssets = check;
        return this;
    }
    
    /**
     * Enables or disables graphics capability checks.
     *
     * @param check Whether to check graphics.
     * @return This validator for chaining.
     */
    public StartupValidator checkGraphics(boolean check)
    {
        this.checkGraphics = check;
        return this;
    }
    
    /**
     * Runs all validation checks.
     *
     * @return List of validation results.
     */
    public List<ValidationResult> validateAll()
    {
        List<ValidationResult> results = new ArrayList<>();
        
        results.add(validateJavaVersion());
        results.add(validateDirectories());
        
        if (checkAssets)
        {
            results.add(validateAssets());
        }
        
        if (checkGraphics)
        {
            results.add(validateGraphicsCapabilities());
        }
        
        return results;
    }
    
    /**
     * Validates Java version is sufficient.
     */
    private ValidationResult validateJavaVersion()
    {
        String javaVersion = System.getProperty("java.version");
        String[] parts = javaVersion.split("\\.");
        
        try
        {
            int majorVersion = Integer.parseInt(parts[0]);
            if (majorVersion >= 17)
            {
                return ValidationResult.success("Java Version");
            }
            else
            {
                return ValidationResult.failure(
                    "Java Version",
                    "Java 17 or higher is required. Current version: " + javaVersion,
                    Arrays.asList(
                        "Download Java 17+ from https://adoptium.net/",
                        "Update JAVA_HOME environment variable",
                        "Verify with: java -version"
                    )
                );
            }
        }
        catch (NumberFormatException e)
        {
            List<String> warnings = new ArrayList<>();
            warnings.add("Could not parse Java version: " + javaVersion);
            return ValidationResult.warning("Java Version", warnings);
        }
    }
    
    /**
     * Validates required directories exist.
     */
    private ValidationResult validateDirectories()
    {
        if (requiredDirectories.isEmpty())
        {
            return ValidationResult.success("Required Directories");
        }
        
        List<String> missingDirs = new ArrayList<>();
        for (String dir : requiredDirectories)
        {
            File directory = new File(dir);
            if (!directory.exists() || !directory.isDirectory())
            {
                missingDirs.add(dir);
            }
        }
        
        if (missingDirs.isEmpty())
        {
            return ValidationResult.success("Required Directories");
        }
        else
        {
            List<String> errors = new ArrayList<>();
            for (String dir : missingDirs)
            {
                errors.add("Missing directory: " + dir);
            }
            
            return ValidationResult.failure(
                "Required Directories",
                errors,
                Arrays.asList(
                    "Create missing directories",
                    "Verify your working directory is correct",
                    "Check that resources are properly packaged"
                )
            );
        }
    }
    
    /**
     * Validates registered assets.
     */
    private ValidationResult validateAssets()
    {
        if (AssetManager.isLoaded())
        {
            return ValidationResult.success("Asset Loading");
        }
        
        if (AssetManager.getLoadQueue().isEmpty())
        {
            List<String> warnings = new ArrayList<>();
            warnings.add("No assets have been registered");
            warnings.add("Did you forget to register assets before loading?");
            return ValidationResult.warning("Asset Loading", warnings);
        }
        
        return ValidationResult.success("Asset Loading");
    }
    
    /**
     * Validates basic graphics capabilities.
     */
    private ValidationResult validateGraphicsCapabilities()
    {
        try
        {
            // Check if we can create a graphics environment
            java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
            
            if (ge.isHeadlessInstance())
            {
                return ValidationResult.failure(
                    "Graphics Capabilities",
                    "Running in headless mode - cannot create windows",
                    Arrays.asList(
                        "Ensure you're not running on a headless server",
                        "Check DISPLAY environment variable on Linux",
                        "Use a system with a display/graphics capability"
                    )
                );
            }
            
            return ValidationResult.success("Graphics Capabilities");
        }
        catch (Exception e)
        {
            return ValidationResult.failure(
                "Graphics Capabilities",
                "Failed to initialize graphics: " + e.getMessage(),
                Arrays.asList(
                    "Check graphics drivers are installed",
                    "Verify display is properly configured",
                    "Run with: java -Djava.awt.headless=false"
                )
            );
        }
    }
    
    /**
     * Checks if all validation results passed.
     *
     * @param results List of validation results.
     * @return True if all passed.
     */
    public static boolean allPassed(List<ValidationResult> results)
    {
        for (ValidationResult result : results)
        {
            if (!result.isSuccess())
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Prints validation results to console.
     *
     * @param results List of validation results.
     */
    public static void printResults(List<ValidationResult> results)
    {
        System.out.println("\n========== STARTUP VALIDATION ==========");
        
        boolean hasFailures = false;
        boolean hasWarnings = false;
        
        for (ValidationResult result : results)
        {
            if (!result.isSuccess())
            {
                hasFailures = true;
            }
            if (result.hasWarnings())
            {
                hasWarnings = true;
            }
            
            System.out.println(result.toString());
            System.out.println();
        }
        
        if (hasFailures)
        {
            System.out.println("❌ Startup validation FAILED");
            System.out.println("Please fix the errors above before running the game.");
        }
        else if (hasWarnings)
        {
            System.out.println("⚠️  Startup validation passed with warnings");
        }
        else
        {
            System.out.println("✓ All startup validation checks passed");
        }
        
        System.out.println("========================================\n");
    }
}
