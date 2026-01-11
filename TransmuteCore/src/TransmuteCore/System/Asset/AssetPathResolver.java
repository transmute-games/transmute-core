package TransmuteCore.System.Asset;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Utility class for resolving and validating asset file paths.
 * <p>
 * Helps with common path operations like:
 * - Converting relative paths to absolute paths
 * - Validating file existence
 * - Normalizing path separators across platforms
 * - Building paths from components
 * </p>
 */
public class AssetPathResolver
{
    private static String assetRoot = "";

    /**
     * Sets the root directory for asset resolution.
     * All relative paths will be resolved relative to this directory.
     *
     * @param rootPath The root directory path for assets.
     */
    public static void setAssetRoot(String rootPath)
    {
        if (rootPath == null || rootPath.trim().isEmpty()) {
            assetRoot = "";
            return;
        }
        
        File root = new File(rootPath);
        if (!root.exists()) {
            throw new IllegalArgumentException(
                String.format("Asset root directory does not exist: %s", rootPath)
            );
        }
        if (!root.isDirectory()) {
            throw new IllegalArgumentException(
                String.format("Asset root path is not a directory: %s", rootPath)
            );
        }
        
        assetRoot = rootPath;
    }

    /**
     * Gets the current asset root directory.
     *
     * @return The asset root directory path, or empty string if not set.
     */
    public static String getAssetRoot()
    {
        return assetRoot;
    }

    /**
     * Resolves a path to an absolute path.
     * If the path is already absolute, returns it as-is.
     * If the path is relative and an asset root is set, resolves it relative to the asset root.
     * Otherwise, resolves it relative to the current working directory.
     *
     * @param path The path to resolve.
     * @return The resolved absolute path.
     */
    public static String resolve(String path)
    {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }

        File file = new File(path);
        
        // If already absolute, return as-is
        if (file.isAbsolute()) {
            return file.getAbsolutePath();
        }

        // If asset root is set, resolve relative to it
        if (!assetRoot.isEmpty()) {
            Path resolved = Paths.get(assetRoot, path);
            return resolved.toAbsolutePath().normalize().toString();
        }

        // Otherwise resolve relative to current directory
        return file.getAbsolutePath();
    }

    /**
     * Checks if a file exists at the given path.
     *
     * @param path The path to check.
     * @return True if the file exists, false otherwise.
     */
    public static boolean exists(String path)
    {
        if (path == null || path.trim().isEmpty()) {
            return false;
        }
        return new File(resolve(path)).exists();
    }

    /**
     * Validates that a file exists at the given path.
     * Throws an exception if the file does not exist.
     *
     * @param path The path to validate.
     * @throws IllegalArgumentException if the file does not exist.
     */
    public static void validateExists(String path)
    {
        if (!exists(path)) {
            throw new IllegalArgumentException(
                String.format("Asset file not found: %s (resolved to: %s)", 
                    path, resolve(path))
            );
        }
    }

    /**
     * Builds a path from multiple components.
     *
     * @param components Path components to join.
     * @return The joined path.
     */
    public static String join(String... components)
    {
        if (components == null || components.length == 0) {
            throw new IllegalArgumentException("Path components cannot be null or empty");
        }

        Path path = Paths.get(components[0]);
        for (int i = 1; i < components.length; i++) {
            if (components[i] != null && !components[i].isEmpty()) {
                path = path.resolve(components[i]);
            }
        }

        return path.toString();
    }

    /**
     * Gets the file extension from a path (including the dot).
     *
     * @param path The file path.
     * @return The file extension, or empty string if none.
     */
    public static String getExtension(String path)
    {
        if (path == null || path.trim().isEmpty()) {
            return "";
        }

        int lastDot = path.lastIndexOf('.');
        int lastSeparator = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));

        if (lastDot > lastSeparator && lastDot < path.length() - 1) {
            return path.substring(lastDot);
        }

        return "";
    }

    /**
     * Gets the filename without extension from a path.
     *
     * @param path The file path.
     * @return The filename without extension.
     */
    public static String getNameWithoutExtension(String path)
    {
        if (path == null || path.trim().isEmpty()) {
            return "";
        }

        File file = new File(path);
        String name = file.getName();

        int lastDot = name.lastIndexOf('.');
        if (lastDot > 0) {
            return name.substring(0, lastDot);
        }

        return name;
    }

    /**
     * Normalizes a path by:
     * - Converting backslashes to forward slashes
     * - Resolving "." and ".." segments
     * - Removing duplicate slashes
     *
     * @param path The path to normalize.
     * @return The normalized path.
     */
    public static String normalize(String path)
    {
        if (path == null || path.trim().isEmpty()) {
            return "";
        }

        return Paths.get(path).normalize().toString().replace('\\', '/');
    }

    private AssetPathResolver()
    {
    }
}
