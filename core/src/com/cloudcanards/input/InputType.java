package com.cloudcanards.input;

/**
 * InputType
 *
 * @author creativitRy
 */
public enum InputType
{
	KEYBOARD_KEY(0),
	MOUSE_BUTTON(1),
	MOUSE_MOVE(1),
	MOUSE_SCROLL(1),
	CONTROLLER_BUTTON(2),
	CONTROLLER_AXIS(2),
	CONTROLLER_POV(2),
	CONTROLLER_TRIGGER(2);
	
	private int type;
	
	InputType(int type)
	{
		this.type = type;
	}
	
	public boolean isKeyboard()
	{
		return type == 0;
	}
	
	public boolean isMouse()
	{
		return type == 1;
	}
	
	public boolean isController()
	{
		return type == 2;
	}
}
