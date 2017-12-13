package Meteor.Graphics.UI;

import java.awt.Rectangle;

import Meteor.GameEngine.Manager;
import Meteor.GameEngine.Interfaces.Renderable;
import Meteor.Graphics.Context;

public class Bar implements Renderable
{
	private int bg, fg;
	private Rectangle bounds;
	private float currentAmount = 0.0f;
	private float max = 1.0f;
	
	public Bar(int bg, int fg, int x, int y, int width, int height)
	{
		this.bg = bg;
		this.fg = fg;
		
		this.bounds = new Rectangle(x, y, width, height);
	}
	
	public Bar(int bg, int fg, int x, int y, int width, int height, float max)
	{
		this.bg = bg;
		this.fg = fg;
		this.max = max;
		
		this.bounds = new Rectangle(x, y, width, height);
	}

	@Override
	public void render(Manager manager, Context ctx)
	{
		ctx.renderFilledRectangle(bounds.x, bounds.y, bounds.width, bounds.height, bg);
		ctx.renderFilledRectangle(bounds.x, bounds.y, getGrowth(), bounds.height, fg);
	}
	
	private float getGrowth() 
	{
		return bounds.width * (this.currentAmount / max);
	}
	
	public void increasePercent(float amount)
	{
		setPercent(currentAmount + amount);
	}
	
	private void setPercent(float amount)
	{
		this.currentAmount = amount;
		if (currentAmount > max) currentAmount = max;
	}
	
}
