package com.cloudcanards.components;

import com.cloudcanards.character.AbstractCharacter;
import com.cloudcanards.input.InputAction;
import com.cloudcanards.input.InputListener;
import com.cloudcanards.input.InputType;

/**
 * InputComponent
 *
 * @author creativitRy
 */
public class InputComponent extends AbstractComponent implements InputListener
{
	public InputComponent(AbstractCharacter character)
	{
		super(character);
		enableInput = true;
	}
	
	@Override
	public void update(float delta)
	{
	
	}
	
	@Override
	public boolean onInput(InputAction action, InputType type, Object... args)
	{
		return false;
	}
	
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
