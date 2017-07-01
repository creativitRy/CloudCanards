package com.cloudcanards.util;

import com.badlogic.gdx.graphics.Color;

/**
 * ColorUtil
 *
 * @author creativitRy
 */
public class ColorUtil
{
	private ColorUtil() {}
	
	/**
	 * Changes the transparency value of the color and returns the changed color
	 *
	 * @param color color to mutate
	 * @param alpha alpha value [0,1] to set
	 * @return adjusted color
	 */
	public static Color adjustAlpha(Color color, float alpha)
	{
		color.a = alpha;
		return color;
	}
}
