package com.cloudcanards.components;

import com.cloudcanards.assets.Assets;
import com.cloudcanards.behavior.Updateable;
import com.cloudcanards.box2d.CollisionFilters;
import com.cloudcanards.character.AbstractCharacter;
import com.cloudcanards.graphics.WarpedTexture;
import com.cloudcanards.loading.AbstractLoadAssetTask;
import com.cloudcanards.loading.Loadable;
import com.cloudcanards.loading.ResourceManager;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;

/**
 * ScarfComponent
 *
 * @author creativitRy
 */
public class ScarfComponent extends AbstractRenderableComponent implements Loadable, Updateable
{
	private static final int WIDTH_DIVISIONS = 2;
	private static final int HEIGHT_DIVISIONS = 8;
	private static final int WIDTH = 1;
	private static final int HEIGHT = 2;
	private static final float JOINT_SLACK = 0.005f;
	
	private World world;
	private Body[][] bodies;
	
	private WarpedTexture texture;
	private Vector2[][] grid;
	
	public ScarfComponent(AbstractCharacter character, World world)
	{
		super(character);
		
		this.world = world;
		createPhysicsBodies();
	}
	
	private void createPhysicsBodies()
	{
		bodies = new Body[HEIGHT_DIVISIONS][WIDTH_DIVISIONS];
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 0.2f;
		fixtureDef.filter.categoryBits = CollisionFilters.CHARACTER;
		fixtureDef.filter.maskBits = CollisionFilters.blacklist(CollisionFilters.CHARACTER);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.05f, 0.05f);
		fixtureDef.shape = shape;
		
		final float widthSeparation = (float) WIDTH / WIDTH_DIVISIONS;
		final float heightSeparation = (float) HEIGHT / HEIGHT_DIVISIONS;
		
		RopeJointDef horizJointDef = new RopeJointDef();
		horizJointDef.maxLength = widthSeparation + JOINT_SLACK;
		horizJointDef.localAnchorA.set(0, 0);
		horizJointDef.localAnchorB.set(0, 0);
		
		RopeJointDef vertJointDef = new RopeJointDef();
		vertJointDef.maxLength = heightSeparation + JOINT_SLACK;
		vertJointDef.localAnchorA.set(0, 0);
		vertJointDef.localAnchorB.set(0, 0);
		
		for (int h = 0; h < bodies.length; h++)
		{
			for (int w = 0; w < bodies[h].length; w++)
			{
				bodyDef.position.set(character.getBody().getPosition())
					.add(widthSeparation * w, heightSeparation * h);
				
				bodies[h][w] = world.createBody(bodyDef);
				bodies[h][w].setLinearDamping(5f);
				bodies[h][w].createFixture(fixtureDef);
				
				if (w > 0)
				{
					horizJointDef.bodyA = bodies[h][w];
					horizJointDef.bodyB = bodies[h][w - 1];
					world.createJoint(horizJointDef);
				}
				if (h > 0)
				{
					vertJointDef.bodyA = bodies[h][w];
					vertJointDef.bodyB = bodies[h - 1][w];
					world.createJoint(vertJointDef);
				}
			}
		}
		
		shape.dispose();
		
		RopeJointDef charJointDef = new RopeJointDef();
		charJointDef.bodyA = character.getBody();
		float charSeparation = 1f / (WIDTH_DIVISIONS - 1); //todo: replace 1f with character width
		Vector2 localAnchorA = new Vector2(-1f / 2f, 1.5f); //todo: replace 1f with character width and 1.5f with character height - character head height
		charJointDef.localAnchorB.set(0, 0);
		charJointDef.maxLength = JOINT_SLACK;
		for (int i = 0; i < bodies[0].length; i++)
		{
			charJointDef.localAnchorA.set(localAnchorA).add(charSeparation * i, 0);
			charJointDef.bodyB = bodies[bodies.length - 1][i];
			world.createJoint(charJointDef);
		}
	}
	
	@Override
	public void load(ResourceManager resourceManager)
	{
		resourceManager.addTask(new AbstractLoadAssetTask()
		{
			@Override
			public void run()
			{
				resourceManager.getAssetManager().load(Assets.DIR + Assets.BADLOGIC, Texture.class);
			}
			
			@Override
			public void postRun()
			{
				texture = new WarpedTexture(
					resourceManager.getAssetManager().get(Assets.DIR + Assets.BADLOGIC, Texture.class),
					30, 60, WIDTH, HEIGHT, WIDTH_DIVISIONS, HEIGHT_DIVISIONS
				);
				grid = texture.getGrid();
			}
		});
	}
	
	@Override
	public void update(float delta)
	{
		for (int h = 0; h < bodies.length; h++)
		{
			for (int w = 0; w < bodies[h].length; w++)
			{
				grid[h][w].set(bodies[h][w].getPosition());
			}
		}
	}
	
	@Override
	public void render(SpriteBatch batch, float delta)
	{
		//		int tempA = (int) (Math.random() * texture.getGrid().length);
		//		int tempB = (int) (Math.random() * texture.getGrid()[0].length);
		//		texture.getGrid()[tempA][tempB].add(new Vector2(0.1f, 0).setAngle(MathUtils.random(360)));
		texture.render(batch, delta);
	}
	
	@Override
	public int getZOrder()
	{
		return -10;
	}
}
