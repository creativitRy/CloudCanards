package com.cloudcanards.grapple;

import com.cloudcanards.behavior.Updateable;
import com.cloudcanards.box2d.CollisionFilters;
import com.cloudcanards.graphics.Renderable;
import com.cloudcanards.util.DebugRenderUtil;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.*;

/**
 * PhysicsGrappleRope
 *
 * @author GahwonLee
 */
public class PhysicsGrappleRope implements Updateable, Renderable
{
	private static final float APPROX_DIST_BETWEEN_SEGMENTS = 0.5f;
	public static final float DENSITY = 10f;
	private static final float ROPE_HALF_THICKNESS = 0.1f;
	private static final float MIN_LENGTH = 0.1f;
	private static final float ROPE_INCREMENT_AMOUNT = 5f; //make sure this is small to avoid physics bugs
	private static final float ROPE_DECREMENT_AMOUNT = 10f;
	
	private GrappleRope grappleRope;
	private World world;
	
	private Body[] bodies;
	private DistanceJoint[] distanceJoints;
	private RopeJoint[] ropeJoints;
	private RopeJointDef ropeJointDef;
	private RevoluteJoint targetJoint;
	private RevoluteJoint sourceJoint;
	
	private float ropeLength;
	
	//private float[] tempPositions;
	
	private boolean climbing;
	private boolean climbUp;
	private boolean climbDown;
	
	public PhysicsGrappleRope(GrappleRope grappleRope, World world, Vector2 pos, float ropeLength)
	{
		this.grappleRope = grappleRope;
		this.world = world;
		this.ropeLength = ropeLength;
		
		ropeJointDef = new RopeJointDef();
		ropeJointDef.localAnchorA.set(0, 0);
		ropeJointDef.localAnchorB.set(0, 0);
		
		createPhysicsRope(pos);
	}
	
	/**
	 * @param pos position of the target (anchor)
	 */
	private void createPhysicsRope(Vector2 pos)
	{
		int amount = getAmount();
		
		bodies = new Body[amount];
		distanceJoints = new DistanceJoint[amount - 1];
		ropeJoints = new RopeJoint[amount - 1];
		//tempPositions = new float[amount << 1]; // 0b0 = x, 0b1 = y
		
		float segmentDistance = ropeLength / (amount - 1);
		Vector2 dir = new Vector2(grappleRope.getGrapple().getPosition()).sub(pos).nor();
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = DENSITY;
		fixtureDef.filter.categoryBits = CollisionFilters.ROPE;
		fixtureDef.filter.maskBits = CollisionFilters.ROPE_COLLISION_MASK;
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(ROPE_HALF_THICKNESS, ROPE_HALF_THICKNESS);
		fixtureDef.shape = shape;
		
		for (int i = 0; i < amount; i++)
		{
			bodyDef.position.set(dir).scl(segmentDistance * i).add(pos);
			bodies[i] = world.createBody(bodyDef);
			bodies[i].setLinearDamping(2f);
			
			bodies[i].createFixture(fixtureDef);
		}
		shape.dispose();
		
		DistanceJointDef distanceJointDef = new DistanceJointDef();
		distanceJointDef.length = segmentDistance;
		distanceJointDef.frequencyHz = 0;
		distanceJointDef.dampingRatio = 1;
		
		for (int i = 1; i < amount; i++)
		{
			distanceJointDef.bodyA = bodies[i - 1];
			distanceJointDef.bodyB = bodies[i];
			
			distanceJoints[i - 1] = (DistanceJoint) world.createJoint(distanceJointDef);
		}
		
		createRopeJoints(segmentDistance);
		
		RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
		revoluteJointDef.bodyA = grappleRope.getTarget().getBody();
		revoluteJointDef.bodyB = bodies[0];
		targetJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);
		
