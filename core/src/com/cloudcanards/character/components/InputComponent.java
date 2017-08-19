package com.cloudcanards.character.components;

import com.cloudcanards.character.AbstractCharacter;
import com.cloudcanards.input.InputAction;
import com.cloudcanards.input.InputListener;
import com.cloudcanards.input.InputManager;
import com.cloudcanards.input.InputType;

/**
 * InputComponent
 *
 * @author creativitRy
 */
public class InputComponent extends AbstractInputComponent implements InputListener
{
	public static final double WALK_AXIS_THRESHOLD = 0.4;
	
	public InputComponent(AbstractCharacter character, boolean enableInput)
	{
		super(character);
		this.enableInput = enableInput;
		
		InputManager.register(this);
	}
	
	@Override
	public boolean onInput(InputAction action, InputType type, Object... args)
	{
		switch (action)
		{
			case WALK:
				if ((Boolean) args[0])
				{
					setWalk(true);
				}
				else
				{
					setWalk(false);
				}
				setSpeed();
				return true;
			
			case SPRINT:
				if ((Boolean) args[0])
				{
					setSprint(true);
				}
				else
				{
					setSprint(false);
				}
				setSpeed();
				return true;
			
			case MOVE:
				//type can either be controller axis or (todo:) mouse move
				if (type == InputType.CONTROLLER_AXIS)
				{
					float axis = (Float) args[1];
					
					setWalk(Math.abs(axis) < WALK_AXIS_THRESHOLD);
					setSpeed();
					
					character.setMovementDir((int) Math.signum(axis));
				}
				return true;
			
			case MOVE_LEFT:
				if ((Boolean) args[0])
				{
					setLeft(true);
				}
				else
				{
					setLeft(false);
				}
				setMovement();
				return true;
			
			case MOVE_RIGHT:
				if ((Boolean) args[0])
				{
					setRight(true);
				}
				else
				{
					setRight(false);
				}
				setMovement();
				return true;
			
			case JUMP:
				if ((Boolean) args[0])
				{
					character.jump();
				}
				else
				{
					character.stopJump();
				}
				return true;
			
		}
		
		return false;
	}
	
	//enable disable input
	private boolean enableInput = true;
	
	@Override
	public boolean isEnabled()
	{
		return enableInput;
	}
	
	@Override
	public void enable()
	{
		enableInput = true;
	}
	
	@Override
	public void disable()
	{
		enableInput = false;
	}
}
