package com.cloudcanards.character.components;

import com.cloudcanards.assets.Assets;
import com.cloudcanards.behavior.Updateable;
import com.cloudcanards.box2d.CollisionFilters;
import com.cloudcanards.character.AbstractCharacter;
import com.cloudcanards.character.CharacterState;
import com.cloudcanards.grapple.GrappleRope;
import com.cloudcanards.grapple.Targetable;
import com.cloudcanards.loading.AbstractLoadAssetTask;
import com.cloudcanards.loading.Loadable;
import com.cloudcanards.loading.ResourceManager;
import com.cloudcanards.screens.GameScreen;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

/**
 * GrappleComponent
 *
 * @author creativitRy
 */
public class GrappleComponent extends AbstractComponent implements Loadable, Updateable
{
	private GrappleRope rope;
	private NinePatch texture;
	private Body body;
	private Targetable target;
	
	public GrappleComponent(AbstractCharacter character, World world)
	{
		super(character);
		
		createPhysicsBody(world);
	}
	
	private void createPhysicsBody(World world)
	{
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(character.getPosition());
		
		body = world.createBody(bodyDef);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = new CircleShape();
		fixtureDef.shape.setRadius(0.25f);
		fixtureDef.filter.categoryBits = CollisionFilters.CHARACTER;
		fixtureDef.density = 0f;
		
		body.createFixture(fixtureDef);
		
		RevoluteJointDef jointDef = new RevoluteJointDef();
		jointDef.initialize(character.getBody(), body, character.getPosition());
		
		world.createJoint(jointDef);
	}
	
	@Override
	public void load(ResourceManager resourceManager)
	{
		resourceManager.addTask(new AbstractLoadAssetTask()
		{
			@Override
			public void run()
			{
				resourceManager.getAssetManager().load(Assets.DIR + Assets.ROPE, TextureAtlas.class);
			}
			
			@Override
			public void postRun()
			{
				texture = resourceManager.getAssetManager().get(Assets.DIR + Assets.ROPE, TextureAtlas.class)
					.createPatch("white");
			}
		});
	}
	
	private float temp;
	
	public boolean shoot(Targetable target)
	{
		System.out.println("test");
		if (target == null)
			return false;
		
		this.target = target;
		
		if (rope != null)
			return false;
		
		rope = new GrappleRope(this, target, GameScreen.getInstance().getWorld(), texture);
		
		return true;
	}
	
	public boolean retract()
	{
		if (rope == null || rope.getState() == 2)
			return false;
		
		return true;
	}
	
	@Override
	public void update(float delta)
	{
		if (rope != null)
		{
			rope.update(delta);
			
			
			return;
		}
		/*else
			temp += delta;
		
		if (temp > 1)
		{
			shoot(GameScreen.getInstance().getStaticGrappleTargets().get(0));
		}*/
	}
	
	public Vector2 getPosition()
	{
		return character.getPosition();
	}
	
	public Body getBody()
	{
		return body;
	}
	
	public void setGrappling()
	{
		character.getStateMachine().changeState(CharacterState.GRAPPLE);
	}
	
	public void setRetractGrapple()
	{
		character.updateState();
	}
}
