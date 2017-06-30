package com.cloudcanards.box2d;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Manifold;

/**
 * PreSolvable
 *
 * @author creativitRy
 */
public interface PreSolvable
{
	/**
	 * This is called after a contact is updated. This allows you to inspect a contact before it goes to the solver. If
	 * you are careful, you can modify the contact manifold (e.g. disable contact). A copy of the old manifold is
	 * provided so that you can detect changes.
	 * <p>
	 * Note: this is called only for awake bodies.
	 * <p>
	 * Note: this is called even when the number of contact points is zero.
	 * <p>
	 * Note: this is not called for sensors.
	 * <p>
	 * Note: if you set the number of contact points to zero, you will not get an EndContact callback. However, you may
	 * get a BeginContact callback the next step.
	 *
	 * @param oldManifold collision manifold of the previous time step
	 */
	void preSolve(Contact contact, Manifold oldManifold);
}
