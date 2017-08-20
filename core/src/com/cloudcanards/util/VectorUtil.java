package com.cloudcanards.util;

import com.badlogic.gdx.math.Vector2;

/**
 * VectorUtil
 *
 * @author creativitRy
 */
public class VectorUtil
{
	private VectorUtil() {}
	
	private static Vector2 temp = new Vector2();
	
	/**
	 * Add without affecting the vector
	 */
	public static Vector2 add(Vector2 vector, float x, float y)
	{
		return temp.set(vector).add(x, y);
	}
}
