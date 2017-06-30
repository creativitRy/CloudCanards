package com.cloudcanards.grapple;

import com.cloudcanards.box2d.CollisionFilters;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * StaticTarget
 *
 * @author creativitRy
 */
public class StaticTarget implements Targetable
{
	private Vector2 position;
	private Body body;
	
	public StaticTarget(float x, float y, World world)
	{
		createPhysicsBody(x, y, world);
	}
	
	private void createPhysicsBody(float x, float y, World world)
	{
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.StaticBody;
		bodyDef.position.set(x, y);
		
		Body body = world.createBody(bodyDef);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = new PolygonShape();
		((PolygonShape) fixtureDef.shape).setAsBox(0.5f, 0.5f);
		fixtureDef.friction = 0;
		fixtureDef.filter.categoryBits = 0;
		fixtureDef.filter.maskBits = CollisionFilters.NONE;
		
		body.createFixture(fixtureDef);
		
		fixtureDef.shape.dispose();
		this.body = body;
		this.position = bodyDef.position;
	}
	
	@Override
	public Vector2 getPosition()
	{
		return position;
	}
	
	@Override
	public Body getBody()
	{
		return body;
	}
}
