package com.cloudcanards.character;

import com.cloudcanards.behavior.Updateable;
import com.cloudcanards.box2d.CollisionFilters;
import com.cloudcanards.components.AbstractComponent;
import com.cloudcanards.graphics.Renderable;
import com.cloudcanards.grapple.Targetable;
import com.cloudcanards.loading.AbstractLoadAssetTask;
import com.cloudcanards.loading.Disposable;
import com.cloudcanards.loading.Loadable;
import com.cloudcanards.loading.ResourceManager;
import com.cloudcanards.screens.GameScreen;
import com.cloudcanards.util.MathUtil;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

/**
 * Character
 *
 * @author creativitRy
 */
public abstract class AbstractCharacter implements Loadable, Updateable, Renderable, Disposable, Targetable
{
	private static final int JUMP_VELOCITY = 17;
	
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
	private float maxSpeedChange = 10f;
	private Vector2 platformVelocity; //if char is standing on a moving platform this is not 0,0
	/**
	 * -1 is left, 0 is not moving, 1 is right
	 */
	private int movementDir;
	
	//components
	private Array<Updateable> updateableComponents;
	private Array<AbstractComponent> miscComponents;
	
	/**
	 * @param world
	 * @param position
	 * @param height
	 * @param diameter
	 */
	public AbstractCharacter(World world, Vector2 position, float height, float diameter, String atlasPath)
	{
		this.world = world;
		body = createPhysicsBody(position, height / 2f, diameter / 2f);
		body.setSleepingAllowed(false);
		
		//todo
		groundFriction = -1;
		
		this.atlasPath = atlasPath;
		faceRight = true;
		
		platformVelocity = new Vector2();
		
		updateState();
		run();
		
		updateableComponents = new Array<>();
		miscComponents = new Array<>();
	}
	
