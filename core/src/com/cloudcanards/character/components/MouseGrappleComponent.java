package com.cloudcanards.character.components;

import com.cloudcanards.behavior.Updateable;
import com.cloudcanards.character.AbstractCharacter;
import com.cloudcanards.input.InputAction;
import com.cloudcanards.input.InputListener;
import com.cloudcanards.input.InputManager;
import com.cloudcanards.input.InputType;
import com.cloudcanards.screens.GameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * MouseGrappleComponent
 *
 * @author creativitRy
 */
public class MouseGrappleComponent extends AbstractGrappleControllerComponent implements Updateable, InputListener
{
	private static final float MIN_ANGLE = 2f;
	private static final int MAX_TARGET_DISTANCE = 20; //actual target distance is 50 * sqrt(2)
	
	public MouseGrappleComponent(AbstractCharacter character, GrappleComponent grapple, boolean enableInput)
	{
		super(character, grapple);
		
		mouseEnabled = enableInput;
		InputManager.register(this);
		
		updateAngle(Gdx.input.getX(), Gdx.input.getY());
	}
	
	private void updateAngle(float mouseX, float mouseY)
	{
		Vector2 pos = GameScreen.screenToWorldCoords(mouseX, mouseY);
		float angle = MathUtils.atan2(pos.y - character.getPosition().y, pos.x - character.getPosition().x);
		
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
		updateAngle(Gdx.input.getX(), Gdx.input.getY());
	}
	
	@Override
	public boolean onInput(InputAction action, InputType type, Object... args)
	{
		if (getCurrentTarget() == null)
		{
			return false;
		}
		if (action == InputAction.SHOOT)
		{
			if (args[0] instanceof Boolean && ((Boolean) args[0]))
			{
				getGrapple().shoot(getCurrentTarget());
			}
			else
			{
				getGrapple().retract();
			}
			return true;
		}
		if (getGrapple().getRope() != null && getGrapple().getRope().getPhysicsGrappleRope() != null)
		{
			if (action == InputAction.GRAPPLE_CLIMB_UP)
			{
				getGrapple().getRope().getPhysicsGrappleRope().setClimbUp(args[0] instanceof Boolean && ((Boolean) args[0]));
			}
			else if (action == InputAction.GRAPPLE_CLIMB_DOWN)
			{
				getGrapple().getRope().getPhysicsGrappleRope().setClimbDown(args[0] instanceof Boolean && ((Boolean) args[0]));
			}
			else
			{
				return false;
			}
			return true;
		}
		return false;
	}
	
	private boolean mouseEnabled;
	
	@Override
	public boolean isEnabled()
	{
		return mouseEnabled;
	}
	
	@Override
	public void enable()
	{
		mouseEnabled = true;
	}
	
	@Override
	public void disable()
	{
		mouseEnabled = false;
	}
}
