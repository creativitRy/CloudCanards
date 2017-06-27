package com.cloudcanards.algorithms.kd;

import com.badlogic.gdx.math.Vector2;

/**
 * Rectangle
 *
 * @author creativitRy
 */
class Rectangle
{
	float x1;
	float y1;
	float x2;
	float y2;
	
	public Rectangle(float x1, float y1, float x2, float y2)
	{
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
	}
	
	public Rectangle divide(Vector2 currPos, boolean left, boolean currUseY)
	{
		if (currUseY)
		{
			if (left)
				return new Rectangle(x1, y1, x2, currPos.y);
			else
				return new Rectangle(x1, currPos.y, x2, y2);
		}
		if (left)
			return new Rectangle(x1, y1, currPos.x, y2);
		return new Rectangle(currPos.x, y1, x2, y2);
	}
	
	public boolean isWithinRange(Vector2 position)
	{
		return x1 <= position.x && y1 <= position.y &&
			position.x <= x2 && position.y <= y2;
	}
	
	public boolean contains(Rectangle bounds)
	{
		return x1 <= bounds.x1 && y1 <= bounds.y1 &&
			bounds.x2 <= x2 && bounds.y2 <= y2;
	}
	
	public boolean intersects(Rectangle bounds)
	{
		return !(x2 < bounds.x1 || bounds.x2 < x1 || y2 < bounds.y1 || bounds.y2 < y1);
	}
	
	@Override
	public String toString()
	{
		return "[" +
			x1 +
			", " + y1 +
			", " + x2 +
			", " + y2 +
			']';
	}
}
