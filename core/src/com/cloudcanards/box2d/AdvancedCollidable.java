package com.cloudcanards.box2d;

import com.badlogic.gdx.physics.box2d.Fixture;

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
	 * @param contactedFixture touched fixture
	 */
	void onBeginContact(Fixture contactedFixture);
	
	/**
	 * Called when this fixture stops touching something
	 *
	 * @param contactedFixture touched body
	 */
	void onEndContact(Fixture contactedFixture);
}
