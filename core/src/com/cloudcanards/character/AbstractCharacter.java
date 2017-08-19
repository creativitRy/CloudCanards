package com.cloudcanards.character;

import com.cloudcanards.behavior.Updateable;
import com.cloudcanards.box2d.CollisionFilters;
import com.cloudcanards.character.components.AbstractComponent;
import com.cloudcanards.character.components.GrappleComponent;
import com.cloudcanards.graphics.Renderable;
import com.cloudcanards.grapple.Targetable;
import com.cloudcanards.health.Damageable;
import com.cloudcanards.health.combat.AttackType;
import com.cloudcanards.health.combat.CombatTeam;
import com.cloudcanards.loading.AbstractLoadAssetTask;
import com.cloudcanards.loading.Disposable;
import com.cloudcanards.loading.Loadable;
import com.cloudcanards.loading.ResourceManager;
import com.cloudcanards.screens.GameScreen;
import com.cloudcanards.ui.FloatingHealthBar;
import com.cloudcanards.util.Logger;
import com.cloudcanards.util.MathUtil;
import org.jetbrains.annotations.Nullable;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
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
public abstract class AbstractCharacter implements Loadable, Updateable, Renderable, Disposable, Targetable, Damageable
{
	private static final int JUMP_VELOCITY = 17;
	
	//render
	private String atlasPath;
	private TextureAtlas atlas;
	private boolean faceRight;
	
	//physics
	private Body body;
	private World world;
	private Body graphicsBody;
	
	//movement
	private DefaultStateMachine<AbstractCharacter, CharacterState> stateMachine;
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
	private CharacterJumpContact jumpContact;
	private boolean jump;
	private boolean stopJump;
	
	//health
	private int health;
	private CombatTeam combatTeam;
	@Nullable
	private AttackType currentAttackType;
	
	//components
	private Array<Updateable> updateableComponents;
	private Array<AbstractComponent> miscComponents;
	private GrappleComponent grappleComponent;
	
	
	public AbstractCharacter(World world, Vector2 position, float height, float diameter, String atlasPath, CombatTeam combatTeam)
	{
		this.world = world;
		createPhysicsBody(position, height / 2f, diameter / 2f);
		createGraphicsBody();
		
		//todo
		groundFriction = -1;
		
		this.atlasPath = atlasPath;
		faceRight = true;
		
		platformVelocity = new Vector2();
		
		stateMachine = new DefaultStateMachine<>(this);
		updateState();
		run();
		
		resetHealth();
		this.combatTeam = combatTeam;
		
		updateableComponents = new Array<>();
		miscComponents = new Array<>();
	}
	
	/**
	 * Default shape is a capsule
	 *
	 * @param position
	 * @param halfHeight
	 * @param radius
	 */
	protected void createPhysicsBody(Vector2 position, float halfHeight, float radius)
	{
		if (halfHeight < radius)
		{
			throw new IllegalArgumentException("half height < radius");
		}
		
		BodyDef def = new BodyDef();
		def.type = BodyDef.BodyType.DynamicBody;
		def.position.set(position);
		
		body = world.createBody(def);
		
		body.setUserData(this);
		body.setBullet(true);
		body.setFixedRotation(true);
		body.setSleepingAllowed(false);
		
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
		
		poly = new PolygonShape();
		poly.setAsBox(radius * 1.5f, 0.2f, new Vector2(0f, -0.05f), 0);
		bodyFixtureDef.shape = poly;
		bodyFixtureDef.density = 0f;
		bodyFixtureDef.filter.categoryBits = CollisionFilters.CHARACTER;
		Fixture jumpFixture = body.createFixture(bodyFixtureDef);
		poly.dispose();
		jumpFixture.setSensor(true);
		jumpContact = new CharacterJumpContact(this);
		jumpFixture.setUserData(jumpContact);
	}
	
	private void createGraphicsBody()
	{
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.KinematicBody;
		bodyDef.position.set(body.getPosition());
		
		graphicsBody = world.createBody(bodyDef);
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
		
		GameScreen.getInstance().getUiStage().addActor(new FloatingHealthBar(this));
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
		
		if (component instanceof GrappleComponent)
			grappleComponent = (GrappleComponent) component;
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
	}
	
	public boolean canJump()
	{
		if (jumpContact.canJump())
		{
			//todo: https://github.com/creativitRy/CloudCanards/issues/9#issuecomment-314543597
			//return true;
		}
		if (isGrounded())
		{
			return true;
		}
		
		//fixme
		return true;
	}
	
	public void jump()
	{
		jump = true;
	}
	
	public void stopJump()
	{
		stopJump = true;
		//todo: using a new field, move this to the update thread along with line 408
		
	}
	
	/**
	 * Set horizontal velocity by lerping from prev velocity
	 *
	 * @param velocity
	 * @param delta    dt
	 * @param slowness float in range (0,1) where 0.1 is fast and 0.9 is slow
	 */
	void setHorizontalVel(float velocity, float delta, float slowness)
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
		return stateMachine.getCurrentState() != CharacterState.GRAPPLE;
	}
	
	public void updateState()
	{
		if (isGrounded())
		{
			stateMachine.changeState(CharacterState.IDLE);
		}
		else
		{
			stateMachine.changeState(CharacterState.FALL);
		}
	}
	
	@Override
	public void update(float delta)
	{
		stateMachine.update();
		
		if (isJumping())
		{
			if (canJump() && canSetState())
			{
				stateMachine.changeState(CharacterState.JUMP);
			}
		}
		
		endJump();
		endStopJump();
		
		graphicsBody.setLinearDamping(body.getLinearDamping());
		graphicsBody.setTransform(body.getPosition(), 0);
		
		for (Updateable component : updateableComponents)
		{
			component.update(delta);
		}
	}
	
	/**
	 * Called every time before the physics world updates
	 */
	public void physicsStep()
	{
		if (body.getPosition().y < -5f)
		{
			//dead
			damage(getHealth() + 1);
		}
		graphicsBody.setLinearVelocity(body.getLinearVelocity());
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
	
	@Override
	public int getHealth()
	{
		return health;
	}
	
	@Override
	public void setHealth(int health)
	{
		this.health = health;
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
	
	public DefaultStateMachine<AbstractCharacter, CharacterState> getStateMachine()
	{
		return stateMachine;
	}
	
	public Vector2 getPlatformVelocity()
	{
		return platformVelocity;
	}
	
	CharacterJumpContact getJumpContact()
	{
		return jumpContact;
	}
	
	boolean isJumping()
	{
		return jump;
	}
	
	void endJump()
	{
		this.jump = false;
	}
	
	boolean isStopJumping()
	{
		return stopJump;
	}
	
	void endStopJump()
	{
		this.stopJump = false;
	}
	
	public float getMovementSpeed()
	{
		return movementSpeed;
	}
	
	public Body getGraphicsBody()
	{
		return graphicsBody;
	}
	
	public GrappleComponent getGrappleComponent()
	{
		if (grappleComponent == null)
		{
			Logger.setLevel(Logger.ERROR);
			Logger.log("Grapple component requested when it is null");
			Logger.resetLevel();
		}
		return grappleComponent;
	}
	
	@Override
	public AttackType getCurrentAttackType()
	{
		return currentAttackType;
	}
}
