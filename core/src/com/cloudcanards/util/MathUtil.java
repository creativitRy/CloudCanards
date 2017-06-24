package com.cloudcanards.util;

import com.badlogic.gdx.math.MathUtils;

/**
 * InterpUtils
 *
 * @author creativitRy
 */
public class MathUtil
{
	private MathUtil() {}
	
	/**
	 * Smooth transition from a to b using the equation a + x * (b - a) based on dt
	 *
	 * @param a        from
	 * @param b        to
	 * @param delta    delta time
	 * @param slowness float in range (0,1) where 0.1 is fast and 0.9 is slow
	 * @return smooth
	 */
	public static float lerp(float a, float b, float delta, float slowness)
	{
		return MathUtils.lerp(a, b, (float) (1 - Math.pow(slowness, delta * 60)));
	}
}
