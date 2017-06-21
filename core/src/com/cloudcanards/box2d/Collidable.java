package com.cloudcanards.box2d;

/**
 * Checks collisions. Attach to fixture's user data.
 *
 * @author creativitRy
 */
public interface Collidable
{
	/**
	 * Called when this fixture begins to touch something
	 */
	void onBeginContact();
	
	/**
	 * Called when this fixture stops touching something
	 */
	void onEndContact();
}
