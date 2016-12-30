package Meteor.Objects.Type;

import Meteor.GameEngine.Manager;
import Meteor.Graphics.Sprites.Sprite;
import Meteor.Level.TiledLevel;
import Meteor.Objects.Object;
import Meteor.Units.Tuple4i;

public class Mob extends Object {
	public Mob(Manager manager, String name, Sprite sprite, Tuple4i properties) {
		super(manager, name, sprite, properties);
	}

	public Mob(Manager manager, String name, Tuple4i properties, Sprite... spriteList) {
		super(manager, name, properties, spriteList);
	}

	public void move(int xMove, int yMove) {
		if (xMove != 0 && yMove != 0) {
			move(xMove, 0);
			move(0, yMove);
			return;
		}
		
		if (!isCollidingWithTile(xMove, yMove)) {
			properties.location.x += xMove;
			properties.location.y += yMove;
		}
	}
	
	private boolean isCollidingWithTile(int xMove, int yMove) {
		if (level instanceof TiledLevel) {
			TiledLevel level = (TiledLevel) this.level;
			for (int i = 0; i < level.getData().length; i++) {
				int xt = (int) Math.floor(((properties.location.x + xMove) - i % level.getWidth()) / level.getTileSize());
				int yt = (int) Math.floor(((properties.location.y + yMove) - i / level.getHeight()) / level.getTileSize());
				
				if (level.getTile(xt, yt) == null) continue;
				if (level.getTile(xt, yt).isSolid()) return true;
			}
		}
		
		return false;
	}
}
