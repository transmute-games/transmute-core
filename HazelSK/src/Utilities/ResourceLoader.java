package Utilities;

import Hazel.System.Asset.AssetManager;
import Hazel.System.Asset.Type.Fonts.Font;

public class ResourceLoader
{
    public static void load()
    {
        Font.initializeDefaultFont("fonts/font.png");
        AssetManager.load();
    }
}
