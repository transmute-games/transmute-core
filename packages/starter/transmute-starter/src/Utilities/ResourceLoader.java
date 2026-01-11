package Utilities;

import TransmuteCore.System.Asset.Type.Fonts.Font;

public class ResourceLoader
{
    public static void load()
    {
        // Initialize the default font
        // The font will auto-register with the global AssetManager instance
        Font.initializeDefaultFont("fonts/font.png");
        
        // Load is called in LoadingState after all assets are registered
    }
}
