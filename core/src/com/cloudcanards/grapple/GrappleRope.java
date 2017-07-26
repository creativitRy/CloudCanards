package com.cloudcanards.grapple;

import com.cloudcanards.behavior.Updateable;
import com.cloudcanards.box2d.CollisionFilters;
import com.cloudcanards.box2d.PreSolvable;
import com.cloudcanards.components.GrappleComponent;
import com.cloudcanards.graphics.Renderable;
import com.cloudcanards.screens.GameScreen;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

import java.util.ArrayDeque;

/**
 * GrappleRope
 *
 * @author creativitRy
 */
public class GrappleRope implements Updateable, Renderable
{
	private static final float DISTANCE_PER_SECOND = 20f;
	public static final float ROPE_HALF_THICKNESS = 0.1f;
	
	private World world;
	private GrappleComponent grapple;
	private float maxRopeLength = 25f;
	/**
	 * 0 = start, 1 = main, 2 = end
	 */
	private int state;
	private boolean moveToState2;
	
	private Targetable target;
	
	private Vector2 end;
	private Vector2 prevEnd;
	private boolean retracting;
	
	private NinePatch texture;
	
	private ArrayDeque<Vector2> points;
	private Body rope;
	private Fixture ropeFixture;
	private RevoluteJoint targetJoint;
	private Body slider;
	private PrismaticJoint prismaticJoint;
	private RevoluteJoint sourceJoint;
	
	private Vector2 createRope;
	private Body newTarget;
	
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
		
		points = new ArrayDeque<>();
	}
	
	/**
	 * Call this to retracting rope back to the hookshot
	 */
	public void stopGrappling()
	{
		moveToState2 = true;
		grapple.setRetractGrapple();
	}
	
	/**
	 *
	 */
	private void createPhysicsRope(Vector2 pos, Body target)
	{
		new PhysicsGrappleRope(this, world, pos);
		final float distance = grapple.getPosition().dst(pos);
		final float angle = (float) Math.atan2(grapple.getPosition().y - pos.y, grapple.getPosition().x - pos.x);
		
		BodyDef bodyDef = new BodyDef();
		bodyDef.type = BodyDef.BodyType.DynamicBody;
		bodyDef.position.set(pos);
		
		rope = world.createBody(bodyDef);
		rope.setBullet(true);
		
		FixtureDef fixtureDef = new FixtureDef();
		fixtureDef.density = 0.01f;
		fixtureDef.filter.categoryBits = CollisionFilters.ROPE;
		fixtureDef.filter.maskBits = CollisionFilters.ROPE_COLLISION_MASK;
		fixtureDef.friction = 0;
		fixtureDef.shape = new PolygonShape();
		((PolygonShape) fixtureDef.shape).set(new float[]{
			0, -ROPE_HALF_THICKNESS,
			0, +ROPE_HALF_THICKNESS,
			maxRopeLength, 0});
		
		ropeFixture = rope.createFixture(fixtureDef);
		ropeFixture.setUserData((PreSolvable) (contact, oldManifold) ->
		{
			contact.setEnabled(false);
			
			//only continue if this is the first time this frame that presolve for this rope fixture is called
			
			//check if that position is between start of rope and player
			// (by checking if distance from rope start to contact is shorter than distance from rope start to player
			if (rope.getPosition().dst2(contact.getWorldManifold().getPoints()[0])
				< rope.getPosition().dst2(grapple.getPosition()))
			{
				//if true then move collision point a bit outwards using contact.getWorldManifold().getNormal()
				//divide rope
				System.out.println(contact.getWorldManifold().getPoints()[0]);
				
				Fixture temp;
				if (contact.getFixtureA() == ropeFixture)
				{
					temp = contact.getFixtureB();
				}
				else
				{
					temp = contact.getFixtureA();
				}
				newTarget = temp.getBody();
				createRope = new Vector2(contact.getWorldManifold().getNormal()).nor().scl(ROPE_HALF_THICKNESS + 0.1f);
				
				createRope.add(contact.getWorldManifold().getPoints()[0]);
				System.out.println(newTarget.getPosition());
			}
			
			
		});
		rope.setTransform(rope.getPosition(), angle);
		
		RevoluteJointDef revoluteJointDef = new RevoluteJointDef();
		revoluteJointDef.bodyA = target;
		revoluteJointDef.localAnchorA.set(target.getLocalPoint(pos));
		revoluteJointDef.bodyB = rope;
		targetJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);
		
		bodyDef.position.set(grapple.getPosition());
		
		slider = world.createBody(bodyDef);
		
		((PolygonShape) fixtureDef.shape).setAsBox(0.25f, 0.25f);
		fixtureDef.filter.maskBits = CollisionFilters.NONE;
		
		slider.createFixture(fixtureDef);
		fixtureDef.shape.dispose();
		
		PrismaticJointDef prismaticJointDef = new PrismaticJointDef();
		prismaticJointDef.bodyA = rope;
		prismaticJointDef.bodyB = slider;
		prismaticJointDef.localAxisA.set(1, 0);
		prismaticJointDef.enableLimit = true;
		prismaticJointDef.lowerTranslation = 0.1f;
		prismaticJointDef.upperTranslation = distance + 2f;
		prismaticJointDef.enableMotor = true;
		prismaticJointDef.maxMotorForce = 2000f;
		
		prismaticJoint = (PrismaticJoint) world.createJoint(prismaticJointDef);
		
		revoluteJointDef.bodyA = slider;
		revoluteJointDef.localAnchorA.set(0, 0);
		revoluteJointDef.bodyB = grapple.getBody();
		sourceJoint = (RevoluteJoint) world.createJoint(revoluteJointDef);
	}
	
	private void destroyPhysicsRope()
	{
		world.destroyJoint(sourceJoint);
		
		world.destroyJoint(prismaticJoint);
		world.destroyBody(slider);
		
		world.destroyJoint(targetJoint);
		world.destroyBody(rope);
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
			if (!grapple.getPosition().equals(end))
			{
				world.rayCast((fixture, point, normal, fraction) ->
				{
					if (fixture.isSensor())
					{
						return -1;
					}
					if (!CollisionFilters.check(CollisionFilters.ROPE_COLLISION_MASK, fixture.getFilterData().categoryBits))
					{
						return -1;
					}
					if (!CollisionFilters.check(fixture.getFilterData().maskBits, CollisionFilters.ROPE))
					{
						return -1;
					}
					
					stopGrappling();
					return 0;
				}, grapple.getPosition(), end);
				
				if (moveToState2)
				{
					return;
				}
			}
			
			
			//lengthen rope
			prevEnd.set(end);
			end.add(new Vector2(target.getPosition().x - end.x, target.getPosition().y - end.y)
				.nor().scl(delta * DISTANCE_PER_SECOND));
			
			if (end.dst2(target.getPosition()) > prevEnd.dst2(target.getPosition()))
			{
				end.set(target.getPosition());
				
				//todo: end of state 0 so move to state 1
				createPhysicsRope(end, target.getBody());
				grapple.setGrappling();
				
				state = 1;
			}
		}
		else if (state == 1) //main
		{
			if (createRope != null)
			{
				points.push(rope.getPosition());
				destroyPhysicsRope();
				createPhysicsRope(createRope, newTarget);
				createRope = null;
			}
			
			if (retracting)
			{
				//shorten rope
			}
		}
		else //end
		{
			System.out.println("test");
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
	
	public Targetable getTarget()
	{
		return target;
	}
	
	public void setTarget(Targetable target)
	{
		this.target = target;
	}
	
	public GrappleComponent getGrapple()
	{
		return grapple;
	}
}
