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
	
	//physics
	private Body body;
	private World world;
	
	//movement
	private CharacterState state;
	private MovementType movementType;
	private float movementSpeed;
	private boolean faceRight;
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
		def.position.x = position.x;
		def.position.y = position.y;
		
		Body body = world.createBody(def);
		
		//middle
		PolygonShape poly = new PolygonShape();
		poly.setAsBox(radius, halfHeight - radius);
		Fixture playerPhysicsFixture = body.createFixture(poly, 1);
		playerPhysicsFixture.setFriction(0f);
		poly.dispose();
		
		//bottom
		CircleShape bottomCircle = new CircleShape();
		bottomCircle.setRadius(radius);
		bottomCircle.setPosition(new Vector2(0, -(halfHeight - radius)));
		Fixture bottomFixture = body.createFixture(bottomCircle, 0);
		bottomFixture.setFriction(1f);
		bottomCircle.dispose();
		
		//top
		CircleShape topCircle = new CircleShape();
		topCircle.setRadius(radius);
		topCircle.setPosition(new Vector2(0, halfHeight - radius));
		Fixture topFixture = body.createFixture(topCircle, 0);
		topFixture.setFriction(1f);
		topCircle.dispose();
		
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
	
	public void jump()
	{
		body.setLinearVelocity(body.getLinearVelocity().x, 0);
		Vector2 pos = body.getWorldCenter();
		body.applyLinearImpulse(0, 15, pos.x, pos.y, true);
	}
	
	@Override
	public void update(float delta)
	{
		if (movementDir != 0)
		{
			Vector2 pos = body.getWorldCenter();
			Vector2 vel = body.getLinearVelocity();
			
			if ((vel.x >= -movementSpeed && movementDir < 0) || (vel.x <= movementSpeed && movementDir > 0))
			{
				float desiredVel = movementDir * movementSpeed;
				float velChange = desiredVel - vel.x;
				float impulse = body.getMass() * CCMathUtils.lerp(vel.x, velChange, delta, 0.25f);
				
				body.applyLinearImpulse(impulse, 0, pos.x, pos.y, true);
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