		revoluteJointDef.bodyA = bodies[bodies.length - 1];
		revoluteJointDef.bodyB = grappleRope.getGrapple().getBody();
		sourceJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);
	}
	
	private void createRopeJoints(float segmentDistance)
	{
		ropeJointDef.bodyA = grappleRope.getTarget().getBody();
		for (int i = 1; i < getAmount(); i++)
		{
			ropeJointDef.maxLength = segmentDistance * i;
			ropeJointDef.bodyB = bodies[i];
			ropeJoints[i - 1] = (RopeJoint) world.createJoint(ropeJointDef);
		}
	}
	
	@Override
	public void update(float delta)
	{
		//apply radial force from target to vertex so rope wants to remain straight
		/*Vector2 temp = new Vector2();
		for (int i = 0; i < bodies.length; i++)
		{
			
			bodies[i].applyForceToCenter(
				temp.set(bodies[i].getPosition()).sub(grappleRope.getTarget().getPosition()).nor().scl(1000),
				true
			);
		}*/
		
		if (climbUp != climbDown)
		{
			/*if (!climbing)
			{
				for (int i = 0; i < ropeJoints.length; i++)
				{
					world.destroyJoint(ropeJoints[i]);
				}
			}*/
			climbing = true;
			if (climbUp)
				decrementRopeLength(delta);
			else
				incrementRopeLength(delta);
		}
		else
		{
			/*if (climbing)
			{
				float segmentDistance = ropeLength / (getAmount() - 1);
				createRopeJoints(segmentDistance);
			}*/
			climbing = false;
		}
		
	}
	
	@Override
	public void render(SpriteBatch batch, float delta)
	{
		batch.flush();
		for (int i = 1; i < getAmount(); i++)
		{
			//todo
			DebugRenderUtil.getInstance().drawLine(
				bodies[i - 1].getPosition(),
				bodies[i].getPosition()
			);
			
		}
	}
	
	public void destroyPhysicsRope()
	{
		world.destroyJoint(sourceJoint);
		world.destroyJoint(targetJoint);
		
		destroyRopeJoints();
		for (int i = 0; i < distanceJoints.length; i++)
		{
			world.destroyJoint(distanceJoints[i]);
		}
		
		for (int i = 0; i < bodies.length; i++)
		{
			world.destroyBody(bodies[i]);
		}
	}
	
	private void destroyRopeJoints()
	{
		for (int i = 0; i < ropeJoints.length; i++)
		{
			world.destroyJoint(ropeJoints[i]);
		}
	}
	
	public void incrementRopeLength(float delta)
	{
		setRopeLength(ropeLength + ROPE_INCREMENT_AMOUNT * delta, false);
	}
	
	public void decrementRopeLength(float delta)
	{
		setRopeLength(ropeLength - ROPE_DECREMENT_AMOUNT * delta);
	}
	
	public int getAmount()
	{
		if (bodies != null)
			return bodies.length;
		return Math.max(2, ((int) (grappleRope.getMaxRopeLength() / APPROX_DIST_BETWEEN_SEGMENTS)));
	}
	
	public float getRopeLength()
	{
		return ropeLength;
	}
	
	public void setRopeLength(float ropeLength)
	{
		setRopeLength(ropeLength, true);
	}
	
	public void setRopeLength(float ropeLength, boolean moveChar)
	{
		if (ropeLength < MIN_LENGTH)
			ropeLength = MIN_LENGTH;
		else if (ropeLength > grappleRope.getMaxRopeLength())
			ropeLength = grappleRope.getMaxRopeLength();
		
		if (ropeLength == this.ropeLength)
			return;
		
		float segmentDistance = ropeLength / (getAmount() - 1);
		
		//move along current path
		/*for (int i = 1; i < getAmount(); i++)
		{
			float num = segmentDistance * i / this.ropeLength * getAmount();
			int iNum = (int) num;
			Vector2 newPos;
			if (getAmount() > iNum + 1)
			{
				Vector2 posA = bodies[iNum].getPosition();
				Vector2 posB = bodies[iNum + 1].getPosition();
				newPos = posA.lerp(posB, num - iNum);
			}
			else
			{
				newPos = bodies[getAmount() - 1].getPosition();
			}
			tempPositions[i << 1] = newPos.x;
			tempPositions[(i << 1) | 1] = newPos.y;
		}*/
		
		this.ropeLength = ropeLength;
		
		for (int i = 0; i < distanceJoints.length; i++)
		{
			distanceJoints[i].setLength(segmentDistance);
		}
		destroyRopeJoints();
		createRopeJoints(segmentDistance);
		
		/*for (int i = 1; i < getAmount(); i++)
		{
			bodies[i].setTransform(tempPositions[i << 1], tempPositions[(i << 1) | 1], bodies[i].getAngle());
			//bodies[i].setLinearVelocity(0, 0);
		}*/
		
		if (moveChar)
			grappleRope.getGrapple().move(bodies[getAmount() - 1].getPosition());
	}
	
	public void setClimbUp(boolean climbUp)
	{
		this.climbUp = climbUp;
	}
	
	public void setClimbDown(boolean climbDown)
	{
		this.climbDown = climbDown;
	}
}
