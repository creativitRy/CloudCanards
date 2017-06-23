package com.cloudcanards.character;

import com.cloudcanards.behavior.Renderable;
import com.cloudcanards.behavior.Updateable;
import com.cloudcanards.components.AbstractComponent;
import com.cloudcanards.loading.AbstractLoadAssetTask;
import com.cloudcanards.loading.Disposable;
import com.cloudcanards.loading.Loadable;
import com.cloudcanards.loading.ResourceManager;
import com.cloudcanards.util.CCMathUtils;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

/**
 * Character
 *
 * @author creativitRy
 */
public abstract class AbstractCharacter implements Loadable, Updateable, Renderable, Disposable
{
	//render
	private String atlasPath;
	private TextureAtlas atlas;
	private boolean faceRight;
	
	//physics
	private Body body;
	private World world;
	
	//movement
	private CharacterState state;
	/**
	 * -1 if in air, friction of ground if touching ground
	 */
	private float groundFriction;
	private MovementType movementType;
	private float movementSpeed;
	/**
	 * -1 is left, 0 is not moving, 1 is right
	 */
	private int movementDir;
	
	//components
	private Array<Updateable> updateableComponents = new Array<>();
	private Array<Renderable> renderableComponents = new Array<>();
	
	/**
	 * @param world
	 * @param position
	 * @param height
	 * @param diameter
	 */
	public AbstractCharacter(World world, Vector2 position, float height, float diameter, String atlasPath)
	{
		this.world = world;
		body = initPhysicsBody(position, height / 2f, diameter / 2f);
		body.setSleepingAllowed(false);
		
		//todo
		groundFriction = 1;
		
		this.atlasPath = atlasPath;
		faceRight = true;
		
		state = CharacterState.FALL;
		run();
	}
	
	/**
	 * Default shape is a capsule
	 *
	 * @param position
	 * @param halfHeight
	 * @param radius
	 * @return
	 */
	protected Body initPhysicsBody(Vector2 position, float halfHeight, float radius)
	{
		if (halfHeight < radius) throw new IllegalArgumentException("half height < radius");
		
		BodyDef def = new BodyDef();
		def.type = BodyDef.BodyType.DynamicBody;
		def.position.set(position);
		
		Body body = world.createBody(def);
		body.setLinearDamping(0.25f);
		
		//middle
		PolygonShape poly = new PolygonShape();
		//x1, y1, x2, y2, x3, y3, etc
		//this causes a bug where if you jump while trying to go up a ledge, you jump higher
		poly.set(new float[]{
			+radius, 2f * halfHeight, +radius, halfHeight / 2f, 0.75f * +radius, 0,
			-radius, 2f * halfHeight, -radius, halfHeight / 2f, 0.75f * -radius, 0});
		//poly.setAsBox(radius, halfHeight, new Vector2(0, halfHeight), 0);
		Fixture bodyFixture = body.createFixture(poly, 40);
		poly.dispose();
		bodyFixture.setFriction(0f);
		
		poly = new PolygonShape();
		poly.setAsBox(radius * 0.75f, 0.1f, new Vector2(0f, -0.05f), 0);
		Fixture bottomFixture = body.createFixture(poly, 0);
		poly.dispose();
		bottomFixture.setSensor(true);
		bottomFixture.setUserData(new CharacterGroundContact(this, halfHeight));
		
		body.setBullet(true);
		body.setFixedRotation(true);
		
		return body;
	}
	
	@Override
	public void load(ResourceManager resourceManager)
	{
		resourceManager.addTask(new AbstractLoadAssetTask()
		{
			@Override
			public void run()
			{
				resourceManager.getAssetManager().load(atlasPath, TextureAtlas.class);
			}
			
			@Override
			public void postRun()
			{
				atlas = resourceManager.getAssetManager().get(atlasPath);
			}
		});
		
		for (Updateable component : updateableComponents)
		{
			if (component instanceof Loadable)
				((Loadable) component).load(resourceManager);
		}
		for (Renderable component : renderableComponents)
		{
			if (component instanceof Loadable)
				((Loadable) component).load(resourceManager);
		}
	}
	
	public void addComponent(AbstractComponent component)
	{
		if (component instanceof Updateable)
			updateableComponents.add((Updateable) component);
		if (component instanceof Renderable)
			renderableComponents.add((Renderable) component);
	}
	
