package com.cloudcanards.character.components;

import com.cloudcanards.behavior.Updateable;
import com.cloudcanards.character.AbstractCharacter;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * ControllerGrappleComponent
 *
 * @author creativitRy
 */
public class ControllerGrappleComponent extends AbstractGrappleControllerComponent implements Updateable
{
	private static final float MIN_ANGLE = 2f;
	private static final int MAX_TARGET_DISTANCE = 20; //actual target distance is 50 * sqrt(2)
	
	private float x;
	private float y;
	
	private boolean shot;
	
	public ControllerGrappleComponent(AbstractCharacter character, GrappleComponent grapple)
	{
		super(character, grapple);
		updateAngle();
	}
	
	private void updateAngle(float x, float y)
	{
		float angle = MathUtils.atan2(y, x);
		changeLength(angle);
		
		//todo
		float bestAngle = 9999;
		int bestIndex = -1;
		
		for (int i = 0; i < getTargets().size; i++)
		{
			final float newAngle = MathUtils.atan2(getTargets().get(i).getPosition().y - character.getPosition().y,
				getTargets().get(i).getPosition().x - character.getPosition().x);
			if (Math.abs(newAngle - angle) < MIN_ANGLE && Math.abs(newAngle - angle) < Math.abs(bestAngle - angle))
			{
				bestAngle = newAngle;
				bestIndex = i;
			}
		}
		
		if (bestIndex == -1)
		{
			setCurrentTarget(null);
		}
		else
		{
			setCurrentTarget(getTargets().get(bestIndex));
		}
	}
	
	@Override
	public void update(float delta)
	{
		if (!character.getBody().getLinearVelocity().equals(Vector2.Zero))
		{
			//todo: change to camera viewport plus a bit of offset
			updateTargets(
				character.getPosition().x - MAX_TARGET_DISTANCE, character.getPosition().y - MAX_TARGET_DISTANCE,
				character.getPosition().x + MAX_TARGET_DISTANCE, character.getPosition().y + MAX_TARGET_DISTANCE);
		}
		if (x * x + y * y > 0.1f)
		{
			updateAngle(x, y);
		}
		else
		{
			changeLength(99999);
		}
	}
	
	public void shoot()
	{
		if (!shot)
		{
			shot = true;
			getGrapple().shoot(getCurrentTarget());
		}
	}
	
	public void retract()
	{
		if (shot)
		{
			getGrapple().retract();
			shot = false;
		}
	}
	
	private void changeLength(float angle)
	{
		if (getGrapple().getRope() == null || getGrapple().getRope().getPhysicsGrappleRope() == null)
		{
			return;
		}
		
		if (angle == 99999)
		{
			getGrapple().getRope().getPhysicsGrappleRope().setClimbUp(false);
			getGrapple().getRope().getPhysicsGrappleRope().setClimbDown(false);
			return;
		}
		
		Vector2 target = getGrapple().getRope().getTarget().getPosition();
		Vector2 pos = character.getPosition();
		
		if (MathUtils.isEqual(angle, MathUtils.atan2(target.y - pos.y, target.x - pos.x), 1f)) //about 60 degrees
		{
			getGrapple().getRope().getPhysicsGrappleRope().setClimbUp(true);
			getGrapple().getRope().getPhysicsGrappleRope().setClimbDown(false);
		}
		else if (MathUtils.isEqual(angle, MathUtils.atan2(pos.y - target.y, pos.x - target.x), 1f))
		{
			getGrapple().getRope().getPhysicsGrappleRope().setClimbUp(false);
			getGrapple().getRope().getPhysicsGrappleRope().setClimbDown(true);
		}
		
		//getGrapple().getRope().getPhysicsGrappleRope().setClimbUp(args[0] instanceof Boolean && ((Boolean) args[0]));
	}
	
	public void setX(float x)
	{
		this.x = x;
	}
	
	public void setY(float y)
	{
		this.y = y;
	}
}
