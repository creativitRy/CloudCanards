package com.cloudcanards.components;

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
public class InputComponent extends AbstractComponent implements InputListener
{
	public static final double WALK_AXIS_THRESHOLD = 0.4;
	
	private boolean walk;
	private boolean sprint;
	private boolean left;
	private boolean right;
	
	public InputComponent(AbstractCharacter character, boolean registerInputListener)
	{
		super(character);
		enableInput = true;
		
		if (registerInputListener)
			InputManager.register(this);
	}
	
	@Override
	public boolean onInput(InputAction action, InputType type, Object... args)
	{
		switch (action)
		{
			case WALK:
				if (args[0] instanceof Boolean && ((Boolean) args[0]))
					walk = true;
				else
					walk = false;
				setSpeed();
				return true;
			
			case SPRINT:
				if (args[0] instanceof Boolean && ((Boolean) args[0]))
					sprint = true;
				else
					sprint = false;
				setSpeed();
				return true;
			
			case MOVE:
				//type can either be controller axis or (todo:) mouse move
				if (type == InputType.CONTROLLER_AXIS)
				{
					float axis = (Float) args[1];
					
					walk = Math.abs(axis) < WALK_AXIS_THRESHOLD;
					setSpeed();
					
					character.setMovementDir((int) Math.signum(axis));
				}
				return true;
			
			case MOVE_LEFT:
				if (args[0] instanceof Boolean && ((Boolean) args[0]))
					left = true;
				else
					left = false;
				setMovement();
				return true;
			
			case MOVE_RIGHT:
				if (args[0] instanceof Boolean && ((Boolean) args[0]))
					right = true;
				else
					right = false;
				setMovement();
				return true;
			
			case JUMP:
				if (args[0] instanceof Boolean && ((Boolean) args[0]))
					character.jump();
				return true;
		}
		
		return false;
	}
	
	private void setMovement()
	{
		if (left == right)
			character.setMovementDir(0);
		else if (left)
			character.setMovementDir(-1);
		else
			character.setMovementDir(1);
	}
	
	private void setSpeed()
	{
		if (walk == sprint)
			character.run();
		else if (walk)
			character.walk();
		else
			character.sprint();
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
