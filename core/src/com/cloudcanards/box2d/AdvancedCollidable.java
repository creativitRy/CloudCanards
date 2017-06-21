package com.cloudcanards.box2d;

import com.badlogic.gdx.physics.box2d.Body;

/**
 * Checks collisions. Attach to fixture's user data. Can get which body this fixture touched
 *
 * @author creativitRy
 */
public interface AdvancedCollidable
{
	/**
	 * Called when this fixture begins to touch something
	 *
	 * @param contactedBody touched body
	 */
	void onBeginContact(Body contactedBody);
	
	/**
	 * Called when this fixture stops touching something
	 *
	 * @param contactedBody touched body
	 */
	void onEndContact(Body contactedBody);
}
