package com.cloudcanards.box2d;

import com.badlogic.gdx.physics.box2d.*;

/**
 * Listens for collisions between fixtures with the collidable user data. Register this on world
 *
 * @author creativitRy
 */
public class WorldContactListener implements ContactListener
{
	@Override
	public void beginContact(Contact contact)
	{
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		//contact.getFriction() = sqrt(frictionA * frictionB)
		//I lov c++
		
		if (fixtureA.getUserData() instanceof Collidable)
			((Collidable) fixtureA.getUserData()).onBeginContact();
		if (fixtureA.getUserData() instanceof AdvancedCollidable)
			((AdvancedCollidable) fixtureA.getUserData()).onBeginContact(fixtureB);
		
		if (fixtureB.getUserData() instanceof Collidable)
			((Collidable) fixtureB.getUserData()).onBeginContact();
		if (fixtureB.getUserData() instanceof AdvancedCollidable)
			((AdvancedCollidable) fixtureB.getUserData()).onBeginContact(fixtureA);
	}
	
	@Override
	public void endContact(Contact contact)
	{
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		if (fixtureA.getUserData() instanceof Collidable)
			((Collidable) fixtureA.getUserData()).onEndContact();
		if (fixtureA.getUserData() instanceof AdvancedCollidable)
			((AdvancedCollidable) fixtureA.getUserData()).onEndContact(fixtureB);
		
		if (fixtureB.getUserData() instanceof Collidable)
			((Collidable) fixtureB.getUserData()).onEndContact();
		if (fixtureB.getUserData() instanceof AdvancedCollidable)
			((AdvancedCollidable) fixtureB.getUserData()).onEndContact(fixtureA);
	}
	
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
	 */
	@Override
	public void preSolve(Contact contact, Manifold oldManifold)
	{
		Fixture fixtureA = contact.getFixtureA();
		Fixture fixtureB = contact.getFixtureB();
		
		if (fixtureA.getUserData() instanceof PreSolvable)
			((PreSolvable) fixtureA.getUserData()).preSolve(contact, oldManifold);
		if (fixtureB.getUserData() instanceof PreSolvable)
			((PreSolvable) fixtureB.getUserData()).preSolve(contact, oldManifold);
	}
	
	/**
	 * This lets you inspect a contact after the solver is finished. This is useful for inspecting impulses.
	 * <p>
	 * Note: the contact manifold does not include time of impact impulses, which can be arbitrarily large if the
	 * sub-step is small. Hence the impulse is provided explicitly in a separate data structure.
	 * <p>
	 * Note: this is only called for contacts that are touching, solid, and awake.
	 */
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse)
	{
	
	}
}
