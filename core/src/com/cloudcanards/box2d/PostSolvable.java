package com.cloudcanards.box2d;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;

/**
 * PostSolvable
 *
 * @author creativitRy
 */
public interface PostSolvable
{
	/**
	 * This lets you inspect a contact after the solver is finished. This is useful for inspecting impulses.
	 * <p>
	 * Note: the contact manifold does not include time of impact impulses, which can be arbitrarily large if the
	 * sub-step is small. Hence the impulse is provided explicitly in a separate data structure.
	 * <p>
	 * Note: this is only called for contacts that are touching, solid, and awake.
	 * @param impulse normal = impulse to push bodies away to stop contact, tangent = friction
	 */
	void postSolve(Contact contact, ContactImpulse impulse);
}
