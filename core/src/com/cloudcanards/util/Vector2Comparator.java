package com.cloudcanards.util;

import com.badlogic.gdx.math.Vector2;

import java.util.Comparator;

/**
 * Vector2Comparator sorts vectors by x first then y
 *
 * @author creativitRy
 */
public class Vector2Comparator implements Comparator<Vector2>
{
	@Override
	public int compare(Vector2 o1, Vector2 o2)
	{
		if (o1.y == o2.y)
			return Float.compare(o1.x, o2.x);
		return Float.compare(o1.y, o2.y);
	}
	
	private static final Vector2Comparator INSTANCE;
	
	static
	{
		INSTANCE = new Vector2Comparator();
	}
	
	/**
	 * Compares vectors
	 *
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static int compareStatic(Vector2 o1, Vector2 o2)
	{
		return INSTANCE.compare(o1, o2);
	}
}
