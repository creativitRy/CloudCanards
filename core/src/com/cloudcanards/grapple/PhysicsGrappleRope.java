package com.cloudcanards.grapple;

import com.cloudcanards.box2d.CollisionFilters;
import com.cloudcanards.box2d.PreSolvable;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.*;

/**
 * PhysicsGrappleRope
 *
 * @author GahwonLee
 */
public class PhysicsGrappleRope
{
	private static final float APPROX_DIST_BETWEEN_SEGMENTS = 0.5f;
	private static final float DENSITY = 0.2f;
	private static final float BOX_HALFSIZE = 0.05f;
	
	private GrappleRope grappleRope;
	private World world;
	
	private Body[] bodies;
	private DistanceJoint[] joints;
	
	public PhysicsGrappleRope(GrappleRope grappleRope, World world, Vector2 pos)
	{
		this.grappleRope = grappleRope;
		this.world = world;
		
		createPhysicsRope(pos);
	}
	
	/**
	 * @param pos position of the target (anchor)
	 */
	private void createPhysicsRope(Vector2 pos)
	{
		float dist = pos.dst(grappleRope.getGrapple().getPosition());
		
		int amount = Math.max(2, ((int) (dist / APPROX_DIST_BETWEEN_SEGMENTS)));
		bodies = new Body[amount];
		joints = new DistanceJoint[amount - 1];
		float segmentDistance = dist / (amount - 1);
		Vector2 dir = new Vector2(grappleRope.getGrapple().getPosition()).sub(pos).nor();
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = DENSITY;
		fixtureDef.filter.categoryBits = CollisionFilters.ROPE;
		fixtureDef.filter.maskBits = CollisionFilters.ROPE_COLLISION_MASK;
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(BOX_HALFSIZE, BOX_HALFSIZE);
		fixtureDef.shape = shape;
		
		for (int i = 0; i < amount; i++)
		{
			bodyDef.position.set(dir).scl(segmentDistance * i).add(pos);
			bodies[i] = world.createBody(bodyDef);
			
			bodies[i].createFixture(fixtureDef);
		}
		
		DistanceJointDef jointDef = new DistanceJointDef();
		jointDef.length = segmentDistance;
		jointDef.dampingRatio = 0; //todo: ?
		
		for (int i = 1; i < amount; i++)
		{
			jointDef.bodyA = bodies[i - 1];
			jointDef.bodyB = bodies[i];
			
			joints[i - 1] = (DistanceJoint) world.createJoint(jointDef);
		}
		
		
		shape.dispose();
	}
	
	public void destroyPhysicsRope()
	{
	
	}
	
	
}
