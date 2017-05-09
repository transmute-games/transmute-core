package Utilities;

import Meteor.Graphics.Sprites.SpriteManager;
import Meteor.System.Asset.AssetManager;

public class ResourceLoader {
	public static SpriteManager SPRITE_MANAGER;
	
	public static void load() {
		AssetManager.load();
	}
}