	/**
	 * Checks remove with == not equals
	 *
	 * @param component
	 * @return
	 */
	public boolean removeComponent(AbstractComponent component)
	{
		boolean temp = false;
		if (component instanceof Updateable)
		{
			updateableComponents.removeValue((Updateable) component, true);
			temp = true;
		}
		if (component instanceof Renderable)
		{
			renderableComponents.removeValue((Renderable) component, true);
			temp = true;
		}
		
		return temp;
	}
	
	public TextureAtlas getAtlas()
	{
		return atlas;
	}
	
	public Body getBody()
	{
		return body;
	}
	
	public World getWorld()
	{
		return world;
	}
	
	public CharacterState getState()
	{
		return state;
	}
	
	public MovementType getMovementType()
	{
		return movementType;
	}
	
	public void walk()
	{
		movementType = MovementType.WALK;
		updateSpeed();
	}
	
	public void run()
	{
		movementType = MovementType.RUN;
		updateSpeed();
	}
	
	public void sprint()
	{
		movementType = MovementType.SPRINT;
		updateSpeed();
	}
	
	/**
	 * Override this to change the speed to move when walking, running, sprinting
	 */
	protected void updateSpeed()
	{
		movementSpeed = movementType.getDefaultSpeed();
	}
	
	public int getMovementDir()
	{
		return movementDir;
	}
	
	public void setMovementDir(int movementDir)
	{
		if (movementDir == -1)
			faceRight = false;
		else if (movementDir == 1)
			faceRight = true;
		this.movementDir = movementDir;
	}
	
	public boolean isFaceRight()
	{
		return faceRight;
	}
	
	public boolean canJump()
	{
		if (isGrounded())
			return true;
		
		return false;
	}
	
	public void jump()
	{
		body.setLinearVelocity(body.getLinearVelocity().x, 0);
		Vector2 pos = body.getWorldCenter();
		//body.setLinearVelocity(body.getLinearVelocity().x, 15);
		body.applyLinearImpulse(0, 15 * body.getMass(), pos.x, pos.y, true);
	}
	
	public void stopJump()
	{
		body.setGravityScale(2f);
	}
	
	/**
	 * Set horizontal velocity by lerping from prev velocity
	 *
	 * @param velocity
	 * @param delta    dt
	 * @param slowness float in range (0,1) where 0.1 is fast and 0.9 is slow
	 */
	private void setHorizontalVel(float velocity, float delta, float slowness)
	{
		Vector2 vel = body.getLinearVelocity();
		
		float desiredVel = CCMathUtils.lerp(vel.x, velocity, delta, slowness);
		float velChange = desiredVel - vel.x;
		float impulse = body.getMass() * velChange;
		
		Vector2 pos = body.getWorldCenter();
		body.applyLinearImpulse(impulse, 0, pos.x, pos.y, true);
	}
	
	@Override
	public void update(float delta)
	{
		float slowness = 0.25f;
		if (!isGrounded())
			slowness = 0.75f;
		
		Vector2 vel = body.getLinearVelocity();
		
		if (movementDir != 0)
		{
			
			if ((vel.x >= -movementSpeed && movementDir < 0) || (vel.x <= movementSpeed && movementDir > 0))
			{
				setHorizontalVel(movementDir * movementSpeed, delta, slowness);
			}
		}
		else
		{
			setHorizontalVel(0, delta, slowness);
		}
		
		if (vel.y <= 0 && body.getGravityScale() == 1f)
			body.setGravityScale(2f);
		else if (vel.y > 0 && body.getGravityScale() == 2f)
			body.setGravityScale(1f);
		
		for (Updateable component : updateableComponents)
		{
			component.update(delta);
		}
	}
	
	public boolean isGrounded()
	{
		return groundFriction != -1;
	}
	
	public float getGroundFriction()
	{
		return groundFriction;
	}
	
	public void setGroundFriction(float friction)
	{
		this.groundFriction = friction;
	}
	
	@Override
	public void render(SpriteBatch batch, float delta)
	{
		for (Renderable component : renderableComponents)
		{
			component.render(batch, delta);
		}
	}
	
	@Override
	public void dispose(ResourceManager resourceManager)
	{
		resourceManager.getAssetManager().unload(atlasPath);
		for (Updateable component : updateableComponents)
		{
			if (component instanceof Disposable)
				((Disposable) component).dispose(resourceManager);
		}
		for (Renderable component : renderableComponents)
		{
			if (component instanceof Disposable)
				((Disposable) component).dispose(resourceManager);
		}
	}
}
