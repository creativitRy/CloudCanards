package com.cloudcanards.grapple;

import com.badlogic.gdx.math.Vector2;

/**
 * Targetable
 *
 * @author creativitRy
 */
public interface Targetable
{
	/**
	 * This better return a variable and not a new vector everytime (lol who needs performance)
	 *
	 * @return
	 */
	Vector2 getPosition();
}