	/**
	 * Default shape is a capsule
	 *
	 * @param position
	 * @param halfHeight
	 * @param radius
	 * @return
	 */
	protected Body createPhysicsBody(Vector2 position, float halfHeight, float radius)
	{
		if (halfHeight < radius)
		{
			throw new IllegalArgumentException("half height < radius");
		}
		
		BodyDef def = new BodyDef();
		def.type = BodyDef.BodyType.DynamicBody;
		def.position.set(position);
		
		Body body = world.createBody(def);
		
		//middle
		PolygonShape poly = new PolygonShape();
		//x1, y1, x2, y2, x3, y3, etc
		//this causes a bug where if you jump while trying to go up a ledge, you jump higher
		poly.set(new float[]{
			+radius, 2f * halfHeight, +radius, halfHeight / 2f, 0.9f * +radius, 0,
			-radius, 2f * halfHeight, -radius, halfHeight / 2f, 0.9f * -radius, 0});
		//poly.setAsBox(radius, halfHeight, new Vector2(0, halfHeight), 0);
		FixtureDef bodyFixtureDef = new FixtureDef();
		bodyFixtureDef.shape = poly;
		bodyFixtureDef.density = 4f;
		bodyFixtureDef.filter.categoryBits = CollisionFilters.CHARACTER;
		Fixture bodyFixture = body.createFixture(bodyFixtureDef);
		poly.dispose();
		bodyFixture.setFriction(0f);
		
		poly = new PolygonShape();
		poly.setAsBox(radius * 0.9f, 0.05f, new Vector2(0f, -0.025f), 0);
		bodyFixtureDef.shape = poly;
		bodyFixtureDef.density = 0f;
		bodyFixtureDef.filter.categoryBits = CollisionFilters.CHARACTER;
		Fixture bottomFixture = body.createFixture(bodyFixtureDef);
		poly.dispose();
		bottomFixture.setSensor(true);
		bottomFixture.setUserData(new CharacterGroundContact(this, halfHeight));
		
		//body.setBullet(true);
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
			{
				((Loadable) component).load(resourceManager);
			}
		}
		for (AbstractComponent component : miscComponents)
		{
			if (component instanceof Loadable)
			{
				((Loadable) component).load(resourceManager);
			}
		}
	}
	
	public void addComponent(AbstractComponent component)
	{
		if (component instanceof Renderable)
		{
			registerRenderable((Renderable) component);
		}
		
		if (component instanceof Updateable)
		{
			updateableComponents.add((Updateable) component);
		}
		else
		{
			miscComponents.add(component);
		}
	}
	
	private void registerRenderable(Renderable component)
	{
		GameScreen.getInstance().getRenderableManager().add(component);
	}
	
	/**
	 * Checks remove with == not equals
	 *
	 * @param component
	 * @return
	 */
	public boolean removeComponent(AbstractComponent component)
	{
		if (component instanceof Updateable)
		{
			return updateableComponents.removeValue((Updateable) component, true);
		}
		else
		{
			boolean temp = miscComponents.removeValue(component, true);
			if (component instanceof Renderable)
			{
				unregisterRenderable(((Renderable) component));
			}
			return temp;
		}
	}
	
	private void unregisterRenderable(Renderable component)
	{
		GameScreen.getInstance().getRenderableManager().remove(component);
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
		if (movementDir != 0)
		{
			if (movementDir == -1)
			{
				faceRight = false;
			}
			else if (movementDir == 1)
			{
				faceRight = true;
			}
		}
		
		this.movementDir = movementDir;
		
		if (isGrounded() && canSetState())
		{
			updateGroundedState();
		}
	}
	
	public boolean canJump()
	{
		if (isGrounded())
		{
			return true;
		}
		
		//fixme
		return true;
	}
	
	public void jump()
	{
		body.setLinearDamping(0f);
		body.setLinearVelocity(body.getLinearVelocity().x, 0);
		Vector2 pos = body.getWorldCenter();
		//body.setLinearVelocity(body.getLinearVelocity().x, 15);
		body.applyLinearImpulse(0, JUMP_VELOCITY * body.getMass(), pos.x, pos.y, true);
		
		if (canSetState())
		{
			setState(CharacterState.JUMP);
		}
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
		
		float desiredVel = MathUtil.lerp(vel.x, velocity, delta, slowness);
		float velChange = desiredVel - vel.x;
		if (velChange > maxSpeedChange)
		{
			velChange = maxSpeedChange;
		}
		else if (velChange < -maxSpeedChange)
		{
			velChange = -maxSpeedChange;
		}
		float impulse = body.getMass() * velChange;
		
		Vector2 pos = body.getWorldCenter();
		body.applyLinearImpulse(impulse, 0, pos.x, pos.y, true);
	}
	
	private boolean canSetState()
	{
		return state != CharacterState.GRAPPLE;
	}
	
	public void updateState()
	{
		if (isGrounded())
		{
			updateGroundedState();
		}
		else
		{
			if (body.getLinearVelocity().y > 0)
			{
				setState(CharacterState.JUMP);
			}
			else
			{
				setState(CharacterState.FALL);
			}
		}
	}
	
	private void updateGroundedState()
	{
		if (movementDir != 0)
		{
			setState(CharacterState.RUN);
		}
		else if (MathUtils.isEqual(body.getLinearVelocity().x, platformVelocity.x, 0.01f) &&
			state != CharacterState.JUMP)
		{
			setState(CharacterState.IDLE);
		}
	}
	
	private void updateLinearDamping()
	{
		if (state == CharacterState.IDLE)
		{
			body.setLinearDamping(9999f);
		}
		else
		{
			body.setLinearDamping(0.25f);
		}
	}
	
	@Override
	public void update(float delta)
	{
		Vector2 vel = body.getLinearVelocity();
		
		float slowness = 0.25f;
		if (isGrounded())
		{
			if (canSetState())
			{
				updateGroundedState();
			}
		}
		else
		{
			slowness = 0.75f;
			
			if (canSetState())
			{
				if (state == CharacterState.JUMP)
				{
					if (vel.y <= 0)
					{
						setState(CharacterState.FALL);
					}
				}
				else
				{
					setState(CharacterState.FALL);
				}
			}
		}
		
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
		
		//todo: don't set custom gravity when swinging on rope
		if (state == CharacterState.GRAPPLE)
		{
			body.setGravityScale(1f);
		}
		else
		{
			if (vel.y <= 0)
			{
				if (body.getGravityScale() != 2f)
				{
					body.setGravityScale(2f);
				}
			}
			else if (vel.y > 0)
			{
				if (body.getGravityScale() != 1f)
				{
					body.setGravityScale(1f);
				}
			}
		}
		
		for (Updateable component : updateableComponents)
		{
			component.update(delta);
		}
	}
	
	@Override
	public void render(SpriteBatch batch, float delta)
	{
	}
	
	@Override
	public void dispose(ResourceManager resourceManager)
	{
		resourceManager.getAssetManager().unload(atlasPath);
		for (Updateable component : updateableComponents)
		{
			if (component instanceof Disposable)
			{
				((Disposable) component).dispose(resourceManager);
			}
		}
		for (AbstractComponent component : miscComponents)
		{
			if (component instanceof Disposable)
			{
				((Disposable) component).dispose(resourceManager);
			}
		}
	}
	
	public TextureAtlas getAtlas()
	{
		return atlas;
	}
	
	@Override
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
	
	public boolean isFaceRight()
	{
		return faceRight;
	}
	
	@Override
	public Vector2 getPosition()
	{
		return body.getWorldCenter();
	}
	
	public void setState(CharacterState state)
	{
		this.state = state;
		
		updateLinearDamping();
	}
}
