package com.cloudcanards.health.combat;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * AbstractWeapon
 *
 * @author GahwonLee
 */
public abstract class AbstractWeapon
{
	private Body collisionBody;
	
	private void createCollisionBody(World world, Vector2 position, float frontLength, float backLength, float thickness, float density)
	{
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.KinematicBody;
		bodyDef.position.set(position);
		
		collisionBody = world.createBody(bodyDef);
		
		FixtureDef fixtureDef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		thickness /= 2;
		backLength *= -1;
		shape.set(new float[]{
			frontLength, thickness, frontLength, -thickness, backLength, -thickness, backLength, thickness
		});
		fixtureDef.shape = shape;
		fixtureDef.density = density;
		//todo: set filter to only damage matching team and not friendly fire
		collisionBody.createFixture(fixtureDef);
		
		shape.dispose();
	}
}
