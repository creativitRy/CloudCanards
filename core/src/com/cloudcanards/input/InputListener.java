package com.cloudcanards.input;

/**
 * InputListener
 *
 * @author creativitRy
 */
public interface InputListener
{
	/**
	 * Called when input is triggered
	 *
	 * @param action action
	 * @param args   arguments
	 * @param type   what kind of input is this
	 * @return true if input was processed (stops other listeners from dealing with this input)
	 */
	boolean onInput(InputAction action, InputType type, Object... args);
	
	boolean isEnabled();
	
	void enable();
	
	void disable();
}
