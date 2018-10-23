package Hazel.System.Asset;

import Hazel.System.Util;

/**
 * <p>An abstract representation of a resource. An abstract asset used for deferred resource loading.
 * Each resource is given a unique resource key (to identify the
 * resource in the registrar) and an object reference to the actual
 * resource.</p>
 */
public abstract class Asset
{
    /**
     * Class variable for use with loading assets into memory
     */
    protected static Class<? extends Asset> assetLoader = Asset.class;

    /**
     * Asset identifier (used in Assets registrar)
     */
    protected String key;

    /**
     * Referred resource, to be loaded on tile-up
     */
    protected Object target;

    /**
     * Resource type (e.g. Image)
     */
    protected String prefix;

    /**
     * Resource name (e.g. fileName)
     */
    protected String name;

    /**
     * Resource location (e.g. directory/fileName.txt)
     */
    protected String filePath;

    /**
     * Resource file-name (e.g. fileName.txt)
     */
    protected String fileName;

    /**
     * Resource file-type (e.g. '.txt')
     */
    protected String fileType;

    /**
     * Creates a deferrable asset.
     *
     * @param type     Asset type prefix (to avoid ID clash).
     * @param name     Unique identifier name.
     * @param filePath Resource path.
     */
    public Asset(String type, String name, String filePath)
    {
        this.prefix = type.toLowerCase();
        this.name = name.toLowerCase();

        this.key = AssetManager.createKey(type, name);

        this.filePath = filePath.toLowerCase();
        this.fileName = Asset.crop("fileName", this.filePath);
        this.fileType = Asset.crop("fileType", this.filePath);

        AssetManager.register(this);
    }

    /**
     * This method will be implemented by concrete classes to load the actual asset.
     */
    public abstract void load();

    /**
     * @return The specific asset object.
     */
    public abstract Object getData();

    /**
     * Method used to clean up memory used by
     * certain processes
     */
    public void flush()
    {
        target = null;
        Util.log("[" + getType() + "]: [" + getKey() + "] was flushed from memory.");
    }

    /**
     * Method used to grab sub-sections of the 'filePath' string, such as 'fileName' and 'fileExtension'.
     * <br>
     * Note: if no 'cropType' is specified, this method will return the 'filePath'.
     *
     * @param cropType The section to crop (e.g. 'fileName').
     * @param filePath The path to the file.
     * @return The sub-section of the 'filePath' string, such as 'fileName' and 'fileExtension'.
     */
    public static String crop(String cropType, String filePath)
    {
        if (cropType.equalsIgnoreCase("fileName"))
        {
            return cropFileName(filePath);
        } else if (cropType.equalsIgnoreCase("fileType") || cropType.equalsIgnoreCase("type")
                || cropType.equalsIgnoreCase("extension") || cropType.equalsIgnoreCase("fileExtension"))
        {
            return cropFileExtension(filePath);
        } else
        {
            return filePath;
        }
    }

    /**
     * Method used to grab either the file-name along with its extension (e.g. fileName.png).
     *
     * @param filePath The path to the file.
     * @return the file-name with the extension (e.g. fileName.png).
     */
    public static String cropFileName(String filePath)
    {
        if (filePath.contains("\\"))
        {
            return filePath.substring(filePath.lastIndexOf("\\") + 1);
        } else
        {
            return filePath.substring(filePath.lastIndexOf("/") + 1);
        }
    }

    /**
     * Method used to crop the file-type from the file-path.
     * <p>
     * NOTE: Doesn't work with {@code .tar.gz} and others like it.
     *
     * @param filePath The path to the file.
     * @return The file-type.
     */
    public static String cropFileExtension(String filePath)
    {
        String fileExtension = filePath.split("\\.(?=[^\\.]+$)")[1];

        if (!fileExtension.contains(".")) fileExtension = new StringBuilder("." + fileExtension).toString();

        return fileExtension;
    }

    /**
     * @return Unique identifier name.
     */
    public String getKey()
    {
        return key;
    }

    /**
     * @return The path to the given file.
     */
    public String getFilePath()
    {
        return filePath;
    }

    /**
     * @return The type of asset.
     */
    protected String getType()
    {
        return prefix;
    }

    /**
     * @return The file name.
     */
    public String getFileName()
    {
        return fileName;
    }

    /**
     * @return The file extension.
     */
    public String getFileType()
    {
        return fileType;
    }

    /**
     * @return The name of the asset.
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return Checks if the asset has been cached.
     */
    public boolean isLoaded()
    {
        return target != null;
    }

    @Override
    public String toString()
    {
        return "Asset{" +
                "key='" + key + '\'' +
                ", target=" + target +
                ", prefix='" + prefix + '\'' +
                ", name='" + name + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileType='" + fileType + '\'' +
                '}';
    }
}
