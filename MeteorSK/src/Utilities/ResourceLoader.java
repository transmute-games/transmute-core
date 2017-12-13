package Utilities;

import Meteor.System.Asset.AssetManager;
import Meteor.System.Asset.Type.Fonts.Font;

public class ResourceLoader
{
    public static void load()
    {
        Font.initializeDefaultFont("fonts/font.png");
        AssetManager.load();
    }
}
