package com.cloudcanards.health.combat;

import com.cloudcanards.box2d.CollisionFilters;
import com.cloudcanards.character.AbstractCharacter;
import com.cloudcanards.character.components.AbstractComponent;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * AbstractWeapon
 *
 * @author GahwonLee
 */
public abstract class AbstractWeapon extends AbstractComponent
{
	private Body collisionBody;
	
	public AbstractWeapon(AbstractCharacter character, World world, float frontLength, float backLength, float thickness, float density)
	{
		super(character);
		createCollisionBody(world, character.getPosition(), frontLength, backLength, thickness, density);
	}
	
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
		fixtureDef.isSensor = true;
		fixtureDef.filter.categoryBits = CollisionFilters.WEAPON;
		
		Fixture fixture = collisionBody.createFixture(fixtureDef);
		fixture.setUserData(new WeaponFixture(this));
		
		shape.dispose();
		
		setEnabled(false);
	}
	
	private void setEnabled(boolean enabled)
	{
		collisionBody.setActive(enabled);
	}
}
