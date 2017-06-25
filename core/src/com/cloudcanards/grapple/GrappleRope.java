package com.cloudcanards.grapple;

import com.cloudcanards.behavior.Updateable;
import com.cloudcanards.box2d.CollisionFilters;
import com.cloudcanards.components.GrappleComponent;
import com.cloudcanards.graphics.Renderable;
import com.cloudcanards.screens.GameScreen;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.physics.box2d.joints.RopeJointDef;

/**
 * GrappleRope
 *
 * @author creativitRy
 */
public class GrappleRope implements Updateable, Renderable
{
	private static final float DISTANCE_PER_SECOND = 20f;
	private static final float SEGMENTS_PER_TILE = 1.5f;
	private static final float ROPE_WIDTH = 0.25f;
	
	private World world;
	private GrappleComponent grapple;
	/**
	 * 0 = start, 1 = main, 2 = end
	 */
	private int state;
	private boolean moveToState2;
	
	private Targetable target;
	
	private Body[] segments;
	private Vector2 end;
	private Vector2 prevEnd;
	private boolean retracting;
	
	private NinePatch texture;
	
	public GrappleRope(GrappleComponent grapple, Targetable target, World world, NinePatch texture)
	{
		this.grapple = grapple;
		state = 0;
		
		this.target = target;
		
		this.world = world;
		
		end = new Vector2(grapple.getPosition());
		prevEnd = new Vector2(end);
		
		moveToState2 = false;
		
		this.texture = texture;
		
		GameScreen.getInstance().getRenderableManager().add(this);
	}
	
	/**
	 * Call this to retracting rope back to the hookshot
	 */
	public void stopGrappling()
	{
		moveToState2 = true;
	}
	
	private void createPhysicsRope(boolean reachedTarget)
	{
		final float distance = grapple.getPosition().dst(end);
		final float angle = (float) Math.atan2(end.y - grapple.getPosition().y, end.x - grapple.getPosition().x);
		int numSegments = MathUtils.roundPositive(distance * SEGMENTS_PER_TILE);
		segments = new Body[numSegments];
		
		final float dx = (end.x - grapple.getPosition().x) / numSegments / 2f;
		final float dy = (end.y - grapple.getPosition().y) / numSegments / 2f;
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(grapple.getPosition().x - dx, grapple.getPosition().y - dy);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.shape = new PolygonShape();
		final float halfWidth = distance / numSegments / 2f;
		((PolygonShape) fixtureDef.shape).setAsBox(halfWidth, ROPE_WIDTH / 2f);
		fixtureDef.density = 2;
		
		fixtureDef.filter.categoryBits = CollisionFilters.ROPE;
		//ropes don't collide with itself
		//fixtureDef.filter.maskBits = CollisionFilters.toggleAll(CollisionFilters.ROPE);
		
		RevoluteJointDef jointDef = new RevoluteJointDef();
		jointDef.localAnchorA.set(halfWidth, 0f);
		jointDef.localAnchorB.set(-halfWidth, 0f);
		
		RopeJointDef ropeJointDef = new RopeJointDef();
		ropeJointDef.localAnchorA.set(halfWidth, 0f);
		ropeJointDef.localAnchorB.set(-halfWidth, 0f);
		ropeJointDef.maxLength = distance / numSegments;
		
		for (int i = 0; i < numSegments; i++)
		{
			bodyDef.position.add(dx, dy);
			segments[i] = world.createBody(bodyDef);
			//segments[i].setLinearDamping(0.25f);
			segments[i].createFixture(fixtureDef);
			segments[i].setTransform(bodyDef.position, angle);
			
			System.out.println(segments[i].getPosition());
			
			if (i == 0)
				continue;
			
			jointDef.bodyA = segments[i - 1];
			jointDef.bodyB = segments[i];
			world.createJoint(jointDef);
			
			ropeJointDef.bodyA = jointDef.bodyA;
			ropeJointDef.bodyB = jointDef.bodyB;
			world.createJoint(ropeJointDef);
		}
		
		fixtureDef.shape.dispose();
		
		jointDef.localAnchorB.set(0, 0);
		ropeJointDef.localAnchorB.set(0, 0);
		
		if (reachedTarget)
		{
			jointDef.bodyA = segments[segments.length - 1];
			jointDef.bodyB = target.getBody();
			world.createJoint(jointDef);
			
			ropeJointDef.bodyA = jointDef.bodyA;
			ropeJointDef.bodyB = jointDef.bodyB;
			world.createJoint(ropeJointDef);
		}
		
		jointDef.bodyA = segments[0];
		jointDef.localAnchorA.set(-halfWidth, 0f);
		jointDef.bodyB = grapple.getBody();
		world.createJoint(jointDef);
		
		ropeJointDef.bodyA = jointDef.bodyA;
		ropeJointDef.localAnchorA.set(-halfWidth, 0f);
		ropeJointDef.bodyB = jointDef.bodyB;
		//world.createJoint(ropeJointDef);
	}
	
	@Override
	public void update(float delta)
	{
		if (moveToState2)
		{
			if (state == 0)
			{
				//construct the rope
			}
			else
			{
				//disconnect rope from target
			}
			
			state = 2;
		}
		
		if (state == 0) //start
		{
			//collision check
			/*world.rayCast((fixture, point, normal, fraction) ->
			{
				if (fixture.isSensor())
					return -1;
				
				stopGrappling();
				return 0;
			}, grapple.getPosition(), end);*/
			
			if (moveToState2)
				return;
			
			//lengthen rope
			prevEnd.set(end);
			end.add(new Vector2(target.getPosition().x - end.x, target.getPosition().y - end.y)
				.nor().scl(delta * DISTANCE_PER_SECOND));
			
			if (end.dst2(target.getPosition()) > prevEnd.dst2(target.getPosition()))
			{
				end.set(target.getPosition());
				
				//todo: end of state 0 so move to state 1
				createPhysicsRope(true);
				
				state = 1;
			}
		}
		else if (state == 1) //main
		{
			if (retracting)
			{
				//shorten rope
			}
		}
		else //end
		{
			//shorten rope
		}
	}
	
	@Override
	public void render(SpriteBatch batch, float delta)
	{
		if (state == 0)
		{
			//angle in degrees
			//todo: htf do I use this method?
			texture.draw(batch, grapple.getPosition().x, grapple.getPosition().y, 0f, 0f,
				grapple.getPosition().dst(end) * 4, 1, 0.25f, 0.25f,
				MathUtils.atan2(end.y - grapple.getPosition().y, end.x - grapple.getPosition().x)
					* MathUtils.radiansToDegrees);
		}
		else
		{
		
		}
		
		//https://github.com/libgdx/libgdx/wiki/Path-interface-%26-Splines
		//https://github.com/libgdx/libgdx/blob/master/tests/gdx-tests/src/com/badlogic/gdx/tests/PathTest.java
		
		//		ImmediateModeRenderer20 renderer = new ImmediateModeRenderer20(false, false, 0); //make this a field
		//		renderer.begin(spriteBatch.getProjectionMatrix(), GL20.GL_LINE_STRIP);
		//		float val = 0f;
		//		while (val <= 1f) {
		//			renderer.color(0f, 0f, 0f, 1f);
		//			paths.get(currentPath).valueAt(/* out: */tmpV, val);
		//			renderer.vertex(tmpV.x, tmpV.y, 0);
		//			val += SAMPLE_POINT_DISTANCE;
		//		}
		//		renderer.end();
	}
	
	public int getState()
	{
		return state;
	}
	
	public boolean isRetracting()
	{
		return retracting;
	}
}
