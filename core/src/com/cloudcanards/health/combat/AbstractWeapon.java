package com.cloudcanards.health.combat;

import com.cloudcanards.behavior.Updateable;
import com.cloudcanards.box2d.CollisionFilters;
import com.cloudcanards.character.AbstractCharacter;
import com.cloudcanards.character.components.AbstractComponent;
import com.cloudcanards.health.Damageable;
import com.cloudcanards.util.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Timer;

/**
 * AbstractWeapon
 *
 * @author GahwonLee
 */
public abstract class AbstractWeapon extends AbstractComponent implements Updateable, Attackable
{
	private World world;
	private Body collisionBody;
	@Nullable
	private Joint joint;
	@Nullable
	private AttackType currentAttackType;
	private boolean faceRight; //true if attacking to the right
	private boolean stopScheduled;
	
	private RevoluteJointDef ramJointDef;
	
	public AbstractWeapon(AbstractCharacter character, World world, float frontLength, float backLength, float thickness, float density)
	{
		super(character);
		this.world = world;
		createCollisionBody(world, character.getPosition(), frontLength, backLength, thickness, density);
		
		createJointDefs();
	}
	
	private void createCollisionBody(World world, Vector2 position, float frontLength, float backLength, float thickness, float density)
	{
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(position);
		
		collisionBody = world.createBody(bodyDef);
		
		FixtureDef fixtureDef = new FixtureDef();
		PolygonShape shape = new PolygonShape();
		thickness /= 2;
		backLength *= -1;
		shape.set(new float[]{
			frontLength, thickness, frontLength, -thickness, -backLength, -thickness, -backLength, thickness
		});
		fixtureDef.shape = shape;
		fixtureDef.density = density;
		fixtureDef.isSensor = true;
		fixtureDef.filter.categoryBits = CollisionFilters.WEAPON;
		
		Fixture fixture = collisionBody.createFixture(fixtureDef);
		fixture.setUserData(new WeaponFixture(this));
		
		shape.dispose();
		Logger.logAll("Weapon mass is ", collisionBody.getMass(), "kg");
		setEnabled(false);
	}
	
	private void createJointDefs()
	{
		ramJointDef = new RevoluteJointDef();
		ramJointDef.bodyA = character.getBody();
		ramJointDef.localAnchorA.set(character.getBody().getLocalCenter());
		ramJointDef.bodyB = collisionBody;
		ramJointDef.referenceAngle = 0;//MathUtils.PI;
		ramJointDef.enableLimit = true;
		ramJointDef.lowerAngle = 0;
		ramJointDef.upperAngle = MathUtils.PI;
		ramJointDef.enableMotor = true;
		ramJointDef.maxMotorTorque = 400; //todo
	}
	
	private void ram()
	{
		if (faceRight)
		{
			collisionBody.setTransform(collisionBody.getPosition(), MathUtils.PI);
			ramJointDef.motorSpeed = -MathUtils.PI2;
		}
		else
		{
			ramJointDef.motorSpeed = MathUtils.PI2;
		}
		
		joint = world.createJoint(ramJointDef);
		
	}
	
	public void startAttack(AttackType attackType)
	{
		if (currentAttackType != null)
		{
			return;
		}
		
		setCurrentAttackType(attackType);
		collisionBody.setTransform(character.getPosition(), 0);
		setEnabled(true);
		
		faceRight = character.isFaceRight();
		stopScheduled = false;
		
		switch (attackType)
		{
			case RAM:
				ram();
				break;
			case PART:
				break;
			case STAB:
				break;
		}
	}
	
	private void stopAttack()
	{
		world.destroyJoint(joint);
		setEnabled(false);
		setCurrentAttackType(null);
	}
	
	protected void notifyCollision(@NotNull Damageable damageable)
	{
		if (currentAttackType == null)
		{
			return;
		}
		attack(damageable, currentAttackType);
	}
	
	@Override
	public void onAttackSuccessful(Damageable damageable, AttackType attackType)
	{
		if (damageable.damage(1))
		{
			if (!(damageable instanceof AbstractCharacter))
			{
				return;
			}
			
			AbstractCharacter character = (AbstractCharacter) damageable;
			
			character.getBody().applyLinearImpulse(
				(character.getPosition().x > this.character.getPosition().x ? 1 : -1) * 100 * collisionBody.getMass(),
				attackType != AttackType.RAM ? 0 :
					((character.getPosition().x > this.character.getPosition().x) == faceRight ? -1 : 1) * 50 * collisionBody.getMass(),
				character.getPosition().x, character.getPosition().y, true);
		}
	}
	
	@Override
	public void onAttackFailed(Damageable damageable, AttackType attackType)
	{
		Logger.log("Attack failed");
	}
	
	@Override
	public void update(float delta)
	{
		if (currentAttackType == AttackType.RAM)
		{
			assert joint != null;
			
			if (!stopScheduled)
			{
				if ((faceRight && MathUtils.isEqual(((RevoluteJoint) joint).getJointAngle(), 0, 0.1f)) ||
					(!faceRight && MathUtils.isEqual(((RevoluteJoint) joint).getJointAngle(), MathUtils.PI, 0.1f)))
				{
					stopScheduled = true;
					Timer.schedule(new Timer.Task()
					{
						@Override
						public void run()
						{
							stopAttack();
						}
					}, 0.2f);
				}
			}
			
		}
	}
	
	private void setEnabled(boolean enabled)
	{
		collisionBody.setActive(enabled);
	}
	
	private void setCurrentAttackType(@Nullable AttackType attackType)
	{
		currentAttackType = attackType;
		character.setCurrentAttackType(attackType);
	}
}
