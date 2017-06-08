package com.cloudcanards.input;

/**
 * Default implementations of all input listener methods except the main one
 *
 * @author creativitRy
 */
public abstract class AbstractInputListener implements InputListener
{
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
