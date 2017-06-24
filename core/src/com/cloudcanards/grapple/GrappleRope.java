package com.cloudcanards.grapple;

import com.cloudcanards.behavior.Renderable;
import com.cloudcanards.behavior.Updateable;
import com.cloudcanards.components.GrappleComponent;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * GrappleRope
 *
 * @author creativitRy
 */
public class GrappleRope implements Updateable, Renderable
{
	private static final float DISTANCE_PER_FRAME = 1f;
	/**
	 * How close does the rope end have to be to the target to move to state 1?
	 */
	private static final float MIN_DISTANCE_SQUARED = 0.5f;
	
	private World world;
	private GrappleComponent grapple;
	/**
	 * 0 = start, 1 = main, 2 = end
	 */
	private int state;
	private Targetable target;
	
	private Vector2 end;
	private boolean retracting;
	private boolean moveToState2;
	
	public GrappleRope(GrappleComponent grapple, Targetable target, World world)
	{
		this.grapple = grapple;
		state = 0;
		
		this.target = target;
		
		this.world = world;
		
		end = grapple.getPosition();
		
		moveToState2 = false;
	}
	
	private void constructRopes(World world)
	{
	
	}
	
	/**
	 * Call this to retracting rope back to the hookshot
	 */
	public void stopGrappling()
	{
		moveToState2 = true;
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
			world.rayCast((fixture, point, normal, fraction) ->
			{
				if (fixture.isSensor())
					return -1;
				
				stopGrappling();
				return 0;
			}, grapple.getPosition(), end);
			
			if (moveToState2)
				return;
			
			//end of state check
			if (target.getPosition().dst2(end) < MIN_DISTANCE_SQUARED)
			{
			
			}
			
			//lengthen rope
			end.add(new Vector2(target.getPosition().x - end.x, target.getPosition().y - end.y)
				.scl(delta * DISTANCE_PER_FRAME));
			
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
