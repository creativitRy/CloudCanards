package com.cloudcanards.health.combat;

import com.cloudcanards.behavior.Updateable;
import com.cloudcanards.box2d.CollisionFilters;
import com.cloudcanards.character.AbstractCharacter;
import com.cloudcanards.graphics.Renderable;
import com.cloudcanards.health.Damageable;
import com.cloudcanards.util.DebugRenderUtil;
import com.cloudcanards.util.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Timer;

/**
 * AbstractWeapon
 *
 * @author GahwonLee
 */
public abstract class AbstractWeapon implements Updateable, Attackable, Renderable
{
	private final AbstractCharacter character;
	
	private World world;
	private Body collisionBody;
	@Nullable
	private Joint joint;
	
	private final float frontLength;
	private final float backLength;
	
	@Nullable
	private AttackType currentAttackType;
	private boolean faceRight; //true if attacking to the right
	private boolean stopScheduled;
	
	private RevoluteJointDef ramJointDef;
	private PrismaticJointDef stabJointDef;
	
	public AbstractWeapon(AbstractCharacter character, World world, float frontLength, float backLength, float thickness, float density)
	{
		this.character = character;
		this.world = world;
		
		this.frontLength = frontLength;
		this.backLength = backLength;
		temp1 = new Vector2(frontLength, 0);
		temp2 = new Vector2(-backLength, 0);
		
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
			frontLength, thickness, frontLength, -thickness, backLength, -thickness, backLength, thickness
		});
		fixtureDef.shape = shape;
		fixtureDef.density = density;
		fixtureDef.isSensor = true;
		fixtureDef.filter.categoryBits = CollisionFilters.WEAPON;
		
		Fixture fixture = collisionBody.createFixture(fixtureDef);
		fixture.setUserData(new WeaponFixture(this));
		
		shape.dispose();
		
		Logger.setLevel(Logger.DEBUG);
		Logger.logAll("Weapon mass is ", collisionBody.getMass(), "kg");
		Logger.resetLevel();
		
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
		
		
		stabJointDef = new PrismaticJointDef();
		stabJointDef.bodyA = character.getBody();
		stabJointDef.localAnchorA.set(character.getBody().getLocalCenter());
		stabJointDef.bodyB = collisionBody;
		stabJointDef.enableLimit = true;
		stabJointDef.lowerTranslation = -frontLength;
		stabJointDef.upperTranslation = backLength;
		stabJointDef.enableMotor = true;
		stabJointDef.maxMotorForce = 200; //todo
		
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
	
	private void part()
	{
		stopScheduled = true;
		
		collisionBody.setGravityScale(0f);
		
		if (!faceRight)
		{
			collisionBody.setTransform(character.getPosition(), MathUtils.PI);
		}
		
		Timer.schedule(new Timer.Task()
		{
			@Override
			public void run()
			{
				collisionBody.setTransform(character.getPosition(), faceRight ? MathUtils.PI : 0);
				Timer.schedule(new Timer.Task()
				{
					@Override
					public void run()
					{
						collisionBody.setGravityScale(1f);
						stopAttack();
					}
				}, 0.5f);
			}
		}, 0.5f);
	}
	
	private void stab()
	{
		if (faceRight)
		{
			stabJointDef.referenceAngle = 0;
			stabJointDef.localAxisA.set(1, 0);
			collisionBody.setLinearVelocity(character.getBody().getLinearVelocity());
			
		}
		else
		{
			collisionBody.setTransform(collisionBody.getPosition(), MathUtils.PI);
			stabJointDef.referenceAngle = MathUtils.PI;
			stabJointDef.localAxisA.set(-1, 0);
			collisionBody.setLinearVelocity(character.getBody().getLinearVelocity().scl(-1));
		}
		
		stabJointDef.motorSpeed = -4;
		
		joint = world.createJoint(stabJointDef);
		
		stopScheduled = true;
		Timer.schedule(new Timer.Task()
		{
			@Override
			public void run()
			{
				((PrismaticJoint) joint).setMotorSpeed(100 * collisionBody.getMass());
				Timer.schedule(new Timer.Task()
				{
					@Override
					public void run()
					{
						stopAttack();
					}
				}, 0.5f);
			}
		}, 0.5f);
		
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
				part();
				break;
			case STAB:
				stab();
				break;
		}
	}
	
	private void stopAttack()
	{
		if (joint != null)
		{
			world.destroyJoint(joint);
			joint = null;
		}
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
		else if (currentAttackType == AttackType.PART)
		{
			assert joint != null;
			
			collisionBody.setTransform(character.getPosition(), collisionBody.getAngle());
			collisionBody.setLinearVelocity(character.getBody().getLinearVelocity());
		}
		else if (currentAttackType == AttackType.STAB)
		{
			assert joint != null;
		}
	}
	
	private Vector2 temp1;
	private Vector2 temp2;
	private Vector2 temp3 = new Vector2();
	
	@Override
	public void render(SpriteBatch batch, float delta)
	{
		batch.flush();
		if (currentAttackType != null)
		{
			temp3.set(collisionBody.getWorldPoint(temp1));
			DebugRenderUtil.getInstance().drawLine(temp3, collisionBody.getWorldPoint(temp2));
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
	
	public AbstractCharacter getCharacter()
	{
		return character;
	}
}
